package com.una.system.gateway.security.authentication;

import com.una.common.pojo.Configuration;
import com.una.common.pojo.PasswordPolicy;
import com.una.common.pojo.User;
import com.una.common.pojo.UserSessionList;
import com.una.common.pojo.UserSessionList.SessionInfo;
import com.una.common.utils.LogUtil;
import com.una.system.gateway.Constants;
import com.una.system.gateway.security.authentication.exception.AuthException;
import com.una.system.gateway.service.ManagerService;
import com.una.system.gateway.service.SharingService;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import reactor.core.publisher.Mono;

// 认证管理
public class LoginAuthentication implements ReactiveAuthenticationManager {

  private static final Logger LOGGER = LogUtil.getRunLogger();
  private final ManagerService managerService;
  private final SharingService sharingService;
  private final int defaultMaximumSessions;
  private final boolean defaultMaxSessionsPreventsLogin;
  private final String maximumSessionsKey;

  private final String maxSessionsPreventsLoginKey;

  public LoginAuthentication(final ManagerService managerService,
      final SharingService sharingService, final int defaultMaximumSessions,
      final boolean defaultMaxSessionsPreventsLogin, final String maximumSessionsKey,
      final String maxSessionsPreventsLoginKey) {
    this.managerService = managerService;
    this.sharingService = sharingService;
    this.defaultMaximumSessions = defaultMaximumSessions;
    this.defaultMaxSessionsPreventsLogin = defaultMaxSessionsPreventsLogin;
    this.maximumSessionsKey = maximumSessionsKey;
    this.maxSessionsPreventsLoginKey = maxSessionsPreventsLoginKey;
  }

  @Override
  public Mono<Authentication> authenticate(final Authentication authentication) {
    if (authentication instanceof UsernamePasswordAuthenticationToken) {
      return this.usernamePasswordAuthenticate(authentication);
    }
    throw new AuthException(authentication.getName(),
        Constants.LOGIN_FAIL_CODE_NOT_SUPPORT_TOKEN.toString(), "not support authentication token");
  }

  private void checkLock(final User user) {
    final PasswordPolicy passwordPolicy = user.getCompany().getPasswordPolicy();
    if (passwordPolicy.getTriesCount() <= 0) {
      return;
    }

    if (user.getFailureCount() >= passwordPolicy.getTriesCount()) {
      user.setAccountNonLocked(false);
    }

  }

  private void checkParameter(final String username, final String password, final String captch) {
    final List<String> condition = new ArrayList<>();
    condition.add(this.checkParameterEmpty(username, "username"));
    condition.add(this.checkParameterEmpty(password, "password"));
    // condition.add(this.checkParameterEmpty(captch, "captch"));
    // condition.add(this.checkParameterEmpty(specifyGroupId, "defaultGroupId"));

    final List<String> list = condition.stream().filter(StringUtils::isNotBlank).toList();
    if (!list.isEmpty()) {
      final String msg = String.format("following parameter must not empty, %s", list.toString());
      throw new AuthException(username, Constants.LOGIN_FAIL_CODE_PARAM.toString(), msg);
    }
  }

  private String checkParameterEmpty(final String value, final String name) {
    if (StringUtils.isBlank(value)) {
      return name;
    }
    return null;
  }

  private Mono<Boolean> checkPassword(final String username, final String rawPassword,
      final User user) {
    Mono<Optional<Boolean>> mono = this.managerService.passwordMatch(username, rawPassword)
        .flatMap(b -> Mono.just(Optional.of(b)));
    mono = mono.defaultIfEmpty(Optional.empty());
    return mono.map(o -> {
      final boolean isMatch = o.orElse(false);
      if (!isMatch) {
        final AuthException authEx = new AuthException(username,
            Constants.LOGIN_FAIL_CODE_PASS_NOT_MATCH.toString(), "password not match");
        user.setFailureCount(Optional.ofNullable(user.getFailureCount()).orElse(0) + 1);
        authEx.setUser(user);
        throw authEx;
      }
      return isMatch;
    });
  }

  private void checkPasswordExpired(final User user) {
    final PasswordPolicy passwordPolicy = user.getCompany().getPasswordPolicy();
    if (passwordPolicy.getMaximumAge() <= 0) {
      return;
    }

    final LocalDateTime lastChangePwDate = Optional.ofNullable(user.getLastChangePwDate())
        .orElse(LocalDateTime.now().minusDays(passwordPolicy.getMaximumAge() + 1));
    final LocalDateTime expiredDate = lastChangePwDate.plusDays(passwordPolicy.getMaximumAge());
    if (expiredDate.isBefore(LocalDateTime.now())) {
      user.setCredentialsNonExpired(false);
    }
  }

  private void checkUserStatus(final User user, final String username) {
    AuthException authenticationException = null;
    if (!user.isCredentialsNonExpired()) {
      authenticationException = new AuthException(username,
          Constants.LOGIN_FAIL_CODE_USER_EXPIRED.toString(), "user account expired");
    }
    if (!user.isAccountNonExpired()) {
      authenticationException = new AuthException(username,
          Constants.LOGIN_FAIL_CODE_USER_STATUS.toString(), "user account disabled");
    }
    if (!user.isAccountNonLocked()) {
      authenticationException = new AuthException(username,
          Constants.LOGIN_FAIL_CODE_USER_LOCKED.toString(), "user account locked");
    }
    if (Objects.nonNull(authenticationException)) {
      authenticationException.setUser(user);
      throw authenticationException;
    }
  }

  private Mono<Optional<User>> fillUser(final Mono<Boolean> mono, final User user) {
    return mono.flatMap(v -> this.managerService.fillUser(user)
        .flatMap(u -> Mono.just(Optional.of(u))).defaultIfEmpty(Optional.empty()));
  }

