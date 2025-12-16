import { Component, ElementRef, ViewChild, inject } from '@angular/core';
import { CommonModule, DecimalPipe } from '@angular/common';
import { FormBuilder, FormGroup, FormsModule, ReactiveFormsModule, Validators } from '@angular/forms';
import { ScoreService } from '../../../services/score/score.service';
import { CreateScoreRequest, TranscriptResponse } from '../../../models/score/score.model';

@Component({
  selector: 'app-manage-scores',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, FormsModule],
  templateUrl: './manage-scores.component.html',
  styleUrl: './manage-scores.component.scss'
})
export class ManageScoresComponent {
  private fb = inject(FormBuilder);
  private scoreService = inject(ScoreService);

  searchStudentCode = '';
  transcript: TranscriptResponse | null = null;
  
  isLoading = false;
  isSaving = false;
  errorMessage = '';
  successMessage = '';

  scoreForm: FormGroup = this.fb.group({
    studentCode: [{value: '', disabled: true}, Validators.required],
    subjectCode: ['', Validators.required],
    processScore: [null, [Validators.required, Validators.min(0), Validators.max(10)]],
    finalScore: [null, [Validators.required, Validators.min(0), Validators.max(10)]]
  });

  @ViewChild('closeModalBtn') closeModalBtn!: ElementRef;

  onSearch() {
    if (!this.searchStudentCode) return;
    
    this.isLoading = true;
    this.transcript = null;
    this.errorMessage = '';

    this.scoreService.getTranscriptByStudentCode(this.searchStudentCode).subscribe({
      next: (data) => {
        this.transcript = data;
        this.isLoading = false;
      },
      error: (err) => {
        this.isLoading = false;
        this.errorMessage = 'Không tìm thấy sinh viên hoặc lỗi hệ thống!';
        console.error(err);
      }
    });
  }

  openAddScoreModal() {
    this.scoreForm.reset();
    this.scoreForm.patchValue({
      studentCode: this.searchStudentCode.toUpperCase(),
      processScore: null,
      finalScore: null
    });
  }

  onSaveScore() {
    if (this.scoreForm.invalid) {
      this.scoreForm.markAllAsTouched();
      return;
    }

    this.isSaving = true;
    
    const formValues = this.scoreForm.getRawValue();

    const payload: CreateScoreRequest = {
      studentCode: formValues.studentCode,
      subjectCode: formValues.subjectCode.toUpperCase(),
      processScore: formValues.processScore,
      finalScore: formValues.finalScore
    };

    this.scoreService.createScore(payload).subscribe({
      next: () => {
        this.isSaving = false;
        this.successMessage = `Đã nhập điểm môn ${payload.subjectCode} thành công!`;
        
        this.closeModalBtn.nativeElement.click(); 

        this.onSearch(); 
        
        setTimeout(() => this.successMessage = '', 3000);
      },
      error: (err) => {
        this.isSaving = false;
        alert('Lỗi: ' + (err.error?.message || 'Không thể lưu điểm'));
      }
    });
  }
}