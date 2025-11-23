package org.acme.taskmanager.resource;

import io.vertx.ext.web.RoutingContext;
import jakarta.inject.Inject;
import jakarta.ws.rs.DefaultValue;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.acme.taskmanager.dto.CompletionHistoryDTO;
import org.acme.taskmanager.dto.CompletionStatsDTO;
import org.acme.taskmanager.service.StatsService;
import org.acme.taskmanager.session.SessionUtils;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import java.util.List;

/**
 * REST resource for task completion statistics.
 *
 * <p>T461-T470: Provides endpoints for completion statistics and history.
 *
 * <p><b>Endpoints:</b>
 * <ul>
 *   <li>GET /api/stats/summary - Get completion statistics summary
 *   <li>GET /api/stats/history - Get completion history (tasks per day)
 * </ul>
 */
@Path("/api/stats")
@Produces(MediaType.APPLICATION_JSON)
@Tag(name = "Statistics", description = "Task completion statistics and history")
public class StatsResource {

  @Inject
  private StatsService statsService;

  @Context
  private RoutingContext routingContext;

  /**
   * Gets completion statistics summary.
   *
   * <p>T463-T466: Returns counts for today, week, total, and completion rate.
   *
   * @return Response with 200 OK and CompletionStatsDTO
   */
  @GET
  @Path("/summary")
  @Operation(
      summary = "Get completion statistics",
      description = "Returns task completion statistics including today, week, total counts and completion rate")
  @APIResponse(
      responseCode = "200",
      description = "Statistics retrieved successfully",
      content = @Content(
          mediaType = MediaType.APPLICATION_JSON,
          schema = @Schema(implementation = CompletionStatsDTO.class)))
  @APIResponse(responseCode = "401", description = "Not authenticated")
  public Response getCompletionStats() {
    // T464: Extract userId from session
    String userId = SessionUtils.getCurrentUserId(routingContext);

    // T465: Call StatsService.getCompletionStats() and return 200 OK
    CompletionStatsDTO stats = statsService.getCompletionStats(userId);

    return Response.ok(stats).build();
  }

  /**
   * Gets completion history.
   *
   * <p>T467-T470: Returns daily completion counts for the specified number of days.
   *
   * @param days number of days to include in history (default 30, max 365)
   * @return Response with 200 OK and List of CompletionHistoryDTO
   */
  @GET
  @Path("/history")
  @Operation(
      summary = "Get completion history",
      description = "Returns daily task completion counts for the specified number of days")
  @APIResponse(
      responseCode = "200",
      description = "History retrieved successfully",
      content = @Content(
          mediaType = MediaType.APPLICATION_JSON,
          schema = @Schema(implementation = CompletionHistoryDTO.class, type = SchemaType.ARRAY)))
  @APIResponse(responseCode = "400", description = "Invalid days parameter")
  @APIResponse(responseCode = "401", description = "Not authenticated")
  public Response getCompletionHistory(
      @Parameter(
          description = "Number of days to include in history (1-365, default 30)",
          schema = @Schema(type = SchemaType.INTEGER, minimum = "1", maximum = "365", defaultValue = "30"))
      @QueryParam("days")
      @DefaultValue("30")
      final int days) {

    // T468: Extract userId from session
    String userId = SessionUtils.getCurrentUserId(routingContext);

    // T469: Call StatsService.getCompletionHistory() and return 200 OK
    List<CompletionHistoryDTO> history = statsService.getCompletionHistory(userId, days);

    return Response.ok(history).build();
  }
}
