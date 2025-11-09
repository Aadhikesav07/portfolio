package com.yourcompany.elearningplatform.controller;

import com.yourcompany.elearningplatform.entity.Certificate;
import com.yourcompany.elearningplatform.service.CertificateService;
import com.yourcompany.elearningplatform.service.PdfService;
import com.yourcompany.elearningplatform.util.RoleUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.util.List;

@RestController
@RequestMapping("/api/certificates")
public class CertificateController {

    @Autowired
    private CertificateService certificateService;

    @Autowired
    private PdfService pdfService;

    // Issue certificate - Only ADMIN can issue (automatic after exam completion)
    @PostMapping("/issue")
    public ResponseEntity<?> issueCertificate(Authentication authentication,
                                               @RequestParam String courseId,
                                               @RequestParam String examId) {
        if (!RoleUtil.isAdmin(authentication)) {
            // Students can request, but it's typically automatic
            String userId = RoleUtil.getUserId(authentication);
            try {
                Certificate cert = certificateService.issueCertificate(userId, courseId, examId);
                return ResponseEntity.ok(cert);
            } catch (RuntimeException e) {
                return ResponseEntity.badRequest().body(e.getMessage());
            }
        } else {
            // Admin can issue for any user
            String userId = RoleUtil.getUserId(authentication);
            try {
                Certificate cert = certificateService.issueCertificate(userId, courseId, examId);
                return ResponseEntity.ok(cert);
            } catch (RuntimeException e) {
                return ResponseEntity.badRequest().body(e.getMessage());
            }
        }
    }

    // Get all certificates - Users can only see their own
    @GetMapping
    public ResponseEntity<List<Certificate>> getUserCertificates(Authentication authentication) {
        String userId = RoleUtil.getUserId(authentication);
        List<Certificate> certificates = certificateService.getCertificatesForUser(userId);
        return ResponseEntity.ok(certificates);
    }

    // Get certificate by ID - Users can only see their own
    @GetMapping("/{id}")
    public ResponseEntity<Certificate> getCertificate(@PathVariable String id, Authentication authentication) {
        Certificate certificate = certificateService.getCertificateById(id);
        if (certificate == null) {
            return ResponseEntity.notFound().build();
        }

        // Students can only view their own certificates
        if (RoleUtil.isStudent(authentication) && 
            !certificate.getUserId().equals(RoleUtil.getUserId(authentication))) {
            return ResponseEntity.status(403).build();
        }

        return ResponseEntity.ok(certificate);
    }

    // Download PDF - Users can only download their own
    @GetMapping("/{id}/download")
    public ResponseEntity<Resource> downloadCertificate(@PathVariable String id, Authentication authentication) {
        Certificate certificate = certificateService.getCertificateById(id);
        if (certificate == null) {
            return ResponseEntity.notFound().build();
        }

        // Students can only download their own certificates
        if (RoleUtil.isStudent(authentication) && 
            !certificate.getUserId().equals(RoleUtil.getUserId(authentication))) {
            return ResponseEntity.status(403).build();
        }

        if (certificate.getPdfPath() == null) {
            return ResponseEntity.notFound().build();
        }

        File file = pdfService.getCertificatePdf(certificate.getPdfPath());
        if (file == null) {
            return ResponseEntity.notFound().build();
        }

        Resource resource = new FileSystemResource(file);
        return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"certificate_" + certificate.getOid() + ".pdf\"")
            .contentType(MediaType.APPLICATION_PDF)
            .body(resource);
    }

    // Get certificate by OID (public verification)
    @GetMapping("/verify/{oid}")
    public ResponseEntity<Certificate> verifyCertificate(@PathVariable String oid) {
        Certificate certificate = certificateService.getCertificateByOID(oid);
        if (certificate == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(certificate);
    }
}
