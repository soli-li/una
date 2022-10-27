package com.una.system.sharing.handler;

import com.una.common.pojo.ResponseVo;
import com.una.common.pojo.User;
import com.una.common.pojo.UserSessionList;
import com.una.common.pojo.UserSessionList.SessionInfo;
import com.una.common.utils.LogUtil;
import com.una.system.sharing.Constants;
import com.una.system.sharing.pojo.TimeoutInfo;
import com.una.system.sharing.utils.ResultMonoUtil;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.redis.core.ReactiveRedisOperations;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerRequest.Headers;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Component
public class UserHandler {
  private static final Logger LOGGER = LogUtil.getRunLogger();

  @Autowired
  @Qualifier("userRedisOperations")
  private ReactiveRedisOperations<String, User> userRedisOperations;

  @Autowired
  @Qualifier("manangerUserRedisOperations")
  private ReactiveRedisOperations<String, UserSessionList> manangerUserRedisOperations;

  @Autowired
  private TimeoutInfo timeoutInfo;

  public Mono<ServerResponse> addUser(final ServerRequest request) {
    final Map<String, String> contextMap = MDC.getCopyOfContextMap();
    final String uuid = UUID.randomUUID().toString();
    final String hasPreUuid = Constants.REDIS_USER_PRE + uuid;

    final Headers headers = request.headers();
    final ResponseVo<Map<String, String>> vo = new ResponseVo<>();
    vo.setCode(com.una.common.Constants.RESP_SUCCESS_CODE.toString());

    final Mono<Optional<User>> requestMono = request.bodyToMono(User.class)
        .flatMap(u -> Mono.just(Optional.ofNullable(u))).defaultIfEmpty(Optional.empty());
    final Mono<Optional<User>> addUserMono = this.addUser(hasPreUuid, requestMono)
        .flatMap(o -> Mono.just(Optional.of(o))).defaultIfEmpty(Optional.empty());
    final Mono<Boolean> result = this.addUserManage(addUserMono, uuid,
        String.join(",", headers.header(com.una.common.Constants.REMOTE_ADDRESS)),
        String.join(",", headers.header(HttpHeaders.USER_AGENT)));
    final Mono<ServerResponse> responseMono = result.flatMap(b -> {
      MDC.setContextMap(contextMap);
      final Map<String, String> map = new HashMap<>();
      map.put(com.una.common.Constants.SESSION_KEY.toString(), uuid);
      vo.setData(map);
      return ServerResponse.status(HttpStatus.OK).contentType(MediaType.APPLICATION_JSON)
          .body(BodyInserters.fromValue(vo));
    });

    return responseMono;
  }

  private Mono<User> addUser(final String uuid, final Mono<Optional<User>> userMono) {
    return userMono.flatMap(o -> {
      final User user = o.orElse(null);
      ResultMonoUtil.assertTrue(
          Objects.isNull(user) || StringUtils.isAnyBlank(user.getUsername(), user.getId()),
          "user is null", UserHandler.LOGGER);

      final Mono<Boolean> result = this.userRedisOperations.opsForValue().set(uuid, user,
          Duration.of(this.timeoutInfo.getUser(), ChronoUnit.MILLIS));
      return result.map(b -> {
        ResultMonoUtil.assertTrue(!b, "cannot add user to redis, username: " + user.getUsername(),
            UserHandler.LOGGER);
        return user;
      });
    });
  }

