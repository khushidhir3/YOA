package com.taskmanager.service;

import com.taskmanager.dto.request.TaskRequest;
import com.taskmanager.dto.response.TaskResponse;
import com.taskmanager.entity.Priority;
import com.taskmanager.entity.TaskStatus;

import java.util.List;

public interface TaskService {
    TaskResponse createTask(TaskRequest request, String username);
    TaskResponse updateTask(Long id, TaskRequest request, String username);
    void deleteTask(Long id, String username);
    TaskResponse getTaskById(Long id, String username);
    List<TaskResponse> getUserTasks(String username, TaskStatus status, Priority priority, String search);
    List<TaskResponse> getAllTasks(TaskStatus status, Priority priority, String search);
    TaskResponse updateStatus(Long id, TaskStatus status, String username);
}
