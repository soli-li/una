package com.una.system.notification.config.interceptor;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.una.common.Constants;
import com.una.common.pojo.ResponseVo;
import com.una.common.utils.LogUtil;
import java.util.Map;
import java.util.Optional;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.NoHandlerFoundException;

@ControllerAdvice
public class GlobalHandlerExceptionResolver {
  private static class ExceptionResponse extends ResponseVo<Object> {
    private String mark;

    public String getMark() {
      return this.mark;
    }

    public void setMark(final String mark) {
      this.mark = mark;
    }
  }

  private static final Logger LOGGER = LogUtil.getRunLogger();

  @Autowired
  private ObjectMapper objectMapper;

  @ExceptionHandler(Exception.class)
  @ResponseBody
  public String handlerException(final HttpServletRequest request,
      final HttpServletResponse response, final Exception ex) {
    final Map<String, String> contextMap = MDC.getCopyOfContextMap();
    final String marks = Optional.ofNullable(contextMap).orElse(Map.of())
        .get(Constants.LOG_MARK.toString());
    final ExceptionResponse exRes = new ExceptionResponse();

    exRes.setMark(marks);
    exRes.setMsg(ex.getMessage());
    exRes.setCode(Constants.RESP_FAILURE_CODE.toString());
    if (ex.getClass().isAssignableFrom(NoHandlerFoundException.class)) {
      response.setStatus(HttpServletResponse.SC_NOT_FOUND);
    } else if (response.getStatus() == HttpServletResponse.SC_OK) {
      response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
    }

    GlobalHandlerExceptionResolver.LOGGER.error(
        "request exception, uri: {}, marks: {}, ex class: {}", request.getRequestURI(), marks,
        ex.getClass().getName(), ex);
    try {
      final String json = this.objectMapper.writeValueAsString(exRes);
      return json;
    } catch (final JsonProcessingException e) {
      GlobalHandlerExceptionResolver.LOGGER.error(
          "ExceptionResponse cannot serialization, mark: {}, msg: {}", exRes.getMark(),
          exRes.getMsg(), e);
      final String errorMessage = """
          {"msg": "system exception!", "marks": "%s"}
          """;
      return String.format(errorMessage, exRes.getMark());
    }
  }
}
