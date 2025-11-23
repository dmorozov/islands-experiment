import eslint from '@eslint/js';
import tseslint from 'typescript-eslint';
import astroPlugin from 'eslint-plugin-astro';
import preactPlugin from 'eslint-plugin-preact';
import prettierConfig from 'eslint-config-prettier';

/**
 * ESLint Flat Config for Task Manager Application
 *
 * Production-ready ESLint configuration for Islands Architecture with:
 * - TypeScript strict checking with Airbnb-style rules
 * - Astro framework support for .astro files
 * - Preact support for interactive islands (.tsx files)
 * - Prettier integration (disables conflicting rules)
 *
 * Architecture: This configuration supports Islands Architecture by:
 * - Enforcing strict TypeScript for type safety across islands
 * - Validating Astro component structure (server-rendered content)
 * - Checking Preact components (client-side interactive islands)
 * - Ensuring consistent code style with Prettier
 */

export default [
  // ============================================================================
  // Base Configuration - Applies to all JavaScript/TypeScript files
  // ============================================================================
  {
    files: ['**/*.{js,mjs,cjs,ts,tsx}'],
    languageOptions: {
      ecmaVersion: 2020,
      sourceType: 'module',
      parserOptions: {
        ecmaFeatures: {
          jsx: true,
        },
      },
    },
    rules: {
      // ESLint recommended rules
      ...eslint.configs.recommended.rules,

      // Airbnb-style rules (manually configured for ESLint 9 compatibility)
      'no-console': ['warn', { allow: ['warn', 'error'] }],
      'no-unused-vars': 'off', // Disabled in favor of TypeScript rule
      'prefer-const': 'error',
      'no-var': 'error',
      'object-shorthand': ['error', 'always'],
      'quote-props': ['error', 'as-needed'],
      'prefer-template': 'error',
      'prefer-destructuring': ['error', { object: true, array: false }],
      'no-useless-concat': 'error',
      'prefer-arrow-callback': 'error',
      'arrow-body-style': ['error', 'as-needed'],
      'no-param-reassign': ['error', { props: false }],
      'no-plusplus': ['error', { allowForLoopAfterthoughts: true }],
      eqeqeq: ['error', 'always'],
      curly: ['error', 'all'],
      'brace-style': ['error', '1tbs'],
      'spaced-comment': ['error', 'always'],
      'prefer-rest-params': 'error',
      'prefer-spread': 'error',
    },
  },

  // ============================================================================
  // TypeScript Configuration - Strict type checking for all .ts and .tsx files
  // Note: Excludes virtual files from Astro plugin (*.astro/**/*.ts)
  // ============================================================================
  {
    files: ['**/*.{ts,tsx}'],
    ignores: ['**/*.astro/**/*.ts'],
    languageOptions: {
      parser: tseslint.parser,
      parserOptions: {
        project: './tsconfig.json',
        ecmaFeatures: {
          jsx: true,
        },
      },
    },
    plugins: {
      '@typescript-eslint': tseslint.plugin,
    },
    rules: {
      // TypeScript ESLint recommended rules
      ...tseslint.configs.recommended[0].rules,
      ...tseslint.configs.recommended[1].rules,

      // TypeScript-specific Airbnb-style rules
      '@typescript-eslint/no-unused-vars': [
        'error',
        {
          argsIgnorePattern: '^_',
          varsIgnorePattern: '^_',
        },
      ],
      '@typescript-eslint/explicit-function-return-type': 'off',
      '@typescript-eslint/explicit-module-boundary-types': 'off',
      '@typescript-eslint/no-explicit-any': 'error',
      '@typescript-eslint/no-non-null-assertion': 'warn',
      // Type-aware rules (only for .ts and .tsx files with tsconfig.json)
      '@typescript-eslint/prefer-optional-chain': 'error',
      '@typescript-eslint/prefer-nullish-coalescing': 'error',
      '@typescript-eslint/no-floating-promises': 'error',
      '@typescript-eslint/await-thenable': 'error',
      '@typescript-eslint/no-misused-promises': 'error',
      '@typescript-eslint/consistent-type-imports': [
        'error',
        {
          prefer: 'type-imports',
        },
      ],
      '@typescript-eslint/naming-convention': [
        'error',
        {
          selector: 'interface',
          format: ['PascalCase'],
        },
        {
          selector: 'typeAlias',
          format: ['PascalCase'],
        },
      ],
    },
  },

  // ============================================================================
  // Preact Configuration - For interactive islands (.tsx files)
  // ============================================================================
  {
    files: ['**/*.tsx'],
    plugins: {
      preact: preactPlugin,
    },
    rules: {
      // Preact-specific rules (using available rules from eslint-plugin-preact)
      // Note: eslint-plugin-preact 0.1.0 has limited rules available
    },
    settings: {
      preact: {
        version: 'detect',
      },
    },
  },

  // ============================================================================
  // Astro Configuration - For .astro component files
  // ============================================================================
  ...astroPlugin.configs.recommended,

  // Disable type-aware rules for Astro files (they require project parser options)
  {
    files: ['**/*.astro'],
    rules: {
      '@typescript-eslint/prefer-optional-chain': 'off',
      '@typescript-eslint/prefer-nullish-coalescing': 'off',
      '@typescript-eslint/no-floating-promises': 'off',
      '@typescript-eslint/await-thenable': 'off',
      '@typescript-eslint/no-misused-promises': 'off',
    },
  },

  // ============================================================================
  // Prettier Integration - MUST BE LAST to disable conflicting rules
  // ============================================================================
  prettierConfig,

  // ============================================================================
  // Ignore Patterns - Files/directories to exclude from linting
  // ============================================================================
  {
    ignores: [
      // Build output
      'dist/',
      '.astro/',
      'types/',
      // Dependencies
      'node_modules/',
      // Generated API client (from Orval)
      'api/',
      // Configuration files
      '*.config.{js,mjs,ts}',
      // Husky git hooks
      '.husky/',
      // Lock files
      'package-lock.json',
      'pnpm-lock.yaml',
      'yarn.lock',
    ],
  },
];
