package org.acme.taskmanager.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.acme.taskmanager.model.Priority;
import org.acme.taskmanager.model.Task;
import org.acme.taskmanager.repository.TaskRepository;
import org.jboss.logging.Logger;

import java.util.List;
import java.util.UUID;

/**
 * Service layer for Task business logic.
 *
 * <p>This service handles task-related operations including retrieval with flexible filtering and
 * pagination. It encapsulates business rules and coordinates between the repository layer and REST
 * endpoints.
 *
 * <p><b>Key Responsibilities:</b>
 *
 * <ul>
 *   <li>Retrieve tasks with flexible filtering (category, priority, status)
 *   <li>Support pagination for large result sets
 *   <li>Validate input parameters
 *   <li>Provide sensible defaults for optional parameters
 * </ul>
 *
 * <p><b>Usage Example:</b>
 *
 * <pre>{@code
 * @Inject
 * TaskService taskService;
 *
 * // Get all tasks
 * List<Task> allTasks = taskService.getAllTasks(userId, null, null, null, 0, 20);
 *
 * // Get only active HIGH priority tasks in a specific category
 * List<Task> filtered = taskService.getAllTasks(
 *     userId, categoryId, Priority.HIGH, false, 0, 20);
 * }</pre>
 */
@ApplicationScoped
public class TaskService {

  private static final Logger LOG = Logger.getLogger(TaskService.class);

  private static final int DEFAULT_PAGE = 0;
  private static final int DEFAULT_PAGE_SIZE = 20;
  private static final int MAX_PAGE_SIZE = 100;

  @Inject private TaskRepository taskRepository;

  /**
   * Retrieves all tasks for a user with optional filtering and pagination.
   *
   * <p>This method supports flexible filtering by category, priority, and completion status. All
   * filter parameters are optional - passing null means that filter is not applied.
   *
   * <p><b>Filter Parameters:</b>
   *
   * <ul>
   *   <li><b>categoryId</b> - Filter by specific category (null = all categories)
   *   <li><b>priority</b> - Filter by priority level (null = all priorities)
   *   <li><b>completed</b> - Filter by completion status (null = all tasks, true = completed
   *       only, false = active only)
   * </ul>
   *
   * <p><b>Pagination:</b> Results are paginated for performance. Default page size is 20, maximum
   * is 100.
   *
   * <p><b>Ordering:</b> Tasks are always returned in reverse chronological order (newest first).
   *
   * @param userId the user's unique identifier (required)
   * @param categoryId optional category ID to filter by (null for all categories)
   * @param priority optional priority level to filter by (null for all priorities)
   * @param completed optional completion status (null for all, true for completed, false for
   *     active)
   * @param page the page number (0-based, defaults to 0 if negative)
   * @param size the number of items per page (defaults to 20 if invalid, max 100)
   * @return list of tasks matching the criteria, ordered by creation date descending (empty if
   *     none found)
   * @throws IllegalArgumentException if userId is null or blank
   */
  public List<Task> getAllTasks(
      final String userId,
      final String categoryId,
      final Priority priority,
      final Boolean completed,
      final int page,
      final int size) {

    // Validate required parameters
    if (userId == null || userId.isBlank()) {
      throw new IllegalArgumentException("User ID cannot be null or blank");
    }

    // Apply pagination defaults and limits
    int effectivePage = Math.max(page, DEFAULT_PAGE);
    int effectiveSize = size;

    if (effectiveSize <= 0) {
      effectiveSize = DEFAULT_PAGE_SIZE;
      LOG.debugf("Invalid page size %d, using default: %d", size, DEFAULT_PAGE_SIZE);
    } else if (effectiveSize > MAX_PAGE_SIZE) {
      effectiveSize = MAX_PAGE_SIZE;
      LOG.debugf("Page size %d exceeds maximum, using: %d", size, MAX_PAGE_SIZE);
    }

    // Parse categoryId if provided
    UUID categoryUuid = null;
    if (categoryId != null && !categoryId.isBlank()) {
      try {
        categoryUuid = UUID.fromString(categoryId);
      } catch (IllegalArgumentException e) {
        LOG.warnf("Invalid category ID format: %s", categoryId);
        throw new IllegalArgumentException("Invalid category ID format: " + categoryId, e);
      }
    }

    // Log the query for debugging
    LOG.debugf(
        "Fetching tasks for user=%s, category=%s, priority=%s, completed=%s, page=%d, size=%d",
        userId, categoryUuid, priority, completed, effectivePage, effectiveSize);

    // Delegate to repository with all filters
    List<Task> tasks =
        taskRepository.findByUserIdWithFilters(
            userId, categoryUuid, priority, completed, effectivePage, effectiveSize);

    LOG.debugf("Found %d tasks matching criteria", tasks.size());

    return tasks;
  }
}
