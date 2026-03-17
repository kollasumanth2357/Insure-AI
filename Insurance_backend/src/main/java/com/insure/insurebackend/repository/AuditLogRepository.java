package com.insure.insurebackend.repository;

import com.insure.insurebackend.model.AuditLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {
    long countByAction(String action);
}
