package com.taskmanager.controller;

import com.taskmanager.dto.request.CommentRequest;
import com.taskmanager.entity.User;
import com.taskmanager.service.CommentService;
import com.taskmanager.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/tasks/{taskId}/comments")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;
    private final UserService userService;

    @PostMapping
    public String addComment(@PathVariable Long taskId,
                             @AuthenticationPrincipal UserDetails ud,
                             @Valid @ModelAttribute CommentRequest request,
                             BindingResult result,
                             RedirectAttributes ra) {
        if (result.hasErrors()) {
            ra.addFlashAttribute("commentError", "Comment cannot be empty.");
        } else {
            commentService.addComment(taskId, request, ud.getUsername());
            ra.addFlashAttribute("success", "Comment added.");
        }
        return "redirect:/tasks/" + taskId;
    }

    @PostMapping("/{commentId}/delete")
    public String deleteComment(@PathVariable Long taskId,
                                @PathVariable Long commentId,
                                @AuthenticationPrincipal UserDetails ud,
                                RedirectAttributes ra) {
        commentService.deleteComment(commentId, ud.getUsername());
        ra.addFlashAttribute("success", "Comment deleted.");
        return "redirect:/tasks/" + taskId;
    }
}
