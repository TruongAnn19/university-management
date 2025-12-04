import { HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';
import { AuthService } from '../services/auth/auth.service';

export const authInterceptor: HttpInterceptorFn = (req, next) => {
  const authService = inject(AuthService);
  const token = authService.getToken();

  let headers = req.headers
    .set('Cache-Control', 'no-cache, no-store, must-revalidate, post-check=0, pre-check=0')
    .set('Pragma', 'no-cache')
    .set('Expires', '0');

  if (token) {
    const cloned = req.clone({
      setHeaders: {
        Authorization: `Bearer ${token}`
      }
    });
    return next(cloned);
  }
  const cloned = req.clone({ headers });

  return next(cloned);
};