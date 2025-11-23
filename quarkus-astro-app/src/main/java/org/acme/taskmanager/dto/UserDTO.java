package org.acme.taskmanager.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * DTO for user session data.
 * Used for demo authentication - username only, no password.
 *
 * @param username the username for demo authentication (1-50 characters)
 */
public record UserDTO(
    @NotNull(message = "Username is required")
    @Size(min = 1, max = MAX_USERNAME_LENGTH,
          message = "Username must be between 1 and 50 characters")
    String username
) {
    /** Maximum allowed length for username. */
    private static final int MAX_USERNAME_LENGTH = 50;
}
