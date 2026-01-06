class TranscriptResponse {
  final String studentName;
  final String facultyName;
  final String status;
  final List<Score> scores;

  TranscriptResponse({
    required this.studentName,
    required this.facultyName,
    required this.status,
    required this.scores,
  });

  factory TranscriptResponse.fromJson(Map<String, dynamic> json) {
    return TranscriptResponse(
      studentName: json['studentName'] ?? '',
      facultyName: json['facultyName'] ?? '',
      status: json['status'] ?? '',
      scores: (json['scores'] as List)
          .map((item) => Score.fromJson(item))
          .toList(),
    );
  }
}

class Score {
  final int id;
  final String subjectName;
  final int credits;
  final double processScore;
  final double finalScore;
  final double totalScore;

  Score({
    required this.id,
    required this.subjectName,
    required this.credits,
    required this.processScore,
    required this.finalScore,
    required this.totalScore,
  });

  factory Score.fromJson(Map<String, dynamic> json) {
    return Score(
      id: json['id'] ?? 0,
      subjectName: json['subjectName'] ?? '',
      credits: json['credits'] ?? 0,
      processScore: (json['processScore'] ?? 0).toDouble(),
      finalScore: (json['finalScore'] ?? 0).toDouble(),
      totalScore: (json['totalScore'] ?? 0).toDouble(),
    );
  }
}

