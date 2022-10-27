package com.una.system.mananger;

import com.una.common.Constants;
import com.una.system.manager.model.Company;
import com.una.system.manager.pojo.RequestHeadKey;
import com.una.system.manager.pojo.SpecialId;
import com.una.system.manager.pojo.req.SearchCompany;
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

public class CompanyControllerTest extends ManagerApplicationTest {
  @Autowired
  private SpecialId specialId;
  @Autowired
  private RequestHeadKey requestHeadKey;

  @Test
  @Order(20)
  @DisplayName("test '/company/exist' api")
  public void existName() {
    try {
      final MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.post("/company/exist");
      builder.header(HttpHeaders.CONTENT_TYPE, Constants.MEDIA_TYPE_X_WWW_FORM_URLENCODED);
      builder.header(this.requestHeadKey.getCompanyIdKey(), this.getCompanyId());
      builder.header(this.requestHeadKey.getUserIdKey(), this.getUserId());
      builder.content("name=company%20manager");

      ResultActions resultActions;
      resultActions = this.getMockMvc().perform(builder);
      resultActions.andDo(MockMvcResultHandlers.print());
      resultActions.andExpect(MockMvcResultMatchers.status().is(200));
      resultActions.andExpect(MockMvcResultMatchers.content().bytes("true".getBytes()));
    } catch (final Exception e) {
      Assertions.assertTrue(false, e.getMessage());
    }
  }

  @Test
  @Order(21)
  @DisplayName("test '/company/exist' api on error 1(all parameter is empty)")
  public void existOnError1() {
    try {
      final MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.post("/company/exist");
      builder.header(HttpHeaders.CONTENT_TYPE, Constants.MEDIA_TYPE_X_WWW_FORM_URLENCODED);
      builder.header(this.requestHeadKey.getCompanyIdKey(), this.getCompanyId());
      builder.header(this.requestHeadKey.getUserIdKey(), this.getUserId());
      builder.content("");

      final ResultActions resultActions = this.getMockMvc().perform(builder);
      resultActions.andDo(MockMvcResultHandlers.print());
      resultActions.andExpect(MockMvcResultMatchers.status().is(500));
    } catch (final Exception e) {
      Assertions.assertTrue(false, e.getMessage());
    }
  }

  @Test
  @Order(20)
  @DisplayName("test '/company/exist' api on error 2(all parameter has value)")
  public void existOnError2() {
    try {
      final MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.post("/company/exist");
      builder.header(HttpHeaders.CONTENT_TYPE, Constants.MEDIA_TYPE_X_WWW_FORM_URLENCODED);
      builder.header(this.requestHeadKey.getCompanyIdKey(), this.getCompanyId());
      builder.header(this.requestHeadKey.getUserIdKey(), this.getUserId());
      builder.content("name=company%20manager&shortName=CM");

      final ResultActions resultActions = this.getMockMvc().perform(builder);
      resultActions.andExpect(MockMvcResultMatchers.status().is(500));
    } catch (final Exception e) {
      Assertions.assertTrue(false, e.getMessage());
    }
  }

  @Test
  @Order(20)
  @DisplayName("test '/company/exist' api")
  public void existShortName() {
    try {
      final MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.post("/company/exist");
      builder.header(HttpHeaders.CONTENT_TYPE, Constants.MEDIA_TYPE_X_WWW_FORM_URLENCODED);
      builder.header(this.requestHeadKey.getCompanyIdKey(), this.getCompanyId());
      builder.header(this.requestHeadKey.getUserIdKey(), this.getUserId());
      builder.content("shortName=CM");

      final ResultActions resultActions = this.getMockMvc().perform(builder);
      resultActions.andExpect(MockMvcResultMatchers.status().is(200));
      resultActions.andExpect(MockMvcResultMatchers.content().bytes("true".getBytes()));
    } catch (final Exception e) {
      Assertions.assertTrue(false, e.getMessage());
    }
  }

