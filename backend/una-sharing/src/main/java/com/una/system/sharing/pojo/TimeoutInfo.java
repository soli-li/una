package com.una.system.sharing.pojo;

import org.springframework.cloud.context.config.annotation.RefreshScope;

@RefreshScope
public class TimeoutInfo {
  private long user;
  private long shortTerm;

  public long getShortTerm() {
    return this.shortTerm;
  }

  public long getUser() {
    return this.user;
  }

  public void setShortTerm(final long shortTerm) {
    this.shortTerm = shortTerm;
  }

  public void setUser(final long user) {
    this.user = user;
  }

}
