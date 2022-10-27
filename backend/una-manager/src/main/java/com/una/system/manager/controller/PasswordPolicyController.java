package com.una.system.manager.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.una.common.utils.LogUtil;
import com.una.system.manager.model.PasswordPolicy;
import com.una.system.manager.pojo.RequestHeadKey;
import com.una.system.manager.pojo.SpecialId;
import com.una.system.manager.service.PasswordPolicyService;
import com.una.system.manager.utils.RequestUtils;
import java.util.Set;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpStatus;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.MimeTypeUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("pwPolicy")
public class PasswordPolicyController {
  private static final Logger LOGGER = LogUtil.getRunLogger();
  @Autowired
  private ObjectMapper objectMapper;

  @Autowired
  private SpecialId specialId;
  @Autowired
  private RequestHeadKey requestHeadKey;

  @Autowired
  private PasswordPolicyService passwordPolicyService;

  @RequestMapping(value = "all",
      method = RequestMethod.GET)
  @ResponseBody
  public Set<PasswordPolicy> all() {
    return this.passwordPolicyService.all();
  }

  @RequestMapping(value = "save",
      method = RequestMethod.PUT,
      consumes = MimeTypeUtils.APPLICATION_JSON_VALUE)
  @ResponseBody
  public void save(final HttpServletRequest request, final HttpServletResponse response,
      @RequestBody final PasswordPolicy passwordPolicy) throws Exception {
    passwordPolicy.setLabel(StringUtils.trim(passwordPolicy.getLabel()));
    passwordPolicy.setId(StringUtils.trim(passwordPolicy.getId()));

    final boolean isSuperuser = RequestUtils.hasSpecId(
        RequestUtils.getPermIds(request, this.requestHeadKey), this.objectMapper,
        this.specialId.getSuperuserAddData());

    final String userId = RequestUtils.getUserId(request, this.requestHeadKey);
    if (!isSuperuser) {
      PasswordPolicyController.LOGGER.error("user not has permissions, user id: {}", userId);
      response.setStatus(HttpStatus.SC_NOT_FOUND);
      throw new RuntimeException("Not Found");
    }
    this.passwordPolicyService.save(passwordPolicy, userId);
  }
}
