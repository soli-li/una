package com.una.system.sharing.utils;

import com.una.common.pojo.ResponseVo;
import com.una.common.utils.LogUtil;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.MDC;
import org.springframework.data.redis.core.ReactiveRedisOperations;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

public final class ResultMonoUtil {
  private static final Logger LOGGER = LogUtil.getRunLogger();

  public static void assertTrue(final boolean bool, final String message, final Logger logger) {
    if (bool) {
      logger.error(message);
      throw new RuntimeException(message);
    }
  }

  public static <T> Mono<ServerResponse> generateResponse(final HttpStatus status,
      final ResponseVo<T> vo) {
    return ServerResponse.status(status).contentType(MediaType.APPLICATION_JSON)
        .body(BodyInserters.fromValue(vo));
  }

  public static <K, T> Mono<T> getExistOrEmptyValue(final ReactiveRedisOperations<K, T> operation,
      final K key, final Duration duration, final boolean ignoreNotExist) {
    final Map<String, String> contextMap = MDC.getCopyOfContextMap();
    return operation.hasKey(key).flatMap(exist -> {
      MDC.setContextMap(contextMap);
      if (!exist && !ignoreNotExist) {
        throw new RuntimeException("not found session");
      }
      if (Objects.isNull(duration)) {
        return operation.opsForValue().get(key);
      }

      return operation.opsForValue().getAndExpire(key, duration);
    });
  }

  public static <T> Mono<ServerResponse> getSuccessResponse(final ResponseVo<T> vo,
      final Mono<T> mono) {
    final Map<String, String> contextMap = MDC.getCopyOfContextMap();
    final Mono<Optional<T>> optionalMono = mono.flatMap(o -> Mono.just(Optional.ofNullable(o)))
        .defaultIfEmpty(Optional.empty());
    return optionalMono.flatMap(o -> {
      MDC.setContextMap(contextMap);
      vo.setData(o.orElse(null));
      return ServerResponse.status(HttpStatus.OK).contentType(MediaType.APPLICATION_JSON)
          .body(BodyInserters.fromValue(vo));
    });
  }

  public static void setErrorResponse(final String message, final ResponseVo<Object> vo,
      final String code, final Throwable e) {
    final StringBuilder buff = new StringBuilder();
    buff.append(message);
    final List<Object> args = new ArrayList<>();
    if (StringUtils.isNotBlank(code)) {
      buff.append(", code: {}");
      args.add(code);
    }
    if (Objects.nonNull(e)) {
      args.add(e);
    }
    ResultMonoUtil.LOGGER.error(buff.toString(), args.toArray());
    vo.setMsg(message);
    vo.setCode(code);
  }
}
