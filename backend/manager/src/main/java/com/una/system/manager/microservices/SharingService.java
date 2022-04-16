package com.una.system.manager.microservices;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.MDC;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.loadbalancer.reactive.ReactorLoadBalancerExchangeFilterFunction;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.MimeTypeUtils;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClient.RequestBodySpec;
import org.springframework.web.reactive.function.client.WebClient.ResponseSpec;

import com.una.common.Constants;
import com.una.common.pojo.ResponseVO;
import com.una.common.pojo.UserSessionList;
import com.una.common.pojo.UserSessionList.SessionInfo;
import com.una.common.utils.LogUtil;
import com.una.system.manager.config.interceptor.NormalUriInterceptor;
import com.una.system.manager.model.Configuration;
import com.una.system.manager.model.User;

@Service
@RefreshScope
public class SharingService {

  @FunctionalInterface
  public interface UpdateUserSessionManage {
    boolean update(UserSessionList userSessionList);
  }

  private static final Logger LOGGER = LogUtil.getRunLogger();

  @Value("${spring.profiles.active}")
  private String active;

  @Value("${app.encoding}")
  private String encoding;

  @Autowired
  private NormalUriInterceptor normalUriInterceptor;
  @Value("${app.cloud-provider.sharing-url}")
  private String sharingUrl;

  @Autowired
  private ReactorLoadBalancerExchangeFilterFunction lbFunction;

  @Value("${app.security.auth-header:x-auth-token}")
  private String authHeaderName;

  public List<String> getAllConfiguration() {
    final String uuid = MDC.get(Constants.LOG_MARK.toString());
    final String contentType = String.join("", MimeTypeUtils.TEXT_PLAIN_VALUE, ";charset=", this.encoding);

    final ParameterizedTypeReference<ResponseVO<List<String>>> forType = new ParameterizedTypeReference<>() {
    };
    final WebClient webClient = WebClient.builder().baseUrl(this.sharingUrl).filter(this.lbFunction).build();
    final ResponseSpec retrieve = webClient.get().uri("/conf/getAll").header(HttpHeaders.CONTENT_TYPE, contentType)
      .header(this.normalUriInterceptor.getHeadUuidKey(), uuid).retrieve();

    final ResponseVO<List<String>> resp = retrieve.bodyToMono(forType).block();
    if (!StringUtils.equals(resp.getCode(), Constants.RESP_SUCCESS_CODE)) {
      throw new RuntimeException("cannot remove configuration list");
    }
    return resp.getData();
  }

  public UserSessionList getAndUpdateUserSessionList(final String username, final boolean ignoreNotExist, final boolean fillUser,
    final UpdateUserSessionManage updateUserSessionManage) {
    Assert.hasText(username, "parameter 'username' must not be empty");
    Objects.requireNonNull(updateUserSessionManage, "parameter 'updateUserSessionManage' must not be empty");

    final UserSessionList userSessionList = this.getUserSessionList(username, ignoreNotExist);
    if (Objects.isNull(userSessionList)) {
      return null;
    }
    final Map<String, SessionInfo> sessionMap = userSessionList.getSessionMap();
    final Map<String, SessionInfo> vaildSessionMap = new HashMap<>(sessionMap);
    final Map<String, SessionInfo> hasUserVaildSessionMap = new HashMap<>();
    vaildSessionMap.forEach((k, v) -> {
      final SessionInfo sessionInfo = new SessionInfo();
      BeanUtils.copyProperties(v, sessionInfo);
      hasUserVaildSessionMap.put(k, sessionInfo);
    });

    sessionMap.keySet().forEach(sessionId -> {
      final ResponseVO<User> responseVO = this.getUser(sessionId, true);
      final User user = responseVO.getData();
      if (Objects.isNull(user)) {
        vaildSessionMap.remove(sessionId);
        hasUserVaildSessionMap.remove(sessionId);
      }
      else if (fillUser) {
        final SessionInfo sessionInfo = hasUserVaildSessionMap.get(sessionId);
        sessionInfo.setUser(user);
        hasUserVaildSessionMap.put(sessionId, sessionInfo);
      }
    });
    if (vaildSessionMap.size() == 0) {
      this.removeUserManage(username);
      return null;
    }

    userSessionList.setSessionMap(hasUserVaildSessionMap);
    if (updateUserSessionManage.update(userSessionList)) {
      userSessionList.setSessionMap(vaildSessionMap);
      this.updateUserSessionList(userSessionList);
    }

    if (fillUser) {
      userSessionList.setSessionMap(hasUserVaildSessionMap);
    }
    else {
      userSessionList.setSessionMap(vaildSessionMap);
    }
    return userSessionList;
  }

