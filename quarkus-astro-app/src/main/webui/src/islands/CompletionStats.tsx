/**
 * CompletionStats Island Component (T492-T498)
 *
 * Interactive Preact island for displaying task completion statistics.
 * Shows task completion counts and completion rate with real-time updates.
 *
 * Features:
 * - Fetches completion stats from API using TanStack Query
 * - Displays today, week, and total completion counts
 * - Shows overall completion rate as percentage
 * - Loading states with skeleton loaders
 * - Error handling with user-friendly messages
 * - Responsive card layout
 *
 * Usage in Astro pages:
 * ```astro
 * ---
 * import CompletionStats from '@/islands/CompletionStats';
 * ---
 * <CompletionStats client:load />
 * ```
 */

import { useEffect } from 'preact/hooks';
import { useGetApiStatsSummary } from '@/lib/api/endpoints/statistics/statistics';
import { QueryProvider } from '@/components/providers/QueryProvider';
import { trackIslandHydration } from '@/lib/performance';

/**
 * Stat card component
 * Displays a single statistic with label and value
 */
function StatCard({
  label,
  value,
  description,
  icon,
}: {
  label: string;
  value: string | number;
  description?: string;
  icon?: string;
}) {
  return (
    <div class="rounded-lg border bg-card p-6 shadow-sm">
      <div class="flex items-start justify-between">
        <div class="space-y-1">
          <p class="text-sm font-medium text-muted-foreground">{label}</p>
          <p class="text-3xl font-bold text-foreground">{value}</p>
          {description && (
            <p class="text-xs text-muted-foreground">{description}</p>
          )}
        </div>
        {icon && (
          <div class="rounded-md bg-primary/10 p-2">
            <span class="text-2xl">{icon}</span>
          </div>
        )}
      </div>
    </div>
  );
}

/**
 * Skeleton loader for stat card
 */
function StatCardSkeleton() {
  return (
    <div class="animate-pulse rounded-lg border bg-card p-6 shadow-sm">
      <div class="space-y-2">
        <div class="h-4 w-24 rounded bg-muted"></div>
        <div class="h-8 w-16 rounded bg-muted"></div>
        <div class="h-3 w-32 rounded bg-muted"></div>
      </div>
    </div>
  );
}

/**
 * Error state component
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
            Failed to load statistics
          </h3>
          <p class="mt-1 text-sm text-destructive/90">
            {error.message ??  'An unexpected error occurred. Please try again.'}
          </p>
        </div>
      </div>
    </div>
  );
}

/**
 * Internal CompletionStats component (T493-T498)
 *
 * Fetches and displays completion statistics with loading and error states.
 */
function CompletionStatsContent() {
  // T542: Track island hydration for performance monitoring
  useEffect(() => {
    trackIslandHydration('CompletionStats');
  }, []);

  // T494: Use useGetApiStatsSummary to fetch stats
  const { data: stats, isLoading, error } = useGetApiStatsSummary();

  // T497: Loading state
  if (isLoading) {
    return (
      <div class="grid gap-4 md:grid-cols-2 lg:grid-cols-4">
        {Array.from({ length: 4 }).map((_, i) => (
          <StatCardSkeleton key={i} />
        ))}
      </div>
    );
  }

  // T497: Error state
  if (error) {
    return <ErrorState error={error as Error} />;
  }

  // T497: No data state
  if (!stats) {
    return (
      <div class="rounded-lg border border-muted bg-muted/50 p-6 text-center">
        <p class="text-sm text-muted-foreground">No statistics available</p>
      </div>
    );
  }

  // T495-T496: Display stats in responsive card layout
  return (
    <div>
      <div class="mb-4">
        <h2 class="text-2xl font-bold text-foreground">Completion Statistics</h2>
        <p class="text-sm text-muted-foreground">Track your task completion progress</p>
      </div>

      <div class="grid gap-4 md:grid-cols-2 lg:grid-cols-4">
        {/* T495: Today's completions */}
        <StatCard
          label="Today"
          value={stats.todayCount}
          description="Tasks completed today"
          icon="☀️"
        />

        {/* T495: This week's completions */}
        <StatCard
          label="This Week"
          value={stats.weekCount}
          description="Tasks completed this week"
          icon="📅"
        />

        {/* T495: Total completions */}
        <StatCard
          label="Total Completed"
          value={stats.totalCount}
          description="All completed tasks"
          icon="✅"
        />

        {/* T496: Completion rate as percentage */}
        <StatCard
          label="Completion Rate"
          value={`${stats.completionRate.toFixed(1)}%`}
          description="Overall completion rate"
          icon="📊"
        />
      </div>
    </div>
  );
}

/**
 * Main CompletionStats component wrapped with QueryProvider
 *
 * This wrapper ensures React Query context is available for all hooks
 */
export default function CompletionStats() {
  return (
    <QueryProvider>
      <CompletionStatsContent />
    </QueryProvider>
  );
}
