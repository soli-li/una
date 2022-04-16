import { Component, Input, OnInit } from '@angular/core';
import { FormBuilder, FormControl, FormGroup } from '@angular/forms';
import { Constants } from '@basic/Constants';
import { CompanyService } from '@basic/core/company.service';
import { PermissionsService } from '@basic/core/permissions.service';
import { RoleService } from '@basic/core/role.service';
import { Utils } from '@basic/layout/utils';
import { Company } from '@basic/models/company.model';
import { Permissions } from '@basic/models/permissions.model';
import { Role } from '@basic/models/role.model';
import { forkJoin, mergeMap, Observable, of } from 'rxjs';

@Component({
  selector: 'app-una-role-perm-relation',
  templateUrl: './role-perm-relation.component.html',
  styleUrls: ['./role-perm-relation.component.less'],
})
export class RolePermRelationComponent implements OnInit {
  @Input() perm?: Permissions;

  formGroup!: FormGroup;
  companies: Company[] = [];
  roleSet: Role[] = [];
  loading = true;
  utils = Utils;
  edMap = Constants.EnableDisableMap;
  checkedSet = new Set<string>();

  constructor(
    private fb: FormBuilder,
    private companyService: CompanyService,
    private roleService: RoleService,
    private permissionsService: PermissionsService
  ) {}
  ngOnInit(): void {
    if (this.perm === undefined || this.perm === null) {
      throw new Error('permissions object is null');
    }
    this.formGroup = this.fb.group({});
    this.formGroup.addControl(`companyId`, new FormControl(null));
    this.companyService.search({ pageable: false }).subscribe({
      next: (data) => {
        this.companies = data.dataList;
        this.formGroup.setValue({ ...this.formGroup.value, companyId: this.companies[0].id });
      },
    });
  }

  companyChange(id: string): void {
    if (this.perm === undefined || this.perm === null) {
      return;
    }
    this.loading = true;
    const all$ = this.roleService.search({ pageable: false, companyId: id });
    const relation$ = this.roleService.findByPerm([this.perm.id], id);
    forkJoin([all$, relation$]).subscribe({
      next: (result) => {
        this.checkedSet = new Set();
        this.roleSet = result[0].dataList;
        const relationRole = result[1];
        relationRole.forEach((role) => this.checkedSet.add(role.id));
        this.loading = false;
      },
    });
  }

  relationChange(id: string, check: boolean): void {
    if (check) {
      this.checkedSet.add(id);
    } else {
      this.checkedSet.delete(id);
    }
  }

  private getCheckedRole(): Role[] {
    const roles = [];
    for (const id of this.checkedSet) {
      for (const role of this.roleSet) {
        if (id === role.id) {
          roles.push(role);
        }
      }
    }
    return roles;
  }

  saveRelation(): Observable<boolean> {
    const roles = this.getCheckedRole();
    if (this.perm === undefined) {
      return of(false);
    }
    const roleIds = [] as string[];
    roles.forEach((o) => roleIds.push(o.id));
    return this.permissionsService.updateRelation(this.perm.id, roleIds, this.formGroup.value['companyId']).pipe(mergeMap(() => of(true)));
  }
}
