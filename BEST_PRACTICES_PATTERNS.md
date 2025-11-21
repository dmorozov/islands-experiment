# Task Manager Islands Architecture: Best Practices & Patterns

Production-ready patterns and best practices for the Task Manager Islands Architecture project.

---

## 1. Astro Islands Architecture Best Practices

### 1.1 Understanding Islands

Astro sends zero JavaScript by default. Islands are interactive components that "hydrate" on the client:

```astro
---
// src/pages/tasks.astro
import TaskList from '../components/TaskList';
import TaskForm from '../components/TaskForm';
---

<html>
  <head>
    <title>Tasks</title>
  </head>
  <body>
    <!-- This is static HTML (no JS) -->
    <h1>Task Manager</h1>

    <!-- These are islands (interactive) -->
    <TaskForm client:load />
    <TaskList client:load />
  </body>
</html>
```

### 1.2 Client Directives

| Directive | When to Use | Example |
|-----------|-----------|---------|
| `client:load` | Needs JavaScript immediately | Forms, modals |
| `client:idle` | Non-critical interactivity | Analytics, secondary UI |
| `client:visible` | Below the fold content | Infinite scroll, lazy features |
| `client:media` | Responsive interactivity | Mobile menus |
| `client:only` | Framework-specific (Preact-only) | Preact-only components |

**Best Practice**: Use `client:visible` for most components to defer hydration:

```astro
---
import ExpensiveComponent from '../components/Expensive';
---

<div>
  <ExpensiveComponent client:visible />
</div>
```

### 1.3 Sharing State Between Islands

Islands cannot share component state. Use these patterns:

#### Pattern 1: URL State (Recommended)
```tsx
// src/components/TaskList.tsx
import { useEffect, useState } from 'preact/hooks';

export default function TaskList() {
  const [filter, setFilter] = useState(() => {
    const url = new URL(window.location);
    return url.searchParams.get('filter') || 'all';
  });

  const handleFilterChange = (newFilter: string) => {
    const url = new URL(window.location);
    url.searchParams.set('filter', newFilter);
    window.history.pushState({}, '', url);
    setFilter(newFilter);
  };

  return (
    <div>
      <button onClick={() => handleFilterChange('active')}>Active</button>
      <button onClick={() => handleFilterChange('completed')}>Completed</button>
      {/* Show tasks based on filter */}
    </div>
  );
}
```

#### Pattern 2: Local Storage
```tsx
// src/components/UserPreferences.tsx
import { useEffect, useState } from 'preact/hooks';

export default function UserPreferences() {
  const [theme, setTheme] = useState<'light' | 'dark'>(() => {
    if (typeof window !== 'undefined') {
      return localStorage.getItem('theme') as 'light' | 'dark' || 'light';
    }
    return 'light';
  });

  useEffect(() => {
    localStorage.setItem('theme', theme);
    document.documentElement.classList.toggle('dark', theme === 'dark');
  }, [theme]);

  return (
    <button onClick={() => setTheme(theme === 'light' ? 'dark' : 'light')}>
      Toggle Theme
    </button>
  );
}
```

#### Pattern 3: Global Event Bus (Advanced)
```typescript
// src/lib/eventBus.ts
type EventListener<T> = (data: T) => void;

class EventBus {
  private listeners: Map<string, Set<EventListener<any>>> = new Map();

  on<T>(event: string, listener: EventListener<T>): () => void {
    if (!this.listeners.has(event)) {
      this.listeners.set(event, new Set());
    }
    this.listeners.get(event)!.add(listener);

    // Return unsubscribe function
    return () => {
      this.listeners.get(event)?.delete(listener);
    };
  }

  emit<T>(event: string, data: T): void {
    this.listeners.get(event)?.forEach((listener) => listener(data));
  }
}

export const eventBus = new EventBus();
```

