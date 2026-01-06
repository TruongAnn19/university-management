import 'package:flutter/material.dart';
import '../services/student_service.dart';
import '../models/student_profile.dart';
import '../services/auth_service.dart';
import '../screens/login_screen.dart';

class ProfileScreen extends StatefulWidget {
  const ProfileScreen({super.key});

  @override
  State<ProfileScreen> createState() => _ProfileScreenState();
}

class _ProfileScreenState extends State<ProfileScreen> {
  final StudentService _studentService = StudentService();
  late Future<StudentProfile?> _profileFuture;

  @override
  void initState() {
    super.initState();
    _profileFuture = _studentService.getProfileInfo();
  }

  Future<void> _handleLogout(BuildContext context) async {
    // Hiện hộp thoại xác nhận
    final bool? confirm = await showDialog<bool>(
      context: context,
      builder: (context) => AlertDialog(
        title: const Text("Đăng xuất"),
        content: const Text("Bạn có chắc chắn muốn đăng xuất không?"),
        actions: [
          TextButton(
            onPressed: () => Navigator.pop(context, false),
            child: const Text("Hủy"),
          ),
          TextButton(
            onPressed: () => Navigator.pop(context, true),
            child: const Text("Đồng ý", style: TextStyle(color: Colors.red)),
          ),
        ],
      ),
    );

    if (confirm == true) {
      final authService = AuthService();
      await authService.logout();
      if (!context.mounted) return;
      // Reset toàn bộ stack và về Login
      Navigator.pushAndRemoveUntil(
        context,
        MaterialPageRoute(builder: (context) => const LoginScreen()),
        (route) => false,
      );
    }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: Colors.grey[100],
      appBar: AppBar(
        title: const Text("Thông tin sinh viên"),
        backgroundColor: Colors.blue,
        elevation: 0,
        automaticallyImplyLeading: false,
      ),
      body: FutureBuilder<StudentProfile?>(
        future: _profileFuture,
        builder: (context, snapshot) {
          // ... (Giữ nguyên phần loading/error như cũ) ...
          if (snapshot.connectionState == ConnectionState.waiting) {
            return const Center(child: CircularProgressIndicator());
          }

          final profile = snapshot.data;
          // Nếu lỗi hoặc null thì dùng data giả để test hiển thị UI logout
          final displayProfile =
              profile ??
              StudentProfile(
                username: "N/A",
                role: "",
                fullName: "Đang tải...",
                dob: "",
                studentCode: "",
                className: "",
                gpa: 0,
              );

          return SingleChildScrollView(
            child: Column(
              children: [
                _buildHeader(displayProfile),
                const SizedBox(height: 15),
                if (profile != null) _buildStatsCard(displayProfile),
                const SizedBox(height: 15),
                if (profile != null) _buildInfoSection(displayProfile),

                const SizedBox(height: 25),
                // --- NÚT ĐĂNG XUẤT ---
                Padding(
                  padding: const EdgeInsets.symmetric(horizontal: 20),
                  child: SizedBox(
                    width: double.infinity,
                    height: 50,
                    child: ElevatedButton.icon(
                      onPressed: () => _handleLogout(context),
                      style: ElevatedButton.styleFrom(
                        backgroundColor: Colors.red.shade50, // Nền đỏ nhạt
                        foregroundColor: Colors.red, // Chữ đỏ
                        elevation: 0,
                        shape: RoundedRectangleBorder(
                          borderRadius: BorderRadius.circular(15),
                        ),
                      ),
                      icon: const Icon(Icons.logout),
                      label: const Text(
                        "Đăng xuất tài khoản",
                        style: TextStyle(
                          fontSize: 16,
                          fontWeight: FontWeight.bold,
                        ),
                      ),
                    ),
                  ),
                ),
                const SizedBox(height: 40),
              ],
            ),
          );
        },
      ),
    );
  }

  // 1. Header: Avatar + Tên + MSV
  Widget _buildHeader(StudentProfile profile) {
    return Container(
      width: double.infinity,
      decoration: const BoxDecoration(
        color: Colors.blue,
        borderRadius: BorderRadius.only(
          bottomLeft: Radius.circular(30),
          bottomRight: Radius.circular(30),
        ),
      ),
      padding: const EdgeInsets.only(bottom: 30, top: 10),
      child: Column(
        children: [
          const CircleAvatar(
            radius: 50,
            backgroundColor: Colors.white,
            child: Icon(Icons.person, size: 60, color: Colors.blue),
          ),
          const SizedBox(height: 15),
          Text(
            profile.fullName.toUpperCase(),
            style: const TextStyle(
              fontSize: 20,
              fontWeight: FontWeight.bold,
              color: Colors.white,
            ),
          ),
          const SizedBox(height: 5),
          Container(
            padding: const EdgeInsets.symmetric(horizontal: 12, vertical: 4),
            decoration: BoxDecoration(
              color: Colors.white24,
              borderRadius: BorderRadius.circular(20),
            ),
            child: Text(
              "MSV: ${profile.studentCode}",
              style: const TextStyle(color: Colors.white),
            ),
          ),
        ],
      ),
    );
  }

  // 2. Card hiển thị GPA và Vai trò (Mới)
  Widget _buildStatsCard(StudentProfile profile) {
    return Padding(
      padding: const EdgeInsets.symmetric(horizontal: 20),
      child: Row(
        children: [
          Expanded(
            child: _buildStatItem(
              "GPA Tích lũy",
              profile.gpa.toString(),
              Colors.orange,
              Icons.star,
            ),
          ),
          const SizedBox(width: 15),
          Expanded(
            child: _buildStatItem(
              "Vai trò",
              profile.role,
              Colors.green,
              Icons.verified_user,
            ),
          ),
        ],
      ),
    );
  }

  Widget _buildStatItem(
    String label,
    String value,
    Color color,
    IconData icon,
  ) {
    return Container(
      padding: const EdgeInsets.all(15),
      decoration: BoxDecoration(
        color: Colors.white,
        borderRadius: BorderRadius.circular(15),
        boxShadow: [
          BoxShadow(color: Colors.grey.withOpacity(0.1), blurRadius: 10),
        ],
      ),
      child: Column(
        children: [
          Icon(icon, color: color, size: 28),
          const SizedBox(height: 8),
          Text(
            value,
            style: TextStyle(
              fontSize: 18,
              fontWeight: FontWeight.bold,
              color: color,
            ),
          ),
          Text(label, style: const TextStyle(fontSize: 12, color: Colors.grey)),
        ],
      ),
    );
  }

  // 3. Danh sách thông tin chi tiết
  Widget _buildInfoSection(StudentProfile profile) {
    return Padding(
      padding: const EdgeInsets.symmetric(horizontal: 20),
      child: Card(
        shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(15)),
        child: Column(
          children: [
            _buildInfoTile(
              Icons.account_circle,
              "Tên tài khoản",
              profile.username,
            ),
            _buildDivider(),
            _buildInfoTile(Icons.class_, "Lớp hành chính", profile.className),
            _buildDivider(),
            _buildInfoTile(Icons.cake, "Ngày sinh", profile.dob),
            _buildDivider(),
          ],
        ),
      ),
    );
  }

  Widget _buildInfoTile(IconData icon, String title, String value) {
    return ListTile(
      leading: Container(
        padding: const EdgeInsets.all(8),
        decoration: BoxDecoration(
          color: Colors.blue.shade50,
          shape: BoxShape.circle,
        ),
        child: Icon(icon, color: Colors.blue, size: 20),
      ),
      title: Text(
        title,
        style: const TextStyle(fontSize: 13, color: Colors.grey),
      ),
      trailing: Text(
        value,
        style: const TextStyle(fontSize: 15, fontWeight: FontWeight.w500),
      ),
    );
  }

  Widget _buildDivider() => const Divider(height: 1, indent: 60, endIndent: 20);
}
