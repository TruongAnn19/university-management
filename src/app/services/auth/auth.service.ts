import { Injectable, inject, PLATFORM_ID } from '@angular/core'; // Thêm PLATFORM_ID
import { HttpClient } from '@angular/common/http';
import { environment } from '../../../environments/environment';
import { LoginRequest, AuthResponse } from '../../models/auth/auth.model';
import { tap } from 'rxjs';
import { Router } from '@angular/router';
import { jwtDecode } from 'jwt-decode';
import { isPlatformBrowser } from '@angular/common'; // Thêm isPlatformBrowser

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private http = inject(HttpClient);
  private router = inject(Router);
  
  private platformId = inject(PLATFORM_ID); 
  
  private apiUrl = `${environment.apiUrl}/auth`;
  private tokenKey = 'access_token';

  constructor() {}

  login(credentials: LoginRequest) {
    return this.http.post<AuthResponse>(`${this.apiUrl}/login`, credentials).pipe(
      tap(response => {
        if (response.token) {
          if (isPlatformBrowser(this.platformId)) {
            localStorage.setItem(this.tokenKey, response.token);
          }
        }
      })
    );
  }

  logout() {
    if (isPlatformBrowser(this.platformId)) {
      localStorage.removeItem(this.tokenKey);
      localStorage.clear();
      sessionStorage.clear();
      window.location.href = '/login';
    }
    this.router.navigate(['/login']);
  }

  getToken(): string | null {
    if (!isPlatformBrowser(this.platformId)) {
      return null;
    }

    const token = localStorage.getItem(this.tokenKey);
    if (!token) {
      return null;
    }

    return token;
  }

  isLoggedIn(): boolean {
    return !!this.getToken();
  }

  getUserInfo(): any {
    const token = this.getToken();
    console.log(token)
    if (token) {
      try {
        const decoded: any = jwtDecode(token);
        return {
          username: decoded.sub,
          role: decoded.role || 'GUEST'
        };
      } catch (e) {
        return null;
      }
    }
    return null;
  }
}