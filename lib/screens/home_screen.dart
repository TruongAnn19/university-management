import 'package:flutter/material.dart';
import 'my_scores_screen.dart';
import 'notifications_screen.dart';
import 'profile_screen.dart';
import '../services/student_service.dart';

class HomeScreen extends StatefulWidget {
  const HomeScreen({super.key});

  @override
  State<HomeScreen> createState() => _HomeScreenState();
}

class _HomeScreenState extends State<HomeScreen> {
  int _selectedIndex = 0;
  final bool isStudent = true;
  String studentName = "Sinh viên";

  void _onItemTapped(int index) {
    setState(() {
      _selectedIndex = index;
    });
  }

  @override
  void initState() {
    super.initState();
    _fetchStudentName();
  }

  Future<void> _fetchStudentName() async {
    final studentService = StudentService();
    final profile = await studentService.getProfileInfo();
    if (profile != null && mounted) {
      setState(() {
        studentName = profile.fullName;
      });
    }
  }

  @override
  Widget build(BuildContext context) {
    final List<Widget> pages = [
      _buildDashboard(context),
      const Center(child: Text("Lịch học (Đang phát triển)")),
      const ProfileScreen(),
    ];
    return Scaffold(
      backgroundColor: Colors.grey[100],
      appBar: _selectedIndex == 2
          ? null
          : AppBar(
              title: const Text("Cổng thông tin sinh viên"),
              backgroundColor: Colors.blue,
              foregroundColor: Colors.white,
              elevation: 0,
              automaticallyImplyLeading: false,
              actions: [
                IconButton(
                  icon: const Icon(Icons.notifications),
                  onPressed: () {
                    Navigator.push(
                      context,
                      MaterialPageRoute(
                        builder: (context) => const NotificationsScreen(),
                      ),
                    );
                  },
                ),
              ],
            ),
      body: pages[_selectedIndex],
      bottomNavigationBar: BottomNavigationBar(
        currentIndex: _selectedIndex,
        type: BottomNavigationBarType.fixed,
        selectedItemColor: Colors.blue,
        unselectedItemColor: Colors.grey,
        onTap: _onItemTapped,
        items: const [
          BottomNavigationBarItem(icon: Icon(Icons.home), label: 'Trang chủ'),
          BottomNavigationBarItem(
            icon: Icon(Icons.calendar_month),
            label: 'Lịch học',
          ),
          BottomNavigationBarItem(icon: Icon(Icons.person), label: 'Cá nhân'),
        ],
      ),
    );
  }
  // --- WIDGETS CON ---

  Widget _buildDashboard(BuildContext context) {
    // 1. Chỉ giữ lại các chức năng nghiệp vụ, BỎ "Thông tin cá nhân"
    List<Map<String, dynamic>> menuItems = [];

    if (isStudent) {
      menuItems.addAll([
        {
          'icon': Icons.bar_chart,
          'label': 'Kết quả học tập',
          'color': Colors.green,
          'route': '/my-scores',
        },
        {
          'icon': Icons.edit_note,
          'label': 'Đăng ký tín chỉ',
          'color': Colors.orange,
          'route': '/register-course',
        },
        {
          'icon': Icons.monetization_on,
          'label': 'Học phí',
          'color': Colors.purple,
          'route': '/tuition',
        },
        {
          'icon': Icons.assignment,
          'label': 'Thi trực tuyến',
          'color': Colors.redAccent,
          'route': '/online-exam',
        },
      ]);
    }

    return Column(
      children: [
        _buildWelcomeHeader(),
        Expanded(
          child: Padding(
            padding: const EdgeInsets.all(15.0),
            child: GridView.builder(
              itemCount: menuItems.length,
              gridDelegate: const SliverGridDelegateWithFixedCrossAxisCount(
                crossAxisCount: 2,
                crossAxisSpacing: 15,
                mainAxisSpacing: 15,
                childAspectRatio: 1.2,
              ),
              itemBuilder: (context, index) {
                final item = menuItems[index];
                return _buildMenuCard(
                  icon: item['icon'],
                  label: item['label'],
                  color: item['color'],
                  onTap: () =>
                      _handleNavigation(context, item['route'], item['label']),
                );
              },
            ),
          ),
        ),
      ],
    );
  }

  Widget _buildWelcomeHeader() {
    return Container(
      width: double.infinity,
      padding: const EdgeInsets.all(20),
      decoration: const BoxDecoration(
        color: Colors.blue,
        borderRadius: BorderRadius.only(
          bottomLeft: Radius.circular(20),
          bottomRight: Radius.circular(20),
        ),
      ),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          Text(
            "Xin chào, $studentName!",
            style: TextStyle(
              color: Colors.white,
              fontSize: 22,
              fontWeight: FontWeight.bold,
            ),
          ),
          SizedBox(height: 5),
          Text(
            "Chúc bạn một ngày học tập hiệu quả.",
            style: TextStyle(color: Colors.white70),
          ),
        ],
      ),
    );
  }

  Widget _buildMenuCard({
    required IconData icon,
    required String label,
    required Color color,
    required VoidCallback onTap,
  }) {
    return Material(
      color: Colors.white,
      borderRadius: BorderRadius.circular(15),
      elevation: 2,
      child: InkWell(
        onTap: onTap,
        borderRadius: BorderRadius.circular(15),
        child: Column(
          mainAxisAlignment: MainAxisAlignment.center,
          children: [
            Container(
              padding: const EdgeInsets.all(12),
              decoration: BoxDecoration(
                color: color.withOpacity(0.1),
                shape: BoxShape.circle,
              ),
              child: Icon(icon, size: 32, color: color),
            ),
            const SizedBox(height: 12),
            Text(
              label,
              textAlign: TextAlign.center,
              style: const TextStyle(
                fontSize: 15,
                fontWeight: FontWeight.w600,
                color: Colors.black87,
              ),
            ),
          ],
        ),
      ),
    );
  }

  void _handleNavigation(BuildContext context, String route, String label) {
    switch (route) {
      case '/my-scores':
        Navigator.push(
          context,
          MaterialPageRoute(builder: (_) => const MyScoresScreen()),
        );
        break;
      // Các case khác...
      default:
        ScaffoldMessenger.of(context).showSnackBar(
          SnackBar(content: Text("Chức năng $label đang phát triển")),
        );
    }
  }
}
