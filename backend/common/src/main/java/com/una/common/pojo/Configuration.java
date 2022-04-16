package com.una.common.pojo;

import java.time.LocalDateTime;

public class Configuration {
  private String id;
  private String companyId;
  private String name;
  private String confKey;
  private String confValue;
  private String valueType;
  private String status;
  private String remark;
  private String createdUserId;
  private LocalDateTime createdDate;

  private Company company;
  private User createdUser;

  public Company getCompany() {
    return this.company;
  }

  public String getCompanyId() {
    return this.companyId;
  }

  public String getConfKey() {
    return this.confKey;
  }

  public String getConfValue() {
    return this.confValue;
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

  public String getRemark() {
    return this.remark;
  }

  public String getStatus() {
    return this.status;
  }

  public String getValueType() {
    return this.valueType;
  }

  public void setCompany(final Company company) {
    this.company = company;
  }

  public void setCompanyId(final String companyId) {
    this.companyId = companyId;
  }

  public void setConfKey(final String confKey) {
    this.confKey = confKey;
  }

  public void setConfValue(final String confValue) {
    this.confValue = confValue;
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

  public void setStatus(final String status) {
    this.status = status;
  }

  public void setValueType(final String valueType) {
    this.valueType = valueType;
  }

}
