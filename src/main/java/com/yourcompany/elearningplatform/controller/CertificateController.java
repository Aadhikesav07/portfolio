package com.yourcompany.elearningplatform.controller;

import com.yourcompany.elearningplatform.entity.Certificate;
import com.yourcompany.elearningplatform.service.CertificateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/certificates")
public class CertificateController {

    @Autowired
    private CertificateService certificateService;

    // Endpoint to request certificate issuance after exam completion
    @PostMapping("/issue")
    public ResponseEntity<Certificate> issueCertificate(Authentication authentication,
                                                       @RequestParam String courseId,
                                                       @RequestParam String examId) {
        // User email as username principal
        String userId = authentication.getName();
        Certificate cert = certificateService.issueCertificate(userId, courseId, examId);
        return ResponseEntity.ok(cert);
    }

    // Get all certificates for the authenticated user
    @GetMapping
    public ResponseEntity<List<Certificate>> getUserCertificates(Authentication authentication) {
        String userId = authentication.getName();
        List<Certificate> certificates = certificateService.getCertificatesForUser(userId);
        return ResponseEntity.ok(certificates);
    }
}
