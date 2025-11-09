package com.yourcompany.elearningplatform.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;

@Document(collection = "student_progress")
public class StudentProgress {

    @Id
    private String id;

    private String studentId;
    private String courseId;
    private int totalMarks; // Sum of all assignment marks
    private int marksObtained; // Sum of marks obtained
    private List<String> completedAssignments; // Assignment IDs
    private List<String> completedMandatoryAssignments; // Mandatory assignment IDs
    private boolean isEligibleForExam; // Based on mandatory assignments and marks
    private LocalDateTime enrolledAt;
    private LocalDateTime lastUpdated;

    public StudentProgress() {
        this.enrolledAt = LocalDateTime.now();
        this.lastUpdated = LocalDateTime.now();
        this.isEligibleForExam = false;
    }

    // Getters and setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getStudentId() { return studentId; }
    public void setStudentId(String studentId) { this.studentId = studentId; }

    public String getCourseId() { return courseId; }
    public void setCourseId(String courseId) { this.courseId = courseId; }

    public int getTotalMarks() { return totalMarks; }
    public void setTotalMarks(int totalMarks) { this.totalMarks = totalMarks; }

    public int getMarksObtained() { return marksObtained; }
    public void setMarksObtained(int marksObtained) { this.marksObtained = marksObtained; }

    public List<String> getCompletedAssignments() { return completedAssignments; }
    public void setCompletedAssignments(List<String> completedAssignments) { this.completedAssignments = completedAssignments; }

    public List<String> getCompletedMandatoryAssignments() { return completedMandatoryAssignments; }
    public void setCompletedMandatoryAssignments(List<String> completedMandatoryAssignments) { this.completedMandatoryAssignments = completedMandatoryAssignments; }

    public boolean isEligibleForExam() { return isEligibleForExam; }
    public void setEligibleForExam(boolean eligibleForExam) { isEligibleForExam = eligibleForExam; }

    public LocalDateTime getEnrolledAt() { return enrolledAt; }
    public void setEnrolledAt(LocalDateTime enrolledAt) { this.enrolledAt = enrolledAt; }

    public LocalDateTime getLastUpdated() { return lastUpdated; }
    public void setLastUpdated(LocalDateTime lastUpdated) { this.lastUpdated = lastUpdated; }
}

