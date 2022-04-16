package com.una.system.manager.controller;

import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpStatus;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Controller;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.util.MimeTypeUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.una.common.Constants;
import com.una.common.pojo.Group;
import com.una.common.pojo.PaginationInfo;
import com.una.common.pojo.ResponseVO;
import com.una.common.pojo.UserSessionList;
import com.una.common.utils.BeanPropUtil;
import com.una.common.utils.LogUtil;
import com.una.system.manager.microservices.NotificationService;
import com.una.system.manager.microservices.SharingService;
import com.una.system.manager.model.Configuration;
import com.una.system.manager.model.User;
import com.una.system.manager.model.UserProfile;
import com.una.system.manager.pojo.RequestHeadKey;
import com.una.system.manager.pojo.SpecialId;
import com.una.system.manager.pojo.TransmissionInfo;
import com.una.system.manager.pojo.req.SearchUser;
import com.una.system.manager.service.PasswordService;
import com.una.system.manager.service.UserService;
import com.una.system.manager.utils.RequestUtils;
import com.una.system.manager.utils.TransmissionUtil;
import com.una.system.manager.utils.TransmissionUtil.Transmission;

@Controller
@RequestMapping("user")
@RefreshScope
public class UserController {
  private static final Logger LOGGER = LogUtil.getRunLogger();

  @Value("${spring.profiles.active}")
  private String active;
  @Value("${app.security.auth-header:x-auth-token}")
  private String authHeaderName;

  @Value("${spring.http.encoding.charset}")
  private String charset;
  @Autowired
  private UserService userService;
  @Autowired
  private PasswordService passwordService;
  @Autowired
  @Qualifier("avatarServer")
  private TransmissionInfo transmissionInfo;
  @Autowired
  private NotificationService notificationService;
  @Autowired
  private SharingService sharingService;
  @Autowired
  private ObjectMapper objectMapper;
  @Autowired
  private RequestHeadKey requestHeadKey;

  @Autowired
  private SpecialId specialId;

  @RequestMapping(value = "changeGroup", method = RequestMethod.POST, consumes = Constants.MEDIA_TYPE_X_WWW_FORM_URLENCODED)
  @ResponseBody
  public void changeGroup(final HttpServletRequest request, final String groupId) {
    final User user = this.current(request);
    final String userId = request.getHeader(this.requestHeadKey.getUserIdKey());
    if (!StringUtils.equals(user.getId(), userId)) {
      throw new RuntimeException("this api only using for user self");
    }
    this.setUserCurrentRoleId(user, groupId);

    final String token = request.getHeader(this.authHeaderName);
    this.sharingService.updateUser(token, user);
  }

  @RequestMapping(value = "changePassword", method = RequestMethod.POST, consumes = Constants.MEDIA_TYPE_X_WWW_FORM_URLENCODED)
  @ResponseBody
  public ResponseVO<String> changePassword(final HttpServletRequest request, final HttpServletResponse response, final String userId, final String oldPassword,
    final String newPassword) throws Exception {
    final String loginUserId = request.getHeader(this.requestHeadKey.getUserIdKey());
    if (!StringUtils.equals(loginUserId, userId)) {
      UserController.LOGGER.error("user not has permissions, user id: {}", userId);
      response.setStatus(HttpStatus.SC_NOT_FOUND);
      throw new RuntimeException("Not Found");
    }

    final User dbUser = this.userService.findById(userId).get();
    return this.changePassword(dbUser, oldPassword, newPassword, false);
  }

  private ResponseVO<String> changePassword(final User user, final String oldPassword, final String newPassword, final boolean updateUser) {
    final ResponseVO<String> vo = new ResponseVO<>();
    vo.setCode(Constants.RESP_SUCCESS_CODE);

    if (StringUtils.equals(oldPassword, newPassword)) {
      vo.setCode(com.una.system.manager.Constants.PASSWORD_SAME);
      vo.setMsg("old and new passwords are the same");
      return vo;
    }

    final String encodedPassword = this.passwordService.getPasswordByUsername(user.getUsername());
    if (!this.passwordService.match(oldPassword, encodedPassword)) {
      vo.setCode(com.una.system.manager.Constants.PASSWORD_EXCEPTION);
      vo.setMsg("password not match current password");
      return vo;
    }

    final String result = this.passwordService.matchPasswordStrength(user.getUsername(), newPassword, user.getCompanyId());
    if (StringUtils.isNotBlank(result)) {
      vo.setCode(com.una.system.manager.Constants.PASSWORD_STRENGTH_EXCEPTION);
      vo.setMsg(result);
      return vo;
    }

    this.userService.updatePassword(user, newPassword, updateUser);
    return vo;
  }

