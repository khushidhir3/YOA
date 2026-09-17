package com.taskmanager.controller.admin;

import com.taskmanager.entity.AuditLog;
import com.taskmanager.entity.User;
import com.taskmanager.service.AuditLogService;
import com.taskmanager.service.NotificationService;
import com.taskmanager.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/admin/audit")
@RequiredArgsConstructor
public class AdminAuditController {

    private final AuditLogService auditLogService;
    private final UserService userService;
    private final NotificationService notificationService;

    @GetMapping
    public String auditLog(@AuthenticationPrincipal UserDetails ud,
                           @RequestParam(defaultValue = "0") int page,
                           @RequestParam(defaultValue = "20") int size,
                           @RequestParam(required = false) String username,
                           Model model) {
        User currentUser = userService.getCurrentUser(ud.getUsername());
        Page<AuditLog> logs = (username != null && !username.isBlank())
                ? auditLogService.getByUser(username, PageRequest.of(page, size))
                : auditLogService.getAll(PageRequest.of(page, size));

        model.addAttribute("logs", logs);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", logs.getTotalPages());
        model.addAttribute("usernameFilter", username);
        model.addAttribute("currentUser", currentUser);
        model.addAttribute("unreadCount", notificationService.getUnreadCount(currentUser));
        return "admin/audit/log";
    }
}
