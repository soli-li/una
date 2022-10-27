package com.una.system.mananger;

import com.una.common.Constants;
import com.una.system.manager.model.Group;
import com.una.system.manager.pojo.RequestHeadKey;
import com.una.system.manager.pojo.SpecialId;
import com.una.system.manager.pojo.req.SearchGroup;
import java.util.List;
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
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.MimeTypeUtils;

public class GroupControllerTest extends ManagerApplicationTest {
  @Autowired
  private SpecialId specialId;
  @Autowired
  private RequestHeadKey requestHeadKey;

  @Test
  @Order(30)
  @DisplayName("test '/group/exist' api")
  public void exist() {
    try {
      final MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.post("/group/exist");
      builder.header(HttpHeaders.CONTENT_TYPE, Constants.MEDIA_TYPE_X_WWW_FORM_URLENCODED);
      builder.header(this.requestHeadKey.getCompanyIdKey(), this.getCompanyId());
      builder.header(this.requestHeadKey.getUserIdKey(), this.getUserId());
      builder.content("name=Default&companyId=" + this.getCompanyId());

      final ResultActions resultActions = this.getMockMvc().perform(builder);
      resultActions.andExpect(MockMvcResultMatchers.status().is(200));
      resultActions.andExpect(MockMvcResultMatchers.content().bytes("true".getBytes()));
    } catch (final Exception e) {
      Assertions.assertTrue(false, e.getMessage());
    }
  }

  @Test
  @Order(50)
  @DisplayName("test '/group/findByRole' api")
  public void findByRole() {
    try {
      final List<String> roleIdList = List.of(this.getRoleId());

      final MockHttpServletRequestBuilder builder = MockMvcRequestBuilders
          .post("/group/findByRole");
      builder.header(HttpHeaders.CONTENT_TYPE, MimeTypeUtils.APPLICATION_JSON_VALUE);
      builder.header(this.requestHeadKey.getCompanyIdKey(), this.getCompanyId());
      builder.header(this.requestHeadKey.getUserIdKey(), this.getUserId());
      builder.content(this.getObjectMapper().writeValueAsString(roleIdList));

      final ResultActions resultActions = this.getMockMvc().perform(builder);

      resultActions.andExpect(MockMvcResultMatchers.status().is(200));
    } catch (final Exception e) {
      Assertions.assertTrue(false, e.getMessage());
    }
  }

  @Test
  @Order(60)
  @DisplayName("test '/group/findByUser' api")
  public void findByUser() {
    try {
      final List<String> userIdList = List.of(this.getUserId());

      final MockHttpServletRequestBuilder builder = MockMvcRequestBuilders
          .post("/group/findByUser");
      builder.header(HttpHeaders.CONTENT_TYPE, MimeTypeUtils.APPLICATION_JSON_VALUE);
      builder.header(this.requestHeadKey.getCompanyIdKey(), this.getCompanyId());
      builder.header(this.requestHeadKey.getUserIdKey(), this.getUserId());
      builder.content(this.getObjectMapper().writeValueAsString(userIdList));

      final ResultActions resultActions = this.getMockMvc().perform(builder);

      resultActions.andExpect(MockMvcResultMatchers.status().is(200));
    } catch (final Exception e) {
      Assertions.assertTrue(false, e.getMessage());
    }
  }

  @Test
  @Order(40)
  @DisplayName("test '/group/save' api")
  @Transactional
  @Rollback
  public void save() {
    try {
      final Group group = new Group();
      group.setName("test 1");
      group.setRemark("test");
      group.setStatus(Constants.ENABLE);
      group.setCompanyId(this.getCompanyId());

      final MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.put("/group/save");
      builder.header(HttpHeaders.CONTENT_TYPE, MimeTypeUtils.APPLICATION_JSON_VALUE);
      builder.header(this.requestHeadKey.getCompanyIdKey(), this.getCompanyId());
      builder.header(this.requestHeadKey.getPermIdKey(),
          String.format("[\"%s\"]", this.specialId.getSuperuserAddData()));
      builder.header(this.requestHeadKey.getUserIdKey(), this.getUserId());
      builder.content(this.getObjectMapper().writeValueAsString(group));

      final ResultActions resultActions = this.getMockMvc().perform(builder);

      resultActions.andExpect(MockMvcResultMatchers.status().is(200));
    } catch (final Exception e) {
      Assertions.assertTrue(false, e.getMessage());
    }
  }

