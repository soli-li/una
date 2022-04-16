package com.una.system.manager.model;

import java.time.LocalDateTime;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.una.system.manager.model.converter.BooleanAttributeConverter;

@Entity
@Table(name = "S_USER")
public class User extends com.una.common.pojo.User {
  @Id
  @Column(name = "ID")
  private String id;
  @Column(name = "COMPANY_ID")
  private String companyId;
  @Column(name = "USERNAME")
  private String username;
  @JsonIgnore
  @Transient
  private String password;
  @Convert(converter = BooleanAttributeConverter.class)
  @Column(name = "ACCOUNT_NON_EXPIRED")
  private boolean accountNonExpired = false; // 是否未到期
  @Convert(converter = BooleanAttributeConverter.class)
  @Column(name = "ACCOUNT_NON_LOCKED")
  private boolean accountNonLocked = false; // 是否未锁定
  @Convert(converter = BooleanAttributeConverter.class)
  @Column(name = "CREDENTIALS_NON_EXPIRED")
  private boolean credentialsNonExpired = false; // 验证未过期
  @Column(name = "DEFAULT_GROUP_ID")
  private String defaultGroupId;
  @Column(name = "LAST_LOGIN_DATE")
  private LocalDateTime lastLoginDate; // 最后登录时间
  @Column(name = "LAST_CHANGE_PW_DATE")
  private LocalDateTime lastChangePWDate; // 最后修改密码时间
  @Column(name = "FAILURE_COUNT")
  private Integer failureCount = 0;

  @Column(name = "CREATED_USER_ID")
  private String createdUserId;
  @Column(name = "CREATED_DATE")
  private LocalDateTime createdDate;
  @Column(name = "UPDATED_USER_ID")
  private String updatedUserId;
  @Column(name = "UPDATED_DATE")
  private LocalDateTime updatedDate;

  @Transient
  private Set<? extends com.una.common.pojo.Role> roleSet;
  @Transient
  private Set<? extends com.una.common.pojo.Group> groupSet;
  @Transient
  private User createdUser;
  @Transient
  private User updatedUser;
  @Transient
  private Company company;
  @Transient
  private Group defaultGroup;
  @Transient
  private UserProfile userProfile;
  @Transient
  private Set<String> currentRoleId;
  @Transient
  private String currentGroupId;

  @Override
  public Company getCompany() {
    return this.company;
  }

  @Override
  public String getCompanyId() {
    return this.companyId;
  }

  @Override
  public LocalDateTime getCreatedDate() {
    return this.createdDate;
  }

  @Override
  public User getCreatedUser() {
    return this.createdUser;
  }

  @Override
  public String getCreatedUserId() {
    return this.createdUserId;
  }

  @Override
  public String getCurrentGroupId() {
    return this.currentGroupId;
  }

  @Override
  public Set<String> getCurrentRoleId() {
    return this.currentRoleId;
  }

  @Override
  public Group getDefaultGroup() {
    return this.defaultGroup;
  }

  @Override
  public String getDefaultGroupId() {
    return this.defaultGroupId;
  }

  @Override
  public Integer getFailureCount() {
    return this.failureCount;
  }

  @Override
  public Set<? extends com.una.common.pojo.Group> getGroupSet() {
    return this.groupSet;
  }

  @Override
  public String getId() {
    return this.id;
  }

  @Override
  public LocalDateTime getLastChangePWDate() {
    return this.lastChangePWDate;
  }

  @Override
  public LocalDateTime getLastLoginDate() {
    return this.lastLoginDate;
  }

  @Override
  public String getPassword() {
    return this.password;
  }

  @Override
  public Set<? extends com.una.common.pojo.Role> getRoleSet() {
    return this.roleSet;
  }

  @Override
  public LocalDateTime getUpdatedDate() {
    return this.updatedDate;
  }

  @Override
  public User getUpdatedUser() {
    return this.updatedUser;
  }

  @Override
  public String getUpdatedUserId() {
    return this.updatedUserId;
  }

  @Override
  public String getUsername() {
    return this.username;
  }

  @Override
  public UserProfile getUserProfile() {
    return this.userProfile;
  }

  @Override
  public boolean isAccountNonExpired() {
    return this.accountNonExpired;
  }

  @Override
  public boolean isAccountNonLocked() {
    return this.accountNonLocked;
  }

  @Override
  public boolean isCredentialsNonExpired() {
    return this.credentialsNonExpired;
  }

  @Override
  public void setAccountNonExpired(final boolean accountNonExpired) {
    this.accountNonExpired = accountNonExpired;
  }

  @Override
  public void setAccountNonLocked(final boolean accountNonLocked) {
    this.accountNonLocked = accountNonLocked;
  }

  public void setCompany(final Company company) {
    this.company = company;
  }

  @Override
  public void setCompanyId(final String companyId) {
    this.companyId = companyId;
  }

  @Override
  public void setCreatedDate(final LocalDateTime createdDate) {
    this.createdDate = createdDate;
  }

  public void setCreatedUser(final User createdUser) {
    this.createdUser = createdUser;
  }

  @Override
  public void setCreatedUserId(final String createdUserId) {
    this.createdUserId = createdUserId;
  }

  @Override
  public void setCredentialsNonExpired(final boolean credentialsNonExpired) {
    this.credentialsNonExpired = credentialsNonExpired;
  }

  @Override
  public void setCurrentGroupId(final String currentGroupId) {
    this.currentGroupId = currentGroupId;
  }

  @Override
  public void setCurrentRoleId(final Set<String> currentRoleId) {
    this.currentRoleId = currentRoleId;
  }

  public void setDefaultGroup(final Group defaultGroup) {
    this.defaultGroup = defaultGroup;
  }

  @Override
  public void setDefaultGroupId(final String defaultGroupId) {
    this.defaultGroupId = defaultGroupId;
  }

  @Override
  public void setFailureCount(final Integer failureCount) {
    this.failureCount = failureCount;
  }

  @Override
  public void setGroupSet(final Set<? extends com.una.common.pojo.Group> groupSet) {
    this.groupSet = groupSet;
  }

  @Override
  public void setId(final String id) {
    this.id = id;
  }

  @Override
  public void setLastChangePWDate(final LocalDateTime lastChangePWDate) {
    this.lastChangePWDate = lastChangePWDate;
  }

  @Override
  public void setLastLoginDate(final LocalDateTime lastLoginDate) {
    this.lastLoginDate = lastLoginDate;
  }

  @Override
  public void setPassword(final String password) {
    this.password = password;
  }

  @Override
  public void setRoleSet(final Set<? extends com.una.common.pojo.Role> roleSet) {
    this.roleSet = roleSet;
  }

  @Override
  public void setUpdatedDate(final LocalDateTime updatedDate) {
    this.updatedDate = updatedDate;
  }

  public void setUpdatedUser(final User updatedUser) {
    this.updatedUser = updatedUser;
  }

  @Override
  public void setUpdatedUserId(final String updatedUserId) {
    this.updatedUserId = updatedUserId;
  }

  @Override
  public void setUsername(final String username) {
    this.username = username;
  }

  public void setUserProfile(final UserProfile userProfile) {
    this.userProfile = userProfile;
  }

}
