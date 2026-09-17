package com.taskmanager.controller;

import com.taskmanager.dto.response.DashboardStats;
import com.taskmanager.entity.User;
import com.taskmanager.service.DashboardService;
import com.taskmanager.service.NotificationService;
import com.taskmanager.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;
    private final UserService userService;
    private final NotificationService notificationService;

    @GetMapping
    public String dashboard(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        User currentUser = userService.getCurrentUser(userDetails.getUsername());
        boolean isAdmin = userDetails.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        DashboardStats stats = isAdmin
                ? dashboardService.getAdminStats()
                : dashboardService.getUserStats(currentUser);

        model.addAttribute("stats", stats);
        model.addAttribute("currentUser", currentUser);
        model.addAttribute("isAdmin", isAdmin);
        model.addAttribute("unreadCount", notificationService.getUnreadCount(currentUser));
        return "dashboard/index";
    }
}
