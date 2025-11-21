# Quick Start Guide: Task Manager Application

**Target**: Get a new developer from clone to running application in <10 minutes

**Date**: 2025-11-21
**Related**: [plan.md](./plan.md), [research.md](./research.md)

---

## Prerequisites Check

**Required Software** (check first):

```bash
# Java 21
java -version
# Should show: openjdk version "21.x.x"

# Maven 3.9+
mvn -version
# Should show: Apache Maven 3.9.x or higher

# Node.js 20.17.0+ (optional, Quinoa manages this)
node -version
# Should show: v20.17.0 or higher

# Git
git --version
```

**If missing**:
- Java 21: https://adoptium.net/
- Maven: https://maven.apache.org/download.cgi
- Node.js: https://nodejs.org/ (LTS version)
- Git: https://git-scm.com/

---

## Quick Start (3 Commands, ~3 minutes)

### Step 1: Clone & Navigate
```bash
git clone <repository-url>
cd islands-architecture-experiment/quarkus-astro-app
```

### Step 2: Install Frontend Dependencies & Hooks
```bash
cd src/main/webui
npm install
# This installs: Astro, Preact, ESLint, Prettier, Husky, Orval, Tailwind, etc.
# Husky pre-commit hooks are automatically set up

cd ../../..  # Return to quarkus-astro-app root
```

### Step 3: Start Development Server
```bash
quarkus dev
```

**Expected Output**:
```
Listening on: http://localhost:7171
--
Tests paused
Press [e] to edit command line args (currently ''), [r] to resume testing, [o] Toggle test output, [:] for the terminal, [h] for more options>
```

**Access Application**:
- Main app: http://localhost:7171
- Swagger UI: http://localhost:7171/swagger-ui
- H2 Console: http://localhost:7171/h2-console (if enabled)

**Done!** You now have a fully running development environment with hot reload.

---

## Verification Checklist (~5 minutes)

Run these commands to verify everything is configured correctly:

### ✅ 1. Backend Build & Quality Checks
```bash
./mvnw clean package
```

**Expected**:
- Build SUCCESS
- 0 Checkstyle violations
- 0 PMD violations
- All tests pass (if any exist)
- Time: ~30-60 seconds

**If failures**: Check `build-tools/src/main/resources/product/` for Checkstyle/PMD configs

### ✅ 2. Frontend Build & Linting
```bash
cd src/main/webui
npm run build
```

**Expected**:
- Build SUCCESS
- 0 ESLint errors
- Code formatted by Prettier
- Optimized bundles in `dist/`
- Time: ~10-20 seconds

**If failures**: Check `eslint.config.js` and `.prettierrc.json`

### ✅ 3. Pre-commit Hooks Work
```bash
# Make a deliberate formatting violation
echo "const x=1" >> src/pages/test.ts

git add src/pages/test.ts
git commit -m "test commit"
```

**Expected**:
- Prettier auto-formats the file
- ESLint checks the file
- Commit succeeds with properly formatted code

**Cleanup**:
```bash
git reset HEAD~1  # Undo test commit
rm src/pages/test.ts
```

### ✅ 4. Hot Reload Works

**Test Backend Hot Reload**:
1. With `quarkus dev` running, open `src/main/java/org/acme/GreetingResource.java` (or any Java file)
2. Make a change (e.g., modify a string)
3. Save the file
4. Check terminal: "Hot replace total time: XXXms"
5. Refresh browser: Change visible

**Test Frontend Hot Reload (HMR)**:
1. With `quarkus dev` running, open `src/main/webui/src/pages/index.astro`
2. Make a change (e.g., modify text)
3. Save the file
4. Browser automatically refreshes (no manual refresh needed)

### ✅ 5. API Documentation Accessible
```bash
# Open in browser
open http://localhost:7171/swagger-ui
```

**Expected**:
- Swagger UI loads
- Shows "Task Manager API" or similar
- Lists all endpoints (if implemented)

---

## Development Workflow

### Daily Development

**Start Developing**:
```bash
cd quarkus-astro-app
quarkus dev
```

**Edit Backend**:
- Files: `src/main/java/org/acme/**/*.java`
- Hot reload: Automatic on save
- View logs: Terminal where `quarkus dev` is running

**Edit Frontend**:
- Files: `src/main/webui/src/**/*`
- Hot reload (HMR): Automatic on save
- View in browser: http://localhost:7171

**Linting**:
- Auto-runs on commit via pre-commit hooks
- Manual check: `npm run lint` (in webui directory)
- Manual fix: `npm run lint:fix`

### When API Changes

**After modifying REST endpoints**:
```bash
# 1. Quarkus auto-regenerates OpenAPI schema to src/main/webui/api/openapi.json
# 2. Regenerate TypeScript client
cd src/main/webui
npm run generate:api

# 3. Use new types in frontend
# import { useGetTasks } from '@/lib/api'
```

### Running Tests

**Backend Tests**:
```bash
./mvnw test                  # Unit tests only
./mvnw verify                # Unit + integration tests
```

**Frontend Tests** (when implemented):
```bash
cd src/main/webui
npm test                     # Run Vitest
npm run test:coverage        # With coverage report
```

