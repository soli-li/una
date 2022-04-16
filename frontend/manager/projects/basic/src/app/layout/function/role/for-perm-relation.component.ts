import { Component, Input, OnInit } from '@angular/core';
import { Constants } from '@basic/Constants';
import { PermissionsService } from '@basic/core/permissions.service';
import { RoleService } from '@basic/core/role.service';
import { Utils } from '@basic/layout/utils';
import { Company } from '@basic/models/company.model';
import { Permissions } from '@basic/models/permissions.model';
import { Role } from '@basic/models/role.model';
import { forkJoin, mergeMap, Observable, of } from 'rxjs';

@Component({
  selector: 'app-una-for-perm-relation',
  templateUrl: './for-perm-relation.component.html',
  styleUrls: ['./for-perm-relation.component.less'],
})
export class ForPermRelationComponent implements OnInit {
  @Input() role?: Role;
  loading = false;
  companies: Company[] = [];
  permissioinsSet: Permissions[] = [];
  utils = Utils;
  edMap = Constants.EnableDisableMap;
  checkedSet = new Set<string>();

  constructor(private roleService: RoleService, private permissionsService: PermissionsService) {}
  ngOnInit(): void {
    if (this.role === undefined || this.role === null) {
      throw new Error('role object is null');
    }
    const all$ = this.permissionsService.search({ pageable: false });
    const relation$ = this.permissionsService.findByRole([this.role.id]);
    forkJoin([all$, relation$]).subscribe({
      next: (result) => {
        this.permissioinsSet = result[0].dataList;
        result[1].forEach((perm) => this.checkedSet.add(perm.id));
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

  private getCheckedPerm(): Permissions[] {
    const permissions = [];
    for (const id of this.checkedSet) {
      for (const perm of this.permissioinsSet) {
        if (id === perm.id) {
          permissions.push(perm);
        }
      }
    }
    return permissions;
  }

  saveRelation(): Observable<boolean> {
    if (this.role === undefined) {
      return of(false);
    }

    const perms = this.getCheckedPerm();
    this.role.permissionsSet = perms;

    return this.roleService.updateRelationForPerm(this.role).pipe(mergeMap(() => of(true)));
  }
}
