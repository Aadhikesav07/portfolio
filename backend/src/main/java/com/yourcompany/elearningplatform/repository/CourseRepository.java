package com.yourcompany.elearningplatform.repository;

import com.yourcompany.elearningplatform.entity.Course;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface CourseRepository extends MongoRepository<Course, String> {
    List<Course> findByIsActiveTrue();
    List<Course> findByInstructorId(String instructorId);
}
