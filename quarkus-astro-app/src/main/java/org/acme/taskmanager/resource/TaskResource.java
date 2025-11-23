package org.acme.taskmanager.resource;

import io.vertx.ext.web.RoutingContext;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.DefaultValue;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.PATCH;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.acme.taskmanager.dto.TaskCreateDTO;
import org.acme.taskmanager.dto.TaskResponseDTO;
import org.acme.taskmanager.dto.TaskUpdateDTO;
import org.acme.taskmanager.model.Priority;
import org.acme.taskmanager.model.Task;
import org.acme.taskmanager.service.TaskService;
import org.acme.taskmanager.session.SessionUtils;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.enums.ParameterIn;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * REST resource for task management operations.
 *
 * <p>
 * This resource provides RESTful endpoints for managing tasks, including retrieval with flexible
 * filtering and pagination. All operations require an authenticated user session.
 *
 * <p>
 * <b>Endpoints:</b>
 *
 * <ul>
 * <li>GET /api/tasks - List tasks with optional filtering
 * <li>POST /api/tasks - Create a new task
 * <li>GET /api/tasks/{id} - Get a single task by ID
 * <li>PUT /api/tasks/{id} - Update an existing task
 * <li>DELETE /api/tasks/{id} - Delete a task
 * </ul>
 *
 * <p>
 * <b>Authentication:</b> All endpoints require a valid user session. Unauthenticated requests
 * will receive a 401 Unauthorized response.
 */
@Path("/api/tasks")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Tasks", description = "Task CRUD operations")
public class TaskResource {

  @Inject
  private TaskService taskService;

  @Context
  private RoutingContext routingContext;

  private static final int MAX_PAGE_SIZE = 100;

  /**
   * Retrieves tasks for the authenticated user with optional filtering and pagination.
   *
   * <p>
   * This endpoint returns a list of tasks matching the provided filter criteria. All filters
   * are optional - omitting a filter parameter means that filter is not applied.
   *
   * <p>
   * <b>Query Parameters:</b>
   *
   * <ul>
   * <li><code>category</code> - Filter by category UUID (optional)
   * <li><code>priority</code> - Filter by priority level: HIGH, MEDIUM, or LOW (optional)
   * <li><code>status</code> - Filter by completion status: "active" or "completed" (optional)
   * <li><code>page</code> - Page number for pagination (0-based, default: 0)
   * <li><code>size</code> - Number of items per page (1-100, default: 20)
   * </ul>
   *
   * <p>
   * <b>Example Requests:</b>
   *
   * <pre>
   * GET /api/tasks                              - All tasks (first page)
   * GET /api/tasks?status=active                - Only active tasks
   * GET /api/tasks?priority=HIGH&status=active  - High priority active tasks
   * GET /api/tasks?page=1&size=50               - Second page with 50 items
   * </pre>
   *
   * @param category filter by category UUID (optional)
   * @param priority filter by priority level: HIGH, MEDIUM, or LOW (optional)
   * @param status filter by completion status: "active" or "completed" (optional)
   * @param page page number for pagination (0-based, default: 0)
   * @param size number of items per page (1-100, default: 20)
   * @return Response with 200 OK and list of TaskResponseDTO, ordered by creation date descending
   */
  @GET
  @Operation(
      summary = "List user's tasks",
      description = "Retrieve tasks with optional filtering by category, priority, and status")
  @APIResponse(
      responseCode = "200",
      description = "List of tasks",
      content = @Content(
          mediaType = MediaType.APPLICATION_JSON,
          schema = @Schema(implementation = TaskResponseDTO.class, type = SchemaType.ARRAY)))
  @APIResponse(responseCode = "401", description = "Not authenticated")
  public Response getAllTasks(
      @Parameter(
          description = "Filter by category UUID",
          in = ParameterIn.QUERY,
          schema = @Schema(type = SchemaType.STRING,
              format = "uuid")) @QueryParam("category") final String category,
      @Parameter(
          description = "Filter by priority level",
          in = ParameterIn.QUERY,
          schema = @Schema(
              implementation = Priority.class)) @QueryParam("priority") final String priority,
      @Parameter(
          description = "Filter by completion status",
          in = ParameterIn.QUERY,
          schema = @Schema(type = SchemaType.STRING,
              enumeration = {"active", "completed"})) @QueryParam("status") final String status,
      @Parameter(
          description = "Page number (0-based)",
          in = ParameterIn.QUERY,
          schema = @Schema(type = SchemaType.INTEGER, minimum = "0",
              defaultValue = "0")) @QueryParam("page") @DefaultValue("0") final int page,
      @Parameter(
          description = "Page size",
          in = ParameterIn.QUERY,
          schema = @Schema(
              type = SchemaType.INTEGER,
              minimum = "1",
              maximum = "100",
              defaultValue = "20")) @QueryParam("size") @DefaultValue("20") final int size) {

    // Extract authenticated user ID from session
    String userId = SessionUtils.getCurrentUserId(routingContext);

    // Validate pagination parameters
    if (page < 0) {
      throw new IllegalArgumentException("Page number must be >= 0");
    }

    if (size < 1 || size > MAX_PAGE_SIZE) {
      throw new IllegalArgumentException("Page size must be between 1 and " + MAX_PAGE_SIZE);
    }

    // Parse priority if provided
    Priority priorityEnum = null;
    if (priority != null && !priority.isBlank()) {
      try {
        priorityEnum = Priority.valueOf(priority.toUpperCase());
      } catch (IllegalArgumentException e) {
        throw new IllegalArgumentException(
            "Invalid priority value. Must be HIGH, MEDIUM, or LOW", e);
      }
    }

    // Parse status to boolean if provided
    Boolean completed = null;
    if (status != null && !status.isBlank()) {
      if ("active".equalsIgnoreCase(status)) {
        completed = false;
      } else if ("completed".equalsIgnoreCase(status)) {
        completed = true;
      } else {
        throw new IllegalArgumentException("Invalid status value. Must be 'active' or 'completed'");
      }
    }

    // Retrieve tasks from service
    List<Task> tasks =
        taskService.getAllTasks(userId, category, priorityEnum, completed, page, size);

    // Convert entities to DTOs
    List<TaskResponseDTO> response =
        tasks.stream().map(TaskResponseDTO::from).collect(Collectors.toList());

    return Response.ok(response).build();
  }

