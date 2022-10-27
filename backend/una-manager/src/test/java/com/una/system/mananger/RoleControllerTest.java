package com.una.system.mananger;

import com.una.common.Constants;
import com.una.system.manager.model.Role;
import com.una.system.manager.pojo.RequestHeadKey;
import com.una.system.manager.pojo.SpecialId;
import com.una.system.manager.pojo.req.PermissionsRelation;
import com.una.system.manager.pojo.req.SearchRole;
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

public class RoleControllerTest extends ManagerApplicationTest {
  @Autowired
  private SpecialId specialId;
  @Autowired
  private RequestHeadKey requestHeadKey;

  @Test
  @Order(30)
  @DisplayName("test '/role/exist' api")
  public void exist() {
    try {
      final MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.post("/role/exist");
      builder.header(HttpHeaders.CONTENT_TYPE, Constants.MEDIA_TYPE_X_WWW_FORM_URLENCODED);
      builder.header(this.requestHeadKey.getCompanyIdKey(), this.getCompanyId());
      builder.header(this.requestHeadKey.getUserIdKey(), this.getUserId());
      builder.content("authority=ADMIN&companyId=" + this.getCompanyId());

      final ResultActions resultActions = this.getMockMvc().perform(builder);
      resultActions.andExpect(MockMvcResultMatchers.status().is(200));
      resultActions.andExpect(MockMvcResultMatchers.content().bytes("true".getBytes()));
    } catch (final Exception e) {
      Assertions.assertTrue(false, e.getMessage());
    }
  }

  @Test
  @Order(70)
  @DisplayName("test '/role/findByPerm' api")
  public void findByPerm() {
    try {
      final PermissionsRelation relation = new PermissionsRelation();
      relation.setCompanyId(this.getCompanyId());
      relation.setPermissionsList(List.of(this.getBasePermId()));

      final MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.post("/role/findByPerm");
      builder.header(HttpHeaders.CONTENT_TYPE, MimeTypeUtils.APPLICATION_JSON_VALUE);
      builder.header(this.requestHeadKey.getCompanyIdKey(), this.getCompanyId());
      builder.header(this.requestHeadKey.getUserIdKey(), this.getUserId());
      builder.content(this.getObjectMapper().writeValueAsString(relation));

      final ResultActions resultActions = this.getMockMvc().perform(builder);

      resultActions.andExpect(MockMvcResultMatchers.status().is(200));
    } catch (final Exception e) {
      Assertions.assertTrue(false, e.getMessage());
    }
  }

  @Test
  @Order(50)
  @DisplayName("test '/role/findByGroup' api")
  public void findByRole() {
    try {
      final List<String> groupIdList = List.of(this.getGroupId());

      final MockHttpServletRequestBuilder builder = MockMvcRequestBuilders
          .post("/role/findByGroup");
      builder.header(HttpHeaders.CONTENT_TYPE, MimeTypeUtils.APPLICATION_JSON_VALUE);
      builder.header(this.requestHeadKey.getCompanyIdKey(), this.getCompanyId());
      builder.header(this.requestHeadKey.getUserIdKey(), this.getUserId());
      builder.content(this.getObjectMapper().writeValueAsString(groupIdList));

      final ResultActions resultActions = this.getMockMvc().perform(builder);

      resultActions.andExpect(MockMvcResultMatchers.status().is(200));
    } catch (final Exception e) {
      Assertions.assertTrue(false, e.getMessage());
    }
  }

