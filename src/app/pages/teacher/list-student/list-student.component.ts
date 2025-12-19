import { Component, inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule, FormsModule } from '@angular/forms';
import { HttpClient, HttpParams } from '@angular/common/http';
import { environment } from '../../../../environments/environment';

export interface StudentDto {
  studentCode: string;
  fullName: string;
  className: string;
  dob: Date;
  status: String;
  facultyName: string;
}

declare var bootstrap: any;

@Component({
  selector: 'app-list-student',
  standalone: true,
  imports: [CommonModule, FormsModule, ReactiveFormsModule],
  templateUrl: './list-student.component.html',
  styleUrl: './list-student.component.scss'
})

export class ListStudentComponent implements OnInit{
  private http = inject(HttpClient);
  private fb = inject(FormBuilder);

  facultyCode: string = '';
  students: StudentDto[] = [];
  isLoading = false;
  private apiUrl = `${environment.apiUrl}`

  studentForm: FormGroup;
  isSubmitting = false;

  constructor() {
    const fb = inject(FormBuilder);
    this.studentForm = fb.group({
      username: ['', [Validators.required, Validators.minLength(4)]],
      password: ['', [Validators.required]],
      studentCode: ['', [Validators.required]],
      fullName: ['', [Validators.required]],
      className: ['', [Validators.required]],
      dob: ['', [Validators.required]],
      facultyCode: ['', [Validators.required]],
      enrollment_year: ['', [Validators.required]]
    });
  }

  ngOnInit(): void {
  }

  getStudents() {
    if (!this.facultyCode.trim()) {
      alert('Vui lòng nhập mã khoa!');
      return;
    }

    this.isLoading = true;
    const params = new HttpParams().set('facultyCode', this.facultyCode.trim());
    this.http.get<StudentDto[]>(`${this.apiUrl}/teachers/list-student`, { params })
      .subscribe({
        next: (data) => {
          this.students = data;
          this.isLoading = false;
        },
        error: (err) => {
          console.error('Lỗi khi lấy danh sách:', err);
          alert('Không thể lấy dữ liệu. Kiểm tra lại mã khoa hoặc kết nối!');
          this.isLoading = false;
        }
      });
  }

  onSubmit() {
    if (this.studentForm.invalid) {
      this.studentForm.markAllAsTouched();
      return;
    }

    this.isSubmitting = true;
    this.http.post(`${this.apiUrl}/students`, this.studentForm.value, { responseType: 'text' })
      .subscribe({
        next: (res) => {
          alert('Thêm thành công!');
          this.isSubmitting = false;
          this.studentForm.reset();

          const modalElement = document.getElementById('createStudentModal');
          const modalInstance = bootstrap.Modal.getInstance(modalElement);
          modalInstance.hide();

          this.getStudents();
        },
        error: (err) => {
          this.isSubmitting = false;
          alert('Lỗi: ' + err.error);
        }
      });
  }
}
