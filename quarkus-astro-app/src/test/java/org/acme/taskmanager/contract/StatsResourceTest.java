package org.acme.taskmanager.contract;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

/**
 * Contract tests for StatsResource API endpoints.
 * T435-T436: These tests verify the API contract for completion statistics.
 */
@QuarkusTest
@DisplayName("StatsResource Contract Tests")
class StatsResourceTest {

    private static final String API_STATS_PATH = "/api/stats";
    private static final int HTTP_OK = 200;

    /**
     * T435: Test GET /api/stats/summary - get completion statistics.
     * Expected: 200 OK with todayCount, weekCount, totalCount, completionRate.
     */
    @Test
    @DisplayName("GET /api/stats/summary - should return 200 with completion statistics")
    void testGetCompletionStats() {
        given()
            .contentType(ContentType.JSON)
            .when()
            .get(API_STATS_PATH + "/summary")
            .then()
            .statusCode(HTTP_OK)
            .contentType(ContentType.JSON)
            .body("todayCount", isA(Number.class))
            .body("weekCount", isA(Number.class))
            .body("totalCount", isA(Number.class))
            .body("completionRate", isA(Number.class));
    }

    /**
     * T436: Test GET /api/stats/history?days=7 - get daily completion counts.
     * Expected: 200 OK with array of CompletionHistoryDTO objects.
     */
    @Test
    @DisplayName("GET /api/stats/history?days=7 - should return 200 with array of daily counts")
    void testGetCompletionHistory() {
        given()
            .contentType(ContentType.JSON)
            .queryParam("days", 7)
            .when()
            .get(API_STATS_PATH + "/history")
            .then()
            .statusCode(HTTP_OK)
            .contentType(ContentType.JSON)
            .body("$", isA(java.util.List.class));
    }

    /**
     * Test GET /api/stats/history with default days parameter.
     * Expected: 200 OK with array (should default to 30 days).
     */
    @Test
    @DisplayName("GET /api/stats/history without days param - should return 200 with default 30 days")
    void testGetCompletionHistoryDefaultDays() {
        given()
            .contentType(ContentType.JSON)
            .when()
            .get(API_STATS_PATH + "/history")
            .then()
            .statusCode(HTTP_OK)
            .contentType(ContentType.JSON)
            .body("$", isA(java.util.List.class));
    }
}
