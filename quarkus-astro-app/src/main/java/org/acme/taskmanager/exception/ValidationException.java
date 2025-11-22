package org.acme.taskmanager.exception;

/**
 * Exception thrown when input validation fails for a business operation.
 *
 * <p>This exception is used for business-level validation errors that go beyond simple bean
 * validation constraints. It should be thrown when the data is syntactically valid but
 * semantically incorrect according to business rules.
 *
 * <p><b>Common Use Cases:</b>
 *
 * <ul>
 *   <li>Attempting to delete a default category
 *   <li>Creating a category with a duplicate name for the same user
 *   <li>Attempting to complete an already completed task
 *   <li>Business rule violations that require custom validation logic
 * </ul>
 *
 * <p><b>HTTP Status:</b> This exception should be mapped to HTTP 400 Bad Request
 *
 * <p><b>Usage Example:</b>
 *
 * <pre>{@code
 * if (category.isDefault()) {
 *   throw new ValidationException("Cannot delete default category");
 * }
 *
 * if (categoryRepository.existsByUserIdAndName(userId, name)) {
 *   throw new ValidationException("Category name already exists");
 * }
 * }</pre>
 *
 * <p><b>Note:</b> For simple field-level validation (e.g., null checks, length constraints), use
 * Jakarta Bean Validation annotations ({@code @NotNull}, {@code @Size}, etc.) instead. This
 * exception is for more complex business rules.
 */
public class ValidationException extends RuntimeException {

  private String field;

  /**
   * Creates a new ValidationException with the specified message.
   *
   * @param message the detail message explaining why validation failed
   */
  public ValidationException(String message) {
    super(message);
  }

  /**
   * Creates a new ValidationException with the specified message and field.
   *
   * @param message the detail message explaining why validation failed
   * @param field the name of the field that failed validation
   */
  public ValidationException(String message, String field) {
    super(message);
    this.field = field;
  }

  /**
   * Creates a new ValidationException with the specified message and cause.
   *
   * @param message the detail message explaining why validation failed
   * @param cause the underlying cause of the validation failure
   */
  public ValidationException(String message, Throwable cause) {
    super(message, cause);
  }

  /**
   * Gets the field that failed validation, if applicable.
   *
   * @return the field name, or null if the validation error is not field-specific
   */
  public String getField() {
    return field;
  }
}
