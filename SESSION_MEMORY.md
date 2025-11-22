# Session Memory - Task Manager Application

**Last Updated**: 2025-11-22
**Project**: Quarkus + Astro Task Manager (Islands Architecture)
**Status**: Phase 3 Complete, Ready for Phase 4

---

## Current State Summary

### Completed Phases

✅ **Phase 1: Initial Setup (T001-T005)** - Project structure created
✅ **Phase 2: Production-Ready Bootstrap Template (T006-T125)** - All quality tooling configured
✅ **Phase 3: Backend Core Infrastructure (T126-T145)** - Foundation complete

**Total Progress**: 145/694 tasks completed (21%)

### What's Next

🎯 **Phase 4: User Story 1 - View and Navigate Tasks (T146-T262)**
- Starting point: T146 - Create contract tests for TaskResource
- This is the first user-facing feature implementation
- Will involve backend (entities, DTOs, services, REST endpoints) and frontend (Astro pages, Preact islands)

---

## Important Context

### Project Architecture

**Backend**: Quarkus 3.29.4 (Java 21)
- Port: 7171
- Database: H2 in-memory (development)
- API Prefix: `/api`
- OpenAPI schema exported to: `src/main/webui/api/openapi.json`

**Frontend**: Astro 5.x + Preact (Islands Architecture)
- Port: 3000 (dev server)
- Build output: `dist/`
- Path aliases configured: `@/*` → `./src/*`

**Integration**: Quinoa (Quarkus extension for SPA integration)
- Quarkus serves both API and frontend in production
- Dev mode: Frontend on 3000, Backend on 7171, CORS configured

### Technology Stack

**Frontend Quality Tools**:
- ESLint 9 (flat config format) - Airbnb-style rules manually configured
- Prettier (standalone formatter with Astro + Tailwind plugins)
- Husky + lint-staged (pre-commit hooks)
- TypeScript 5.x strict mode
- Vitest (testing framework)

**Frontend Libraries**:
- Tailwind CSS v4 (@tailwindcss/vite plugin)
- Shadcn/ui (copy-paste components with cn() utility)
- TanStack Query v5 (data fetching/caching)
- Orval (OpenAPI → TypeScript client generator)
- Nano Stores (state management for island communication)
- Axios (HTTP client)

**Backend Quality Tools**:
- Checkstyle (pre-configured in parent POM)
- PMD (pre-configured in parent POM)
- JUnit 5 for testing

---

## Critical Constraints & Decisions

### DO NOT Commit Automatically
⚠️ **IMPORTANT**: User will review and commit manually. Never auto-commit changes.

### Code Quality Standards
- Zero tolerance for quality violations
- ESLint `no-explicit-any` enforced (no `any` types allowed)
- Pre-commit hooks block bad commits
- All code must pass format/lint checks

### ESLint 9 Compatibility Issue
- Could NOT use `eslint-config-airbnb-base` or `eslint-config-airbnb-typescript`
- Incompatible with ESLint 9 (they require ESLint 7-8)
- **Solution**: Manually configured Airbnb-style rules in `eslint.config.js`

### Husky in Monorepo
- Husky MUST be at repository root: `/workspaces/.husky/`
- Pre-commit script changes directory to `quarkus-astro-app/src/main/webui` before running lint-staged
- **Why**: Git root is `/workspaces/`, not the webui subdirectory

---

## File Structure

