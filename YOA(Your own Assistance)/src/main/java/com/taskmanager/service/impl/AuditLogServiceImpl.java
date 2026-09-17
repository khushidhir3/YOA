package com.taskmanager.service.impl;

import com.taskmanager.entity.AuditLog;
import com.taskmanager.repository.AuditLogRepository;
import com.taskmanager.service.AuditLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AuditLogServiceImpl implements AuditLogService {

    private final AuditLogRepository auditLogRepository;

    @Override
    @Transactional
    public AuditLog log(String action, String performedBy, String targetEntity, Long targetId, String details) {
        AuditLog entry = AuditLog.builder()
                .action(action)
                .performedBy(performedBy)
                .targetEntity(targetEntity)
                .targetId(targetId)
                .details(details)
                .timestamp(LocalDateTime.now())
                .build();
        return auditLogRepository.save(entry);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<AuditLog> getAll(Pageable pageable) {
        return auditLogRepository.findAllByOrderByTimestampDesc(pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<AuditLog> getByUser(String username, Pageable pageable) {
        return auditLogRepository.findByPerformedByOrderByTimestampDesc(username, pageable);
    }
}
