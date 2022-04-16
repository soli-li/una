package com.una.system.sharing;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.FluxExchangeResult;
import org.springframework.test.web.reactive.server.WebTestClient.ResponseSpec;
import org.springframework.web.reactive.function.BodyInserters;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.una.common.pojo.Configuration;
import com.una.common.pojo.ResponseVO;

import reactor.core.publisher.Flux;

public class ConfigurationHandlerTest extends SharingApplicationTests {
  @Test
  @Order(40)
  @DisplayName("test '/conf/remove/{companyId}' api")
  public void clear() throws JsonMappingException, JsonProcessingException {
    final ParameterizedTypeReference<ResponseVO<List<String>>> forType = new ParameterizedTypeReference<>() {
    };
    final ResponseSpec response = this.findAll();
    final List<String> ids = response.returnResult(forType).getResponseBody().collectList().map(resp -> {
      final List<String> idList = new ArrayList<>();
      resp.forEach(r -> idList.addAll(r.getData()));
      return idList;
    }).block();
    for (final String id : ids) {
      final ResponseSpec responseSpec = this.getWebTestClient().delete().uri("/conf/remove/{companyId}", id).accept(MediaType.TEXT_PLAIN).exchange();
      responseSpec.expectStatus().isOk();
    }
  }

  private ResponseSpec findAll() {
    final ResponseSpec responseSpec = this.getWebTestClient().get().uri("/conf/getAll").accept(MediaType.TEXT_PLAIN).exchange();
    return responseSpec;
  }

  @Test
  @Order(30)
  @DisplayName("test '/conf/getAll' api")
  public void getAll() throws JsonMappingException, JsonProcessingException {
    final ResponseSpec responseSpec = this.findAll();
    responseSpec.expectStatus().isOk();
  }

  @Test
  @Order(20)
  @DisplayName("test '/conf/getOrEmpty/{companyId}/{ignoreNotExist}' api")
  public void getOrEmpty() throws JsonMappingException, JsonProcessingException {
    this.save();
    final String companyId = this.getCompanyId();
    final ResponseSpec responseSpec = this.getWebTestClient().get().uri("/conf/getOrEmpty/{companyId}/{ignoreNotExist}", companyId, "true")
      .accept(MediaType.TEXT_PLAIN).exchange();
    responseSpec.expectStatus().isOk();
    final ParameterizedTypeReference<ResponseVO<List<Configuration>>> forType = new ParameterizedTypeReference<>() {
    };
    final FluxExchangeResult<ResponseVO<List<Configuration>>> returnResult = responseSpec.returnResult(forType);
    final Flux<ResponseVO<List<Configuration>>> responseBody = returnResult.getResponseBody();
    responseBody.doOnNext(o -> o.getData().forEach(System.out::println)).subscribe();
  }

  @Test
  @Order(21)
  @DisplayName("test '/conf/getOrEmpty/{companyId}/{ignoreNotExist}' api on error 1(not exist company id and ignoreNotExist is false)")
  public void getOrEmptyOnError1() throws Exception {
    final String companyId = this.getCompanyId();

    final ResponseSpec responseSpec = this.getWebTestClient().get().uri("/conf/getOrEmpty/{companyId}/{ignoreNotExist}", companyId, "false")
      .accept(MediaType.TEXT_PLAIN).exchange();
    responseSpec.expectStatus().is5xxServerError();
  }

  @Test
  //  @RepeatedTest(100)
  @Order(10)
  @DisplayName("test '/conf/save/{companyId}' api")
  public void save() throws JsonMappingException, JsonProcessingException {
    final List<Configuration> list = new ArrayList<>();

    final String companyId = this.getCompanyId();

    final Configuration config = new Configuration();
    config.setCompanyId(companyId);
    config.setName("test");
    config.setId("abc");
    list.add(config);

    final ResponseSpec responseSpec = this.getWebTestClient().put().uri("/conf/save/{companyId}", companyId).accept(MediaType.APPLICATION_JSON)
      .body(BodyInserters.fromValue(list)).exchange();
    responseSpec.expectStatus().isOk();
  }
}
