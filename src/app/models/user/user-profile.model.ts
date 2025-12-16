export interface UserDetails {
  studentCode?: string;
  className?: string;
  gpa?: number;
  teacherCode?: string;
  faculty?: string;
  fullName?: string;
  dob?: string;
}

export interface UserProfileResponse {
  username: string;
  role: 'STUDENT' | 'TEACHER' | 'ADMIN';
  details?: UserDetails;
}