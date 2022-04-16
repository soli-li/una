import { Component, OnInit } from '@angular/core';
import { AbstractControl, FormBuilder, FormControl, FormGroup, ValidationErrors, ValidatorFn, Validators } from '@angular/forms';
import { Title } from '@angular/platform-browser';
import { Router } from '@angular/router';
import { Constants } from '@basic/Constants';
import { LoginService } from '@basic/core/login.service';
import { NzMessageService } from 'ng-zorro-antd/message';

@Component({
  selector: 'app-una-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.less'],
})
export class LoginComponent implements OnInit {
  validateForm!: FormGroup;
  elementDisabled = false;
  errorModalVisible = false;
  errorCode = '';
  private title = 'Login';
  formGroup!: FormGroup;

  changePasswordWidth = 350;
  changePasswordVisible = false;
  showErrorMsg = false;
  errorMsg: string[] = [];

  token = '';

  constructor(
    private fb: FormBuilder,
    private loginService: LoginService,
    private titleService: Title,
    private router: Router,
    private nzMessageService: NzMessageService
  ) {}

  ngOnInit(): void {
    let username = null;
    const rememberMe = localStorage.getItem('rememberMe') || false;
    if (rememberMe === 'true') {
      username = localStorage.getItem('username') || '';
    }
    this.validateForm = this.fb.group({
      userName: [username, [Validators.required]],
      password: [null, [Validators.required]],
      remember: [rememberMe],
    });
    this.titleService.setTitle(this.title);

    this.formGroup = this.fb.group({});
    this.formGroup.addControl(`newPassword`, new FormControl('', Validators.required));
    this.formGroup.addControl(`againNewPassword`, new FormControl('', Validators.required));

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

  submitForm(): void {
    if (this.validateForm.valid) {
      this.elementDisabled = true;
      const formValue = this.validateForm.value;
      const result$ = this.loginService.login(formValue.userName, formValue.password);
      result$.subscribe({
        next: (resp) => {
          const body = resp.body;
          if (body === null) {
            throw new Error('body is null');
          }
          if (body['status' as keyof object] === 'success') {
            let username = null;
            if (formValue.remember) {
              username = formValue.userName;
            }
            localStorage.setItem('rememberMe', formValue.remember);
            localStorage.setItem('username', username);
            this.elementDisabled = false;
            this.router.navigateByUrl(Constants.ROUTER_MAIN);
          } else if (body['status' as keyof object] === 'failure') {
            this.errorCode = body['errorCode' as keyof object];
            if (this.errorCode === 'L00106') {
              this.changePasswordVisible = true;
              this.token = resp.headers.get(Constants.HEAD_TOKEN) || '';
            } else {
              this.errorModalVisible = true;
              this.elementDisabled = false;
            }
          }
        },
        error: () => {
          this.errorModalVisible = true;
          this.elementDisabled = false;
        },
      });
    } else {
      Object.values(this.validateForm.controls).forEach((control) => {
        if (control.invalid) {
          control.markAsDirty();
          control.updateValueAndValidity({ onlySelf: true });
        }
      });
    }
  }

  changePasswordCancel(): void {
    this.changePasswordVisible = false;
    this.elementDisabled = false;
    this.formGroup.reset();
    this.validateForm.reset();
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
    const username = this.validateForm.value['userName'];
    const oldPassword = this.validateForm.value['password'];
    const newPassword = this.formGroup.value['newPassword'];
    this.errorMsg = [];
    this.loginService.resetPassword(username, oldPassword, newPassword, this.token).subscribe({
      next: (data) => {
        const respCode = data['code' as keyof object];
        const respMsg = data['msg' as keyof object];
        if (respCode === '000000') {
          window.location.reload();
        } else if (respCode === 'P00200') {
          this.showErrorMsg = true;
          const errorMsg = JSON.parse(respMsg).description;
          if (errorMsg !== undefined) {
            this.errorMsg = errorMsg.split('<br/>');
          }
        } else {
          this.nzMessageService.error(respMsg);
        }
      },
    });
  }
}
