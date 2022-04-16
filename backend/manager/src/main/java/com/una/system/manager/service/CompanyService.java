package com.una.system.manager.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Order;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import com.una.common.Constants;
import com.una.common.pojo.PaginationInfo;
import com.una.common.utils.LogUtil;
import com.una.common.utils.ObjectsToEntityUtil;
import com.una.common.utils.ObjectsToEntityUtil.Transform4;
import com.una.system.manager.dao.CompanyDao;
import com.una.system.manager.model.Company;
import com.una.system.manager.model.PasswordPolicy;
import com.una.system.manager.model.User;
import com.una.system.manager.pojo.req.SearchCompany;
import com.una.system.manager.utils.PaginationSortUtil;

@Service
public class CompanyService {
  private static final Logger LOGGER = LogUtil.getRunLogger();
  @Autowired
  private CompanyDao companyDao;
  @Autowired
  private PasswordPolicyService passwordPolicyService;
  @Autowired
  private GenerateService generateService;

  @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
  public Optional<Company> findById(final String companyId) {
    Assert.hasText(companyId, "parameter 'companyId' must not be empty");
    return this.companyDao.findById(companyId);
  }

  @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
  public void save(final Company company, final String userId) {
    Assert.notNull(company, "parameter 'company' must not be null");
    Assert.hasText(userId, "parameter 'userId' must not be empty");
    final String name = company.getName();
    final String shortName = company.getShortName();

    if (StringUtils.isAnyBlank(name, shortName)) {
      throw new RuntimeException("name and short name must not be empty.");
    }

    Assert.isTrue(StringUtils.equalsAny(company.getStatus(), Constants.ENABLE, Constants.DISABLE), "incorrect status value");
    this.passwordPolicyService.findById(company.getPwPolicyId()).orElseThrow(() -> new RuntimeException("password policy id not found"));

    if (StringUtils.isBlank(company.getId())) {
      company.setId(this.generateService.generateId());
      company.setCreatedDate(LocalDateTime.now());
      company.setCreatedUserId(userId);

      SearchCompany searchCompany = new SearchCompany();
      searchCompany.setEqName(name);

      PaginationInfo<Company> result = this.search(searchCompany, "", true);
      if (!result.isEmpty()) {
        throw new RuntimeException("name exist");
      }

      searchCompany = new SearchCompany();
      searchCompany.setEqSNamd(shortName);
      result = this.search(searchCompany, "", true);
      if (!result.isEmpty()) {
        throw new RuntimeException("short name exist");
      }
    }
    else {
      company.setUpdatedDate(LocalDateTime.now());
      company.setUpdatedUserId(userId);
      final Optional<Company> optional = this.companyDao.findById(company.getId());
      if (optional.isEmpty()) {
        throw new RuntimeException("company not exist");
      }
      company.setName(optional.get().getName());
      company.setShortName(optional.get().getShortName());
    }

    CompanyService.LOGGER.info("save or update company object -> already checked company value, ready save");

    this.companyDao.save(company);
  }

  @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
  public PaginationInfo<Company> search(final SearchCompany searchCompany, final String companyId, final boolean allowNotCompanyCond) {
    Objects.requireNonNull(searchCompany, "parameter 'searchGroup' must not be null");
    searchCompany.setName(StringUtils.wrapIfMissing(StringUtils.defaultIfBlank(searchCompany.getName(), null), "%"));
    searchCompany.setLegalPerson(StringUtils.wrapIfMissing(StringUtils.defaultIfBlank(searchCompany.getLegalPerson(), null), "%"));
    searchCompany.setShortName(StringUtils.wrapIfMissing(StringUtils.defaultIfBlank(searchCompany.getShortName(), null), "%"));

    Pageable pageable = Pageable.unpaged();
    if (searchCompany.isPageable()) {
      final List<Order> orderList = PaginationSortUtil.transformToOrder(searchCompany.getSort());
      pageable = PageRequest.of(searchCompany.getPageNumber() - 1, searchCompany.getPageSize(), Sort.by(orderList));
    }

    CompanyService.LOGGER.info("search company, current{} contain pagination information", pageable.isPaged() ? "" : " does not");
    CompanyService.LOGGER.info("search condition, {}", searchCompany.toString());

    final Page<Company> result = this.companyDao.search(searchCompany, companyId, allowNotCompanyCond, pageable).map(o -> {
      final User createdUser = new User();
      final User updatedUser = new User();
      final Transform4<Company, PasswordPolicy, User, User> transform = ObjectsToEntityUtil.getTransform4(o);
      BeanUtils.copyProperties(Optional.ofNullable(transform.getT3()).orElse(new User()), createdUser);
      BeanUtils.copyProperties(Optional.ofNullable(transform.getT4()).orElse(new User()), updatedUser);

      final Company company = transform.getT1();
      company.setPasswordPolicy(transform.getT2());
      company.setCreatedUser(createdUser);
      company.setUpdatedUser(updatedUser);
      return company;
    });
    final PaginationInfo<Company> paginationInfo = PaginationInfo.of(searchCompany.getPageNumber(), searchCompany.getPageSize(), searchCompany.isPageable(),
      result.getContent(), result.getTotalElements());
    if (pageable.isUnpaged()) {
      final List<Company> dataList = new ArrayList<>(paginationInfo.getDataList());
      Collections.sort(dataList, this::sort);
      paginationInfo.setDataList(dataList);
    }
    return paginationInfo;
  }

  private int sort(final Company o1, final Company o2) {
    final String s1 = Optional.ofNullable(o1).map(Company::getName).orElse("");
    final String s2 = Optional.ofNullable(o2).map(Company::getName).orElse("");
    return s1.compareTo(s2);
  }
}
