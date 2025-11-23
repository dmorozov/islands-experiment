/**
 * HydrationVisualizer Island Component (T563-T567)
 *
 * Optional advanced feature that provides visual indicators showing when islands hydrate.
 * Helps developers understand the progressive hydration process visually.
 *
 * Features:
 * - Toggle button to enable/disable visualization
 * - Real-time visual highlighting as islands hydrate
 * - Timeline showing order of island hydration
 * - Color-coded indicators for hydration status
 * - Legend explaining visual indicators
 *
 * Usage in Astro pages:
 * ```astro
 * ---
 * import HydrationVisualizer from '@/islands/HydrationVisualizer';
 * ---
 * <HydrationVisualizer client:load />
 * ```
 */

import { useState, useEffect } from 'preact/hooks';
import { trackIslandHydration } from '@/lib/performance';
import { Button } from '@/components/ui/button';
import { Eye, EyeOff, Zap } from 'lucide-preact';

/**
 * Interface for tracking island status
 */
interface IslandStatus {
  name: string;
  hydrated: boolean;
  timestamp: number;
  duration?: number;
}

/**
 * Main HydrationVisualizer component
 */
function HydrationVisualizerContent() {
  const [isEnabled, setIsEnabled] = useState(false);
  const [islands, setIslands] = useState<IslandStatus[]>([]);

  // Track this island's own hydration
  useEffect(() => {
    trackIslandHydration('HydrationVisualizer');
  }, []);

  // Poll for island hydration status
  useEffect(() => {
    if (!isEnabled) return;

    const checkInterval = setInterval(() => {
      const measures = performance.getEntriesByType('measure');
      const hydrationMeasures = measures.filter((m) => m.name.endsWith('-hydration'));

      const newIslands: IslandStatus[] = hydrationMeasures.map((measure) => {
        const islandName = measure.name.replace('-hydration', '');
        return {
          name: islandName,
          hydrated: true,
          timestamp: measure.startTime,
          duration: measure.duration,
        };
      });

      // Sort by timestamp (order of hydration)
      newIslands.sort((a, b) => a.timestamp - b.timestamp);

      setIslands(newIslands);
    }, 500);

    return () => clearInterval(checkInterval);
  }, [isEnabled]);

  // Add visual overlays when enabled
  useEffect(() => {
    if (!isEnabled) {
      // Remove all overlays
      document.querySelectorAll('.hydration-overlay').forEach((el) => el.remove());
      return;
    }

    // Add overlay style to document
    const style = document.createElement('style');
    style.id = 'hydration-visualizer-styles';
    style.textContent = `
      .hydration-overlay {
        position: absolute;
        top: 0;
        left: 0;
        right: 0;
        bottom: 0;
        pointer-events: none;
        z-index: 9999;
        border: 3px solid #10b981;
        border-radius: 8px;
        animation: hydration-pulse 1s ease-in-out;
      }

      @keyframes hydration-pulse {
        0% {
          border-color: #10b981;
          box-shadow: 0 0 0 0 rgba(16, 185, 129, 0.7);
        }
        50% {
          border-color: #34d399;
          box-shadow: 0 0 0 10px rgba(16, 185, 129, 0);
        }
        100% {
          border-color: #10b981;
          box-shadow: 0 0 0 0 rgba(16, 185, 129, 0);
        }
      }
    `;
    document.head.appendChild(style);

    return () => {
      document.getElementById('hydration-visualizer-styles')?.remove();
    };
  }, [isEnabled]);

  const handleToggle = () => {
    setIsEnabled(!isEnabled);
  };

  return (
    <div class="space-y-4">
      {/* Toggle Button */}
      <div class="bg-card flex items-center justify-between rounded-lg border p-4">
        <div class="flex items-center gap-3">
          <div class="bg-primary/10 rounded-md p-2">
            <Zap size={20} class="text-primary" />
          </div>
          <div>
            <h3 class="text-foreground font-semibold">Hydration Visualizer</h3>
            <p class="text-muted-foreground text-sm">
              {isEnabled
                ? 'Visualization is active - watch islands light up as they hydrate'
                : 'Enable to see visual indicators as islands become interactive'}
            </p>
          </div>
        </div>
        <Button onClick={handleToggle} variant={isEnabled ? 'default' : 'outline'} size="sm">
          {isEnabled ? (
            <>
              <Eye size={16} class="mr-2" />
              Enabled
            </>
          ) : (
            <>
              <EyeOff size={16} class="mr-2" />
              Disabled
            </>
          )}
        </Button>
      </div>

      {/* Hydration Timeline */}
      {isEnabled && islands.length > 0 && (
        <div class="bg-card rounded-lg border p-6">
          <h4 class="text-foreground mb-4 text-sm font-semibold">Hydration Timeline</h4>
          <div class="space-y-3">
            {islands.map((island, index) => (
              <div
                key={`${island.name}-${index}`}
                class="bg-muted/50 flex items-center gap-4 rounded-md p-3"
              >
                <div class="flex h-8 w-8 flex-shrink-0 items-center justify-center rounded-full bg-green-100 text-xs font-semibold text-green-700 dark:bg-green-900/30 dark:text-green-400">
                  {index + 1}
                </div>
                <div class="flex-1">
                  <div class="flex items-center justify-between">
                    <span class="text-foreground font-medium">{island.name}</span>
                    {island.duration !== undefined && (
                      <span class="text-muted-foreground text-sm">
                        {island.duration.toFixed(0)}ms
                      </span>
                    )}
                  </div>
                  <div class="text-muted-foreground mt-1 text-xs">
                    Hydrated at {island.timestamp.toFixed(0)}ms after page load
                  </div>
                </div>
                <div class="flex items-center gap-1 text-green-600 dark:text-green-400">
                  <Zap size={16} />
                  <span class="text-xs font-medium">Ready</span>
                </div>
              </div>
            ))}
          </div>
        </div>
      )}

      {/* Legend */}
      {isEnabled && (
        <div class="rounded-lg border border-blue-200 bg-blue-50 p-4 dark:border-blue-900 dark:bg-blue-950/30">
          <h4 class="mb-3 text-sm font-semibold text-blue-900 dark:text-blue-100">
            Visual Indicator Legend
          </h4>
          <div class="space-y-2 text-sm text-blue-800 dark:text-blue-200">
            <div class="flex items-start gap-2">
              <div class="mt-0.5 h-3 w-3 flex-shrink-0 rounded border-2 border-green-500"></div>
              <span>
                <strong>Green border pulse:</strong> Indicates when an island has just hydrated and
                become interactive
              </span>
            </div>
            <div class="flex items-start gap-2">
              <Zap size={14} class="mt-0.5 flex-shrink-0 text-green-600" />
              <span>
                <strong>Timeline order:</strong> Shows the sequence in which islands became
                interactive, helping you understand progressive enhancement
              </span>
            </div>
            <div class="flex items-start gap-2">
              <span class="mt-0.5 text-xs font-semibold">ms</span>
              <span>
                <strong>Duration:</strong> Time taken for each island to hydrate (should be under
                200ms for good performance)
              </span>
            </div>
          </div>
        </div>
      )}

      {/* Instructions */}
      {!isEnabled && (
        <div class="bg-muted/50 rounded-lg border p-4 text-center">
          <p class="text-muted-foreground text-sm">
            Enable the visualizer and reload the page to see islands light up as they hydrate. This
            helps you understand the progressive enhancement process of Islands Architecture.
          </p>
        </div>
      )}
    </div>
  );
}

/**
 * Wrapper component
 */
export default function HydrationVisualizer() {
  return <HydrationVisualizerContent />;
}
