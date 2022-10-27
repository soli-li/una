package com.una.system.manager.microservices;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.una.common.Constants;
import com.una.common.pojo.ResponseVo;
import com.una.common.pojo.UserSessionList;
import com.una.common.pojo.UserSessionList.SessionInfo;
import com.una.common.utils.LogUtil;
import com.una.system.manager.config.interceptor.NormalUriInterceptor;
import com.una.system.manager.model.Configuration;
import com.una.system.manager.model.User;
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
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.MimeTypeUtils;
import org.springframework.web.client.RestTemplate;

@Service
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
  @Value("${app.cloud-provider.sharing-url}")
  private String sharingUrl;
  @Value("${app.security.auth-header:x-auth-token}")
  private String authHeaderName;

  @Autowired
  private RestTemplate restTemplate;
  @Autowired
  private NormalUriInterceptor normalUriInterceptor;
  @Autowired
  private ObjectMapper objectMapper;

  public List<String> getAllConfiguration() {
    final String uuid = MDC.get(Constants.LOG_MARK.toString());
    final String contentType = String.join("", MimeTypeUtils.TEXT_PLAIN_VALUE, ";charset=",
        this.encoding);

    final TypeReference<ResponseVo<List<String>>> typeRef = new TypeReference<>() {
    };

    final ResponseVo<List<String>> resp = this.restTemplate
        .execute(this.sharingUrl + "/conf/getAll", HttpMethod.GET, r -> {
          final HttpHeaders headers = r.getHeaders();
          headers.add(this.normalUriInterceptor.getHeadUuidKey(), uuid);
          headers.setContentType(MediaType.parseMediaType(contentType));
        }, r -> this.objectMapper.readValue(r.getBody(), typeRef));

    if (!StringUtils.equals(resp.getCode(), Constants.RESP_SUCCESS_CODE)) {
      throw new RuntimeException("cannot remove configuration list");
    }
    return resp.getData();
  }

  public UserSessionList getAndUpdateUserSessionList(final String username,
      final boolean ignoreNotExist, final boolean fillUser,
      final UpdateUserSessionManage updateUserSessionManage) {
    Assert.hasText(username, "parameter 'username' must not be empty");
    Objects.requireNonNull(updateUserSessionManage,
        "parameter 'updateUserSessionManage' must not be empty");

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
      final ResponseVo<User> responseVo = this.getUser(sessionId, true);
      final User user = responseVo.getData();
      if (Objects.isNull(user)) {
        vaildSessionMap.remove(sessionId);
        hasUserVaildSessionMap.remove(sessionId);
      } else if (fillUser) {
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
    } else {
      userSessionList.setSessionMap(vaildSessionMap);
    }
    return userSessionList;
  }

  public List<Configuration> getConfigurationList(final String companyId) {
    Assert.hasText(companyId, "parameter 'companyId' must not be empty");

    final String uuid = MDC.get(Constants.LOG_MARK.toString());
    final String contentType = String.join("", MimeTypeUtils.TEXT_PLAIN_VALUE, ";charset=",
        this.encoding);

    final TypeReference<ResponseVo<List<Configuration>>> typeRef = new TypeReference<>() {
    };

    final ResponseVo<List<Configuration>> resp = this.restTemplate.execute(
        this.sharingUrl + "/conf/getOrEmpty/{companyId}/{ignoreNotExist}", HttpMethod.GET, r -> {
          final HttpHeaders headers = r.getHeaders();
          headers.add(this.normalUriInterceptor.getHeadUuidKey(), uuid);
          headers.setContentType(MediaType.parseMediaType(contentType));
        }, r -> this.objectMapper.readValue(r.getBody(), typeRef), companyId, "true");

    if (!StringUtils.equals(resp.getCode(), Constants.RESP_SUCCESS_CODE)) {
      throw new RuntimeException("cannot update configuration list");
    }
    return resp.getData();
  }

  public ResponseVo<List<String>> getShortTerm(final String sessionId) {
    Assert.hasText(sessionId, "parameter 'sessionId' must not be empty");
    final String uuid = MDC.get(Constants.LOG_MARK.toString());
    final String contentType = String.join("", MimeTypeUtils.TEXT_PLAIN_VALUE, ";charset=",
        this.encoding);

    final TypeReference<ResponseVo<List<String>>> typeRef = new TypeReference<>() {
    };

    final ResponseVo<List<String>> resp = this.restTemplate.execute(
        this.sharingUrl + "/shortTerm/getOrEmpty/{id}/{ignoreNotExist}", HttpMethod.GET, r -> {
          final HttpHeaders headers = r.getHeaders();
          headers.add(this.normalUriInterceptor.getHeadUuidKey(), uuid);
          headers.setContentType(MediaType.parseMediaType(contentType));
        }, r -> this.objectMapper.readValue(r.getBody(), typeRef), sessionId, "true");

    if (!StringUtils.equals(resp.getCode(), Constants.RESP_SUCCESS_CODE)) {
      throw new RuntimeException("cannot get short time");
    }
    return resp;
  }

  public ResponseVo<User> getUser(final String sessionId, final boolean ignore) {
    Assert.hasText(sessionId, "parameter 'sessionId' must not be empty");
    final String uuid = MDC.get(Constants.LOG_MARK.toString());
    final String contentType = String.join("", MimeTypeUtils.APPLICATION_JSON_VALUE, ";charset=",
        this.encoding);

    final TypeReference<ResponseVo<User>> typeRef = new TypeReference<>() {
    };

    final ResponseVo<User> resp = this.restTemplate.execute(
        this.sharingUrl + "/user/getUserOrEmpty/{id}/{ignoreNotExist}", HttpMethod.GET, r -> {
          final HttpHeaders headers = r.getHeaders();
          headers.add(this.normalUriInterceptor.getHeadUuidKey(), uuid);
          headers.setContentType(MediaType.parseMediaType(contentType));
        }, r -> this.objectMapper.readValue(r.getBody(), typeRef), sessionId,
        String.valueOf(ignore));

    if (!StringUtils.equals(resp.getCode(), Constants.RESP_SUCCESS_CODE)) {
      throw new RuntimeException("cannot get user");
    }
    return resp;
  }

  public UserSessionList getUserSessionList(final String username, final boolean ignoreNotExist) {
    Assert.hasText(username, "parameter 'username' must not be empty");
    final String uuid = MDC.get(Constants.LOG_MARK.toString());
    final String contentType = MediaType.TEXT_PLAIN_VALUE;

    final TypeReference<ResponseVo<UserSessionList>> typeRef = new TypeReference<>() {
    };

    final ResponseVo<UserSessionList> resp = this.restTemplate.execute(
        this.sharingUrl + "/user/getUserManage/{username}/{ignoreNotExist}", HttpMethod.GET, r -> {
          final HttpHeaders headers = r.getHeaders();
          headers.add(this.normalUriInterceptor.getHeadUuidKey(), uuid);
          headers.setContentType(MediaType.parseMediaType(contentType));
        }, r -> this.objectMapper.readValue(r.getBody(), typeRef), username,
        String.valueOf(ignoreNotExist));

    if (!StringUtils.equals(resp.getCode(), Constants.RESP_SUCCESS_CODE)) {
      throw new RuntimeException("cannot get user manage");
    }

    return resp.getData();
  }

  public void refreshUser(final User user) {
    Objects.requireNonNull(user, "parameter 'user' must not be empty");
    SharingService.LOGGER.info("refresh user on redis, user id: {}, user name: {}", user.getId(),
        user.getUsername());
    final UserSessionList userSessionList = this.getUserSessionList(user.getUsername(), true);
    if (Objects.isNull(userSessionList)) {
      return;
    }

    final Map<String, SessionInfo> sessionMap = userSessionList.getSessionMap();
    final Set<String> userKeySet = sessionMap.keySet();
    for (final String sessionId : userKeySet) {
      final ResponseVo<User> responseVo = this.getUser(sessionId, true);
      final User userCache = responseVo.getData();
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
    final String contentType = String.join("", MimeTypeUtils.TEXT_PLAIN_VALUE, ";charset=",
        this.encoding);

    final TypeReference<ResponseVo<Long>> typeRef = new TypeReference<>() {
    };

    final ResponseVo<Long> resp = this.restTemplate
        .execute(this.sharingUrl + "/conf/remove/{companyId}", HttpMethod.DELETE, r -> {
          final HttpHeaders headers = r.getHeaders();
          headers.add(this.normalUriInterceptor.getHeadUuidKey(), uuid);
          headers.setContentType(MediaType.parseMediaType(contentType));
        }, r -> this.objectMapper.readValue(r.getBody(), typeRef), companyId);

    if (!StringUtils.equals(resp.getCode(), Constants.RESP_SUCCESS_CODE)) {
      throw new RuntimeException("cannot remove configuration list");
    }
  }

  public ResponseVo<List<String>> removeShortTerm(final String sessionId) {
    Assert.hasText(sessionId, "parameter 'sessionId' must not be empty");
    final String uuid = MDC.get(Constants.LOG_MARK.toString());
    final String contentType = String.join("", MimeTypeUtils.TEXT_PLAIN_VALUE, ";charset=",
        this.encoding);

    final TypeReference<ResponseVo<List<String>>> typeRef = new TypeReference<>() {
    };

    final ResponseVo<List<String>> resp = this.restTemplate
        .execute(this.sharingUrl + "/shortTerm/remove/{id}", HttpMethod.DELETE, r -> {
          final HttpHeaders headers = r.getHeaders();
          headers.add(this.normalUriInterceptor.getHeadUuidKey(), uuid);
          headers.setContentType(MediaType.parseMediaType(contentType));
        }, r -> this.objectMapper.readValue(r.getBody(), typeRef), sessionId);

    if (!StringUtils.equals(resp.getCode(), Constants.RESP_SUCCESS_CODE)) {
      throw new RuntimeException("cannot get short time");
    }
    return resp;
  }

  public void removeUser(final String sessionId) {
    Assert.hasText(sessionId, "parameter 'sessionId' must not be empty");
    final String uuid = MDC.get(Constants.LOG_MARK.toString());
    final String contentType = String.join("", MimeTypeUtils.TEXT_PLAIN_VALUE, ";charset=",
        this.encoding);

    final TypeReference<ResponseVo<Object>> typeRef = new TypeReference<>() {
    };

    final ResponseVo<Object> resp = this.restTemplate
        .execute(this.sharingUrl + "/user/removeUser/{id}", HttpMethod.DELETE, r -> {
          final HttpHeaders headers = r.getHeaders();
          headers.add(this.normalUriInterceptor.getHeadUuidKey(), uuid);
          headers.setContentType(MediaType.parseMediaType(contentType));
        }, r -> this.objectMapper.readValue(r.getBody(), typeRef), sessionId);

    if (!StringUtils.equals(resp.getCode(), Constants.RESP_SUCCESS_CODE)) {
      throw new RuntimeException("cannot remove user");
    }
  }

  public void removeUserManage(final String username) {
    Assert.hasText(username, "parameter 'username' must not be empty");
    final String uuid = MDC.get(Constants.LOG_MARK.toString());
    final String contentType = String.join("", MimeTypeUtils.TEXT_PLAIN_VALUE, ";charset=",
        this.encoding);

    final TypeReference<ResponseVo<Object>> typeRef = new TypeReference<>() {
    };

    final ResponseVo<Object> resp = this.restTemplate
        .execute(this.sharingUrl + "/user/removeUserManage/{username}", HttpMethod.DELETE, r -> {
          final HttpHeaders headers = r.getHeaders();
          headers.add(this.normalUriInterceptor.getHeadUuidKey(), uuid);
          headers.setContentType(MediaType.parseMediaType(contentType));
        }, r -> this.objectMapper.readValue(r.getBody(), typeRef), username);

    if (!StringUtils.equals(resp.getCode(), Constants.RESP_SUCCESS_CODE)) {
      throw new RuntimeException("cannot remove user manage list");
    }
  }

  public void saveConfigurationList(final String companyId,
      final List<Configuration> configurationList) {
    Assert.notEmpty(configurationList, "parameter 'configurationList' must not be empty");
    Assert.hasText(companyId, "parameter 'companyId' must not be empty");

    final String uuid = MDC.get(Constants.LOG_MARK.toString());
    final String contentType = String.join("", MimeTypeUtils.APPLICATION_JSON_VALUE, ";charset=",
        this.encoding);

    ParameterizedTypeReference<ResponseVo<Boolean>> paramTypeRef = null;
    paramTypeRef = new ParameterizedTypeReference<>() {
    };
    final HttpMethod method = HttpMethod.PUT;
    final HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.parseMediaType(contentType));
    headers.add(this.normalUriInterceptor.getHeadUuidKey(), uuid);

    final HttpEntity<List<Configuration>> httpEntity = new HttpEntity<>(configurationList, headers);
    final ResponseEntity<ResponseVo<Boolean>> responseEntity = this.restTemplate.exchange(
        this.sharingUrl + "/conf/save/{companyId}", method, httpEntity, paramTypeRef, companyId);
    final ResponseVo<Boolean> resp = responseEntity.getBody();

    if (!StringUtils.equals(resp.getCode(), Constants.RESP_SUCCESS_CODE)) {
      throw new RuntimeException("cannot update configuration list");
    }
  }

  public void updateUser(final String sessionId, final User user) {
    Assert.hasText(sessionId, "parameter 'sessionId' must not be empty");
    Objects.requireNonNull(user, "parameter 'user' must not be empty");
    final String uuid = MDC.get(Constants.LOG_MARK.toString());
    final String contentType = String.join("", MimeTypeUtils.APPLICATION_JSON_VALUE, ";charset=",
        this.encoding);

    ParameterizedTypeReference<ResponseVo<Boolean>> paramTypeRef = null;
    paramTypeRef = new ParameterizedTypeReference<>() {
    };
    final HttpMethod method = HttpMethod.POST;
    final HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.parseMediaType(contentType));
    headers.add(this.normalUriInterceptor.getHeadUuidKey(), uuid);

    final HttpEntity<User> httpEntity = new HttpEntity<>(user, headers);
    final ResponseEntity<ResponseVo<Boolean>> responseEntity = this.restTemplate.exchange(
        this.sharingUrl + "/user/updateUser/{id}", method, httpEntity, paramTypeRef, sessionId);
    final ResponseVo<Boolean> resp = responseEntity.getBody();

    if (!StringUtils.equals(resp.getCode(), Constants.RESP_SUCCESS_CODE)) {
      throw new RuntimeException("cannot update user");
    }
  }

  public void updateUserSessionList(final UserSessionList userSessionList) {
    Objects.requireNonNull(userSessionList, "parameter 'userSessionList' must not be empty");
    final String uuid = MDC.get(Constants.LOG_MARK.toString());
    final String contentType = String.join("", MimeTypeUtils.APPLICATION_JSON_VALUE, ";charset=",
        this.encoding);

    ParameterizedTypeReference<ResponseVo<Boolean>> paramTypeRef = null;
    paramTypeRef = new ParameterizedTypeReference<>() {
    };
    final HttpMethod method = HttpMethod.POST;
    final HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.parseMediaType(contentType));
    headers.add(this.normalUriInterceptor.getHeadUuidKey(), uuid);

    final HttpEntity<UserSessionList> httpEntity = new HttpEntity<>(userSessionList, headers);
    final ResponseEntity<ResponseVo<Boolean>> responseEntity = this.restTemplate
        .exchange(this.sharingUrl + "/user/updateUserManage", method, httpEntity, paramTypeRef);
    final ResponseVo<Boolean> resp = responseEntity.getBody();

    if (!StringUtils.equals(resp.getCode(), Constants.RESP_SUCCESS_CODE)) {
      throw new RuntimeException("cannot update user manage list");
    }
  }
}
