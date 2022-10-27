package com.una.system.sharing.router;

import com.una.system.sharing.handler.ShortTermHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.RequestPredicate;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

@Configuration(proxyBeanMethods = false)
public class ShortTermRouter {
  @Bean
  public RouterFunction<ServerResponse> shortTermRouteFunction(
      final ShortTermHandler shortTermHandler) {
    final RequestPredicate contentTypeJson = RequestPredicates.accept(MediaType.APPLICATION_JSON);
    final RequestPredicate contentTypeText = RequestPredicates.accept(MediaType.TEXT_PLAIN);

    RouterFunction<ServerResponse> route = RouterFunctions
        .route(RequestPredicates.PUT("/shortTerm/add").and(contentTypeJson), shortTermHandler::add);
    route = route.andRoute(
        RequestPredicates.GET("/shortTerm/getOrEmpty/{id}/{ignoreNotExist}").and(contentTypeText),
        shortTermHandler::getOrEmpty);
    route = route.andRoute(RequestPredicates.DELETE("/shortTerm/remove/{id}").and(contentTypeText),
        shortTermHandler::remove);

    return route;
  }
}
