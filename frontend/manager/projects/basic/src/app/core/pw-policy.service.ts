import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Constants } from '@basic/Constants';
import { PasswordPolicy } from '@basic/models/password-policy.model';
import { Observable } from 'rxjs';
import { Utils } from './util';

@Injectable({ providedIn: 'root' })
export class PasswordPolicyService {
  constructor(private httpClient: HttpClient) {}
  public all(): Observable<PasswordPolicy[]> {
    const httpOptions = {
      headers: Utils.getHeader(`${Constants.MEDIA_TYPE_JSON};charset=${Constants.UTF_8_ENCODING}`),
    };
    return this.httpClient.get<PasswordPolicy[]>(`${Constants.SERVER_API_URL}/pwPolicy/all`, httpOptions);
  }

  public save(entity: PasswordPolicy): Observable<object> {
    const httpOptions = {
      headers: Utils.getHeader(`${Constants.MEDIA_TYPE_JSON};charset=${Constants.UTF_8_ENCODING}`),
    };
    return this.httpClient.put(`${Constants.SERVER_API_URL}/pwPolicy/save`, entity, httpOptions);
  }
}
