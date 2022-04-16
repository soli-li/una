package com.una.system.gateway.config;

import java.util.Map;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.reactive.error.ErrorWebExceptionHandler;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.ServerWebExchange;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.una.common.Constants;
import com.una.common.pojo.ResponseVO;
import com.una.common.utils.LogUtil;
import com.una.system.gateway.security.exception.UserNotFoundException;

import reactor.core.publisher.Mono;

@Component
@Order(-1)
public class GlobalHandlerException implements ErrorWebExceptionHandler {

  private static class ExceptionResponse extends ResponseVO<Object> {
    private String mark;

    public String getMark() {
      return this.mark;
    }

    public void setMark(final String mark) {
      this.mark = mark;
    }
  }

  private static final Logger LOGGER = LogUtil.getRunLogger();

  @Autowired
  private ObjectMapper objectMapper;

  @Override
  public Mono<Void> handle(final ServerWebExchange exchange, final Throwable ex) {
    final Map<String, String> contextMap = MDC.getCopyOfContextMap();
    final String marks = Optional.ofNullable(contextMap).orElse(Map.of()).get(Constants.LOG_MARK.toString());
    final ExceptionResponse exRes = new ExceptionResponse();

    exRes.setMark(marks);
    exRes.setCode(Constants.RESP_FAILURE_CODE.toString());

    final ServerHttpRequest request = exchange.getRequest();
    final ServerHttpResponse response = exchange.getResponse();
    if (ex instanceof final UserNotFoundException unf) {
      exRes.setCode(Constants.RESP_FAILURE_CODE_NO_SESSION.toString());
      exRes.setMsg("cannot found session");
      response.setRawStatusCode(HttpStatus.UNAUTHORIZED.value());
    }
    if (ex instanceof final ResponseStatusException responseEx) {
      response.setRawStatusCode(responseEx.getRawStatusCode());
    }
    else if (response.getStatusCode() == HttpStatus.OK) {
      response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR);
    }

    GlobalHandlerException.LOGGER.error("request exception, uri: {}, marks: {}, ex class: {}", request.getPath(), marks, ex.getClass().getName(), ex);
    try {
      final DataBuffer dataBuffer = response.bufferFactory().wrap(this.objectMapper.writeValueAsBytes(exRes));
      return response.writeWith(Mono.just(dataBuffer));
    }
    catch (final JsonProcessingException e) {
      GlobalHandlerException.LOGGER.error("ExceptionResponse cannot serialization, mark: {}, msg: {}", exRes.getMark(), exRes.getMsg(), e);
      final String errorMessage = """
{"msg": "system exception!", "marks": "%s"}
""";
      final DataBuffer dataBuffer = response.bufferFactory().wrap(errorMessage.getBytes());
      return response.writeWith(Mono.just(dataBuffer));
    }
  }

}
