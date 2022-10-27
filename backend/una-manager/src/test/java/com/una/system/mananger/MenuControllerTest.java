package com.una.system.mananger;

import com.una.common.Constants;
import com.una.system.manager.model.Menu;
import com.una.system.manager.pojo.RequestHeadKey;
import com.una.system.manager.pojo.SpecialId;
import com.una.system.manager.pojo.req.SearchMenu;
import java.util.HashSet;
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
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.MimeTypeUtils;

public class MenuControllerTest extends ManagerApplicationTest {
  @Autowired
  private SpecialId specialId;
  @Autowired
  private RequestHeadKey requestHeadKey;

  @Test
  @Order(40)
  @DisplayName("test '/menu/exist' api")
  public void exist() {
    try {
      final MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.post("/menu/exist");
      builder.header(HttpHeaders.CONTENT_TYPE, Constants.MEDIA_TYPE_X_WWW_FORM_URLENCODED);
      builder.header(this.requestHeadKey.getCompanyIdKey(), this.getCompanyId());
      builder.header(this.requestHeadKey.getUserIdKey(), this.getUserId());
      builder.content("uri=/system-manage");

      final ResultActions resultActions = this.getMockMvc().perform(builder);
      resultActions.andExpect(MockMvcResultMatchers.status().is(200));
      resultActions.andExpect(MockMvcResultMatchers.content().bytes("true".getBytes()));
    } catch (final Exception e) {
      Assertions.assertTrue(false, e.getMessage());
    }
  }

  @Test
  @Order(20)
  @DisplayName("test '/menu/current' api")
  public void findByRole() {
    try {
      final Set<String> roleIds = new HashSet<>();
      roleIds.add("global-base");
      final MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.get("/menu/current");
      final String permIds = """
          ["02e304d2-11cc-4261-845c-11a62e90dab3",
          "0794436b-c87c-4923-bd78-fafae99c39c5",
          "968cbb84-d944-4054-ae72-6da55d2e604d",
          "5ab7116c-4c48-4af2-ade2-305f535984c1",
          "2fda023d-7633-441b-a192-551cd53cd29d",
          "37fb0c94-b4ce-4612-b58f-3b01e1f1323e",
          "d525478c-0620-4ae3-a4a9-874c3aea0a94",
          "b3f9a279-4393-4f67-853f-74ae774d55d9",
          "b589761c-16f0-4198-902f-c8ae9e33f328"]
          """;
      builder.header(this.requestHeadKey.getPermIdKey(), permIds);
      builder.content(this.getObjectMapper().writeValueAsString(roleIds));

      final ResultActions resultActions = this.getMockMvc().perform(builder);
      resultActions.andExpect(MockMvcResultMatchers.status().is(200));
    } catch (final Exception e) {
      Assertions.assertTrue(false, e.getMessage());
    }
  }

  @Test
  @Order(50)
  @DisplayName("test '/menu/save' api")
  @Transactional
  @Rollback
  public void save() {
    try {
      final Menu menu = new Menu();
      menu.setName("test 1");
      menu.setRemark("test");
      menu.setFrontEndUri("/test1");
      menu.setSort(100);
      menu.setStatus(Constants.ENABLE);

      final MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.put("/menu/save");
      builder.header(HttpHeaders.CONTENT_TYPE, MimeTypeUtils.APPLICATION_JSON_VALUE);
      builder.header(this.requestHeadKey.getCompanyIdKey(), this.getCompanyId());
      builder.header(this.requestHeadKey.getPermIdKey(),
          String.format("[\"%s\"]", this.specialId.getSuperuserAddData()));
      builder.header(this.requestHeadKey.getUserIdKey(), this.getUserId());
      builder.content(this.getObjectMapper().writeValueAsString(menu));

      final ResultActions resultActions = this.getMockMvc().perform(builder);
      resultActions.andDo(MockMvcResultHandlers.print());
      resultActions.andExpect(MockMvcResultMatchers.status().is(200));
    } catch (final Exception e) {
      Assertions.assertTrue(false, e.getMessage());
    }
  }

  @Test
  @Order(50)
  @DisplayName("test '/menu/save' api for update")
  @Transactional
  @Rollback
  public void saveForUpdate() {
    try {
      final Menu menu = new Menu();
      menu.setId(this.getConfigMenuId());
      menu.setName("test 2");
      menu.setRemark("test");
      menu.setFrontEndUri("/test2");
      menu.setSort(100);
      menu.setStatus(Constants.ENABLE);

      final MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.put("/menu/save");
      builder.header(HttpHeaders.CONTENT_TYPE, MimeTypeUtils.APPLICATION_JSON_VALUE);
      builder.header(this.requestHeadKey.getCompanyIdKey(), this.getCompanyId());
      builder.header(this.requestHeadKey.getPermIdKey(),
          String.format("[\"%s\"]", this.specialId.getSuperuserAddData()));
      builder.header(this.requestHeadKey.getUserIdKey(), this.getUserId());
      builder.content(this.getObjectMapper().writeValueAsString(menu));

      final ResultActions resultActions = this.getMockMvc().perform(builder);
      resultActions.andExpect(MockMvcResultMatchers.status().is(200));
    } catch (final Exception e) {
      Assertions.assertTrue(false, e.getMessage());
    }
  }

