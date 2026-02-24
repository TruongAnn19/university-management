import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink, RouterLinkActive, Router } from '@angular/router';
import { AuthService } from '../../../services/auth/auth.service';

@Component({
  selector: 'app-sidebar',
  standalone: true,
  imports: [CommonModule, RouterLink, RouterLinkActive],
  templateUrl: './sidebar.component.html',
  styleUrls: ['./sidebar.component.scss']
})
export class SidebarComponent {
  authService = inject(AuthService);
  router = inject(Router);

  user = this.authService.getUserInfo();

  get isStudent()        { return this.user?.role === 'STUDENT'; }
  get isAdminOrTeacher() { return ['ADMIN', 'TEACHER'].includes(this.user?.role); }
  get userInitial()      { return (this.user?.username || this.user?.name || 'U').charAt(0).toUpperCase(); }
  get userName()         { return this.user?.name || this.user?.username || 'Người dùng'; }
  get userRole() {
    const map: Record<string, string> = { ADMIN: 'Quản trị viên', TEACHER: 'Giảng viên', STUDENT: 'Sinh viên' };
    return map[this.user?.role] || this.user?.role || '';
  }

  logout() {
    this.authService.logout();
  }
}