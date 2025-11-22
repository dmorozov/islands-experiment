package org.acme.taskmanager.resource;

import io.vertx.ext.web.RoutingContext;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.acme.taskmanager.dto.CategoryResponseDTO;
import org.acme.taskmanager.model.Category;
import org.acme.taskmanager.service.CategoryService;
import org.acme.taskmanager.session.SessionUtils;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import java.util.List;
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
}
