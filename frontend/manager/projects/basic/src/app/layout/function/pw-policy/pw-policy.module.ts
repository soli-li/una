import { NgModule } from '@angular/core';

import { SharedModule } from '@basic/shared/shared.module';
import { RouterRoutingModule } from '@basic/router/router-routing.module';
import { PwPolicyManageComponent } from './pw-policy-manage.component';
import { PwPolicyDetailComponent } from './pw-policy-detail.component';

@NgModule({
  imports: [SharedModule, RouterRoutingModule],
  declarations: [PwPolicyDetailComponent, PwPolicyManageComponent],
  exports: [PwPolicyDetailComponent, PwPolicyManageComponent],
})
export class PwPolicyModule {}
