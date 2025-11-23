package org.acme.taskmanager.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.acme.taskmanager.dto.CompletionHistoryDTO;
import org.acme.taskmanager.dto.CompletionStatsDTO;
import org.acme.taskmanager.repository.TaskRepository;
import org.jboss.logging.Logger;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Service layer for task completion statistics.
 *
 * <p>T446-T456: Provides methods to get completion stats (today, week, total)
 * and completion history (tasks completed per day).
 */
@ApplicationScoped
public class StatsService {

  private static final Logger LOG = Logger.getLogger(StatsService.class);
  private static final double PERCENTAGE_MULTIPLIER = 100.0;
  private static final double ROUNDING_PRECISION = 100.0;
  private static final int MAX_HISTORY_DAYS = 365;

  @Inject
  private TaskRepository taskRepository;

  /**
   * Gets completion statistics for a user.
   *
   * <p>T448-T453: Returns counts for tasks completed today, this week, total,
   * and the overall completion rate.
   *
   * @param userId the user's unique identifier
   * @return CompletionStatsDTO with all statistics
   * @throws IllegalArgumentException if userId is null or blank
   */
  public CompletionStatsDTO getCompletionStats(final String userId) {
    if (userId == null || userId.isBlank()) {
      throw new IllegalArgumentException("User ID cannot be null or blank");
    }

    LOG.debugf("Getting completion stats for user: %s", userId);

    // T449: Query tasks completed today
    LocalDateTime startOfDay = LocalDate.now().atStartOfDay();
    long todayCount = taskRepository.count(
        "userId = ?1 and completed = true and completedAt >= ?2",
        userId, startOfDay);

    // T450: Query tasks completed this week (assuming week starts on Monday)
    LocalDate startOfWeek = LocalDate.now().minusDays(LocalDate.now().getDayOfWeek().getValue() - 1);
    LocalDateTime startOfWeekDateTime = startOfWeek.atStartOfDay();
    long weekCount = taskRepository.count(
        "userId = ?1 and completed = true and completedAt >= ?2",
        userId, startOfWeekDateTime);

    // T451: Query total completed tasks
    long totalCount = taskRepository.count(
        "userId = ?1 and completed = true",
        userId);

    // T452: Calculate completion rate
    long totalTasks = taskRepository.count("userId = ?1", userId);
    double completionRate = totalTasks > 0
        ? (double) totalCount / totalTasks * PERCENTAGE_MULTIPLIER
        : 0.0;

    LOG.debugf("Stats for user %s: today=%d, week=%d, total=%d, rate=%.2f%%",
        userId, todayCount, weekCount, totalCount, completionRate);

    // T453: Return CompletionStatsDTO
    return new CompletionStatsDTO(
        todayCount,
        weekCount,
        totalCount,
        Math.round(completionRate * ROUNDING_PRECISION) / ROUNDING_PRECISION
    );
  }

  /**
   * Gets completion history for a user.
   *
   * <p>T454-T456: Returns a list of daily completion counts for the last N days.
   *
   * @param userId the user's unique identifier
   * @param days number of days to include in history (max 365)
   * @return List of CompletionHistoryDTO, one per day
   * @throws IllegalArgumentException if userId is null/blank or days is invalid
   */
  public List<CompletionHistoryDTO> getCompletionHistory(final String userId, final int days) {
    if (userId == null || userId.isBlank()) {
      throw new IllegalArgumentException("User ID cannot be null or blank");
    }
    if (days < 1 || days > MAX_HISTORY_DAYS) {
      throw new IllegalArgumentException("Days must be between 1 and " + MAX_HISTORY_DAYS);
    }

    LOG.debugf("Getting completion history for user: %s, days: %d", userId, days);

    // T455: Query tasks completed in the last N days
    LocalDate endDate = LocalDate.now();
    LocalDate startDate = endDate.minusDays(days - 1);
    LocalDateTime startDateTime = startDate.atStartOfDay();

    // Get all completed tasks in date range and manually group by date
    List<org.acme.taskmanager.model.Task> completedTasks = taskRepository.find(
        "userId = ?1 and completed = true and completedAt >= ?2 "
        + "order by completedAt",
        userId, startDateTime
    ).list();

    // Convert results to map for easy lookup
    Map<LocalDate, Long> completionMap = completedTasks.stream()
        .collect(Collectors.groupingBy(
            task -> task.getCompletedAt().toLocalDate(),
            Collectors.counting()
        ));

    // T456: Return List<CompletionHistoryDTO> with all days (including zeros)
    List<CompletionHistoryDTO> history = new ArrayList<>();
    for (int i = days - 1; i >= 0; i--) {
      LocalDate date = endDate.minusDays(i);
      long count = completionMap.getOrDefault(date, 0L);
      history.add(new CompletionHistoryDTO(date, count));
    }

    LOG.debugf("Found %d days of history for user: %s", history.size(), userId);

    return history;
  }
}
