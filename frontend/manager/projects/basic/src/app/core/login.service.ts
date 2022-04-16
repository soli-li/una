import { HttpClient, HttpResponse } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Constants } from '@basic/Constants';
import { Observable, tap } from 'rxjs';
import { CacheService } from './cache.service';
import { Utils } from './util';

@Injectable({ providedIn: 'root' })
export class LoginService {
  private LOGIN_URL = '/login';
  private LOGOUT_URL = '/logout';
  private RESET_CREDENTIALS_URL = '/user/resetCredentials';
  constructor(private httpClient: HttpClient, private cacheService: CacheService) {}
  public login(username: string, password: string, captcha = '', groupId = ''): Observable<HttpResponse<object>> {
    const content = `username=${username}&password=${encodeURIComponent(password)}&captcha=${captcha}&groupId=${groupId}`;
    const observable = this.httpClient.post(`${Constants.SERVER_API_URL}${this.LOGIN_URL}`, content, {
      headers: Utils.getHeader(`${Constants.MEDIA_TYPE_X_WWW_FORM_URLENCODED};charset=${Constants.UTF_8_ENCODING}`),
      observe: 'response',
    });
    return observable.pipe(
      tap((resp) => {
        const body = resp.body;
        if (body === undefined || body === null) {
          throw new Error('body is null');
        }
        if (body['status' as keyof object] === 'success') {
          const token = resp.headers.get(Constants.HEAD_TOKEN);
          if (token === null) {
            throw new Error('token is null');
          }
          this.cacheService.saveToken(token);
          this.cacheService.saveUsername(username);
        }
      })
    );
  }

  public getLoginUrl(): string {
    return this.LOGIN_URL;
  }
  public getResetCredentialsUrl(): string {
    return this.RESET_CREDENTIALS_URL;
  }

  public logout(): Observable<object> {
    const httpOptions = {
      headers: Utils.getHeader(`${Constants.MEDIA_TYPE_X_WWW_FORM_URLENCODED};charset=${Constants.UTF_8_ENCODING}`),
    };
    const observable = this.httpClient.post(`${Constants.SERVER_API_URL}${this.LOGOUT_URL}`, httpOptions);
    return observable.pipe(
      tap(() => {
        this.cacheService.clearAttributes(this.cacheService.getTokenKeyName());
        this.cacheService.clearAttributes(this.cacheService.getUserKeyName());
      })
    );
  }

  public resetPassword(username: string, oldPassword: string, newPassword: string, token: string): Observable<object> {
    const content = `username=${username}&oldPassword=${encodeURIComponent(oldPassword)}&newPassword=${encodeURIComponent(newPassword)}`;
    let header = Utils.getHeader(`${Constants.MEDIA_TYPE_X_WWW_FORM_URLENCODED};charset=${Constants.UTF_8_ENCODING}`);
    header = header.set(Constants.HEAD_TOKEN, token);
    const httpOptions = {
      headers: header,
    };
    return this.httpClient.post(`${Constants.SERVER_API_URL}${this.RESET_CREDENTIALS_URL}`, content, httpOptions);
  }
}