```
/workspaces/
├── .husky/                          # Pre-commit hooks (repo root)
│   └── pre-commit
├── .github/workflows/
│   └── ci.yml                       # CI/CD pipeline (4 jobs)
├── .vscode/
│   ├── settings.json                # Editor config
│   └── extensions.json              # Recommended extensions
├── specs/
│   └── 001-task-manager-app/
│       ├── tasks.md                 # Master task list (694 tasks)
│       ├── spec.md                  # Feature specification
│       ├── plan.md                  # Architecture plan
│       └── data-model.md            # Database schema
└── quarkus-astro-app/
    ├── pom.xml                      # Maven config (Quarkus parent)
    ├── src/main/
    │   ├── resources/
    │   │   └── application.properties  # Quarkus configuration
    │   ├── java/org/acme/taskmanager/
    │   │   ├── model/
    │   │   │   └── Priority.java    # HIGH, MEDIUM, LOW enum
    │   │   ├── dto/
    │   │   │   └── ErrorDTO.java    # Standardized error response
    │   │   ├── exception/
    │   │   │   ├── ResourceNotFoundException.java
    │   │   │   ├── ValidationException.java
    │   │   │   ├── UnauthorizedException.java
    │   │   │   └── GlobalExceptionMapper.java  # JAX-RS exception handler
    │   │   ├── session/
    │   │   │   └── SessionUtils.java           # getCurrentUserId(), setCurrentUser()
    │   │   ├── service/             # (empty - ready for US1)
    │   │   └── resource/            # (empty - ready for US1)
    │   └── webui/                   # Frontend directory
    │       ├── package.json
    │       ├── tsconfig.json        # TypeScript strict mode + path aliases
    │       ├── eslint.config.js     # Flat config with Airbnb-style rules
    │       ├── .prettierrc.json
    │       ├── .lintstagedrc.json
    │       ├── tailwind.config.mjs
    │       ├── astro.config.mjs
    │       ├── vitest.config.ts
    │       ├── orval.config.ts      # API client generation
    │       ├── components.json      # Shadcn/ui config
    │       ├── api/                 # OpenAPI schema location (empty - will be generated)
    │       └── src/
    │           ├── styles/
    │           │   └── globals.css  # Tailwind directives + theme variables
    │           ├── lib/
    │           │   └── utils.ts     # cn() utility for Tailwind
    │           ├── api/
    │           │   └── mutator.ts   # Axios instance with interceptors
    │           ├── components/ui/   # (empty - ready for Shadcn components)
    │           ├── islands/         # (empty - ready for Preact islands)
    │           └── pages/           # (empty - ready for Astro pages)
    └── src/test/java/org/acme/taskmanager/
        └── (empty - ready for tests)
```

---

## Phase 3 Implementation Details

### Backend Configuration (`application.properties`)

**Key Settings**:
```properties
quarkus.http.port=7171
quarkus.datasource.db-kind=h2
quarkus.datasource.jdbc.url=jdbc:h2:mem:taskmanager;DB_CLOSE_DELAY=-1
quarkus.hibernate-orm.database.generation=drop-and-create  # DEV ONLY
quarkus.quinoa.ui-dir=src/main/webui
quarkus.quinoa.build-dir=dist
quarkus.quinoa.ignored-path-prefixes=/api
quarkus.smallrye-openapi.store-schema-directory=src/main/webui/api
quarkus.http.cors=true
quarkus.http.cors.origins=http://localhost:3000,http://localhost:7171
```

### Session Management Pattern

**File**: `src/main/java/org/acme/taskmanager/session/SessionUtils.java`

**Key Methods**:
- `getCurrentUserId(RoutingContext context)` - Throws `UnauthorizedException` if not authenticated
- `setCurrentUser(RoutingContext context, String userId)` - Sets user in session after login
- `clearCurrentUser(RoutingContext context)` - Logout
- `isAuthenticated(RoutingContext context)` - Check auth status

**Session Attribute**: `userId` (String)

**Note**: Uses Vert.x `RoutingContext` (not `HttpSession`) - this is Quarkus's reactive approach

### Exception Handling Pattern

**File**: `src/main/java/org/acme/taskmanager/exception/GlobalExceptionMapper.java`

**Mappings**:
- `ResourceNotFoundException` → HTTP 404 + `ErrorDTO{message, code: "NOT_FOUND"}`
- `ValidationException` → HTTP 400 + `ErrorDTO{message, field?, code: "VALIDATION_ERROR"}`
- `UnauthorizedException` → HTTP 401 + `ErrorDTO{message, code: "UNAUTHORIZED"}`
- `Exception` (generic) → HTTP 500 + `ErrorDTO{message: "An unexpected error occurred...", code: "INTERNAL_SERVER_ERROR"}`

**Security**: Generic exceptions hide implementation details from clients, log full stack traces server-side

### Error DTO Pattern

**File**: `src/main/java/org/acme/taskmanager/dto/ErrorDTO.java`

```java
public record ErrorDTO(String message, String field, String code) {
  public static ErrorDTO of(String message, String code) { ... }
  public static ErrorDTO fieldError(String message, String field, String code) { ... }
}
```

**JSON Example**:
```json
{
  "message": "Task not found with ID: abc123",
  "field": null,
  "code": "NOT_FOUND"
}
```

---

## Frontend Configuration Highlights

### NPM Scripts

```json
{
  "dev": "astro dev --port 3000",
  "build": "astro build",
  "lint": "eslint . --ext .js,.ts,.tsx,.astro",
  "lint:fix": "eslint . --ext .js,.ts,.tsx,.astro --fix",
  "format": "prettier --write .",
  "format:check": "prettier --check .",
  "test": "vitest",
  "test:coverage": "vitest --coverage",
  "generate:api": "orval --config orval.config.ts"
}
```

### Pre-commit Hooks

**Location**: `/workspaces/.husky/pre-commit`

