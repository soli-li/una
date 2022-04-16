import { NgModule } from '@angular/core';

import { SharedModule } from '@basic/shared/shared.module';
import { RouterRoutingModule } from '@basic/router/router-routing.module';
import { ConfigurationManageComponent } from './configuration-manage.component';
import { ConfigurationDetailComponent } from './configuration-detail.component';

@NgModule({
  imports: [SharedModule, RouterRoutingModule],
  declarations: [ConfigurationManageComponent, ConfigurationDetailComponent],
  exports: [ConfigurationManageComponent, ConfigurationDetailComponent],
})
export class ConfigurationModule {}
