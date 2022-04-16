package com.una.system.manager.controller;

import java.util.List;
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

import com.fasterxml.jackson.databind.ObjectMapper;
import com.una.common.Constants;
import com.una.common.pojo.PaginationInfo;
import com.una.common.utils.LogUtil;
import com.una.system.manager.model.Group;
import com.una.system.manager.pojo.RequestHeadKey;
import com.una.system.manager.pojo.SpecialId;
import com.una.system.manager.pojo.req.SearchGroup;
import com.una.system.manager.service.GroupService;
import com.una.system.manager.utils.RequestUtils;

@Controller
@RequestMapping("group")
public class GroupController {

  private static final Logger LOGGER = LogUtil.getRunLogger();

  @Autowired
  private ObjectMapper objectMapper;

  @Autowired
  private GroupService groupService;

  @Autowired
  private RequestHeadKey requestHeadKey;

  @Autowired
  private SpecialId specialId;

  @RequestMapping(value = "exist", method = RequestMethod.POST, consumes = Constants.MEDIA_TYPE_X_WWW_FORM_URLENCODED)
  @ResponseBody
  public boolean exist(final HttpServletRequest request, final String name, final String companyId) throws Exception {
    Assert.hasText(name, "parameter 'name' must not be empty");
    Assert.hasText(companyId, "parameter 'companyId' must not be empty");
    final SearchGroup searchGroup = new SearchGroup();
    searchGroup.setEqName(name);
    searchGroup.setCompanyId(companyId);

    final PaginationInfo<Group> paginationInfo = this.search(request, searchGroup);
    return !paginationInfo.isEmpty();
  }

  @RequestMapping(value = "findByRole", method = RequestMethod.POST, consumes = MimeTypeUtils.APPLICATION_JSON_VALUE)
  @ResponseBody
  public Set<Group> findByRole(final HttpServletRequest request, @RequestBody final List<String> roles) throws Exception {
    String companyId = RequestUtils.getCompanyId(request, this.requestHeadKey);
    final boolean allowNotCompanyCond = RequestUtils.hasSpecId(request.getHeader(this.requestHeadKey.getPermIdKey()), this.objectMapper,
      this.specialId.getNotIncludeCompanyId());
    if (allowNotCompanyCond) {
      companyId = null;
    }
    return this.groupService.findByRole(roles, null, companyId);
  }

  @RequestMapping(value = "findByUser", method = RequestMethod.POST, consumes = MimeTypeUtils.APPLICATION_JSON_VALUE)
  @ResponseBody
  public Set<Group> findByUser(final HttpServletRequest request, @RequestBody final List<String> users) throws Exception {
    String companyId = RequestUtils.getCompanyId(request, this.requestHeadKey);
    final boolean allowNotCompanyCond = RequestUtils.hasSpecId(request.getHeader(this.requestHeadKey.getPermIdKey()), this.objectMapper,
      this.specialId.getNotIncludeCompanyId());
    if (allowNotCompanyCond) {
      companyId = null;
    }
    return this.groupService.findByUser(users, null, companyId);
  }

  @RequestMapping(value = "save", method = RequestMethod.PUT, consumes = MimeTypeUtils.APPLICATION_JSON_VALUE)
  @ResponseBody
  public void save(final HttpServletRequest request, final HttpServletResponse response, @RequestBody final Group group) throws Exception {
    group.setParentId(StringUtils.trim(group.getParentId()));
    group.setName(StringUtils.trim(group.getName()));
    group.setCompanyId(StringUtils.trim(group.getCompanyId()));
    group.setId(StringUtils.trim(group.getId()));

    final String userId = request.getHeader(this.requestHeadKey.getUserIdKey());

    final boolean isSuperuser = RequestUtils.hasSpecId(request.getHeader(this.requestHeadKey.getPermIdKey()), this.objectMapper,
      this.specialId.getSuperuserAddData());
    final String companyId = RequestUtils.getCompanyId(request, this.requestHeadKey);
    if (!isSuperuser && !StringUtils.equals(group.getCompanyId(), companyId)) {
      GroupController.LOGGER.error("user not has permissions, user id: {}", userId);
      response.setStatus(HttpStatus.SC_NOT_FOUND);
      throw new RuntimeException("Not Found");
    }

    try {
      this.groupService.save(group, userId);
    }
    catch (final Exception e) {
      response.setStatus(HttpStatus.SC_INTERNAL_SERVER_ERROR);
      throw e;
    }
  }

  @RequestMapping(value = "search", method = RequestMethod.POST, consumes = MimeTypeUtils.APPLICATION_JSON_VALUE)
  @ResponseBody
  public PaginationInfo<Group> search(final HttpServletRequest request, @RequestBody final SearchGroup searchGroup) throws Exception {
    searchGroup.setName(StringUtils.defaultIfBlank(searchGroup.getName(), null));
    searchGroup.setCompanyName(StringUtils.defaultIfBlank(searchGroup.getCompanyName(), null));
    searchGroup.setStatus(StringUtils.defaultIfBlank(searchGroup.getStatus(), null));
    searchGroup.setCompanyId(StringUtils.defaultIfBlank(searchGroup.getCompanyId(), null));
    searchGroup.setParentId(StringUtils.defaultIfBlank(searchGroup.getParentId(), null));
    searchGroup.setEqName(StringUtils.defaultIfBlank(searchGroup.getEqName(), null));

    final String companyId = RequestUtils.getCompanyId(request, this.requestHeadKey);
    final boolean allowNotCompanyCond = RequestUtils.hasSpecId(request.getHeader(this.requestHeadKey.getPermIdKey()), this.objectMapper,
      this.specialId.getNotIncludeCompanyId());

    return this.groupService.search(searchGroup, companyId, allowNotCompanyCond);
  }

  @RequestMapping(value = "updateRelation", method = RequestMethod.POST, consumes = MimeTypeUtils.APPLICATION_JSON_VALUE)
  @ResponseBody
  public void updateRelation(final HttpServletRequest request, final HttpServletResponse response, @RequestBody final Group group) throws Exception {
    final String userId = RequestUtils.getUserId(request, this.requestHeadKey);
    final boolean isSuperuser = RequestUtils.hasSpecId(RequestUtils.getPermIds(request, this.requestHeadKey), this.objectMapper,
      this.specialId.getSuperuserAddData());
    if (!isSuperuser) {
      final String companyId = RequestUtils.getCompanyId(request, this.requestHeadKey);
      if (!StringUtils.equals(group.getCompanyId(), companyId)) {
        GroupController.LOGGER.error("user not has permissions, user id: {}", userId);
        response.setStatus(HttpStatus.SC_NOT_FOUND);
        throw new RuntimeException("Not Found");
      }
    }

    this.groupService.updateRelation(group, userId);
  }

}
