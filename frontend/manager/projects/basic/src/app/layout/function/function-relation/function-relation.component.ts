import { Component, Input, OnInit } from '@angular/core';
import { Constants } from '@basic/Constants';
import { GroupService } from '@basic/core/group.service';
import { PermissionsService } from '@basic/core/permissions.service';
import { RoleService } from '@basic/core/role.service';
import { UserService } from '@basic/core/user.service';
import { Group } from '@basic/models/group.model';
import { Role } from '@basic/models/role.model';
import { User } from '@basic/models/user.model';
import { forkJoin, map, mergeMap, Observable, of } from 'rxjs';

export declare type Mode = 'user' | 'group' | 'role';

interface FunctionItem {
  id: string;
  value: string;
  type: string;
  remark: string;
}

interface Item<T> {
  id: string;
  entity: T;
  disable: boolean;
  checked: boolean;
}

@Component({
  selector: 'app-una-function-relation',
  templateUrl: './function-relation.component.html',
  styleUrls: ['./function-relation.component.less'],
})
export class FunctionRelationComponent implements OnInit {
  @Input() user = {} as User;
  @Input() group = {} as Group;
  @Input() role = {} as Role;
  @Input() mode = 'user' as Mode;

  userItemMap = new Map<string, Item<User>>();
  groupItemMap = new Map<string, Item<Group>>();
  roleItemMap = new Map<string, Item<Role>>();
  functionItemList = [] as FunctionItem[];
  enable = Constants.ENABLE;
  disable = Constants.DISABLE;

  constructor(
    private userService: UserService,
    private groupService: GroupService,
    private roleService: RoleService,
    private permissionService: PermissionsService
  ) {}
  ngOnInit(): void {
    if (this.mode === 'user') {
      const groupSearch$ = this.groupService.search({ pageable: false, companyId: this.user.companyId });
      const roleSearch$ = this.roleService.search({ pageable: false, companyId: this.user.companyId });
      const userGroup$ = this.groupService.findByUser([this.user.id]);
      const userRole$ = this.roleService.findByUser([this.user.id]);
      this.addEntity(this.userItemMap, [this.user], true, true);
      forkJoin([groupSearch$, roleSearch$, userGroup$, userRole$]).subscribe({
        next: (result) => {
          this.addEntity(this.groupItemMap, result[0].dataList, false, false);
          this.addEntity(this.roleItemMap, result[1].dataList, false, false);
          this.setChecked(this.groupItemMap, result[2], true);
          this.setChecked(this.roleItemMap, result[3], true);

          const groupIds = [];
          const defaultGroupId = this.user.defaultGroupId || '';
          if (defaultGroupId !== '') {
            groupIds.push(defaultGroupId);
            if (this.user.defaultGroup !== undefined && this.user.defaultGroup !== null) {
              this.setDisable(this.groupItemMap, [this.user.defaultGroup], true);
            }
          }
          for (const g of result[2]) {
            groupIds.push(g.id);
          }
          if (groupIds.length > 0) {
            this.roleService.findByGroup(groupIds).subscribe({
              next: (data) => {
                this.setDisable(this.roleItemMap, data, true);
                this.findFunctionItemByRole();
              },
            });
          } else {
            this.findFunctionItemByRole();
          }
        },
      });
    } else if (this.mode === 'group') {
      const userSearch$ = this.userService.search({ pageable: false, companyId: this.group.companyId });
      const roleSearch$ = this.roleService.search({ pageable: false, companyId: this.group.companyId });
      const groupUser$ = this.userService.findByGroup([this.group.id]);
      const groupRole$ = this.roleService.findByGroup([this.group.id]);
      this.addEntity(this.groupItemMap, [this.group], true, true);
      forkJoin([userSearch$, roleSearch$, groupUser$, groupRole$]).subscribe({
        next: (result) => {
          this.addEntity(this.userItemMap, result[0].dataList, false, false);
          this.addEntity(this.roleItemMap, result[1].dataList, false, false);
          this.setChecked(this.userItemMap, result[2], true);
          this.setChecked(this.roleItemMap, result[3], true);

          this.findFunctionItemByRole();
        },
      });
    } else if (this.mode === 'role') {
      const userSearch$ = this.userService.search({ pageable: false, companyId: this.role.companyId });
      const groupSearch$ = this.groupService.search({ pageable: false, companyId: this.role.companyId });
      const roleUser$ = this.userService.findByRole([this.role.id]);
      const roleGroup$ = this.groupService.findByRole([this.role.id]);
      this.addEntity(this.roleItemMap, [this.role], true, true);
      this.findFunctionItemByRole();
      forkJoin([userSearch$, groupSearch$, roleUser$, roleGroup$]).subscribe({
        next: (result) => {
          this.addEntity(this.userItemMap, result[0].dataList, false, false);
          this.addEntity(this.groupItemMap, result[1].dataList, false, false);
          this.setChecked(this.userItemMap, result[2], true);
          this.setChecked(this.groupItemMap, result[3], true);

          const groupIds = [];
          for (const g of result[3]) {
            groupIds.push(g.id);
          }
          if (groupIds.length > 0) {
            this.userService.findByGroup(groupIds).subscribe({
              next: (data) => {
                this.setDisable(this.userItemMap, data, true);
              },
            });
          }
        },
      });
    }
  }

