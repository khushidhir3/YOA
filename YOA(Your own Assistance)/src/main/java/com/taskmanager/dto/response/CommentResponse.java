package com.taskmanager.dto.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class CommentResponse {
    private Long id;
    private String content;
    private String authorUsername;
    private String authorFullName;
    private LocalDateTime createdAt;
}
