package com.una.system.mananger;

import com.una.common.Constants;
import com.una.system.manager.model.Permissions;
import com.una.system.manager.pojo.RequestHeadKey;
import com.una.system.manager.pojo.SpecialId;
import com.una.system.manager.pojo.req.PermissionsRelation;
import com.una.system.manager.pojo.req.SearchPermissions;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
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

public class PermissionsControllerTest extends ManagerApplicationTest {
  @Autowired
  private SpecialId specialId;
  @Autowired
  private RequestHeadKey requestHeadKey;

  @Test
  @Order(60)
  @DisplayName("test '/perm/updateRelation' api")
  @Transactional
  @Rollback
  public void findByPerm() {
    try {
      final PermissionsRelation relation = new PermissionsRelation();
      relation.setCompanyId(this.getCompanyId());
      relation.setPermissionsList(List.of(this.getBasePermId()));

      final MockHttpServletRequestBuilder builder = MockMvcRequestBuilders
          .post("/perm/updateRelation");
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
  @Order(20)
  @DisplayName("test '/perm/findByRole' api")
  public void findByRole() {
    try {
      final Set<String> roleIds = new HashSet<>();
      roleIds.add(this.getRoleId());
      final MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.post("/perm/findByRole");
      builder.header(HttpHeaders.CONTENT_TYPE, MimeTypeUtils.APPLICATION_JSON_VALUE);
      builder.content(this.getObjectMapper().writeValueAsString(roleIds));

      final ResultActions resultActions = this.getMockMvc().perform(builder);
      resultActions.andExpect(MockMvcResultMatchers.status().is(200));
    } catch (final Exception e) {
      Assertions.assertTrue(false, e.getMessage());
    }
  }

  @Test
  @Order(50)
  @DisplayName("test '/group/findFunctionByRole' api")
  public void findFunctionByRole() {
    try {
      final List<String> roleIdList = List.of(this.getRoleId());

      final MockHttpServletRequestBuilder builder = MockMvcRequestBuilders
          .post("/perm/findFunctionByRole");
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
  @Order(40)
  @DisplayName("test '/perm/save' api")
  @Transactional
  @Rollback
  public void save() {
    try {
      final Permissions permissions = new Permissions();
      permissions.setStatus(Constants.ENABLE);
      permissions.setName("test 1");
      permissions.setRemark("test");

      final MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.put("/perm/save");
      builder.header(HttpHeaders.CONTENT_TYPE, MimeTypeUtils.APPLICATION_JSON_VALUE);
      builder.header(this.requestHeadKey.getCompanyIdKey(), this.getCompanyId());
      builder.header(this.requestHeadKey.getPermIdKey(),
          String.format("[\"%s\"]", this.specialId.getSuperuserAddData()));
      builder.header(this.requestHeadKey.getUserIdKey(), this.getUserId());
      builder.content(this.getObjectMapper().writeValueAsString(permissions));

      final ResultActions resultActions = this.getMockMvc().perform(builder);
      resultActions.andExpect(MockMvcResultMatchers.status().is(200));
    } catch (final Exception e) {
      Assertions.assertTrue(false, e.getMessage());
    }
  }

  @Test
  @Order(40)
  @DisplayName("test '/perm/save' api for special id")
  @Transactional
  @Rollback
  public void saveForSpecialId() {
    try {
      final Permissions permissions = new Permissions();
      permissions.setId("test-special-id");
      permissions.setStatus(Constants.ENABLE);
      permissions.setName("test 3");
      permissions.setRemark("test");

      final MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.put("/perm/save");
      builder.header(HttpHeaders.CONTENT_TYPE, MimeTypeUtils.APPLICATION_JSON_VALUE);
      builder.header(this.requestHeadKey.getCompanyIdKey(), this.getCompanyId());
      builder.header(this.requestHeadKey.getPermIdKey(),
          String.format("[\"%s\"]", this.specialId.getSuperuserAddData()));
      builder.header(this.requestHeadKey.getUserIdKey(), this.getUserId());
      builder.content(this.getObjectMapper().writeValueAsString(permissions));

      final ResultActions resultActions = this.getMockMvc().perform(builder);
      resultActions.andExpect(MockMvcResultMatchers.status().is(200));
    } catch (final Exception e) {
      Assertions.assertTrue(false, e.getMessage());
    }
  }

  @Test
  @Order(40)
  @DisplayName("test '/perm/save' api for update")
  @Transactional
  @Rollback
  public void saveForUpdate() {
    try {
      final Permissions permissions = new Permissions();
      permissions.setId(this.getBasePermId());
      permissions.setStatus(Constants.ENABLE);
      permissions.setName("test 2");
      permissions.setRemark("test");

      final MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.put("/perm/save");
      builder.header(HttpHeaders.CONTENT_TYPE, MimeTypeUtils.APPLICATION_JSON_VALUE);
      builder.header(this.requestHeadKey.getCompanyIdKey(), this.getCompanyId());
      builder.header(this.requestHeadKey.getPermIdKey(),
          String.format("[\"%s\"]", this.specialId.getSuperuserAddData()));
      builder.header(this.requestHeadKey.getUserIdKey(), this.getUserId());
      builder.content(this.getObjectMapper().writeValueAsString(permissions));

      final ResultActions resultActions = this.getMockMvc().perform(builder);
      resultActions.andExpect(MockMvcResultMatchers.status().is(200));
    } catch (final Exception e) {
      Assertions.assertTrue(false, e.getMessage());
    }
  }

  @Test
  @Order(41)
  @DisplayName("test '/perm/save' api on error 1(name is null)")
  public void saveOnError1() {
    try {
      final Permissions permissions = new Permissions();
      permissions.setStatus(Constants.ENABLE);
      permissions.setRemark("test");

      final MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.put("/perm/save");
      builder.header(HttpHeaders.CONTENT_TYPE, MimeTypeUtils.APPLICATION_JSON_VALUE);
      builder.header(this.requestHeadKey.getCompanyIdKey(), this.getCompanyId());
      builder.header(this.requestHeadKey.getPermIdKey(),
          String.format("[\"%s\"]", this.specialId.getSuperuserAddData()));
      builder.header(this.requestHeadKey.getUserIdKey(), this.getUserId());
      builder.content(this.getObjectMapper().writeValueAsString(permissions));

      final ResultActions resultActions = this.getMockMvc().perform(builder);
      resultActions.andExpect(MockMvcResultMatchers.status().is(500));
    } catch (final Exception e) {
      Assertions.assertTrue(false, e.getMessage());
    }
  }

  @Test
  @Order(41)
  @DisplayName("test '/perm/save' api on error 2(status is incorrect)")
  public void saveOnError2() {
    try {
      final Permissions permissions = new Permissions();
      permissions.setName("test 3");
      permissions.setRemark("test");
      permissions.setStatus("");

      final MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.put("/perm/save");
      builder.header(HttpHeaders.CONTENT_TYPE, MimeTypeUtils.APPLICATION_JSON_VALUE);
      builder.header(this.requestHeadKey.getCompanyIdKey(), this.getCompanyId());
      builder.header(this.requestHeadKey.getPermIdKey(),
          String.format("[\"%s\"]", this.specialId.getSuperuserAddData()));
      builder.header(this.requestHeadKey.getUserIdKey(), this.getUserId());
      builder.content(this.getObjectMapper().writeValueAsString(permissions));

      final ResultActions resultActions = this.getMockMvc().perform(builder);
      resultActions.andExpect(MockMvcResultMatchers.status().is(500));
    } catch (final Exception e) {
      Assertions.assertTrue(false, e.getMessage());
    }
  }

  @Test
  @Order(30)
  @DisplayName("test '/perm/search' api")
  public void search() {
    try {
      final SearchPermissions searchPermissions = new SearchPermissions();
      searchPermissions.setPageSize(5);
      searchPermissions.setPageNumber(1);
      searchPermissions.setPageable(false);
      final MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.post("/perm/search");
      builder.header(HttpHeaders.CONTENT_TYPE, MimeTypeUtils.APPLICATION_JSON_VALUE);
      builder.content(this.getObjectMapper().writeValueAsString(searchPermissions));

      final ResultActions resultActions = this.getMockMvc().perform(builder);
      resultActions.andExpect(MockMvcResultMatchers.status().is(200));
    } catch (final Exception e) {
      Assertions.assertTrue(false, e.getMessage());
    }
  }

}
