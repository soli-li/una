import { Component, Input } from '@angular/core';
import { Store } from '@ngrx/store';

import * as userReducer from '@basic/state/user/user-state.reducer';

@Component({
  selector: 'app-una-right-side',
  templateUrl: './right-side.component.html',
  styleUrls: ['./right-side.component.less'],
})
export class RightSideComponent {
  @Input() collapsed = false;
  constructor(private store: Store<userReducer.AppState>) {}
}
