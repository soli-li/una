import { Component, Input, OnInit } from '@angular/core';
import { AbstractControl, AsyncValidatorFn, FormBuilder, FormControl, FormGroup, ValidationErrors, Validators } from '@angular/forms';
import { Constants } from '@basic/Constants';
import { CompanyService } from '@basic/core/company.service';
import { GroupService } from '@basic/core/group.service';
import { Company } from '@basic/models/company.model';
import { Group } from '@basic/models/group.model';
import { Observable, map } from 'rxjs';

export declare type Mode = 'update' | 'add' | 'add-manager';

@Component({
  selector: 'app-una-group-detail',
  templateUrl: './group-detail.component.html',
  styleUrls: ['./group-detail.component.less'],
})
export class GroupDetailComponent implements OnInit {
  @Input() group?: Group;
  @Input() mode: Mode = 'update';
  companies: Company[] = [];
  parentGroups: Group[] = [];

  formGroup!: FormGroup;
  edMap = Constants.EnableDisableMap;

  constructor(private fb: FormBuilder, private groupService: GroupService, private companyService: CompanyService) {}
  ngOnInit(): void {
    if (this.group === undefined || this.group === null) {
      this.group = {} as Group;
    }
    this.group.status = this.group.status || Constants.ENABLE;
    this.formGroup = this.fb.group({});
    this.formGroup.addControl(`companyId`, new FormControl(this.group?.companyId));
    this.formGroup.addControl(`name`, new FormControl(this.group?.name));
    this.formGroup.addControl(`status`, new FormControl(this.group?.status));
    this.formGroup.addControl(`parentId`, new FormControl(this.group?.parentId));
    this.formGroup.addControl(`remark`, new FormControl(this.group?.remark));

    if (this.mode === 'add' || this.mode === 'add-manager') {
      this.formGroup.controls['name'].addValidators(Validators.required);
      this.formGroup.controls['name'].addAsyncValidators(this.existingNameValidator());
    }
    if (this.mode === 'add-manager') {
      this.companyService.search({ pageable: false }).subscribe({
        next: (data) => {
          this.companies = data.dataList;
          this.formGroup.setValue({ ...this.formGroup.value, companyId: this.companies[0].id });
          this.changeCompany(this.companies[0].id);
        },
      });
    } else if (this.mode === 'add') {
      this.changeCompany();
    } else {
      this.changeCompany(undefined, () => {
        const groups = this.parentGroups;
        this.parentGroups = [];
        for (const group of groups) {
          if (this.group?.id !== group.id) {
            this.parentGroups.push(group);
          }
        }
      });
      this.formGroup.controls['name'].clearValidators();
      this.formGroup.controls['name'].clearAsyncValidators();
    }
  }

  existingNameValidator(): AsyncValidatorFn {
    return (control: AbstractControl): Promise<ValidationErrors | null> | Observable<ValidationErrors | null> => {
      return this.groupService.exist(control.value, this.formGroup.value['companyId']).pipe(
        map((exist: boolean) => {
          return exist ? { exist: true } : null;
        })
      );
    };
  }

  changeCompany(companyId?: string, callback?: () => void): void {
    this.groupService.search({ pageable: false, companyId: companyId }).subscribe({
      next: (data) => {
        this.parentGroups = data.dataList;
        this.formGroup.controls['name'].setValue('');
        if (callback !== undefined && callback !== null) {
          callback();
        }
      },
    });
  }

  setData(): Group | null {
    Object.values(this.formGroup.controls).forEach((control) => {
      if (control.invalid) {
        control.markAsDirty();
        control.updateValueAndValidity({ onlySelf: true });
      }
    });
    if (!this.formGroup.valid) {
      return null;
    }
    const obj = { ...this.group, ...this.formGroup.value };
    if (this.mode === 'update') {
      obj.name = this.group?.name;
    }
    return obj;
  }
}
