package com.una.system.manager.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Order;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import com.una.common.Constants;
import com.una.common.pojo.PaginationInfo;
import com.una.common.pojo.Role;
import com.una.common.utils.LogUtil;
import com.una.common.utils.ObjectsToEntityUtil;
import com.una.common.utils.ObjectsToEntityUtil.Transform5;
import com.una.system.manager.dao.GroupDao;
import com.una.system.manager.model.Company;
import com.una.system.manager.model.Group;
import com.una.system.manager.model.User;
import com.una.system.manager.pojo.req.SearchGroup;
import com.una.system.manager.utils.PaginationSortUtil;

@Service
public class GroupService {
  private static final Logger LOGGER = LogUtil.getRunLogger();
  @Autowired
  private GroupDao groupDao;
  @Autowired
  private GenerateService generateService;

  @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
  public Optional<Group> findById(final String groupId) {
    Assert.hasText(groupId, "parameter 'groupId' must not be empty");
    return this.groupDao.findById(groupId);
  }

  @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
  public Set<Group> findByRole(Collection<String> roleIds, final String status, final String companyId) {
    roleIds = Optional.ofNullable(roleIds).orElse(List.of());
    roleIds = roleIds.stream().filter(StringUtils::isNotBlank).collect(Collectors.toSet());
    if (roleIds.isEmpty()) {
      throw new RuntimeException("parameter 'roleIds' must not be empty");
    }
    return this.groupDao.findByRole(roleIds, status, companyId);
  }

  @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
  public Set<Group> findByUser(Collection<String> userIds, final String status, final String companyId) {
    userIds = Optional.ofNullable(userIds).orElse(List.of());
    userIds = userIds.stream().filter(StringUtils::isNotBlank).collect(Collectors.toSet());
    if (userIds.isEmpty()) {
      throw new RuntimeException("parameter 'userIds' must not be empty");
    }
    return this.groupDao.findByUser(userIds, status, companyId);
  }

  @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
  public void save(final Group group, final String userId) {
    Objects.requireNonNull(group, "parameter 'group' must not be empty");
    Assert.hasText(userId, "parameter 'userId' must not be empty");

    if (StringUtils.isAnyBlank(group.getName(), group.getCompanyId())) {
      throw new RuntimeException("name and company must not be empty");
    }

    Assert.isTrue(StringUtils.equalsAny(group.getStatus(), Constants.ENABLE, Constants.DISABLE), "incorrect status value");
    if (StringUtils.isNotBlank(group.getParentId()) && this.groupDao.findById(group.getParentId()).isEmpty()) {
      throw new RuntimeException("parent group not exist");
    }

    if (StringUtils.isBlank(group.getId())) {
      group.setId(this.generateService.generateId());
      group.setCreatedUserId(userId);
      group.setCreatedDate(LocalDateTime.now());

      final SearchGroup searchGroup = new SearchGroup();
      searchGroup.setEqName(group.getName());
      searchGroup.setCompanyId(group.getCompanyId());
      if (!this.search(searchGroup, group.getCompanyId(), true).isEmpty()) {
        throw new RuntimeException("group name exist");
      }
    }
    else {
      group.setUpdatedDate(LocalDateTime.now());
      group.setUpdatedUserId(userId);
      final Optional<Group> optional = this.groupDao.findById(group.getId());
      if (optional.isEmpty()) {
        throw new RuntimeException("group not exist");
      }
      group.setName(optional.get().getName());
      group.setCompanyId(optional.get().getCompanyId());
    }

    GroupService.LOGGER.info("save or update group object -> already checked group value, ready save");

    this.groupDao.save(group);
  }

