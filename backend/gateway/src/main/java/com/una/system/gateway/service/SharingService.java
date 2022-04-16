package com.una.system.gateway.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import org.apache.commons.codec.binary.StringUtils;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.loadbalancer.reactive.ReactorLoadBalancerExchangeFilterFunction;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.util.MimeTypeUtils;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClient.RequestBodySpec;
import org.springframework.web.reactive.function.client.WebClient.ResponseSpec;

import com.una.common.Constants;
import com.una.common.pojo.Configuration;
import com.una.common.pojo.ResponseVO;
import com.una.common.pojo.User;
import com.una.system.gateway.config.NormalUriWebFilter;

import reactor.core.publisher.Mono;

@Component
@RefreshScope
public class SharingService {
  @Value("${app.cloud-provider.sharing-url}")
  private String sharingUrl;

  @Value("${app.encoding:UTF-8}")
  private String encoding;

  @Autowired
  private NormalUriWebFilter normalUriWebFilter;

  @Autowired
  private ReactorLoadBalancerExchangeFilterFunction lbFunction;

  public Mono<Map<String, String>> addUser(final User user, final String ip, final String userAgent) {
    Objects.requireNonNull(user, "parameter 'user' must not be null");
    final String uuid = MDC.get(Constants.LOG_MARK.toString());
    final String contentType = String.join("", MimeTypeUtils.APPLICATION_JSON_VALUE, ";charset=", this.encoding);

    final WebClient webClient = WebClient.builder().baseUrl(this.sharingUrl).filter(this.lbFunction).build();
    RequestBodySpec requestBodySpec = webClient.put().uri("/user/addUser");
    requestBodySpec = requestBodySpec.contentType(MediaType.parseMediaType(contentType));
    requestBodySpec = requestBodySpec.header(this.normalUriWebFilter.getHeadUuidKey(), uuid);
    requestBodySpec = requestBodySpec.header(HttpHeaders.USER_AGENT, userAgent);
    requestBodySpec = requestBodySpec.header(Constants.REMOTE_ADDRESS, ip);
    requestBodySpec.body(BodyInserters.fromValue(user));

    final ParameterizedTypeReference<ResponseVO<Map<String, String>>> forType = new ParameterizedTypeReference<>() {
    };
    final Mono<ResponseVO<Map<String, String>>> bodyMono = requestBodySpec.retrieve().bodyToMono(forType);
    return bodyMono.map(resp -> {
      if (StringUtils.equals(Constants.RESP_SUCCESS_CODE, resp.getCode())) {
        return resp.getData();
      }
      throw new RuntimeException(String.format("code: {}, msg: {}", resp.getCode(), resp.getMsg()));
    });
  }

  public Mono<Map<String, String>> generateShortSession(final Object object) {
    final String uuid = MDC.get(Constants.LOG_MARK.toString());
    final String contentType = String.join("", MimeTypeUtils.APPLICATION_JSON_VALUE, ";charset=", this.encoding);

    final WebClient webClient = WebClient.builder().baseUrl(this.sharingUrl).filter(this.lbFunction).build();
    RequestBodySpec requestBodySpec = webClient.put().uri("/shortTerm/add");
    requestBodySpec = requestBodySpec.contentType(MediaType.parseMediaType(contentType));
    requestBodySpec = requestBodySpec.header(this.normalUriWebFilter.getHeadUuidKey(), uuid);
    requestBodySpec.body(BodyInserters.fromValue(object));

    final ParameterizedTypeReference<ResponseVO<Map<String, String>>> forType = new ParameterizedTypeReference<>() {
    };
    final Mono<ResponseVO<Map<String, String>>> bodyMono = requestBodySpec.retrieve().bodyToMono(forType);
    return bodyMono.map(resp -> {
      if (StringUtils.equals(Constants.RESP_SUCCESS_CODE, resp.getCode())) {
        return resp.getData();
      }
      throw new RuntimeException(String.format("code: {}, msg: {}", resp.getCode(), resp.getMsg()));
    });
  }