  public List<Configuration> getConfigurationList(final String companyId) {
    Assert.hasText(companyId, "parameter 'companyId' must not be empty");

    final String uuid = MDC.get(Constants.LOG_MARK.toString());
    final String contentType = String.join("", MimeTypeUtils.TEXT_PLAIN_VALUE, ";charset=", this.encoding);

    final WebClient webClient = WebClient.builder().baseUrl(this.sharingUrl).filter(this.lbFunction).build();
    final ResponseSpec responseSpec = webClient.get().uri("/conf/getOrEmpty/{companyId}/{ignoreNotExist}", companyId, "true")
      .header(this.normalUriInterceptor.getHeadUuidKey(), uuid).header(HttpHeaders.CONTENT_TYPE, contentType).retrieve();
    final ParameterizedTypeReference<ResponseVO<List<Configuration>>> forType = new ParameterizedTypeReference<>() {
    };
    final ResponseVO<List<Configuration>> resp = responseSpec.bodyToMono(forType).block();
    if (!StringUtils.equals(resp.getCode(), Constants.RESP_SUCCESS_CODE)) {
      throw new RuntimeException("cannot update configuration list");
    }
    return resp.getData();
  }

  public ResponseVO<List<String>> getShortTerm(final String sessionId) {
    Assert.hasText(sessionId, "parameter 'sessionId' must not be empty");
    final String uuid = MDC.get(Constants.LOG_MARK.toString());
    final String contentType = String.join("", MimeTypeUtils.TEXT_PLAIN_VALUE, ";charset=", this.encoding);

    final WebClient webClient = WebClient.builder().baseUrl(this.sharingUrl).filter(this.lbFunction).build();
    final ResponseSpec responseSpec = webClient.get().uri("/shortTerm/getOrEmpty/{id}/{ignoreNotExist}", sessionId, "true")
      .header(HttpHeaders.CONTENT_TYPE, contentType).header(this.normalUriInterceptor.getHeadUuidKey(), uuid).retrieve();

    final ParameterizedTypeReference<ResponseVO<List<String>>> forType = new ParameterizedTypeReference<>() {
    };
    final ResponseVO<List<String>> resp = responseSpec.bodyToMono(forType).block();
    if (!StringUtils.equals(resp.getCode(), Constants.RESP_SUCCESS_CODE)) {
      throw new RuntimeException("cannot get short time");
    }
    return resp;
  }

  public ResponseVO<User> getUser(final String sessionId, final boolean ignore) {
    Assert.hasText(sessionId, "parameter 'sessionId' must not be empty");
    final String uuid = MDC.get(Constants.LOG_MARK.toString());
    final String contentType = String.join("", MimeTypeUtils.APPLICATION_JSON_VALUE, ";charset=", this.encoding);

    final Map<String, String> param = new HashMap<>();
    param.put(Constants.SESSION_KEY, sessionId);

    final WebClient webClient = WebClient.builder().baseUrl(this.sharingUrl).filter(this.lbFunction).build();
    final ResponseSpec responseSpec = webClient.get().uri("/user/getUserOrEmpty/{id}/{ignoreNotExist}", sessionId, String.valueOf(ignore))
      .header(HttpHeaders.CONTENT_TYPE, contentType).header(this.normalUriInterceptor.getHeadUuidKey(), uuid).retrieve();

    final ParameterizedTypeReference<ResponseVO<User>> forType = new ParameterizedTypeReference<>() {
    };
    final ResponseVO<User> resp = responseSpec.bodyToMono(forType).block();
    if (!StringUtils.equals(resp.getCode(), Constants.RESP_SUCCESS_CODE)) {
      throw new RuntimeException("cannot get user");
    }
    return resp;
  }

  public UserSessionList getUserSessionList(final String username, final boolean ignoreNotExist) {
    Assert.hasText(username, "parameter 'username' must not be empty");
    final String uuid = MDC.get(Constants.LOG_MARK.toString());
    final String contentType = MediaType.TEXT_PLAIN_VALUE;

    final WebClient webClient = WebClient.builder().baseUrl(this.sharingUrl).filter(this.lbFunction).build();
    final ResponseSpec responseSpec = webClient.get().uri("/user/getUserManage/{username}/{ignoreNotExist}", username, String.valueOf(ignoreNotExist))
      .header(HttpHeaders.CONTENT_TYPE, contentType).header(this.normalUriInterceptor.getHeadUuidKey(), uuid).retrieve();

    final ParameterizedTypeReference<ResponseVO<UserSessionList>> forType = new ParameterizedTypeReference<>() {
    };
    final ResponseVO<UserSessionList> resp = responseSpec.bodyToMono(forType).block();
    if (!StringUtils.equals(resp.getCode(), Constants.RESP_SUCCESS_CODE)) {
      throw new RuntimeException("cannot get user manage");
    }

    return resp.getData();
  }

