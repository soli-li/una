package com.una.system.mananger;

import com.una.system.manager.model.PasswordPolicy;
import com.una.system.manager.pojo.RequestHeadKey;
import com.una.system.manager.pojo.SpecialId;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.MimeTypeUtils;

public class PasswordPolicyControllerTest extends ManagerApplicationTest {

  @Autowired
  private SpecialId specialId;
  @Autowired
  private RequestHeadKey requestHeadKey;

  @Test
  @Order(20)
  @DisplayName("test '/pwPolicy/all' api")
  public void all() {
    try {
      final MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.get("/pwPolicy/all");
      builder.header(HttpHeaders.CONTENT_TYPE, MimeTypeUtils.APPLICATION_JSON_VALUE);

      final ResultActions resultActions = this.getMockMvc().perform(builder);
      resultActions.andExpect(MockMvcResultMatchers.status().is(200));

      resultActions.andDo(MockMvcResultHandlers.print());
    } catch (final Exception e) {
      Assertions.assertTrue(false, e.getMessage());
    }
  }

  @Test
  @Order(30)
  @DisplayName("test '/pwPolicy/save' api")
  @Transactional
  @Rollback
  public void save() {
    try {
      final PasswordPolicy passwordPolicy = new PasswordPolicy();
      passwordPolicy.setLabel("test");
      passwordPolicy.setDescription("test");
      passwordPolicy.setCaseSensitive(true);
      passwordPolicy.setDigitals(true);
      passwordPolicy.setLength(10);
      passwordPolicy.setLetters(true);
      passwordPolicy.setMaximumAge(10);
      passwordPolicy.setNonAlphanumeric(true);
      passwordPolicy.setRepeatCount(10);

      final MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.put("/pwPolicy/save");
      builder.header(HttpHeaders.CONTENT_TYPE, MimeTypeUtils.APPLICATION_JSON_VALUE);
      builder.header(this.requestHeadKey.getPermIdKey(),
          String.format("[\"%s\"]", this.specialId.getSuperuserAddData()));
      builder.header(this.requestHeadKey.getUserIdKey(), this.getUserId());
      builder.content(this.getObjectMapper().writeValueAsString(passwordPolicy));

      final ResultActions resultActions = this.getMockMvc().perform(builder);
      resultActions.andExpect(MockMvcResultMatchers.status().is(200));
    } catch (final Exception e) {
      Assertions.assertTrue(false, e.getMessage());
    }
  }

  @Test
  @Order(31)
  @DisplayName("test '/pwPolicy/save' api on error (no permissions)")
  public void saveOnError() {
    try {
      final PasswordPolicy passwordPolicy = new PasswordPolicy();
      passwordPolicy.setLabel("test1");
      passwordPolicy.setDescription("test1");
      passwordPolicy.setCaseSensitive(true);
      passwordPolicy.setDigitals(true);
      passwordPolicy.setLength(11);
      passwordPolicy.setLetters(true);
      passwordPolicy.setMaximumAge(11);
      passwordPolicy.setNonAlphanumeric(true);
      passwordPolicy.setRepeatCount(11);

      final MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.put("/pwPolicy/save");
      builder.header(HttpHeaders.CONTENT_TYPE, MimeTypeUtils.APPLICATION_JSON_VALUE);
      builder.content(this.getObjectMapper().writeValueAsString(passwordPolicy));

      final ResultActions resultActions = this.getMockMvc().perform(builder);
      resultActions.andExpect(MockMvcResultMatchers.status().is(404));
    } catch (final Exception e) {
      Assertions.assertTrue(false, e.getMessage());
    }
  }
}
