import { Component, OnInit, ViewChild } from '@angular/core';
import { FormBuilder, FormControl, FormGroup } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Constants } from '@basic/Constants';
import { RoleService } from '@basic/core/role.service';
import { Utils } from '@basic/layout/utils';
import { Role } from '@basic/models/role.model';
import * as userReducer from '@basic/state/user/user-state.reducer';
import { Store } from '@ngrx/store';
import { NzTableQueryParams } from 'ng-zorro-antd/table';
import { of } from 'rxjs';
import { FunctionRelationComponent, Mode as RelationMode } from '../function-relation/function-relation.component';
import { ForPermRelationComponent } from './for-perm-relation.component';
import { Mode, RoleDetailComponent } from './role-detail.component';

@Component({
  selector: 'app-una-role-manage',
  templateUrl: './role-manage.component.html',
  styleUrls: ['./role-manage.component.less'],
})
export class RoleManageComponent implements OnInit {
  validateForm!: FormGroup;
  manager = false;
  dataSet: Role[] = [];
  selectedRole?: Role;
  searchCondition = {};
  loading = true;
  pageSize = Constants.PAGE_SIZE;
  pageNumber = 1;
  total = 1;
  sort = [] as Array<{
    key: string;
    value: string | 'ascend' | 'descend' | null;
  }>;
  utils = Utils;
  edMap = Constants.EnableDisableMap;

  detailWidth = 800;
  detailVisible = false;
  detailMode = 'update' as Mode;
  @ViewChild('detailModal') detailModal?: RoleDetailComponent;

  relationWidth = 1100;
  relationVisible = false;
  relationMode = 'role' as RelationMode;
  @ViewChild('relationModal') relationModal?: FunctionRelationComponent;

  permRelationWidth = 800;
  permRelationVisible = false;
  @ViewChild('permRelationModal') permRelationModal?: ForPermRelationComponent;

  constructor(private roleService: RoleService, private fb: FormBuilder, private route: ActivatedRoute, private store: Store<userReducer.AppState>) {}
  ngOnInit(): void {
    this.validateForm = this.fb.group({});
    this.validateForm.addControl(`companyName`, new FormControl());
    this.validateForm.addControl(`authority`, new FormControl());
    this.validateForm.addControl(`status`, new FormControl());
    this.initVar();
  }

  private initVar(): void {
    this.manager = false;
    this.detailMode = 'update';
    if (this.route.snapshot.url[0].path === 'role-manage-for-manager') {
      this.manager = true;
    }
  }

  onQueryParamsChange(params?: NzTableQueryParams): void {
    if (params !== undefined) {
      this.pageSize = params.pageSize;
      this.pageNumber = params.pageIndex;
      this.sort = params.sort;
    }
    const searchCondition = { ...this.searchCondition, pageSize: this.pageSize, pageNumber: this.pageNumber, sort: this.sort };
    this.search(searchCondition);
  }

  search(queryParams: object): void {
    this.loading = true;
    this.roleService.search(queryParams).subscribe({
      next: (data) => {
        this.total = data.total;
        this.loading = false;
        this.dataSet = data.dataList;
      },
    });
  }

  openDetail(data: Role): void {
    this.initVar();
    this.selectedRole = JSON.parse(JSON.stringify(data));
    this.detailVisible = true;
  }

  cleanDailog(): void {
    this.selectedRole = undefined;
    this.detailVisible = false;
  }
  handleCancel(): void {
    this.cleanDailog();
  }
  handleOk(): void {
    const data = this.detailModal?.setData();
    if (data === undefined || data === null) {
      return;
    }
    this.roleService.save(data).subscribe({ next: () => this.onQueryParamsChange() });
    this.cleanDailog();
  }

  add(): void {
    this.detailMode = 'add-manager';
    this.selectedRole = undefined;
    if (!this.manager) {
      this.detailMode = 'add';
      this.store.select(userReducer.UserSelector.getCompany).subscribe((company) => {
        this.selectedRole = {} as Role;
        this.selectedRole.company = company;
        this.selectedRole.companyId = company?.id || '';
      });
    }
    this.detailVisible = true;
  }

  openRelation(data: Role): void {
    this.relationVisible = true;
    this.selectedRole = data;
  }

  openPermRelation(data: Role): void {
    this.permRelationVisible = true;
    this.selectedRole = data;
  }

  closeRelation(): void {
    const result = this.relationModal?.saveRelation() || of(false);
    result.subscribe({
      next: (r) => {
        if (r) {
          this.relationVisible = false;
          this.selectedRole = undefined;
        }
      },
    });
  }
  closePermRelation(): void {
    const result = this.permRelationModal?.saveRelation() || of(false);
    result.subscribe({
      next: (r) => {
        if (r) {
          this.permRelationVisible = false;
          this.selectedRole = undefined;
        }
      },
    });
  }
}
