export interface ClassResponse {
  classCode: string;
  subjectName: string;
  facultyName: string;
  credits: number;
  currentSlot: number;
  maxSlot: number;
  studyStatus: string;
  isRegistered: boolean;
  isMyClass: boolean;
  semesterName: string;
  startDate: string;
  endDate: string;
  timeStatus: string;
}

export interface Faculty {
  id: number;
  facultyCode: string;
  facultyName: string;
}

export interface RegistrationRequest {
  // studentCode: string;
  classCode: string;
}