Usage in islands:
```tsx
// src/components/TaskForm.tsx
import { eventBus } from '../lib/eventBus';

export default function TaskForm() {
  const handleSubmit = (e: Event) => {
    e.preventDefault();
    // Create task
    eventBus.emit('task:created', { id: 1, title: 'New Task' });
  };

  return <form onSubmit={handleSubmit}>...</form>;
}

// src/components/TaskList.tsx
import { useEffect, useState } from 'preact/hooks';
import { eventBus } from '../lib/eventBus';

export default function TaskList() {
  const [tasks, setTasks] = useState<Task[]>([]);

  useEffect(() => {
    const unsubscribe = eventBus.on('task:created', (task) => {
      setTasks((prev) => [...prev, task]);
    });
    return unsubscribe;
  }, []);

  return <ul>{tasks.map((t) => <li key={t.id}>{t.title}</li>)}</ul>;
}
```

---

## 2. ESLint Best Practices

### 2.1 Custom ESLint Rules for Project

Extend the configuration with project-specific rules:

```javascript
// eslint.config.js - add to end of file
{
  files: ['src/**/*.{ts,tsx,astro}'],
  rules: {
    // Require task/issue references in commits
    'no-console': ['warn', { allow: ['warn', 'error'] }],

    // Disallow console in production code
    'no-debugger': 'error',

    // Enforce type imports for better tree-shaking
    '@typescript-eslint/consistent-type-imports': [
      'error',
      { prefer: 'type-imports' },
    ],

    // Require explicit return types for better documentation
    '@typescript-eslint/explicit-function-return-types': [
      'warn',
      {
        allowExpressions: true,
        allowTypedFunctionExpressions: true,
      },
    ],
  },
}
```

### 2.2 Disabling Rules for Specific Cases

Use inline comments sparingly, with clear reasoning:

```typescript
// Good: Specific rule with reason
// eslint-disable-next-line @typescript-eslint/no-unsafe-assignment -- API response type is dynamic
const data: unknown = await fetchData();

// Bad: Generic disable
// eslint-disable-next-line
const data = await fetchData();

// Good: Multiple lines
/* eslint-disable @typescript-eslint/no-explicit-any */
function legacyFunction(arg: any): any {
  return arg;
}
/* eslint-enable @typescript-eslint/no-explicit-any */
```

### 2.3 ESLint for Different File Types

Configure different rules for tests vs. source:

```javascript
// eslint.config.js
{
  files: ['**/*.test.{ts,tsx}', '**/*.spec.{ts,tsx}'],
  rules: {
    'no-console': 'off',        // Allow console in tests
    '@typescript-eslint/no-explicit-any': 'off',
  },
},
```

---

## 3. Prettier Best Practices

### 3.1 Prettier Formatting Philosophy

**Key Principle**: Accept Prettier's opinions. Fighting it reduces value.

```tsx
// Good: Let Prettier reformat as needed
const Component = ({ longPropName, anotherLongProp, thirdProp }: Props) => (
  <div>Content</div>
);

// Bad: Manual formatting that Prettier will override
const Component = ({
  longPropName,
  anotherLongProp,
  thirdProp,
}: Props) => (
  <div>Content</div>
);
```

### 3.2 Prettier Configuration Override by File Type

Use overrides for different file types:

```json
{
  ".prettierrc.json": {
    "overrides": [
      {
        "files": "*.astro",
        "options": {
          "parser": "astro"
        }
      },
      {
        "files": ["*.json", "*.jsonc"],
        "options": {
          "parser": "json",
          "trailingComma": "none"
        }
      },
      {
        "files": "*.md",
        "options": {
          "proseWrap": "always",
          "printWidth": 80
        }
      }
    ]
  }
}
```

### 3.3 Running Prettier Selectively

```bash
# Format specific file
npx prettier --write src/components/Button.tsx

# Format by glob pattern
npx prettier --write "src/**/*.{ts,tsx}"

# Check without writing
npx prettier --check src/

# Dry run
npx prettier --write src/ --dry-run
```

---

## 4. TypeScript Best Practices

### 4.1 Type-Safe Props Pattern

