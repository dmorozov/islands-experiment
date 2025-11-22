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
  // ============================================================================
  {
    files: ['**/*.{ts,tsx}'],
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
      // Preact-specific rules
      'preact/no-deprecated': 'error',
      'preact/no-unknown-property': 'error',
      'preact/prefer-props': 'warn',

      // JSX best practices
      'react/jsx-no-target-blank': 'error', // Preact uses React rules
      'react/jsx-key': 'error',
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

  // ============================================================================
  // Prettier Integration - MUST BE LAST to disable conflicting rules
  // ============================================================================
  prettierConfig,

  // ============================================================================
  // Ignore Patterns - Files/directories to exclude from linting
  // ============================================================================
  {
    ignores: ['dist/', 'node_modules/', '.astro/', 'api/', '*.config.{js,mjs,ts}', '.husky/'],
  },
];
