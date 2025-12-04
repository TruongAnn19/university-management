export interface Score {
  subjectName: string;
  credits: number;
  processScore: number;
  finalScore: number;
  totalScore: number; 
}

export interface TranscriptResponse {
  studentName: string;
  facultyName: string;
  status: string;      
  message: string;     
  scores: Score[];     
}