# Tasks: Task Manager Sample Application

**Input**: Design documents from `/workspaces/specs/001-task-manager-app/`
**Prerequisites**: plan.md ✅, spec.md ✅, research.md ✅, data-model.md ✅, contracts/api.yaml ✅

**Tests**: Contract tests are included for all API endpoints per constitution (Principle VI: Test-Conscious Development)

**Organization**: Tasks are grouped by user story (P0-P5) to enable independent implementation and testing of each story

## Format: `[ID] [P?] [Story] Description`

- **[P]**: Can run in parallel (different files, no dependencies)
- **[Story]**: Which user story this task belongs to (e.g., US0, US1, US2...)
- Include exact file paths in descriptions

## Path Conventions

This is a web application with the following structure:

- **Backend**: `quarkus-astro-app/src/main/java/org/acme/taskmanager/`
- **Frontend**: `quarkus-astro-app/src/main/webui/src/`
- **Tests**: `quarkus-astro-app/src/test/java/`
- **Config**: Various locations per plan.md

---

## Phase 1: Setup (Project Initialization) ✅

**Purpose**: Initialize project structure and verify existing configuration

- [x] T001 Verify Quarkus project structure matches plan.md at quarkus-astro-app/
- [x] T002 Verify parent POM exists with Checkstyle/PMD configuration at parent/pom.xml
- [x] T003 Verify build-tools project exists with quality configs at build-tools/src/main/resources/product/
- [x] T004 Create package structure for backend: model/, resource/, service/, dto/, session/ in quarkus-astro-app/src/main/java/org/acme/taskmanager/
- [x] T005 Create test package structure: contract/, integration/, unit/ in quarkus-astro-app/src/test/java/org/acme/taskmanager/

---

## Phase 2: Foundational - User Story 0 (Priority: P0) - Production-Ready Bootstrap Template ✅

**Goal**: Establish complete quality tooling infrastructure before any feature work

**Independent Test**: Clone repo, run `./mvnw clean package` (passes with 0 violations), run `npm run build` (passes with 0 violations), attempt commit with violations (blocked by pre-commit hooks)

**⚠️ CRITICAL**: This entire phase MUST be complete before ANY other user story work begins. This is the foundation.

### Frontend Dependencies Installation

- [x] T006 [US0] Navigate to quarkus-astro-app/src/main/webui/ and verify package.json exists
- [x] T007 [US0] Install core dependencies: `npm install astro@latest @astrojs/preact preact`
- [x] T008 [US0] Install TypeScript and tooling: `npm install -D typescript@latest @types/node @preact/preset-vite`
- [x] T009 [US0] Install Tailwind CSS v4: `npm install -D tailwindcss@next @tailwindcss/vite`
- [x] T010 [US0] Install Shadcn/ui CLI and dependencies: `npm install -D shadcn-ui clsx tailwind-merge class-variance-authority lucide-preact`
- [x] T011 [US0] Install TanStack Query and Axios: `npm install @tanstack/react-query@latest axios zod`
- [x] T012 [US0] Install Orval for API generation: `npm install -D orval`
- [x] T013 [US0] Install Nano Stores for state management: `npm install nanostores @nanostores/preact @nanostores/persistent`
- [x] T014 [US0] Install ESLint core: `npm install -D eslint@latest @eslint/js typescript-eslint`
- [x] T015 [US0] Install ESLint plugins: `npm install -D eslint-plugin-astro eslint-plugin-preact eslint-config-airbnb-base eslint-config-airbnb-typescript @typescript-eslint/parser @typescript-eslint/eslint-plugin`
- [x] T016 [US0] Install ESLint-Prettier integration: `npm install -D eslint-config-prettier`
- [x] T017 [US0] Install Prettier and plugins: `npm install -D prettier prettier-plugin-astro prettier-plugin-tailwindcss`
- [x] T018 [US0] Install Husky and lint-staged: `npm install -D husky lint-staged`
- [x] T019 [US0] Install Vitest for testing: `npm install -D vitest jsdom @testing-library/preact @testing-library/user-event`
- [x] T020 [US0] Run `npm install` to ensure all dependencies resolve correctly

### ESLint Configuration

- [x] T021 [US0] Create eslint.config.js in quarkus-astro-app/src/main/webui/ with flat config format
- [x] T022 [US0] Add TypeScript ESLint parser and plugin configuration to eslint.config.js
- [x] T023 [US0] Add Airbnb TypeScript style guide rules to eslint.config.js
- [x] T024 [US0] Add eslint-plugin-astro configuration for .astro files to eslint.config.js
- [x] T025 [US0] Add eslint-plugin-preact configuration for .tsx files to eslint.config.js
- [x] T026 [US0] Add eslint-config-prettier as last config to disable conflicting rules in eslint.config.js
- [x] T027 [US0] Add detailed comments explaining each section of eslint.config.js (production-ready documentation)
- [x] T028 [US0] Create .eslintignore in quarkus-astro-app/src/main/webui/ with: dist/, node_modules/, .astro/, api/
- [x] T029 [US0] Add npm script "lint" to package.json: "eslint . --ext .js,.ts,.tsx,.astro"
- [x] T030 [US0] Add npm script "lint:fix" to package.json: "eslint . --ext .js,.ts,.tsx,.astro --fix"
- [x] T031 [US0] Test ESLint: create temporary file with violation, run `npm run lint`, verify it fails with clear error

### Prettier Configuration

- [x] T032 [US0] Create .prettierrc.json in quarkus-astro-app/src/main/webui/ with: semi, singleQuote, tabWidth, trailingComma, printWidth, plugins
- [x] T033 [US0] Add prettier-plugin-astro to plugins array in .prettierrc.json
- [x] T034 [US0] Add prettier-plugin-tailwindcss to plugins array in .prettierrc.json
- [x] T035 [US0] Create .prettierignore in quarkus-astro-app/src/main/webui/ with: dist/, node_modules/, .astro/, api/, pnpm-lock.yaml, package-lock.json
- [x] T036 [US0] Add npm script "format" to package.json: "prettier --write ."
- [x] T037 [US0] Add npm script "format:check" to package.json: "prettier --check ."
- [x] T038 [US0] Test Prettier: create temporary file with bad formatting, run `npm run format`, verify it formats correctly

### Pre-commit Hooks Configuration

- [x] T039 [US0] Initialize Husky: run `npx husky init` in quarkus-astro-app/src/main/webui/
- [x] T040 [US0] Create .husky directory if not exists in quarkus-astro-app/src/main/webui/
- [x] T041 [US0] Create .husky/pre-commit file with: `#!/usr/bin/env sh\n. "$(dirname -- "$0")/_/husky.sh"\nnpx lint-staged`
- [x] T042 [US0] Make .husky/pre-commit executable: `chmod +x .husky/pre-commit`
- [x] T043 [US0] Create .lintstagedrc.json in quarkus-astro-app/src/main/webui/ with file-type specific commands
- [x] T044 [US0] Configure .lintstagedrc.json to run Prettier first, then ESLint with --fix for _.ts, _.tsx files
- [x] T045 [US0] Configure .lintstagedrc.json to run Prettier for \*.astro files
- [x] T046 [US0] Add npm script "prepare" to package.json: "husky install" (for team setup)
- [x] T047 [US0] Test pre-commit hooks: create file with violation, attempt commit, verify it's blocked or auto-fixed

### TypeScript Configuration

- [x] T048 [US0] Create tsconfig.json in quarkus-astro-app/src/main/webui/ with strict mode enabled
- [x] T049 [US0] Add path aliases to tsconfig.json: "@/_" maps to "./src/_"
- [x] T050 [US0] Add compiler options: target ES2020, module ESNext, jsx preserve, lib ES2020/DOM
- [x] T051 [US0] Add include/exclude patterns: include src/, exclude node_modules/, dist/
- [x] T052 [US0] Configure baseUrl and paths for absolute imports in tsconfig.json
- [x] T053 [US0] Test TypeScript: create .ts file with type error, run `npx tsc --noEmit`, verify it fails

### Tailwind CSS Configuration

- [x] T054 [US0] Create tailwind.config.mjs in quarkus-astro-app/src/main/webui/ with content paths
- [x] T055 [US0] Add content globs to tailwind.config.mjs: './src/\*_/_.{astro,html,js,jsx,md,mdx,tsx}'
- [x] T056 [US0] Configure theme extension in tailwind.config.mjs for custom colors/spacing if needed
- [x] T057 [US0] Add darkMode: 'class' to tailwind.config.mjs for theme toggle support
- [x] T058 [US0] Create src/styles/globals.css in quarkus-astro-app/src/main/webui/src/styles/
- [x] T059 [US0] Add Tailwind directives to globals.css: @tailwind base; @tailwind components; @tailwind utilities;
- [x] T060 [US0] Add custom CSS for Shadcn/ui theme variables to globals.css (CSS variables for colors)

### Shadcn/ui Configuration

- [x] T061 [US0] Create components.json in quarkus-astro-app/src/main/webui/ for Shadcn/ui config
- [x] T062 [US0] Configure components.json with: style: "default", rsc: false, tsx: true, aliases for @/components, @/lib
- [x] T063 [US0] Create src/lib/utils.ts in quarkus-astro-app/src/main/webui/src/lib/ with cn() utility function
- [x] T064 [US0] Implement cn() function using clsx and tailwind-merge: `export function cn(...inputs: ClassValue[]) { return twMerge(clsx(inputs)) }`
- [x] T065 [US0] Create src/components/ui/ directory in quarkus-astro-app/src/main/webui/src/components/ui/
- [x] T066 [US0] Install first Shadcn component as test: `npx shadcn-ui@latest add button`
- [x] T067 [US0] Verify button component created at src/components/ui/button.tsx

### Orval Configuration

- [x] T068 [US0] Create orval.config.ts in quarkus-astro-app/src/main/webui/ for API client generation
- [x] T069 [US0] Configure orval.config.ts input: './api/openapi.json' (generated by Quarkus)
- [x] T070 [US0] Configure orval.config.ts output: './src/lib/api/' with TanStack Query v5 client
- [x] T071 [US0] Configure orval.config.ts to use Axios with custom mutator at './src/api/mutator.ts'
- [x] T072 [US0] Create src/api/ directory in quarkus-astro-app/src/main/webui/src/api/
- [x] T073 [US0] Create src/api/mutator.ts with Axios instance configured for baseURL, auth headers, error handling
- [x] T074 [US0] Add TypeScript types for error responses in src/api/mutator.ts
- [x] T075 [US0] Add npm script "generate:api" to package.json: "orval --config orval.config.ts"
- [x] T076 [US0] Create api/ directory in quarkus-astro-app/src/main/webui/api/ for OpenAPI schema (will be generated by Quarkus)

