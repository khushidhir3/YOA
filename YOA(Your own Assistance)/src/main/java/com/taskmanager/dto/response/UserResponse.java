package com.taskmanager.dto.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Set;

@Data
@Builder
public class UserResponse {
    private Long id;
    private String username;
    private String email;
    private String fullName;
    private boolean enabled;
    private Set<String> roles;
    private LocalDateTime createdAt;
}
