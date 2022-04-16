import { Component, OnInit, ViewChild } from '@angular/core';
import { FormGroup, FormBuilder, FormControl } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { CompanyService } from '@basic/core/company.service';
import { Company } from '@basic/models/company.model';
import { CompanyDetailComponent, Mode } from './company-detail.component';
import { NzTableQueryParams } from 'ng-zorro-antd/table';
import { Constants } from '@basic/Constants';
import { Utils } from '@basic/layout/utils';

@Component({
  selector: 'app-una-company-manage',
  templateUrl: './company-manage.component.html',
  styleUrls: ['./company-manage.component.less'],
})
export class CompanyManageComponent implements OnInit {
  validateForm!: FormGroup;
  manager = false;
  dataSet: Company[] = [];
  selectedCompany?: Company;
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

  detailWidth = 800;
  detailVisible = false;
  detailMode = 'update' as Mode;
  @ViewChild('detailModal') detailModal?: CompanyDetailComponent;

  constructor(private companyService: CompanyService, private fb: FormBuilder, private route: ActivatedRoute) {}
  ngOnInit(): void {
    this.validateForm = this.fb.group({});
    this.validateForm.addControl(`shortName`, new FormControl());
    this.validateForm.addControl(`name`, new FormControl());
    this.validateForm.addControl(`legalPerson`, new FormControl());
    this.validateForm.addControl(`status`, new FormControl());
    this.initVar();
  }

  private initVar(): void {
    this.manager = false;
    this.detailMode = 'update';
    if (this.route.snapshot.url[0].path === 'company-manage-for-manager') {
      this.manager = true;
      this.detailMode = 'update-manager';
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
    this.companyService.search(queryParams).subscribe({
      next: (data) => {
        this.total = data.total;
        this.loading = false;
        this.dataSet = data.dataList;
      },
    });
  }

  openDetail(data: Company): void {
    this.initVar();
    this.selectedCompany = JSON.parse(JSON.stringify(data));
    this.detailVisible = true;
  }

  cleanDailog(): void {
    this.selectedCompany = undefined;
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

    this.companyService.save(data).subscribe({ next: () => this.onQueryParamsChange() });
    this.cleanDailog();
  }

  add(): void {
    this.selectedCompany = undefined;
    this.detailMode = 'add';
    this.detailVisible = true;
  }
}
