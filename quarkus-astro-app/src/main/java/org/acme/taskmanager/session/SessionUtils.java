package org.acme.taskmanager.session;

import io.vertx.ext.web.RoutingContext;
import org.acme.taskmanager.exception.UnauthorizedException;

/**
 * Utility class for managing HTTP session state in the Task Manager application.
 *
 * <p>This class provides methods for storing and retrieving user authentication information from
 * HTTP sessions. It follows a session-based authentication pattern where user identity is stored
 * server-side after login.
 *
 * <p><b>Session Attributes:</b>
 *
 * <ul>
 *   <li><code>userId</code> - The unique identifier of the authenticated user
 * </ul>
 *
 * <p><b>Security Notes:</b>
 *
 * <ul>
 *   <li>Always validate session state before accessing protected resources
 *   <li>Session cookies should be HTTP-only and secure in production
 *   <li>Consider session timeout configuration in application.properties
 * </ul>
 *
 * <p><b>Usage Example:</b>
 *
 * <pre>{@code
 * // In login endpoint
 * SessionUtils.setCurrentUser(context, "user123");
 *
 * // In protected endpoint
 * String userId = SessionUtils.getCurrentUserId(context);
 * // Use userId to fetch user-specific data
 * }</pre>
 */
public final class SessionUtils {

  /** Session attribute key for storing the authenticated user's ID. */
  private static final String USER_ID_ATTRIBUTE = "userId";

  /** Private constructor to prevent instantiation of utility class. */
  private SessionUtils() {
    throw new UnsupportedOperationException("Utility class cannot be instantiated");
  }

  /**
   * Retrieves the current authenticated user's ID from the session.
   *
   * <p>This method extracts the user ID from the HTTP session. If no user is authenticated (i.e.,
   * the session doesn't contain a user ID), it throws an {@link UnauthorizedException}.
   *
   * @param context the Vert.x routing context containing the session
   * @return the authenticated user's ID
   * @throws UnauthorizedException if no user is authenticated in the current session
   * @throws IllegalArgumentException if context is null
   */
  public static String getCurrentUserId(RoutingContext context) {
    if (context == null) {
      throw new IllegalArgumentException("RoutingContext cannot be null");
    }

    String userId = context.session().get(USER_ID_ATTRIBUTE);

    if (userId == null || userId.isBlank()) {
      throw new UnauthorizedException("No authenticated user found in session");
    }

    return userId;
  }

  /**
   * Sets the current user in the session.
   *
   * <p>This method stores the user's ID in the HTTP session, marking them as authenticated. This
   * should be called after successful login validation.
   *
   * @param context the Vert.x routing context containing the session
   * @param userId the user's unique identifier to store in the session
   * @throws IllegalArgumentException if context or userId is null/blank
   */
  public static void setCurrentUser(RoutingContext context, String userId) {
    if (context == null) {
      throw new IllegalArgumentException("RoutingContext cannot be null");
    }

    if (userId == null || userId.isBlank()) {
      throw new IllegalArgumentException("User ID cannot be null or blank");
    }

    context.session().put(USER_ID_ATTRIBUTE, userId);
  }

  /**
   * Clears the current user from the session (logout).
   *
   * <p>This method removes the user ID from the session, effectively logging them out. The session
   * itself is not destroyed, only the user authentication state is cleared.
   *
   * @param context the Vert.x routing context containing the session
   * @throws IllegalArgumentException if context is null
   */
  public static void clearCurrentUser(RoutingContext context) {
    if (context == null) {
      throw new IllegalArgumentException("RoutingContext cannot be null");
    }

    context.session().remove(USER_ID_ATTRIBUTE);
  }

  /**
   * Checks if a user is currently authenticated in the session.
   *
   * <p>This method returns true if a valid user ID exists in the session, false otherwise.
   *
   * @param context the Vert.x routing context containing the session
   * @return true if a user is authenticated, false otherwise
   * @throws IllegalArgumentException if context is null
   */
  public static boolean isAuthenticated(RoutingContext context) {
    if (context == null) {
      throw new IllegalArgumentException("RoutingContext cannot be null");
    }

    String userId = context.session().get(USER_ID_ATTRIBUTE);
    return userId != null && !userId.isBlank();
  }
}
