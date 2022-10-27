package com.una.system.manager.config;

import com.una.system.manager.config.interceptor.NormalUriInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@ComponentScan(basePackageClasses = { SpringMvcInit.class },
    lazyInit = false)
public class SpringMvcInit implements WebMvcConfigurer {
  @Autowired
  @Qualifier("normalUriInterceptor")
  private NormalUriInterceptor normalUriInterceptor;

  /**
   * 设置拦截器
   */
  @Override
  public void addInterceptors(final InterceptorRegistry registry) {
    registry.addInterceptor(this.normalUriInterceptor).addPathPatterns("/**")
        .order(Integer.MIN_VALUE);
    WebMvcConfigurer.super.addInterceptors(registry);
  }
}
