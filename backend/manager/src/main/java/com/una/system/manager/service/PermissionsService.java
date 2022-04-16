package com.una.system.manager.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
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
import org.springframework.util.CollectionUtils;

import com.una.common.Constants;
import com.una.common.pojo.PaginationInfo;
import com.una.common.utils.LogUtil;
import com.una.common.utils.ObjectsToEntityUtil;
import com.una.common.utils.ObjectsToEntityUtil.Transform3;
import com.una.system.manager.dao.PermissionsDao;
import com.una.system.manager.dao.RoleDao;
import com.una.system.manager.model.Permissions;
import com.una.system.manager.model.User;
import com.una.system.manager.pojo.req.PermissionsRelation;
import com.una.system.manager.pojo.req.SearchPermissions;
import com.una.system.manager.utils.PaginationSortUtil;

@Service
public class PermissionsService {
  private static final Logger LOGGER = LogUtil.getRunLogger();
  @Autowired
  private PermissionsDao permissionsDao;
  @Autowired
  private GenerateService generateService;
  @Autowired
  private RoleDao roleDao;

  @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
  public Optional<Permissions> findById(final String permissionsId) {
    Assert.hasText(permissionsId, "parameter 'permissionsId' must not be empty");
    return this.permissionsDao.findById(permissionsId);
  }

  @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
  public Set<Permissions> findByRoleIds(final Collection<String> roleIds) {
    Assert.isTrue(!CollectionUtils.isEmpty(roleIds), "parameter 'roleIds' must not be empty");
    return this.permissionsDao.findByRoleId(roleIds, Constants.ENABLE);
  }

  @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
  public void save(final Permissions permissions, final String userId) {
    Objects.requireNonNull(permissions, "parameter 'permissions' must not be empty");
    Assert.hasText(userId, "parameter 'userId' must not be empty");

    if (StringUtils.isAnyBlank(permissions.getName())) {
      throw new RuntimeException("name must not be empty");
    }

    Assert.isTrue(StringUtils.equalsAny(permissions.getStatus(), Constants.ENABLE, Constants.DISABLE), "incorrect status value");

    if (StringUtils.isBlank(permissions.getId())) {
      permissions.setId(this.generateService.generateId());
      permissions.setCreatedUserId(userId);
      permissions.setCreatedDate(LocalDateTime.now());
    }
    else if (this.permissionsDao.findById(permissions.getId()).isEmpty()) {
      permissions.setCreatedUserId(userId);
      permissions.setCreatedDate(LocalDateTime.now());
    }
    else {
      permissions.setUpdatedDate(LocalDateTime.now());
      permissions.setUpdatedUserId(userId);
    }

    PermissionsService.LOGGER.info("save or update permissions object -> already checked permissions value, ready save");

    this.permissionsDao.save(permissions);
  }

  @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
  public PaginationInfo<Permissions> search(final SearchPermissions searchPermissions) {
    Assert.isTrue(Objects.nonNull(searchPermissions), "parameter 'searchPermissions' must not be empty");

    searchPermissions.setName(StringUtils.wrapIfMissing(StringUtils.defaultIfBlank(searchPermissions.getName(), null), "%"));

    Pageable pageable = Pageable.unpaged();
    if (searchPermissions.isPageable()) {
      final List<Order> orderList = PaginationSortUtil.transformToOrder(searchPermissions.getSort());
      pageable = PageRequest.of(searchPermissions.getPageNumber() - 1, searchPermissions.getPageSize(), Sort.by(orderList));
    }

    PermissionsService.LOGGER.info("search permissions, current{} contain pagination information", pageable.isPaged() ? "" : " does not");
    PermissionsService.LOGGER.info("search condition, {}", searchPermissions.toString());

    final Page<Permissions> result = this.permissionsDao.search(searchPermissions, pageable).map(o -> {
      final User createdUser = new User();
      final User updatedUser = new User();
      final Transform3<Permissions, User, User> transform = ObjectsToEntityUtil.getTransform3(o);
      BeanUtils.copyProperties(Optional.ofNullable(transform.getT2()).orElse(new User()), createdUser);
      BeanUtils.copyProperties(Optional.ofNullable(transform.getT3()).orElse(new User()), updatedUser);

      final Permissions permissions = transform.getT1();
      permissions.setCreatedUser(createdUser);
      permissions.setUpdatedUser(updatedUser);
      return permissions;
    });

    final PaginationInfo<Permissions> paginationInfo = PaginationInfo.of(searchPermissions.getPageNumber(), searchPermissions.getPageSize(),
      searchPermissions.isPageable(), result.getContent(), result.getTotalElements());
    if (pageable.isUnpaged()) {
      final List<Permissions> dataList = new ArrayList<>(paginationInfo.getDataList());
      Collections.sort(dataList, this::sort);
      paginationInfo.setDataList(dataList);
    }
    return paginationInfo;
  }

  private int sort(final Permissions o1, final Permissions o2) {
    final String s1 = Optional.ofNullable(o1).map(Permissions::getName).orElse("");
    final String s2 = Optional.ofNullable(o2).map(Permissions::getName).orElse("");
    return s1.compareTo(s2);
  }

  @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
  public void updateRelation(final PermissionsRelation permissionsRelation, final String userId) {
    Objects.requireNonNull(permissionsRelation, "parameter 'permissions' must not be empty");
    Assert.hasText(userId, "parameter 'userId' must not be empty");
    Assert.hasText(permissionsRelation.getCompanyId(), "parameter 'permissionsRelation.companyId' must not be empty");
    List<String> permIds = permissionsRelation.getPermissionsList();
    permIds = Optional.ofNullable(permIds).orElse(List.of());
    final Set<String> permIdSet = permIds.stream().filter(StringUtils::isNotBlank).collect(Collectors.toSet());
    if (permIdSet.isEmpty()) {
      throw new RuntimeException("parameter 'permissionsRelation.permissionsList' must not be empty");
    }

    PermissionsService.LOGGER.info("ready update permissions's relation role, operate user id: {}", userId);
    PermissionsService.LOGGER.info("delete role relation data, operate user id: {}", userId);
    final LocalDateTime now = LocalDateTime.now();

    final Set<com.una.system.manager.model.Role> roles = this.roleDao.findByPerm(permIdSet, permissionsRelation.getCompanyId());
    roles.forEach(o -> {
      permIdSet.forEach(p -> this.permissionsDao.deleteRoleRelation(o.getId(), p));
    });

    PermissionsService.LOGGER.info("insert role relation data, operate user id: {}", userId);
    Set<String> roleSet = new HashSet<>(Optional.ofNullable(permissionsRelation.getRoleList()).orElse(List.of()));
    roleSet = roleSet.stream().filter(StringUtils::isNotBlank).collect(Collectors.toSet());
    for (final String permId : permissionsRelation.getPermissionsList()) {
      roleSet.forEach(o -> this.permissionsDao.saveRoleRelation(this.generateService.generateId(), o, permId, userId, now));
    }

  }
}
