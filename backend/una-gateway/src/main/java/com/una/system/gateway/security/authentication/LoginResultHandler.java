package com.una.system.gateway.security.authentication;

import com.una.common.Constants;
import com.una.common.pojo.User;
import com.una.common.utils.LogUtil;
import com.una.system.gateway.security.authentication.exception.AuthException;
import com.una.system.gateway.service.ManagerService;
import com.una.system.gateway.service.SharingService;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.server.WebFilterExchange;
import org.springframework.security.web.server.authentication.ServerAuthenticationFailureHandler;
import org.springframework.security.web.server.authentication.ServerAuthenticationSuccessHandler;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;

public class LoginResultHandler
    implements ServerAuthenticationSuccessHandler, ServerAuthenticationFailureHandler {

  private static final Logger LOGGER = LogUtil.getRunLogger();

  private final String encoding;
  private final ManagerService managerService;
  private final SharingService sharingService;
  private final String authHeaderName;

  public LoginResultHandler(final String encoding, final ManagerService managerService,
      final SharingService sharingService, final String authHeaderName) {
    this.encoding = encoding;
    this.managerService = managerService;
    this.sharingService = sharingService;
    this.authHeaderName = authHeaderName;
  }

  @Override
  public Mono<Void> onAuthenticationFailure(final WebFilterExchange webFilterExchange,
      final AuthenticationException exception) {
    final ServerHttpResponse response = webFilterExchange.getExchange().getResponse();
    // response.setStatusCode(HttpStatus.UNAUTHORIZED);
    final String format = """
        {"status": "failure", "errorCode": "%s", "errorMessage": "%s"}
        """;
    if (exception instanceof final AuthException authException) {
      try {
        LoginResultHandler.LOGGER.error("login failure, username: {}", authException.getUsername(),
            authException);
        final User user = authException.getUser();
        Mono<Object> mono = Mono.just(new Object());
        if (Objects.nonNull(user)) {
          this.managerService.updateUser(user).subscribe();
          if (!user.isCredentialsNonExpired()) {
            mono = this.sharingService
                .generateShortSession(List.of(Constants.TEMP_ACTION_EXPIRED, user.getUsername()))
                .zipWhen(map -> {
                  if (StringUtils.isNotBlank(map.get(Constants.SESSION_KEY))) {
                    final HttpHeaders headers = response.getHeaders();
                    headers.add(this.authHeaderName, map.get(Constants.SESSION_KEY));
                  }
                  return Mono.just(new Object());
                }).map(Tuple2::getT2);
          }
        }
        final String errorCode = authException.getErrorCode();
        final String errorMessage = authException.getMessage();
        final byte[] message = String.format(format, errorCode, errorMessage)
            .getBytes(this.encoding);
        return mono.flatMap(o -> this.writeResponse(response, message));
      } catch (final Exception e) {
        LoginResultHandler.LOGGER.error("", e);
      }
    }
    LoginResultHandler.LOGGER.error("login failure", exception);

    return Mono.empty();
  }

  @Override
  public Mono<Void> onAuthenticationSuccess(final WebFilterExchange webFilterExchange,
      final Authentication authentication) {
    final String message = """
        {"status": "success"}
        """;
    LoginResultHandler.LOGGER.info("login success, username: {}", authentication.getName());
    final ServerHttpResponse response = webFilterExchange.getExchange().getResponse();

    if (authentication instanceof final UsernamePasswordAuthenticationToken token
        && token.getDetails() instanceof final AuthenticationDetail detail) {
      final User user = detail.getUser();
      if (Objects.nonNull(user)) {
        user.setFailureCount(0);
        user.setLastLoginDate(LocalDateTime.now());
        this.managerService.updateUser(user).subscribe();
      }
    }

    try {
      return this.writeResponse(response, message.getBytes(this.encoding));
    } catch (final Exception e) {
      response.getHeaders().setBearerAuth("");
      response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR);
      LoginResultHandler.LOGGER.error("", e);
    }
    return Mono.empty();
  }

  private Mono<Void> writeResponse(final ServerHttpResponse response, final byte[] body) {
    response.getHeaders().setContentType(MediaType.APPLICATION_JSON);
    final DataBufferFactory bufferFactory = response.bufferFactory();

    return response.writeWith(Mono.just(bufferFactory.wrap(body)));
  }

}
