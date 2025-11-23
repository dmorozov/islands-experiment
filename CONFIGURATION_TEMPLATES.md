# Configuration Templates - Copy & Paste Ready

This document contains complete, copy-paste ready configuration files for the Task Manager Islands Architecture project.

---

## 1. eslint.config.js

**Location**: `/workspaces/quarkus-astro-app/src/main/webui/eslint.config.js`

```javascript
import js from '@eslint/js';
import globals from 'globals';
import eslintPluginAstro from 'eslint-plugin-astro';
import eslintPluginPreact from 'eslint-plugin-preact';
import typescriptEslint from 'typescript-eslint';
import airbnbBase from 'eslint-config-airbnb-base';
import airbnbTypeScript from 'eslint-config-airbnb-typescript';
import eslintConfigPrettier from 'eslint-config-prettier';

export default [
  // Global ignores
  {
    ignores: [
      'dist',
      'node_modules',
      '.astro',
      'build',
      'coverage',
      '.env',
      '.env.*',
      '.DS_Store',
      '*.log',
    ],
  },

  // JavaScript base configuration
  {
    files: ['**/*.{js,mjs,cjs,ts,tsx}'],
    languageOptions: {
      ecmaVersion: 2024,
      sourceType: 'module',
      globals: {
        ...globals.browser,
        ...globals.node,
        ...globals.es2024,
      },
    },
  },

  // Airbnb base for JavaScript
  {
    files: ['**/*.{js,mjs,cjs}'],
    ...airbnbBase,
  },

  // TypeScript configuration
  {
    files: ['**/*.{ts,tsx}'],
    languageOptions: {
      parser: typescriptEslint.parser,
      parserOptions: {
        ecmaVersion: 2024,
        sourceType: 'module',
        project: './tsconfig.json',
        warnOnUnsupportedTypeScriptVersion: false,
      },
    },
    plugins: {
      '@typescript-eslint': typescriptEslint.plugin,
    },
    rules: {
      ...typescriptEslint.configs.recommendedTypeChecked.rules,
      ...typescriptEslint.configs.stylisticTypeChecked.rules,
      '@typescript-eslint/explicit-function-return-types': 'off',
      '@typescript-eslint/explicit-module-boundary-types': 'off',
      '@typescript-eslint/no-unused-vars': [
        'error',
        {
          argsIgnorePattern: '^_',
          varsIgnorePattern: '^_',
          caughtErrorsIgnorePattern: '^_',
        },
      ],
      '@typescript-eslint/no-floating-promises': 'warn',
      '@typescript-eslint/no-misused-promises': 'warn',
      '@typescript-eslint/require-await': 'warn',
      '@typescript-eslint/no-unsafe-assignment': 'warn',
      '@typescript-eslint/no-unsafe-call': 'warn',
      '@typescript-eslint/no-unsafe-member-access': 'warn',
      '@typescript-eslint/no-unsafe-return': 'warn',
    },
  },

  // Airbnb TypeScript
  {
    files: ['**/*.{ts,tsx}'],
    ...airbnbTypeScript,
  },

  // Astro configuration
  {
    files: ['**/*.astro'],
    ...eslintPluginAstro.configs.recommended,
  },

  // Preact/React configuration
  {
    files: ['**/*.{jsx,tsx}'],
    plugins: {
      preact: eslintPluginPreact,
    },
    rules: {
      ...eslintPluginPreact.configs.recommended.rules,
      'react/jsx-uses-react': 'off',
      'react/react-in-jsx-scope': 'off',
      'react/no-unknown-property': ['error', { ignore: ['class'] }],
      'react/prop-types': 'off',
      'react/require-default-props': 'off',
    },
  },

  // Prettier config (must be last to disable formatting conflicts)
  eslintConfigPrettier,

  // Custom rule overrides
  {
    files: ['**/*.{js,mjs,cjs,ts,tsx,astro}'],
    rules: {
      'import/extensions': [
        'error',
        'ignorePackages',
        {
          ts: 'never',
          tsx: 'never',
          js: 'never',
          jsx: 'never',
        },
      ],
      'import/no-unresolved': 'off',
      'no-console': ['warn', { allow: ['warn', 'error', 'info'] }],
      'no-await-in-loop': 'warn',
      'prefer-destructuring': ['warn', { VariableDeclarator: { object: true } }],
    },
  },
];
```