  @RequestMapping(value = "current", method = RequestMethod.GET)
  @ResponseBody
  public User current(final HttpServletRequest request) {
    final String token = request.getHeader(this.authHeaderName);
    if (StringUtils.isBlank(token)) {
      throw new RuntimeException("not found token");
    }
    final ResponseVO<User> resp = this.sharingService.getUser(token, false);
    if (StringUtils.equals(Constants.RESP_SUCCESS_CODE, resp.getCode())) {
      return resp.getData();
    }
    throw new RuntimeException(String.format("code: {}, msg: {}", resp.getCode(), resp.getMsg()));
  }

  @RequestMapping(value = "currentAvatar", method = RequestMethod.GET, consumes = MimeTypeUtils.TEXT_PLAIN_VALUE)
  @ResponseBody
  public void currentAvatar(final HttpServletRequest request, final HttpServletResponse response) throws Exception {
    final String userId = request.getHeader(this.requestHeadKey.getUserIdKey());
    final Optional<UserProfile> userProfileOptional = this.userService.findProfileByUserId(userId);

    final String avatar = userProfileOptional.map(UserProfile::getAvatar).orElse("");
    if (StringUtils.isBlank(avatar)) {
      return;
    }
    final long size = this.downlodAvatar(RequestUtils.getCompanyId(request, this.requestHeadKey), avatar, response.getOutputStream());
    final String avatarType = userProfileOptional.map(UserProfile::getAvatarType).orElse("");
    this.setResponseHeadForAvatar(response, size, avatar, avatarType);
  }

  private long downlodAvatar(final String companyId, final String filename, final OutputStream outputStream) throws Exception {
    final TransmissionInfo dInfo = this.transmissionInfo;
    final String storeType = this.transmissionInfo.getStoreType();
    final String serverUsername = this.transmissionInfo.getUsername();
    final String password = this.transmissionInfo.getPassword();
    final String ip = this.transmissionInfo.getIp();
    final String folder = this.transmissionInfo.getFolder();
    final int port = Optional.ofNullable(this.transmissionInfo.getPort()).orElse(0);

    final List<Configuration> cList = Optional.ofNullable(this.sharingService.getConfigurationList(companyId)).orElse(List.of());
    final String cStoreType = BeanPropUtil.getValueForKey(cList, dInfo.getStoreTypeKey(), storeType, v -> StringUtils.defaultIfBlank(v, storeType));
    final String cUsername = BeanPropUtil.getValueForKey(cList, dInfo.getUsernameKey(), serverUsername, v -> StringUtils.defaultIfBlank(v, serverUsername));
    final String cPassword = BeanPropUtil.getValueForKey(cList, dInfo.getPasswordKey(), password, v -> StringUtils.defaultIfBlank(v, password));
    final String cIp = BeanPropUtil.getValueForKey(cList, dInfo.getIpKey(), ip, v -> StringUtils.defaultIfBlank(v, ip));
    String cFolder = BeanPropUtil.getValueForKey(cList, dInfo.getFolderKey(), folder, v -> StringUtils.defaultIfBlank(v, folder));
    final int cPort = BeanPropUtil.getValueForKey(cList, dInfo.getPortKey(), port, v -> Integer.parseInt(StringUtils.defaultString(v, "0")));

    if (StringUtils.equals("file", cStoreType)) {
      cFolder = this.getAvatarFolder1(cFolder);
    }

    try (Transmission instance = TransmissionUtil.Type.getInstance(cStoreType);) {
      instance.login(cIp, cPort, cUsername, cPassword);
      return instance.downloadFile(cFolder, filename, outputStream);
    }
    catch (final Exception e) {
      UserController.LOGGER.error("download avatar exception", e);
      return -1;
    }
  }

  @RequestMapping(value = "exist", method = RequestMethod.POST, consumes = Constants.MEDIA_TYPE_X_WWW_FORM_URLENCODED)
  @ResponseBody
  public boolean exist(final HttpServletRequest request, final String username) throws Exception {
    Assert.hasText(username, "parameter 'username' must not be empty");
    final SearchUser searchUser = new SearchUser();
    searchUser.setEqName(username);

    final PaginationInfo<User> paginationInfo = this.search(request, searchUser);
    return !paginationInfo.isEmpty();
  }

