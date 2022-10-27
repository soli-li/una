package com.una.common.pojo;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.una.common.Constants;
import java.time.LocalDateTime;
import java.util.Set;

public class Group {
  private String id;
  private String companyId;
  private String parentId;
  private String name;
  private String status = Constants.ENABLE.toString();
  private String remark;

  private String createdUserId;
  @JsonDeserialize(using = LocalDateTimeDeserializer.class)
  @JsonSerialize(using = LocalDateTimeSerializer.class)
  private LocalDateTime createdDate;
  private String updatedUserId;
  @JsonDeserialize(using = LocalDateTimeDeserializer.class)
  @JsonSerialize(using = LocalDateTimeSerializer.class)
  private LocalDateTime updatedDate;

  private Group parentGroup;
  private Set<? extends User> userSet;
  private Set<? extends Role> roleSet;
  private User createdUser;
  private User updatedUser;
  private Company company;

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

  public String getId() {
    return this.id;
  }

  public String getName() {
    return this.name;
  }

  public Group getParentGroup() {
    return this.parentGroup;
  }

  public String getParentId() {
    return this.parentId;
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

  public Set<? extends User> getUserSet() {
    return this.userSet;
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

  public void setId(final String id) {
    this.id = id;
  }

  public void setName(final String name) {
    this.name = name;
  }

  public void setParentGroup(final Group parentGroup) {
    this.parentGroup = parentGroup;
  }

  public void setParentId(final String parentId) {
    this.parentId = parentId;
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

  public void setUserSet(final Set<? extends User> userSet) {
    this.userSet = userSet;
  }

}