  public Mono<List<Configuration>> getConfigurationList(final String companyId) {
    Assert.hasText(companyId, "parameter 'companyId' must not be empty");
    final String uuid = MDC.get(Constants.LOG_MARK.toString());
    final String contentType = String.join("", MimeTypeUtils.TEXT_PLAIN_VALUE, ";charset=", this.encoding);

    final WebClient webClient = WebClient.builder().baseUrl(this.sharingUrl).filter(this.lbFunction).build();
    final ResponseSpec responseSpec = webClient.get().uri("/conf/getOrEmpty/{companyId}/{ignoreNotExist}", companyId, "true")
      .header(HttpHeaders.CONTENT_TYPE, contentType).header(this.normalUriWebFilter.getHeadUuidKey(), uuid).retrieve();

    final ParameterizedTypeReference<ResponseVO<List<Configuration>>> forType = new ParameterizedTypeReference<>() {
    };
    final Mono<ResponseVO<List<Configuration>>> bodyMono = responseSpec.bodyToMono(forType);
    return bodyMono.map(resp -> {
      if (StringUtils.equals(Constants.RESP_SUCCESS_CODE, resp.getCode())) {
        return Optional.ofNullable(resp.getData()).orElse(List.of());
      }
      throw new RuntimeException(String.format("code: {}, msg: {}", resp.getCode(), resp.getMsg()));
    });
  }

  public Mono<Object> getShortSession(final String sessionId) {
    final String uuid = MDC.get(Constants.LOG_MARK.toString());
    final String contentType = String.join("", MimeTypeUtils.TEXT_PLAIN_VALUE, ";charset=", this.encoding);

    final WebClient webClient = WebClient.builder().baseUrl(this.sharingUrl).filter(this.lbFunction).build();
    final ResponseSpec responseSpec = webClient.get().uri("/shortTerm/getOrEmpty/{id}/{ignoreNotExist}", sessionId, "true")
      .header(HttpHeaders.CONTENT_TYPE, contentType).header(this.normalUriWebFilter.getHeadUuidKey(), uuid).retrieve();

    final ParameterizedTypeReference<ResponseVO<Object>> forType = new ParameterizedTypeReference<>() {
    };
    final Mono<ResponseVO<Object>> bodyMono = responseSpec.bodyToMono(forType);
    return bodyMono.map(resp -> {
      if (StringUtils.equals(Constants.RESP_SUCCESS_CODE, resp.getCode())) {
        return resp.getData();
      }
      throw new RuntimeException(String.format("code: {}, msg: {}", resp.getCode(), resp.getMsg()));
    });
  }

  public Mono<User> loadUser(final String sessionId) {
    Assert.hasText(sessionId, "parameter 'sessionId' must not be empty");
    final String uuid = MDC.get(Constants.LOG_MARK.toString());
    final String contentType = String.join("", MimeTypeUtils.TEXT_PLAIN_VALUE, ";charset=", this.encoding);

    final Map<String, String> param = new HashMap<>();
    param.put(Constants.SESSION_KEY.toString(), sessionId);
    final WebClient webClient = WebClient.builder().baseUrl(this.sharingUrl).filter(this.lbFunction).build();
    final ResponseSpec responseSpec = webClient.get().uri("/user/getUserOrEmpty/{id}/{ignoreNotExist}", sessionId, "false")
      .header(HttpHeaders.CONTENT_TYPE, contentType).header(this.normalUriWebFilter.getHeadUuidKey(), uuid).retrieve();

    final ParameterizedTypeReference<ResponseVO<User>> forType = new ParameterizedTypeReference<>() {
    };
    final Mono<ResponseVO<User>> bodyMono = responseSpec.bodyToMono(forType);
    return bodyMono.map(resp -> {
      if (StringUtils.equals(Constants.RESP_SUCCESS_CODE, resp.getCode())) {
        return resp.getData();
      }
      throw new RuntimeException(String.format("code: {}, msg: {}", resp.getCode(), resp.getMsg()));
    });
  }

  public Mono<Void> removeUser(final String sessionId) {
    Assert.hasText(sessionId, "parameter 'sessionId' must not be empty");
    final String uuid = MDC.get(Constants.LOG_MARK.toString());
    final String contentType = String.join("", MimeTypeUtils.APPLICATION_JSON_VALUE, ";charset=", this.encoding);

    final Map<String, String> param = new HashMap<>();
    param.put(Constants.SESSION_KEY.toString(), sessionId);
    final WebClient webClient = WebClient.builder().baseUrl(this.sharingUrl).filter(this.lbFunction).build();
    final ResponseSpec responseSpec = webClient.delete().uri("/user/removeUser/{id}", sessionId).header(this.normalUriWebFilter.getHeadUuidKey(), uuid)
      .header(HttpHeaders.CONTENT_TYPE, contentType).retrieve();

    final ParameterizedTypeReference<ResponseVO<Object>> forType = new ParameterizedTypeReference<>() {
    };
    final Mono<ResponseVO<Object>> bodyMono = responseSpec.bodyToMono(forType);
    return bodyMono.flatMap(resp -> {
      if (StringUtils.equals(Constants.RESP_SUCCESS_CODE, resp.getCode())) {
        return Mono.empty();
      }
      throw new RuntimeException(String.format("code: {}, msg: {}", resp.getCode(), resp.getMsg()));
    });
  }
}