```tsx
// Good: Separate type definition
interface TaskItemProps {
  id: string;
  title: string;
  completed: boolean;
  onToggle: (id: string) => Promise<void>;
}

export function TaskItem({ id, title, completed, onToggle }: TaskItemProps) {
  return (
    <li>
      <input
        type="checkbox"
        checked={completed}
        onChange={() => onToggle(id)}
      />
      <span>{title}</span>
    </li>
  );
}

// Bad: Inline type (harder to document and reuse)
export function TaskItem({
  id,
  title,
  completed,
  onToggle,
}: {
  id: string;
  title: string;
  completed: boolean;
  onToggle: (id: string) => Promise<void>;
}) {
  // ...
}
```

### 4.2 Strict Type Safety

Enable strict mode for catching errors:

```json
{
  "compilerOptions": {
    "strict": true,
    "noImplicitAny": true,
    "strictNullChecks": true,
    "strictFunctionTypes": true,
    "noImplicitThis": true,
    "alwaysStrict": true
  }
}
```

### 4.3 Type Imports

Use `type` imports for better tree-shaking:

```tsx
// Good: Type-only import
import type { Task } from '../types/task';
import { useTask } from '../hooks/useTask';

// Less ideal: Mixed import
import { useTask, type Task } from '../hooks/useTask';

// Avoid: Default import
import Task from '../types/task';
```

### 4.4 Type Utilities

Create reusable type utilities:

```typescript
// src/types/common.ts
export type DeepReadonly<T> = {
  readonly [K in keyof T]: T[K] extends object
    ? DeepReadonly<T[K]>
    : T[K];
};

export type Nullable<T> = T | null;

export type Async<T> = Promise<T>;

export type Result<T, E = Error> = { success: true; data: T } | { success: false; error: E };

// Usage
import type { DeepReadonly, Result } from '../types/common';

type Config = DeepReadonly<{ db: { host: string; port: number } }>;

async function fetchTask(id: string): Async<Result<Task>> {
  // Implementation
}
```

---

## 5. Tailwind CSS Best Practices

### 5.1 Organize Styles with @layer

Use Tailwind's layers for organization:

```css
/* src/styles/globals.css */
@import "tailwindcss";

/* Base styles for HTML elements */
@layer base {
  body {
    @apply bg-white text-gray-900 antialiased;
  }

  a {
    @apply text-blue-600 hover:underline;
  }
}

/* Component classes for reuse */
@layer components {
  .btn-primary {
    @apply inline-flex items-center justify-center gap-2 rounded-lg bg-blue-600 px-4 py-2 font-semibold text-white transition-colors hover:bg-blue-700 disabled:opacity-50;
  }

  .card {
    @apply rounded-lg border border-gray-200 bg-white p-6 shadow-sm;
  }
}

/* Utility overrides */
@layer utilities {
  .text-truncate-2 {
    display: -webkit-box;
    -webkit-line-clamp: 2;
    -webkit-box-orient: vertical;
    overflow: hidden;
  }
}
```

### 5.2 Extracting Component Classes

Instead of inline classes, extract to components:

```tsx
// Good: Extracted component
interface ButtonProps extends React.ButtonHTMLAttributes<HTMLButtonElement> {
  variant?: 'primary' | 'secondary' | 'ghost';
}

export function Button({ variant = 'primary', className, ...props }: ButtonProps) {
  const baseStyles = 'inline-flex items-center justify-center gap-2 rounded-lg font-semibold transition-colors';
  const variants = {
    primary: 'bg-blue-600 text-white hover:bg-blue-700',
    secondary: 'border border-gray-300 hover:bg-gray-50',
    ghost: 'hover:bg-gray-100',
  };

  return (
    <button
      className={cn(baseStyles, variants[variant], className)}
      {...props}
    />
  );
}

// Bad: Inline classes everywhere
<button className="inline-flex items-center justify-center gap-2 rounded-lg bg-blue-600 px-4 py-2 font-semibold text-white transition-colors hover:bg-blue-700">
  Click me
</button>
```

### 5.3 Dark Mode Support

```css
/* src/styles/globals.css */
@layer components {
  .card {
    @apply rounded-lg border border-gray-200 bg-white p-6 shadow-sm;
    @apply dark:border-gray-700 dark:bg-gray-900;
  }
}
```