  private Mono<Boolean> addUserManage(final Mono<Optional<User>> userMono, final String sessioinId,
      final String ip, final String userAgent) {
    final Map<String, String> contextMap = MDC.getCopyOfContextMap();
    final Mono<User> mono = userMono.map(o -> {
      MDC.setContextMap(contextMap);
      final String username = o.map(User::getUsername).orElse("");
      ResultMonoUtil.assertTrue(o.isEmpty() || StringUtils.isAnyBlank(sessioinId, username),
          "cannot add user session to redis, username: " + username, UserHandler.LOGGER);
      return o.get();
    });

    final Mono<Boolean> result = mono.flatMap(u -> {
      MDC.setContextMap(contextMap);
      final String managerUuid = this.getUserManagerSessionKey(u.getUsername());

      final Mono<Optional<UserSessionList>> userSessionMono = ResultMonoUtil
          .getExistOrEmptyValue(this.manangerUserRedisOperations, managerUuid, null, true)
          .flatMap(o -> Mono.just(Optional.ofNullable(o))).defaultIfEmpty(Optional.empty());
      final Mono<Boolean> saveResult = userSessionMono.flatMap(o -> {
        MDC.setContextMap(contextMap);
        final UserSessionList userSessionList = o.orElse(new UserSessionList());
        if (o.isEmpty()) {
          userSessionList.setKey(managerUuid);
          userSessionList.setCompanyId(u.getCompanyId());
        }

        final SessionInfo sessionInfo = new SessionInfo();
        sessionInfo.setCreatedTime(LocalDateTime.now());
        sessionInfo.setIp(ip);
        sessionInfo.setSessionId(sessioinId);
        sessionInfo.setUserAgent(userAgent);
        userSessionList.getSessionMap().put(sessioinId, sessionInfo);

        return this.manangerUserRedisOperations.opsForValue().set(managerUuid, userSessionList);
      });
      return saveResult.map(b -> {
        MDC.setContextMap(contextMap);
        ResultMonoUtil.assertTrue(!b,
            "cannot add user session to redis, username: " + u.getUsername(), UserHandler.LOGGER);
        return b;
      });
    });

    return result;
  }

  public Mono<ServerResponse> getUserManage(final ServerRequest request) {
    final ResponseVo<UserSessionList> vo = new ResponseVo<>();
    vo.setCode(com.una.common.Constants.RESP_SUCCESS_CODE.toString());

    final String username = request.pathVariable("username");
    final boolean ignoreNotExist = StringUtils
        .equalsIgnoreCase(request.pathVariable("ignoreNotExist"), "true");
    ResultMonoUtil.assertTrue(StringUtils.isBlank(username), "username is null",
        UserHandler.LOGGER);

    final String userManagerSessionKey = this.getUserManagerSessionKey(username);
    final Mono<UserSessionList> responseMono = ResultMonoUtil.getExistOrEmptyValue(
        this.manangerUserRedisOperations, userManagerSessionKey, null, ignoreNotExist);

    return ResultMonoUtil.getSuccessResponse(vo, responseMono);
  }

  private String getUserManagerSessionKey(final String username) {
    return Constants.REDIS_MANAGER_USER_PRE + username;
  }

  public Mono<ServerResponse> getUserOrEmpty(final ServerRequest request) {
    final ResponseVo<User> vo = new ResponseVo<>();
    vo.setCode(com.una.common.Constants.RESP_SUCCESS_CODE.toString());

    final String id = request.pathVariable("id");
    ResultMonoUtil.assertTrue(StringUtils.isBlank(id), "session id is empty", UserHandler.LOGGER);
    final boolean ignoreNotExist = StringUtils
        .equalsIgnoreCase(request.pathVariable("ignoreNotExist"), "true");

    final Mono<User> responseMono = ResultMonoUtil.getExistOrEmptyValue(this.userRedisOperations,
        Constants.REDIS_USER_PRE + id, Duration.of(this.timeoutInfo.getUser(), ChronoUnit.MILLIS),
        ignoreNotExist);
    return ResultMonoUtil.getSuccessResponse(vo, responseMono);
  }

