import { Injectable } from '@angular/core';

@Injectable({ providedIn: 'root' })
export class CacheService {
  private readonly TOKEN_KEY = 'TOKEN';
  private readonly USER_KEY = 'USER';
  public saveToken(token: string): void {
    sessionStorage.setItem(this.TOKEN_KEY, token);
  }
  public getToken(): string | null {
    return sessionStorage.getItem(this.TOKEN_KEY);
  }
  public saveUsername(username: string): void {
    sessionStorage.setItem(this.USER_KEY, username);
  }
  public getUsername(): string | null {
    return sessionStorage.getItem(this.USER_KEY);
  }

  public clearAttributes(key?: string): void {
    if (key === undefined || key === null) {
      sessionStorage.clear();
    } else {
      sessionStorage.removeItem(key);
    }
  }

  public getTokenKeyName(): string {
    return this.TOKEN_KEY;
  }
  public getUserKeyName(): string {
    return this.USER_KEY;
  }
}