```tsx
// Toggle dark mode
export function ThemeToggle() {
  const [isDark, setIsDark] = useState(
    document.documentElement.classList.contains('dark')
  );

  const toggle = () => {
    document.documentElement.classList.toggle('dark');
    setIsDark(!isDark);
    localStorage.setItem('theme', isDark ? 'light' : 'dark');
  };

  return <button onClick={toggle}>Toggle Dark Mode</button>;
}
```

---

## 6. Shadcn/ui Best Practices

### 6.1 Customizing Shadcn Components

Shadcn components are in your codebase—customize freely:

```tsx
// src/components/ui/button.tsx - Customized version
import { cn } from '@/lib/utils';

interface ButtonProps extends React.ButtonHTMLAttributes<HTMLButtonElement> {
  variant?: 'default' | 'outline' | 'ghost' | 'destructive';
  size?: 'sm' | 'md' | 'lg';
  loading?: boolean;
}

export function Button({
  className,
  variant = 'default',
  size = 'md',
  loading = false,
  disabled,
  children,
  ...props
}: ButtonProps) {
  return (
    <button
      className={cn(
        // Base styles
        'inline-flex items-center justify-center gap-2 font-semibold rounded-lg transition-colors',
        // Size variants
        size === 'sm' && 'px-3 py-1 text-sm',
        size === 'md' && 'px-4 py-2',
        size === 'lg' && 'px-6 py-3 text-lg',
        // Color variants
        variant === 'default' && 'bg-blue-600 text-white hover:bg-blue-700',
        variant === 'outline' && 'border border-gray-300 hover:bg-gray-50',
        variant === 'ghost' && 'hover:bg-gray-100',
        variant === 'destructive' && 'bg-red-600 text-white hover:bg-red-700',
        // Disabled state
        (disabled || loading) && 'opacity-50 cursor-not-allowed',
        className
      )}
      disabled={disabled || loading}
      {...props}
    >
      {loading && <span className="animate-spin">⌛</span>}
      {children}
    </button>
  );
}
```

### 6.2 Creating Composition Patterns

```tsx
// src/components/Form.tsx
import type { PropsWithChildren } from 'preact';
import { Label } from './ui/label';
import { Input } from './ui/input';

interface FormProps extends PropsWithChildren {
  onSubmit: (data: Record<string, any>) => void;
}

export function Form({ onSubmit, children }: FormProps) {
  return (
    <form onSubmit={(e) => {
      e.preventDefault();
      const formData = new FormData(e.currentTarget);
      onSubmit(Object.fromEntries(formData));
    }}>
      {children}
    </form>
  );
}

interface FormFieldProps {
  name: string;
  label: string;
  type?: string;
  required?: boolean;
  error?: string;
}

export function FormField({ name, label, type = 'text', required, error }: FormFieldProps) {
  return (
    <div className="space-y-2">
      <Label htmlFor={name}>{label}</Label>
      <Input
        id={name}
        name={name}
        type={type}
        required={required}
        aria-invalid={!!error}
      />
      {error && <p className="text-sm text-red-600">{error}</p>}
    </div>
  );
}

// Usage
export default function LoginForm() {
  const handleSubmit = (data: Record<string, any>) => {
    console.log('Login:', data);
  };

  return (
    <Form onSubmit={handleSubmit}>
      <FormField name="email" label="Email" type="email" required />
      <FormField name="password" label="Password" type="password" required />
      <button type="submit">Login</button>
    </Form>
  );
}
```

---

## 7. Orval + TanStack Query Best Practices

### 7.1 Using Generated Hooks Safely

