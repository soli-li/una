package com.una.system.sharing;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient.ResponseSpec;
import org.springframework.web.reactive.function.BodyInserters;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.una.common.Constants;
import com.una.common.pojo.ResponseVO;
import com.una.common.pojo.User;
import com.una.common.pojo.UserSessionList;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public class UserHandlerTest extends SharingApplicationTests {
  @Test
  @Order(10)
  @DisplayName("test '/user/addUser' api")
  public void addUser() throws JsonMappingException, JsonProcessingException {
    final User user = new User();
    user.setId(UUID.randomUUID().toString());
    user.setUsername("00001");

    this.addUser(user, true);
  }

  private Mono<String> addUser(final User user, final boolean expect) {
    final ResponseSpec responseSpec = this.getWebTestClient().put().uri("/user/addUser").accept(MediaType.APPLICATION_JSON).body(BodyInserters.fromValue(user))
      .exchange();

    if (expect) {
      responseSpec.expectStatus().isOk();
    }

    final ParameterizedTypeReference<ResponseVO<Map<String, String>>> forType = new ParameterizedTypeReference<>() {
    };
    final Flux<ResponseVO<Map<String, String>>> responseBody = responseSpec.returnResult(forType).getResponseBody();
    return responseBody.map(o -> o.getData().get(Constants.SESSION_KEY)).collectList().map(o -> o.get(0));
  }

  @Test
  @Order(40)
  @DisplayName("test '/user/getUserManage/{username}/{ignoreNotExist}' api")
  public void getUserManage() throws JsonMappingException, JsonProcessingException {
    final ResponseSpec responseSpec = this.getWebTestClient().get().uri("/user/getUserManage/{username}/{ignoreNotExist}", "00001", "true")
      .accept(MediaType.TEXT_PLAIN).exchange();
    responseSpec.expectStatus().isOk();
  }

  @Test
  @Order(20)
  @DisplayName("test '/user/getUserOrEmpty/{id}/{ignoreNotExist}' api")
  public void getUserOrEmpty() throws JsonMappingException, JsonProcessingException {
    final User user = new User();
    user.setId(UUID.randomUUID().toString());
    user.setUsername("00001");

    final Mono<String> mono = this.addUser(user, false);
    final String key = mono.block();
    final ResponseSpec responseSpec = this.getWebTestClient().get().uri("/user/getUserOrEmpty/{id}/{ignoreNotExist}", key, "true").accept(MediaType.TEXT_PLAIN)
      .exchange();
    responseSpec.expectStatus().isOk();
  }

  @Test
  @Order(90)
  @DisplayName("test '/user/removeUser/{id}' api")
  public void removeUser() throws JsonMappingException, JsonProcessingException {
    final User user = new User();
    user.setId(UUID.randomUUID().toString());
    user.setUsername("00001");

    final Mono<String> mono = this.addUser(user, false);
    final String key = mono.block();
    final ResponseSpec responseSpec = this.getWebTestClient().delete().uri("/user/removeUser/{id}", key).accept(MediaType.TEXT_PLAIN).exchange();
    responseSpec.expectStatus().isOk();
  }

  @Test
  @Order(80)
  @DisplayName("test '/user/removeUserManage/{id}' api")
  public void removeUserManage() throws JsonMappingException, JsonProcessingException {
    final UserSessionList userSessionList = new UserSessionList();
    userSessionList.setCompanyId(this.getCompanyId());
    userSessionList.setKey(com.una.system.sharing.Constants.REDIS_MANAGER_USER_PRE + "00001");
    final ResponseSpec responseSpec = this.getWebTestClient().delete().uri("/user/removeUserManage/{username}", "00001").accept(MediaType.TEXT_PLAIN)
      .exchange();
    responseSpec.expectStatus().isOk();
  }

  @Test
  @Order(30)
  @DisplayName("test '/user/updateUser/{id}' api")
  public void updateUser() throws JsonMappingException, JsonProcessingException {
    final User user = new User();
    user.setId(UUID.randomUUID().toString());
    user.setUsername("00001");

    final Mono<String> mono = this.addUser(user, false);
    final String key = mono.block();

    user.setCreatedDate(LocalDateTime.now());
    final ResponseSpec responseSpec = this.getWebTestClient().post().uri("/user/updateUser/{id}", key).accept(MediaType.APPLICATION_JSON)
      .body(BodyInserters.fromValue(user)).exchange();
    responseSpec.expectStatus().isOk();
  }

  @Test
  @Order(40)
  @DisplayName("test '/user/updateUserManage' api")
  public void updateUserManage() throws JsonMappingException, JsonProcessingException {
    final UserSessionList userSessionList = new UserSessionList();
    userSessionList.setCompanyId(this.getCompanyId());
    userSessionList.setKey(com.una.system.sharing.Constants.REDIS_MANAGER_USER_PRE + "00001");
    final ResponseSpec responseSpec = this.getWebTestClient().post().uri("/user/updateUserManage").accept(MediaType.APPLICATION_JSON)
      .body(BodyInserters.fromValue(userSessionList)).exchange();
    responseSpec.expectStatus().isOk();
  }
}
