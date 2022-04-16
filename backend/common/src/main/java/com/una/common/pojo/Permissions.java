package com.una.common.pojo;

import java.time.LocalDateTime;
import java.util.Set;

import com.una.common.Constants;

public class Permissions {
  private String id;
  private String name;
  private String status = Constants.ENABLE.toString();
  private String remark;
  private String createdUserId;
  private LocalDateTime createdDate;
  private String updatedUserId;
  private LocalDateTime updatedDate;

  private User createdUser;
  private User updatedUser;
  private Set<? extends Role> roleSet;

  public LocalDateTime getCreatedDate() {
    return this.createdDate;
  }

  public User getCreatedUser() {
    return this.createdUser;
  }

  public String getCreatedUserId() {
    return this.createdUserId;
  }

  public String getId() {
    return this.id;
  }

  public String getName() {
    return this.name;
  }

  public String getRemark() {
    return this.remark;
  }

  public Set<? extends Role> getRoleSet() {
    return this.roleSet;
  }

  public String getStatus() {
    return this.status;
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

  public void setCreatedDate(final LocalDateTime createdDate) {
    this.createdDate = createdDate;
  }

  public void setCreatedUser(final User createdUser) {
    this.createdUser = createdUser;
  }

  public void setCreatedUserId(final String createdUserId) {
    this.createdUserId = createdUserId;
  }

  public void setId(final String id) {
    this.id = id;
  }

  public void setName(final String name) {
    this.name = name;
  }

  public void setRemark(final String remark) {
    this.remark = remark;
  }

  public void setRoleSet(final Set<? extends Role> roleSet) {
    this.roleSet = roleSet;
  }

  public void setStatus(final String status) {
    this.status = status;
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

}
