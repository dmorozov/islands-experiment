# Quick Reference - Task Manager Project

**Last Updated**: 2025-11-22
**Current Phase**: Phase 3 Complete → Ready for Phase 4
**Progress**: 145/694 tasks (21%)

---

## 🚀 Quick Start Commands

```bash
# Terminal 1: Backend (from /workspaces/quarkus-astro-app)
./mvnw quarkus:dev
# → http://localhost:7171 (API)
# → http://localhost:7171/swagger-ui (Swagger)

# Terminal 2: Frontend (from /workspaces/quarkus-astro-app/src/main/webui)
npm run dev
# → http://localhost:3000 (Astro dev server)
```

---

## 📁 Key File Locations

```
/workspaces/
├── SESSION_MEMORY.md              ← Full session details
├── specs/001-task-manager-app/
│   └── tasks.md                   ← Master task list (START HERE)
├── .husky/pre-commit              ← Pre-commit hooks
└── quarkus-astro-app/
    ├── src/main/resources/
    │   └── application.properties ← Quarkus config (Phase 3 ✅)
    ├── src/main/java/org/acme/taskmanager/
    │   ├── model/Priority.java    ← HIGH, MEDIUM, LOW
    │   ├── dto/ErrorDTO.java      ← API error format
    │   ├── exception/             ← 4 exception classes + mapper
    │   └── session/SessionUtils.java ← Session management
    └── src/main/webui/
        ├── package.json           ← NPM scripts
        ├── eslint.config.js       ← Flat config (ESLint 9)
        ├── orval.config.ts        ← API client generator
        └── src/
            ├── styles/globals.css ← Tailwind + theme vars
            ├── lib/utils.ts       ← cn() utility
            └── api/mutator.ts     ← Axios instance
```

---

## ✅ What's Done

**Phase 1**: Project structure ✅
**Phase 2**: Frontend quality tooling (ESLint, Prettier, Husky, TypeScript, Tailwind v4, Shadcn/ui, Orval, Vitest) ✅ COMMITTED
**Phase 3**: Backend infrastructure (config, session, exceptions, DTOs) ✅ NOT COMMITTED

---

## 🎯 Next Steps

**Start Phase 4**: User Story 1 - View and Navigate Tasks

1. **T146-T151**: Write contract tests for `GET /api/tasks` with filters
2. **T152-T163**: Create JPA entities (Category, Task)
3. **T164-T169**: Create response DTOs
4. **T170+**: Repositories, services, REST endpoints
5. **Frontend**: Astro pages + Preact islands

---

## 🔧 Common Tasks

### Lint & Format
```bash
cd src/main/webui
npm run format      # Auto-fix formatting
npm run lint:fix    # Auto-fix ESLint
npm run test        # Run Vitest
```

### Generate API Client
```bash
# 1. Start Quarkus (generates OpenAPI schema)
cd quarkus-astro-app
./mvnw quarkus:dev

# 2. Generate TypeScript client
cd src/main/webui
npm run generate:api
```

### Git Workflow
```bash
# Check what's changed
git status

# Phase 3 uncommitted files:
# - application.properties (modified)
# - tasks.md (modified)
# - 7 new Java files (untracked)
```

---

## 📦 Tech Stack

**Backend**: Quarkus 3.29.4 + H2 Database
**Frontend**: Astro 5.x + Preact + TypeScript strict
**Styling**: Tailwind CSS v4 + Shadcn/ui
**Data Fetching**: TanStack Query v5 + Orval + Axios
**Quality**: ESLint 9 + Prettier + Husky + Vitest
**Backend QA**: Checkstyle + PMD + JUnit 5

---

## ⚠️ Important Notes

1. **NO auto-commit**: User reviews all changes manually
2. **ESLint 9**: Airbnb rules manually configured (packages incompatible)
3. **Husky location**: Must be at `/workspaces/.husky/` (repo root)
4. **SessionUtils**: Uses `RoutingContext` (Vert.x), not `HttpSession`
5. **H2 Database**: In-memory, data lost on restart (dev only)

---

## 🔗 Full Documentation

See `/workspaces/SESSION_MEMORY.md` for complete session details including:
- File structure
- Configuration details
- Code patterns
- Architecture decisions
- Known issues
- Setup checklist

---

## 📞 Port Reference

- **7171**: Quarkus backend + API
- **3000**: Astro dev server (frontend)

---

**Current Git Branch**: `001-task-manager-app`
**Last Commit**: `2f05d4e - all dependencies and quality tooling`
