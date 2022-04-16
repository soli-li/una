import { NgModule } from '@angular/core';
import { NZ_ICONS, NzIconModule } from 'ng-zorro-antd/icon';
import { IconDefinition } from '@ant-design/icons-angular';

import {
  UserOutline,
  LockOutline,
  MailOutline,
  AppstoreOutline,
  SettingOutline,
  MenuOutline,
  SafetyCertificateOutline,
  TeamOutline,
  AuditOutline,
  InsuranceOutline,
  HomeOutline,
  UnlockFill,
  LinkOutline,
  // LockTwoTone,
} from '@ant-design/icons-angular/icons';
const outLineIcon: IconDefinition[] = [
  UserOutline,
  LockOutline,
  MailOutline,
  AppstoreOutline,
  SettingOutline,
  MenuOutline,
  SafetyCertificateOutline,
  TeamOutline,
  AuditOutline,
  InsuranceOutline,
  HomeOutline,
  LinkOutline,
];
const fillIcon: IconDefinition[] = [UnlockFill];
// const twoToneIcon: IconDefinition[] = [LockTwoTone];
// const icons: IconDefinition[] = outLineIcon.concat(fillIcon, twoToneIcon);
export const icons: IconDefinition[] = outLineIcon.concat(fillIcon);

@NgModule({
  imports: [NzIconModule.forRoot(icons)],
  exports: [NzIconModule],
  providers: [{ provide: NZ_ICONS, useValue: icons }],
})
export class IconsProviderModule {}
