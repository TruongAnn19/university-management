import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../../environments/environment'; 
import { TranscriptResponse } from '../../models/score/score.model'; 

@Injectable({
  providedIn: 'root'
})
export class ScoreService {
  private http = inject(HttpClient);
  private apiUrl = `${environment.apiUrl}/scores`;

  getMyTranscript(studentCode: string) {
    return this.http.get<TranscriptResponse>(`${this.apiUrl}/${studentCode}`);
  }
}