package com.una.system.sharing.handler;

import com.una.common.pojo.ResponseVo;
import com.una.common.utils.LogUtil;
import com.una.system.sharing.Constants;
import com.una.system.sharing.pojo.TimeoutInfo;
import com.una.system.sharing.utils.ResultMonoUtil;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.ReactiveRedisOperations;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Component
public class ShortTermHandler {
  private static final Logger LOGGER = LogUtil.getRunLogger();

  @Autowired
  @Qualifier("shortTermOperations")
  private ReactiveRedisOperations<String, Object> shortTermOperations;

  @Autowired
  private TimeoutInfo timeoutInfo;

  public Mono<ServerResponse> add(final ServerRequest request) {
    final Map<String, String> contextMap = MDC.getCopyOfContextMap();
    final String uuid = UUID.randomUUID().toString();
    final String key = Constants.REDIS_SHORT_TERM_PRE + uuid;

    final ResponseVo<Object> vo = new ResponseVo<>();
    vo.setCode(com.una.common.Constants.RESP_SUCCESS_CODE.toString());

    final Mono<Optional<Object>> requestMono = request.bodyToMono(Object.class)
        .flatMap(o -> Mono.just(Optional.ofNullable(o))).defaultIfEmpty(Optional.empty());
    final Mono<Boolean> result = requestMono.flatMap(o -> {
      MDC.setContextMap(contextMap);
      final Object obj = o.orElse(new Object());
      return this.shortTermOperations.opsForValue().set(key, obj,
          Duration.of(this.timeoutInfo.getShortTerm(), ChronoUnit.MICROS));
    });

    final Mono<ServerResponse> responseMono = result.flatMap(b -> {
      final Map<String, String> map = new HashMap<>();
      MDC.setContextMap(contextMap);
      map.put(com.una.common.Constants.SESSION_KEY.toString(), uuid);
      vo.setData(map);
      return ServerResponse.status(HttpStatus.OK).contentType(MediaType.APPLICATION_JSON)
          .body(BodyInserters.fromValue(vo));
    });
    return responseMono;
  }

  public Mono<ServerResponse> getOrEmpty(final ServerRequest request) {
    final ResponseVo<Object> vo = new ResponseVo<>();
    vo.setCode(com.una.common.Constants.RESP_SUCCESS_CODE.toString());

    final String sessionId = request.pathVariable("id");
    final boolean ignoreNotExist = StringUtils
        .equalsIgnoreCase(request.pathVariable("ignoreNotExist"), "true");
    ResultMonoUtil.assertTrue(StringUtils.isBlank(sessionId), "request parameter exception",
        ShortTermHandler.LOGGER);

    final String hasPreSessionId = Constants.REDIS_SHORT_TERM_PRE + sessionId;
    final Mono<Object> responseMono = ResultMonoUtil.getExistOrEmptyValue(this.shortTermOperations,
        hasPreSessionId, null, ignoreNotExist);

    return ResultMonoUtil.getSuccessResponse(vo, responseMono);
  }

  public Mono<ServerResponse> remove(final ServerRequest request) {
    final ResponseVo<Object> vo = new ResponseVo<>();
    vo.setCode(com.una.common.Constants.RESP_SUCCESS_CODE.toString());

    final String sessionId = request.pathVariable("id");
    ResultMonoUtil.assertTrue(StringUtils.isBlank(sessionId), "request parameter exception",
        ShortTermHandler.LOGGER);

    final String hasPreSessionId = Constants.REDIS_SHORT_TERM_PRE + sessionId;
    final Mono<ServerResponse> responseMono = this.shortTermOperations.delete(hasPreSessionId)
        .flatMap(l -> ServerResponse.status(HttpStatus.OK).contentType(MediaType.APPLICATION_JSON)
            .body(BodyInserters.fromValue(vo)));
    return responseMono;
  }

}
