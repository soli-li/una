package com.una.system.sharing;

import com.una.common.pojo.Configuration;
import com.una.common.pojo.ResponseVo;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.FluxExchangeResult;
import org.springframework.test.web.reactive.server.WebTestClient.ResponseSpec;
import org.springframework.web.reactive.function.BodyInserters;
import reactor.core.publisher.Flux;

public class ConfigurationHandlerTest extends SharingApplicationTests {
  @Test
  @Order(40)
  @DisplayName("test '/conf/remove/{companyId}' api")
  public void clear() {
    try {
      ParameterizedTypeReference<ResponseVo<List<String>>> paramTypeRef = null;
      paramTypeRef = new ParameterizedTypeReference<>() {
      };
      final ResponseSpec response = this.findAll();
      final List<String> ids = response.returnResult(paramTypeRef).getResponseBody().collectList()
          .map(resp -> {
            final List<String> idList = new ArrayList<>();
            resp.forEach(r -> idList.addAll(r.getData()));
            return idList;
          }).block();
      for (final String id : ids) {
        final ResponseSpec responseSpec = this.getWebTestClient().delete()
            .uri("/conf/remove/{companyId}", id).accept(MediaType.TEXT_PLAIN).exchange();
        responseSpec.expectStatus().isOk();
      }
    } catch (final Exception e) {
      Assertions.assertTrue(false, e.getMessage());
    }
  }

  private ResponseSpec findAll() {
    final ResponseSpec responseSpec = this.getWebTestClient().get().uri("/conf/getAll")
        .accept(MediaType.TEXT_PLAIN).exchange();
    return responseSpec;
  }

  @Test
  @Order(30)
  @DisplayName("test '/conf/getAll' api")
  public void getAll() {
    try {
      final ResponseSpec responseSpec = this.findAll();
      responseSpec.expectStatus().isOk();
    } catch (final Exception e) {
      Assertions.assertTrue(false, e.getMessage());
    }
  }

  @Test
  @Order(20)
  @DisplayName("test '/conf/getOrEmpty/{companyId}/{ignoreNotExist}' api")
  public void getOrEmpty() {
    try {
      this.save();
      final String companyId = this.getCompanyId();
      final ResponseSpec responseSpec = this.getWebTestClient().get()
          .uri("/conf/getOrEmpty/{companyId}/{ignoreNotExist}", companyId, "true")
          .accept(MediaType.TEXT_PLAIN).exchange();
      responseSpec.expectStatus().isOk();
      ParameterizedTypeReference<ResponseVo<List<Configuration>>> paramTypeRef = null;
      paramTypeRef = new ParameterizedTypeReference<>() {
      };
      final FluxExchangeResult<ResponseVo<List<Configuration>>> returnResult = responseSpec
          .returnResult(paramTypeRef);
      final Flux<ResponseVo<List<Configuration>>> responseBody = returnResult.getResponseBody();
      responseBody.doOnNext(o -> o.getData().forEach(System.out::println)).subscribe();
    } catch (final Exception e) {
      Assertions.assertTrue(false, e.getMessage());
    }
  }

  @Test
  @Order(21)
  @DisplayName("test '/conf/getOrEmpty/{companyId}/{ignoreNotExist}' api "
      + "on error 1(not exist company id and ignoreNotExist is false)")
  public void getOrEmptyOnError1() throws Exception {
    try {
      final String companyId = this.getCompanyId();

      final ResponseSpec responseSpec = this.getWebTestClient().get()
          .uri("/conf/getOrEmpty/{companyId}/{ignoreNotExist}", companyId, "false")
          .accept(MediaType.TEXT_PLAIN).exchange();
      responseSpec.expectStatus().is5xxServerError();
    } catch (final Exception e) {
      Assertions.assertTrue(false, e.getMessage());
    }
  }

  @Test
  // @RepeatedTest(100)
  @Order(10)
  @DisplayName("test '/conf/save/{companyId}' api")
  public void save() {
    try {
      final List<Configuration> list = new ArrayList<>();

      final String companyId = this.getCompanyId();

      final Configuration config = new Configuration();
      config.setCompanyId(companyId);
      config.setName("test");
      config.setId("abc");
      list.add(config);

      final ResponseSpec responseSpec = this.getWebTestClient().put()
          .uri("/conf/save/{companyId}", companyId).accept(MediaType.APPLICATION_JSON)
          .body(BodyInserters.fromValue(list)).exchange();
      responseSpec.expectStatus().isOk();
    } catch (final Exception e) {
      Assertions.assertTrue(false, e.getMessage());
    }
  }

}