### Astro Configuration

- [x] T077 [US0] Create astro.config.mjs in quarkus-astro-app/src/main/webui/ with Preact integration
- [x] T078 [US0] Add @astrojs/preact integration to astro.config.mjs
- [x] T079 [US0] Add Tailwind integration (@tailwindcss/vite) to astro.config.mjs
- [x] T080 [US0] Configure output: 'static' in astro.config.mjs (static site generation)
- [x] T081 [US0] Configure build.outDir: './dist' in astro.config.mjs
- [x] T082 [US0] Add Vite configuration for path aliases matching tsconfig.json in astro.config.mjs
- [x] T083 [US0] Configure server.port: 3000 for Astro dev server in astro.config.mjs

### Vitest Configuration

- [x] T084 [US0] Create vitest.config.ts in quarkus-astro-app/src/main/webui/ for testing framework
- [x] T085 [US0] Configure Vitest with @preact/preset-vite plugin in vitest.config.ts
- [x] T086 [US0] Configure test environment: 'jsdom' in vitest.config.ts
- [x] T087 [US0] Configure globals: true for global test functions in vitest.config.ts
- [x] T088 [US0] Add npm script "test" to package.json: "vitest"
- [x] T089 [US0] Add npm script "test:coverage" to package.json: "vitest --coverage"

### Documentation & README

- [x] T090 [US0] Create comprehensive README.md at repository root (quarkus-astro-app/README.md)
- [x] T091 [US0] Add Prerequisites section to README.md: Java 21, Maven 3.9+, Node 20.17+
- [x] T092 [US0] Add Quick Start section to README.md with 3-command setup from quickstart.md
- [x] T093 [US0] Add Development Workflow section to README.md: how to develop backend/frontend
- [x] T094 [US0] Add Quality Tools section to README.md: Checkstyle, PMD, ESLint, Prettier, pre-commit hooks
- [x] T095 [US0] Add Testing section to README.md: how to run backend tests, frontend tests
- [x] T096 [US0] Add API Documentation section to README.md: link to Swagger UI at http://localhost:7171/swagger-ui
- [x] T097 [US0] Add Troubleshooting section to README.md with common issues from quickstart.md
- [x] T098 [US0] Add Architecture section to README.md: Islands Architecture explanation, state management patterns
- [x] T099 [US0] Add Contributing section to README.md: how to add features, run quality checks, commit guidelines

### CI/CD Configuration Examples

- [x] T100 [US0] Create .github/workflows/ directory at repository root
- [x] T101 [US0] Create .github/workflows/ci.yml with GitHub Actions workflow for quality checks
- [x] T102 [US0] Add Maven build job to ci.yml: runs `./mvnw clean verify` on Java 21
- [x] T103 [US0] Add frontend build job to ci.yml: runs `npm run build` and `npm run lint` in webui/
- [x] T104 [US0] Add test job to ci.yml: runs `./mvnw test` and `npm test` if tests exist
- [x] T105 [US0] Configure ci.yml to fail on any Checkstyle/PMD/ESLint violation
- [x] T106 [US0] Add comments to ci.yml explaining each step (production-ready documentation)
- [x] T107 [US0] Create .gitlab-ci.yml as alternative CI/CD example for GitLab users

### VS Code Configuration (Optional but Recommended)

- [x] T108 [US0] Create .vscode/settings.json at repository root with recommended settings
- [x] T109 [US0] Add ESLint integration to .vscode/settings.json: "eslint.validate" for .astro, .ts, .tsx
- [x] T110 [US0] Add Prettier integration to .vscode/settings.json: "editor.defaultFormatter": "esbenp.prettier-vscode"
- [x] T111 [US0] Add format on save to .vscode/settings.json: "editor.formatOnSave": true
- [x] T112 [US0] Create .vscode/extensions.json with recommended extensions: Astro, Prettier, ESLint, Tailwind CSS IntelliSense
- [x] T113 [US0] Create .vscode/tasks.json with tasks for "Run Quarkus Dev", "Run Frontend Build", "Run Tests"

### Final Verification & Testing

- [x] T114 [US0] Run full Maven build: `./mvnw clean package` from quarkus-astro-app/ - verify 0 Checkstyle violations
- [x] T115 [US0] Run full Maven build: `./mvnw clean package` from quarkus-astro-app/ - verify 0 PMD violations
- [x] T116 [US0] Run frontend build: `cd src/main/webui && npm run build` - verify 0 ESLint errors
- [x] T117 [US0] Run frontend lint: `cd src/main/webui && npm run lint` - verify passes
- [x] T118 [US0] Run frontend format check: `cd src/main/webui && npm run format:check` - verify passes
- [x] T119 [US0] Test pre-commit hooks: create file with intentional formatting issue, stage it, attempt commit, verify Prettier auto-fixes
- [x] T120 [US0] Test pre-commit hooks: create file with intentional ESLint violation, stage it, attempt commit, verify commit is blocked
- [x] T121 [US0] Start Quarkus dev mode: `quarkus dev` - verify starts without errors on http://localhost:7171
- [x] T122 [US0] Verify Swagger UI loads: http://localhost:7171/swagger-ui (may be empty, just verify page loads)
- [x] T123 [US0] Verify Astro hot reload works: modify a file in src/main/webui/src/, verify browser updates
- [x] T124 [US0] Measure setup time: verify a new developer can complete setup in <10 minutes per SC-016
- [x] T125 [US0] Update CLAUDE.md with new technologies: ESLint flat config, Prettier plugins, Husky, Orval, Tailwind v4, Shadcn/ui, Nano Stores, Vitest

**Checkpoint US0**: At this point, the project has a production-ready bootstrap template with all quality tools enforced. Zero violations across 100% of codebase. Pre-commit hooks prevent bad commits. README documentation complete. SC-013, SC-014, SC-015, SC-016, SC-017 are satisfied.

---

## Phase 3: Foundational (Backend Core Infrastructure) ✅

**Purpose**: Core backend infrastructure that MUST be complete before user story implementations

**⚠️ CRITICAL**: These tasks must complete before US1-US5 work begins

### Backend Configuration

- [x] T126 Update quarkus-astro-app/src/main/resources/application.properties with HTTP port 7171
- [x] T127 Configure H2 database connection in application.properties
- [x] T128 Configure Hibernate DDL generation: `quarkus.hibernate-orm.database.generation=drop-and-create` for development
- [x] T129 Configure Quinoa settings in application.properties: UI directory, build directory, API path prefix /api
- [x] T130 Configure OpenAPI schema export location in application.properties: quarkus.smallrye-openapi.store-schema-directory=src/main/webui/api
- [x] T131 Configure CORS settings in application.properties for local development

### Priority Enum (Shared)

- [x] T132 [P] Create Priority enum in quarkus-astro-app/src/main/java/org/acme/taskmanager/model/Priority.java with values: HIGH, MEDIUM, LOW

### Session Management Infrastructure

- [x] T133 [P] Create SessionUtils class in quarkus-astro-app/src/main/java/org/acme/taskmanager/session/SessionUtils.java
- [x] T134 [P] Implement getCurrentUserId(HttpSession) method in SessionUtils to extract user ID from session
- [x] T135 [P] Implement setCurrentUser(HttpSession, String userId) method in SessionUtils
- [x] T136 [P] Add validation: throw UnauthorizedException if session user is null in SessionUtils

### Base DTOs (Shared)

- [x] T137 [P] Create ErrorDTO record in quarkus-astro-app/src/main/java/org/acme/taskmanager/dto/ErrorDTO.java with: message, field, code

### Exception Handling Infrastructure

- [x] T138 [P] Create custom exception ResourceNotFoundException in quarkus-astro-app/src/main/java/org/acme/taskmanager/exception/ResourceNotFoundException.java
- [x] T139 [P] Create custom exception ValidationException in quarkus-astro-app/src/main/java/org/acme/taskmanager/exception/ValidationException.java
- [x] T140 [P] Create custom exception UnauthorizedException in quarkus-astro-app/src/main/java/org/acme/taskmanager/exception/UnauthorizedException.java
- [x] T141 [P] Create GlobalExceptionMapper in quarkus-astro-app/src/main/java/org/acme/taskmanager/exception/GlobalExceptionMapper.java
- [x] T142 [P] Implement ExceptionMapper for ResourceNotFoundException → 404 with ErrorDTO response
- [x] T143 [P] Implement ExceptionMapper for ValidationException → 400 with ErrorDTO response
- [x] T144 [P] Implement ExceptionMapper for UnauthorizedException → 401 with ErrorDTO response
- [x] T145 [P] Implement ExceptionMapper for generic Exception → 500 with ErrorDTO response

**Checkpoint Foundation**: Backend infrastructure ready - API endpoints can now be implemented

---

## Phase 4: User Story 1 - View and Navigate Tasks (Priority: P1) 🎯 MVP

**Goal**: Users can view their task list, filter by status/category/priority, and navigate. Client-side preferences persist (theme, filters).

**Independent Test**: Load homepage, verify tasks display. Filter by "Completed", verify only completed tasks show. Refresh page, verify filter persists. Change theme, navigate to another page, come back, verify theme persists.

### Contract Tests for US1

- [x] T146 [P] [US1] Create TaskResourceTest in quarkus-astro-app/src/test/java/org/acme/taskmanager/contract/TaskResourceTest.java
- [x] T147 [P] [US1] Write contract test for GET /api/tasks (no filters) - expect 200, empty array initially
- [x] T148 [P] [US1] Write contract test for GET /api/tasks?status=active - expect 200, filtered results
- [x] T149 [P] [US1] Write contract test for GET /api/tasks?category={id} - expect 200, filtered results
- [x] T150 [P] [US1] Write contract test for GET /api/tasks?priority=HIGH - expect 200, filtered results
- [x] T151 [P] [US1] Write contract test for GET /api/tasks?page=0&size=20 - expect 200, paginated results

### Backend Models for US1

