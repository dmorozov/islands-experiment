# Task Manager Islands Architecture: Linting, Formatting & Tooling Guide

This document provides comprehensive guidelines for configuring ESLint, Prettier, pre-commit hooks, Orval, and Tailwind CSS + Shadcn UI in the Task Manager Islands Architecture project (Astro + Preact + TypeScript + Quarkus).

---

## 1. ESLint Configuration for Astro + Preact + TypeScript

### 1.1 Overview

ESLint is a static analysis tool that helps identify and fix problems in your JavaScript code. For this project, we need to support:
- **Astro** components (`.astro` files)
- **Preact** JSX components
- **TypeScript** type checking
- **Airbnb style guide** for consistency

### 1.2 Best Practices for Astro

1. **Use Flat Config Format** - Modern ESLint (v8.21.0+) uses the flat config system in `eslint.config.js` (or `.mjs` for ESM)
2. **Separate Astro-specific rules** - Use `eslint-plugin-astro` to lint `.astro` file contents
3. **Include TypeScript parser** - Use `@typescript-eslint/parser` for type-aware linting
4. **Extend Airbnb for JavaScript** - Use `eslint-config-airbnb-base` for JavaScript best practices
5. **Add TypeScript variants** - Use `eslint-config-airbnb-typescript` for TypeScript-specific rules

### 1.3 Required npm Packages

```json
{
  "devDependencies": {
    "eslint": "^9.0.0",
    "eslint-plugin-astro": "^1.2.0",
    "eslint-plugin-preact": "^3.2.0",
    "eslint-config-airbnb-base": "^15.0.0",
    "eslint-config-airbnb-typescript": "^18.0.0",
    "eslint-plugin-import": "^2.29.0",
    "eslint-plugin-jsx-a11y": "^6.8.0",
    "@typescript-eslint/eslint-plugin": "^7.0.0",
    "@typescript-eslint/parser": "^7.0.0",
    "eslint-config-prettier": "^9.0.0"
  }
}
```

### 1.4 ESLint Flat Config Setup (eslint.config.js)

Create `/workspaces/quarkus-astro-app/src/main/webui/eslint.config.js`:

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
      },
    },
  },

  // Airbnb base configuration for JavaScript
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
      // TypeScript-specific rule adjustments
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
    },
  },

  // Airbnb TypeScript configuration
  {
    files: ['**/*.{ts,tsx}'],
    ...airbnbTypeScript,
  },

  // Astro configuration
  {
    files: ['**/*.astro'],
    ...eslintPluginAstro.configs.recommended,
  },

  // Preact configuration for .jsx/.tsx files
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
    },
  },

  // Prettier configuration (disables ESLint formatting rules)
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
      'no-console': ['warn', { allow: ['warn', 'error'] }],
    },
  },
];
```

### 1.5 ESLint Ignore File (.eslintignore)

Create `/workspaces/quarkus-astro-app/src/main/webui/.eslintignore`:

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

# IDE
.vscode
.idea
*.swp

# OS
.DS_Store
Thumbs.db

# Generated files
coverage
*.log
```

### 1.6 TypeScript Configuration for ESLint

Ensure `tsconfig.json` includes proper path mappings and includes ESLint-relevant files:

```json
{
  "extends": "astro/tsconfigs/strict",
  "compilerOptions": {
    "baseUrl": ".",
    "paths": {
      "@/*": ["./src/*"],
      "@components/*": ["./src/components/*"],
      "@layouts/*": ["./src/layouts/*"],
      "@pages/*": ["./src/pages/*"],
      "@assets/*": ["./src/assets/*"]
    }
  },
  "include": [".astro/types.d.ts", "**/*"],
  "exclude": ["dist", "node_modules"]
}
```

### 1.7 VS Code ESLint Integration

Create/update `.vscode/settings.json`:

```json
{
  "editor.codeActionsOnSave": {
    "source.fixAll.eslint": "explicit"
  },
  "eslint.validate": [
    "javascript",
    "javascriptreact",
    "typescript",
    "typescriptreact",
    "astro"
  ],
  "eslint.enable": true
}
```

