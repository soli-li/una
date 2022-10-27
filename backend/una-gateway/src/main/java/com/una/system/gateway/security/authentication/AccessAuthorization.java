package com.una.system.gateway.security.authentication;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.una.common.pojo.Permissions;
import com.una.common.pojo.UrlPerm;
import com.una.common.pojo.User;
import com.una.common.utils.LogUtil;
import com.una.system.gateway.service.ManagerService;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.slf4j.Logger;
import org.springframework.http.server.RequestPath;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpRequest.Builder;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.authorization.ReactiveAuthorizationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.server.authorization.AuthorizationContext;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.CollectionUtils;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;

// 访问决策（是否允许访问）
public class AccessAuthorization implements ReactiveAuthorizationManager<AuthorizationContext> {
  private static final Logger LOGGER = LogUtil.getRunLogger();
  private final ManagerService managerService;
  private final String permIdHeaderName;
  private final ObjectMapper objectMapper;
  private final AntPathMatcher antPathMatcher = new AntPathMatcher();

  public AccessAuthorization(final String permIdHeaderName, final ManagerService managerService,
      final ObjectMapper objectMapper) {
    this.permIdHeaderName = permIdHeaderName;
    this.managerService = managerService;
    this.objectMapper = objectMapper;
  }

  @Override
  public Mono<AuthorizationDecision> check(final Mono<Authentication> authentication,
      final AuthorizationContext object) {
    final ServerHttpRequest request = object.getExchange().getRequest();
    final RequestPath path = request.getPath();

    final AuthorizationDecision decision = new AuthorizationDecision(false);
    return authentication.flatMap(auth -> {
      if (!auth.isAuthenticated()) {
        return Mono.just(decision);
      }
      if (auth instanceof final UsernamePasswordAuthenticationToken token
          && token.getDetails() instanceof final AuthenticationDetail detail) {
        final User user = detail.getUser();
        final Set<String> currentRoleId = user.getCurrentRoleId();
        if (CollectionUtils.isEmpty(currentRoleId)) {
          return Mono.just(decision);
        }

        final Mono<Set<String>> permIds = this.getPermIds(currentRoleId);
        final Mono<Tuple2<Set<String>, List<UrlPerm>>> urlPerm = this.getUrlPerm(permIds);
        final Mono<Tuple2<Tuple2<Set<String>, List<UrlPerm>>, Boolean>> hasPermission = this
            .hasPermission(urlPerm, path.value());

        return hasPermission.map(o -> {
          final Boolean b = o.getT2();
          if (!b) {
            return decision;
          } else {
            final Set<String> permIdsSet = o.getT1().getT1();
            Builder builder = request.mutate();
            builder = builder.headers(h -> h.remove(this.permIdHeaderName));
            builder = builder
                .headers(h -> h.set(this.permIdHeaderName, this.object2String(permIdsSet)));
            object.getExchange().mutate().request(builder.build());

            return new AuthorizationDecision(true);
          }
        });
      }
      return Mono.just(decision);
    });
  }

  private Mono<Set<String>> getPermIds(final Set<String> roleIds) {
    Mono<Optional<List<Permissions>>> mono = this.managerService.findPermByRole(roleIds)
        .flatMap(o -> Mono.just(Optional.ofNullable(o)));
    mono = mono.defaultIfEmpty(Optional.of(List.of()));
    final Mono<Set<String>> permIdsMono = mono.flatMap(o -> {
      final List<Permissions> permList = o.get();
      if (permList.isEmpty()) {
        return Mono.just(Set.of());
      }
      final Set<String> permIdSet = new HashSet<>();
      permList.forEach(p -> permIdSet.add(p.getId()));
      return Mono.just(permIdSet);
    });
    return permIdsMono;
  }

  private Mono<Tuple2<Set<String>, List<UrlPerm>>> getUrlPerm(final Mono<Set<String>> permIdsMono) {
    return permIdsMono.zipWhen(permIdsSet -> {
      if (permIdsSet.isEmpty()) {
        return Mono.just(List.of());
      }
      Mono<Optional<List<UrlPerm>>> mono = this.managerService.findUrlByPerm(permIdsSet)
          .flatMap(o -> Mono.just(Optional.ofNullable(o)));
      mono = mono.defaultIfEmpty(Optional.of(List.of()));
      final Mono<List<UrlPerm>> result = mono.map(o -> {
        if (o.isEmpty()) {
          return List.of();
        }
        return o.get();
      });

      return result;
    });
  }

  private Mono<Tuple2<Tuple2<Set<String>, List<UrlPerm>>, Boolean>> hasPermission(
      final Mono<Tuple2<Set<String>, List<UrlPerm>>> urlPermMono, final String path) {
    return urlPermMono.zipWhen(o -> {
      final List<UrlPerm> urlList = o.getT2();
      Collections.sort(urlList, (o1, o2) -> Optional.ofNullable(o1.getSort()).orElse(0)
          .compareTo(Optional.ofNullable(o2.getSort()).orElse(0)));
      for (final UrlPerm url : urlList) {
        if (this.antPathMatcher.match(url.getUri(), path)) {
          return Mono.just(true);
        }
      }
      return Mono.just(false);
    });
  }

  private String object2String(final Set<? extends Object> set) {
    try {
      return this.objectMapper.writeValueAsString(set);
    } catch (final JsonProcessingException e) {
      AccessAuthorization.LOGGER.error("", e);
      return "[]";
    }
  }
}
