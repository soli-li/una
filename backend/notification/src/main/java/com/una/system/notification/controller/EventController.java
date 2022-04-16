package com.una.system.notification.controller;

import org.springframework.stereotype.Controller;
import org.springframework.util.MimeTypeUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.una.common.pojo.EventMessage;

@Controller
@RequestMapping("event")
public class EventController {
  @RequestMapping(value = "receive", method = RequestMethod.PUT, consumes = MimeTypeUtils.APPLICATION_JSON_VALUE)
  @ResponseBody
  public <T> void receive(@RequestBody final EventMessage<T> eventMessage) {
    System.out.println(eventMessage.getBody());
  }
}
