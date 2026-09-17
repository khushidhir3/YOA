package com.taskmanager.dao;

import com.taskmanager.entity.Priority;
import com.taskmanager.entity.Task;
import com.taskmanager.entity.TaskStatus;
import com.taskmanager.entity.User;

import java.util.List;
import java.util.Optional;

public interface TaskDao {
    Task save(Task task);
    Optional<Task> findById(Long id);
    List<Task> findByUser(User user, TaskStatus status, Priority priority, String search);
    List<Task> findAll(TaskStatus status, Priority priority, String search);
    void deleteById(Long id);
    List<Task> findAllByUser(User user);
}