  @Test
  @Order(50)
  @DisplayName("test '/company/save' api for company")
  @Transactional
  @Rollback
  public void save() {
    try {
      final Company company = new Company();
      company.setAddress("test");
      company.setLegalPerson("test");
      company.setName("test");
      company.setPwPolicyId("8f4ffca8-72b2-491f-9c1c-fbd8807a6ba6");
      company.setShortName("test");
      company.setStatus(Constants.ENABLE);
      company.setId(this.getCompanyId());

      final MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.put("/company/save");
      builder.header(HttpHeaders.CONTENT_TYPE, MimeTypeUtils.APPLICATION_JSON_VALUE);
      builder.header(this.requestHeadKey.getCompanyIdKey(), this.getCompanyId());
      builder.header(this.requestHeadKey.getUserIdKey(), this.getUserId());
      builder.content(this.getObjectMapper().writeValueAsString(company));

      final ResultActions resultActions = this.getMockMvc().perform(builder);
      resultActions.andExpect(MockMvcResultMatchers.status().is(200));
    } catch (final Exception e) {
      Assertions.assertTrue(false, e.getMessage());
    }
  }

  @Test
  @Order(50)
  @DisplayName("test '/company/save' api for superuser")
  @Transactional
  @Rollback
  public void saveForSuperUser() {
    try {
      final Company company = new Company();
      company.setAddress("test");
      company.setLegalPerson("test");
      company.setName("test1");
      company.setPwPolicyId("8f4ffca8-72b2-491f-9c1c-fbd8807a6ba6");
      company.setShortName("test1");
      company.setStatus(Constants.ENABLE);

      final MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.put("/company/save");
      builder.header(HttpHeaders.CONTENT_TYPE, MimeTypeUtils.APPLICATION_JSON_VALUE);
      builder.header(this.requestHeadKey.getCompanyIdKey(), this.getCompanyId());
      builder.header(this.requestHeadKey.getPermIdKey(),
          String.format("[\"%s\"]", this.specialId.getSuperuserAddData()));
      builder.header(this.requestHeadKey.getUserIdKey(), this.getUserId());
      builder.content(this.getObjectMapper().writeValueAsString(company));

      final ResultActions resultActions = this.getMockMvc().perform(builder);
      resultActions.andExpect(MockMvcResultMatchers.status().is(200));
    } catch (final Exception e) {
      Assertions.assertTrue(false, e.getMessage());
    }
  }

  @Test
  @Order(51)
  @DisplayName("test '/company/save' api on error 1(no permissions)")
  public void saveOnError1() {
    try {
      final Company company = new Company();
      company.setAddress("test");
      company.setLegalPerson("test");
      company.setName("test1");
      company.setPwPolicyId("8f4ffca8-72b2-491f-9c1c-fbd8807a6ba6");
      company.setShortName("test1");
      company.setStatus(Constants.ENABLE);

      final MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.put("/company/save");
      builder.header(HttpHeaders.CONTENT_TYPE, MimeTypeUtils.APPLICATION_JSON_VALUE);
      builder.header(this.requestHeadKey.getCompanyIdKey(), this.getCompanyId());
      builder.header(this.requestHeadKey.getUserIdKey(), this.getUserId());
      builder.content(this.getObjectMapper().writeValueAsString(company));

      final ResultActions resultActions = this.getMockMvc().perform(builder);
      resultActions.andExpect(MockMvcResultMatchers.status().is(404));
    } catch (final Exception e) {
      Assertions.assertTrue(false, e.getMessage());
    }
  }

