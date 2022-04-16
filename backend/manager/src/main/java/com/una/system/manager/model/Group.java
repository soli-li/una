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
@Table(name = "S_GROUP")
public class Group extends com.una.common.pojo.Group {
  @Id
  @Column(name = "ID")
  private String id;
  @Column(name = "COMPANY_ID")
  private String companyId;
  @Column(name = "PARENT_ID")
  private String parentId;
  @Column(name = "NAME")
  private String name;
  @Column(name = "STATUS")
  private String status = Constants.ENABLE.toString();
  @Column(name = "REMARK")
  private String remark;

  @Column(name = "CREATED_USER_ID")
  private String createdUserId;
  @Column(name = "CREATED_DATE")
  private LocalDateTime createdDate;
  @Column(name = "UPDATED_USER_ID")
  private String updatedUserId;
  @Column(name = "UPDATED_DATE")
  private LocalDateTime updatedDate;

  @Transient
  private Group parentGroup;
  @Transient
  private Set<? extends com.una.common.pojo.User> userSet;
  @Transient
  private Set<? extends com.una.common.pojo.Role> roleSet;
  @Transient
  private User createdUser;
  @Transient
  private User updatedUser;
  @Transient
  private Company company;

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
  public String getId() {
    return this.id;
  }

  @Override
  public String getName() {
    return this.name;
  }

  @Override
  public Group getParentGroup() {
    return this.parentGroup;
  }

  @Override
  public String getParentId() {
    return this.parentId;
  }

  @Override
  public String getRemark() {
    return this.remark;
  }

  @Override
  public Set<? extends com.una.common.pojo.Role> getRoleSet() {
    return this.roleSet;
  }

  @Override
  public String getStatus() {
    return this.status;
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
  public Set<? extends com.una.common.pojo.User> getUserSet() {
    return this.userSet;
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
  public void setId(final String id) {
    this.id = id;
  }

  @Override
  public void setName(final String name) {
    this.name = name;
  }

  public void setParentGroup(final Group parentGroup) {
    this.parentGroup = parentGroup;
  }

  @Override
  public void setParentId(final String parentId) {
    this.parentId = parentId;
  }

  @Override
  public void setRemark(final String remark) {
    this.remark = remark;
  }

  @Override
  public void setRoleSet(final Set<? extends com.una.common.pojo.Role> roleSet) {
    this.roleSet = roleSet;
  }

  @Override
  public void setStatus(final String status) {
    this.status = status;
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
  public void setUserSet(final Set<? extends com.una.common.pojo.User> userSet) {
    this.userSet = userSet;
  }

}
