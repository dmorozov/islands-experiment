import { defineConfig } from 'vitest/config';
import preact from '@preact/preset-vite';

/**
 * Vitest Configuration for Task Manager Application
 *
 * This configuration enables:
 * - Preact testing with jsdom environment
 * - Global test functions (describe, it, expect)
 * - Coverage reporting
 *
 * Usage:
 * - Run tests: `npm test`
 * - Run with coverage: `npm run test:coverage`
 */

export default defineConfig({
  plugins: [preact()],
  test: {
    // Test environment - jsdom for DOM testing
    environment: 'jsdom',

    // Global test functions - no need to import describe, it, expect
    globals: true,

    // Setup files (if needed)
    // setupFiles: ['./src/test/setup.ts'],

    // Coverage configuration
    coverage: {
      provider: 'v8',
      reporter: ['text', 'json', 'html'],
      exclude: [
        'node_modules/',
        'dist/',
        '.astro/',
        'api/',
        '**/*.config.{js,ts,mjs}',
        '**/test/**',
      ],
    },

    // Exclude patterns
    exclude: ['node_modules', 'dist', '.astro', 'api'],
  },
});
