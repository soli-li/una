package com.una.system.mananger;

import java.util.HashSet;
import java.util.Set;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.util.MimeTypeUtils;

import com.una.common.Constants;
import com.una.system.manager.model.UrlPerm;
import com.una.system.manager.pojo.RequestHeadKey;
import com.una.system.manager.pojo.SpecialId;
import com.una.system.manager.pojo.req.SearchUrlPerm;

public class UrlPermControllerTest extends ManagerApplicationTests {
  @Autowired
  private SpecialId specialId;
  @Autowired
  private RequestHeadKey requestHeadKey;

  @Test
  @Order(20)
  @DisplayName("test '/url/findByPerm' api")
  public void findByRole() throws Exception {
    final Set<String> roleIds = new HashSet<>();
    roleIds.add("global-base");
    final MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.post("/url/findByPerm");
    builder.header(HttpHeaders.CONTENT_TYPE, MimeTypeUtils.APPLICATION_JSON_VALUE);
    builder.content(this.getObjectMapper().writeValueAsString(roleIds));

    final ResultActions resultActions = this.getMockMvc().perform(builder);
    resultActions.andExpect(MockMvcResultMatchers.status().is(200));
  }

  @Test
  @Order(40)
  @DisplayName("test '/url/save' api")
  public void save() throws Exception {
    final UrlPerm urlPerm = new UrlPerm();
    urlPerm.setName("test 1");
    urlPerm.setRemark("test");
    urlPerm.setStatus(Constants.ENABLE);
    urlPerm.setUri("/test");

    final MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.put("/url/save");
    builder.header(HttpHeaders.CONTENT_TYPE, MimeTypeUtils.APPLICATION_JSON_VALUE);
    builder.header(this.requestHeadKey.getCompanyIdKey(), this.getCompanyId());
    builder.header(this.requestHeadKey.getPermIdKey(), String.format("[\"%s\"]", this.specialId.getSuperuserAddData()));
    builder.header(this.requestHeadKey.getUserIdKey(), this.getUserId());
    builder.content(this.getObjectMapper().writeValueAsString(urlPerm));

    final ResultActions resultActions = this.getMockMvc().perform(builder);
    resultActions.andExpect(MockMvcResultMatchers.status().is(200));
  }

  @Test
  @Order(41)
  @DisplayName("test '/url/save' api on error 1(permissions is incorrect)")
  public void saveOnError1() throws Exception {
    final UrlPerm urlPerm = new UrlPerm();
    urlPerm.setName("test 1");
    urlPerm.setRemark("test");
    urlPerm.setStatus(Constants.ENABLE);
    urlPerm.setUri("/test");
    urlPerm.setPermissionsId("abc");

    final MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.put("/url/save");
    builder.header(HttpHeaders.CONTENT_TYPE, MimeTypeUtils.APPLICATION_JSON_VALUE);
    builder.header(this.requestHeadKey.getCompanyIdKey(), this.getCompanyId());
    builder.header(this.requestHeadKey.getPermIdKey(), String.format("[\"%s\"]", this.specialId.getSuperuserAddData()));
    builder.header(this.requestHeadKey.getUserIdKey(), this.getUserId());
    builder.content(this.getObjectMapper().writeValueAsString(urlPerm));

    final ResultActions resultActions = this.getMockMvc().perform(builder);
    resultActions.andExpect(MockMvcResultMatchers.status().is(500));
  }

  @Test
  @Order(41)
  @DisplayName("test '/url/save' api on error 2(status is incorrect)")
  public void saveOnError2() throws Exception {
    final UrlPerm urlPerm = new UrlPerm();
    urlPerm.setName("test 1");
    urlPerm.setRemark("test");
    urlPerm.setStatus("");
    urlPerm.setUri("/test");
    urlPerm.setPermissionsId("abc");

    final MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.put("/url/save");
    builder.header(HttpHeaders.CONTENT_TYPE, MimeTypeUtils.APPLICATION_JSON_VALUE);
    builder.header(this.requestHeadKey.getCompanyIdKey(), this.getCompanyId());
    builder.header(this.requestHeadKey.getPermIdKey(), String.format("[\"%s\"]", this.specialId.getSuperuserAddData()));
    builder.header(this.requestHeadKey.getUserIdKey(), this.getUserId());
    builder.content(this.getObjectMapper().writeValueAsString(urlPerm));

    final ResultActions resultActions = this.getMockMvc().perform(builder);
    resultActions.andExpect(MockMvcResultMatchers.status().is(500));
  }

  @Test
  @Order(30)
  @DisplayName("test '/url/search' api")
  public void search() throws Exception {
    final SearchUrlPerm searchUrlPerm = new SearchUrlPerm();
    final MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.post("/url/search");
    builder.header(HttpHeaders.CONTENT_TYPE, MimeTypeUtils.APPLICATION_JSON_VALUE);
    builder.content(this.getObjectMapper().writeValueAsString(searchUrlPerm));

    final ResultActions resultActions = this.getMockMvc().perform(builder);
    resultActions.andExpect(MockMvcResultMatchers.status().is(200));
  }

}
