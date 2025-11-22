/**
 * TaskList Island Component
 *
 * Interactive Preact island for displaying and managing tasks.
 * This component demonstrates Islands Architecture by being a hydrated
 * interactive component within otherwise static Astro pages.
 *
 * Features:
 * - Fetches tasks from API using TanStack Query
 * - Reactive to filter changes via Nano Stores
 * - Loading states with skeleton loaders
 * - Error handling with user-friendly messages
 * - Empty state messaging
 * - Responsive grid layout
 * - Priority and category visual indicators
 *
 * Usage in Astro pages:
 * ```astro
 * ---
 * import TaskList from '@/islands/TaskList';
 * ---
 * <TaskList client:load />
 * ```
 *
 * The client:load directive ensures this component hydrates immediately on page load.
 */

import { useStore } from '@nanostores/preact';
import { taskFilter, currentPage, tasksPerPage } from '@/lib/state';
import { useGetApiTasks } from '@/lib/api/endpoints/tasks/tasks';
import type { TaskResponseDTO } from '@/lib/api/model';

/**
 * Priority badge component
 * Displays priority with color coding
 */
function PriorityBadge({ priority }: { priority: string }) {
  const colors = {
    HIGH: 'bg-red-100 text-red-800 dark:bg-red-900/30 dark:text-red-400',
    MEDIUM: 'bg-yellow-100 text-yellow-800 dark:bg-yellow-900/30 dark:text-yellow-400',
    LOW: 'bg-green-100 text-green-800 dark:bg-green-900/30 dark:text-green-400',
  };

  const colorClass = colors[priority as keyof typeof colors] || colors.MEDIUM;

  return (
    <span
      class={`inline-flex items-center rounded-full px-2.5 py-0.5 text-xs font-medium ${colorClass}`}
    >
      {priority}
    </span>
  );
}

/**
 * Category badge component
 * Displays category with custom color
 */
function CategoryBadge({
  name,
  colorCode,
}: {
  name: string;
  colorCode?: string;
}) {
  return (
    <span
      class="inline-flex items-center rounded-full px-2.5 py-0.5 text-xs font-medium"
      style={{
        backgroundColor: colorCode
          ? `${colorCode}20`
          : 'hsl(var(--muted))',
        color: colorCode || 'hsl(var(--muted-foreground))',
      }}
    >
      {name}
    </span>
  );
}

/**
 * Task card component
 * Renders a single task item
 */
function TaskCard({ task }: { task: TaskResponseDTO }) {
  const isCompleted = task.completed;

  return (
    <div
      class={`rounded-lg border bg-card p-4 shadow-sm transition-all hover:shadow-md ${
        isCompleted ? 'opacity-60' : ''
      }`}
    >
      <div class="flex items-start gap-3">
        {/* Completion Checkbox (read-only for US1) */}
        <div class="flex h-5 items-center pt-0.5">
          <input
            type="checkbox"
            checked={isCompleted}
            disabled
            class="h-4 w-4 rounded border-gray-300 text-primary focus:ring-primary disabled:cursor-not-allowed disabled:opacity-50"
            aria-label={`Task ${task.title} is ${isCompleted ? 'completed' : 'not completed'}`}
          />
        </div>

        {/* Task Content */}
        <div class="flex-1 space-y-2">
          {/* Title */}
          <h3
            class={`text-base font-medium ${
              isCompleted ? 'line-through text-muted-foreground' : 'text-foreground'
            }`}
          >
            {task.title}
          </h3>

          {/* Description */}
          {task.description && (
            <p class="text-sm text-muted-foreground line-clamp-2">
              {task.description}
            </p>
          )}

          {/* Badges */}
          <div class="flex flex-wrap items-center gap-2">
            {task.category && (
              <CategoryBadge
                name={task.category.name || 'Uncategorized'}
                colorCode={task.category.colorCode}
              />
            )}
            {task.priority && <PriorityBadge priority={task.priority} />}
          </div>

          {/* Metadata */}
          <div class="flex items-center gap-4 text-xs text-muted-foreground">
            {task.completedAt && (
              <span>
                Completed:{' '}
                {new Date(task.completedAt).toLocaleDateString()}
              </span>
            )}
            <span>
              Created: {new Date(task.createdAt || '').toLocaleDateString()}
            </span>
          </div>
        </div>
      </div>
    </div>
  );
}

/**
 * Skeleton loader component
 * Displays during loading state
 */
function TaskSkeleton() {
  return (
    <div class="animate-pulse rounded-lg border bg-card p-4 shadow-sm">
      <div class="flex items-start gap-3">
        <div class="h-4 w-4 rounded bg-muted"></div>
        <div class="flex-1 space-y-2">
          <div class="h-5 w-3/4 rounded bg-muted"></div>
          <div class="h-4 w-full rounded bg-muted"></div>
          <div class="flex gap-2">
            <div class="h-6 w-20 rounded-full bg-muted"></div>
            <div class="h-6 w-16 rounded-full bg-muted"></div>
          </div>
        </div>
      </div>
    </div>
  );
}

