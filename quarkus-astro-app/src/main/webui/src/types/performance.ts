/**
 * Performance measurement types for Islands Architecture demonstration
 */

/**
 * Represents a single island's hydration metrics
 */
export interface IslandHydration {
  /** Name of the island component */
  islandName: string;
  /** Time taken to hydrate in milliseconds */
  durationMs: number;
  /** Timestamp when hydration started */
  startTime?: number;
  /** Timestamp when hydration completed */
  endTime?: number;
}

/**
 * Complete performance metrics for a page
 */
export interface PerformanceMetrics {
  /** Name of the page being measured */
  pageName: string;
  /** Total bundle size in bytes */
  bundleSize?: number;
  /** Array of island hydration measurements */
  islandHydrations: IslandHydration[];
  /** Time to Interactive in milliseconds */
  timeToInteractive?: number;
  /** First Contentful Paint in milliseconds */
  firstContentfulPaint?: number;
  /** Largest Contentful Paint in milliseconds */
  largestContentfulPaint?: number;
  /** Timestamp when metrics were collected */
  timestamp: number;
}

/**
 * Web Vitals thresholds based on Google's recommendations
 */
export const WEB_VITALS_THRESHOLDS = {
  /** FCP should be under 1.5 seconds (good) */
  FCP_GOOD: 1500,
  /** FCP warning threshold (needs improvement) */
  FCP_NEEDS_IMPROVEMENT: 2500,
  /** LCP should be under 2.5 seconds (good) */
  LCP_GOOD: 2500,
  /** LCP warning threshold (needs improvement) */
  LCP_NEEDS_IMPROVEMENT: 4000,
  /** TTI should be under 3.8 seconds (good) */
  TTI_GOOD: 3800,
  /** TTI warning threshold (needs improvement) */
  TTI_NEEDS_IMPROVEMENT: 7300,
  /** Individual island hydration should be under 200ms */
  ISLAND_HYDRATION_TARGET: 200,
  /** Bundle size should be under 100KB (gzipped) */
  BUNDLE_SIZE_TARGET: 100 * 1024,
} as const;

/**
 * Metric status based on thresholds
 */
export type MetricStatus = 'good' | 'needs-improvement' | 'poor';

/**
 * Helper to determine metric status
 */
export function getMetricStatus(
  value: number,
  goodThreshold: number,
  needsImprovementThreshold: number
): MetricStatus {
  if (value <= goodThreshold) return 'good';
  if (value <= needsImprovementThreshold) return 'needs-improvement';
  return 'poor';
}
