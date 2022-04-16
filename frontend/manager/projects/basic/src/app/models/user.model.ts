import { Company } from './company.model';
import { Group } from './group.model';
import { Role } from './role.model';
import { UserProfile } from './user-profile.model';

export interface User {
  readonly id: string;
  username: string;

  companyId: string;
  password?: string;
  accountNonExpired: boolean; // 是否未到期
  accountNonLocked: boolean; // 是否未锁定
  credentialsNonExpired: boolean; // 验证未过期
  defaultGroupId?: string;
  lastChangePWDate?: Date; // 最后修改密码时间
  lastLoginDate?: Date; // 最后登录时间
  failureCount?: number;
  profileId?: string;

  createdUserId?: string;
  createdDate?: Date;
  updatedUserId?: string;
  updatedDate?: Date;

  roleSet?: Role[];
  groupSet?: Group[];
  createdUser?: User;
  updatedUser?: User;
  company?: Company;
  defaultGroup?: Group;
  userProfile?: UserProfile;
  currentRoleId?: string[];
  currentGroupId?: string;
}
