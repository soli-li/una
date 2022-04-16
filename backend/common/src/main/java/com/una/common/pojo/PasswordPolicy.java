package com.una.common.pojo;

import java.time.LocalDateTime;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;

public class PasswordPolicy {
  private String id;
  private String label;
  private String description;
  private boolean letters = false; // 是否包含字母 Y/N
  private boolean caseSensitive = false; // 是否区分大小写 Y/N
  private boolean digitals = false; // 是否包含数字 Y/N
  private boolean nonAlphanumeric = false; // 是否包含符号 Y/N
  private int length = 0; // 密码长度，0表示不限
  private int maximumAge = 0; // 密码有效期，单位天，0表示无限
  private int repeatCount = 0; // 不能与前几次密码相同，0表示无限
  private int triesCount = 0; // 密码试错次数，0表示无限
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

  public LocalDateTime getCreatedDate() {
    return this.createdDate;
  }

  public User getCreatedUser() {
    return this.createdUser;
  }

  public String getCreatedUserId() {
    return this.createdUserId;
  }

  public String getDescription() {
    return this.description;
  }

  public String getId() {
    return this.id;
  }

  public String getLabel() {
    return this.label;
  }

  public int getLength() {
    return this.length;
  }

  public int getMaximumAge() {
    return this.maximumAge;
  }

  public int getRepeatCount() {
    return this.repeatCount;
  }

  public int getTriesCount() {
    return this.triesCount;
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

  public boolean isCaseSensitive() {
    return this.caseSensitive;
  }

  public boolean isDigitals() {
    return this.digitals;
  }

  public boolean isLetters() {
    return this.letters;
  }

  public boolean isNonAlphanumeric() {
    return this.nonAlphanumeric;
  }

  public void setCaseSensitive(final boolean caseSensitive) {
    this.caseSensitive = caseSensitive;
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

  public void setDescription(final String description) {
    this.description = description;
  }

  public void setDigitals(final boolean digitals) {
    this.digitals = digitals;
  }

  public void setId(final String id) {
    this.id = id;
  }

  public void setLabel(final String label) {
    this.label = label;
  }

  public void setLength(final int length) {
    this.length = length;
  }

  public void setLetters(final boolean letters) {
    this.letters = letters;
  }

  public void setMaximumAge(final int maximumAge) {
    this.maximumAge = maximumAge;
  }

  public void setNonAlphanumeric(final boolean nonAlphanumeric) {
    this.nonAlphanumeric = nonAlphanumeric;
  }

  public void setRepeatCount(final int repeatCount) {
    this.repeatCount = repeatCount;
  }

  public void setTriesCount(final int triesCount) {
    this.triesCount = triesCount;
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
