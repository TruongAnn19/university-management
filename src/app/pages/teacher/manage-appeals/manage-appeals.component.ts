import { Component, OnInit, inject } from '@angular/core';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { AppealService, GradeAppeal } from "../../../services/appeal/appeal.service";
import { CommonModule } from '@angular/common';

declare var bootstrap: any;

@Component({
  selector: 'app-manage-appeals',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './manage-appeals.component.html',
  styleUrl: './manage-appeals.component.scss'
})
export class ManageAppealsComponent implements OnInit {
  private appealService = inject(AppealService);
  private fb = inject(FormBuilder);

  appeals: GradeAppeal[] = [];
  selectedAppeal: GradeAppeal | null = null;
  reviewForm: FormGroup;
  isProcessing = false;

  constructor() {
    this.reviewForm = this.fb.group({
      status: ['APPROVED', Validators.required],
      newScore: [null], 
      teacherResponse: ['', [Validators.required, Validators.minLength(10)]] 
    });
  }

  ngOnInit() {
    this.loadPendingAppeals();
    this.onDecisionChange(); 
    
    this.reviewForm.get('status')?.valueChanges.subscribe(() => {
      this.onDecisionChange();
    });
  }

  loadPendingAppeals() {
    this.appealService.getPendingAppeals().subscribe({
      next: (data) => this.appeals = data,
      error: (err) => console.error('Lỗi tải danh sách:', err)
    });
  }

  openReviewModal(appeal: GradeAppeal) {
    this.selectedAppeal = appeal;
    
    this.reviewForm.reset({
      status: 'APPROVED',
      newScore: null,
      teacherResponse: ''
    });
    
    this.onDecisionChange();
  }

  onDecisionChange() {
    const status = this.reviewForm.get('status')?.value;
    const scoreControl = this.reviewForm.get('newScore');

    if (status === 'APPROVED') {
      scoreControl?.setValidators([Validators.required, Validators.min(0), Validators.max(10)]);
      scoreControl?.enable();
    } else {
      scoreControl?.clearValidators();
      scoreControl?.setValue(null);
      scoreControl?.disable(); 
    }
    scoreControl?.updateValueAndValidity(); 
  }

  submitReview() {
    if (this.reviewForm.invalid) {
      this.reviewForm.markAllAsTouched(); 
      return;
    }

    if (!this.selectedAppeal) return;

    this.isProcessing = true;
    const request = this.reviewForm.value;

    if (request.status === 'APPROVED' && (request.newScore === null || request.newScore === undefined)) {
        alert("Vui lòng nhập điểm mới!");
        this.isProcessing = false;
        return;
    }

    this.appealService.reviewAppeal(this.selectedAppeal.id, request).subscribe({
      next: (res) => {
        alert('Xử lý thành công!');
        this.isProcessing = false;

        const modalEl = document.getElementById('reviewModal');
        if (modalEl) {
            const modalInstance = bootstrap.Modal.getInstance(modalEl);
            modalInstance?.hide();
        }

        this.loadPendingAppeals();
      },
      error: (err) => {
        this.isProcessing = false;
        console.error('Chi tiết lỗi:', err);

        let message = 'Có lỗi xảy ra, vui lòng thử lại.';

        if (err.error) {
            if (typeof err.error === 'string') {
                try {
                    const parsed = JSON.parse(err.error);
                    message = parsed.message || err.error;
                } catch (e) {
                    message = err.error;
                }
            } 
            else if (typeof err.error === 'object') {
                message = err.error.message || JSON.stringify(err.error);
            }
        }

        alert('Lỗi: ' + message);
      }
    });
  }
}