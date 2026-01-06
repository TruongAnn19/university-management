import 'package:flutter/material.dart';
import 'screens/login_screen.dart';
import 'screens/home_screen.dart';
import 'services/auth_service.dart';
import 'package:firebase_core/firebase_core.dart';
import 'package:firebase_app_check/firebase_app_check.dart';
import 'firebase_options.dart';

void main() async {
  WidgetsFlutterBinding.ensureInitialized();
  await Firebase.initializeApp(options: DefaultFirebaseOptions.currentPlatform);
  // Lưu ý: Khi đang code/test trên máy ảo, bạn dùng provider là "debug"
  // Khi nào build ra file apk/ipa để cài lên máy thật thì đổi lại
  await FirebaseAppCheck.instance.activate(
    androidProvider: AndroidProvider.debug,
    appleProvider: AppleProvider.debug,
    webProvider: ReCaptchaV3Provider('6LedfR4sAAAAAKCV2HH9osQQMqC8HQuN-Ucm9NWJ')
  );
  runApp(const MyApp());
}

class MyApp extends StatelessWidget {
  const MyApp({super.key});

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      title: 'Uni Management',
      debugShowCheckedModeBanner: false,
      theme: ThemeData(
        primarySwatch: Colors.blue,
        useMaterial3: true,
        inputDecorationTheme: const InputDecorationTheme(
          border: OutlineInputBorder(),
          contentPadding: EdgeInsets.symmetric(horizontal: 16, vertical: 16),
        ),
      ),
      home: const CheckAuthScreen(),
    );
  }
}

// Widget trung gian để kiểm tra trạng thái đăng nhập
class CheckAuthScreen extends StatefulWidget {
  const CheckAuthScreen({super.key});

  @override
  State<CheckAuthScreen> createState() => _CheckAuthScreenState();
}

class _CheckAuthScreenState extends State<CheckAuthScreen> {
  final AuthService _authService = AuthService();

  // Biến Future để hứng kết quả kiểm tra
  late Future<bool> _checkLoginFuture;

  @override
  void initState() {
    super.initState();
    // Gọi hàm kiểm tra ngay khi app bật lên
    _checkLoginFuture = _authService.isLoggedIn();
  }

  @override
  Widget build(BuildContext context) {
    return FutureBuilder<bool>(
      future: _checkLoginFuture,
      builder: (context, snapshot) {
        // 1. Đang kiểm tra (Loading)
        if (snapshot.connectionState == ConnectionState.waiting) {
          return const Scaffold(
            body: Center(
              child: CircularProgressIndicator(), // Vòng xoay chờ
            ),
          );
        }

        // 2. Nếu có dữ liệu trả về
        if (snapshot.hasData) {
          bool isLogged = snapshot.data!;
          if (isLogged) {
            return const HomeScreen(); // Đã đăng nhập -> Vào trang chủ
          } else {
            return const LoginScreen(); // Chưa đăng nhập -> Vào Login
          }
        }

        // 3. Mặc định về Login nếu có lỗi
        return const LoginScreen();
      },
    );
  }
}