  @Test
  @Order(40)
  @DisplayName("test '/group/save' api for company")
  @Transactional
  @Rollback
  public void saveForCompany() {
    try {
      final Group group = new Group();
      group.setName("test 2");
      group.setRemark("test");
      group.setStatus(Constants.ENABLE);
      group.setCompanyId(this.getCompanyId());

      final MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.put("/group/save");
      builder.header(HttpHeaders.CONTENT_TYPE, MimeTypeUtils.APPLICATION_JSON_VALUE);
      builder.header(this.requestHeadKey.getCompanyIdKey(), this.getCompanyId());
      builder.header(this.requestHeadKey.getUserIdKey(), this.getUserId());
      builder.content(this.getObjectMapper().writeValueAsString(group));

      final ResultActions resultActions = this.getMockMvc().perform(builder);

      resultActions.andExpect(MockMvcResultMatchers.status().is(200));
    } catch (final Exception e) {
      Assertions.assertTrue(false, e.getMessage());
    }
  }

  @Test
  @Order(40)
  @DisplayName("test '/group/save' api for update")
  @Transactional
  @Rollback
  public void saveForUpdate() {
    try {
      final Group group = new Group();
      group.setId(this.getGroupId());
      group.setName("第一级");
      group.setRemark("test");
      group.setStatus(Constants.ENABLE);
      group.setCompanyId(this.getCompanyId());

      final MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.put("/group/save");
      builder.header(HttpHeaders.CONTENT_TYPE, MimeTypeUtils.APPLICATION_JSON_VALUE);
      builder.header(this.requestHeadKey.getCompanyIdKey(), this.getCompanyId());
      builder.header(this.requestHeadKey.getUserIdKey(), this.getUserId());
      builder.content(this.getObjectMapper().writeValueAsString(group));

      final ResultActions resultActions = this.getMockMvc().perform(builder);
      resultActions.andExpect(MockMvcResultMatchers.status().is(200));
    } catch (final Exception e) {
      Assertions.assertTrue(false, e.getMessage());
    }
  }

  @Test
  @Order(41)
  @DisplayName("test '/group/save' api on error 1(name is null)")
  public void saveOnError1() {
    try {
      final Group group = new Group();
      group.setId(this.getGroupId());
      group.setRemark("test");
      group.setStatus(Constants.ENABLE);
      group.setCompanyId(this.getCompanyId());

      final MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.put("/group/save");
      builder.header(HttpHeaders.CONTENT_TYPE, MimeTypeUtils.APPLICATION_JSON_VALUE);
      builder.header(this.requestHeadKey.getCompanyIdKey(), this.getCompanyId());
      builder.header(this.requestHeadKey.getUserIdKey(), this.getUserId());
      builder.content(this.getObjectMapper().writeValueAsString(group));

      final ResultActions resultActions = this.getMockMvc().perform(builder);
      resultActions.andExpect(MockMvcResultMatchers.status().is(500));
    } catch (final Exception e) {
      Assertions.assertTrue(false, e.getMessage());
    }
  }

  @Test
  @Order(41)
  @DisplayName("test '/group/save' api on error 2(company is null)")
  public void saveOnError2() {
    try {
      final Group group = new Group();
      group.setId(this.getGroupId());
      group.setName("test 1");
      group.setRemark("test");
      group.setStatus(Constants.ENABLE);

      final MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.put("/group/save");
      builder.header(HttpHeaders.CONTENT_TYPE, MimeTypeUtils.APPLICATION_JSON_VALUE);
      builder.header(this.requestHeadKey.getCompanyIdKey(), this.getCompanyId());
      builder.header(this.requestHeadKey.getUserIdKey(), this.getUserId());
      builder.header(this.requestHeadKey.getPermIdKey(),
          String.format("[\"%s\"]", this.specialId.getSuperuserAddData()));
      builder.content(this.getObjectMapper().writeValueAsString(group));

      final ResultActions resultActions = this.getMockMvc().perform(builder);
      resultActions.andExpect(MockMvcResultMatchers.status().is(500));
    } catch (final Exception e) {
      Assertions.assertTrue(false, e.getMessage());
    }
  }

