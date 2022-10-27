package com.una.system.manager.microservices;

import com.una.common.Constants;
import com.una.common.pojo.EventMessage;
import com.una.system.manager.config.interceptor.NormalUriInterceptor;
import org.apache.commons.codec.binary.StringUtils;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.MimeTypeUtils;
import org.springframework.web.client.RestTemplate;

@Service
public class NotificationService {
  @Autowired
  private RestTemplate restTemplate;

  @Value("${spring.application.name}")
  private String source;

  @Value("${app.cloud-provider.notification-url}")
  private String notificationUrl;

  @Value("${app.encoding}")
  private String encoding;
  @Value("${app.running-mode:}")
  private String runningMode;

  @Autowired
  private NormalUriInterceptor normalUriInterceptor;

  private <T> void send(final EventMessage<T> event) {
    if (!StringUtils.equals(this.runningMode, "normal")) {
      return;
    }
    final String uuid = MDC.get(Constants.LOG_MARK.toString());
    final String contentType = String.join("", MimeTypeUtils.APPLICATION_JSON_VALUE, ";charset=",
        this.encoding);

    final HttpMethod method = HttpMethod.PUT;
    final HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.parseMediaType(contentType));
    headers.add(this.normalUriInterceptor.getHeadUuidKey(), uuid);

    final HttpEntity<Object> httpEntity = new HttpEntity<>(event, headers);
    final ResponseEntity<Object> responseEntity = this.restTemplate
        .exchange(this.notificationUrl + "/event/receive", method, httpEntity, Object.class);
    responseEntity.getBody();
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
