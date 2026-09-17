package com.taskmanager.service;

import com.taskmanager.dto.request.UserUpdateRequest;
import com.taskmanager.dto.response.UserResponse;
import com.taskmanager.entity.User;

import java.util.List;

public interface UserService {
    List<UserResponse> getAllUsers();
    UserResponse getUserById(Long id);
    UserResponse updateUser(Long id, UserUpdateRequest request);
    void disableUser(Long id);
    void enableUser(Long id);
    void deleteUser(Long id);
    User getCurrentUser(String username);
    UserResponse mapToResponse(User user);
}
