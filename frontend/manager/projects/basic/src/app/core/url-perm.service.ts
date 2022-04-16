import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Constants } from '@basic/Constants';
import { PaginationInfo } from '@basic/models/pagination-info.model';
import { UrlPerm } from '@basic/models/url-perm';
import { Observable } from 'rxjs';
import { Utils } from './util';

@Injectable({ providedIn: 'root' })
export class UrlPermService {
  constructor(private httpClient: HttpClient) {}
  public search(condition: object): Observable<PaginationInfo<UrlPerm>> {
    const httpOptions = {
      headers: Utils.getHeader(`${Constants.MEDIA_TYPE_JSON};charset=${Constants.UTF_8_ENCODING}`),
    };
    return this.httpClient.post<PaginationInfo<UrlPerm>>(`${Constants.SERVER_API_URL}/url/search`, condition, httpOptions);
  }

  public save(urlPerm: UrlPerm): Observable<object> {
    const httpOptions = {
      headers: Utils.getHeader(`${Constants.MEDIA_TYPE_JSON};charset=${Constants.UTF_8_ENCODING}`),
    };
    return this.httpClient.put(`${Constants.SERVER_API_URL}/url/save`, urlPerm, httpOptions);
  }
}
