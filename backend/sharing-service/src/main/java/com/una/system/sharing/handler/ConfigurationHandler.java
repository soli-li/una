package com.una.system.sharing.handler;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.redis.core.ReactiveRedisOperations;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.data.redis.core.ScanOptions.ScanOptionsBuilder;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.una.common.pojo.Configuration;
import com.una.common.pojo.ResponseVO;
import com.una.common.utils.LogUtil;
import com.una.system.sharing.Constants;
import com.una.system.sharing.utils.ResultMonoUtil;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
public class ConfigurationHandler {
  private static final Logger LOGGER = LogUtil.getRunLogger();

  @Autowired
  private ObjectMapper objectMapper;

  @Autowired
  @Qualifier("configurationRedisOperations")
  private ReactiveRedisOperations<String, String> confRedisOperations;

  public Mono<ServerResponse> getAll(final ServerRequest request) {
    final Map<String, String> contextMap = MDC.getCopyOfContextMap();
    final ResponseVO<List<String>> vo = new ResponseVO<>();
    vo.setCode(com.una.common.Constants.RESP_SUCCESS_CODE.toString());

    final ScanOptionsBuilder scanOptionsBuilder = ScanOptions.scanOptions().match(this.getConfigurationKey("*")).count(10);
    final Flux<String> scan = this.confRedisOperations.scan(scanOptionsBuilder.build());
    final Mono<List<String>> responseMono = scan.buffer(100).collectList().map(o -> {
      MDC.setContextMap(contextMap);
      final List<String> keyList = new ArrayList<>();
      o.forEach(l -> l.forEach(id -> keyList.add(StringUtils.substringAfter(id, Constants.REDIS_CONF_PRE))));
      return keyList;
    });
    return ResultMonoUtil.getSuccessResponse(vo, responseMono);
  }

  private String getConfigurationKey(final String companyId) {
    return String.join("", Constants.REDIS_CONF_PRE, companyId);
  }

  public Mono<ServerResponse> getOrEmpty(final ServerRequest request) {
    final Map<String, String> contextMap = MDC.getCopyOfContextMap();
    final ResponseVO<List<Configuration>> vo = new ResponseVO<>();
    vo.setCode(com.una.common.Constants.RESP_SUCCESS_CODE.toString());

    final String companyId = request.pathVariable("companyId");
    final boolean ignoreNotExist = StringUtils.equalsIgnoreCase(request.pathVariable("ignoreNotExist"), "true");
    ResultMonoUtil.assertTrue(StringUtils.isBlank(companyId), "companyId is empty", ConfigurationHandler.LOGGER);

    final TypeReference<List<Configuration>> typeRef = new TypeReference<>() {
    };

    final String redisKey = this.getConfigurationKey(companyId);
    final Mono<String> valueMono = ResultMonoUtil.getExistOrEmptyValue(this.confRedisOperations, redisKey, null, ignoreNotExist);
    final Mono<List<Configuration>> responseMono = valueMono.map(v -> {
      MDC.setContextMap(contextMap);
      try {
        return this.objectMapper.readValue(v, typeRef);
      }
      catch (final Exception e) {
        throw new RuntimeException(e);
      }
    });

    return ResultMonoUtil.getSuccessResponse(vo, responseMono);
  }

  public Mono<ServerResponse> remove(final ServerRequest request) {
    final ResponseVO<Long> vo = new ResponseVO<>();
    vo.setCode(com.una.common.Constants.RESP_SUCCESS_CODE.toString());

    final String companyId = request.pathVariable("companyId");
    final Mono<Long> responseMono = this.confRedisOperations.delete(this.getConfigurationKey(companyId));
    return ResultMonoUtil.getSuccessResponse(vo, responseMono);
  }

  public Mono<ServerResponse> save(final ServerRequest request) {
    final Map<String, String> contextMap = MDC.getCopyOfContextMap();
    final ResponseVO<Boolean> vo = new ResponseVO<>();
    vo.setCode(com.una.common.Constants.RESP_SUCCESS_CODE.toString());

    final String companyId = request.pathVariable("companyId");
    ResultMonoUtil.assertTrue(StringUtils.isBlank(companyId), "companyId is empty", ConfigurationHandler.LOGGER);

    final ParameterizedTypeReference<List<Configuration>> typeRef = new ParameterizedTypeReference<>() {
    };
    final Mono<Optional<List<Configuration>>> requestMono = request.bodyToMono(typeRef).flatMap(o -> Mono.just(Optional.ofNullable(o)))
      .defaultIfEmpty(Optional.empty());
    final Mono<Boolean> responseMono = requestMono.flatMap(o -> {
      MDC.setContextMap(contextMap);
      final List<Configuration> configurationList = o.orElse(List.of());

      final String redisKey = this.getConfigurationKey(companyId);
      try {
        final String value = this.objectMapper.writeValueAsString(configurationList);
        return this.confRedisOperations.opsForValue().set(redisKey, value);
      }
      catch (final JsonProcessingException e) {
        throw new RuntimeException(e);
      }
    });

    return ResultMonoUtil.getSuccessResponse(vo, responseMono);
  }
}
