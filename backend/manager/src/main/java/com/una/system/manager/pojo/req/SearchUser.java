package com.una.system.manager.pojo.req;

import com.una.common.pojo.PaginationInfo;
import com.una.system.manager.model.User;

public class SearchUser extends PaginationInfo<User> {
  private String name;
  private String companyName;
  private String companyId;
  private String realName;
  private String eqName;

  @Override
  protected void appendValue() {
    this.buffStr("name", this.getName());
    this.buffStr("companyId", this.getCompanyId());
    this.buffStr("companyName", this.getCompanyName());
    this.buffStr("realName", this.getRealName());
    this.buffStr("eqName", this.getEqName());
    super.appendValue();
  }

  public String getCompanyId() {
    return this.companyId;
  }

  public String getCompanyName() {
    return this.companyName;
  }

  public String getEqName() {
    return this.eqName;
  }

  public String getName() {
    return this.name;
  }

  public String getRealName() {
    return this.realName;
  }

  public void setCompanyId(final String companyId) {
    this.companyId = companyId;
  }

  public void setCompanyName(final String companyName) {
    this.companyName = companyName;
  }

  public void setEqName(final String eqName) {
    this.eqName = eqName;
  }

  public void setName(final String name) {
    this.name = name;
  }

  public void setRealName(final String realName) {
    this.realName = realName;
  }

}
