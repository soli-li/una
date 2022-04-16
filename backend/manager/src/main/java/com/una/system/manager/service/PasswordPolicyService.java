package com.una.system.manager.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import com.una.common.utils.LogUtil;
import com.una.system.manager.dao.PasswordPolicyDao;
import com.una.system.manager.model.PasswordPolicy;

@Service
public class PasswordPolicyService {
  private static final Logger LOGGER = LogUtil.getRunLogger();
  @Autowired
  private PasswordPolicyDao passwordPolicyDao;
  @Autowired
  private GenerateService generateService;

  @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
  public Set<PasswordPolicy> all() {
    final List<PasswordPolicy> result = new ArrayList<>();
    final Iterable<PasswordPolicy> iter = this.passwordPolicyDao.findAll();
    iter.forEach(result::add);
    Collections.sort(result, Comparator.comparing(PasswordPolicy::getLabel));
    return new LinkedHashSet<>(result);
  }

  @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
  public Optional<PasswordPolicy> findById(final String passwordPolicyId) {
    Assert.hasText(passwordPolicyId, "parameter 'passwordPolicyId' must not be empty");
    return this.passwordPolicyDao.findById(passwordPolicyId);
  }

  @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
  public void save(final PasswordPolicy passwordPolicy, final String userId) {
    Assert.notNull(passwordPolicy, "parameter 'passwordPolicy' must not be null");
    Assert.hasText(userId, "parameter 'userId' must not be empty");
    if (StringUtils.isBlank(passwordPolicy.getLabel())) {
      throw new RuntimeException("label must not be empty");
    }

    if (StringUtils.isBlank(passwordPolicy.getId())) {
      passwordPolicy.setId(this.generateService.generateId());
      passwordPolicy.setCreatedUserId(userId);
      passwordPolicy.setCreatedDate(LocalDateTime.now());
    }
    else {
      passwordPolicy.setUpdatedUserId(userId);
      passwordPolicy.setUpdatedDate(LocalDateTime.now());
    }
    passwordPolicy.setDescription(StringUtils.defaultString(passwordPolicy.getDescription(), "").replaceAll("\t", ""));
    PasswordPolicyService.LOGGER.info("save or update password policy object -> already checked password policy value, ready save");

    this.passwordPolicyDao.save(passwordPolicy);
  }
}
