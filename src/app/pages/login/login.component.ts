import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { AuthService } from '../../services/auth/auth.service';
import { Router } from '@angular/router';
import { environment } from '../../../environments/environment';

declare const grecaptcha: any;

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.scss']
})
export class LoginComponent {
  fb = inject(FormBuilder);
  authService = inject(AuthService);
  router = inject(Router);

  loginForm: FormGroup = this.fb.group({
    username: ['', Validators.required],
    password: ['', Validators.required]
  });

  errorMessage = '';
  isLoading = false;

  onSubmit() {
    if (this.loginForm.invalid) return;

    this.isLoading = true;
    this.errorMessage = '';

    try {
      grecaptcha.ready(() => {
        grecaptcha.execute(environment.recaptchaSiteKey, { action: 'login' })
          .then((token: string) => {
            
            this.performLogin(token);
            
          }, (err: any) => {
            console.error('Lỗi Recaptcha:', err);
            this.errorMessage = 'Không thể xác thực Recaptcha. Vui lòng thử lại.';
            this.isLoading = false;
          });
      });
    } catch (e) {
      console.error('Lỗi tải thư viện Recaptcha:', e);
      this.errorMessage = 'Lỗi hệ thống. Kiểm tra kết nối mạng.';
      this.isLoading = false;
    }
  }

  private performLogin(captchaToken: string) {
    const loginData = {
      username: this.loginForm.value.username,
      password: this.loginForm.value.password,
      recaptchaToken: captchaToken
    };
    
    this.authService.login(loginData).subscribe({
      next: (res) => {
        this.router.navigate(['/dashboard']);
      },
      error: (err) => {
        if (err.error && typeof err.error === 'string') {
            this.errorMessage = err.error;
        } else if (err.error && err.error.message) {
            this.errorMessage = err.error.message;
        } else {
            this.errorMessage = 'Sai tên đăng nhập hoặc mật khẩu';
        }
        this.isLoading = false;
      }
    });
  }
}