```tsx
// src/components/TaskList.tsx
import { useGetTasks, useCreateTask, useUpdateTask } from '@/api/generated/queries';
import type { Task } from '@/api/generated/schemas';

export default function TaskList() {
  const { data: tasks, isLoading, error } = useGetTasks();
  const createMutation = useCreateTask();
  const updateMutation = useUpdateTask();

  if (isLoading) return <div>Loading...</div>;
  if (error) return <div>Error: {error.message}</div>;

  const handleCreate = async (title: string) => {
    try {
      await createMutation.mutateAsync({ title });
      // Success! UI will auto-update via React Query
    } catch (error) {
      console.error('Failed to create task:', error);
    }
  };

  const handleToggle = async (task: Task) => {
    await updateMutation.mutateAsync({
      id: task.id,
      completed: !task.completed,
    });
  };

  return (
    <div>
      <button onClick={() => handleCreate('New Task')}>
        {createMutation.isPending ? 'Creating...' : 'Create Task'}
      </button>

      <ul>
        {tasks?.map((task) => (
          <li key={task.id}>
            <input
              type="checkbox"
              checked={task.completed}
              onChange={() => handleToggle(task)}
              disabled={updateMutation.isPending}
            />
            {task.title}
          </li>
        ))}
      </ul>
    </div>
  );
}
```

### 7.2 Query Invalidation Pattern

```typescript
// src/api/hooks.ts
import { useMutation, useQueryClient } from '@tanstack/react-query';
import { createTask as apiCreateTask } from './generated/mutations';

export function useCreateTaskWithInvalidation() {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: apiCreateTask,
    onSuccess: (data) => {
      // Invalidate and refetch tasks list
      queryClient.invalidateQueries({ queryKey: ['tasks'] });

      // Or update cache directly for instant UI update
      queryClient.setQueryData(['tasks'], (old: Task[]) => [...old, data]);
    },
    onError: (error) => {
      console.error('Create failed:', error);
    },
  });
}
```

### 7.3 Error Handling with Zod Validation

```typescript
// src/api/schemas.ts
import { z } from 'zod';

export const TaskSchema = z.object({
  id: z.string(),
  title: z.string().min(1),
  completed: z.boolean(),
  createdAt: z.string().datetime(),
});

export type Task = z.infer<typeof TaskSchema>;

// Validate API responses
const validatedTasks = TaskSchema.array().parse(apiResponse);
```

---

## 8. Pre-commit Hooks Best Practices

### 8.1 Optimizing Hook Performance

Stage only necessary files:

```json
{
  ".lintstagedrc.json": {
    "*.{ts,tsx}": [
      "prettier --write",
      "eslint --fix --max-warnings=0"
    ],
    "*.astro": [
      "prettier --write"
    ]
  }
}
```

### 8.2 Skipping Hooks Safely

Only for emergencies:

```bash
# Skip pre-commit hook (use sparingly!)
git commit --no-verify

# Run linting before committing to catch issues
npm run lint:check
npm run format:check

# Fix issues
npm run lint:fix
npm run format

# Now commit
git commit -m "feat: add task feature"
```

### 8.3 Debugging Hook Issues

```bash
# Run lint-staged directly to debug
npx lint-staged --debug

# Check which files are staged
git diff --cached --name-only

# Manually run linters on staged files
npx eslint $(git diff --cached --name-only --diff-filter=ACM '*.ts' '*.tsx')
```

---

## 9. Component Organization Best Practices

### 9.1 Folder Structure

```
src/
├── components/
│   ├── ui/                    # Shadcn UI components
│   │   ├── button.tsx
│   │   ├── input.tsx
│   │   └── card.tsx
│   ├── forms/                 # Form components
│   │   ├── TaskForm.tsx
│   │   └── TaskFilter.tsx
│   ├── features/              # Feature-specific
│   │   ├── tasks/
│   │   │   ├── TaskList.tsx
│   │   │   ├── TaskItem.tsx
│   │   │   └── TaskStats.tsx
│   │   └── users/
│   │       └── UserProfile.tsx
│   └── layout/                # Layout components
│       ├── Header.tsx
│       ├── Sidebar.tsx
│       └── Footer.tsx
├── layouts/
│   └── Layout.astro
├── pages/
│   ├── index.astro
│   ├── tasks.astro
│   └── settings.astro
├── api/
│   ├── generated/             # Orval generated files
│   ├── mutator.ts
│   └── hooks.ts               # Custom TanStack Query hooks
├── lib/
│   ├── utils.ts               # Utility functions
│   └── eventBus.ts
├── types/
│   ├── common.ts
│   ├── api.ts
│   └── domain.ts
├── styles/
│   └── globals.css
└── assets/
    └── ...
```

