class StudentProfile {
  final String username;
  final String role;
  final String fullName;
  final String dob;
  final String studentCode;
  final String className;
  final double gpa;

  StudentProfile({
    required this.username,
    required this.role,
    required this.fullName,
    required this.dob,
    required this.studentCode,
    required this.className,
    required this.gpa,
  });

  factory StudentProfile.fromJson(Map<String, dynamic> json) {
    final details = json['details'] ?? {};

    return StudentProfile(
      username: json['username'] ?? '',
      role: json['role'] ?? '',
      fullName: details['fullName'] ?? 'Chưa cập nhật',
      dob: details['dob'] ?? '',
      studentCode: details['studentCode'] ?? '',
      className: details['className'] ?? '',
      gpa: (details['gpa'] is int)
          ? (details['gpa'] as int).toDouble()
          : (details['gpa'] ?? 0.0),
    );
  }
}
