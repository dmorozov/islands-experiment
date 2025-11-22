package org.acme.taskmanager.exception;

/**
 * Exception thrown when a user attempts to access a protected resource without proper
 * authentication.
 *
 * <p>This exception is typically thrown when:
 *
 * <ul>
 *   <li>No user session exists
 *   <li>Session has expired
 *   <li>User ID cannot be found in the session
 * </ul>
 *
 * <p><b>HTTP Status:</b> This exception should be mapped to HTTP 401 Unauthorized
 *
 * <p><b>Usage:</b>
 *
 * <pre>{@code
 * String userId = SessionUtils.getCurrentUserId(session);
 * // throws UnauthorizedException if session is invalid
 * }</pre>
 */
public class UnauthorizedException extends RuntimeException {

  /**
   * Creates a new UnauthorizedException with the specified message.
   *
   * @param message the detail message explaining why authentication failed
   */
  public UnauthorizedException(final String message) {
    super(message);
  }

  /**
   * Creates a new UnauthorizedException with the specified message and cause.
   *
   * @param message the detail message explaining why authentication failed
   * @param cause the underlying cause of the authentication failure
   */
  public UnauthorizedException(final String message, final Throwable cause) {
    super(message, cause);
  }
}
