import { NgModule } from '@angular/core';
import { StoreModule } from '@ngrx/store';
import * as stateReducer from './user/user-state.reducer';

@NgModule({
  imports: [StoreModule.forRoot({ [stateReducer.stateName]: stateReducer.reducer })],
  providers: [],
})
export class StateModule {}
