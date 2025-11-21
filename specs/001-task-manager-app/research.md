# Research: Technology Decisions for Task Manager Application

**Date**: 2025-11-21
**Status**: Complete
**Related**: [plan.md](./plan.md)

## Executive Summary

All technology research has been completed and documented. This document consolidates key decisions and references detailed documentation in the repository root (`/workspaces/*.md`).

**Comprehensive Research Documentation**:
- `/workspaces/LINTING_FORMATTING_GUIDE.md` (30KB) - Main technical guide
- `/workspaces/CONFIGURATION_TEMPLATES.md` (20KB) - 17 copy-paste configs
- `/workspaces/NPM_PACKAGES_REFERENCE.md` (8.5KB) - Package management
- `/workspaces/IMPLEMENTATION_CHECKLIST.md` (14KB) - Step-by-step execution
- `/workspaces/BEST_PRACTICES_PATTERNS.md` (24KB) - Production patterns
- `/workspaces/RESEARCH_SUMMARY.md` (14KB) - Overview & findings

---

## Research Area 1: ESLint Configuration for Astro + Preact + TypeScript

### Decision
Use **ESLint flat config format** (`eslint.config.js`) with Airbnb TypeScript style guide

### Rationale
- Flat config is the new standard (ESLint v9+)
- Airbn TypeScript provides production-ready rules
- Native support for Astro and Preact via official plugins
- Better TypeScript integration than legacy `.eslintrc`

### Alternatives Considered
- Standard.js - Rejected: too opinionated, limited customization
- ESLint legacy config - Rejected: deprecated in favor of flat config
- TSLint - Rejected: deprecated, merged into ESLint

### Configuration
See: `/workspaces/CONFIGURATION_TEMPLATES.md` → Section 1: `eslint.config.js`

**Key elements**:
- `eslint-plugin-astro` - Astro-specific linting rules
- `eslint-plugin-preact` - Preact component linting
- `@typescript-eslint/parser` + `@typescript-eslint/eslint-plugin` - TypeScript support
- `eslint-config-airbnb-base` + `eslint-config-airbnb-typescript` - Airbnb style guide
- `eslint-config-prettier` - Disable rules that conflict with Prettier (MUST be last)

### Dependencies
```bash
npm install -D eslint @eslint/js typescript-eslint \
  eslint-plugin-astro eslint-plugin-preact \
  eslint-config-airbnb-base eslint-config-airbnb-typescript \
  eslint-config-prettier
```

See full list: `/workspaces/NPM_PACKAGES_REFERENCE.md`