### 9.2 Component Naming Conventions

```typescript
// Components
TaskList.tsx           // React/Preact component
TaskListLayout.astro   // Astro layout component
task-item.astro        // Astro content component

// Hooks
useTask.ts            // Custom React hook
useTasks.ts           // Multiple items hook

// Utils
taskUtils.ts          // Task-related utilities
formatDate.ts         // Single utility file
```

---

## 10. Testing Best Practices

### 10.1 ESLint Configuration for Tests

```javascript
// eslint.config.js
{
  files: ['**/*.test.{ts,tsx}', '**/*.spec.{ts,tsx}'],
  rules: {
    'no-console': 'off',
    '@typescript-eslint/no-explicit-any': 'off',
  },
},
```

### 10.2 Test File Organization

```
src/
├── components/
│   ├── __tests__/
│   │   ├── Button.test.tsx
│   │   └── TaskList.test.tsx
│   └── Button.tsx
└── lib/
    ├── __tests__/
    │   └── utils.test.ts
    └── utils.ts
```

---

## 11. Documentation Best Practices

### 11.1 JSDoc Comments

```typescript
/**
 * Creates a new task
 * @param title - The task title (required, min 1 char)
 * @param description - Optional task description
 * @returns The created task with ID
 * @throws {ValidationError} If title is empty
 * @example
 * const task = createTask('Buy milk');
 */
export async function createTask(
  title: string,
  description?: string
): Promise<Task> {
  // Implementation
}
```

### 11.2 Component Documentation

```tsx
/**
 * TaskList Component
 *
 * Displays a filterable list of tasks using TanStack Query
 * for server state management.
 *
 * @component
 * @example
 * return (
 *   <TaskList />
 * )
 */
interface TaskListProps {
  /** Filter tasks by status (optional) */
  filter?: 'all' | 'active' | 'completed';
  /** Callback when task is selected */
  onSelectTask?: (task: Task) => void;
}

export function TaskList({ filter = 'all', onSelectTask }: TaskListProps) {
  // Implementation
}
```

---

## 12. Performance Optimization Best Practices

### 12.1 Component Memoization

```tsx
import { memo } from 'preact/compat';

// Memoize expensive components
export const TaskItem = memo(({ task, onToggle }: TaskItemProps) => {
  return (
    <li>
      <input
        type="checkbox"
        checked={task.completed}
        onChange={() => onToggle(task.id)}
      />
      {task.title}
    </li>
  );
});

TaskItem.displayName = 'TaskItem';
```

### 12.2 Lazy Loading Components

```astro
---
// src/pages/dashboard.astro
import Dashboard from '../components/Dashboard';
---

<html>
  <body>
    <!-- Load with client:visible for below-the-fold content -->
    <Dashboard client:visible />
  </body>
</html>
```

### 12.3 Query Optimization

```typescript
// Use stale time to reduce unnecessary refetches
export const useGetTasks = (options) => {
  return useQuery({
    queryKey: ['tasks'],
    queryFn: () => fetchTasks(),
    staleTime: 5 * 60 * 1000,      // 5 minutes
    cacheTime: 10 * 60 * 1000,     // 10 minutes
    ...options,
  });
};
```

---

## Summary of Best Practices

| Area | Best Practice |
|------|---|
| **Architecture** | Use island directives wisely, prefer `client:visible` |
| **ESLint** | Enforce rules, document exceptions, run in CI/CD |
| **Prettier** | Accept its opinions, run before linting |
| **TypeScript** | Enable strict mode, use type imports |
| **Tailwind** | Use @layer, extract components, embrace utilities |
| **Shadcn/ui** | Customize in your codebase, use composition |
| **Orval** | Validate schemas, handle errors gracefully |
| **TanStack Query** | Use hooks, invalidate caches, handle loading states |
| **Git Hooks** | Use Husky + lint-staged, optimize for speed |
| **Components** | Organize by feature, document with JSDoc |
| **Testing** | Configure ESLint for tests, test islands separately |
| **Performance** | Memoize, lazy load, optimize queries |