### 1.8 Package.json Scripts

Add to `package.json`:

```json
{
  "scripts": {
    "lint": "eslint .",
    "lint:fix": "eslint . --fix",
    "lint:check": "eslint . --max-warnings=0"
  }
}
```

---

## 2. Prettier Configuration

### 2.1 Overview

Prettier is an opinionated code formatter that enforces consistent code style automatically. It works alongside ESLint by handling formatting while ESLint handles code quality.

### 2.2 Best Practices with Astro

1. **Use eslint-config-prettier** - This disables ESLint formatting rules that conflict with Prettier
2. **Run Prettier independently** - Use pre-commit hooks or editor integration, not as an ESLint rule
3. **Minimal configuration** - Prettier is opinionated; only override critical settings
4. **Exclude Prettier from ESLint** - Never use `eslint-plugin-prettier`; let Prettier run standalone

### 2.3 Required npm Packages

```json
{
  "devDependencies": {
    "prettier": "^3.0.0",
    "prettier-plugin-astro": "^0.13.0",
    "prettier-plugin-tailwindcss": "^0.5.0",
    "eslint-config-prettier": "^9.0.0"
  }
}
```

### 2.4 Prettier Configuration (.prettierrc.json)

Create `/workspaces/quarkus-astro-app/src/main/webui/.prettierrc.json`:

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
    }
  ]
}
```

### 2.5 Prettier Ignore File (.prettierignore)

Create `/workspaces/quarkus-astro-app/src/main/webui/.prettierignore`:

```
# Dependencies
node_modules
.pnp
.pnp.js

# Build outputs
dist
build
.astro

# Environment variables
.env
.env.*

# IDE
.vscode
.idea

# Generated files
coverage
*.log
package-lock.json
pnpm-lock.yaml
yarn.lock
```

### 2.6 ESLint + Prettier Integration

The key to avoiding conflicts is:

1. **Use eslint-config-prettier** - Already included in ESLint flat config above
2. **Run Prettier before ESLint** - In pre-commit hooks, run: `prettier --write` then `eslint --fix`
3. **Let Prettier own formatting** - Don't use ESLint rules for formatting decisions

### 2.7 VS Code Prettier Integration

Update `.vscode/settings.json`:

```json
{
  "editor.defaultFormatter": "esbenp.prettier-vscode",
  "editor.formatOnSave": true,
  "editor.formatOnPaste": true,
  "[astro]": {
    "editor.defaultFormatter": "esbenp.prettier-vscode"
  },
  "[typescript]": {
    "editor.defaultFormatter": "esbenp.prettier-vscode"
  },
  "[json]": {
    "editor.defaultFormatter": "esbenp.prettier-vscode"
  }
}
```

### 2.8 Package.json Scripts

Add to `package.json`:

```json
{
  "scripts": {
    "format": "prettier --write \"src/**/*.{astro,js,jsx,ts,tsx,json,css}\"",
    "format:check": "prettier --check \"src/**/*.{astro,js,jsx,ts,tsx,json,css}\""
  }
}
```

---

## 3. Pre-commit Hooks Configuration

### 3.1 Overview

Pre-commit hooks ensure code quality standards before commits enter the repository. For this project, we'll use:
- **Husky** - Git hook manager
- **lint-staged** - Run linters on staged files only

### 3.2 Why Husky + lint-staged?

- **Husky** - Manages `.git/hooks` through npm, ensuring all team members get the same hooks
- **lint-staged** - Runs only on staged files, making commits faster and more targeted
- **Both tools together** - Provide a robust, team-friendly setup for Node.js projects

### 3.3 Husky + lint-staged vs. Alternatives

| Tool | Pros | Cons | Best For |
|------|------|------|----------|
| **Husky + lint-staged** | Team-friendly, npm-integrated, fast | Node-specific | Node.js/JavaScript projects ✓ |
| **Pre-commit framework** | Language-agnostic, Docker support | No npm integration | Multi-language monorepos |
| **Lefthook** | Fast, config-simple | Fewer integrations | Performance-critical setups |
| **simple-git-hooks** | Lightweight alternative | Fewer features | Minimal setups |

**Recommendation**: Use **Husky + lint-staged** for this project (JavaScript-focused).

### 3.4 Required npm Packages

```json
{
  "devDependencies": {
    "husky": "^9.0.0",
    "lint-staged": "^15.0.0"
  }
}
```

### 3.5 Husky Installation & Setup

```bash
# Install husky
npm install husky --save-dev

