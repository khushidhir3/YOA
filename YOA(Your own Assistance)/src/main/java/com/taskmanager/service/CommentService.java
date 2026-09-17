package com.taskmanager.service;

import com.taskmanager.dto.request.CommentRequest;
import com.taskmanager.entity.Comment;

import java.util.List;

public interface CommentService {
    Comment addComment(Long taskId, CommentRequest request, String username);
    void deleteComment(Long commentId, String username);
    List<Comment> getCommentsForTask(Long taskId, String username);
}