  @Test
  @Order(51)
  @DisplayName("test '/menu/save' api on error 1(name is null)")
  public void saveOnError1() {
    try {
      final Menu menu = new Menu();
      menu.setRemark("test");
      menu.setFrontEndUri("/test");
      menu.setSort(100);
      menu.setStatus(Constants.ENABLE);

      final MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.put("/menu/save");
      builder.header(HttpHeaders.CONTENT_TYPE, MimeTypeUtils.APPLICATION_JSON_VALUE);
      builder.header(this.requestHeadKey.getCompanyIdKey(), this.getCompanyId());
      builder.header(this.requestHeadKey.getPermIdKey(),
          String.format("[\"%s\"]", this.specialId.getSuperuserAddData()));
      builder.header(this.requestHeadKey.getUserIdKey(), this.getUserId());
      builder.content(this.getObjectMapper().writeValueAsString(menu));

      final ResultActions resultActions = this.getMockMvc().perform(builder);
      resultActions.andExpect(MockMvcResultMatchers.status().is(500));
    } catch (final Exception e) {
      Assertions.assertTrue(false, e.getMessage());
    }
  }

  @Test
  @Order(51)
  @DisplayName("test '/menu/save' api on error 2(front end uri is null)")
  public void saveOnError2() {
    try {
      final Menu menu = new Menu();
      menu.setName("test 1");
      menu.setRemark("test");
      menu.setSort(100);
      menu.setStatus(Constants.ENABLE);

      final MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.put("/menu/save");
      builder.header(HttpHeaders.CONTENT_TYPE, MimeTypeUtils.APPLICATION_JSON_VALUE);
      builder.header(this.requestHeadKey.getCompanyIdKey(), this.getCompanyId());
      builder.header(this.requestHeadKey.getPermIdKey(),
          String.format("[\"%s\"]", this.specialId.getSuperuserAddData()));
      builder.header(this.requestHeadKey.getUserIdKey(), this.getUserId());
      builder.content(this.getObjectMapper().writeValueAsString(menu));

      final ResultActions resultActions = this.getMockMvc().perform(builder);
      resultActions.andExpect(MockMvcResultMatchers.status().is(500));
    } catch (final Exception e) {
      Assertions.assertTrue(false, e.getMessage());
    }
  }

  @Test
  @Order(51)
  @DisplayName("test '/menu/save' api on error 3(status is incorrect)")
  public void saveOnError3() {
    try {
      final Menu menu = new Menu();
      menu.setName("test 1");
      menu.setRemark("test");
      menu.setFrontEndUri("/test");
      menu.setSort(100);
      menu.setStatus("");

      final MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.put("/menu/save");
      builder.header(HttpHeaders.CONTENT_TYPE, MimeTypeUtils.APPLICATION_JSON_VALUE);
      builder.header(this.requestHeadKey.getCompanyIdKey(), this.getCompanyId());
      builder.header(this.requestHeadKey.getPermIdKey(),
          String.format("[\"%s\"]", this.specialId.getSuperuserAddData()));
      builder.header(this.requestHeadKey.getUserIdKey(), this.getUserId());
      builder.content(this.getObjectMapper().writeValueAsString(menu));

      final ResultActions resultActions = this.getMockMvc().perform(builder);
      resultActions.andExpect(MockMvcResultMatchers.status().is(500));
    } catch (final Exception e) {
      Assertions.assertTrue(false, e.getMessage());
    }
  }

  @Test
  @Order(51)
  @DisplayName("test '/menu/save' api on error 4(not exist permissions)")
  public void saveOnError4() {
    try {
      final Menu menu = new Menu();
      menu.setName("test 1");
      menu.setRemark("test");
      menu.setFrontEndUri("/test");
      menu.setSort(100);
      menu.setStatus(Constants.ENABLE);
      menu.setPermissionsId("abc");

      final MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.put("/menu/save");
      builder.header(HttpHeaders.CONTENT_TYPE, MimeTypeUtils.APPLICATION_JSON_VALUE);
      builder.header(this.requestHeadKey.getCompanyIdKey(), this.getCompanyId());
      builder.header(this.requestHeadKey.getPermIdKey(),
          String.format("[\"%s\"]", this.specialId.getSuperuserAddData()));
      builder.header(this.requestHeadKey.getUserIdKey(), this.getUserId());
      builder.content(this.getObjectMapper().writeValueAsString(menu));

      final ResultActions resultActions = this.getMockMvc().perform(builder);
      resultActions.andExpect(MockMvcResultMatchers.status().is(500));
    } catch (final Exception e) {
      Assertions.assertTrue(false, e.getMessage());
    }
  }

