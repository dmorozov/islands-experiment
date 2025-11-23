package org.acme.taskmanager.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.quarkus.runtime.annotations.RegisterForReflection;

import java.time.LocalDate;

/**
 * DTO for completion history entry (tasks completed on a specific date).
 * T439-T440: Contains the date and count of tasks completed on that date.
 *
 * @param date the date for this history entry
 * @param count number of tasks completed on this date
 */
@RegisterForReflection
@JsonInclude(JsonInclude.Include.NON_NULL)
public record CompletionHistoryDTO(
    LocalDate date,
    long count
) { }
