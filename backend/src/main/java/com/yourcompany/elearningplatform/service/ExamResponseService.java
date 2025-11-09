package com.yourcompany.elearningplatform.service;

import com.yourcompany.elearningplatform.entity.Exam;
import com.yourcompany.elearningplatform.entity.ExamResponse;
import com.yourcompany.elearningplatform.entity.Question;
import com.yourcompany.elearningplatform.repository.ExamResponseRepository;
import com.yourcompany.elearningplatform.repository.ExamRepository;
import com.yourcompany.elearningplatform.service.StudentProgressService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class ExamResponseService {

    @Autowired
    private ExamResponseRepository examResponseRepository;

    @Autowired
    private ExamRepository examRepository;

    @Autowired
    private StudentProgressService studentProgressService;

    public ExamResponse startExam(String examId, String studentId) {
        // Check if already started
        Optional<ExamResponse> existing = examResponseRepository.findByExamIdAndStudentId(examId, studentId);
        if (existing.isPresent() && existing.get().isSubmitted()) {
            throw new RuntimeException("Exam already submitted");
        }
        if (existing.isPresent()) {
            return existing.get(); // Return existing response
        }

        Exam exam = examRepository.findById(examId)
            .orElseThrow(() -> new RuntimeException("Exam not found"));

        ExamResponse response = new ExamResponse();
        response.setExamId(examId);
        response.setStudentId(studentId);
        response.setCourseId(exam.getCourseId());
        response.setTotalMarks(exam.getQuestions().stream()
            .mapToInt(Question::getMarks)
            .sum());
        response.setStartedAt(LocalDateTime.now());
        response.setAnswers(new HashMap<>());

        return examResponseRepository.save(response);
    }

    public ExamResponse submitExam(String examId, String studentId, Map<String, String> answers) {
        ExamResponse response = examResponseRepository.findByExamIdAndStudentId(examId, studentId)
            .orElseThrow(() -> new RuntimeException("Exam not started"));

        if (response.isSubmitted()) {
            throw new RuntimeException("Exam already submitted");
        }

        Exam exam = examRepository.findById(examId)
            .orElseThrow(() -> new RuntimeException("Exam not found"));

        // Grade the exam
        int marksObtained = 0;
        if (exam.getQuestions() != null && answers != null) {
            for (int i = 0; i < exam.getQuestions().size(); i++) {
                Question question = exam.getQuestions().get(i);
                String studentAnswer = answers.get(String.valueOf(i));
                if (studentAnswer != null && studentAnswer.equalsIgnoreCase(question.getCorrectAnswer())) {
                    marksObtained += question.getMarks();
                }
            }
        }

        response.setAnswers(answers);
        response.setMarksObtained(marksObtained);
        response.setSubmitted(true);
        response.setSubmittedAt(LocalDateTime.now());

        ExamResponse saved = examResponseRepository.save(response);

        // Update student progress
        updateStudentProgress(studentId, exam.getCourseId(), marksObtained);

        return saved;
    }

    public ExamResponse getExamResponse(String examId, String studentId) {
        return examResponseRepository.findByExamIdAndStudentId(examId, studentId)
            .orElse(null);
    }

    public Map<String, String> getExamResults(String examId, String studentId) {
        ExamResponse response = examResponseRepository.findByExamIdAndStudentId(examId, studentId)
            .orElseThrow(() -> new RuntimeException("Exam response not found"));

        if (!response.isSubmitted()) {
            throw new RuntimeException("Exam not yet submitted");
        }

        Exam exam = examRepository.findById(examId)
            .orElseThrow(() -> new RuntimeException("Exam not found"));

        Map<String, String> results = new HashMap<>();
        results.put("totalMarks", String.valueOf(response.getTotalMarks()));
        results.put("marksObtained", String.valueOf(response.getMarksObtained()));
        results.put("percentage", String.valueOf((response.getMarksObtained() * 100) / response.getTotalMarks()));

        // Detailed question results
        Map<String, Map<String, String>> questionResults = new HashMap<>();
        if (exam.getQuestions() != null && response.getAnswers() != null) {
            for (int i = 0; i < exam.getQuestions().size(); i++) {
                Question question = exam.getQuestions().get(i);
                String studentAnswer = response.getAnswers().get(String.valueOf(i));
                boolean isCorrect = studentAnswer != null && 
                    studentAnswer.equalsIgnoreCase(question.getCorrectAnswer());

                Map<String, String> qResult = new HashMap<>();
                qResult.put("question", question.getQuestionText());
                qResult.put("correctAnswer", question.getCorrectAnswer());
                qResult.put("studentAnswer", studentAnswer != null ? studentAnswer : "Not answered");
                qResult.put("isCorrect", String.valueOf(isCorrect));
                qResult.put("marks", String.valueOf(isCorrect ? question.getMarks() : 0));

                questionResults.put(String.valueOf(i), qResult);
            }
        }
        results.put("questionResults", questionResults.toString());

        return results;
    }

    private void updateStudentProgress(String studentId, String courseId, int examMarks) {
        // Student progress is updated through StudentProgressService
        // Exam marks are separate from assignment marks
        studentProgressService.getOrCreateProgress(studentId, courseId);
    }

    public boolean isEligibleForExam(String studentId, String examId) {
        Exam exam = examRepository.findById(examId)
            .orElseThrow(() -> new RuntimeException("Exam not found"));

        return studentProgressService.isEligibleForExam(studentId, exam.getCourseId(), exam.getMinimumMarksRequired());
    }
}