  @Test
  @Order(51)
  @DisplayName("test '/company/save' api on error 2(duplicate name)")
  public void saveOnError2() {
    try {
      this.save();
      final Company company = new Company();
      company.setAddress("test");
      company.setLegalPerson("test");
      company.setName("Test");
      company.setPwPolicyId("8f4ffca8-72b2-491f-9c1c-fbd8807a6ba6");
      company.setShortName("T");
      company.setStatus(Constants.ENABLE);

      final MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.put("/company/save");
      builder.header(HttpHeaders.CONTENT_TYPE, MimeTypeUtils.APPLICATION_JSON_VALUE);
      builder.header(this.requestHeadKey.getCompanyIdKey(), this.getCompanyId());
      builder.header(this.requestHeadKey.getPermIdKey(),
          String.format("[\"%s\"]", this.specialId.getSuperuserAddData()));
      builder.header(this.requestHeadKey.getUserIdKey(), this.getUserId());
      builder.content(this.getObjectMapper().writeValueAsString(company));

      final ResultActions resultActions = this.getMockMvc().perform(builder);
      resultActions.andExpect(MockMvcResultMatchers.status().is(500));
    } catch (final Exception e) {
      Assertions.assertTrue(false, e.getMessage());
    }
  }

  @Test
  @Order(51)
  @DisplayName("test '/company/save' api on error 3(password policy is null)")
  public void saveOnError3() {
    try {
      final Company company = new Company();
      company.setAddress("test");
      company.setLegalPerson("test");
      company.setName("test2");
      company.setShortName("test2");
      company.setStatus(Constants.ENABLE);

      final MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.put("/company/save");
      builder.header(HttpHeaders.CONTENT_TYPE, MimeTypeUtils.APPLICATION_JSON_VALUE);
      builder.header(this.requestHeadKey.getCompanyIdKey(), this.getCompanyId());
      builder.header(this.requestHeadKey.getPermIdKey(),
          String.format("[\"%s\"]", this.specialId.getSuperuserAddData()));
      builder.header(this.requestHeadKey.getUserIdKey(), this.getUserId());
      builder.content(this.getObjectMapper().writeValueAsString(company));

      final ResultActions resultActions = this.getMockMvc().perform(builder);
      resultActions.andExpect(MockMvcResultMatchers.status().is(500));
    } catch (final Exception e) {
      Assertions.assertTrue(false, e.getMessage());
    }
  }

  @Test
  @Order(51)
  @DisplayName("test '/company/save' api on error 4(status is incorrect)")
  public void saveOnError4() {
    try {
      final Company company = new Company();
      company.setAddress("test");
      company.setLegalPerson("test");
      company.setName("test2");
      company.setPwPolicyId("8f4ffca8-72b2-491f-9c1c-fbd8807a6ba6");
      company.setShortName("test2");
      company.setStatus("");

      final MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.put("/company/save");
      builder.header(HttpHeaders.CONTENT_TYPE, MimeTypeUtils.APPLICATION_JSON_VALUE);
      builder.header(this.requestHeadKey.getCompanyIdKey(), this.getCompanyId());
      builder.header(this.requestHeadKey.getPermIdKey(),
          String.format("[\"%s\"]", this.specialId.getSuperuserAddData()));
      builder.header(this.requestHeadKey.getUserIdKey(), this.getUserId());
      builder.content(this.getObjectMapper().writeValueAsString(company));

      final ResultActions resultActions = this.getMockMvc().perform(builder);
      resultActions.andExpect(MockMvcResultMatchers.status().is(500));
    } catch (final Exception e) {
      Assertions.assertTrue(false, e.getMessage());
    }
  }

  @Test
  @Order(20)
  @DisplayName("test '/company/search' api")
  public void search() {
    try {
      final SearchCompany searchCompany = new SearchCompany();

      final MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.post("/company/search");
      builder.header(HttpHeaders.CONTENT_TYPE, MimeTypeUtils.APPLICATION_JSON_VALUE);
      builder.header(this.requestHeadKey.getCompanyIdKey(), this.getCompanyId());
      builder.header(this.requestHeadKey.getPermIdKey(),
          String.format("[\"%s\"]", this.specialId.getNotIncludeCompanyId()));
      builder.content(this.getObjectMapper().writeValueAsString(searchCompany));

      final ResultActions resultActions = this.getMockMvc().perform(builder);
      resultActions.andExpect(MockMvcResultMatchers.status().is(200));
    } catch (final Exception e) {
      Assertions.assertTrue(false, e.getMessage());
    }
  }
}
