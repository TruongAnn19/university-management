import 'package:flutter/material.dart';
import '../services/student_service.dart';
import '../models/notification_model.dart';

class NotificationsScreen extends StatefulWidget {
  const NotificationsScreen({super.key});

  @override
  State<NotificationsScreen> createState() => _NotificationsScreenState();
}

class _NotificationsScreenState extends State<NotificationsScreen> {
  final StudentService _studentService = StudentService();
  List<NotificationModel> _notifications = [];
  bool _isLoading = true;

  @override
  void initState() {
    super.initState();
    _loadNotifications();
  }

  // Gọi API lấy danh sách
  void _loadNotifications() async {
    final notifs = await _studentService.getNotifications();
    setState(() {
      _notifications = notifs;
      _isLoading = false;
    });
  }

  // Xử lý khi bấm vào thông báo
  void _onNotificationTap(NotificationModel notif) async {
    if (!notif.isRead) {
      // 1. Gọi API mark read
      await _studentService.markNotificationRead(notif.id);

      // 2. Cập nhật UI ngay lập tức
      setState(() {
        notif.isRead = true;
      });
    }

    // 3. Hiển thị nội dung chi tiết (Alert Dialog)
    showDialog(
      context: context,
      builder: (ctx) => AlertDialog(
        title: Text(notif.title),
        content: Text(notif.message),
        actions: [
          TextButton(
            onPressed: () => Navigator.pop(ctx),
            child: const Text("Đóng"),
          ),
        ],
      ),
    );
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text("Thông báo"),
        backgroundColor: Colors.blue,
      ),
      body: _isLoading
          ? const Center(child: CircularProgressIndicator())
          : _notifications.isEmpty
          ? const Center(child: Text("Không có thông báo nào."))
          : ListView.builder(
              itemCount: _notifications.length,
              itemBuilder: (context, index) {
                final notif = _notifications[index];
                return Card(
                  color: notif.isRead
                      ? Colors.white
                      : Colors.blue.shade50, // Chưa đọc thì màu xanh nhạt
                  margin: const EdgeInsets.symmetric(
                    horizontal: 10,
                    vertical: 5,
                  ),
                  child: ListTile(
                    leading: Icon(
                      notif.isRead
                          ? Icons.notifications_none
                          : Icons.notifications_active,
                      color: notif.isRead ? Colors.grey : Colors.blue,
                    ),
                    title: Text(
                      notif.title,
                      style: TextStyle(
                        fontWeight: notif.isRead
                            ? FontWeight.normal
                            : FontWeight.bold,
                      ),
                    ),
                    subtitle: Column(
                      crossAxisAlignment: CrossAxisAlignment.start,
                      children: [
                        Text(
                          notif.message,
                          maxLines: 2,
                          overflow: TextOverflow.ellipsis,
                        ),
                        const SizedBox(height: 5),
                        Text(
                          notif
                              .createdAt, // Bạn có thể format lại ngày tháng nếu cần
                          style: const TextStyle(
                            fontSize: 12,
                            color: Colors.grey,
                          ),
                        ),
                      ],
                    ),
                    onTap: () => _onNotificationTap(notif),
                  ),
                );
              },
            ),
    );
  }
}
