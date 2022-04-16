import { User } from './user.model';
import { Role } from './role.model';

export interface Permissions {
  readonly id: string;
  name: string;
  status: string;
  remark?: string;
  createdUserId?: string;
  createdDate?: Date;
  updatedUserId?: string;
  updatedDate?: Date;

  createdUser?: User;
  updatedUser?: User;
  roleSet?: Role[];
}
