import { User } from './user.model';
import { Permissions } from './permissions.model';

export interface UrlPerm {
  readonly id: string;
  name: string;
  uri: string;
  permissionsId?: string;
  sort: number;
  status: string;
  remark?: string;

  createdUserId?: string;
  createdDate?: Date;

  permissions?: Permissions;
  createdUser?: User;
}
