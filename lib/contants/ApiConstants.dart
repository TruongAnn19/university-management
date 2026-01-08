import 'dart:io'; // Để dùng Platform.isAndroid
import 'package:flutter/foundation.dart'; // Để dùng kIsWeb

class ApiConstants {
  static String get baseUrl {
    if (kIsWeb) {
      return "http://localhost:8081/api";
    } else if (Platform.isAndroid) {
      return "http://10.0.2.2:8081/api";
    } else {
      return "http://localhost:8081/api";
    }
  }
}