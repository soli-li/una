import { Component, OnInit, ViewChild } from '@angular/core';
import { FormBuilder, FormControl, FormGroup } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Constants } from '@basic/Constants';
import { GroupService } from '@basic/core/group.service';
import { Utils } from '@basic/layout/utils';
import { Group } from '@basic/models/group.model';
import * as userReducer from '@basic/state/user/user-state.reducer';
import { Store } from '@ngrx/store';
import { NzTableQueryParams } from 'ng-zorro-antd/table';
import { of } from 'rxjs';
import { Mode as RelationMode, FunctionRelationComponent } from '../function-relation/function-relation.component';
import { GroupDetailComponent, Mode } from './group-detail.component';

@Component({
  selector: 'app-una-group-manage',
  templateUrl: './group-manage.component.html',
  styleUrls: ['./group-manage.component.less'],
})
export class GroupManageComponent implements OnInit {
  validateForm!: FormGroup;
  manager = false;
  dataSet: Group[] = [];
  selectedGroup?: Group;
  searchCondition = {};
  loading = true;
  pageSize = Constants.PAGE_SIZE;
  pageNumber = 1;
  total = 1;
  sort = [] as Array<{
    key: string;
    value: string | 'ascend' | 'descend' | null;
  }>;
  parentGroups?: Group[] = [];
  utils = Utils;
  edMap = Constants.EnableDisableMap;

  detailWidth = 800;
  detailVisible = false;
  detailMode = 'update' as Mode;
  @ViewChild('detailModal') detailModal?: GroupDetailComponent;

  relationWidth = 1100;
  relationVisible = false;
  relationMode = 'group' as RelationMode;
  @ViewChild('relationModal') relationModal?: FunctionRelationComponent;

  constructor(private groupService: GroupService, private fb: FormBuilder, private route: ActivatedRoute, private store: Store<userReducer.AppState>) {}
  ngOnInit(): void {
    this.validateForm = this.fb.group({});
    this.validateForm.addControl(`companyName`, new FormControl());
    this.validateForm.addControl(`name`, new FormControl());
    this.validateForm.addControl(`status`, new FormControl());
    this.validateForm.addControl(`parentId`, new FormControl());
    this.initVar();
    this.groupService.search({ pageable: false }).subscribe({
      next: (data) => {
        this.parentGroups = data.dataList;
      },
    });
  }

  private initVar(): void {
    this.manager = false;
    this.detailMode = 'update';
    if (this.route.snapshot.url[0].path === 'group-manage-for-manager') {
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
    this.groupService.search(queryParams).subscribe({
      next: (data) => {
        this.total = data.total;
        this.loading = false;
        this.dataSet = data.dataList;
      },
    });
  }

  openDetail(data: Group): void {
    this.initVar();
    this.selectedGroup = JSON.parse(JSON.stringify(data));
    this.detailVisible = true;
  }

  cleanDailog(): void {
    this.selectedGroup = undefined;
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
    this.groupService.save(data).subscribe({ next: () => this.onQueryParamsChange() });
    this.cleanDailog();
  }

  add(): void {
    this.detailMode = 'add-manager';
    this.selectedGroup = undefined;
    if (!this.manager) {
      this.detailMode = 'add';
      this.store.select(userReducer.UserSelector.getCompany).subscribe((company) => {
        this.selectedGroup = {} as Group;
        this.selectedGroup.company = company;
        this.selectedGroup.companyId = company?.id || '';
      });
    }
    this.detailVisible = true;
  }

  openRelation(data: Group): void {
    this.relationVisible = true;
    this.selectedGroup = data;
  }

  closeRelation(): void {
    const result = this.relationModal?.saveRelation() || of(false);
    result.subscribe({
      next: (r) => {
        if (r) {
          this.relationVisible = false;
          this.selectedGroup = undefined;
        }
      },
    });
  }
}
