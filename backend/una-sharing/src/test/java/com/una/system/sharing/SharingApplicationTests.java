package com.una.system.sharing;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.una.common.pojo.User;
import com.una.common.pojo.UserSessionList;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.annotation.Order;
import org.springframework.data.redis.core.ReactiveRedisOperations;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.data.redis.core.ScanOptions.ScanOptionsBuilder;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.util.CollectionUtils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@SpringBootTest(classes = SharingApplication.class,
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Rollback(value = true)
@ActiveProfiles(profiles = { "unit-test" })
@TestMethodOrder(value = MethodOrderer.OrderAnnotation.class)
public class SharingApplicationTests {

  @BeforeAll
  public static void initActive() {
  }

  private final String companyId = UUID.randomUUID().toString();
  @Autowired
  private ObjectMapper objectMapper;

  @Autowired
  private WebTestClient webTestClient;

  @Autowired
  @Qualifier("configurationRedisOperations")
  private ReactiveRedisOperations<String, String> confRedisOperations;
  @Autowired
  @Qualifier("shortTermOperations")
  private ReactiveRedisOperations<String, Object> shortTermOperations;
  @Autowired
  @Qualifier("userRedisOperations")
  private ReactiveRedisOperations<String, User> userRedisOperations;

  @Autowired
  @Qualifier("manangerUserRedisOperations")
  private ReactiveRedisOperations<String, UserSessionList> manangerUserRedisOperations;

  private void clearConfRedis() {
    final ScanOptionsBuilder scanOptionsBuilder = ScanOptions.scanOptions()
        .match(Constants.REDIS_CONF_PRE + "*").count(10);
    final Flux<String> scan = this.confRedisOperations.scan(scanOptionsBuilder.build());
    scan.buffer(100).collectList().flatMap(o -> {
      final List<String> keyList = new ArrayList<>();
      o.forEach(keyList::addAll);
      if (CollectionUtils.isEmpty(keyList)) {
        return Mono.empty();
      }
      return this.confRedisOperations.delete(keyList.toArray(String[]::new));
    }).subscribe();
  }

  private void clearManangerUserRedis() {
    final ScanOptionsBuilder scanOptionsBuilder = ScanOptions.scanOptions()
        .match(Constants.REDIS_MANAGER_USER_PRE + "*").count(10);
    final Flux<String> scan = this.manangerUserRedisOperations.scan(scanOptionsBuilder.build());
    scan.buffer(100).collectList().flatMap(o -> {
      final List<String> keyList = new ArrayList<>();
      o.forEach(keyList::addAll);
      if (CollectionUtils.isEmpty(keyList)) {
        return Mono.empty();
      }
      return this.manangerUserRedisOperations.delete(keyList.toArray(String[]::new));
    }).subscribe();
  }

  @Test
  @Order(Integer.MAX_VALUE)
  public void clearRedis() {
    this.clearConfRedis();
    this.clearManangerUserRedis();
    this.clearShortTerm();
    this.clearUserRedis();
  }

  private void clearShortTerm() {
    final ScanOptionsBuilder scanOptionsBuilder = ScanOptions.scanOptions()
        .match(Constants.REDIS_SHORT_TERM_PRE + "*").count(10);
    final Flux<String> scan = this.shortTermOperations.scan(scanOptionsBuilder.build());
    scan.buffer(100).collectList().flatMap(o -> {
      final List<String> keyList = new ArrayList<>();
      o.forEach(keyList::addAll);
      if (CollectionUtils.isEmpty(keyList)) {
        return Mono.empty();
      }
      return this.shortTermOperations.delete(keyList.toArray(String[]::new));
    }).subscribe();
  }

  private void clearUserRedis() {
    final ScanOptionsBuilder scanOptionsBuilder = ScanOptions.scanOptions()
        .match(Constants.REDIS_USER_PRE + "*").count(10);
    final Flux<String> scan = this.userRedisOperations.scan(scanOptionsBuilder.build());
    scan.buffer(100).collectList().flatMap(o -> {
      final List<String> keyList = new ArrayList<>();
      o.forEach(keyList::addAll);
      if (CollectionUtils.isEmpty(keyList)) {
        return Mono.empty();
      }
      return this.userRedisOperations.delete(keyList.toArray(String[]::new));
    }).subscribe();
  }

  public String getCompanyId() {
    return this.companyId;
  }

  public ObjectMapper getObjectMapper() {
    return this.objectMapper;
  }

  public WebTestClient getWebTestClient() {
    return this.webTestClient;
  }

}
