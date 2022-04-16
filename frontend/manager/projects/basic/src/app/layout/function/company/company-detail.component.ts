import { Component, Input, OnInit } from '@angular/core';
import { AbstractControl, AsyncValidatorFn, FormBuilder, FormControl, FormGroup, ValidationErrors, Validators } from '@angular/forms';
import { Constants } from '@basic/Constants';
import { CompanyService } from '@basic/core/company.service';
import { PasswordPolicyService } from '@basic/core/pw-policy.service';
import { Company } from '@basic/models/company.model';
import { PasswordPolicy } from '@basic/models/password-policy.model';
import { Observable, map } from 'rxjs';

export declare type Mode = 'update-manager' | 'update' | 'add';
@Component({
  selector: 'app-una-company-detail',
  templateUrl: './company-detail.component.html',
  styleUrls: ['./company-detail.component.less'],
})
export class CompanyDetailComponent implements OnInit {
  @Input() company?: Company;
  @Input() mode: Mode = 'update';

  formGroup!: FormGroup;
  passwordPolicies?: PasswordPolicy[];
  edMap = Constants.EnableDisableMap;

  constructor(private fb: FormBuilder, private passwordPolicyService: PasswordPolicyService, private companyService: CompanyService) {}

  ngOnInit(): void {
    if (this.company === undefined) {
      this.company = {} as Company;
    }
    this.company.status = this.company.status || Constants.ENABLE;
    this.formGroup = this.fb.group({});
    this.formGroup.addControl(`name-add`, new FormControl(this.company?.name));
    this.formGroup.addControl(`shortName-add`, new FormControl(this.company?.shortName));
    this.formGroup.addControl(`legalPerson`, new FormControl(this.company?.legalPerson));
    this.formGroup.addControl(`address`, new FormControl(this.company?.address));
    this.formGroup.addControl(`remark`, new FormControl(this.company?.remark));
    this.formGroup.addControl(`pwPolicyId`, new FormControl(this.company?.pwPolicyId));
    this.formGroup.addControl(`status`, new FormControl(this.company?.status));

    if (this.mode === 'add') {
      this.formGroup.controls['name-add'].addValidators(Validators.required);
      this.formGroup.controls['name-add'].addAsyncValidators(this.existingNameValidator());
      this.formGroup.controls['shortName-add'].addValidators(Validators.required);
      this.formGroup.controls['shortName-add'].addAsyncValidators(this.existingShortNameValidator());
    }

    this.passwordPolicyService.all().subscribe({
      next: (data) => {
        this.passwordPolicies = data;

        if (this.mode === 'add') {
          const value = { ...this.formGroup.value, pwPolicyId: this.passwordPolicies[0].id };
          this.formGroup.setValue(value);
        }
      },
    });
  }

  existingNameValidator(): AsyncValidatorFn {
    return (control: AbstractControl): Promise<ValidationErrors | null> | Observable<ValidationErrors | null> => {
      return this.companyService.existName(control.value).pipe(
        map((exist: boolean) => {
          return exist ? { exist: true } : null;
        })
      );
    };
  }

  existingShortNameValidator(): AsyncValidatorFn {
    return (control: AbstractControl): Promise<ValidationErrors | null> | Observable<ValidationErrors | null> => {
      return this.companyService.existShortName(control.value).pipe(
        map((exist: boolean) => {
          return exist ? { exist: true } : null;
        })
      );
    };
  }

  setData(): Company | null {
    Object.values(this.formGroup.controls).forEach((control) => {
      if (control.invalid) {
        control.markAsDirty();
        control.updateValueAndValidity({ onlySelf: true });
      }
    });
    if (!this.formGroup.valid) {
      return null;
    }
    const data = { ...this.company, ...this.formGroup.value };
    let name = this.company?.name;
    let shortName = this.company?.shortName;
    if (this.mode === 'add') {
      name = this.formGroup.value['name-add'];
      shortName = this.formGroup.value['shortName-add'];
    }
    return { ...data, name: name, shortName: shortName };
  }
}
