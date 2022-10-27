package com.una.system.manager.utils;

import com.una.common.Constants;
import com.una.common.pojo.PaginationInfo.Sort;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Sort.Order;

public final class PaginationSortUtil {
  private static final List<String> DESC_LIST = Arrays
      .asList(StringUtils.split(Constants.SORT_DESC, ",")).stream().filter(StringUtils::isNotBlank)
      .collect(Collectors.toList());
  private static final List<String> ASC_LIST = Arrays
      .asList(StringUtils.split(Constants.SORT_ASC, ",")).stream().filter(StringUtils::isNotBlank)
      .collect(Collectors.toList());

  public static List<Order> transformToOrder(List<Sort> sortList) {
    sortList = Optional.ofNullable(sortList).orElse(List.of());
    final List<Order> orderList = new ArrayList<>();
    final Stream<Sort> stream = sortList.stream()
        .filter(s -> StringUtils.isNoneBlank(s.getValue()));
    stream.forEach(s -> {
      if (StringUtils.equalsAnyIgnoreCase(s.getValue(),
          PaginationSortUtil.DESC_LIST.toArray(new String[] {}))) {
        orderList.add(Order.desc(s.getKey()));
      } else if (StringUtils.equalsAnyIgnoreCase(s.getValue(),
          PaginationSortUtil.ASC_LIST.toArray(new String[] {}))) {
        orderList.add(Order.asc(s.getKey()));
      }
    });
    return orderList;
  }
}
