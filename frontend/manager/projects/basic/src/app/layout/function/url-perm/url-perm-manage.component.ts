import { Component, OnInit, ViewChild } from '@angular/core';
import { FormBuilder, FormControl, FormGroup } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Constants } from '@basic/Constants';
import { UrlPermService } from '@basic/core/url-perm.service';
import { Utils } from '@basic/layout/utils';
import { UrlPerm } from '@basic/models/url-perm';
import { NzTableQueryParams } from 'ng-zorro-antd/table';
import { Mode, UrlPermDetailComponent } from './url-perm-detail.component';

@Component({
  selector: 'app-una-url-perm-manage',
  templateUrl: './url-perm-manage.component.html',
  styleUrls: ['./url-perm-manage.component.less'],
})
export class UrlPermManageComponent implements OnInit {
  validateForm!: FormGroup;
  manager = false;
  dataSet: UrlPerm[] = [];
  selectedUrlPerm?: UrlPerm;
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
  @ViewChild('detailModal') detailModal?: UrlPermDetailComponent;

  constructor(private urlPermService: UrlPermService, private fb: FormBuilder, private route: ActivatedRoute) {}
  ngOnInit(): void {
    this.validateForm = this.fb.group({});
    this.validateForm.addControl(`name`, new FormControl());
    this.validateForm.addControl(`uri`, new FormControl());
    this.validateForm.addControl(`permissionsId`, new FormControl());
    this.validateForm.addControl(`status`, new FormControl());
    this.route.queryParams.subscribe({
      next: (v) => {
        if (v['type'] === 'manager') {
          this.manager = true;
        }
      },
    });
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
    this.urlPermService.search(queryParams).subscribe({
      next: (data) => {
        this.total = data.total;
        this.loading = false;
        this.dataSet = data.dataList;
      },
    });
  }

  resetForm(): void {
    this.validateForm.reset();
  }

  openDetail(data: UrlPerm): void {
    this.detailMode = 'update';
    this.selectedUrlPerm = JSON.parse(JSON.stringify(data));
    this.detailVisible = true;
  }
  cleanDailog(): void {
    this.selectedUrlPerm = undefined;
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
    this.urlPermService.save(data).subscribe({ next: () => this.onQueryParamsChange() });
    this.cleanDailog();
  }
  add(): void {
    this.selectedUrlPerm = undefined;
    this.detailMode = 'add';
    this.detailVisible = true;
  }
}
