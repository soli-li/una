package com.una.common.pojo;

public class EventMessage<T> {
  private String srouce;
  private String type;
  private T body;

  public T getBody() {
    return this.body;
  }

  public String getSrouce() {
    return this.srouce;
  }

  public String getType() {
    return this.type;
  }

  public void setBody(final T body) {
    this.body = body;
  }

  public void setSrouce(final String srouce) {
    this.srouce = srouce;
  }

  public void setType(final String type) {
    this.type = type;
  }

}
