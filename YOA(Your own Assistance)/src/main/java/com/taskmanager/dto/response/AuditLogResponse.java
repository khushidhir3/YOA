package com.taskmanager.dto.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class AuditLogResponse {
    private Long id;
    private String action;
    private String performedBy;
    private String targetEntity;
    private Long targetId;
    private String details;
    private LocalDateTime timestamp;
}
