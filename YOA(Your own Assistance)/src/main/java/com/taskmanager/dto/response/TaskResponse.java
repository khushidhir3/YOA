package com.taskmanager.dto.response;

import com.taskmanager.entity.Priority;
import com.taskmanager.entity.TaskStatus;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Data
@Builder
public class TaskResponse {
    private Long id;
    private String title;
    private String description;
    private Priority priority;
    private TaskStatus status;
    private LocalDate dueDate;
    private boolean overdue;
    private String categoryName;
    private String categoryColor;
    private String assignedToUsername;
    private String assignedToFullName;
    private String createdByUsername;
    private Set<String> tags;
    private List<CommentResponse> comments;
    private List<AttachmentResponse> attachments;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
