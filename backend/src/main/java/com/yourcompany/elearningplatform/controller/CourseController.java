package com.yourcompany.elearningplatform.controller;

import com.yourcompany.elearningplatform.entity.Course;
import com.yourcompany.elearningplatform.service.CourseService;
import com.yourcompany.elearningplatform.util.RoleUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/courses")
public class CourseController {

    @Autowired
    private CourseService courseService;

    // GET - All roles can view courses
    @GetMapping
    public ResponseEntity<List<Course>> getCourses(Authentication authentication) {
        if (RoleUtil.isStudent(authentication)) {
            // Students see only active courses
            return ResponseEntity.ok(courseService.getActiveCourses());
        }
        // Admin and Instructor see all courses
        return ResponseEntity.ok(courseService.getAllCourses());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Course> getCourse(@PathVariable String id, Authentication authentication) {
        Course course = courseService.getCourseById(id);
        if (course == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(course);
    }

    // POST - Only ADMIN can create courses
    @PostMapping
    public ResponseEntity<?> addCourse(@RequestBody Course course, Authentication authentication) {
        if (!RoleUtil.isAdmin(authentication)) {
            return ResponseEntity.status(403).body("Only ADMIN can create courses");
        }
        course.setCreatedBy(RoleUtil.getUserId(authentication));
        return ResponseEntity.ok(courseService.addCourse(course));
    }

    // PUT - Only ADMIN can update courses
    @PutMapping("/{id}")
    public ResponseEntity<?> updateCourse(@PathVariable String id, @RequestBody Course course, Authentication authentication) {
        if (!RoleUtil.isAdmin(authentication)) {
            return ResponseEntity.status(403).body("Only ADMIN can update courses");
        }
        Course updated = courseService.updateCourse(id, course);
        if (updated == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(updated);
    }

    // DELETE - Only ADMIN can delete courses
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteCourse(@PathVariable String id, Authentication authentication) {
        if (!RoleUtil.isAdmin(authentication)) {
            return ResponseEntity.status(403).body("Only ADMIN can delete courses");
        }
        courseService.deleteCourse(id);
        return ResponseEntity.ok().build();
    }

    // Get courses by instructor
    @GetMapping("/instructor/{instructorId}")
    public ResponseEntity<List<Course>> getCoursesByInstructor(@PathVariable String instructorId, Authentication authentication) {
        if (RoleUtil.isStudent(authentication) && !RoleUtil.getUserId(authentication).equals(instructorId)) {
            return ResponseEntity.status(403).body(null);
        }
        return ResponseEntity.ok(courseService.getCoursesByInstructor(instructorId));
    }
}
