package com.una.system.mananger;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.util.MimeTypeUtils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.una.common.Constants;
import com.una.common.pojo.User;
import com.una.system.manager.pojo.RequestHeadKey;
import com.una.system.manager.pojo.SpecialId;
import com.una.system.manager.pojo.req.SearchUser;
import com.una.system.manager.service.UserService;

public class UserControllerTest extends ManagerApplicationTests {

  @Autowired
  private SpecialId specialId;
  @Autowired
  private RequestHeadKey requestHeadKey;

  @Autowired
  private UserService userService;

  @Test
  @Order(110)
  @DisplayName("test '/user/changePassword' api")
  public void changePassword() throws Exception {
    final String param = String.format("userId=%s&oldPassword=%s&newPassword=%s", this.getUserId(), "soli", "abcABCdef1234567890#");

    final MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.post("/user/changePassword");
    builder.header(HttpHeaders.CONTENT_TYPE, Constants.MEDIA_TYPE_X_WWW_FORM_URLENCODED);
    builder.header(this.requestHeadKey.getCompanyIdKey(), this.getCompanyId());
    builder.header(this.requestHeadKey.getUserIdKey(), this.getUserId());
    builder.header(this.requestHeadKey.getPermIdKey(), String.format("[\"%s\"]", this.specialId.getSuperuserAddData()));
    builder.content(param);

    final ResultActions resultActions = this.getMockMvc().perform(builder);
    resultActions.andExpect(MockMvcResultMatchers.status().is(200));
    //    resultActions.andDo(MockMvcResultHandlers.print());
    final MvcResult mvcResult = resultActions.andReturn();
    final TypeReference<Map<String, Object>> typeReference = new TypeReference<>() {
    };
    final Map<String, Object> map = this.getObjectMapper().readValue(mvcResult.getResponse().getContentAsString(), typeReference);
    if (!Constants.RESP_SUCCESS_CODE.equals(map.get("code"))) {
      throw new RuntimeException("response code not " + Constants.RESP_SUCCESS_CODE + ", result code is: " + map.get("code"));
    }
  }

  @Test
  @Order(60)
  @DisplayName("test '/user/exist' api")
  public void exist() throws Exception {

    final MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.post("/user/exist");
    builder.header(HttpHeaders.CONTENT_TYPE, Constants.MEDIA_TYPE_X_WWW_FORM_URLENCODED);
    builder.header(this.requestHeadKey.getCompanyIdKey(), this.getCompanyId());
    builder.header(this.requestHeadKey.getUserIdKey(), this.getUserId());
    builder.content("username=00001");

    final ResultActions resultActions = this.getMockMvc().perform(builder);
    resultActions.andExpect(MockMvcResultMatchers.status().is(200));
    resultActions.andExpect(MockMvcResultMatchers.content().bytes("true".getBytes()));
  }

  @Test
  @Order(30)
  @DisplayName("test '/user/fill' api")
  // @Disabled
  public void fill() throws Exception {
    final User user = this.findByName("00001");

    final MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.post("/user/fill");
    builder.header(HttpHeaders.CONTENT_TYPE, MimeTypeUtils.APPLICATION_JSON_VALUE);
    builder.content(this.getObjectMapper().writeValueAsString(user));

    final ResultActions resultActions = this.getMockMvc().perform(builder);
    resultActions.andExpect(MockMvcResultMatchers.status().is(200));
  }

  @Test
  @Order(20)
  // @Disabled
  @DisplayName("test '/user/find' api")
  public void find() throws Exception {
    this.findByName("00001");
  }

  private User findByName(final String name) throws Exception {
    final MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.post("/user/find");
    builder.header(HttpHeaders.CONTENT_TYPE, Constants.MEDIA_TYPE_X_WWW_FORM_URLENCODED);
    builder.param("name", name);

    final ResultActions resultActions = this.getMockMvc().perform(builder);
    resultActions.andExpect(MockMvcResultMatchers.status().is(200));

    final MockHttpServletResponse response = resultActions.andReturn().getResponse();
    return this.getObjectMapper().readValue(response.getContentAsString(), User.class);
  }

  @Test
  @Order(80)
  @DisplayName("test '/user/findByGroup' api")
  public void findByPerm() throws Exception {
    final List<String> groupIdList = List.of(this.getGroupId());

    final MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.post("/user/findByGroup");
    builder.header(HttpHeaders.CONTENT_TYPE, MimeTypeUtils.APPLICATION_JSON_VALUE);
    builder.header(this.requestHeadKey.getCompanyIdKey(), this.getCompanyId());
    builder.header(this.requestHeadKey.getUserIdKey(), this.getUserId());
    builder.content(this.getObjectMapper().writeValueAsString(groupIdList));

    final ResultActions resultActions = this.getMockMvc().perform(builder);

    resultActions.andExpect(MockMvcResultMatchers.status().is(200));
  }

