package com.una.system.manager.microservices;

import org.apache.commons.codec.binary.StringUtils;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.loadbalancer.reactive.ReactorLoadBalancerExchangeFilterFunction;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.MimeTypeUtils;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClient.RequestBodySpec;

import com.una.common.Constants;
import com.una.common.pojo.EventMessage;
import com.una.system.manager.config.interceptor.NormalUriInterceptor;

import reactor.core.publisher.Mono;

@Service
@RefreshScope
public class NotificationService {
  @Value("${spring.application.name}")
  private String source;

  @Value("${app.cloud-provider.notification-url}")
  private String notificationUrl;

  @Autowired
  private ReactorLoadBalancerExchangeFilterFunction lbFunction;

  @Value("${app.encoding}")
  private String encoding;
  @Value("${spring.profiles.active}")
  private String active;
  @Autowired
  private NormalUriInterceptor normalUriInterceptor;

  private <T> void send(final EventMessage<T> event) {
    if (StringUtils.equals(this.active, "test")) {
      return;
    }
    final String uuid = MDC.get(Constants.LOG_MARK.toString());
    final String contentType = String.join("", MimeTypeUtils.APPLICATION_JSON_VALUE, ";charset=", this.encoding);

    final WebClient webClient = WebClient.builder().baseUrl(this.notificationUrl).filter(this.lbFunction).build();
    RequestBodySpec requestBodySpec = webClient.put().uri("/event/receive");
    requestBodySpec = requestBodySpec.contentType(MediaType.parseMediaType(contentType));
    requestBodySpec = requestBodySpec.header(this.normalUriInterceptor.getHeadUuidKey(), uuid);
    requestBodySpec.body(BodyInserters.fromValue(event));

    final ParameterizedTypeReference<Object> forType = ParameterizedTypeReference.forType(Object.class);
    final Mono<Object> mono = requestBodySpec.retrieve().bodyToMono(forType);
    mono.block();
  }

  public <T> void send(final String type, final T body) {
    Assert.hasText(type, "parameter 'type' must not be empty");
    final EventMessage<T> event = new EventMessage<>();
    event.setBody(body);
    event.setSrouce(this.source);
    event.setType(type);

    this.send(event);
  }
}
