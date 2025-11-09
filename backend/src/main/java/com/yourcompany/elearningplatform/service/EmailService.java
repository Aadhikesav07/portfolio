package com.yourcompany.elearningplatform.service;

import com.yourcompany.elearningplatform.entity.Certificate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import java.io.File;

@Service
public class EmailService {

    @Autowired(required = false)
    private JavaMailSender mailSender;

    public void sendCertificateEmail(Certificate certificate) {
        if (mailSender == null) {
            System.out.println("Email service not configured. Certificate OID: " + certificate.getOid());
            return;
        }

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setTo(certificate.getStudentEmail());
            helper.setSubject("Certificate of Completion - " + certificate.getCourseName());
            helper.setText(buildEmailBody(certificate), true);

            // Attach PDF if available
            if (certificate.getPdfPath() != null && new File(certificate.getPdfPath()).exists()) {
                helper.addAttachment("Certificate.pdf", new File(certificate.getPdfPath()));
            }

            mailSender.send(message);
        } catch (MessagingException e) {
            System.err.println("Error sending email: " + e.getMessage());
            throw new RuntimeException("Failed to send certificate email", e);
        }
    }

    private String buildEmailBody(Certificate certificate) {
        return "<html><body>" +
            "<h2>Congratulations!</h2>" +
            "<p>Dear " + certificate.getStudentName() + ",</p>" +
            "<p>You have successfully completed the course <strong>" + certificate.getCourseName() + "</strong>.</p>" +
            "<p>Your Certificate OID: <strong>" + certificate.getOid() + "</strong></p>" +
            "<p>Exam Score: " + certificate.getExamMarks() + " marks</p>" +
            "<p>Please find your certificate attached to this email.</p>" +
            "<p>Best regards,<br>E-Learning Platform</p>" +
            "</body></html>";
    }
}