  /**
   * Creates a new task for the authenticated user.
   *
   * <p>T289-T293: Validates request body, extracts userId from session, and creates task.
   *
   * @param dto the task creation data containing title, description, categoryId, and priority
   * @return Response with 201 Created and the created TaskResponseDTO
   */
  @POST
  @Operation(
      summary = "Create a new task",
      description = "Creates a new task for the authenticated user")
  @APIResponse(
      responseCode = "201",
      description = "Task created successfully",
      content = @Content(
          mediaType = MediaType.APPLICATION_JSON,
          schema = @Schema(implementation = TaskResponseDTO.class)))
  @APIResponse(responseCode = "400", description = "Invalid request body or validation error")
  @APIResponse(responseCode = "401", description = "Not authenticated")
  public Response createTask(@Valid final TaskCreateDTO dto) {
    // T291: Extract userId from session
    String userId = SessionUtils.getCurrentUserId(routingContext);

    // T292: Call TaskService.createTask() and return 201 Created with TaskResponseDTO
    TaskResponseDTO response = taskService.createTask(userId, dto);

    return Response.status(Response.Status.CREATED).entity(response).build();
  }

  /**
   * Retrieves a single task by ID.
   *
   * <p>T294-T297: Extracts userId from session and retrieves task by ID.
   *
   * @param id the task's unique identifier
   * @return Response with 200 OK and TaskResponseDTO
   */
  @GET
  @Path("/{id}")
  @Operation(
      summary = "Get task by ID",
      description = "Retrieves a single task by its unique identifier")
  @APIResponse(
      responseCode = "200",
      description = "Task found",
      content = @Content(
          mediaType = MediaType.APPLICATION_JSON,
          schema = @Schema(implementation = TaskResponseDTO.class)))
  @APIResponse(responseCode = "404", description = "Task not found")
  @APIResponse(responseCode = "401", description = "Not authenticated")
  public Response getTaskById(
      @Parameter(
          description = "Task UUID",
          required = true,
          schema = @Schema(type = SchemaType.STRING, format = "uuid"))
      @PathParam("id") final UUID id) {

    // T295: Extract userId from session
    String userId = SessionUtils.getCurrentUserId(routingContext);

    // T296: Call TaskService.getTaskById() and return 200 OK with TaskResponseDTO
    TaskResponseDTO response = taskService.getTaskById(userId, id);

    return Response.ok(response).build();
  }