  @RequestMapping(value = "fill", method = RequestMethod.POST, consumes = MimeTypeUtils.APPLICATION_JSON_VALUE)
  @ResponseBody
  public User fill(@RequestBody final User user) {
    this.userService.fillUser(user);

    this.setUserCurrentRoleId(user, user.getDefaultGroupId());
    return user;
  }

  @RequestMapping(value = "find", method = RequestMethod.POST, consumes = Constants.MEDIA_TYPE_X_WWW_FORM_URLENCODED)
  @ResponseBody
  public User find(final String name) {
    final Optional<User> optional = this.userService.findByName(name);
    return optional.orElse(null);
  }

  @RequestMapping(value = "findByGroup", method = RequestMethod.POST, consumes = MimeTypeUtils.APPLICATION_JSON_VALUE)
  @ResponseBody
  public Set<User> findByGroup(final HttpServletRequest request, @RequestBody final List<String> groups) throws Exception {
    String companyId = RequestUtils.getCompanyId(request, this.requestHeadKey);
    final boolean allowNotCompanyCond = RequestUtils.hasSpecId(request.getHeader(this.requestHeadKey.getPermIdKey()), this.objectMapper,
      this.specialId.getNotIncludeCompanyId());
    if (allowNotCompanyCond) {
      companyId = null;
    }
    return this.userService.findByGroup(groups, companyId);
  }

  @RequestMapping(value = "findByRole", method = RequestMethod.POST, consumes = MimeTypeUtils.APPLICATION_JSON_VALUE)
  @ResponseBody
  public Set<User> findByRole(final HttpServletRequest request, @RequestBody final List<String> roles) throws Exception {
    String companyId = RequestUtils.getCompanyId(request, this.requestHeadKey);
    final boolean allowNotCompanyCond = RequestUtils.hasSpecId(request.getHeader(this.requestHeadKey.getPermIdKey()), this.objectMapper,
      this.specialId.getNotIncludeCompanyId());
    if (allowNotCompanyCond) {
      companyId = null;
    }
    return this.userService.findByRole(roles, companyId);
  }

  @RequestMapping(value = "getAvatar", method = RequestMethod.POST, consumes = Constants.MEDIA_TYPE_X_WWW_FORM_URLENCODED)
  @ResponseBody
  public void getAvatar(final HttpServletRequest request, final HttpServletResponse response, final String userId) throws Exception {
    final String companyId = RequestUtils.getCompanyId(request, this.requestHeadKey);
    final boolean isSuperuser = RequestUtils.hasSpecId(RequestUtils.getPermIds(request, this.requestHeadKey), this.objectMapper,
      this.specialId.getSuperuserAddData());

    final User user = this.userService.findById(userId).get();
    if (!isSuperuser && !StringUtils.equals(user.getCompanyId(), companyId)) {
      UserController.LOGGER.error("user not has permissions, user id: {}", userId);
      response.setStatus(HttpStatus.SC_NOT_FOUND);
      throw new RuntimeException("Not Found");
    }
    final Optional<UserProfile> userProfileOptional = this.userService.findProfileByUserId(userId);

    final String avatar = userProfileOptional.map(UserProfile::getAvatar).orElse("");
    if (StringUtils.isBlank(avatar)) {
      return;
    }
    final long size = this.downlodAvatar(RequestUtils.getCompanyId(request, this.requestHeadKey), avatar, response.getOutputStream());
    final String avatarType = userProfileOptional.map(UserProfile::getAvatarType).orElse("");
    this.setResponseHeadForAvatar(response, size, avatar, avatarType);
  }

  private String getAvatarFolder1(String configFolder) throws FileNotFoundException {
    if (StringUtils.startsWith(configFolder, ".")) {
      configFolder = System.getProperty("user.dir") + configFolder;
    }
    return configFolder;
  }

