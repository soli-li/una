package com.una.system.gateway.service;

import java.util.List;
import java.util.Objects;
import java.util.Set;

import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.loadbalancer.reactive.ReactorLoadBalancerExchangeFilterFunction;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.util.MimeTypeUtils;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClient.RequestBodySpec;

import com.una.common.Constants;
import com.una.common.pojo.Permissions;
import com.una.common.pojo.UrlPerm;
import com.una.common.pojo.User;
import com.una.common.pojo.UserSessionList;
import com.una.system.gateway.config.NormalUriWebFilter;

import reactor.core.publisher.Mono;

@Component
@RefreshScope
public class ManagerService {
  @Value("${app.cloud-provider.manager-url}")
  private String managerPath;

  @Autowired
  private ReactorLoadBalancerExchangeFilterFunction lbFunction;

  @Value("${app.encoding:UTF-8}")
  private String encoding;

  @Autowired
  private NormalUriWebFilter normalUriWebFilter;

  public Mono<User> fillUser(final User user) {
    Objects.requireNonNull(user, "parameter 'user' must not be null");
    final String uuid = MDC.get(Constants.LOG_MARK.toString());
    final String contentType = String.join("", MimeTypeUtils.APPLICATION_JSON_VALUE, ";charset=", this.encoding);

    final WebClient webClient = WebClient.builder().baseUrl(this.managerPath).filter(this.lbFunction).build();
    RequestBodySpec requestBodySpec = webClient.post().uri("/user/fill");
    requestBodySpec.contentType(MediaType.parseMediaType(contentType));
    requestBodySpec = requestBodySpec.header(this.normalUriWebFilter.getHeadUuidKey(), uuid);
    requestBodySpec.body(BodyInserters.fromValue(user));

    return requestBodySpec.retrieve().bodyToMono(User.class);
  }

  public Mono<List<Permissions>> findPermByRole(final Set<String> roleIds) {
    Assert.isTrue(!CollectionUtils.isEmpty(roleIds), "parameter 'roleIds' must not be null");
    final String uuid = MDC.get(Constants.LOG_MARK.toString());
    final String contentType = String.join("", MimeTypeUtils.APPLICATION_JSON_VALUE, ";charset=", this.encoding);

    final WebClient webClient = WebClient.builder().baseUrl(this.managerPath).filter(this.lbFunction).build();
    RequestBodySpec requestBodySpec = webClient.post().uri("/perm/findByRole");
    requestBodySpec.contentType(MediaType.parseMediaType(contentType));
    requestBodySpec = requestBodySpec.header(this.normalUriWebFilter.getHeadUuidKey(), uuid);
    requestBodySpec.body(BodyInserters.fromValue(roleIds));

    return requestBodySpec.retrieve().bodyToMono(new ParameterizedTypeReference<List<Permissions>>() {
    });
  }

  public Mono<List<UrlPerm>> findUrlByPerm(final Set<String> permIds) {
    Assert.isTrue(!CollectionUtils.isEmpty(permIds), "parameter 'permIds' must not be null");
    final String uuid = MDC.get(Constants.LOG_MARK.toString());
    final String contentType = String.join("", MimeTypeUtils.APPLICATION_JSON_VALUE, ";charset=", this.encoding);

    final WebClient webClient = WebClient.builder().baseUrl(this.managerPath).filter(this.lbFunction).build();
    RequestBodySpec requestBodySpec = webClient.post().uri("/url/findByPerm");
    requestBodySpec.contentType(MediaType.parseMediaType(contentType));
    requestBodySpec = requestBodySpec.header(this.normalUriWebFilter.getHeadUuidKey(), uuid);
    requestBodySpec.body(BodyInserters.fromValue(permIds));

    return requestBodySpec.retrieve().bodyToMono(new ParameterizedTypeReference<List<UrlPerm>>() {
    });
  }

  public Mono<User> findUser(final String name) {
    Assert.hasText(name, "parameter 'name' must not be empty");
    final String uuid = MDC.get(Constants.LOG_MARK.toString());
    final String contentType = String.join("", Constants.MEDIA_TYPE_X_WWW_FORM_URLENCODED.toString(), ";charset=", this.encoding);

    final WebClient webClient = WebClient.builder().baseUrl(this.managerPath).filter(this.lbFunction).build();
    RequestBodySpec requestBodySpec = webClient.post().uri("/user/find");
    requestBodySpec = requestBodySpec.contentType(MediaType.parseMediaType(contentType));
    requestBodySpec = requestBodySpec.header(this.normalUriWebFilter.getHeadUuidKey(), uuid);
    requestBodySpec.body(BodyInserters.fromFormData("name", name));
    return requestBodySpec.retrieve().bodyToMono(User.class);
  }

  public Mono<UserSessionList> getUserSession(final String username) {
    Assert.hasText(username, "parameter 'username' must not be empty");
    final String uuid = MDC.get(Constants.LOG_MARK.toString());
    final String contentType = String.join("", Constants.MEDIA_TYPE_X_WWW_FORM_URLENCODED.toString(), ";charset=", this.encoding);

    final WebClient webClient = WebClient.builder().baseUrl(this.managerPath).filter(this.lbFunction).build();
    RequestBodySpec requestBodySpec = webClient.post().uri("/user/getUserSession");
    requestBodySpec = requestBodySpec.contentType(MediaType.parseMediaType(contentType));
    requestBodySpec = requestBodySpec.header(this.normalUriWebFilter.getHeadUuidKey(), uuid);
    requestBodySpec.body(BodyInserters.fromFormData("username", username));

    return requestBodySpec.retrieve().bodyToMono(UserSessionList.class);
  }

  public Mono<Boolean> passwordMatch(final String name, final String rawPassword) {
    Assert.hasText(name, "parameter 'name' must not be empty");
    Assert.hasText(rawPassword, "parameter 'rawPassword' must not be empty");
    final String uuid = MDC.get(Constants.LOG_MARK.toString());
    final String contentType = String.join("", Constants.MEDIA_TYPE_X_WWW_FORM_URLENCODED.toString(), ";charset=", this.encoding);

    final WebClient webClient = WebClient.builder().baseUrl(this.managerPath).filter(this.lbFunction).build();
    RequestBodySpec requestBodySpec = webClient.post().uri("/password/match");
    requestBodySpec = requestBodySpec.contentType(MediaType.parseMediaType(contentType));
    requestBodySpec = requestBodySpec.header(this.normalUriWebFilter.getHeadUuidKey(), uuid);
    requestBodySpec.body(BodyInserters.fromFormData("username", name).with("password", rawPassword));

    return requestBodySpec.retrieve().bodyToMono(Boolean.class);
  }

  public Mono<Void> updateUser(final User user) {
    Objects.requireNonNull(user, "parameter 'user' must not be null");
    final String uuid = MDC.get(Constants.LOG_MARK.toString());
    final String contentType = String.join("", MimeTypeUtils.APPLICATION_JSON_VALUE, ";charset=", this.encoding);

    final WebClient webClient = WebClient.builder().baseUrl(this.managerPath).filter(this.lbFunction).build();
    RequestBodySpec requestBodySpec = webClient.post().uri("/user/updateForLogin");
    requestBodySpec = requestBodySpec.contentType(MediaType.parseMediaType(contentType));
    requestBodySpec = requestBodySpec.header(this.normalUriWebFilter.getHeadUuidKey(), uuid);
    requestBodySpec.body(BodyInserters.fromValue(user));

    return requestBodySpec.retrieve().bodyToMono(Void.class);
  }
}
