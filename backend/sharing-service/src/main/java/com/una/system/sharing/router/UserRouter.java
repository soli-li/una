package com.una.system.sharing.router;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.RequestPredicate;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

import com.una.system.sharing.handler.UserHandler;

@Configuration(proxyBeanMethods = false)
public class UserRouter {
  @Bean
  public RouterFunction<ServerResponse> userRouterFunction(final UserHandler userHandler) {
    final RequestPredicate contentTypeJson = RequestPredicates.accept(MediaType.APPLICATION_JSON);
    final RequestPredicate contentTypeText = RequestPredicates.accept(MediaType.TEXT_PLAIN);

    RouterFunction<ServerResponse> route = RouterFunctions.route(RequestPredicates.PUT("/user/addUser").and(contentTypeJson), userHandler::addUser);
    route = route.andRoute(RequestPredicates.GET("/user/getUserOrEmpty/{id}/{ignoreNotExist}").and(contentTypeText), userHandler::getUserOrEmpty);
    route = route.andRoute(RequestPredicates.DELETE("/user/removeUser/{id}").and(contentTypeText), userHandler::removeUser);
    route = route.andRoute(RequestPredicates.POST("/user/updateUser/{id}").and(contentTypeJson), userHandler::updateUser);
    route = route.andRoute(RequestPredicates.DELETE("/user/removeUserManage/{username}").and(contentTypeText), userHandler::removeUserManage);
    route = route.andRoute(RequestPredicates.POST("/user/updateUserManage").and(contentTypeJson), userHandler::updateUserManage);
    route = route.andRoute(RequestPredicates.GET("/user/getUserManage/{username}/{ignoreNotExist}").and(contentTypeText), userHandler::getUserManage);

    return route;
  }

}
