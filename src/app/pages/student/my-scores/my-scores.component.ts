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
    const user = this.authService.getUserInfo();
    if (user && user.username) {
      this.loadTranscript(user.username);
    }
  }

  loadTranscript(studentCode: string) {
    this.scoreService.getMyTranscript(studentCode).subscribe({
      next: (data) => {
        this.transcript = data;
        this.isLoading = false;
      },
      error: (err) => {
        console.error(err);
        this.errorMessage = 'Không thể tải bảng điểm. Vui lòng thử lại sau.';
        this.isLoading = false;
      }
    });
  }
}