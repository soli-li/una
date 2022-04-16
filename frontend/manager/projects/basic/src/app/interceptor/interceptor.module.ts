import { HTTP_INTERCEPTORS } from '@angular/common/http';
import { NgModule } from '@angular/core';

import { AuthHeadInterceptor } from './auth-head.interceptor';

export const httpInterceptorProviders = [{ provide: HTTP_INTERCEPTORS, useClass: AuthHeadInterceptor, multi: true }];

@NgModule({
  providers: [httpInterceptorProviders],
})
export class InterceptorModule {}
