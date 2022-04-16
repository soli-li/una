import { NgModule } from '@angular/core';

import { SharedModule } from '@basic/shared/shared.module';
import { RouterRoutingModule } from '@basic/router/router-routing.module';
import { PermManageComponent } from './perm-manage.component';
import { PermDetailComponent } from './perm-detail.component';
import { RolePermRelationComponent } from './role-perm-relation.component';

@NgModule({
  imports: [SharedModule, RouterRoutingModule],
  declarations: [PermManageComponent, PermDetailComponent, RolePermRelationComponent],
  exports: [PermManageComponent, PermDetailComponent],
})
export class PermModule {}
