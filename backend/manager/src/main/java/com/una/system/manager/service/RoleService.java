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
import com.una.common.utils.LogUtil;
import com.una.common.utils.ObjectsToEntityUtil;
import com.una.common.utils.ObjectsToEntityUtil.Transform4;
import com.una.system.manager.dao.RoleDao;
import com.una.system.manager.model.Company;
import com.una.system.manager.model.Permissions;
import com.una.system.manager.model.Role;
import com.una.system.manager.model.User;
import com.una.system.manager.pojo.req.SearchRole;
import com.una.system.manager.utils.PaginationSortUtil;

@Service
public class RoleService {
  private static final Logger LOGGER = LogUtil.getRunLogger();
  @Autowired
  private RoleDao roleDao;
  @Autowired
  private GenerateService generateService;

  @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
  public Set<Role> findByGroup(Collection<String> groupIds, final String status, final String companyId) {
    groupIds = Optional.ofNullable(groupIds).orElse(List.of());
    groupIds = groupIds.stream().filter(StringUtils::isNotBlank).collect(Collectors.toSet());
    if (groupIds.isEmpty()) {
      throw new RuntimeException("parameter 'groupIds' must not be empty");
    }
    return this.roleDao.findByGroup(groupIds, status, companyId);
  }

  @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
  public Set<Role> findByPerm(Collection<String> permIds, final String companyId) {
    Assert.hasText(companyId, "parameter 'companyId' must not be empty");
    permIds = Optional.ofNullable(permIds).orElse(List.of());
    permIds = permIds.stream().filter(StringUtils::isNotBlank).collect(Collectors.toSet());
    if (permIds.isEmpty()) {
      throw new RuntimeException("parameter 'permIds' must not be empty");
    }
    return this.roleDao.findByPerm(permIds, companyId);
  }

  @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
  public Set<Role> findByUser(Collection<String> userIds, final String status, final String companyId) {
    userIds = Optional.ofNullable(userIds).orElse(List.of());
    userIds = userIds.stream().filter(StringUtils::isNotBlank).collect(Collectors.toSet());
    if (userIds.isEmpty()) {
      throw new RuntimeException("parameter 'userIds' must not be empty");
    }
    return this.roleDao.findByUser(userIds, status, companyId);
  }

  @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
  public void save(final Role role, final String userId) {
    Objects.requireNonNull(role, "parameter 'role' must not be empty");
    Assert.hasText(userId, "parameter 'userId' must not be empty");

    if (StringUtils.isAnyBlank(role.getAuthority(), role.getCompanyId())) {
      throw new RuntimeException("authority and company must not be empty");
    }

    Assert.isTrue(StringUtils.equalsAny(role.getStatus(), Constants.ENABLE, Constants.DISABLE), "incorrect status value");

    if (StringUtils.isBlank(role.getId())) {
      role.setId(this.generateService.generateId());
      role.setCreatedUserId(userId);
      role.setCreatedDate(LocalDateTime.now());

      final SearchRole searchRole = new SearchRole();
      searchRole.setEqAuthority(role.getAuthority());
      searchRole.setCompanyId(role.getCompanyId());
      if (!this.search(searchRole, role.getCompanyId(), true).isEmpty()) {
        throw new RuntimeException("role name exist");
      }
    }
    else {
      role.setUpdatedDate(LocalDateTime.now());
      role.setUpdatedUserId(userId);
      final Optional<Role> optional = this.roleDao.findById(role.getId());
      if (optional.isEmpty()) {
        throw new RuntimeException("role not exist");
      }
      role.setAuthority(optional.get().getAuthority());
      role.setCompanyId(optional.get().getCompanyId());
    }

    RoleService.LOGGER.info("save or update role object -> already checked role value, ready save");

    this.roleDao.save(role);
  }

