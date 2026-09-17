package com.taskmanager.controller;

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
@RequestMapping("/tasks")
@RequiredArgsConstructor
public class TaskController {

    private final TaskService taskService;
    private final CategoryService categoryService;
    private final UserService userService;
    private final NotificationService notificationService;

    @GetMapping
    public String listTasks(@AuthenticationPrincipal UserDetails ud,
                            @RequestParam(required = false) TaskStatus status,
                            @RequestParam(required = false) Priority priority,
                            @RequestParam(required = false) String search,
                            Model model) {
        User currentUser = userService.getCurrentUser(ud.getUsername());
        List<TaskResponse> tasks = taskService.getUserTasks(ud.getUsername(), status, priority, search);
        model.addAttribute("tasks", tasks);
        model.addAttribute("statusFilter", status);
        model.addAttribute("priorityFilter", priority);
        model.addAttribute("searchFilter", search);
        model.addAttribute("statuses", TaskStatus.values());
        model.addAttribute("priorities", Priority.values());
        model.addAttribute("currentUser", currentUser);
        model.addAttribute("unreadCount", notificationService.getUnreadCount(currentUser));
        return "tasks/list";
    }

    @GetMapping("/new")
    public String newTaskForm(@AuthenticationPrincipal UserDetails ud, Model model) {
        User currentUser = userService.getCurrentUser(ud.getUsername());
        model.addAttribute("taskRequest", new TaskRequest());
        model.addAttribute("categories", categoryService.getCategoriesForUser(ud.getUsername()));
        model.addAttribute("priorities", Priority.values());
        model.addAttribute("statuses", TaskStatus.values());
        model.addAttribute("currentUser", currentUser);
        model.addAttribute("unreadCount", notificationService.getUnreadCount(currentUser));
        return "tasks/form";
    }

    @PostMapping("/new")
    public String createTask(@AuthenticationPrincipal UserDetails ud,
                             @Valid @ModelAttribute TaskRequest taskRequest,
                             BindingResult bindingResult,
                             RedirectAttributes redirectAttributes,
                             Model model) {
        if (bindingResult.hasErrors()) {
            User currentUser = userService.getCurrentUser(ud.getUsername());
            model.addAttribute("categories", categoryService.getCategoriesForUser(ud.getUsername()));
            model.addAttribute("priorities", Priority.values());
            model.addAttribute("statuses", TaskStatus.values());
            model.addAttribute("currentUser", currentUser);
            model.addAttribute("unreadCount", notificationService.getUnreadCount(currentUser));
            return "tasks/form";
        }
        taskService.createTask(taskRequest, ud.getUsername());
        redirectAttributes.addFlashAttribute("success", "Task created successfully!");
        return "redirect:/tasks";
    }

    @GetMapping("/{id}")
    public String viewTask(@PathVariable Long id,
                           @AuthenticationPrincipal UserDetails ud,
                           Model model) {
        User currentUser = userService.getCurrentUser(ud.getUsername());
        TaskResponse task = taskService.getTaskById(id, ud.getUsername());
        model.addAttribute("task", task);
        model.addAttribute("currentUser", currentUser);
        model.addAttribute("unreadCount", notificationService.getUnreadCount(currentUser));
        return "tasks/detail";
    }

    @GetMapping("/{id}/edit")
    public String editTaskForm(@PathVariable Long id,
                               @AuthenticationPrincipal UserDetails ud,
                               Model model) {
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
        model.addAttribute("priorities", Priority.values());
        model.addAttribute("statuses", TaskStatus.values());
        model.addAttribute("currentUser", currentUser);
        model.addAttribute("unreadCount", notificationService.getUnreadCount(currentUser));
        model.addAttribute("editMode", true);
        return "tasks/form";
    }

    @PostMapping("/{id}/edit")
    public String updateTask(@PathVariable Long id,
                             @AuthenticationPrincipal UserDetails ud,
                             @Valid @ModelAttribute TaskRequest taskRequest,
                             BindingResult bindingResult,
                             RedirectAttributes redirectAttributes,
                             Model model) {
        if (bindingResult.hasErrors()) {
            User currentUser = userService.getCurrentUser(ud.getUsername());
            model.addAttribute("taskId", id);
            model.addAttribute("categories", categoryService.getCategoriesForUser(ud.getUsername()));
            model.addAttribute("priorities", Priority.values());
            model.addAttribute("statuses", TaskStatus.values());
            model.addAttribute("currentUser", currentUser);
            model.addAttribute("unreadCount", notificationService.getUnreadCount(currentUser));
            model.addAttribute("editMode", true);
            return "tasks/form";
        }
        taskService.updateTask(id, taskRequest, ud.getUsername());
        redirectAttributes.addFlashAttribute("success", "Task updated successfully!");
        return "redirect:/tasks/" + id;
    }

    @PostMapping("/{id}/delete")
    public String deleteTask(@PathVariable Long id,
                             @AuthenticationPrincipal UserDetails ud,
                             RedirectAttributes redirectAttributes) {
        taskService.deleteTask(id, ud.getUsername());
        redirectAttributes.addFlashAttribute("success", "Task deleted.");
        return "redirect:/tasks";
    }

    @PostMapping("/{id}/status")
    public String updateStatus(@PathVariable Long id,
                               @RequestParam TaskStatus status,
                               @AuthenticationPrincipal UserDetails ud,
                               RedirectAttributes redirectAttributes) {
        taskService.updateStatus(id, status, ud.getUsername());
        redirectAttributes.addFlashAttribute("success", "Status updated to " + status.name().replace("_", " ") + ".");
        return "redirect:/tasks/" + id;
    }
}
