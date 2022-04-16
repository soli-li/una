import { Component, OnInit } from '@angular/core';
import { Title } from '@angular/platform-browser';

import { Constants } from '@basic/Constants';
import { UserService } from '@basic/core/user.service';
import { CacheService } from '@basic/core/cache.service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-una-main',
  templateUrl: './main.component.html',
  styleUrls: ['./main.component.less'],
})
export class MainComponent implements OnInit {
  headerHeight = 0;
  leftWidth = 216;
  rightWidth = 0;
  footerHeight = 0;
  collapsedWidth = 50;
  isLeftCollapsed = false;
  isRightCollapsed = false;

  logined$ = false;
  mainScroll = 'auto'; // 主面板是否滚动，默认滚动，空值不滚动，auto自动滚动

  constructor(private titleService: Title, private userService: UserService, private cacheService: CacheService, private router: Router) {}
  ngOnInit(): void {
    const username = this.cacheService.getUsername();
    if (username === null) {
      this.returnLoginPage();
      return;
    }
    this.userService.setCurrentUser(() => this.returnLoginPage());
    this.titleService.setTitle(Constants.BASIC_TITLE);
  }

  private returnLoginPage(): void {
    this.router.navigateByUrl(Constants.ROUTER_LOGIN);
    this.cacheService.clearAttributes(this.cacheService.getTokenKeyName());
    this.cacheService.clearAttributes(this.cacheService.getUserKeyName());
  }
}