  public void refreshUser(final User user) {
    Objects.requireNonNull(user, "parameter 'user' must not be empty");
    SharingService.LOGGER.info("refresh user on redis, user id: {}, user name: {}", user.getId(), user.getUsername());
    final UserSessionList userSessionList = this.getUserSessionList(user.getUsername(), true);
    if (Objects.isNull(userSessionList)) {
      return;
    }

    final Map<String, SessionInfo> sessionMap = userSessionList.getSessionMap();
    final Set<String> userKeySet = sessionMap.keySet();
    for (final String sessionId : userKeySet) {
      final ResponseVO<User> responseVO = this.getUser(sessionId, true);
      final User userCache = responseVO.getData();
      if (Objects.isNull(userCache)) {
        continue;
      }
      user.setCurrentRoleId(userCache.getCurrentRoleId());
      BeanUtils.copyProperties(user, userCache);
      this.updateUser(sessionId, user);
    }
  }

  public void removeConfiguration(final String companyId) {
    final String uuid = MDC.get(Constants.LOG_MARK.toString());
    final String contentType = String.join("", MimeTypeUtils.TEXT_PLAIN_VALUE, ";charset=", this.encoding);

    final ParameterizedTypeReference<ResponseVO<Long>> forType = new ParameterizedTypeReference<>() {
    };

    final WebClient webClient = WebClient.builder().baseUrl(this.sharingUrl).filter(this.lbFunction).build();
    final ResponseSpec retrieve = webClient.delete().uri("/conf/remove/{companyId}", companyId).header(HttpHeaders.CONTENT_TYPE, contentType)
      .header(this.normalUriInterceptor.getHeadUuidKey(), uuid).retrieve();
    final ResponseVO<Long> resp = retrieve.bodyToMono(forType).block();
    if (!StringUtils.equals(resp.getCode(), Constants.RESP_SUCCESS_CODE)) {
      throw new RuntimeException("cannot remove configuration list");
    }
  }

  public ResponseVO<List<String>> removeShortTerm(final String sessionId) {
    Assert.hasText(sessionId, "parameter 'sessionId' must not be empty");
    final String uuid = MDC.get(Constants.LOG_MARK.toString());
    final String contentType = String.join("", MimeTypeUtils.TEXT_PLAIN_VALUE, ";charset=", this.encoding);

    final WebClient webClient = WebClient.builder().baseUrl(this.sharingUrl).filter(this.lbFunction).build();
    final ResponseSpec responseSpec = webClient.delete().uri("/shortTerm/remove/{id}", sessionId).header(HttpHeaders.CONTENT_TYPE, contentType)
      .header(this.normalUriInterceptor.getHeadUuidKey(), uuid).retrieve();

    final ParameterizedTypeReference<ResponseVO<List<String>>> forType = new ParameterizedTypeReference<>() {
    };
    final ResponseVO<List<String>> resp = responseSpec.bodyToMono(forType).block();
    if (!StringUtils.equals(resp.getCode(), Constants.RESP_SUCCESS_CODE)) {
      throw new RuntimeException("cannot get short time");
    }
    return resp;
  }

  public void removeUser(final String sessionId) {
    Assert.hasText(sessionId, "parameter 'sessionId' must not be empty");
    final String uuid = MDC.get(Constants.LOG_MARK.toString());
    final String contentType = String.join("", MimeTypeUtils.TEXT_PLAIN_VALUE, ";charset=", this.encoding);

    final WebClient webClient = WebClient.builder().baseUrl(this.sharingUrl).filter(this.lbFunction).build();
    final ResponseSpec responseSpec = webClient.delete().uri("/user/removeUser/{id}", sessionId).header(HttpHeaders.CONTENT_TYPE, contentType)
      .header(this.normalUriInterceptor.getHeadUuidKey(), uuid).retrieve();

    final ParameterizedTypeReference<ResponseVO<Object>> forType = new ParameterizedTypeReference<>() {
    };
    final ResponseVO<Object> resp = responseSpec.bodyToMono(forType).block();
    if (!StringUtils.equals(resp.getCode(), Constants.RESP_SUCCESS_CODE)) {
      throw new RuntimeException("cannot remove user");
    }
  }

