package com.una.system.manager.pojo;

public class RequestHeadKey {
  private String reqUuidKey;
  private String userIdKey;
  private String companyIdKey;
  private String permIdKey;

  public String getCompanyIdKey() {
    return this.companyIdKey;
  }

  public String getPermIdKey() {
    return this.permIdKey;
  }

  public String getReqUuidKey() {
    return this.reqUuidKey;
  }

  public String getUserIdKey() {
    return this.userIdKey;
  }

  public void setCompanyIdKey(final String companyIdKey) {
    this.companyIdKey = companyIdKey;
  }

  public void setPermIdKey(final String permIdKey) {
    this.permIdKey = permIdKey;
  }

  public void setReqUuidKey(final String reqUuidKey) {
    this.reqUuidKey = reqUuidKey;
  }

  public void setUserIdKey(final String userIdKey) {
    this.userIdKey = userIdKey;
  }

}
