package com.taskmanager.service.impl;

import com.taskmanager.dto.response.DashboardStats;
import com.taskmanager.entity.Priority;
import com.taskmanager.entity.TaskStatus;
import com.taskmanager.entity.User;
import com.taskmanager.repository.TaskRepository;
import com.taskmanager.repository.UserRepository;
import com.taskmanager.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class DashboardServiceImpl implements DashboardService {

    private final TaskRepository taskRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public DashboardStats getUserStats(User user) {
        long total = taskRepository.countByAssignedTo(user) + taskRepository.findByCreatedBy(user).size();
        long done = taskRepository.countByAssignedToAndStatus(user, TaskStatus.DONE);
        long inProgress = taskRepository.countByAssignedToAndStatus(user, TaskStatus.IN_PROGRESS);
        long todo = taskRepository.countByAssignedToAndStatus(user, TaskStatus.TODO);
        long overdue = taskRepository.findByDueDateBeforeAndStatusNot(LocalDate.now(), TaskStatus.DONE)
                .stream().filter(t -> t.getAssignedTo() != null && t.getAssignedTo().getId().equals(user.getId()))
                .count();

        // Simplified: for user dashboard, use tasks they can see
        var userTasks = taskRepository.findAllByUser(user);
        long totalU = userTasks.size();
        long todoU = userTasks.stream().filter(t -> t.getStatus() == TaskStatus.TODO).count();
        long ipU = userTasks.stream().filter(t -> t.getStatus() == TaskStatus.IN_PROGRESS).count();
        long doneU = userTasks.stream().filter(t -> t.getStatus() == TaskStatus.DONE).count();
        long overdueU = userTasks.stream().filter(t ->
                t.getDueDate() != null && t.getDueDate().isBefore(LocalDate.now())
                && t.getStatus() != TaskStatus.DONE).count();
        long lowU = userTasks.stream().filter(t -> t.getPriority() == Priority.LOW).count();
        long medU = userTasks.stream().filter(t -> t.getPriority() == Priority.MEDIUM).count();
        long highU = userTasks.stream().filter(t -> t.getPriority() == Priority.HIGH).count();
        long critU = userTasks.stream().filter(t -> t.getPriority() == Priority.CRITICAL).count();

        return DashboardStats.builder()
                .totalTasks(totalU)
                .todoTasks(todoU)
                .inProgressTasks(ipU)
                .doneTasks(doneU)
                .overdueTasks(overdueU)
                .lowPriorityTasks(lowU)
                .mediumPriorityTasks(medU)
                .highPriorityTasks(highU)
                .criticalPriorityTasks(critU)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public DashboardStats getAdminStats() {
        long total = taskRepository.count();
        long done = taskRepository.countByStatus(TaskStatus.DONE);
        long inProgress = taskRepository.countByStatus(TaskStatus.IN_PROGRESS);
        long todo = taskRepository.countByStatus(TaskStatus.TODO);
        long overdue = taskRepository.findByDueDateBeforeAndStatusNot(LocalDate.now(), TaskStatus.DONE).size();
        long low = taskRepository.countByPriority(Priority.LOW);
        long medium = taskRepository.countByPriority(Priority.MEDIUM);
        long high = taskRepository.countByPriority(Priority.HIGH);
        long critical = taskRepository.countByPriority(Priority.CRITICAL);
        long totalUsers = userRepository.count();
        long activeUsers = userRepository.findAll().stream().filter(User::isEnabled).count();

        return DashboardStats.builder()
                .totalTasks(total)
                .todoTasks(todo)
                .inProgressTasks(inProgress)
                .doneTasks(done)
                .overdueTasks(overdue)
                .lowPriorityTasks(low)
                .mediumPriorityTasks(medium)
                .highPriorityTasks(high)
                .criticalPriorityTasks(critical)
                .totalUsers(totalUsers)
                .activeUsers(activeUsers)
                .build();
    }
}
