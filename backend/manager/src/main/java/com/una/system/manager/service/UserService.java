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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Order;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.una.common.Constants;
import com.una.common.pojo.PaginationInfo;
import com.una.common.utils.LogUtil;
import com.una.common.utils.ObjectsToEntityUtil;
import com.una.common.utils.ObjectsToEntityUtil.Transform6;
import com.una.system.manager.dao.UserDao;
import com.una.system.manager.dao.UserProfileDao;
import com.una.system.manager.microservices.NotificationService;
import com.una.system.manager.microservices.SharingService;
import com.una.system.manager.model.Company;
import com.una.system.manager.model.Configuration;
import com.una.system.manager.model.Group;
import com.una.system.manager.model.PasswordPolicy;
import com.una.system.manager.model.Role;
import com.una.system.manager.model.User;
import com.una.system.manager.model.UserProfile;
import com.una.system.manager.pojo.req.SearchUser;
import com.una.system.manager.utils.PaginationSortUtil;

@Service
public class UserService {
  private static final Logger LOGGER = LogUtil.getRunLogger();

  @Value("${spring.profiles.active}")
  private String active;

  @Autowired
  private ObjectMapper objectMapper;
  @Autowired
  private UserDao userDao;
  @Autowired
  private CompanyService companyService;
  @Autowired
  private UserProfileDao userProfileDao;
  @Autowired
  private GroupService groupService;
  @Autowired
  private RoleService roleService;
  @Autowired
  private GenerateService generateService;
  @Autowired
  private PasswordService passwordService;
  @Autowired
  private PasswordPolicyService passwordPolicyService;
  @Autowired
  private SharingService sharingService;

  @Autowired
  private NotificationService notificationService;

  private void checkUsername(final String username) {
    if (StringUtils.isBlank(username)) {
      throw new RuntimeException("username must not be empty");
    }
    if (StringUtils.replace(username, " ", "").length() != username.length()) {
      throw new RuntimeException("username cannot has space character");
    }
  }

  @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
  public void fillUser(final User user) {
    Assert.notNull(user, "parameter 'user' must not be null");
    final String companyId = user.getCompanyId();
    Assert.hasText(companyId, "parameter 'companyId' must not be null");
    Assert.hasText(user.getId(), "parameter 'user.id' must not be null");

    final List<Group> groupRoleList = new ArrayList<>();

    final Company company = this.companyService.findById(companyId).orElseThrow();
    final PasswordPolicy passwordPolicy = this.passwordPolicyService.findById(company.getPwPolicyId()).orElseThrow();
    final UserProfile userProfile = this.userProfileDao.findByUserId(user.getId()).orElse(null);
    final Set<Group> groupSet = this.groupService.findByUser(List.of(user.getId()), Constants.ENABLE, companyId);
    final Set<Role> userRoles = this.roleService.findByUser(List.of(user.getId()), Constants.ENABLE, companyId);

    // set default group and group
    if (StringUtils.isNotBlank(user.getDefaultGroupId())) {
      final Group defaultGroup = this.groupService.findById(user.getDefaultGroupId()).orElse(null);
      if (StringUtils.equals(defaultGroup.getStatus(), Constants.ENABLE)) {
        user.setDefaultGroup(defaultGroup);
        groupRoleList.add(defaultGroup);
      }
    }
    user.setGroupSet(groupSet);

    // set role for user and group
    groupRoleList.addAll(groupSet);
    groupRoleList.remove(null);
    groupRoleList.forEach(o -> o.setRoleSet(this.roleService.findByGroup(List.of(o.getId()), Constants.ENABLE, companyId)));
    user.setRoleSet(userRoles);

    // set company for group and role
    userRoles.forEach(o -> this.setRoleCompany(o, company));
    groupRoleList.forEach(o -> {
      this.setGroupCompany(o, company);
      o.getRoleSet().forEach(r -> this.setRoleCompany(r, company));
    });

    user.setCreatedUser(Optional.ofNullable(user.getCreatedUserId()).flatMap(this::findById).orElse(null));
    user.setUpdatedUser(Optional.ofNullable(user.getUpdatedUserId()).flatMap(this::findById).orElse(null));

    company.setPasswordPolicy(passwordPolicy);
    user.setCompany(company);
    user.setUserProfile(userProfile);
  }

  @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
  public Set<User> findByGroup(Collection<String> groupIds, final String companyId) {
    groupIds = Optional.ofNullable(groupIds).orElse(List.of());
    groupIds = groupIds.stream().filter(StringUtils::isNotBlank).collect(Collectors.toSet());
    if (groupIds.isEmpty()) {
      throw new RuntimeException("parameter 'groupIds' must not be empty");
    }
    final Set<User> userSet = this.userDao.findByGroup(groupIds, companyId);
    userSet.forEach(u -> u.setUserProfile(this.userProfileDao.findByUserId(u.getId()).orElse(null)));
    return userSet;
  }

  @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
  public Optional<User> findById(final String id) {
    Assert.hasText(id, "parameter 'id' must not be empty");
    return this.userDao.findById(id);
  }

