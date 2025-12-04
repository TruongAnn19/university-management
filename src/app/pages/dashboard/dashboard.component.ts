import { Component, inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { AuthService } from '../../services/auth/auth.service';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [CommonModule],
  template: `
    <div class="dashboard-container">
      <div class="welcome-card">
        <h1>Xin chào, {{ user?.username }}! 👋</h1>
        <p>Vai trò hiện tại: <span class="badge">{{ user?.role }}</span></p>
      </div>

      <div class="role-content" *ngIf="isAdmin">
        <h3>⚡ Admin Panel</h3>
        <p>Bạn có quyền quản lý hệ thống, nhập điểm và thêm tài khoản.</p>
      </div>

      <div class="role-content" *ngIf="isStudent">
        <h3>🎓 Góc học tập</h3>
        <p>Xem điểm thi và đăng ký tín chỉ mới nhất.</p>
      </div>
    </div>
  `,
  styles: [`
    .dashboard-container { padding: 20px; }
    .welcome-card { 
      background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
      color: white; padding: 30px; border-radius: 10px; margin-bottom: 20px;
    }
    .badge { 
      background: rgba(255,255,255,0.2); padding: 5px 10px; border-radius: 15px; font-weight: bold;
    }
    .role-content { 
      background: #f8f9fa; padding: 20px; border-radius: 8px; border-left: 5px solid #007bff;
    }
  `]
})
export class DashboardComponent implements OnInit {
  authService = inject(AuthService);
  user: any = null;

  ngOnInit() {
    this.user = this.authService.getUserInfo();
  }

  get isAdmin() { return this.user?.role === 'ADMIN' || this.user?.role === 'TEACHER'; }
  get isStudent() { return this.user?.role === 'STUDENT'; }
}