---

## 2. .eslintignore

**Location**: `/workspaces/quarkus-astro-app/src/main/webui/.eslintignore`

```
# Dependencies
node_modules
.pnp
.pnp.js

# Build outputs
dist
build
target
.astro

# Environment variables
.env
.env.*
!.env.example
!.env.local.example

# IDE and editor directories
.vscode
.idea
*.swp
*.swo
*~
.DS_Store
Thumbs.db

# Generated files
coverage
*.log
logs
*.log.json
.turbo

# Test files
.nyc_output

# Cache
.cache
.eslintcache
.next

# OS
*.pem
```

---

## 3. .prettierrc.json

**Location**: `/workspaces/quarkus-astro-app/src/main/webui/.prettierrc.json`

```json
{
  "printWidth": 100,
  "tabWidth": 2,
  "useTabs": false,
  "semi": true,
  "singleQuote": true,
  "quoteProps": "as-needed",
  "jsxSingleQuote": false,
  "trailingComma": "es5",
  "bracketSpacing": true,
  "bracketSameLine": false,
  "arrowParens": "always",
  "proseWrap": "preserve",
  "htmlWhitespaceSensitivity": "css",
  "endOfLine": "lf",
  "embeddedLanguageFormatting": "auto",
  "singleAttributePerLine": false,
  "plugins": [
    "prettier-plugin-astro",
    "prettier-plugin-tailwindcss"
  ],
  "overrides": [
    {
      "files": "*.astro",
      "options": {
        "parser": "astro"
      }
    },
    {
      "files": [
        "*.json",
        ".prettierrc",
        ".babelrc"
      ],
      "options": {
        "parser": "json"
      }
    }
  ]
}
```

---

## 4. .prettierignore

**Location**: `/workspaces/quarkus-astro-app/src/main/webui/.prettierignore`

```
# Dependencies
node_modules
.pnp
.pnp.js

# Build outputs
dist
build
.astro
coverage

# Environment variables
.env
.env.*
!.env.example
!.env.local.example

# IDE
.vscode
.idea

# Generated files by build tools
*.log
.next
.turbo

# Lock files
package-lock.json
pnpm-lock.yaml
yarn.lock

# Other
.DS_Store
Thumbs.db
```

---

## 5. .lintstagedrc.json

**Location**: `/workspaces/quarkus-astro-app/src/main/webui/.lintstagedrc.json`

```json
{
  "*.{js,mjs,cjs,ts,tsx,jsx}": [
    "prettier --write",
    "eslint --fix --max-warnings=0"
  ],
  "*.astro": [
    "prettier --write"
  ],
  "{src,public}/**/*.{json,jsonc}": [
    "prettier --write"
  ],
  "*.{css,scss}": [
    "prettier --write"
  ],
  "*.{md,mdx}": [
    "prettier --write --prose-wrap=always"
  ]
}
```

---

## 6. .husky/pre-commit

**Location**: `/workspaces/quarkus-astro-app/src/main/webui/.husky/pre-commit`

```bash
#!/bin/sh
. "$(dirname "$0")/_/husky.sh"

# Run lint-staged on staged files
npx lint-staged

# Capture exit code
EXIT_CODE=$?

# Exit with the status from lint-staged
exit $EXIT_CODE
```

Make executable:
```bash
chmod +x .husky/pre-commit
```

---

## 7. tsconfig.json

**Location**: `/workspaces/quarkus-astro-app/src/main/webui/tsconfig.json`

