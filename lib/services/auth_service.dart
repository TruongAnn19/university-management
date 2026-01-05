import 'dart:convert';
import 'package:http/http.dart' as http;
import 'package:flutter_secure_storage/flutter_secure_storage.dart';
import '../models/auth_model.dart';

class AuthService {
  static const String baseUrl = "http://localhost:8081/api/auth";

  final _storage = const FlutterSecureStorage();

  Future<bool> login(String username, String password, String captchaToken) async {
    try {
      print("Đang gọi API: $baseUrl/login");

      final requestBody = LoginRequest(
        username: username,
        password: password,
        recaptchaToken: captchaToken,
      );

      final response = await http.post(
        Uri.parse("$baseUrl/login"),
        headers: {"Content-Type": "application/json"},
        body: jsonEncode(requestBody.toJson()),
      );

      print("Response status: ${response.statusCode}");
      print("Response body: ${response.body}");

      if (response.statusCode == 200) {
        final data = LoginResponse.fromJson(
          jsonDecode(utf8.decode(response.bodyBytes)),
        );
        await _storage.write(key: "jwt_token", value: data.token);
        await _storage.write(key: "username", value: data.username);

        return true;
      } else {
        return false;
      }
    } catch (e) {
      print("Lỗi kết nối: $e");
      return false;
    }
  }

  Future<void> logout() async {
    await _storage.deleteAll();
  }

  Future<bool> isLoggedIn() async {
    String? token = await _storage.read(key: "jwt_token");
    return token != null && token.isNotEmpty;
  }
}
