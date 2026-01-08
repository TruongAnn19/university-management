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

  @override
  void initState() {
    super.initState();
    _fetchStudentName();
  }

  Future<void> _fetchStudentName() async {
    final studentService = StudentService();
    // Giả sử API lấy profile
    final profile = await studentService.getProfileInfo();
    if (profile != null && mounted) {
      setState(() {
        studentName = profile.fullName;
      });
    }
  }

  @override
  Widget build(BuildContext context) {
    return LayoutBuilder(
      builder: (context, constraints) {
        if (constraints.maxWidth < 600) {
          return _buildMobileLayout();
        } else {
          return _buildDesktopLayout();
        }
      },
    );
  }

  Widget _buildMobileLayout() {
    return Scaffold(
      backgroundColor: Colors.grey[100],
      appBar: _selectedIndex == 2
          ? null
          : _buildCommonAppBar(),
      body: _buildBodyContent(),
      bottomNavigationBar: BottomNavigationBar(
        currentIndex: _selectedIndex,
        type: BottomNavigationBarType.fixed,
        selectedItemColor: Colors.blue,
        unselectedItemColor: Colors.grey,
        onTap: (index) => setState(() => _selectedIndex = index),
        items: const [
          BottomNavigationBarItem(icon: Icon(Icons.home), label: 'Trang chủ'),
          BottomNavigationBarItem(icon: Icon(Icons.calendar_month), label: 'Lịch học'),
          BottomNavigationBarItem(icon: Icon(Icons.person), label: 'Cá nhân'),
        ],
      ),
    );
  }

  Widget _buildDesktopLayout() {
    return Scaffold(
      backgroundColor: Colors.grey[100],
      appBar: _selectedIndex == 2 ? null : _buildCommonAppBar(),
      body: Row(
        children: [
          NavigationRail(
            selectedIndex: _selectedIndex,
            onDestinationSelected: (index) => setState(() => _selectedIndex = index),
            labelType: NavigationRailLabelType.all,
            destinations: const [
              NavigationRailDestination(
                icon: Icon(Icons.home),
                label: Text('Trang chủ'),
              ),
              NavigationRailDestination(
                icon: Icon(Icons.calendar_month),
                label: Text('Lịch học'),
              ),
              NavigationRailDestination(
                icon: Icon(Icons.person),
                label: Text('Cá nhân'),
              ),
            ],
          ),
          const VerticalDivider(thickness: 1, width: 1),
          Expanded(
            child: _buildBodyContent(),
          ),
        ],
      ),
    );
  }

  AppBar _buildCommonAppBar() {
    return AppBar(
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
              MaterialPageRoute(builder: (context) => const NotificationsScreen()),
            );
          },
        ),
      ],
    );
  }

  Widget _buildBodyContent() {
    switch (_selectedIndex) {
      case 0:
        return _buildDashboard(context);
      case 1:
        return const Center(child: Text("Lịch học (Đang phát triển)"));
      case 2:
        return const ProfileScreen();
      default:
        return _buildDashboard(context);
    }
  }

  Widget _buildDashboard(BuildContext context) {
    List<Map<String, dynamic>> menuItems = [];

    if (isStudent) {
      menuItems.addAll([
        {'icon': Icons.bar_chart, 'label': 'Kết quả học tập', 'color': Colors.green, 'route': '/my-scores'},
        {'icon': Icons.edit_note, 'label': 'Đăng ký tín chỉ', 'color': Colors.orange, 'route': '/register-course'},
        {'icon': Icons.monetization_on, 'label': 'Học phí', 'color': Colors.purple, 'route': '/tuition'},
        {'icon': Icons.assignment, 'label': 'Thi trực tuyến', 'color': Colors.redAccent, 'route': '/online-exam'},
        {'icon': Icons.book, 'label': 'Thư viện số', 'color': Colors.teal, 'route': '/library'},
        {'icon': Icons.medical_services, 'label': 'Y tế học đường', 'color': Colors.pink, 'route': '/health'},
      ]);
    }

    double screenWidth = MediaQuery.of(context).size.width;
    int crossAxisCount = screenWidth > 600 ? 4 : 2;

    return Column(
      children: [
        _buildWelcomeHeader(),
        Expanded(
          child: Padding(
            padding: const EdgeInsets.all(15.0),
            child: GridView.builder(
              itemCount: menuItems.length,
              gridDelegate: SliverGridDelegateWithFixedCrossAxisCount(
                crossAxisCount: crossAxisCount, 
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
                  onTap: () => _handleNavigation(context, item['route'], item['label']),
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
            style: const TextStyle(
              color: Colors.white,
              fontSize: 22,
              fontWeight: FontWeight.bold,
            ),
          ),
          const SizedBox(height: 5),
          const Text(
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
      case '/register-course':
      case '/tuition':
      case '/online-exam':
        ScaffoldMessenger.of(context).showSnackBar(
          SnackBar(content: Text("Chức năng $label đang phát triển")),
        );
        break;
      default:
        ScaffoldMessenger.of(context).showSnackBar(
          SnackBar(content: Text("Chức năng chưa hỗ trợ")),
        );
    }
  }
}