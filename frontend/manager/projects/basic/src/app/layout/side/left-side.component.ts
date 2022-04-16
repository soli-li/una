import { Component, Input, OnInit, ViewChild } from '@angular/core';
import { AbstractControl, FormBuilder, FormControl, FormGroup, ValidationErrors, ValidatorFn, Validators } from '@angular/forms';
import { DomSanitizer, SafeUrl } from '@angular/platform-browser';
import { ActivatedRoute, Router } from '@angular/router';
import { Constants } from '@basic/Constants';
import { LoginService } from '@basic/core/login.service';
import { MenuService } from '@basic/core/menu.service';
import { UserService } from '@basic/core/user.service';
import { Utils } from '@basic/layout/utils';
import { Menu } from '@basic/models/menu.model';
import { User } from '@basic/models/user.model';
import * as userReducer from '@basic/state/user/user-state.reducer';
import { Store } from '@ngrx/store';
import { Mode, UserDetailComponent } from '../function/user/user-detail.component';

@Component({
  selector: 'app-una-left-side',
  templateUrl: './left-side.component.html',
  styleUrls: ['./left-side.component.less'],
})
export class LeftSideComponent implements OnInit {
  @Input() collapsed = false;
  menus: Menu[] = [];
  utils = Utils;
  currentUser?: User;
  name?: string;
  formGroup!: FormGroup;
  avatar?: SafeUrl;

  detailWidth = 800;
  detailVisible = false;
  detailMode = 'user-update' as Mode;
  @ViewChild('detailModal') detailModal?: UserDetailComponent;

  changePasswordWidth = 350;
  changePasswordVisible = false;
  showErrorMsg = false;
  errorMsg: string[] = [];

  constructor(
    private sanitizer: DomSanitizer,
    private store: Store<userReducer.AppState>,
    private loginService: LoginService,
    private userService: UserService,
    private menuService: MenuService,
    private activatedRoute: ActivatedRoute,
    private router: Router,
    private fb: FormBuilder
  ) {}
  ngOnInit(): void {
    this.store.select(userReducer.UserSelector.getUser).subscribe((user) => {
      this.currentUser = user || ({} as User);
      this.name = this.currentUser.userProfile?.realName;
      if (this.name === undefined || this.name === null || this.name === '') {
        this.name = this.currentUser.username;
      }
    });
    this.userService.currentAvatar().subscribe({
      next: (data) => {
        if (data.size > 0) {
          const url = this.sanitizer.bypassSecurityTrustUrl(window.URL.createObjectURL(data));
          this.avatar = url;
        }
      },
    });
    this.menuService.getCurrent().subscribe({
      next: (v) => {
        const path = this.activatedRoute.firstChild?.routeConfig?.path;
        this.menus = v;
        this.setParam(this.menus);
        this.setSelected(this.menus, path);
      },
    });

    this.formGroup = this.fb.group({});
    this.formGroup.addControl(`currPassword`, new FormControl('', Validators.required));
    this.formGroup.addControl(`newPassword`, new FormControl(''));
    this.formGroup.addControl(`againNewPassword`, new FormControl(''));

    this.formGroup.controls['newPassword'].addValidators(this.matchNewPassword(false));
    this.formGroup.controls['againNewPassword'].addValidators(this.matchNewPassword(true));
  }

  private matchNewPassword(again: boolean): ValidatorFn {
    return (control: AbstractControl): ValidationErrors | null => {
      let password = this.formGroup.value['againNewPassword'];
      if (again) {
        password = this.formGroup.value['newPassword'];
      }
      if (control.value !== password) {
        this.formGroup.controls['againNewPassword'].setErrors({ notMatch: true });
        this.formGroup.controls['newPassword'].setErrors({ notMatch: true });
        return { notMatch: true };
      }
      this.formGroup.controls['againNewPassword'].setErrors(null);
      this.formGroup.controls['newPassword'].setErrors(null);
      return null;
    };
  }

  private setParam(menus: Menu[]): void {
    for (const menu of menus) {
      let uri = menu.frontEndUri;
      const delimiterIndex = uri.indexOf('?');
      if (delimiterIndex > -1) {
        const param: { [k: string]: string } = {};
        const paramStr = uri.substring(uri.indexOf('?') + 1);
        for (const p of paramStr.split('&')) {
          const paramKV = p.split('=');
          if (paramKV.length === 2) {
            param[paramKV[0]] = paramKV[1];
          }
        }
        uri = uri.substring(0, delimiterIndex);
        menu.frontEndUri = uri;
        menu.param = param;
      }
      const childrenMenu = menu.childrenMenuSet;
      if (childrenMenu !== undefined && childrenMenu !== null) {
        this.setParam(childrenMenu);
      }
    }
  }

  private setSelected(menus: Menu[], path: string | undefined): boolean {
    if (path === undefined) {
      return false;
    }
    let hasSelected = false;
    for (const menu of menus) {
      menu.selected = false;
      if (menu.frontEndUri.indexOf(path) > -1) {
        menu.selected = true;
        hasSelected = true;
      }
      const childrenMenu = menu.childrenMenuSet;
      if (childrenMenu !== undefined && childrenMenu !== null) {
        if (this.setSelected(childrenMenu, path)) {
          menu.selected = true;
        }
      }
    }
    return hasSelected;
  }

  operDetail(): void {
    this.detailVisible = true;
  }

  handleCancel(): void {
    this.detailVisible = false;
  }
  handleOk(): void {
    const data = this.detailModal?.setData();
    if (data === undefined || data === null) {
      return;
    }
    this.detailVisible = false;
    this.userService.update(data, data.userProfile?.avatarFile).subscribe({
      next: () => {
        if (this.currentUser !== undefined && this.currentUser !== null) {
          const user = { ...this.currentUser };
          user.userProfile = data.userProfile;
          user.defaultGroupId = data.defaultGroupId;
          this.store.dispatch(userReducer.UserAction.updateUser({ user: user }));
        }
      },
    });
  }

  logout(): void {
    this.loginService.logout().subscribe({
      next: () => {
        this.router.navigateByUrl(Constants.ROUTER_LOGIN);
      },
    });
  }

  changeGroup(groupId: string): void {
    this.userService.changeGroup(groupId).subscribe({
      next: () => {
        window.location.reload();
      },
    });
  }

  changePassword(): void {
    this.changePasswordVisible = true;
  }

  changePasswordCancel(): void {
    this.changePasswordVisible = false;
    this.formGroup.reset();
  }
  changePasswordOk(): void {
    Object.values(this.formGroup.controls).forEach((control) => {
      if (control.invalid) {
        control.markAsDirty();
        control.updateValueAndValidity({ onlySelf: false });
      }
    });
    if (!this.formGroup.valid) {
      return;
    }
    if (this.currentUser !== undefined && this.currentUser !== null) {
      this.userService.changePassword(this.currentUser.id, this.formGroup.value['currPassword'], this.formGroup.value['newPassword']).subscribe({
        next: (r) => {
          const resultCode = r['code' as keyof object];
          this.showErrorMsg = true;
          this.errorMsg = [];
          if (resultCode === '000000') {
            this.showErrorMsg = false;
            this.detailVisible = false;
            this.changePasswordCancel();
          } else if (resultCode === 'P00100') {
            this.errorMsg.push('当前密码不正确');
          } else if (resultCode === 'P00200') {
            const errorMsg = JSON.parse(r['msg' as keyof object])['description'];
            if (errorMsg !== undefined) {
              this.errorMsg = errorMsg.split('<br/>');
            }
          } else {
            this.errorMsg = r['msg' as keyof object];
          }
        },
      });
    }
  }
}
