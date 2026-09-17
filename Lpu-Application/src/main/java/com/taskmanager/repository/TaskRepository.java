package com.taskmanager.repository;

import com.taskmanager.entity.Priority;
import com.taskmanager.entity.Task;
import com.taskmanager.entity.TaskStatus;
import com.taskmanager.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {

    List<Task> findByAssignedTo(User user);
    List<Task> findByCreatedBy(User user);

    @Query("""
        SELECT t FROM Task t
        WHERE (t.assignedTo = :user OR t.createdBy = :user)
        AND (:status IS NULL OR t.status = :status)
        AND (:priority IS NULL OR t.priority = :priority)
        AND (:search IS NULL OR LOWER(t.title) LIKE LOWER(CONCAT('%',:search,'%')))
        ORDER BY t.createdAt DESC
    """)
    List<Task> findUserTasksFiltered(
            @Param("user") User user,
            @Param("status") TaskStatus status,
            @Param("priority") Priority priority,
            @Param("search") String search
    );

    @Query("""
        SELECT t FROM Task t
        WHERE (:status IS NULL OR t.status = :status)
        AND (:priority IS NULL OR t.priority = :priority)
        AND (:search IS NULL OR LOWER(t.title) LIKE LOWER(CONCAT('%',:search,'%')))
        ORDER BY t.createdAt DESC
    """)
    List<Task> findAllTasksFiltered(
            @Param("status") TaskStatus status,
            @Param("priority") Priority priority,
            @Param("search") String search
    );

    long countByAssignedToAndStatus(User user, TaskStatus status);
    long countByAssignedTo(User user);
    long countByStatus(TaskStatus status);
    long countByPriority(Priority priority);

    List<Task> findByDueDateBeforeAndStatusNot(LocalDate date, TaskStatus status);

    @Query("SELECT t FROM Task t WHERE t.assignedTo = :user OR t.createdBy = :user ORDER BY t.createdAt DESC")
    List<Task> findAllByUser(@Param("user") User user);
}
