import { NgModule } from '@angular/core';

import { SharedModule } from '@basic/shared/shared.module';
import { RouterRoutingModule } from '@basic/router/router-routing.module';
import { CompanyDetailComponent } from './company-detail.component';
import { CompanyManageComponent } from './company-manage.component';

@NgModule({
  imports: [SharedModule, RouterRoutingModule],
  declarations: [CompanyDetailComponent, CompanyManageComponent],
  exports: [CompanyDetailComponent, CompanyManageComponent],
})
export class CompanyModule {}
