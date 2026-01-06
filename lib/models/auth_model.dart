class LoginResponse {
  final String token;
  final String username;
  final String role;

  LoginResponse({
    required this.token,
    required this.username,
    required this.role,
  });

  factory LoginResponse.fromJson(Map<String, dynamic> json) {
    return LoginResponse(
      token: json['token'] ?? json['accessToken'] ?? "",
      username: json['username'] ?? "",
      role: (json['roles'] != null && (json['roles'] as List).isNotEmpty)
          ? json['roles'][0]
          : (json['role'] ?? ""),
    );
  }
}

class LoginRequest {
  final String username;
  final String password;
  final String recaptchaToken;

  LoginRequest({
    required this.username,
    required this.password,
    required this.recaptchaToken,
  });

  Map<String, dynamic> toJson() {
    return {
      "username": username,
      "password": password,
      "recaptchaToken": recaptchaToken,
    };
  }
}
