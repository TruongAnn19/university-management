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

    getMyTranscript() {
        return this.http.get<TranscriptResponse>(`${this.apiUrl}/my-transcript`);
    }

    importScores(file: File) {
        const formData = new FormData();
        formData.append('file', file);

        return this.http.post(`${this.apiUrl}/import`, formData, {
            responseType: 'text'
        });
    }
}