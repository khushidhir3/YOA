package com.taskmanager.service.impl;

import com.taskmanager.dao.TaskDao;
import com.taskmanager.dao.UserDao;
import com.taskmanager.dto.request.CommentRequest;
import com.taskmanager.entity.Comment;
import com.taskmanager.entity.Task;
import com.taskmanager.entity.User;
import com.taskmanager.exception.ResourceNotFoundException;
import com.taskmanager.exception.UnauthorizedException;
import com.taskmanager.repository.CommentRepository;
import com.taskmanager.service.CommentService;
import com.taskmanager.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;
    private final TaskDao taskDao;
    private final UserDao userDao;
    private final NotificationService notificationService;

    @Override
    @Transactional
    public Comment addComment(Long taskId, CommentRequest request, String username) {
        Task task = taskDao.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("Task", "id", taskId));
        User author = userDao.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));

        Comment comment = Comment.builder()
                .content(request.getContent())
                .task(task)
                .author(author)
                .build();

        Comment saved = commentRepository.save(comment);

        // Notify task owner if it's not the commenter
        if (!task.getCreatedBy().getUsername().equals(username)) {
            notificationService.createNotification(
                task.getCreatedBy(),
                author.getUsername() + " commented on your task: " + task.getTitle(),
                "COMMENT_ADDED",
                taskId
            );
        }

        return saved;
    }

    @Override
    @Transactional
    public void deleteComment(Long commentId, String username) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new ResourceNotFoundException("Comment", "id", commentId));
        User user = userDao.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));

        boolean isAdmin = user.getRoles().stream().anyMatch(r -> r.getName().equals("ROLE_ADMIN"));
        boolean isAuthor = comment.getAuthor().getUsername().equals(username);

        if (!isAdmin && !isAuthor) {
            throw new UnauthorizedException("You cannot delete this comment");
        }
        commentRepository.delete(comment);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Comment> getCommentsForTask(Long taskId, String username) {
        Task task = taskDao.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("Task", "id", taskId));
        return commentRepository.findByTaskOrderByCreatedAtDesc(task);
    }
}
