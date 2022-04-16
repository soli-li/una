package com.una.system.manager.model;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.una.system.manager.model.converter.BooleanAttributeConverter;

@Entity
@Table(name = "S_PW_POLICY")
public class PasswordPolicy extends com.una.common.pojo.PasswordPolicy {
  @Id
  @Column(name = "ID")
  private String id;
  @Column(name = "LABEL")
  private String label;
  @Column(name = "DESCRIPTION")
  private String description;
  @Convert(converter = BooleanAttributeConverter.class)
  @Column(name = "LETTERS")
  private boolean letters = false; // 是否包含字母 Y/N
  @Convert(converter = BooleanAttributeConverter.class)
  @Column(name = "CASE_SENSITIVE")
  private boolean caseSensitive = false; // 是否区分大小写 Y/N
  @Convert(converter = BooleanAttributeConverter.class)
  @Column(name = "DIGITALS")
  private boolean digitals = false; // 是否包含数字 Y/N
  @Convert(converter = BooleanAttributeConverter.class)
  @Column(name = "NON_ALPHANUMERIC")
  private boolean nonAlphanumeric = false; // 是否包含符号 Y/N
  @Column(name = "LENGTH")
  private int length = 0; // 密码长度，0表示不限
  @Column(name = "MAXIMUM_AGE")
  private int maximumAge = 0; // 密码有效期，单位天，0表示无限
  @Column(name = "REPEAT_COUNT")
  private int repeatCount = 0; // 不能与前几次密码相同，0表示无限
  @Column(name = "TRIES_COUNT")
  private int triesCount = 0; // 密码试错次数，0表示无限
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
  public String getDescription() {
    return this.description;
  }

  @Override
  public String getId() {
    return this.id;
  }

  @Override
  public String getLabel() {
    return this.label;
  }

  @Override
  public int getLength() {
    return this.length;
  }

  @Override
  public int getMaximumAge() {
    return this.maximumAge;
  }

  @Override
  public int getRepeatCount() {
    return this.repeatCount;
  }

  @Override
  public int getTriesCount() {
    return this.triesCount;
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
  public boolean isCaseSensitive() {
    return this.caseSensitive;
  }

  @Override
  public boolean isDigitals() {
    return this.digitals;
  }

  @Override
  public boolean isLetters() {
    return this.letters;
  }

  @Override
  public boolean isNonAlphanumeric() {
    return this.nonAlphanumeric;
  }

  @Override
  public void setCaseSensitive(final boolean caseSensitive) {
    this.caseSensitive = caseSensitive;
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
  public void setDescription(final String description) {
    this.description = description;
  }

  @Override
  public void setDigitals(final boolean digitals) {
    this.digitals = digitals;
  }

  @Override
  public void setId(final String id) {
    this.id = id;
  }

  @Override
  public void setLabel(final String label) {
    this.label = label;
  }

  @Override
  public void setLength(final int length) {
    this.length = length;
  }

  @Override
  public void setLetters(final boolean letters) {
    this.letters = letters;
  }

  @Override
  public void setMaximumAge(final int maximumAge) {
    this.maximumAge = maximumAge;
  }

  @Override
  public void setNonAlphanumeric(final boolean nonAlphanumeric) {
    this.nonAlphanumeric = nonAlphanumeric;
  }

  @Override
  public void setRepeatCount(final int repeatCount) {
    this.repeatCount = repeatCount;
  }

  @Override
  public void setTriesCount(final int triesCount) {
    this.triesCount = triesCount;
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
