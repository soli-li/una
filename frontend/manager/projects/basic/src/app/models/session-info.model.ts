import { User } from './user.model';

export interface SessionInfo {
  sessionId: string;
  userAgent: string;
  ip: string;
  createdTime: Date;
  user?: User;
}
