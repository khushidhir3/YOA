package com.taskmanager.config;

import com.taskmanager.entity.*;
import com.taskmanager.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Set;

/**
 * DataLoader - Seeds the database with initial roles, users, categories, and sample tasks.
 * This is IDEMPOTENT - safe to run on every restart. It checks for existing data before inserting.
 * This class ONLY calls repository methods and does NOT modify any entity, service, or controller class.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class DataLoader implements ApplicationListener<ApplicationReadyEvent> {

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final TaskRepository taskRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void onApplicationEvent(ApplicationReadyEvent event) {
        log.info("🌱 DataLoader: Checking and seeding initial data...");

        Role adminRole = seedRole("ROLE_ADMIN");
        Role userRole = seedRole("ROLE_USER");

        User admin = seedUser("admin", "Admin User", "admin@taskmanager.com", "Admin@123", Set.of(adminRole));
        User alice = seedUser("alice", "Alice", "alice@example.com", "User@123", Set.of(userRole));
        User bob   = seedUser("bob",   "Bob Smith",    "bob@example.com",   "User@123", Set.of(userRole));
        User carol = seedUser("carol", "Carol White",  "carol@example.com", "User@123", Set.of(userRole));
        User dave  = seedUser("dave",  "Dave Brown",   "dave@example.com",  "User@123", Set.of(userRole));

        Category workCat    = seedCategory("Work",     "#6366f1", null);
        Category personalCat = seedCategory("Personal", "#8b5cf6", null);
        Category bugCat     = seedCategory("Bug Fix",  "#ef4444", null);

        // Sample Tasks (only seed if task table is empty)
        if (taskRepository.count() == 0) {
            seedTask("Set up CI/CD pipeline",           "Configure GitHub Actions for automated deployments",       Priority.HIGH,     TaskStatus.IN_PROGRESS, LocalDate.now().plusDays(5),   workCat,    admin, alice);
            seedTask("Design landing page mockup",      "Create a Figma mockup for the new homepage",               Priority.MEDIUM,   TaskStatus.TODO,         LocalDate.now().plusDays(10),  workCat,    alice, alice);
            seedTask("Fix login redirect bug",          "After login, users are being redirected to /error",        Priority.CRITICAL, TaskStatus.TODO,         LocalDate.now().minusDays(1),  bugCat,     admin, bob);
            seedTask("Write unit tests for AuthService","Achieve 80%% coverage for the authentication module",      Priority.HIGH,     TaskStatus.TODO,         LocalDate.now().plusDays(3),   workCat,    bob,   bob);
            seedTask("Update API documentation",        "Sync Swagger docs with latest endpoint changes",           Priority.LOW,      TaskStatus.DONE,         LocalDate.now().minusDays(3),  workCat,    alice, carol);
            seedTask("Database backup script",          "Schedule a daily H2 backup to cloud storage",              Priority.MEDIUM,   TaskStatus.TODO,         LocalDate.now().plusDays(7),   workCat,    admin, dave);
            seedTask("Grocery shopping",                "Buy vegetables, fruits and weekly supplies",               Priority.LOW,      TaskStatus.TODO,         LocalDate.now().plusDays(1),   personalCat,alice, alice);
            seedTask("Performance profiling",           "Identify and fix N+1 queries in the task list endpoint",   Priority.HIGH,     TaskStatus.IN_PROGRESS,  LocalDate.now().plusDays(4),   bugCat,     bob,   bob);
            seedTask("Onboard new team members",        "Prepare onboarding docs and walkthrough sessions",         Priority.MEDIUM,   TaskStatus.TODO,         LocalDate.now().plusDays(14),  workCat,    admin, carol);
            seedTask("Release v1.0",                    "Final QA and production deployment",                       Priority.CRITICAL, TaskStatus.TODO,         LocalDate.now().plusDays(21),  workCat,    admin, admin);
            log.info("✅ Seeded 10 sample tasks.");
        }

        log.info("✅ DataLoader: Seeding complete.");
    }

    private Role seedRole(String name) {
        return roleRepository.findByName(name).orElseGet(() -> {
            log.info("  → Creating role: {}", name);
            return roleRepository.save(Role.builder().name(name).build());
        });
    }

    private User seedUser(String username, String fullName, String email, String rawPassword, Set<Role> roles) {
        return userRepository.findByUsername(username).orElseGet(() -> {
            log.info("  → Creating user: {}", username);
            return userRepository.save(User.builder()
                    .username(username)
                    .fullName(fullName)
                    .email(email)
                    .password(passwordEncoder.encode(rawPassword))
                    .enabled(true)
                    .roles(roles)
                    .build());
        });
    }

    private Category seedCategory(String name, String color, User user) {
        boolean exists = categoryRepository.findByUserOrGlobal(
            user != null ? user : userRepository.findAll().get(0)
        ).stream().anyMatch(c -> c.getName().equals(name) && c.getUser() == null);

        if (!exists) {
            log.info("  → Creating category: {}", name);
            return categoryRepository.save(Category.builder().name(name).color(color).user(user).build());
        }
        return categoryRepository.findAll().stream()
                .filter(c -> c.getName().equals(name)).findFirst().orElse(null);
    }

    private void seedTask(String title, String description, Priority priority, TaskStatus status,
                          LocalDate dueDate, Category category, User createdBy, User assignedTo) {
        Task task = Task.builder()
                .title(title)
                .description(description)
                .priority(priority)
                .status(status)
                .dueDate(dueDate)
                .category(category)
                .createdBy(createdBy)
                .assignedTo(assignedTo)
                .build();
        taskRepository.save(task);
    }
}
