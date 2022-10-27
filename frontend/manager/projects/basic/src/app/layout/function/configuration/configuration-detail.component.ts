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
    this.formGroup.addControl(`valueType`, new FormControl(this.configuration.valueType));
    this.formGroup.addControl(`status`, new FormControl(this.configuration.status));
    this.formGroup.addControl(`remark`, new FormControl(this.configuration.remark));
    this.formGroup.addControl(`confValueBool`, new FormControl());
    this.formGroup.addControl(`confValueDateTime`, new FormControl());
    this.formGroup.addControl(`confValueDate`, new FormControl());
    this.formGroup.addControl(`confValueNum`, new FormControl());
    this.formGroup.addControl(`confValueStr`, new FormControl());

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
    this.changeValueType(this.valueTypeControl, this.configuration?.confValue);
  }

  changeValueType(type: string, value?: string | undefined): void {
    this.valueTypeControl = type;
    const currDate = new Date();
    if (this.valueTypeControl === 'boolean') {
      this.formGroup.setControl('confValueBool', new FormControl(value === undefined ? true : value));
    } else if (this.valueTypeControl === 'datetime') {
      this.formGroup.setControl('confValueDateTime', new FormControl(value === undefined ? currDate : new Date(value)));
    } else if (this.valueTypeControl === 'date') {
      this.formGroup.setControl('confValueDate', new FormControl(value === undefined ? currDate : new Date(value)));
    } else if (this.valueTypeControl === 'number') {
      this.formGroup.setControl('confValueNum', new FormControl(value === undefined ? 0 : value));
    } else if (this.valueTypeControl === 'string') {
      this.formGroup.setControl('confValueStr', new FormControl(value === undefined ? '' : value));
    }
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
      data.confValue = this.formGroup.controls['confValueBool'].value ? Constants.TRUE : Constants.FALSE;
    } else if (this.valueTypeControl === 'datetime') {
      const value = this.formGroup.controls['confValueDateTime'].value;
      data.confValue = value === null ? null : this.datePige.transform(value, 'yyyy-MM-dd HH:mm:ss');
    } else if (this.valueTypeControl === 'date') {
      const value = this.formGroup.controls['confValueDate'].value;
      data.confValue = value === null ? null : this.datePige.transform(value, 'yyyy-MM-dd');
    } else if (this.valueTypeControl === 'number') {
      const value = this.formGroup.controls['confValueNum'].value;
      data.confValue = value === '' ? -1 : value;
    } else if (this.valueTypeControl === 'string') {
      data.confValue = this.formGroup.controls['confValueStr'].value;
    }

    return data;
  }
}
