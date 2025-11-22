# Task Manager - Islands Architecture Demo

A production-ready demonstration of Islands Architecture using Quarkus (backend) and Astro + Preact (frontend). This project showcases best practices for building modern web applications with minimal JavaScript and optimal performance.

## 📋 Table of Contents

- [Prerequisites](#prerequisites)
- [Quick Start](#quick-start)
- [Architecture](#architecture)
- [Development Workflow](#development-workflow)
- [Quality Tools](#quality-tools)
- [Testing](#testing)
- [API Documentation](#api-documentation)
- [Troubleshooting](#troubleshooting)
- [Contributing](#contributing)

## 🎯 Prerequisites

**Required Software**:
- **Java 21** - [Download](https://adoptium.net/)
- **Maven 3.9+** - [Download](https://maven.apache.org/download.cgi)
- **Node.js 20.17.0+** - [Download](https://nodejs.org/) (LTS version)
- **Git** - [Download](https://git-scm.com/)

**Verify Installation**:
```bash
java -version    # Should show openjdk version "21.x.x"
mvn -version     # Should show Apache Maven 3.9.x or higher
node -version    # Should show v20.17.0 or higher
git --version
```

## 🚀 Quick Start

Get up and running in <10 minutes:

### 1. Clone and Navigate
```bash
git clone <repository-url>
cd quarkus-astro-app
```

### 2. Install Frontend Dependencies
```bash
cd src/main/webui
npm install
cd ../../..
```

### 3. Start Development Server
```bash
quarkus dev
```

**Expected Output**:
```
Listening on: http://localhost:7171
```

**Access Points**:
- Main application: http://localhost:7171
- Swagger UI (API docs): http://localhost:7171/swagger-ui
- H2 Console (database): http://localhost:7171/h2-console

✅ **Done!** You now have a fully running development environment with hot reload.

## 🏗️ Architecture

### Islands Architecture

This project demonstrates **Islands Architecture**, a modern frontend pattern that:
- Renders pages as **static HTML by default** (zero JavaScript)
- Adds **interactive "islands"** only where needed
- Results in **faster page loads** and **better performance**

**Technology Stack**:

**Backend (Java)**:
- Quarkus 3.29.4 - Supersonic, subatomic Java framework
- H2 Database - In-memory database for development
- SmallRye OpenAPI - API documentation generation
- Quinoa - Frontend integration for Quarkus

**Frontend (TypeScript)**:
- Astro 5.x - Static site generator with Islands Architecture
- Preact - Lightweight React alternative (3KB) for interactive islands
- Tailwind CSS v4 - Utility-first CSS framework
- Shadcn/ui - Accessible component library
- TanStack Query v5 - Data fetching and caching
- Nano Stores - Tiny state management (<1KB)

**Code Quality**:
- ESLint (flat config) with TypeScript support
- Prettier with Astro and Tailwind plugins
- Husky + lint-staged for pre-commit hooks
- Checkstyle and PMD for Java code quality

### State Management Patterns

This project demonstrates **4 state management patterns**:

1. **Server Sessions** - User authentication (Quarkus HttpSession)
2. **Client Storage** - User preferences (localStorage)
3. **Island Communication** - Shared state between islands (Nano Stores)
4. **Cross-Page State** - Persistent state across navigation (Persistent Nano Stores)

## 💻 Development Workflow

### Daily Development

**Start Developing**:
```bash
quarkus dev
```

This starts:
- Backend server on http://localhost:7171
- Frontend dev server (Astro) integrated via Quinoa
- Hot reload for both backend and frontend

**Edit Backend** (`src/main/java/org/acme/taskmanager/`):
- Modify Java files
- Save → Quarkus hot-reloads automatically
- View logs in terminal

**Edit Frontend** (`src/main/webui/src/`):
- Modify `.astro`, `.tsx`, or CSS files
- Save → Browser refreshes automatically (HMR)

**Linting and Formatting**:
```bash
cd src/main/webui

# Check code quality
npm run lint

# Auto-fix linting issues
npm run lint:fix

# Format code
npm run format

# Check formatting without changes
npm run format:check
```

### When API Changes

After modifying REST endpoints:

1. Quarkus auto-regenerates OpenAPI schema → `src/main/webui/api/openapi.json`
2. Regenerate TypeScript client:
   ```bash
   cd src/main/webui
   npm run generate:api
   ```
3. Use new types in frontend:
   ```typescript
   import { useGetTasks } from '@/lib/api';
   ```

### Running Tests

**Backend Tests**:
```bash
./mvnw test                  # Unit tests only
./mvnw verify                # Unit + integration tests
```

**Frontend Tests**:
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
- JAR: `target/quarkus-app/quarkus-run.jar`
- Includes compiled frontend in `META-INF/resources/`

**Run Production JAR**:
```bash
java -jar target/quarkus-app/quarkus-run.jar
```

## 🔧 Quality Tools

This project enforces **zero-tolerance quality standards** with automated tooling.

### Backend Quality (Java)

**Checkstyle** - Code style enforcement:
```bash
./mvnw checkstyle:check
```

**PMD** - Static code analysis:
```bash
./mvnw pmd:check
```

**Configuration**: See `build-tools/src/main/resources/product/`

### Frontend Quality (TypeScript)

**ESLint** - Code quality and best practices:
```bash
cd src/main/webui
npm run lint
```

**Prettier** - Code formatting:
```bash
npm run format:check
```

**TypeScript** - Strict type checking:
```bash
npx tsc --noEmit
```

### Pre-commit Hooks

**Husky + lint-staged** automatically runs quality checks before every commit:
- Prettier formats staged files
- ESLint fixes auto-fixable issues
- TypeScript type-checks
- **Commits are blocked** if violations remain

**Test Pre-commit Hooks**:
```bash
# Create file with bad formatting
echo "const x=1" >> src/main/webui/src/test.ts

# Try to commit
git add src/main/webui/src/test.ts
git commit -m "test"

# Prettier auto-fixes → commit succeeds
```

## 🧪 Testing

### Backend Testing Strategy

**JUnit 5 + REST Assured** for contract tests:

```java
@QuarkusTest
class TaskResourceTest {
    @Test
    void testCreateTask() {
        given()
            .contentType(ContentType.JSON)
            .body(new TaskCreateDTO("Buy milk", uuid, "HIGH"))
        .when()
            .post("/api/tasks")
        .then()
            .statusCode(201)
            .body("title", equalTo("Buy milk"));
    }
}
```

**Coverage Targets**:
- Backend services: >70%
- REST endpoints: Contract tests for all

### Frontend Testing Strategy

**Vitest** for unit and component tests:

```typescript
import { render } from '@testing-library/preact';
import { TaskList } from './TaskList';

describe('TaskList', () => {
  it('renders tasks correctly', () => {
    const { getByText } = render(<TaskList tasks={mockTasks} />);
    expect(getByText('Buy milk')).toBeInTheDocument();
  });
});
```

**Coverage Targets**:
- Interactive islands: >50%
- Utility functions: >80%

## 📚 API Documentation

### Swagger UI

Interactive API documentation at: **http://localhost:7171/swagger-ui**

Features:
- Try API endpoints directly from browser
- View request/response schemas
- See validation rules

### OpenAPI Schema

Machine-readable schema at: `src/main/webui/api/openapi.json`

**Workflow**:
1. Quarkus generates schema from JAX-RS annotations
2. Orval generates TypeScript client from schema
3. Frontend uses type-safe API hooks

## 🔍 Troubleshooting

### Issue: `mvn` command not found
**Solution**: Install Maven or use included wrapper: `./mvnw` instead of `mvn`

### Issue: Checkstyle/PMD violations on fresh clone
**Cause**: Strict code quality rules
**Solution**: Check specific violations in build output, fix code or justify suppression

### Issue: ESLint errors on frontend build
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
```

### Issue: Port 7171 already in use
**Solution**: Kill existing process or change port in `application.properties`:
```properties
quarkus.http.port=8080
```

### Issue: Frontend build slow or fails
**Solution**:
```bash
cd src/main/webui
rm -rf node_modules package-lock.json
npm install
npm run build
```

### Issue: Orval client generation fails
**Solution**:
1. Ensure Quarkus is running: `quarkus dev`
2. Check schema exists: `ls src/main/webui/api/openapi.json`
3. Validate schema: https://editor.swagger.io/ (paste content)
4. Run Orval: `npm run generate:api`

## 🤝 Contributing

### How to Add Features

1. **Create feature branch**:
   ```bash
   git checkout -b feature/your-feature
   ```

2. **Develop with hot reload**:
   - Backend: Modify Java files → Quarkus hot-reloads
   - Frontend: Modify `.astro`/`.tsx` files → Browser updates

3. **Run quality checks**:
   ```bash
   # Backend
   ./mvnw clean verify

   # Frontend
   cd src/main/webui
   npm run lint
   npm run format:check
   npm test
   ```

4. **Commit changes**:
   ```bash
   git add .
   git commit -m "Add: your feature description"
   # Pre-commit hooks run automatically
   ```

5. **Push and create PR**:
   ```bash
   git push origin feature/your-feature
   ```

### Commit Guidelines

- **Format**: `<Type>: <Description>`
- **Types**: Add, Update, Fix, Refactor, Test, Docs
- **Example**: `Add: task completion statistics endpoint`

### Code Quality Standards

**All code must**:
- Pass Checkstyle/PMD (Java)
- Pass ESLint (TypeScript)
- Be formatted with Prettier
- Have no TypeScript errors
- Pass all tests

Pre-commit hooks enforce these automatically.

---

## 📦 Project Structure

```
quarkus-astro-app/
├── src/main/
│   ├── java/org/acme/taskmanager/    # Backend code
│   │   ├── model/                     # JPA entities
│   │   ├── resource/                  # REST endpoints
│   │   ├── service/                   # Business logic
│   │   ├── dto/                       # Data Transfer Objects
│   │   └── session/                   # Session management
│   ├── resources/
│   │   └── application.properties     # Quarkus config
│   └── webui/                         # Frontend (Astro)
│       ├── src/
│       │   ├── pages/                 # Astro pages (routes)
│       │   ├── components/            # Static Astro components
│       │   ├── islands/               # Interactive Preact islands
│       │   ├── lib/                   # Utilities & API client
│       │   ├── styles/                # Tailwind CSS
│       │   └── api/                   # API mutator (Axios)
│       ├── api/                       # OpenAPI schema (generated)
│       ├── astro.config.mjs           # Astro configuration
│       ├── tailwind.config.mjs        # Tailwind configuration
│       ├── tsconfig.json              # TypeScript configuration
│       ├── eslint.config.js           # ESLint configuration
│       ├── .prettierrc.json           # Prettier configuration
│       └── package.json               # Frontend dependencies
└── src/test/java/                     # Backend tests
```

---

## 🎓 Learning Resources

- **Astro Documentation**: https://docs.astro.build
- **Quarkus Guides**: https://quarkus.io/guides/
- **Islands Architecture**: https://www.patterns.dev/vanilla/islands-architecture/
- **Tailwind CSS**: https://tailwindcss.com/docs
- **TanStack Query**: https://tanstack.com/query/latest

---

## 📄 License

[Your License Here]

---

**Built with ❤️ using Islands Architecture**