  /**
   * Updates an existing task.
   *
   * <p>T298-T302: Validates request body, extracts userId from session, and updates task.
   *
   * @param id the task's unique identifier
   * @param dto the task update data with optional fields
   * @return Response with 200 OK and updated TaskResponseDTO
   */
  @PUT
  @Path("/{id}")
  @Operation(
      summary = "Update a task",
      description = "Updates an existing task. All fields in the request body are optional.")
  @APIResponse(
      responseCode = "200",
      description = "Task updated successfully",
      content = @Content(
          mediaType = MediaType.APPLICATION_JSON,
          schema = @Schema(implementation = TaskResponseDTO.class)))
  @APIResponse(responseCode = "400", description = "Invalid request body or validation error")
  @APIResponse(responseCode = "404", description = "Task not found")
  @APIResponse(responseCode = "401", description = "Not authenticated")
  public Response updateTask(
      @Parameter(
          description = "Task UUID",
          required = true,
          schema = @Schema(type = SchemaType.STRING, format = "uuid"))
      @PathParam("id") final UUID id,
      @Valid final TaskUpdateDTO dto) {

    // T300: Extract userId from session
    String userId = SessionUtils.getCurrentUserId(routingContext);

    // T301: Call TaskService.updateTask() and return 200 OK with TaskResponseDTO
    TaskResponseDTO response = taskService.updateTask(userId, id, dto);

    return Response.ok(response).build();
  }

  /**
   * Deletes a task.
   *
   * <p>T303-T306: Extracts userId from session and deletes task.
   *
   * @param id the task's unique identifier
   * @return Response with 204 No Content
   */
  @DELETE
  @Path("/{id}")
  @Operation(
      summary = "Delete a task",
      description = "Deletes an existing task")
  @APIResponse(
      responseCode = "204",
      description = "Task deleted successfully")
  @APIResponse(responseCode = "404", description = "Task not found")
  @APIResponse(responseCode = "401", description = "Not authenticated")
  public Response deleteTask(
      @Parameter(
          description = "Task UUID",
          required = true,
          schema = @Schema(type = SchemaType.STRING, format = "uuid"))
      @PathParam("id") final UUID id) {

    // T304: Extract userId from session
    String userId = SessionUtils.getCurrentUserId(routingContext);

    // T305: Call TaskService.deleteTask() and return 204 No Content
    taskService.deleteTask(userId, id);

    return Response.noContent().build();
  }

  /**
   * Toggles the completion status of a task.
   *
   * <p>T457-T460: Toggles a task between complete and incomplete states.
   *
   * @param id the task's unique identifier
   * @return Response with 200 OK and updated TaskResponseDTO
   */
  @PATCH
  @Path("/{id}/complete")
  @Operation(
      summary = "Toggle task completion",
      description = "Toggles a task between complete and incomplete. "
                    + "If incomplete, marks as complete with timestamp. "
                    + "If complete, marks as incomplete and clears timestamp.")
  @APIResponse(
      responseCode = "200",
      description = "Task completion toggled successfully",
      content = @Content(
          mediaType = MediaType.APPLICATION_JSON,
          schema = @Schema(implementation = TaskResponseDTO.class)))
  @APIResponse(responseCode = "404", description = "Task not found")
  @APIResponse(responseCode = "401", description = "Not authenticated")
  public Response toggleTaskCompletion(
      @Parameter(
          description = "Task UUID",
          required = true,
          schema = @Schema(type = SchemaType.STRING, format = "uuid"))
      @PathParam("id") final UUID id) {

    // T458: Extract userId from session
    String userId = SessionUtils.getCurrentUserId(routingContext);

    // T459: Call TaskService.toggleTaskCompletion() and return 200 OK
    TaskResponseDTO response = taskService.toggleTaskCompletion(userId, id);

    return Response.ok(response).build();
  }
}