  @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
  public Optional<User> findByName(final String username) {
    Assert.hasText(username, "parameter 'username' must not be empty");
    return this.userDao.findByUsername(username);
  }

  @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
  public Set<User> findByRole(Collection<String> roleIds, final String companyId) {
    roleIds = Optional.ofNullable(roleIds).orElse(List.of());
    roleIds = roleIds.stream().filter(StringUtils::isNotBlank).collect(Collectors.toSet());
    if (roleIds.isEmpty()) {
      throw new RuntimeException("parameter 'roleIds' must not be empty");
    }
    final Set<User> userSet = this.userDao.findByRole(roleIds, companyId);
    userSet.forEach(u -> u.setUserProfile(this.userProfileDao.findByUserId(u.getId()).orElse(null)));
    return userSet;

  }

  @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
  public Optional<UserProfile> findProfileByUserId(final String userId) {
    Assert.hasText(userId, "parameter 'userId' must not be empty");
    return this.userProfileDao.findByUserId(userId);
  }

  @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
  public void save(final User user, final String userId) throws Exception {
    Objects.requireNonNull(user, "parameter 'user' must not be null");
    Assert.hasText(userId, "parameter 'userId' must not be empty");

    if (StringUtils.isAnyBlank(user.getCompanyId())) {
      throw new RuntimeException("company must not be empty");
    }

    this.checkUsername(user.getUsername());

    if (StringUtils.isNotBlank(user.getDefaultGroupId())) {
      this.groupService.findById(user.getDefaultGroupId()).orElseThrow(() -> new RuntimeException("group not exist"));
    }
    this.companyService.findById(user.getCompanyId()).orElseThrow(() -> new RuntimeException("company is not exist"));

    // save
    if (StringUtils.isBlank(user.getId())) {
      user.setId(this.generateService.generateId());
      user.setCreatedUserId(userId);
      user.setCreatedDate(LocalDateTime.now());
      user.setCredentialsNonExpired(false);

      final SearchUser searchUser = new SearchUser();
      searchUser.setEqName(user.getUsername());
      if (!this.search(searchUser, user.getCompanyId(), true).isEmpty()) {
        throw new RuntimeException("user name exist");
      }

      UserService.LOGGER.info("save or update user object -> already checked user value, ready save");
      this.userDao.saveUser(user);

      if (!StringUtils.equals(this.active, "test")) {
        final List<Configuration> configList = this.sharingService.getConfigurationList(user.getCompanyId());
        final String password = this.passwordService.generate(configList);
        this.passwordService.resetPassword(user.getUsername(), password);
        final ObjectNode objectNode = this.objectMapper.convertValue(user, ObjectNode.class);
        objectNode.put("password", password);
        this.notificationService.send("new-user-password", this.objectMapper.writeValueAsString(objectNode));
      }
    }
    // update
    else {
      user.setUpdatedDate(LocalDateTime.now());
      user.setUpdatedUserId(userId);
      final Optional<User> optional = this.userDao.findById(user.getId());
      if (optional.isPresent()) {
        user.setUsername(optional.get().getUsername());
        user.setCompanyId(optional.get().getCompanyId());
        UserService.LOGGER.info("save or update user object -> already checked user value, ready update");
        this.userDao.save(user);
      }
      else {
        throw new RuntimeException("not found user");
      }
    }

    if (Objects.nonNull(user.getUserProfile())) {
      final UserProfile userProfile = user.getUserProfile();
      userProfile.setUserId(user.getId());
      this.saveProfile(userProfile, userId);
    }
  }

  @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
  public void saveProfile(final UserProfile userProfile, final String userId) throws Exception {
    Objects.requireNonNull(userProfile, "parameter 'userProfile' must not be null");
    Assert.hasText(userId, "parameter 'userId' must not be empty");

    if (StringUtils.isBlank(userProfile.getId())) {
      userProfile.setId(Optional.ofNullable(userProfile.getId()).orElse(this.generateService.generateId()));
      userProfile.setCreatedDate(LocalDateTime.now());
      userProfile.setCreatedUserId(userId);
    }
    else {
      userProfile.setUpdatedDate(LocalDateTime.now());
      userProfile.setUpdatedUserId(userId);
    }
    this.userProfileDao.save(userProfile);
  }

