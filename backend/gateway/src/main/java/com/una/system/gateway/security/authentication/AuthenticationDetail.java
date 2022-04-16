package com.una.system.gateway.security.authentication;

import com.una.common.pojo.User;

public class AuthenticationDetail {
  private final String captchaCode;
  private User user;

  public AuthenticationDetail(final String captchaCode) {
    this.captchaCode = captchaCode;
  }

  public String getCaptchaCode() {
    return this.captchaCode;
  }

  public User getUser() {
    return this.user;
  }

  public void setUser(final User user) {
    this.user = user;
  }

}
