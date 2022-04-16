import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Constants } from '@basic/Constants';
import { PaginationInfo } from '@basic/models/pagination-info.model';
import { Permissions } from '@basic/models/permissions.model';
import { Observable, of } from 'rxjs';
import { Utils } from './util';

@Injectable({ providedIn: 'root' })
export class PermissionsService {
  constructor(private httpClient: HttpClient) {}
  public search(condition: object): Observable<PaginationInfo<Permissions>> {
    const httpOptions = {
      headers: Utils.getHeader(`${Constants.MEDIA_TYPE_JSON};charset=${Constants.UTF_8_ENCODING}`),
    };
    return this.httpClient.post<PaginationInfo<Permissions>>(`${Constants.SERVER_API_URL}/perm/search`, condition, httpOptions);
  }

  public save(permissions: Permissions): Observable<object> {
    const httpOptions = {
      headers: Utils.getHeader(`${Constants.MEDIA_TYPE_JSON};charset=${Constants.UTF_8_ENCODING}`),
    };
    return this.httpClient.put<object>(`${Constants.SERVER_API_URL}/perm/save`, permissions, httpOptions);
  }

  public findFunctionByRole(roles: string[]): Observable<object[]> {
    if (roles.length === 0) {
      return of([]);
    }
    const httpOptions = {
      headers: Utils.getHeader(`${Constants.MEDIA_TYPE_JSON};charset=${Constants.UTF_8_ENCODING}`),
    };
    return this.httpClient.post<object[]>(`${Constants.SERVER_API_URL}/perm/findFunctionByRole`, roles, httpOptions);
  }

  public updateRelation(perm: string, role: string[], companyId: string): Observable<object> {
    if (perm === null || perm === '' || role === null || role.length === 0 || companyId === null || companyId === '') {
      throw new Error('param is incorrect');
    }
    const httpOptions = {
      headers: Utils.getHeader(`${Constants.MEDIA_TYPE_JSON};charset=${Constants.UTF_8_ENCODING}`),
    };
    const param = { companyId: companyId, permissionsList: [perm], roleList: role };
    return this.httpClient.post<object>(`${Constants.SERVER_API_URL}/perm/updateRelation`, param, httpOptions);
  }

  public findByRole(roles: string[]): Observable<Permissions[]> {
    if (roles.length === 0) {
      return of([]);
    }
    const httpOptions = {
      headers: Utils.getHeader(`${Constants.MEDIA_TYPE_JSON};charset=${Constants.UTF_8_ENCODING}`),
    };
    return this.httpClient.post<Permissions[]>(`${Constants.SERVER_API_URL}/perm/findByRole`, roles, httpOptions);
  }
}
