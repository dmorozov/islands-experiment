package org.acme.taskmanager.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.quarkus.runtime.annotations.RegisterForReflection;

/**
 * Data Transfer Object for API error responses.
 *
 * <p>This record provides a standardized format for error messages returned by the Task Manager
 * API. It includes information about the error message, the specific field that caused the error
 * (if applicable), and an error code for programmatic handling.
 *
 * <p><b>Fields:</b>
 *
 * <ul>
 *   <li><code>message</code> - Human-readable error description
 *   <li><code>field</code> - The field name that caused the error (null for non-field errors)
 *   <li><code>code</code> - Machine-readable error code (e.g., "VALIDATION_ERROR", "NOT_FOUND")
 * </ul>
 *
 * <p><b>Usage Example:</b>
 *
 * <pre>{@code
 * // Field validation error
 * new ErrorDTO(
 *   "Title must be between 1 and 200 characters",
 *   "title",
 *   "VALIDATION_ERROR"
 * )
 *
 * // Resource not found error
 * new ErrorDTO(
 *   "Task not found with ID: abc123",
 *   null,
 *   "NOT_FOUND"
 * )
 * }</pre>
 *
 * <p><b>JSON Serialization:</b> Fields with null values are omitted from JSON output using {@code
 * JsonInclude.Include.NON_NULL}.
 *
 * @param message the human-readable error message describing what went wrong
 * @param field the name of the field that caused the error (null if not field-specific)
 * @param code the machine-readable error code for programmatic error handling
 */
@RegisterForReflection
@JsonInclude(JsonInclude.Include.NON_NULL)
public record ErrorDTO(String message, String field, String code) {

  /**
   * Creates an ErrorDTO for a general error without a specific field.
   *
   * @param message the error message
   * @param code the error code
   * @return a new ErrorDTO with field set to null
   */
  public static ErrorDTO of(String message, String code) {
    return new ErrorDTO(message, null, code);
  }

  /**
   * Creates an ErrorDTO for a field-specific validation error.
   *
   * @param message the error message
   * @param field the field that caused the error
   * @param code the error code
   * @return a new ErrorDTO with all fields populated
   */
  public static ErrorDTO fieldError(String message, String field, String code) {
    return new ErrorDTO(message, field, code);
  }
}
