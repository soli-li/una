package com.una.system.manager.model;

import java.time.LocalDateTime;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "S_CP_RECORD")
public class CpRecord {
  @Id
  @Column(name = "ID")
  private String id;
  @Column(name = "USERNAME")
  private String username;
  @Column(name = "PASSWORD")
  private String password;
  @Column(name = "CHANGED_DATE")
  private LocalDateTime changedDate;

  public LocalDateTime getChangedDate() {
    return this.changedDate;
  }

  public String getId() {
    return this.id;
  }

  public String getPassword() {
    return this.password;
  }

  public String getUsername() {
    return this.username;
  }

  public void setChangedDate(final LocalDateTime changedDate) {
    this.changedDate = changedDate;
  }

  public void setId(final String id) {
    this.id = id;
  }

  public void setPassword(final String password) {
    this.password = password;
  }

  public void setUsername(final String username) {
    this.username = username;
  }

}