  @Test
  @Order(90)
  @DisplayName("test '/user/findByRole' api")
  public void findByRole() throws Exception {
    final List<String> roleIdList = List.of(this.getRoleId());

    final MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.post("/user/findByRole");
    builder.header(HttpHeaders.CONTENT_TYPE, MimeTypeUtils.APPLICATION_JSON_VALUE);
    builder.header(this.requestHeadKey.getCompanyIdKey(), this.getCompanyId());
    builder.header(this.requestHeadKey.getUserIdKey(), this.getUserId());
    builder.content(this.getObjectMapper().writeValueAsString(roleIdList));

    final ResultActions resultActions = this.getMockMvc().perform(builder);

    resultActions.andExpect(MockMvcResultMatchers.status().is(200));
  }

  @Test
  @Order(140)
  @DisplayName("test '/user/resetCredentials' api")
  public void resetCredentials() throws Exception {
    this.initDB();
    final com.una.system.manager.model.User user = this.userService.findById(this.getUserId()).get();
    user.setCredentialsNonExpired(false);
    this.userService.updateUser(user);

    final String param = String.format("username=%s&oldPassword=%s&newPassword=%s", "00001", "soli", "abcABCdef1234567890#");

    final MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.post("/user/resetCredentials");
    builder.header(HttpHeaders.CONTENT_TYPE, Constants.MEDIA_TYPE_X_WWW_FORM_URLENCODED);
    builder.content(param);

    final ResultActions resultActions = this.getMockMvc().perform(builder);
    resultActions.andDo(MockMvcResultHandlers.print());
    resultActions.andExpect(MockMvcResultMatchers.status().is(200));
  }

  @Test
  @Order(120)
  @DisplayName("test '/user/resetPassword' api")
  public void resetPassword() throws Exception {
    final User user = new User();
    user.setId(this.getUserId());
    user.setCompanyId(this.getCompanyId());

    final MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.post("/user/resetPassword");
    builder.header(HttpHeaders.CONTENT_TYPE, MimeTypeUtils.APPLICATION_JSON_VALUE);
    builder.header(this.requestHeadKey.getCompanyIdKey(), this.getCompanyId());
    builder.header(this.requestHeadKey.getUserIdKey(), this.getUserId());
    builder.header(this.requestHeadKey.getPermIdKey(), String.format("[\"%s\"]", this.specialId.getSuperuserAddData()));
    builder.content(this.getObjectMapper().writeValueAsString(user));

    final ResultActions resultActions = this.getMockMvc().perform(builder);
    //    resultActions.andDo(MockMvcResultHandlers.print());
    resultActions.andExpect(MockMvcResultMatchers.status().is(200));
  }

  @Test
  @Order(70)
  @DisplayName("test '/user/save' api")
  public void save() throws Exception {
    final User user = new User();
    user.setCompanyId(this.getCompanyId());
    user.setAccountNonExpired(false);
    user.setAccountNonLocked(true);
    user.setCredentialsNonExpired(true);
    user.setUsername("00002");

    //    MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.multipart("/user/save").file(null, null).param("user", this.getObjectMapper().writeValueAsString(user));
    final MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.multipart("/user/save").param("user", this.getObjectMapper().writeValueAsString(user));
    builder.header(HttpHeaders.CONTENT_TYPE, Constants.MEDIA_TYPE_MULTIPART_FORM_DATA);
    builder.header(this.requestHeadKey.getCompanyIdKey(), this.getCompanyId());
    builder.header(this.requestHeadKey.getUserIdKey(), this.getUserId());
    builder.header(this.requestHeadKey.getPermIdKey(), String.format("[\"%s\"]", this.specialId.getSuperuserAddData()));

    final ResultActions resultActions = this.getMockMvc().perform(builder);
    resultActions.andExpect(MockMvcResultMatchers.status().is(200));
  }

