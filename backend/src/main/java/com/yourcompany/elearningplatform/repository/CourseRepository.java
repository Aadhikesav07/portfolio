package com.yourcompany.elearningplatform.repository;

import com.yourcompany.elearningplatform.entity.Course;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface CourseRepository extends MongoRepository<Course, String> {
}
