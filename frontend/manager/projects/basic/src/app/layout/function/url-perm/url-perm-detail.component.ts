import { Component, Input, OnInit } from '@angular/core';
import { FormBuilder, FormControl, FormGroup, Validators } from '@angular/forms';
import { Constants } from '@basic/Constants';
import { PermissionsService } from '@basic/core/permissions.service';
import { Permissions } from '@basic/models/permissions.model';
import { UrlPerm } from '@basic/models/url-perm';

export declare type Mode = 'update' | 'add';

@Component({
  selector: 'app-una-url-perm-detail',
  templateUrl: './url-perm-detail.component.html',
  styleUrls: ['./url-perm-detail.component.less'],
})
export class UrlPermDetailComponent implements OnInit {
  @Input() urlPerm?: UrlPerm;
  @Input() mode: Mode = 'update';

  formGroup!: FormGroup;
  edMap = Constants.EnableDisableMap;
  permissions?: Permissions[];

  constructor(private permissionsService: PermissionsService, private fb: FormBuilder) {}
  ngOnInit(): void {
    if (this.urlPerm === undefined || this.urlPerm === null) {
      this.urlPerm = {} as UrlPerm;
    }
    this.urlPerm.status = this.urlPerm.status || Constants.ENABLE;
    this.formGroup = this.fb.group({});
    this.formGroup.addControl(`name`, new FormControl(this.urlPerm?.name, Validators.required));
    this.formGroup.addControl(`uri`, new FormControl(this.urlPerm?.uri, Validators.required));
    this.formGroup.addControl(`permissionsId`, new FormControl(this.urlPerm?.permissionsId));
    this.formGroup.addControl(`sort`, new FormControl(this.urlPerm?.sort));
    this.formGroup.addControl(`status`, new FormControl(this.urlPerm?.status));
    this.formGroup.addControl(`remark`, new FormControl(this.urlPerm?.remark));

    this.permissionsService.search({ pageable: false }).subscribe({
      next: (data) => {
        this.permissions = data.dataList;
      },
    });
  }

  setData(): UrlPerm | null {
    Object.values(this.formGroup.controls).forEach((control) => {
      if (control.invalid) {
        control.markAsDirty();
        control.updateValueAndValidity({ onlySelf: true });
      }
    });
    if (!this.formGroup.valid) {
      return null;
    }
    return { ...this.urlPerm, ...this.formGroup.value };
  }
}
