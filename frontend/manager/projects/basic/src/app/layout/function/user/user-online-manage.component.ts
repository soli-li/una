import { DatePipe } from '@angular/common';
import { Component, Input, OnInit } from '@angular/core';
import { UserService } from '@basic/core/user.service';
import { SessionInfo } from '@basic/models/session-info.model';
import { User } from '@basic/models/user.model';

@Component({
  selector: 'app-una-user-online-manage',
  templateUrl: './user-online-manage.component.html',
  styleUrls: ['./user-online-manage.component.less'],
})
export class UserOnlineManageComponent implements OnInit {
  @Input() user: User = {} as User;
  sessionInfos: SessionInfo[] = [];
  constructor(private userService: UserService, private datePipe: DatePipe) {}
  ngOnInit(): void {
    if (this.user.username !== '') {
      this.queryOnlinSession();
    }
  }

  private queryOnlinSession(): void {
    this.userService.getOnlineUserSession(this.user.username).subscribe({
      next: (data) => {
        this.sessionInfos = [];
        const map = data?.sessionMap;
        if (map !== undefined) {
          Object.values(map).forEach((entry) => {
            this.sessionInfos.push(entry);
          });
        }
      },
    });
  }

  takeOut(sessionInfo: SessionInfo): void {
    this.userService.takeOutUser(sessionInfo.sessionId).subscribe({ next: () => this.queryOnlinSession() });
  }

  formatDate(format: string, date?: Date): string | null {
    if (date === undefined || date === null) {
      return '';
    }
    return this.datePipe.transform(date, format);
  }

  getDeviceTyep(userAgent: string): string {
    const mobileAgents = ['Android', 'iPhone', 'iPad', 'iPod', 'Silk', 'BlackBerry', 'Opera Mini', 'IEMobile'];
    for (const mobileAgent of mobileAgents) {
      if (userAgent.indexOf(mobileAgent) > 0) {
        return mobileAgent;
      }
    }
    return 'PC';
  }
}
