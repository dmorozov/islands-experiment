# Implementation Plan: Task Manager Sample Application

**Branch**: `001-task-manager-app` | **Date**: 2025-11-21 | **Spec**: [spec.md](./spec.md)
**Input**: Feature specification from `/workspaces/specs/001-task-manager-app/spec.md`

## Summary

Build a production-ready Task Manager application demonstrating Islands Architecture with Quarkus backend and Astro frontend. The project prioritizes **quality tool configuration first** (P0), establishing a complete linting and formatting infrastructure (Checkstyle, PMD, ESLint, Prettier, pre-commit hooks) before implementing features. This ensures the project serves as a battle-tested template for production applications.

**Technical Approach**: Web application with strict quality gates, multi-pattern state management demonstration, and comprehensive performance monitoring.

## Technical Context

**Language/Version**:
- Backend: Java 21
- Frontend: TypeScript 5.x (strict mode), ES2020+

**Primary Dependencies**:
- Backend: Quarkus 3.29.4, SmallRye OpenAPI, Quarkus REST, Quarkus WebSockets Next
- Frontend: Astro 5.x, Preact, TanStack Query v5, Axios, Tailwind CSS v4, Shadcn/ui
- Code Quality: Checkstyle, PMD, ESLint (flat config), Prettier, Husky + lint-staged
- API Generation: Orval (OpenAPI → TypeScript client)

**Storage**: H2 in-memory database (server-side), localStorage/sessionStorage (client-side)

**Testing**:
- Backend: JUnit 5, REST Assured (contract tests)
- Frontend: Vitest (unit), Playwright (integration - optional)

**Target Platform**: Modern browsers (Chrome, Firefox, Safari, Edge), ES2020+, no IE11

**Project Type**: Web application (frontend + backend)

**Performance Goals**:
- First Contentful Paint (FCP): <1.5s on 3G
- JavaScript bundle: <100KB gzipped for homepage
- Island hydration: <200ms per island
- API latency: <500ms p95
- Build time: <2 minutes (full build)

**Constraints**:
- Zero quality violations (Checkstyle, PMD, ESLint, Prettier)
- TypeScript strict mode (no `any` types)
- Islands Architecture fidelity (minimal JS, static-first)
- Production-ready configuration (template suitable)

**Scale/Scope**:
- 4-6 pages (moderate complexity)
- 4 entities (Task, Category, User Preferences, Performance Metrics)
- 20-30 REST endpoints
- 50 concurrent users (load test target)
- 6 user stories (P0-P5)

## Constitution Check

*GATE: Must pass before Phase 0 research. Re-check after Phase 1 design.*

### Principle I: Islands Architecture Fidelity ✅
- **Requirement**: Ship minimal JavaScript, static-first rendering, component isolation
- **Implementation**: Astro for static pages, Preact islands for interactivity only
- **Verification**: Performance metrics page tracks bundle sizes and hydration
- **Status**: PASS - Architecture chosen specifically for Islands pattern

### Principle II: Production-Ready Prototype Quality ✅
- **Requirement**: Code follows best practices, quality gates enforce standards
- **Implementation**: P0 user story dedicated to tooling configuration
- **Verification**: All builds fail on quality violations
- **Status**: PASS - P0 priority ensures foundation before features

### Principle III: API Contract-Driven Development ✅
- **Requirement**: Code-first workflow with OpenAPI generation and Orval client
- **Implementation**: Quarkus generates OpenAPI → Orval → TanStack Query client
- **Verification**: Frontend cannot call APIs without generated client
- **Status**: PASS - Workflow designed around contract generation

### Principle IV: State Management Research (Multi-Pattern) ✅
- **Requirement**: Demonstrate 4 state patterns with working examples
- **Implementation**:
  - Server sessions: Quarkus session management
  - Client storage: localStorage/sessionStorage
  - Island communication: Nano Stores or custom event bus
  - Cross-page state: Astro middleware + persistent storage
- **Verification**: Each pattern has dedicated implementation and documentation
- **Status**: PASS - Spec explicitly requires all 4 patterns

### Principle V: Developer Experience & Type Safety ✅
- **Requirement**: TypeScript strict mode, proper Java types, tooling integration
- **Implementation**: TypeScript strict + ESLint, Shadcn/ui components, HMR via Quinoa
- **Verification**: Build fails on type errors, IDE integration with configs
- **Status**: PASS - DX prioritized throughout design

