package com.una.system.gateway.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.una.system.gateway.config.NormalUriWebFilter;
import com.una.system.gateway.security.authentication.AccessAuthorization;
import com.una.system.gateway.security.authentication.AccessDeniedHandler;
import com.una.system.gateway.security.authentication.AuthenticationEntryPoint;
import com.una.system.gateway.security.authentication.LoginAuthentication;
import com.una.system.gateway.security.authentication.LoginResultHandler;
import com.una.system.gateway.security.authentication.LogoutHandle;
import com.una.system.gateway.service.ManagerService;
import com.una.system.gateway.service.SharingService;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.security.reactive.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity.AuthorizeExchangeSpec;
import org.springframework.security.config.web.server.ServerHttpSecurity.ExceptionHandlingSpec;
import org.springframework.security.config.web.server.ServerHttpSecurity.FormLoginSpec;
import org.springframework.security.config.web.server.ServerHttpSecurity.HeaderSpec;
import org.springframework.security.config.web.server.ServerHttpSecurity.LogoutSpec;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.authentication.AuthenticationWebFilter;

@EnableWebFluxSecurity
public class SecurityConfig {
  // @formatter:off
  private static final String MAXIMUM_SESSIONS_KEY =
      "system-config.security.session.maximumSessions";
  private static final String MAX_SESSIONS_PREVENTS_LOGIN_KEY =
      "system-config.security.session.maxSessionsPreventsLogin";
  // @formatter:on
  @Value("${app.security.formLogin.url:/login}")
  private String formLoginUrl;
  @Value("${app.security.formLogin.usernameParameter:username}")
  private String usernameParameter;
  @Value("${app.security.formLogin.passwordParameter:password}")
  private String passwordParameter;
  @Value("${app.security.formLogin.captchaParameter:captcha}")
  private String captchaParameter;
  @Value("${app.security.formLogin.method:POST}")
  private String method;

  @Value("${app.security.uri.ignore:}")
  private List<String> ignore;
  @Value("${app.security.uri.block:}")
  private List<String> block;

  @Value("${app.security.logout.url:/logout}")
  private String logoutUrl;
  @Value("${app.security.auth-header:x-auth-token}")
  private String authHeaderName;
  @Value("${app.request.head.user-id-key:una-internal-user-id-key}")
  private String userIdHeaderName;
  @Value("${app.request.head.company-id-key:una-internal-company-id-key}")
  private String companyIdHeaderName;
  @Value("${app.request.head.perm-id-key:una-internal-perm-id-key}")
  private String permIdHeaderName;

  @Value("${" + SecurityConfig.MAXIMUM_SESSIONS_KEY + ":0}")
  private int maximumSessions;
  @Value("${" + SecurityConfig.MAX_SESSIONS_PREVENTS_LOGIN_KEY + ":true}")
  private boolean maxSessionsPreventsLogin;

  @Value("${app.encoding:UTF-8}")
  private String encoding;

  @Autowired
  private ManagerService managerService;
  @Autowired
  private SharingService sharingService;
  @Autowired
  private ObjectMapper objectMapper;
  @Autowired
  private NormalUriWebFilter normalUriWebFilter;

  @Bean
  public SecurityWebFilterChain securityWebFilterChain(final ServerHttpSecurity httpSecurity) {
    this.setConfigurer(httpSecurity.formLogin());
    this.setConfigurer(httpSecurity.logout());
    this.setConfigurer(httpSecurity.headers());
    this.setConfigurer(httpSecurity.exceptionHandling());
    this.setConfigurer(httpSecurity.authorizeExchange());
    // this.setConfigurer(httpSecurity.redirectToHttps());
    httpSecurity.securityContextRepository(new SecurityContextRepository(this.authHeaderName,
        this.userIdHeaderName, this.companyIdHeaderName, this.sharingService, this.formLoginUrl));

    httpSecurity.csrf().disable();
    httpSecurity.cors().disable();
    httpSecurity.anonymous().disable();
    httpSecurity.httpBasic().disable();

    httpSecurity.addFilterAfter(this.normalUriWebFilter, SecurityWebFiltersOrder.FIRST);

    final AuthenticationConverter authenticationConverter = new AuthenticationConverter(
        this.formLoginUrl, this.usernameParameter, this.passwordParameter, this.captchaParameter,
        this.method, this.encoding);
    final SecurityWebFilterChain securityWebFilterChain = httpSecurity.build();

    securityWebFilterChain.getWebFilters().collectList().subscribe(filters -> {
      filters.forEach(filter -> {
        if (filter instanceof final AuthenticationWebFilter authFilter) {
          authFilter.setServerAuthenticationConverter(authenticationConverter);
        }
      });
    });

    return securityWebFilterChain;
  }

  private void setConfigurer(final AuthorizeExchangeSpec authorizeExchange) {
    final List<String> blockUriList = new ArrayList<>();
    this.block.forEach(s -> {
      if (StringUtils.isNotBlank(s)) {
        final String url = StringUtils.trim(s);
        blockUriList.add(StringUtils.startsWith(url, "/") ? url : "/" + url);
      }
    });
    final List<String> ignoreUriList = new ArrayList<>();
    this.ignore.forEach(s -> {
      if (StringUtils.isNotBlank(s)) {
        final String url = StringUtils.trim(s);
        ignoreUriList.add(StringUtils.startsWith(url, "/") ? url : "/" + url);
      }
    });
    blockUriList.forEach(s -> authorizeExchange.pathMatchers(s).denyAll());
    ignoreUriList.forEach(s -> authorizeExchange.pathMatchers(s).permitAll());
    authorizeExchange.matchers(PathRequest.toStaticResources().atCommonLocations()).permitAll();
    authorizeExchange.pathMatchers("/**").access(
        new AccessAuthorization(this.permIdHeaderName, this.managerService, this.objectMapper));
  }

  private void setConfigurer(final ExceptionHandlingSpec exceptionHandling) {
    exceptionHandling.accessDeniedHandler(new AccessDeniedHandler(this.objectMapper));
    exceptionHandling.authenticationEntryPoint(new AuthenticationEntryPoint(this.objectMapper));
  }

  private void setConfigurer(final FormLoginSpec formLogin) {
    final LoginResultHandler loginResultHandler = new LoginResultHandler(this.encoding,
        this.managerService, this.sharingService, this.authHeaderName);
    formLogin.loginPage(this.formLoginUrl);
    formLogin.authenticationFailureHandler(loginResultHandler);
    formLogin.authenticationSuccessHandler(loginResultHandler);
    formLogin.authenticationManager(new LoginAuthentication(this.managerService,
        this.sharingService, this.maximumSessions, this.maxSessionsPreventsLogin,
        SecurityConfig.MAXIMUM_SESSIONS_KEY, SecurityConfig.MAX_SESSIONS_PREVENTS_LOGIN_KEY));
  }

  private void setConfigurer(final HeaderSpec headers) {
    // headers.frameOptions().sameOrigin();
  }

  // private void setConfigurer(final HttpsRedirectSpec redirectToHttps) {
  // }

  private void setConfigurer(final LogoutSpec logout) {
    final LogoutHandle logoutHandle = new LogoutHandle(this.authHeaderName, this.sharingService,
        this.encoding);
    logout.logoutUrl(this.logoutUrl);
    logout.logoutHandler(logoutHandle);
    logout.logoutSuccessHandler(logoutHandle);
  }

}