  @Test
  @Order(41)
  @DisplayName("test '/group/save' api on error 3(status is incorrect)")
  public void saveOnError3() {
    try {
      final Group group = new Group();
      group.setId(this.getGroupId());
      group.setName("test 1");
      group.setRemark("test");
      group.setStatus("");
      group.setCompanyId(this.getCompanyId());

      final MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.put("/group/save");
      builder.header(HttpHeaders.CONTENT_TYPE, MimeTypeUtils.APPLICATION_JSON_VALUE);
      builder.header(this.requestHeadKey.getCompanyIdKey(), this.getCompanyId());
      builder.header(this.requestHeadKey.getUserIdKey(), this.getUserId());
      builder.content(this.getObjectMapper().writeValueAsString(group));

      final ResultActions resultActions = this.getMockMvc().perform(builder);
      resultActions.andExpect(MockMvcResultMatchers.status().is(500));
    } catch (final Exception e) {
      Assertions.assertTrue(false, e.getMessage());
    }
  }

  @Test
  @Order(41)
  @DisplayName("test '/group/save' api on error 4(duplicate name)")
  public void saveOnError4() {
    try {
      final Group group = new Group();
      group.setName("Default");
      group.setRemark("test");
      group.setStatus(Constants.ENABLE);
      group.setCompanyId(this.getCompanyId());

      final MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.put("/group/save");
      builder.header(HttpHeaders.CONTENT_TYPE, MimeTypeUtils.APPLICATION_JSON_VALUE);
      builder.header(this.requestHeadKey.getCompanyIdKey(), this.getCompanyId());
      builder.header(this.requestHeadKey.getUserIdKey(), this.getUserId());
      builder.content(this.getObjectMapper().writeValueAsString(group));

      final ResultActions resultActions = this.getMockMvc().perform(builder);
      resultActions.andExpect(MockMvcResultMatchers.status().is(500));
    } catch (final Exception e) {
      Assertions.assertTrue(false, e.getMessage());
    }
  }

  @Test
  @Order(20)
  @DisplayName("test '/group/search' api")
  public void search() {
    try {
      final SearchGroup searchGroup = new SearchGroup();

      final MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.post("/group/search");
      builder.header(HttpHeaders.CONTENT_TYPE, MimeTypeUtils.APPLICATION_JSON_VALUE);
      builder.header(this.requestHeadKey.getCompanyIdKey(), this.getCompanyId());
      builder.header(this.requestHeadKey.getPermIdKey(),
          String.format("[\"%s\"]", this.specialId.getNotIncludeCompanyId()));
      builder.content(this.getObjectMapper().writeValueAsString(searchGroup));

      final ResultActions resultActions = this.getMockMvc().perform(builder);
      resultActions.andExpect(MockMvcResultMatchers.status().is(200));
    } catch (final Exception e) {
      Assertions.assertTrue(false, e.getMessage());
    }
  }

  @Test
  @Order(70)
  @DisplayName("test '/group/updateRelation' api")
  @Transactional
  @Rollback
  public void updateRelation() {
    try {
      final Group group = new Group();
      group.setId(this.getGroupId());

      final MockHttpServletRequestBuilder builder = MockMvcRequestBuilders
          .post("/group/updateRelation");
      builder.header(HttpHeaders.CONTENT_TYPE, MimeTypeUtils.APPLICATION_JSON_VALUE);
      builder.header(this.requestHeadKey.getCompanyIdKey(), this.getCompanyId());
      builder.header(this.requestHeadKey.getUserIdKey(), this.getUserId());
      builder.header(this.requestHeadKey.getPermIdKey(),
          String.format("[\"%s\"]", this.specialId.getSuperuserAddData()));
      builder.content(this.getObjectMapper().writeValueAsString(group));

      final ResultActions resultActions = this.getMockMvc().perform(builder);

      resultActions.andExpect(MockMvcResultMatchers.status().is(200));
    } catch (final Exception e) {
      Assertions.assertTrue(false, e.getMessage());
    }
  }
}
