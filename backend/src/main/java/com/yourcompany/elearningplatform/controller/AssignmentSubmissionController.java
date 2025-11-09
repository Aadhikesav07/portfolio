package com.yourcompany.elearningplatform.controller;

import com.yourcompany.elearningplatform.entity.AssignmentSubmission;
import com.yourcompany.elearningplatform.service.AssignmentSubmissionService;
import com.yourcompany.elearningplatform.util.RoleUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/assignment-submissions")
public class AssignmentSubmissionController {

    @Autowired
    private AssignmentSubmissionService submissionService;

    // Submit assignment - Students only
    @PostMapping("/submit")
    public ResponseEntity<?> submitAssignment(@RequestBody Map<String, String> request, Authentication authentication) {
        if (!RoleUtil.isStudent(authentication)) {
            return ResponseEntity.status(403).body("Only students can submit assignments");
        }

        String assignmentId = request.get("assignmentId");
        String submissionContent = request.get("submissionContent");

        if (assignmentId == null || submissionContent == null) {
            return ResponseEntity.badRequest().body("assignmentId and submissionContent are required");
        }

        try {
            AssignmentSubmission submission = submissionService.submitAssignment(
                assignmentId,
                RoleUtil.getUserId(authentication),
                submissionContent
            );
            return ResponseEntity.ok(submission);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // Grade assignment - Instructors and Admin only
    @PostMapping("/grade/{submissionId}")
    public ResponseEntity<?> gradeAssignment(@PathVariable String submissionId,
                                            @RequestBody Map<String, Object> request,
                                            Authentication authentication) {
        if (!RoleUtil.isAdminOrInstructor(authentication)) {
            return ResponseEntity.status(403).body("Only ADMIN and INSTRUCTOR can grade assignments");
        }

        Integer marks = (Integer) request.get("marks");
        String feedback = (String) request.get("feedback");

        if (marks == null) {
            return ResponseEntity.badRequest().body("marks is required");
        }

        try {
            AssignmentSubmission submission = submissionService.gradeAssignment(
                submissionId,
                marks,
                feedback != null ? feedback : "",
                RoleUtil.getUserId(authentication)
            );
            return ResponseEntity.ok(submission);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // Get student's submissions
    @GetMapping("/my-submissions")
    public ResponseEntity<List<AssignmentSubmission>> getMySubmissions(Authentication authentication) {
        if (!RoleUtil.isStudent(authentication)) {
            return ResponseEntity.status(403).build();
        }
        return ResponseEntity.ok(submissionService.getSubmissionsByStudent(RoleUtil.getUserId(authentication)));
    }

    // Get submissions for an assignment - Instructors and Admin
    @GetMapping("/assignment/{assignmentId}")
    public ResponseEntity<?> getSubmissionsByAssignment(@PathVariable String assignmentId, Authentication authentication) {
        if (!RoleUtil.isAdminOrInstructor(authentication)) {
            return ResponseEntity.status(403).body("Only ADMIN and INSTRUCTOR can view assignment submissions");
        }
        return ResponseEntity.ok(submissionService.getSubmissionsByAssignment(assignmentId));
    }

    // Get specific submission
    @GetMapping("/{assignmentId}")
    public ResponseEntity<AssignmentSubmission> getSubmission(@PathVariable String assignmentId, Authentication authentication) {
        AssignmentSubmission submission = submissionService.getSubmission(
            assignmentId,
            RoleUtil.getUserId(authentication)
        );
        if (submission == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(submission);
    }
}

