import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface StudentDTO {
  id: number;
  studentCode: string;
  fullName: string;
  classCode: string; // Lớp hành chính (VD: CNTT-K65)
  email: string;
}

@Injectable({ providedIn: 'root' })
export class StudentService {
  private apiUrl = 'http://localhost:8081/api/students';

  constructor(private http: HttpClient) {}

  getAll(): Observable<StudentDTO[]> {
    return this.http.get<StudentDTO[]>(this.apiUrl);
  }

  getByCode(code: string): Observable<StudentDTO> {
    return this.http.get<StudentDTO>(`${this.apiUrl}/code/${code}`);
  }
}