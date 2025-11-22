package org.acme.taskmanager.resource;

import io.vertx.ext.web.RoutingContext;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.acme.taskmanager.dto.CategoryCreateDTO;
import org.acme.taskmanager.dto.CategoryResponseDTO;
import org.acme.taskmanager.dto.CategoryUpdateDTO;
import org.acme.taskmanager.model.Category;
import org.acme.taskmanager.service.CategoryService;
import org.acme.taskmanager.session.SessionUtils;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import java.net.URI;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * REST resource for category management operations.
 *
 * <p>This resource provides RESTful endpoints for managing task categories. Categories are
 * user-specific and include both default categories (Work, Personal, Shopping) and custom
 * user-created categories. All operations require an authenticated user session.
 *
 * <p><b>Endpoints:</b>
 *
 * <ul>
 *   <li>GET /api/categories - List all categories for the authenticated user
 *   <li>POST /api/categories - Create a new category
 *   <li>GET /api/categories/{id} - Get a specific category
 *   <li>PUT /api/categories/{id} - Update a category
 *   <li>DELETE /api/categories/{id} - Delete a category
 * </ul>
 *
 * <p><b>Default Categories:</b> New users automatically receive three default categories:
 *
 * <ul>
 *   <li>Work (blue) - For work-related tasks
 *   <li>Personal (green) - For personal tasks
 *   <li>Shopping (amber) - For shopping lists
 * </ul>
 *
 * <p><b>Authentication:</b> All endpoints require a valid user session. Unauthenticated requests
 * will receive a 401 Unauthorized response.
 */
@Path("/api/categories")
@Produces(MediaType.APPLICATION_JSON)
@Tag(name = "Categories", description = "Category management")
public class CategoryResource {

  @Inject private CategoryService categoryService;

  @Context private RoutingContext routingContext;

  /**
   * Retrieves all categories for the authenticated user.
   *
   * <p>This endpoint returns a list of all categories belonging to the current user, including both
   * default categories (Work, Personal, Shopping) and any custom categories they've created.
   * Categories are returned ordered by name alphabetically.
   *
   * <p><b>Response Fields:</b> Each category includes:
   *
   * <ul>
   *   <li><code>id</code> - Unique category UUID
   *   <li><code>name</code> - Category name
   *   <li><code>colorCode</code> - Hex color code for UI display (e.g., "#3B82F6")
   *   <li><code>isDefault</code> - Whether this is a default category (cannot be deleted)
   *   <li><code>createdAt</code> - Timestamp when the category was created
   *   <li><code>taskCount</code> - Number of tasks in this category
   * </ul>
   *
   * <p><b>Example Response:</b>
   *
   * <pre>
   * [
   *   {
   *     "id": "550e8400-e29b-41d4-a716-446655440000",
   *     "name": "Personal",
   *     "colorCode": "#10B981",
   *     "isDefault": true,
   *     "createdAt": "2025-11-22T10:30:00",
   *     "taskCount": 5
   *   },
   *   {
   *     "id": "660e8400-e29b-41d4-a716-446655440001",
   *     "name": "Shopping",
   *     "colorCode": "#F59E0B",
   *     "isDefault": true,
   *     "createdAt": "2025-11-22T10:30:00",
   *     "taskCount": 2
   *   }
   * ]
   * </pre>
   *
   * @return Response with 200 OK and list of CategoryResponseDTO, ordered by name alphabetically
   */
  @GET
  @Operation(
      summary = "List user's categories",
      description = "Retrieve all categories for the authenticated user")
  @APIResponse(
      responseCode = "200",
      description = "List of categories",
      content =
          @Content(
              mediaType = MediaType.APPLICATION_JSON,
              schema =
                  @Schema(implementation = CategoryResponseDTO.class, type = SchemaType.ARRAY)))
  @APIResponse(responseCode = "401", description = "Not authenticated")
  public Response getCategories() {
    // Extract authenticated user ID from session
    String userId = SessionUtils.getCurrentUserId(routingContext);

    // Retrieve categories from service
    List<Category> categories = categoryService.getAllCategoriesByUser(userId);

    // Convert entities to DTOs
    List<CategoryResponseDTO> response =
        categories.stream().map(CategoryResponseDTO::from).collect(Collectors.toList());

    return Response.ok(response).build();
  }

