package com.taskmanager.controller.admin;

import com.taskmanager.dto.request.UserUpdateRequest;
import com.taskmanager.dto.response.UserResponse;
import com.taskmanager.entity.User;
import com.taskmanager.service.NotificationService;
import com.taskmanager.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/admin/users")
@RequiredArgsConstructor
public class AdminUserController {

    private final UserService userService;
    private final NotificationService notificationService;

    @GetMapping
    public String listUsers(@AuthenticationPrincipal UserDetails ud, Model model) {
        User currentUser = userService.getCurrentUser(ud.getUsername());
        List<UserResponse> users = userService.getAllUsers();
        model.addAttribute("users", users);
        model.addAttribute("currentUser", currentUser);
        model.addAttribute("unreadCount", notificationService.getUnreadCount(currentUser));
        return "admin/users/list";
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id,
                           @AuthenticationPrincipal UserDetails ud, Model model) {
        User currentUser = userService.getCurrentUser(ud.getUsername());
        UserResponse user = userService.getUserById(id);
        model.addAttribute("userResponse", user);
        model.addAttribute("userUpdateRequest", new UserUpdateRequest());
        model.addAttribute("currentUser", currentUser);
        model.addAttribute("unreadCount", notificationService.getUnreadCount(currentUser));
        return "admin/users/form";
    }

    @PostMapping("/{id}/edit")
    public String updateUser(@PathVariable Long id,
                             @Valid @ModelAttribute UserUpdateRequest request,
                             BindingResult result,
                             @AuthenticationPrincipal UserDetails ud,
                             RedirectAttributes ra, Model model) {
        if (result.hasErrors()) {
            User currentUser = userService.getCurrentUser(ud.getUsername());
            model.addAttribute("userResponse", userService.getUserById(id));
            model.addAttribute("currentUser", currentUser);
            model.addAttribute("unreadCount", notificationService.getUnreadCount(currentUser));
            return "admin/users/form";
        }
        userService.updateUser(id, request);
        ra.addFlashAttribute("success", "User updated successfully.");
        return "redirect:/admin/users";
    }

    @PostMapping("/{id}/disable")
    public String disableUser(@PathVariable Long id, RedirectAttributes ra) {
        userService.disableUser(id);
        ra.addFlashAttribute("success", "User disabled.");
        return "redirect:/admin/users";
    }

    @PostMapping("/{id}/enable")
    public String enableUser(@PathVariable Long id, RedirectAttributes ra) {
        userService.enableUser(id);
        ra.addFlashAttribute("success", "User enabled.");
        return "redirect:/admin/users";
    }

    @PostMapping("/{id}/delete")
    public String deleteUser(@PathVariable Long id, RedirectAttributes ra) {
        userService.deleteUser(id);
        ra.addFlashAttribute("success", "User deleted.");
        return "redirect:/admin/users";
    }
}
