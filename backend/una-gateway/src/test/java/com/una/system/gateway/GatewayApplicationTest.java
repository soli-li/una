package com.una.system.gateway;

import com.una.common.pojo.User;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClient.RequestBodySpec;
import org.springframework.web.reactive.function.client.WebClient.ResponseSpec;
import reactor.core.publisher.Mono;

@SpringBootTest(classes = GatewayApplication.class,
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
// @Rollback(value = true)
@ActiveProfiles(profiles = { "unit-test" })
@ExtendWith(SpringExtension.class)
@TestMethodOrder(value = MethodOrderer.OrderAnnotation.class)
public class GatewayApplicationTest {

  @Value("${app.security.formLogin.url:/login}")
  private String formLoginUrl;
  @Value("${app.security.formLogin.usernameParameter:username}")
  private String usernameParameter;
  @Value("${app.security.formLogin.passwordParameter:password}")
  private String passwordParameter;
  @Value("${app.security.auth-header:x-auth-token}")
  private String authHeaderName;
  @Value("${app.request.head.company-id-key}")
  private String companyIdHeaderName;

  @Value("${app.test.manager.url}")
  private String managerUrl;
  @Value("${app.test.manager.enanled:false}")
  private boolean managerEnabled;

  private User getLoginUser(final String session) {
    final ResponseSpec responseSpec = WebClient.builder().build().get()
        .uri(this.managerUrl + "/user/current").header(this.authHeaderName, session).retrieve();
    final User user = responseSpec.bodyToMono(User.class).block();
    return user;
  }

  @Test
  @Order(1)
  @DisplayName("test '/login' api")
  public void login() throws Exception {
    try {
      if (!this.managerEnabled) {
        return;
      }
      final String session = this.loginReturnSessionId();
      System.out.println("session id:\t" + session);

      final User user = this.getLoginUser(session);
      System.out.println("user id:\t" + user.getId());
    } catch (final Exception e) {
      Assertions.assertTrue(false, e.getMessage());
    }
  }

  private String loginReturnSessionId() throws Exception {
    RequestBodySpec requestBodySpec = WebClient.builder().build().post()
        .uri(this.managerUrl + this.formLoginUrl);
    requestBodySpec = requestBodySpec.contentType(
        MediaType.parseMediaType(com.una.common.Constants.MEDIA_TYPE_X_WWW_FORM_URLENCODED));
    requestBodySpec.header(this.companyIdHeaderName, "c808faa6-6721-42a1-bdd7-3ca88141a67e");
    final ResponseSpec responseSpec = requestBodySpec.body(BodyInserters
        .fromFormData(this.usernameParameter, "00001").with(this.passwordParameter, "soli"))
        .retrieve();

    ParameterizedTypeReference<Map<String, String>> paramTypeRef = null;
    paramTypeRef = new ParameterizedTypeReference<>() {
    };
    final Mono<ResponseEntity<Map<String, String>>> response = responseSpec.toEntity(paramTypeRef);
    final ResponseEntity<Map<String, String>> responseEntity = response.block();
    final HttpHeaders headers = responseEntity.getHeaders();
    final List<String> list = headers.get(this.authHeaderName);
    return String.join(",", list);
  }

}