# Initialize husky
npx husky install

# Verify installation
npx husky --version
```

The `husky install` command:
1. Creates `.husky` directory with git hooks
2. Adds `prepare` script to `package.json` (ensures hooks install for all team members)

### 3.6 Create Pre-commit Hook

Create `.husky/pre-commit` (executable):

```bash
#!/bin/sh
. "$(dirname "$0")/_/husky.sh"

# Run lint-staged for staged files
npx lint-staged

# Return the exit code from lint-staged
exit $?
```

Make it executable:
```bash
chmod +x .husky/pre-commit
```

### 3.7 Lint-staged Configuration

Create `.lintstagedrc.json` in the webui directory:

```json
{
  "*.{js,mjs,cjs,ts,tsx,jsx}": [
    "prettier --write",
    "eslint --fix"
  ],
  "*.astro": [
    "prettier --write"
  ],
  "*.{json,jsonc}": [
    "prettier --write"
  ],
  "*.{css,scss}": [
    "prettier --write"
  ],
  "*.{md,mdx}": [
    "prettier --write"
  ]
}
```

### 3.8 Husky + Maven Integration

For the monorepo structure (Maven parent + npm webui), create a root-level pre-commit that handles both:

Create `/workspaces/.husky/pre-commit` (root level):

```bash
#!/bin/sh
. "$(dirname "$0")/_/husky.sh"

# If changes are in the webui directory, run npm pre-commit
if git diff --cached --name-only | grep -q "src/main/webui/"; then
  cd src/main/webui
  npx lint-staged
  exit_code=$?
  cd - > /dev/null

  if [ $exit_code -ne 0 ]; then
    exit $exit_code
  fi
fi

# Note: Maven-side hooks would be configured separately via maven-git-hooks plugin
# See: https://github.com/oslomarketsolutions/pre-commit-maven-plugin

exit 0
```

### 3.9 Package.json Scripts

Add to `package.json`:

```json
{
  "scripts": {
    "prepare": "husky install"
  }
}
```

### 3.10 Bypassing Hooks (Emergency Use Only)

For rare cases where you need to skip hooks:

```bash
git commit --no-verify
```

---

## 4. Orval Configuration for OpenAPI Client Generation

### 4.1 Overview

Orval automatically generates TypeScript API clients from OpenAPI specifications. For this project:
- **Input**: OpenAPI schema at `/workspaces/quarkus-astro-app/src/main/webui/api/openapi.json`
- **Output**: TypeScript clients with TanStack Query hooks
- **HTTP Client**: Axios or Fetch API

### 4.2 OpenAPI Schema Location

Place your OpenAPI schema at:
```
/workspaces/quarkus-astro-app/src/main/webui/api/openapi.json
```

The schema can be:
- A static `openapi.json` file
- Generated from Quarkus SmallRye OpenAPI endpoint: `http://localhost:8080/q/openapi`

### 4.3 Required npm Packages

```json
{
  "devDependencies": {
    "orval": "^7.0.0"
  },
  "dependencies": {
    "axios": "^1.6.0",
    "@tanstack/react-query": "^5.0.0",
    "zod": "^3.22.0"
  }
}
```

### 4.4 Orval Configuration File (orval.config.ts)

