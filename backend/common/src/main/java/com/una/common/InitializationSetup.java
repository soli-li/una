package com.una.common;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import org.slf4j.Logger;
import org.springframework.boot.Banner.Mode;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.context.ApplicationPidFileWriter;
import org.springframework.boot.context.event.ApplicationEnvironmentPreparedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.core.env.Environment;

import com.una.common.utils.LogUtil;

public class InitializationSetup {
  private static class InitializerEnvironmentPreparedListener implements ApplicationListener<ApplicationEnvironmentPreparedEvent> {
    @Override
    public void onApplicationEvent(final ApplicationEnvironmentPreparedEvent event) {
      final Environment environment = event.getEnvironment();
      // System.out.println/System.err.println等等重定向，true只打印到控制台，false会根据日志配置输出
      final boolean outConsole = Boolean.parseBoolean(environment.getProperty(InitializationSetup.OUT_CONSOLE_KEY)); // 是否输出到控制台
      System.setOut(new ConsolePrintStream(System.out, false, outConsole));
      System.setErr(new ConsolePrintStream(System.err, true, outConsole));

      final Logger runLogger = LogUtil.getRunLogger();
      for (final String info : InitializationSetup.beforeInitializeInformation) {
        runLogger.info(info);
      }

    }
  }

  private static final String OUT_CONSOLE_KEY = "app.stdout.printToConsole"; // system.out/err 是否输出到控制台
  private static List<String> beforeInitializeInformation = new LinkedList<>();

  public static SpringApplication getSpringApplication(final String[] args, final Collection<Class<?>> additionalPrimarySources) {
    final SpringApplication application = new SpringApplication();
    application.addPrimarySources(additionalPrimarySources);
    application.setBannerMode(Mode.OFF);
    application.addListeners(new InitializerEnvironmentPreparedListener());
    application.addListeners(new ApplicationPidFileWriter());
    return application;
  }
}