```json
{
  "extends": "astro/tsconfigs/strict",
  "compilerOptions": {
    "baseUrl": ".",
    "paths": {
      "@/*": ["./src/*"],
      "@components/*": ["./src/components/*"],
      "@components/ui/*": ["./src/components/ui/*"],
      "@layouts/*": ["./src/layouts/*"],
      "@pages/*": ["./src/pages/*"],
      "@assets/*": ["./src/assets/*"],
      "@api/*": ["./src/api/*"],
      "@lib/*": ["./src/lib/*"],
      "@styles/*": ["./src/styles/*"]
    },
    "strictNullChecks": true,
    "forceConsistentCasingInFileNames": true,
    "moduleResolution": "bundler",
    "target": "ES2022",
    "lib": [
      "ES2022",
      "DOM",
      "DOM.Iterable"
    ]
  },
  "include": [
    ".astro/types.d.ts",
    "**/*.ts",
    "**/*.tsx",
    "**/*.astro"
  ],
  "exclude": [
    "dist",
    "node_modules",
    ".next",
    "coverage"
  ]
}
```

---

## 8. orval.config.ts

**Location**: `/workspaces/quarkus-astro-app/src/main/webui/orval.config.ts`

```typescript
import { defineConfig } from 'orval';

export default defineConfig({
  api: {
    input: {
      target: './api/openapi.json',
    },
    output: {
      mode: 'split',
      target: './src/api/generated',
      schemas: './src/api/generated/schemas',
      client: 'react-query',
      httpClient: 'axios',
      baseUrl: process.env.VITE_API_BASE_URL || 'http://localhost:8080',
      prettier: true,
      clean: true,
      override: {
        mutator: {
          path: './src/api/mutator.ts',
          name: 'customAxiosInstance',
        },
        query: {
          useQuery: true,
          useInfiniteQuery: false,
          useInfiniteQueryParam: 'limit',
          options: {
            staleTime: 30000,
            cacheTime: 60000,
          },
        },
        operationName: (operation) => `${operation.method}${operation.path}`,
      },
      mock: {
        enabled: true,
        type: 'msw',
        baseUrl: 'http://localhost:3000',
      },
      validation: true,
    },
  },
});
```

---

## 9. src/api/mutator.ts

**Location**: `/workspaces/quarkus-astro-app/src/main/webui/src/api/mutator.ts`

```typescript
import axios, {
  AxiosRequestConfig,
  AxiosResponse,
  AxiosError,
} from 'axios';

const API_BASE_URL = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080';
const API_TIMEOUT = parseInt(import.meta.env.VITE_API_TIMEOUT || '30000', 10);

export const customAxiosInstance = axios.create({
  baseURL: API_BASE_URL,
  timeout: API_TIMEOUT,
  withCredentials: true,
  headers: {
    'Content-Type': 'application/json',
  },
});

// Request interceptor for adding auth tokens
customAxiosInstance.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('authToken');
    if (token && config.headers) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error) => {
    console.error('Request error:', error);
    return Promise.reject(error);
  }
);

// Response interceptor for handling errors
customAxiosInstance.interceptors.response.use(
  (response: AxiosResponse) => response,
  (error: AxiosError) => {
    if (error.response?.status === 401) {
      // Handle unauthorized - clear auth and redirect
      localStorage.removeItem('authToken');
      window.location.href = '/login';
    } else if (error.response?.status === 403) {
      console.error('Access forbidden');
    } else if (error.response?.status === 500) {
      console.error('Server error:', error.response.data);
    }
    return Promise.reject(error);
  }
);

export const axiosInstance = customAxiosInstance;

// Export a function to update the base URL dynamically
export function setApiBaseUrl(baseUrl: string) {
  customAxiosInstance.defaults.baseURL = baseUrl;
}

// Export a function to set auth token
export function setAuthToken(token: string | null) {
  if (token) {
    customAxiosInstance.defaults.headers.common.Authorization = `Bearer ${token}`;
  } else {
    delete customAxiosInstance.defaults.headers.common.Authorization;
  }
}
```

