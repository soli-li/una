package com.una.common.pojo;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.una.common.Constants;
import java.time.LocalDateTime;

public class Company {
  private String id;
  private String name;
  private String shortName;
  private String legalPerson;
  private String address;
  private String remark;
  private String pwPolicyId;
  private String status = Constants.ENABLE.toString();

  private String createdUserId;
  @JsonDeserialize(using = LocalDateTimeDeserializer.class)
  @JsonSerialize(using = LocalDateTimeSerializer.class)
  private LocalDateTime createdDate;
  private String updatedUserId;
  @JsonDeserialize(using = LocalDateTimeDeserializer.class)
  @JsonSerialize(using = LocalDateTimeSerializer.class)
  private LocalDateTime updatedDate;

  private User createdUser;
  private User updatedUser;
  private PasswordPolicy passwordPolicy;

  public String getAddress() {
    return this.address;
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

  public String getLegalPerson() {
    return this.legalPerson;
  }

  public String getName() {
    return this.name;
  }

  public PasswordPolicy getPasswordPolicy() {
    return this.passwordPolicy;
  }

  public String getPwPolicyId() {
    return this.pwPolicyId;
  }

  public String getRemark() {
    return this.remark;
  }

  public String getShortName() {
    return this.shortName;
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

  public void setAddress(final String address) {
    this.address = address;
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

  public void setLegalPerson(final String legalPerson) {
    this.legalPerson = legalPerson;
  }

  public void setName(final String name) {
    this.name = name;
  }

  public void setPasswordPolicy(final PasswordPolicy passwordPolicy) {
    this.passwordPolicy = passwordPolicy;
  }

  public void setPwPolicyId(final String pwPolicyId) {
    this.pwPolicyId = pwPolicyId;
  }

  public void setRemark(final String remark) {
    this.remark = remark;
  }

  public void setShortName(final String shortName) {
    this.shortName = shortName;
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
