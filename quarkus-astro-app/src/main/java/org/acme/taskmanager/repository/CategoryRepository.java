package org.acme.taskmanager.repository;

import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;
import org.acme.taskmanager.model.Category;

import java.util.List;
import java.util.UUID;

/**
 * Repository for Category entity operations.
 *
 * <p>This repository provides data access methods for Category entities using Panache. It extends
 * PanacheRepositoryBase to leverage Quarkus's simplified persistence layer.
 *
 * <p><b>Key Features:</b>
 *
 * <ul>
 *   <li>UUID-based primary keys
 *   <li>User-scoped queries for multi-tenancy
 *   <li>Automatic transaction management via Quarkus
 * </ul>
 *
 * <p><b>Usage Example:</b>
 *
 * <pre>{@code
 * @Inject
 * CategoryRepository categoryRepository;
 *
 * // Find all categories for a user
 * List<Category> categories = categoryRepository.findByUserId(userId);
 *
 * // Count categories for a user
 * long count = categoryRepository.countByUserId(userId);
 * }</pre>
 */
@ApplicationScoped
public class CategoryRepository implements PanacheRepositoryBase<Category, UUID> {

  /**
   * Finds all categories belonging to a specific user.
   *
   * <p>Categories are returned in alphabetical order by name for consistent UI display.
   *
   * @param userId the user's unique identifier
   * @return list of categories for the user, ordered by name (empty if none found)
   * @throws IllegalArgumentException if userId is null or blank
   */
  public List<Category> findByUserId(final String userId) {
    if (userId == null || userId.isBlank()) {
      throw new IllegalArgumentException("User ID cannot be null or blank");
    }

    return find("userId = ?1 order by name", userId).list();
  }

  /**
   * Counts the total number of categories belonging to a specific user.
   *
   * <p>This is useful for checking if a user has any categories before seeding defaults.
   *
   * @param userId the user's unique identifier
   * @return the number of categories owned by the user (0 if none)
   * @throws IllegalArgumentException if userId is null or blank
   */
  public long countByUserId(final String userId) {
    if (userId == null || userId.isBlank()) {
      throw new IllegalArgumentException("User ID cannot be null or blank");
    }

    return count("userId = ?1", userId);
  }

  /**
   * Checks if a category with the given name exists for a specific user.
   *
   * <p>This is used to enforce unique category names per user.
   *
   * @param userId the user's unique identifier
   * @param name the category name to check
   * @return true if a category with this name exists for the user, false otherwise
   * @throws IllegalArgumentException if userId or name is null or blank
   */
  public boolean existsByUserIdAndName(final String userId, final String name) {
    if (userId == null || userId.isBlank()) {
      throw new IllegalArgumentException("User ID cannot be null or blank");
    }
    if (name == null || name.isBlank()) {
      throw new IllegalArgumentException("Category name cannot be null or blank");
    }

    return count("userId = ?1 and name = ?2", userId, name) > 0;
  }
}
