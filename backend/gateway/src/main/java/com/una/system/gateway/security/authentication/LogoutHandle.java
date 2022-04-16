package com.una.system.gateway.security.authentication;

import java.util.List;

import org.slf4j.Logger;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.server.WebFilterExchange;
import org.springframework.security.web.server.authentication.logout.ServerLogoutHandler;
import org.springframework.security.web.server.authentication.logout.ServerLogoutSuccessHandler;

import com.una.common.utils.LogUtil;
import com.una.system.gateway.service.SharingService;

import reactor.core.publisher.Mono;

public class LogoutHandle implements ServerLogoutHandler, ServerLogoutSuccessHandler {
  private static final Logger LOGGER = LogUtil.getRunLogger();
  private final String authHeaderName;
  private final SharingService sharingService;
  private final String encoding;

  public LogoutHandle(final String authHeaderName, final SharingService sharingService, final String encoding) {
    this.authHeaderName = authHeaderName;
    this.sharingService = sharingService;
    this.encoding = encoding;
  }

  @Override
  public Mono<Void> logout(final WebFilterExchange exchange, final Authentication authentication) {
    final HttpHeaders headers = exchange.getExchange().getRequest().getHeaders();
    final String sessionId = headers.getOrDefault(this.authHeaderName, List.of()).stream().findAny().orElse(null);
    final Mono<Void> mono = this.sharingService.removeUser(sessionId);
    mono.doOnError(e -> LogoutHandle.LOGGER.error("", e));
    return mono;
  }

  @Override
  public Mono<Void> onLogoutSuccess(final WebFilterExchange exchange, final Authentication authentication) {
    final String message = """
      {"statue": "success"}
      """;
    LogoutHandle.LOGGER.info("logout success, username: {}", authentication.getName());
    final ServerHttpResponse response = exchange.getExchange().getResponse();
    try {
      return this.writeResponse(response, message.getBytes(this.encoding));
    }
    catch (final Exception e) {
      LogoutHandle.LOGGER.error("", e);
      return Mono.error(e);
    }
  }

  private Mono<Void> writeResponse(final ServerHttpResponse response, final byte[] body) {
    response.getHeaders().setContentType(MediaType.APPLICATION_JSON);
    final DataBufferFactory bufferFactory = response.bufferFactory();

    return response.writeWith(Mono.just(bufferFactory.wrap(body)));
  }

}
