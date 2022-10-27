package com.una.system.sharing.config;

import com.una.system.sharing.pojo.TimeoutInfo;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
public class InitBean {
  @ConfigurationProperties("app.session-timeout")
  @Bean
  @RefreshScope
  public TimeoutInfo getTimeoutInfo() {
    return new TimeoutInfo();
  }
}
