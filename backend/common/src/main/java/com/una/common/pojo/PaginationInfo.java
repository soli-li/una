package com.una.common.pojo;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import com.una.common.Constants;

public class PaginationInfo<T> {

  public static class Sort {
    private String key;
    private String value = "asc";

    public String getKey() {
      return this.key;
    }

    public String getValue() {
      return this.value;
    }

    public void setKey(final String key) {
      this.key = key;
    }

    public void setValue(final String value) {
      this.value = value;
    }
  }

  public static <E> PaginationInfo<E> of(final int pageNumber, final int pageSize, final boolean pageable, final List<E> dataList, final long total) {
    if (pageable) {
      return PaginationInfo.of(pageNumber, pageSize, dataList, total);
    }
    else {
      return PaginationInfo.of(dataList);
    }
  }

  private static <E> PaginationInfo<E> of(final int pageNumber, final int pageSize, List<E> dataList, final long total) {
    dataList = Optional.ofNullable(dataList).orElse(List.of());
    final PaginationInfo<E> paginationInfo = new PaginationInfo<>();

    long totalPages = 0;
    long remainder = 0;
    if (pageSize != 0) {
      totalPages = total / pageSize;
      remainder = total % pageSize;
    }
    if (remainder > 0) {
      totalPages = totalPages + 1;
    }
    final boolean isFirst = pageNumber == 1;
    final boolean isLast = totalPages == pageNumber;

    paginationInfo.setPageNumber(pageNumber);
    paginationInfo.setPageSize(pageSize);
    paginationInfo.setDataList(dataList);
    paginationInfo.setTotal(total);
    paginationInfo.setFirst(isFirst);
    paginationInfo.setLast(isLast);
    paginationInfo.setEmpty(dataList.size() == 0);
    paginationInfo.setTotalPages(totalPages);
    paginationInfo.setPageable(true);

    return paginationInfo;
  }

  private static <E> PaginationInfo<E> of(List<E> dataList) {
    dataList = Optional.ofNullable(dataList).orElse(List.of());
    final PaginationInfo<E> paginationInfo = new PaginationInfo<>();

    paginationInfo.setPageNumber(1);
    paginationInfo.setPageSize(dataList.size());
    paginationInfo.setDataList(dataList);
    paginationInfo.setTotal(dataList.size());
    paginationInfo.setFirst(true);
    paginationInfo.setLast(true);
    paginationInfo.setEmpty(dataList.size() == 0);
    paginationInfo.setTotalPages(1);
    paginationInfo.setPageable(false);

    return paginationInfo;
  }

  private boolean pageable = true;
  private int pageNumber = Constants.PAGE_NUMBER;
  private int pageSize = Constants.PAGE_SIZE;
  private List<T> dataList;
  private long total;
  private boolean first;
  private boolean last;
  private boolean empty;
  private long totalPages;
  private List<Sort> sort;

  private final StringBuilder buff = new StringBuilder();

  protected void appendValue() {
    this.buffStr("pageable", this.isPageable());
    this.buffStr("pageNumber", this.getPageNumber());
    this.buffStr("pageSize", this.getPageSize());
    this.buffStr("total", this.getTotal());
    this.buffStr("first", this.isFirst());
    this.buffStr("last", this.isLast());
    this.buffStr("empty", this.isEmpty());
    this.buffStr("totalPages", this.getTotalPages());

    final StringBuilder buff = new StringBuilder();
    buff.append("[");
    List<Sort> sortList = this.getSort();
    sortList = sortList == null ? List.of() : sortList;
    for (final Sort sort : sortList) {
      final String key = sort.getKey();
      final String value = sort.getValue();
      if (Objects.nonNull(key) && !"".equals(key.trim())) {
        buff.append("\"").append(key).append("\": ").append("\"").append(value).append("\"");
      }
    }
    buff.append("]");

    this.buff.append(", \"sort\": ").append(buff.toString());
  }

  protected PaginationInfo<T> buffStr(final String name, final Object value) {
    if (this.buff.length() > 0) {
      this.buff.append(", ");
    }
    this.buff.append("\"").append(name).append("\"").append(": ");
    if (value instanceof String) {
      this.buff.append("\"").append(value).append("\"");
    }
    else if (value instanceof List) {
      final List<?> list = (List<?>) value;
      this.buff.append(this.buffStrByList(list));
    }
    else if (value instanceof Map) {
      final Map<?, ?> map = (Map<?, ?>) value;
      this.buff.append(this.buffStrByMap(map));
    }
    else {
      this.buff.append(value);
    }
    return this;
  }

  protected <I> String buffStrByList(final List<I> list) {
    if (Objects.isNull(list)) {
      return "null";
    }
    final StringBuilder buff = new StringBuilder();
    buff.append("[");
    String delimiter = "";
    for (final I i : list) {
      buff.append(delimiter);
      if (Objects.isNull(i)) {
        buff.append("null");
      }
      else {
        buff.append("\"").append(i.toString()).append("\"");
      }
      delimiter = ", ";
    }
    buff.append("]");
    return buff.toString();
  }

  protected <K, V> String buffStrByMap(final Map<K, V> map) {
    if (Objects.isNull(map)) {
      return "null";
    }
    final StringBuilder buff = new StringBuilder();
    buff.append("{");
    String delimiter = "";
    for (final Map.Entry<K, V> entry : map.entrySet()) {
      buff.append(delimiter);
      final K key = entry.getKey();
      final V value = entry.getValue();
      if (Objects.isNull(key) || "".equals(key.toString().trim())) {
        continue;
      }
      buff.append("\"").append(key).append("\": ");
      if (Objects.isNull(value)) {
        buff.append("null");
      }
      else {
        buff.append("\"").append(value.toString()).append("\"");
      }
      delimiter = ", ";
    }
    buff.append("}");
    return buff.toString();
  }

  public List<T> getDataList() {
    return this.dataList;
  }

  public int getPageNumber() {
    return this.pageNumber;
  }

  public int getPageSize() {
    return this.pageSize;
  }

  public List<Sort> getSort() {
    return this.sort;
  }

  public long getTotal() {
    return this.total;
  }

  public long getTotalPages() {
    return this.totalPages;
  }

  public boolean isEmpty() {
    return this.empty;
  }

  public boolean isFirst() {
    return this.first;
  }

  public boolean isLast() {
    return this.last;
  }

  public boolean isPageable() {
    return this.pageable;
  }

  public void setDataList(final List<T> dataList) {
    this.dataList = dataList;
  }

  public void setEmpty(final boolean empty) {
    this.empty = empty;
  }

  public void setFirst(final boolean first) {
    this.first = first;
  }

  public void setLast(final boolean last) {
    this.last = last;
  }

  public void setPageable(final boolean pageable) {
    this.pageable = pageable;
  }

  public void setPageNumber(final int pageNumber) {
    this.pageNumber = pageNumber;
  }

  public void setPageSize(final int pageSize) {
    this.pageSize = pageSize;
  }

  public void setSort(final List<Sort> sort) {
    this.sort = sort;
  }

  public void setTotal(final long total) {
    this.total = total;
  }

  public void setTotalPages(final long totalPages) {
    this.totalPages = totalPages;
  }

  @Override
  public String toString() {
    this.appendValue();

    final String s = "{\"%s\": {";
    this.buff.insert(0, String.format(s, this.getClass().getSimpleName()));
    this.buff.append("}}");
    return this.buff.toString();
  }

}
