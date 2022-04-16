import { NgModule } from '@angular/core';

import { SharedModule } from '@basic/shared/shared.module';
import { RouterRoutingModule } from '@basic/router/router-routing.module';
import { LoginComponent } from './login/login.component';
import { MainComponent } from './main/main.component';
import { LeftSideComponent } from './side/left-side.component';
import { RightSideComponent } from './side/right-side.component';
import { HeaderComponent } from './header/header.component';
import { FooterComponent } from './footer/footer.component';
import { ContentComponent } from './content/content.component';

import { PwPolicyModule } from './function/pw-policy/pw-policy.module';
import { CompanyModule } from './function/company/company.module';
import { UserModule } from './function/user/user.module';
import { GroupModule } from './function/group/group.module';
import { RoleModule } from './function/role/role.module';
import { PermModule } from './function/perm/perm.module';
import { MenuModule } from './function/menu/menu.module';
import { UrlPermModule } from './function/url-perm/url-perm.module';

import { FunctionRelationModule } from './function/function-relation/function-relation.module';
import { ConfigurationModule } from './function/configuration/configuration.module';

@NgModule({
  imports: [
    SharedModule,
    RouterRoutingModule,
    PwPolicyModule,
    CompanyModule,
    UserModule,
    GroupModule,
    RoleModule,
    PermModule,
    MenuModule,
    UrlPermModule,
    FunctionRelationModule,
    ConfigurationModule,
  ],
  declarations: [LoginComponent, MainComponent, LeftSideComponent, RightSideComponent, HeaderComponent, FooterComponent, ContentComponent],
  exports: [LoginComponent, MainComponent],
})
export class LayoutModule {}
