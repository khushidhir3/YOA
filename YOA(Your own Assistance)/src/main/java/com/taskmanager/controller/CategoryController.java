package com.taskmanager.controller;

import com.taskmanager.dto.request.CategoryRequest;
import com.taskmanager.entity.Category;
import com.taskmanager.entity.User;
import com.taskmanager.service.CategoryService;
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
@RequestMapping("/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;
    private final UserService userService;
    private final NotificationService notificationService;

    @GetMapping
    public String list(@AuthenticationPrincipal UserDetails ud, Model model) {
        User currentUser = userService.getCurrentUser(ud.getUsername());
        List<Category> categories = categoryService.getCategoriesForUser(ud.getUsername());
        model.addAttribute("categories", categories);
        model.addAttribute("currentUser", currentUser);
        model.addAttribute("unreadCount", notificationService.getUnreadCount(currentUser));
        return "categories/list";
    }

    @GetMapping("/new")
    public String newForm(@AuthenticationPrincipal UserDetails ud, Model model) {
        User currentUser = userService.getCurrentUser(ud.getUsername());
        model.addAttribute("categoryRequest", new CategoryRequest());
        model.addAttribute("currentUser", currentUser);
        model.addAttribute("unreadCount", notificationService.getUnreadCount(currentUser));
        return "categories/form";
    }

    @PostMapping("/new")
    public String create(@AuthenticationPrincipal UserDetails ud,
                         @Valid @ModelAttribute CategoryRequest req,
                         BindingResult result,
                         RedirectAttributes ra, Model model) {
        if (result.hasErrors()) {
            User currentUser = userService.getCurrentUser(ud.getUsername());
            model.addAttribute("currentUser", currentUser);
            model.addAttribute("unreadCount", notificationService.getUnreadCount(currentUser));
            return "categories/form";
        }
        categoryService.createCategory(req, ud.getUsername());
        ra.addFlashAttribute("success", "Category created!");
        return "redirect:/categories";
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, @AuthenticationPrincipal UserDetails ud, Model model) {
        User currentUser = userService.getCurrentUser(ud.getUsername());
        List<Category> categories = categoryService.getCategoriesForUser(ud.getUsername());
        Category cat = categories.stream().filter(c -> c.getId().equals(id)).findFirst().orElseThrow();
        CategoryRequest req = new CategoryRequest();
        req.setName(cat.getName());
        req.setColor(cat.getColor());
        model.addAttribute("categoryRequest", req);
        model.addAttribute("categoryId", id);
        model.addAttribute("editMode", true);
        model.addAttribute("currentUser", currentUser);
        model.addAttribute("unreadCount", notificationService.getUnreadCount(currentUser));
        return "categories/form";
    }

    @PostMapping("/{id}/edit")
    public String update(@PathVariable Long id,
                         @AuthenticationPrincipal UserDetails ud,
                         @Valid @ModelAttribute CategoryRequest req,
                         BindingResult result,
                         RedirectAttributes ra, Model model) {
        if (result.hasErrors()) {
            User currentUser = userService.getCurrentUser(ud.getUsername());
            model.addAttribute("categoryId", id);
            model.addAttribute("editMode", true);
            model.addAttribute("currentUser", currentUser);
            model.addAttribute("unreadCount", notificationService.getUnreadCount(currentUser));
            return "categories/form";
        }
        categoryService.updateCategory(id, req, ud.getUsername());
        ra.addFlashAttribute("success", "Category updated!");
        return "redirect:/categories";
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id,
                         @AuthenticationPrincipal UserDetails ud,
                         RedirectAttributes ra) {
        categoryService.deleteCategory(id, ud.getUsername());
        ra.addFlashAttribute("success", "Category deleted.");
        return "redirect:/categories";
    }
}
