package org.acme.taskmanager.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.quarkus.runtime.annotations.RegisterForReflection;
import org.acme.taskmanager.model.Priority;
import org.acme.taskmanager.model.Task;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Data Transfer Object for Task API responses.
 *
 * <p>This record represents a task in API responses, including the nested category information. It
 * provides a clean separation between the internal entity model and the external API contract.
 *
 * <p><b>Fields:</b>
 *
 * <ul>
 *   <li><code>id</code> - Unique task identifier (UUID)
 *   <li><code>title</code> - Task title (1-200 characters)
 *   <li><code>description</code> - Task description (optional, max 2000 characters)
 *   <li><code>category</code> - Category this task belongs to (nested DTO)
 *   <li><code>priority</code> - Priority level (HIGH, MEDIUM, LOW)
 *   <li><code>completed</code> - Whether the task is completed
 *   <li><code>completedAt</code> - Timestamp when task was completed (null if not completed)
 *   <li><code>createdAt</code> - Timestamp when the task was created
 *   <li><code>updatedAt</code> - Timestamp when the task was last updated
 * </ul>
 *
 * <p><b>Usage Example:</b>
 *
 * <pre>{@code
 * Task task = taskRepository.findById(id);
 * TaskResponseDTO dto = TaskResponseDTO.from(task);
 * return Response.ok(dto).build();
 * }</pre>
 *
 * <p><b>JSON Serialization:</b> Null fields are omitted from JSON output using
 * {@code JsonInclude.Include.NON_NULL}.
 *
 * @param id the unique task identifier
 * @param title the task title
 * @param description the task description (optional)
 * @param category the category this task belongs to
 * @param priority the priority level
 * @param completed whether the task is completed
 * @param completedAt the completion timestamp (null if not completed)
 * @param createdAt the creation timestamp
 * @param updatedAt the last update timestamp
 */
@RegisterForReflection
@JsonInclude(JsonInclude.Include.NON_NULL)
public record TaskResponseDTO(
    UUID id,
    String title,
    String description,
    CategoryResponseDTO category,
    Priority priority,
    boolean completed,
    LocalDateTime completedAt,
    LocalDateTime createdAt,
    LocalDateTime updatedAt) {

  /**
   * Creates a TaskResponseDTO from a Task entity.
   *
   * <p>This method maps the entity to a DTO, including the nested category information by calling
   * {@link CategoryResponseDTO#from(org.acme.taskmanager.model.Category)}.
   *
   * @param task the task entity to convert
   * @return a new TaskResponseDTO with all fields populated
   * @throws IllegalArgumentException if task is null
   */
  public static TaskResponseDTO from(final Task task) {
    if (task == null) {
      throw new IllegalArgumentException("Task cannot be null");
    }

    return new TaskResponseDTO(
        task.getId(),
        task.getTitle(),
        task.getDescription(),
        CategoryResponseDTO.from(task.getCategory()),
        task.getPriority(),
        task.isCompleted(),
        task.getCompletedAt(),
        task.getCreatedAt(),
        task.getUpdatedAt());
  }
}