  @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
  public PaginationInfo<Group> search(final SearchGroup searchGroup, final String companyId, final boolean allowNotCompanyCond) {
    Assert.isTrue(Objects.nonNull(searchGroup), "parameter 'searchGroup' must not be empty");
    Assert.isTrue(StringUtils.isNotBlank(companyId), "parameter 'companyId' must not be empty");

    searchGroup.setCompanyName(StringUtils.wrapIfMissing(StringUtils.defaultIfBlank(searchGroup.getCompanyName(), null), "%"));
    searchGroup.setName(StringUtils.wrapIfMissing(StringUtils.defaultIfBlank(searchGroup.getName(), null), "%"));

    Pageable pageable = Pageable.unpaged();
    if (searchGroup.isPageable()) {
      final List<Order> orderList = PaginationSortUtil.transformToOrder(searchGroup.getSort());
      pageable = PageRequest.of(searchGroup.getPageNumber() - 1, searchGroup.getPageSize(), Sort.by(orderList));
    }

    GroupService.LOGGER.info("search group, current{} contain pagination information", pageable.isPaged() ? "" : " does not");
    GroupService.LOGGER.info("search condition, {}", searchGroup.toString());

    final Page<Group> result = this.groupDao.search(searchGroup, companyId, allowNotCompanyCond, pageable).map(o -> {
      final User createdUser = new User();
      final User updatedUser = new User();
      final Transform5<Group, Company, Group, User, User> transform = ObjectsToEntityUtil.getTransform5(o);
      BeanUtils.copyProperties(Optional.ofNullable(transform.getT4()).orElse(new User()), createdUser);
      BeanUtils.copyProperties(Optional.ofNullable(transform.getT5()).orElse(new User()), updatedUser);

      final Group group = transform.getT1();

      group.setCompany(transform.getT2());
      group.setCreatedUser(createdUser);
      group.setUpdatedUser(updatedUser);
      return group;
    });

    final PaginationInfo<Group> paginationInfo = PaginationInfo.of(searchGroup.getPageNumber(), searchGroup.getPageSize(), searchGroup.isPageable(),
      result.getContent(), result.getTotalElements());
    if (pageable.isUnpaged()) {
      final List<Group> dataList = new ArrayList<>(paginationInfo.getDataList());
      Collections.sort(dataList, this::sort);
      paginationInfo.setDataList(dataList);
    }
    return paginationInfo;
  }

  private int sort(final Group o1, final Group o2) {
    final String s1 = Optional.ofNullable(o1).map(Group::getName).orElse("");
    final String s2 = Optional.ofNullable(o2).map(Group::getName).orElse("");
    return s1.compareTo(s2);
  }

  @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
  public void updateRelation(final Group group, final String userId) {
    Objects.requireNonNull(group, "parameter 'group' must not be empty");
    Assert.hasText(userId, "parameter 'userId' must not be empty");
    Assert.hasText(group.getId(), "parameter 'group.id' must not be empty");

    GroupService.LOGGER.info("ready update group's relation user and role, operate user id: {}", userId);
    GroupService.LOGGER.info("delete role and user relation data, operate user id: {}", userId);
    final LocalDateTime now = LocalDateTime.now();
    this.groupDao.deleteRoleRelation(group.getId());
    this.groupDao.deleteUserRelation(group.getId());

    GroupService.LOGGER.info("insert role and user relation data, operate user id: {}", userId);
    Set<? extends com.una.common.pojo.User> userSet = Optional.ofNullable(group.getUserSet()).orElse(Set.of());
    userSet = userSet.stream().filter(o -> StringUtils.isNotBlank(o.getId())).collect(Collectors.toSet());
    userSet.forEach(o -> this.groupDao.saveUserRelation(this.generateService.generateId(), o.getId(), group.getId(), userId, now));

    Set<? extends Role> roleSet = Optional.ofNullable(group.getRoleSet()).orElse(Set.of());
    roleSet = roleSet.stream().filter(o -> StringUtils.isNotBlank(o.getId())).collect(Collectors.toSet());
    roleSet.forEach(o -> this.groupDao.saveRoleRelation(this.generateService.generateId(), group.getId(), o.getId(), userId, now));
  }
}
