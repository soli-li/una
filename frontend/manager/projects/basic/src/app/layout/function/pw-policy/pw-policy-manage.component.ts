import { Component, OnInit, ViewChild } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { PasswordPolicyService } from '@basic/core/pw-policy.service';
import { Utils } from '@basic/layout/utils';
import { PasswordPolicy } from '@basic/models/password-policy.model';
import { PwPolicyDetailComponent, Mode } from './pw-policy-detail.component';

@Component({
  selector: 'app-una-pw-plicy-manage',
  templateUrl: './pw-policy-manage.component.html',
  styleUrls: ['./pw-policy-manage.component.less'],
})
export class PwPolicyManageComponent implements OnInit {
  manager = false;
  dataSet: PasswordPolicy[] = [];
  selectedPasswordPolicy?: PasswordPolicy;
  loading = true;
  utils = Utils;

  detailWidth = 800;
  detailVisible = false;
  detailMode = 'readonly' as Mode;
  @ViewChild('detailModal') detailModal?: PwPolicyDetailComponent;

  constructor(private passwordPolicyService: PasswordPolicyService, private route: ActivatedRoute) {}
  ngOnInit(): void {
    this.initVar();
    this.search();
  }

  private initVar(): void {
    this.manager = false;
    this.detailMode = 'readonly';
    if (this.route.snapshot.url[0].path === 'pwPolicy-manage-for-manager') {
      this.manager = true;
      this.detailMode = 'update';
    }
  }

  search(): void {
    this.loading = true;
    this.passwordPolicyService.all().subscribe({
      next: (data) => {
        this.loading = false;
        this.dataSet = data;
      },
    });
  }

  openDetail(data: PasswordPolicy): void {
    this.initVar();
    this.selectedPasswordPolicy = JSON.parse(JSON.stringify(data));
    this.detailVisible = true;
  }

  cleanDailog(): void {
    this.selectedPasswordPolicy = undefined;
    this.detailVisible = false;
  }
  handleCancel(): void {
    this.cleanDailog();
  }
  handleOk(): void {
    if (this.manager === false) {
      this.cleanDailog();
      return;
    }
    const data = this.detailModal?.setData();
    if (data === undefined || data === null) {
      return;
    }
    data.description = this.utils.replaceAll('\n', '<br/>', data.description) || '';

    this.passwordPolicyService.save(data).subscribe({ next: () => this.search() });
    this.cleanDailog();
  }

  replaceAll(data?: string): string | undefined {
    return this.utils.replaceAll('<br/>', '', data);
  }

  add(): void {
    this.selectedPasswordPolicy = undefined;
    this.detailMode = 'add';
    this.detailVisible = true;
  }
}
