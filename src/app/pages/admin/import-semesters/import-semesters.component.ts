import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { SemesterService } from '../../../services/semester/semester.service'; 

@Component({
  selector: 'app-import-semesters',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './import-semesters.component.html',
  styleUrls: ['./import-semesters.component.scss']
})
export class ImportSemestersComponent {
  semesterService = inject(SemesterService);

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
    if (!this.selectedFile) return;

    this.isLoading = true;
    this.message = '';

    this.semesterService.importSemesters(this.selectedFile).subscribe({
      next: (res) => {
        this.message = res || 'Import thành công!';
        this.isError = false;
        this.isLoading = false;
        this.selectedFile = null;
      },
      error: (err) => {
        console.error(err);
        this.message = err.error || 'Có lỗi xảy ra.';
        this.isError = true;
        this.isLoading = false;
      }
    });
  }
}