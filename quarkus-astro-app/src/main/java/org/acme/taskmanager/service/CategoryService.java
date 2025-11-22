package org.acme.taskmanager.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.acme.taskmanager.model.Category;
import org.acme.taskmanager.repository.CategoryRepository;
import org.jboss.logging.Logger;

import java.util.List;

/**
 * Service layer for Category business logic.
 *
 * <p>This service handles category-related operations including default category seeding and user
 * category retrieval. It encapsulates business rules and coordinates between the repository layer
 * and REST endpoints.
 *
 * <p><b>Key Responsibilities:</b>
 *
 * <ul>
 *   <li>Seed default categories for new users
 *   <li>Retrieve user-specific categories
 *   <li>Enforce business rules (e.g., cannot delete default categories)
 * </ul>
 *
 * <p><b>Usage Example:</b>
 *
 * <pre>{@code
 * @Inject
 * CategoryService categoryService;
 *
 * // Ensure user has default categories
 * categoryService.ensureDefaultCategories(userId);
 *
 * // Get all categories for user
 * List<Category> categories = categoryService.getAllCategoriesByUser(userId);
 * }</pre>
 */
@ApplicationScoped
public class CategoryService {

  private static final Logger LOG = Logger.getLogger(CategoryService.class);

  private static final String DEFAULT_WORK_NAME = "Work";
  private static final String DEFAULT_WORK_COLOR = "#3B82F6";

  private static final String DEFAULT_PERSONAL_NAME = "Personal";
  private static final String DEFAULT_PERSONAL_COLOR = "#10B981";

  private static final String DEFAULT_SHOPPING_NAME = "Shopping";
  private static final String DEFAULT_SHOPPING_COLOR = "#F59E0B";

  @Inject private CategoryRepository categoryRepository;

  /**
   * Ensures that default categories exist for a user.
   *
   * <p>This method checks if the user has any categories. If not, it creates three default
   * categories: Work (blue), Personal (green), and Shopping (amber). These categories are marked
   * as default and cannot be deleted by the user.
   *
   * <p><b>Default Categories:</b>
   *
   * <ul>
   *   <li><b>Work</b> - Blue (#3B82F6) - For work-related tasks
   *   <li><b>Personal</b> - Green (#10B981) - For personal tasks
   *   <li><b>Shopping</b> - Amber (#F59E0B) - For shopping lists
   * </ul>
   *
   * <p>This method is typically called after user login or registration to ensure a good initial
   * experience.
   *
   * @param userId the user's unique identifier
   * @throws IllegalArgumentException if userId is null or blank
   */
  @Transactional
  public void ensureDefaultCategories(final String userId) {
    if (userId == null || userId.isBlank()) {
      throw new IllegalArgumentException("User ID cannot be null or blank");
    }

    // Check if user already has categories
    long existingCount = categoryRepository.countByUserId(userId);

    if (existingCount == 0) {
      LOG.infof("Seeding default categories for user: %s", userId);

      // Create Work category
      Category work = new Category(DEFAULT_WORK_NAME, DEFAULT_WORK_COLOR, true, userId);
      categoryRepository.persist(work);

      // Create Personal category
      Category personal =
          new Category(DEFAULT_PERSONAL_NAME, DEFAULT_PERSONAL_COLOR, true, userId);
      categoryRepository.persist(personal);

      // Create Shopping category
      Category shopping =
          new Category(DEFAULT_SHOPPING_NAME, DEFAULT_SHOPPING_COLOR, true, userId);
      categoryRepository.persist(shopping);

      LOG.infof("Successfully created 3 default categories for user: %s", userId);
    }
  }

  /**
   * Retrieves all categories for a specific user.
   *
   * <p>Categories are returned in alphabetical order by name for consistent display in the UI.
   *
   * @param userId the user's unique identifier
   * @return list of categories ordered by name (empty if user has no categories)
   * @throws IllegalArgumentException if userId is null or blank
   */
  public List<Category> getAllCategoriesByUser(final String userId) {
    if (userId == null || userId.isBlank()) {
      throw new IllegalArgumentException("User ID cannot be null or blank");
    }

    return categoryRepository.findByUserId(userId);
  }
}
