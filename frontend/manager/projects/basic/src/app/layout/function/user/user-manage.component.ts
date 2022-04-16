import { Component, OnInit, ViewChild } from '@angular/core';
import { FormBuilder, FormControl, FormGroup } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Constants } from '@basic/Constants';
import { UserService } from '@basic/core/user.service';
import { User } from '@basic/models/user.model';
import * as userReducer from '@basic/state/user/user-state.reducer';
import { Store } from '@ngrx/store';
import { NzTableQueryParams } from 'ng-zorro-antd/table';
import { of } from 'rxjs';
import { FunctionRelationComponent, Mode as RelationMode } from '../function-relation/function-relation.component';
import { Mode, UserDetailComponent } from './user-detail.component';
import { UserOnlineManageComponent } from './user-online-manage.component';

@Component({
  selector: 'app-una-user-manage',
  templateUrl: './user-manage.component.html',
  styleUrls: ['./user-manage.component.less'],
})
export class UserManageComponent implements OnInit {
  validateForm!: FormGroup;
  manager = false;
  dataSet: User[] = [];
  searchCondition = {};
  loading = true;
  pageSize = Constants.PAGE_SIZE;
  pageNumber = 1;
  total = 0;
  sort = [] as Array<{
    key: string;
    value: string | 'ascend' | 'descend' | null;
  }>;
  stateUser: User = {} as User;

  detailWidth = 800;
  detailVisible = false;
  selectedUser?: User;
  detailMode = 'update' as Mode;
  @ViewChild('detailModal') detailModal?: UserDetailComponent;

  relationWidth = 1100;
  relationVisible = false;
  relationMode = 'user' as RelationMode;
  @ViewChild('relationModal') relationModal?: FunctionRelationComponent;

  onlineManageWidth = 800;
  onlineManageVisible = false;
  @ViewChild('onlineManageModal') onlineManageModal?: UserOnlineManageComponent;

  constructor(private userService: UserService, private fb: FormBuilder, private route: ActivatedRoute, private store: Store<userReducer.AppState>) {}
  ngOnInit(): void {
    this.validateForm = this.fb.group({});
    this.validateForm.addControl(`companyName`, new FormControl());
    this.validateForm.addControl(`name`, new FormControl());
    this.validateForm.addControl(`realName`, new FormControl());
    this.initVar();

    this.store.select(userReducer.UserSelector.getUser).subscribe((user) => {
      this.stateUser = user || ({} as User);
    });
  }

  private initVar(): void {
    this.manager = false;
    this.detailMode = 'update';
    if (this.route.snapshot.url[0].path === 'user-manage-for-manager') {
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
    this.userService.search(queryParams).subscribe({
      next: (data) => {
        this.total = data.total;
        this.loading = false;
        this.dataSet = data.dataList;
      },
    });
  }

  openDetail(data: User): void {
    this.initVar();
    this.selectedUser = JSON.parse(JSON.stringify(data));
    this.detailVisible = true;
  }

  cleanDailog(): void {
    this.selectedUser = undefined;
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
    this.userService.save(data, data.userProfile?.avatarFile).subscribe({
      next: () => {
        this.onQueryParamsChange();
        if (this.stateUser.id === data.id) {
          const user = { ...this.stateUser };
          user.userProfile = data.userProfile;
          user.defaultGroupId = data.defaultGroupId;
          this.store.dispatch(userReducer.UserAction.updateUser({ user: user }));
        }
      },
    });
    this.cleanDailog();
  }

  add(): void {
    this.detailMode = 'add-manager';
    this.selectedUser = undefined;
    if (!this.manager) {
      this.detailMode = 'add';
      this.store.select(userReducer.UserSelector.getCompany).subscribe((company) => {
        this.selectedUser = {} as User;
        this.selectedUser.company = company;
        this.selectedUser.companyId = company?.id || '';
      });
    }
    this.detailVisible = true;
  }

  openRelation(data: User): void {
    this.relationVisible = true;
    this.selectedUser = data;
  }

  closeRelation(): void {
    const result = this.relationModal?.saveRelation() || of(false);
    result.subscribe({
      next: (r) => {
        if (r) {
          this.relationVisible = false;
          this.selectedUser = undefined;
        }
      },
    });
  }

  resetPassword(): void {
    if (this.selectedUser !== undefined && this.selectedUser !== null) {
      this.userService.resetPassword(this.selectedUser).subscribe();
    }
    this.cleanDailog();
  }

  openOnlineManage(data: User): void {
    this.selectedUser = data;
    this.onlineManageVisible = true;
  }

  closeOnlineManage(): void {
    this.selectedUser = undefined;
    this.onlineManageVisible = false;
  }
}
