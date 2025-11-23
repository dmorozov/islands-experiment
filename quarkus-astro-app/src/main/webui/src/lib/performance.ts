/**
 * Performance measurement utilities for Islands Architecture demonstration
 * Uses the Performance API to collect Web Vitals and island hydration metrics
 */

import type { PerformanceMetrics, IslandHydration } from '../types/performance';

/**
 * Collect comprehensive performance metrics for a page
 * @param pageName - Name of the page being measured
 * @returns Promise that resolves with collected metrics
 */
export async function collectMetrics(pageName: string): Promise<PerformanceMetrics> {
  const metrics: PerformanceMetrics = {
    pageName,
    islandHydrations: [],
    timestamp: Date.now(),
  };

  // Collect First Contentful Paint (FCP)
  const fcpEntry = await getFirstContentfulPaint();
  if (fcpEntry) {
    metrics.firstContentfulPaint = fcpEntry.startTime;
  }

  // Collect Largest Contentful Paint (LCP)
  const lcpEntry = await getLargestContentfulPaint();
  if (lcpEntry) {
    metrics.largestContentfulPaint = lcpEntry.startTime;
  }

  // Collect Time to Interactive (TTI) - approximation using domInteractive
  const navigationTiming = performance.getEntriesByType('navigation')[0] as PerformanceNavigationTiming;
  if (navigationTiming) {
    metrics.timeToInteractive = navigationTiming.domInteractive;
  }

  // Collect island hydration measurements
  metrics.islandHydrations = getIslandHydrations();

  return metrics;
}

/**
 * Get First Contentful Paint using PerformanceObserver
 */
function getFirstContentfulPaint(): Promise<PerformanceEntry | null> {
  return new Promise((resolve) => {
    // Check if already available
    const entries = performance.getEntriesByName('first-contentful-paint');
    if (entries.length > 0) {
      resolve(entries[0]);
      return;
    }

    // Otherwise observe for it
    if (!('PerformanceObserver' in window)) {
      resolve(null);
      return;
    }

    try {
      const observer = new PerformanceObserver((list) => {
        const entries = list.getEntries();
        const fcpEntry = entries.find((entry) => entry.name === 'first-contentful-paint');
        if (fcpEntry) {
          observer.disconnect();
          resolve(fcpEntry);
        }
      });

      observer.observe({ type: 'paint', buffered: true });

      // Timeout after 5 seconds
      setTimeout(() => {
        observer.disconnect();
        resolve(null);
      }, 5000);
    } catch {
      resolve(null);
    }
  });
}

/**
 * Get Largest Contentful Paint using PerformanceObserver
 */
function getLargestContentfulPaint(): Promise<PerformanceEntry | null> {
  return new Promise((resolve) => {
    if (!('PerformanceObserver' in window)) {
      resolve(null);
      return;
    }

    try {
      let largestEntry: PerformanceEntry | null = null;

      const observer = new PerformanceObserver((list) => {
        const entries = list.getEntries();
        // LCP can change as larger content appears, so keep the last one
        if (entries.length > 0) {
          largestEntry = entries[entries.length - 1];
        }
      });

      observer.observe({ type: 'largest-contentful-paint', buffered: true });

      // Resolve after 2 seconds or on page visibility change
      const resolveWithEntry = () => {
        observer.disconnect();
        resolve(largestEntry);
      };

      setTimeout(resolveWithEntry, 2000);

      // Also disconnect on visibility change (user interaction)
      document.addEventListener('visibilitychange', resolveWithEntry, { once: true });
    } catch {
      resolve(null);
    }
  });
}

/**
 * Get all island hydration measurements from performance marks/measures
 * Islands should call trackIslandHydration() to register their hydration
 */
