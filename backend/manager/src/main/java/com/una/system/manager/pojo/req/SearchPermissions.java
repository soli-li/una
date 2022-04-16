package com.una.system.manager.pojo.req;

import com.una.common.pojo.PaginationInfo;
import com.una.system.manager.model.Group;

public class SearchPermissions extends PaginationInfo<Group> {
  private String name;
  private String status;

  @Override
  protected void appendValue() {
    this.buffStr("name", this.getName());
    this.buffStr("status", this.getStatus());
    super.appendValue();
  }

  public String getName() {
    return this.name;
  }

  public String getStatus() {
    return this.status;
  }

  public void setName(final String name) {
    this.name = name;
  }

  public void setStatus(final String status) {
    this.status = status;
  }

}
