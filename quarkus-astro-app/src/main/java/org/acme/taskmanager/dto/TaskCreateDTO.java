package org.acme.taskmanager.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.acme.taskmanager.model.Priority;

import java.util.UUID;

/**
 * DTO for creating a new task.
 * T272-T273: Contains title, description, categoryId, and priority.
 *
 * @param title the task title (required, 1-200 characters)
 * @param description the task description (optional, max 2000 characters)
 * @param categoryId the UUID of the category to assign the task to (required)
 * @param priority the priority level of the task (required)
 */
public record TaskCreateDTO(
    @NotNull(message = "Title is required")
    @Size(min = 1, max = TaskValidationConstants.MAX_TITLE_LENGTH,
          message = "Title must be between 1 and 200 characters")
    String title,

    @Size(max = TaskValidationConstants.MAX_DESCRIPTION_LENGTH,
          message = "Description must not exceed 2000 characters")
    String description,

    @NotNull(message = "Category ID is required")
    UUID categoryId,

    @NotNull(message = "Priority is required")
    Priority priority
) {
    /**
     * Validation constants for task fields.
     */
    static final class TaskValidationConstants {
        static final int MAX_TITLE_LENGTH = 200;
        static final int MAX_DESCRIPTION_LENGTH = 2000;

        private TaskValidationConstants() {
            // Utility class
        }
    }
}
