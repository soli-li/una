package com.una.system.sharing;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient.ResponseSpec;
import org.springframework.web.reactive.function.BodyInserters;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;

public class ShortTermHandlerTest extends SharingApplicationTests {
  @Test
  @Order(20)
  @DisplayName("test '/shortTerm/getOrEmpty/{id}/{ignoreNotExist}' api")
  public void getOrEmpty() throws JsonMappingException, JsonProcessingException {
    final ResponseSpec responseSpec = this.getWebTestClient().get().uri("/shortTerm/getOrEmpty/{id}/{ignoreNotExist}", "abc", "true")
      .accept(MediaType.TEXT_PLAIN).exchange();
    responseSpec.expectStatus().isOk();
  }

  @Test
  @Order(21)
  @DisplayName("test '/shortTerm/getOrEmpty/{id}/{ignoreNotExist}' api on error 1(not exist company id and ignoreNotExist is false)")
  public void getOrEmptyOnError1() throws Exception {
    final ResponseSpec responseSpec = this.getWebTestClient().get().uri("/conf/getOrEmpty/{id}/{ignoreNotExist}", "abc", "false").accept(MediaType.TEXT_PLAIN)
      .exchange();
    responseSpec.expectStatus().is5xxServerError();
  }

  @Test
  @Order(40)
  @DisplayName("test '/shortTerm/remove/{id}' api")
  public void remove() throws JsonMappingException, JsonProcessingException {
    final ResponseSpec responseSpec = this.getWebTestClient().delete().uri("/shortTerm/remove/{id}", "abc").accept(MediaType.TEXT_PLAIN).exchange();
    responseSpec.expectStatus().isOk();
  }

  @Test
  @Order(10)
  @DisplayName("test '/shortTerm/add' api")
  public void save() throws JsonMappingException, JsonProcessingException {
    final Map<String, String> param = new HashMap<>();

    final ResponseSpec responseSpec = this.getWebTestClient().put().uri("/shortTerm/add").accept(MediaType.APPLICATION_JSON)
      .body(BodyInserters.fromValue(param)).exchange();
    responseSpec.expectStatus().isOk();
  }
}
