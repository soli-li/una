package com.una.system.manager.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

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
import com.una.common.utils.ObjectsToEntityUtil.Transform3;
import com.una.system.manager.dao.ConfigurationDao;
import com.una.system.manager.microservices.SharingService;
import com.una.system.manager.model.Company;
import com.una.system.manager.model.Configuration;
import com.una.system.manager.model.User;
import com.una.system.manager.pojo.req.SearchCompany;
import com.una.system.manager.pojo.req.SearchConf;
import com.una.system.manager.utils.PaginationSortUtil;

@Service
public class ConfigurationService {
  private static final Logger LOGGER = LogUtil.getRunLogger();
  @Autowired
  private ConfigurationDao configurationDao;
  @Autowired
  private GenerateService generateService;
  @Autowired
  private SharingService sharingService;
  @Autowired
  private UserService userService;
  @Autowired
  private CompanyService companyService;

  @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
  public void refresh() {
    final SearchConf searchConf = new SearchConf();
    searchConf.setPageable(false);
    final PaginationInfo<Configuration> paginationInfo = this.search(searchConf, "xxx", true);
    final List<Configuration> configList = Optional.ofNullable(paginationInfo.getDataList()).orElse(List.of());
    final Map<String, List<Configuration>> configMap = new HashMap<>();
    configList.forEach(c -> {
      c.setCreatedUser(null);
      c.setCompany(null);

      final List<Configuration> list = configMap.getOrDefault(c.getCompanyId(), new ArrayList<>());
      list.add(c);
      configMap.put(c.getCompanyId(), list);
    });

    final List<String> allConfigurationList = Optional.ofNullable(this.sharingService.getAllConfiguration()).orElse(List.of());
    allConfigurationList.forEach(companyId -> {
      this.sharingService.removeConfiguration(companyId);
      if (configMap.containsKey(companyId)) {
        this.sharingService.saveConfigurationList(companyId, configMap.get(companyId));
        configMap.remove(companyId);
      }
      else {
        this.sharingService.removeConfiguration(companyId);
      }
    });

    configMap.forEach((k, v) -> this.sharingService.saveConfigurationList(k, v));
  }

  @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
  public void save(final Configuration configuration, final String userId) {
    Objects.requireNonNull(configuration, "parameter 'configuration' must not be empty");
    Assert.hasText(userId, "parameter 'userId' must not be empty");

    Assert.isTrue(StringUtils.equalsAny(configuration.getStatus(), Constants.ENABLE, Constants.DISABLE), "incorrect status value");
    if (StringUtils.isAnyBlank(configuration.getConfKey(), configuration.getCompanyId())) {
      throw new RuntimeException("conf key and company id must not be empty");
    }

    if (StringUtils.isBlank(configuration.getId())) {
      configuration.setId(this.generateService.generateId());
      configuration.setCreatedUserId(userId);
      configuration.setCreatedDate(LocalDateTime.now());

      final SearchConf searchConf = new SearchConf();
      searchConf.setEqConfKey(configuration.getConfKey());
      searchConf.setCompanyId(configuration.getCompanyId());
      if (!this.search(searchConf, searchConf.getCompanyId(), true).isEmpty()) {
        throw new RuntimeException("conf key exist");
      }
    }
    else {
      final Optional<Configuration> optional = this.configurationDao.findById(configuration.getId());
      if (optional.isEmpty()) {
        throw new RuntimeException("configuration not exist");
      }
      configuration.setCompanyId(optional.get().getCompanyId());
    }

    ConfigurationService.LOGGER.info("save or update configuration object -> already checked configuration value, ready save");

    this.configurationDao.save(configuration);
  }

