package org.acme.taskmanager.model;

/**
 * Priority levels for tasks.
 *
 * <p>This enum defines the priority classification for tasks in the Task Manager application.
 * Priorities help users organize and focus on the most important tasks first.
 *
 * <p><b>Values:</b>
 * <ul>
 *   <li>{@link #HIGH} - Urgent or critical tasks that require immediate attention
 *   <li>{@link #MEDIUM} - Important tasks with moderate urgency (default priority)
 *   <li>{@link #LOW} - Tasks that can be deferred or are less time-sensitive
 * </ul>
 *
 * <p><b>Usage:</b>
 * <pre>{@code
 * Task task = new Task();
 * task.setPriority(Priority.HIGH);
 * }</pre>
 *
 * <p>This enum is persisted as a string in the database using {@code @Enumerated(EnumType.STRING)}
 * to maintain database readability and avoid issues with ordinal changes.
 */
public enum Priority {
  /**
   * High priority - urgent or critical tasks requiring immediate attention.
   */
  HIGH,

  /**
   * Medium priority - important tasks with moderate urgency.
   * This is the default priority for new tasks.
   */
  MEDIUM,

  /**
   * Low priority - tasks that can be deferred or are less time-sensitive.
   */
  LOW
}
