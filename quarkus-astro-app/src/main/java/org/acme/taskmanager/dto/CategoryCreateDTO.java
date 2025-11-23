package org.acme.taskmanager.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * DTO for creating a new category.
 * T356-T357: Contains name and colorCode fields with validation.
 *
 * @param name the category name (required, 1-50 characters, unique per user)
 * @param colorCode the hex color code (required, 7 characters including #, e.g., #FF5733)
 */
public record CategoryCreateDTO(
    @NotNull(message = "Category name is required")
    @Size(min = 1, max = CategoryValidationConstants.MAX_NAME_LENGTH,
          message = "Category name must be between 1 and 50 characters")
    String name,

    @NotNull(message = "Color code is required")
    @Size(min = CategoryValidationConstants.COLOR_CODE_LENGTH,
          max = CategoryValidationConstants.COLOR_CODE_LENGTH,
          message = "Color code must be exactly 7 characters (e.g., #FF5733)")
    @Pattern(regexp = CategoryValidationConstants.COLOR_CODE_PATTERN,
             message = "Color code must be a valid hex color (e.g., #FF5733)")
    String colorCode
) {
    /**
     * Validation constants for category fields.
     */
    static final class CategoryValidationConstants {
        static final int MAX_NAME_LENGTH = 50;
        static final int COLOR_CODE_LENGTH = 7;
        static final String COLOR_CODE_PATTERN = "^#[0-9A-Fa-f]{6}$";

        private CategoryValidationConstants() {
            // Utility class
        }
    }
}
