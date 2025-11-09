package com.yourcompany.elearningplatform.service;

import com.yourcompany.elearningplatform.entity.Exam;
import com.yourcompany.elearningplatform.entity.Course;
import com.yourcompany.elearningplatform.repository.ExamRepository;
import com.yourcompany.elearningplatform.repository.CourseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ExamService {

    @Autowired
    private ExamRepository examRepository;

    @Autowired
    private CourseRepository courseRepository;

    public List<Exam> getExamsForCourse(String courseId) {
        return examRepository.findByCourseIdAndIsActiveTrue(courseId);
    }

    public Exam getExamById(String id) {
        Optional<Exam> exam = examRepository.findById(id);
        return exam.orElse(null);
    }

    public Exam addExam(Exam exam) {
        // Verify course exists
        Optional<Course> course = courseRepository.findById(exam.getCourseId());
        if (course.isEmpty()) {
            throw new RuntimeException("Course not found");
        }
        return examRepository.save(exam);
    }

    public Exam updateExam(String id, Exam exam) {
        Optional<Exam> existing = examRepository.findById(id);
        if (existing.isPresent()) {
            Exam existingExam = existing.get();
            existingExam.setTitle(exam.getTitle());
            existingExam.setDescription(exam.getDescription());
            existingExam.setQuestions(exam.getQuestions());
            existingExam.setExamDate(exam.getExamDate());
            existingExam.setEndDate(exam.getEndDate());
            existingExam.setDurationMinutes(exam.getDurationMinutes());
            existingExam.setMinimumMarksRequired(exam.getMinimumMarksRequired());
            existingExam.setActive(exam.isActive());
            return examRepository.save(existingExam);
        }
        return null;
    }

    public void deleteExam(String id) {
        examRepository.deleteById(id);
    }

    public List<Exam> getExamsByCreator(String createdBy) {
        return examRepository.findByCreatedBy(createdBy);
    }

    public boolean canInstructorAccessExam(String examId, String instructorId) {
        Optional<Exam> exam = examRepository.findById(examId);
        if (exam.isEmpty()) return false;
        
        Exam e = exam.get();
        // Check if instructor created this exam or is assigned to the course
        Optional<Course> course = courseRepository.findById(e.getCourseId());
        if (course.isEmpty()) return false;
        
        return e.getCreatedBy().equals(instructorId) || 
               course.get().getInstructorId().equals(instructorId);
    }
}
