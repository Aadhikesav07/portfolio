package com.yourcompany.elearningplatform.repository;

import com.yourcompany.elearningplatform.entity.StudentProgress;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface StudentProgressRepository extends MongoRepository<StudentProgress, String> {
    List<StudentProgress> findByStudentId(String studentId);
    List<StudentProgress> findByCourseId(String courseId);
    Optional<StudentProgress> findByStudentIdAndCourseId(String studentId, String courseId);
}

