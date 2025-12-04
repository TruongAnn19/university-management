import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { TeacherService } from '../../../services/teacher/teacher.service';
@Component({
  selector: 'app-import-teachers',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './import-teachers.component.html',
  styleUrls: ['./import-teachers.component.scss']
})
export class ImportTeachersComponent {
  teacherService = inject(TeacherService);

  selectedFile: File | null = null;
  message = '';
  isError = false;
  isLoading = false;

  onFileSelected(event: any) {
    const file = event.target.files[0];
    if (file) {
      this.selectedFile = file;
      this.message = '';
    }
  }

  onUpload() {
    if (!this.selectedFile) {
      this.message = 'Vui lòng chọn file Excel trước!';
      this.isError = true;
      return;
    }

    this.isLoading = true;
    this.message = '';

    this.teacherService.importTeachers(this.selectedFile).subscribe({
      next: (res) => {
        this.message = res || 'Import danh sách giảng viên thành công!';
        this.isError = false;
        this.isLoading = false;
        this.selectedFile = null;
      },
      error: (err) => {
        console.error(err);
        this.message = err.error || 'Có lỗi xảy ra khi import.';
        this.isError = true;
        this.isLoading = false;
      }
    });
  }
}