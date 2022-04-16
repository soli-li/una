import { Component, ViewChild } from '@angular/core';
import { FormBuilder, FormGroup } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Constants } from '@basic/Constants';
import { MenuService } from '@basic/core/menu.service';
import { Utils } from '@basic/layout/utils';
import { Menu } from '@basic/models/menu.model';
import { MenuDetailComponent, Mode } from './menu-detail.component';

@Component({
  selector: 'app-una-menu-manage',
  templateUrl: './menu-manage.component.html',
  styleUrls: ['./menu-manage.component.less'],
})
export class MenuManageComponent {
  validateForm!: FormGroup;
  manager = false;
  dataSet: Menu[] = [];
  selectedMenu?: Menu;
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
  @ViewChild('detailModal') detailModal?: MenuDetailComponent;

  mapOfExpandedData: { [key: string]: Menu[] } = {};

  constructor(private menuService: MenuService, private fb: FormBuilder, private route: ActivatedRoute) {}

  convertTreeToList(root: Menu): Menu[] {
    const stack: Menu[] = [];
    const array: Menu[] = [];
    const hashMap = {};
    stack.push({ ...root, level: 0, expand: true });

    while (stack.length !== 0) {
      let node = stack.pop();
      node = node === undefined ? ({} as Menu) : node;
      this.visitNode(node, hashMap, array);
      if (node.childrenMenuSet) {
        for (let i = node.childrenMenuSet.length - 1; i >= 0; i--) {
          stack.push({ ...node.childrenMenuSet[i], level: node.level === undefined ? 0 : node.level + 1, expand: true, parentMenu: node });
        }
      }
    }

    return array;
  }

  visitNode(node: Menu, hashMap: { [key: string]: boolean }, array: Menu[]): void {
    if (!hashMap[node.id]) {
      hashMap[node.id] = true;
      array.push(node);
    }
  }

  collapse(array: Menu[], data: Menu, $event: boolean): void {
    if (!$event) {
      if (data.childrenMenuSet) {
        data.childrenMenuSet.forEach((d) => {
          let target = array.find((a) => a.id === d.id);
          target = target === undefined ? ({} as Menu) : target;
          target.expand = false;
          this.collapse(array, target, false);
        });
      } else {
        return;
      }
    }
  }

  onQueryParamsChange(): void {
    const searchCondition = { pageable: false };
    this.search(searchCondition);
  }

  search(queryParams: object): void {
    this.loading = true;
    this.menuService.search(queryParams).subscribe({
      next: (data) => {
        this.total = data.total;
        this.loading = false;
        this.dataSet = data.dataList;

        this.dataSet.forEach((item) => {
          this.mapOfExpandedData[item.id] = this.convertTreeToList(item);
        });
      },
    });
  }

  openDetail(data: Menu): void {
    this.detailMode = 'update';
    this.selectedMenu = JSON.parse(JSON.stringify(data));
    this.detailVisible = true;
  }
  cleanDailog(): void {
    this.selectedMenu = undefined;
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
    this.menuService.save(data).subscribe({ next: () => this.onQueryParamsChange() });
    this.cleanDailog();
  }

  add(): void {
    this.selectedMenu = undefined;
    this.detailMode = 'add';
    this.detailVisible = true;
  }
}