---

## 10. astro.config.mjs

**Location**: `/workspaces/quarkus-astro-app/src/main/webui/astro.config.mjs`

```javascript
// @ts-check
import { defineConfig } from 'astro/config';
import preact from '@astrojs/preact';
import tailwindcss from '@tailwindcss/vite';

// https://astro.build/config
export default defineConfig({
  integrations: [
    preact({
      compat: true,
    }),
  ],
  vite: {
    plugins: [tailwindcss()],
    server: {
      proxy: {
        '/api': {
          target: 'http://localhost:8080',
          changeOrigin: true,
          rewrite: (path) => path.replace(/^\/api/, '/api'),
        },
      },
    },
  },
  output: 'static',
  site: 'http://localhost:3000',
});
```

---

## 11. tailwind.config.mjs

**Location**: `/workspaces/quarkus-astro-app/src/main/webui/tailwind.config.mjs`

```javascript
/** @type {import('tailwindcss').Config} */
export default {
  content: [
    './src/**/*.{astro,html,js,jsx,md,mdx,svelte,ts,tsx,vue}',
  ],
  theme: {
    extend: {
      colors: {
        brand: {
          50: '#f9fafb',
          100: '#f3f4f6',
          200: '#e5e7eb',
          500: '#3b82f6',
          600: '#2563eb',
          700: '#1d4ed8',
          900: '#111827',
        },
      },
      spacing: {
        128: '32rem',
      },
      animation: {
        'fade-in': 'fadeIn 0.3s ease-in-out',
      },
      keyframes: {
        fadeIn: {
          '0%': { opacity: '0' },
          '100%': { opacity: '1' },
        },
      },
    },
  },
  plugins: [],
};
```

---

## 12. components.json (shadcn/ui)

**Location**: `/workspaces/quarkus-astro-app/src/main/webui/components.json`

```json
{
  "$schema": "https://ui.shadcn.com/schema.json",
  "style": "default",
  "rsc": false,
  "tsx": true,
  "aliasPrefix": "@",
  "baseColor": "slate",
  "tailwind": {
    "config": "tailwind.config.mjs",
    "css": "src/styles/globals.css",
    "baseColor": "slate"
  },
  "aliases": {
    "@": "./src",
    "@components": "./src/components",
    "@components/ui": "./src/components/ui",
    "@layouts": "./src/layouts",
    "@pages": "./src/pages",
    "@assets": "./src/assets",
    "@api": "./src/api",
    "@lib": "./src/lib",
    "@styles": "./src/styles"
  }
}
```

---

## 13. .env.local (Example)

**Location**: `/workspaces/quarkus-astro-app/src/main/webui/.env.local`

```
# API Configuration
VITE_API_BASE_URL=http://localhost:8080
VITE_API_TIMEOUT=30000

# Logging
VITE_LOG_LEVEL=debug

# Development
VITE_ENV=development
```

---

## 14. .vscode/settings.json

**Location**: `/workspaces/quarkus-astro-app/src/main/webui/.vscode/settings.json`

