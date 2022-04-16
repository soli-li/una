package com.una.system.gateway.security;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.security.reactive.PathRequest;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity.AuthorizeExchangeSpec;
import org.springframework.security.config.web.server.ServerHttpSecurity.ExceptionHandlingSpec;
import org.springframework.security.config.web.server.ServerHttpSecurity.FormLoginSpec;
import org.springframework.security.config.web.server.ServerHttpSecurity.HeaderSpec;
import org.springframework.security.config.web.server.ServerHttpSecurity.HttpsRedirectSpec;
import org.springframework.security.config.web.server.ServerHttpSecurity.LogoutSpec;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.authentication.AuthenticationWebFilter;

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

@EnableWebFluxSecurity
@RefreshScope
public class SecurityConfig {
  //
  //
  //  //  @Value("${portal.security.jwt.headName:X-Auth-Token}")
  //  //  private String tokenName;
  //  //  @Value("#{${portal.security.jwt.duration:5 * 60 * 1000}}")
  //  //  private int durationMillisecond;
  //
  //  //
  //  //  @Value("${portal.login.captcha.encoder:pbkdf2}")
  //  //  private String loginCaptchaEncoder;
  //  //  @Value("${portal.login.fail.lock:5}")
  //  //  private int loginFailCntToLock;
  //  @Value("${app.security.passVerify.password:false}")
  //  private boolean passPasswordVerify;
  //  //  @Value("${portal.security.passVerify.captcha:false}")
  //  //  private boolean passCaptchaVerify;
  //
  //  @Autowired
  //  private ObjectMapper mapper;
  //  @Autowired
  //  private IUserService userService;
  //  @Autowired
  //  private IPasswordService passwordServcie;
  //
  //  private LoginLogoutHandler resultHandler;
  //
  //  @Override
  //  protected AuthenticationManager authenticationManager() throws Exception {
  //    final LoginAuthenticationProvider loginAuthenticationProvider = new LoginAuthenticationProvider(this.activeEnv, this.userService, this.passwordServcie,
  //      this.passPasswordVerify);
  //    final List<AuthenticationProvider> authList = Arrays.asList(loginAuthenticationProvider);
  //    SecurityConfig.LOGGER.debug("add authentication provider instance, {}", authList);
  //    final ProviderManager authenticationManager = new ProviderManager(authList);
  //    authenticationManager.setEraseCredentialsAfterAuthentication(true);
  //    return authenticationManager;
  //  }
  //
  //  @Override
  //  protected void configure(final HttpSecurity http) throws Exception {
  //    this.setConfigurer(http.formLogin());
  //    this.setConfigurer(http.logout());
  //    this.setConfigurer(http.sessionManagement());
  //    this.setConfigurer(http.authorizeRequests());
  //    this.setConfigurer(http.headers());
  //
  //    http.csrf().disable();
  //    http.anonymous().disable();
  //    http.rememberMe().disable();
  //
  //    final LogFilter logFilter = new LogFilter(this.normalUriInterceptor);
  //    //    final JwtFilter jwtFilter = new JwtFilter(this.tokenName, this.tokenService, this.durationMillisecond, this.formLoginUrl, this.formLoginProcessing,
  //    //      this.logoutUrl);
  //
  //    http.addFilterBefore(logFilter, ChannelProcessingFilter.class);
  //    //    http.addFilterBefore(jwtFilter, LogoutFilter.class);
  //    //    http.addFilterBefore(jwtFilter, ExceptionTranslationFilter.class);
  //  }
  //
  //  @Override
  //  public void configure(final WebSecurity web) throws Exception {
  //    final IgnoredRequestConfigurer ignoredRequestConfigurer = web.ignoring();
  //    final List<String> urlList = new ArrayList<>();
  //    Arrays.asList(StringUtils.split(this.ignoringUrl, ",")).forEach(o -> urlList.add(StringUtils.trim(o)));
  //    final Set<String> urlSet = urlList.stream().filter(o -> StringUtils.startsWith(o, "/")).collect(Collectors.toSet());
  //
  //    ignoredRequestConfigurer.antMatchers(urlSet.toArray(new String[] {}));
  //  }
  //
  //  @PostConstruct
  //  public void init() {
  //    this.resultHandler = new LoginLogoutHandler(this.mapper, this.formLoginUrl, this.contextPath, this.encode);
  //  }
  //
  //  private void setConfigurer(final ExpressionUrlAuthorizationConfigurer<HttpSecurity>.ExpressionInterceptUrlRegistry authorizeRequests) {
  //    authorizeRequests.anyRequest().authenticated().accessDecisionManager(new CustomAccessDecisionManager());
  //  }
  //

