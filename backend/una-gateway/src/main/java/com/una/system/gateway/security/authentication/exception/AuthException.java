package com.una.system.gateway.security.authentication.exception;

import com.una.common.pojo.User;

public class AuthException
    extends org.springframework.security.core.AuthenticationException {

  private static final long serialVersionUID = -3652810782361399252L;

  private final String errorCode;
  private String username;
  private User user;

  public AuthException(final String username, final String errorCode, final String msg) {
    this(username, errorCode, msg, null);
  }

  public AuthException(final String username, final String errorCode, final String msg,
      final Throwable cause) {
    super(msg, cause);
    this.errorCode = errorCode;
    this.username = username;
  }

  public String getErrorCode() {
    return this.errorCode;
  }

  public User getUser() {
    return this.user;
  }

  public String getUsername() {
    return this.username;
  }

  public void setUser(final User user) {
    this.user = user;
  }

  public void setUsername(final String username) {
    this.username = username;
  }

}
