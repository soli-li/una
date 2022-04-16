import { User } from './user.model';

export interface PasswordPolicy {
  readonly id: string;
  label: string;
  description: string;
  letters: boolean; // 是否包含字母
  caseSensitive: boolean; // 是否区分大小写
  digitals: boolean; // 是否包含数字
  nonAlphanumeric: boolean; // 是否包含符号
  length: number; // 密码长度，0表示不限
  maximumAge: number; // 密码有效期，单位天，0表示无限
  repeatCount: number; // 不能与前几次密码相同，0表示无限
  triesCount: number; // 密码试错次数，0表示无限
  createdUserId?: string;
  createdDate?: Date;
  updatedUserId?: string;
  updatedDate?: Date;

  createdUser?: User;
  updatedUser?: User;
}
