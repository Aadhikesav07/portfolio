package com.yourcompany.elearningplatform.repository;

import com.yourcompany.elearningplatform.entity.Certificate;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface CertificateRepository extends MongoRepository<Certificate, String> {
    List<Certificate> findByUserId(String userId);
}
