package com.una.system.manager.model;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.una.common.pojo.User;

@Entity
@Table(name = "S_USER_PROFILE")
public class UserProfile extends com.una.common.pojo.UserProfile {
  @Id
  @Column(name = "ID")
  private String id;
  @Column(name = "USER_ID")
  private String userId;
  @Column(name = "REAL_NAME")
  private String realName;
  @Column(name = "PHONE")
  private String phone;
  @Column(name = "EMAIL")
  private String email;
  @Column(name = "AVATAR")
  private String avatar;
  @Column(name = "AVATAR_TYPE")
  private String avatarType;

  @Column(name = "CREATED_USER_ID")
  private String createdUserId;
  @Column(name = "CREATED_DATE")
  private LocalDateTime createdDate;
  @Column(name = "UPDATED_USER_ID")
  private String updatedUserId;
  @Column(name = "UPDATED_DATE")
  private LocalDateTime updatedDate;

  @Transient
  private User user;
  @Transient
  private User createdUser;
  @Transient
  private User updatedUser;

  @Override
  public String getAvatar() {
    return this.avatar;
  }

  @Override
  public String getAvatarType() {
    return this.avatarType;
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
  public String getEmail() {
    return this.email;
  }

  @Override
  public String getId() {
    return this.id;
  }

  @Override
  public String getPhone() {
    return this.phone;
  }

  @Override
  public String getRealName() {
    return this.realName;
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
  public User getUser() {
    return this.user;
  }

  @Override
  public String getUserId() {
    return this.userId;
  }

  @Override
  public void setAvatar(final String avatar) {
    this.avatar = avatar;
  }

  @Override
  public void setAvatarType(final String avatarType) {
    this.avatarType = avatarType;
  }

  @Override
  public void setCreatedDate(final LocalDateTime createdDate) {
    this.createdDate = createdDate;
  }

  @Override
  public void setCreatedUser(final User createdUser) {
    this.createdUser = createdUser;
  }

  @Override
  public void setCreatedUserId(final String createdUserId) {
    this.createdUserId = createdUserId;
  }

  @Override
  public void setEmail(final String email) {
    this.email = email;
  }

  @Override
  public void setId(final String id) {
    this.id = id;
  }

  @Override
  public void setPhone(final String phone) {
    this.phone = phone;
  }

  @Override
  public void setRealName(final String realName) {
    this.realName = realName;
  }

  @Override
  public void setUpdatedDate(final LocalDateTime updatedDate) {
    this.updatedDate = updatedDate;
  }

  @Override
  public void setUpdatedUser(final User updatedUser) {
    this.updatedUser = updatedUser;
  }

  @Override
  public void setUpdatedUserId(final String updatedUserId) {
    this.updatedUserId = updatedUserId;
  }

  @Override
  public void setUser(final User user) {
    this.user = user;
  }

  @Override
  public void setUserId(final String userId) {
    this.userId = userId;
  }
}
