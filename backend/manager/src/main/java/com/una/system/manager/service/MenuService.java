package com.una.system.manager.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

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

import com.una.common.Constants;
import com.una.common.pojo.PaginationInfo;
import com.una.common.utils.LogUtil;
import com.una.common.utils.ObjectsToEntityUtil;
import com.una.common.utils.ObjectsToEntityUtil.Transform4;
import com.una.system.manager.dao.MenuDao;
import com.una.system.manager.model.Menu;
import com.una.system.manager.model.Permissions;
import com.una.system.manager.model.User;
import com.una.system.manager.pojo.req.SearchMenu;
import com.una.system.manager.utils.PaginationSortUtil;

@Service
public class MenuService {
  private static final Logger LOGGER = LogUtil.getRunLogger();
  @Autowired
  private MenuDao menuDao;
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

  private void checkFrontEndUri(final Menu menu) {
    if (StringUtils.isBlank(menu.getFrontEndUri())) {
      throw new RuntimeException("front end uri must not be empty");
    }
    if (StringUtils.replace(menu.getFrontEndUri(), " ", "").length() != menu.getFrontEndUri().length()) {
      throw new RuntimeException("front end uri cannot has space character");
    }
    final SearchMenu searchMenu = new SearchMenu();
    searchMenu.setEqFrontEndUri(menu.getFrontEndUri());
    final PaginationInfo<Menu> paginationInfo = this.search(searchMenu);
    if (paginationInfo.isEmpty()) {
      return;
    }

    final List<Menu> dataList = paginationInfo.getDataList();
    final Optional<Menu> optional = dataList.stream()
      .filter(m -> StringUtils.equals(m.getId(), menu.getId()) && StringUtils.equals(m.getFrontEndUri(), menu.getFrontEndUri())).findAny();
    if (optional.isPresent()) {
    }
    else {
      throw new RuntimeException("exist front end uri");
    }
  }

  private void checkPermissions(final Menu menu) {
    final String permissionsId = menu.getPermissionsId();
    if (StringUtils.isBlank(permissionsId)) {
      return;
    }
    this.permissionsService.findById(permissionsId).orElseThrow(() -> new RuntimeException("cannot find permissions"));
  }

  private void cleanParentMenu(Collection<Menu> menus) {
    menus = Optional.ofNullable(menus).orElse(List.of());
    for (final Menu menu : menus) {
      menu.setParentMenu(null);
      this.cleanParentMenu(menu.getChildrenMenuSet());
    }
  }

  @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
  public Set<Menu> findByPermIds(final Collection<String> premIds) {
    Assert.isTrue(!CollectionUtils.isEmpty(premIds), "parameter 'premIds' must not be empty");
    Set<Menu> menuSet = this.menuDao.findByPermissionsId(premIds, Constants.ENABLE);
    menuSet = this.transformToTreeMode(menuSet, false);
    this.setMenuLevel(menuSet, 0);
    return menuSet;
  }

  private void flatMeun(Collection<Menu> menus, final Collection<Menu> coll) {
    menus = Optional.ofNullable(menus).orElse(List.of());
    for (final Menu menu : menus) {
      coll.add(menu);
      this.flatMeun(menu.getChildrenMenuSet(), coll);
    }
  }

  private void getAllChildrenId(final Menu menu, final Set<String> uriSet) {
    if (Objects.isNull(menu) || CollectionUtils.isEmpty(menu.getChildrenMenuSet())) {
      return;
    }
    final Set<Menu> childrenMenuSet = menu.getChildrenMenuSet();
    for (final Menu childrenMenu : childrenMenuSet) {
      uriSet.add(childrenMenu.getId());
      this.getAllChildrenId(childrenMenu, uriSet);
    }
  }

  @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
  public void save(final Menu menu, final String userId) {
    Objects.requireNonNull(menu, "parameter 'menu' must not be empty");
    Assert.hasText(userId, "parameter 'userId' must not be empty");

    if (StringUtils.isAnyBlank(menu.getName())) {
      throw new RuntimeException("name must not be empty");
    }

    this.checkFrontEndUri(menu);
    menu.setFrontEndUri(this.addUriStartSlash(menu.getFrontEndUri()));
    Assert.isTrue(StringUtils.equalsAny(menu.getStatus(), Constants.ENABLE, Constants.DISABLE), "incorrect status value");
    this.checkPermissions(menu);
    final SearchMenu searchMenu = new SearchMenu();
    searchMenu.setPageable(false);
    final List<Menu> treeMenuList = this.search(searchMenu).getDataList();
    final Set<Menu> menuSet = new HashSet<>();
    this.flatMeun(treeMenuList, menuSet);
    if (StringUtils.isNotBlank(menu.getParentId())) {
      menuSet.stream().filter(m -> StringUtils.equals(m.getId(), menu.getParentId())).findAny()
        .orElseThrow(() -> new RuntimeException("cannot find parent menu"));
    }

    if (StringUtils.isBlank(menu.getId())) {
      menu.setId(this.generateService.generateId());
      menu.setCreatedUserId(userId);
      menu.setCreatedDate(LocalDateTime.now());
    }
    else {
      final Menu dbMenu = menuSet.stream().filter(m -> StringUtils.equals(m.getId(), menu.getId())).findAny()
        .orElseThrow(() -> new RuntimeException("menu not exist"));
      final Set<String> uriSet = new HashSet<>();
      this.getAllChildrenId(dbMenu, uriSet);
      if (uriSet.contains(menu.getParentId())) {
        throw new RuntimeException("cannot be associated with the parent menu");
      }
    }

    MenuService.LOGGER.info("save or update menu object -> already checked menu value, ready save");

    this.menuDao.save(menu);
  }

