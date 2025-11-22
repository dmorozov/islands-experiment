package org.acme.taskmanager.exception;

/**
 * Exception thrown when a requested resource cannot be found in the system.
 *
 * <p>This exception is typically thrown when attempting to retrieve, update, or delete a resource
 * that doesn't exist or doesn't belong to the current user.
 *
 * <p><b>Common Use Cases:</b>
 *
 * <ul>
 *   <li>Task not found by ID
 *   <li>Category not found by ID
 *   <li>Resource doesn't belong to the authenticated user
 * </ul>
 *
 * <p><b>HTTP Status:</b> This exception should be mapped to HTTP 404 Not Found
 *
 * <p><b>Usage Example:</b>
 *
 * <pre>{@code
 * Task task = taskRepository.findById(taskId);
 * if (task == null) {
 *   throw new ResourceNotFoundException("Task not found with ID: " + taskId);
 * }
 * }</pre>
 */
public class ResourceNotFoundException extends RuntimeException {

  /**
   * Creates a new ResourceNotFoundException with the specified message.
   *
   * @param message the detail message explaining which resource was not found
   */
  public ResourceNotFoundException(String message) {
    super(message);
  }

  /**
   * Creates a new ResourceNotFoundException with the specified message and cause.
   *
   * @param message the detail message explaining which resource was not found
   * @param cause the underlying cause of the failure
   */
  public ResourceNotFoundException(String message, Throwable cause) {
    super(message, cause);
  }
}