- [x] T152 [P] [US1] Create Category entity in quarkus-astro-app/src/main/java/org/acme/taskmanager/model/Category.java per data-model.md
- [x] T153 [P] [US1] Add JPA annotations to Category: @Entity, @Table with indexes, unique constraint on (userId, name)
- [x] T154 [P] [US1] Add fields to Category: id (UUID), name, colorCode, isDefault, userId, createdAt, tasks (OneToMany)
- [x] T155 [P] [US1] Add validation annotations to Category: @NotNull, @Size constraints
- [x] T156 [P] [US1] Implement equals() and hashCode() for Category based on id
- [x] T157 [P] [US1] Add canDelete() method to Category: return !isDefault
- [x] T158 [P] [US1] Create Task entity in quarkus-astro-app/src/main/java/org/acme/taskmanager/model/Task.java per data-model.md
- [x] T159 [P] [US1] Add JPA annotations to Task: @Entity, @Table with indexes on userId, categoryId, completed, createdAt
- [x] T160 [P] [US1] Add fields to Task: id (UUID), title, description, category (ManyToOne), priority, completed, completedAt, userId, createdAt, updatedAt
- [x] T161 [P] [US1] Add validation annotations to Task: @NotNull, @Size constraints on title/description
- [x] T162 [P] [US1] Add @CreationTimestamp and @UpdateTimestamp annotations to Task
- [x] T163 [P] [US1] Implement equals() and hashCode() for Task based on id

### Backend DTOs for US1

- [x] T164 [P] [US1] Create CategoryResponseDTO record in quarkus-astro-app/src/main/java/org/acme/taskmanager/dto/CategoryResponseDTO.java
- [x] T165 [P] [US1] Add fields to CategoryResponseDTO: id, name, colorCode, isDefault, createdAt, taskCount
- [x] T166 [P] [US1] Add static from(Category) method to CategoryResponseDTO
- [x] T167 [P] [US1] Create TaskResponseDTO record in quarkus-astro-app/src/main/java/org/acme/taskmanager/dto/TaskResponseDTO.java
- [x] T168 [P] [US1] Add fields to TaskResponseDTO: id, title, description, category (CategoryResponseDTO), priority, completed, completedAt, createdAt, updatedAt
- [x] T169 [P] [US1] Add static from(Task) method to TaskResponseDTO

### Backend Services for US1

- [x] T170 [US1] Create CategoryRepository interface in quarkus-astro-app/src/main/java/org/acme/taskmanager/repository/CategoryRepository.java extends PanacheRepositoryBase<Category, UUID>
- [x] T171 [US1] Add query methods to CategoryRepository: findByUserId(String userId), countByUserId(String userId)
- [x] T172 [US1] Create CategoryService in quarkus-astro-app/src/main/java/org/acme/taskmanager/service/CategoryService.java
- [x] T173 [US1] Implement ensureDefaultCategories(String userId) method in CategoryService to seed Work, Personal, Shopping categories
- [x] T174 [US1] Implement getAllCategoriesByUser(String userId) method in CategoryService
- [x] T175 [US1] Create TaskRepository interface in quarkus-astro-app/src/main/java/org/acme/taskmanager/repository/TaskRepository.java extends PanacheRepositoryBase<Task, UUID>
- [x] T176 [US1] Add query methods to TaskRepository: findByUserId, findByUserIdAndCompleted, findByUserIdAndCategoryId, findByUserIdAndPriority
- [x] T177 [US1] Add pagination support to TaskRepository query methods using PanacheQuery
- [x] T178 [US1] Create TaskService in quarkus-astro-app/src/main/java/org/acme/taskmanager/service/TaskService.java
- [x] T179 [US1] Implement getAllTasks(String userId, String categoryId, Priority priority, Boolean completed, int page, int size) in TaskService
- [x] T180 [US1] Add proper null checks and filtering logic in TaskService.getAllTasks()

### Backend API Endpoints for US1

- [x] T181 [US1] Create TaskResource in quarkus-astro-app/src/main/java/org/acme/taskmanager/resource/TaskResource.java with @Path("/api/tasks")
- [x] T182 [US1] Inject TaskService and HttpSession into TaskResource
- [x] T183 [US1] Implement GET /api/tasks endpoint in TaskResource with query params: category, priority, status, page, size
- [x] T184 [US1] Add OpenAPI annotations to GET /api/tasks: @Operation, @Parameter, @APIResponse descriptions matching api.yaml
- [x] T185 [US1] Validate query parameters in GET /api/tasks (page >= 0, size between 1-100)
- [x] T186 [US1] Extract userId from session and pass to TaskService
- [x] T187 [US1] Return List<TaskResponseDTO> from GET /api/tasks
- [x] T188 [US1] Create CategoryResource in quarkus-astro-app/src/main/java/org/acme/taskmanager/resource/CategoryResource.java with @Path("/api/categories")
- [x] T189 [US1] Inject CategoryService and HttpSession into CategoryResource
- [x] T190 [US1] Implement GET /api/categories endpoint in CategoryResource
- [x] T191 [US1] Add OpenAPI annotations to GET /api/categories matching api.yaml
- [x] T192 [US1] Return List<CategoryResponseDTO> from GET /api/categories
- [x] T193 [US1] Run `quarkus dev` and verify OpenAPI schema generates to src/main/webui/api/openapi.json

### Frontend API Client Generation for US1

- [x] T194 [US1] Verify OpenAPI schema exists at quarkus-astro-app/src/main/webui/api/openapi.json
- [x] T195 [US1] Run `npm run generate:api` in src/main/webui to generate TypeScript client
- [x] T196 [US1] Verify generated client exists at src/lib/api/ with TanStack Query hooks
- [x] T197 [US1] Verify useGetTasks hook generated with proper TypeScript types
- [x] T198 [US1] Verify useGetCategories hook generated with proper TypeScript types

### Frontend Client-Side Storage for US1

- [x] T199 [P] [US1] Create UserPreferences TypeScript interface in quarkus-astro-app/src/main/webui/src/types/preferences.ts
- [x] T200 [P] [US1] Add fields to UserPreferences: theme ('light' | 'dark'), lastViewedPage, activeFilters, tasksPerPage
- [x] T201 [P] [US1] Export defaultPreferences constant in preferences.ts
- [x] T202 [P] [US1] Create Zod schema for UserPreferences validation in preferences.ts
- [x] T203 [P] [US1] Create storage utility in quarkus-astro-app/src/main/webui/src/lib/storage.ts
- [x] T204 [P] [US1] Implement preferences.get(userId) method in storage.ts with try-catch for localStorage errors
- [x] T205 [P] [US1] Implement preferences.set(userId, prefs) method in storage.ts with try-catch
- [x] T206 [P] [US1] Implement preferences.clear(userId) method in storage.ts

### Frontend State Management for US1

- [x] T207 [P] [US1] Create Nano Stores atoms in quarkus-astro-app/src/main/webui/src/lib/state.ts
- [x] T208 [P] [US1] Create taskFilter atom with type: { category?: string, priority?: string, status?: string }
- [x] T209 [P] [US1] Create userTheme persistent atom with persistentAtom('theme', 'light')
- [x] T210 [P] [US1] Export atoms from state.ts for use in islands

### Frontend Layout & Navigation for US1

- [x] T211 [P] [US1] Create Layout.astro component in quarkus-astro-app/src/main/webui/src/components/Layout.astro
- [x] T212 [P] [US1] Import globals.css in Layout.astro: `import '../styles/globals.css'`
- [x] T213 [P] [US1] Add HTML boilerplate to Layout.astro with Tailwind theme classes
- [x] T214 [P] [US1] Add <slot /> for page content in Layout.astro
- [x] T215 [P] [US1] Create Navigation.astro component in quarkus-astro-app/src/main/webui/src/components/Navigation.astro
- [x] T216 [P] [US1] Add navigation links in Navigation.astro: Tasks, Categories, Dashboard, Performance
- [x] T217 [P] [US1] Style Navigation.astro with Tailwind classes for responsive menu
- [x] T218 [P] [US1] Include Navigation in Layout.astro

### Frontend Task List Island for US1

- [x] T219 [US1] Create TaskList.tsx island in quarkus-astro-app/src/main/webui/src/islands/TaskList.tsx
- [x] T220 [US1] Import useGetTasks hook from generated API client in TaskList.tsx
- [x] T221 [US1] Import useStore from @nanostores/preact for taskFilter state in TaskList.tsx
- [x] T222 [US1] Use taskFilter atom to get current filter state in TaskList.tsx
- [x] T223 [US1] Call useGetTasks with filter parameters from taskFilter atom in TaskList.tsx
- [x] T224 [US1] Handle loading state in TaskList.tsx: show skeleton loaders
- [x] T225 [US1] Handle error state in TaskList.tsx: display user-friendly error message
- [x] T226 [US1] Render task list in TaskList.tsx: map over tasks, display title, category badge, priority badge, completion checkbox (read-only for US1)
- [x] T227 [US1] Add Tailwind styling to TaskList.tsx: grid layout, responsive, visual indicators for priority (colors)
- [x] T228 [US1] Handle empty state in TaskList.tsx: "Create your first task" message per edge cases

### Frontend Filter Island for US1

- [x] T229 [US1] Create TaskFilter.tsx island in quarkus-astro-app/src/main/webui/src/islands/TaskFilter.tsx
- [x] T230 [US1] Import useGetCategories hook from generated API client in TaskFilter.tsx
- [x] T231 [US1] Import taskFilter atom and useStore in TaskFilter.tsx
- [x] T232 [US1] Create dropdowns for: Category, Priority, Status (Active/Completed) in TaskFilter.tsx
- [x] T233 [US1] Populate category dropdown from useGetCategories() data in TaskFilter.tsx
- [x] T234 [US1] Populate priority dropdown with HIGH/MEDIUM/LOW options in TaskFilter.tsx
- [x] T235 [US1] Handle filter changes: update taskFilter atom on dropdown change in TaskFilter.tsx
- [x] T236 [US1] Update URL query params when filters change in TaskFilter.tsx (cross-page state persistence)
- [x] T237 [US1] Load filters from URL query params on mount in TaskFilter.tsx
- [x] T238 [US1] Save filters to localStorage using preferences.set() in TaskFilter.tsx

### Frontend Theme Toggle Island for US1

