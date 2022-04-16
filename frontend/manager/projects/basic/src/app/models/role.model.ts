import { Company } from './company.model';
import { Group } from './group.model';
import { User } from './user.model';
import { Permissions } from './permissions.model';

export interface Role {
  readonly id: string;
  companyId: string;
  authority: string;
  status: string;
  remark?: string;

  createdUserId?: string;
  createdDate?: Date;
  updatedUserId?: string;
  updatedDate?: Date;

  userSet?: User[];
  groupSet?: Group[];
  createdUser?: User;
  updatedUser?: User;
  company?: Company;
  permissionsSet?: Permissions[];
}
