import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Constants } from '@basic/Constants';
import { Menu } from '@basic/models/menu.model';
import { PaginationInfo } from '@basic/models/pagination-info.model';
import { Observable, of } from 'rxjs';
import { Utils } from './util';

@Injectable({ providedIn: 'root' })
export class MenuService {
  constructor(private httpClient: HttpClient) {}
  public getCurrent(): Observable<Menu[]> {
    return this.httpClient.get<Menu[]>(`${Constants.SERVER_API_URL}/menu/current`);
  }

  public search(condition: object): Observable<PaginationInfo<Menu>> {
    const httpOptions = {
      headers: Utils.getHeader(`${Constants.MEDIA_TYPE_JSON};charset=${Constants.UTF_8_ENCODING}`),
    };
    return this.httpClient.post<PaginationInfo<Menu>>(`${Constants.SERVER_API_URL}/menu/search`, condition, httpOptions);
  }

  public save(menu: Menu): Observable<object> {
    const httpOptions = {
      headers: Utils.getHeader(`${Constants.MEDIA_TYPE_JSON};charset=${Constants.UTF_8_ENCODING}`),
    };
    return this.httpClient.put(`${Constants.SERVER_API_URL}/menu/save`, menu, httpOptions);
  }

  public exist(uri: string): Observable<boolean> {
    if (uri === '') {
      return of(false);
    }
    const httpOptions = {
      headers: Utils.getHeader(`${Constants.MEDIA_TYPE_X_WWW_FORM_URLENCODED};charset=${Constants.UTF_8_ENCODING}`),
    };
    return this.httpClient.post<boolean>(`${Constants.SERVER_API_URL}/menu/exist`, `uri=${uri}`, httpOptions);
  }
}