  @Test
  @Order(51)
  @DisplayName("test '/menu/save' api on error 5(not exist parent menu)")
  public void saveOnError5() {
    try {
      final Menu menu = new Menu();
      menu.setName("test 1");
      menu.setRemark("test");
      menu.setFrontEndUri("/test");
      menu.setSort(100);
      menu.setStatus(Constants.ENABLE);
      menu.setParentId("cccc");

      final MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.put("/menu/save");
      builder.header(HttpHeaders.CONTENT_TYPE, MimeTypeUtils.APPLICATION_JSON_VALUE);
      builder.header(this.requestHeadKey.getCompanyIdKey(), this.getCompanyId());
      builder.header(this.requestHeadKey.getPermIdKey(),
          String.format("[\"%s\"]", this.specialId.getSuperuserAddData()));
      builder.header(this.requestHeadKey.getUserIdKey(), this.getUserId());
      builder.content(this.getObjectMapper().writeValueAsString(menu));

      final ResultActions resultActions = this.getMockMvc().perform(builder);
      resultActions.andExpect(MockMvcResultMatchers.status().is(500));
    } catch (final Exception e) {
      Assertions.assertTrue(false, e.getMessage());
    }
  }

  @Test
  @Order(51)
  @DisplayName("test '/menu/save' api on error 6(cycle menu)")
  public void saveOnError6() {
    try {
      final Menu menu = new Menu();
      menu.setId(this.getConfigMenuId());
      menu.setName("test 1");
      menu.setRemark("test");
      menu.setFrontEndUri("/test");
      menu.setSort(100);
      menu.setStatus(Constants.ENABLE);
      menu.setParentId("8bbce648-e118-41ed-80df-458caad09e92");

      final MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.put("/menu/save");
      builder.header(HttpHeaders.CONTENT_TYPE, MimeTypeUtils.APPLICATION_JSON_VALUE);
      builder.header(this.requestHeadKey.getCompanyIdKey(), this.getCompanyId());
      builder.header(this.requestHeadKey.getPermIdKey(),
          String.format("[\"%s\"]", this.specialId.getSuperuserAddData()));
      builder.header(this.requestHeadKey.getUserIdKey(), this.getUserId());
      builder.content(this.getObjectMapper().writeValueAsString(menu));

      final ResultActions resultActions = this.getMockMvc().perform(builder);
      resultActions.andExpect(MockMvcResultMatchers.status().is(500));
    } catch (final Exception e) {
      Assertions.assertTrue(false, e.getMessage());
    }
  }

  @Test
  @Order(51)
  @DisplayName("test '/menu/save' api on error 7(uri has space character)")
  public void saveOnError7() {
    try {
      final Menu menu = new Menu();
      menu.setId(this.getConfigMenuId());
      menu.setName("test 1");
      menu.setRemark("test");
      menu.setFrontEndUri("/test a");
      menu.setSort(100);
      menu.setStatus(Constants.ENABLE);

      final MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.put("/menu/save");
      builder.header(HttpHeaders.CONTENT_TYPE, MimeTypeUtils.APPLICATION_JSON_VALUE);
      builder.header(this.requestHeadKey.getCompanyIdKey(), this.getCompanyId());
      builder.header(this.requestHeadKey.getPermIdKey(),
          String.format("[\"%s\"]", this.specialId.getSuperuserAddData()));
      builder.header(this.requestHeadKey.getUserIdKey(), this.getUserId());
      builder.content(this.getObjectMapper().writeValueAsString(menu));

      final ResultActions resultActions = this.getMockMvc().perform(builder);
      resultActions.andExpect(MockMvcResultMatchers.status().is(500));
    } catch (final Exception e) {
      Assertions.assertTrue(false, e.getMessage());
    }
  }

  @Test
  @Order(30)
  @DisplayName("test '/menu/search' api")
  public void search() {
    try {
      final SearchMenu searchMenu = new SearchMenu();
      final MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.post("/menu/search");
      builder.header(HttpHeaders.CONTENT_TYPE, MimeTypeUtils.APPLICATION_JSON_VALUE);
      builder.content(this.getObjectMapper().writeValueAsString(searchMenu));

      final ResultActions resultActions = this.getMockMvc().perform(builder);
      resultActions.andExpect(MockMvcResultMatchers.status().is(200));
    } catch (final Exception e) {
      Assertions.assertTrue(false, e.getMessage());
    }
  }

}