  @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
  public PaginationInfo<Role> search(final SearchRole searchRole, final String companyId, final boolean allowNotCompanyCond) {
    Assert.isTrue(Objects.nonNull(searchRole), "parameter 'searchRole' must not be empty");
    Assert.isTrue(StringUtils.isNotBlank(companyId), "parameter 'companyId' must not be empty");

    searchRole.setAuthority(StringUtils.wrapIfMissing(StringUtils.defaultIfBlank(searchRole.getAuthority(), null), "%"));
    searchRole.setCompanyName(StringUtils.wrapIfMissing(StringUtils.defaultIfBlank(searchRole.getCompanyName(), null), "%"));

    Pageable pageable = Pageable.unpaged();
    if (searchRole.isPageable()) {
      final List<Order> orderList = PaginationSortUtil.transformToOrder(searchRole.getSort());
      pageable = PageRequest.of(searchRole.getPageNumber() - 1, searchRole.getPageSize(), Sort.by(orderList));
    }

    RoleService.LOGGER.info("search role, current{} contain pagination information", pageable.isPaged() ? "" : " does not");
    RoleService.LOGGER.info("search condition, {}", searchRole.toString());

    final Page<Role> result = this.roleDao.search(searchRole, companyId, allowNotCompanyCond, pageable).map(o -> {
      final User createdUser = new User();
      final User updatedUser = new User();
      final Transform4<Role, Company, User, User> transform = ObjectsToEntityUtil.getTransform4(o);
      BeanUtils.copyProperties(Optional.ofNullable(transform.getT3()).orElse(new User()), createdUser);
      BeanUtils.copyProperties(Optional.ofNullable(transform.getT4()).orElse(new User()), updatedUser);

      final Role role = transform.getT1();
      role.setCompany(transform.getT2());
      role.setCreatedUser(createdUser);
      role.setUpdatedUser(updatedUser);
      return role;
    });

    final PaginationInfo<Role> paginationInfo = PaginationInfo.of(searchRole.getPageNumber(), searchRole.getPageSize(), searchRole.isPageable(),
      result.getContent(), result.getTotalElements());
    if (pageable.isUnpaged()) {
      final List<Role> dataList = new ArrayList<>(paginationInfo.getDataList());
      Collections.sort(dataList, this::sort);
      paginationInfo.setDataList(dataList);
    }
    return paginationInfo;
  }

  private int sort(final Role o1, final Role o2) {
    final String s1 = Optional.ofNullable(o1).map(Role::getAuthority).orElse("");
    final String s2 = Optional.ofNullable(o2).map(Role::getAuthority).orElse("");
    return s1.compareTo(s2);
  }

  @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
  public void updateRelation(final Role role, final String userId) {
    Objects.requireNonNull(role, "parameter 'role' must not be empty");
    Assert.hasText(userId, "parameter 'userId' must not be empty");
    Assert.hasText(role.getId(), "parameter 'role.id' must not be empty");

    RoleService.LOGGER.info("ready update role's relation group and user, operate user id: {}", userId);
    RoleService.LOGGER.info("delete user and group relation data, operate user id: {}", userId);
    final LocalDateTime now = LocalDateTime.now();
    this.roleDao.deleteUserRelation(role.getId());
    this.roleDao.deleteGroupRelation(role.getId());

    RoleService.LOGGER.info("insert user and group relation data, operate user id: {}", userId);
    Set<? extends com.una.common.pojo.Group> groupSet = Optional.ofNullable(role.getGroupSet()).orElse(Set.of());
    groupSet = groupSet.stream().filter(o -> StringUtils.isNotBlank(o.getId())).collect(Collectors.toSet());
    groupSet.forEach(o -> this.roleDao.saveGroupRelation(this.generateService.generateId(), o.getId(), role.getId(), userId, now));

    Set<? extends com.una.common.pojo.User> userSet = Optional.ofNullable(role.getUserSet()).orElse(Set.of());
    userSet = userSet.stream().filter(o -> StringUtils.isNotBlank(o.getId())).collect(Collectors.toSet());
    userSet.forEach(o -> this.roleDao.saveUserRelation(this.generateService.generateId(), o.getId(), role.getId(), userId, now));
  }

  @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
  public void updateRelationForPerm(final Role role, final String userId) {
    Objects.requireNonNull(role, "parameter 'role' must not be empty");
    Assert.hasText(userId, "parameter 'userId' must not be empty");
    Assert.hasText(role.getId(), "parameter 'role.id' must not be empty");

    RoleService.LOGGER.info("ready update role's relation permissions, operate user id: {}", userId);
    RoleService.LOGGER.info("delete permissions relation data, operate user id: {}", userId);
    final LocalDateTime now = LocalDateTime.now();
    this.roleDao.deletePermRelation(role.getId());

    RoleService.LOGGER.info("insert permissions relation data, operate user id: {}", userId);
    Set<Permissions> permSet = Optional.ofNullable(role.getPermissionsSet()).orElse(Set.of());
    permSet = permSet.stream().filter(o -> StringUtils.isNotBlank(o.getId())).collect(Collectors.toSet());
    permSet.forEach(o -> this.roleDao.savePermRelation(this.generateService.generateId(), role.getId(), o.getId(), userId, now));
  }
}
