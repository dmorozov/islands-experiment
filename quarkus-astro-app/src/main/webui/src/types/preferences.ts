import { z } from 'zod';

/**
 * User preferences interface for client-side storage.
 *
 * This interface defines the structure of user preferences that are persisted
 * in localStorage for a personalized user experience across sessions.
 *
 * @example
 * ```typescript
 * const prefs: UserPreferences = {
 *   theme: 'dark',
 *   lastViewedPage: '/tasks',
 *   activeFilters: { status: 'active', priority: 'HIGH' },
 *   tasksPerPage: 20
 * };
 * ```
 */
export interface UserPreferences {
  /**
   * UI theme preference
   * @default 'light'
   */
  theme: 'light' | 'dark';

  /**
   * Last viewed page URL for navigation restoration
   * @default '/'
   */
  lastViewedPage: string;

  /**
   * Active task filters for persistence across page navigation
   */
  activeFilters: {
    /**
     * Filter by category UUID
     */
    category?: string;

    /**
     * Filter by priority level
     */
    priority?: string;

    /**
     * Filter by completion status
     */
    status?: string;
  };

  /**
   * Number of tasks to display per page
   * @default 20
   */
  tasksPerPage: number;
}

/**
 * Default user preferences.
 *
 * These values are used when no preferences are found in localStorage
 * or when preferences are reset.
 */
export const defaultPreferences: UserPreferences = {
  theme: 'light',
  lastViewedPage: '/',
  activeFilters: {},
  tasksPerPage: 20,
};

/**
 * Zod schema for UserPreferences validation.
 *
 * This schema is used to validate preferences loaded from localStorage
 * to ensure data integrity and prevent errors from corrupted storage.
 *
 * @example
 * ```typescript
 * const result = userPreferencesSchema.safeParse(data);
 * if (result.success) {
 *   const prefs = result.data;
 * }
 * ```
 */
export const userPreferencesSchema = z.object({
  theme: z.enum(['light', 'dark']),
  lastViewedPage: z.string(),
  activeFilters: z.object({
    category: z.string().uuid().optional(),
    priority: z.string().optional(),
    status: z.enum(['active', 'completed']).optional(),
  }),
  tasksPerPage: z.number().int().min(1).max(100),
});

/**
 * Type guard to check if an object is a valid UserPreferences.
 *
 * @param value - The value to check
 * @returns True if the value is a valid UserPreferences object
 */
export function isUserPreferences(value: unknown): value is UserPreferences {
  return userPreferencesSchema.safeParse(value).success;
}
