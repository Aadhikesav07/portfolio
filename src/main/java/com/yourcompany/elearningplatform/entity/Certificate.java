package com.yourcompany.elearningplatform.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "certificates")
public class Certificate {

    @Id
    private String id;

    private String userId;
    private String courseId;
    private String examId;
    private LocalDateTime issueDate;

    public Certificate() {}

    public Certificate(String userId, String courseId, String examId) {
        this.userId = userId;
        this.courseId = courseId;
        this.examId = examId;
        this.issueDate = LocalDateTime.now();
    }

    // Getters and setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getCourseId() { return courseId; }
    public void setCourseId(String courseId) { this.courseId = courseId; }

    public String getExamId() { return examId; }
    public void setExamId(String examId) { this.examId = examId; }

    public LocalDateTime getIssueDate() { return issueDate; }
    public void setIssueDate(LocalDateTime issueDate) { this.issueDate = issueDate; }
}
