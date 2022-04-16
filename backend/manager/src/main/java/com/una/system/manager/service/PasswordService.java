package com.una.system.manager.service;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.DelegatingPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.crypto.password.Pbkdf2PasswordEncoder;
import org.springframework.security.crypto.scrypt.SCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import com.una.common.pojo.PaginationInfo;
import com.una.common.utils.BeanPropUtil;
import com.una.common.utils.LogUtil;
import com.una.system.manager.dao.CpRecordDao;
import com.una.system.manager.dao.PasswordDao;
import com.una.system.manager.model.Company;
import com.una.system.manager.model.Configuration;
import com.una.system.manager.model.CpRecord;
import com.una.system.manager.model.PasswordPolicy;
import com.una.system.manager.pojo.req.SearchCompany;
import com.una.system.manager.utils.PaginationSortUtil;

@Service
@RefreshScope
public class PasswordService {
  private static String lettersList = "abcdefghijklmnopqrstuvwxyz";
  private static String uppercaseLettersList = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
  private static String digitalsList = "0123456789";
  private static String nonAlphanumericList = "`~!@#$%^&*()_-={}|[]><";
  private final static Random passwordRandom = new SecureRandom();
  private static final Logger LOGGER = LogUtil.getRunLogger();

  private static final String LENGTH_KEY = "system-config.new-password.length";
  private static final String LETTERS_KEY = "system-config.new-password.letters";
  private static final String DIGITALS_KEY = "system-config.new-password.digitals";
  private static final String CASE_SENSITIVE_KEY = "system-config.new-password.case-sensitive";
  private static final String NON_ALPHANUMERIC_KEY = "system-config.new-password.non-alphanumeric";
  @Value("${app.security.encode:bcrypt}")
  private String idForEncode;

  @Value("${" + PasswordService.LENGTH_KEY + ":8}")
  private int length;
  @Value("${" + PasswordService.LETTERS_KEY + ":true}")
  private boolean letters;
  @Value("${" + PasswordService.DIGITALS_KEY + ":true}")
  private boolean digitals;
  @Value("${" + PasswordService.CASE_SENSITIVE_KEY + ":true}")
  private boolean caseSensitive;
  @Value("${" + PasswordService.NON_ALPHANUMERIC_KEY + ":true}")
  private boolean nonAlphanumeric;

  private PasswordEncoder passwordEncoder;
  private Map<String, PasswordEncoder> encoders;

  @Autowired
  private PasswordDao passwordDao;

  @Autowired
  private CompanyService companyService;

  @Autowired
  private CpRecordDao cpRecordDao;

  @Autowired
  private GenerateService generateService;

  private String conditionJudge(final boolean mustExec, final boolean condition, final String errorMsg) {
    if (!mustExec || condition) {
      return null;
    }
    return errorMsg;
  }

  public String encodePassword(final CharSequence rawPassword) {
    return this.passwordEncoder.encode(rawPassword);
  }

  public String generate(final List<Configuration> configList) {
    final int cLength = BeanPropUtil.getValueForKey(configList, PasswordService.LENGTH_KEY, this.length,
      v -> Integer.parseInt(StringUtils.defaultString(v, "8")));
    final boolean cLetters = BeanPropUtil.getValueForKey(configList, PasswordService.LETTERS_KEY, this.letters, v -> StringUtils.equalsAnyIgnoreCase(v, "Y"));
    final boolean cDigitals = BeanPropUtil.getValueForKey(configList, PasswordService.DIGITALS_KEY, this.digitals,
      v -> StringUtils.equalsAnyIgnoreCase(v, "Y"));
    final boolean cCaseSensitive = BeanPropUtil.getValueForKey(configList, PasswordService.CASE_SENSITIVE_KEY, this.caseSensitive,
      v -> StringUtils.equalsAnyIgnoreCase(v, "Y"));
    final boolean cNonAlphanumeric = BeanPropUtil.getValueForKey(configList, PasswordService.NON_ALPHANUMERIC_KEY, this.nonAlphanumeric,
      v -> StringUtils.equalsAnyIgnoreCase(v, "Y"));

    final List<Character> list = new ArrayList<>();
    if (cCaseSensitive) {
      list.add(this.random(PasswordService.uppercaseLettersList.toCharArray()));
    }
    if (cLetters) {
      String str = PasswordService.lettersList;
      if (!cCaseSensitive) {
        str = PasswordService.lettersList + cCaseSensitive;
      }
      list.add(this.random(str.toCharArray()));
    }
    if (cDigitals) {
      list.add(this.random(PasswordService.digitalsList.toCharArray()));
    }
    if (cNonAlphanumeric) {
      list.add(this.random(PasswordService.nonAlphanumericList.toCharArray()));
    }
    final char[] source = String
      .join("", PasswordService.lettersList, PasswordService.digitalsList, PasswordService.uppercaseLettersList, PasswordService.nonAlphanumericList)
      .toCharArray();
    final int listSize = list.size();
    for (int i = 0; i < cLength - listSize; i++) {
      list.add(this.random(source));
    }
    final StringBuilder buff = new StringBuilder();
    for (int i = 0; i < cLength; i++) {
      final int nextInt = PasswordService.passwordRandom.nextInt(list.size());
      buff.append(list.remove(nextInt));
    }

    return buff.toString();
  }

  public Map<String, PasswordEncoder> getEncoders() {
    return new HashMap<>(this.encoders);
  }

  @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
  public String getPasswordByUsername(final String username) {
    Assert.hasText(username, "parameter 'username' must not be empty");
    return this.passwordDao.getPasswordByUsername(username);
  }

