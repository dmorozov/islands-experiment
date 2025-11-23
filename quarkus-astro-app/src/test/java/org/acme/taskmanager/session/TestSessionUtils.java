package org.acme.taskmanager.session;

/**
 * Test utility for managing test user sessions.
 */
public final class TestSessionUtils {

  /** Test user ID used in all integration tests. */
  public static final String TEST_USER_ID = "test-user-123";

  private static final ThreadLocal<String> CURRENT_USER = new ThreadLocal<>();

  private TestSessionUtils() {
    throw new UnsupportedOperationException("Utility class");
  }

  /**
   * Sets the current test user ID.
   *
   * @param userId the user ID to set
   */
  public static void setCurrentUser(final String userId) {
    CURRENT_USER.set(userId);
  }

  /**
   * Gets the current test user ID.
   *
   * @return the current user ID, or TEST_USER_ID if not set
   */
  public static String getCurrentUserId() {
    String userId = CURRENT_USER.get();
    return userId != null ? userId : TEST_USER_ID;
  }

  /**
   * Clears the current test user.
   */
  public static void clear() {
    CURRENT_USER.remove();
  }
}
