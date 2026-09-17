package com.taskmanager.service.impl;

import com.taskmanager.dao.TaskDao;
import com.taskmanager.dao.UserDao;
import com.taskmanager.dto.request.TaskRequest;
import com.taskmanager.dto.response.AttachmentResponse;
import com.taskmanager.dto.response.CommentResponse;
import com.taskmanager.dto.response.TaskResponse;
import com.taskmanager.entity.*;
import com.taskmanager.exception.ResourceNotFoundException;
import com.taskmanager.exception.UnauthorizedException;
import com.taskmanager.repository.CategoryRepository;
import com.taskmanager.service.NotificationService;
import com.taskmanager.service.TaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TaskServiceImpl implements TaskService {

    private final TaskDao taskDao;
    private final UserDao userDao;
    private final CategoryRepository categoryRepository;
    private final NotificationService notificationService;

    @Override
    @Transactional
    public TaskResponse createTask(TaskRequest request, String username) {
        User creator = getUser(username);

        Task task = Task.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .priority(request.getPriority() != null ? request.getPriority() : Priority.MEDIUM)
                .status(request.getStatus() != null ? request.getStatus() : TaskStatus.TODO)
                .dueDate(request.getDueDate())
                .createdBy(creator)
                .tags(request.getTags())
                .build();

        if (request.getCategoryId() != null) {
            categoryRepository.findById(request.getCategoryId())
                    .ifPresent(task::setCategory);
        }

        if (request.getAssignedToId() != null) {
            userDao.findById(request.getAssignedToId()).ifPresent(assignee -> {
                task.setAssignedTo(assignee);
                // Notify assignee
                notificationService.createNotification(
                    assignee,
                    "You have been assigned a new task: " + task.getTitle(),
                    "TASK_ASSIGNED",
                    null // task id set after save
                );
            });
        }

        Task saved = taskDao.save(task);
        return mapToResponse(saved);
    }

    @Override
    @Transactional
    public TaskResponse updateTask(Long id, TaskRequest request, String username) {
        Task task = getTaskWithAccess(id, username);

        task.setTitle(request.getTitle());
        task.setDescription(request.getDescription());
        task.setPriority(request.getPriority());
        task.setStatus(request.getStatus());
        task.setDueDate(request.getDueDate());
        task.setTags(request.getTags());

        if (request.getCategoryId() != null) {
            categoryRepository.findById(request.getCategoryId())
                    .ifPresent(task::setCategory);
        } else {
            task.setCategory(null);
        }

        if (request.getAssignedToId() != null) {
            userDao.findById(request.getAssignedToId()).ifPresent(assignee -> {
                boolean changed = task.getAssignedTo() == null ||
                        !task.getAssignedTo().getId().equals(assignee.getId());
                task.setAssignedTo(assignee);
                if (changed) {
                    notificationService.createNotification(
                        assignee,
                        "You have been assigned task: " + task.getTitle(),
                        "TASK_ASSIGNED",
                        task.getId()
                    );
                }
            });
        } else {
            task.setAssignedTo(null);
        }

        return mapToResponse(taskDao.save(task));
    }

    @Override
    @Transactional
    public void deleteTask(Long id, String username) {
        Task task = getTaskWithAccess(id, username);
        taskDao.deleteById(task.getId());
    }

    @Override
    @Transactional(readOnly = true)
    public TaskResponse getTaskById(Long id, String username) {
        Task task = getTaskWithAccess(id, username);
        return mapToResponse(task);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TaskResponse> getUserTasks(String username, TaskStatus status, Priority priority, String search) {
        User user = getUser(username);
        return taskDao.findByUser(user, status, priority, search)
                .stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<TaskResponse> getAllTasks(TaskStatus status, Priority priority, String search) {
        return taskDao.findAll(status, priority, search)
                .stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public TaskResponse updateStatus(Long id, TaskStatus status, String username) {
        Task task = getTaskWithAccess(id, username);
        task.setStatus(status);
        return mapToResponse(taskDao.save(task));
    }

    // ---- Helpers ----

    private User getUser(String username) {
        return userDao.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));
    }

    private Task getTaskWithAccess(Long id, String username) {
        Task task = taskDao.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task", "id", id));

        User user = getUser(username);
        boolean isAdmin = user.getRoles().stream()
                .anyMatch(r -> r.getName().equals("ROLE_ADMIN"));
        boolean isOwner = task.getCreatedBy().getUsername().equals(username) ||
                (task.getAssignedTo() != null && task.getAssignedTo().getUsername().equals(username));

        if (!isAdmin && !isOwner) {
            throw new UnauthorizedException("You don't have access to this task");
        }
        return task;
    }

    public TaskResponse mapToResponse(Task task) {
        List<CommentResponse> comments = task.getComments().stream()
                .map(c -> CommentResponse.builder()
                        .id(c.getId())
                        .content(c.getContent())
                        .authorUsername(c.getAuthor().getUsername())
                        .authorFullName(c.getAuthor().getFullName())
                        .createdAt(c.getCreatedAt())
                        .build())
                .collect(Collectors.toList());

        List<AttachmentResponse> attachments = task.getAttachments().stream()
                .map(a -> AttachmentResponse.builder()
                        .id(a.getId())
                        .fileName(a.getFileName())
                        .fileType(a.getFileType())
                        .fileSize(a.getFileSize())
                        .uploadedByUsername(a.getUploadedBy() != null ? a.getUploadedBy().getUsername() : null)
                        .uploadedAt(a.getUploadedAt())
                        .build())
                .collect(Collectors.toList());

        boolean overdue = task.getDueDate() != null &&
                task.getDueDate().isBefore(LocalDate.now()) &&
                task.getStatus() != TaskStatus.DONE;

        return TaskResponse.builder()
                .id(task.getId())
                .title(task.getTitle())
                .description(task.getDescription())
                .priority(task.getPriority())
                .status(task.getStatus())
                .dueDate(task.getDueDate())
                .overdue(overdue)
                .categoryName(task.getCategory() != null ? task.getCategory().getName() : null)
                .categoryColor(task.getCategory() != null ? task.getCategory().getColor() : null)
                .assignedToUsername(task.getAssignedTo() != null ? task.getAssignedTo().getUsername() : null)
                .assignedToFullName(task.getAssignedTo() != null ? task.getAssignedTo().getFullName() : null)
                .createdByUsername(task.getCreatedBy().getUsername())
                .tags(task.getTags())
                .comments(comments)
                .attachments(attachments)
                .createdAt(task.getCreatedAt())
                .updatedAt(task.getUpdatedAt())
                .build();
    }
}
