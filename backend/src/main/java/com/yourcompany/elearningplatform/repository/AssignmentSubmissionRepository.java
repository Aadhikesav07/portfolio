package com.yourcompany.elearningplatform.repository;

import com.yourcompany.elearningplatform.entity.AssignmentSubmission;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface AssignmentSubmissionRepository extends MongoRepository<AssignmentSubmission, String> {
    List<AssignmentSubmission> findByStudentId(String studentId);
    List<AssignmentSubmission> findByAssignmentId(String assignmentId);
    List<AssignmentSubmission> findByCourseId(String courseId);
    Optional<AssignmentSubmission> findByAssignmentIdAndStudentId(String assignmentId, String studentId);
    List<AssignmentSubmission> findByStudentIdAndCourseId(String studentId, String courseId);
}

