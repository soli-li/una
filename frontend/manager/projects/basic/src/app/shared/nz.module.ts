import { NgModule, LOCALE_ID } from '@angular/core';

import { NzButtonModule } from 'ng-zorro-antd/button';
import { NzCheckboxModule } from 'ng-zorro-antd/checkbox';
import { NzInputModule } from 'ng-zorro-antd/input';
import { NzModalModule } from 'ng-zorro-antd/modal';
import { NzFormModule } from 'ng-zorro-antd/form';
import { NzMessageModule } from 'ng-zorro-antd/message';
import { NzLayoutModule } from 'ng-zorro-antd/layout';
import { NzMenuModule } from 'ng-zorro-antd/menu';
import { NzTableModule } from 'ng-zorro-antd/table';
import { NzDividerModule } from 'ng-zorro-antd/divider';
import { NzSwitchModule } from 'ng-zorro-antd/switch';
import { NzInputNumberModule } from 'ng-zorro-antd/input-number';
import { NzSelectModule } from 'ng-zorro-antd/select';
import { NzPaginationModule } from 'ng-zorro-antd/pagination';
import { NzTreeSelectModule } from 'ng-zorro-antd/tree-select';
import { NzCardModule } from 'ng-zorro-antd/card';
import { NzDatePickerModule } from 'ng-zorro-antd/date-picker';
import { NzTimePickerModule } from 'ng-zorro-antd/time-picker';
import { NzUploadModule } from 'ng-zorro-antd/upload';

import { NZ_I18N, zh_CN, en_US } from 'ng-zorro-antd/i18n';
import { NzConfig, NZ_CONFIG } from 'ng-zorro-antd/core/config';

const ngZorroConfig: NzConfig = {
  message: { nzDuration: 10000 },
};

@NgModule({
  exports: [
    NzButtonModule,
    NzCheckboxModule,
    NzInputModule,
    NzModalModule,
    NzFormModule,
    NzMessageModule,
    NzLayoutModule,
    NzMenuModule,
    NzTableModule,
    NzDividerModule,
    NzSwitchModule,
    NzInputNumberModule,
    NzSelectModule,
    NzPaginationModule,
    NzTreeSelectModule,
    NzCardModule,
    NzDatePickerModule,
    NzTimePickerModule,
    NzUploadModule,
  ],
  providers: [
    {
      provide: NZ_I18N,
      useFactory: (localId: string) => {
        switch (localId) {
          case 'en':
            return en_US;
          /** 与 angular.json i18n/locales 配置一致 **/
          case 'zh':
            return zh_CN;
          default:
            return zh_CN;
        }
      },
    },
    { provide: NZ_CONFIG, useValue: ngZorroConfig },
  ],
})
export class NzModule {}
