package com.taskmanager.controller.admin;

import com.taskmanager.dto.request.TaskRequest;
import com.taskmanager.dto.response.TaskResponse;
import com.taskmanager.entity.Priority;
import com.taskmanager.entity.TaskStatus;
import com.taskmanager.entity.User;
import com.taskmanager.service.*;
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
@RequestMapping("/admin/tasks")
@RequiredArgsConstructor
public class AdminTaskController {

    private final TaskService taskService;
    private final UserService userService;
    private final CategoryService categoryService;
    private final NotificationService notificationService;

    @GetMapping
    public String listAll(@AuthenticationPrincipal UserDetails ud,
                          @RequestParam(required = false) TaskStatus status,
                          @RequestParam(required = false) Priority priority,
                          @RequestParam(required = false) String search,
                          Model model) {
        User currentUser = userService.getCurrentUser(ud.getUsername());
        List<TaskResponse> tasks = taskService.getAllTasks(status, priority, search);
        model.addAttribute("tasks", tasks);
        model.addAttribute("statusFilter", status);
        model.addAttribute("priorityFilter", priority);
        model.addAttribute("searchFilter", search);
        model.addAttribute("statuses", TaskStatus.values());
        model.addAttribute("priorities", Priority.values());
        model.addAttribute("currentUser", currentUser);
        model.addAttribute("unreadCount", notificationService.getUnreadCount(currentUser));
        return "admin/tasks/list";
    }

    @GetMapping("/new")
    public String newForm(@AuthenticationPrincipal UserDetails ud, Model model) {
        User currentUser = userService.getCurrentUser(ud.getUsername());
        model.addAttribute("taskRequest", new TaskRequest());
        model.addAttribute("categories", categoryService.getCategoriesForUser(ud.getUsername()));
        model.addAttribute("users", userService.getAllUsers());
        model.addAttribute("priorities", Priority.values());
        model.addAttribute("statuses", TaskStatus.values());
        model.addAttribute("currentUser", currentUser);
        model.addAttribute("unreadCount", notificationService.getUnreadCount(currentUser));
        return "admin/tasks/form";
    }

    @PostMapping("/new")
    public String create(@AuthenticationPrincipal UserDetails ud,
                         @Valid @ModelAttribute TaskRequest req,
                         BindingResult result,
                         RedirectAttributes ra, Model model) {
        if (result.hasErrors()) {
            User currentUser = userService.getCurrentUser(ud.getUsername());
            model.addAttribute("categories", categoryService.getCategoriesForUser(ud.getUsername()));
            model.addAttribute("users", userService.getAllUsers());
            model.addAttribute("priorities", Priority.values());
            model.addAttribute("statuses", TaskStatus.values());
            model.addAttribute("currentUser", currentUser);
            model.addAttribute("unreadCount", notificationService.getUnreadCount(currentUser));
            return "admin/tasks/form";
        }
        taskService.createTask(req, ud.getUsername());
        ra.addFlashAttribute("success", "Task created.");
        return "redirect:/admin/tasks";
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, @AuthenticationPrincipal UserDetails ud, Model model) {
        User currentUser = userService.getCurrentUser(ud.getUsername());
        TaskResponse task = taskService.getTaskById(id, ud.getUsername());
        TaskRequest req = new TaskRequest();
        req.setTitle(task.getTitle());
        req.setDescription(task.getDescription());
        req.setPriority(task.getPriority());
        req.setStatus(task.getStatus());
        req.setDueDate(task.getDueDate());
        req.setTags(task.getTags());
        model.addAttribute("taskRequest", req);
        model.addAttribute("taskId", id);
        model.addAttribute("categories", categoryService.getCategoriesForUser(ud.getUsername()));
        model.addAttribute("users", userService.getAllUsers());
        model.addAttribute("priorities", Priority.values());
        model.addAttribute("statuses", TaskStatus.values());
        model.addAttribute("currentUser", currentUser);
        model.addAttribute("unreadCount", notificationService.getUnreadCount(currentUser));
        model.addAttribute("editMode", true);
        return "admin/tasks/form";
    }

    @PostMapping("/{id}/edit")
    public String update(@PathVariable Long id, @AuthenticationPrincipal UserDetails ud,
                         @Valid @ModelAttribute TaskRequest req, BindingResult result,
                         RedirectAttributes ra, Model model) {
        if (result.hasErrors()) {
            User currentUser = userService.getCurrentUser(ud.getUsername());
            model.addAttribute("taskId", id);
            model.addAttribute("categories", categoryService.getCategoriesForUser(ud.getUsername()));
            model.addAttribute("users", userService.getAllUsers());
            model.addAttribute("priorities", Priority.values());
            model.addAttribute("statuses", TaskStatus.values());
            model.addAttribute("currentUser", currentUser);
            model.addAttribute("unreadCount", notificationService.getUnreadCount(currentUser));
            model.addAttribute("editMode", true);
            return "admin/tasks/form";
        }
        taskService.updateTask(id, req, ud.getUsername());
        ra.addFlashAttribute("success", "Task updated.");
        return "redirect:/admin/tasks";
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id, @AuthenticationPrincipal UserDetails ud, RedirectAttributes ra) {
        taskService.deleteTask(id, ud.getUsername());
        ra.addFlashAttribute("success", "Task deleted.");
        return "redirect:/admin/tasks";
    }
}