  @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
  public PaginationInfo<Configuration> search(final SearchConf searchConf, final String companyId, final boolean allowNotCompanyCond) {
    Assert.isTrue(Objects.nonNull(searchConf), "parameter 'searchConf' must not be empty");
    Assert.isTrue(StringUtils.isNotBlank(companyId), "parameter 'companyId' must not be empty");

    searchConf.setName(StringUtils.wrapIfMissing(StringUtils.defaultIfBlank(searchConf.getName(), null), "%"));
    searchConf.setConfKey(StringUtils.wrapIfMissing(StringUtils.defaultIfBlank(searchConf.getConfKey(), null), "%"));
    searchConf.setCompanyName(StringUtils.wrapIfMissing(StringUtils.defaultIfBlank(searchConf.getCompanyName(), null), "%"));

    Pageable pageable = Pageable.unpaged();
    if (searchConf.isPageable()) {
      final List<Order> orderList = PaginationSortUtil.transformToOrder(searchConf.getSort());
      pageable = PageRequest.of(searchConf.getPageNumber() - 1, searchConf.getPageSize(), Sort.by(orderList));
    }

    ConfigurationService.LOGGER.info("search user, current{} contain pagination information", pageable.isPaged() ? "" : " does not");
    ConfigurationService.LOGGER.info("search condition, {}", searchConf.toString());

    final Page<Configuration> result = this.configurationDao.search(searchConf, companyId, allowNotCompanyCond, pageable).map(o -> {
      final User createdUser = new User();
      final Transform3<Configuration, Company, User> transform = ObjectsToEntityUtil.getTransform3(o);
      BeanUtils.copyProperties(Optional.ofNullable(transform.getT3()).orElse(new User()), createdUser);

      final Configuration configuration = transform.getT1();
      configuration.setCompany(transform.getT2());
      configuration.setCreatedUser(createdUser);

      return configuration;
    });

    final PaginationInfo<Configuration> paginationInfo = PaginationInfo.of(searchConf.getPageNumber(), searchConf.getPageSize(), searchConf.isPageable(),
      result.getContent(), result.getTotalElements());
    if (pageable.isUnpaged()) {
      final List<Configuration> dataList = new ArrayList<>(paginationInfo.getDataList());
      Collections.sort(dataList, this::sort);
      paginationInfo.setDataList(dataList);
    }
    return paginationInfo;
  }

  private int sort(final Configuration o1, final Configuration o2) {
    return StringUtils.defaultIfBlank(o1.getName(), "").compareTo(StringUtils.defaultString(o2.getName(), ""));
  }

  @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
  public void syncFormManage(final String userId) {
    final User user = this.userService.findById(userId).get();
    final SearchConf searchConf = new SearchConf();
    searchConf.setPageable(false);

    final List<Configuration> allConfList = Optional.ofNullable(this.search(searchConf, user.getCompanyId(), true).getDataList()).orElse(List.of());
    final Map<String, List<Configuration>> configMap = new HashMap<>();
    for (final Configuration configuration : allConfList) {
      final List<Configuration> list = configMap.getOrDefault(configuration.getCompanyId(), new ArrayList<>());
      list.add(configuration);
      configMap.put(configuration.getCompanyId(), list);
    }
    final SearchCompany searchCompany = new SearchCompany();
    searchCompany.setPageable(false);
    final List<Company> companyList = Optional.ofNullable(this.companyService.search(searchCompany, user.getCompanyId(), true).getDataList()).orElse(List.of());
    companyList.forEach(c -> {
      if (!configMap.containsKey(c.getId())) {
        configMap.put(c.getId(), new ArrayList<>());
      }
    });

    final Map<String, Configuration> manageConfigMap = Optional.ofNullable(configMap.remove(user.getCompanyId())).orElse(List.of()).stream()
      .collect(Collectors.toMap(Configuration::getConfKey, c -> c));
    for (final Map.Entry<String, List<Configuration>> entry : configMap.entrySet()) {
      final Map<String, Configuration> companyConfigMap = Optional.ofNullable(entry.getValue()).orElse(List.of()).stream()
        .collect(Collectors.toMap(Configuration::getConfKey, c -> c));
      final Map<String, Configuration> tempMap = new HashMap<>(manageConfigMap);
      companyConfigMap.keySet().forEach(tempMap::remove);
      tempMap.values().forEach(c -> {
        final Configuration newConfiguration = new Configuration();
        BeanUtils.copyProperties(c, newConfiguration);
        newConfiguration.setId(null);
        newConfiguration.setCompanyId(entry.getKey());
        newConfiguration.setCreatedUserId(userId);
        newConfiguration.setCreatedDate(LocalDateTime.now());
        this.save(newConfiguration, userId);
      });
    }
  }
}
