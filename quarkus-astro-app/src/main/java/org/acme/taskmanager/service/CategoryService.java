package org.acme.taskmanager.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.acme.taskmanager.dto.CategoryCreateDTO;
import org.acme.taskmanager.dto.CategoryResponseDTO;
import org.acme.taskmanager.dto.CategoryUpdateDTO;
import org.acme.taskmanager.exception.ConflictException;
import org.acme.taskmanager.exception.ResourceNotFoundException;
import org.acme.taskmanager.exception.ValidationException;
import org.acme.taskmanager.model.Category;
import org.acme.taskmanager.repository.CategoryRepository;
import org.jboss.logging.Logger;

import java.util.List;
import java.util.UUID;

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

  /**
   * Creates a new category for a user.
   *
   * <p>T360-T364: Validates category name uniqueness per user, creates the category entity,
   * and returns the DTO representation.
   *
   * @param userId the user's unique identifier
   * @param dto the category creation data
   * @return CategoryResponseDTO containing the created category
   * @throws IllegalArgumentException if userId is null or blank
   * @throws ValidationException if a category with the same name already exists for this user
   */
  @Transactional
  public CategoryResponseDTO createCategory(final String userId, final CategoryCreateDTO dto) {
    if (userId == null || userId.isBlank()) {
      throw new IllegalArgumentException("User ID cannot be null or blank");
    }

    // T361-T362: Validate name uniqueness per user
    boolean nameExists = categoryRepository.existsByUserIdAndName(userId, dto.name());
    if (nameExists) {
      throw new ConflictException("A category with name '" + dto.name() + "' already exists");
    }

    // T363: Create category entity, set userId, isDefault=false
    Category category = new Category(dto.name(), dto.colorCode(), false, userId);
    categoryRepository.persist(category);
    categoryRepository.flush(); // Ensure createdAt is set by @CreationTimestamp

    LOG.infof("Created category '%s' for user: %s", dto.name(), userId);

    // T364: Return CategoryResponseDTO
    return CategoryResponseDTO.from(category);
  }

  /**
   * Retrieves a single category by ID.
   *
   * <p>T365-T366: Validates the category exists and belongs to the requesting user.
   *
   * @param userId the user's unique identifier
   * @param categoryId the category's unique identifier
   * @return CategoryResponseDTO containing the category details
   * @throws IllegalArgumentException if userId is null or blank
   * @throws ResourceNotFoundException if category not found or doesn't belong to user
   */
  public CategoryResponseDTO getCategoryById(final String userId, final UUID categoryId) {
    if (userId == null || userId.isBlank()) {
      throw new IllegalArgumentException("User ID cannot be null or blank");
    }

    // T366: Find category and validate ownership
    Category category = categoryRepository.findByIdOptional(categoryId)
        .orElseThrow(() -> new ResourceNotFoundException("Category not found"));

    if (!category.getUserId().equals(userId)) {
      throw new ResourceNotFoundException("Category not found");
    }

    return CategoryResponseDTO.from(category);
  }

  /**
   * Updates an existing category.
   *
   * <p>T367-T370: Validates category exists and belongs to user, checks name uniqueness if name
   * is being changed, and updates only non-null fields from the DTO (PATCH semantics).
   *
   * @param userId the user's unique identifier
   * @param categoryId the category's unique identifier
   * @param dto the update data (only non-null fields will be updated)
   * @return CategoryResponseDTO containing the updated category
   * @throws IllegalArgumentException if userId is null or blank
   * @throws ResourceNotFoundException if category not found or doesn't belong to user
   * @throws ValidationException if name is being changed to a duplicate name
   */
  @Transactional
  public CategoryResponseDTO updateCategory(
      final String userId, final UUID categoryId, final CategoryUpdateDTO dto) {
    if (userId == null || userId.isBlank()) {
      throw new IllegalArgumentException("User ID cannot be null or blank");
    }

    // T368: Validate category exists and belongs to user
    Category category = categoryRepository.findByIdOptional(categoryId)
        .orElseThrow(() -> new ResourceNotFoundException("Category not found"));

    if (!category.getUserId().equals(userId)) {
      throw new ResourceNotFoundException("Category not found");
    }

    // T369: Validate name uniqueness if name is being updated
    if (dto.name() != null && !dto.name().equals(category.getName())) {
      boolean nameExists = categoryRepository.existsByUserIdAndName(userId, dto.name());
      if (nameExists) {
        throw new ConflictException("A category with name '" + dto.name() + "' already exists");
      }
    }

    // T370: Update only non-null fields
    if (dto.name() != null) {
      category.setName(dto.name());
    }
    if (dto.colorCode() != null) {
      category.setColorCode(dto.colorCode());
    }

    categoryRepository.persist(category);

    LOG.infof("Updated category %s for user: %s", categoryId, userId);

    return CategoryResponseDTO.from(category);
  }

  /**
   * Deletes a category.
   *
   * <p>T371-T373: Validates category exists, belongs to user, and is not a default category
   * (canDelete() must return true).
   *
   * @param userId the user's unique identifier
   * @param categoryId the category's unique identifier
   * @throws IllegalArgumentException if userId is null or blank
   * @throws ResourceNotFoundException if category not found or doesn't belong to user
   * @throws ValidationException if attempting to delete a default category
   */
  @Transactional
  public void deleteCategory(final String userId, final UUID categoryId) {
    if (userId == null || userId.isBlank()) {
      throw new IllegalArgumentException("User ID cannot be null or blank");
    }

    // T372: Validate category exists, belongs to user, and canDelete() returns true
    Category category = categoryRepository.findByIdOptional(categoryId)
        .orElseThrow(() -> new ResourceNotFoundException("Category not found"));

    if (!category.getUserId().equals(userId)) {
      throw new ResourceNotFoundException("Category not found");
    }

    // T373: Throw ValidationException if attempting to delete default category
    if (!category.canDelete()) {
      throw new ValidationException("Cannot delete default categories");
    }

    categoryRepository.delete(category);

    LOG.infof("Deleted category %s for user: %s", categoryId, userId);
  }
}