  @RequestMapping(value = { "getOnlineUserSession/{username}", "getOnlineUserSession/{username}/{hasUser}" }, method = RequestMethod.GET)
  @ResponseBody
  public UserSessionList getOnlineUserSession(final HttpServletRequest request, final HttpServletResponse response,
    @PathVariable("username") final String username, @PathVariable(value = "hasUser", required = false) final String hasUser) throws Exception {
    final String userId = request.getHeader(this.requestHeadKey.getUserIdKey());
    final String companyId = RequestUtils.getCompanyId(request, this.requestHeadKey);
    final boolean allowNotCompanyCond = RequestUtils.hasSpecId(request.getHeader(this.requestHeadKey.getPermIdKey()), this.objectMapper,
      this.specialId.getNotIncludeCompanyId());

    final boolean fillUser = StringUtils.equalsIgnoreCase(hasUser, "true");
    return this.sharingService.getAndUpdateUserSessionList(username, true, fillUser, manage -> {
      final List<Boolean> flagList = new ArrayList<>();
      flagList.add(!CollectionUtils.isEmpty(manage.getSessionMap()));
      flagList.add(!allowNotCompanyCond);
      flagList.add(!StringUtils.equals(companyId, manage.getCompanyId()));
      if (BooleanUtils.and(flagList.toArray(Boolean[]::new))) {
        UserController.LOGGER.error("user not has permissions, user id: {}", userId);
        response.setStatus(HttpStatus.SC_NOT_FOUND);
        throw new RuntimeException("Not Found");
      }
      return true;
    });
  }

  @RequestMapping(value = "getUserSession", method = RequestMethod.POST, consumes = Constants.MEDIA_TYPE_X_WWW_FORM_URLENCODED)
  @ResponseBody
  public UserSessionList getUserSession(final HttpServletRequest request, final HttpServletResponse response, final String username) throws Exception {
    return this.sharingService.getAndUpdateUserSessionList(username, true, false, manage -> true);
  }

  @RequestMapping(value = "resetCredentials", method = RequestMethod.POST, consumes = Constants.MEDIA_TYPE_X_WWW_FORM_URLENCODED)
  @ResponseBody
  public ResponseVO<String> resetCredentials(final HttpServletRequest request, final HttpServletResponse response, final String username,
    final String oldPassword, final String newPassword) {
    ResponseVO<String> vo = new ResponseVO<>();
    vo.setCode(Constants.RESP_FAILURE_CODE);

    final String sessionId = request.getHeader(this.authHeaderName);
    if (!StringUtils.equals("test", this.active)) {

      final ResponseVO<List<String>> resp = this.sharingService.getShortTerm(sessionId);
      final List<String> list = Optional.ofNullable(resp.getData()).orElse(List.of());
      final List<Boolean> conditionList = new ArrayList<>();
      conditionList.add(!StringUtils.equals(Constants.RESP_SUCCESS_CODE, resp.getCode()));
      conditionList.add(!list.contains(Constants.TEMP_ACTION_EXPIRED));
      conditionList.add(!list.contains(username));

      if (BooleanUtils.or(conditionList.toArray(new Boolean[] {}))) {
        return vo;
      }
    }

    User user = this.userService.findByName(username).orElse(null);
    if (Objects.isNull(user)) {
      UserController.LOGGER.error("username not exist, username: {}", username);
      return vo;
    }

    if (user.isCredentialsNonExpired()) {
      UserController.LOGGER.error("username credentials non expired, username: {}", username);
      return vo;
    }

    user = this.fill(user);
    if (!StringUtils.equals(Constants.ENABLE, user.getCompany().getStatus())) {
      UserController.LOGGER.error("company status is not enable, username: {}", username);
      return vo;
    }

    user.setCredentialsNonExpired(true);
    user.setLastChangePWDate(LocalDateTime.now());
    vo = this.changePassword(user, oldPassword, newPassword, true);
    if (!StringUtils.equals("test", this.active)) {
      this.sharingService.removeShortTerm(sessionId);
    }
    return vo;
  }

