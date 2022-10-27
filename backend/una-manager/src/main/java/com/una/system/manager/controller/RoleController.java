package com.una.system.manager.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.una.common.Constants;
import com.una.common.pojo.PaginationInfo;
import com.una.common.utils.LogUtil;
import com.una.system.manager.model.Role;
import com.una.system.manager.pojo.RequestHeadKey;
import com.una.system.manager.pojo.SpecialId;
import com.una.system.manager.pojo.req.PermissionsRelation;
import com.una.system.manager.pojo.req.SearchRole;
import com.una.system.manager.service.RoleService;
import com.una.system.manager.utils.RequestUtils;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpStatus;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.Assert;
import org.springframework.util.MimeTypeUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("role")
public class RoleController {

  private static final Logger LOGGER = LogUtil.getRunLogger();
  @Autowired
  private ObjectMapper objectMapper;

  @Autowired
  private RoleService roleService;

  @Autowired
  private RequestHeadKey requestHeadKey;

  @Autowired
  private SpecialId specialId;

  @RequestMapping(value = "exist",
      method = RequestMethod.POST,
      consumes = Constants.MEDIA_TYPE_X_WWW_FORM_URLENCODED)
  @ResponseBody
  public boolean exist(final HttpServletRequest request, final String authority,
      final String companyId) throws Exception {
    Assert.hasText(authority, "parameter 'authority' must not be empty");
    Assert.hasText(companyId, "parameter 'companyId' must not be empty");
    final SearchRole searchRole = new SearchRole();
    searchRole.setEqAuthority(authority);
    searchRole.setCompanyId(companyId);

    final PaginationInfo<Role> paginationInfo = this.search(request, searchRole);
    return !paginationInfo.isEmpty();
  }

  @RequestMapping(value = "findByGroup",
      method = RequestMethod.POST,
      consumes = MimeTypeUtils.APPLICATION_JSON_VALUE)
  @ResponseBody
  public Set<Role> findByGroup(final HttpServletRequest request,
      @RequestBody final List<String> groups) throws Exception {
    String companyId = RequestUtils.getCompanyId(request, this.requestHeadKey);
    final boolean allowNotCompanyCond = RequestUtils.hasSpecId(
        request.getHeader(this.requestHeadKey.getPermIdKey()), this.objectMapper,
        this.specialId.getNotIncludeCompanyId());
    if (allowNotCompanyCond) {
      companyId = null;
    }
    return this.roleService.findByGroup(groups, null, companyId);
  }

  @RequestMapping(value = "findByPerm",
      method = RequestMethod.POST,
      consumes = MimeTypeUtils.APPLICATION_JSON_VALUE)
  @ResponseBody
  public Set<Role> findByPerm(final HttpServletRequest request, final HttpServletResponse response,
      @RequestBody final PermissionsRelation permissionsRelation) throws Exception {
    String companyId = RequestUtils.getCompanyId(request, this.requestHeadKey);
    final boolean allowNotCompanyCond = RequestUtils.hasSpecId(
        request.getHeader(this.requestHeadKey.getPermIdKey()), this.objectMapper,
        this.specialId.getNotIncludeCompanyId());
    if (allowNotCompanyCond) {
      companyId = permissionsRelation.getCompanyId();
    } else if (!StringUtils.equals(companyId, permissionsRelation.getCompanyId())) {
      final String userId = RequestUtils.getUserId(request, this.requestHeadKey);
      RoleController.LOGGER.error("user not has permissions, user id: {}", userId);
      response.setStatus(HttpStatus.SC_NOT_FOUND);
      throw new RuntimeException("Not Found");
    }
    return this.roleService.findByPerm(permissionsRelation.getPermissionsList(),
        permissionsRelation.getCompanyId());
  }

  @RequestMapping(value = "findByUser",
      method = RequestMethod.POST,
      consumes = MimeTypeUtils.APPLICATION_JSON_VALUE)
  @ResponseBody
  public Set<Role> findByUser(final HttpServletRequest request,
      @RequestBody final List<String> users) throws Exception {
    String companyId = RequestUtils.getCompanyId(request, this.requestHeadKey);
    final boolean allowNotCompanyCond = RequestUtils.hasSpecId(
        request.getHeader(this.requestHeadKey.getPermIdKey()), this.objectMapper,
        this.specialId.getNotIncludeCompanyId());
    if (allowNotCompanyCond) {
      companyId = null;
    }
    return this.roleService.findByUser(users, null, companyId);
  }