- [x] T239 [US1] Install Shadcn Button component if not already: `npx shadcn-ui@latest add button`
- [x] T240 [US1] Create ThemeToggle.tsx island in quarkus-astro-app/src/main/webui/src/islands/ThemeToggle.tsx
- [x] T241 [US1] Import userTheme persistent atom in ThemeToggle.tsx
- [x] T242 [US1] Create toggle button using Shadcn Button component in ThemeToggle.tsx
- [x] T243 [US1] Handle theme toggle: update userTheme atom and apply 'dark' class to document.documentElement in ThemeToggle.tsx
- [x] T244 [US1] Add moon/sun icon from lucide-preact based on current theme in ThemeToggle.tsx
- [x] T245 [US1] Apply theme on initial load from userTheme atom in ThemeToggle.tsx

### Frontend Homepage (index.astro) for US1

- [x] T246 [US1] Create index.astro page in quarkus-astro-app/src/main/webui/src/pages/index.astro
- [x] T247 [US1] Import Layout component in index.astro
- [x] T248 [US1] Import TaskList island with client:load directive in index.astro
- [x] T249 [US1] Import TaskFilter island with client:load directive in index.astro
- [x] T250 [US1] Import ThemeToggle island with client:load directive in index.astro
- [x] T251 [US1] Add page title "Task Manager" in index.astro
- [x] T252 [US1] Layout components: header with ThemeToggle, main content area with TaskFilter and TaskList
- [x] T253 [US1] Add responsive grid layout using Tailwind in index.astro

### Testing US1

- [x] T254 [US1] Manually test: start `quarkus dev`, navigate to http://localhost:7171, verify homepage loads
- [x] T255 [US1] Manually test: verify empty state shows "Create your first task" message
- [ ] T256 [US1] Manually test: use Swagger UI to POST a test task via /api/tasks (will be implemented in US2, or use curl)
- [ ] T257 [US1] Manually test: verify task appears in TaskList after refresh
- [ ] T258 [US1] Manually test: select "Completed" filter, verify only completed tasks show (or empty if none)
- [ ] T259 [US1] Manually test: select category filter, verify filtering works
- [ ] T260 [US1] Manually test: toggle theme, verify UI switches between light/dark mode
- [ ] T261 [US1] Manually test: refresh page, verify theme persists
- [ ] T262 [US1] Manually test: change filters, refresh page, verify filters persist
- [ ] T263 [US1] Run contract tests: `./mvnw test -Dtest=TaskResourceTest`
- [ ] T264 [US1] Verify test coverage for TaskService >70% per constitution (run `./mvnw verify` with Jacoco)

**Checkpoint US1**: At this point, US1 is fully functional and independently testable. Users can view tasks, filter them, and preferences persist. SC-001 (partial), SC-002, SC-006, SC-011 (partial) are satisfied for viewing functionality.

---

## Phase 5: User Story 2 - Create and Edit Tasks (Priority: P2)

**Goal**: Users can create new tasks via a form, edit existing tasks inline, with optimistic UI updates and server persistence.

**Independent Test**: Click "New Task", fill form with title "Buy milk", category "Personal", priority "High", submit. Verify task appears in list. Click task to edit, change title to "Buy milk and bread", save. Verify update persists.

### Contract Tests for US2

- [x] T265 [P] [US2] Write contract test for POST /api/tasks - expect 201, task created with correct fields
- [x] T266 [P] [US2] Write contract test for POST /api/tasks with missing title - expect 400, validation error
- [x] T267 [P] [US2] Write contract test for POST /api/tasks with title >200 chars - expect 400, validation error
- [x] T268 [P] [US2] Write contract test for GET /api/tasks/{id} - expect 200, single task details
- [x] T269 [P] [US2] Write contract test for GET /api/tasks/{id} with invalid UUID - expect 404, not found
- [x] T270 [P] [US2] Write contract test for PUT /api/tasks/{id} - expect 200, task updated
- [x] T271 [P] [US2] Write contract test for DELETE /api/tasks/{id} - expect 204, task deleted

### Backend DTOs for US2

- [x] T272 [P] [US2] Create TaskCreateDTO record in quarkus-astro-app/src/main/java/org/acme/taskmanager/dto/TaskCreateDTO.java
- [x] T273 [P] [US2] Add fields to TaskCreateDTO: title (@NotNull, @Size 1-200), description (@Size max 2000), categoryId (@NotNull), priority (@NotNull)
- [x] T274 [P] [US2] Create TaskUpdateDTO record in quarkus-astro-app/src/main/java/org/acme/taskmanager/dto/TaskUpdateDTO.java
- [x] T275 [P] [US2] Add fields to TaskUpdateDTO: title (@Size 1-200), description (@Size max 2000), categoryId, priority (all optional for PATCH semantics)

### Backend Services for US2

- [x] T276 [US2] Implement createTask(String userId, TaskCreateDTO dto) in TaskService
- [x] T277 [US2] Validate category exists and belongs to user in TaskService.createTask()
- [x] T278 [US2] Create Task entity from DTO, set userId, persist to repository in TaskService.createTask()
- [x] T279 [US2] Return TaskResponseDTO from TaskService.createTask()
- [x] T280 [US2] Implement getTaskById(String userId, UUID taskId) in TaskService
- [x] T281 [US2] Throw ResourceNotFoundException if task not found or doesn't belong to user in TaskService.getTaskById()
- [x] T282 [US2] Implement updateTask(String userId, UUID taskId, TaskUpdateDTO dto) in TaskService
- [x] T283 [US2] Validate task exists and belongs to user in TaskService.updateTask()
- [x] T284 [US2] Update only non-null fields from DTO in TaskService.updateTask()
- [x] T285 [US2] Validate category if categoryId provided in TaskService.updateTask()
- [x] T286 [US2] Return updated TaskResponseDTO from TaskService.updateTask()
- [x] T287 [US2] Implement deleteTask(String userId, UUID taskId) in TaskService
- [x] T288 [US2] Validate task exists and belongs to user before deletion in TaskService.deleteTask()

### Backend API Endpoints for US2

- [x] T289 [US2] Implement POST /api/tasks endpoint in TaskResource
- [x] T290 [US2] Validate request body using @Valid annotation on TaskCreateDTO parameter
- [x] T291 [US2] Extract userId from session in POST /api/tasks
- [x] T292 [US2] Call TaskService.createTask() and return 201 Created with TaskResponseDTO
- [x] T293 [US2] Add OpenAPI annotations to POST /api/tasks matching api.yaml
- [x] T294 [US2] Implement GET /api/tasks/{id} endpoint in TaskResource
- [x] T295 [US2] Extract userId from session in GET /api/tasks/{id}
- [x] T296 [US2] Call TaskService.getTaskById() and return 200 OK with TaskResponseDTO
- [x] T297 [US2] Add OpenAPI annotations to GET /api/tasks/{id} matching api.yaml
- [x] T298 [US2] Implement PUT /api/tasks/{id} endpoint in TaskResource
- [x] T299 [US2] Validate request body using @Valid annotation on TaskUpdateDTO parameter
- [x] T300 [US2] Extract userId from session in PUT /api/tasks/{id}
- [x] T301 [US2] Call TaskService.updateTask() and return 200 OK with TaskResponseDTO
- [x] T302 [US2] Add OpenAPI annotations to PUT /api/tasks/{id} matching api.yaml
- [x] T303 [US2] Implement DELETE /api/tasks/{id} endpoint in TaskResource
- [x] T304 [US2] Extract userId from session in DELETE /api/tasks/{id}
- [x] T305 [US2] Call TaskService.deleteTask() and return 204 No Content
- [x] T306 [US2] Add OpenAPI annotations to DELETE /api/tasks/{id} matching api.yaml
- [x] T307 [US2] Regenerate OpenAPI schema by restarting `quarkus dev`
- [x] T308 [US2] Run `npm run generate:api` to regenerate TypeScript client with new endpoints

### Frontend Task Form Island for US2

- [x] T309 [US2] Create TaskForm.tsx island in quarkus-astro-app/src/main/webui/src/islands/TaskForm.tsx
- [x] T310 [US2] Import useCreateTask, useUpdateTask hooks from generated API client in TaskForm.tsx
- [x] T311 [US2] Import useGetCategories hook for category dropdown in TaskForm.tsx
- [x] T312 [US2] Install Shadcn form components: `npx shadcn-ui@latest add input textarea select button label`
- [x] T313 [US2] Create form state using Preact useState: title, description, categoryId, priority in TaskForm.tsx
- [x] T314 [US2] Create mode prop to support "create" or "edit" mode in TaskForm.tsx
- [x] T315 [US2] If edit mode, accept initialTask prop and pre-fill form fields in TaskForm.tsx
- [x] T316 [US2] Render title input (Shadcn Input) with required validation in TaskForm.tsx
- [x] T317 [US2] Render description textarea (Shadcn Textarea) with max length 2000 in TaskForm.tsx
- [x] T318 [US2] Render category dropdown (Shadcn Select) populated from useGetCategories() in TaskForm.tsx
- [x] T319 [US2] Render priority dropdown (Shadcn Select) with HIGH/MEDIUM/LOW options in TaskForm.tsx
- [x] T320 [US2] Handle form submission in TaskForm.tsx: validate title required, call createTask or updateTask mutation
- [x] T321 [US2] Implement optimistic update in TaskForm.tsx: update UI before server confirms
- [x] T322 [US2] Handle mutation success in TaskForm.tsx: clear form if create mode, close form if edit mode, invalidate tasks query cache
- [x] T323 [US2] Handle mutation error in TaskForm.tsx: show user-friendly error message "Unable to save task. Please try again."
- [x] T324 [US2] Add loading state to submit button in TaskForm.tsx (disable during mutation)
- [x] T325 [US2] Add Cancel button in TaskForm.tsx to reset form or close edit mode
- [x] T326 [US2] Style TaskForm.tsx with Tailwind classes for responsive layout

### Frontend Task List Integration for US2

- [x] T327 [US2] Update TaskList.tsx to support inline editing: add onClick handler to each task
- [x] T328 [US2] Add state to track selected task ID for editing in TaskList.tsx
- [x] T329 [US2] When task clicked, render TaskForm.tsx inline with mode="edit" and initialTask data
- [x] T330 [US2] After update successful, clear selected task ID to exit edit mode in TaskList.tsx
- [x] T331 [US2] Add delete button to each task in TaskList.tsx
- [x] T332 [US2] Implement delete confirmation dialog (Shadcn AlertDialog) in TaskList.tsx
- [x] T333 [US2] Call useDeleteTask mutation on confirm in TaskList.tsx
- [x] T334 [US2] Handle optimistic delete: remove from UI immediately in TaskList.tsx

