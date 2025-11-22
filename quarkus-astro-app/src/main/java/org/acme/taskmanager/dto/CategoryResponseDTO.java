package org.acme.taskmanager.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.quarkus.runtime.annotations.RegisterForReflection;
import org.acme.taskmanager.model.Category;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Data Transfer Object for Category API responses.
 *
 * <p>This record represents a category in API responses, including computed fields like task count.
 * It provides a clean separation between the internal entity model and the external API contract.
 *
 * <p><b>Fields:</b>
 *
 * <ul>
 *   <li><code>id</code> - Unique category identifier (UUID)
 *   <li><code>name</code> - Category name (1-50 characters)
 *   <li><code>colorCode</code> - Hex color code for UI display (e.g., "#FF5733")
 *   <li><code>isDefault</code> - Whether this is a system default category (cannot be deleted)
 *   <li><code>createdAt</code> - Timestamp when the category was created
 *   <li><code>taskCount</code> - Number of tasks in this category (computed field)
 * </ul>
 *
 * <p><b>Usage Example:</b>
 *
 * <pre>{@code
 * Category category = categoryRepository.findById(id);
 * CategoryResponseDTO dto = CategoryResponseDTO.from(category);
 * return Response.ok(dto).build();
 * }</pre>
 *
 * <p><b>JSON Serialization:</b> Null fields are omitted from JSON output using
 * {@code JsonInclude.Include.NON_NULL}.
 *
 * @param id the unique category identifier
 * @param name the category name
 * @param colorCode the hex color code for UI display (optional)
 * @param isDefault whether this is a default category
 * @param createdAt the creation timestamp
 * @param taskCount the number of tasks in this category
 */
@RegisterForReflection
@JsonInclude(JsonInclude.Include.NON_NULL)
public record CategoryResponseDTO(
    UUID id,
    String name,
    String colorCode,
    boolean isDefault,
    LocalDateTime createdAt,
    Long taskCount) {

  /**
   * Creates a CategoryResponseDTO from a Category entity.
   *
   * <p>This method maps the entity to a DTO, computing the task count from the entity's task
   * collection.
   *
   * @param category the category entity to convert
   * @return a new CategoryResponseDTO with all fields populated
   * @throws IllegalArgumentException if category is null
   */
  public static CategoryResponseDTO from(final Category category) {
    if (category == null) {
      throw new IllegalArgumentException("Category cannot be null");
    }

    return new CategoryResponseDTO(
        category.getId(),
        category.getName(),
        category.getColorCode(),
        category.isDefault(),
        category.getCreatedAt(),
        (long) category.getTasks().size());
  }
}
