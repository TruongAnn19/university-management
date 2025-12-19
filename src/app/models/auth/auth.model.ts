export interface LoginRequest {
  username: string;
  password: string;
  recaptchaToken: string;
}

export interface AuthResponse {
  token: string;
  message: string;
}

export interface UserInfo {
  sub: string;
  roles: string[];
  exp: number;
}

export interface Register {
  username: string;
  password: string;
  role: "ADMIN" | "STUDENT" | "TEACHER";
  adminKey: String;
}