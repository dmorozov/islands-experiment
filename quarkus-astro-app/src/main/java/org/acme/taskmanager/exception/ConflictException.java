package org.acme.taskmanager.exception;

/**
 * Exception thrown when a resource already exists (duplicate).
 * This exception is mapped to HTTP 409 Conflict.
 */
public class ConflictException extends RuntimeException {

  /**
   * Creates a ConflictException with the specified message.
   *
   * @param message the error message describing the conflict
   */
  public ConflictException(final String message) {
    super(message);
  }

  /**
   * Creates a ConflictException with the specified message and cause.
   *
   * @param message the error message describing the conflict
   * @param cause the underlying cause of the conflict
   */
  public ConflictException(final String message, final Throwable cause) {
    super(message, cause);
  }
}
