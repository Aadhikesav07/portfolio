package com.yourcompany.elearningplatform.service;

import com.yourcompany.elearningplatform.entity.Exam;
import com.yourcompany.elearningplatform.repository.ExamRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ExamService {

    @Autowired
    private ExamRepository examRepository;

    public List<Exam> getExamsForCourse(String courseId) {
        return examRepository.findByCourseId(courseId);
    }

    public Exam addExam(Exam exam) {
        return examRepository.save(exam);
    }
}
