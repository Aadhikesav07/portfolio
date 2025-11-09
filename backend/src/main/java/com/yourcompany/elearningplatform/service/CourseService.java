package com.yourcompany.elearningplatform.service;

import com.yourcompany.elearningplatform.entity.Course;
import com.yourcompany.elearningplatform.repository.CourseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CourseService {

    @Autowired
    private CourseRepository courseRepository;

    public List<Course> getAllCourses() {
        return courseRepository.findAll();
    }

    public List<Course> getActiveCourses() {
        return courseRepository.findByIsActiveTrue();
    }

    public Course getCourseById(String id) {
        Optional<Course> course = courseRepository.findById(id);
        return course.orElse(null);
    }

    public Course addCourse(Course course) {
        return courseRepository.save(course);
    }

    public Course updateCourse(String id, Course course) {
        Optional<Course> existing = courseRepository.findById(id);
        if (existing.isPresent()) {
            Course existingCourse = existing.get();
            existingCourse.setTitle(course.getTitle());
            existingCourse.setDescription(course.getDescription());
            existingCourse.setInstructorId(course.getInstructorId());
            existingCourse.setInstructorName(course.getInstructorName());
            existingCourse.setActive(course.isActive());
            return courseRepository.save(existingCourse);
        }
        return null;
    }

    public void deleteCourse(String courseId) {
        courseRepository.deleteById(courseId);
    }

    public List<Course> getCoursesByInstructor(String instructorId) {
        return courseRepository.findByInstructorId(instructorId);
    }
}