Create `/workspaces/quarkus-astro-app/src/main/webui/orval.config.ts`:

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
      baseUrl: process.env.API_BASE_URL || 'http://localhost:8080',
      prettier: true,
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
      },
      mock: {
        enabled: true,
        type: 'msw',
      },
      validation: true,
    },
  },
});
```

### 4.5 Configuration Options Explained

| Option | Purpose | Example |
|--------|---------|---------|
| `input.target` | Path to OpenAPI schema | `./api/openapi.json` |
| `output.target` | Generated client output directory | `./src/api/generated` |
| `output.mode` | File organization | `split`, `tags`, or `single` |
| `output.client` | API client type | `axios`, `react-query`, `fetch` |
| `output.httpClient` | HTTP transport | `axios` or `fetch` |
| `output.baseUrl` | API base URL | `http://localhost:8080` |
| `override.mutator` | Custom Axios instance | Points to `./src/api/mutator.ts` |
| `override.query` | TanStack Query options | Stale time, cache time |
| `output.mock.enabled` | MSW mocking | `true` or `false` |

### 4.6 Custom Axios Mutator (src/api/mutator.ts)

Create `/workspaces/quarkus-astro-app/src/main/webui/src/api/mutator.ts`:

```typescript
import axios, { AxiosRequestConfig } from 'axios';

export const customAxiosInstance = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080',
  timeout: 30000,
  withCredentials: true,
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
  (error) => Promise.reject(error)
);

// Response interceptor for handling errors
customAxiosInstance.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response?.status === 401) {
      // Handle unauthorized - redirect to login
      window.location.href = '/login';
    }
    return Promise.reject(error);
  }
);

export const axiosInstance = customAxiosInstance;
```

### 4.7 TanStack Query + Axios Integration

The generated code will look like:

```typescript
// src/api/generated/queries.ts (auto-generated)
import { useQuery } from '@tanstack/react-query';
import { customAxiosInstance } from '../mutator';

export const useGetTasks = (options?) => {
  return useQuery({
    queryKey: ['tasks'],
    queryFn: () => customAxiosInstance.get('/api/tasks').then(res => res.data),
    ...options,
  });
};
```

### 4.8 Environment Variables (.env.local)

Create `/workspaces/quarkus-astro-app/src/main/webui/.env.local`:

```
VITE_API_BASE_URL=http://localhost:8080
VITE_API_TIMEOUT=30000
```

### 4.9 Package.json Scripts

Add to `package.json`:

```json
{
  "scripts": {
    "api:generate": "orval --config orval.config.ts",
    "api:validate": "orval validate --config orval.config.ts",
    "api:watch": "orval --config orval.config.ts --watch",
    "prebuild": "npm run api:generate"
  }
}
```

### 4.10 Integration with Astro Islands

Example of using generated hooks in Astro with Preact islands:

```astro
---
// src/pages/tasks.astro
import TaskList from '../components/TaskList';
---

<html>
  <head>
    <title>Tasks</title>
  </head>
  <body>
    <!-- TaskList is a Preact island that uses React Query hooks -->
    <TaskList client:load />
  </body>
</html>
```

```tsx
// src/components/TaskList.tsx (Preact component with React Query)
import { useGetTasks } from '../api/generated/queries';

export default function TaskList() {
  const { data: tasks, isLoading, error } = useGetTasks();

  if (isLoading) return <div>Loading...</div>;
  if (error) return <div>Error: {error.message}</div>;

  return (
    <ul>
      {tasks?.map(task => (
        <li key={task.id}>{task.title}</li>
      ))}
    </ul>
  );
}
```

---

## 5. Tailwind CSS + Shadcn UI Configuration with Astro

### 5.1 Overview

Tailwind CSS provides utility-first CSS styling. Shadcn/ui provides pre-built, customizable components. Together they enable rapid UI development.

### 5.2 Best Practices

1. **Tailwind v4 with Vite plugin** - Use the modern approach with `@tailwindcss/vite`
2. **Shadcn/ui with Astro** - Add components directly to your codebase (not as a package)
3. **Island architecture** - Wrap Shadcn UI components in Preact/React islands for interactivity
4. **CSS scope management** - Keep Tailwind CSS properly scoped to avoid conflicts

### 5.3 Required npm Packages

