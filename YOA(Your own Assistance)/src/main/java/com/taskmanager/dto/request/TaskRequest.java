package com.taskmanager.dto.request;

import com.taskmanager.entity.Priority;
import com.taskmanager.entity.TaskStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
public class TaskRequest {
    @NotBlank(message = "Title is required")
    @Size(max = 200, message = "Title cannot exceed 200 characters")
    private String title;

    private String description;

    private Priority priority = Priority.MEDIUM;

    private TaskStatus status = TaskStatus.TODO;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate dueDate;

    private Long categoryId;

    private Long assignedToId;

    private Set<String> tags = new HashSet<>();
}
