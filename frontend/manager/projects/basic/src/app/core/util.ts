import { HttpHeaders } from '@angular/common/http';

export class Utils {
  public static getHeader(contentType = ''): HttpHeaders {
    let httpHeaders = new HttpHeaders();
    httpHeaders = httpHeaders.set('Content-Type', contentType);
    return httpHeaders;
  }
}
