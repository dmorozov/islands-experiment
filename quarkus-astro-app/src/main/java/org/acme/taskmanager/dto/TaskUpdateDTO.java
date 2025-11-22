package org.acme.taskmanager.dto;

import jakarta.validation.constraints.Size;
import org.acme.taskmanager.model.Priority;

import java.util.UUID;

/**
 * DTO for updating an existing task.
 * T274-T275: All fields are optional to support partial updates (PATCH semantics).
 *
 * @param title the task title (optional, 1-200 characters if provided)
 * @param description the task description (optional, max 2000 characters if provided)
 * @param categoryId the UUID of the category to assign the task to (optional)
 * @param priority the priority level of the task (optional)
 */
public record TaskUpdateDTO(
    @Size(min = 1, max = TaskValidationConstants.MAX_TITLE_LENGTH,
          message = "Title must be between 1 and 200 characters")
    String title,

    @Size(max = TaskValidationConstants.MAX_DESCRIPTION_LENGTH,
          message = "Description must not exceed 2000 characters")
    String description,

    UUID categoryId,

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
