import { Company } from './company.model';
import { Role } from './role.model';
import { User } from './user.model';

export interface Group {
  readonly id: string;
  companyId: string;
  parentId?: string;
  name: string;
  status: string;
  remark?: string;

  createdUserId?: string;
  createdDate?: Date;
  updatedUserId?: string;
  updatedDate?: Date;

  parentGroup?: Group;
  userSet?: User[];
  roleSet?: Role[];
  createdUser?: User;
  updatedUser?: User;
  company?: Company;
}
