import { Component, OnInit } from '@angular/core';
import { Store } from '@ngrx/store';
import { ActivatedRoute, Router } from '@angular/router';

import * as userReducer from '@basic/state/user/user-state.reducer';
// import * as appConst from '@/app.constants';
// import { UserService } from '@/framework/core/user/user.service';
// import { MenuService } from '@/framework/core/menu/menu.service';

@Component({
  selector: 'app-una-header',
  templateUrl: './header.component.html',
  styleUrls: ['./header.component.less'],
})
export class HeaderComponent implements OnInit {
  userName$ = '';
  visible = false;

  // menus = [] as Menu[];
  selectedIndex = 0;

  constructor(private store: Store<userReducer.AppState>, private router: Router, private activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
    this.store.select(userReducer.UserSelector.getUserProfile).subscribe((profile) => {
      if (profile !== undefined && profile !== null) {
        // this.userName$ = profile.realName;
      }
    });

    // this.subscribeTabs();
  }

  // private subscribeTabs(): void {
  //   this.menuService.menuEmitter.subscribe((menu) => {
  //     let hasMenu = false;
  //     // let index = 0;
  //     for (const m of this.menus) {
  //       if (m.uri === menu.uri) {
  //         hasMenu = true;
  //         // this.selectedIndex = index;
  //         break;
  //       }
  //       // index++;
  //     }

  //     if (!hasMenu) {
  //       this.menus.push(menu);
  //       // this.selectedIndex = this.menus.length - 1;
  //     }
  //   });
  // }

  // public closeTab({ index }: { index: number }): void {
  //   const options = {
  //     relativeTo: this.activatedRoute,
  //     queryParamsHandling: 'merge',
  //     skipLocationChange: false
  //   } as UrlCreationOptions;
  //   let urlTree = null;
  //   if (this.menus.length === 0) {
  //     return;
  //   } else if (this.menus.length === 1) {
  //     urlTree = this.router.createUrlTree(['./'], options);
  //   } else if (index === this.selectedIndex) {
  //     urlTree = this.router.createUrlTree(['./', this.menus[index - 1].uri], options);
  //     this.selectedIndex = this.selectedIndex - 1;
  //   } else if (index < this.selectedIndex) {
  //     urlTree = this.router.createUrlTree(['./', this.menus[this.selectedIndex].uri], options);
  //     this.selectedIndex = this.selectedIndex - 1;
  //   } else {
  //     this.menus.splice(index, 1);
  //     return;
  //   }

  //   this.menus.splice(index, 1);
  //   void this.router.navigateByUrl(urlTree);
  // }

  // public logout(): void {
  //   this.userService.logout();
  //   void this.router.navigateByUrl(appConst.LOGIN_URL);
  // }

  // public closeAllTab(): void {
  //   if (this.menus.length > 1) {
  //     this.menus.splice(1, this.menus.length - 1);
  //   }
  //   this.closeTab({ index: 0 });
  // }
}
