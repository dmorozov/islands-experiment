import {
  defaultPreferences,
  userPreferencesSchema,
  type UserPreferences,
} from '@/types/preferences';

/**
 * Storage utility for managing user preferences in localStorage.
 *
 * This module provides a type-safe interface for storing and retrieving
 * user preferences with validation and error handling.
 *
 * Key features:
 * - Per-user storage scoping
 * - Zod schema validation
 * - Graceful error handling with fallbacks
 * - TypeScript type safety
 *
 * @example
 * ```typescript
 * import { preferences } from '@/lib/storage';
 *
 * // Get preferences
 * const prefs = preferences.get('user123');
 *
 * // Update preferences
 * preferences.set('user123', { ...prefs, theme: 'dark' });
 *
 * // Clear preferences
 * preferences.clear('user123');
 * ```
 */

const STORAGE_KEY_PREFIX = 'taskmanager_prefs_';

/**
 * Generates a storage key for a specific user.
 *
 * @param userId - The user's unique identifier
 * @returns The scoped storage key
 */
function getStorageKey(userId: string): string {
  return `${STORAGE_KEY_PREFIX}${userId}`;
}

/**
 * Preferences storage API.
 *
 * Provides methods to get, set, and clear user preferences in localStorage.
 */
export const preferences = {
  /**
   * Retrieves user preferences from localStorage.
   *
   * This method attempts to load preferences from localStorage and validates
   * them against the Zod schema. If preferences are not found, corrupted,
   * or invalid, it returns default preferences.
   *
   * Error handling:
   * - localStorage not available (SSR, private browsing): returns defaults
   * - Invalid JSON: returns defaults
   * - Schema validation failure: returns defaults
   * - Quota exceeded: returns defaults
   *
   * @param userId - The user's unique identifier
   * @returns User preferences or default preferences if unavailable
   *
   * @example
   * ```typescript
   * const prefs = preferences.get('user123');
   * console.log(prefs.theme); // 'light' or 'dark'
   * ```
   */
  get(userId: string): UserPreferences {
    try {
      // Check if localStorage is available
      if (typeof localStorage === 'undefined') {
        console.warn('[Storage] localStorage is not available (SSR context)');
        return { ...defaultPreferences };
      }

      const key = getStorageKey(userId);
      const stored = localStorage.getItem(key);

      // No preferences found - return defaults
      if (!stored) {
        return { ...defaultPreferences };
      }

      // Parse JSON
      const parsed = JSON.parse(stored);

      // Validate with Zod schema
      const result = userPreferencesSchema.safeParse(parsed);

      if (result.success) {
        return result.data;
      } else {
        console.warn(
          '[Storage] Invalid preferences format, using defaults:',
          result.error.errors
        );
        return { ...defaultPreferences };
      }
    } catch (error) {
      // Handle any errors (JSON parse errors, localStorage errors, etc.)
      console.error('[Storage] Error retrieving preferences:', error);
      return { ...defaultPreferences };
    }
  },

  /**
   * Stores user preferences to localStorage.
   *
   * This method validates the preferences against the Zod schema before
   * storing them. If validation fails or storage is unavailable, it logs
   * an error but does not throw.
   *
   * Error handling:
   * - localStorage not available: logs warning, no-op
   * - Schema validation failure: logs error, no-op
   * - Quota exceeded: logs error, no-op
   * - Other errors: logs error, no-op
   *
   * @param userId - The user's unique identifier
   * @param prefs - The preferences to store
   *
   * @example
   * ```typescript
   * preferences.set('user123', {
   *   theme: 'dark',
   *   lastViewedPage: '/tasks',
   *   activeFilters: { status: 'active' },
   *   tasksPerPage: 20
   * });
   * ```
   */
  set(userId: string, prefs: UserPreferences): void {
    try {
      // Check if localStorage is available
      if (typeof localStorage === 'undefined') {
        console.warn('[Storage] localStorage is not available (SSR context)');
        return;
      }

      // Validate preferences before storing
      const result = userPreferencesSchema.safeParse(prefs);

      if (!result.success) {
        console.error(
          '[Storage] Invalid preferences provided:',
          result.error.errors
        );
        return;
      }

      const key = getStorageKey(userId);
      const serialized = JSON.stringify(result.data);

      localStorage.setItem(key, serialized);
    } catch (error) {
      // Handle quota exceeded or other localStorage errors
      if (error instanceof Error) {
        if (error.name === 'QuotaExceededError') {
          console.error('[Storage] localStorage quota exceeded');
        } else {
          console.error('[Storage] Error storing preferences:', error);
        }
      } else {
        console.error('[Storage] Unknown error storing preferences:', error);
      }
    }
  },

  /**
   * Clears user preferences from localStorage.
   *
   * This removes all stored preferences for the specified user.
   * Subsequent calls to get() will return default preferences.
   *
   * Error handling:
   * - localStorage not available: logs warning, no-op
   * - Other errors: logs error, no-op
   *
   * @param userId - The user's unique identifier
   *
   * @example
   * ```typescript
   * // Reset user preferences to defaults
   * preferences.clear('user123');
   * ```
   */
  clear(userId: string): void {
    try {
      // Check if localStorage is available
      if (typeof localStorage === 'undefined') {
        console.warn('[Storage] localStorage is not available (SSR context)');
        return;
      }

      const key = getStorageKey(userId);
      localStorage.removeItem(key);
    } catch (error) {
      console.error('[Storage] Error clearing preferences:', error);
    }
  },
};

/**
 * Utility function to check if localStorage is available.
 *
 * This is useful for SSR environments or browsers with localStorage disabled.
 *
 * @returns True if localStorage is available and writable
 */
export function isLocalStorageAvailable(): boolean {
  try {
    if (typeof localStorage === 'undefined') {
      return false;
    }

    const testKey = '__storage_test__';
    localStorage.setItem(testKey, 'test');
    localStorage.removeItem(testKey);
    return true;
  } catch {
    return false;
  }
}
