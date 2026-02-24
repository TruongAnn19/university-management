import { Component, OnInit, OnDestroy, inject } from '@angular/core';
import { NotificationService, NotificationResponse } from '../../../services/notify/notification.service';
import { CommonModule } from '@angular/common';
import { AuthService } from '../../../services/auth/auth.service';

@Component({
  selector: 'app-header',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './header.component.html',
  styleUrl: './header.component.scss'
})
export class HeaderComponent implements OnInit, OnDestroy {
  notifications: NotificationResponse[] = [];
  unreadCount: number = 0;
  pollingInterval: any;

  authService = inject(AuthService);
  notiService = inject(NotificationService);

  user = this.authService.getUserInfo();

  // ─── Role helpers ───────────────────────────────────
  get isStudent() { return this.user?.role === 'STUDENT'; }
  get isAdminOrTeacher() { return ['ADMIN', 'TEACHER'].includes(this.user?.role); }

  // ─── Display helpers ────────────────────────────────
  get userInitial() {
    return (this.user?.name || this.user?.username || 'U').charAt(0).toUpperCase();
  }
  get userName() {
    return this.user?.name || this.user?.username || 'Người dùng';
  }
  get roleLabel() {
    const map: Record<string, string> = {
      ADMIN: 'Quản trị viên',
      TEACHER: 'Giảng viên',
      STUDENT: 'Sinh viên'
    };
    return map[this.user?.role] || this.user?.role || '';
  }

  // ─── Lifecycle ──────────────────────────────────────
  ngOnInit(): void {
    // Chỉ fetch thông báo khi là sinh viên
    if (this.isStudent) {
      this.fetchNotifications();

      this.pollingInterval = setInterval(() => {
        this.fetchNotifications();
      }, 30000);
    }
  }

  ngOnDestroy(): void {
    if (this.pollingInterval) {
      clearInterval(this.pollingInterval);
    }
  }

  // ─── Methods ────────────────────────────────────────
  fetchNotifications() {
    this.notiService.getMyNotifications().subscribe({
      next: (data) => {
        this.notifications = data;
        this.unreadCount = this.notifications.filter(n => !n.isRead).length;
      },
      error: (err) => console.error('Lỗi lấy thông báo:', err)
    });
  }

  onNotificationClick(noti: NotificationResponse) {
    if (!noti.isRead) {
      this.notiService.markAsRead(noti.id).subscribe({
        next: () => { noti.isRead = true; this.unreadCount--; },
        error: (err) => console.error('Lỗi mark read:', err)
      });
    }
  }
}