```json
{
  "editor.defaultFormatter": "esbenp.prettier-vscode",
  "editor.formatOnSave": true,
  "editor.formatOnPaste": true,
  "editor.codeActionsOnSave": {
    "source.fixAll.eslint": "explicit",
    "source.fixAll": "explicit"
  },
  "[astro]": {
    "editor.defaultFormatter": "esbenp.prettier-vscode"
  },
  "[typescript]": {
    "editor.defaultFormatter": "esbenp.prettier-vscode",
    "editor.formatOnSave": true
  },
  "[typescriptreact]": {
    "editor.defaultFormatter": "esbenp.prettier-vscode",
    "editor.formatOnSave": true
  },
  "[javascript]": {
    "editor.defaultFormatter": "esbenp.prettier-vscode"
  },
  "[json]": {
    "editor.defaultFormatter": "esbenp.prettier-vscode"
  },
  "[css]": {
    "editor.defaultFormatter": "esbenp.prettier-vscode"
  },
  "eslint.validate": [
    "javascript",
    "javascriptreact",
    "typescript",
    "typescriptreact",
    "astro"
  ],
  "eslint.enable": true,
  "files.exclude": {
    "node_modules": true,
    ".astro": true,
    "dist": true
  },
  "search.exclude": {
    "node_modules": true,
    ".astro": true,
    "dist": true
  }
}
```

---

## 15. package.json (webui)

**Location**: `/workspaces/quarkus-astro-app/src/main/webui/package.json`

```json
{
  "name": "webui",
  "type": "module",
  "version": "0.0.1",
  "description": "Task Manager Web UI - Astro + Preact + TypeScript",
  "scripts": {
    "dev": "astro dev --port 3000",
    "build": "astro build",
    "preview": "astro preview",
    "astro": "astro",
    "lint": "eslint .",
    "lint:fix": "eslint . --fix",
    "lint:check": "eslint . --max-warnings=0",
    "format": "prettier --write \"src/**/*.{astro,js,jsx,ts,tsx,json,css,md}\"",
    "format:check": "prettier --check \"src/**/*.{astro,js,jsx,ts,tsx,json,css,md}\"",
    "api:generate": "orval --config orval.config.ts",
    "api:validate": "orval validate --config orval.config.ts",
    "api:watch": "orval --config orval.config.ts --watch",
    "prebuild": "npm run api:generate",
    "prepare": "husky install",
    "add:component": "shadcn-cli add"
  },
  "dependencies": {
    "astro": "^5.16.0",
    "tailwindcss": "^4.0.0",
    "axios": "^1.6.0",
    "@tanstack/react-query": "^5.0.0",
    "class-variance-authority": "^0.7.0",
    "clsx": "^2.0.0",
    "tailwind-merge": "^2.2.0",
    "zod": "^3.22.0"
  },
  "devDependencies": {
    "@astrojs/preact": "^4.0.0",
    "@eslint/js": "^9.0.0",
    "@shadcn-cli/init": "^0.7.0",
    "@tailwindcss/vite": "^4.0.0",
    "@typescript-eslint/eslint-plugin": "^7.0.0",
    "@typescript-eslint/parser": "^7.0.0",
    "eslint": "^9.0.0",
    "eslint-config-airbnb-base": "^15.0.0",
    "eslint-config-airbnb-typescript": "^18.0.0",
    "eslint-config-prettier": "^9.0.0",
    "eslint-plugin-astro": "^1.2.0",
    "eslint-plugin-import": "^2.29.0",
    "eslint-plugin-jsx-a11y": "^6.8.0",
    "eslint-plugin-preact": "^3.2.0",
    "globals": "^15.0.0",
    "husky": "^9.0.0",
    "lint-staged": "^15.0.0",
    "orval": "^7.0.0",
    "prettier": "^3.0.0",
    "prettier-plugin-astro": "^0.13.0",
    "prettier-plugin-tailwindcss": "^0.5.0",
    "typescript": "^5.5.0",
    "typescript-eslint": "^7.0.0"
  },
  "engines": {
    "node": ">=18.0.0",
    "npm": ">=9.0.0"
  }
}
```

---

## 16. src/styles/globals.css

**Location**: `/workspaces/quarkus-astro-app/src/main/webui/src/styles/globals.css`

