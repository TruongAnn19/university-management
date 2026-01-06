import 'dart:convert';
import 'package:http/http.dart' as http;
import 'package:flutter_secure_storage/flutter_secure_storage.dart';
import '../models/notification_model.dart';
import '../models/score_model.dart';
import '../models/student_profile.dart';

class StudentService {
  final String baseUrl = "http://localhost:8081/api";
  final _storage = const FlutterSecureStorage();
  Future<Map<String, String>> _getHeaders() async {
    final token = await _storage.read(key: "jwt_token");
    print("DEBUG TOKEN: $token");
    return {
      'Content-Type': 'application/json',
      'Authorization': 'Bearer ${token ?? ""}',
    };
  }

  Future<StudentProfile?> getProfileInfo() async {
    final headers = await _getHeaders();
    try {
      final response = await http.get(
        Uri.parse('$baseUrl/profiles/me'),
        headers: headers,
      );

      if (response.statusCode == 200) {
        final data = jsonDecode(utf8.decode(response.bodyBytes));
        return StudentProfile.fromJson(data);
      }
    } catch (e) {
      print("Exception getProfile: $e");
    }
    return null;
  }

  // 1. GET: Xem bảng điểm
  Future<TranscriptResponse?> getTranscript() async {
    final headers = await _getHeaders();
    try {
      final response = await http.get(
        Uri.parse('$baseUrl/scores/my-transcript'),
        headers: headers,
      );

      if (response.statusCode == 200) {
        final data = jsonDecode(utf8.decode(response.bodyBytes));
        return TranscriptResponse.fromJson(data);
      }
    } catch (e) {
      print("Lỗi getTranscript: $e");
    }
    return null;
  }

  // 2. GET: Lấy danh sách thông báo
  Future<List<NotificationModel>> getNotifications() async {
    final headers = await _getHeaders();
    try {
      final response = await http.get(
        Uri.parse('$baseUrl/notifications'),
        headers: headers,
      );

      if (response.statusCode == 200) {
        final List<dynamic> data = jsonDecode(utf8.decode(response.bodyBytes));
        return data.map((e) => NotificationModel.fromJson(e)).toList();
      }
    } catch (e) {
      print("Lỗi getNotifications: $e");
    }
    return [];
  }

  // 3. PUT: Đánh dấu đã đọc
  Future<bool> markNotificationRead(int id) async {
    final headers = await _getHeaders();
    try {
      final response = await http.put(
        Uri.parse('$baseUrl/notifications/$id/read'),
        headers: headers,
      );
      return response.statusCode == 200;
    } catch (e) {
      print("Lỗi markRead: $e");
      return false;
    }
  }

  // 4. POST: Cập nhật FCM Token
  Future<void> updateFcmToken(String fcmToken) async {
    final headers = await _getHeaders();
    try {
      await http.post(
        Uri.parse('$baseUrl/notifications/fcm-token'),
        headers: headers,
        body: jsonEncode({"token": fcmToken}),
      );
    } catch (e) {
      print("Lỗi updateFcmToken: $e");
    }
  }
}
