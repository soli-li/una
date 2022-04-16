import { Component, Input, OnInit } from '@angular/core';
import { FormBuilder, FormControl, FormGroup, Validators } from '@angular/forms';
import { Constants } from '@basic/Constants';
import { Permissions } from '@basic/models/permissions.model';

export declare type Mode = 'update' | 'add';

@Component({
  selector: 'app-una-perm-detail',
  templateUrl: './perm-detail.component.html',
  styleUrls: ['./perm-detail.component.less'],
})
export class PermDetailComponent implements OnInit {
  @Input() perm?: Permissions;
  @Input() mode: Mode = 'update';

  formGroup!: FormGroup;
  edMap = Constants.EnableDisableMap;

  constructor(private fb: FormBuilder) {}
  ngOnInit(): void {
    if (this.perm === undefined || this.perm === null) {
      this.perm = {} as Permissions;
    }
    this.perm.status = this.perm.status || Constants.ENABLE;
    this.formGroup = this.fb.group({});
    this.formGroup.addControl(`id`, new FormControl(this.perm?.id));
    this.formGroup.addControl(`name`, new FormControl(this.perm?.name));
    this.formGroup.addControl(`status`, new FormControl(this.perm?.status, Validators.required));
    this.formGroup.addControl(`remark`, new FormControl(this.perm?.remark));
  }

  setData(): Permissions | null {
    Object.values(this.formGroup.controls).forEach((control) => {
      if (control.invalid) {
        control.markAsDirty();
        control.updateValueAndValidity({ onlySelf: true });
      }
    });
    if (!this.formGroup.valid) {
      return null;
    }
    return { ...this.perm, ...this.formGroup.value };
  }
}
