package com.una.system.manager.controller;

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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.una.common.Constants;
import com.una.common.pojo.PaginationInfo;
import com.una.common.utils.LogUtil;
import com.una.system.manager.model.Company;
import com.una.system.manager.pojo.RequestHeadKey;
import com.una.system.manager.pojo.SpecialId;
import com.una.system.manager.pojo.req.SearchCompany;
import com.una.system.manager.service.CompanyService;
import com.una.system.manager.utils.RequestUtils;

@Controller
@RequestMapping("company")
public class CompanyController {
  private static final Logger LOGGER = LogUtil.getRunLogger();
  @Autowired
  private ObjectMapper objectMapper;

  @Autowired
  private SpecialId specialId;
  @Autowired
  private RequestHeadKey requestHeadKey;

  @Autowired
  private CompanyService companyService;

  @RequestMapping(value = "exist", method = RequestMethod.POST, consumes = Constants.MEDIA_TYPE_X_WWW_FORM_URLENCODED)
  @ResponseBody
  public boolean existSName(final HttpServletRequest request, @RequestParam(required = false) final String name,
    @RequestParam(required = false) final String shortName) throws Exception {
    final boolean allBlank = StringUtils.isAllBlank(name, shortName);
    final boolean allHasText = StringUtils.isNotBlank(name) && StringUtils.isNotBlank(shortName);
    Assert.isTrue(!allBlank && !allHasText, "one of parameter 'name' and 'shortName' must not be empty");
    final SearchCompany searchCompany = new SearchCompany();
    if (StringUtils.isNotBlank(shortName)) {
      searchCompany.setEqSNamd(shortName);
    }
    else if (StringUtils.isNotBlank(name)) {
      searchCompany.setEqName(name);
    }

    final PaginationInfo<Company> paginationInfo = this.search(request, searchCompany);
    return !paginationInfo.isEmpty();
  }

  @RequestMapping(value = "save", method = RequestMethod.PUT, consumes = MimeTypeUtils.APPLICATION_JSON_VALUE)
  @ResponseBody
  public void save(final HttpServletRequest request, final HttpServletResponse response, @RequestBody final Company company) throws Exception {
    company.setName(StringUtils.trim(company.getName()));
    company.setShortName(StringUtils.trim(company.getShortName()));
    company.setPwPolicyId(StringUtils.trim(company.getPwPolicyId()));
    company.setId(StringUtils.trim(company.getId()));

    final String userId = RequestUtils.getUserId(request, this.requestHeadKey);
    final boolean isSuperuser = RequestUtils.hasSpecId(RequestUtils.getPermIds(request, this.requestHeadKey), this.objectMapper,
      this.specialId.getSuperuserAddData());
    if (!isSuperuser) {
      final String companyId = RequestUtils.getCompanyId(request, this.requestHeadKey);
      if (!StringUtils.equals(company.getId(), companyId)) {
        CompanyController.LOGGER.error("user not has permissions, user id: {}", userId);
        response.setStatus(HttpStatus.SC_NOT_FOUND);
        throw new RuntimeException("Not Found");
      }
    }

    try {
      this.companyService.save(company, userId);
    }
    catch (final Exception e) {
      response.setStatus(HttpStatus.SC_INTERNAL_SERVER_ERROR);
      throw e;
    }

  }

  @RequestMapping(value = "search", method = RequestMethod.POST, consumes = MimeTypeUtils.APPLICATION_JSON_VALUE)
  @ResponseBody
  public PaginationInfo<Company> search(final HttpServletRequest request, @RequestBody final SearchCompany searchCompany) throws Exception {
    searchCompany.setName(StringUtils.defaultIfBlank(searchCompany.getName(), null));
    searchCompany.setShortName(StringUtils.defaultIfBlank(searchCompany.getShortName(), null));
    searchCompany.setLegalPerson(StringUtils.defaultIfBlank(searchCompany.getLegalPerson(), null));
    searchCompany.setStatus(StringUtils.defaultIfBlank(searchCompany.getStatus(), null));
    searchCompany.setId(StringUtils.defaultIfBlank(searchCompany.getId(), null));
    searchCompany.setEqName(StringUtils.defaultIfBlank(searchCompany.getEqName(), null));
    searchCompany.setEqSNamd(StringUtils.defaultIfBlank(searchCompany.getEqSNamd(), null));

    final String companyId = RequestUtils.getCompanyId(request, this.requestHeadKey);
    final boolean allowNotCompanyCond = RequestUtils.hasSpecId(request.getHeader(this.requestHeadKey.getPermIdKey()), this.objectMapper,
      this.specialId.getNotIncludeCompanyId());

    return this.companyService.search(searchCompany, companyId, allowNotCompanyCond);
  }
}
