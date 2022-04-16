package com.una.system.manager.pojo.req;

import java.util.List;

public class PermissionsRelation {
  private String companyId;
  private List<String> permissionsList;
  private List<String> roleList;

  public String getCompanyId() {
    return this.companyId;
  }

  public List<String> getPermissionsList() {
    return this.permissionsList;
  }

  public List<String> getRoleList() {
    return this.roleList;
  }

  public void setCompanyId(final String companyId) {
    this.companyId = companyId;
  }

  public void setPermissionsList(final List<String> permissionsList) {
    this.permissionsList = permissionsList;
  }

  public void setRoleList(final List<String> roleList) {
    this.roleList = roleList;
  }

}
