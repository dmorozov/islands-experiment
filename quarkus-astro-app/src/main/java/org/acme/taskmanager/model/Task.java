package org.acme.taskmanager.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Task entity representing a user's task or to-do item.
 * Each task belongs to a specific user and category.
 */
@Entity
@Table(name = "tasks", indexes = {
    @Index(name = "idx_task_user", columnList = "user_id"),
    @Index(name = "idx_task_category", columnList = "category_id"),
    @Index(name = "idx_task_completed", columnList = "completed"),
    @Index(name = "idx_task_created", columnList = "created_at")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Task {

    private static final int TITLE_MAX_LENGTH = 200;
    private static final int DESCRIPTION_MAX_LENGTH = 2000;
    private static final int PRIORITY_MAX_LENGTH = 10;
    private static final int USER_ID_MAX_LENGTH = 100;

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @EqualsAndHashCode.Include
    private UUID id;

    @NotNull
    @Size(min = 1, max = TITLE_MAX_LENGTH)
    @Column(nullable = false, length = TITLE_MAX_LENGTH)
    private String title;

    @Size(max = DESCRIPTION_MAX_LENGTH)
    @Column(length = DESCRIPTION_MAX_LENGTH)
    private String description;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = PRIORITY_MAX_LENGTH)
    private Priority priority = Priority.MEDIUM;

    @Column(nullable = false)
    private boolean completed = false;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    @NotNull
    @Column(name = "user_id", nullable = false, length = USER_ID_MAX_LENGTH)
    private String userId;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    /**
     * Constructor for creating a new task.
     *
     * @param title the task title
     * @param description the task description
     * @param category the category this task belongs to
     * @param priority the priority level
     * @param userId the user ID who owns this task
     */
    public Task(final String title, final String description, final Category category,
               final Priority priority, final String userId) {
        this.title = title;
        this.description = description;
        this.category = category;
        this.priority = priority;
        this.userId = userId;
    }
}
