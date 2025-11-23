/**
 * ThemeToggle Island Component
 *
 * Interactive Preact island for toggling between light and dark themes.
 * Demonstrates Islands Architecture with persistent theme state.
 *
 * Features:
 * - Toggles between light and dark themes
 * - Persists theme to localStorage via Nano Stores
 * - Applies .dark class to document element
 * - Animated icon transitions (sun/moon)
 * - Accessible button with proper ARIA labels
 * - Styled with Tailwind and Shadcn/ui theme
 *
 * Usage in Astro pages:
 * ```astro
 * ---
 * import ThemeToggle from '@/islands/ThemeToggle';
 * ---
 * <ThemeToggle client:load />
 * ```
 *
 * The client:load directive ensures theme is applied immediately on page load.
 */

import { useStore } from '@nanostores/preact';
import { useEffect } from 'preact/hooks';
import { trackIslandHydration } from '@/lib/performance';
import { Moon, Sun } from 'lucide-preact';
import { userTheme, toggleTheme } from '@/lib/state';

/**
 * Main ThemeToggle component
 *
 * Renders a button that toggles the theme and updates the document class
 */
export default function ThemeToggle() {
  // Subscribe to theme state from Nano Stores
  const theme = useStore(userTheme);

  /**
   * Apply theme on initial load and whenever theme changes
   * Updates the document element's class to enable CSS theme switching
   */
  useEffect(() => {
    if (typeof document === 'undefined') return;

    const root = document.documentElement;

    if (theme === 'dark') {
      root.classList.add('dark');
    } else {
      root.classList.remove('dark');
    }
  }, [theme]);

  // T541: Track island hydration for performance monitoring
  useEffect(() => {
    trackIslandHydration('ThemeToggle');
  }, []);

  /**
   * Handle theme toggle button click
   * Uses the toggleTheme helper from state module
   */
  const handleToggle = () => {
    toggleTheme();
  };

  const isDark = theme === 'dark';

  return (
    <button
      type="button"
      onClick={handleToggle}
      aria-label={`Switch to ${isDark ? 'light' : 'dark'} theme`}
      class="border-input bg-background hover:bg-accent hover:text-accent-foreground focus-visible:ring-ring inline-flex h-10 w-10 items-center justify-center rounded-md border text-sm font-medium transition-colors focus-visible:ring-2 focus-visible:ring-offset-2 focus-visible:outline-none disabled:pointer-events-none disabled:opacity-50"
      title={`Switch to ${isDark ? 'light' : 'dark'} theme`}
    >
      {/* Sun icon for light mode (visible when dark mode is active) */}
      {isDark ? (
        <Sun class="h-5 w-5 transition-all" strokeWidth={2} aria-hidden="true" />
      ) : (
        /* Moon icon for dark mode (visible when light mode is active) */
        <Moon class="h-5 w-5 transition-all" strokeWidth={2} aria-hidden="true" />
      )}
      <span class="sr-only">{isDark ? 'Switch to light theme' : 'Switch to dark theme'}</span>
    </button>
  );
}
