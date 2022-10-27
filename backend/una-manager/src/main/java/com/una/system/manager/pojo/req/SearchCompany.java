package com.una.system.manager.pojo.req;

import com.una.common.pojo.PaginationInfo;
import com.una.system.manager.model.Group;

public class SearchCompany extends PaginationInfo<Group> {
  private String name;
  private String shortName;
  private String legalPerson;
  private String status;
  private String id;

  private String eqName;
  private String eqShortNamd;

  @Override
  protected void appendValue() {
    this.buffStr("name", this.getName());
    this.buffStr("shortName", this.getShortName());
    this.buffStr("legalPerson", this.getLegalPerson());
    this.buffStr("status", this.getStatus());
    this.buffStr("id", this.getId());
    this.buffStr("eqName", this.getEqName());
    this.buffStr("eqShortNamd", this.getEqShortNamd());
    super.appendValue();
  }

  public String getEqName() {
    return this.eqName;
  }

  public String getEqShortNamd() {
    return this.eqShortNamd;
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

  public String getShortName() {
    return this.shortName;
  }

  public String getStatus() {
    return this.status;
  }

  public void setEqName(final String eqName) {
    this.eqName = eqName;
  }

  public void setEqShortNamd(final String eqShortNamd) {
    this.eqShortNamd = eqShortNamd;
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

  public void setShortName(final String shortName) {
    this.shortName = shortName;
  }

  public void setStatus(final String status) {
    this.status = status;
  }

}
