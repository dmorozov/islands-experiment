/**
 * TaskFilter Island Component
 *
 * Interactive Preact island for filtering tasks by category, priority, and status.
 * This component demonstrates Islands Architecture with stateful filter management.
 *
 * Features:
 * - Fetches categories from API for dropdown
 * - Updates filter state via Nano Stores
 * - Persists filters to URL query params
 * - Loads filters from URL on mount
 * - Saves filters to localStorage for persistence
 * - Clear filters functionality
 * - Responsive design
 *
 * Usage in Astro pages:
 * ```astro
 * ---
 * import TaskFilter from '@/islands/TaskFilter';
 * ---
 * <TaskFilter client:load />
 * ```
 */

import { useStore } from '@nanostores/preact';
import { useEffect } from 'preact/hooks';
import { trackIslandHydration } from '@/lib/performance';
import { taskFilter, updateFilter, resetFilters } from '@/lib/state';
import { useGetApiCategories } from '@/lib/api/endpoints/categories/categories';
import { preferences } from '@/lib/storage';
import { QueryProvider } from '@/components/providers/QueryProvider';

/**
 * Priority options for the dropdown
 */
const PRIORITY_OPTIONS = [
  { value: '', label: 'All Priorities' },
  { value: 'HIGH', label: 'High' },
  { value: 'MEDIUM', label: 'Medium' },
  { value: 'LOW', label: 'Low' },
];

/**
 * Status options for the dropdown
 */
const STATUS_OPTIONS = [
  { value: '', label: 'All Status' },
  { value: 'active', label: 'Active' },
  { value: 'completed', label: 'Completed' },
];

/**
 * Select component for filter dropdowns
 * Styled with Tailwind and Shadcn/ui theme
 */
interface SelectProps {
  label: string;
  value: string;
  onChange: (value: string) => void;
  options: Array<{ value: string; label: string }>;
  isLoading?: boolean;
}

function Select({ label, value, onChange, options, isLoading }: SelectProps) {
  return (
    <div class="flex flex-col gap-2">
      <label class="text-sm font-medium text-foreground">{label}</label>
      <select
        value={value}
        onChange={(e) => onChange((e.target as HTMLSelectElement).value)}
        disabled={isLoading}
        class="flex h-10 w-full rounded-md border border-input bg-background px-3 py-2 text-sm ring-offset-background transition-colors focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-ring focus-visible:ring-offset-2 disabled:cursor-not-allowed disabled:opacity-50"
      >
        {options.map((option) => (
          <option key={option.value} value={option.value}>
            {option.label}
          </option>
        ))}
      </select>
    </div>
  );
}

/**
 * Internal TaskFilter component
 *
 * Manages filter state with persistence to URL and localStorage
 */
