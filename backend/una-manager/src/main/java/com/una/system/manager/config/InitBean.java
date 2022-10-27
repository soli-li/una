package com.una.system.manager.config;

import com.una.system.manager.pojo.RequestHeadKey;
import com.una.system.manager.pojo.SpecialId;
import com.una.system.manager.pojo.TransmissionInfo;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class InitBean {
  @ConfigurationProperties("system-config.user.avatar")
  @RefreshScope
  @Bean(name = "avatarServer")
  public TransmissionInfo getAvatarServerInfo() {
    return new TransmissionInfo();
  }

  @ConfigurationProperties("app.request.head")
  @Bean
  public RequestHeadKey getRequestHeadKey() {
    return new RequestHeadKey();
  }

  @ConfigurationProperties("app.special-id")
  @Bean
  public SpecialId getSpecialId() {
    return new SpecialId();
  }

  @LoadBalanced
  @Bean
  RestTemplate restTemplate() {
    final RestTemplate restTemplate = new RestTemplate();
    return restTemplate;
  }
}