function getIslandHydrations(): IslandHydration[] {
  const hydrations: IslandHydration[] = [];

  // Get all measures that match our island hydration pattern
  const measures = performance.getEntriesByType('measure');

  for (const measure of measures) {
    // Look for measures with naming pattern: "{IslandName}-hydration"
    if (measure.name.endsWith('-hydration')) {
      const islandName = measure.name.replace('-hydration', '');
      hydrations.push({
        islandName,
        durationMs: measure.duration,
        startTime: measure.startTime,
      });
    }
  }

  return hydrations;
}

/**
 * Track hydration timing for an island component
 * Call this in your island component's useEffect hook
 *
 * @example
 * ```tsx
 * useEffect(() => {
 *   trackIslandHydration('TaskList');
 * }, []);
 * ```
 */
export function trackIslandHydration(islandName: string): void {
  const startMark = `${islandName}-start`;
  const endMark = `${islandName}-hydrated`;
  const measureName = `${islandName}-hydration`;

  try {
    // Mark the end of hydration
    performance.mark(endMark);

    // Try to measure from the start mark (if it exists)
    // The start mark should be set before the component renders
    const startMarks = performance.getEntriesByName(startMark);
    if (startMarks.length > 0) {
      performance.measure(measureName, startMark, endMark);
    } else {
      // If no start mark, measure from navigation start
      performance.measure(measureName, undefined, endMark);
    }
  } catch (e) {
    console.warn(`Failed to track hydration for ${islandName}:`, e);
  }
}

/**
 * Mark the start of island hydration
 * This should be called BEFORE the island component is rendered
 * Can be called inline in the HTML or in a script tag before the island
 *
 * @example
 * ```html
 * <script>window.markIslandStart?.('TaskList')</script>
 * <TaskList client:load />
 * ```
 */
export function markIslandStart(islandName: string): void {
  try {
    performance.mark(`${islandName}-start`);
  } catch (e) {
    console.warn(`Failed to mark start for ${islandName}:`, e);
  }
}

/**
 * Clear all performance marks and measures for islands
 * Useful for re-measuring or cleanup
 */
export function clearIslandMetrics(): void {
  try {
    const entries = performance.getEntriesByType('mark');
    for (const entry of entries) {
      if (entry.name.includes('-start') || entry.name.includes('-hydrated')) {
        performance.clearMarks(entry.name);
      }
    }

    const measures = performance.getEntriesByType('measure');
    for (const measure of measures) {
      if (measure.name.includes('-hydration')) {
        performance.clearMeasures(measure.name);
      }
    }
  } catch (e) {
    console.warn('Failed to clear island metrics:', e);
  }
}

/**
 * Format duration in milliseconds to readable string
 */
export function formatDuration(ms: number): string {
  if (ms < 1000) {
    return `${Math.round(ms)}ms`;
  }
  return `${(ms / 1000).toFixed(2)}s`;
}

/**
 * Format bytes to readable size
 */
export function formatBytes(bytes: number): string {
  if (bytes < 1024) {
    return `${bytes} B`;
  }
  if (bytes < 1024 * 1024) {
    return `${(bytes / 1024).toFixed(2)} KB`;
  }
  return `${(bytes / (1024 * 1024)).toFixed(2)} MB`;
}

/**
 * Export metrics as JSON for download
 */
export function exportMetrics(metrics: PerformanceMetrics): void {
  const dataStr = JSON.stringify(metrics, null, 2);
  const dataBlob = new Blob([dataStr], { type: 'application/json' });
  const url = URL.createObjectURL(dataBlob);

  const link = document.createElement('a');
  link.href = url;
  link.download = `performance-metrics-${metrics.pageName}-${Date.now()}.json`;
  document.body.appendChild(link);
  link.click();
  document.body.removeChild(link);

  URL.revokeObjectURL(url);
}

// Extend Window interface for performance tracking functions
declare global {
  interface Window {
    markIslandStart?: typeof markIslandStart;
    trackIslandHydration?: typeof trackIslandHydration;
  }
}

// Make trackIslandHydration available globally for easy access
if (typeof window !== 'undefined') {
  window.markIslandStart = markIslandStart;
  window.trackIslandHydration = trackIslandHydration;
}
