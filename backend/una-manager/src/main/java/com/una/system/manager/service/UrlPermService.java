package com.una.system.manager.service;

import com.una.common.Constants;
import com.una.common.pojo.PaginationInfo;
import com.una.common.utils.LogUtil;
import com.una.common.utils.ObjectsToEntityUtil;
import com.una.common.utils.ObjectsToEntityUtil.Transform3;
import com.una.system.manager.dao.UrlPermDao;
import com.una.system.manager.model.Permissions;
import com.una.system.manager.model.UrlPerm;
import com.una.system.manager.model.User;
import com.una.system.manager.pojo.req.SearchUrlPerm;
import com.una.system.manager.utils.PaginationSortUtil;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Order;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

@Service
public class UrlPermService {
  private static final Logger LOGGER = LogUtil.getRunLogger();
  @Autowired
  private UrlPermDao urlPermDao;
  @Autowired
  private GenerateService generateService;
  @Autowired
  private PermissionsService permissionsService;

  private String addUriStartSlash(String uri) {
    if (StringUtils.isBlank(uri)) {
      return "";
    }
    uri = StringUtils.trim(uri);
    if (StringUtils.startsWith(uri, "/")) {
      return uri;
    }
    return "/" + uri;
  }

  private void checkFrontEndUri(final UrlPerm urlPerm) {
    if (StringUtils.isBlank(urlPerm.getUri())) {
      throw new RuntimeException("uri must not be empty");
    }
    if (StringUtils.replace(urlPerm.getUri(), " ", "").length() != urlPerm.getUri().length()) {
      throw new RuntimeException("uri cannot has space character");
    }
  }

  @Transactional(readOnly = true,
      propagation = Propagation.SUPPORTS)
  public Set<UrlPerm> findByPermIds(final Collection<String> premIds) {
    Assert.isTrue(!CollectionUtils.isEmpty(premIds), "parameter 'premIds' must not be empty");

    final Set<UrlPerm> urlPermSet = this.urlPermDao.findByPermissionsId(premIds, Constants.ENABLE);
    return urlPermSet;
  }

  @Transactional(readOnly = false,
      propagation = Propagation.REQUIRED)
  public void save(final UrlPerm urlPerm, final String userId) {
    Objects.requireNonNull(urlPerm, "parameter 'urlPerm' must not be empty");
    Assert.hasText(userId, "parameter 'userId' must not be empty");

    if (StringUtils.isAnyBlank(urlPerm.getName())) {
      throw new RuntimeException("name must not be empty");
    }
    this.checkFrontEndUri(urlPerm);
    urlPerm.setUri(this.addUriStartSlash(urlPerm.getUri()));

    final String permissionsId = urlPerm.getPermissionsId();
    if (StringUtils.isNotBlank(permissionsId)) {
      this.permissionsService.findById(permissionsId)
          .orElseThrow(() -> new RuntimeException("cannot find permissions"));
    }

    Assert.isTrue(StringUtils.equalsAny(urlPerm.getStatus(), Constants.ENABLE, Constants.DISABLE),
        "incorrect status value");

    if (StringUtils.isBlank(urlPerm.getId())) {
      urlPerm.setId(this.generateService.generateId());
      urlPerm.setCreatedUserId(userId);
      urlPerm.setCreatedDate(LocalDateTime.now());
    }

    UrlPermService.LOGGER
        .info("save or update url perm object -> already checked url perm value, ready save");

    this.urlPermDao.save(urlPerm);
  }

  @Transactional(readOnly = true,
      propagation = Propagation.SUPPORTS)
  public PaginationInfo<UrlPerm> search(final SearchUrlPerm searchUrlPerm) {
    Assert.isTrue(Objects.nonNull(searchUrlPerm), "parameter 'searchUrlPerm' must not be empty");

    searchUrlPerm.setName(
        StringUtils.wrapIfMissing(StringUtils.defaultIfBlank(searchUrlPerm.getName(), null), "%"));
    searchUrlPerm.setUri(
        StringUtils.wrapIfMissing(StringUtils.defaultIfBlank(searchUrlPerm.getUri(), null), "%"));
    searchUrlPerm.setPerm(
        StringUtils.wrapIfMissing(StringUtils.defaultIfBlank(searchUrlPerm.getPerm(), null), "%"));

    Pageable pageable = Pageable.unpaged();
    if (searchUrlPerm.isPageable()) {
      final List<Order> orderList = PaginationSortUtil.transformToOrder(searchUrlPerm.getSort());
      pageable = PageRequest.of(searchUrlPerm.getPageNumber() - 1, searchUrlPerm.getPageSize(),
          Sort.by(orderList));
    }

    UrlPermService.LOGGER.info("search url perm, current{} contain pagination information",
        pageable.isPaged() ? "" : " does not");
    UrlPermService.LOGGER.info("search condition, {}", searchUrlPerm.toString());

    final Page<UrlPerm> result = this.urlPermDao.search(searchUrlPerm, pageable).map(o -> {
      final User createdUser = new User();
      final Transform3<UrlPerm, Permissions, User> transform = ObjectsToEntityUtil.getTransform3(o);
      BeanUtils.copyProperties(Optional.ofNullable(transform.getT3()).orElse(new User()),
          createdUser);

      final UrlPerm urlPerm = transform.getT1();
      urlPerm.setPermissions(transform.getT2());
      urlPerm.setCreatedUser(createdUser);
      return urlPerm;
    });

    final PaginationInfo<UrlPerm> paginationInfo = PaginationInfo.of(searchUrlPerm.getPageNumber(),
        searchUrlPerm.getPageSize(), searchUrlPerm.isPageable(), result.getContent(),
        result.getTotalElements());
    if (pageable.isUnpaged()) {
      final List<UrlPerm> dataList = new ArrayList<>(paginationInfo.getDataList());
      Collections.sort(dataList, this::sort);
      paginationInfo.setDataList(dataList);
    }
    return paginationInfo;
  }

  private int sort(final UrlPerm o1, final UrlPerm o2) {
    final Integer s1 = Optional.ofNullable(o1).map(UrlPerm::getSort).orElse(0);
    final Integer s2 = Optional.ofNullable(o2).map(UrlPerm::getSort).orElse(0);
    return s1.compareTo(s2);
  }
}
