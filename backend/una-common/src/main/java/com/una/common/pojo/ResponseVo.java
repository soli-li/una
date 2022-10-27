package com.una.common.pojo;

public class ResponseVo<T> {
  private String code = "0";
  private String msg;
  private T data;

  public String getCode() {
    return this.code;
  }

  public T getData() {
    return this.data;
  }

  public String getMsg() {
    return this.msg;
  }

  public void setCode(final String code) {
    this.code = code;
  }

  public void setData(final T data) {
    this.data = data;
  }

  public void setMsg(final String msg) {
    this.msg = msg;
  }
}
