import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink, RouterLinkActive } from '@angular/router';
import { AuthService } from '../../../services/auth/auth.service';

@Component({
  selector: 'app-sidebar',
  standalone: true,
  imports: [CommonModule, RouterLink, RouterLinkActive],
  template: `
    <div class="sidebar-header">
      <h3>UniApp 🎓</h3>
    </div>
    
    <ul class="menu-list">
      <li><a routerLink="/dashboard" routerLinkActive="active">🏠 Trang chủ</a></li>

      <ng-container *ngIf="isStudent">
        <li><a routerLink="/my-scores" routerLinkActive="active">📊 Xem điểm</a></li>
        <li><a routerLink="/register-course" routerLinkActive="active">📝 Đăng ký tín chỉ</a></li>
        <li><a routerLink="/my-appeals" routerLinkActive="active">⚖️ Phúc khảo</a></li>
      </ng-container>

      <ng-container *ngIf="isAdminOrTeacher">
        <li><a routerLink="/manage-scores" routerLinkActive="active">✍️ Nhập điểm</a></li>
        <li><a routerLink="/students" routerLinkActive="active">👨‍🎓 Quản lý Sinh viên</a></li>
        <li><a routerLink="/appeals" routerLinkActive="active">📩 Duyệt phúc khảo</a></li>
        <li><a routerLink="/import-students" routerLinkActive="active">📥 Import Sinh viên</a></li>
        <li><a routerLink="/import-teachers" routerLinkActive="active">👨‍🏫 Import Giảng viên</a></li>
        <li><a routerLink="/import-subjects" routerLinkActive="active">📚 Import Môn học</a></li>
        <li><a routerLink="/import-semesters" routerLinkActive="active">📅 Import Học kỳ</a></li>
        <li><a routerLink="/import-scores" routerLinkActive="active">✍️ Import Điểm</a></li>
      </ng-container>

      <li class="logout" (click)="logout()">🚪 Đăng xuất</li>
    </ul>
  `,
  styleUrls: ['./sidebar.component.scss']
})
export class SidebarComponent {
  authService = inject(AuthService);

  user = this.authService.getUserInfo();

  get isStudent() { return this.user?.role === 'STUDENT'; }
  get isAdminOrTeacher() { return ['ADMIN', 'TEACHER'].includes(this.user?.role); }

  logout() {
    this.authService.logout();
  }
}