  //  private static final Logger LOGGER = LogUtil.getRunLogger();
  //  @Value("${spring.profiles.active:dev}")
  //  private String activeEnv;
  //
  //  @Value("${server.servlet.context-path:/}")
  //  private String contextPath;
  //
  //  @Value("${app.encode:UTF-8}")
  //  private String encode;
  //
  //  @Value("${app.security.ignoring:}")
  //  private String ignoringUrl;
  //
  private static final String MAXIMUM_SESSIONS_KEY = "system-config.security.session.maximumSessions";
  private static final String MAX_SESSIONS_PREVENTS_LOGIN_KEY = "system-config.security.session.maxSessionsPreventsLogin";

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
    //    this.setConfigurer(httpSecurity.redirectToHttps());
    httpSecurity.securityContextRepository(
      new SecurityContextRepository(this.authHeaderName, this.userIdHeaderName, this.companyIdHeaderName, this.sharingService, this.formLoginUrl));

    httpSecurity.csrf().disable();
    httpSecurity.cors().disable();
    httpSecurity.anonymous().disable();
    httpSecurity.httpBasic().disable();

    //        final LogFilter logFilter = new LogFilter(this.normalUriInterceptor);
    //    final JwtFilter jwtFilter = new JwtFilter(this.tokenName, this.tokenService, this.durationMillisecond, this.formLoginUrl, this.formLoginProcessing,
    //      this.logoutUrl);

    httpSecurity.addFilterAfter(this.normalUriWebFilter, SecurityWebFiltersOrder.FIRST);
    //    http.addFilterBefore(jwtFilter, LogoutFilter.class);
    //    http.addFilterBefore(jtFilter, ExceptionTranslationFilter.class);

    final AuthenticationConverter authenticationConverter = new AuthenticationConverter(this.formLoginUrl, this.usernameParameter, this.passwordParameter,
      this.captchaParameter, this.method, this.encoding);
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
    authorizeExchange.pathMatchers("/**").access(new AccessAuthorization(this.permIdHeaderName, this.managerService, this.objectMapper));
  }

  private void setConfigurer(final ExceptionHandlingSpec exceptionHandling) {
    exceptionHandling.accessDeniedHandler(new AccessDeniedHandler(this.objectMapper));
    exceptionHandling.authenticationEntryPoint(new AuthenticationEntryPoint(this.objectMapper));
  }

  private void setConfigurer(final FormLoginSpec formLogin) {
    final LoginResultHandler loginResultHandler = new LoginResultHandler(this.encoding, this.managerService, this.sharingService, this.authHeaderName);
    formLogin.loginPage(this.formLoginUrl);
    formLogin.authenticationFailureHandler(loginResultHandler);
    formLogin.authenticationSuccessHandler(loginResultHandler);
    formLogin.authenticationManager(new LoginAuthentication(this.managerService, this.sharingService, this.maximumSessions, this.maxSessionsPreventsLogin,
      SecurityConfig.MAXIMUM_SESSIONS_KEY, SecurityConfig.MAX_SESSIONS_PREVENTS_LOGIN_KEY));
  }

  private void setConfigurer(final HeaderSpec headers) {
    //    headers.frameOptions().sameOrigin();
  }

  private void setConfigurer(final HttpsRedirectSpec redirectToHttps) {
  }

  private void setConfigurer(final LogoutSpec logout) {
    final LogoutHandle logoutHandle = new LogoutHandle(this.authHeaderName, this.sharingService, this.encoding);
    logout.logoutUrl(this.logoutUrl);
    logout.logoutHandler(logoutHandle);
    logout.logoutSuccessHandler(logoutHandle);
  }

}
