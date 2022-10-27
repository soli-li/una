package com.una.system.manager.pojo.req;

import com.una.common.pojo.PaginationInfo;
import com.una.system.manager.model.Menu;

public class SearchMenu extends PaginationInfo<Menu> {
  private String id;
  private String name;
  private String eqFrontEndUri;

  @Override
  protected void appendValue() {
    this.buffStr("id", this.getId());
    this.buffStr("name", this.getName());
    this.buffStr("eqFrontEndUri", this.getEqFrontEndUri());
    super.appendValue();
  }

  public String getEqFrontEndUri() {
    return this.eqFrontEndUri;
  }

  public String getId() {
    return this.id;
  }

  public String getName() {
    return this.name;
  }

  public void setEqFrontEndUri(final String eqFrontEndUri) {
    this.eqFrontEndUri = eqFrontEndUri;
  }

  public void setId(final String id) {
    this.id = id;
  }

  public void setName(final String name) {
    this.name = name;
  }

}
