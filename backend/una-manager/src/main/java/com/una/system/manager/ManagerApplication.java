package com.una.system.manager;

import com.una.common.InitializationSetup;
import com.una.common.utils.LogUtil;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import org.slf4j.Logger;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ManagerApplication {
  public static void main(final String[] args) throws Exception {
    final SpringApplication application = InitializationSetup.getSpringApplication(args,
        List.of(ManagerApplication.class));
    application.run(args);
    final Logger runLogger = LogUtil.getRunLogger();
    runLogger.info("app startup successfully, now is {}",
        LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS")));
  }
}