  @Test
  @Order(70)
  @DisplayName("test '/user/save' api for company")
  public void saveForCompany() throws Exception {
    final User user = new User();
    user.setCompanyId(this.getCompanyId());
    user.setAccountNonExpired(false);
    user.setUsername("00003");

    //    MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.multipart("/user/save").file(null, null).param("user", this.getObjectMapper().writeValueAsString(user));
    final MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.multipart("/user/save").param("user", this.getObjectMapper().writeValueAsString(user));
    builder.header(HttpHeaders.CONTENT_TYPE, Constants.MEDIA_TYPE_MULTIPART_FORM_DATA);
    builder.header(this.requestHeadKey.getCompanyIdKey(), this.getCompanyId());
    builder.header(this.requestHeadKey.getUserIdKey(), this.getUserId());
    builder.header(this.requestHeadKey.getPermIdKey(), String.format("[\"%s\"]", this.specialId.getSuperuserAddData()));

    final ResultActions resultActions = this.getMockMvc().perform(builder);
    resultActions.andExpect(MockMvcResultMatchers.status().is(200));
  }

  @Test
  @Order(70)
  @DisplayName("test '/user/save' api for update")
  public void saveForUpdate() throws Exception {
    final com.una.system.manager.model.User user = this.userService.findById(this.getUserId()).get();
    this.userService.fillUser(user);
    user.setId(this.getUserId());
    user.setCompanyId(this.getCompanyId());
    user.setAccountNonExpired(false);
    user.setUsername("00001");

    //    MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.multipart("/user/save").file(null, null).param("user", this.getObjectMapper().writeValueAsString(user));
    final MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.multipart("/user/save").param("user", this.getObjectMapper().writeValueAsString(user));
    builder.header(HttpHeaders.CONTENT_TYPE, Constants.MEDIA_TYPE_MULTIPART_FORM_DATA);
    builder.header(this.requestHeadKey.getCompanyIdKey(), this.getCompanyId());
    builder.header(this.requestHeadKey.getUserIdKey(), this.getUserId());

    final ResultActions resultActions = this.getMockMvc().perform(builder);
    resultActions.andExpect(MockMvcResultMatchers.status().is(200));
  }

  @Test
  @Order(71)
  @DisplayName("test '/user/save' api on error 1(username is null)")
  public void saveOnError1() throws Exception {
    final User user = new User();
    user.setCompanyId(this.getCompanyId());
    user.setAccountNonExpired(false);
    user.setAccountNonLocked(true);
    user.setCredentialsNonExpired(true);

    final MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.put("/user/save");
    builder.header(HttpHeaders.CONTENT_TYPE, MimeTypeUtils.APPLICATION_JSON_VALUE);
    builder.header(this.requestHeadKey.getCompanyIdKey(), this.getCompanyId());
    builder.header(this.requestHeadKey.getUserIdKey(), this.getUserId());
    builder.content(this.getObjectMapper().writeValueAsString(user));

    final ResultActions resultActions = this.getMockMvc().perform(builder);
    resultActions.andExpect(MockMvcResultMatchers.status().is(500));
  }

  @Test
  @Order(71)
  @DisplayName("test '/user/save' api on error 2(company is null)")
  public void saveOnError2() throws Exception {
    final User user = new User();
    user.setUsername("00004");
    user.setAccountNonExpired(false);
    user.setAccountNonLocked(true);
    user.setCredentialsNonExpired(true);

    final MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.put("/user/save");
    builder.header(HttpHeaders.CONTENT_TYPE, MimeTypeUtils.APPLICATION_JSON_VALUE);
    builder.header(this.requestHeadKey.getCompanyIdKey(), this.getCompanyId());
    builder.header(this.requestHeadKey.getUserIdKey(), this.getUserId());
    builder.header(this.requestHeadKey.getPermIdKey(), String.format("[\"%s\"]", this.specialId.getSuperuserAddData()));
    builder.content(this.getObjectMapper().writeValueAsString(user));

    final ResultActions resultActions = this.getMockMvc().perform(builder);
    resultActions.andExpect(MockMvcResultMatchers.status().is(500));
  }

  @Test
  @Order(71)
  @DisplayName("test '/user/save' api on error 3(duplicate name)")
  public void saveOnError3() throws Exception {
    final User user = new User();
    user.setUsername("00001");
    user.setCompanyId(this.getCompanyId());
    user.setAccountNonExpired(false);
    user.setAccountNonLocked(true);
    user.setCredentialsNonExpired(true);

    final MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.put("/user/save");
    builder.header(HttpHeaders.CONTENT_TYPE, MimeTypeUtils.APPLICATION_JSON_VALUE);
    builder.header(this.requestHeadKey.getCompanyIdKey(), this.getCompanyId());
    builder.header(this.requestHeadKey.getUserIdKey(), this.getUserId());
    builder.content(this.getObjectMapper().writeValueAsString(user));

    final ResultActions resultActions = this.getMockMvc().perform(builder);
    resultActions.andExpect(MockMvcResultMatchers.status().is(500));
  }