**What it does**:
1. Changes directory to `quarkus-astro-app/src/main/webui`
2. Runs `npx lint-staged`
3. Lint-staged runs:
   - Prettier on all staged files
   - ESLint on `.ts`, `.tsx`, `.astro`, `.js` files

**Result**: Commits are blocked if linting fails, auto-fixed if possible

### Path Aliases

Configured in **3 places** (must stay in sync):

1. `tsconfig.json`:
```json
{
  "compilerOptions": {
    "baseUrl": ".",
    "paths": {
      "@/*": ["./src/*"],
      "@/components/*": ["./src/components/*"],
      "@/islands/*": ["./src/islands/*"],
      "@/lib/*": ["./src/lib/*"],
      "@/styles/*": ["./src/styles/*"],
      "@/api/*": ["./src/api/*"]
    }
  }
}
```

2. `astro.config.mjs`:
```javascript
export default defineConfig({
  vite: {
    resolve: {
      alias: {
        '@': '/src',
        '@/components': '/src/components',
        '@/islands': '/src/islands',
        '@/lib': '/src/lib',
        '@/styles': '/src/styles',
        '@/api': '/src/api',
      },
    },
  },
});
```

3. ESLint uses `tsconfig.json` via `project: './tsconfig.json'`

### API Client Generation Workflow

**Code-first approach**:

1. Write Quarkus REST endpoint with JAX-RS annotations
2. Run Quarkus dev mode → OpenAPI schema auto-generated at `src/main/webui/api/openapi.json`
3. Run `npm run generate:api` → Orval generates:
   - TypeScript types in `src/lib/api/model/`
   - TanStack Query hooks in `src/lib/api/endpoints/`
4. Use generated hooks in Preact components:
   ```typescript
   import { useGetTasks } from '@/lib/api/endpoints/tasks';

   const { data, isLoading } = useGetTasks();
   ```

**Custom Axios Instance**: `src/api/mutator.ts`
- Base URL: `http://localhost:7171/api`
- `withCredentials: true` (sends cookies for session management)
- 401 interceptor → redirects to `/login`

---

## Next Steps - Phase 4 Roadmap

### Contract Tests (T146-T151)
Create `TaskResourceTest.java` with tests for:
- `GET /api/tasks` (no filters)
- `GET /api/tasks?status=active`
- `GET /api/tasks?category={id}`
- `GET /api/tasks?priority=HIGH`
- `GET /api/tasks?page=0&size=20`

### Backend Models (T152-T163)
Create JPA entities:
- `Category` (id, name, colorCode, isDefault, userId, createdAt)
- `Task` (id, title, description, category, priority, completed, completedAt, userId, createdAt, updatedAt)

**Key JPA Annotations**:
- `@Entity`, `@Table` with indexes
- Unique constraint: `@UniqueConstraint(columnNames = {"user_id", "name"})` on Category
- `@CreationTimestamp`, `@UpdateTimestamp` on timestamps
- `@Enumerated(EnumType.STRING)` for Priority enum

### Backend DTOs (T164-T169)
- `CategoryResponseDTO` with `from(Category)` static method
- `TaskResponseDTO` with `from(Task)` static method

### Backend Services (T170+)
- `CategoryRepository` extends `PanacheRepositoryBase<Category, UUID>`
- `TaskRepository` extends `PanacheRepositoryBase<Task, UUID>`
- Service layer with business logic

### REST Endpoints
- `CategoryResource` - CRUD for categories
- `TaskResource` - CRUD + filtering for tasks

### Frontend Components
- Astro pages for task list, task detail
- Preact islands for interactive components (filters, forms, buttons)
- Shadcn/ui components (Button, Card, Badge, Select, etc.)

---

## Common Commands

### Start Development

```bash
# Terminal 1: Start Quarkus backend (from /workspaces/quarkus-astro-app)
./mvnw quarkus:dev
# → Backend on http://localhost:7171
# → Swagger UI on http://localhost:7171/swagger-ui

# Terminal 2: Start Astro frontend (from /workspaces/quarkus-astro-app/src/main/webui)
npm run dev
# → Frontend on http://localhost:3000
```

### Run Quality Checks

```bash
# Frontend (from webui directory)
npm run format        # Auto-fix formatting
npm run lint:fix      # Auto-fix linting
npm run test          # Run tests
npm run generate:api  # Generate API client from OpenAPI schema

# Backend (from quarkus-astro-app directory)
./mvnw verify         # Run tests + Checkstyle + PMD
```

### Install Dependencies

```bash
# Frontend (from webui directory)
npm install

# Backend (from quarkus-astro-app directory)
./mvnw clean install
```

---

## Reference Documentation

