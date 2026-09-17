package com.taskmanager.dao.impl;

import com.taskmanager.dao.TaskDao;
import com.taskmanager.entity.Priority;
import com.taskmanager.entity.Task;
import com.taskmanager.entity.TaskStatus;
import com.taskmanager.entity.User;
import com.taskmanager.repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class TaskDaoImpl implements TaskDao {

    private final TaskRepository taskRepository;

    @Override
    public Task save(Task task) {
        return taskRepository.save(task);
    }

    @Override
    public Optional<Task> findById(Long id) {
        return taskRepository.findById(id);
    }

    @Override
    public List<Task> findByUser(User user, TaskStatus status, Priority priority, String search) {
        String searchParam = (search != null && !search.isBlank()) ? search.trim() : null;
        return taskRepository.findUserTasksFiltered(user, status, priority, searchParam);
    }

    @Override
    public List<Task> findAll(TaskStatus status, Priority priority, String search) {
        String searchParam = (search != null && !search.isBlank()) ? search.trim() : null;
        return taskRepository.findAllTasksFiltered(status, priority, searchParam);
    }

    @Override
    public void deleteById(Long id) {
        taskRepository.deleteById(id);
    }

    @Override
    public List<Task> findAllByUser(User user) {
        return taskRepository.findAllByUser(user);
    }
}
