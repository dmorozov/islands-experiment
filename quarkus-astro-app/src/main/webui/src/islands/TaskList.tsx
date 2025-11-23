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

import { useState, useEffect } from 'preact/hooks';
import { useStore } from '@nanostores/preact';
import { taskFilter, currentPage, tasksPerPage } from '@/lib/state';
import {
  useGetApiTasks,
  useDeleteApiTasksId,
  usePatchApiTasksIdComplete,
  getGetApiTasksQueryKey
} from '@/lib/api/endpoints/tasks/tasks';
import type { TaskResponseDTO } from '@/lib/api/model';
import { QueryProvider } from '@/components/providers/QueryProvider';
import { useQueryClient } from '@tanstack/react-query';
import TaskForm from './TaskForm';
import { trackIslandHydration } from '@/lib/performance';
import { Button } from '@/components/ui/button';
import { Checkbox } from '@/components/ui/checkbox';
import {
  AlertDialog,
  AlertDialogAction,
  AlertDialogCancel,
  AlertDialogContent,
  AlertDialogDescription,
  AlertDialogFooter,
  AlertDialogHeader,
  AlertDialogTitle,
  AlertDialogTrigger,
} from '@/components/ui/alert-dialog';

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
        color: colorCode ??  'hsl(var(--muted-foreground))',
      }}
    >
      {name}
    </span>
  );
}

/**
 * Task card component (T327-T334, T481-T491)
 * Renders a single task item with edit, delete, and completion toggle functionality
 */
