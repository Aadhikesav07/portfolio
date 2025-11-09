package com.yourcompany.elearningplatform.controller;

import com.yourcompany.elearningplatform.entity.Assignment;
import com.yourcompany.elearningplatform.entity.Course;
import com.yourcompany.elearningplatform.service.AssignmentService;
import com.yourcompany.elearningplatform.service.CourseService;
import com.yourcompany.elearningplatform.util.RoleUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/assignments")
public class AssignmentController {

    @Autowired
    private AssignmentService assignmentService;

    @Autowired
    private CourseService courseService;

    // GET - All roles can view assignments
    @GetMapping("/course/{courseId}")
    public ResponseEntity<List<Assignment>> getAssignmentsByCourse(@PathVariable String courseId, Authentication authentication) {
        return ResponseEntity.ok(assignmentService.getAssignmentsByCourse(courseId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Assignment> getAssignment(@PathVariable String id, Authentication authentication) {
        Assignment assignment = assignmentService.getAssignmentById(id);
        if (assignment == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(assignment);
    }

    // POST - Only ADMIN and INSTRUCTOR can create assignments
    @PostMapping
    public ResponseEntity<?> addAssignment(@RequestBody Assignment assignment, Authentication authentication) {
        if (!RoleUtil.isAdminOrInstructor(authentication)) {
            return ResponseEntity.status(403).body("Only ADMIN and INSTRUCTOR can create assignments");
        }

        // Verify instructor can create assignment for this course
        if (RoleUtil.isInstructor(authentication)) {
            Course course = courseService.getCourseById(assignment.getCourseId());
            if (course == null || !course.getInstructorId().equals(RoleUtil.getUserId(authentication))) {
                return ResponseEntity.status(403).body("Instructor can only create assignments for their own courses");
            }
        }

        assignment.setCreatedBy(RoleUtil.getUserId(authentication));
        try {
            return ResponseEntity.ok(assignmentService.addAssignment(assignment));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // PUT - Only ADMIN and INSTRUCTOR (own assignments) can update
    @PutMapping("/{id}")
    public ResponseEntity<?> updateAssignment(@PathVariable String id, @RequestBody Assignment assignment, Authentication authentication) {
        if (!RoleUtil.isAdminOrInstructor(authentication)) {
            return ResponseEntity.status(403).body("Only ADMIN and INSTRUCTOR can update assignments");
        }

        if (RoleUtil.isInstructor(authentication)) {
            if (!assignmentService.canInstructorAccessAssignment(id, RoleUtil.getUserId(authentication))) {
                return ResponseEntity.status(403).body("Instructor can only update their own assignments");
            }
        }

        Assignment updated = assignmentService.updateAssignment(id, assignment);
        if (updated == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(updated);
    }

    // DELETE - Only ADMIN and INSTRUCTOR (own assignments) can delete
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteAssignment(@PathVariable String id, Authentication authentication) {
        if (!RoleUtil.isAdminOrInstructor(authentication)) {
            return ResponseEntity.status(403).body("Only ADMIN and INSTRUCTOR can delete assignments");
        }

        if (RoleUtil.isInstructor(authentication)) {
            if (!assignmentService.canInstructorAccessAssignment(id, RoleUtil.getUserId(authentication))) {
                return ResponseEntity.status(403).body("Instructor can only delete their own assignments");
            }
        }

        assignmentService.deleteAssignment(id);
        return ResponseEntity.ok().build();
    }

    // Get mandatory assignments for a course
    @GetMapping("/mandatory/course/{courseId}")
    public ResponseEntity<List<Assignment>> getMandatoryAssignments(@PathVariable String courseId, Authentication authentication) {
        return ResponseEntity.ok(assignmentService.getMandatoryAssignments(courseId));
    }
}

