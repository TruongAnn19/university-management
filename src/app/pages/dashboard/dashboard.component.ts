import { Component, inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { AuthService } from '../../services/auth/auth.service';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './dashboard.component.html',
  styleUrls: ['./dashboard.component.scss']
})
export class DashboardComponent implements OnInit {
  authService = inject(AuthService);
  user: any = null;
  currentDate = new Date();

  ngOnInit() {
    this.user = this.authService.getUserInfo();
  }

  get isAdmin() { return this.user?.role === 'ADMIN'; }
  get isTeacher() { return this.user?.role === 'TEACHER'; }
  get isStudent() { return this.user?.role === 'STUDENT'; }
  get isAdminOrTeacher() { return ['ADMIN', 'TEACHER'].includes(this.user?.role); }

  get greeting() {
    const h = new Date().getHours();
    if (h < 12) return 'Chào buổi sáng';
    if (h < 18) return 'Chào buổi chiều';
    return 'Chào buổi tối';
  }

  get userDisplayName() {
    return this.user?.name || this.user?.username || 'bạn';
  }

  get roleLabel() {
    const map: Record<string, string> = { ADMIN: 'Quản trị viên', TEACHER: 'Giảng viên', STUDENT: 'Sinh viên' };
    return map[this.user?.role] || this.user?.role || '';
  }

  get roleColorClass() {
    const map: Record<string, string> = { ADMIN: 'badge-admin', TEACHER: 'badge-teacher', STUDENT: 'badge-student' };
    return map[this.user?.role] || '';
  }
}