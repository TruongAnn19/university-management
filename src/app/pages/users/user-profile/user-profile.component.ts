import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute } from '@angular/router'; // Để lấy ID từ URL nếu cần
import { UserService } from '../../../services/users/user.service';
import { UserProfileResponse } from '../../../models/user/user-profile.model';

@Component({
  selector: 'app-user-profile',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './user-profile.component.html',
  styleUrl: './user-profile.component.scss'
})
export class UserProfileComponent implements OnInit {
  private userService = inject(UserService);
  private route = inject(ActivatedRoute);

  profile: UserProfileResponse | null = null;
  isLoading = true;
  errorMsg = '';

  ngOnInit(): void {
    const userIdParam = this.route.snapshot.paramMap.get('id');

    if (userIdParam) {
      this.fetchUserProfile(+userIdParam);
    } else {
      this.fetchMyProfile();
    }
  }

  fetchMyProfile() {
    this.userService.getMyProfile().subscribe({
      next: (data) => {
        this.profile = data;
        this.isLoading = false;
      },
      error: (err) => {
        this.errorMsg = 'Lỗi tải thông tin cá nhân';
        this.isLoading = false;
        console.error(err);
      }
    });
  }

  fetchUserProfile(id: number) {
    this.userService.getUserProfileById(id).subscribe({
      next: (data) => {
        this.profile = data;
        this.isLoading = false;
      },
      error: (err) => {
        if (err.status === 403) {
          this.errorMsg = 'Bạn không có quyền xem thông tin người này (Yêu cầu quyền ADMIN)';
        } else {
          this.errorMsg = 'Không tìm thấy người dùng';
        }
        this.isLoading = false;
      }
    });
  }

  getInitial(name: string): string {
    return name ? name.charAt(0).toUpperCase() : '?';
  }

  getRoleClass(role: string): string {
    switch (role) {
      case 'STUDENT': return 'student-theme';
      case 'TEACHER': return 'teacher-theme';
      case 'ADMIN': return 'admin-theme';
      default: return '';
    }
  }

  getGpaColorClass(gpa: any): string {
    if (gpa === null || gpa === undefined) return 'none';
    
    if (gpa >= 3.6) return 'good';
    if (gpa >= 3.2) return 'good';
    if (gpa >= 2.5) return 'avg'; 
    return 'bad';                  
  }
}
