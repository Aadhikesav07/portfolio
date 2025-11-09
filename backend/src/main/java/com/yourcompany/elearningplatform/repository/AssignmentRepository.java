package com.yourcompany.elearningplatform.repository;

import com.yourcompany.elearningplatform.entity.Assignment;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface AssignmentRepository extends MongoRepository<Assignment, String> {
    List<Assignment> findByCourseId(String courseId);
    List<Assignment> findByCourseIdAndIsActiveTrue(String courseId);
    List<Assignment> findByCreatedBy(String createdBy);
    List<Assignment> findByCourseIdAndIsMandatoryTrue(String courseId);
}

