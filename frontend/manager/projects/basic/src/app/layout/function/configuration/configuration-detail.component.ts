import { DatePipe } from '@angular/common';
import { Component, Input, OnInit } from '@angular/core';
import { AbstractControl, AsyncValidatorFn, FormBuilder, FormControl, FormGroup, ValidationErrors, Validators } from '@angular/forms';
import { Constants } from '@basic/Constants';
import { CompanyService } from '@basic/core/company.service';
import { ConfigurationService } from '@basic/core/configuration.service';
import { Utils } from '@basic/layout/utils';
import { Company } from '@basic/models/company.model';
import { Configuration } from '@basic/models/configuration.model';
import { map, Observable, of } from 'rxjs';

export declare type Mode = 'update' | 'add' | 'add-manager';

@Component({
  selector: 'app-una-configuration-detail',
  templateUrl: './configuration-detail.component.html',
  styleUrls: ['./configuration-detail.component.less'],
})
export class ConfigurationDetailComponent implements OnInit {
  @Input() configuration?: Configuration;
  @Input() mode: Mode = 'update';
  @Input() valueTypeMap = new Map<string, string>();

  companies = [] as Company[];
  formGroup!: FormGroup;
  utils = Utils;
  edMap = Constants.EnableDisableMap;
  valueTypeControl = 'string';
  currConfKey = '';

  constructor(
    private fb: FormBuilder,
    private datePige: DatePipe,
    private companyService: CompanyService,
    private configurationService: ConfigurationService
  ) {}
  ngOnInit(): void {
    if (this.configuration === undefined) {
      this.configuration = {} as Configuration;
      this.configuration.valueType = this.valueTypeControl;
    }

    this.configuration.status = this.configuration.status || Constants.ENABLE;
    this.formGroup = this.fb.group({});
    this.formGroup.addControl(`companyId`, new FormControl(this.configuration.companyId));
    this.formGroup.addControl(`name`, new FormControl(this.configuration.name));
    this.formGroup.addControl(`confKey`, new FormControl(this.configuration.confKey));
    this.formGroup.addControl(`confValue`, new FormControl(this.configuration.confValue));
    this.formGroup.addControl(`valueType`, new FormControl(this.configuration.valueType));
    this.formGroup.addControl(`status`, new FormControl(this.configuration.status));
    this.formGroup.addControl(`remark`, new FormControl(this.configuration.remark));

    if (this.mode === 'add-manager') {
      this.companyService.search({ pageable: false }).subscribe({
        next: (data) => {
          this.companies = data.dataList;
          this.formGroup.setValue({ ...this.formGroup.value, companyId: this.companies[0].id });
        },
      });
    } else if (this.mode === 'update') {
      this.currConfKey = this.configuration.confKey;
    }
    this.formGroup.controls['confKey'].addValidators(Validators.required);
    this.formGroup.controls['confKey'].addAsyncValidators(this.existingConfKeyValidator());
    this.valueTypeControl = this.configuration.valueType || 'string';
  }

  changeValueType(type: string): void {
    const currDate = new Date();
    if (type === 'date') {
      this.formGroup.controls['confValue'].setValue(currDate);
    } else if (type === 'datetime') {
      this.formGroup.controls['confValue'].setValue(currDate);
    } else if (type === 'number') {
      this.formGroup.controls['confValue'].setValue(0);
    } else {
      this.formGroup.controls['confValue'].setValue('');
    }
    this.valueTypeControl = type;
  }

  changeCompany(): void {
    this.formGroup.controls['confKey'].setValue('');
  }

  existingConfKeyValidator(): AsyncValidatorFn {
    return (control: AbstractControl): Promise<ValidationErrors | null> | Observable<ValidationErrors | null> => {
      if (this.mode === 'update' && this.currConfKey === this.formGroup.controls['confKey'].value) {
        return of(null);
      }
      return this.configurationService.exist(this.formGroup.value['companyId'], control.value).pipe(
        map((exist: boolean) => {
          return exist ? { exist: true } : null;
        })
      );
    };
  }

  setData(): Configuration | null {
    if (this.mode === 'update' && this.currConfKey === this.formGroup.controls['confKey'].value) {
      this.formGroup.controls['confKey'].setErrors(null);
    }
    Object.values(this.formGroup.controls).forEach((control) => {
      if (control.invalid) {
        control.markAsDirty();
        control.updateValueAndValidity({ onlySelf: true });
      }
    });
    if (!this.formGroup.valid) {
      return null;
    }
    const data = { ...this.configuration, ...this.formGroup.value };
    if (this.valueTypeControl === 'boolean') {
      if (data.confValue === true) {
        data.confValue = Constants.TRUE;
      } else {
        data.confValue = Constants.FALSE;
      }
    }
    return data;
  }
}
