package com.yourcompany.elearningplatform.controller;

import com.yourcompany.elearningplatform.entity.Exam;
import com.yourcompany.elearningplatform.entity.Course;
import com.yourcompany.elearningplatform.service.ExamService;
import com.yourcompany.elearningplatform.service.CourseService;
import com.yourcompany.elearningplatform.util.RoleUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/exams")
public class ExamController {

    @Autowired
    private ExamService examService;

    @Autowired
    private CourseService courseService;

    // GET - All roles can view exams
    @GetMapping("/course/{courseId}")
    public ResponseEntity<List<Exam>> getExamsForCourse(@PathVariable String courseId, Authentication authentication) {
        return ResponseEntity.ok(examService.getExamsForCourse(courseId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Exam> getExam(@PathVariable String id, Authentication authentication) {
        Exam exam = examService.getExamById(id);
        if (exam == null) {
            return ResponseEntity.notFound().build();
        }
        
        // For students, don't return correct answers
        if (RoleUtil.isStudent(authentication)) {
            Exam studentExam = new Exam();
            studentExam.setId(exam.getId());
            studentExam.setCourseId(exam.getCourseId());
            studentExam.setTitle(exam.getTitle());
            studentExam.setDescription(exam.getDescription());
            studentExam.setExamDate(exam.getExamDate());
            studentExam.setEndDate(exam.getEndDate());
            studentExam.setDurationMinutes(exam.getDurationMinutes());
            studentExam.setMinimumMarksRequired(exam.getMinimumMarksRequired());
            // Copy questions without correct answers
            if (exam.getQuestions() != null) {
                studentExam.setQuestions(exam.getQuestions().stream()
                    .map(q -> {
                        com.yourcompany.elearningplatform.entity.Question sq = new com.yourcompany.elearningplatform.entity.Question();
                        sq.setQuestionText(q.getQuestionText());
                        sq.setOptionA(q.getOptionA());
                        sq.setOptionB(q.getOptionB());
                        sq.setOptionC(q.getOptionC());
                        sq.setOptionD(q.getOptionD());
                        sq.setMarks(q.getMarks());
                        // Don't set correctAnswer for students
                        return sq;
                    })
                    .collect(java.util.stream.Collectors.toList()));
            }
            return ResponseEntity.ok(studentExam);
        }
        
        return ResponseEntity.ok(exam);
    }

    // POST - Only ADMIN and INSTRUCTOR can create exams
    @PostMapping
    public ResponseEntity<?> addExam(@RequestBody Exam exam, Authentication authentication) {
        if (!RoleUtil.isAdminOrInstructor(authentication)) {
            return ResponseEntity.status(403).body("Only ADMIN and INSTRUCTOR can create exams");
        }

        // Verify instructor can create exam for this course
        if (RoleUtil.isInstructor(authentication)) {
            Course course = courseService.getCourseById(exam.getCourseId());
            if (course == null || !course.getInstructorId().equals(RoleUtil.getUserId(authentication))) {
                return ResponseEntity.status(403).body("Instructor can only create exams for their own courses");
            }
        }

        exam.setCreatedBy(RoleUtil.getUserId(authentication));
        try {
            return ResponseEntity.ok(examService.addExam(exam));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // PUT - Only ADMIN and INSTRUCTOR (own exams) can update
    @PutMapping("/{id}")
    public ResponseEntity<?> updateExam(@PathVariable String id, @RequestBody Exam exam, Authentication authentication) {
        if (!RoleUtil.isAdminOrInstructor(authentication)) {
            return ResponseEntity.status(403).body("Only ADMIN and INSTRUCTOR can update exams");
        }

        if (RoleUtil.isInstructor(authentication)) {
            if (!examService.canInstructorAccessExam(id, RoleUtil.getUserId(authentication))) {
                return ResponseEntity.status(403).body("Instructor can only update their own exams");
            }
        }

        Exam updated = examService.updateExam(id, exam);
        if (updated == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(updated);
    }

    // DELETE - Only ADMIN and INSTRUCTOR (own exams) can delete
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteExam(@PathVariable String id, Authentication authentication) {
        if (!RoleUtil.isAdminOrInstructor(authentication)) {
            return ResponseEntity.status(403).body("Only ADMIN and INSTRUCTOR can delete exams");
        }

        if (RoleUtil.isInstructor(authentication)) {
            if (!examService.canInstructorAccessExam(id, RoleUtil.getUserId(authentication))) {
                return ResponseEntity.status(403).body("Instructor can only delete their own exams");
            }
        }

        examService.deleteExam(id);
        return ResponseEntity.ok().build();
    }
}
