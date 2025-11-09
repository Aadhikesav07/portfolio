package com.yourcompany.elearningplatform.controller;

import com.yourcompany.elearningplatform.entity.Exam;
import com.yourcompany.elearningplatform.service.ExamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/exams")
public class ExamController {

    @Autowired
    private ExamService examService;

    @GetMapping("/course/{courseId}")
    public ResponseEntity<List<Exam>> getExamsForCourse(@PathVariable String courseId) {
        return ResponseEntity.ok(examService.getExamsForCourse(courseId));
    }

    @PostMapping
    public ResponseEntity<Exam> addExam(@RequestBody Exam exam) {
        return ResponseEntity.ok(examService.addExam(exam));
    }
}
