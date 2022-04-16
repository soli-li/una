import { NgModule } from '@angular/core';

import { SharedModule } from '@basic/shared/shared.module';
import { RouterRoutingModule } from '@basic/router/router-routing.module';
import { UrlPermManageComponent } from './url-perm-manage.component';
import { UrlPermDetailComponent } from './url-perm-detail.component';

@NgModule({
  imports: [SharedModule, RouterRoutingModule],
  declarations: [UrlPermManageComponent, UrlPermDetailComponent],
  exports: [UrlPermManageComponent, UrlPermDetailComponent],
})
export class UrlPermModule {}
