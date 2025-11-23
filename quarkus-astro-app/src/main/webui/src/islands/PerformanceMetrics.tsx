/**
 * PerformanceMetrics Island Component (T549-T556)
 *
 * Interactive Preact island for displaying Islands Architecture performance metrics.
 * Shows Web Vitals, bundle sizes, and individual island hydration times.
 *
 * Features:
 * - Collects FCP, LCP, TTI metrics using Performance API
 * - Displays island-by-island hydration times
 * - Highlights slow islands (>200ms)
 * - Visual indicators for good/needs-improvement/poor metrics
 * - Export metrics as JSON for analysis
 *
 * Usage in Astro pages:
 * ```astro
 * ---
 * import PerformanceMetrics from '@/islands/PerformanceMetrics';
 * ---
 * <PerformanceMetrics client:load />
 * ```
 */

import { useState, useEffect } from 'preact/hooks';
import {
  collectMetrics,
  formatDuration,
  formatBytes,
  exportMetrics,
  trackIslandHydration,
} from '@/lib/performance';
import type { PerformanceMetrics as PerfMetrics, IslandHydration } from '@/types/performance';
import { WEB_VITALS_THRESHOLDS, getMetricStatus } from '@/types/performance';
import { Button } from '@/components/ui/button';
import { CheckCircle2, AlertTriangle, XCircle, Download } from 'lucide-preact';

/**
 * Metric card component
 * Displays a single performance metric with status indicator
 */
function MetricCard({
  label,
  value,
  threshold,
  unit = 'ms',
  description,
}: {
  label: string;
  value?: number;
  threshold?: { good: number; needsImprovement: number };
  unit?: string;
  description?: string;
}) {
  if (value === undefined) {
    return (
      <div class="rounded-lg border bg-card p-6 shadow-sm">
        <div class="space-y-2">
          <p class="text-sm font-medium text-muted-foreground">{label}</p>
          <p class="text-lg text-muted-foreground">Measuring...</p>
          {description && <p class="text-xs text-muted-foreground">{description}</p>}
        </div>
      </div>
    );
  }

  let status: 'good' | 'needs-improvement' | 'poor' = 'good';
  let StatusIcon = CheckCircle2;
  let iconColor = 'text-green-500';

  if (threshold) {
    status = getMetricStatus(value, threshold.good, threshold.needsImprovement);
    if (status === 'needs-improvement') {
      StatusIcon = AlertTriangle;
      iconColor = 'text-yellow-500';
    } else if (status === 'poor') {
      StatusIcon = XCircle;
      iconColor = 'text-red-500';
    }
  }

  const displayValue = unit === 'ms' ? formatDuration(value) : unit === 'bytes' ? formatBytes(value) : `${value}${unit}`;

  return (
    <div class="rounded-lg border bg-card p-6 shadow-sm">
      <div class="flex items-start justify-between">
        <div class="space-y-1">
          <p class="text-sm font-medium text-muted-foreground">{label}</p>
          <p class="text-3xl font-bold text-foreground">{displayValue}</p>
          {description && <p class="text-xs text-muted-foreground">{description}</p>}
        </div>
        <div class={`rounded-md p-2 ${iconColor}`}>
          <StatusIcon size={24} />
        </div>
      </div>
    </div>
  );
}

/**
 * Island hydration table component
 * Lists all islands with their hydration times
 */
function HydrationTable({ hydrations }: { hydrations: IslandHydration[] }) {
  if (hydrations.length === 0) {
    return (
      <div class="rounded-lg border border-muted bg-muted/50 p-6 text-center">
        <p class="text-sm text-muted-foreground">No island hydration data available yet</p>
        <p class="mt-2 text-xs text-muted-foreground">
          Islands will appear here as they hydrate on the page
        </p>
      </div>
    );
  }

  return (
    <div class="overflow-hidden rounded-lg border shadow-sm">
      <table class="w-full">
        <thead class="bg-muted">
          <tr>
            <th class="px-4 py-3 text-left text-sm font-medium text-foreground">Island Name</th>
            <th class="px-4 py-3 text-right text-sm font-medium text-foreground">Hydration Time</th>
            <th class="px-4 py-3 text-center text-sm font-medium text-foreground">Status</th>
          </tr>
        </thead>
        <tbody class="bg-card">
          {hydrations.map((hydration, index) => {
            const isSlowStatus = hydration.durationMs > WEB_VITALS_THRESHOLDS.ISLAND_HYDRATION_TARGET;
            const statusColor = isSlowStatus ? 'text-red-500' : 'text-green-500';
            const rowBg = index % 2 === 0 ? 'bg-card' : 'bg-muted/30';

            return (
              <tr key={`${hydration.islandName}-${index}`} class={rowBg}>
                <td class="px-4 py-3 text-sm font-medium text-foreground">{hydration.islandName}</td>
                <td class="px-4 py-3 text-right text-sm text-foreground">
                  {formatDuration(hydration.durationMs)}
                </td>
                <td class="px-4 py-3 text-center">
                  <span class={`inline-flex items-center gap-1 text-sm font-medium ${statusColor}`}>
                    {isSlowStatus ? (
                      <>
                        <XCircle size={16} /> Slow
                      </>
                    ) : (
                      <>
                        <CheckCircle2 size={16} /> Fast
                      </>
                    )}
                  </span>
                </td>
              </tr>
            );
          })}
        </tbody>
      </table>
      <div class="border-t bg-muted px-4 py-2">
        <p class="text-xs text-muted-foreground">
          Target: Individual islands should hydrate in under {WEB_VITALS_THRESHOLDS.ISLAND_HYDRATION_TARGET}ms
        </p>
      </div>
    </div>
  );
}

