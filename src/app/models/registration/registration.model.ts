export interface ClassResponse {
  classCode: string;
  subjectName: string;
  facultyName: string;
  credits: number;
  currentSlot: number;
  maxSlot: number;
}

export interface Faculty {
  id: number;
  facultyCode: string;
  facultyName: string;
}

export interface RegistrationRequest {
  studentCode: string;
  classCode: string;
}