  private Mono<UsernamePasswordAuthenticationToken> generateToken(final Mono<Optional<User>> mono,
      final AuthenticationDetail authDetail) {

    return mono.flatMap(ou -> {
      final User user = ou.get();
      if (!StringUtils.equals(com.una.common.Constants.ENABLE, user.getCompany().getStatus())) {
        throw new RuntimeException("company status is not enable, username: " + user.getUsername());
      }
      this.checkPasswordExpired(user);
      this.checkLock(user);
      this.checkUserStatus(user, user.getUsername());

      final Mono<UsernamePasswordAuthenticationToken> successTokenMono = Mono.fromCallable(() -> {
        final Set<GrantedAuthority> grantedAuthoritySet = new HashSet<>();
        user.getCurrentRoleId()
            .forEach(id -> grantedAuthoritySet.add(new UserGrantedAuthority(id)));
        authDetail.setUser(user);
        final UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
            user.getUsername(), "", grantedAuthoritySet);
        auth.setDetails(authDetail);
        return auth;
      });

      final Mono<List<Configuration>> configurationListMono = this.sharingService
          .getConfigurationList(user.getCompanyId());
      return configurationListMono.flatMap(list -> {
        final String maxConfValue = list.stream()
            .filter(c -> StringUtils.equals(c.getConfKey(), this.maximumSessionsKey))
            .filter(c -> StringUtils.equals(c.getStatus(), com.una.common.Constants.ENABLE))
            .findAny().map(Configuration::getConfValue).orElse("");
        final String preventsConfValue = list.stream()
            .filter(c -> StringUtils.equals(c.getConfKey(), this.maxSessionsPreventsLoginKey))
            .filter(c -> StringUtils.equals(c.getStatus(), com.una.common.Constants.ENABLE))
            .findAny().map(Configuration::getConfValue).orElse("");

        int max = this.defaultMaximumSessions;
        boolean prevents = this.defaultMaxSessionsPreventsLogin;
        if (StringUtils.isNotBlank(maxConfValue)) {
          try {
            max = Integer.parseInt(maxConfValue);
          } catch (final Exception e) {
            LoginAuthentication.LOGGER.error("", e);
          }
        }
        if (StringUtils.isNotBlank(preventsConfValue)) {
          prevents = StringUtils.equals(preventsConfValue, com.una.common.Constants.TRUE);
        }
        if (max <= 0) {
          return successTokenMono;
        }
        return this.loginOver(user.getUsername(), max, prevents, successTokenMono);
      });
    });
  }

  private Mono<UsernamePasswordAuthenticationToken> loginOver(final String username,
      final int maximumSessions, final boolean maxSessionsPreventsLogin,
      final Mono<UsernamePasswordAuthenticationToken> successMono) {
    final Mono<Optional<UserSessionList>> onlineSessionListMono = this.managerService
        .getUserSession(username).flatMap(o -> Mono.just(Optional.ofNullable(o)))
        .defaultIfEmpty(Optional.empty());
    return onlineSessionListMono.flatMap(o -> {
      final Map<String, SessionInfo> sessionMap = o.map(UserSessionList::getSessionMap)
          .orElse(null);
      if (Objects.isNull(sessionMap) || sessionMap.size() < maximumSessions) {
        return successMono;
      }

      if (maxSessionsPreventsLogin) {
        throw new AuthException(username, Constants.LOGIN_FAIL_CODE_OVER_SESSION.toString(),
            "over max sessions prevents login");
      }

      final List<SessionInfo> sessionList = new ArrayList<>(sessionMap.values());
      Collections.sort(sessionList, (o1, o2) -> o1.getCreatedTime().compareTo(o2.getCreatedTime()));
      int size = sessionList.size();
      int i = 0;
      while (maximumSessions <= size) {
        final SessionInfo sessionInfo = sessionList.get(i);
        this.sharingService.removeUser(sessionInfo.getSessionId()).subscribe();
        i++;
        size--;
      }

      return successMono;
    });
  }

  private void throwAuthenticationException(final Throwable e, final String username) {
    if (!(e instanceof final AuthException authException)) {
      throw new AuthException(username, Constants.LOGIN_FAIL_CODE_100.toString(),
          "login authenticate exception", e);
    }
  }

  private Mono<Authentication> usernamePasswordAuthenticate(final Authentication authentication) {
    final String username = authentication.getName();
    final String rawPassword = String.valueOf(authentication.getCredentials());
    String captchaCode = "";
    final Object details = authentication.getDetails();
    AuthenticationDetail detail = new AuthenticationDetail(captchaCode);
    if (details instanceof final AuthenticationDetail authenticationDetail) {
      captchaCode = authenticationDetail.getCaptchaCode();
      detail = authenticationDetail;
    }
    final AuthenticationDetail authDetail = detail;

    this.checkParameter(username, rawPassword, captchaCode);

    Mono<Optional<User>> mono = this.managerService.findUser(authentication.getName())
        .flatMap(u -> Mono.just(Optional.of(u)));
    mono = mono.defaultIfEmpty(Optional.empty());
    mono = mono.doOnError(e -> this.throwAuthenticationException(e, authentication.getName()));
    return mono.flatMap(o -> {
      if (o.isEmpty()) {
        throw new AuthException(username, Constants.LOGIN_FAIL_CODE_USER_NOT_FOUND.toString(),
            "user not found");
      }
      final User user = o.get();
      final Mono<Boolean> checkPassword = this.checkPassword(user.getUsername(), rawPassword, user);
      final Mono<Optional<User>> userMono = this.fillUser(checkPassword, user);

      return this.generateToken(userMono, authDetail);
    });
  }
}
