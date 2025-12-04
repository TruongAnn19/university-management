import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { SubjectService } from '../../../services/subject/subject.service'; 

@Component({
  selector: 'app-import-subjects',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './import-subjects.component.html',
  styleUrls: ['./import-subjects.component.scss']
})
export class ImportSubjectsComponent {
  subjectService = inject(SubjectService);

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

    this.subjectService.importSubjects(this.selectedFile).subscribe({
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