function TaskCard({
  task,
  onEdit,
  onDelete,
  onToggleComplete,
}: {
  task: TaskResponseDTO;
  onEdit: () => void;
  onDelete: () => void;
  onToggleComplete: (taskId: string) => void;
}) {
  const isCompleted = task.completed;

  return (
    <div
      class={`rounded-lg border bg-card p-4 shadow-sm transition-all hover:shadow-md ${
        isCompleted ? 'opacity-60' : ''
      }`}
    >
      <div class="flex items-start gap-3">
        {/* T481: Completion Checkbox - now interactive */}
        <div class="flex h-5 items-center pt-0.5">
          <Checkbox
            checked={isCompleted}
            onCheckedChange={() => onToggleComplete(task.id)}
            aria-label={`Mark task ${task.title} as ${isCompleted ? 'incomplete' : 'complete'}`}
          />
        </div>

        {/* Task Content */}
        <div class="flex-1 space-y-2">
          {/* Title */}
          <h3
            class={`text-base font-medium cursor-pointer hover:text-primary ${
              isCompleted ? 'line-through text-muted-foreground' : 'text-foreground'
            }`}
            onClick={onEdit}
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
                name={task.category.name ??  'Uncategorized'}
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
              Created: {new Date(task.createdAt ??  '').toLocaleDateString()}
            </span>
          </div>

          {/* T331: Actions - Edit and Delete buttons */}
          <div class="flex gap-2 pt-2">
            <Button size="sm" variant="outline" onClick={onEdit}>
              Edit
            </Button>

            {/* T332: Delete confirmation dialog */}
            <AlertDialog>
              <AlertDialogTrigger asChild>
                <Button size="sm" variant="destructive">
                  Delete
                </Button>
              </AlertDialogTrigger>
              <AlertDialogContent
                onCloseAutoFocus={(e: Event) => {
                  // Prevent auto-focus behavior that causes errors
                  e.preventDefault();
                }}
              >
                <AlertDialogHeader>
                  <AlertDialogTitle>Delete Task</AlertDialogTitle>
                  <AlertDialogDescription>
                    Are you sure you want to delete "{task.title}"? This action
                    cannot be undone.
                  </AlertDialogDescription>
                </AlertDialogHeader>
                <AlertDialogFooter>
                  <AlertDialogCancel>Cancel</AlertDialogCancel>
                  <AlertDialogAction onClick={onDelete}>
                    Delete
                  </AlertDialogAction>
                </AlertDialogFooter>
              </AlertDialogContent>
            </AlertDialog>
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
            {error.message ??  'An unexpected error occurred. Please try again.'}
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
 * Internal TaskList component (T327-T334, T473-T491)
 *
 * Fetches and displays tasks with filtering, loading, and error states.
 * Supports inline editing, deletion, and completion toggling with undo.
 */
function TaskListContent() {
  const queryClient = useQueryClient();

  // T328: Add state to track selected task ID for editing
  const [editingTaskId, setEditingTaskId] = useState<string | null>(null);

  // T477: Add state for tracking last completed task (for undo)
  const [lastCompletedTask, setLastCompletedTask] = useState<{
    id: string;
    title: string;
    wasCompleted: boolean;
  } | null>(null);

  // Subscribe to Nano Stores atoms for reactive state
  const filter = useStore(taskFilter);
  const page = useStore(currentPage);
  const perPage = useStore(tasksPerPage);

  // T535: Track island hydration for performance monitoring
  useEffect(() => {
    trackIslandHydration('TaskList');
  }, []);

  // Fetch tasks from API using generated TanStack Query hook
  const { data: tasks, isLoading, error } = useGetApiTasks({
    category: filter.category,
    priority: filter.priority,
    status: filter.status,
    page,
    size: perPage,
  });

  // T333: Call useDeleteTask mutation
  const deleteMutation = useDeleteApiTasksId();

  // T476: Call usePatchApiTasksIdComplete mutation
  const toggleCompleteMutation = usePatchApiTasksIdComplete();

  // Check if any filters are active
  const hasActiveFilters = Boolean(
    filter.category ?? filter.priority ?? filter.status
  );

  // T327: Handle task edit
  const handleEdit = (taskId: string) => {
    setEditingTaskId(taskId);
  };

  // T330: After update successful, clear selected task ID to exit edit mode
  const handleEditSuccess = () => {
    setEditingTaskId(null);
  };

  // T333-T334: Handle task delete with optimistic update
  const handleDelete = async (taskId: string) => {
    try {
      await deleteMutation.mutateAsync({ id: taskId });

      // Invalidate tasks query cache to refetch
      void queryClient.invalidateQueries({
        queryKey: getGetApiTasksQueryKey(),
      });
    } catch (error) {
      console.error('Failed to delete task:', error);
      alert('Unable to delete task. Please try again.');
    }
  };

  // T478-T480: Handle task completion toggle with undo functionality
  const handleToggleComplete = async (taskId: string) => {
    const task = tasks?.find((t) => t.id === taskId);
    if (!task) return;

    // Store for undo
    setLastCompletedTask({
      id: task.id,
      title: task.title,
      wasCompleted: task.completed,
    });

    try {
      // T479: Call toggle completion mutation
      await toggleCompleteMutation.mutateAsync({ id: taskId });

      // Invalidate tasks query cache to refetch
      void queryClient.invalidateQueries({
        queryKey: getGetApiTasksQueryKey(),
      });

      // Auto-dismiss undo notification after 5 seconds
      setTimeout(() => {
        setLastCompletedTask((current) =>
          current?.id === taskId ? null : current
        );
      }, 5000);
    } catch (error) {
      console.error('Failed to toggle task completion:', error);
      alert('Unable to update task. Please try again.');
      setLastCompletedTask(null);
    }
  };

  // T480: Handle undo completion toggle
  const handleUndoComplete = async () => {
    if (!lastCompletedTask) return;

    try {
      await toggleCompleteMutation.mutateAsync({ id: lastCompletedTask.id });

      // Invalidate tasks query cache to refetch
      void queryClient.invalidateQueries({
        queryKey: getGetApiTasksQueryKey(),
      });

      setLastCompletedTask(null);
    } catch (error) {
      console.error('Failed to undo completion:', error);
      alert('Unable to undo. Please try again.');
    }
  };

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

  // T414: Sort tasks by priority (HIGH first, then MEDIUM, then LOW)
  const priorityOrder = { HIGH: 0, MEDIUM: 1, LOW: 2 };
  const sortedTasks = [...tasks].sort((a, b) => {
    const aPriority = priorityOrder[a.priority as keyof typeof priorityOrder] ?? 3;
    const bPriority = priorityOrder[b.priority as keyof typeof priorityOrder] ?? 3;
    return aPriority - bPriority;
  });

  // Success state: Render task list
  return (
    <div class="space-y-4">
      {/* T489: Undo notification banner */}
      {lastCompletedTask && (
        <div class="flex items-center justify-between rounded-lg border border-primary bg-primary/10 p-4">
          <p class="text-sm text-foreground">
            Task "{lastCompletedTask.title}" marked as{' '}
            {lastCompletedTask.wasCompleted ? 'incomplete' : 'complete'}
          </p>
          <Button
            size="sm"
            variant="outline"
            onClick={handleUndoComplete}
            disabled={toggleCompleteMutation.isPending}
          >
            Undo
          </Button>
        </div>
      )}

      {/* Task count header */}
      <div class="flex items-center justify-between">
        <p class="text-sm text-muted-foreground">
          Showing {sortedTasks.length} {sortedTasks.length === 1 ? 'task' : 'tasks'}
        </p>
      </div>

      {/* Task grid */}
      <div class="grid gap-4 sm:grid-cols-1 lg:grid-cols-2">
        {sortedTasks.map((task) => (
          <div key={task.id}>
            {/* T329: When task clicked, render TaskForm inline with mode="edit" */}
            {editingTaskId === task.id ? (
              <TaskForm
                mode="edit"
                initialTask={task}
                onSuccess={handleEditSuccess}
                onCancel={() => setEditingTaskId(null)}
              />
            ) : (
              <TaskCard
                task={task}
                onEdit={() => handleEdit(task.id)}
                onDelete={() => void handleDelete(task.id)}
                onToggleComplete={(id) => void handleToggleComplete(id)}
              />
            )}
          </div>
        ))}
      </div>
    </div>
  );
}

/**
 * Main TaskList component wrapped with QueryProvider
 *
 * This wrapper ensures React Query context is available for all hooks
 */
export default function TaskList() {
  return (
    <QueryProvider>
      <TaskListContent />
    </QueryProvider>
  );
}
