package com.taskmanager.service;

import com.taskmanager.entity.Notification;
import com.taskmanager.entity.User;

import java.util.List;

public interface NotificationService {
    Notification createNotification(User user, String message, String type, Long relatedTaskId);
    List<Notification> getUserNotifications(User user);
    long getUnreadCount(User user);
    void markAllAsRead(User user);
    void markAsRead(Long notificationId, User user);
}
