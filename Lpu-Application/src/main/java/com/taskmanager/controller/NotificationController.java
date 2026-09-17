package com.taskmanager.controller;

import com.taskmanager.entity.Notification;
import com.taskmanager.entity.User;
import com.taskmanager.service.NotificationService;
import com.taskmanager.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;
    private final UserService userService;

    @GetMapping
    public String listNotifications(@AuthenticationPrincipal UserDetails ud, Model model) {
        User currentUser = userService.getCurrentUser(ud.getUsername());
        List<Notification> notifications = notificationService.getUserNotifications(currentUser);
        model.addAttribute("notifications", notifications);
        model.addAttribute("currentUser", currentUser);
        model.addAttribute("unreadCount", notificationService.getUnreadCount(currentUser));
        return "notifications/index";
    }

    @PostMapping("/mark-all-read")
    public String markAllRead(@AuthenticationPrincipal UserDetails ud, RedirectAttributes ra) {
        User currentUser = userService.getCurrentUser(ud.getUsername());
        notificationService.markAllAsRead(currentUser);
        ra.addFlashAttribute("success", "All notifications marked as read.");
        return "redirect:/notifications";
    }

    @PostMapping("/{id}/read")
    public String markRead(@PathVariable Long id,
                           @AuthenticationPrincipal UserDetails ud,
                           RedirectAttributes ra) {
        User currentUser = userService.getCurrentUser(ud.getUsername());
        notificationService.markAsRead(id, currentUser);
        return "redirect:/notifications";
    }

    // AJAX endpoint for live unread count polling
    @GetMapping("/unread-count")
    @ResponseBody
    public ResponseEntity<Map<String, Long>> getUnreadCount(@AuthenticationPrincipal UserDetails ud) {
        User currentUser = userService.getCurrentUser(ud.getUsername());
        long count = notificationService.getUnreadCount(currentUser);
        return ResponseEntity.ok(Map.of("count", count));
    }
}