function TaskFilterContent() {
  // Subscribe to filter state from Nano Stores
  const filter = useStore(taskFilter);

  // Fetch categories for dropdown
  const { data: categories, isLoading: categoriesLoading } = useGetApiCategories();

  // T538: Track island hydration for performance monitoring
  useEffect(() => {
    trackIslandHydration('TaskFilter');
  }, []);

  /**
   * Load filters from URL query params on mount
   * Enables sharing filtered views via URL
   */
  useEffect(() => {
    if (typeof window === 'undefined') return;

    const params = new URLSearchParams(window.location.search);
    const category = params.get('category');
    const priority = params.get('priority');
    const status = params.get('status');

    // Only update if URL has filter params
    if (category || priority || status) {
      taskFilter.set({
        category: category || undefined,
        priority: priority || undefined,
        status: status || undefined,
      });
    }
  }, []);

  /**
   * Update URL query params when filters change
   * Enables browser back/forward navigation and shareable URLs
   */
  useEffect(() => {
    if (typeof window === 'undefined') return;

    const params = new URLSearchParams();

    if (filter.category) params.set('category', filter.category);
    if (filter.priority) params.set('priority', filter.priority);
    if (filter.status) params.set('status', filter.status);

    const newUrl = params.toString()
      ? `${window.location.pathname}?${params.toString()}`
      : window.location.pathname;

    // Update URL without page reload
    window.history.replaceState({}, '', newUrl);

    // Save to localStorage for persistence
    // Note: In a real app, you'd get the userId from authentication
    // For now, using a demo user ID
    const currentUser = 'demo-user';
    const currentPrefs = preferences.get(currentUser);
    preferences.set(currentUser, {
      ...currentPrefs,
      activeFilters: filter,
    });
  }, [filter]);

  /**
   * Handle category filter change
   */
  const handleCategoryChange = (value: string) => {
    updateFilter('category', value || undefined);
  };

  /**
   * Handle priority filter change
   */
  const handlePriorityChange = (value: string) => {
    updateFilter('priority', value || undefined);
  };

  /**
   * Handle status filter change
   */
  const handleStatusChange = (value: string) => {
    updateFilter('status', value || undefined);
  };

  /**
   * Handle clear all filters
   */
  const handleClearFilters = () => {
    resetFilters();
  };

  /**
   * Check if any filters are active
   */
  const hasActiveFilters = Boolean(
    filter.category || filter.priority || filter.status
  );

  /**
   * Prepare category options for dropdown
   */
  const categoryOptions = [
    { value: '', label: 'All Categories' },
    ...(categories?.map((cat) => ({
      value: cat.id || '',
      label: cat.name || 'Unnamed',
    })) || []),
  ];

  return (
    <div class="rounded-lg border bg-card p-6 shadow-sm">
      {/* Header */}
      <div class="mb-6 flex items-center justify-between">
        <div>
          <h2 class="text-lg font-semibold text-foreground">Filters</h2>
          <p class="text-sm text-muted-foreground">
            Refine your task list
          </p>
        </div>
        {hasActiveFilters && (
          <button
            type="button"
            onClick={handleClearFilters}
            class="text-sm font-medium text-primary hover:underline"
          >
            Clear all
          </button>
        )}
      </div>

      {/* Filter Controls */}
      <div class="grid gap-4 sm:grid-cols-2 lg:grid-cols-3">
        {/* Category Filter */}
        <Select
          label="Category"
          value={filter.category || ''}
          onChange={handleCategoryChange}
          options={categoryOptions}
          isLoading={categoriesLoading}
        />

        {/* Priority Filter */}
        <Select
          label="Priority"
          value={filter.priority || ''}
          onChange={handlePriorityChange}
          options={PRIORITY_OPTIONS}
        />

        {/* Status Filter */}
        <Select
          label="Status"
          value={filter.status || ''}
          onChange={handleStatusChange}
          options={STATUS_OPTIONS}
        />
      </div>

      {/* Active Filters Summary */}
      {hasActiveFilters && (
        <div class="mt-4 flex flex-wrap items-center gap-2 border-t pt-4">
          <span class="text-sm font-medium text-muted-foreground">
            Active filters:
          </span>
          {filter.category && (
            <span class="inline-flex items-center gap-1 rounded-full bg-primary/10 px-3 py-1 text-xs font-medium text-primary">
              Category
              <button
                type="button"
                onClick={() => updateFilter('category', undefined)}
                class="ml-1 rounded-full hover:bg-primary/20"
                aria-label="Remove category filter"
              >
                <svg
                  class="h-3 w-3"
                  fill="none"
                  stroke="currentColor"
                  viewBox="0 0 24 24"
                >
                  <path
                    stroke-linecap="round"
                    stroke-linejoin="round"
                    stroke-width="2"
                    d="M6 18L18 6M6 6l12 12"
                  />
                </svg>
              </button>
            </span>
          )}
          {filter.priority && (
            <span class="inline-flex items-center gap-1 rounded-full bg-primary/10 px-3 py-1 text-xs font-medium text-primary">
              Priority: {filter.priority}
              <button
                type="button"
                onClick={() => updateFilter('priority', undefined)}
                class="ml-1 rounded-full hover:bg-primary/20"
                aria-label="Remove priority filter"
              >
                <svg
                  class="h-3 w-3"
                  fill="none"
                  stroke="currentColor"
                  viewBox="0 0 24 24"
                >
                  <path
                    stroke-linecap="round"
                    stroke-linejoin="round"
                    stroke-width="2"
                    d="M6 18L18 6M6 6l12 12"
                  />
                </svg>
              </button>
            </span>
          )}
          {filter.status && (
            <span class="inline-flex items-center gap-1 rounded-full bg-primary/10 px-3 py-1 text-xs font-medium text-primary">
              Status: {filter.status}
              <button
                type="button"
                onClick={() => updateFilter('status', undefined)}
                class="ml-1 rounded-full hover:bg-primary/20"
                aria-label="Remove status filter"
              >
                <svg
                  class="h-3 w-3"
                  fill="none"
                  stroke="currentColor"
                  viewBox="0 0 24 24"
                >
                  <path
                    stroke-linecap="round"
                    stroke-linejoin="round"
                    stroke-width="2"
                    d="M6 18L18 6M6 6l12 12"
                  />
                </svg>
              </button>
            </span>
          )}
        </div>
      )}
    </div>
  );
}

/**
 * Main TaskFilter component wrapped with QueryProvider
 *
 * This wrapper ensures React Query context is available for all hooks
 */
export default function TaskFilter() {
  return (
    <QueryProvider>
      <TaskFilterContent />
    </QueryProvider>
  );
}