### Task List
**Location**: `/workspaces/specs/001-task-manager-app/tasks.md`
- 694 total tasks
- 145 completed (Phases 1-3)
- 549 remaining (Phases 4-8)

### Architecture Documentation
- **spec.md**: User stories and acceptance criteria
- **plan.md**: Technical architecture decisions
- **data-model.md**: Database schema and relationships

### Official Docs
- Quarkus: https://quarkus.io/guides/
- Astro: https://docs.astro.build/
- Preact: https://preactjs.com/
- TanStack Query: https://tanstack.com/query/latest
- Shadcn/ui: https://ui.shadcn.com/
- Orval: https://orval.dev/

---

## Important Notes

### Git Status
- Branch: `001-task-manager-app`
- Last commits:
  - `2f05d4e` - all dependencies and quality tooling (Phase 2 ✅)
  - `c2a7456` - feat: generate comprehensive task list for task manager (694 tasks)
  - `883060c` - Prepared plan
  - `a765e33` - docs: complete implementation plan for task manager app
  - `8f3f673` - docs: enhance spec with production-ready tooling requirements

- **Phase 2 Status**: ✅ Committed (all frontend quality tooling configured)
- **Phase 3 Status**: ⚠️ NOT committed yet (backend infrastructure)
- User will review Phase 3 changes and commit manually

**Modified Files**:
- `quarkus-astro-app/src/main/resources/application.properties` (updated with comprehensive config)
- `specs/001-task-manager-app/tasks.md` (marked T126-T145 as completed)

**New Untracked Files (Phase 3)**:
- `quarkus-astro-app/src/main/java/org/acme/taskmanager/model/Priority.java`
- `quarkus-astro-app/src/main/java/org/acme/taskmanager/dto/ErrorDTO.java`
- `quarkus-astro-app/src/main/java/org/acme/taskmanager/exception/ResourceNotFoundException.java`
- `quarkus-astro-app/src/main/java/org/acme/taskmanager/exception/ValidationException.java`
- `quarkus-astro-app/src/main/java/org/acme/taskmanager/exception/UnauthorizedException.java`
- `quarkus-astro-app/src/main/java/org/acme/taskmanager/exception/GlobalExceptionMapper.java`
- `quarkus-astro-app/src/main/java/org/acme/taskmanager/session/SessionUtils.java`
- `SESSION_MEMORY.md` (this file)

### VS Code Extensions Installed
Check `.vscode/extensions.json` for recommended extensions:
- Astro (astro-build.astro-vscode)
- Prettier (esbenp.prettier-vscode)
- ESLint (dbaeumer.vscode-eslint)
- Tailwind CSS IntelliSense (bradlc.vscode-tailwindcss)
- Java Extension Pack (redhat.java, vscjava.*)
- Quarkus (redhat.vscode-quarkus)

### Known Issues / Quirks

1. **ESLint Airbnb Config**: Cannot use official packages due to ESLint 9 incompatibility
2. **Husky Location**: MUST be at `/workspaces/.husky/`, not in webui subdirectory
3. **SessionUtils**: Uses `RoutingContext` (Vert.x), not `HttpSession` (Jakarta)
4. **H2 Database**: In-memory only, data lost on restart (intended for dev)
5. **Tailwind v4**: Beta version, uses `@tailwindcss/vite` plugin (different from v3)

---

## Session State Snapshot

### What Was Being Worked On
Just completed all Phase 3 foundational backend infrastructure tasks.

### Last Action
Updated `tasks.md` to mark T126-T145 as completed with ✅ on Phase 3 header.

### Current Todo List State
All Phase 3 tasks marked as completed. Todo list cleared.

### Ready to Resume
Ready to start **Phase 4: User Story 1 - View and Navigate Tasks** beginning with T146 (contract tests).

---

## Quick Start Checklist for New Computer

- [ ] Clone repository
- [ ] Install Java 21 JDK
- [ ] Install Node.js 20.17.0+
- [ ] Install Maven (or use `./mvnw`)
- [ ] Open project in VS Code
- [ ] Install recommended VS Code extensions
- [ ] From `/workspaces/quarkus-astro-app/src/main/webui`: Run `npm install`
- [ ] From `/workspaces/quarkus-astro-app`: Run `./mvnw clean install`
- [ ] Start backend: `./mvnw quarkus:dev` (from quarkus-astro-app)
- [ ] Start frontend: `npm run dev` (from webui directory)
- [ ] Verify:
  - [ ] Backend: http://localhost:7171
  - [ ] Swagger UI: http://localhost:7171/swagger-ui
  - [ ] Frontend: http://localhost:3000
- [ ] Read `/workspaces/specs/001-task-manager-app/tasks.md` for next steps

---

**End of Session Memory**
