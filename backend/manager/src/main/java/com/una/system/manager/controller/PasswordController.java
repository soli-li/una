package com.una.system.manager.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.una.common.Constants;
import com.una.system.manager.service.PasswordService;

@Controller
@RequestMapping("password")
public class PasswordController {
  @Autowired
  private PasswordService passwordService;

  @RequestMapping(value = "match", method = RequestMethod.POST, consumes = Constants.MEDIA_TYPE_X_WWW_FORM_URLENCODED)
  @ResponseBody
  public boolean match(@RequestParam("username") final String name, @RequestParam("password") final String password) {
    final String encodedPassword = this.passwordService.getPasswordByUsername(name);
    Assert.hasText(encodedPassword, "cannot find user by username");
    return this.passwordService.match(password, encodedPassword);
  }
}
