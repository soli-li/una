package com.una.system.manager.config.interceptor;

import java.nio.charset.Charset;
import java.util.Objects;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.util.MimeTypeUtils;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

@ControllerAdvice
@RefreshScope
public class ResponseHeaderInterceptor implements ResponseBodyAdvice<Object> {

  @Value("${app.encoding:UTF-8}")
  private String encoding;

  @Override
  public Object beforeBodyWrite(final Object body, final MethodParameter returnType, final MediaType selectedContentType,
    final Class<? extends HttpMessageConverter<?>> selectedConverterType, final ServerHttpRequest request, final ServerHttpResponse response) {
    final String mimeType = String.format("%s/%s", selectedContentType.getType(), selectedContentType.getSubtype());
    final Charset charset = selectedContentType.getCharset();
    if (StringUtils.equalsAny(mimeType, MimeTypeUtils.TEXT_PLAIN_VALUE, MimeTypeUtils.APPLICATION_JSON_VALUE) && Objects.isNull(charset)) {

      final MediaType mediaType = MediaType.parseMediaType(String.format("%s;charset=%s", mimeType, this.encoding));
      response.getHeaders().setContentType(mediaType);
    }

    return body;
  }

  @Override
  public boolean supports(final MethodParameter returnType, final Class<? extends HttpMessageConverter<?>> converterType) {
    return true;
  }

}
