package com.una.common.pojo;

import java.time.LocalDateTime;
import java.util.Set;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.una.common.Constants;

public class Role {
  private String id;
  private String companyId;
  private String authority;
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

  private Set<? extends User> userSet;
  private Set<? extends Group> groupSet;
  private User createdUser;
  private User updatedUser;
  private Company company;

  public String getAuthority() {
    return this.authority;
  }

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

  public Set<? extends Group> getGroupSet() {
    return this.groupSet;
  }

  public String getId() {
    return this.id;
  }

  public String getRemark() {
    return this.remark;
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

  public void setAuthority(final String authority) {
    this.authority = authority;
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

  public void setGroupSet(final Set<? extends Group> groupSet) {
    this.groupSet = groupSet;
  }

  public void setId(final String id) {
    this.id = id;
  }

  public void setRemark(final String remark) {
    this.remark = remark;
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
