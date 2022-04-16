import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Constants } from '@basic/Constants';
import { PaginationInfo } from '@basic/models/pagination-info.model';
import { UserSessionList } from '@basic/models/user-session-list.model';
import { User } from '@basic/models/user.model';
import * as userReducer from '@basic/state/user/user-state.reducer';
import { Store } from '@ngrx/store';
import { Observable, of } from 'rxjs';
import { Utils } from './util';

@Injectable({ providedIn: 'root' })
export class UserService {
  constructor(private httpClient: HttpClient, private store: Store<userReducer.AppState>) {}

  public getUser(username: string): Observable<User> {
    const content = `name=${username}`;
    const httpOptions = {
      headers: Utils.getHeader(`${Constants.MEDIA_TYPE_X_WWW_FORM_URLENCODED};charset=${Constants.UTF_8_ENCODING}`),
    };
    return this.httpClient.post<User>(`${Constants.SERVER_API_URL}/user/find`, content, httpOptions);
  }

  public setCurrentUser(failureFn?: (error: Error) => void): void {
    const httpOptions = {
      headers: Utils.getHeader(`${Constants.MEDIA_TYPE_JSON};charset=${Constants.UTF_8_ENCODING}`),
    };
    const observable = this.httpClient.get<User>(`${Constants.SERVER_API_URL}/user/current`, httpOptions);
    observable.subscribe({
      next: (u) => {
        this.store.dispatch(userReducer.UserAction.updateUser({ user: u }));
      },
      error: (e) => {
        if (failureFn !== undefined && failureFn !== null) {
          failureFn(e);
        }
      },
    });
  }

  public search(condition: object): Observable<PaginationInfo<User>> {
    const httpOptions = {
      headers: Utils.getHeader(`${Constants.MEDIA_TYPE_JSON};charset=${Constants.UTF_8_ENCODING}`),
    };
    return this.httpClient.post<PaginationInfo<User>>(`${Constants.SERVER_API_URL}/user/search`, condition, httpOptions);
  }

  public save(user: User, file?: File): Observable<object> {
    const formData = new FormData();
    if (file !== undefined) {
      formData.append('file', file);
    }
    formData.append('user', JSON.stringify(user));
    return this.httpClient.post(`${Constants.SERVER_API_URL}/user/save`, formData, {});
  }

  public update(user: User, file?: File): Observable<object> {
    const formData = new FormData();
    if (file !== undefined) {
      formData.append('file', file, 'test');
    }
    formData.append('user', JSON.stringify(user));
    return this.httpClient.post(`${Constants.SERVER_API_URL}/user/update`, formData, {});
  }

  public exist(username: string): Observable<boolean> {
    if (username === '') {
      return of(false);
    }
    const httpOptions = {
      headers: Utils.getHeader(`${Constants.MEDIA_TYPE_X_WWW_FORM_URLENCODED};charset=${Constants.UTF_8_ENCODING}`),
    };
    return this.httpClient.post<boolean>(`${Constants.SERVER_API_URL}/user/exist`, `username=${username}`, httpOptions);
  }

  public findByGroup(groups: string[]): Observable<User[]> {
    if (groups.length === 0) {
      return of([]);
    }
    const httpOptions = {
      headers: Utils.getHeader(`${Constants.MEDIA_TYPE_JSON};charset=${Constants.UTF_8_ENCODING}`),
    };
    return this.httpClient.post<User[]>(`${Constants.SERVER_API_URL}/user/findByGroup`, groups, httpOptions);
  }

  public findByRole(roles: string[]): Observable<User[]> {
    if (roles.length === 0) {
      return of([]);
    }
    const httpOptions = {
      headers: Utils.getHeader(`${Constants.MEDIA_TYPE_JSON};charset=${Constants.UTF_8_ENCODING}`),
    };
    return this.httpClient.post<User[]>(`${Constants.SERVER_API_URL}/user/findByRole`, roles, httpOptions);
  }

  public updateRelation(user: User): Observable<object> {
    const httpOptions = {
      headers: Utils.getHeader(`${Constants.MEDIA_TYPE_JSON};charset=${Constants.UTF_8_ENCODING}`),
    };
    return this.httpClient.post<object>(`${Constants.SERVER_API_URL}/user/updateRelation`, user, httpOptions);
  }

  public resetPassword(user: User): Observable<object> {
    const httpOptions = {
      headers: Utils.getHeader(`${Constants.MEDIA_TYPE_JSON};charset=${Constants.UTF_8_ENCODING}`),
    };
    return this.httpClient.post<object>(`${Constants.SERVER_API_URL}/user/resetPassword`, user, httpOptions);
  }

  public changePassword(userId: string, oldPassword: string, newPassword: string): Observable<object> {
    const param = `userId=${userId}&oldPassword=${oldPassword}&newPassword=${newPassword}`;
    const httpOptions = {
      headers: Utils.getHeader(`${Constants.MEDIA_TYPE_X_WWW_FORM_URLENCODED};charset=${Constants.UTF_8_ENCODING}`),
    };
    return this.httpClient.post<object>(`${Constants.SERVER_API_URL}/user/changePassword`, param, httpOptions);
  }

  public changeGroup(groupId: string): Observable<object> {
    const param = `groupId=${groupId}`;
    const httpOptions = {
      headers: Utils.getHeader(`${Constants.MEDIA_TYPE_X_WWW_FORM_URLENCODED};charset=${Constants.UTF_8_ENCODING}`),
    };
    return this.httpClient.post(`${Constants.SERVER_API_URL}/user/changeGroup`, param, httpOptions);
  }

  public getOnlineUserSession(username: string): Observable<UserSessionList> {
    const httpOptions = {
      headers: Utils.getHeader(`${Constants.MEDIA_TYPE_X_WWW_FORM_URLENCODED};charset=${Constants.UTF_8_ENCODING}`),
    };
    return this.httpClient.get<UserSessionList>(`${Constants.SERVER_API_URL}/user/getOnlineUserSession/${username}/true`, httpOptions);
  }

  public takeOutUser(sessionId: string): Observable<object> {
    const param = `sessionId=${sessionId}`;
    const httpOptions = {
      headers: Utils.getHeader(`${Constants.MEDIA_TYPE_X_WWW_FORM_URLENCODED};charset=${Constants.UTF_8_ENCODING}`),
    };
    return this.httpClient.post<object>(`${Constants.SERVER_API_URL}/user/takeOutUser`, param, httpOptions);
  }

  public currentAvatar(): Observable<Blob> {
    return this.httpClient.get(`${Constants.SERVER_API_URL}/user/currentAvatar`, {
      headers: Utils.getHeader(`${Constants.MEDIA_TYPE_TEXT_PLAIN};charset=${Constants.UTF_8_ENCODING}`),
      responseType: 'blob',
    });
  }

  public getAvatar(userId: string): Observable<Blob> {
    return this.httpClient.post(`${Constants.SERVER_API_URL}/user/getAvatar`, `userId=${userId}`, {
      headers: Utils.getHeader(`${Constants.MEDIA_TYPE_X_WWW_FORM_URLENCODED};charset=${Constants.UTF_8_ENCODING}`),
      responseType: 'blob',
    });
  }
}
