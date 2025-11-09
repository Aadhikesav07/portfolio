package com.yourcompany.elearningplatform.controller;

import com.yourcompany.elearningplatform.entity.StudentProgress;
import com.yourcompany.elearningplatform.service.StudentProgressService;
import com.yourcompany.elearningplatform.util.RoleUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/progress")
public class StudentProgressController {

    @Autowired
    private StudentProgressService progressService;

    // Get progress for a course
    @GetMapping("/course/{courseId}")
    public ResponseEntity<StudentProgress> getProgress(@PathVariable String courseId, Authentication authentication) {
        String studentId = RoleUtil.getUserId(authentication);
        
        // Students can only see their own progress
        if (RoleUtil.isStudent(authentication)) {
            StudentProgress progress = progressService.getProgress(studentId, courseId);
            if (progress == null) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.ok(progress);
        }
        
        // Instructors and Admin can see any student's progress (would need studentId param)
        return ResponseEntity.status(403).build();
    }

    // Get all progress for current student
    @GetMapping("/my-progress")
    public ResponseEntity<List<StudentProgress>> getMyProgress(Authentication authentication) {
        if (!RoleUtil.isStudent(authentication)) {
            return ResponseEntity.status(403).build();
        }
        return ResponseEntity.ok(progressService.getProgressByStudent(RoleUtil.getUserId(authentication)));
    }

    // Check exam eligibility
    @GetMapping("/eligibility/{courseId}/{examId}")
    public ResponseEntity<Map<String, Object>> checkExamEligibility(@PathVariable String courseId,
                                                                    @PathVariable String examId,
                                                                    Authentication authentication) {
        if (!RoleUtil.isStudent(authentication)) {
            return ResponseEntity.status(403).build();
        }

        String studentId = RoleUtil.getUserId(authentication);
        StudentProgress progress = progressService.getProgress(studentId, courseId);
        
        Map<String, Object> response = new HashMap<>();
        if (progress == null) {
            response.put("eligible", false);
            response.put("message", "No progress found for this course");
            return ResponseEntity.ok(response);
        }

        // This would need exam's minimum marks requirement
        boolean eligible = progress.isEligibleForExam();
        response.put("eligible", eligible);
        response.put("marksObtained", progress.getMarksObtained());
        response.put("totalMarks", progress.getTotalMarks());
        response.put("mandatoryAssignmentsCompleted", progress.getCompletedMandatoryAssignments().size());
        
        if (!eligible) {
            response.put("message", "Complete all mandatory assignments to be eligible for exam");
        } else {
            response.put("message", "You are eligible for the exam");
        }

        return ResponseEntity.ok(response);
    }
}

