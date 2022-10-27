package com.una.system.manager.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.una.common.pojo.PaginationInfo;
import com.una.common.utils.LogUtil;
import com.una.system.manager.model.Menu;
import com.una.system.manager.model.Permissions;
import com.una.system.manager.model.UrlPerm;
import com.una.system.manager.pojo.PermissionsFunctionItemRecord;
import com.una.system.manager.pojo.RequestHeadKey;
import com.una.system.manager.pojo.SpecialId;
import com.una.system.manager.pojo.req.PermissionsRelation;
import com.una.system.manager.pojo.req.SearchPermissions;
import com.una.system.manager.service.MenuService;
import com.una.system.manager.service.PermissionsService;
import com.una.system.manager.service.UrlPermService;
import com.una.system.manager.utils.RequestUtils;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpStatus;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.util.MimeTypeUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("perm")
public class PermController {
  private static final Logger LOGGER = LogUtil.getRunLogger();
  @Autowired
  private SpecialId specialId;
  @Autowired
  private RequestHeadKey requestHeadKey;

  @Autowired
  private ObjectMapper objectMapper;
  @Autowired
  private PermissionsService permissionsService;
  @Autowired
  private MenuService menuService;
  @Autowired
  private UrlPermService urlPermService;

  @RequestMapping(value = "findByRole",
      method = RequestMethod.POST,
      consumes = MimeTypeUtils.APPLICATION_JSON_VALUE)
  @ResponseBody
  public Set<Permissions> findByRole(@RequestBody final Set<String> roleIdSet) {
    final Set<String> currentRoleIdSet = Optional.ofNullable(roleIdSet).orElse(Set.of());
    if (CollectionUtils.isEmpty(currentRoleIdSet)) {
      return Set.of();
    }
    return this.permissionsService.findByRoleIds(currentRoleIdSet);
  }

  @RequestMapping(value = "findFunctionByRole",
      method = RequestMethod.POST,
      consumes = MimeTypeUtils.APPLICATION_JSON_VALUE)
  @ResponseBody
  public Collection<PermissionsFunctionItemRecord> findFunctionByRole(
      @RequestBody final Set<String> roleIdSet) {
    final Map<String, Permissions> permissionsMap = this.findByRole(roleIdSet).stream()
        .collect(Collectors.toMap(Permissions::getId, o -> o));
    if (CollectionUtils.isEmpty(permissionsMap)) {
      return List.of();
    }
    final Set<Menu> menuSet = this.menuService.findByPermIds(permissionsMap.keySet());
    final List<UrlPerm> urlPermList = new ArrayList<>(
        this.urlPermService.findByPermIds(permissionsMap.keySet()));
    final Collection<PermissionsFunctionItemRecord> records = new ArrayList<>();
    final List<Menu> menuList = new ArrayList<>();
    this.flatMenu(menuSet, menuList);
    Collections.sort(menuList, (o1, o2) -> StringUtils.defaultIfBlank(o1.getName(), "")
        .compareTo(StringUtils.defaultIfBlank(o2.getName(), "")));
    Collections.sort(urlPermList, (o1, o2) -> StringUtils.defaultIfBlank(o1.getName(), "")
        .compareTo(StringUtils.defaultIfBlank(o2.getName(), "")));

    final Set<String> readyRemovePermIdSet = new HashSet<>();
    for (final Menu menu : menuList) {
      records.add(new PermissionsFunctionItemRecord(menu.getId(), "menu", menu.getName(),
          menu.getRemark()));
      readyRemovePermIdSet.add(menu.getPermissionsId());
    }
    for (final UrlPerm urlPerm : urlPermList) {
      records.add(new PermissionsFunctionItemRecord(urlPerm.getId(), "url", urlPerm.getName(),
          urlPerm.getRemark()));
      readyRemovePermIdSet.add(urlPerm.getPermissionsId());
    }
    readyRemovePermIdSet.forEach(permissionsMap::remove);
    for (final Permissions permissions : permissionsMap.values()) {
      records.add(new PermissionsFunctionItemRecord(permissions.getId(), "perm",
          permissions.getName(), permissions.getRemark()));
    }
    return records;
  }

  private void flatMenu(final Collection<Menu> rootMenu, final List<Menu> menuList) {
    for (final Menu menu : rootMenu) {
      menuList.add(menu);
      if (!CollectionUtils.isEmpty(menu.getChildrenMenuSet())) {
        this.flatMenu(menu.getChildrenMenuSet(), menuList);
      }
    }
  }

  @RequestMapping(value = "save",
      method = RequestMethod.PUT,
      consumes = MimeTypeUtils.APPLICATION_JSON_VALUE)
  @ResponseBody
  public void save(final HttpServletRequest request, final HttpServletResponse response,
      @RequestBody final Permissions permissions) throws Exception {
    permissions.setName(StringUtils.trim(permissions.getName()));
    permissions.setId(StringUtils.trim(permissions.getId()));

    final String userId = request.getHeader(this.requestHeadKey.getUserIdKey());
    final boolean isSuperuser = RequestUtils.hasSpecId(
        RequestUtils.getPermIds(request, this.requestHeadKey), this.objectMapper,
        this.specialId.getSuperuserAddData());
    if (!isSuperuser) {
      PermController.LOGGER.error("user not has permissions, user id: {}", userId);
      response.setStatus(HttpStatus.SC_NOT_FOUND);
      throw new RuntimeException("Not Found");
    }
    try {
      this.permissionsService.save(permissions, userId);
    } catch (final Exception e) {
      response.setStatus(HttpStatus.SC_INTERNAL_SERVER_ERROR);
      throw e;
    }
  }

  @RequestMapping(value = "search",
      method = RequestMethod.POST,
      consumes = MimeTypeUtils.APPLICATION_JSON_VALUE)
  @ResponseBody
  public PaginationInfo<Permissions> search(final HttpServletRequest request,
      @RequestBody final SearchPermissions searchPermissions) throws Exception {
    searchPermissions.setName(StringUtils.defaultIfBlank(searchPermissions.getName(), null));
    searchPermissions.setStatus(StringUtils.defaultIfBlank(searchPermissions.getStatus(), null));

    return this.permissionsService.search(searchPermissions);
  }

  @RequestMapping(value = "updateRelation",
      method = RequestMethod.POST,
      consumes = MimeTypeUtils.APPLICATION_JSON_VALUE)
  @ResponseBody
  public void updateRelation(final HttpServletRequest request,
      @RequestBody final PermissionsRelation permissionsRelation) throws Exception {
    final String userId = RequestUtils.getUserId(request, this.requestHeadKey);
    this.permissionsService.updateRelation(permissionsRelation, userId);
  }

}
