import { Component, inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ScoreService } from '../../../services/score/score.service';
import { AuthService } from '../../../services/auth/auth.service';
import { TranscriptResponse } from '../../../models/score/score.model';
import { AppealRequest, AppealService } from '../../../services/appeal/appeal.service';

declare var bootstrap: any;

@Component({
  selector: 'app-my-scores',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './my-scores.component.html',
  styleUrls: ['./my-scores.component.scss']
})
export class MyScoresComponent implements OnInit {
  scoreService = inject(ScoreService);
  authService = inject(AuthService);
  appealService = inject(AppealService);

  transcript: TranscriptResponse | null = null;
  isLoading = true;
  errorMessage = '';

  selectedScoreForAppeal: any = null;
  appealReason: string = '';

  ngOnInit() {
    this.isLoading = true
    this.scoreService.getMyTranscript().subscribe({
      next: (data) => {
        this.transcript = data;
        this.isLoading = false;
      },
      error: (err) => {
        this.errorMessage = 'Lỗi tải bảng điểm';
        this.isLoading = false;
      }
    });
  }

  openAppealModal(score: any) {
    console.log(score);
    this.selectedScoreForAppeal = score;
    this.appealReason = '';
    const modal = new bootstrap.Modal(document.getElementById('appealModal'));
    modal.show();
  }

  submitAppeal() {
    console.log('Dữ liệu gửi đi:', {
      scoreId: this.selectedScoreForAppeal?.id,
      reason: this.appealReason
    });
    if (!this.selectedScoreForAppeal) return;
    const btnClose = document.querySelector('#appealModal .btn-close') as HTMLElement;
    const request: AppealRequest = {
      scoreId: this.selectedScoreForAppeal.id,
      reason: this.appealReason
    };
    this.appealService.createAppeal(request).subscribe({
      next: () => {
        alert('Đã gửi đơn phúc khảo thành công!');
        btnClose?.click();
      },

      error: (err) => {
        let message = 'Có lỗi xảy ra, vui lòng thử lại sau.';
        if (typeof err.error === 'string') {
          try {
            const parsedError = JSON.parse(err.error);
            message = parsedError.message;
          } catch (e) {
            message = err.error;
          }
        }
        else if (err.error && err.error.message) {
          message = err.error.message;
        }

        alert(message);
        btnClose?.click();
      }
    });
  }
}