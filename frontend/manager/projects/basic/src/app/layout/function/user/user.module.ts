import { NgModule } from '@angular/core';

import { SharedModule } from '@basic/shared/shared.module';
import { RouterRoutingModule } from '@basic/router/router-routing.module';
import { UserDetailComponent } from './user-detail.component';
import { UserManageComponent } from './user-manage.component';
import { UserOnlineManageComponent } from './user-online-manage.component';
import { FunctionRelationModule } from '../function-relation/function-relation.module';

@NgModule({
  imports: [SharedModule, RouterRoutingModule, FunctionRelationModule],
  declarations: [UserManageComponent, UserDetailComponent, UserOnlineManageComponent],
  exports: [UserManageComponent, UserDetailComponent],
})
export class UserModule {}
