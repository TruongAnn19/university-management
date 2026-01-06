import 'package:flutter/material.dart';
import '../services/student_service.dart';
import '../models/score_model.dart';

class MyScoresScreen extends StatefulWidget {
  const MyScoresScreen({super.key});

  @override
  State<MyScoresScreen> createState() => _MyScoresScreenState();
}

class _MyScoresScreenState extends State<MyScoresScreen> {
  final StudentService _studentService = StudentService();
  late Future<TranscriptResponse?> _transcriptFuture;

  @override
  void initState() {
    super.initState();
    _transcriptFuture = _studentService.getTranscript();
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text("Kết quả học tập"),
        backgroundColor: Colors.blue,
      ),
      body: FutureBuilder<TranscriptResponse?>(
        future: _transcriptFuture,
        builder: (context, snapshot) {
          if (snapshot.connectionState == ConnectionState.waiting) {
            return const Center(child: CircularProgressIndicator());
          } else if (snapshot.hasError || !snapshot.hasData) {
            return const Center(child: Text("Không thể tải bảng điểm."));
          }

          final data = snapshot.data!;
          
          return SingleChildScrollView(
            padding: const EdgeInsets.all(16),
            child: Column(
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                // Thông tin chung
                Card(
                  color: Colors.blue.shade50,
                  child: Padding(
                    padding: const EdgeInsets.all(16.0),
                    child: Column(
                      crossAxisAlignment: CrossAxisAlignment.start,
                      children: [
                        Text("Sinh viên: ${data.studentName}", style: const TextStyle(fontSize: 18, fontWeight: FontWeight.bold)),
                        const SizedBox(height: 5),
                        Text("Khoa: ${data.facultyName}"),
                        const SizedBox(height: 5),
                        Text("Trạng thái: ${data.status}", style: const TextStyle(color: Colors.green, fontWeight: FontWeight.bold)),
                      ],
                    ),
                  ),
                ),
                const SizedBox(height: 20),
                const Text("Bảng điểm chi tiết:", style: TextStyle(fontSize: 16, fontWeight: FontWeight.bold)),
                const SizedBox(height: 10),
                
                // Bảng điểm
                SingleChildScrollView(
                  scrollDirection: Axis.horizontal, // Cho phép cuộn ngang nếu bảng rộng
                  child: DataTable(
                    border: TableBorder.all(color: Colors.grey.shade300),
                    headingRowColor: MaterialStateProperty.all(Colors.grey.shade200),
                    columns: const [
                      DataColumn(label: Text("Môn học")),
                      DataColumn(label: Text("TC")),
                      DataColumn(label: Text("QT")),
                      DataColumn(label: Text("Thi")),
                      DataColumn(label: Text("Tổng")),
                    ],
                    rows: data.scores.map((score) {
                      return DataRow(cells: [
                        DataCell(Text(score.subjectName)),
                        DataCell(Text(score.credits.toString())),
                        DataCell(Text(score.processScore.toString())),
                        DataCell(Text(score.finalScore.toString())),
                        DataCell(Text(
                          score.totalScore.toString(),
                          style: TextStyle(
                            fontWeight: FontWeight.bold,
                            color: score.totalScore >= 4.0 ? Colors.black : Colors.red,
                          ),
                        )),
                      ]);
                    }).toList(),
                  ),
                ),
              ],
            ),
          );
        },
      ),
    );
  }
}