import { Company } from './company.model';
import { User } from './user.model';

export interface Configuration {
  readonly id: string;
  companyId: string;
  name?: string;

  confKey: string;
  confValue?: string;
  valueType?: string;
  status?: string;
  remark?: string;

  createdUserId?: string;
  createdDate?: Date;

  createdUser?: User;
  company?: Company;
}
