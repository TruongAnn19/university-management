export interface Score {
  subjectName: string;
  credits: number;
  processScore: number;
  finalScore: number;
  totalScore: number; 
}

export interface CreateScoreRequest {
  studentCode: string;
  subjectCode: string;
  processScore: number;
  finalScore: number;
}

export interface TranscriptResponse {
  studentName: string;
  facultyName: string;
  status: string;      
  message: string;     
  scores: Score[];     
}