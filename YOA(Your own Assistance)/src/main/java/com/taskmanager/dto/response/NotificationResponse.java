package com.taskmanager.dto.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class NotificationResponse {
    private Long id;
    private String message;
    private boolean read;
    private String type;
    private Long relatedTaskId;
    private LocalDateTime createdAt;
}
