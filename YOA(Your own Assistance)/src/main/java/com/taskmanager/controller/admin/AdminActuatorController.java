package com.taskmanager.controller.admin;

import com.taskmanager.entity.User;
import com.taskmanager.service.NotificationService;
import com.taskmanager.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Controller
@RequestMapping("/admin/actuator-dashboard")
@RequiredArgsConstructor
public class AdminActuatorController {

    private final UserService userService;
    private final NotificationService notificationService;

    @Value("${server.port:8080}")
    private String serverPort;

    @GetMapping
    public String actuatorDashboard(@AuthenticationPrincipal UserDetails ud, Model model) {
        User currentUser = userService.getCurrentUser(ud.getUsername());
        String baseUrl = "http://localhost:" + serverPort + "/actuator";

        try {
            RestTemplate rt = new RestTemplate();
            Map<?, ?> health = rt.getForObject(baseUrl + "/health", Map.class);
            Map<?, ?> info = rt.getForObject(baseUrl + "/info", Map.class);
            model.addAttribute("health", health);
            model.addAttribute("info", info);
            model.addAttribute("actuatorBase", baseUrl);
        } catch (Exception e) {
            model.addAttribute("actuatorError", "Could not fetch actuator data: " + e.getMessage());
        }

        model.addAttribute("currentUser", currentUser);
        model.addAttribute("unreadCount", notificationService.getUnreadCount(currentUser));
        return "admin/actuator/dashboard";
    }
}
