package com.una.system.gateway.security.exception;

public class UserNotFoundException extends RuntimeException {
  private static final long serialVersionUID = -3764241821496498133L;

  public UserNotFoundException() {
  }

  public UserNotFoundException(final String message) {
    super(message);
  }

  public UserNotFoundException(final String message, final Throwable cause) {
    super(message, cause);
  }

  public UserNotFoundException(final String message, final Throwable cause,
      final boolean enableSuppression, final boolean writableStackTrace) {
    super(message, cause, enableSuppression, writableStackTrace);
  }

  public UserNotFoundException(final Throwable cause) {
    super(cause);
  }

}