### References
- [ESLint Flat Config Documentation](https://eslint.org/docs/latest/use/configure/configuration-files-new)
- [eslint-plugin-astro](https://ota-meshi.github.io/eslint-plugin-astro/)
- [Airbnb JavaScript Style Guide](https://github.com/airbnb/javascript)

---

## Research Area 2: Prettier Configuration and ESLint Integration

### Decision
Use **Prettier as standalone formatter** (not as ESLint rule) with Astro and Tailwind plugins

### Rationale
- Prettier focuses on formatting, ESLint focuses on code quality (separation of concerns)
- Better performance running Prettier independently
- Avoids complex ESLint plugin conflicts
- Official recommendation from both Prettier and ESLint teams

### Alternatives Considered
- eslint-plugin-prettier - Rejected: performance issues, conflicts with other rules
- Inline Prettier via ESLint - Rejected: slower, harder to debug

### Configuration
See: `/workspaces/CONFIGURATION_TEMPLATES.md` → Section 3: `.prettierrc.json`

**Key elements**:
- Minimal configuration (defaults are good)
- `prettier-plugin-astro` - Format .astro files correctly
- `prettier-plugin-tailwindcss` - Sort Tailwind classes automatically
- `eslint-config-prettier` in ESLint config - Disable conflicting ESLint rules

**Recommended settings**:
```json
{
  "semi": true,
  "singleQuote": true,
  "tabWidth": 2,
  "trailingComma": "es5",
  "printWidth": 100,
  "plugins": ["prettier-plugin-astro", "prettier-plugin-tailwindcss"]
}
```

### Dependencies
```bash
npm install -D prettier prettier-plugin-astro prettier-plugin-tailwindcss
```

### Integration Order
1. Prettier runs first (formats code)
2. ESLint runs second (checks quality)
3. `eslint-config-prettier` prevents ESLint from complaining about Prettier formatting

### References
- [Prettier vs. Linters](https://prettier.io/docs/en/comparison.html)
- [prettier-plugin-astro](https://github.com/withastro/prettier-plugin-astro)
- [prettier-plugin-tailwindcss](https://github.com/tailwindlabs/prettier-plugin-tailwindcss)

---

## Research Area 3: Pre-commit Hook Implementation

### Decision
Use **Husky + lint-staged** for pre-commit hooks

### Rationale
- npm-native solution (team-friendly, no Python dependency)
- lint-staged runs linters only on staged files (fast)
- Husky integrates seamlessly with npm scripts
- Industry standard for JavaScript projects

### Alternatives Considered
- pre-commit (Python) - Rejected: requires Python runtime, extra dependency
- simple-git-hooks - Rejected: less feature-rich, smaller community
- Manual git hooks - Rejected: hard to share across team, not version-controlled

### Configuration
See: `/workspaces/CONFIGURATION_TEMPLATES.md` → Section 5: `.lintstagedrc.json` and Section 6: `.husky/pre-commit`

**Workflow**:
1. Developer runs `git commit`
2. Husky triggers `.husky/pre-commit` script
3. lint-staged runs commands on staged files only:
   - Prettier formats files
   - ESLint checks for errors (with --fix)
   - TypeScript type-checks
4. If all pass, commit succeeds; if any fail, commit is blocked

### Dependencies
```bash
npm install -D husky lint-staged
```

### Setup Commands
```bash
# Initialize Husky
npx husky init

# Install git hooks
npm run prepare
```

### Integration with Maven
Optional: Can add Maven pre-commit plugin to check Java files, but Checkstyle/PMD already run on `mvn validate`

### References
- [Husky Documentation](https://typicode.github.io/husky/)
- [lint-staged Documentation](https://github.com/okonet/lint-staged)

---

## Research Area 4: Orval Configuration for OpenAPI Client Generation

### Decision
Use **Orval with TanStack Query v5** to generate TypeScript client from Quarkus OpenAPI schema

### Rationale
- Type-safe API client generation (eliminates manual typing)
- TanStack Query provides excellent data fetching/caching/state management
- Axios custom mutator allows auth headers and error handling
- Automatic updates when backend API changes

### Alternatives Considered
- Manual fetch calls - Rejected: no type safety, manual typing error-prone
- openapi-typescript-codegen - Rejected: less feature-rich than Orval
- RTK Query - Rejected: tied to Redux, too heavy for this project

### Configuration
See: `/workspaces/CONFIGURATION_TEMPLATES.md` → Section 8: `orval.config.ts` and Section 9: `src/api/mutator.ts`

**Workflow**:
1. Quarkus generates OpenAPI schema to `src/main/webui/api/openapi.json`
2. Run `npm run generate:api` (calls Orval)
3. Orval generates TypeScript client in `src/lib/api/` with TanStack Query hooks
4. Frontend imports generated hooks: `import { useGetTasks } from '@/lib/api'`

**File Locations**:
- Input schema: `./api/openapi.json` (generated by Quarkus)
- Output client: `./src/lib/api/` (generated by Orval)
- Axios mutator: `./src/api/mutator.ts` (custom, add auth/error handling)

### Dependencies
```bash
npm install -D orval
npm install axios @tanstack/react-query zod
```

### References
- [Orval Documentation](https://orval.dev/)
- [TanStack Query Documentation](https://tanstack.com/query/latest)
- [Quarkus OpenAPI Extension](https://quarkus.io/guides/openapi-swaggerui)

---

## Research Area 5: Tailwind CSS v4 + Shadcn/ui with Astro

### Decision
Use **Tailwind CSS v4 with @tailwindcss/vite** and **Shadcn/ui components** (copy, not package)

### Rationale
- Tailwind v4 is the latest (better performance, native Vite plugin)
- Shadcn/ui provides production-ready, accessible components
- Copy approach (vs npm package) allows full customization
- Excellent TypeScript support and theme management

### Alternatives Considered
- Tailwind v3 - Rejected: v4 offers better DX and performance
- UI libraries (MUI, Chakra) - Rejected: heavier, less customizable
- CSS modules - Rejected: more boilerplate, less utility-focused

### Configuration
See: `/workspaces/CONFIGURATION_TEMPLATES.md` → Sections 11, 12, 16, 17

**Setup Steps**:
1. Install Tailwind CSS v4: `npm install -D tailwindcss @tailwindcss/vite`
2. Configure `tailwind.config.mjs` with content paths
3. Install Shadcn/ui CLI: `npm install -D shadcn-ui`
4. Initialize Shadcn/ui: `npx shadcn-ui@latest init`
5. Add components as needed: `npx shadcn-ui@latest add button`

**File Structure**:
- `tailwind.config.mjs` - Tailwind configuration
- `components.json` - Shadcn/ui configuration (path aliases)
- `src/styles/globals.css` - Tailwind directives + custom styles
- `src/components/ui/` - Copied Shadcn/ui components
- `src/lib/utils.ts` - `cn()` utility for class merging

**Integration with Astro**:
- Tailwind works in `.astro` files (server-rendered)
- For Preact islands, wrap Shadcn components in `.tsx` files
- Use client:load directive: `<Button client:load />`

### Dependencies
```bash
npm install -D tailwindcss @tailwindcss/vite shadcn-ui
npm install clsx tailwind-merge class-variance-authority lucide-react
```

### References
- [Tailwind CSS v4 Beta](https://tailwindcss.com/docs/v4-beta)
- [Shadcn/ui Astro Integration](https://ui.shadcn.com/docs/installation/astro)
- [Astro Tailwind Integration](https://docs.astro.build/en/guides/integrations-guide/tailwind/)

---

## Research Area 6: State Management Patterns for Islands Architecture

### Decision
Use **Nano Stores** for island-to-island communication, with multiple patterns for different use cases

### Rationale
- Nano Stores is tiny (< 1KB), framework-agnostic
- Excellent Astro integration (official recommendation)
- Supports all 4 required state patterns:
  1. Server sessions → Quarkus session management
  2. Client storage → localStorage/sessionStorage utilities
  3. Island communication → Nano Stores atoms
  4. Cross-page state → Nano Stores persistent atoms + Astro middleware

### Alternatives Considered
- Zustand - Rejected: React-specific, larger bundle
- Jotai - Rejected: React-specific
- Custom event bus - Rejected: more code, less battle-tested

### Configuration & Patterns

#### 1. Server Sessions (Quarkus)
```java
// Quarkus automatically manages HttpSession
@GET
@Path("/user")
public User getCurrentUser(@Context HttpSession session) {
    return (User) session.getAttribute("user");
}
```

#### 2. Client Storage (localStorage helpers)
```typescript
// src/lib/storage.ts
export const preferences = {
  get: () => JSON.parse(localStorage.getItem('taskmanager:prefs') || '{}'),
  set: (prefs) => localStorage.setItem('taskmanager:prefs', JSON.stringify(prefs)),
};
```

#### 3. Island Communication (Nano Stores)
```typescript
// src/lib/state.ts
import { atom } from 'nanostores';
export const taskFilter = atom<{ category?: string; priority?: string }>({});

// In TaskFilter.tsx
import { useStore } from '@nanostores/preact';
import { taskFilter } from '@/lib/state';
const filter = useStore(taskFilter);

// In TaskList.tsx
import { useStore } from '@nanostores/preact';
import { taskFilter } from '@/lib/state';
const filter = useStore(taskFilter);
// Both islands react to same state
```

#### 4. Cross-Page State (Persistent Nano Stores)
```typescript
// src/lib/state.ts
import { persistentAtom } from '@nanostores/persistent';
export const userTheme = persistentAtom<'light' | 'dark'>('theme', 'light');

// Persists to localStorage automatically
// Available across all Astro pages
```

### Dependencies
```bash
npm install nanostores @nanostores/preact @nanostores/persistent
```

### References
- [Nano Stores Documentation](https://github.com/nanostores/nanostores)
- [Nano Stores with Astro](https://docs.astro.build/en/recipes/sharing-state-islands/)
- [Persistent Nano Stores](https://github.com/nanostores/persistent)

---

## Research Area 7: Performance Measurement in Astro

### Decision
Use **Web Vitals API + custom performance marks** for measuring Islands Architecture performance

### Rationale
- Web Vitals API is browser-native (no library needed)
- Performance marks allow custom hydration timing
- Easy to collect and display on Performance page
- Aligns with industry-standard metrics (FCP, TTI, LCP)

### Implementation Approach

#### 1. Web Vitals Collection
```typescript
// src/lib/performance.ts
export function measurePageLoad() {
  const observer = new PerformanceObserver((list) => {
    for (const entry of list.getEntries()) {
      if (entry.entryType === 'paint' && entry.name === 'first-contentful-paint') {
        console.log(`FCP: ${entry.startTime}ms`);
      }
    }
  });
  observer.observe({ entryTypes: ['paint', 'navigation'] });
}
```

#### 2. Island Hydration Timing
```typescript
// In each island component
import { useEffect } from 'preact/hooks';

export function TaskForm() {
  useEffect(() => {
    performance.mark('TaskForm-hydrated');
    performance.measure('TaskForm-hydration', 'TaskForm-start', 'TaskForm-hydrated');
  }, []);
}
```

#### 3. Bundle Size Tracking
```bash
# Use Astro's build output
npm run build
# Check dist/ folder sizes
# Display in Performance page
```

### Metrics to Track
- First Contentful Paint (FCP): <1.5s target
- Time to Interactive (TTI): <3s target
- JavaScript bundle size: <100KB target
- Per-island hydration time: <200ms target
- Largest Contentful Paint (LCP): <2.5s target

### Display on Performance Page
Create dedicated `/performance` Astro page showing:
- Current metrics vs targets
- Historical trends (if persisted)
- Comparison chart: Islands vs traditional SPA

### References
- [Web Vitals API](https://web.dev/vitals/)
- [Performance API](https://developer.mozilla.org/en-US/docs/Web/API/Performance)
- [Astro Performance](https://docs.astro.build/en/concepts/why-astro/#server-first)

---

## Research Area 8: Testing Strategy for Islands

### Decision
Use **Vitest** for frontend unit tests, **REST Assured** for backend contract tests

### Rationale
- Vitest is Vite-native (fast, modern, excellent DX)
- Works well with Astro and Preact components
- REST Assured is JUnit 5 compatible, Quarkus recommended
- Balanced approach: test critical paths, not every line

### Configuration

#### Frontend Testing (Vitest)
```typescript
// vitest.config.ts
import { defineConfig } from 'vitest/config';
import preact from '@preact/preset-vite';

export default defineConfig({
  plugins: [preact()],
  test: {
    environment: 'jsdom',
    globals: true,
  },
});
```

**Test Structure**:
- Unit tests for utility functions (`src/lib/utils.test.ts`)
- Component tests for complex islands (`src/islands/TaskForm.test.tsx`)
- Integration tests for state management (`src/lib/state.test.ts`)

#### Backend Testing (JUnit 5 + REST Assured)
```java
@QuarkusTest
class TaskResourceTest {
    @Test
    void testCreateTask() {
        given()
            .contentType(ContentType.JSON)
            .body(new TaskCreateDTO("Buy milk", "Personal", "HIGH"))
        .when()
            .post("/api/tasks")
        .then()
            .statusCode(201)
            .body("title", equalTo("Buy milk"));
    }
}
```

**Test Coverage Targets** (from spec):
- Backend services: >70%
- Interactive islands: >50%

### Dependencies
```bash
# Frontend
npm install -D vitest jsdom @testing-library/preact @testing-library/user-event

# Backend (already in Quarkus)
# REST Assured, JUnit 5 included
```

### References
- [Vitest Documentation](https://vitest.dev/)
- [Testing Library - Preact](https://testing-library.com/docs/preact-testing-library/intro/)
- [Quarkus Testing Guide](https://quarkus.io/guides/getting-started-testing)

---

## Implementation Readiness

All research is complete. All technical decisions have been made and documented.

**Ready for Phase 1**: Data model design, API contract creation, and quickstart guide generation.

**Next Steps**:
1. Create `data-model.md` with JPA entities and TypeScript types
2. Create `contracts/api.yaml` with REST endpoint specifications
3. Create `quickstart.md` with <10 minute onboarding guide
4. Update agent context with new technologies

**Reference Materials** (in `/workspaces/`):
- Complete configuration files ready to copy
- Step-by-step implementation checklist
- Production-ready best practices
- Comprehensive troubleshooting guides
