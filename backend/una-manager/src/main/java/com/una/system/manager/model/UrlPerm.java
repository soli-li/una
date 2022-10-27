package com.una.system.manager.model;

import com.una.common.Constants;
import java.time.LocalDateTime;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

@Entity
@Table(name = "S_URL_PERM")
public class UrlPerm extends com.una.common.pojo.UrlPerm {
  @Id
  @Column(name = "ID")
  private String id;
  @Column(name = "NAME")
  private String name;
  @Column(name = "URI")
  private String uri;
  @Column(name = "PERMISSIONS_ID")
  private String permissionsId;
  @Column(name = "SORT")
  private Integer sort = 0;
  @Column(name = "STATUS")
  private String status = Constants.ENABLE.toString();
  @Column(name = "REMARK")
  private String remark;
  @Column(name = "CREATED_USER_ID")
  private String createdUserId;
  @Column(name = "CREATED_DATE")
  private LocalDateTime createdDate;

  @Transient
  private Permissions permissions;
  @Transient
  private User createdUser;

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
  public String getId() {
    return this.id;
  }

  @Override
  public String getName() {
    return this.name;
  }

  @Override
  public Permissions getPermissions() {
    return this.permissions;
  }

  @Override
  public String getPermissionsId() {
    return this.permissionsId;
  }

  @Override
  public String getRemark() {
    return this.remark;
  }

  @Override
  public Integer getSort() {
    return this.sort;
  }

  @Override
  public String getStatus() {
    return this.status;
  }

  @Override
  public String getUri() {
    return this.uri;
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
  public void setId(final String id) {
    this.id = id;
  }

  @Override
  public void setName(final String name) {
    this.name = name;
  }

  public void setPermissions(final Permissions permissions) {
    this.permissions = permissions;
  }

  @Override
  public void setPermissionsId(final String permissionsId) {
    this.permissionsId = permissionsId;
  }

  @Override
  public void setRemark(final String remark) {
    this.remark = remark;
  }

  @Override
  public void setSort(final Integer sort) {
    this.sort = sort;
  }

  @Override
  public void setStatus(final String status) {
    this.status = status;
  }

  @Override
  public void setUri(final String uri) {
    this.uri = uri;
  }

}
