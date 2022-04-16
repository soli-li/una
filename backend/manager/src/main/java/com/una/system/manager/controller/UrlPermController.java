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
import org.springframework.util.CollectionUtils;
import org.springframework.util.MimeTypeUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.una.common.pojo.PaginationInfo;
import com.una.common.utils.LogUtil;
import com.una.system.manager.model.UrlPerm;
import com.una.system.manager.pojo.RequestHeadKey;
import com.una.system.manager.pojo.SpecialId;
import com.una.system.manager.pojo.req.SearchUrlPerm;
import com.una.system.manager.service.UrlPermService;
import com.una.system.manager.utils.RequestUtils;

@Controller
@RequestMapping("url")
public class UrlPermController {
  private static final Logger LOGGER = LogUtil.getRunLogger();
  @Autowired
  private UrlPermService urlPermService;
  @Autowired
  private ObjectMapper objectMapper;

  @Autowired
  private RequestHeadKey requestHeadKey;

  @Autowired
  private SpecialId specialId;

  @RequestMapping(value = "findByPerm", method = RequestMethod.POST, consumes = MimeTypeUtils.APPLICATION_JSON_VALUE)
  @ResponseBody
  public Set<UrlPerm> findByPerm(@RequestBody final Set<String> permIdSet) {
    final Set<String> currentPermIdSet = Optional.ofNullable(permIdSet).orElse(Set.of());
    if (CollectionUtils.isEmpty(currentPermIdSet)) {
      return Set.of();
    }
    return this.urlPermService.findByPermIds(currentPermIdSet);
  }

  @RequestMapping(value = "save", method = RequestMethod.PUT, consumes = MimeTypeUtils.APPLICATION_JSON_VALUE)
  @ResponseBody
  public void save(final HttpServletRequest request, final HttpServletResponse response, @RequestBody final UrlPerm urlPerm) throws Exception {
    urlPerm.setId(StringUtils.trim(urlPerm.getId()));
    urlPerm.setName(StringUtils.trim(urlPerm.getName()));
    urlPerm.setPermissionsId(StringUtils.trim(urlPerm.getPermissionsId()));
    urlPerm.setUri(StringUtils.trim(urlPerm.getUri()));
    urlPerm.setSort(Objects.isNull(urlPerm.getSort()) ? 0 : urlPerm.getSort());

    final String userId = request.getHeader(this.requestHeadKey.getUserIdKey());
    final boolean isSuperuser = RequestUtils.hasSpecId(request.getHeader(this.requestHeadKey.getPermIdKey()), this.objectMapper,
      this.specialId.getSuperuserAddData());
    if (!isSuperuser) {
      UrlPermController.LOGGER.error("user not has permissions, user id: {}", userId);
      response.setStatus(HttpStatus.SC_NOT_FOUND);
      throw new RuntimeException("Not Found");
    }
    try {
      this.urlPermService.save(urlPerm, userId);
    }
    catch (final Exception e) {
      response.setStatus(HttpStatus.SC_INTERNAL_SERVER_ERROR);
      throw e;
    }
  }

  @RequestMapping(value = "search", method = RequestMethod.POST, consumes = MimeTypeUtils.APPLICATION_JSON_VALUE)
  @ResponseBody
  public PaginationInfo<UrlPerm> search(final HttpServletRequest request, @RequestBody final SearchUrlPerm searchUrlPerm) throws Exception {
    searchUrlPerm.setName(StringUtils.defaultIfBlank(searchUrlPerm.getName(), null));
    searchUrlPerm.setUri(StringUtils.defaultIfBlank(searchUrlPerm.getUri(), null));
    searchUrlPerm.setPerm(StringUtils.defaultIfBlank(searchUrlPerm.getPerm(), null));
    searchUrlPerm.setStatus(StringUtils.defaultIfBlank(searchUrlPerm.getStatus(), null));

    return this.urlPermService.search(searchUrlPerm);
  }
}
