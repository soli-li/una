package com.una.system.manager.pojo.req;

import com.una.common.pojo.PaginationInfo;
import com.una.system.manager.model.User;

public class SearchConf extends PaginationInfo<User> {
  private String name;
  private String companyName;
  private String companyId;
  private String confKey;
  private String status;

  private String eqConfKey;

  @Override
  protected void appendValue() {
    this.buffStr("name", this.getName());
    this.buffStr("companyName", this.getCompanyName());
    this.buffStr("companyId", this.getCompanyId());
    this.buffStr("confKey", this.getConfKey());
    this.buffStr("status", this.getStatus());
    this.buffStr("eqConfKey", this.getEqConfKey());
    super.appendValue();
  }

  public String getCompanyId() {
    return this.companyId;
  }

  public String getCompanyName() {
    return this.companyName;
  }

  public String getConfKey() {
    return this.confKey;
  }

  public String getEqConfKey() {
    return this.eqConfKey;
  }

  public String getName() {
    return this.name;
  }

  public String getStatus() {
    return this.status;
  }

  public void setCompanyId(final String companyId) {
    this.companyId = companyId;
  }

  public void setCompanyName(final String companyName) {
    this.companyName = companyName;
  }

  public void setConfKey(final String confKey) {
    this.confKey = confKey;
  }

  public void setEqConfKey(final String eqConfKey) {
    this.eqConfKey = eqConfKey;
  }

  public void setName(final String name) {
    this.name = name;
  }

  public void setStatus(final String status) {
    this.status = status;
  }

}
