import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Constants } from '@basic/Constants';
import { Company } from '@basic/models/company.model';
import { PaginationInfo } from '@basic/models/pagination-info.model';
import { Observable, of } from 'rxjs';
import { Utils } from './util';

@Injectable({ providedIn: 'root' })
export class CompanyService {
  constructor(private httpClient: HttpClient) {}
  public search(condition: object): Observable<PaginationInfo<Company>> {
    const httpOptions = {
      headers: Utils.getHeader(`${Constants.MEDIA_TYPE_JSON};charset=${Constants.UTF_8_ENCODING}`),
    };
    return this.httpClient.post<PaginationInfo<Company>>(`${Constants.SERVER_API_URL}/company/search`, condition, httpOptions);
  }

  public save(company: Company): Observable<object> {
    const httpOptions = {
      headers: Utils.getHeader(`${Constants.MEDIA_TYPE_JSON};charset=${Constants.UTF_8_ENCODING}`),
    };
    return this.httpClient.put(`${Constants.SERVER_API_URL}/company/save`, company, httpOptions);
  }

  public existName(name: string): Observable<boolean> {
    if (name === '') {
      return of(false);
    }
    const httpOptions = {
      headers: Utils.getHeader(`${Constants.MEDIA_TYPE_X_WWW_FORM_URLENCODED};charset=${Constants.UTF_8_ENCODING}`),
    };
    return this.httpClient.post<boolean>(`${Constants.SERVER_API_URL}/company/exist`, `name=${name}`, httpOptions);
  }

  public existShortName(name: string): Observable<boolean> {
    if (name === '') {
      return of(false);
    }
    const httpOptions = {
      headers: Utils.getHeader(`${Constants.MEDIA_TYPE_X_WWW_FORM_URLENCODED};charset=${Constants.UTF_8_ENCODING}`),
    };
    return this.httpClient.post<boolean>(`${Constants.SERVER_API_URL}/company/exist`, `shortName=${name}`, httpOptions);
  }
}
