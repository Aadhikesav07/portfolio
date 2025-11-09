package com.yourcompany.elearningplatform.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "certificates")
public class Certificate {

    @Id
    private String id;

    private String oid; // Unique Object Identifier for certificate
    private String userId;
    private String studentName;
    private String studentEmail;
    private String courseId;
    private String courseName;
    private String examId;
    private int examMarks;
    private LocalDateTime issueDate;
    private boolean emailSent;
    private String pdfPath; // Path to generated PDF

    public Certificate() {
        this.issueDate = LocalDateTime.now();
        this.emailSent = false;
    }

    public Certificate(String userId, String courseId, String examId) {
        this.userId = userId;
        this.courseId = courseId;
        this.examId = examId;
        this.issueDate = LocalDateTime.now();
        this.emailSent = false;
    }

    // Getters and setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getOid() { return oid; }
    public void setOid(String oid) { this.oid = oid; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getStudentName() { return studentName; }
    public void setStudentName(String studentName) { this.studentName = studentName; }

    public String getStudentEmail() { return studentEmail; }
    public void setStudentEmail(String studentEmail) { this.studentEmail = studentEmail; }

    public String getCourseId() { return courseId; }
    public void setCourseId(String courseId) { this.courseId = courseId; }

    public String getCourseName() { return courseName; }
    public void setCourseName(String courseName) { this.courseName = courseName; }

    public String getExamId() { return examId; }
    public void setExamId(String examId) { this.examId = examId; }

    public int getExamMarks() { return examMarks; }
    public void setExamMarks(int examMarks) { this.examMarks = examMarks; }

    public LocalDateTime getIssueDate() { return issueDate; }
    public void setIssueDate(LocalDateTime issueDate) { this.issueDate = issueDate; }

    public boolean isEmailSent() { return emailSent; }
    public void setEmailSent(boolean emailSent) { this.emailSent = emailSent; }

    public String getPdfPath() { return pdfPath; }
    public void setPdfPath(String pdfPath) { this.pdfPath = pdfPath; }
}