  /**
   * Creates a new category for the authenticated user.
   *
   * <p>T374-T378: Creates a custom category with a unique name and color code. The category
   * is automatically associated with the authenticated user.
   *
   * @param dto the category creation data (name and colorCode)
   * @return Response with 201 Created and the created CategoryResponseDTO
   */
  @POST
  @Consumes(MediaType.APPLICATION_JSON)
  @Operation(
      summary = "Create a new category",
      description = "Create a custom category with a unique name and color code")
  @RequestBody(
      required = true,
      content = @Content(
          mediaType = MediaType.APPLICATION_JSON,
          schema = @Schema(implementation = CategoryCreateDTO.class)))
  @APIResponse(
      responseCode = "201",
      description = "Category created successfully",
      content = @Content(
          mediaType = MediaType.APPLICATION_JSON,
          schema = @Schema(implementation = CategoryResponseDTO.class)))
  @APIResponse(responseCode = "400", description = "Invalid input")
  @APIResponse(responseCode = "401", description = "Not authenticated")
  @APIResponse(responseCode = "409", description = "Category name already exists")
  public Response createCategory(@Valid final CategoryCreateDTO dto) {
    // T376: Extract userId from session
    String userId = SessionUtils.getCurrentUserId(routingContext);

    // T377: Call CategoryService.createCategory() and return 201 Created
    CategoryResponseDTO created = categoryService.createCategory(userId, dto);

    return Response.created(URI.create("/api/categories/" + created.id()))
        .entity(created)
        .build();
  }

  /**
   * Retrieves a specific category by ID.
   *
   * <p>T379-T382: Fetches a single category belonging to the authenticated user.
   *
   * @param id the category UUID
   * @return Response with 200 OK and the CategoryResponseDTO
   */
  @GET
  @Path("/{id}")
  @Operation(
      summary = "Get category by ID",
      description = "Retrieve a specific category belonging to the authenticated user")
  @Parameter(name = "id", description = "Category UUID", required = true)
  @APIResponse(
      responseCode = "200",
      description = "Category found",
      content = @Content(
          mediaType = MediaType.APPLICATION_JSON,
          schema = @Schema(implementation = CategoryResponseDTO.class)))
  @APIResponse(responseCode = "401", description = "Not authenticated")
  @APIResponse(responseCode = "404", description = "Category not found")
  public Response getCategoryById(@PathParam("id") final UUID id) {
    // T380: Extract userId from session
    String userId = SessionUtils.getCurrentUserId(routingContext);

    // T381: Call CategoryService.getCategoryById() and return 200 OK
    CategoryResponseDTO category = categoryService.getCategoryById(userId, id);

    return Response.ok(category).build();
  }

  /**
   * Updates an existing category.
   *
   * <p>T383-T387: Updates a category's name and/or color code. Uses PATCH semantics - only
   * non-null fields in the DTO will be updated.
   *
   * @param id the category UUID
   * @param dto the update data (name and/or colorCode)
   * @return Response with 200 OK and the updated CategoryResponseDTO
   */
  @PUT
  @Path("/{id}")
  @Consumes(MediaType.APPLICATION_JSON)
  @Operation(
      summary = "Update a category",
      description = "Update a category's name and/or color code (PATCH semantics)")
  @Parameter(name = "id", description = "Category UUID", required = true)
  @RequestBody(
      required = true,
      content = @Content(
          mediaType = MediaType.APPLICATION_JSON,
          schema = @Schema(implementation = CategoryUpdateDTO.class)))
  @APIResponse(
      responseCode = "200",
      description = "Category updated successfully",
      content = @Content(
          mediaType = MediaType.APPLICATION_JSON,
          schema = @Schema(implementation = CategoryResponseDTO.class)))
  @APIResponse(responseCode = "400", description = "Invalid input")
  @APIResponse(responseCode = "401", description = "Not authenticated")
  @APIResponse(responseCode = "404", description = "Category not found")
  @APIResponse(responseCode = "409", description = "Category name already exists")
  public Response updateCategory(@PathParam("id") final UUID id,
                                  @Valid final CategoryUpdateDTO dto) {
    // T385: Extract userId from session
    String userId = SessionUtils.getCurrentUserId(routingContext);

    // T386: Call CategoryService.updateCategory() and return 200 OK
    CategoryResponseDTO updated = categoryService.updateCategory(userId, id, dto);

    return Response.ok(updated).build();
  }

  /**
   * Deletes a category.
   *
   * <p>T388-T391: Deletes a non-default category. Default categories (Work, Personal, Shopping)
   * cannot be deleted and will return a 400 Bad Request error.
   *
   * @param id the category UUID
   * @return Response with 204 No Content
   */
  @DELETE
  @Path("/{id}")
  @Operation(
      summary = "Delete a category",
      description = "Delete a non-default category. Default categories cannot be deleted.")
  @Parameter(name = "id", description = "Category UUID", required = true)
  @APIResponse(responseCode = "204", description = "Category deleted successfully")
  @APIResponse(responseCode = "400", description = "Cannot delete default category")
  @APIResponse(responseCode = "401", description = "Not authenticated")
  @APIResponse(responseCode = "404", description = "Category not found")
  public Response deleteCategory(@PathParam("id") final UUID id) {
    // T389: Extract userId from session
    String userId = SessionUtils.getCurrentUserId(routingContext);

    // T390: Call CategoryService.deleteCategory() and return 204 No Content
    categoryService.deleteCategory(userId, id);

    return Response.noContent().build();
  }
}
