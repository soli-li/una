package com.una.system.manager.controller;

import java.util.Objects;
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
import org.springframework.util.CollectionUtils;
import org.springframework.util.MimeTypeUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.una.common.Constants;
import com.una.common.pojo.PaginationInfo;
import com.una.common.utils.LogUtil;
import com.una.system.manager.model.Menu;
import com.una.system.manager.pojo.RequestHeadKey;
import com.una.system.manager.pojo.SpecialId;
import com.una.system.manager.pojo.req.SearchMenu;
import com.una.system.manager.service.MenuService;
import com.una.system.manager.utils.RequestUtils;

@Controller
@RequestMapping("menu")
public class MenuController {
  private static final Logger LOGGER = LogUtil.getRunLogger();

  @Autowired
  private ObjectMapper objectMapper;
  @Autowired
  private MenuService menuService;

  @Autowired
  private SpecialId specialId;
  @Autowired
  private RequestHeadKey requestHeadKey;

  @RequestMapping(value = "current", method = RequestMethod.GET)
  @ResponseBody
  public Set<Menu> current(final HttpServletRequest request) throws Exception {
    final String permIds = request.getHeader(this.requestHeadKey.getPermIdKey());
    final Set<String> permissionsIds = this.objectMapper.readValue(permIds, new TypeReference<Set<String>>() {
    });
    final Set<String> currentPermIdSet = Optional.ofNullable(permissionsIds).orElse(Set.of());
    if (CollectionUtils.isEmpty(currentPermIdSet)) {
      return Set.of();
    }
    return this.menuService.findByPermIds(currentPermIdSet);
  }

  @RequestMapping(value = "exist", method = RequestMethod.POST, consumes = Constants.MEDIA_TYPE_X_WWW_FORM_URLENCODED)
  @ResponseBody
  public boolean exist(final HttpServletRequest request, final String uri) throws Exception {
    Assert.hasText(uri, "parameter 'uri' must not be empty");
    final SearchMenu searchMenu = new SearchMenu();
    searchMenu.setEqFrontEndUri(uri);

    final PaginationInfo<Menu> paginationInfo = this.search(request, searchMenu);
    return !paginationInfo.isEmpty();
  }

  @RequestMapping(value = "save", method = RequestMethod.PUT, consumes = MimeTypeUtils.APPLICATION_JSON_VALUE)
  @ResponseBody
  public void save(final HttpServletRequest request, final HttpServletResponse response, @RequestBody final Menu menu) throws Exception {
    menu.setPermissionsId(StringUtils.trim(menu.getPermissionsId()));
    menu.setParentId(StringUtils.trim(menu.getParentId()));
    menu.setName(StringUtils.trim(menu.getName()));
    menu.setFrontEndUri(StringUtils.trim(menu.getFrontEndUri()));
    menu.setId(StringUtils.trim(menu.getId()));
    menu.setSort(Objects.isNull(menu.getSort()) ? 0 : menu.getSort());

    final String userId = request.getHeader(this.requestHeadKey.getUserIdKey());
    final boolean isSuperuser = RequestUtils.hasSpecId(RequestUtils.getPermIds(request, this.requestHeadKey), this.objectMapper,
      this.specialId.getSuperuserAddData());
    if (!isSuperuser) {
      MenuController.LOGGER.error("user not has permissions, user id: {}", userId);
      response.setStatus(HttpStatus.SC_NOT_FOUND);
      throw new RuntimeException("Not Found");
    }
    try {
      this.menuService.save(menu, userId);
    }
    catch (final Exception e) {
      response.setStatus(HttpStatus.SC_INTERNAL_SERVER_ERROR);
      throw e;
    }
  }

  @RequestMapping(value = "search", method = RequestMethod.POST, consumes = MimeTypeUtils.APPLICATION_JSON_VALUE)
  @ResponseBody
  public PaginationInfo<Menu> search(final HttpServletRequest request, @RequestBody final SearchMenu searchMenu) throws Exception {
    searchMenu.setName(StringUtils.defaultIfBlank(searchMenu.getName(), null));
    searchMenu.setId(StringUtils.defaultIfBlank(searchMenu.getId(), null));
    searchMenu.setEqFrontEndUri(StringUtils.defaultIfBlank(searchMenu.getEqFrontEndUri(), null));

    return this.menuService.search(searchMenu);
  }
}