  @RequestMapping(value = "resetPassword", method = RequestMethod.POST, consumes = MimeTypeUtils.APPLICATION_JSON_VALUE)
  @ResponseBody
  public void resetPassword(final HttpServletRequest request, final HttpServletResponse response, @RequestBody final User user) throws Exception {
    final String userId = request.getHeader(this.requestHeadKey.getUserIdKey());
    final User dbUser = this.userService.findById(user.getId()).get();
    final String companyId = RequestUtils.getCompanyId(request, this.requestHeadKey);

    final boolean isSuperuser = RequestUtils.hasSpecId(request.getHeader(this.requestHeadKey.getPermIdKey()), this.objectMapper,
      this.specialId.getSuperuserAddData());
    if (!isSuperuser && !StringUtils.equals(dbUser.getCompanyId(), companyId)) {
      UserController.LOGGER.error("user not has permissions, user id: {}", userId);
      response.setStatus(HttpStatus.SC_NOT_FOUND);
      throw new RuntimeException("Not Found");
    }

    if (!StringUtils.equals(this.active, "test")) {
      final List<Configuration> configList = this.sharingService.getConfigurationList(companyId);
      final String newPassword = this.passwordService.generate(configList);
      dbUser.setCredentialsNonExpired(false);
      dbUser.setLastChangePWDate(LocalDateTime.now());
      this.userService.updatePassword(dbUser, newPassword, true);
      final ObjectNode objectNode = this.objectMapper.convertValue(dbUser, ObjectNode.class);
      objectNode.put("password", newPassword);
      this.notificationService.send("reset password", this.objectMapper.writeValueAsString(objectNode));
    }
  }

  @RequestMapping(value = "save", method = RequestMethod.POST, consumes = Constants.MEDIA_TYPE_MULTIPART_FORM_DATA)
  @ResponseBody
  public void save(final HttpServletRequest request, final HttpServletResponse response, @RequestParam("user") final String userJson,
    @RequestParam(value = "file", required = false) final MultipartFile file) throws Exception {
    final User user = this.objectMapper.readValue(userJson, User.class);
    user.setUsername(StringUtils.trim(user.getUsername()));
    user.setCompanyId(StringUtils.trim(user.getCompanyId()));
    user.setDefaultGroupId(StringUtils.trim(user.getDefaultGroupId()));
    user.setId(StringUtils.trim(user.getId()));

    final String userId = request.getHeader(this.requestHeadKey.getUserIdKey());
    final String companyId = RequestUtils.getCompanyId(request, this.requestHeadKey);

    final boolean isSuperuser = RequestUtils.hasSpecId(request.getHeader(this.requestHeadKey.getPermIdKey()), this.objectMapper,
      this.specialId.getSuperuserAddData());
    if (!isSuperuser && !StringUtils.equals(user.getCompanyId(), companyId)) {
      UserController.LOGGER.error("user not has permissions, user id: {}", userId);
      response.setStatus(HttpStatus.SC_NOT_FOUND);
      throw new RuntimeException("Not Found");
    }

    if (Objects.nonNull(file)) {
      final String filename = this.uploadAvatar(file.getBytes(), file.getOriginalFilename(), user.getUsername(), user.getCompanyId());
      final UserProfile userProfile = Optional.ofNullable(user.getUserProfile()).orElse(new UserProfile());
      userProfile.setAvatar(filename);
      userProfile.setAvatarType(file.getContentType());
      user.setUserProfile(userProfile);
    }
    try {
      this.userService.save(user, userId);
    }
    catch (final Exception e) {
      response.setStatus(HttpStatus.SC_INTERNAL_SERVER_ERROR);
      throw e;
    }
    if (!StringUtils.equals("test", this.active) && StringUtils.equals(user.getId(), userId)) {
      this.sharingService.refreshUser(user);
    }
  }

  @RequestMapping(value = "search", method = RequestMethod.POST, consumes = MimeTypeUtils.APPLICATION_JSON_VALUE)
  @ResponseBody
  public PaginationInfo<User> search(final HttpServletRequest request, @RequestBody final SearchUser searchUser) throws Exception {
    searchUser.setCompanyName(StringUtils.defaultIfBlank(searchUser.getCompanyName(), null));
    searchUser.setName(StringUtils.defaultIfBlank(searchUser.getName(), null));
    searchUser.setRealName(StringUtils.defaultIfBlank(searchUser.getRealName(), null));
    searchUser.setEqName(StringUtils.defaultIfBlank(searchUser.getEqName(), null));
    searchUser.setCompanyId(StringUtils.defaultIfBlank(searchUser.getCompanyId(), null));

    final String companyId = RequestUtils.getCompanyId(request, this.requestHeadKey);
    final boolean allowNotCompanyCond = RequestUtils.hasSpecId(request.getHeader(this.requestHeadKey.getPermIdKey()), this.objectMapper,
      this.specialId.getNotIncludeCompanyId());
    return this.userService.search(searchUser, companyId, allowNotCompanyCond);
  }

