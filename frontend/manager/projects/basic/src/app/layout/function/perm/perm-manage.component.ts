import { Component, OnInit, ViewChild } from '@angular/core';
import { FormBuilder, FormControl, FormGroup } from '@angular/forms';
import { Constants } from '@basic/Constants';
import { PermissionsService } from '@basic/core/permissions.service';
import { Utils } from '@basic/layout/utils';
import { Permissions } from '@basic/models/permissions.model';
import { NzTableQueryParams } from 'ng-zorro-antd/table';
import { of } from 'rxjs';
import { RolePermRelationComponent } from './role-perm-relation.component';
import { Mode, PermDetailComponent } from './perm-detail.component';

@Component({
  selector: 'app-una-perm-manage',
  templateUrl: './perm-manage.component.html',
  styleUrls: ['./perm-manage.component.less'],
})
export class PermManageComponent implements OnInit {
  validateForm!: FormGroup;
  manager = false;
  dataSet: Permissions[] = [];
  selectedPerm?: Permissions;
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
  @ViewChild('detailModal') detailModal?: PermDetailComponent;

  relationWidth = 800;
  relationVisible = false;
  @ViewChild('relationModal') relationModal?: RolePermRelationComponent;

  constructor(private permissionsService: PermissionsService, private fb: FormBuilder) {}
  ngOnInit(): void {
    this.validateForm = this.fb.group({});
    this.validateForm.addControl(`name`, new FormControl());
    this.validateForm.addControl(`status`, new FormControl());
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
    this.permissionsService.search(queryParams).subscribe({
      next: (data) => {
        this.total = data.total;
        this.loading = false;
        this.dataSet = data.dataList;
      },
    });
  }

  openDetail(data: Permissions): void {
    this.detailMode = 'update';
    this.selectedPerm = JSON.parse(JSON.stringify(data));
    this.detailVisible = true;
  }
  cleanDailog(): void {
    this.selectedPerm = undefined;
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
    this.permissionsService.save(data).subscribe({ next: () => this.onQueryParamsChange() });
    this.cleanDailog();
  }
  add(): void {
    this.selectedPerm = undefined;
    this.detailMode = 'add';
    this.detailVisible = true;
  }

  openRelation(data: Permissions): void {
    this.relationVisible = true;
    this.selectedPerm = data;
  }

  closeRelation(): void {
    const result = this.relationModal?.saveRelation() || of(false);
    result.subscribe({
      next: (r) => {
        if (r) {
          this.relationVisible = false;
          this.selectedPerm = undefined;
        }
      },
    });
  }
}
