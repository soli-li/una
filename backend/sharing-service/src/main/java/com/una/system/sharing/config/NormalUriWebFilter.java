package com.una.system.sharing.config;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.ApplicationContext;
import org.springframework.core.env.Environment;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;

import com.una.common.Constants;
import com.una.common.utils.LogUtil;

import reactor.core.publisher.Mono;

@Component
@RefreshScope
public class NormalUriWebFilter implements WebFilter {
  private static final Logger LOGGER = LogUtil.getRunLogger();
  private static final String SPRING_REQUEST_OUT_UUID = "app.request.out.uuid";
  private static final String SPRING_USING_HEAD_UUID = "app.request.head.req-uuid-key";

  private static boolean outUuid = Boolean.TRUE;
  private static String headUuidKey = "";
  public static final String START_TIME = NormalUriWebFilter.class.getName() + "_startTime";
  public static final String END_TIME = NormalUriWebFilter.class.getName() + "_endTime";
  @Autowired
  private ApplicationContext applicationContext;

  @Value("${app.timeZoneOffset:'+8'}")
  private String timeZoneOffset;

  @Override
  public Mono<Void> filter(final ServerWebExchange exchange, final WebFilterChain chain) {
    this.preHandle(exchange);
    final Mono<Void> mono = chain.filter(exchange).then(Mono.fromRunnable(() -> {
      this.postHandle(exchange);
    }));
    return mono;
  }

  public String getHeadUuidKey() {
    return NormalUriWebFilter.headUuidKey;
  }

  @PostConstruct
  public void init() {
    final Environment environment = this.applicationContext.getEnvironment();
    String value = environment.getProperty(NormalUriWebFilter.SPRING_REQUEST_OUT_UUID);
    final String defaultValue = Boolean.TRUE.toString();
    NormalUriWebFilter.LOGGER.info("using spring properties key '{}', value is: {}, default value is: {}", NormalUriWebFilter.SPRING_REQUEST_OUT_UUID, value,
      defaultValue);
    NormalUriWebFilter.outUuid = Boolean.parseBoolean(environment.getProperty(NormalUriWebFilter.SPRING_REQUEST_OUT_UUID, defaultValue));

    value = environment.getProperty(NormalUriWebFilter.SPRING_USING_HEAD_UUID);
    NormalUriWebFilter.LOGGER.info("using spring properties key '{}', value is: {}", NormalUriWebFilter.SPRING_USING_HEAD_UUID, value);
    NormalUriWebFilter.headUuidKey = environment.getProperty(NormalUriWebFilter.SPRING_USING_HEAD_UUID);
  }

  public boolean isOutUuid() {
    return NormalUriWebFilter.outUuid;
  }

  public void markToMdc(final ServerWebExchange exchange) {
    if (!this.isOutUuid()) {
      return;
    }
    String uuid = "";

    final ServerHttpRequest request = exchange.getRequest();
    final List<String> headUuidValue = request.getHeaders().get(NormalUriWebFilter.headUuidKey);
    if (StringUtils.hasText(this.getHeadUuidKey()) && !CollectionUtils.isEmpty(headUuidValue)) {
      uuid = headUuidValue.stream().findAny().orElse("");
    }
    else {
      uuid = UUID.randomUUID().toString().replaceAll("-", "");
      final String tempUuid = uuid;
      exchange.mutate().request(request.mutate().headers(h -> h.set(NormalUriWebFilter.headUuidKey, tempUuid)).build());
    }
    MDC.put(Constants.LOG_MARK.toString(), uuid);

  }

  private void postHandle(final ServerWebExchange exchange) {
    final Object endTimeFlag = exchange.getAttribute(NormalUriWebFilter.END_TIME);
    if (Objects.isNull(endTimeFlag)) {
      final LocalDateTime localDateTime = (LocalDateTime) exchange.getAttributes().get(NormalUriWebFilter.START_TIME);
      final LocalDateTime startTime = Optional.ofNullable(localDateTime).orElse(LocalDateTime.now());
      final LocalDateTime endTime = LocalDateTime.now();
      final long duration = endTime.toInstant(ZoneOffset.of(this.timeZoneOffset)).toEpochMilli()
        - startTime.toInstant(ZoneOffset.of(this.timeZoneOffset)).toEpochMilli();
      NormalUriWebFilter.LOGGER.info("requested done, request uri: {}, now is: {}, used {}ms", exchange.getRequest().getPath(),
        endTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS")), duration);
      exchange.getAttributes().put(NormalUriWebFilter.END_TIME, endTime);
    }

  }

  private void preHandle(final ServerWebExchange exchange) {
    this.markToMdc(exchange);
    final Object startTimeFlag = exchange.getAttribute(NormalUriWebFilter.START_TIME);
    if (Objects.isNull(startTimeFlag)) {
      final ServerHttpRequest request = exchange.getRequest();

      final LocalDateTime startTime = LocalDateTime.now();
      NormalUriWebFilter.LOGGER.info("request uri: {}, now is: {}", request.getPath(),
        startTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS")));
      exchange.getAttributes().put(NormalUriWebFilter.START_TIME, startTime);
    }
  }

}
