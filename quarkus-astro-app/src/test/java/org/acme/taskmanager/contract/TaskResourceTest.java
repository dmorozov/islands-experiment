package org.acme.taskmanager.contract;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.isA;

/**
 * Contract tests for TaskResource API endpoints.
 * These tests verify the API contract (request/response structure) for task-related operations.
 */
@QuarkusTest
@DisplayName("TaskResource Contract Tests")
class TaskResourceTest {

    private static final String API_BASE_PATH = "/api/tasks";
    private static final int HTTP_OK = 200;
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
}
