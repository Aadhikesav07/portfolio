package com.yourcompany.elearningplatform.controller;

import com.yourcompany.elearningplatform.entity.ExamResponse;
import com.yourcompany.elearningplatform.service.ExamResponseService;
import com.yourcompany.elearningplatform.util.RoleUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/exam-responses")
public class ExamResponseController {

    @Autowired
    private ExamResponseService examResponseService;

    // Start exam - Students only
    @PostMapping("/start/{examId}")
    public ResponseEntity<?> startExam(@PathVariable String examId, Authentication authentication) {
        if (!RoleUtil.isStudent(authentication)) {
            return ResponseEntity.status(403).body("Only students can take exams");
        }

        // Check eligibility
        if (!examResponseService.isEligibleForExam(RoleUtil.getUserId(authentication), examId)) {
            return ResponseEntity.status(403).body("You are not eligible for this exam. Please complete mandatory assignments and achieve minimum marks.");
        }

        try {
            ExamResponse response = examResponseService.startExam(examId, RoleUtil.getUserId(authentication));
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // Submit exam with answers - Students only
    @PostMapping("/submit/{examId}")
    public ResponseEntity<?> submitExam(@PathVariable String examId, 
                                       @RequestBody Map<String, String> answers,
                                       Authentication authentication) {
        if (!RoleUtil.isStudent(authentication)) {
            return ResponseEntity.status(403).body("Only students can submit exams");
        }

        try {
            ExamResponse response = examResponseService.submitExam(
                examId, 
                RoleUtil.getUserId(authentication), 
                answers
            );
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // Get exam results with answer checking - Students only (own results)
    @GetMapping("/results/{examId}")
    public ResponseEntity<?> getExamResults(@PathVariable String examId, Authentication authentication) {
        if (!RoleUtil.isStudent(authentication)) {
            return ResponseEntity.status(403).body("Only students can view their exam results");
        }

        try {
            Map<String, String> results = examResponseService.getExamResults(
                examId, 
                RoleUtil.getUserId(authentication)
            );
            return ResponseEntity.ok(results);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // Get current exam response status
    @GetMapping("/{examId}")
    public ResponseEntity<?> getExamResponse(@PathVariable String examId, Authentication authentication) {
        if (!RoleUtil.isStudent(authentication)) {
            return ResponseEntity.status(403).body("Only students can view their exam responses");
        }

        ExamResponse response = examResponseService.getExamResponse(
            examId, 
            RoleUtil.getUserId(authentication)
        );
        
        if (response == null) {
            return ResponseEntity.notFound().build();
        }
        
        return ResponseEntity.ok(response);
    }
}

