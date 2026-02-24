import { Component, inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ScoreService } from '../../../services/score/score.service';
import { AuthService } from '../../../services/auth/auth.service';
import { TranscriptResponse } from '../../../models/score/score.model';
import { AppealRequest, AppealService } from '../../../services/appeal/appeal.service';

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

  // Modal state – Angular-native, không dùng bootstrap.Modal
  showAppealModal = false;
  selectedScoreForAppeal: any = null;
  appealReason = '';
  isSubmitting = false;

  ngOnInit() {
    this.scoreService.getMyTranscript().subscribe({
      next: (data) => { this.transcript = data; this.isLoading = false; },
      error: () => { this.errorMessage = 'Lỗi tải bảng điểm'; this.isLoading = false; }
    });
  }

  // ─── Modal open / close ───────────────────────────────
  openAppealModal(score: any) {
    this.selectedScoreForAppeal = score;
    this.appealReason = '';
    this.isSubmitting = false;
    this.showAppealModal = true;
    // Ngăn scroll body khi modal mở
    document.body.style.overflow = 'hidden';
  }

  closeAppealModal(event?: MouseEvent) {
    // Chỉ đóng khi click vào overlay (nền tối), không phải dialog bên trong
    if (event && (event.target as HTMLElement).closest('.appeal-dialog')) return;
    this._closeModal();
  }

  private _closeModal() {
    this.showAppealModal = false;
    document.body.style.overflow = '';
  }

  // ─── Submit ───────────────────────────────────────────
  submitAppeal() {
    if (!this.selectedScoreForAppeal || this.appealReason.trim().length < 10) return;

    this.isSubmitting = true;
    const request: AppealRequest = {
      scoreId: this.selectedScoreForAppeal.id,
      reason: this.appealReason
    };

    this.appealService.createAppeal(request).subscribe({
      next: () => {
        this.isSubmitting = false;
        this._closeModal();
        alert('Đã gửi đơn phúc khảo thành công!');
      },
      error: (err) => {
        this.isSubmitting = false;
        let message = 'Có lỗi xảy ra, vui lòng thử lại sau.';
        if (typeof err.error === 'string') {
          try { message = JSON.parse(err.error).message; }
          catch { message = err.error; }
        } else if (err.error?.message) {
          message = err.error.message;
        }
        alert(message);
        this._closeModal();
      }
    });
  }
}