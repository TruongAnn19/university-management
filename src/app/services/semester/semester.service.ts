import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../../environments/environment'; 

@Injectable({
  providedIn: 'root'
})
export class SemesterService {
  private http = inject(HttpClient);
  private apiUrl = `${environment.apiUrl}/semesters`;

  importSemesters(file: File) {
    const formData = new FormData();
    formData.append('file', file);

    return this.http.post(`${this.apiUrl}/import`, formData, {
      responseType: 'text'
    });
  }
}