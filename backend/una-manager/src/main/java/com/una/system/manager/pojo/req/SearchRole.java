package com.una.system.manager.pojo.req;

import com.una.common.pojo.PaginationInfo;
import com.una.system.manager.model.Role;

public class SearchRole extends PaginationInfo<Role> {
  private String authority;
  private String companyName;
  private String status;
  private String companyId;

  private String eqAuthority;

  @Override
  protected void appendValue() {
    this.buffStr("authority", this.getAuthority());
    this.buffStr("companyName", this.getCompanyName());
    this.buffStr("status", this.getStatus());
    this.buffStr("companyId", this.getCompanyId());
    this.buffStr("eqAuthority", this.getEqAuthority());
    super.appendValue();
  }

  public String getAuthority() {
    return this.authority;
  }

  public String getCompanyId() {
    return this.companyId;
  }

  public String getCompanyName() {
    return this.companyName;
  }

  public String getEqAuthority() {
    return this.eqAuthority;
  }

  public String getStatus() {
    return this.status;
  }

  public void setAuthority(final String authority) {
    this.authority = authority;
  }

  public void setCompanyId(final String companyId) {
    this.companyId = companyId;
  }

  public void setCompanyName(final String companyName) {
    this.companyName = companyName;
  }

  public void setEqAuthority(final String eqAuthority) {
    this.eqAuthority = eqAuthority;
  }

  public void setStatus(final String status) {
    this.status = status;
  }
}
