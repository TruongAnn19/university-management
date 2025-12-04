import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { StudentService } from '../../../services/students/student.service'; 

@Component({
  selector: 'app-import-students',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './import-students.component.html',
  styleUrls: ['./import-students.component.scss']
})
export class ImportStudentsComponent {
  studentService = inject(StudentService);

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

    this.studentService.importStudents(this.selectedFile).subscribe({
      next: (res) => {
        this.message = res || 'Import thành công!';
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