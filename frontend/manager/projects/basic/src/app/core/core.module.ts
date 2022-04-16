import { NgModule } from '@angular/core';
import { CacheService } from './cache.service';
import { CompanyService } from './company.service';
import { GroupService } from './group.service';
import { LoginService } from './login.service';
import { MenuService } from './menu.service';
import { PermissionsService } from './permissions.service';
import { PasswordPolicyService } from './pw-policy.service';
import { RoleService } from './role.service';
import { UrlPermService } from './url-perm.service';
import { UserService } from './user.service';

@NgModule({
  providers: [
    LoginService,
    CacheService,
    PasswordPolicyService,
    CompanyService,
    UserService,
    GroupService,
    RoleService,
    PermissionsService,
    MenuService,
    UrlPermService,
  ],
})
export class CoreModule {}
