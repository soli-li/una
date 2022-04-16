import { Injectable } from '@angular/core';
import { HttpEvent, HttpInterceptor, HttpHandler, HttpRequest, HttpErrorResponse } from '@angular/common/http';
import { Observable, tap } from 'rxjs';
import { Router } from '@angular/router';

import { LoginService } from '@basic/core/login.service';
import { Constants } from '@basic/Constants';
import { CacheService } from '@basic/core/cache.service';
import { NzMessageService } from 'ng-zorro-antd/message';

@Injectable({ providedIn: 'root' })
export class AuthHeadInterceptor implements HttpInterceptor {
  private inDebounce = 0;
  private requestNotVerification: string[] = [];
  constructor(loginService: LoginService, private cacheService: CacheService, private router: Router, private nzMessageService: NzMessageService) {
    this.requestNotVerification.push(`${Constants.SERVER_API_URL}${loginService.getLoginUrl()}`);
    this.requestNotVerification.push(`${Constants.SERVER_API_URL}${loginService.getResetCredentialsUrl()}`);
  }
  intercept(req: HttpRequest<object>, next: HttpHandler): Observable<HttpEvent<object>> {
    let authReq = req;
    if (this.requestNotVerification.indexOf(req.url) < 0) {
      const token = this.cacheService.getToken();
      if (token === null) {
        this.router.navigateByUrl(Constants.ROUTER_LOGIN);
        throw new Error('cannot found user token');
      }
      authReq = req.clone({ headers: req.headers.set(Constants.HEAD_TOKEN, token) });
    }

    return next.handle(authReq).pipe(tap({ error: (res) => this.responseErrorHandler(authReq.url, res) }));
  }

  private responseErrorHandler(url: string, res: object): void {
    if (res instanceof HttpErrorResponse) {
      const error = res.error as object;
      const code = error['code' as keyof object];
      let tigMsg = '';
      if (res.status === 401 && code !== undefined && code === '999998') {
        this.cacheService.clearAttributes();
        tigMsg = '登录超时';
      } else {
        tigMsg = '请求失败';
      }
      this.debounce(() => this.nzMessageService.error(tigMsg), 500)();
    }
  }

  private debounce(func: () => void, delay: number) {
    return (...args: never) => {
      // eslint-disable-next-line @typescript-eslint/no-this-alias
      const context = this;
      clearTimeout(this.inDebounce);
      this.inDebounce = window.setTimeout(() => func.apply(context, args), delay);
    };
  }
}
