package org.acme.taskmanager.resource;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;

import java.util.Collections;
import java.util.List;

/**
 * REST resource for task management operations.
 * Handles CRUD operations and filtering for tasks.
 */
@Path("/api/tasks")
@Produces(MediaType.APPLICATION_JSON)
public class TaskResource {

    /**
     * Get all tasks with optional filtering.
     *
     * @param status filter by status (active/completed)
     * @param category filter by category ID
     * @param priority filter by priority level
     * @param page page number for pagination
     * @param size page size for pagination
     * @return list of tasks (currently empty as implementation is pending)
     */
    @GET
    public List<Object> getAllTasks(
            @QueryParam("status") final String status,
            @QueryParam("category") final String category,
            @QueryParam("priority") final String priority,
            @QueryParam("page") final Integer page,
            @QueryParam("size") final Integer size) {
        // Minimal implementation to pass contract tests
        // TODO: Implement actual task retrieval logic
        return Collections.emptyList();
    }
}
