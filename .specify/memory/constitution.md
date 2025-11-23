<!--
Sync Impact Report - Constitution Update

Version Change: 0.0.0 → 1.0.0 (Initial Constitution)
Created: 2025-11-21

Principles Established:
- I. Islands Architecture Fidelity
- II. Production-Ready Prototype Quality
- III. API Contract-Driven Development
- IV. State Management Research
- V. Developer Experience & Type Safety
- VI. Test-Conscious Development
- VII. Code Quality Enforcement

Added Sections:
- Core Principles (7 principles)
- Technology Constraints
- Quality Standards
- Governance

Templates Requiring Updates:
- ✅ .specify/templates/plan-template.md - Constitution Check section aligned
- ✅ .specify/templates/spec-template.md - Requirements sections aligned
- ✅ .specify/templates/tasks-template.md - Task categorization aligned

Follow-up TODOs: None - all fields populated
-->

# Islands Architecture Research Project Constitution

## Core Principles

### I. Islands Architecture Fidelity

The project MUST demonstrate authentic Islands Architecture patterns:
- Ship minimal JavaScript: only interactive components ("islands") should hydrate on the client
- Static-first rendering: pages are server-rendered HTML by default
- Component isolation: each interactive island operates independently
- Progressive enhancement: functionality degrades gracefully without JavaScript
- Performance measurement: track bundle sizes, hydration timing, and Time to Interactive (TTI)

**Rationale**: This is a research project specifically exploring Islands Architecture. Every
implementation decision must serve this educational and demonstrative purpose. Violating these
patterns defeats the project's core objective.

### II. Production-Ready Prototype Quality

Code quality MUST meet production standards:
- All code follows language/framework best practices (Google Java Style, Airbnb/TypeScript ESLint)
- Checkstyle and PMD violations fail builds (no warnings ignored without justification)
- Clear separation of concerns: backend (Quarkus), frontend (Astro), UI components (Preact)
- Comprehensive error handling: graceful degradation, user-friendly messages, proper logging
- Security best practices: input validation, CORS configuration, session management
- Documentation at decision points: why this approach, what alternatives were rejected

**Rationale**: This prototype serves as a reference implementation. Low-quality code undermines its
value as a learning resource and template for real projects.

### III. API Contract-Driven Development

Backend-frontend integration MUST follow code-first contract workflow:
- Quarkus REST endpoints define the API contract
- SmallRye OpenAPI auto-generates OpenAPI schema to `src/main/webui/api`
- Orval generates TypeScript client with TanStack Query and Axios from OpenAPI schema
- Frontend code MUST NOT call backend APIs directly; use generated client only
- Breaking API changes require version increment and migration documentation

**Rationale**: Type-safe API integration eliminates entire classes of runtime errors. Generated
clients ensure frontend and backend stay synchronized. This demonstrates modern full-stack
development practices.

### IV. State Management Research (Multi-Pattern Demonstration)

The project MUST implement and compare multiple state management approaches:
- **Server-Side Sessions**: Quarkus session management for authentication and secure data
- **Client-Side State**: Browser storage (localStorage/sessionStorage) for UI preferences
- **Hybrid Patterns**: Demonstrate when to use each approach and how they interoperate
- **Cross-Page Persistence**: User session state accessible across multiple Astro pages
- **Island State Sharing**: State synchronization between independent interactive islands

Each approach MUST include:
- Working implementation with code examples
- Documentation explaining use case, trade-offs, and implementation details
- Performance characteristics (memory usage, network requests, hydration impact)

**Rationale**: State management is complex in Islands Architecture. By researching multiple
patterns, this project provides actionable guidance for real-world decision-making.

### V. Developer Experience & Type Safety

Development workflow MUST prioritize type safety and tooling:
- TypeScript MUST be used for all frontend code (strict mode enabled)
- Quarkus REST endpoints MUST use proper Java types (no raw Map/Object returns)
- Shadcn UI components integrated with Tailwind for consistent, typed UI
- ESLint and Prettier enforce consistent code style
- Hot module replacement (HMR) working in dev mode for both frontend and backend
- Clear error messages: framework errors supplemented with project-specific guidance

**Rationale**: Developer experience directly impacts prototype quality and educational value.
Type safety catches errors at compile time and improves code comprehension.

### VI. Test-Conscious Development

Testing strategy balances rigor with research flexibility:
- **MUST have tests**: API endpoints (contract tests), critical business logic, state management patterns
- **SHOULD have tests**: UI components with complex behavior, integration flows, error handling
- **OPTIONAL tests**: Experimental features, proof-of-concept code, obvious utilities
- Contract tests MUST be written for any API endpoint before frontend integration
- Test coverage targets: >70% for backend services, >50% for interactive islands
- Integration tests MUST cover state persistence across page navigation