```json
{
  "dependencies": {
    "astro": "^5.16.0",
    "tailwindcss": "^4.0.0",
    "@tailwindcss/vite": "^4.0.0",
    "class-variance-authority": "^0.7.0",
    "clsx": "^2.0.0",
    "tailwind-merge": "^2.2.0"
  },
  "devDependencies": {
    "prettier-plugin-tailwindcss": "^0.5.0"
  }
}
```

### 5.4 Tailwind CSS v4 Setup with Astro

1. **Create global CSS file** (`src/styles/globals.css`):

```css
@import "tailwindcss";

/* Custom CSS can go here */
@layer components {
  .btn-primary {
    @apply px-4 py-2 rounded-lg bg-blue-600 text-white hover:bg-blue-700 transition-colors;
  }
}
```

2. **Import in Astro layout** (`src/layouts/Layout.astro`):

```astro
---
import '../styles/globals.css';
---

<html lang="en">
  <head>
    <meta charset="utf-8" />
    <meta name="viewport" content="width=device-width" />
    <title>Task Manager</title>
  </head>
  <body>
    <slot />
  </body>
</html>
```

3. **Update astro.config.mjs**:

```javascript
import { defineConfig } from 'astro/config';
import tailwindcss from '@tailwindcss/vite';
import preact from '@astrojs/preact';

export default defineConfig({
  integrations: [preact()],
  vite: {
    plugins: [tailwindcss()],
  },
});
```

### 5.5 Tailwind Config (tailwind.config.mjs) - Optional

For Tailwind v3 or custom theme configuration:

```javascript
/** @type {import('tailwindcss').Config} */
export default {
  content: ['./src/**/*.{astro,html,js,jsx,md,mdx,svelte,ts,tsx,vue}'],
  theme: {
    extend: {
      colors: {
        brand: {
          50: '#f9fafb',
          500: '#3b82f6',
          900: '#111827',
        },
      },
      spacing: {
        128: '32rem',
      },
    },
  },
  plugins: [],
};
```

### 5.6 Shadcn/ui Setup with Astro

#### Step 1: Create components.json

Create `/workspaces/quarkus-astro-app/src/main/webui/components.json`:

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
    "@ui": "./src/components/ui"
  }
}
```

#### Step 2: Install Shadcn/ui CLI

```bash
npm install -D @shadcn-cli/init
```

#### Step 3: Initialize Shadcn/ui

```bash
npx shadcn-cli@latest init
```

This will:
- Ask for styling preferences
- Create `src/components/ui` directory
- Add utility functions to `src/lib/utils.ts`

#### Step 4: Add Components

```bash
npx shadcn-cli@latest add button
npx shadcn-cli@latest add card
npx shadcn-cli@latest add input
npx shadcn-cli@latest add select
# Add more as needed
```

### 5.7 Shadcn UI Components with Astro Islands

Shadcn/ui components must be wrapped in islands (client-side Preact/React components) for interactivity:

```astro
---
// src/pages/tasks.astro
import Layout from '../layouts/Layout.astro';
import TaskForm from '../components/TaskForm';
---

<Layout title="Tasks">
  <main class="container mx-auto py-8">
    <h1 class="text-4xl font-bold mb-6">Task Manager</h1>

    <!-- This is a Preact island that uses Shadcn UI buttons -->
    <TaskForm client:load />
  </main>
</Layout>
```

```tsx
// src/components/TaskForm.tsx (Preact island)
import { useState } from 'preact/hooks';
import { Button } from './ui/button';
import { Input } from './ui/input';
import { Card } from './ui/card';

export default function TaskForm() {
  const [title, setTitle] = useState('');

  const handleSubmit = (e: Event) => {
    e.preventDefault();
    // Submit task
    console.log('Create task:', title);
  };

  return (
    <Card class="p-6 max-w-md">
      <form onSubmit={handleSubmit} class="space-y-4">
        <Input
          placeholder="Task title"
          value={title}
          onInput={(e) => setTitle((e.target as HTMLInputElement).value)}
        />
        <Button type="submit">Create Task</Button>
      </form>
    </Card>
  );
}
```

### 5.8 Utility Functions (src/lib/utils.ts)

Create `/workspaces/quarkus-astro-app/src/main/webui/src/lib/utils.ts`:

```typescript
import { clsx, type ClassValue } from 'clsx';
import { twMerge } from 'tailwind-merge';

