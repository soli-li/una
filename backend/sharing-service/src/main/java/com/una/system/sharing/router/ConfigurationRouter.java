package com.una.system.sharing.router;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.RequestPredicate;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

import com.una.system.sharing.handler.ConfigurationHandler;

@Configuration(proxyBeanMethods = false)
public class ConfigurationRouter {
  @Bean
  public RouterFunction<ServerResponse> configurationRouterFunction(final ConfigurationHandler configurationHandler) {
    final RequestPredicate contentTypeJson = RequestPredicates.accept(MediaType.APPLICATION_JSON);
    final RequestPredicate contentTypeText = RequestPredicates.accept(MediaType.TEXT_PLAIN);

    RouterFunction<ServerResponse> route = RouterFunctions.route(RequestPredicates.GET("/conf/getAll").and(contentTypeText), configurationHandler::getAll);
    route = route.andRoute(RequestPredicates.DELETE("/conf/remove/{companyId}").and(contentTypeText), configurationHandler::remove);
    route = route.andRoute(RequestPredicates.PUT("/conf/save/{companyId}").and(contentTypeJson), configurationHandler::save);
    route = route.andRoute(RequestPredicates.GET("/conf/getOrEmpty/{companyId}/{ignoreNotExist}").and(contentTypeText), configurationHandler::getOrEmpty);
    return route;
  }
}
