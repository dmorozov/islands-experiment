package org.acme.taskmanager.filter;

import io.quarkus.runtime.StartupEvent;
import io.vertx.core.Vertx;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.SessionHandler;
import io.vertx.ext.web.sstore.LocalSessionStore;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;
import org.acme.taskmanager.session.SessionUtils;
import org.eclipse.microprofile.config.inject.ConfigProperty;

/**
 * Session and development-mode auto-login filter.
 *
 * <p>This filter enables HTTP sessions for the application and automatically
 * logs in a test user during development to allow testing of authenticated
 * endpoints without implementing a full login system.
 *
 * <p><b>IMPORTANT:</b> Auto-login is ONLY active in dev mode and should never
 * be used in production.
 */
@ApplicationScoped
public class DevAutoLoginFilter {

  /** Default test user ID for development. */
  private static final String DEV_USER_ID = "dev-user-123";

  /** Order for session handler - runs very early. */
  private static final int SESSION_HANDLER_ORDER = -10;

  /** Order for auto-login handler - runs after session handler. */
  private static final int AUTO_LOGIN_ORDER = -5;

  @ConfigProperty(name = "quarkus.profile")
  private String profile;

  @Inject
  private Vertx vertx;

  /**
   * Registers session handler and auto-login filter.
   *
   * <p>This method:
   * <ol>
   *   <li>Enables HTTP sessions for all routes
   *   <li>In dev mode, automatically logs in a test user
   * </ol>
   *
   * @param event the startup event
   * @param router the Vert.x router
   */
  public void init(@Observes final StartupEvent event, final Router router) {
    // Create and register session handler for ALL profiles
    SessionHandler sessionHandler = SessionHandler.create(
        LocalSessionStore.create(vertx));

    // Register session handler with high priority (runs early)
    router.route().order(SESSION_HANDLER_ORDER).handler(sessionHandler);

    // Only register auto-login in dev mode
    if ("dev".equals(profile)) {
      router.route().order(AUTO_LOGIN_ORDER).handler(routingContext -> {
        // Check if user is not already authenticated
        if (!SessionUtils.isAuthenticated(routingContext)) {
          // Auto-login with dev user
          SessionUtils.setCurrentUser(routingContext, DEV_USER_ID);
        }
        routingContext.next();
      });
    }
  }
}
