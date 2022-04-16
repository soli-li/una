import { Component, Input, OnInit } from '@angular/core';
import { AbstractControl, AsyncValidatorFn, FormBuilder, FormControl, FormGroup, ValidationErrors, Validators } from '@angular/forms';
import { DomSanitizer, SafeUrl } from '@angular/platform-browser';
import { CompanyService } from '@basic/core/company.service';
import { GroupService } from '@basic/core/group.service';
import { UserService } from '@basic/core/user.service';
import { Company } from '@basic/models/company.model';
import { Group } from '@basic/models/group.model';
import {} from '@basic/models/pagination-info.model';
import { User } from '@basic/models/user.model';
import { NzMessageService } from 'ng-zorro-antd/message';
import { NzUploadFile, NzUploadXHRArgs } from 'ng-zorro-antd/upload';
// import { debounce, debounceTime, distinctUntilChanged, mergeMap, map, Observable, Subject } from 'rxjs';
import { map, Observable, Observer, of, Subscription } from 'rxjs';

// import { disableDebugTools } from '@angular/platform-browser';

export declare type Mode = 'user-update' | 'update' | 'add' | 'add-manager';
@Component({
  selector: 'app-una-user-detail',
  templateUrl: './user-detail.component.html',
  styleUrls: ['./user-detail.component.less'],
})
export class UserDetailComponent implements OnInit {
  @Input() user?: User;
  @Input() mode: Mode = 'user-update';
  formGroup!: FormGroup;
  groups?: Group[];
  companies: Company[] = [];
  accountPrefix = '';
  avatarFile?: File;
  avatar?: SafeUrl;

  loading = false;

  constructor(
    private sanitizer: DomSanitizer,
    private fb: FormBuilder,
    private groupService: GroupService,
    private companyService: CompanyService,
    private userService: UserService,
    private msg: NzMessageService
  ) {}
  ngOnInit(): void {
    this.formGroup = this.fb.group({});
    this.formGroup.addControl('user.companyId', new FormControl(this.user?.companyId));
    this.formGroup.addControl('user.username', new FormControl(this.user?.username));
    this.formGroup.addControl('user.accountExpired', new FormControl(this.user?.accountNonExpired));
    this.formGroup.addControl('user.accountLocked', new FormControl(!this.user?.accountNonLocked));
    this.formGroup.addControl('user.defaultGroupId', new FormControl(this.user?.defaultGroupId));
    this.formGroup.addControl('realName', new FormControl(this.user?.userProfile?.realName));
    this.formGroup.addControl('email', new FormControl(this.user?.userProfile?.email, Validators.email));
    this.formGroup.addControl('phone', new FormControl(this.user?.userProfile?.phone));
    this.accountPrefix = this.user?.company?.shortName || '';
    if (this.mode === 'add-manager' || this.mode === 'add') {
      this.formGroup.controls['user.username'].addValidators(Validators.required);
      this.formGroup.controls['user.username'].addAsyncValidators(this.existingUserValidator());
      this.formGroup.setValue({
        ...this.formGroup.value,
        'user.accountExpired': true,
        'user.accountLocked': false,
      });
    }
    if (this.mode === 'add-manager') {
      this.companyService.search({ pageable: false }).subscribe({
        next: (data) => {
          this.companies = data.dataList;
          this.formGroup.setValue({ ...this.formGroup.value, 'user.companyId': this.companies[0].id });

          this.accountPrefix = this.companies[0].shortName;
        },
      });
    } else if (this.mode === 'update' || this.mode === 'add') {
      if (this.user?.companyId !== undefined && this.user?.companyId !== null) {
        this.changeCompany(this.formGroup.value['user.companyId']);
      }
    } else {
      this.groups = this.user?.groupSet || [];
    }

    let avatar$ = this.userService.currentAvatar();
    if (this.mode === 'update' && this.user !== undefined) {
      avatar$ = this.userService.getAvatar(this.user.id);
    }
    avatar$.subscribe({
      next: (data) => {
        if (data.size > 0) {
          this.avatar = this.sanitizer.bypassSecurityTrustUrl(window.URL.createObjectURL(data));
        }
      },
    });
  }

  changeCompany(companyId: string): void {
    for (const company of this.companies) {
      if (companyId === company.id) {
        this.accountPrefix = company.shortName;
        this.formGroup.controls['user.username'].setValue('');
      }
    }
    this.groupService.search({ companyId: companyId, pageable: false }).subscribe({ next: (data) => (this.groups = data.dataList) });
  }

  existingUserValidator(): AsyncValidatorFn {
    return (control: AbstractControl): Promise<ValidationErrors | null> | Observable<ValidationErrors | null> => {
      return this.userService.exist(this.accountPrefix + control.value).pipe(
        map((exist: boolean) => {
          return exist ? { exist: true } : null;
        })
      );
    };
  }

  setData(): User | null {
    Object.values(this.formGroup.controls).forEach((control) => {
      if (control.invalid) {
        control.markAsDirty();
        control.updateValueAndValidity({ onlySelf: false });
      }
    });
    if (!this.formGroup.valid) {
      return null;
    }
    const locked = Boolean(this.formGroup.value['user.accountLocked']);
    let user = {
      ...this.user,
      accountNonExpired: Boolean(this.formGroup.value['user.accountExpired']),
      accountNonLocked: !locked,
      defaultGroupId: this.formGroup.value['user.defaultGroupId'],
    } as User;

    if (this.mode === 'add-manager' || this.mode === 'add') {
      user = { ...user, username: this.accountPrefix + this.formGroup.value['user.username'] };
      user = { ...user, companyId: this.formGroup.value['user.companyId'] };
    }
    const userProfile = { ...this.user?.userProfile, ...this.formGroup.value, avatarFile: this.avatarFile };
    user.userProfile = userProfile;

    return user;
  }

  upload = (item: NzUploadXHRArgs): Subscription => {
    const file = item.postFile as File;
    return of(true).subscribe({
      next: () => {
        this.loading = false;
        this.avatar = this.sanitizer.bypassSecurityTrustUrl(window.URL.createObjectURL(file));
        this.avatarFile = file;
      },
    });
  };

  beforeUpload = (file: NzUploadFile): Observable<boolean> => {
    return new Observable((observer: Observer<boolean>) => {
      this.avatar = undefined;
      const isJpgOrPng = file.type === 'image/jpeg' || file.type === 'image/png';
      if (!isJpgOrPng) {
        this.msg.error('You can only upload JPG file!');
        observer.complete();
        return;
      }
      const isLt2M = (file.size || Number.MAX_VALUE) / 1024 / 1024 < 2;
      if (!isLt2M) {
        this.msg.error('Image must smaller than 2MB!');
        observer.complete();
        return;
      }
      observer.next(isJpgOrPng && isLt2M);
      observer.complete();
    });
  };

  private getBase64(img: File, callback: (img: string) => void): void {
    const reader = new FileReader();
    reader.addEventListener('load', () => callback((reader.result || '').toString()));
    reader.readAsDataURL(img);
  }
}
