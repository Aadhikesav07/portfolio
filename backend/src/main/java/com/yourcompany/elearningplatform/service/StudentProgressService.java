package com.yourcompany.elearningplatform.service;

import com.yourcompany.elearningplatform.entity.StudentProgress;
import com.yourcompany.elearningplatform.entity.Assignment;
import com.yourcompany.elearningplatform.entity.AssignmentSubmission;
import com.yourcompany.elearningplatform.repository.StudentProgressRepository;
import com.yourcompany.elearningplatform.repository.AssignmentRepository;
import com.yourcompany.elearningplatform.repository.AssignmentSubmissionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class StudentProgressService {

    @Autowired
    private StudentProgressRepository studentProgressRepository;

    @Autowired
    private AssignmentRepository assignmentRepository;

    @Autowired
    private AssignmentSubmissionRepository assignmentSubmissionRepository;

    public StudentProgress getOrCreateProgress(String studentId, String courseId) {
        Optional<StudentProgress> progressOpt = studentProgressRepository
            .findByStudentIdAndCourseId(studentId, courseId);
        
        if (progressOpt.isPresent()) {
            return progressOpt.get();
        }

        StudentProgress progress = new StudentProgress();
        progress.setStudentId(studentId);
        progress.setCourseId(courseId);
        progress.setCompletedAssignments(new ArrayList<>());
        progress.setCompletedMandatoryAssignments(new ArrayList<>());
        return studentProgressRepository.save(progress);
    }

    public StudentProgress updateProgress(String studentId, String courseId) {
        StudentProgress progress = getOrCreateProgress(studentId, courseId);

        // Get all assignments for the course
        List<Assignment> assignments = assignmentRepository.findByCourseId(courseId);
        List<Assignment> mandatoryAssignments = assignmentRepository.findByCourseIdAndIsMandatoryTrue(courseId);

        // Calculate total marks and marks obtained
        int totalMarks = 0;
        int marksObtained = 0;
        List<String> completedAssignments = new ArrayList<>();
        List<String> completedMandatoryAssignments = new ArrayList<>();

        for (Assignment assignment : assignments) {
            totalMarks += assignment.getMaxMarks();
            
            Optional<AssignmentSubmission> submission = assignmentSubmissionRepository
                .findByAssignmentIdAndStudentId(assignment.getId(), studentId);
            
            if (submission.isPresent() && submission.get().isGraded()) {
                completedAssignments.add(assignment.getId());
                marksObtained += submission.get().getMarksObtained();
                
                if (assignment.isMandatory()) {
                    completedMandatoryAssignments.add(assignment.getId());
                }
            }
        }

        // Check if all mandatory assignments are completed
        boolean allMandatoryCompleted = mandatoryAssignments.size() == completedMandatoryAssignments.size();

        progress.setTotalMarks(totalMarks);
        progress.setMarksObtained(marksObtained);
        progress.setCompletedAssignments(completedAssignments);
        progress.setCompletedMandatoryAssignments(completedMandatoryAssignments);
        progress.setEligibleForExam(allMandatoryCompleted);
        progress.setLastUpdated(LocalDateTime.now());

        return studentProgressRepository.save(progress);
    }

    public StudentProgress getProgress(String studentId, String courseId) {
        return studentProgressRepository.findByStudentIdAndCourseId(studentId, courseId)
            .orElse(null);
    }

    public List<StudentProgress> getProgressByStudent(String studentId) {
        return studentProgressRepository.findByStudentId(studentId);
    }

    public boolean isEligibleForExam(String studentId, String courseId, int minimumMarksRequired) {
        StudentProgress progress = getProgress(studentId, courseId);
        if (progress == null) {
            return false;
        }

        // Check marks requirement
        if (progress.getMarksObtained() < minimumMarksRequired) {
            return false;
        }

        // Check mandatory assignments
        return progress.isEligibleForExam();
    }
}

