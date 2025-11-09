package com.yourcompany.elearningplatform.repository;

import com.yourcompany.elearningplatform.entity.ExamResponse;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface ExamResponseRepository extends MongoRepository<ExamResponse, String> {
    List<ExamResponse> findByStudentId(String studentId);
    List<ExamResponse> findByExamId(String examId);
    Optional<ExamResponse> findByExamIdAndStudentId(String examId, String studentId);
    List<ExamResponse> findByCourseId(String courseId);
}

