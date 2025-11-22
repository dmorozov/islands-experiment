/**
 * Global state management using Nano Stores.
 *
 * This module defines reactive atoms for managing application state across
 * Astro islands and Preact components. Nano Stores provides a minimal,
 * framework-agnostic state management solution perfect for Islands Architecture.
 *
 * Key features:
 * - Reactive atoms that trigger re-renders on changes
 * - Persistent atoms that sync with localStorage
 * - Computed atoms for derived state
 * - TypeScript type safety
 * - Framework agnostic (works with Preact, React, Vue, etc.)
 *
 * Usage in Preact components:
 * ```tsx
 * import { useStore } from '@nanostores/preact';
 * import { taskFilter, userTheme } from '@/lib/state';
 *
 * export function MyComponent() {
 *   const filter = useStore(taskFilter);
 *   const theme = useStore(userTheme);
 *
 *   return <div>Current theme: {theme}</div>;
 * }
 * ```
 *
 * Usage in vanilla JS:
 * ```typescript
 * import { taskFilter } from '@/lib/state';
 *
 * // Read value
 * const currentFilter = taskFilter.get();
 *
 * // Update value
 * taskFilter.set({ status: 'active' });
 *
 * // Subscribe to changes
 * const unsubscribe = taskFilter.listen((value) => {
 *   console.log('Filter changed:', value);
 * });
 * ```
 *
 * @see https://github.com/nanostores/nanostores
 */

import { atom, computed } from 'nanostores';
import { persistentAtom } from '@nanostores/persistent';

/**
 * Task filter state type.
 *
 * Defines the structure of task filters used for querying the API.
 * All fields are optional to allow flexible filtering.
 */
export interface TaskFilter {
  /**
   * Filter by category UUID
   */
  category?: string;

  /**
   * Filter by priority level: HIGH, MEDIUM, or LOW
   */
  priority?: string;

  /**
   * Filter by completion status: active or completed
   */
  status?: string;
}

/**
 * Task filter atom.
 *
 * Holds the current task filter state used across the application.
 * When this atom changes, all islands using it will re-render with
 * the new filter values.
 *
 * This state is:
 * - Not persisted (resets on page reload)
 * - Shared across all components/islands
 * - Type-safe with TypeScript
 *
 * @example
 * ```typescript
 * import { taskFilter } from '@/lib/state';
 *
 * // Set filter to show only active tasks
 * taskFilter.set({ status: 'active' });
 *
 * // Clear all filters
 * taskFilter.set({});
 *
 * // Set multiple filters
 * taskFilter.set({
 *   status: 'active',
 *   priority: 'HIGH',
 *   category: '123e4567-e89b-12d3-a456-426614174000'
 * });
 * ```
 */
export const taskFilter = atom<TaskFilter>({});

/**
 * User theme atom with localStorage persistence.
 *
 * Stores the user's theme preference and automatically persists it to
 * localStorage. The theme persists across page reloads and browser sessions.
 *
 * This atom uses @nanostores/persistent which:
 * - Automatically syncs with localStorage
 * - Works across browser tabs (storage events)
 * - Falls back to default value if localStorage is unavailable
 * - Handles SSR gracefully
 *
 * Storage key: 'theme'
 * Default value: 'light'
 *
 * @example
 * ```typescript
 * import { userTheme } from '@/lib/state';
 *
 * // Get current theme
 * const theme = userTheme.get(); // 'light' | 'dark'
 *
 * // Toggle theme
 * userTheme.set(theme === 'light' ? 'dark' : 'light');
 * ```
 */
export const userTheme = persistentAtom<'light' | 'dark'>('theme', 'light', {
  encode: (value) => value,
  decode: (value) => {
    // Validate the stored value
    if (value === 'light' || value === 'dark') {
      return value;
    }
    // Fall back to light if invalid
    return 'light';
  },
});

/**
 * Current user atom.
 *
 * Stores information about the currently authenticated user.
 * This is set after successful login and cleared on logout.
 *
 * Note: This is not persisted to localStorage for security reasons.
 * User authentication state should be managed server-side with sessions.
 *
 * @example
 * ```typescript
 * import { currentUser } from '@/lib/state';
 *
 * // Set user after login
 * currentUser.set({ username: 'john_doe' });
 *
 * // Clear user on logout
 * currentUser.set(null);
 * ```
 */