/**
 * Empty state component
 * Displayed when no tasks match the current filters
 */
function EmptyState({ hasFilters }: { hasFilters: boolean }) {
  return (
    <div class="flex flex-col items-center justify-center rounded-lg border-2 border-dashed bg-muted/50 p-12 text-center">
      <svg
        class="mb-4 h-16 w-16 text-muted-foreground"
        fill="none"
        stroke="currentColor"
        viewBox="0 0 24 24"
        xmlns="http://www.w3.org/2000/svg"
      >
        <path
          stroke-linecap="round"
          stroke-linejoin="round"
          stroke-width="2"
          d="M9 5H7a2 2 0 00-2 2v12a2 2 0 002 2h10a2 2 0 002-2V7a2 2 0 00-2-2h-2M9 5a2 2 0 002 2h2a2 2 0 002-2M9 5a2 2 0 012-2h2a2 2 0 012 2"
        ></path>
      </svg>
      <h3 class="mb-2 text-lg font-semibold text-foreground">
        {hasFilters ? 'No tasks found' : 'No tasks yet'}
      </h3>
      <p class="mb-4 text-sm text-muted-foreground">
        {hasFilters
          ? 'Try adjusting your filters to see more tasks.'
          : 'Create your first task to get started with organizing your work.'}
      </p>
      {!hasFilters && (
        <button
          type="button"
          class="rounded-md bg-primary px-4 py-2 text-sm font-medium text-primary-foreground hover:bg-primary/90 focus:outline-none focus:ring-2 focus:ring-primary focus:ring-offset-2"
        >
          Create Task
        </button>
      )}
    </div>
  );
}

/**
 * Error state component
 * Displayed when the API request fails
 */
function ErrorState({ error }: { error: Error }) {
  return (
    <div class="rounded-lg border border-destructive bg-destructive/10 p-6">
      <div class="flex items-start gap-3">
        <svg
          class="h-6 w-6 flex-shrink-0 text-destructive"
          fill="none"
          stroke="currentColor"
          viewBox="0 0 24 24"
          xmlns="http://www.w3.org/2000/svg"
        >
          <path
            stroke-linecap="round"
            stroke-linejoin="round"
            stroke-width="2"
            d="M12 9v2m0 4h.01m-6.938 4h13.856c1.54 0 2.502-1.667 1.732-3L13.732 4c-.77-1.333-2.694-1.333-3.464 0L3.34 16c-.77 1.333.192 3 1.732 3z"
          ></path>
        </svg>
        <div class="flex-1">
          <h3 class="text-base font-semibold text-destructive">
            Failed to load tasks
          </h3>
          <p class="mt-1 text-sm text-destructive/90">
            {error.message || 'An unexpected error occurred. Please try again.'}
          </p>
          <button
            type="button"
            onClick={() => window.location.reload()}
            class="mt-3 text-sm font-medium text-destructive underline hover:no-underline"
          >
            Reload page
          </button>
        </div>
      </div>
    </div>
  );
}

/**
 * Main TaskList component
 *
 * Fetches and displays tasks with filtering, loading, and error states.
 */
export default function TaskList() {
  // Subscribe to Nano Stores atoms for reactive state
  const filter = useStore(taskFilter);
  const page = useStore(currentPage);
  const perPage = useStore(tasksPerPage);

  // Fetch tasks from API using generated TanStack Query hook
  const { data: tasks, isLoading, error } = useGetApiTasks({
    category: filter.category,
    priority: filter.priority,
    status: filter.status,
    page,
    size: perPage,
  });

  // Check if any filters are active
  const hasActiveFilters = Boolean(
    filter.category || filter.priority || filter.status
  );

  // Loading state: Show skeleton loaders
  if (isLoading) {
    return (
      <div class="space-y-4">
        {Array.from({ length: 3 }).map((_, i) => (
          <TaskSkeleton key={i} />
        ))}
      </div>
    );
  }

  // Error state: Show error message
  if (error) {
    return <ErrorState error={error as Error} />;
  }

  // Empty state: No tasks found
  if (!tasks || tasks.length === 0) {
    return <EmptyState hasFilters={hasActiveFilters} />;
  }

  // Success state: Render task list
  return (
    <div class="space-y-4">
      {/* Task count header */}
      <div class="flex items-center justify-between">
        <p class="text-sm text-muted-foreground">
          Showing {tasks.length} {tasks.length === 1 ? 'task' : 'tasks'}
        </p>
      </div>

      {/* Task grid */}
      <div class="grid gap-4 sm:grid-cols-1 lg:grid-cols-2">
        {tasks.map((task) => (
          <TaskCard key={task.id} task={task} />
        ))}
      </div>
    </div>
  );
}
