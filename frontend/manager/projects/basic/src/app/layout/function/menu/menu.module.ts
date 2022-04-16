import { NgModule } from '@angular/core';

import { SharedModule } from '@basic/shared/shared.module';
import { RouterRoutingModule } from '@basic/router/router-routing.module';
import { MenuManageComponent } from './menu-manage.component';
import { MenuDetailComponent } from './menu-detail.component';

@NgModule({
  imports: [SharedModule, RouterRoutingModule],
  declarations: [MenuManageComponent, MenuDetailComponent],
  exports: [MenuManageComponent, MenuDetailComponent],
})
export class MenuModule {}