### Principle VI: Test-Conscious Development ✅
- **Requirement**: Tests for APIs/critical logic, optional for experimental code
- **Implementation**: Contract tests for all API endpoints, integration for state patterns
- **Verification**: Test coverage targets: >70% backend, >50% islands
- **Status**: PASS - Balanced approach allows research flexibility

### Principle VII: Code Quality Enforcement (Strict) ✅
- **Requirement**: All quality checks must pass, zero tolerance for violations
- **Implementation**: P0 story configures all tools, pre-commit hooks enforce
- **Verification**: Builds fail on any Checkstyle/PMD/ESLint/Prettier violation
- **Status**: PASS - Explicit requirement in spec (FR-021 through FR-030)

**GATE STATUS**: ✅ ALL PRINCIPLES SATISFIED - Proceed to Phase 0

## Project Structure

### Documentation (this feature)

```text
specs/001-task-manager-app/
├── plan.md              # This file
├── spec.md              # Feature specification
├── CLARIFICATION.md     # Clarification summary (tooling requirements)
├── checklists/
│   └── requirements.md  # Specification quality checklist
├── research.md          # Phase 0: Technology research
├── data-model.md        # Phase 1: Entity design
├── contracts/           # Phase 1: OpenAPI schemas
│   └── api.yaml
└── quickstart.md        # Phase 1: Developer onboarding guide
```

### Source Code (repository root)

```text
quarkus-astro-app/
├── src/main/
│   ├── java/org/acme/taskmanager/
│   │   ├── model/          # JPA entities: Task, Category
│   │   ├── resource/       # JAX-RS endpoints (REST controllers)
│   │   ├── service/        # Business logic
│   │   ├── dto/            # Data Transfer Objects for API
│   │   └── session/        # Session management utilities
│   ├── resources/
│   │   ├── application.properties  # Quarkus configuration
│   │   └── META-INF/resources/     # Static assets
│   └── webui/              # Astro frontend (managed by Quinoa)
│       ├── src/
│       │   ├── pages/      # Astro pages (file-based routing)
│       │   │   ├── index.astro           # Task list (P1)
│       │   │   ├── categories.astro      # Category management (P3)
│       │   │   ├── dashboard.astro       # Completion stats (P4)
│       │   │   └── performance.astro     # Performance metrics (P5)
│       │   ├── components/ # Astro components (static)
│       │   │   ├── Layout.astro          # Base layout
│       │   │   ├── Navigation.astro      # Navigation menu
│       │   │   └── ui/                   # Shadcn/ui components
│       │   ├── islands/    # Preact islands (interactive)
│       │   │   ├── TaskForm.tsx          # Create/edit task form (P2)
│       │   │   ├── TaskList.tsx          # Task display with filters (P1)
│       │   │   ├── CategoryManager.tsx   # Category CRUD (P3)
│       │   │   ├── CompletionToggle.tsx  # Checkbox component (P4)
│       │   │   ├── ThemeToggle.tsx       # Dark/light mode (P1)
│       │   │   └── PerformanceChart.tsx  # Metrics visualization (P5)
│       │   ├── lib/        # Utilities and helpers
│       │   │   ├── api/              # Orval-generated API client
│       │   │   ├── utils.ts          # Shadcn/ui utils
│       │   │   ├── storage.ts        # localStorage helpers
│       │   │   └── state.ts          # State management (Nano Stores)
│       │   ├── styles/
│       │   │   └── globals.css       # Tailwind base + custom
│       │   └── types/      # TypeScript type definitions
│       ├── public/         # Static assets
│       ├── api/            # Generated OpenAPI schema (from Quarkus)
│       │   └── openapi.json
│       ├── astro.config.mjs         # Astro configuration
│       ├── tailwind.config.mjs      # Tailwind CSS configuration
│       ├── tsconfig.json            # TypeScript configuration
│       ├── components.json          # Shadcn/ui configuration
│       ├── orval.config.ts          # Orval configuration
│       ├── eslint.config.js         # ESLint (flat config)
│       ├── .prettierrc.json         # Prettier configuration
│       ├── .lintstagedrc.json       # lint-staged configuration
│       ├── package.json             # npm dependencies
│       └── .husky/                  # Pre-commit hooks
│           └── pre-commit
├── src/test/java/           # Backend tests
│   ├── contract/            # Contract tests (REST endpoints)
│   ├── integration/         # Integration tests
│   └── unit/                # Unit tests
└── pom.xml                  # Maven build configuration

parent/
└── pom.xml                  # Parent POM (Checkstyle, PMD already configured)

build-tools/
└── src/main/resources/product/
    ├── checkstyle.xml            # Checkstyle rules
    ├── checkstyle-suppressions.xml
    ├── pmd-ruleset.xml           # PMD rules
    └── eclipse-java-google-style.xml
```

