package com.una.common.pojo;

import java.time.LocalDateTime;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;

public class UserProfile {
  private String id;
  private String userId;
  private String realName;
  private String phone;
  private String email;
  private String avatar;
  private String avatarType;

  private String createdUserId;
  @JsonDeserialize(using = LocalDateTimeDeserializer.class)
  @JsonSerialize(using = LocalDateTimeSerializer.class)
  private LocalDateTime createdDate;
  private String updatedUserId;
  @JsonDeserialize(using = LocalDateTimeDeserializer.class)
  @JsonSerialize(using = LocalDateTimeSerializer.class)
  private LocalDateTime updatedDate;

  private User user;
  private User createdUser;
  private User updatedUser;

  public String getAvatar() {
    return this.avatar;
  }

  public String getAvatarType() {
    return this.avatarType;
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

  public String getEmail() {
    return this.email;
  }

  public String getId() {
    return this.id;
  }

  public String getPhone() {
    return this.phone;
  }

  public String getRealName() {
    return this.realName;
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

  public User getUser() {
    return this.user;
  }

  public String getUserId() {
    return this.userId;
  }

  public void setAvatar(final String avatar) {
    this.avatar = avatar;
  }

  public void setAvatarType(final String avatarType) {
    this.avatarType = avatarType;
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

  public void setEmail(final String email) {
    this.email = email;
  }

  public void setId(final String id) {
    this.id = id;
  }

  public void setPhone(final String phone) {
    this.phone = phone;
  }

  public void setRealName(final String realName) {
    this.realName = realName;
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

  public void setUser(final User user) {
    this.user = user;
  }

  public void setUserId(final String userId) {
    this.userId = userId;
  }
}
