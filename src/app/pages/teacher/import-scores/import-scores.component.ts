import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ScoreService } from '../../../services/score/score.service';

@Component({
  selector: 'app-import-scores',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './import-scores.component.html',
  styleUrls: ['./import-scores.component.scss']
})
export class ImportScoresComponent {
  scoreService = inject(ScoreService);

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

    this.scoreService.importScores(this.selectedFile).subscribe({
      next: (res) => {
        this.message = res || 'Import bảng điểm thành công!';
        this.isError = false;
        this.isLoading = false;
        this.selectedFile = null;
      },
      error: (err) => {
        console.error(err);
        this.isError = true;
        this.isLoading = false;
        let rawMessage = '';

        if (err.error && typeof err.error === 'object' && err.error.message) {
          rawMessage = err.error.message;
        }
        else if (typeof err.error === 'string') {
          try {
            const parsedError = JSON.parse(err.error);
            rawMessage = parsedError.message || err.error;
          } catch (e) {
            rawMessage = err.error;
          }
        }
        else {
          this.message = 'Có lỗi xảy ra khi import (Lỗi không xác định).';
        }
        this.message = rawMessage.replace('Lỗi dữ liệu tại dòng đang xử lý: ', '');
      }
    });
  }
}