**Structure Decision**: Web application structure (Option 2 from template). Backend in `quarkus-astro-app/src/main/java`, frontend in `quarkus-astro-app/src/main/webui`. Quinoa integration manages the build coordination. Java sources follow standard Maven layout. Frontend uses Astro's file-based routing with islands in dedicated directory. Configuration files colocated with frontend for clarity.

## Complexity Tracking

> **No violations to justify** - All constitution checks pass

---

## Phase 0: Outline & Research

**Status**: To be executed
**Output**: `research.md` resolving all technical unknowns

### Research Tasks

1. **ESLint Configuration for Astro + Preact + TypeScript**
   - Research flat config format (`eslint.config.js`)
   - Airbnb TypeScript style guide integration
   - Preact and Astro plugin compatibility
   - Package requirements and configuration structure

2. **Prettier Configuration and ESLint Integration**
   - Prettier plugins for Astro and Tailwind
   - Conflict resolution with ESLint
   - Best practice configuration values
   - Order of operations in pre-commit hooks

3. **Pre-commit Hook Implementation (Husky + lint-staged)**
   - Setup process for npm project
   - Integration with Maven build (optional)
   - Configuration for staged file linting
   - Performance optimization for large commits

4. **Orval Configuration for OpenAPI Client Generation**
   - TanStack Query v5 integration
   - Axios custom mutator for auth/error handling
   - File paths and generation workflow
   - Integration with Quarkus OpenAPI schema location

5. **Tailwind CSS v4 + Shadcn/ui with Astro**
   - Tailwind v4 setup with Astro
   - Shadcn/ui component installation workflow
   - Preact island integration
   - TypeScript path aliases configuration

6. **State Management Patterns for Islands Architecture**
   - Nano Stores vs Zustand vs custom event bus
   - Server session management with Quarkus
   - localStorage/sessionStorage best practices
   - Cross-page state persistence strategies

7. **Performance Measurement in Astro**
   - Bundle analysis tools
   - Hydration timing measurement
   - Web Vitals integration
   - Custom performance metrics collection

8. **Testing Strategy for Islands**
   - Vitest configuration for Astro
   - Testing Preact islands in isolation
   - Contract testing for REST APIs
   - Integration testing with state management

### Research Output Structure (`research.md`)

For each research area, document:
- **Decision**: What was chosen (e.g., "Use Husky + lint-staged for pre-commit hooks")
- **Rationale**: Why chosen (e.g., "Best npm integration, runs only on staged files")
- **Alternatives Considered**: What else was evaluated (e.g., "pre-commit (Python), simple-git-hooks")
- **Configuration**: Specific setup required
- **Dependencies**: npm packages or Maven dependencies needed
- **References**: Links to official documentation

---

## Phase 1: Design & Contracts

**Prerequisites**: `research.md` complete with all decisions documented

**Status**: To be executed
**Output**: `data-model.md`, `contracts/api.yaml`, `quickstart.md`

### 1. Data Model Design (`data-model.md`)

Extract entities from spec and design JPA entities + TypeScript types:

#### Task Entity
- **Fields**: id (UUID), title (String, max 200), description (String, max 2000), categoryId (UUID FK), priority (enum: HIGH/MEDIUM/LOW), completed (boolean), completedAt (timestamp nullable), createdAt (timestamp), updatedAt (timestamp), userId (String from session)
- **Relationships**: ManyToOne with Category
- **Validation**: title required, length constraints, priority enum validation
- **Indexes**: userId, categoryId, completed, createdAt

#### Category Entity
- **Fields**: id (UUID), name (String, max 50, unique per user), colorCode (String nullable), isDefault (boolean), createdAt (timestamp), userId (String from session)
- **Relationships**: OneToMany with Task
- **Validation**: name required and unique per user, cannot delete if isDefault=true
- **Indexes**: userId, name

