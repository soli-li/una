package com.una.system.gateway.security.authentication;

import org.slf4j.Logger;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.server.authorization.ServerAccessDeniedHandler;
import org.springframework.web.server.ServerWebExchange;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.una.common.pojo.ResponseVO;
import com.una.common.utils.LogUtil;
import com.una.system.gateway.Constants;

import reactor.core.publisher.Mono;

// 鉴权失败（无权访问）
public class AccessDeniedHandler implements ServerAccessDeniedHandler {
  private static final Logger LOGGER = LogUtil.getRunLogger();
  private final ObjectMapper objectMapper;

  public AccessDeniedHandler(final ObjectMapper objectMapper) {
    this.objectMapper = objectMapper;
  }

  @Override
  public Mono<Void> handle(final ServerWebExchange exchange, final AccessDeniedException denied) {
    AccessDeniedHandler.LOGGER.error("no authorization, uri: {}", exchange.getRequest().getURI().toString(), denied);
    final ResponseVO<Object> vo = new ResponseVO<>();
    vo.setCode(Constants.NO_AUTHORIZATION.toString());
    vo.setMsg(denied.getMessage());
    final ServerHttpResponse response = exchange.getResponse();
    response.setStatusCode(HttpStatus.UNAUTHORIZED);
    try {
      final DataBuffer dataBuffer = response.bufferFactory().wrap(this.objectMapper.writeValueAsBytes(vo));
      response.writeWith(Mono.just(dataBuffer)).subscribe();
    }
    catch (final JsonProcessingException e) {
      AccessDeniedHandler.LOGGER.error("", e);
    }
    throw denied;
  }
}
