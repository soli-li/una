import { Component, OnInit, ViewChild } from '@angular/core';
import { FormBuilder, FormControl, FormGroup } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Constants } from '@basic/Constants';
import { ConfigurationService } from '@basic/core/configuration.service';
import { Utils } from '@basic/layout/utils';
import { Configuration } from '@basic/models/configuration.model';
import * as userReducer from '@basic/state/user/user-state.reducer';
import { Store } from '@ngrx/store';
import { NzTableQueryParams } from 'ng-zorro-antd/table';
import { ConfigurationDetailComponent, Mode } from './configuration-detail.component';

@Component({
  selector: 'app-una-configuration-manage',
  templateUrl: './configuration-manage.component.html',
  styleUrls: ['./configuration-manage.component.less'],
})
export class ConfigurationManageComponent implements OnInit {
  validateForm!: FormGroup;
  manager = false;
  dataSet: Configuration[] = [];
  selectedConfig?: Configuration;
  searchCondition = {};
  loading = true;
  pageSize = Constants.PAGE_SIZE;
  pageNumber = 1;
  total = 0;
  sort = [] as Array<{
    key: string;
    value: string | 'ascend' | 'descend' | null;
  }>;
  utils = Utils;
  edMap = Constants.EnableDisableMap;
  valueTypeMap = new Map<string, string>();

  detailWidth = 800;
  detailVisible = false;
  detailMode = 'readonly' as Mode;
  @ViewChild('detailModal') detailModal?: ConfigurationDetailComponent;

  constructor(
    private configurationService: ConfigurationService,
    private fb: FormBuilder,
    private route: ActivatedRoute,
    private store: Store<userReducer.AppState>
  ) {}
  ngOnInit(): void {
    this.validateForm = this.fb.group({});
    this.validateForm.addControl(`companyName`, new FormControl());
    this.validateForm.addControl(`name`, new FormControl());
    this.validateForm.addControl(`confKey`, new FormControl());
    this.validateForm.addControl(`status`, new FormControl());
    this.initVar();

    this.valueTypeMap.set('number', '数字');
    this.valueTypeMap.set('date', '日期');
    this.valueTypeMap.set('datetime', '日期时间');
    this.valueTypeMap.set('boolean', '布尔');
    this.valueTypeMap.set('string', '字符');
  }

  private initVar(): void {
    this.manager = false;
    this.detailMode = 'update';
    if (this.route.snapshot.url[0].path === 'config-manage-for-manager') {
      this.manager = true;
    }
  }

  transformValueType(key?: string): string {
    if (key === undefined) {
      return '';
    }
    return this.valueTypeMap.get(key) || '';
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
    this.configurationService.search(queryParams).subscribe({
      next: (data) => {
        this.total = data.total;
        this.loading = false;
        this.dataSet = data.dataList;
      },
    });
  }

  openDetail(data: Configuration): void {
    this.initVar();
    this.selectedConfig = JSON.parse(JSON.stringify(data));
    this.detailVisible = true;
  }

  cleanDailog(): void {
    this.selectedConfig = undefined;
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

    this.configurationService.save(data).subscribe({ next: () => this.onQueryParamsChange() });
    this.cleanDailog();
  }

  replaceAll(data?: string): string | undefined {
    return this.utils.replaceAll('<br/>', '', data);
  }

  add(): void {
    this.detailMode = 'add-manager';
    this.selectedConfig = undefined;
    if (!this.manager) {
      this.detailMode = 'add';
      this.store.select(userReducer.UserSelector.getCompany).subscribe((company) => {
        this.selectedConfig = {} as Configuration;
        this.selectedConfig.company = company;
        this.selectedConfig.companyId = company?.id || '';
      });
    }
    this.detailVisible = true;
  }
  refresh(): void {
    this.configurationService.refresh().subscribe({ next: () => this.onQueryParamsChange() });
  }
}
