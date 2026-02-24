import { Component, inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RegistrationService } from '../../../services/registration/registration.service';
import { StudentService } from '../../../services/students/student.service';
import { ClassResponse, Faculty } from '../../../models/registration/registration.model';

@Component({
  selector: 'app-course-registration',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './course-registration.component.html',
  styleUrls: ['./course-registration.component.scss']
})
export class CourseRegistrationComponent implements OnInit {
  registrationService = inject(RegistrationService);
  studentService = inject(StudentService);

  classes: ClassResponse[] = [];
  faculties: Faculty[] = [];

  selectedFacultyId: number | null = null;
  isLoading = false;
  message = '';
  isError = false;

  ngOnInit() {
    this.initData();
  }

  initData() {
    this.isLoading = true;
    this.loadFaculties();
    this.loadClasses();
  }

  loadFaculties() {
    this.registrationService.getAllFaculties().subscribe(data => {
      this.faculties = data;
    });
  }

  loadClasses() {
    this.isLoading = true;
    const facId = this.selectedFacultyId ? this.selectedFacultyId : undefined;

    this.registrationService.getOpenClasses(facId).subscribe({
      next: (data) => {
        this.classes = data;
        this.isLoading = false;
      },
      error: (err) => {
        console.error(err);
        this.isLoading = false;
      }
    });
  }

  onFacultyChange() {
    this.loadClasses();
  }

  onRegister(classCode: string) {
    if (!confirm(`Bạn có chắc chắn muốn đăng ký lớp ${classCode}?`)) return;

    this.isLoading = true;
    this.message = '';

    const request = {
      classCode: classCode
    };

    this.registrationService.registerCourse(request).subscribe({
      next: (res) => {
        this.message = res;
        this.isError = false;
        this.loadClasses();
      },
      error: (err) => {
        console.error(err);
        let errorMsg = 'Đăng ký thất bại';

        if (err.error) {
          if (typeof err.error === 'string') {
            errorMsg = err.error;
          } else if (err.error.message) {
            errorMsg = err.error.message;
          }
        }
        this.message = errorMsg;
        this.isError = true;
        this.isLoading = false;
      }
    });
  }

  onCancel(classCode: string) {
    if (!confirm(`Cảnh báo: Bạn có chắc chắn muốn HỦY lớp ${classCode} không?`)) return;

    this.isLoading = true;
    this.message = '';

    const request = { classCode: classCode };

    this.registrationService.cancelRegistration(request).subscribe({
      next: (res) => {
        this.message = res;
        this.isError = false;
        this.loadClasses();
      },
      error: (err) => {
        this.message = (err.error || 'Hủy thất bại');
        this.isError = true;
        this.isLoading = false;
      }
    });
  }

  getCourseStatus(c: any): { text: string, class: string } | null {
    if (c.isMyClass) return { text: '📚 Đang học', class: 'status-learning' };
    if (!c.isRegistered && c.studyStatus === 'PASSED') return { text: 'Đã qua', class: 'status-passed' };
    if (c.timeStatus === 'EXPIRED') return { text: 'Hết hạn ĐK', class: 'status-closed' };
    if (c.timeStatus === 'WAITING') return { text: 'Sắp mở', class: 'status-waiting' };
    if (c.currentSlot >= c.maxSlot) return { text: 'Lớp đầy', class: 'status-full' };
    if (c.isRegistered) return { text: 'Trùng môn', class: 'status-retake' };
    if (c.timeStatus === 'OPEN') return { text: 'Cho phép ĐK', class: 'status-open' };
    return null;
  }
}