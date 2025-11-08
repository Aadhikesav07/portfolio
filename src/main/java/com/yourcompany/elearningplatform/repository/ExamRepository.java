package com.yourcompany.elearningplatform.repository;

import com.yourcompany.elearningplatform.entity.Exam;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface ExamRepository extends MongoRepository<Exam, String> {
    List<Exam> findByCourseId(String courseId);
}
