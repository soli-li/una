package com.una.system.manager.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import com.una.system.manager.pojo.RequestHeadKey;
import com.una.system.manager.pojo.SpecialId;
import com.una.system.manager.pojo.TransmissionInfo;

@Component
public class InitBean {
  @ConfigurationProperties("system-config.user.avatar")
  @RefreshScope
  @Bean(name = "avatarServer")
  public TransmissionInfo getAvatarServerInfo() {
    return new TransmissionInfo();
  }

  @ConfigurationProperties("app.request.head")
  @RefreshScope
  @Bean
  public RequestHeadKey getRequestHeadKey() {
    return new RequestHeadKey();
  }

  @ConfigurationProperties("app.special-id")
  @RefreshScope
  @Bean
  public SpecialId getSpecialId() {
    return new SpecialId();
  }
}
