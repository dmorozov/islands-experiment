package org.acme.taskmanager.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.acme.taskmanager.dto.TaskCreateDTO;
import org.acme.taskmanager.dto.TaskResponseDTO;
import org.acme.taskmanager.dto.TaskUpdateDTO;
import org.acme.taskmanager.exception.ResourceNotFoundException;
import org.acme.taskmanager.exception.ValidationException;
import org.acme.taskmanager.model.Category;
import org.acme.taskmanager.model.Priority;
import org.acme.taskmanager.model.Task;
import org.acme.taskmanager.repository.CategoryRepository;
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
  @Inject private CategoryRepository categoryRepository;

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

  /**
   * Creates a new task for a user.
   *
   * <p>T276-T279: Validates category exists and belongs to user, creates Task entity from DTO,
   * sets userId, and persists to repository.
   *
   * @param userId the user's unique identifier (required)
   * @param dto the task creation data transfer object containing title, description, categoryId, and priority
   * @return TaskResponseDTO containing the created task details
   * @throws IllegalArgumentException if userId is null or blank
   * @throws ValidationException if category doesn't exist or doesn't belong to user
   */
  @Transactional
  public TaskResponseDTO createTask(final String userId, final TaskCreateDTO dto) {
    // Validate required parameters
    if (userId == null || userId.isBlank()) {
      throw new IllegalArgumentException("User ID cannot be null or blank");
    }

    LOG.debugf("Creating task for user=%s with title=%s", userId, dto.title());

    // T277: Validate category exists and belongs to user
    Category category = categoryRepository.findByIdOptional(dto.categoryId())
        .orElseThrow(() -> new ValidationException(
            "Category not found with ID: " + dto.categoryId()));

    if (!category.getUserId().equals(userId)) {
      throw new ValidationException(
          "Category does not belong to user");
    }

    // T278: Create Task entity from DTO, set userId, persist
    Task task = new Task();
    task.setTitle(dto.title());
    task.setDescription(dto.description());
    task.setCategory(category);
    task.setPriority(dto.priority());
    task.setCompleted(false);
    task.setUserId(userId);

    taskRepository.persist(task);

    LOG.debugf("Created task with ID=%s", task.getId());

    // T279: Return TaskResponseDTO
    return TaskResponseDTO.from(task);
  }

  /**
   * Retrieves a single task by ID.
   *
   * <p>T280-T281: Validates task exists and belongs to user.
   *
   * @param userId the user's unique identifier (required)
   * @param taskId the task's unique identifier (required)
   * @return TaskResponseDTO containing the task details
   * @throws IllegalArgumentException if userId or taskId is null
   * @throws ResourceNotFoundException if task not found or doesn't belong to user
   */
  public TaskResponseDTO getTaskById(final String userId, final UUID taskId) {
    // Validate required parameters
    if (userId == null || userId.isBlank()) {
      throw new IllegalArgumentException("User ID cannot be null or blank");
    }
    if (taskId == null) {
      throw new IllegalArgumentException("Task ID cannot be null");
    }

    LOG.debugf("Fetching task with ID=%s for user=%s", taskId, userId);

    // T281: Throw ResourceNotFoundException if task not found or doesn't belong to user
    Task task = taskRepository.findByIdOptional(taskId)
        .orElseThrow(() -> new ResourceNotFoundException(
            "Task not found with ID: " + taskId));

    if (!task.getUserId().equals(userId)) {
      throw new ResourceNotFoundException(
          "Task not found with ID: " + taskId);
    }

    return TaskResponseDTO.from(task);
  }

  /**
   * Updates an existing task.
   *
   * <p>T282-T286: Validates task exists and belongs to user, updates only non-null fields from DTO,
   * validates category if provided, and returns updated TaskResponseDTO.
   *
   * @param userId the user's unique identifier (required)
   * @param taskId the task's unique identifier (required)
   * @param dto the task update data transfer object with optional fields
   * @return TaskResponseDTO containing the updated task details
   * @throws IllegalArgumentException if userId or taskId is null
   * @throws ResourceNotFoundException if task not found or doesn't belong to user
   * @throws ValidationException if category doesn't exist or doesn't belong to user
   */
  @Transactional
  public TaskResponseDTO updateTask(
      final String userId,
      final UUID taskId,
      final TaskUpdateDTO dto) {

    // Validate required parameters
    if (userId == null || userId.isBlank()) {
      throw new IllegalArgumentException("User ID cannot be null or blank");
    }
    if (taskId == null) {
      throw new IllegalArgumentException("Task ID cannot be null");
    }

    LOG.debugf("Updating task with ID=%s for user=%s", taskId, userId);

    // T283: Validate task exists and belongs to user
    Task task = taskRepository.findByIdOptional(taskId)
        .orElseThrow(() -> new ResourceNotFoundException(
            "Task not found with ID: " + taskId));

    if (!task.getUserId().equals(userId)) {
      throw new ResourceNotFoundException(
          "Task not found with ID: " + taskId);
    }

    // T284: Update only non-null fields from DTO
    if (dto.title() != null) {
      task.setTitle(dto.title());
    }

    if (dto.description() != null) {
      task.setDescription(dto.description());
    }

    // T285: Validate category if categoryId provided
    if (dto.categoryId() != null) {
      Category category = categoryRepository.findByIdOptional(dto.categoryId())
          .orElseThrow(() -> new ValidationException(
              "Category not found with ID: " + dto.categoryId()));

      if (!category.getUserId().equals(userId)) {
        throw new ValidationException(
            "Category does not belong to user");
      }

      task.setCategory(category);
    }

    if (dto.priority() != null) {
      task.setPriority(dto.priority());
    }

    taskRepository.persist(task);

    LOG.debugf("Updated task with ID=%s", taskId);

    // T286: Return updated TaskResponseDTO
    return TaskResponseDTO.from(task);
  }

  /**
   * Deletes a task.
   *
   * <p>T287-T288: Validates task exists and belongs to user before deletion.
   *
   * @param userId the user's unique identifier (required)
   * @param taskId the task's unique identifier (required)
   * @throws IllegalArgumentException if userId or taskId is null
   * @throws ResourceNotFoundException if task not found or doesn't belong to user
   */
  @Transactional
  public void deleteTask(final String userId, final UUID taskId) {
    // Validate required parameters
    if (userId == null || userId.isBlank()) {
      throw new IllegalArgumentException("User ID cannot be null or blank");
    }
    if (taskId == null) {
      throw new IllegalArgumentException("Task ID cannot be null");
    }

    LOG.debugf("Deleting task with ID=%s for user=%s", taskId, userId);

    // T288: Validate task exists and belongs to user before deletion
    Task task = taskRepository.findByIdOptional(taskId)
        .orElseThrow(() -> new ResourceNotFoundException(
            "Task not found with ID: " + taskId));

    if (!task.getUserId().equals(userId)) {
      throw new ResourceNotFoundException(
          "Task not found with ID: " + taskId);
    }

    taskRepository.delete(task);

    LOG.debugf("Deleted task with ID=%s", taskId);
  }

  /**
   * Toggles the completion status of a task.
   *
   * <p>T441-T445: If task is incomplete, marks it as complete with timestamp.
   * If task is already complete, marks it as incomplete and clears timestamp.
   *
   * @param userId the user's unique identifier (required)
   * @param taskId the task's unique identifier (required)
   * @return TaskResponseDTO containing the updated task
   * @throws IllegalArgumentException if userId or taskId is null
   * @throws ResourceNotFoundException if task not found or doesn't belong to user
   */
  @Transactional
  public TaskResponseDTO toggleTaskCompletion(final String userId, final UUID taskId) {
    // T442: Validate required parameters
    if (userId == null || userId.isBlank()) {
      throw new IllegalArgumentException("User ID cannot be null or blank");
    }
    if (taskId == null) {
      throw new IllegalArgumentException("Task ID cannot be null");
    }

    LOG.debugf("Toggling completion for task ID=%s for user=%s", taskId, userId);

    // T442: Validate task exists and belongs to user
    Task task = taskRepository.findByIdOptional(taskId)
        .orElseThrow(() -> new ResourceNotFoundException(
            "Task not found with ID: " + taskId));

    if (!task.getUserId().equals(userId)) {
      throw new ResourceNotFoundException(
          "Task not found with ID: " + taskId);
    }

    // T443-T444: Toggle completion status
    if (task.isCompleted()) {
      // Mark as incomplete
      task.setCompleted(false);
      task.setCompletedAt(null);
      LOG.debugf("Marked task ID=%s as incomplete", taskId);
    } else {
      // Mark as complete
      task.setCompleted(true);
      task.setCompletedAt(java.time.LocalDateTime.now());
      LOG.debugf("Marked task ID=%s as complete", taskId);
    }

    taskRepository.persist(task);

    // T445: Return updated TaskResponseDTO
    return TaskResponseDTO.from(task);
  }
}
