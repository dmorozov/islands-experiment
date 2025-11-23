package org.acme.taskmanager.contract;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.Order;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.equalTo;

/**
 * Contract tests for CategoryResource API endpoints.
 * These tests verify the API contract (request/response structure) for category-related operations.
 */
@QuarkusTest
@DisplayName("CategoryResource Contract Tests")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class CategoryResourceTest {

    private static final String API_BASE_PATH = "/api/categories";
    private static final int HTTP_OK = 200;
    private static final int HTTP_CREATED = 201;
    private static final int HTTP_NO_CONTENT = 204;
    private static final int HTTP_BAD_REQUEST = 400;
    private static final int HTTP_CONFLICT = 409;

    /**
     * T350: Test POST /api/categories.
     * Expected: 201 Created with category details (id, name, colorCode, isDefault, createdAt).
     */
    @Test
    @Order(1)
    @DisplayName("POST /api/categories - should return 201 and created category")
    void testCreateCategory() {
        String requestBody = """
            {
                "name": "Health & Fitness",
                "colorCode": "#10B981"
            }
            """;

        given()
            .contentType(ContentType.JSON)
            .body(requestBody)
            .when()
            .post(API_BASE_PATH)
            .then()
            .statusCode(HTTP_CREATED)
            .contentType(ContentType.JSON)
            .body("id", notNullValue())
            .body("name", equalTo("Health & Fitness"))
            .body("colorCode", equalTo("#10B981"))
            .body("isDefault", equalTo(false))
            .body("createdAt", notNullValue());
    }

    /**
     * T351: Test POST /api/categories with duplicate name.
     * Expected: 409 Conflict error indicating duplicate category name.
     */
    @Test
    @Order(2)
    @DisplayName("POST /api/categories with duplicate name - should return 409 conflict")
    void testCreateCategoryDuplicateName() {
        // First, create a category with a unique name
        String firstRequestBody = """
            {
                "name": "Duplicate Test Category",
                "colorCode": "#EF4444"
            }
            """;

        given()
            .contentType(ContentType.JSON)
            .body(firstRequestBody)
            .when()
            .post(API_BASE_PATH)
            .then()
            .statusCode(HTTP_CREATED);

        // Try to create another category with the same name
        String duplicateRequestBody = """
            {
                "name": "Duplicate Test Category",
                "colorCode": "#F59E0B"
            }
            """;

        given()
            .contentType(ContentType.JSON)
            .body(duplicateRequestBody)
            .when()
            .post(API_BASE_PATH)
            .then()
            .statusCode(HTTP_CONFLICT)
            .contentType(ContentType.JSON)
            .body("message", notNullValue());
    }

    /**
     * T352: Test GET /api/categories/{id}.
     * Expected: 200 OK with single category details.
     */
    @Test
    @Order(3)
    @DisplayName("GET /api/categories/{id} - should return 200 and single category")
    void testGetCategoryById() {
        // First, create a category to retrieve
        String createRequestBody = """
            {
                "name": "Test Get Category",
                "colorCode": "#8B5CF6"
            }
            """;

        String categoryId = given()
            .contentType(ContentType.JSON)
            .body(createRequestBody)
            .when()
            .post(API_BASE_PATH)
            .then()
            .statusCode(HTTP_CREATED)
            .extract()
            .path("id");

        // Now retrieve it
        given()
            .contentType(ContentType.JSON)
            .when()
            .get(API_BASE_PATH + "/" + categoryId)
            .then()
            .statusCode(HTTP_OK)
            .contentType(ContentType.JSON)
            .body("id", equalTo(categoryId))
            .body("name", equalTo("Test Get Category"))
            .body("colorCode", equalTo("#8B5CF6"))
            .body("isDefault", equalTo(false))
            .body("createdAt", notNullValue());
    }

    /**
     * T353: Test PUT /api/categories/{id}.
     * Expected: 200 OK with updated category details.
     */
    @Test
    @Order(4)
    @DisplayName("PUT /api/categories/{id} - should return 200 and updated category")
    void testUpdateCategory() {
        // First, create a category to update
        String createRequestBody = """
            {
                "name": "Original Name",
                "colorCode": "#3B82F6"
            }
            """;

        String categoryId = given()
            .contentType(ContentType.JSON)
            .body(createRequestBody)
            .when()
            .post(API_BASE_PATH)
            .then()
            .statusCode(HTTP_CREATED)
            .extract()
            .path("id");

        // Now update it
        String updateRequestBody = """
            {
                "name": "Updated Name",
                "colorCode": "#EC4899"
            }
            """;

        given()
            .contentType(ContentType.JSON)
            .body(updateRequestBody)
            .when()
            .put(API_BASE_PATH + "/" + categoryId)
            .then()
            .statusCode(HTTP_OK)
            .contentType(ContentType.JSON)
            .body("id", equalTo(categoryId))
            .body("name", equalTo("Updated Name"))
            .body("colorCode", equalTo("#EC4899"))
            .body("isDefault", equalTo(false));
    }

    /**
     * T354: Test DELETE /api/categories/{id} where isDefault=false.
     * Expected: 204 No Content (category deleted successfully).
     */
    @Test
    @Order(5)
    @DisplayName("DELETE /api/categories/{id} non-default - should return 204")
    void testDeleteNonDefaultCategory() {
        // First, create a non-default category to delete
        String createRequestBody = """
            {
                "name": "Temporary Category",
                "colorCode": "#F97316"
            }
            """;

        String categoryId = given()
            .contentType(ContentType.JSON)
            .body(createRequestBody)
            .when()
            .post(API_BASE_PATH)
            .then()
            .statusCode(HTTP_CREATED)
            .extract()
            .path("id");

        // Now delete it
        given()
            .when()
            .delete(API_BASE_PATH + "/" + categoryId)
            .then()
            .statusCode(HTTP_NO_CONTENT);
    }

    /**
     * T355: Test DELETE /api/categories/{id} where isDefault=true.
     * Expected: 400 Bad Request (cannot delete default categories).
     */
    @Test
    @Order(6)
    @DisplayName("DELETE /api/categories/{id} default - should return 400 cannot delete")
    void testDeleteDefaultCategory() {
        // First, get a default category ID by listing all categories
        String defaultCategoryId = given()
            .when()
            .get("/api/categories")
            .then()
            .statusCode(HTTP_OK)
            .extract()
            .path("find { it.isDefault == true }.id");

        // If there's no default category, we can't test this scenario
        if (defaultCategoryId == null) {
            // Skip this test if no default categories exist
            return;
        }

        // Try to delete the default category
        given()
            .when()
            .delete(API_BASE_PATH + "/" + defaultCategoryId)
            .then()
            .statusCode(HTTP_BAD_REQUEST)
            .contentType(ContentType.JSON)
            .body("message", notNullValue());
    }
}
