package com.una.common.pojo;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import java.time.LocalDateTime;
import java.util.Set;

public class User {
  private String id;
  private String companyId;
  private String username;
  private String password;
  private boolean accountNonExpired = false; // 是否未到期
  private boolean accountNonLocked = false; // 是否未锁定
  private boolean credentialsNonExpired = false; // 验证未过期
  private String defaultGroupId;
  @JsonDeserialize(using = LocalDateTimeDeserializer.class)
  @JsonSerialize(using = LocalDateTimeSerializer.class)
  private LocalDateTime lastChangePwDate; // 最后修改密码时间
  @JsonDeserialize(using = LocalDateTimeDeserializer.class)
  @JsonSerialize(using = LocalDateTimeSerializer.class)
  private LocalDateTime lastLoginDate; // 最后登录时间
  private Integer failureCount = 0;

  private String createdUserId;
  @JsonDeserialize(using = LocalDateTimeDeserializer.class)
  @JsonSerialize(using = LocalDateTimeSerializer.class)
  private LocalDateTime createdDate;
  private String updatedUserId;
  @JsonDeserialize(using = LocalDateTimeDeserializer.class)
  @JsonSerialize(using = LocalDateTimeSerializer.class)
  private LocalDateTime updatedDate;

  private Set<? extends Role> roleSet;
  private Set<? extends Group> groupSet;
  private User createdUser;
  private User updatedUser;
  private Company company;
  private Group defaultGroup;
  private UserProfile userProfile;
  private Set<String> currentRoleId;
  private String currentGroupId;

  public Company getCompany() {
    return this.company;
  }

  public String getCompanyId() {
    return this.companyId;
  }

  public LocalDateTime getCreatedDate() {
    return this.createdDate;
  }

  public User getCreatedUser() {
    return this.createdUser;
  }

  public String getCreatedUserId() {
    return this.createdUserId;
  }

  public String getCurrentGroupId() {
    return this.currentGroupId;
  }

  public Set<String> getCurrentRoleId() {
    return this.currentRoleId;
  }

  public Group getDefaultGroup() {
    return this.defaultGroup;
  }

  public String getDefaultGroupId() {
    return this.defaultGroupId;
  }

  public Integer getFailureCount() {
    return this.failureCount;
  }

  public Set<? extends Group> getGroupSet() {
    return this.groupSet;
  }

  public String getId() {
    return this.id;
  }

  public LocalDateTime getLastChangePwDate() {
    return this.lastChangePwDate;
  }

  public LocalDateTime getLastLoginDate() {
    return this.lastLoginDate;
  }

  public String getPassword() {
    return this.password;
  }

  public Set<? extends Role> getRoleSet() {
    return this.roleSet;
  }

  public LocalDateTime getUpdatedDate() {
    return this.updatedDate;
  }

  public User getUpdatedUser() {
    return this.updatedUser;
  }

  public String getUpdatedUserId() {
    return this.updatedUserId;
  }

  public String getUsername() {
    return this.username;
  }

  public UserProfile getUserProfile() {
    return this.userProfile;
  }

  public boolean isAccountNonExpired() {
    return this.accountNonExpired;
  }

  public boolean isAccountNonLocked() {
    return this.accountNonLocked;
  }

  public boolean isCredentialsNonExpired() {
    return this.credentialsNonExpired;
  }

  public void setAccountNonExpired(final boolean accountNonExpired) {
    this.accountNonExpired = accountNonExpired;
  }

  public void setAccountNonLocked(final boolean accountNonLocked) {
    this.accountNonLocked = accountNonLocked;
  }

  public void setCompany(final Company company) {
    this.company = company;
  }

  public void setCompanyId(final String companyId) {
    this.companyId = companyId;
  }

  public void setCreatedDate(final LocalDateTime createdDate) {
    this.createdDate = createdDate;
  }

  public void setCreatedUser(final User createdUser) {
    this.createdUser = createdUser;
  }

  public void setCreatedUserId(final String createdUserId) {
    this.createdUserId = createdUserId;
  }

  public void setCredentialsNonExpired(final boolean credentialsNonExpired) {
    this.credentialsNonExpired = credentialsNonExpired;
  }

  public void setCurrentGroupId(final String currentGroupId) {
    this.currentGroupId = currentGroupId;
  }

  public void setCurrentRoleId(final Set<String> currentRoleId) {
    this.currentRoleId = currentRoleId;
  }

  public void setDefaultGroup(final Group defaultGroup) {
    this.defaultGroup = defaultGroup;
  }

  public void setDefaultGroupId(final String defaultGroupId) {
    this.defaultGroupId = defaultGroupId;
  }

  public void setFailureCount(final Integer failureCount) {
    this.failureCount = failureCount;
  }

  public void setGroupSet(final Set<? extends Group> groupSet) {
    this.groupSet = groupSet;
  }

  public void setId(final String id) {
    this.id = id;
  }

  public void setLastChangePwDate(final LocalDateTime lastChangePwDate) {
    this.lastChangePwDate = lastChangePwDate;
  }

  public void setLastLoginDate(final LocalDateTime lastLoginDate) {
    this.lastLoginDate = lastLoginDate;
  }

  public void setPassword(final String password) {
    this.password = password;
  }

  public void setRoleSet(final Set<? extends Role> roleSet) {
    this.roleSet = roleSet;
  }

  public void setUpdatedDate(final LocalDateTime updatedDate) {
    this.updatedDate = updatedDate;
  }

  public void setUpdatedUser(final User updatedUser) {
    this.updatedUser = updatedUser;
  }

  public void setUpdatedUserId(final String updatedUserId) {
    this.updatedUserId = updatedUserId;
  }

  public void setUsername(final String username) {
    this.username = username;
  }

  public void setUserProfile(final UserProfile userProfile) {
    this.userProfile = userProfile;
  }
}
