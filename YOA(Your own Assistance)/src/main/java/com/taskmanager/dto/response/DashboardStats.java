package com.taskmanager.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DashboardStats {
    // Task counts
    private long totalTasks;
    private long todoTasks;
    private long inProgressTasks;
    private long doneTasks;
    private long overdueTasks;

    // Priority breakdown
    private long lowPriorityTasks;
    private long mediumPriorityTasks;
    private long highPriorityTasks;
    private long criticalPriorityTasks;

    // Admin only
    private long totalUsers;
    private long activeUsers;
}
