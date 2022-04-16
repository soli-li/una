import { Action, createReducer, on } from '@ngrx/store';
import { createAction, props } from '@ngrx/store';
import { createSelector } from '@ngrx/store';
import { User } from '@basic/models/user.model';

export const stateName = 'user';
export interface UserState {
  user: User | null;
}
export interface AppState {
  [stateName]: UserState;
}

export class UserAction {
  public static logout = createAction('[Logout] Logout');
  public static updateUser = createAction('[update] User', props<{ user: User }>());
}
export class UserSelector {
  public static getUser = createSelector(
    (state: AppState) => state[stateName],
    (state: UserState) => state?.user
  );

  public static getUserProfile = createSelector(
    (state: AppState) => state[stateName],
    (state: UserState) => state?.user?.userProfile
  );

  public static getCompany = createSelector(
    (state: AppState) => state[stateName],
    (state: UserState) => state?.user?.company
  );
}

const initialUser: UserState = {
  user: null,
};

const userReducer = createReducer(
  initialUser,
  on(UserAction.logout, () => initialUser),
  on(UserAction.updateUser, (state: UserState, { user }) => ({
    ...state,
    user: user,
  }))
);
export const reducer = (state: UserState | undefined, action: Action): UserState => userReducer(state, action);
