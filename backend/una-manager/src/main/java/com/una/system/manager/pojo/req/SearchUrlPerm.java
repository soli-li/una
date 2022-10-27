package com.una.system.manager.pojo.req;

import com.una.common.pojo.PaginationInfo;
import com.una.system.manager.model.Group;

public class SearchUrlPerm extends PaginationInfo<Group> {
  private String name;
  private String uri;
  private String perm;
  private String status;

  @Override
  protected void appendValue() {
    this.buffStr("name", this.getName());
    this.buffStr("uri", this.getUri());
    this.buffStr("permissions", this.getPerm());
    this.buffStr("status", this.getStatus());
    super.appendValue();
  }

  public String getName() {
    return this.name;
  }

  public String getPerm() {
    return this.perm;
  }

  public String getStatus() {
    return this.status;
  }

  public String getUri() {
    return this.uri;
  }

  public void setName(final String name) {
    this.name = name;
  }

  public void setPerm(final String perm) {
    this.perm = perm;
  }

  public void setStatus(final String status) {
    this.status = status;
  }

  public void setUri(final String uri) {
    this.uri = uri;
  }

}
