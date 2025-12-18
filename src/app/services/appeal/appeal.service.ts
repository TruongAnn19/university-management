import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';

export enum AppealStatus {
  PENDING = 'PENDING',
  APPROVED = 'APPROVED',
  REJECTED = 'REJECTED'
}

export interface AppealRequest {
  scoreId: number;
  reason: string;
}

export interface AppealReviewRequest {
  status: AppealStatus;
  teacherResponse: string;
  newScore?: number;
}

export interface GradeAppeal {
  id: number;
  studentName: string;
  studentCode: string;
  subjectName: string;
  scoreId: number;
  oldScore: number;
  reason: string;
  status: AppealStatus;
  teacherResponse?: string;
  createdAt: string;
}

@Injectable({ providedIn: 'root' })
export class AppealService {
  private http = inject(HttpClient);
  private apiUrl = `${environment.apiUrl}/appeals`;

  createAppeal(request: AppealRequest): Observable<string> {
    return this.http.post(this.apiUrl, request, { responseType: 'text' });
  }

  getMyAppeals(): Observable<GradeAppeal[]> {
    return this.http.get<GradeAppeal[]>(`${environment.apiUrl}/student/me`); 
  }

  getPendingAppeals(): Observable<GradeAppeal[]> {
    return this.http.get<GradeAppeal[]>(`${this.apiUrl}/pending`);
  }

  reviewAppeal(id: number, request: AppealReviewRequest): Observable<string> {
    return this.http.put(`${this.apiUrl}/${id}/review`, request, { responseType: 'text' });
  }
}