import { NgModule } from '@angular/core';
import { HttpClientModule } from '@angular/common/http';
import { registerLocaleData } from '@angular/common';

import { AppComponent } from './app.component';

import { RouterRoutingModule } from './router/router-routing.module';
import { SharedModule } from './shared/shared.module';
import { CoreModule } from './core/core.module';
import { StateModule } from './state/state.module';
import { LayoutModule } from './layout/layout.module';
import { InterceptorModule } from './interceptor/interceptor.module';

import zh from '@angular/common/locales/zh';
import en from '@angular/common/locales/en';

registerLocaleData(zh);
registerLocaleData(en);

@NgModule({
  declarations: [AppComponent],
  imports: [HttpClientModule, SharedModule, CoreModule, LayoutModule, StateModule, RouterRoutingModule, InterceptorModule],
  providers: [],
  bootstrap: [AppComponent],
})
export class AppModule {}
