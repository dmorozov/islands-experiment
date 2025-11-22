package org.acme.taskmanager.filter;

import io.quarkus.runtime.StartupEvent;
import io.vertx.ext.web.Router;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import org.acme.taskmanager.session.TestSessionUtils;
import org.acme.taskmanager.session.SessionUtils;

/**
 * Test-mode session filter.
 *
 * <p>This filter automatically logs in a test user for all test requests.
 * It only exists on the test classpath and will not be present in production.
 */
@ApplicationScoped
public class TestSessionFilter {

  /**
   * Registers the test auto-login filter.
   *
   * <p>This filter automatically sets a test user in the session for all requests
   * during testing.
   *
   * @param event the startup event
   * @param router the Vert.x router
   */
  public void init(@Observes final StartupEvent event, final Router router) {
    router.route().order(-3).handler(routingContext -> {
      // Auto-login with test user if not already authenticated
      if (!SessionUtils.isAuthenticated(routingContext)) {
        SessionUtils.setCurrentUser(routingContext, TestSessionUtils.TEST_USER_ID);
      }
      routingContext.next();
    });
  }
}