#### UserPreferences (Client-Side Model)
- **Fields**: theme (LIGHT/DARK), lastViewedPage (string URL), activeFilters (object: {category, priority, status}), tasksPerPage (number, default 20)
- **Storage**: localStorage key: `taskmanager:prefs:${userId}`
- **TypeScript Interface**: Full type safety with Zod schema

#### PerformanceMetrics (Client-Side Model)
- **Fields**: pageName (string), bundleSize (number KB), islandHydrations (array of {islandName, durationMs}), timeToInteractive (number ms), firstContentfulPaint (number ms), timestamp (ISO string)
- **Storage**: Optional server persistence, primarily client-side collection
- **TypeScript Interface**: Performance API integration

### 2. API Contract Design (`contracts/api.yaml`)

Generate OpenAPI 3.0 schema for REST endpoints:

#### Task Endpoints
- `GET /api/tasks` - List user's tasks (query params: category, priority, status, page, size)
- `GET /api/tasks/{id}` - Get single task
- `POST /api/tasks` - Create task (body: TaskCreateDTO)
- `PUT /api/tasks/{id}` - Update task (body: TaskUpdateDTO)
- `DELETE /api/tasks/{id}` - Delete task
- `PATCH /api/tasks/{id}/complete` - Toggle completion status

#### Category Endpoints
- `GET /api/categories` - List user's categories
- `GET /api/categories/{id}` - Get single category
- `POST /api/categories` - Create category (body: CategoryCreateDTO)
- `PUT /api/categories/{id}` - Update category (body: CategoryUpdateDTO)
- `DELETE /api/categories/{id}` - Delete category (validates not default)

#### Statistics Endpoints
- `GET /api/stats/summary` - Get completion statistics (today, week, total)
- `GET /api/stats/history` - Get historical completion data (query param: days)

#### Session Endpoints
- `GET /api/session/user` - Get current user info
- `POST /api/session/login` - Simple login (username only, demo auth)
- `POST /api/session/logout` - Logout

**OpenAPI Generation**: Quarkus SmallRye OpenAPI will auto-generate schema at runtime. Manual schema will be created as reference for planning, then replaced by Quarkus-generated version during implementation.

### 3. Developer Quickstart Guide (`quickstart.md`)

Document new developer onboarding process (target: <10 minutes):

#### Prerequisites
- Java 21 (check: `java -version`)
- Node.js 20.17.0+ (managed by Quinoa, but check: `node -version`)
- Maven 3.9+ (check: `mvn -version`)
- Git

#### Quick Start (3 commands)
```bash
# 1. Clone and navigate
git clone <repo> && cd quarkus-astro-app

# 2. Install pre-commit hooks
cd src/main/webui && npm install && cd ../../..

# 3. Start development server
quarkus dev
```

Access at: http://localhost:7171

#### Verification Checklist
- [ ] Maven build passes: `./mvnw clean package` (0 Checkstyle/PMD violations)
- [ ] Frontend build passes: `cd src/main/webui && npm run build`
- [ ] Pre-commit hooks work: Make a change, attempt commit with violation
- [ ] Hot reload works: Edit Java file, edit Astro file, verify browser updates
- [ ] API docs accessible: http://localhost:7171/swagger-ui

#### Development Workflow
- Backend dev: Edit Java in `src/main/java`, Quarkus auto-reloads
- Frontend dev: Edit Astro/Preact in `src/main/webui/src`, HMR updates browser
- API changes: Edit endpoints, OpenAPI regenerates, run `npm run generate:api` in webui
- Linting: Auto-runs on commit via pre-commit hooks

### 4. Agent Context Update

Run agent context update script:
```bash
cd /workspaces
./.specify/scripts/bash/update-agent-context.sh claude
```

This will add new technologies to CLAUDE.md between markers while preserving manual content.

**Technologies to add**:
- ESLint flat config with Airbnb TypeScript
- Prettier with Astro/Tailwind plugins
- Husky + lint-staged
- Orval with TanStack Query v5
- Tailwind CSS v4
- Shadcn/ui component library
- Nano Stores (or chosen state library)
- Vitest (testing framework)

---

## Phase 2: Constitution Re-Check

After Phase 1 design artifacts are complete, re-evaluate Constitution Check:

### Re-evaluation Criteria

