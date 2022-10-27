package com.una.system.manager.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.una.common.Constants;
import com.una.common.pojo.PaginationInfo;
import com.una.common.utils.LogUtil;
import com.una.system.manager.microservices.SharingService;
import com.una.system.manager.model.Configuration;
import com.una.system.manager.pojo.RequestHeadKey;
import com.una.system.manager.pojo.SpecialId;
import com.una.system.manager.pojo.req.SearchConf;
import com.una.system.manager.service.ConfigurationService;
import com.una.system.manager.utils.RequestUtils;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpStatus;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.util.Assert;
import org.springframework.util.MimeTypeUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("conf")
public class ConfigurationController {
  private static final Logger LOGGER = LogUtil.getRunLogger();

  @Value("${app.running-mode:}")
  private String runningMode;

  @Autowired
  private SharingService sharingService;

  @Autowired
  private ConfigurationService configurationService;

  @Autowired
  private ObjectMapper objectMapper;

  @Autowired
  private RequestHeadKey requestHeadKey;

  @Autowired
  private SpecialId specialId;

  @RequestMapping(value = "exist",
      method = RequestMethod.POST,
      consumes = Constants.MEDIA_TYPE_X_WWW_FORM_URLENCODED)
  @ResponseBody
  public boolean exist(final HttpServletRequest request, final String companyId, final String key)
      throws Exception {
    Assert.hasText(key, "parameter 'key' must not be empty");
    final SearchConf searchConf = new SearchConf();
    searchConf.setEqConfKey(key);
    searchConf.setCompanyId(companyId);

    final PaginationInfo<Configuration> paginationInfo = this.search(request, searchConf);
    return !paginationInfo.isEmpty();
  }

  @RequestMapping(value = "refresh",
      method = RequestMethod.POST,
      consumes = MimeTypeUtils.TEXT_PLAIN_VALUE)
  @ResponseBody
  public void refresh(final HttpServletRequest request) {
    final String userId = request.getHeader(this.requestHeadKey.getUserIdKey());
    this.configurationService.syncFormManage(userId);
    this.configurationService.refresh();
  }

  @RequestMapping(value = "save",
      method = RequestMethod.PUT,
      consumes = MimeTypeUtils.APPLICATION_JSON_VALUE)
  @ResponseBody
  public void save(final HttpServletRequest request, final HttpServletResponse response,
      @RequestBody final Configuration configuration) throws Exception {
    configuration.setName(StringUtils.trim(configuration.getName()));
    configuration.setCompanyId(StringUtils.trim(configuration.getCompanyId()));
    configuration.setConfKey(StringUtils.trim(configuration.getConfKey()));
    configuration.setValueType(StringUtils.trim(configuration.getValueType()));

    final String userId = request.getHeader(this.requestHeadKey.getUserIdKey());
    final String companyId = RequestUtils.getCompanyId(request, this.requestHeadKey);

    final boolean isSuperuser = RequestUtils.hasSpecId(
        request.getHeader(this.requestHeadKey.getPermIdKey()), this.objectMapper,
        this.specialId.getSuperuserAddData());
    if (!isSuperuser && !StringUtils.equals(configuration.getCompanyId(), companyId)) {
      ConfigurationController.LOGGER.error("user not has permissions, user id: {}", userId);
      response.setStatus(HttpStatus.SC_NOT_FOUND);
      throw new RuntimeException("Not Found");
    }

    try {
      this.configurationService.save(configuration, userId);
    } catch (final Exception e) {
      response.setStatus(HttpStatus.SC_INTERNAL_SERVER_ERROR);
      throw e;
    }
    if (StringUtils.equals("normal", this.runningMode)) {
      List<Configuration> configurationList = this.sharingService
          .getConfigurationList(configuration.getCompanyId());
      configurationList = new ArrayList<>(Optional.ofNullable(configurationList).orElse(List.of()));
      for (final Iterator<Configuration> iter = configurationList.iterator(); iter.hasNext();) {
        final Configuration config = iter.next();
        if (StringUtils.equals(config.getId(), configuration.getId())) {
          iter.remove();
        }
      }
      configuration.setCompany(null);
      configuration.setCreatedUser(null);
      configurationList.add(configuration);
      this.sharingService.saveConfigurationList(configuration.getCompanyId(), configurationList);
    }
  }

  @RequestMapping(value = "search",
      method = RequestMethod.POST,
      consumes = MimeTypeUtils.APPLICATION_JSON_VALUE)
  @ResponseBody
  public PaginationInfo<Configuration> search(final HttpServletRequest request,
      @RequestBody final SearchConf searchConf) throws Exception {
    searchConf.setName(StringUtils.defaultIfBlank(searchConf.getName(), null));
    searchConf.setCompanyId(StringUtils.defaultIfBlank(searchConf.getCompanyId(), null));
    searchConf.setConfKey(StringUtils.defaultIfBlank(searchConf.getConfKey(), null));
    searchConf.setStatus(StringUtils.defaultIfBlank(searchConf.getStatus(), null));
    searchConf.setCompanyName(StringUtils.defaultIfBlank(searchConf.getCompanyName(), null));
    searchConf.setEqConfKey(StringUtils.defaultIfBlank(searchConf.getEqConfKey(), null));

    final String companyId = RequestUtils.getCompanyId(request, this.requestHeadKey);
    final boolean allowNotCompanyCond = RequestUtils.hasSpecId(
        request.getHeader(this.requestHeadKey.getPermIdKey()), this.objectMapper,
        this.specialId.getNotIncludeCompanyId());
    return this.configurationService.search(searchConf, companyId, allowNotCompanyCond);
  }
}