  @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
  public PaginationInfo<Menu> search(final SearchMenu searchMenu) {
    Assert.isTrue(Objects.nonNull(searchMenu), "parameter 'searchMenu' must not be empty");

    searchMenu.setName(StringUtils.wrapIfMissing(StringUtils.defaultIfBlank(searchMenu.getName(), null), "%"));

    Pageable pageable = Pageable.unpaged();
    if (searchMenu.isPageable()) {
      final List<Order> orderList = PaginationSortUtil.transformToOrder(searchMenu.getSort());
      pageable = PageRequest.of(searchMenu.getPageNumber() - 1, searchMenu.getPageSize(), Sort.by(orderList));
    }

    MenuService.LOGGER.info("search menu, current{} contain pagination information", pageable.isPaged() ? "" : " does not");
    MenuService.LOGGER.info("search condition, {}", searchMenu.toString());

    final Page<Menu> result = this.menuDao.search(searchMenu, pageable).map(o -> {
      final User createdUser = new User();
      final Transform4<Menu, Menu, Permissions, User> transform = ObjectsToEntityUtil.getTransform4(o);
      BeanUtils.copyProperties(Optional.ofNullable(transform.getT4()).orElse(new User()), createdUser);

      final Menu menu = transform.getT1();
      final Menu parentMenu = transform.getT2();
      menu.setParentMenu(parentMenu);
      menu.setPermissions(transform.getT3());
      menu.setCreatedUser(createdUser);
      return menu;
    });

    final PaginationInfo<Menu> paginationInfo = PaginationInfo.of(searchMenu.getPageNumber(), searchMenu.getPageSize(), searchMenu.isPageable(),
      result.getContent(), result.getTotalElements());
    if (pageable.isUnpaged()) {
      final List<Menu> dataList = paginationInfo.getDataList();
      final Set<Menu> menuSet = new LinkedHashSet<>(this.transformToTreeMode(dataList, true));
      this.setMenuLevel(menuSet, 0);
      this.cleanParentMenu(menuSet);
      this.sortMenu(menuSet);
      paginationInfo.setDataList(new ArrayList<>(menuSet));
    }
    return paginationInfo;
  }

  private void setMenuLevel(Collection<Menu> menus, final int level) {
    menus = Optional.ofNullable(menus).orElse(Collections.emptyList());
    for (final Menu menu : menus) {
      menu.setLevel(level);
      final Set<Menu> childrenMenuSet = menu.getChildrenMenuSet();
      this.setMenuLevel(childrenMenuSet, level + 1);
    }
  }

  private void sortMenu(final Collection<Menu> menus) {
    if (CollectionUtils.isEmpty(menus)) {
      return;
    }
    final List<Menu> list = new ArrayList<>(menus);
    Collections.sort(list, Comparator.comparing(Menu::getSort));
    menus.clear();
    menus.addAll(list);

    for (final Menu menu : menus) {
      if (Objects.isNull(menu.getChildrenMenuSet())) {
        continue;
      }
      final Set<Menu> childrenMenuSet = new LinkedHashSet<>(menu.getChildrenMenuSet());
      this.sortMenu(childrenMenuSet);
      menu.setChildrenMenuSet(childrenMenuSet);
    }
  }

  private Set<Menu> transformToTreeMode(final Collection<Menu> menus, final boolean addNotRootMenu) {
    final Set<Menu> rootMenuSet = new LinkedHashSet<>();
    final Map<String, Menu> menuMap = menus.stream().collect(Collectors.toMap(Menu::getId, m -> m, (o1, o2) -> o1, LinkedHashMap::new));
    menuMap.forEach((id, menu) -> {
      String parentId = "";
      if (StringUtils.isBlank(menu.getParentId())) {
        rootMenuSet.add(menu);
      }
      else {
        parentId = menu.getParentId();
      }
      final Menu parentMenu = menuMap.getOrDefault(parentId, null);

      if (Objects.nonNull(parentMenu)) {
        final Set<Menu> childrenMenuSet = Optional.ofNullable(parentMenu.getChildrenMenuSet()).orElse(new LinkedHashSet<>());
        childrenMenuSet.add(menu);
        parentMenu.setChildrenMenuSet(childrenMenuSet);
      }
      else if (addNotRootMenu) {
        rootMenuSet.add(menu);
      }
    });
    return rootMenuSet;
  }
}