  private void setResponseHeadForAvatar(final HttpServletResponse response, final long size, final String filename, final String fileType)
    throws FileNotFoundException, IOException {
    if (StringUtils.isAnyBlank(filename, fileType)) {
      return;
    }
    response.setContentType(fileType);
    response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=" + filename);
    response.setContentLength((int) size);
  }

  private void setUserCurrentRoleId(final User user, final String groupId) {
    final Set<com.una.common.pojo.Role> allRoleSet = new HashSet<>();
    if (StringUtils.isNotBlank(groupId)) {
      final Optional<? extends Group> groupOptional = Optional.ofNullable(user.getGroupSet()).orElse(Set.of()).stream()
        .filter(o -> StringUtils.equals(o.getId(), groupId)).findAny();
      allRoleSet.addAll(groupOptional.map(Group::getRoleSet).orElse(Set.of()));
    }
    allRoleSet.addAll(Optional.ofNullable(user.getRoleSet()).orElse(Set.of()));
    final Set<String> roleIdSet = new HashSet<>();
    allRoleSet.forEach(r -> roleIdSet.add(r.getId()));
    user.setCurrentRoleId(roleIdSet);
    user.setCurrentGroupId(groupId);
  }

  @RequestMapping(value = "takeOutUser", method = RequestMethod.POST, consumes = Constants.MEDIA_TYPE_X_WWW_FORM_URLENCODED)
  @ResponseBody
  public void takeOutUser(final HttpServletRequest request, final HttpServletResponse response, final String sessionId) throws Exception {
    final ResponseVO<User> responseVO = this.sharingService.getUser(sessionId, true);
    final User user = responseVO.getData();
    if (Objects.isNull(user)) {
      return;
    }

    final String userId = RequestUtils.getUserId(request, this.requestHeadKey);
    final boolean isSuperuser = RequestUtils.hasSpecId(RequestUtils.getPermIds(request, this.requestHeadKey), this.objectMapper,
      this.specialId.getSuperuserAddData());
    if (!isSuperuser) {
      final String companyId = RequestUtils.getCompanyId(request, this.requestHeadKey);
      if (!StringUtils.equals(user.getCompanyId(), companyId)) {
        UserController.LOGGER.error("user not has permissions, user id: {}", userId);
        response.setStatus(HttpStatus.SC_NOT_FOUND);
        throw new RuntimeException("Not Found");
      }
    }

    this.sharingService.removeUser(sessionId);
  }

  @RequestMapping(value = "update", method = RequestMethod.POST, consumes = Constants.MEDIA_TYPE_MULTIPART_FORM_DATA)
  @ResponseBody
  public void update(final HttpServletRequest request, @RequestParam("user") final String userJson,
    @RequestParam(value = "file", required = false) final MultipartFile file) throws Exception {
    final User user = this.objectMapper.readValue(userJson, User.class);
    final String userId = request.getHeader(this.requestHeadKey.getUserIdKey());
    if (!StringUtils.equals(user.getId(), userId)) {
      throw new RuntimeException("this api only using for user self");
    }
    if (Objects.nonNull(file)) {
      final String filename = this.uploadAvatar(file.getBytes(), file.getOriginalFilename(), user.getUsername(), user.getCompanyId());
      final UserProfile userProfile = Optional.ofNullable(user.getUserProfile()).orElse(new UserProfile());
      userProfile.setAvatar(filename);
      userProfile.setAvatarType(file.getContentType());
      user.setUserProfile(userProfile);
    }
    this.userService.save(user, userId);
    if (!StringUtils.equals("test", this.active) && StringUtils.equals(user.getId(), userId)) {
      this.sharingService.refreshUser(user);
    }
  }

  @RequestMapping(value = "updateForLogin", method = RequestMethod.POST, consumes = MimeTypeUtils.APPLICATION_JSON_VALUE)
  @ResponseBody
  public void updateForLogin(@RequestBody final User user) {
    this.userService.updateUser(user);
  }

  @RequestMapping(value = "updateRelation", method = RequestMethod.POST, consumes = MimeTypeUtils.APPLICATION_JSON_VALUE)
  @ResponseBody
  public void updateRelation(final HttpServletRequest request, final HttpServletResponse response, @RequestBody final User user) throws Exception {
    final String userId = RequestUtils.getUserId(request, this.requestHeadKey);
    final boolean isSuperuser = RequestUtils.hasSpecId(RequestUtils.getPermIds(request, this.requestHeadKey), this.objectMapper,
      this.specialId.getSuperuserAddData());
    if (!isSuperuser) {
      final String companyId = RequestUtils.getCompanyId(request, this.requestHeadKey);
      if (!StringUtils.equals(user.getCompanyId(), companyId)) {
        UserController.LOGGER.error("user not has permissions, user id: {}", userId);
        response.setStatus(HttpStatus.SC_NOT_FOUND);
        throw new RuntimeException("Not Found");
      }
    }
    this.userService.updateRelation(user, userId);
  }

