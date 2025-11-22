package org.acme.taskmanager.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Category entity representing a task organization category.
 * Each category belongs to a specific user and can contain multiple tasks.
 */
@Entity
@Table(name = "categories",
       uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "name"}),
       indexes = {
           @Index(name = "idx_category_user", columnList = "user_id"),
           @Index(name = "idx_category_name", columnList = "name")
       })
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @EqualsAndHashCode.Include
    private UUID id;

    @NotNull
    @Size(min = 1, max = 50)
    @Column(nullable = false, length = 50)
    private String name;

    @Size(max = 7)
    @Column(name = "color_code", length = 7)
    private String colorCode;

    @Column(name = "is_default", nullable = false)
    private boolean isDefault = false;

    @NotNull
    @Column(name = "user_id", nullable = false, length = 100)
    private String userId;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "category", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Task> tasks = new ArrayList<>();

    /**
     * Constructor for creating a new category.
     *
     * @param name the category name
     * @param colorCode the hex color code (e.g., "#FF5733")
     * @param isDefault whether this is a default category
     * @param userId the user ID who owns this category
     */
    public Category(String name, String colorCode, boolean isDefault, String userId) {
        this.name = name;
        this.colorCode = colorCode;
        this.isDefault = isDefault;
        this.userId = userId;
    }

    /**
     * Business logic to determine if this category can be deleted.
     * Default categories cannot be deleted.
     *
     * @return true if the category can be deleted, false otherwise
     */
    public boolean canDelete() {
        return !isDefault;
    }
}
