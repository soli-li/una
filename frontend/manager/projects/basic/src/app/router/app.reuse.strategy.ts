import { ActivatedRouteSnapshot, DetachedRouteHandle, RouteReuseStrategy } from '@angular/router';

interface RouteData {
  useCache: boolean;
}

export class AppReuseStrategy implements RouteReuseStrategy {
  private static snapshots: { [key: string]: DetachedRouteHandle } = {};
  /**
   * 路由是否发生变化
   *
   * @param future 将要离开的路由
   * @param curr 将要加载的路由
   * @returns 返回true，路由将不会跳转（意味着路由没有发生变化）。返回false，则路由发生变化并且其余方法会被调用
   */
  shouldReuseRoute(future: ActivatedRouteSnapshot, curr: ActivatedRouteSnapshot): boolean {
    return future.routeConfig === curr.routeConfig;
  }

  /**
   * 路由刚刚被打开，当我们加载到这个路由的组件上时，shouldAttach会被调用。一旦组件被加载这个方法都会被调用
   *
   * @param route
   * @returns 返回true，retrieve方法将会被调用。否则这个组件将会被重新创建。
   */
  shouldAttach(route: ActivatedRouteSnapshot): boolean {
    const handle = this.findDetached(route);
    return handle !== null;
  }

  /**
   * 当shouldAttach方法返回true时这个方法会被调用。提供当前路由的参数（刚打开的路由），并且返回一个缓存的RouteHandle
   *
   * @param route
   * @returns 返回null表示没有效果。我们可以使用这个方法手动获取任何已被缓存的RouteHandle。框架不会自动管理它，需要我们手动实现。
   */
  retrieve(route: ActivatedRouteSnapshot): DetachedRouteHandle | null {
    return this.findDetached(route);
  }

  private findDetached(route: ActivatedRouteSnapshot): DetachedRouteHandle | null {
    const routeConfig = route.routeConfig;
    if (routeConfig !== null && routeConfig.path !== undefined) {
      const handle = AppReuseStrategy.snapshots[routeConfig.path];
      if (handle === undefined) {
        return null;
      }
      return handle;
    }
    return null;
  }

  /**
   * 当离开当前路由时这个方法会被调用
   *
   * @param route
   * @returns 返回true，store方法会被调用
   */
  shouldDetach(route: ActivatedRouteSnapshot): boolean {
    if (route.routeConfig === null) {
      return false;
    }
    if (route.routeConfig.data === null) {
      return false;
    }

    const data = route.routeConfig.data as RouteData;
    if (data === undefined) {
      return false;
    }
    return data.useCache === undefined ? false : data.useCache;
  }

  /**
   * 这个方法当且仅当shouldDetach方法返回true时被调用。我们可以在这里具体实现如何缓存RouteHandle。在这个方法中缓存的内容将会被用在retrieve方法中。它提供了我们离开的路由和RouteHandle。
   *
   * @param route
   * @param handle
   */
  store(route: ActivatedRouteSnapshot, handle: DetachedRouteHandle | null): void {
    if (route.routeConfig === null || handle === null) {
      return;
    }
    const path = route.routeConfig.path;
    if (path === null || path === undefined) {
      return;
    }
    AppReuseStrategy.snapshots[path] = handle;
  }
}
