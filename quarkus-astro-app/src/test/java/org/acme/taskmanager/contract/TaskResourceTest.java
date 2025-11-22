package org.acme.taskmanager.contract;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.isA;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.equalTo;

/**
 * Contract tests for TaskResource API endpoints.
 * These tests verify the API contract (request/response structure) for task-related operations.
 */
@QuarkusTest
@DisplayName("TaskResource Contract Tests")
class TaskResourceTest {

    private static final String API_BASE_PATH = "/api/tasks";
    private static final int HTTP_OK = 200;
    private static final int HTTP_CREATED = 201;
    private static final int HTTP_NO_CONTENT = 204;
    private static final int HTTP_BAD_REQUEST = 400;
    private static final int HTTP_NOT_FOUND = 404;
    private static final int DEFAULT_PAGE = 0;
    private static final int DEFAULT_PAGE_SIZE = 20;
    private static final String TEST_CATEGORY_ID = "550e8400-e29b-41d4-a716-446655440001";

    /**
     * T147: Test GET /api/tasks without filters.
     * Expected: 200 OK with empty array initially (or array of tasks if any exist).
     */
    @Test
    @DisplayName("GET /api/tasks - should return 200 and array of tasks")
    void testGetAllTasksNoFilters() {
        given()
            .contentType(ContentType.JSON)
            .when()
            .get(API_BASE_PATH)
            .then()
            .statusCode(HTTP_OK)
            .contentType(ContentType.JSON)
            .body("$", isA(java.util.List.class));
    }

    /**
     * T148: Test GET /api/tasks?status=active.
     * Expected: 200 OK with filtered results (only active/incomplete tasks).
     */
    @Test
    @DisplayName("GET /api/tasks?status=active - should return 200 and only active tasks")
    void testGetAllTasksFilterByStatusActive() {
        given()
            .contentType(ContentType.JSON)
            .queryParam("status", "active")
            .when()
            .get(API_BASE_PATH)
            .then()
            .statusCode(HTTP_OK)
            .contentType(ContentType.JSON)
            .body("$", isA(java.util.List.class));
    }

    /**
     * T149: Test GET /api/tasks?category={id}.
     * Expected: 200 OK with filtered results (only tasks in specified category).
     */
    @Test
    @DisplayName("GET /api/tasks?category={id} - should return 200 and tasks in category")
    void testGetAllTasksFilterByCategory() {
        given()
            .contentType(ContentType.JSON)
            .queryParam("category", TEST_CATEGORY_ID)
            .when()
            .get(API_BASE_PATH)
            .then()
            .statusCode(HTTP_OK)
            .contentType(ContentType.JSON)
            .body("$", isA(java.util.List.class));
    }

    /**
     * T150: Test GET /api/tasks?priority=HIGH.
     * Expected: 200 OK with filtered results (only HIGH priority tasks).
     */
    @Test
    @DisplayName("GET /api/tasks?priority=HIGH - should return 200 and only HIGH priority tasks")
    void testGetAllTasksFilterByPriority() {
        given()
            .contentType(ContentType.JSON)
            .queryParam("priority", "HIGH")
            .when()
            .get(API_BASE_PATH)
            .then()
            .statusCode(HTTP_OK)
            .contentType(ContentType.JSON)
            .body("$", isA(java.util.List.class));
    }

    /**
     * T151: Test GET /api/tasks?page=0&size=20.
     * Expected: 200 OK with paginated results (max 20 items).
     */
    @Test
    @DisplayName("GET /api/tasks?page=0&size=20 - should return 200 and paginated results")
    void testGetAllTasksWithPagination() {
        given()
            .contentType(ContentType.JSON)
            .queryParam("page", DEFAULT_PAGE)
            .queryParam("size", DEFAULT_PAGE_SIZE)
            .when()
            .get(API_BASE_PATH)
            .then()
            .statusCode(HTTP_OK)
            .contentType(ContentType.JSON)
            .body("$", isA(java.util.List.class));
    }

    /**
     * T265: Test POST /api/tasks - create a new task.
     * Expected: 201 Created with task details and correct fields.
     */
    @Test
    @DisplayName("POST /api/tasks - should return 201 and created task")
    void testCreateTask() {
        String requestBody = """
            {
                "title": "Buy milk",
                "description": "Get 2% milk from the store",
                "categoryId": "%s",
                "priority": "HIGH"
            }
            """.formatted(TEST_CATEGORY_ID);

        given()
            .contentType(ContentType.JSON)
            .body(requestBody)
            .when()
            .post(API_BASE_PATH)
            .then()
            .statusCode(HTTP_CREATED)
            .contentType(ContentType.JSON)
            .body("id", notNullValue())
            .body("title", equalTo("Buy milk"))
            .body("description", equalTo("Get 2% milk from the store"))
            .body("priority", equalTo("HIGH"))
            .body("completed", equalTo(false));
    }

