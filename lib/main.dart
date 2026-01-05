import 'package:flutter/material.dart';
import 'screens/login_screen.dart';
import 'screens/home_screen.dart';
import 'services/auth_service.dart';

void main() {
  WidgetsFlutterBinding.ensureInitialized();
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