/**
 * Main PerformanceMetrics component
 */
function PerformanceMetricsContent() {
  const [metrics, setMetrics] = useState<PerfMetrics | null>(null);
  const [isCollecting, setIsCollecting] = useState(true);

  // T551: Track island hydration for performance monitoring
  useEffect(() => {
    trackIslandHydration('PerformanceMetrics');
  }, []);

  // Collect metrics after component mounts and page is interactive
  useEffect(() => {
    const collect = async () => {
      setIsCollecting(true);

      // Wait for page to be fully interactive
      await new Promise(resolve => {
        if (document.readyState === 'complete') {
          resolve(null);
        } else {
          window.addEventListener('load', () => resolve(null), { once: true });
        }
      });

      // Small delay to ensure all islands have hydrated
      await new Promise(resolve => setTimeout(resolve, 1000));

      const collected = await collectMetrics('performance');
      setMetrics(collected);
      setIsCollecting(false);
    };

    void collect();

    // Re-collect metrics every 5 seconds to catch late-hydrating islands
    const interval = setInterval(() => {
      void collectMetrics('performance').then(setMetrics);
    }, 5000);

    return () => clearInterval(interval);
  }, []);

  const handleExport = () => {
    if (metrics) {
      exportMetrics(metrics);
    }
  };

  // Estimated bundle size (will be updated with actual build size)
  // T547: Document bundle size
  const estimatedBundleSize = 85 * 1024; // ~85KB (to be measured from actual build)

  return (
    <div class="space-y-6">
      {/* Header */}
      <div class="flex items-center justify-between">
        <div>
          <h2 class="text-2xl font-bold text-foreground">Performance Metrics</h2>
          <p class="mt-1 text-sm text-muted-foreground">
            Real-time Web Vitals and Islands Architecture performance data
          </p>
        </div>
        <Button
          onClick={handleExport}
          disabled={!metrics || isCollecting}
          variant="outline"
          class="gap-2"
        >
          <Download size={16} />
          Export JSON
        </Button>
      </div>

      {/* Web Vitals Metrics */}
      <div>
        <h3 class="mb-4 text-lg font-semibold text-foreground">Web Vitals</h3>
        <div class="grid gap-4 md:grid-cols-2 lg:grid-cols-4">
          <MetricCard
            label="First Contentful Paint"
            value={metrics?.firstContentfulPaint}
            threshold={{
              good: WEB_VITALS_THRESHOLDS.FCP_GOOD,
              needsImprovement: WEB_VITALS_THRESHOLDS.FCP_NEEDS_IMPROVEMENT,
            }}
            unit="ms"
            description="Time until first content appears"
          />
          <MetricCard
            label="Largest Contentful Paint"
            value={metrics?.largestContentfulPaint}
            threshold={{
              good: WEB_VITALS_THRESHOLDS.LCP_GOOD,
              needsImprovement: WEB_VITALS_THRESHOLDS.LCP_NEEDS_IMPROVEMENT,
            }}
            unit="ms"
            description="Time until largest content is visible"
          />
          <MetricCard
            label="Time to Interactive"
            value={metrics?.timeToInteractive}
            threshold={{
              good: WEB_VITALS_THRESHOLDS.TTI_GOOD,
              needsImprovement: WEB_VITALS_THRESHOLDS.TTI_NEEDS_IMPROVEMENT,
            }}
            unit="ms"
            description="Time until page is fully interactive"
          />
          <MetricCard
            label="Bundle Size (gzipped)"
            value={estimatedBundleSize}
            threshold={{
              good: WEB_VITALS_THRESHOLDS.BUNDLE_SIZE_TARGET,
              needsImprovement: WEB_VITALS_THRESHOLDS.BUNDLE_SIZE_TARGET * 1.5,
            }}
            unit="bytes"
            description="Total JavaScript bundle size"
          />
        </div>
      </div>

      {/* Island Hydration Times */}
      <div>
        <h3 class="mb-4 text-lg font-semibold text-foreground">Island Hydration Performance</h3>
        <HydrationTable hydrations={metrics?.islandHydrations ?? []} />
      </div>

      {/* Educational Note */}
      <div class="rounded-lg border border-blue-200 bg-blue-50 p-4 dark:border-blue-900 dark:bg-blue-950/30">
        <h4 class="font-semibold text-blue-900 dark:text-blue-100">
          About Islands Architecture
        </h4>
        <p class="mt-2 text-sm text-blue-800 dark:text-blue-200">
          This application uses Islands Architecture, which ships minimal JavaScript by only hydrating
          interactive components. Static content remains as pure HTML, resulting in faster load times
          and better performance compared to traditional SPAs.
        </p>
        <ul class="mt-3 space-y-1 text-sm text-blue-800 dark:text-blue-200">
          <li class="flex items-start gap-2">
            <CheckCircle2 size={16} class="mt-0.5 flex-shrink-0" />
            <span>Only interactive islands load JavaScript</span>
          </li>
          <li class="flex items-start gap-2">
            <CheckCircle2 size={16} class="mt-0.5 flex-shrink-0" />
            <span>Static content loads instantly as HTML</span>
          </li>
          <li class="flex items-start gap-2">
            <CheckCircle2 size={16} class="mt-0.5 flex-shrink-0" />
            <span>Each island hydrates independently</span>
          </li>
        </ul>
      </div>

      {isCollecting && (
        <div class="text-center text-sm text-muted-foreground">
          <p>Collecting performance metrics...</p>
        </div>
      )}
    </div>
  );
}

/**
 * Wrapper component with QueryProvider (not needed for performance metrics, but keeping pattern)
 */
export default function PerformanceMetrics() {
  return <PerformanceMetricsContent />;
}