### Frontend New Task Button for US2

- [x] T335 [US2] Update index.astro to add "New Task" button in header
- [x] T336 [US2] Create NewTaskModal.tsx island or use state in index.astro to show/hide TaskForm
- [x] T337 [US2] When "New Task" clicked, show TaskForm.tsx in modal or expanded section with mode="create"
- [x] T338 [US2] After task created, close modal/form and refresh task list

### Testing US2

- [ ] T339 [US2] Manually test: click "New Task", fill form, submit, verify task appears immediately (optimistic update)
- [ ] T340 [US2] Manually test: verify task persists after page refresh
- [ ] T341 [US2] Manually test: submit form without title, verify validation error shows
- [ ] T342 [US2] Manually test: submit form with title >200 chars, verify validation error
- [ ] T343 [US2] Manually test: click existing task, verify edit form opens with pre-filled data
- [ ] T344 [US2] Manually test: edit task title, save, verify update shows immediately and persists
- [ ] T345 [US2] Manually test: click delete, confirm, verify task removed immediately and persists
- [ ] T346 [US2] Manually test: simulate network error (disconnect), try to create task, verify error message shows
- [ ] T347 [US2] Manually test: measure task creation time <3 seconds per SC-001
- [ ] T348 [US2] Run contract tests for POST/PUT/DELETE endpoints
- [ ] T349 [US2] Verify optimistic updates work: create task, observe UI updates before server responds

**Checkpoint US2**: At this point, US2 is fully functional. Users can create, edit, delete tasks with optimistic UI updates. SC-001, SC-007, SC-013 are satisfied.

---

## Phase 6: User Story 3 - Organize with Categories and Priorities (Priority: P3)

**Goal**: Users can create custom categories, assign priorities, filter tasks by category/priority with visual indicators (colors, badges). Cross-page state persistence for filters.

**Independent Test**: Click "Manage Categories", create category "Health", save. Create task assigned to "Health" category. Filter by "Health", verify only tasks in that category show. Navigate to another page, return, verify "Health" filter still applied.

### Contract Tests for US3

- [x] T350 [P] [US3] Write contract test for POST /api/categories - expect 201, category created
- [x] T351 [P] [US3] Write contract test for POST /api/categories with duplicate name - expect 409, conflict error
- [x] T352 [P] [US3] Write contract test for GET /api/categories/{id} - expect 200, single category
- [x] T353 [P] [US3] Write contract test for PUT /api/categories/{id} - expect 200, category updated
- [x] T354 [P] [US3] Write contract test for DELETE /api/categories/{id} where isDefault=false - expect 204, deleted
- [x] T355 [P] [US3] Write contract test for DELETE /api/categories/{id} where isDefault=true - expect 400, cannot delete default

### Backend DTOs for US3

- [x] T356 [P] [US3] Create CategoryCreateDTO record in quarkus-astro-app/src/main/java/org/acme/taskmanager/dto/CategoryCreateDTO.java
- [x] T357 [P] [US3] Add fields to CategoryCreateDTO: name (@NotNull, @Size 1-50), colorCode (@Size 7, hex pattern validation)
- [x] T358 [P] [US3] Create CategoryUpdateDTO record in quarkus-astro-app/src/main/java/org/acme/taskmanager/dto/CategoryUpdateDTO.java
- [x] T359 [P] [US3] Add fields to CategoryUpdateDTO: name (@Size 1-50), colorCode (@Size 7) - both optional

### Backend Services for US3

- [x] T360 [US3] Implement createCategory(String userId, CategoryCreateDTO dto) in CategoryService
- [x] T361 [US3] Validate category name unique per user in CategoryService.createCategory()
- [x] T362 [US3] Throw ValidationException if duplicate name in CategoryService.createCategory()
- [x] T363 [US3] Create Category entity from DTO, set userId, isDefault=false in CategoryService.createCategory()
- [x] T364 [US3] Return CategoryResponseDTO from CategoryService.createCategory()
- [x] T365 [US3] Implement getCategoryById(String userId, UUID categoryId) in CategoryService
- [x] T366 [US3] Throw ResourceNotFoundException if category not found or doesn't belong to user
- [x] T367 [US3] Implement updateCategory(String userId, UUID categoryId, CategoryUpdateDTO dto) in CategoryService
- [x] T368 [US3] Validate category exists and belongs to user in CategoryService.updateCategory()
- [x] T369 [US3] Validate name uniqueness if name is being updated in CategoryService.updateCategory()
- [x] T370 [US3] Update only non-null fields from DTO in CategoryService.updateCategory()
- [x] T371 [US3] Implement deleteCategory(String userId, UUID categoryId) in CategoryService
- [x] T372 [US3] Validate category exists, belongs to user, and canDelete() returns true in CategoryService.deleteCategory()
- [x] T373 [US3] Throw ValidationException if attempting to delete default category

### Backend API Endpoints for US3

- [x] T374 [US3] Implement POST /api/categories endpoint in CategoryResource
- [x] T375 [US3] Validate request body using @Valid annotation on CategoryCreateDTO
- [x] T376 [US3] Extract userId from session in POST /api/categories
- [x] T377 [US3] Call CategoryService.createCategory() and return 201 Created with CategoryResponseDTO
- [x] T378 [US3] Add OpenAPI annotations to POST /api/categories matching api.yaml
- [x] T379 [US3] Implement GET /api/categories/{id} endpoint in CategoryResource
- [x] T380 [US3] Extract userId from session in GET /api/categories/{id}
- [x] T381 [US3] Call CategoryService.getCategoryById() and return 200 OK with CategoryResponseDTO
- [x] T382 [US3] Add OpenAPI annotations to GET /api/categories/{id} matching api.yaml
- [x] T383 [US3] Implement PUT /api/categories/{id} endpoint in CategoryResource
- [x] T384 [US3] Validate request body using @Valid annotation on CategoryUpdateDTO
- [x] T385 [US3] Extract userId from session in PUT /api/categories/{id}
- [x] T386 [US3] Call CategoryService.updateCategory() and return 200 OK with CategoryResponseDTO
- [x] T387 [US3] Add OpenAPI annotations to PUT /api/categories/{id} matching api.yaml
- [x] T388 [US3] Implement DELETE /api/categories/{id} endpoint in CategoryResource
- [x] T389 [US3] Extract userId from session in DELETE /api/categories/{id}
- [x] T390 [US3] Call CategoryService.deleteCategory() and return 204 No Content
- [x] T391 [US3] Add OpenAPI annotations to DELETE /api/categories/{id} matching api.yaml
- [x] T392 [US3] Regenerate OpenAPI schema by restarting `quarkus dev`
- [x] T393 [US3] Run `npm run generate:api` to regenerate TypeScript client

### Frontend Category Manager Island for US3

- [x] T394 [US3] Create CategoryManager.tsx island in quarkus-astro-app/src/main/webui/src/islands/CategoryManager.tsx
- [x] T395 [US3] Import useGetCategories, useCreateCategory, useUpdateCategory, useDeleteCategory hooks in CategoryManager.tsx
- [x] T396 [US3] Render list of categories with name, color badge, task count, edit/delete buttons in CategoryManager.tsx
- [x] T397 [US3] Mark default categories (Work, Personal, Shopping) as non-deletable in UI
- [x] T398 [US3] Add "New Category" button to open category form in CategoryManager.tsx
- [x] T399 [US3] Create inline form for category: name input, color picker in CategoryManager.tsx
- [x] T400 [US3] Implement color picker using Shadcn Input with type="color" or custom component in CategoryManager.tsx
- [x] T401 [US3] Handle category creation: call useCreateCategory mutation in CategoryManager.tsx
- [x] T402 [US3] Handle category update: support inline editing of name and color in CategoryManager.tsx
- [x] T403 [US3] Handle category deletion: show confirmation dialog, call useDeleteCategory in CategoryManager.tsx
- [x] T404 [US3] Display error if trying to delete default category: "Cannot delete default categories"
- [x] T405 [US3] Display error if duplicate category name: "Category name already exists"
- [x] T406 [US3] Invalidate categories query cache after create/update/delete operations

### Frontend Categories Page for US3

- [x] T407 [US3] Create categories.astro page in quarkus-astro-app/src/main/webui/src/pages/categories.astro
- [x] T408 [US3] Import Layout component in categories.astro
- [x] T409 [US3] Import CategoryManager island with client:load directive in categories.astro
- [x] T410 [US3] Add page title "Manage Categories" and description in categories.astro
- [x] T411 [US3] Style categories.astro page with Tailwind responsive layout

### Frontend Visual Indicators for US3

- [x] T412 [US3] Update TaskList.tsx to display category color badge next to each task
- [x] T413 [US3] Update TaskList.tsx to display priority badge with color coding: red (HIGH), yellow (MEDIUM), green (LOW)
- [x] T414 [US3] Add visual sorting in TaskList.tsx: HIGH priority tasks appear first
- [x] T415 [US3] Update TaskFilter.tsx to show category name with color indicator in dropdown
- [x] T416 [US3] Style category and priority badges using Shadcn Badge component or Tailwind

### Frontend Cross-Page State Persistence for US3

- [x] T417 [US3] Update TaskFilter.tsx to save activeFilters to localStorage on filter change
- [x] T418 [US3] Update TaskFilter.tsx to load activeFilters from localStorage on mount
- [x] T419 [US3] Update TaskFilter.tsx to sync URL query params with localStorage for bookmarkability
- [x] T420 [US3] Test cross-page persistence: set category filter on index.astro, navigate to categories.astro, return to index.astro, verify filter persists

### Testing US3

- [x] T421 [US3] Manually test: click "Manage Categories", verify default categories show (Work, Personal, Shopping)
- [x] T422 [US3] Manually test: create new category "Health & Fitness" with green color, verify it appears in list
- [x] T423 [US3] Manually test: create task assigned to "Health & Fitness", verify category color badge displays
- [x] T424 [US3] Manually test: filter tasks by "Health & Fitness", verify only matching tasks show
- [x] T425 [US3] Manually test: set task priority to HIGH, verify red badge appears
- [x] T426 [US3] Manually test: filter by HIGH priority, verify only HIGH priority tasks show
- [x] T427 [US3] Manually test: try to delete default category "Work", verify error message shows
- [x] T428 [US3] Manually test: delete custom category "Health & Fitness" (after removing tasks), verify deletion works
- [x] T429 [US3] Manually test: create category with duplicate name, verify 409 error message
- [x] T430 [US3] Manually test: set category filter, navigate away, return, verify filter persists (cross-page state)
- [x] T431 [US3] Run contract tests for category CRUD endpoints
- [x] T432 [US3] Verify island communication: change filter in TaskFilter.tsx, verify TaskList.tsx updates immediately