  public void removeUserManage(final String username) {
    Assert.hasText(username, "parameter 'username' must not be empty");
    final String uuid = MDC.get(Constants.LOG_MARK.toString());
    final String contentType = String.join("", MimeTypeUtils.TEXT_PLAIN_VALUE, ";charset=", this.encoding);

    final WebClient webClient = WebClient.builder().baseUrl(this.sharingUrl).filter(this.lbFunction).build();
    final ResponseSpec responseSpec = webClient.delete().uri("/user/removeUserManage/{username}", username).header(HttpHeaders.CONTENT_TYPE, contentType)
      .header(this.normalUriInterceptor.getHeadUuidKey(), uuid).retrieve();

    final ParameterizedTypeReference<ResponseVO<Object>> forType = new ParameterizedTypeReference<>() {
    };
    final ResponseVO<Object> resp = responseSpec.bodyToMono(forType).block();
    if (!StringUtils.equals(resp.getCode(), Constants.RESP_SUCCESS_CODE)) {
      throw new RuntimeException("cannot remove user manage list");
    }
  }

  public void saveConfigurationList(final String companyId, final List<Configuration> configurationList) {
    Assert.notEmpty(configurationList, "parameter 'configurationList' must not be empty");
    Assert.hasText(companyId, "parameter 'companyId' must not be empty");

    final String uuid = MDC.get(Constants.LOG_MARK.toString());
    final String contentType = String.join("", MimeTypeUtils.APPLICATION_JSON_VALUE, ";charset=", this.encoding);

    final WebClient webClient = WebClient.builder().baseUrl(this.sharingUrl).filter(this.lbFunction).build();
    RequestBodySpec requestBodySpec = webClient.put().uri("/conf/save/{companyId}", companyId);
    requestBodySpec = requestBodySpec.contentType(MediaType.parseMediaType(contentType));
    requestBodySpec = requestBodySpec.header(this.normalUriInterceptor.getHeadUuidKey(), uuid);
    requestBodySpec.body(BodyInserters.fromValue(configurationList));
    final ParameterizedTypeReference<ResponseVO<Boolean>> forType = new ParameterizedTypeReference<>() {
    };
    final ResponseVO<Boolean> resp = requestBodySpec.retrieve().bodyToMono(forType).block();
    if (!StringUtils.equals(resp.getCode(), Constants.RESP_SUCCESS_CODE)) {
      throw new RuntimeException("cannot update configuration list");
    }
  }

  public void updateUser(final String sessionId, final User user) {
    Assert.hasText(sessionId, "parameter 'sessionId' must not be empty");
    Objects.requireNonNull(user, "parameter 'user' must not be empty");
    final String uuid = MDC.get(Constants.LOG_MARK.toString());
    final String contentType = String.join("", MimeTypeUtils.APPLICATION_JSON_VALUE, ";charset=", this.encoding);

    final WebClient webClient = WebClient.builder().baseUrl(this.sharingUrl).filter(this.lbFunction).build();
    RequestBodySpec requestBodySpec = webClient.post().uri("/user/updateUser/{id}", sessionId);
    requestBodySpec = requestBodySpec.contentType(MediaType.parseMediaType(contentType));
    requestBodySpec = requestBodySpec.header(this.normalUriInterceptor.getHeadUuidKey(), uuid);
    requestBodySpec.body(BodyInserters.fromValue(user));

    final ParameterizedTypeReference<ResponseVO<Boolean>> forType = new ParameterizedTypeReference<>() {
    };
    final ResponseVO<Boolean> resp = requestBodySpec.retrieve().bodyToMono(forType).block();
    if (!StringUtils.equals(resp.getCode(), Constants.RESP_SUCCESS_CODE)) {
      throw new RuntimeException("cannot update user");
    }
  }

  public void updateUserSessionList(final UserSessionList userSessionList) {
    Objects.requireNonNull(userSessionList, "parameter 'userSessionList' must not be empty");
    final String uuid = MDC.get(Constants.LOG_MARK.toString());
    final String contentType = String.join("", MimeTypeUtils.APPLICATION_JSON_VALUE, ";charset=", this.encoding);

    final WebClient webClient = WebClient.builder().baseUrl(this.sharingUrl).filter(this.lbFunction).build();
    RequestBodySpec requestBodySpec = webClient.post().uri("/user/updateUserManage");
    requestBodySpec = requestBodySpec.contentType(MediaType.parseMediaType(contentType));
    requestBodySpec = requestBodySpec.header(this.normalUriInterceptor.getHeadUuidKey(), uuid);
    requestBodySpec.body(BodyInserters.fromValue(userSessionList));

    final ParameterizedTypeReference<ResponseVO<Boolean>> forType = new ParameterizedTypeReference<>() {
    };
    final ResponseVO<Boolean> resp = requestBodySpec.retrieve().bodyToMono(forType).block();
    if (!StringUtils.equals(resp.getCode(), Constants.RESP_SUCCESS_CODE)) {
      throw new RuntimeException("cannot update user manage list");
    }
  }
}