export function cn(...inputs: ClassValue[]) {
  return twMerge(clsx(inputs));
}
```

### 5.9 Example Shadcn Component Override

Shadcn/ui components are copy-pasted into your codebase, allowing full customization:

```tsx
// src/components/ui/button.tsx (customized example)
import { cn } from '@/lib/utils';

interface ButtonProps extends React.ButtonHTMLAttributes<HTMLButtonElement> {
  variant?: 'default' | 'outline' | 'ghost';
  size?: 'sm' | 'md' | 'lg';
}

export function Button({
  className,
  variant = 'default',
  size = 'md',
  ...props
}: ButtonProps) {
  return (
    <button
      className={cn(
        'font-semibold rounded-lg transition-colors',
        variant === 'default' && 'bg-blue-600 text-white hover:bg-blue-700',
        variant === 'outline' && 'border border-gray-300 hover:bg-gray-50',
        variant === 'ghost' && 'hover:bg-gray-100',
        size === 'sm' && 'px-3 py-1 text-sm',
        size === 'md' && 'px-4 py-2',
        size === 'lg' && 'px-6 py-3 text-lg',
        className
      )}
      {...props}
    />
  );
}
```

### 5.10 Package.json Scripts

Add to `package.json`:

```json
{
  "scripts": {
    "add:component": "shadcn-cli add"
  }
}
```

---

## 6. Complete Integration Example

### 6.1 Project Structure

```
quarkus-astro-app/src/main/webui/
├── .husky/
│   └── pre-commit
├── src/
│   ├── api/
│   │   ├── generated/         (auto-generated by Orval)
│   │   ├── mutator.ts
│   │   └── openapi.json
│   ├── components/
│   │   ├── ui/                (Shadcn UI components)
│   │   │   ├── button.tsx
│   │   │   ├── card.tsx
│   │   │   └── ...
│   │   ├── TaskList.tsx       (Preact island)
│   │   └── TaskForm.tsx       (Preact island)
│   ├── layouts/
│   │   └── Layout.astro
│   ├── pages/
│   │   ├── index.astro
│   │   └── tasks.astro
│   ├── styles/
│   │   └── globals.css
│   └── lib/
│       └── utils.ts
├── public/
├── api/
│   └── openapi.json
├── .eslintignore
├── .eslintrc.json
├── .gitignore
├── .lintstagedrc.json
├── .prettierignore
├── .prettierrc.json
├── astro.config.mjs
├── components.json
├── eslint.config.js
├── orval.config.ts
├── package.json
├── tailwind.config.mjs
└── tsconfig.json
```

### 6.2 Setup Workflow

```bash
# 1. Install dependencies
cd src/main/webui
npm install

# 2. Initialize Husky
npx husky install

# 3. Initialize Prettier
# (Already configured via .prettierrc.json)

# 4. Setup Tailwind CSS
npm install @tailwindcss/vite
# Update astro.config.mjs (see section 5.4)

# 5. Initialize Shadcn/ui
npm install -D @shadcn-cli/init
npx shadcn-cli@latest init
npx shadcn-cli@latest add button card input

# 6. Setup Orval
npm install orval
npm run api:generate

# 7. Test the setup
npm run lint
npm run format:check
npm run api:validate
npm run build
```

### 6.3 Development Commands

```bash
# Development
npm run dev

# Linting and formatting
npm run lint         # Check ESLint
npm run lint:fix     # Fix ESLint issues
npm run format       # Format with Prettier
npm run format:check # Check Prettier formatting

# API generation
npm run api:generate # Generate API client from OpenAPI
npm run api:watch    # Watch for OpenAPI changes

# Build
npm run build

# Add Shadcn components
npm run add:component button
```

---

## 7. CI/CD Integration

### 7.1 GitHub Actions Workflow

Create `.github/workflows/lint-and-test.yml`:

```yaml
name: Lint and Test

