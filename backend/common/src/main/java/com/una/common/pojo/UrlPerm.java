package com.una.common.pojo;

import java.time.LocalDateTime;

import com.una.common.Constants;

public class UrlPerm {
  private String id;
  private String name;
  private String uri;
  private String permissionsId;
  private Integer sort = 0;
  private String status = Constants.ENABLE.toString();
  private String remark;
  private String createdUserId;
  private LocalDateTime createdDate;

  private Permissions permissions;
  private User createdUser;

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

  public Permissions getPermissions() {
    return this.permissions;
  }

  public String getPermissionsId() {
    return this.permissionsId;
  }

  public String getRemark() {
    return this.remark;
  }

  public Integer getSort() {
    return this.sort;
  }

  public String getStatus() {
    return this.status;
  }

  public String getUri() {
    return this.uri;
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

  public void setPermissions(final Permissions permissions) {
    this.permissions = permissions;
  }

  public void setPermissionsId(final String permissionsId) {
    this.permissionsId = permissionsId;
  }

  public void setRemark(final String remark) {
    this.remark = remark;
  }

  public void setSort(final Integer sort) {
    this.sort = sort;
  }

  public void setStatus(final String status) {
    this.status = status;
  }

  public void setUri(final String uri) {
    this.uri = uri;
  }

}
