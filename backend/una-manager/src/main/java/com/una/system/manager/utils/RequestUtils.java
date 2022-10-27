package com.una.system.manager.utils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.una.system.manager.pojo.RequestHeadKey;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import javax.servlet.http.HttpServletRequest;
import org.springframework.util.Assert;

public final class RequestUtils {
  public static String getCompanyId(final HttpServletRequest request,
      final RequestHeadKey requestHeadKey) {
    return request.getHeader(requestHeadKey.getCompanyIdKey());
  }

  public static String getPermIds(final HttpServletRequest request,
      final RequestHeadKey requestHeadKey) {
    return request.getHeader(requestHeadKey.getPermIdKey());
  }

  public static String getRequestUuid(final HttpServletRequest request,
      final RequestHeadKey requestHeadKey) {
    return request.getHeader(requestHeadKey.getReqUuidKey());
  }

  public static String getUserId(final HttpServletRequest request,
      final RequestHeadKey requestHeadKey) {
    return request.getHeader(requestHeadKey.getUserIdKey());
  }

  public static boolean hasSpecId(final String permIds, final ObjectMapper objectMapper,
      final String specId) throws Exception {
    Objects.requireNonNull(objectMapper, "parameter 'passwordPolicy' must not be null");
    Assert.hasText(specId, "parameter 'passwordPolicy' must not be empty");
    final TypeReference<Set<String>> typeRef = new TypeReference<>() {
    };
    final Set<String> permIdsSet = objectMapper.readValue(Optional.ofNullable(permIds).orElse("[]"),
        typeRef);
    return permIdsSet.contains(specId);
  }

}
