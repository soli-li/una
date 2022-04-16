package com.una.common.utils;

import java.util.Objects;

public final class ObjectsToEntityUtil {
  public static class Transform2<T1, T2> {
    private final Object[] objs;

    private final int legnth = 2;

    private Transform2(final Object[] objs) {
      this.objs = objs;
    }

    public int getLength() {
      return this.legnth;
    }

    public Object[] getObjs() {
      return this.objs;
    }

    public T1 getT1() {
      @SuppressWarnings("unchecked")
      final T1 t = (T1) this.getObjs()[0];
      return t;
    }

    public T2 getT2() {
      @SuppressWarnings("unchecked")
      final T2 t = (T2) this.getObjs()[1];
      return t;
    }
  }

  public static class Transform3<T1, T2, T3> extends Transform2<T1, T2> {
    private final int legnth = 3;

    private Transform3(final Object[] objs) {
      super(objs);
    }

    @Override
    public int getLength() {
      return this.legnth;
    }

    public T3 getT3() {
      @SuppressWarnings("unchecked")
      final T3 t = (T3) this.getObjs()[this.legnth - 1];
      return t;
    }
  }

  public static class Transform4<T1, T2, T3, T4> extends Transform3<T1, T2, T3> {
    private final int legnth = 4;

    private Transform4(final Object[] objs) {
      super(objs);
    }

    @Override
    public int getLength() {
      return this.legnth;
    }

    public T4 getT4() {
      @SuppressWarnings("unchecked")
      final T4 t = (T4) this.getObjs()[this.legnth - 1];
      return t;
    }
  }

  public static class Transform5<T1, T2, T3, T4, T5> extends Transform4<T1, T2, T3, T4> {
    private final int legnth = 5;

    private Transform5(final Object[] objs) {
      super(objs);
    }

    @Override
    public int getLength() {
      return this.legnth;
    }

    public T5 getT5() {
      @SuppressWarnings("unchecked")
      final T5 t = (T5) this.getObjs()[this.legnth - 1];
      return t;
    }
  }

  public static class Transform6<T1, T2, T3, T4, T5, T6> extends Transform5<T1, T2, T3, T4, T5> {
    private final int legnth = 6;

    private Transform6(final Object[] objs) {
      super(objs);
    }

    @Override
    public int getLength() {
      return this.legnth;
    }

    public T6 getT6() {
      @SuppressWarnings("unchecked")
      final T6 t = (T6) this.getObjs()[this.legnth - 1];
      return t;
    }
  }

  public static class Transform7<T1, T2, T3, T4, T5, T6, T7> extends Transform6<T1, T2, T3, T4, T5, T6> {
    private final int legnth = 7;

    private Transform7(final Object[] objs) {
      super(objs);
    }

    @Override
    public int getLength() {
      return this.legnth;
    }

    public T7 getT7() {
      @SuppressWarnings("unchecked")
      final T7 t = (T7) this.getObjs()[this.legnth - 1];
      return t;
    }
  }

  public static class Transform8<T1, T2, T3, T4, T5, T6, T7, T8> extends Transform7<T1, T2, T3, T4, T5, T6, T7> {
    private final int legnth = 8;

    private Transform8(final Object[] objs) {
      super(objs);
    }

    @Override
    public int getLength() {
      return this.legnth;
    }

    public T8 getT8() {
      @SuppressWarnings("unchecked")
      final T8 t = (T8) this.getObjs()[this.legnth - 1];
      return t;
    }
  }

  public static class Transform9<T1, T2, T3, T4, T5, T6, T7, T8, T9> extends Transform8<T1, T2, T3, T4, T5, T6, T7, T8> {
    private final int legnth = 9;

    private Transform9(final Object[] objs) {
      super(objs);
    }

    @Override
    public int getLength() {
      return this.legnth;
    }

    public T9 getT9() {
      @SuppressWarnings("unchecked")
      final T9 t = (T9) this.getObjs()[this.legnth - 1];
      return t;
    }
  }

  private static void check(final Object[] objs, final int length) {
    Objects.requireNonNull(objs, "parameter 'objs' must not be null");
    if (objs.length != length) {
      throw new IllegalArgumentException("parameter 'objs' length not match " + length);
    }
  }

  public static <T1, T2> Transform2<T1, T2> getTransform2(final Object[] objs) {
    final Transform2<T1, T2> transform = new Transform2<>(objs);
    ObjectsToEntityUtil.check(objs, transform.getLength());
    return transform;
  }

  public static <T1, T2, T3> Transform3<T1, T2, T3> getTransform3(final Object[] objs) {
    final Transform3<T1, T2, T3> transform = new Transform3<>(objs);
    ObjectsToEntityUtil.check(objs, transform.getLength());
    return transform;
  }

  public static <T1, T2, T3, T4> Transform4<T1, T2, T3, T4> getTransform4(final Object[] objs) {
    final Transform4<T1, T2, T3, T4> transform = new Transform4<>(objs);
    ObjectsToEntityUtil.check(objs, transform.getLength());
    return transform;
  }

  public static <T1, T2, T3, T4, T5> Transform5<T1, T2, T3, T4, T5> getTransform5(final Object[] objs) {
    final Transform5<T1, T2, T3, T4, T5> transform = new Transform5<>(objs);
    ObjectsToEntityUtil.check(objs, transform.getLength());
    return transform;
  }

  public static <T1, T2, T3, T4, T5, T6> Transform6<T1, T2, T3, T4, T5, T6> getTransform6(final Object[] objs) {
    final Transform6<T1, T2, T3, T4, T5, T6> transform = new Transform6<>(objs);
    ObjectsToEntityUtil.check(objs, transform.getLength());
    return transform;
  }

  public static <T1, T2, T3, T4, T5, T6, T7> Transform7<T1, T2, T3, T4, T5, T6, T7> getTransform7(final Object[] objs) {
    final Transform7<T1, T2, T3, T4, T5, T6, T7> transform = new Transform7<>(objs);
    ObjectsToEntityUtil.check(objs, transform.getLength());
    return transform;
  }

  public static <T1, T2, T3, T4, T5, T6, T7, T8> Transform8<T1, T2, T3, T4, T5, T6, T7, T8> getTransform8(final Object[] objs) {
    final Transform8<T1, T2, T3, T4, T5, T6, T7, T8> transform = new Transform8<>(objs);
    ObjectsToEntityUtil.check(objs, transform.getLength());
    return transform;
  }

  public static <T1, T2, T3, T4, T5, T6, T7, T8, T9> Transform9<T1, T2, T3, T4, T5, T6, T7, T8, T9> getTransform9(final Object[] objs) {
    final Transform9<T1, T2, T3, T4, T5, T6, T7, T8, T9> transform = new Transform9<>(objs);
    ObjectsToEntityUtil.check(objs, transform.getLength());
    return transform;
  }
}
