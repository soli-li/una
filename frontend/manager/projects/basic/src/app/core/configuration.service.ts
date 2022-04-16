import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Constants } from '@basic/Constants';
import { Configuration } from '@basic/models/configuration.model';
import { PaginationInfo } from '@basic/models/pagination-info.model';
import { Observable, of } from 'rxjs';
import { Utils } from './util';

@Injectable({ providedIn: 'root' })
export class ConfigurationService {
  constructor(private httpClient: HttpClient) {}
  public search(condition: object): Observable<PaginationInfo<Configuration>> {
    const httpOptions = {
      headers: Utils.getHeader(`${Constants.MEDIA_TYPE_JSON};charset=${Constants.UTF_8_ENCODING}`),
    };
    return this.httpClient.post<PaginationInfo<Configuration>>(`${Constants.SERVER_API_URL}/conf/search`, condition, httpOptions);
  }

  public save(conf: Configuration): Observable<object> {
    const httpOptions = {
      headers: Utils.getHeader(`${Constants.MEDIA_TYPE_JSON};charset=${Constants.UTF_8_ENCODING}`),
    };
    return this.httpClient.put(`${Constants.SERVER_API_URL}/conf/save`, conf, httpOptions);
  }

  public exist(companyId: string, key: string): Observable<boolean> {
    if (key === '' || companyId === '') {
      return of(false);
    }
    const httpOptions = {
      headers: Utils.getHeader(`${Constants.MEDIA_TYPE_X_WWW_FORM_URLENCODED};charset=${Constants.UTF_8_ENCODING}`),
    };
    return this.httpClient.post<boolean>(`${Constants.SERVER_API_URL}/conf/exist`, `companyId=${companyId}&key=${key}`, httpOptions);
  }

  public refresh(): Observable<object> {
    const httpOptions = {
      headers: Utils.getHeader(`${Constants.MEDIA_TYPE_TEXT_PLAIN};charset=${Constants.UTF_8_ENCODING}`),
    };
    return this.httpClient.post(`${Constants.SERVER_API_URL}/conf/refresh`, ``, httpOptions);
  }
}
