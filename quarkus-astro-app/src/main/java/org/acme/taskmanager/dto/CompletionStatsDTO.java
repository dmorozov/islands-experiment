package org.acme.taskmanager.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.quarkus.runtime.annotations.RegisterForReflection;

/**
 * DTO for completion statistics summary.
 * T437-T438: Contains counts for tasks completed today, this week, and total, plus completion rate.
 *
 * @param todayCount number of tasks completed today
 * @param weekCount number of tasks completed this week
 * @param totalCount total number of completed tasks
 * @param completionRate percentage of tasks completed (0-100)
 */
@RegisterForReflection
@JsonInclude(JsonInclude.Include.NON_NULL)
public record CompletionStatsDTO(
    long todayCount,
    long weekCount,
    long totalCount,
    double completionRate
) { }
