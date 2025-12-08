import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { environment } from '../../../environments/environment'; 
import { ClassResponse, Faculty, RegistrationRequest } from '../../models/registration/registration.model';
@Injectable({
  providedIn: 'root'
})
export class RegistrationService {
  private http = inject(HttpClient);
  private apiUrl = `${environment.apiUrl}`;

  getOpenClasses(facultyId?: number) {
    let params = new HttpParams();
    if (facultyId) {
      params = params.set('facultyId', facultyId);
    }
    return this.http.get<ClassResponse[]>(`${this.apiUrl}/registration/open-classes`, { params });
  }

  registerCourse(request: RegistrationRequest) {
    return this.http.post(`${this.apiUrl}/registration`, request, {
      responseType: 'text'
    });
  }

  getAllFaculties() {
    return this.http.get<Faculty[]>(`${this.apiUrl}/faculties`);
  }
}