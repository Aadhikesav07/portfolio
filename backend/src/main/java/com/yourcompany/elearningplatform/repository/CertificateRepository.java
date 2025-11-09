package com.yourcompany.elearningplatform.repository;

import com.yourcompany.elearningplatform.entity.Certificate;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface CertificateRepository extends MongoRepository<Certificate, String> {
    List<Certificate> findByUserId(String userId);
    Optional<Certificate> findByUserIdAndCourseIdAndExamId(String userId, String courseId, String examId);
    Optional<Certificate> findByOid(String oid);
}
