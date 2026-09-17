package com.taskmanager.service;

import com.taskmanager.dto.response.DashboardStats;
import com.taskmanager.entity.User;

public interface DashboardService {
    DashboardStats getUserStats(User user);
    DashboardStats getAdminStats();
}