  private String uploadAvatar(final byte[] content, final String origName, final String username, final String companyId) throws Exception {
    final String uuid = UUID.randomUUID().toString();
    final int lastIndexOf = StringUtils.lastIndexOf(origName, ".");
    String name = "";
    String subfix = "";
    if (lastIndexOf <= 0) {
      name = origName;
    }
    else {
      name = StringUtils.substring(origName, 0, lastIndexOf);
      subfix = StringUtils.substring(origName, lastIndexOf);
    }

    final TransmissionInfo dInfo = this.transmissionInfo;
    final String storeType = this.transmissionInfo.getStoreType();
    final String serverUsername = this.transmissionInfo.getUsername();
    final String password = this.transmissionInfo.getPassword();
    final String ip = this.transmissionInfo.getIp();
    final String folder = this.transmissionInfo.getFolder();
    final int port = Optional.ofNullable(this.transmissionInfo.getPort()).orElse(0);
    final int length = Optional.ofNullable(this.transmissionInfo.getFilenameLength()).orElse(128);
    final long size = Optional.ofNullable(this.transmissionInfo.getFileLength()).orElse(2L * 1024 * 1024);

    final List<Configuration> cList = Optional.ofNullable(this.sharingService.getConfigurationList(companyId)).orElse(List.of());
    final String cStoreType = BeanPropUtil.getValueForKey(cList, dInfo.getStoreTypeKey(), storeType, v -> StringUtils.defaultIfBlank(v, storeType));
    final String cUsername = BeanPropUtil.getValueForKey(cList, dInfo.getUsernameKey(), serverUsername, v -> StringUtils.defaultIfBlank(v, serverUsername));
    final String cPassword = BeanPropUtil.getValueForKey(cList, dInfo.getPasswordKey(), password, v -> StringUtils.defaultIfBlank(v, password));
    final String cIp = BeanPropUtil.getValueForKey(cList, dInfo.getIpKey(), ip, v -> StringUtils.defaultIfBlank(v, ip));
    String cFolder = BeanPropUtil.getValueForKey(cList, dInfo.getFolderKey(), folder, v -> StringUtils.defaultIfBlank(v, folder));
    final int cPort = BeanPropUtil.getValueForKey(cList, dInfo.getPortKey(), port, v -> Integer.parseInt(StringUtils.defaultString(v, "0")));
    final int cLength = BeanPropUtil.getValueForKey(cList, dInfo.getFilenameLengthKey(), length, v -> Integer.parseInt(StringUtils.defaultString(v, "128")));
    final long cSize = BeanPropUtil.getValueForKey(cList, dInfo.getFileLengthKey(), size, v -> Long.parseLong(StringUtils.defaultString(v, "2097152")));

    if (content.length > cSize) {
      throw new RuntimeException(String.format("avatar size too big, must less than or equal to %s byte", cSize));
    }

    String filename = String.join("_", companyId, uuid, username, name);
    if (filename.length() + subfix.length() > cLength) {
      final String tempname = String.join("_", companyId, uuid) + subfix;
      if (tempname.length() >= cLength) {
        filename = StringUtils.substring(tempname, 0, cLength);
      }
      else {
        filename = StringUtils.substring(filename, 0, cLength - subfix.length()) + subfix;
      }
    }
    else {
      filename = filename + subfix;
    }

    if (StringUtils.equals("file", cStoreType)) {
      cFolder = this.getAvatarFolder1(cFolder);
    }
    try (Transmission instance = TransmissionUtil.Type.getInstance(cStoreType); InputStream inputStream = new ByteArrayInputStream(content);) {
      instance.login(cIp, cPort, cUsername, cPassword);
      instance.mkdirs(cFolder);
      instance.uploadFile(inputStream, cFolder, filename);
    }
    catch (final Exception e) {
      UserController.LOGGER.error("upload avatar exception", e);
    }
    return filename;
  }

}