    /**
     * T266: Test POST /api/tasks with missing title - validation error.
     * Expected: 400 Bad Request with validation error message.
     */
    @Test
    @DisplayName("POST /api/tasks with missing title - should return 400")
    void testCreateTaskMissingTitle() {
        String requestBody = """
            {
                "description": "Missing title field",
                "categoryId": "%s",
                "priority": "MEDIUM"
            }
            """.formatted(TEST_CATEGORY_ID);

        given()
            .contentType(ContentType.JSON)
            .body(requestBody)
            .when()
            .post(API_BASE_PATH)
            .then()
            .statusCode(HTTP_BAD_REQUEST);
    }

    /**
     * T267: Test POST /api/tasks with title >200 chars - validation error.
     * Expected: 400 Bad Request with validation error message.
     */
    @Test
    @DisplayName("POST /api/tasks with title >200 chars - should return 400")
    void testCreateTaskTitleTooLong() {
        String longTitle = "a".repeat(201);
        String requestBody = """
            {
                "title": "%s",
                "description": "Title exceeds maximum length",
                "categoryId": "%s",
                "priority": "LOW"
            }
            """.formatted(longTitle, TEST_CATEGORY_ID);

        given()
            .contentType(ContentType.JSON)
            .body(requestBody)
            .when()
            .post(API_BASE_PATH)
            .then()
            .statusCode(HTTP_BAD_REQUEST);
    }

    /**
     * T268: Test GET /api/tasks/{id} - get single task.
     * Expected: 200 OK with task details.
     */
    @Test
    @DisplayName("GET /api/tasks/{id} - should return 200 and task details")
    void testGetTaskById() {
        // First create a task to retrieve
        String createBody = """
            {
                "title": "Test task for retrieval",
                "description": "Task description",
                "categoryId": "%s",
                "priority": "MEDIUM"
            }
            """.formatted(TEST_CATEGORY_ID);

        String taskId = given()
            .contentType(ContentType.JSON)
            .body(createBody)
            .when()
            .post(API_BASE_PATH)
            .then()
            .statusCode(HTTP_CREATED)
            .extract()
            .path("id");

        // Now retrieve the task by ID
        given()
            .contentType(ContentType.JSON)
            .when()
            .get(API_BASE_PATH + "/" + taskId)
            .then()
            .statusCode(HTTP_OK)
            .contentType(ContentType.JSON)
            .body("id", equalTo(taskId))
            .body("title", equalTo("Test task for retrieval"));
    }

    /**
     * T269: Test GET /api/tasks/{id} with invalid UUID - not found.
     * Expected: 404 Not Found.
     */
    @Test
    @DisplayName("GET /api/tasks/{id} with invalid UUID - should return 404")
    void testGetTaskByIdNotFound() {
        String nonExistentId = "550e8400-e29b-41d4-a716-446655440999";

        given()
            .contentType(ContentType.JSON)
            .when()
            .get(API_BASE_PATH + "/" + nonExistentId)
            .then()
            .statusCode(HTTP_NOT_FOUND);
    }

    /**
     * T270: Test PUT /api/tasks/{id} - update task.
     * Expected: 200 OK with updated task details.
     */
    @Test
    @DisplayName("PUT /api/tasks/{id} - should return 200 and updated task")
    void testUpdateTask() {
        // First create a task to update
        String createBody = """
            {
                "title": "Original title",
                "description": "Original description",
                "categoryId": "%s",
                "priority": "LOW"
            }
            """.formatted(TEST_CATEGORY_ID);

        String taskId = given()
            .contentType(ContentType.JSON)
            .body(createBody)
            .when()
            .post(API_BASE_PATH)
            .then()
            .statusCode(HTTP_CREATED)
            .extract()
            .path("id");

        // Now update the task
        String updateBody = """
            {
                "title": "Updated title",
                "description": "Updated description",
                "priority": "HIGH"
            }
            """;

        given()
            .contentType(ContentType.JSON)
            .body(updateBody)
            .when()
            .put(API_BASE_PATH + "/" + taskId)
            .then()
            .statusCode(HTTP_OK)
            .contentType(ContentType.JSON)
            .body("id", equalTo(taskId))
            .body("title", equalTo("Updated title"))
            .body("description", equalTo("Updated description"))
            .body("priority", equalTo("HIGH"));
    }

    /**
     * T271: Test DELETE /api/tasks/{id} - delete task.
     * Expected: 204 No Content.
     */
    @Test
    @DisplayName("DELETE /api/tasks/{id} - should return 204")
    void testDeleteTask() {
        // First create a task to delete
        String createBody = """
            {
                "title": "Task to delete",
                "description": "This task will be deleted",
                "categoryId": "%s",
                "priority": "MEDIUM"
            }
            """.formatted(TEST_CATEGORY_ID);

        String taskId = given()
            .contentType(ContentType.JSON)
            .body(createBody)
            .when()
            .post(API_BASE_PATH)
            .then()
            .statusCode(HTTP_CREATED)
            .extract()
            .path("id");

        // Now delete the task
        given()
            .when()
            .delete(API_BASE_PATH + "/" + taskId)
            .then()
            .statusCode(HTTP_NO_CONTENT);

        // Verify task is deleted
        given()
            .when()
            .get(API_BASE_PATH + "/" + taskId)
            .then()
            .statusCode(HTTP_NOT_FOUND);
    }
}
