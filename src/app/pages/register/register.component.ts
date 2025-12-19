import { Component, inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { Router, RouterModule } from '@angular/router';
import { AuthService } from '../../services/auth/auth.service';
import { Register } from '../../models/auth/auth.model'; 

@Component({
  selector: 'app-register',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterModule],
  templateUrl: './register.component.html',
  styleUrl: './register.component.scss'
})
export class RegisterComponent implements OnInit {
  private fb = inject(FormBuilder);
  private authService = inject(AuthService);
  private router = inject(Router);

  registerForm: FormGroup;
  isProcessing = false;
  showAdminKey = false;

  constructor() {
    this.registerForm = this.fb.group({
      username: ['', [Validators.required, Validators.minLength(4)]],
      password: ['', [Validators.required, Validators.minLength(6)]],
      role: ['ADMIN', Validators.required], 
      adminKey: [''] 
    });
  }

  ngOnInit(): void {
    this.registerForm.get('role')?.valueChanges.subscribe(role => {
      this.updateValidators(role);
    });
    this.updateValidators(this.registerForm.get('role')?.value);
  }

  updateValidators(role: string) {
    const keyControl = this.registerForm.get('adminKey');

    if (role === 'ADMIN') {
      this.showAdminKey = true;
      keyControl?.setValidators([Validators.required]);
    } else {
      this.showAdminKey = false;
      keyControl?.clearValidators(); 
      keyControl?.setValue(''); 
    }
    keyControl?.updateValueAndValidity();
  }

  onSubmit() {
    if (this.registerForm.invalid) {
      this.registerForm.markAllAsTouched();
      return;
    }

    const role = this.registerForm.get('role')?.value;
    if (role !== 'ADMIN') {
      alert('Sinh viên và Giảng viên vui lòng liên hệ Phòng đào tạo để được cấp tài khoản!');
      return;
    }

    this.isProcessing = true;
    const request: Register = this.registerForm.value;

    this.authService.register(request).subscribe({
      next: (res) => {
        alert('Đăng ký thành công! Vui lòng đăng nhập.');
        this.router.navigate(['/login']);
      },
      error: (err) => {
        this.isProcessing = false;
        const msg = err.error?.message || err.error || 'Đăng ký thất bại';
        alert(msg);
      }
    });
  }
}