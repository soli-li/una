import { Component, Input, OnInit } from '@angular/core';
import { AbstractControl, AsyncValidatorFn, FormBuilder, FormControl, FormGroup, ValidationErrors, Validators } from '@angular/forms';
import { Constants } from '@basic/Constants';
import { PermissionsService } from '@basic/core/permissions.service';
import { Menu } from '@basic/models/menu.model';
import { Permissions } from '@basic/models/permissions.model';
import { NzIconService } from 'ng-zorro-antd/icon';
import { Icon, ThemeType } from '@basic/models/icon.module';
import { Utils } from '@basic/layout/utils';
import { MenuService } from '@basic/core/menu.service';
import { Observable, map, of } from 'rxjs';
import { NzTreeNodeOptions } from 'ng-zorro-antd/tree';

export declare type Mode = 'update' | 'add';

@Component({
  selector: 'app-una-menu-detail',
  templateUrl: './menu-detail.component.html',
  styleUrls: ['./menu-detail.component.less'],
})
export class MenuDetailComponent implements OnInit {
  @Input() menu?: Menu;
  @Input() menus: Menu[] = [];
  @Input() mode: Mode = 'update';
  parent: NzTreeNodeOptions[] = [];
  formGroup!: FormGroup;
  edMap = Constants.EnableDisableMap;
  permissions?: Permissions[];
  icons: Icon[] = [];
  currentIcon = this.menu?.icon || '';
  origUri = '';
  utils = Utils;

  constructor(
    private permissionsService: PermissionsService,
    private nzIconService: NzIconService,
    private fb: FormBuilder,
    private menuService: MenuService
  ) {}
  ngOnInit(): void {
    if (this.menu === undefined || this.menu === null) {
      this.menu = {} as Menu;
    }
    this.menu.status = this.menu.status || Constants.ENABLE;
    this.origUri = this.menu.frontEndUri;
    this.formGroup = this.fb.group({});
    this.formGroup.addControl(`name`, new FormControl(this.menu?.name, Validators.required));
    this.formGroup.addControl(`icon`, new FormControl(this.menu?.icon));
    this.formGroup.addControl(`sort`, new FormControl(this.menu?.sort));
    this.formGroup.addControl(`frontEndUri`, new FormControl(this.menu?.frontEndUri, Validators.required, this.existingUriValidator()));
    this.formGroup.addControl(`parentId`, new FormControl(this.menu?.parentId, [], this.cycleMenuValidator()));
    this.formGroup.addControl(`permissionsId`, new FormControl(this.menu?.permissionsId));
    this.formGroup.addControl(`status`, new FormControl(this.menu?.status, Validators.required));
    this.formGroup.addControl(`remark`, new FormControl(this.menu?.remark));

    const parentMenus = JSON.parse(JSON.stringify(this.menus));
    this.parent.push({ key: '', title: ' ', isLeaf: true });
    this.setParentMenu(parentMenus, this.parent, this.menu?.id);
    this.permissionsService.search({ pageable: false }).subscribe({
      next: (data) => {
        this.permissions = data.dataList;
      },
    });

    this.nzIconService.getCachedIcons().forEach((v) => {
      let theme = 'outline' as ThemeType;
      if (v.theme !== undefined) {
        theme = v.theme;
      }
      this.icons.push({ name: `${v.name}|${theme}`, type: v.name, theme: theme });
    });

    this.currentIcon = this.menu?.icon || '';
  }

  private setParentMenu(parentMenus: Menu[], parent: NzTreeNodeOptions[], currentMenuId?: string): void {
    for (let i = 0; i < parentMenus.length; i++) {
      const m = parentMenus[i];
      if (m.id === currentMenuId) {
        continue;
      }
      const treeNode = { key: m.id, title: m.name, icon: m.icon } as NzTreeNodeOptions;
      if (m.childrenMenuSet !== undefined && m.childrenMenuSet !== null) {
        const childrenTreeNode = [] as NzTreeNodeOptions[];
        treeNode['children'] = childrenTreeNode;
        this.setParentMenu(m.childrenMenuSet, childrenTreeNode, currentMenuId);
      } else {
        treeNode['isLeaf'] = true;
      }
      parent.push(treeNode);
    }
  }

  existingUriValidator(): AsyncValidatorFn {
    return (control: AbstractControl): Promise<ValidationErrors | null> | Observable<ValidationErrors | null> => {
      if (this.mode === 'update' && this.origUri === control.value) {
        return of(null);
      }
      return this.menuService.exist(control.value).pipe(
        map((exist: boolean) => {
          return exist ? { exist: true } : null;
        })
      );
    };
  }

  private cycleMenuValidator(): AsyncValidatorFn {
    return (control: AbstractControl): Promise<ValidationErrors | null> | Observable<ValidationErrors | null> => {
      if (!this.checkCycleMenu(this.menu?.childrenMenuSet, control.value)) {
        return of({ cycle: true });
      }
      return of(null);
    };
  }

  private checkCycleMenu(childrenMenu?: Menu[], parentId?: string): boolean {
    if (parentId === undefined || parentId === null) {
      return true;
    }
    if (childrenMenu === undefined || childrenMenu === null || childrenMenu.length === 0) {
      return true;
    }

    for (const cMenu of childrenMenu) {
      if (cMenu.id === parentId) {
        return false;
      }
      if (!this.checkCycleMenu(cMenu.childrenMenuSet, parentId)) {
        return false;
      }
    }
    return true;
  }

  setData(): Menu | null {
    Object.values(this.formGroup.controls).forEach((control) => {
      if (control.invalid) {
        control.markAsDirty();
        control.updateValueAndValidity({ onlySelf: true });
      }
    });
    if (!this.formGroup.valid) {
      return null;
    }
    return { ...this.menu, ...this.formGroup.value };
  }

  changeIcon(selectedIcon: string): void {
    this.currentIcon = selectedIcon;
  }
}