  @Test
  @Order(71)
  @DisplayName("test '/user/save' api on error 4(default group not exist)")
  public void saveOnError4() throws Exception {
    final User user = new User();
    user.setUsername("00005");
    user.setCompanyId(this.getCompanyId());
    user.setAccountNonExpired(false);
    user.setAccountNonLocked(true);
    user.setCredentialsNonExpired(true);
    user.setDefaultGroupId("abc");

    final MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.put("/user/save");
    builder.header(HttpHeaders.CONTENT_TYPE, MimeTypeUtils.APPLICATION_JSON_VALUE);
    builder.header(this.requestHeadKey.getCompanyIdKey(), this.getCompanyId());
    builder.header(this.requestHeadKey.getUserIdKey(), this.getUserId());
    builder.content(this.getObjectMapper().writeValueAsString(user));

    final ResultActions resultActions = this.getMockMvc().perform(builder);
    resultActions.andExpect(MockMvcResultMatchers.status().is(500));
  }

  @Test
  @Order(40)
  // @Disabled
  @DisplayName("test '/user/search' api")
  public void search() throws Exception {
    final SearchUser searchUser = new SearchUser();

    final MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.post("/user/search");
    builder.header(HttpHeaders.CONTENT_TYPE, MimeTypeUtils.APPLICATION_JSON_VALUE);
    builder.header(this.requestHeadKey.getCompanyIdKey(), this.getCompanyId());
    builder.header(this.requestHeadKey.getPermIdKey(), String.format("[\"%s\"]", this.specialId.getNotIncludeCompanyId()));
    builder.content(this.getObjectMapper().writeValueAsString(searchUser));

    final ResultActions resultActions = this.getMockMvc().perform(builder);
    resultActions.andExpect(MockMvcResultMatchers.status().is(200));
  }

  @Test
  @Order(130)
  @DisplayName("test '/user/update' api")
  public void update() throws Exception {
    final com.una.system.manager.model.User user = this.userService.findById(this.getUserId()).get();
    user.setAccountNonExpired(false);
    user.setAccountNonLocked(false);
    user.setCredentialsNonExpired(false);

    //    MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.multipart("/user/save").file(null, null).param("user", this.getObjectMapper().writeValueAsString(user));
    final MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.multipart("/user/save").param("user", this.getObjectMapper().writeValueAsString(user));
    builder.header(HttpHeaders.CONTENT_TYPE, Constants.MEDIA_TYPE_MULTIPART_FORM_DATA);
    builder.header(this.requestHeadKey.getCompanyIdKey(), this.getCompanyId());
    builder.header(this.requestHeadKey.getUserIdKey(), this.getUserId());
    builder.header(this.requestHeadKey.getPermIdKey(), String.format("[\"%s\"]", this.specialId.getSuperuserAddData()));

    final ResultActions resultActions = this.getMockMvc().perform(builder);
    resultActions.andExpect(MockMvcResultMatchers.status().is(200));
  }

  @Test
  @Order(50)
  // @Disabled
  @DisplayName("test '/user/updateForLogin' api")
  public void updateForLogin() throws Exception {
    final String name = "00001";
    final int count = 10;

    final User user = this.findByName(name);
    user.setFailureCount(count);

    final MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.post("/user/updateForLogin");
    builder.header(HttpHeaders.CONTENT_TYPE, MimeTypeUtils.APPLICATION_JSON_VALUE);
    builder.content(this.getObjectMapper().writeValueAsString(user));

    final ResultActions resultActions = this.getMockMvc().perform(builder);
    resultActions.andExpect(MockMvcResultMatchers.status().is(200));

    if (this.findByName(name).getFailureCount() != count) {
      throw new RuntimeException("cannot update user");
    }
  }

  @Test
  @Order(100)
  @DisplayName("test '/user/updateRelation' api")
  public void updateRelation() throws Exception {
    final User user = new User();
    user.setId(this.getUserId());

    final MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.post("/user/updateRelation");
    builder.header(HttpHeaders.CONTENT_TYPE, MimeTypeUtils.APPLICATION_JSON_VALUE);
    builder.header(this.requestHeadKey.getCompanyIdKey(), this.getCompanyId());
    builder.header(this.requestHeadKey.getUserIdKey(), this.getUserId());
    builder.header(this.requestHeadKey.getPermIdKey(), String.format("[\"%s\"]", this.specialId.getSuperuserAddData()));
    builder.content(this.getObjectMapper().writeValueAsString(user));

    final ResultActions resultActions = this.getMockMvc().perform(builder);
    //    resultActions.andDo(MockMvcResultHandlers.print());
    resultActions.andExpect(MockMvcResultMatchers.status().is(200));
  }
}