**Checkpoint US3**: At this point, US3 is fully functional. Users can manage categories, see visual indicators, and filters persist cross-page. SC-011 (cross-page state pattern) partially satisfied.

---

## Phase 7: User Story 4 - Track Task Completion and History (Priority: P4)

**Goal**: Users can mark tasks complete with checkbox, see completion timestamp, view statistics (today, week, total), undo completion within 5 seconds.

**Independent Test**: Mark 3 tasks complete, navigate to dashboard, verify "3 tasks completed today" shows. Mark task complete, click undo within 5 seconds, verify task returns to active status.

### Contract Tests for US4

- [ ] T433 [P] [US4] Write contract test for PATCH /api/tasks/{id}/complete - expect 200, task marked complete with timestamp
- [ ] T434 [P] [US4] Write contract test for PATCH /api/tasks/{id}/complete on already completed task - expect 200, task marked incomplete (toggle)
- [ ] T435 [P] [US4] Write contract test for GET /api/stats/summary - expect 200, completion statistics
- [ ] T436 [P] [US4] Write contract test for GET /api/stats/history?days=7 - expect 200, array of daily completion counts

### Backend DTOs for US4

- [ ] T437 [P] [US4] Create CompletionStatsDTO record in quarkus-astro-app/src/main/java/org/acme/taskmanager/dto/CompletionStatsDTO.java
- [ ] T438 [P] [US4] Add fields to CompletionStatsDTO: todayCount, weekCount, totalCount, completionRate
- [ ] T439 [P] [US4] Create CompletionHistoryDTO record in quarkus-astro-app/src/main/java/org/acme/taskmanager/dto/CompletionHistoryDTO.java
- [ ] T440 [P] [US4] Add fields to CompletionHistoryDTO: date (LocalDate), count (Long)

### Backend Services for US4

- [ ] T441 [US4] Implement toggleTaskCompletion(String userId, UUID taskId) in TaskService
- [ ] T442 [US4] Validate task exists and belongs to user in TaskService.toggleTaskCompletion()
- [ ] T443 [US4] If task.completed == false: set completed=true, completedAt=now() in TaskService.toggleTaskCompletion()
- [ ] T444 [US4] If task.completed == true: set completed=false, completedAt=null in TaskService.toggleTaskCompletion()
- [ ] T445 [US4] Return updated TaskResponseDTO from TaskService.toggleTaskCompletion()
- [ ] T446 [US4] Create StatsService in quarkus-astro-app/src/main/java/org/acme/taskmanager/service/StatsService.java
- [ ] T447 [US4] Inject TaskRepository into StatsService
- [ ] T448 [US4] Implement getCompletionStats(String userId) in StatsService
- [ ] T449 [US4] Query tasks completed today (completedAt >= start of day) in StatsService.getCompletionStats()
- [ ] T450 [US4] Query tasks completed this week (completedAt >= start of week) in StatsService.getCompletionStats()
- [ ] T451 [US4] Query total completed tasks (completed = true) in StatsService.getCompletionStats()
- [ ] T452 [US4] Calculate completion rate: (completed / total tasks) \* 100 in StatsService.getCompletionStats()
- [ ] T453 [US4] Return CompletionStatsDTO from StatsService.getCompletionStats()
- [ ] T454 [US4] Implement getCompletionHistory(String userId, int days) in StatsService
- [ ] T455 [US4] Query tasks completed per day for last N days using GROUP BY date in StatsService.getCompletionHistory()
- [ ] T456 [US4] Return List<CompletionHistoryDTO> from StatsService.getCompletionHistory()

### Backend API Endpoints for US4

- [ ] T457 [US4] Implement PATCH /api/tasks/{id}/complete endpoint in TaskResource
- [ ] T458 [US4] Extract userId from session in PATCH /api/tasks/{id}/complete
- [ ] T459 [US4] Call TaskService.toggleTaskCompletion() and return 200 OK with TaskResponseDTO
- [ ] T460 [US4] Add OpenAPI annotations to PATCH /api/tasks/{id}/complete matching api.yaml
- [ ] T461 [US4] Create StatsResource in quarkus-astro-app/src/main/java/org/acme/taskmanager/resource/StatsResource.java with @Path("/api/stats")
- [ ] T462 [US4] Inject StatsService and HttpSession into StatsResource
- [ ] T463 [US4] Implement GET /api/stats/summary endpoint in StatsResource
- [ ] T464 [US4] Extract userId from session in GET /api/stats/summary
- [ ] T465 [US4] Call StatsService.getCompletionStats() and return 200 OK with CompletionStatsDTO
- [ ] T466 [US4] Add OpenAPI annotations to GET /api/stats/summary matching api.yaml
- [ ] T467 [US4] Implement GET /api/stats/history endpoint in StatsResource with @QueryParam days (default 30, max 365)
- [ ] T468 [US4] Extract userId from session in GET /api/stats/history
- [ ] T469 [US4] Call StatsService.getCompletionHistory(userId, days) and return 200 OK with List<CompletionHistoryDTO>
- [ ] T470 [US4] Add OpenAPI annotations to GET /api/stats/history matching api.yaml
- [ ] T471 [US4] Regenerate OpenAPI schema by restarting `quarkus dev`
- [ ] T472 [US4] Run `npm run generate:api` to regenerate TypeScript client

### Frontend Completion Toggle Component for US4

- [ ] T473 [US4] Create CompletionToggle.tsx island in quarkus-astro-app/src/main/webui/src/islands/CompletionToggle.tsx
- [ ] T474 [US4] Accept taskId and initialCompleted props in CompletionToggle.tsx
- [ ] T475 [US4] Import useToggleTaskCompletion mutation hook from generated API client
- [ ] T476 [US4] Render checkbox using Shadcn Checkbox component in CompletionToggle.tsx
- [ ] T477 [US4] Set checkbox checked state based on task.completed in CompletionToggle.tsx
- [ ] T478 [US4] Handle checkbox change: call toggleTaskCompletion mutation in CompletionToggle.tsx
- [ ] T479 [US4] Implement optimistic update: toggle checkbox immediately in CompletionToggle.tsx
- [ ] T480 [US4] Invalidate tasks query cache after mutation success in CompletionToggle.tsx
- [ ] T481 [US4] Show loading spinner on checkbox during mutation in CompletionToggle.tsx

### Frontend Undo Completion Feature for US4

- [ ] T482 [US4] Add undo state to CompletionToggle.tsx: track last toggle timestamp
- [ ] T483 [US4] After marking complete, show "Undo" button for 5 seconds in CompletionToggle.tsx
- [ ] T484 [US4] Use setTimeout to hide "Undo" button after 5 seconds in CompletionToggle.tsx
- [ ] T485 [US4] On "Undo" click, call toggleTaskCompletion again to revert in CompletionToggle.tsx
- [ ] T486 [US4] Clear timeout if component unmounts or user navigates away

### Frontend Task List Integration for US4

- [ ] T487 [US4] Update TaskList.tsx to render CompletionToggle island for each task
- [ ] T488 [US4] Pass task.id and task.completed as props to CompletionToggle
- [ ] T489 [US4] Display completion timestamp (completedAt) next to completed tasks in TaskList.tsx
- [ ] T490 [US4] Add visual strikethrough styling to completed task titles in TaskList.tsx
- [ ] T491 [US4] Move completed tasks to separate "Completed" section or fold in TaskList.tsx

### Frontend Completion Stats Component for US4

- [ ] T492 [US4] Create CompletionStats.tsx island in quarkus-astro-app/src/main/webui/src/islands/CompletionStats.tsx
- [ ] T493 [US4] Import useGetCompletionStats hook from generated API client
- [ ] T494 [US4] Call useGetCompletionStats() and display loading/error states
- [ ] T495 [US4] Render stats: "X tasks completed today", "Y this week", "Z total" in CompletionStats.tsx
- [ ] T496 [US4] Display completion rate as percentage with progress bar in CompletionStats.tsx
- [ ] T497 [US4] Style CompletionStats.tsx with Tailwind cards and progress indicators
- [ ] T498 [US4] Add confetti animation when completionRate reaches 100% using canvas-confetti library

### Frontend Completion History Chart for US4

- [ ] T499 [US4] Install chart library for visualization: `npm install recharts` (lightweight for Preact)
- [ ] T500 [US4] Create CompletionChart.tsx island in quarkus-astro-app/src/main/webui/src/islands/CompletionChart.tsx
- [ ] T501 [US4] Import useGetCompletionHistory hook from generated API client
- [ ] T502 [US4] Call useGetCompletionHistory({ days: 7 }) for last week
- [ ] T503 [US4] Render bar chart or line chart showing tasks completed per day using recharts
- [ ] T504 [US4] Format chart with proper axis labels, tooltip on hover
- [ ] T505 [US4] Style chart to match light/dark theme from userTheme atom
- [ ] T506 [US4] Handle empty data: show "No completion history yet" message

### Frontend Dashboard Page for US4

- [ ] T507 [US4] Create dashboard.astro page in quarkus-astro-app/src/main/webui/src/pages/dashboard.astro
- [ ] T508 [US4] Import Layout component in dashboard.astro
- [ ] T509 [US4] Import CompletionStats island with client:load directive
- [ ] T510 [US4] Import CompletionChart island with client:load directive
- [ ] T511 [US4] Add page title "Dashboard" and description in dashboard.astro
- [ ] T512 [US4] Layout stats and chart in responsive grid using Tailwind
- [ ] T513 [US4] Add motivational messages based on completion stats (e.g., "Great job! Keep it up!")

### Testing US4

- [ ] T514 [US4] Manually test: mark task complete, verify checkbox toggles and task gets strikethrough
- [ ] T515 [US4] Manually test: mark task complete, verify timestamp appears
- [ ] T516 [US4] Manually test: mark task complete, click "Undo" within 5 seconds, verify task returns to active
- [ ] T517 [US4] Manually test: mark task complete, wait 6 seconds, verify "Undo" button disappears
- [ ] T518 [US4] Manually test: complete 3 tasks today, navigate to dashboard, verify "3 tasks completed today" shows
- [ ] T519 [US4] Manually test: verify completion rate calculates correctly
- [ ] T520 [US4] Manually test: complete all tasks, verify confetti animation shows (if implemented)
- [ ] T521 [US4] Manually test: view completion history chart, verify last 7 days show correctly
- [ ] T522 [US4] Run contract tests for toggle completion and stats endpoints
- [ ] T523 [US4] Verify optimistic updates: toggle completion, observe immediate UI change