  @PostConstruct
  public void init() {
    this.encoders = new HashMap<>();
    this.encoders.put("bcrypt", new BCryptPasswordEncoder());
    this.encoders.put("pbkdf2", new Pbkdf2PasswordEncoder());
    this.encoders.put("scrypt", new SCryptPasswordEncoder());
    this.encoders.put("argon2", new Argon2PasswordEncoder());

    this.passwordEncoder = new DelegatingPasswordEncoder(this.idForEncode, this.encoders);
  }

  @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
  public boolean isMatchingHistoricalPasswords(final String username, final String password, final int repeatCount) {
    if (repeatCount == 0) {
      return false;
    }
    Assert.hasText(username, "parameter 'userId' must not be null");
    Assert.hasText(password, "parameter 'encodedPassword' must not be null");
    Assert.isTrue(repeatCount > 0, "parameter 'repeatCount' must be greater to 0");

    final com.una.common.pojo.PaginationInfo.Sort sort = new PaginationInfo.Sort();
    sort.setKey("changedDate");
    sort.setValue("desc");
    final PageRequest pageRequest = PageRequest.of(0, repeatCount, Sort.by(PaginationSortUtil.transformToOrder(List.of(sort))));

    final List<CpRecord> recordList = this.cpRecordDao.findByUsername(username, pageRequest);
    return recordList.stream().anyMatch(r -> this.match(password, r.getPassword()));
  }

  @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
  public boolean match(final CharSequence rawPassword, final String encodedPassword) {
    Assert.notNull(rawPassword, "parameter 'rawPassword' must not be null");
    Assert.hasText(encodedPassword, "parameter 'encodedPassword' must not be empty");
    return this.passwordEncoder.matches(rawPassword, encodedPassword);
  }

  @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
  public String matchPasswordStrength(final String username, String password, String companyId) {
    Assert.hasText(companyId, "parameter 'companyId' must not be empty");
    Assert.hasText(password, "parameter 'password' must not be empty");
    password = password.trim();
    companyId = companyId.trim();
    final SearchCompany searchCompany = new SearchCompany();
    searchCompany.setId(companyId);
    final PaginationInfo<Company> result = this.companyService.search(searchCompany, companyId, false);
    if (CollectionUtils.isEmpty(result.getDataList())) {
      throw new RuntimeException("cannot found company, company id: " + companyId);
    }
    final Company company = result.getDataList().get(0);
    final PasswordPolicy passwordPolicy = company.getPasswordPolicy();
    final int length = passwordPolicy.getLength();
    final int repeatCount = passwordPolicy.getRepeatCount();
    final boolean caseSensitive = passwordPolicy.isCaseSensitive();
    final boolean digitals = passwordPolicy.isDigitals();
    final boolean letters = passwordPolicy.isLetters();
    final boolean nonAlphanumeric = passwordPolicy.isNonAlphanumeric();

    List<String> errorList = new ArrayList<>();
    errorList.add(this.conditionJudge(length > 0, password.length() >= length, String.format("\"length\": \"%s\"", length)));
    errorList.add(this.conditionJudge(digitals, StringUtils.containsAny(password, PasswordService.digitalsList),
      String.format("\"digitals\": \"%s\"", PasswordService.digitalsList)));
    errorList.add(this.conditionJudge(nonAlphanumeric, StringUtils.containsAny(password, PasswordService.nonAlphanumericList),
      String.format("\"nonAlphanumeric\": \"%s\"", PasswordService.nonAlphanumericList)));
    errorList.add(this.conditionJudge(caseSensitive, StringUtils.containsAny(password, PasswordService.uppercaseLettersList),
      String.format("\"caseSensitive\": \"%s\"", PasswordService.uppercaseLettersList)));
    String str = PasswordService.lettersList;
    if (!caseSensitive) {
      str = PasswordService.lettersList + PasswordService.uppercaseLettersList;
    }
    errorList.add(this.conditionJudge(letters, StringUtils.containsAny(password, str), String.format("\"letters\": \"%s\"", str)));
    errorList.add(this.conditionJudge(repeatCount > 0, !this.isMatchingHistoricalPasswords(username, password, repeatCount),
      String.format("\"repeatCount\": %s", repeatCount)));
    errorList = errorList.stream().filter(StringUtils::isNotBlank).collect(Collectors.toList());
    if (errorList.isEmpty()) {
      return null;
    }

    errorList.add(String.format("\"description\": \"%s\"", passwordPolicy.getDescription()));
    final String errorContent = String.format("{%s}", String.join(", ", errorList));
    PasswordService.LOGGER.error("cannot pass password strength, {}", errorContent);
    return errorContent;
  }

  private char random(final char[] source) {
    final int nextInt = PasswordService.passwordRandom.nextInt(source.length);
    return source[nextInt];
  }

  @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
  public boolean resetPassword(final String username, final CharSequence rawPassword) {
    Assert.hasText(username, "parameter 'username' must not be empty");
    Assert.notNull(rawPassword, "parameter 'rawPassword' must not be null");
    final String encodedPassword = this.passwordEncoder.encode(rawPassword);
    final int result = this.passwordDao.upgradePassword(username, encodedPassword, LocalDateTime.now());
    final CpRecord cpRecord = new CpRecord();
    cpRecord.setId(this.generateService.generateId());
    cpRecord.setChangedDate(LocalDateTime.now());
    cpRecord.setPassword(encodedPassword);
    cpRecord.setUsername(username);

    this.cpRecordDao.save(cpRecord);
    return result > 0;
  }

  @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
  public boolean resetPassword(final String username, final String currentPassword, final CharSequence rawPassword) {
    final String dbPassword = this.getPasswordByUsername(username);
    final boolean matches = this.match(currentPassword, dbPassword);
    if (!matches) {
      Assert.notNull(null, "current password not matches db password");
    }

    return this.resetPassword(username, rawPassword);
  }

}
