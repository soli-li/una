package com.una.system.gateway.security.authentication;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.una.common.pojo.ResponseVo;
import com.una.common.utils.LogUtil;
import com.una.system.gateway.Constants;
import org.slf4j.Logger;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.server.ServerAuthenticationEntryPoint;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

// 未认证处理（访问需要权限的资源）
public class AuthenticationEntryPoint implements ServerAuthenticationEntryPoint {
  private static Logger LOGGER = LogUtil.getRunLogger();
  private final ObjectMapper objectMapper;

  public AuthenticationEntryPoint(final ObjectMapper objectMapper) {
    this.objectMapper = objectMapper;
  }

  @Override
  public Mono<Void> commence(final ServerWebExchange exchange, final AuthenticationException ex) {
    AuthenticationEntryPoint.LOGGER.error("not authentication, uri: {}",
        exchange.getRequest().getURI().toString(), ex);
    final ResponseVo<Object> vo = new ResponseVo<>();
    vo.setCode(Constants.NOT_AUTHENTICATION.toString());
    vo.setMsg(ex.getMessage());
    final ServerHttpResponse response = exchange.getResponse();
    response.setStatusCode(HttpStatus.UNAUTHORIZED);
    try {
      final DataBuffer dataBuffer = response.bufferFactory()
          .wrap(this.objectMapper.writeValueAsBytes(vo));
      response.writeWith(Mono.just(dataBuffer)).subscribe();
    } catch (final JsonProcessingException e) {
      AuthenticationEntryPoint.LOGGER.error("", e);
    }
    throw ex;
  }

}