**Checkpoint US4**: At this point, US4 is fully functional. Users can track completion with timestamps, undo mistakes, view statistics and history. SC-011 (server-side state pattern) satisfied.

---

## Phase 8: User Story 5 - Responsive Performance Demonstration (Priority: P5)

**Goal**: Demonstrate Islands Architecture benefits through performance metrics page showing bundle sizes, hydration timing, Web Vitals. Educational value for developers.

**Independent Test**: Navigate to /performance, verify page loads. Verify bundle size <100KB displayed. Verify individual island hydration times displayed. Verify comparison chart shows Islands vs SPA benefits.

### Frontend Performance Measurement Utilities

- [ ] T524 [P] [US5] Create PerformanceMetrics TypeScript interface in quarkus-astro-app/src/main/webui/src/types/performance.ts
- [ ] T525 [P] [US5] Add fields to PerformanceMetrics: pageName, bundleSize, islandHydrations[], timeToInteractive, firstContentfulPaint, largestContentfulPaint, timestamp
- [ ] T526 [P] [US5] Create IslandHydration interface: islandName (string), durationMs (number)
- [ ] T527 [P] [US5] Create performance.ts utility in quarkus-astro-app/src/main/webui/src/lib/performance.ts
- [ ] T528 [P] [US5] Implement collectMetrics(pageName: string) function using Performance API
- [ ] T529 [P] [US5] Collect First Contentful Paint using PerformanceObserver for 'paint' entries
- [ ] T530 [P] [US5] Collect Largest Contentful Paint using PerformanceObserver for 'largest-contentful-paint'
- [ ] T531 [P] [US5] Collect Time to Interactive from PerformanceNavigationTiming
- [ ] T532 [P] [US5] Collect island hydration marks from performance.getEntriesByType('measure')
- [ ] T533 [P] [US5] Export collectMetrics function and PerformanceMetrics type

### Frontend Island Hydration Tracking

- [ ] T534 [P] [US5] Update TaskList.tsx to mark hydration start and end using performance.mark()
- [ ] T535 [P] [US5] Add useEffect hook in TaskList.tsx: performance.mark('TaskList-hydrated') on mount
- [ ] T536 [P] [US5] Call performance.measure('TaskList-hydration', 'TaskList-start', 'TaskList-hydrated')
- [ ] T537 [P] [US5] Repeat hydration tracking for TaskForm.tsx: mark and measure hydration
- [ ] T538 [P] [US5] Repeat hydration tracking for TaskFilter.tsx: mark and measure hydration
- [ ] T539 [P] [US5] Repeat hydration tracking for CategoryManager.tsx: mark and measure hydration
- [ ] T540 [P] [US5] Repeat hydration tracking for CompletionToggle.tsx: mark and measure hydration
- [ ] T541 [P] [US5] Repeat hydration tracking for ThemeToggle.tsx: mark and measure hydration
- [ ] T542 [P] [US5] Repeat hydration tracking for CompletionStats.tsx: mark and measure hydration
- [ ] T543 [P] [US5] Repeat hydration tracking for CompletionChart.tsx: mark and measure hydration

### Frontend Bundle Size Calculation

- [ ] T544 [P] [US5] Run production build: `npm run build` in src/main/webui/
- [ ] T545 [P] [US5] Inspect dist/ folder to find generated JS bundle files
- [ ] T546 [P] [US5] Calculate gzipped size of main bundle: `gzip -c dist/assets/index-*.js | wc -c`
- [ ] T547 [P] [US5] Document bundle size in performance.ts or as constant
- [ ] T548 [P] [US5] Verify bundle size <100KB gzipped per SC-003 (if not, optimize imports/code splitting)

### Frontend Performance Metrics Island

- [ ] T549 [US5] Create PerformanceMetrics.tsx island in quarkus-astro-app/src/main/webui/src/islands/PerformanceMetrics.tsx
- [ ] T550 [US5] Import collectMetrics from performance.ts
- [ ] T551 [US5] Call collectMetrics('homepage') on component mount in PerformanceMetrics.tsx
- [ ] T552 [US5] Display collected metrics in cards: FCP, LCP, TTI, Bundle Size
- [ ] T553 [US5] Display individual island hydration times in a table in PerformanceMetrics.tsx
- [ ] T554 [US5] Highlight islands that hydrate >200ms in red (violation of SC-004)
- [ ] T555 [US5] Add visual indicators: green checkmark if metric meets target, red X if exceeds
- [ ] T556 [US5] Style PerformanceMetrics.tsx with Tailwind cards and tables

### Frontend Performance Comparison Chart

- [ ] T557 [US5] Create PerformanceComparison.tsx island in quarkus-astro-app/src/main/webui/src/islands/PerformanceComparison.tsx
- [ ] T558 [US5] Import recharts library for comparison chart
- [ ] T559 [US5] Create mock data comparing Islands Architecture vs traditional SPA for: bundle size, load time, TTI
- [ ] T560 [US5] Render bar chart showing Islands vs SPA side-by-side for each metric
- [ ] T561 [US5] Add annotations explaining Islands benefits: "3x faster load time", "5x smaller bundles"
- [ ] T562 [US5] Style chart with Tailwind and theme support

### Frontend Hydration Visualization (Optional Advanced Feature)

- [ ] T563 [P] [US5] Create HydrationVisualizer.tsx island with toggle button to enable/disable
- [ ] T564 [P] [US5] When enabled, overlay visual indicators on each island showing when it hydrated
- [ ] T565 [P] [US5] Use CSS animations to highlight islands as they hydrate (flash border or background)
- [ ] T566 [P] [US5] Display timeline showing order of island hydration
- [ ] T567 [P] [US5] Add legend explaining what each visual indicator means

### Frontend Performance Page

- [ ] T568 [US5] Create performance.astro page in quarkus-astro-app/src/main/webui/src/pages/performance.astro
- [ ] T569 [US5] Import Layout component in performance.astro
- [ ] T570 [US5] Import PerformanceMetrics island with client:load directive
- [ ] T571 [US5] Import PerformanceComparison island with client:load directive
- [ ] T572 [US5] Import HydrationVisualizer island if implemented
- [ ] T573 [US5] Add page title "Performance Insights" and educational description explaining Islands Architecture
- [ ] T574 [US5] Add section explaining each metric: what it measures, why it matters, target values
- [ ] T575 [US5] Add section with architecture diagram or explanation of Islands concept
- [ ] T576 [US5] Layout metrics, comparison chart, and visualizer in responsive grid using Tailwind
- [ ] T577 [US5] Add "Export Metrics" button to download performance data as JSON

### Documentation for Performance Demonstration

- [ ] T578 [US5] Add Performance section to README.md explaining metrics and how to interpret them
- [ ] T579 [US5] Document bundle size optimization techniques in README.md
- [ ] T580 [US5] Document hydration optimization techniques in README.md
- [ ] T581 [US5] Add troubleshooting section for performance issues in README.md

### Testing US5

- [ ] T582 [US5] Manually test: navigate to http://localhost:7171/performance, verify page loads
- [ ] T583 [US5] Manually test: verify FCP metric displays and is <1.5s per SC-002
- [ ] T584 [US5] Manually test: verify TTI metric displays
- [ ] T585 [US5] Manually test: verify bundle size displays and is <100KB gzipped per SC-003
- [ ] T586 [US5] Manually test: verify island hydration times table shows all islands
- [ ] T587 [US5] Manually test: verify each island hydrates <200ms per SC-004 (or document which exceed)
- [ ] T588 [US5] Manually test: verify comparison chart shows Islands vs SPA benefits
- [ ] T589 [US5] Manually test: toggle hydration visualizer (if implemented), reload page, verify visual indicators show
- [ ] T590 [US5] Manually test: run Lighthouse audit on homepage, verify good Web Vitals scores
- [ ] T591 [US5] Manually test: run production build, measure actual bundle sizes, verify <100KB
- [ ] T592 [US5] Document performance results in README.md or performance page

**Checkpoint US5**: At this point, US5 is fully functional. Performance demonstration shows Islands Architecture benefits with real metrics. SC-002, SC-003, SC-004, SC-009 are satisfied. Educational value achieved.

---

## Phase 9: Session Management & Authentication (Priority: Foundation for All)

**Goal**: Implement simple session management for demo authentication. Users can "login" with username only, session persists user ID.

**Note**: This should have been in Foundational phase, but can be implemented in parallel with user stories or after US1.

### Backend Session Endpoints

- [ ] T593 [P] Create UserDTO record in quarkus-astro-app/src/main/java/org/acme/taskmanager/dto/UserDTO.java with username field
- [ ] T594 [P] Create SessionResource in quarkus-astro-app/src/main/java/org/acme/taskmanager/resource/SessionResource.java with @Path("/api/session")
- [ ] T595 Implement POST /api/session/login endpoint in SessionResource
- [ ] T596 Accept username in request body (no password validation for demo)
- [ ] T597 Store username in HttpSession using SessionUtils.setCurrentUser()
- [ ] T598 Return 200 OK with UserDTO
- [ ] T599 Add OpenAPI annotations to POST /api/session/login matching api.yaml
- [ ] T600 Implement GET /api/session/user endpoint in SessionResource
- [ ] T601 Call SessionUtils.getCurrentUserId() to get username from session
- [ ] T602 Return 200 OK with UserDTO if session exists, 401 Unauthorized if not
- [ ] T603 Add OpenAPI annotations to GET /api/session/user matching api.yaml
- [ ] T604 Implement POST /api/session/logout endpoint in SessionResource
- [ ] T605 Invalidate HttpSession
- [ ] T606 Return 204 No Content
- [ ] T607 Add OpenAPI annotations to POST /api/session/logout matching api.yaml
- [ ] T608 Regenerate OpenAPI schema and TypeScript client

### Frontend Login Component

