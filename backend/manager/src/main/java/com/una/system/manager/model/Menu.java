package com.una.system.manager.model;

import java.time.LocalDateTime;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.una.common.Constants;

@Entity
@Table(name = "S_MENU")
public class Menu {
  @Id
  @Column(name = "ID")
  private String id;
  @Column(name = "NAME")
  private String name;
  @Column(name = "ICON")
  private String icon;
  @Column(name = "SORT")
  private Integer sort = 0;
  @Column(name = "FRONT_END_URI")
  private String frontEndUri;
  @Column(name = "PARENT_ID")
  private String parentId;
  @Column(name = "PERMISSIONS_ID")
  private String permissionsId;
  @Column(name = "STATUS")
  private String status = Constants.ENABLE.toString();
  @Column(name = "REMARK")
  private String remark;
  @Column(name = "CREATED_USER_ID")
  private String createdUserId;
  @Column(name = "CREATED_DATE")
  private LocalDateTime createdDate;

  @Transient
  private int level;

  @Transient
  private Menu parentMenu;
  @Transient
  private Permissions permissions;
  @Transient
  private User createdUser;
  @Transient
  private Set<Menu> childrenMenuSet;

  public Set<Menu> getChildrenMenuSet() {
    return this.childrenMenuSet;
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

  public String getFrontEndUri() {
    return this.frontEndUri;
  }

  public String getIcon() {
    return this.icon;
  }

  public String getId() {
    return this.id;
  }

  public int getLevel() {
    return this.level;
  }

  public String getName() {
    return this.name;
  }

  public String getParentId() {
    return this.parentId;
  }

  public Menu getParentMenu() {
    return this.parentMenu;
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

  public void setChildrenMenuSet(final Set<Menu> childrenMenuSet) {
    this.childrenMenuSet = childrenMenuSet;
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

  public void setFrontEndUri(final String frontEndUri) {
    this.frontEndUri = frontEndUri;
  }

  public void setIcon(final String icon) {
    this.icon = icon;
  }

  public void setId(final String id) {
    this.id = id;
  }

  public void setLevel(final int level) {
    this.level = level;
  }

  public void setName(final String name) {
    this.name = name;
  }

  public void setParentId(final String parentId) {
    this.parentId = parentId;
  }

  public void setParentMenu(final Menu parentMenu) {
    this.parentMenu = parentMenu;
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

}
