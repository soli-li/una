package com.una.system.gateway.security;

import com.una.common.utils.LogUtil;
import com.una.system.gateway.security.authentication.AuthenticationDetail;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.server.RequestPath;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.server.authentication.ServerAuthenticationConverter;
import org.springframework.util.CollectionUtils;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public class AuthenticationConverter implements ServerAuthenticationConverter {

  private static final Logger LOGGER = LogUtil.getRunLogger();
  private final String formLoginUrl;
  private final String usernameParameter;
  private final String passwordParameter;
  private final String captchaParameter;
  private final String method;
  private final String encoding;

  public AuthenticationConverter(final String formLoginUrl, final String usernameParameter,
      final String passwordParameter, final String captchaParameter, final String method,
      final String encoding) {
    this.formLoginUrl = formLoginUrl;
    this.usernameParameter = usernameParameter;
    this.passwordParameter = passwordParameter;
    this.captchaParameter = captchaParameter;
    this.method = method;
    this.encoding = encoding;
  }

  @Override
  public Mono<Authentication> convert(final ServerWebExchange exchange) {
    final ServerHttpRequest request = exchange.getRequest();
    final HttpHeaders headers = request.getHeaders();
    final RequestPath path = request.getPath();
    final MediaType contentType = headers.getContentType();
    final String type = String.format("%s/%s", contentType.getType(), contentType.getSubtype());
    final Charset charset = contentType.getCharset();
    final String encoding = Objects.isNull(charset) ? this.encoding : charset.displayName();

    final List<Boolean> xWwwFormUrlencodedList = new ArrayList<>();
    xWwwFormUrlencodedList.add(StringUtils.equals(path.value(), this.formLoginUrl));
    xWwwFormUrlencodedList.add(StringUtils.equals(request.getMethodValue(), this.method));
    xWwwFormUrlencodedList.add(StringUtils.startsWith(type,
        com.una.common.Constants.MEDIA_TYPE_X_WWW_FORM_URLENCODED.toString()));

    if (BooleanUtils.and(xWwwFormUrlencodedList.toArray(new Boolean[] {}))) {
      final Flux<DataBuffer> body = exchange.getRequest().getBody();
      return body.as(bodyData -> {
        final Mono<List<DataBuffer>> monoDataList = bodyData.collect(Collectors.toList());
        return monoDataList.map(o -> this.generateAuthentication(o, encoding));
      });
    }
    return Mono.empty();
  }

  private Authentication generateAuthentication(final List<DataBuffer> dataList,
      final String encoding) {
    String username = "";
    String password = "";
    String captcha = "";
    AuthenticationDetail detail = new AuthenticationDetail(captcha);
    UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(username,
        password);
    auth.setDetails(detail);
    if (CollectionUtils.isEmpty(dataList)) {
      return auth;
    }
    String data = "";
    for (final DataBuffer dataBuffer : dataList) {
      data = this.readByte(dataBuffer, encoding);
      if (StringUtils.isNotBlank(data)) {
        break;
      }
    }

    if (StringUtils.isBlank(data)) {
      return auth;
    }

    final String[] dataParameter = StringUtils.split(data, "&");
    final Map<String, String> paramMap = this.parseParam(dataParameter, encoding);

    username = paramMap.getOrDefault(this.usernameParameter, "");
    password = paramMap.getOrDefault(this.passwordParameter, "");
    captcha = paramMap.getOrDefault(this.captchaParameter, "");
    detail = new AuthenticationDetail(captcha);
    auth = new UsernamePasswordAuthenticationToken(username, password);
    auth.setDetails(detail);
    return auth;
  }

  private Map<String, String> parseParam(final String[] dataParameter, final String encoding) {
    final Map<String, String> map = new HashMap<>();
    final String equalSign = "=";
    for (final String param : dataParameter) {
      final String[] keyValue = StringUtils.split(param, equalSign, 2);
      if (keyValue.length == 2) {
        try {
          map.put(URLDecoder.decode(keyValue[0], encoding),
              URLDecoder.decode(keyValue[1], encoding));
        } catch (final UnsupportedEncodingException e) {
          AuthenticationConverter.LOGGER.error("parse kev value exception, key value: {}", keyValue,
              e);
        }
      }
    }
    return map;
  }

  private String readByte(final DataBuffer dataBuffer, final String encoding) {
    final int length = dataBuffer.readableByteCount();
    final byte[] buff = new byte[length];
    dataBuffer.read(buff);
    try {
      return new String(buff, encoding);
    } catch (final UnsupportedEncodingException e) {
      AuthenticationConverter.LOGGER.error("parse dataBuffer exception.", e);
      return "";
    }
  }

}