  @RequestMapping(value = "save",
      method = RequestMethod.PUT,
      consumes = MimeTypeUtils.APPLICATION_JSON_VALUE)
  @ResponseBody
  public void save(final HttpServletRequest request, final HttpServletResponse response,
      @RequestBody final Role role) throws Exception {
    role.setAuthority(StringUtils.trim(role.getAuthority()));
    role.setCompanyId(StringUtils.trim(role.getCompanyId()));
    role.setId(StringUtils.trim(role.getId()));

    final String userId = request.getHeader(this.requestHeadKey.getUserIdKey());
    final String companyId = RequestUtils.getCompanyId(request, this.requestHeadKey);

    final boolean isSuperuser = RequestUtils.hasSpecId(
        request.getHeader(this.requestHeadKey.getPermIdKey()), this.objectMapper,
        this.specialId.getSuperuserAddData());
    if (!isSuperuser && !StringUtils.equals(role.getCompanyId(), companyId)) {
      RoleController.LOGGER.error("user not has permissions, user id: {}", userId);
      response.setStatus(HttpStatus.SC_NOT_FOUND);
      throw new RuntimeException("Not Found");
    }

    try {
      this.roleService.save(role, userId);
    } catch (final Exception e) {
      response.setStatus(HttpStatus.SC_INTERNAL_SERVER_ERROR);
      throw e;
    }
  }

  @RequestMapping(value = "search",
      method = RequestMethod.POST,
      consumes = MimeTypeUtils.APPLICATION_JSON_VALUE)
  @ResponseBody
  public PaginationInfo<Role> search(final HttpServletRequest request,
      @RequestBody final SearchRole searchRole) throws Exception {
    searchRole.setAuthority(StringUtils.defaultIfBlank(searchRole.getAuthority(), null));
    searchRole.setCompanyName(StringUtils.defaultIfBlank(searchRole.getCompanyName(), null));
    searchRole.setStatus(StringUtils.defaultIfBlank(searchRole.getStatus(), null));
    searchRole.setEqAuthority(StringUtils.defaultIfBlank(searchRole.getEqAuthority(), null));
    searchRole.setCompanyId(StringUtils.defaultIfBlank(searchRole.getCompanyId(), null));

    final String companyId = request.getHeader(this.requestHeadKey.getCompanyIdKey());
    final TypeReference<Set<String>> typeRef = new TypeReference<>() {
    };
    final Set<String> permIdsSet = this.objectMapper.readValue(
        Optional.ofNullable(request.getHeader(this.requestHeadKey.getPermIdKey())).orElse("[]"),
        typeRef);
    final boolean allowNotCompanyCond = permIdsSet
        .contains(this.specialId.getNotIncludeCompanyId());
    return this.roleService.search(searchRole, companyId, allowNotCompanyCond);
  }

  @RequestMapping(value = "updateRelation",
      method = RequestMethod.POST,
      consumes = MimeTypeUtils.APPLICATION_JSON_VALUE)
  @ResponseBody
  public void updateRelation(final HttpServletRequest request, final HttpServletResponse response,
      @RequestBody final Role role) throws Exception {
    final String userId = RequestUtils.getUserId(request, this.requestHeadKey);
    final boolean isSuperuser = RequestUtils.hasSpecId(
        RequestUtils.getPermIds(request, this.requestHeadKey), this.objectMapper,
        this.specialId.getSuperuserAddData());
    if (!isSuperuser) {
      final String companyId = RequestUtils.getCompanyId(request, this.requestHeadKey);
      if (!StringUtils.equals(role.getCompanyId(), companyId)) {
        RoleController.LOGGER.error("user not has permissions, user id: {}", userId);
        response.setStatus(HttpStatus.SC_NOT_FOUND);
        throw new RuntimeException("Not Found");
      }
    }
    this.roleService.updateRelation(role, userId);
  }

  @RequestMapping(value = "updateRelationForPerm",
      method = RequestMethod.POST,
      consumes = MimeTypeUtils.APPLICATION_JSON_VALUE)
  @ResponseBody
  public void updateRelationForPerm(final HttpServletRequest request,
      final HttpServletResponse response, @RequestBody final Role role) throws Exception {
    final String userId = RequestUtils.getUserId(request, this.requestHeadKey);
    this.roleService.updateRelationForPerm(role, userId);
  }
}
