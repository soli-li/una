import { NgModule } from '@angular/core';

import { SharedModule } from '@basic/shared/shared.module';
import { RouterRoutingModule } from '@basic/router/router-routing.module';
import { RoleManageComponent } from './role-manage.component';
import { RoleDetailComponent } from './role-detail.component';
import { ForPermRelationComponent } from './for-perm-relation.component';
import { FunctionRelationModule } from '../function-relation/function-relation.module';

@NgModule({
  imports: [SharedModule, RouterRoutingModule, FunctionRelationModule],
  declarations: [RoleManageComponent, RoleDetailComponent, ForPermRelationComponent],
  exports: [RoleManageComponent, RoleDetailComponent],
})
export class RoleModule {}