  @Test
  @Order(60)
  @DisplayName("test '/role/findByUser' api")
  public void findByUser() {
    try {
      final List<String> userIdList = List.of(this.getUserId());

      final MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.post("/role/findByUser");
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
  @DisplayName("test '/role/save' api")
  @Transactional
  @Rollback
  public void save() {
    try {
      final Role role = new Role();
      role.setAuthority("test 1");
      role.setRemark("test");
      role.setStatus(Constants.ENABLE);
      role.setCompanyId(this.getCompanyId());

      final MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.put("/role/save");
      builder.header(HttpHeaders.CONTENT_TYPE, MimeTypeUtils.APPLICATION_JSON_VALUE);
      builder.header(this.requestHeadKey.getCompanyIdKey(), this.getCompanyId());
      builder.header(this.requestHeadKey.getPermIdKey(),
          String.format("[\"%s\"]", this.specialId.getSuperuserAddData()));
      builder.header(this.requestHeadKey.getUserIdKey(), this.getUserId());
      builder.content(this.getObjectMapper().writeValueAsString(role));

      final ResultActions resultActions = this.getMockMvc().perform(builder);
      resultActions.andExpect(MockMvcResultMatchers.status().is(200));
    } catch (final Exception e) {
      Assertions.assertTrue(false, e.getMessage());
    }
  }

  @Test
  @Order(40)
  @DisplayName("test '/role/save' api for company")
  @Transactional
  @Rollback
  public void saveForCompany() {
    try {
      final Role role = new Role();
      role.setAuthority("test 2");
      role.setRemark("test");
      role.setStatus(Constants.ENABLE);
      role.setCompanyId(this.getCompanyId());

      final MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.put("/role/save");
      builder.header(HttpHeaders.CONTENT_TYPE, MimeTypeUtils.APPLICATION_JSON_VALUE);
      builder.header(this.requestHeadKey.getCompanyIdKey(), this.getCompanyId());
      builder.header(this.requestHeadKey.getUserIdKey(), this.getUserId());
      builder.content(this.getObjectMapper().writeValueAsString(role));

      final ResultActions resultActions = this.getMockMvc().perform(builder);
      resultActions.andExpect(MockMvcResultMatchers.status().is(200));
    } catch (final Exception e) {
      Assertions.assertTrue(false, e.getMessage());
    }
  }

  @Test
  @Order(40)
  @DisplayName("test '/role/save' api for update")
  @Transactional
  @Rollback
  public void saveForUpdate() {
    try {
      final Role role = new Role();
      role.setId(this.getRoleId());
      role.setAuthority("ADMIN");
      role.setRemark("test");
      role.setStatus(Constants.ENABLE);
      role.setCompanyId(this.getCompanyId());

      final MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.put("/role/save");
      builder.header(HttpHeaders.CONTENT_TYPE, MimeTypeUtils.APPLICATION_JSON_VALUE);
      builder.header(this.requestHeadKey.getCompanyIdKey(), this.getCompanyId());
      builder.header(this.requestHeadKey.getPermIdKey(),
          String.format("[\"%s\"]", this.specialId.getSuperuserAddData()));
      builder.header(this.requestHeadKey.getUserIdKey(), this.getUserId());
      builder.content(this.getObjectMapper().writeValueAsString(role));

      final ResultActions resultActions = this.getMockMvc().perform(builder);
      resultActions.andExpect(MockMvcResultMatchers.status().is(200));
    } catch (final Exception e) {
      Assertions.assertTrue(false, e.getMessage());
    }
  }

  @Test
  @Order(41)
  @DisplayName("test '/role/save' api on error 1(authority is null)")
  public void saveOnError1() {
    try {
      final Role role = new Role();
      role.setRemark("test 3");
      role.setStatus(Constants.ENABLE);
      role.setCompanyId(this.getCompanyId());

      final MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.put("/role/save");
      builder.header(HttpHeaders.CONTENT_TYPE, MimeTypeUtils.APPLICATION_JSON_VALUE);
      builder.header(this.requestHeadKey.getCompanyIdKey(), this.getCompanyId());
      builder.header(this.requestHeadKey.getUserIdKey(), this.getUserId());
      builder.content(this.getObjectMapper().writeValueAsString(role));

      final ResultActions resultActions = this.getMockMvc().perform(builder);
      resultActions.andExpect(MockMvcResultMatchers.status().is(500));
    } catch (final Exception e) {
      Assertions.assertTrue(false, e.getMessage());
    }
  }

  @Test
  @Order(41)
  @DisplayName("test '/role/save' api on error 2(company is null)")
  public void saveOnError2() {
    try {
      final Role role = new Role();
      role.setAuthority("test 4");
      role.setRemark("test");
      role.setStatus(Constants.ENABLE);

      final MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.put("/role/save");
      builder.header(HttpHeaders.CONTENT_TYPE, MimeTypeUtils.APPLICATION_JSON_VALUE);
      builder.header(this.requestHeadKey.getCompanyIdKey(), this.getCompanyId());
      builder.header(this.requestHeadKey.getPermIdKey(),
          String.format("[\"%s\"]", this.specialId.getSuperuserAddData()));
      builder.header(this.requestHeadKey.getUserIdKey(), this.getUserId());
      builder.content(this.getObjectMapper().writeValueAsString(role));

      final ResultActions resultActions = this.getMockMvc().perform(builder);
      resultActions.andExpect(MockMvcResultMatchers.status().is(500));
    } catch (final Exception e) {
      Assertions.assertTrue(false, e.getMessage());
    }
  }

  @Test
  @Order(41)
  @DisplayName("test '/role/save' api on error 3(status is incorrect)")
  public void saveOnError3() {
    try {
      final Role role = new Role();
      role.setAuthority("test 4");
      role.setRemark("test");
      role.setStatus("");
      role.setCompanyId(this.getCompanyId());

      final MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.put("/role/save");
      builder.header(HttpHeaders.CONTENT_TYPE, MimeTypeUtils.APPLICATION_JSON_VALUE);
      builder.header(this.requestHeadKey.getCompanyIdKey(), this.getCompanyId());
      builder.header(this.requestHeadKey.getUserIdKey(), this.getUserId());
      builder.content(this.getObjectMapper().writeValueAsString(role));

      final ResultActions resultActions = this.getMockMvc().perform(builder);
      resultActions.andExpect(MockMvcResultMatchers.status().is(500));
    } catch (final Exception e) {
      Assertions.assertTrue(false, e.getMessage());
    }
  }

  @Test
  @Order(41)
  @DisplayName("test '/role/save' api on error 4(duplicate name)")
  public void saveOnError4() {
    try {
      final Role role = new Role();
      role.setAuthority("admin");
      role.setRemark("test");
      role.setStatus(Constants.ENABLE);
      role.setCompanyId(this.getCompanyId());

      final MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.put("/role/save");
      builder.header(HttpHeaders.CONTENT_TYPE, MimeTypeUtils.APPLICATION_JSON_VALUE);
      builder.header(this.requestHeadKey.getCompanyIdKey(), this.getCompanyId());
      builder.header(this.requestHeadKey.getUserIdKey(), this.getUserId());
      builder.content(this.getObjectMapper().writeValueAsString(role));

      final ResultActions resultActions = this.getMockMvc().perform(builder);
      resultActions.andExpect(MockMvcResultMatchers.status().is(500));
    } catch (final Exception e) {
      Assertions.assertTrue(false, e.getMessage());
    }
  }

  @Test
  @Order(20)
  @DisplayName("test '/role/search' api")
  public void search() {
    try {
      final SearchRole searchRole = new SearchRole();

      final MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.post("/role/search");
      builder.header(HttpHeaders.CONTENT_TYPE, MimeTypeUtils.APPLICATION_JSON_VALUE);
      builder.header(this.requestHeadKey.getCompanyIdKey(), this.getCompanyId());
      builder.header(this.requestHeadKey.getPermIdKey(),
          String.format("[\"%s\"]", this.specialId.getNotIncludeCompanyId()));
      builder.content(this.getObjectMapper().writeValueAsString(searchRole));

      final ResultActions resultActions = this.getMockMvc().perform(builder);
      resultActions.andExpect(MockMvcResultMatchers.status().is(200));
    } catch (final Exception e) {
      Assertions.assertTrue(false, e.getMessage());
    }
  }

  @Test
  @Order(80)
  @DisplayName("test '/role/updateRelation' api")
  @Transactional
  @Rollback
  public void updateRelation() {
    try {
      final Role role = new Role();
      role.setId(this.getRoleId());

      final MockHttpServletRequestBuilder builder = MockMvcRequestBuilders
          .post("/role/updateRelation");
      builder.header(HttpHeaders.CONTENT_TYPE, MimeTypeUtils.APPLICATION_JSON_VALUE);
      builder.header(this.requestHeadKey.getCompanyIdKey(), this.getCompanyId());
      builder.header(this.requestHeadKey.getUserIdKey(), this.getUserId());
      builder.header(this.requestHeadKey.getPermIdKey(),
          String.format("[\"%s\"]", this.specialId.getSuperuserAddData()));
      builder.content(this.getObjectMapper().writeValueAsString(role));

      final ResultActions resultActions = this.getMockMvc().perform(builder);

      resultActions.andExpect(MockMvcResultMatchers.status().is(200));
    } catch (final Exception e) {
      Assertions.assertTrue(false, e.getMessage());
    }
  }

  @Test
  @Order(90)
  @DisplayName("test '/role/updateRelationForPerm' api")
  @Transactional
  @Rollback
  public void updateRelationForPerm() {
    try {
      final Role role = new Role();
      role.setId(this.getRoleId());

      final MockHttpServletRequestBuilder builder = MockMvcRequestBuilders
          .post("/role/updateRelationForPerm");
      builder.header(HttpHeaders.CONTENT_TYPE, MimeTypeUtils.APPLICATION_JSON_VALUE);
      builder.header(this.requestHeadKey.getCompanyIdKey(), this.getCompanyId());
      builder.header(this.requestHeadKey.getUserIdKey(), this.getUserId());
      builder.header(this.requestHeadKey.getPermIdKey(),
          String.format("[\"%s\"]", this.specialId.getSuperuserAddData()));
      builder.content(this.getObjectMapper().writeValueAsString(role));

      final ResultActions resultActions = this.getMockMvc().perform(builder);

      resultActions.andExpect(MockMvcResultMatchers.status().is(200));
    } catch (final Exception e) {
      Assertions.assertTrue(false, e.getMessage());
    }
  }
}