- [ ] T609 Create Login.tsx island in quarkus-astro-app/src/main/webui/src/islands/Login.tsx
- [ ] T610 Import useLogin mutation hook from generated API client
- [ ] T611 Create username input field using Shadcn Input
- [ ] T612 Create "Login" button
- [ ] T613 Handle login submission: call useLogin mutation with username
- [ ] T614 On success, redirect to homepage or store user in global state
- [ ] T615 On error, display error message
- [ ] T616 Style Login.tsx with Tailwind centered card layout

### Frontend Login Page

- [ ] T617 Create login.astro page in quarkus-astro-app/src/main/webui/src/pages/login.astro (optional, or use modal)
- [ ] T618 Import Layout and Login island
- [ ] T619 Display login form centered on page
- [ ] T620 After successful login, redirect to /

### Frontend Session Check

- [ ] T621 Create useSession hook in quarkus-astro-app/src/main/webui/src/lib/hooks/useSession.ts
- [ ] T622 Call useGetCurrentUser() from generated API client in useSession
- [ ] T623 Return user data and isAuthenticated boolean
- [ ] T624 Update Layout.astro or Navigation to show username if logged in
- [ ] T625 Add "Logout" button to Navigation that calls useLogout mutation
- [ ] T626 Redirect to /login if not authenticated (optional, or allow guest access)

### Backend Default Category Seeding

- [ ] T627 Update CategoryService to call ensureDefaultCategories() on first login
- [ ] T628 Hook into SessionResource POST /login to call categoryService.ensureDefaultCategories(userId)
- [ ] T629 Verify default categories (Work, Personal, Shopping) exist after login

### Testing Session Management

- [ ] T630 Manually test: navigate to /login, enter username "testuser", submit
- [ ] T631 Manually test: verify session persists, refresh page, user still logged in
- [ ] T632 Manually test: click Logout, verify session cleared
- [ ] T633 Manually test: verify default categories created on first login
- [ ] T634 Manually test: login as "user1", create tasks, logout, login as "user2", verify tasks don't show (user isolation)
- [ ] T635 Write contract test for POST /api/session/login
- [ ] T636 Write contract test for GET /api/session/user
- [ ] T637 Write contract test for POST /api/session/logout

**Checkpoint Session**: Session management complete. SC-011 (server-side session pattern) fully satisfied.

---

## Phase 10: Polish & Cross-Cutting Concerns

**Purpose**: Final polish, accessibility, error handling, documentation, CI/CD integration

### Accessibility Audit

- [ ] T638 [P] Install accessibility testing tool: `npm install -D @axe-core/react` or use browser extension
- [ ] T639 [P] Run accessibility audit on all pages: index, categories, dashboard, performance
- [ ] T640 [P] Fix missing alt text on images (if any)
- [ ] T641 [P] Fix missing ARIA labels on interactive elements
- [ ] T642 [P] Verify keyboard navigation works: Tab through all interactive elements
- [ ] T643 [P] Verify screen reader compatibility: test with NVDA or VoiceOver
- [ ] T644 [P] Fix color contrast issues if any (WCAG AA minimum 4.5:1)
- [ ] T645 [P] Add skip-to-content link for keyboard users
- [ ] T646 [P] Verify all forms have proper labels and error messages
- [ ] T647 [P] Document accessibility features in README.md

### Error Handling & Edge Cases

- [ ] T648 [P] Add global error boundary in Layout.astro to catch React errors
- [ ] T649 [P] Create 404.astro page for not found errors
- [ ] T650 [P] Create 500.astro page for server errors
- [ ] T651 [P] Test empty states: no tasks, no categories, no completion history
- [ ] T652 [P] Test large data sets: create 100+ tasks, verify pagination works
- [ ] T653 [P] Test long text content: task title with 200 chars, description with 2000 chars
- [ ] T654 [P] Test browser storage limits: fill localStorage, verify graceful degradation
- [ ] T655 [P] Test rapid interactions: spam click "Complete" checkbox, verify debouncing works
- [ ] T656 [P] Test network errors: disconnect network, try to create task, verify error message
- [ ] T657 [P] Test concurrent edits: open same task in two tabs, edit both, verify last write wins message

### Performance Optimization

- [ ] T658 [P] Run Lighthouse audit on all pages, aim for >90 performance score
- [ ] T659 [P] Optimize images: compress, use WebP format, add lazy loading
- [ ] T660 [P] Review bundle size: remove unused dependencies, use code splitting if needed
- [ ] T661 [P] Verify HTTP caching headers set correctly in application.properties
- [ ] T662 [P] Enable gzip compression in Quarkus
- [ ] T663 [P] Measure API response times, verify <500ms p95 per SC-007
- [ ] T664 [P] Test with 50 concurrent users using load testing tool (k6 or Apache JMeter), verify SC-008

### Documentation Finalization

- [ ] T665 Update README.md with final architecture diagrams
- [ ] T666 Update README.md with state management pattern documentation per SC-012
- [ ] T667 Document all environment variables and configuration options
- [ ] T668 Add CONTRIBUTING.md with guidelines for contributors
- [ ] T669 Add LICENSE file (choose appropriate license)
- [ ] T670 Update CLAUDE.md with final project structure and commands
- [ ] T671 Create ARCHITECTURE.md documenting Islands Architecture decisions and trade-offs
- [ ] T672 Document all 4 state management patterns with code examples in ARCHITECTURE.md per SC-012

### CI/CD Finalization

- [ ] T673 Verify .github/workflows/ci.yml runs successfully on push
- [ ] T674 Add badge to README.md showing CI status
- [ ] T675 Add code coverage reporting to CI pipeline (Jacoco for backend, Vitest for frontend)
- [ ] T676 Set up automatic OpenAPI schema validation in CI
- [ ] T677 Set up automatic bundle size monitoring in CI (fail if >100KB)

### Final Testing & Validation

- [ ] T678 Run full test suite: `./mvnw verify` for backend tests
- [ ] T679 Run full frontend tests: `npm test` in src/main/webui/
- [ ] T680 Verify zero Checkstyle violations: `./mvnw validate`
- [ ] T681 Verify zero PMD violations: `./mvnw validate`
- [ ] T682 Verify zero ESLint violations: `npm run lint`
- [ ] T683 Verify zero Prettier issues: `npm run format:check`
- [ ] T684 Verify all success criteria met (SC-001 through SC-017)
- [ ] T685 Run full production build: `./mvnw clean package -DskipTests`
- [ ] T686 Test production JAR: run and verify application works
- [ ] T687 Measure total build time, verify <2 minutes per SC-014

### Template Readiness

- [ ] T688 Create template checklist: verify project can be cloned and used as bootstrap
- [ ] T689 Test fresh clone: git clone in new directory, run setup, verify <10 minutes
- [ ] T690 Review all configuration files for production-readiness and comments
- [ ] T691 Verify project structure matches plan.md exactly
- [ ] T692 Remove any demo data or temporary files
- [ ] T693 Create TEMPLATE_USAGE.md guide for teams using this as bootstrap
- [ ] T694 Tag release v1.0.0 as production-ready template

**Final Checkpoint**: All user stories complete, all success criteria met, project is production-ready template with zero quality violations and comprehensive documentation.

---

## Dependencies & Execution Order

### Story Completion Order

```
P0 (US0) → Foundation → US1
                    ↓
                   US2 (depends on US1 for UI)
                    ↓
                   US3 (depends on US1, US2)
                    ↓
                   US4 (depends on US1, US2)
                    ↓
                   US5 (independent, can be parallel)
                    ↓
               Session Management (can be parallel with US1-US5)
                    ↓
                  Polish
```

### Recommended MVP

**MVP Scope** (US0 + Foundation + US1):

- Production-ready tooling infrastructure
- View and navigate tasks
- Filter by category/priority/status
- Client-side preferences persistence
- **Estimated effort**: ~40-50 hours
- **Value**: Fully functional template with viewing capabilities

### Parallel Execution Opportunities

**Phase 2 (US0) - High Parallelism**:

- T007-T020: npm dependency installation can run in parallel
- T021-T031: ESLint configuration independent
- T032-T038: Prettier configuration independent
- T048-T053: TypeScript configuration independent
- T054-T060: Tailwind configuration independent
- T068-T076: Orval configuration independent
- T100-T107: CI/CD configuration independent

**Phase 4 (US1) - Moderate Parallelism**:

- T146-T151: Contract tests can be written in parallel
- T152-T163: Models can be created in parallel
- T164-T169: DTOs can be created in parallel
- T199-T210: Frontend state management independent from backend

**Phases 5-8 (US2-US5) - Each user story is independent**:

- After US1 complete, US2, US3, US4 can be implemented in parallel by different developers
- US5 is completely independent and can be implemented anytime

### Task Execution Summary

**Total Tasks**: 694
**Tasks by Phase**:

- Phase 1 (Setup): 5 tasks
- Phase 2 (US0 Bootstrap): 120 tasks
- Phase 3 (Foundation): 20 tasks
- Phase 4 (US1 View Tasks): 119 tasks
- Phase 5 (US2 Create/Edit): 85 tasks
- Phase 6 (US3 Categories): 83 tasks
- Phase 7 (US4 Completion): 91 tasks
- Phase 8 (US5 Performance): 69 tasks
- Phase 9 (Session): 45 tasks
- Phase 10 (Polish): 57 tasks

**Estimated Effort**:

- US0 Bootstrap: ~16-20 hours (critical foundation)
- Foundation: ~4-6 hours
- US1: ~20-24 hours
- US2: ~16-20 hours
- US3: ~14-18 hours
- US4: ~16-20 hours
- US5: ~12-16 hours
- Session: ~8-10 hours
- Polish: ~10-12 hours
- **Total**: ~120-150 hours (3-4 weeks for 1 developer)

**Parallel Development** (3 developers):

- Developer 1: US0 + Foundation + US1
- Developer 2: US2 + US3
- Developer 3: US4 + US5 + Session
- All: Polish together
- **Total**: ~50-60 hours (1.5-2 weeks)

---

## Implementation Strategy

1. **Start with US0**: Complete entire bootstrap template configuration before any features
2. **Build Foundation**: Complete backend infrastructure before user stories
3. **MVP First**: US1 is minimum viable product - get this working end-to-end
4. **Incremental Delivery**: Each user story after US1 is independently deployable
5. **Quality Gates**: Run linting/testing after each phase, never commit violations
6. **Documentation as You Go**: Update README as features are added
7. **Test Continuously**: Run contract tests after each API endpoint implementation

This task list provides maximum detail with 694 specific, actionable tasks organized by user story for independent implementation and testing.
