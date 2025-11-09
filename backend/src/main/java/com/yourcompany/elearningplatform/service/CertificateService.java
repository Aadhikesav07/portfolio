package com.yourcompany.elearningplatform.service;

import com.yourcompany.elearningplatform.entity.Certificate;
import com.yourcompany.elearningplatform.entity.ExamResponse;
import com.yourcompany.elearningplatform.entity.User;
import com.yourcompany.elearningplatform.entity.Course;
import com.yourcompany.elearningplatform.repository.CertificateRepository;
import com.yourcompany.elearningplatform.repository.ExamResponseRepository;
import com.yourcompany.elearningplatform.repository.UserRepository;
import com.yourcompany.elearningplatform.repository.CourseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class CertificateService {

    @Autowired
    private CertificateRepository certificateRepository;

    @Autowired
    private ExamResponseRepository examResponseRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private EmailService emailService;

    @Autowired
    private PdfService pdfService;

    public Certificate issueCertificate(String userId, String courseId, String examId) {
        // Check if certificate already exists
        Optional<Certificate> existing = certificateRepository.findByUserIdAndCourseIdAndExamId(userId, courseId, examId);
        if (existing.isPresent()) {
            return existing.get();
        }

        // Get exam response
        ExamResponse examResponse = examResponseRepository.findByExamIdAndStudentId(examId, userId)
            .orElseThrow(() -> new RuntimeException("Exam not completed"));

        if (!examResponse.isSubmitted()) {
            throw new RuntimeException("Exam not yet submitted");
        }

        // Get user and course details (userId is email in JWT)
        User user = userRepository.findByEmail(userId)
            .orElseGet(() -> userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found")));
        Course course = courseRepository.findById(courseId)
            .orElseThrow(() -> new RuntimeException("Course not found"));

        // Generate unique OID
        String oid = generateOID();

        // Create certificate
        Certificate certificate = new Certificate();
        certificate.setOid(oid);
        certificate.setUserId(userId);
        certificate.setStudentName(user.getFullName());
        certificate.setStudentEmail(user.getEmail());
        certificate.setCourseId(courseId);
        certificate.setCourseName(course.getTitle());
        certificate.setExamId(examId);
        certificate.setExamMarks(examResponse.getMarksObtained());
        certificate.setIssueDate(LocalDateTime.now());

        Certificate saved = certificateRepository.save(certificate);

        // Generate PDF
        try {
            String pdfPath = pdfService.generateCertificatePdf(saved);
            saved.setPdfPath(pdfPath);
            saved = certificateRepository.save(saved);

            // Send email with PDF
            emailService.sendCertificateEmail(saved);
            saved.setEmailSent(true);
            saved = certificateRepository.save(saved);
        } catch (Exception e) {
            // Log error but don't fail certificate creation
            System.err.println("Error generating PDF or sending email: " + e.getMessage());
        }

        return saved;
    }

    private String generateOID() {
        // Format: CERT-{timestamp}-{random}
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        String random = UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        return "CERT-" + timestamp + "-" + random;
    }

    public List<Certificate> getCertificatesForUser(String userId) {
        return certificateRepository.findByUserId(userId);
    }

    public Certificate getCertificateById(String id) {
        Optional<Certificate> certificate = certificateRepository.findById(id);
        return certificate.orElse(null);
    }

    public Certificate getCertificateByOID(String oid) {
        Optional<Certificate> certificate = certificateRepository.findByOid(oid);
        return certificate.orElse(null);
    }
}
