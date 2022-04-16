import { NgModule } from '@angular/core';

import { SharedModule } from '@basic/shared/shared.module';
import { RouterRoutingModule } from '@basic/router/router-routing.module';
import { GroupManageComponent } from './group-manage.component';
import { GroupDetailComponent } from './group-detail.component';
import { FunctionRelationModule } from '../function-relation/function-relation.module';

@NgModule({
  imports: [SharedModule, RouterRoutingModule, FunctionRelationModule],
  declarations: [GroupManageComponent, GroupDetailComponent],
  exports: [GroupManageComponent, GroupDetailComponent],
})
export class GroupModule {}
