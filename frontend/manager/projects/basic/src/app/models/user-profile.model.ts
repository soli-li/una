import { User } from './user.model';

export interface UserProfile {
  readonly id: string;
  readonly userId: string;
  realName?: string;
  phone?: string;
  email?: string;
  avatar?: string;
  avatarType?: string;

  createdUserId?: string;
  createdDate?: Date;
  updatedUserId?: string;
  updatedDate?: Date;

  createdUser?: User;
  updatedUser?: User;
  user?: User;
  avatarFile?: File;
}