**Rationale**: Test-first discipline would slow experimentation. However, untested code cannot
demonstrate production readiness. This balance prioritizes tests where they add most value.

### VII. Code Quality Enforcement (Strict)

Quality gates MUST pass before code is considered complete:
- Checkstyle violations fail Maven builds (configured in parent POM)
- PMD violations fail Maven builds (configured in parent POM)
- ESLint violations fail npm builds (must be configured)
- TypeScript strict mode errors block compilation
- Justification required: If a quality rule must be suppressed, document why in code comments
- Pre-commit hooks (recommended): Run linters and formatters automatically

**Rationale**: Strict enforcement prevents quality degradation over time. Suppressions are allowed
but must be justified, ensuring conscious decisions rather than lazy shortcuts.

## Technology Constraints

### Mandatory Stack

**Backend**:
- Java 21 with Quarkus 3.29.4+
- Quarkus REST (JAX-RS) for API endpoints
- SmallRye OpenAPI for schema generation
- Quarkus WebSockets Next for real-time features (if needed)
- Lombok for boilerplate reduction (configured in parent POM)

**Frontend**:
- Astro 5.x for Islands Architecture framework
- Preact for interactive islands (React-compatible, smaller bundle)
- TypeScript (strict mode) for all .ts/.tsx files
- Tailwind CSS for styling
- Shadcn UI for accessible, typed component library
- TanStack Query + Axios (via Orval) for API communication

**Build & Integration**:
- Quarkus Quinoa (2.7.0+) to integrate Astro within Quarkus
- Maven for Java builds, npm for frontend builds
- Node 20.17.0, npm 10.8.2 (managed by Quinoa)

### Prohibited Patterns

- **NO** direct fetch/axios calls to backend (use generated Orval client)
- **NO** inline styles or CSS-in-JS (use Tailwind utility classes)
- **NO** large client-side frameworks (Vue/Angular) that defeat Islands Architecture purpose
- **NO** bypassing Quinoa (frontend must build through Quarkus integration)
- **NO** disabling TypeScript strict mode or quality checks without documented justification

## Quality Standards

### Performance Targets

- **Page Load**: First Contentful Paint (FCP) <1.5s on 3G connection
- **JavaScript Bundle**: Total JS for homepage <100KB gzipped
- **Island Hydration**: Individual islands hydrate in <200ms
- **API Response Time**: p95 latency <500ms for CRUD operations
- **Build Time**: Full production build <2 minutes

### Documentation Requirements

Each feature MUST include:
- **User-facing docs**: How to use the feature (in application UI or README)
- **Developer docs**: Implementation approach, design decisions, alternative patterns
- **API docs**: Generated OpenAPI schema with endpoint descriptions and examples
- **Code comments**: Complex logic, non-obvious decisions, TODO/FIXME with context

### Accessibility Standards

- Semantic HTML: Proper heading hierarchy, landmarks, form labels
- Keyboard navigation: All interactive elements accessible via keyboard
- ARIA attributes: Where semantic HTML insufficient (accordions, tabs, modals)
- Color contrast: WCAG AA minimum (4.5:1 for normal text)
- Screen reader testing: Verify major flows with NVDA/VoiceOver

## Governance

### Amendment Procedure

1. **Propose Change**: Document what principle/section changes and why
2. **Impact Analysis**: Identify affected code, templates, and documentation
3. **Version Bump**: Determine if MAJOR (breaking), MINOR (additive), or PATCH (clarification)
4. **Update Constitution**: Modify `.specify/memory/constitution.md` with sync impact report
5. **Propagate Changes**: Update templates, docs, and runtime guidance files
6. **Commit**: Include version bump in commit message (e.g., `docs: amend constitution to v2.0.0`)

### Versioning Policy

- **MAJOR** (X.0.0): Principle removed, redefined, or governance change that invalidates prior work
- **MINOR** (x.Y.0): New principle added, existing principle materially expanded
- **PATCH** (x.y.Z): Wording clarification, typo fix, non-semantic refinement

### Compliance Review

- All PRs MUST verify adherence to constitution principles
- Complexity/violations MUST be justified in plan.md Complexity Tracking table
- Quality gate failures MUST be fixed or explicitly suppressed with rationale
- Constitution supersedes all other practices (README, CLAUDE.md, etc.)
- Use CLAUDE.md for runtime development guidance, not governance rules

**Version**: 1.0.0 | **Ratified**: 2025-11-21 | **Last Amended**: 2025-11-21
