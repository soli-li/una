package com.una.common.utils;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.util.StringUtils;

import com.una.common.Constants;
import com.una.common.pojo.Configuration;

public final class BeanPropUtil {
  @FunctionalInterface
  public interface TransFun<T> {
    T trans(String value);
  }

  public static <T> T getValueForKey(List<? extends Configuration> configList, final String key, final String status, final T defaultValue,
    final TransFun<T> transUtil) {
    if (!StringUtils.hasText(key)) {
      throw new RuntimeException("parameter 'key' must not be empty");
    }
    configList = Optional.ofNullable(configList).orElse(List.of());
    final Stream<? extends Configuration> stream = configList.stream().filter(c -> StringUtils.hasText(c.getStatus()) && c.getStatus().equals(status));
    final Map<String, String> keyValueMap = stream.collect(Collectors.toMap(Configuration::getConfKey, Configuration::getConfValue));
    final String value = keyValueMap.get(key);
    if (!StringUtils.hasText(value) || Objects.isNull(transUtil)) {
      return defaultValue;
    }
    return transUtil.trans(value);
  }

  public static <T> T getValueForKey(final List<? extends Configuration> configList, final String key, final T defaultValue, final TransFun<T> transUtil) {
    return BeanPropUtil.getValueForKey(configList, key, Constants.ENABLE, defaultValue, transUtil);
  }
}
