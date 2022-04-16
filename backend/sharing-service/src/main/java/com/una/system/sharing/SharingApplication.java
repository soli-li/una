package com.una.system.sharing;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.slf4j.Logger;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

import com.una.common.InitializationSetup;
import com.una.common.utils.LogUtil;

@SpringBootApplication(exclude = { SecurityAutoConfiguration.class })
@EnableDiscoveryClient
public class SharingApplication {
  public static void main(final String[] args) throws Exception {
    final SpringApplication application = InitializationSetup.getSpringApplication(args, List.of(SharingApplication.class));
    application.run(args);
    final Logger runLogger = LogUtil.getRunLogger();
    runLogger.info("app startup successfully, now is {}", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS")));
  }
}