on: [push, pull_request]

jobs:
  lint:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v4
      - uses: actions/setup-node@v4
        with:
          node-version: 20
      - name: Install dependencies
        working-directory: src/main/webui
        run: npm ci
      - name: Run ESLint
        working-directory: src/main/webui
        run: npm run lint:check
      - name: Check Prettier formatting
        working-directory: src/main/webui
        run: npm run format:check
      - name: Validate OpenAPI schema
        working-directory: src/main/webui
        run: npm run api:validate

  build:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v4
      - uses: actions/setup-node@v4
        with:
          node-version: 20
      - name: Install dependencies
        working-directory: src/main/webui
        run: npm ci
      - name: Build
        working-directory: src/main/webui
        run: npm run build
```

---

## 8. Troubleshooting

### ESLint Issues

**Issue**: Parser errors with `.astro` files
```
Error: UnexpectedToken
```

**Solution**: Ensure `eslint-plugin-astro` is installed and configured in `eslint.config.js`

---

**Issue**: TypeScript path alias errors
```
Error: import/no-unresolved for '@/*'
```

**Solution**:
1. Add path mappings to `tsconfig.json`
2. Disable `import/no-unresolved` in ESLint config for custom paths

---

### Prettier Issues

**Issue**: Prettier and ESLint conflicts
```
Conflict: ESLint rule X vs Prettier format
```

**Solution**: Ensure `eslint-config-prettier` is last in extends array

---

**Issue**: Astro file formatting errors
```
Error: Unknown parser "astro"
```

**Solution**: Install `prettier-plugin-astro` and configure in `.prettierrc.json`

---

### Pre-commit Hook Issues

**Issue**: Hooks not running
```
npm ERR! Husky not properly installed
```

**Solution**: Run `npm install` (triggers `prepare` script) or `npx husky install`

---

**Issue**: Hooks blocking all commits
```
All commits blocked by lint-staged
```

**Solution**: Run `npm run lint:fix && npm run format` to fix issues, then commit again

---

### Orval Issues

**Issue**: OpenAPI schema not found
```
Error: Cannot find openapi.json
```

**Solution**: Ensure schema exists at `./api/openapi.json` or update `orval.config.ts` path

---

**Issue**: Generated code has compilation errors
```
Error: Type 'unknown' is not assignable to type 'string'
```

**Solution**: Enable `validation: true` in `orval.config.ts` or refine OpenAPI schema

---

## 9. Summary Table

| Tool | Purpose | Config File | Key Packages |
|------|---------|-------------|--------------|
| **ESLint** | Code quality | `eslint.config.js` | `eslint`, `@typescript-eslint/*`, `eslint-plugin-astro` |
| **Prettier** | Code formatting | `.prettierrc.json` | `prettier`, `prettier-plugin-astro` |
| **Husky** | Git hooks | `.husky/pre-commit` | `husky` |
| **lint-staged** | Staged file linting | `.lintstagedrc.json` | `lint-staged` |
| **Orval** | API generation | `orval.config.ts` | `orval`, `axios`, `@tanstack/react-query` |
| **Tailwind CSS** | Utility CSS | `tailwind.config.mjs` | `tailwindcss`, `@tailwindcss/vite` |
| **Shadcn/ui** | UI components | `components.json` | Component files in `src/components/ui` |

---

## 10. References

- [Astro Documentation](https://docs.astro.build/)
- [ESLint Flat Config](https://eslint.org/docs/latest/use/configure/configuration-files)
- [eslint-plugin-astro](https://github.com/ota-meshi/eslint-plugin-astro)
- [Prettier Documentation](https://prettier.io/docs/en/index.html)
- [Husky](https://typicode.github.io/husky/)
- [lint-staged](https://github.com/lint-staged/lint-staged)
- [Orval](https://orval.dev/)
- [TanStack Query](https://tanstack.com/query/latest)
- [Tailwind CSS](https://tailwindcss.com/)
- [Shadcn/ui](https://ui.shadcn.com/)

