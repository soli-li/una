package com.una.system.mananger;

import com.una.common.Constants;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.util.MimeTypeUtils;

public class PasswordControllerTest extends ManagerApplicationTest {

  @Test
  @Order(20)
  @DisplayName("test '/password/match' api")
  public void match() {
    try {
      final MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.post("/password/match");
      builder.header(HttpHeaders.CONTENT_TYPE, MimeTypeUtils
          .parseMimeType(Constants.MEDIA_TYPE_X_WWW_FORM_URLENCODED.toString() + ";charset=UTF-8"));
      builder.param("username", "00001");
      builder.param("password", "soli");

      final ResultActions resultActions = this.getMockMvc().perform(builder);
      resultActions.andExpect(MockMvcResultMatchers.status().is(200));
      // resultActions.andDo(MockMvcResultHandlers.print());
    } catch (final Exception e) {
      Assertions.assertTrue(false, e.getMessage());
    }
  }
}