export const currentUser = atom<{ username: string } | null>(null);

/**
 * Derived atom: Is user authenticated?
 *
 * A computed atom that derives its value from currentUser.
 * Automatically updates when currentUser changes.
 *
 * This is more efficient than checking currentUser !== null in every
 * component, as the computation happens once at the store level.
 *
 * @example
 * ```typescript
 * import { useStore } from '@nanostores/preact';
 * import { isAuthenticated } from '@/lib/state';
 *
 * export function Header() {
 *   const authenticated = useStore(isAuthenticated);
 *
 *   return (
 *     <nav>
 *       {authenticated ? (
 *         <UserMenu />
 *       ) : (
 *         <LoginButton />
 *       )}
 *     </nav>
 *   );
 * }
 * ```
 */
export const isAuthenticated = computed(currentUser, (user) => user !== null);

/**
 * Pagination state atom.
 *
 * Stores the current page number for task pagination.
 * Resets to 0 when filters change.
 *
 * @example
 * ```typescript
 * import { currentPage } from '@/lib/state';
 *
 * // Go to next page
 * currentPage.set(currentPage.get() + 1);
 *
 * // Reset to first page
 * currentPage.set(0);
 * ```
 */
export const currentPage = atom<number>(0);

/**
 * Tasks per page atom with localStorage persistence.
 *
 * Stores the user's preference for how many tasks to display per page.
 * Persists across sessions.
 *
 * Valid range: 1-100 tasks per page
 * Default: 20 tasks per page
 *
 * @example
 * ```typescript
 * import { tasksPerPage } from '@/lib/state';
 *
 * // Change pagination size
 * tasksPerPage.set(50);
 * ```
 */
export const tasksPerPage = persistentAtom<number>('tasksPerPage', 20, {
  encode: (value) => String(value),
  decode: (value) => {
    const parsed = parseInt(value, 10);
    // Validate range 1-100
    if (isNaN(parsed) || parsed < 1 || parsed > 100) {
      return 20; // Default
    }
    return parsed;
  },
});

/**
 * Helper function to reset all filter state.
 *
 * Clears all filters and resets pagination to the first page.
 * Useful for "Clear Filters" buttons.
 *
 * @example
 * ```typescript
 * import { resetFilters } from '@/lib/state';
 *
 * function ClearFiltersButton() {
 *   return (
 *     <button onClick={resetFilters}>
 *       Clear All Filters
 *     </button>
 *   );
 * }
 * ```
 */
export function resetFilters(): void {
  taskFilter.set({});
  currentPage.set(0);
}

/**
 * Helper function to update a single filter field.
 *
 * Updates one filter field while preserving others.
 * Automatically resets pagination to page 0.
 *
 * @param field - The filter field to update
 * @param value - The new value (undefined to clear the field)
 *
 * @example
 * ```typescript
 * import { updateFilter } from '@/lib/state';
 *
 * // Set status filter
 * updateFilter('status', 'active');
 *
 * // Clear priority filter
 * updateFilter('priority', undefined);
 * ```
 */
export function updateFilter(
  field: keyof TaskFilter,
  value: string | undefined
): void {
  const current = taskFilter.get();

  if (value === undefined) {
    // Remove the field
    const { [field]: _, ...rest } = current;
    taskFilter.set(rest);
  } else {
    // Update the field
    taskFilter.set({
      ...current,
      [field]: value,
    });
  }

  // Reset to first page when filter changes
  currentPage.set(0);
}

/**
 * Helper function to toggle theme.
 *
 * Switches between light and dark themes.
 *
 * @example
 * ```typescript
 * import { toggleTheme } from '@/lib/state';
 *
 * function ThemeToggle() {
 *   return (
 *     <button onClick={toggleTheme}>
 *       Toggle Theme
 *     </button>
 *   );
 * }
 * ```
 */
export function toggleTheme(): void {
  const current = userTheme.get();
  userTheme.set(current === 'light' ? 'dark' : 'light');
}
