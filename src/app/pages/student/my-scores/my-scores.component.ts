import { Component, inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ScoreService } from '../../../services/score/score.service';
import { AuthService } from '../../../services/auth/auth.service';
import { TranscriptResponse } from '../../../models/score/score.model';

@Component({
  selector: 'app-my-scores',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './my-scores.component.html',
  styleUrls: ['./my-scores.component.scss']
})
export class MyScoresComponent implements OnInit {
  scoreService = inject(ScoreService);
  authService = inject(AuthService);

  transcript: TranscriptResponse | null = null;
  isLoading = true;
  errorMessage = '';

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

}