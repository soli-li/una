package com.una.system.manager.config.interceptor;

import com.una.common.Constants;
import com.una.common.utils.LogUtil;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import javax.annotation.PostConstruct;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

@Component
public class NormalUriInterceptor implements HandlerInterceptor {
  private static final Logger LOGGER = LogUtil.getRunLogger();
  private static final String SPRING_REQUEST_OUT_UUID = "app.request.out.uuid";
  private static final String SPRING_USING_HEAD_UUID = "app.request.head.req-uuid-key";

  private static boolean outUuid = Boolean.TRUE;
  private static String headUuidKey = "";
  public static final String START_TIME = NormalUriInterceptor.class.getName() + "_startTime";
  public static final String END_TIME = NormalUriInterceptor.class.getName() + "_endTime";

  @Autowired
  private ApplicationContext applicationContext;

  @Value("${app.timeZoneOffset:'+8'}")
  private String timeZoneOffset;

  public String getHeadUuidKey() {
    return NormalUriInterceptor.headUuidKey;
  }

  @PostConstruct
  public void init() {
    final Environment environment = this.applicationContext.getEnvironment();
    String value = environment.getProperty(NormalUriInterceptor.SPRING_REQUEST_OUT_UUID);
    final String defaultValue = Boolean.TRUE.toString();
    NormalUriInterceptor.LOGGER.info(
        "using spring properties key '{}', value is: {}, default value is: {}",
        NormalUriInterceptor.SPRING_REQUEST_OUT_UUID, value, defaultValue);
    NormalUriInterceptor.outUuid = Boolean.parseBoolean(
        environment.getProperty(NormalUriInterceptor.SPRING_REQUEST_OUT_UUID, defaultValue));

    value = environment.getProperty(NormalUriInterceptor.SPRING_USING_HEAD_UUID);
    NormalUriInterceptor.LOGGER.info("using spring properties key '{}', value is: {}",
        NormalUriInterceptor.SPRING_USING_HEAD_UUID, value);
    NormalUriInterceptor.headUuidKey = environment
        .getProperty(NormalUriInterceptor.SPRING_USING_HEAD_UUID);
  }

  public boolean isOutUuid() {
    return NormalUriInterceptor.outUuid;
  }

  public void markToMdc(final ServletRequest request) {
    if (!this.isOutUuid()) {
      return;
    }
    String uuid = "";

    if (request instanceof final HttpServletRequest httpRequest
        && StringUtils.hasText(NormalUriInterceptor.headUuidKey)) {
      uuid = Optional.ofNullable(httpRequest.getHeader(NormalUriInterceptor.headUuidKey))
          .orElse("");
    } else {
      uuid = UUID.randomUUID().toString().replaceAll("-", "");
    }
    MDC.put(Constants.LOG_MARK.toString(), uuid);
  }

  @Override
  public void postHandle(final HttpServletRequest request, final HttpServletResponse response,
      final Object handler, final ModelAndView modelAndView) throws Exception {
    final Object endTimeFlag = request.getAttribute(NormalUriInterceptor.END_TIME);
    if (Objects.isNull(endTimeFlag)) {
      final LocalDateTime localDateTime = (LocalDateTime) request
          .getAttribute(NormalUriInterceptor.START_TIME);
      final LocalDateTime startTime = Optional.ofNullable(localDateTime)
          .orElse(LocalDateTime.now());
      final LocalDateTime endTime = LocalDateTime.now();
      final long duration = endTime.toInstant(ZoneOffset.of(this.timeZoneOffset)).toEpochMilli()
          - startTime.toInstant(ZoneOffset.of(this.timeZoneOffset)).toEpochMilli();
      NormalUriInterceptor.LOGGER.info("requested done, request uri: {}, now is: {}, used {}ms",
          request.getRequestURI(),
          endTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS")), duration);
      request.setAttribute(NormalUriInterceptor.END_TIME, endTime);
    }
  }

  @Override
  public boolean preHandle(final HttpServletRequest request, final HttpServletResponse response,
      final Object handler) throws Exception {
    this.markToMdc(request);
    final Object startTimeFlag = request.getAttribute(NormalUriInterceptor.START_TIME);
    if (Objects.isNull(startTimeFlag)) {
      final LocalDateTime startTime = LocalDateTime.now();
      NormalUriInterceptor.LOGGER.info("request uri: {}, method: {}, now is: {}",
          request.getRequestURI(), request.getMethod(),
          startTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS")));
      request.setAttribute(NormalUriInterceptor.START_TIME, startTime);
    }
    return HandlerInterceptor.super.preHandle(request, response, handler);
  }

}
