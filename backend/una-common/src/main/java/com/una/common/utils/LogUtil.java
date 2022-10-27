package com.una.common.utils;

import java.util.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class LogUtil {
  /**
   * 输出到不同文件的可选选项
   */
  public enum Options {
    /**
     * 接口类型日志
     */
    INTERFACE,
    /**
     * 操作类型日志
     */
    OPERATE,
    /**
     * 运行时日志
     */
    RUN;
    // /**
    // * 警告日志
    // */
    // WARNING;
  }

  private static final String DEFAULT_LOG_MARK;

  static {
    final String defaultLogMark = "defaultLogMark";
    final String logMarkKey = "app.log.mark";
    final String markValue = System.getProperty(logMarkKey, defaultLogMark);
    DEFAULT_LOG_MARK = markValue;
    final String info = String.format(
        "using system properties key '%s', value is %s, default value is: %s", logMarkKey,
        markValue, defaultLogMark);
    System.out.println(info);
  }

  /**
   * 获取自定义日志文件实现类
   *
   * @param customize 自定义前缀
   *
   * @return 具体的工厂实例
   */
  public static Logger getCustomizeLogger(final String customize) {
    return LogUtil.getCustomizeLogger(customize, "");
  }

  /**
   * 获取自定义日志文件实现类
   *
   * @param customize 自定义前缀
   * @param clazz 类名，兼容以往的模式（后缀）
   *
   * @return 具体的工厂实例
   */
  public static Logger getCustomizeLogger(final String customize, final Class<?> clazz) {
    return LogUtil.getCustomizeLogger(customize, clazz.getName());
  }

  /**
   * 获取自定义日志文件实现类
   *
   * @param customize 自定义前缀
   * @param name 自定义名字（后缀）
   *
   * @return 具体的工厂实例
   */
  public static Logger getCustomizeLogger(final String customize, final String name) {
    return LogUtil.getLogger(String.valueOf(customize).toUpperCase(), name);
  }

  public static String getDefaultLogMark() {
    return LogUtil.DEFAULT_LOG_MARK;
  }

  /**
   * 获取INTERFACE日志文件实现类
   *
   * @return 具体的工厂实例
   */
  public static Logger getInterfaceLogger() {
    return LogUtil.getInterfaceLogger("");
  }

  /**
   * 获取INTERFACE日志文件实现类
   *
   * @param clazz 类名，兼容以往的模式
   *
   * @return 具体的工厂实例
   */
  public static Logger getInterfaceLogger(final Class<?> clazz) {
    return LogUtil.getInterfaceLogger(clazz.getName());
  }

  /**
   * 获取INTERFACE日志文件实现类
   *
   * @param name 自定义名字
   *
   * @return 具体的工厂实例
   */
  public static Logger getInterfaceLogger(final String name) {
    return LogUtil.getLogger(Options.INTERFACE.name(), name);
  }

  /**
   * 获取日志工厂接口的具体实现类
   *
   * @param option 日志类型
   * @param name 后缀名字
   *
   * @return 具体的工厂实例
   */
  private static Logger getLogger(final String option, String name) {
    if (Objects.isNull(name) || "".equals(name.trim())) {
      name = LogUtil.getOriginClassName();
    }
    return LoggerFactory.getLogger(option + "." + name);
  }

  /**
   * 获取OPERATE日志文件实现类
   *
   * @return 具体的工厂实例
   */
  public static Logger getOperateLogger() {
    return LogUtil.getOperateLogger("");
  }

  /**
   * 获取OPERATE日志文件实现类
   *
   * @param clazz 类名，兼容以往的模式
   *
   * @return 具体的工厂实例
   */
  public static Logger getOperateLogger(final Class<?> clazz) {
    return LogUtil.getOperateLogger(clazz.getName());
  }

  /**
   * 获取OPERATE日志文件实现类
   *
   * @param name 自定义名字
   *
   * @return 具体的工厂实例
   */
  public static Logger getOperateLogger(final String name) {
    return LogUtil.getLogger(Options.OPERATE.name(), name);
  }

  /**
   * 获取是哪一个类调用该类的
   *
   * @return 返回类栈中的对象
   */
  private static StackTraceElement getOriginClass() {
    final StackTraceElement[] stackTraceElements = Thread.currentThread().getStackTrace();
    for (final StackTraceElement element : stackTraceElements) {
      final String originClass = element.getClassName();
      if (!Thread.class.getName().equals(originClass)
          && originClass.indexOf(LogUtil.class.getName()) < 0) {
        return element;
      }
    }
    return null;
  }

  /**
   * 获取是哪一个类调用该类的
   *
   * @return 返回类栈中的类名
   */
  private static String getOriginClassName() {
    final StackTraceElement stackTraceElement = LogUtil.getOriginClass();
    if (stackTraceElement == null) {
      return LogUtil.class.getName();
    }
    return stackTraceElement.getClassName();
  }

  /**
   * 获取RUN日志文件实现类
   *
   * @return 具体的工厂实例
   */
  public static Logger getRunLogger() {
    return LogUtil.getRunLogger("");
  }

  /**
   * 获取RUN日志文件实现类
   *
   * @param clazz 类名，兼容以往的模式
   *
   * @return 具体的工厂实例
   */
  public static Logger getRunLogger(final Class<?> clazz) {
    return LogUtil.getRunLogger(clazz.getName());
  }

  /**
   * 获取RUN日志文件实现类
   *
   * @param name 自定义名字
   *
   * @return 具体的工厂实例
   */
  public static Logger getRunLogger(final String name) {
    return LogUtil.getLogger(Options.RUN.name(), name);
  }

  /**
   * 获取WARNING日志文件实现类
   *
   * @return 具体的工厂实例
   */
  // public static Logger getWarningLogger() {
  // return LogUtil.getWarningLogger("");
  // }

  /**
   * 获取WARNING日志文件实现类
   *
   * @param clazz 类名，兼容以往的模式
   *
   * @return 具体的工厂实例
   */
  // public static Logger getWarningLogger(final Class<?> clazz) {
  // return LogUtil.getWarningLogger(clazz.getName());
  // }

  /**
   * 获取WARNING日志文件实现类
   *
   * @param name 自定义名字
   *
   * @return 具体的工厂实例
   */
  // public static Logger getWarningLogger(final String name) {
  // return LogUtil.getLogger(Options.WARNING.name(), name);
  // }

  private LogUtil() {
  }

}