1. **Islands Architecture Fidelity**: Does the API design support minimal data fetching? Are islands truly isolated?
   - Verify: No shared state dependencies, each island has dedicated endpoint

2. **Production-Ready Quality**: Are config files production-grade? Is documentation complete?
   - Verify: All 17 config files from research included, README comprehensive

3. **API Contract-Driven**: Does Orval config correctly generate from Quarkus schema?
   - Verify: File paths align, generation tested with sample schema

4. **State Management Research**: Are all 4 patterns architected in data model?
   - Verify: Server session (Quarkus), client storage (UserPreferences), island communication (state lib), cross-page (middleware)

5. **Developer Experience**: Does quickstart achieve <10 minute setup?
   - Verify: Test with fresh clone, time the process, document any issues

6. **Test-Conscious**: Are contract test endpoints identified?
   - Verify: Each API endpoint mapped to contract test in structure

7. **Code Quality Enforcement**: Are all quality tools configured and tested?
   - Verify: Run linting on sample code, confirm violations fail build

**Expected Outcome**: All principles still satisfied. If violations found, document in Complexity Tracking table.

---

## Implementation Phases (Post-Planning)

*The following phases will be executed by `/speckit.tasks` and `/speckit.implement` commands*

### Recommended Task Organization (Preview)

Based on spec priorities and constitution requirements:

**Phase 0: Foundation (P0 - Bootstrap Template)**
- Install all npm dependencies
- Configure ESLint + Prettier + Husky
- Configure Tailwind CSS + Shadcn/ui
- Configure Orval
- Configure TypeScript strict mode
- Verify all quality gates work
- Write comprehensive README

**Phase 1: Backend Infrastructure**
- Create JPA entities (Task, Category)
- Implement session management
- Create DTOs and mappers
- Setup validation

**Phase 2: API Layer (Priority: P1-P2 endpoints)**
- Task CRUD endpoints
- Category CRUD endpoints
- Contract tests for each endpoint
- Generate OpenAPI schema

**Phase 3: Frontend Foundation**
- Setup Astro pages structure
- Create base layout and navigation
- Configure Orval and generate API client
- Implement client-side storage utilities

**Phase 4: User Story Implementation (P1 → P5)**
- P1: View and navigate tasks
- P2: Create and edit tasks
- P3: Organize with categories
- P4: Track completion
- P5: Performance demonstration

**Phase 5: State Management Patterns**
- Server sessions (authentication)
- Client storage (preferences)
- Island communication (event bus)
- Cross-page state (middleware)

**Phase 6: Polish & Documentation**
- Performance optimization
- Accessibility audit
- Documentation updates
- CI/CD examples

---

## Success Criteria Validation

The implementation will be complete when all success criteria from spec are met:

### Quality & Build (P0 Requirements)
- ✅ SC-013: All quality checks pass with zero violations
- ✅ SC-014: Maven build <2 minutes with all gates passing
- ✅ SC-015: Frontend build <1 minute with all linting passing
- ✅ SC-016: New developer setup <10 minutes
- ✅ SC-017: Pre-commit hooks 100% effective

### Performance (P5 Requirements)
- ✅ SC-002: FCP <1.5s on 3G
- ✅ SC-003: Bundle <100KB gzipped
- ✅ SC-004: Island hydration <200ms each
- ✅ SC-007: API latency <500ms p95

### Functionality (P1-P4 Requirements)
- ✅ SC-001: Task creation <3 seconds
- ✅ SC-005: 90% success rate for 10 common tasks
- ✅ SC-006: 100% preference persistence
- ✅ SC-008: 50 concurrent users without degradation

### Educational Value (P5 Requirements)
- ✅ SC-009: Performance page shows 3x faster load vs SPA
- ✅ SC-010: WCAG AA accessibility
- ✅ SC-011: All 4 state patterns demonstrated
- ✅ SC-012: 100% architecture documentation coverage

---

## Next Steps

1. **Execute Phase 0**: Run research tasks to populate `research.md`
2. **Execute Phase 1**: Create data model, contracts, and quickstart guide
3. **Run `/speckit.tasks`**: Generate actionable task list from this plan
4. **Run `/speckit.implement`**: Execute task list with quality gates enforced

**Planning Complete**: This plan provides the foundation for implementation. All unknowns will be resolved in Phase 0 research. Phase 1 design will provide concrete artifacts for task generation.
