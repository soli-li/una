import { PasswordPolicy } from './password-policy.model';
import { User } from './user.model';

export interface Company {
  readonly id: string;
  name: string;
  shortName: string;
  legalPerson: string;
  address?: string;
  remark?: string;
  pwPolicyId: string;
  status: string;
  createdUserId?: string;
  createdDate?: Date;
  updatedUserId?: string;
  updatedDate?: Date;

  createdUser?: User;
  updatedUser?: User;
  passwordPolicy?: PasswordPolicy;
}
