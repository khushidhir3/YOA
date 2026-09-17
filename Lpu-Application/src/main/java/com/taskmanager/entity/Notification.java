package com.taskmanager.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "notifications")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
@Builder
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 500)
    private String message;

    @Column(nullable = false)
    @Builder.Default
    private boolean read = false;

    @Column(length = 50)
    @Builder.Default
    private String type = "INFO"; // INFO, WARNING, TASK_ASSIGNED, COMMENT_ADDED

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    private Long relatedTaskId;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;
}
