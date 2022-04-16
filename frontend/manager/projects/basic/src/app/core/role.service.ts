import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Constants } from '@basic/Constants';
import { PaginationInfo } from '@basic/models/pagination-info.model';
import { Role } from '@basic/models/role.model';
import { Observable, of } from 'rxjs';
import { Utils } from './util';

@Injectable({ providedIn: 'root' })
export class RoleService {
  constructor(private httpClient: HttpClient) {}
  public search(condition: object): Observable<PaginationInfo<Role>> {
    const httpOptions = {
      headers: Utils.getHeader(`${Constants.MEDIA_TYPE_JSON};charset=${Constants.UTF_8_ENCODING}`),
    };
    return this.httpClient.post<PaginationInfo<Role>>(`${Constants.SERVER_API_URL}/role/search`, condition, httpOptions);
  }

  public save(role: Role): Observable<object> {
    const httpOptions = {
      headers: Utils.getHeader(`${Constants.MEDIA_TYPE_JSON};charset=${Constants.UTF_8_ENCODING}`),
    };
    return this.httpClient.put<object>(`${Constants.SERVER_API_URL}/role/save`, role, httpOptions);
  }

  public exist(authority: string, companyId: string): Observable<boolean> {
    if (authority === '') {
      return of(false);
    }
    const httpOptions = {
      headers: Utils.getHeader(`${Constants.MEDIA_TYPE_X_WWW_FORM_URLENCODED};charset=${Constants.UTF_8_ENCODING}`),
    };
    return this.httpClient.post<boolean>(`${Constants.SERVER_API_URL}/role/exist`, `authority=${authority}&companyId=${companyId}`, httpOptions);
  }

  public findByUser(users: string[]): Observable<Role[]> {
    if (users.length === 0) {
      return of([]);
    }
    const httpOptions = {
      headers: Utils.getHeader(`${Constants.MEDIA_TYPE_JSON};charset=${Constants.UTF_8_ENCODING}`),
    };
    return this.httpClient.post<Role[]>(`${Constants.SERVER_API_URL}/role/findByUser`, users, httpOptions);
  }

  public findByGroup(groups: string[]): Observable<Role[]> {
    if (groups.length === 0) {
      return of([]);
    }
    const httpOptions = {
      headers: Utils.getHeader(`${Constants.MEDIA_TYPE_JSON};charset=${Constants.UTF_8_ENCODING}`),
    };
    return this.httpClient.post<Role[]>(`${Constants.SERVER_API_URL}/role/findByGroup`, groups, httpOptions);
  }

  public findByPerm(perms: string[], companyId: string): Observable<Role[]> {
    if (perms.length === 0 || companyId === null || companyId === '') {
      return of([]);
    }
    const httpOptions = {
      headers: Utils.getHeader(`${Constants.MEDIA_TYPE_JSON};charset=${Constants.UTF_8_ENCODING}`),
    };
    const param = { companyId: companyId, permissionsList: perms };
    return this.httpClient.post<Role[]>(`${Constants.SERVER_API_URL}/role/findByPerm`, param, httpOptions);
  }

  public updateRelation(role: Role): Observable<object> {
    const httpOptions = {
      headers: Utils.getHeader(`${Constants.MEDIA_TYPE_JSON};charset=${Constants.UTF_8_ENCODING}`),
    };
    return this.httpClient.post<object>(`${Constants.SERVER_API_URL}/role/updateRelation`, role, httpOptions);
  }

  public updateRelationForPerm(role: Role): Observable<object> {
    const httpOptions = {
      headers: Utils.getHeader(`${Constants.MEDIA_TYPE_JSON};charset=${Constants.UTF_8_ENCODING}`),
    };
    return this.httpClient.post<object>(`${Constants.SERVER_API_URL}/role/updateRelationForPerm`, role, httpOptions);
  }
}
