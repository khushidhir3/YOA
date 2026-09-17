package com.taskmanager.service;

import com.taskmanager.entity.AuditLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface AuditLogService {
    AuditLog log(String action, String performedBy, String targetEntity, Long targetId, String details);
    Page<AuditLog> getAll(Pageable pageable);
    Page<AuditLog> getByUser(String username, Pageable pageable);
}
