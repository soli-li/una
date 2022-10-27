package com.una.system.manager.pojo.req;

import com.una.common.pojo.PaginationInfo;
import com.una.system.manager.model.Group;

public class SearchGroup extends PaginationInfo<Group> {
  private String name;
  private String companyName;
  private String status;
  private String companyId;
  private String parentId;
  private String eqName;

  @Override
  protected void appendValue() {
    this.buffStr("name", this.getName());
    this.buffStr("companyName", this.getCompanyName());
    this.buffStr("status", this.getStatus());
    this.buffStr("companyId", this.getCompanyId());
    this.buffStr("parentId", this.getParentId());
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

  public String getParentId() {
    return this.parentId;
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

  public void setEqName(final String eqName) {
    this.eqName = eqName;
  }

  public void setName(final String name) {
    this.name = name;
  }

  public void setParentId(final String parentId) {
    this.parentId = parentId;
  }

  public void setStatus(final String status) {
    this.status = status;
  }

}
