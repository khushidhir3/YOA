package com.taskmanager.aop;

import com.taskmanager.dto.response.TaskResponse;
import com.taskmanager.dto.response.UserResponse;
import com.taskmanager.service.AuditLogService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Slf4j
@RequiredArgsConstructor
public class AuditAspect {

    private final AuditLogService auditLogService;

    @Pointcut("execution(* com.taskmanager.service.impl.TaskServiceImpl.createTask(..))")
    public void taskCreate() {}

    @Pointcut("execution(* com.taskmanager.service.impl.TaskServiceImpl.updateTask(..))")
    public void taskUpdate() {}

    @Pointcut("execution(* com.taskmanager.service.impl.TaskServiceImpl.deleteTask(..))")
    public void taskDelete() {}

    @Pointcut("execution(* com.taskmanager.service.impl.UserServiceImpl.disableUser(..))")
    public void userDisable() {}

    @Pointcut("execution(* com.taskmanager.service.impl.UserServiceImpl.enableUser(..))")
    public void userEnable() {}

    @Pointcut("execution(* com.taskmanager.service.impl.UserServiceImpl.deleteUser(..))")
    public void userDelete() {}

    @Pointcut("execution(* com.taskmanager.service.impl.UserServiceImpl.updateUser(..))")
    public void userUpdate() {}

    @AfterReturning(pointcut = "taskCreate()", returning = "result")
    public void auditTaskCreate(JoinPoint jp, Object result) {
        if (result instanceof TaskResponse task) {
            audit("TASK_CREATED", "Task", task.getId(), "Created: " + task.getTitle());
        }
    }

    @AfterReturning(pointcut = "taskUpdate()", returning = "result")
    public void auditTaskUpdate(JoinPoint jp, Object result) {
        if (result instanceof TaskResponse task) {
            audit("TASK_UPDATED", "Task", task.getId(), "Updated: " + task.getTitle());
        }
    }

    @AfterReturning("taskDelete()")
    public void auditTaskDelete(JoinPoint jp) {
        Object[] args = jp.getArgs();
        if (args.length > 0 && args[0] instanceof Long id) {
            audit("TASK_DELETED", "Task", id, "Deleted task id=" + id);
        }
    }

    @AfterReturning("userDisable()")
    public void auditUserDisable(JoinPoint jp) {
        if (jp.getArgs().length > 0 && jp.getArgs()[0] instanceof Long id) {
            audit("USER_DISABLED", "User", id, "Disabled user id=" + id);
        }
    }

    @AfterReturning("userEnable()")
    public void auditUserEnable(JoinPoint jp) {
        if (jp.getArgs().length > 0 && jp.getArgs()[0] instanceof Long id) {
            audit("USER_ENABLED", "User", id, "Enabled user id=" + id);
        }
    }

    @AfterReturning("userDelete()")
    public void auditUserDelete(JoinPoint jp) {
        if (jp.getArgs().length > 0 && jp.getArgs()[0] instanceof Long id) {
            audit("USER_DELETED", "User", id, "Deleted user id=" + id);
        }
    }

    @AfterReturning(pointcut = "userUpdate()", returning = "result")
    public void auditUserUpdate(JoinPoint jp, Object result) {
        if (result instanceof UserResponse user) {
            audit("USER_UPDATED", "User", user.getId(), "Updated: " + user.getUsername());
        }
    }

    private void audit(String action, String entity, Long entityId, String details) {
        String performer = getCurrentUsername();
        try {
            auditLogService.log(action, performer, entity, entityId, details);
        } catch (Exception e) {
            log.warn("Failed to write audit log for action {}: {}", action, e.getMessage());
        }
    }

    private String getCurrentUsername() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return auth != null ? auth.getName() : "system";
    }
}
