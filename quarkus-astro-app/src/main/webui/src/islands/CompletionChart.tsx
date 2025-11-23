/**
 * CompletionChart Island Component (T499-T506)
 *
 * Interactive Preact island for displaying task completion history as a chart.
 * Shows daily task completion counts over a specified time period.
 *
 * Features:
 * - Fetches completion history from API using TanStack Query
 * - Renders bar chart with daily completion counts
 * - Interactive tooltips showing exact counts
 * - Customizable date range (default 30 days)
 * - Loading states with skeleton loader
 * - Error handling with user-friendly messages
 * - Responsive layout
 *
 * Usage in Astro pages:
 * ```astro
 * ---
 * import CompletionChart from '@/islands/CompletionChart';
 * ---
 * <CompletionChart client:load days={30} />
 * ```
 */

import { useState, useEffect } from 'preact/hooks';
import { useGetApiStatsHistory } from '@/lib/api/endpoints/statistics/statistics';
import { QueryProvider } from '@/components/providers/QueryProvider';
import { trackIslandHydration } from '@/lib/performance';
import {
  BarChart,
  Bar,
  XAxis,
  YAxis,
  CartesianGrid,
  Tooltip,
  ResponsiveContainer,
  Legend,
} from 'recharts';
import { Button } from '@/components/ui/button';

/**
 * Chart skeleton loader
 */
function ChartSkeleton() {
  return (
    <div class="animate-pulse rounded-lg border bg-card p-6 shadow-sm">
      <div class="space-y-4">
        <div class="h-6 w-48 rounded bg-muted"></div>
        <div class="h-[300px] rounded bg-muted"></div>
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
            Failed to load chart data
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
 * Empty state component
 */
function EmptyState() {
  return (
    <div class="flex flex-col items-center justify-center rounded-lg border-2 border-dashed bg-muted/50 p-12 text-center">
      <svg
        class="mb-4 h-16 w-16 text-muted-foreground"
        fill="none"
        stroke="currentColor"
        viewBox="0 0 24 24"
      >
        <path
          stroke-linecap="round"
          stroke-linejoin="round"
          stroke-width="2"
          d="M9 19v-6a2 2 0 00-2-2H5a2 2 0 00-2 2v6a2 2 0 002 2h2a2 2 0 002-2zm0 0V9a2 2 0 012-2h2a2 2 0 012 2v10m-6 0a2 2 0 002 2h2a2 2 0 002-2m0 0V5a2 2 0 012-2h2a2 2 0 012 2v14a2 2 0 01-2 2h-2a2 2 0 01-2-2z"
        ></path>
      </svg>
      <h3 class="mb-2 text-lg font-semibold text-foreground">
        No completion history
      </h3>
      <p class="text-sm text-muted-foreground">
        Complete some tasks to see your history here.
      </p>
    </div>
  );
}

/**
 * Custom tooltip for the chart
 */
interface CustomTooltipProps {
  active?: boolean;
  payload?: Array<{ payload: { date: string; count: number } }>;
}

function CustomTooltip({ active, payload }: CustomTooltipProps) {
  if (active && payload?.length) {
    const data = payload[0].payload;
    return (
      <div class="rounded-lg border bg-card p-3 shadow-lg">
        <p class="text-sm font-medium text-foreground">
          {new Date(data.date).toLocaleDateString('en-US', {
            month: 'short',
            day: 'numeric',
            year: 'numeric',
          })}
        </p>
        <p class="text-sm text-muted-foreground">
          {data.count} {data.count === 1 ? 'task' : 'tasks'} completed
        </p>
      </div>
    );
  }
  return null;
}

/**
 * Internal CompletionChart component (T501-T506)
 *
 * Fetches and displays completion history chart with loading and error states.
 */
function CompletionChartContent({ days = 30 }: { days?: number }) {
  // State for date range selection
  const [selectedDays, setSelectedDays] = useState(days);

  // T543: Track island hydration for performance monitoring
  useEffect(() => {
    trackIslandHydration('CompletionChart');
  }, []);

  // T502: Use useGetApiStatsHistory to fetch history data
  const { data: history, isLoading, error } = useGetApiStatsHistory({
    days: selectedDays,
  });

  // T505: Loading state
  if (isLoading) {
    return <ChartSkeleton />;
  }

  // T505: Error state
  if (error) {
    return <ErrorState error={error as Error} />;
  }

  // T505: No data state
  if (!history || history.length === 0) {
    return <EmptyState />;
  }

  // Format data for recharts
  const chartData = history.map((entry) => ({
    date: entry.date,
    count: entry.count,
    formattedDate: new Date(entry.date).toLocaleDateString('en-US', {
      month: 'short',
      day: 'numeric',
    }),
  }));

  // Calculate total for the period
  const totalCount = history.reduce((sum, entry) => sum + entry.count, 0);

  // T503-T506: Render responsive bar chart
  return (
    <div class="rounded-lg border bg-card p-6 shadow-sm">
      <div class="mb-6 flex items-center justify-between">
        <div>
          <h2 class="text-2xl font-bold text-foreground">Completion History</h2>
          <p class="text-sm text-muted-foreground">
            {totalCount} {totalCount === 1 ? 'task' : 'tasks'} completed in the last{' '}
            {selectedDays} {selectedDays === 1 ? 'day' : 'days'}
          </p>
        </div>

        {/* Date range selector */}
        <div class="flex gap-2">
          <Button
            size="sm"
            variant={selectedDays === 7 ? 'default' : 'outline'}
            onClick={() => setSelectedDays(7)}
          >
            7 days
          </Button>
          <Button
            size="sm"
            variant={selectedDays === 30 ? 'default' : 'outline'}
            onClick={() => setSelectedDays(30)}
          >
            30 days
          </Button>
          <Button
            size="sm"
            variant={selectedDays === 90 ? 'default' : 'outline'}
            onClick={() => setSelectedDays(90)}
          >
            90 days
          </Button>
        </div>
      </div>

      {/* T503: Responsive bar chart */}
      <ResponsiveContainer width="100%" height={300}>
        <BarChart data={chartData}>
          <CartesianGrid strokeDasharray="3 3" stroke="hsl(var(--muted-foreground) / 0.2)" />
          <XAxis
            dataKey="formattedDate"
            stroke="hsl(var(--muted-foreground))"
            fontSize={12}
            tickLine={false}
            axisLine={false}
          />
          <YAxis
            stroke="hsl(var(--muted-foreground))"
            fontSize={12}
            tickLine={false}
            axisLine={false}
            allowDecimals={false}
          />
          {/* T504: Add tooltips */}
          <Tooltip content={<CustomTooltip />} cursor={{ fill: 'hsl(var(--muted))' }} />
          <Legend />
          <Bar
            dataKey="count"
            name="Tasks Completed"
            fill="hsl(var(--primary))"
            radius={[4, 4, 0, 0]}
          />
        </BarChart>
      </ResponsiveContainer>
    </div>
  );
}

/**
 * Main CompletionChart component wrapped with QueryProvider
 *
 * This wrapper ensures React Query context is available for all hooks
 */
export default function CompletionChart({ days = 30 }: { days?: number }) {
  return (
    <QueryProvider>
      <CompletionChartContent days={days} />
    </QueryProvider>
  );
}
