package com.yourcompany.elearningplatform.service;

import com.yourcompany.elearningplatform.entity.AssignmentSubmission;
import com.yourcompany.elearningplatform.entity.Assignment;
import com.yourcompany.elearningplatform.repository.AssignmentSubmissionRepository;
import com.yourcompany.elearningplatform.repository.AssignmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class AssignmentSubmissionService {

    @Autowired
    private AssignmentSubmissionRepository submissionRepository;

    @Autowired
    private AssignmentRepository assignmentRepository;

    @Autowired
    private StudentProgressService studentProgressService;

    public AssignmentSubmission submitAssignment(String assignmentId, String studentId, String submissionContent) {
        // Verify assignment exists
        Assignment assignment = assignmentRepository.findById(assignmentId)
            .orElseThrow(() -> new RuntimeException("Assignment not found"));

        // Check if already submitted
        Optional<AssignmentSubmission> existing = submissionRepository
            .findByAssignmentIdAndStudentId(assignmentId, studentId);
        
        if (existing.isPresent()) {
            AssignmentSubmission submission = existing.get();
            submission.setSubmissionContent(submissionContent);
            submission.setSubmittedAt(LocalDateTime.now());
            return submissionRepository.save(submission);
        }

        AssignmentSubmission submission = new AssignmentSubmission();
        submission.setAssignmentId(assignmentId);
        submission.setStudentId(studentId);
        submission.setCourseId(assignment.getCourseId());
        submission.setSubmissionContent(submissionContent);
        submission.setSubmittedAt(LocalDateTime.now());

        AssignmentSubmission saved = submissionRepository.save(submission);

        // Update student progress
        studentProgressService.updateProgress(studentId, assignment.getCourseId());

        return saved;
    }

    public AssignmentSubmission gradeAssignment(String submissionId, int marks, String feedback, String graderId) {
        AssignmentSubmission submission = submissionRepository.findById(submissionId)
            .orElseThrow(() -> new RuntimeException("Submission not found"));

        submission.setMarksObtained(marks);
        submission.setFeedback(feedback);
        submission.setGraded(true);
        submission.setGradedAt(LocalDateTime.now());

        AssignmentSubmission saved = submissionRepository.save(submission);

        // Update student progress after grading
        studentProgressService.updateProgress(submission.getStudentId(), submission.getCourseId());

        return saved;
    }

    public List<AssignmentSubmission> getSubmissionsByStudent(String studentId) {
        return submissionRepository.findByStudentId(studentId);
    }

    public List<AssignmentSubmission> getSubmissionsByAssignment(String assignmentId) {
        return submissionRepository.findByAssignmentId(assignmentId);
    }

    public AssignmentSubmission getSubmission(String assignmentId, String studentId) {
        return submissionRepository.findByAssignmentIdAndStudentId(assignmentId, studentId)
            .orElse(null);
    }
}

