import { NgModule } from '@angular/core';

import { SharedModule } from '@basic/shared/shared.module';
import { RouterRoutingModule } from '@basic/router/router-routing.module';
import { FunctionRelationComponent } from './function-relation.component';

@NgModule({
  imports: [SharedModule, RouterRoutingModule],
  declarations: [FunctionRelationComponent],
  exports: [FunctionRelationComponent],
})
export class FunctionRelationModule {}
