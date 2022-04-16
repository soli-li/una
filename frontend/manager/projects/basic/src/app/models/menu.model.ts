import { User } from './user.model';
import { Permissions } from './permissions.model';

export interface Menu {
  readonly id: string;
  name: string;
  icon?: string;
  sort?: number;
  frontEndUri: string;
  parentId?: string;
  permissionsId?: string;
  status: string;
  remark?: string;
  createdUserId?: string;
  createdDate?: Date;

  level?: number;
  parentMenu?: Menu;
  permissions?: Permissions;
  createdUser?: User;
  childrenMenuSet?: Menu[];

  selected: boolean;
  param?: object;
  expand?: boolean;
}
