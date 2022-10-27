package com.una.system.gateway.security;

import com.una.common.Constants;
import com.una.common.pojo.User;
import com.una.common.utils.LogUtil;
import com.una.system.gateway.security.authentication.AuthenticationDetail;
import com.una.system.gateway.security.authentication.UserGrantedAuthority;
import com.una.system.gateway.security.exception.UserNotFoundException;
import com.una.system.gateway.service.SharingService;
import java.net.InetSocketAddress;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpRequest.Builder;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.security.web.server.context.ServerSecurityContextRepository;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

public class SecurityContextRepository implements ServerSecurityContextRepository {
  private static final Logger LOGGER = LogUtil.getRunLogger();
  private final SharingService sharingService;
  private final String authHeaderName;
  private final String formLoginUrl;
  private final String userIdHeaderName;
  private final String companyIdHeaderName;

  public SecurityContextRepository(final String authHeaderName, final String userIdHeaderName,
      final String companyIdHeaderName, final SharingService sharingService,
      final String formLoginUrl) {
    this.authHeaderName = authHeaderName;
    this.companyIdHeaderName = companyIdHeaderName;
    this.userIdHeaderName = userIdHeaderName;
    this.sharingService = sharingService;
    this.formLoginUrl = formLoginUrl;
  }

  @Override
  public Mono<SecurityContext> load(final ServerWebExchange exchange) {
    final HttpHeaders headers = exchange.getRequest().getHeaders();
    final String sessionId = headers.getOrDefault(this.authHeaderName, List.of()).stream().findAny()
        .orElse(null);
    final SecurityContext securityContext = new SecurityContextImpl();
    final String uri = exchange.getRequest().getURI().getPath();
    if (StringUtils.equals(uri, this.formLoginUrl)) {
      final UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken("",
          "");
      token.setAuthenticated(false);
      securityContext.setAuthentication(token);
      return Mono.just(securityContext);
    } else if (StringUtils.isBlank(sessionId)) {
      return Mono.error(new UserNotFoundException("session id is empty"));
    }

    final ServerHttpRequest request = exchange.getRequest();
    final Mono<Optional<User>> mono = this.sharingService.loadUser(sessionId)
        .flatMap(u -> Mono.just(Optional.of(u))).defaultIfEmpty(Optional.empty());
    return mono.flatMap(optional -> {
      if (optional.isEmpty()) {
        throw new RuntimeException("cannot found user");
      }
      final User user = optional.get();
      final Set<String> currentRoleIdSet = Optional.ofNullable(user.getCurrentRoleId())
          .orElse(Set.of());

      final Set<GrantedAuthority> authoritySet = new HashSet<>();
      currentRoleIdSet.forEach(o -> authoritySet.add(new UserGrantedAuthority(o)));

      final UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(
          user.getUsername(), "", authoritySet);
      final AuthenticationDetail detail = new AuthenticationDetail("");
      detail.setUser(user);
      token.setDetails(detail);
      securityContext.setAuthentication(token);

      Builder builder = request.mutate();
      builder = builder.headers(h -> h.remove(this.userIdHeaderName));
      builder = builder.headers(h -> h.set(this.userIdHeaderName, user.getId()));
      builder = builder.headers(h -> h.remove(this.companyIdHeaderName));
      builder = builder.headers(h -> h.set(this.companyIdHeaderName, user.getCompanyId()));
      exchange.mutate().request(builder.build());

      return Mono.just(securityContext);
    }).doOnError(e -> {
      SecurityContextRepository.LOGGER.error("", e);
      throw new UserNotFoundException(e);
    });
  }

  @Override
  public Mono<Void> save(final ServerWebExchange exchange, final SecurityContext context) {
    if (context
        .getAuthentication() instanceof final UsernamePasswordAuthenticationToken authentication
        && authentication.getDetails() instanceof final AuthenticationDetail detail) {
      final User user = detail.getUser();

      final ServerHttpRequest request = exchange.getRequest();
      final HttpHeaders headers = request.getHeaders();
      final InetSocketAddress remoteAddress = request.getRemoteAddress();

      Mono<Optional<Map<String, String>>> sessionResult = this.sharingService
          .addUser(user, remoteAddress.getAddress().getHostAddress(),
              String.join(",", headers.get(HttpHeaders.USER_AGENT)))
          .flatMap(m -> Mono.just(Optional.of(m)));
      sessionResult = sessionResult.defaultIfEmpty(Optional.empty());
      sessionResult.doOnError(e -> SecurityContextRepository.LOGGER.error("", e));

      return sessionResult.flatMap(o -> {
        final Map<String, String> map = o.orElse(Map.of());
        if (StringUtils.isBlank(map.get(Constants.SESSION_KEY.toString()))) {
          throw new RuntimeException("session id is empty");
        }
        try {
          exchange.getResponse().getHeaders().add(this.authHeaderName,
              map.get(Constants.SESSION_KEY));
          return Mono.empty();
        } catch (final Exception e) {
          throw new RuntimeException("cannot generate session Id", e);
        }
      });
    }

    return Mono.error(new RuntimeException("not support authentication token type"));
  }

}
