// @ts-check
import { defineConfig } from 'astro/config';
import preact from '@astrojs/preact';
import tailwindcss from '@tailwindcss/vite';

/**
 * Astro Configuration for Task Manager Application (Islands Architecture)
 *
 * This configuration enables:
 * - Preact integration for interactive islands (client-side components)
 * - Tailwind CSS v4 via Vite plugin for utility-first styling
 * - Static site generation for optimal performance
 * - Path aliases for clean imports
 *
 * Islands Architecture:
 * - .astro files are server-rendered (no JavaScript by default)
 * - .tsx components in src/islands/ are client-side interactive islands
 * - Use client:load, client:visible, etc. directives to hydrate islands
 */

// https://astro.build/config
export default defineConfig({
  // Preact Integration - For interactive islands
  integrations: [
    preact({
      compat: true, // Enable React compatibility mode
    }),
  ],

  // Vite Configuration - Tailwind CSS v4 and path aliases
  vite: {
    plugins: [tailwindcss()],
    resolve: {
      alias: {
        '@': '/src',
        '@/components': '/src/components',
        '@/islands': '/src/islands',
        '@/lib': '/src/lib',
        '@/styles': '/src/styles',
        '@/api': '/src/api',
        // 'react': 'preact/compat',
        // 'react-dom/test-utils': 'preact/test-utils',
        // 'react-dom': 'preact/compat',
        // 'react/jsx-runtime': 'preact/jsx-runtime',
      },
    },
  },

  // Output Configuration - Static site generation
  output: 'static',

  // Build Configuration
  build: {
    outDir: './dist',
    format: 'directory', // Clean URLs without .html extension
  },

  // Development Server Configuration
  server: {
    port: 3000,
    host: true,
  },
});
