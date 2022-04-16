import { Component, Input, OnInit } from '@angular/core';
import { FormBuilder, FormControl, FormGroup, Validators } from '@angular/forms';
import { Utils } from '@basic/layout/utils';
import { PasswordPolicy } from '@basic/models/password-policy.model';

export declare type Mode = 'readonly' | 'update' | 'add';
@Component({
  selector: 'app-una-pw-policy-detail',
  templateUrl: './pw-policy-detail.component.html',
  styleUrls: ['./pw-policy-detail.component.less'],
})
export class PwPolicyDetailComponent implements OnInit {
  @Input() passwordPolicy?: PasswordPolicy;
  @Input() mode: Mode = 'readonly';

  readonly = true;
  formGroup!: FormGroup;
  utils = Utils;

  constructor(private fb: FormBuilder) {}
  ngOnInit(): void {
    if (this.passwordPolicy === undefined) {
      this.passwordPolicy = {
        letters: true,
        caseSensitive: true,
        digitals: true,
        nonAlphanumeric: true,
        length: 8,
        maximumAge: 90,
        repeatCount: 3,
      } as PasswordPolicy;
    }
    this.formGroup = this.fb.group({});
    this.formGroup.addControl(`label`, new FormControl(this.passwordPolicy?.label, Validators.required));
    this.formGroup.addControl(`description`, new FormControl(this.utils.replaceAll('<br/>', '\n', this.passwordPolicy?.description)));
    this.formGroup.addControl(`letters`, new FormControl(this.passwordPolicy?.letters));
    this.formGroup.addControl(`caseSensitive`, new FormControl(this.passwordPolicy?.caseSensitive));
    this.formGroup.addControl(`digitals`, new FormControl(this.passwordPolicy?.digitals));
    this.formGroup.addControl(`nonAlphanumeric`, new FormControl(this.passwordPolicy?.nonAlphanumeric));
    this.formGroup.addControl(`length`, new FormControl(this.passwordPolicy?.length));
    this.formGroup.addControl(`maximumAge`, new FormControl(this.passwordPolicy?.maximumAge));
    this.formGroup.addControl(`repeatCount`, new FormControl(this.passwordPolicy?.repeatCount));
    this.formGroup.addControl(`triesCount`, new FormControl(this.passwordPolicy?.triesCount));
    this.readonly = this.mode === 'readonly';
  }

  setData(): PasswordPolicy | null {
    Object.values(this.formGroup.controls).forEach((control) => {
      if (control.invalid) {
        control.markAsDirty();
        control.updateValueAndValidity({ onlySelf: true });
      }
    });
    if (!this.formGroup.valid) {
      return null;
    }
    const data = { ...this.passwordPolicy, ...this.formGroup.value };
    return data;
  }
}
