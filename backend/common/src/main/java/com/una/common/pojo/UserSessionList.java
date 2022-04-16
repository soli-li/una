package com.una.common.pojo;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;

public class UserSessionList {
  public static class SessionInfo {
    private String sessionId;
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private LocalDateTime createdTime;
    private User user;
    private String userAgent;
    private String ip;

    public LocalDateTime getCreatedTime() {
      return this.createdTime;
    }

    public String getIp() {
      return this.ip;
    }

    public String getSessionId() {
      return this.sessionId;
    }

    public User getUser() {
      return this.user;
    }

    public String getUserAgent() {
      return this.userAgent;
    }

    public void setCreatedTime(final LocalDateTime createdTime) {
      this.createdTime = createdTime;
    }

    public void setIp(final String ip) {
      this.ip = ip;
    }

    public void setSessionId(final String sessionId) {
      this.sessionId = sessionId;
    }

    public void setUser(final User user) {
      this.user = user;
    }

    public void setUserAgent(final String userAgent) {
      this.userAgent = userAgent;
    }

  }

  private Map<String, SessionInfo> sessionMap = new HashMap<>();
  private String key;
  private String companyId;

  public String getCompanyId() {
    return this.companyId;
  }

  public String getKey() {
    return this.key;
  }

  public Map<String, SessionInfo> getSessionMap() {
    return this.sessionMap;
  }

  public void setCompanyId(final String companyId) {
    this.companyId = companyId;
  }

  public void setKey(final String key) {
    this.key = key;
  }

  public void setSessionMap(final Map<String, SessionInfo> sessionMap) {
    this.sessionMap = sessionMap;
  }

}
