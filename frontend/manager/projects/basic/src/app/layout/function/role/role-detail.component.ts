import { Component, Input, OnInit } from '@angular/core';
import { AbstractControl, AsyncValidatorFn, FormBuilder, FormControl, FormGroup, ValidationErrors, Validators } from '@angular/forms';
import { Constants } from '@basic/Constants';
import { CompanyService } from '@basic/core/company.service';
import { RoleService } from '@basic/core/role.service';
import { Company } from '@basic/models/company.model';
import { Role } from '@basic/models/role.model';
import { Observable, map } from 'rxjs';

export declare type Mode = 'update' | 'add' | 'add-manager';

@Component({
  selector: 'app-una-role-detail',
  templateUrl: './role-detail.component.html',
  styleUrls: ['./role-detail.component.less'],
})
export class RoleDetailComponent implements OnInit {
  @Input() role?: Role;
  @Input() mode: Mode = 'update';
  companies: Company[] = [];

  formGroup!: FormGroup;
  edMap = Constants.EnableDisableMap;

  constructor(private fb: FormBuilder, private companyService: CompanyService, private roleService: RoleService) {}
  ngOnInit(): void {
    if (this.role === undefined || this.role === null) {
      this.role = {} as Role;
    }
    this.role.status = this.role.status || Constants.ENABLE;
    this.formGroup = this.fb.group({});
    this.formGroup.addControl(`companyId`, new FormControl(this.role?.companyId));
    this.formGroup.addControl(`authority`, new FormControl(this.role?.authority));
    this.formGroup.addControl(`status`, new FormControl(this.role?.status));
    this.formGroup.addControl(`remark`, new FormControl(this.role?.remark));

    if (this.mode === 'add' || this.mode === 'add-manager') {
      this.formGroup.controls['authority'].addValidators(Validators.required);
      this.formGroup.controls['authority'].addAsyncValidators(this.existingNameValidator());
    }
    if (this.mode === 'add-manager') {
      this.companyService.search({ pageable: false }).subscribe({
        next: (data) => {
          this.companies = data.dataList;
          this.formGroup.setValue({ ...this.formGroup.value, companyId: this.companies[0].id });
        },
      });
    } else if (this.mode === 'update') {
      this.formGroup.controls['authority'].clearAsyncValidators();
      this.formGroup.controls['authority'].clearValidators();
    }
  }

  existingNameValidator(): AsyncValidatorFn {
    return (control: AbstractControl): Promise<ValidationErrors | null> | Observable<ValidationErrors | null> => {
      return this.roleService.exist(control.value, this.formGroup.value['companyId']).pipe(
        map((exist: boolean) => {
          return exist ? { exist: true } : null;
        })
      );
    };
  }

  changeCompany(): void {
    this.formGroup.controls['authority'].setValue('');
  }

  setData(): Role | null {
    Object.values(this.formGroup.controls).forEach((control) => {
      if (control.invalid) {
        control.markAsDirty();
        control.updateValueAndValidity({ onlySelf: true });
      }
    });
    if (!this.formGroup.valid) {
      return null;
    }
    const obj = { ...this.role, ...this.formGroup.value };
    if (this.mode === 'update') {
      obj.authority = this.role?.authority;
    }
    return obj;
  }
}
