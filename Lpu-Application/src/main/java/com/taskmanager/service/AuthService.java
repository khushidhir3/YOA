package com.taskmanager.service;

import com.taskmanager.dto.request.LoginRequest;
import com.taskmanager.dto.request.RegisterRequest;
import jakarta.servlet.http.HttpServletResponse;

public interface AuthService {
    void login(LoginRequest request, HttpServletResponse response);
    void register(RegisterRequest request);
    void logout(HttpServletResponse response);
}