  private findFunctionItemByRole(): void {
    const ids = [];
    this.functionItemList = [];
    for (const entity of this.roleItemMap.values()) {
      if (entity.checked || entity.disable) {
        ids.push(entity.id);
      }
    }
    if (ids.length > 0) {
      this.permissionService
        .findFunctionByRole(ids)
        .pipe(map((v) => v as FunctionItem[]))
        .subscribe({
          next: (data) => {
            this.functionItemList = data;
          },
        });
    }
  }

  private setDisable<T extends User | Group | Role>(itemMap: Map<string, Item<T>>, entities: T[], disable: boolean) {
    for (const entity of entities) {
      const item = itemMap.get(entity.id) || ({} as Item<T>);
      item.disable = disable;
    }
  }
  private setChecked<T extends User | Group | Role>(itemMap: Map<string, Item<T>>, entities: T[], checked: boolean) {
    for (const entity of entities) {
      const item = itemMap.get(entity.id) || ({} as Item<T>);
      item.checked = checked;
    }
  }

  private addEntity<T extends User | Group | Role>(itemMap: Map<string, Item<T>>, items: T[], disable: boolean, checked: boolean): void {
    for (const entity of items) {
      itemMap.set(entity.id, { id: entity.id, entity: entity, disable: disable, checked: checked });
    }
  }

  groupChange(): void {
    if (this.mode === 'user') {
      const groupIds = [];
      for (const entity of this.roleItemMap.values()) {
        entity.disable = false;
      }
      for (const entity of this.groupItemMap.values()) {
        if (entity.checked || entity.disable) {
          groupIds.push(entity.id);
        }
      }
      if (groupIds.length > 0) {
        this.roleService.findByGroup(groupIds).subscribe({
          next: (data) => {
            this.setDisable(this.roleItemMap, data, true);
            this.findFunctionItemByRole();
          },
        });
      }
    }
  }
  roleChange(): void {
    this.findFunctionItemByRole();
  }

  private getCheckedUser(): User[] {
    const users = [];
    for (const entry of this.userItemMap.values()) {
      if (entry.checked) {
        users.push(entry.entity);
      }
    }
    return users;
  }

  private getCheckedGroup(): Group[] {
    const groups = [];
    for (const entry of this.groupItemMap.values()) {
      if (entry.checked) {
        groups.push(entry.entity);
      }
    }
    return groups;
  }

  private getCheckedRole(): Role[] {
    const role = [];
    for (const entry of this.roleItemMap.values()) {
      if (entry.checked) {
        role.push(entry.entity);
      }
    }
    return role;
  }

  saveRelation(): Observable<boolean> {
    if (this.mode === 'user') {
      const saveObj = { ...this.user };
      const groups = this.getCheckedGroup();
      const roles = this.getCheckedRole();
      saveObj.groupSet = groups;
      saveObj.roleSet = roles;
      return this.userService.updateRelation(saveObj).pipe(mergeMap(() => of(true)));
    } else if (this.mode === 'group') {
      const saveObj = { ...this.group };
      const users = this.getCheckedUser();
      const roles = this.getCheckedRole();
      saveObj.userSet = users;
      saveObj.roleSet = roles;
      return this.groupService.updateRelation(saveObj).pipe(mergeMap(() => of(true)));
    } else if (this.mode === 'role') {
      const saveObj = { ...this.role };
      const users = this.getCheckedUser();
      const groups = this.getCheckedGroup();
      saveObj.userSet = users;
      saveObj.groupSet = groups;
      return this.roleService.updateRelation(saveObj).pipe(mergeMap(() => of(true)));
    }
    return of(false);
  }
}