  @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
  public PaginationInfo<User> search(final SearchUser searchUser, final String companyId, final boolean allowNotCompanyCond) {
    Assert.isTrue(Objects.nonNull(searchUser), "parameter 'searchUser' must not be empty");
    Assert.isTrue(StringUtils.isNotBlank(companyId), "parameter 'companyId' must not be empty");

    searchUser.setName(StringUtils.wrapIfMissing(StringUtils.defaultIfBlank(searchUser.getName(), null), "%"));
    searchUser.setCompanyName(StringUtils.wrapIfMissing(StringUtils.defaultIfBlank(searchUser.getCompanyName(), null), "%"));
    searchUser.setRealName(StringUtils.wrapIfMissing(StringUtils.defaultIfBlank(searchUser.getRealName(), null), "%"));

    Pageable pageable = Pageable.unpaged();
    if (searchUser.isPageable()) {
      final List<Order> orderList = PaginationSortUtil.transformToOrder(searchUser.getSort());
      pageable = PageRequest.of(searchUser.getPageNumber() - 1, searchUser.getPageSize(), Sort.by(orderList));
    }

    UserService.LOGGER.info("search user, current{} contain pagination information", pageable.isPaged() ? "" : " does not");
    UserService.LOGGER.info("search condition, {}", searchUser.toString());

    final Page<User> result = this.userDao.search(searchUser, companyId, allowNotCompanyCond, pageable).map(o -> {
      final User createdUser = new User();
      final User updatedUser = new User();
      final Transform6<User, Company, UserProfile, Group, User, User> transform = ObjectsToEntityUtil.getTransform6(o);
      BeanUtils.copyProperties(Optional.ofNullable(transform.getT5()).orElse(new User()), createdUser);
      BeanUtils.copyProperties(Optional.ofNullable(transform.getT6()).orElse(new User()), updatedUser);

      final User user = transform.getT1();
      user.setCompany(transform.getT2());
      user.setUserProfile(transform.getT3());
      user.setDefaultGroup(transform.getT4());
      user.setCreatedUser(createdUser);
      user.setUpdatedUser(updatedUser);

      return user;
    });

    final PaginationInfo<User> paginationInfo = PaginationInfo.of(searchUser.getPageNumber(), searchUser.getPageSize(), searchUser.isPageable(),
      result.getContent(), result.getTotalElements());
    if (pageable.isUnpaged()) {
      final List<User> dataList = new ArrayList<>(paginationInfo.getDataList());
      Collections.sort(dataList, this::sort);
      paginationInfo.setDataList(dataList);
    }
    return paginationInfo;
  }

  private void setGroupCompany(final Group group, final Company company) {
    if (Objects.isNull(group)) {
      return;
    }
    final String companyId = Optional.ofNullable(company).map(Company::getId).orElse("");
    if (StringUtils.equals(group.getCompanyId(), companyId)) {
      group.setCompany(company);
    }
  }

  private <T extends com.una.common.pojo.Role> void setRoleCompany(final T role, final Company company) {
    if (Objects.isNull(role)) {
      return;
    }
    final String companyId = Optional.ofNullable(company).map(Company::getId).orElse("");
    if (StringUtils.equals(role.getCompanyId(), companyId)) {
      role.setCompany(company);
    }
  }

  private int sort(final User o1, final User o2) {
    final String s1 = Optional.ofNullable(o1).map(User::getUsername).orElse("");
    final String s2 = Optional.ofNullable(o2).map(User::getUsername).orElse("");
    return s1.compareTo(s2);
  }

  @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
  public void updatePassword(final User user, final String password, final boolean updateUser) {
    Objects.requireNonNull(user, "parameter 'user' must not be null");
    Assert.hasText(user.getUsername(), "parameter 'user.username' must not be empty");
    Assert.hasText(password, "parameter 'password' must not be empty");

    this.passwordService.resetPassword(user.getUsername(), password);
    if (updateUser) {
      Assert.hasText(user.getId(), "parameter 'user.id' must not be empty");
      this.updateUser(user);
    }
  }

  @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
  public void updateRelation(final User user, final String userId) {
    Objects.requireNonNull(user, "parameter 'user' must not be empty");
    Assert.hasText(userId, "parameter 'userId' must not be empty");
    Assert.hasText(user.getId(), "parameter 'user.id' must not be empty");

    UserService.LOGGER.info("ready update user's relation group and role, operate user id: {}", userId);
    UserService.LOGGER.info("delete role and group relation data, operate user id: {}", userId);
    final LocalDateTime now = LocalDateTime.now();
    this.userDao.deleteRoleRelation(user.getId());
    this.userDao.deleteGroupRelation(user.getId());

    UserService.LOGGER.info("insert role and group relation data, operate user id: {}", userId);
    Set<? extends com.una.common.pojo.Group> groupSet = Optional.ofNullable(user.getGroupSet()).orElse(Set.of());
    groupSet = groupSet.stream().filter(o -> StringUtils.isNotBlank(o.getId())).collect(Collectors.toSet());
    groupSet.forEach(o -> this.userDao.saveGroupRelation(this.generateService.generateId(), user.getId(), o.getId(), userId, now));

    Set<? extends com.una.common.pojo.Role> roleSet = Optional.ofNullable(user.getRoleSet()).orElse(Set.of());
    roleSet = roleSet.stream().filter(o -> StringUtils.isNotBlank(o.getId())).collect(Collectors.toSet());
    roleSet.forEach(o -> this.userDao.saveRoleRelation(this.generateService.generateId(), user.getId(), o.getId(), userId, now));
  }

  @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
  public User updateUser(final User user) {
    Assert.notNull(user, "parameter 'user' must not be null");
    return this.userDao.save(user);
  }
}
