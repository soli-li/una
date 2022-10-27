package com.una.system.gateway.security.authentication;

import org.springframework.security.core.GrantedAuthority;

public class UserGrantedAuthority implements GrantedAuthority {
  private static final long serialVersionUID = 4356283556118047878L;
  private final String authority;

  public UserGrantedAuthority(final String authority) {
    this.authority = authority;
  }

  @Override
  public String getAuthority() {
    return this.authority;
  }

}
