package com.yourcompany.elearningplatform.service;

import com.yourcompany.elearningplatform.entity.Certificate;
import com.yourcompany.elearningplatform.repository.CertificateRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CertificateService {

    @Autowired
    private CertificateRepository certificateRepository;

    public Certificate issueCertificate(String userId, String courseId, String examId) {
        Certificate certificate = new Certificate(userId, courseId, examId);
        return certificateRepository.save(certificate);
    }

    public List<Certificate> getCertificatesForUser(String userId) {
        return certificateRepository.findByUserId(userId);
    }
}