### Building for Production

**Full Production Build**:
```bash
./mvnw clean package -DskipTests
```

**Output**:
- JAR: `target/quarkus-astro-app-1.0.0-SNAPSHOT.jar`
- Includes compiled frontend in `META-INF/resources/`

**Run Production JAR**:
```bash
java -jar target/quarkus-app/quarkus-run.jar
```

---

## Troubleshooting

### Issue: `mvn` command not found
**Solution**: Install Maven or use included wrapper: `./mvnw` instead of `mvn`

### Issue: Checkstyle violations on fresh clone
**Cause**: Checkstyle/PMD configs in `build-tools/` may be strict
**Solution**: Check specific violations in build output, fix code or justify suppression

### Issue: ESLint errors on frontend build
**Cause**: ESLint config may be strict (Airbnb style)
**Solution**:
```bash
cd src/main/webui
npm run lint:fix  # Auto-fix what's possible
npm run lint      # Check remaining errors
```

### Issue: Pre-commit hooks not running
**Solution**:
```bash
cd src/main/webui
npx husky install  # Reinstall hooks
npm run prepare    # Run prepare script
```

### Issue: Port 7171 already in use
**Solution**: Kill existing process or change port in `application.properties`:
```properties
quarkus.http.port=8080  # Or another available port
```

### Issue: Frontend build slow or fails
**Cause**: Missing dependencies or cache issues
**Solution**:
```bash
cd src/main/webui
rm -rf node_modules package-lock.json
npm install
npm run build
```

### Issue: Orval client generation fails
**Cause**: OpenAPI schema not generated yet or malformed
**Solution**:
1. Ensure Quarkus is running: `quarkus dev`
2. Check schema exists: `ls src/main/webui/api/openapi.json`
3. Validate schema: https://editor.swagger.io/ (paste content)
4. Run Orval: `npm run generate:api`

---

## Project Structure Quick Reference

```
quarkus-astro-app/
├── src/main/
│   ├── java/org/acme/taskmanager/    # Backend code
│   │   ├── model/                     # JPA entities
│   │   ├── resource/                  # REST endpoints
│   │   ├── service/                   # Business logic
│   │   └── dto/                       # Data Transfer Objects
│   ├── resources/
│   │   └── application.properties     # Quarkus config
│   └── webui/                         # Frontend (Astro)
│       ├── src/
│       │   ├── pages/                 # Astro pages (routes)
│       │   ├── components/            # Static Astro components
│       │   ├── islands/               # Interactive Preact islands
│       │   ├── lib/                   # Utilities & API client
│       │   └── styles/                # Tailwind CSS
│       ├── public/                    # Static assets
│       └── api/                       # OpenAPI schema (generated)
└── src/test/java/                     # Backend tests
```

---

## Key Commands Reference

| Task | Command | Location |
|------|---------|----------|
| Start dev server | `quarkus dev` | `quarkus-astro-app/` |
| Build backend | `./mvnw clean package` | `quarkus-astro-app/` |
| Build frontend | `npm run build` | `src/main/webui/` |
| Run tests (backend) | `./mvnw test` | `quarkus-astro-app/` |
| Run tests (frontend) | `npm test` | `src/main/webui/` |
| Lint frontend | `npm run lint` | `src/main/webui/` |
| Fix linting | `npm run lint:fix` | `src/main/webui/` |
| Format code | `npm run format` | `src/main/webui/` |
| Generate API client | `npm run generate:api` | `src/main/webui/` |
| Open Swagger UI | Visit http://localhost:7171/swagger-ui | Browser |

---

## Next Steps

After verifying the setup:

1. **Read Architecture Docs**:
   - `/workspaces/CLAUDE.md` - Development guidance
   - `/specs/001-task-manager-app/plan.md` - Implementation plan
   - `/specs/001-task-manager-app/data-model.md` - Entity design

2. **Explore Configuration**:
   - `/workspaces/CONFIGURATION_TEMPLATES.md` - All config files
   - `/workspaces/BEST_PRACTICES_PATTERNS.md` - Production patterns

3. **Start Implementing**:
   - Run `/speckit.tasks` to generate task list
   - Run `/speckit.implement` to execute tasks

4. **Join Team Workflow**:
   - Create feature branch: `git checkout -b feature/your-feature`
   - Make changes with hot reload
   - Commit (pre-commit hooks run automatically)
   - Push and create PR

---

## Success Criteria

Your environment is ready when:

- ✅ `quarkus dev` starts without errors
- ✅ http://localhost:7171 loads successfully
- ✅ `./mvnw clean package` passes with 0 violations
- ✅ `npm run build` passes with 0 linting errors
- ✅ Pre-commit hooks block commits with violations
- ✅ Hot reload works for both backend and frontend
- ✅ Swagger UI displays API documentation

**Estimated Setup Time**: 5-8 minutes (with good internet connection)

**Support**:
- Issues: Check `/specs/001-task-manager-app/research.md` for detailed configuration info
- Patterns: See `/workspaces/BEST_PRACTICES_PATTERNS.md`
- Detailed guides: `/workspaces/LINTING_FORMATTING_GUIDE.md`
