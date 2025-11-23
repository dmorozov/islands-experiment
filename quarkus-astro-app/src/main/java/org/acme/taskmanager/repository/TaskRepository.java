package org.acme.taskmanager.repository;

import io.quarkus.hibernate.orm.panache.PanacheQuery;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import io.quarkus.panache.common.Page;
import jakarta.enterprise.context.ApplicationScoped;
import org.acme.taskmanager.model.Priority;
import org.acme.taskmanager.model.Task;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Repository for Task entity operations.
 *
 * <p>This repository provides data access methods for Task entities using Panache. It supports
 * flexible querying with multiple filter combinations and pagination for efficient data retrieval.
 *
 * <p><b>Key Features:</b>
 *
 * <ul>
 *   <li>UUID-based primary keys
 *   <li>User-scoped queries for multi-tenancy
 *   <li>Flexible filtering (status, category, priority)
 *   <li>Pagination support for large result sets
 *   <li>Automatic transaction management via Quarkus
 * </ul>
 *
 * <p><b>Usage Example:</b>
 *
 * <pre>{@code
 * @Inject
 * TaskRepository taskRepository;
 *
 * // Find all tasks for a user
 * List<Task> tasks = taskRepository.findByUserId(userId, 0, 20);
 *
 * // Find active tasks only
 * List<Task> activeTasks = taskRepository.findByUserIdAndCompleted(userId, false, 0, 20);
 *
 * // Find high priority tasks
 * List<Task> highPriority = taskRepository.findByUserIdAndPriority(userId, Priority.HIGH, 0, 20);
 * }</pre>
 */
@ApplicationScoped
public class TaskRepository implements PanacheRepositoryBase<Task, UUID> {

  /**
   * Finds all tasks belonging to a specific user with pagination.
   *
   * <p>Tasks are ordered by creation date (newest first) for consistent display.
   *
   * @param userId the user's unique identifier
   * @param page the page number (0-based)
   * @param size the number of items per page
   * @return list of tasks for the specified page (empty if none found)
   * @throws IllegalArgumentException if userId is null/blank or page parameters are invalid
   */
  public List<Task> findByUserId(final String userId, final int page, final int size) {
    validateUserId(userId);
    validatePagination(page, size);

    return find("userId = ?1 order by createdAt desc", userId).page(Page.of(page, size)).list();
  }

  /**
   * Finds tasks by user and completion status with pagination.
   *
   * <p>Useful for filtering between active (incomplete) and completed tasks.
   *
   * @param userId the user's unique identifier
   * @param completed true for completed tasks, false for active tasks
   * @param page the page number (0-based)
   * @param size the number of items per page
   * @return list of tasks matching the criteria (empty if none found)
   * @throws IllegalArgumentException if userId is null/blank or page parameters are invalid
   */
  public List<Task> findByUserIdAndCompleted(
      final String userId, final boolean completed, final int page, final int size) {
    validateUserId(userId);
    validatePagination(page, size);

    return find("userId = ?1 and completed = ?2 order by createdAt desc", userId, completed)
        .page(Page.of(page, size))
        .list();
  }

  /**
   * Finds tasks by user and category with pagination.
   *
   * <p>Allows filtering tasks by their assigned category.
   *
   * @param userId the user's unique identifier
   * @param categoryId the category's unique identifier
   * @param page the page number (0-based)
   * @param size the number of items per page
   * @return list of tasks in the specified category (empty if none found)
   * @throws IllegalArgumentException if userId/categoryId is null/blank or page parameters are
   *     invalid
   */
  public List<Task> findByUserIdAndCategoryId(
      final String userId, final UUID categoryId, final int page, final int size) {
    validateUserId(userId);
    if (categoryId == null) {
      throw new IllegalArgumentException("Category ID cannot be null");
    }
    validatePagination(page, size);

    return find("userId = ?1 and category.id = ?2 order by createdAt desc", userId, categoryId)
        .page(Page.of(page, size))
        .list();
  }

  /**
   * Finds tasks by user and priority level with pagination.
   *
   * <p>Allows filtering tasks by their priority (HIGH, MEDIUM, LOW).
   *
   * @param userId the user's unique identifier
   * @param priority the priority level to filter by
   * @param page the page number (0-based)
   * @param size the number of items per page
   * @return list of tasks with the specified priority (empty if none found)
   * @throws IllegalArgumentException if userId is null/blank, priority is null, or page parameters
   *     are invalid
   */
  public List<Task> findByUserIdAndPriority(
      final String userId, final Priority priority, final int page, final int size) {
    validateUserId(userId);
    if (priority == null) {
      throw new IllegalArgumentException("Priority cannot be null");
    }
    validatePagination(page, size);

    return find("userId = ?1 and priority = ?2 order by createdAt desc", userId, priority)
        .page(Page.of(page, size))
        .list();
  }

  /**
   * Finds tasks with flexible filtering using dynamic query building.
   *
   * <p>This method allows combining multiple filter criteria (category, priority, completed) with
   * pagination. Only non-null filter parameters are applied to the query.
   *
   * @param userId the user's unique identifier (required)
   * @param categoryId optional category filter
   * @param priority optional priority filter
   * @param completed optional completion status filter
   * @param page the page number (0-based)
   * @param size the number of items per page
   * @return list of tasks matching all provided criteria (empty if none found)
   * @throws IllegalArgumentException if userId is null/blank or page parameters are invalid
   */
  public List<Task> findByUserIdWithFilters(
      final String userId,
      final UUID categoryId,
      final Priority priority,
      final Boolean completed,
      final int page,
      final int size) {
    validateUserId(userId);
    validatePagination(page, size);

    // Build dynamic query
    StringBuilder query = new StringBuilder("userId = :userId");
    Map<String, Object> params = new HashMap<>();
    params.put("userId", userId);

    if (categoryId != null) {
      query.append(" and category.id = :categoryId");
      params.put("categoryId", categoryId);
    }

    if (priority != null) {
      query.append(" and priority = :priority");
      params.put("priority", priority);
    }

    if (completed != null) {
      query.append(" and completed = :completed");
      params.put("completed", completed);
    }

    query.append(" order by createdAt desc");

    PanacheQuery<Task> panacheQuery = find(query.toString(), params);
    return panacheQuery.page(Page.of(page, size)).list();
  }

  /**
   * Validates user ID is not null or blank.
   *
   * @param userId the user ID to validate
   * @throws IllegalArgumentException if userId is null or blank
   */
  private void validateUserId(final String userId) {
    if (userId == null || userId.isBlank()) {
      throw new IllegalArgumentException("User ID cannot be null or blank");
    }
  }

  /**
   * Validates pagination parameters.
   *
   * @param page the page number
   * @param size the page size
   * @throws IllegalArgumentException if page or size are invalid
   */
  private void validatePagination(final int page, final int size) {
    if (page < 0) {
      throw new IllegalArgumentException("Page number cannot be negative");
    }
    if (size <= 0) {
      throw new IllegalArgumentException("Page size must be greater than zero");
    }
  }
}