```css
@import "tailwindcss";

/* Base styles */
@layer base {
  html {
    scroll-behavior: smooth;
  }

  body {
    @apply bg-white text-gray-900 dark:bg-gray-950 dark:text-gray-50;
  }

  h1 {
    @apply text-4xl font-bold;
  }

  h2 {
    @apply text-3xl font-bold;
  }

  h3 {
    @apply text-2xl font-bold;
  }

  h4 {
    @apply text-xl font-bold;
  }
}

/* Component styles */
@layer components {
  .btn-primary {
    @apply inline-flex items-center justify-center gap-2 rounded-lg bg-blue-600 px-4 py-2 font-semibold text-white transition-colors hover:bg-blue-700 disabled:opacity-50 disabled:cursor-not-allowed;
  }

  .btn-secondary {
    @apply inline-flex items-center justify-center gap-2 rounded-lg border border-gray-300 px-4 py-2 font-semibold transition-colors hover:bg-gray-50 dark:border-gray-600 dark:hover:bg-gray-800;
  }

  .card {
    @apply rounded-lg border border-gray-200 bg-white p-6 shadow-sm dark:border-gray-700 dark:bg-gray-900;
  }

  .input-field {
    @apply w-full rounded-lg border border-gray-300 px-3 py-2 text-gray-900 placeholder-gray-500 transition-colors focus:border-blue-500 focus:outline-none focus:ring-2 focus:ring-blue-200 dark:border-gray-600 dark:bg-gray-800 dark:text-white;
  }

  .container {
    @apply mx-auto w-full max-w-7xl px-4 sm:px-6 lg:px-8;
  }
}

/* Utility animations */
@layer utilities {
  .animate-fade-in {
    animation: fadeIn 0.3s ease-in-out;
  }

  .animate-slide-in {
    animation: slideIn 0.3s ease-in-out;
  }
}

@keyframes fadeIn {
  from {
    opacity: 0;
  }

  to {
    opacity: 1;
  }
}

@keyframes slideIn {
  from {
    transform: translateX(-100%);
  }

  to {
    transform: translateX(0);
  }
}

/* Scrollbar styling (optional) */
@supports (scrollbar-gutter: stable) {
  html {
    scrollbar-gutter: stable;
  }
}
```

---

## 17. src/lib/utils.ts

**Location**: `/workspaces/quarkus-astro-app/src/main/webui/src/lib/utils.ts`

```typescript
import { clsx, type ClassValue } from 'clsx';
import { twMerge } from 'tailwind-merge';

/**
 * Merge Tailwind CSS classes safely, avoiding conflicts
 * @param inputs - Class names and conditions
 * @returns Merged class string
 */
export function cn(...inputs: ClassValue[]): string {
  return twMerge(clsx(inputs));
}
```

---

## Setup Instructions

To use these templates:

```bash
# 1. Copy eslint.config.js to webui root
cp eslint.config.js src/main/webui/

# 2. Copy all dot-files
cp .eslintignore src/main/webui/
cp .prettierrc.json src/main/webui/
cp .prettierignore src/main/webui/
cp .lintstagedrc.json src/main/webui/

# 3. Create .husky directory and pre-commit hook
mkdir -p src/main/webui/.husky
cp .husky/pre-commit src/main/webui/.husky/
chmod +x src/main/webui/.husky/pre-commit

# 4. Copy TypeScript config
cp tsconfig.json src/main/webui/

# 5. Copy Orval config and mutator
cp orval.config.ts src/main/webui/
mkdir -p src/main/webui/src/api
cp src/api/mutator.ts src/main/webui/src/api/

# 6. Copy Astro config
cp astro.config.mjs src/main/webui/

# 7. Copy Tailwind config
cp tailwind.config.mjs src/main/webui/

# 8. Copy Shadcn config
cp components.json src/main/webui/

# 9. Copy environment example
cp .env.local src/main/webui/

# 10. Copy VS Code settings
cp .vscode/settings.json src/main/webui/.vscode/

# 11. Install packages
cd src/main/webui
npm install

# 12. Initialize Husky
npx husky install

# 13. Initialize Shadcn/ui
npx shadcn-cli@latest init
```

