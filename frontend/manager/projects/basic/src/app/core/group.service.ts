import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Constants } from '@basic/Constants';
import { Group } from '@basic/models/group.model';
import { PaginationInfo } from '@basic/models/pagination-info.model';
import { Observable, of } from 'rxjs';
import { Utils } from './util';

@Injectable({ providedIn: 'root' })
export class GroupService {
  constructor(private httpClient: HttpClient) {}
  public search(condition: object): Observable<PaginationInfo<Group>> {
    const httpOptions = {
      headers: Utils.getHeader(`${Constants.MEDIA_TYPE_JSON};charset=${Constants.UTF_8_ENCODING}`),
    };
    return this.httpClient.post<PaginationInfo<Group>>(`${Constants.SERVER_API_URL}/group/search`, condition, httpOptions);
  }

  public save(group: Group): Observable<object> {
    const httpOptions = {
      headers: Utils.getHeader(`${Constants.MEDIA_TYPE_JSON};charset=${Constants.UTF_8_ENCODING}`),
    };
    return this.httpClient.put(`${Constants.SERVER_API_URL}/group/save`, group, httpOptions);
  }

  public exist(name: string, companyId: string): Observable<boolean> {
    if (name === '') {
      return of(false);
    }
    const httpOptions = {
      headers: Utils.getHeader(`${Constants.MEDIA_TYPE_X_WWW_FORM_URLENCODED};charset=${Constants.UTF_8_ENCODING}`),
    };
    return this.httpClient.post<boolean>(`${Constants.SERVER_API_URL}/group/exist`, `name=${name}&companyId=${companyId}`, httpOptions);
  }

  public findByUser(users: string[]): Observable<Group[]> {
    if (users.length === 0) {
      return of([]);
    }
    const httpOptions = {
      headers: Utils.getHeader(`${Constants.MEDIA_TYPE_JSON};charset=${Constants.UTF_8_ENCODING}`),
    };
    return this.httpClient.post<Group[]>(`${Constants.SERVER_API_URL}/group/findByUser`, users, httpOptions);
  }

  public findByRole(roles: string[]): Observable<Group[]> {
    if (roles.length === 0) {
      return of([]);
    }
    const httpOptions = {
      headers: Utils.getHeader(`${Constants.MEDIA_TYPE_JSON};charset=${Constants.UTF_8_ENCODING}`),
    };
    return this.httpClient.post<Group[]>(`${Constants.SERVER_API_URL}/group/findByRole`, roles, httpOptions);
  }

  public updateRelation(group: Group): Observable<object> {
    const httpOptions = {
      headers: Utils.getHeader(`${Constants.MEDIA_TYPE_JSON};charset=${Constants.UTF_8_ENCODING}`),
    };
    return this.httpClient.post(`${Constants.SERVER_API_URL}/group/updateRelation`, group, httpOptions);
  }
}
