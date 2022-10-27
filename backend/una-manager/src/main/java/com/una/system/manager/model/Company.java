package com.una.system.manager.model;

import com.una.common.Constants;
import java.time.LocalDateTime;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

@Entity
@Table(name = "S_COMPANY")
public class Company extends com.una.common.pojo.Company {
  @Id
  @Column(name = "ID")
  private String id;
  @Column(name = "NAME")
  private String name;
  @Column(name = "SHORT_NAME")
  private String shortName;
  @Column(name = "LEGAL_PERSON")
  private String legalPerson;
  @Column(name = "ADDRESS")
  private String address;
  @Column(name = "REMARK")
  private String remark;
  @Column(name = "PW_POLICY_ID")
  private String pwPolicyId;
  @Column(name = "STATUS")
  private String status = Constants.ENABLE.toString();

  @Column(name = "CREATED_USER_ID")
  private String createdUserId;
  @Column(name = "CREATED_DATE")
  private LocalDateTime createdDate;
  @Column(name = "UPDATED_USER_ID")
  private String updatedUserId;
  @Column(name = "UPDATED_DATE")
  private LocalDateTime updatedDate;

  @Transient
  private User createdUser;
  @Transient
  private User updatedUser;
  @Transient
  private PasswordPolicy passwordPolicy;

  @Override
  public String getAddress() {
    return this.address;
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
  public String getLegalPerson() {
    return this.legalPerson;
  }

  @Override
  public String getName() {
    return this.name;
  }

  @Override
  public PasswordPolicy getPasswordPolicy() {
    return this.passwordPolicy;
  }

  @Override
  public String getPwPolicyId() {
    return this.pwPolicyId;
  }

  @Override
  public String getRemark() {
    return this.remark;
  }

  @Override
  public String getShortName() {
    return this.shortName;
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
  public void setAddress(final String address) {
    this.address = address;
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
  public void setLegalPerson(final String legalPerson) {
    this.legalPerson = legalPerson;
  }

  @Override
  public void setName(final String name) {
    this.name = name;
  }

  public void setPasswordPolicy(final PasswordPolicy passwordPolicy) {
    this.passwordPolicy = passwordPolicy;
  }

  @Override
  public void setPwPolicyId(final String pwPolicyId) {
    this.pwPolicyId = pwPolicyId;
  }

  @Override
  public void setRemark(final String remark) {
    this.remark = remark;
  }

  @Override
  public void setShortName(final String shortName) {
    this.shortName = shortName;
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

}
