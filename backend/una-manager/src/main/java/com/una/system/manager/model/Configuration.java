package com.una.system.manager.model;

import com.una.common.pojo.Company;
import com.una.common.pojo.User;
import java.time.LocalDateTime;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

@Entity
@Table(name = "S_CONF")
public class Configuration extends com.una.common.pojo.Configuration {
  @Id
  @Column(name = "ID")
  private String id;
  @Column(name = "COMPANY_ID")
  private String companyId;
  @Column(name = "NAME")
  private String name;
  @Column(name = "CONF_KEY")
  private String confKey;
  @Column(name = "CONF_VALUE")
  private String confValue;
  @Column(name = "VALUE_TYPE")
  private String valueType;
  @Column(name = "STATUS")
  private String status;
  @Column(name = "REMARK")
  private String remark;
  @Column(name = "CREATED_USER_ID")
  private String createdUserId;
  @Column(name = "CREATED_DATE")
  private LocalDateTime createdDate;

  @Transient
  private Company company;
  @Transient
  private User createdUser;

  @Override
  public Company getCompany() {
    return this.company;
  }

  @Override
  public String getCompanyId() {
    return this.companyId;
  }

  @Override
  public String getConfKey() {
    return this.confKey;
  }

  @Override
  public String getConfValue() {
    return this.confValue;
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
  public String getRemark() {
    return this.remark;
  }

  @Override
  public String getStatus() {
    return this.status;
  }

  @Override
  public String getValueType() {
    return this.valueType;
  }

  @Override
  public void setCompany(final Company company) {
    this.company = company;
  }

  @Override
  public void setCompanyId(final String companyId) {
    this.companyId = companyId;
  }

  @Override
  public void setConfKey(final String confKey) {
    this.confKey = confKey;
  }

  @Override
  public void setConfValue(final String confValue) {
    this.confValue = confValue;
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
  public void setId(final String id) {
    this.id = id;
  }

  @Override
  public void setName(final String name) {
    this.name = name;
  }

  @Override
  public void setRemark(final String remark) {
    this.remark = remark;
  }

  @Override
  public void setStatus(final String status) {
    this.status = status;
  }

  @Override
  public void setValueType(final String valueType) {
    this.valueType = valueType;
  }

}
