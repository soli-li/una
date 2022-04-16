import { NgModule } from '@angular/core';
import { RouteReuseStrategy, RouterModule, Routes } from '@angular/router';

import { AppReuseStrategy } from './app.reuse.strategy';
import { Constants } from '@basic/Constants';
import { LoginComponent } from '@basic/layout/login/login.component';
import { MainComponent } from '@basic/layout/main/main.component';

import { ConfigurationManageComponent } from '@basic/layout/function/configuration/configuration-manage.component';
import { PwPolicyManageComponent } from '@basic/layout/function/pw-policy/pw-policy-manage.component';
import { CompanyManageComponent } from '@basic/layout/function/company/company-manage.component';
import { UserManageComponent } from '@basic/layout/function/user/user-manage.component';
import { GroupManageComponent } from '@basic/layout/function/group/group-manage.component';
import { RoleManageComponent } from '@basic/layout/function/role/role-manage.component';
import { PermManageComponent } from '@basic/layout/function/perm/perm-manage.component';
import { MenuManageComponent } from '@basic/layout/function/menu/menu-manage.component';
import { UrlPermManageComponent } from '@basic/layout/function/url-perm/url-perm-manage.component';

const routes: Routes = [
  { path: Constants.ROUTER_LOGIN, component: LoginComponent },
  {
    path: Constants.ROUTER_MAIN,
    component: MainComponent,
    children: [
      // for super admin
      { path: 'config-manage-for-manager', component: ConfigurationManageComponent },
      { path: 'pwPolicy-manage-for-manager', component: PwPolicyManageComponent },
      { path: 'company-manage-for-manager', component: CompanyManageComponent },
      { path: 'user-manage-for-manager', component: UserManageComponent },
      { path: 'group-manage-for-manager', component: GroupManageComponent },
      { path: 'role-manage-for-manager', component: RoleManageComponent },
      { path: 'perm-manage', component: PermManageComponent },
      { path: 'menu-manage', component: MenuManageComponent },
      { path: 'url-manage', component: UrlPermManageComponent },
      // for company admin
      { path: 'config-manage', component: ConfigurationManageComponent },
      { path: 'pwPolicy-manage', component: PwPolicyManageComponent },
      { path: 'company-manage', component: CompanyManageComponent },
      { path: 'user-manage', component: UserManageComponent },
      { path: 'group-manage', component: GroupManageComponent },
      { path: 'role-manage', component: RoleManageComponent },
    ],
  },
  { path: '', redirectTo: `/${Constants.ROUTER_LOGIN}`, pathMatch: 'full' },
  { path: '**', redirectTo: `/${Constants.ROUTER_LOGIN}`, pathMatch: 'full' },
];
@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule],
  providers: [{ provide: RouteReuseStrategy, useClass: AppReuseStrategy }],
})
export class RouterRoutingModule {}