  public Mono<ServerResponse> removeUser(final ServerRequest request) {
    final ResponseVo<Long> vo = new ResponseVo<>();
    vo.setCode(com.una.common.Constants.RESP_SUCCESS_CODE.toString());

    final String id = request.pathVariable("id");
    ResultMonoUtil.assertTrue(StringUtils.isBlank(id), "session id is empty", UserHandler.LOGGER);

    final Mono<Long> responseMono = this.userRedisOperations.delete(Constants.REDIS_USER_PRE + id);
    return ResultMonoUtil.getSuccessResponse(vo, responseMono);
  }

  public Mono<ServerResponse> removeUserManage(final ServerRequest request) {
    final ResponseVo<Long> vo = new ResponseVo<>();
    vo.setCode(com.una.common.Constants.RESP_SUCCESS_CODE.toString());

    final String username = request.pathVariable("username");
    ResultMonoUtil.assertTrue(StringUtils.isBlank(username), "username is empty",
        UserHandler.LOGGER);

    final Mono<Long> responseMono = this.manangerUserRedisOperations
        .delete(this.getUserManagerSessionKey(username));
    return ResultMonoUtil.getSuccessResponse(vo, responseMono);
  }

  public Mono<ServerResponse> updateUser(final ServerRequest request) {
    final String sessionId = Constants.REDIS_USER_PRE + request.pathVariable("id");
    ResultMonoUtil.assertTrue(StringUtils.isBlank(sessionId), "request parameter exception",
        UserHandler.LOGGER);

    final Map<String, String> contextMap = MDC.getCopyOfContextMap();
    final ResponseVo<Boolean> vo = new ResponseVo<>();
    vo.setCode(com.una.common.Constants.RESP_SUCCESS_CODE.toString());

    final ParameterizedTypeReference<User> forType = new ParameterizedTypeReference<>() {
    };
    final Mono<Optional<User>> requestMono = request.bodyToMono(forType)
        .flatMap(u -> Mono.just(Optional.ofNullable(u))).defaultIfEmpty(Optional.empty());
    final Mono<Boolean> responseMono = requestMono.flatMap(o -> {
      MDC.setContextMap(contextMap);

      final User user = o.orElse(null);
      ResultMonoUtil.assertTrue(Objects.isNull(user), "request parameter exception",
          UserHandler.LOGGER);

      this.userRedisOperations.getExpire(sessionId).flatMap(expire -> {
        MDC.setContextMap(contextMap);
        return this.userRedisOperations.opsForValue().set(sessionId, user, expire);
      }).subscribe();
      return Mono.just(true);
    });
    return ResultMonoUtil.getSuccessResponse(vo, responseMono);
  }

  public Mono<ServerResponse> updateUserManage(final ServerRequest request) {
    final Map<String, String> contextMap = MDC.getCopyOfContextMap();
    final ResponseVo<Boolean> vo = new ResponseVo<>();
    vo.setCode(com.una.common.Constants.RESP_SUCCESS_CODE.toString());

    ParameterizedTypeReference<UserSessionList> refernece = null;
    refernece = new ParameterizedTypeReference<>() {
    };
    final Mono<Optional<UserSessionList>> requestMono = request.bodyToMono(refernece)
        .flatMap(m -> Mono.just(Optional.ofNullable(m))).defaultIfEmpty(Optional.empty());
    final Mono<Boolean> responseMono = requestMono.flatMap(o -> {
      MDC.setContextMap(contextMap);
      if (StringUtils.isBlank(o.map(UserSessionList::getKey).orElse(""))) {
        ResultMonoUtil.assertTrue(true, "request parameter exception", UserHandler.LOGGER);
      }
      final UserSessionList userSessionList = o.get();
      return this.manangerUserRedisOperations.hasKey(userSessionList.getKey()).flatMap(exist -> {
        MDC.setContextMap(contextMap);
        if (exist) {
          return this.manangerUserRedisOperations.opsForValue().set(userSessionList.getKey(),
              userSessionList);
        } else {
          return Mono.just(false);
        }
      });
    });
    return ResultMonoUtil.getSuccessResponse(vo, responseMono);
  }

}
