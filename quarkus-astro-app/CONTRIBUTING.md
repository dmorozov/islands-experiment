# Contributing to Task Manager

Thank you for your interest in contributing to the Task Manager project! This document provides guidelines and instructions for contributing.

## Table of Contents

- [Code of Conduct](#code-of-conduct)
- [Getting Started](#getting-started)
- [Development Workflow](#development-workflow)
- [Coding Standards](#coding-standards)
- [Testing Guidelines](#testing-guidelines)
- [Pull Request Process](#pull-request-process)
- [Project Structure](#project-structure)

## Code of Conduct

This project follows a simple code of conduct:
- Be respectful and professional
- Provide constructive feedback
- Focus on what is best for the community
- Show empathy towards other community members

## Getting Started

### Prerequisites

- Java 21 or higher
- Maven 3.9 or higher
- Node.js 20.17 or higher
- Git

### Initial Setup

1. **Fork and Clone**
   ```bash
   git clone https://github.com/your-username/quarkus-astro-app.git
   cd quarkus-astro-app
   ```

2. **Install Dependencies**
   ```bash
   # Install Maven dependencies (from project root)
   ./mvnw clean install

   # Install frontend dependencies
   cd quarkus-astro-app/src/main/webui
   npm install
   ```

3. **Verify Setup**
   ```bash
   # Run quality checks
   ./mvnw validate

   # Run frontend linting
   cd quarkus-astro-app/src/main/webui
   npm run lint
   ```

4. **Start Development Server**
   ```bash
   # From project root
   ./mvnw -pl quarkus-astro-app quarkus:dev

   # Application runs on http://localhost:7171
   ```

## Development Workflow

### Branch Strategy

- `main` - Production-ready code
- `develop` - Integration branch for features
- `feature/*` - New features
- `bugfix/*` - Bug fixes
- `hotfix/*` - Urgent production fixes

### Making Changes

1. **Create a Branch**
   ```bash
   git checkout -b feature/your-feature-name
   ```

2. **Make Your Changes**
   - Follow the [Coding Standards](#coding-standards)
   - Write tests for new functionality
   - Update documentation as needed

3. **Test Your Changes**
   ```bash
   # Backend tests
   ./mvnw test

   # Frontend tests
   cd quarkus-astro-app/src/main/webui
   npm test

   # Quality checks
   ./mvnw validate
   npm run lint
   ```

4. **Commit Your Changes**
   ```bash
   git add .
   git commit -m "feat: add new feature"
   ```

   **Commit Message Format**:
   - `feat:` - New feature
   - `fix:` - Bug fix
   - `docs:` - Documentation only
   - `style:` - Code style changes (formatting)
   - `refactor:` - Code refactoring
   - `test:` - Adding or updating tests
   - `chore:` - Maintenance tasks

5. **Push and Create Pull Request**
   ```bash
   git push origin feature/your-feature-name
   ```

## Coding Standards

### Java Backend

**Style Guide**: This project uses Checkstyle and PMD to enforce code quality.

**Key Rules**:
- Use final parameters: `public void method(final String param)`
- Use private fields with getters/setters
- No magic numbers - use constants
- Comprehensive Javadoc for public APIs
- Maximum line length: 120 characters
- Use `@Transactional` for database operations
- Validate inputs with Jakarta Bean Validation

**Example**:
```java
/**
 * Creates a new task for the specified user.
 *
 * @param userId the user ID
 * @param dto the task creation data
 * @return the created task
 * @throws ValidationException if validation fails
 */
@Transactional
public TaskResponseDTO createTask(final String userId, final TaskCreateDTO dto) {
    // Implementation
}
```

### Frontend (TypeScript/Astro)

**Style Guide**: ESLint + Prettier (Airbnb TypeScript config)

**Key Rules**:
- Use TypeScript for all `.ts` and `.tsx` files
- Prefer `const` over `let`
- Use arrow functions for callbacks
- Destructure props in components
- Add prop types for all components
- Use meaningful variable names

**Astro Components**:
```astro
---
interface Props {
  title: string;
  description?: string;
}

const { title, description = 'Default description' } = Astro.props;
---

<div>
  <h1>{title}</h1>
  {description && <p>{description}</p>}
</div>
```

**Preact Islands**:
```typescript
interface TaskListProps {
  initialTasks?: Task[];
}

export default function TaskList({ initialTasks = [] }: TaskListProps) {
  // Implementation
}
```

### CSS/Styling

- Use Tailwind utility classes
- Prefer Shadcn/UI components
- Use theme variables from `globals.css`
- Mobile-first responsive design

**Example**:
```tsx
<div className="flex flex-col gap-4 md:flex-row md:gap-6">
  <Button variant="outline" size="sm">
    Click me
  </Button>
</div>
```

## Testing Guidelines

### Backend Testing

**Contract Tests** (Priority):
```java
@QuarkusTest
class TaskResourceTest {
    @Test
    void testCreateTask() {
        given()
            .contentType(MediaType.APPLICATION_JSON)
            .body(new TaskCreateDTO("Title", "Description", categoryId, Priority.HIGH))
        .when()
            .post("/api/tasks")
        .then()
            .statusCode(201)
            .body("title", equalTo("Title"));
    }
}
```

**Service Tests**:
```java
@QuarkusTest
class TaskServiceTest {
    @Inject
    TaskService taskService;

    @Test
    @Transactional
    void testCreateTask() {
        TaskResponseDTO task = taskService.createTask("user1", dto);
        assertNotNull(task.id());
        assertEquals("Title", task.title());
    }
}
```

### Frontend Testing

**Component Tests** (Vitest + Testing Library):
```typescript
import { render, screen } from '@testing-library/preact';
import { describe, it, expect } from 'vitest';
import TaskList from './TaskList';

describe('TaskList', () => {
  it('renders empty state', () => {
    render(<TaskList />);
    expect(screen.getByText('No tasks found')).toBeInTheDocument();
  });
});
```

### Test Coverage

- Aim for >70% code coverage
- All new features must include tests
- Contract tests for all API endpoints
- Unit tests for business logic
- Integration tests for critical paths

## Pull Request Process

### Before Submitting

1. **Ensure all tests pass**
   ```bash
   ./mvnw verify
   npm test
   ```

2. **Ensure zero quality violations**
   ```bash
   ./mvnw validate
   npm run lint
   npm run format:check
   ```

3. **Update documentation**
   - Update README.md if needed
   - Add/update API documentation
   - Update ARCHITECTURE.md for architectural changes

4. **Rebase on latest main**
   ```bash
   git fetch origin
   git rebase origin/main
   ```

### PR Template

```markdown
## Description
Brief description of changes

## Type of Change
- [ ] Bug fix
- [ ] New feature
- [ ] Breaking change
- [ ] Documentation update

## Testing
- [ ] Added contract tests
- [ ] Added unit tests
- [ ] Manual testing completed

## Checklist
- [ ] Code follows style guidelines
- [ ] Self-reviewed code
- [ ] Commented hard-to-understand areas
- [ ] Updated documentation
- [ ] No new warnings
- [ ] Added tests
- [ ] All tests pass
- [ ] Dependent changes merged
```

### Review Process

1. **Automated Checks**
   - CI pipeline runs automatically
   - Must pass all quality checks
   - Test coverage must not decrease

2. **Code Review**
   - At least one approval required
   - Address all review comments
   - Re-request review after changes

3. **Merge**
   - Squash commits on merge
   - Delete branch after merge

## Project Structure

See [ARCHITECTURE.md](ARCHITECTURE.md) for detailed architecture documentation.

### Adding a New Feature

1. **Backend**:
   - Create entity in `model/`
   - Create DTOs in `dto/`
   - Create repository in `repository/`
   - Create service in `service/`
   - Create resource in `resource/`
   - Add contract tests in `src/test/`

2. **Frontend**:
   - Create Astro page in `pages/`
   - Create Preact island in `islands/`
   - Update state management in `lib/state.ts`
   - Generate API client: `npm run generate:api`

3. **Documentation**:
   - Update README.md
   - Update ARCHITECTURE.md if architectural changes
   - Add inline comments for complex logic

### Common Tasks

**Add a new Shadcn component**:
```bash
cd quarkus-astro-app/src/main/webui
npx shadcn-ui@latest add [component-name]
```

**Regenerate API client**:
```bash
# Start backend
./mvnw -pl quarkus-astro-app quarkus:dev

# In another terminal
cd quarkus-astro-app/src/main/webui
npm run generate:api
```

**Run production build**:
```bash
./mvnw clean package -DskipTests
java -jar quarkus-astro-app/target/quarkus-app/quarkus-run.jar
```

## Style Guide Resources

- **Java**: [Google Java Style Guide](https://google.github.io/styleguide/javaguide.html)
- **TypeScript**: [Airbnb TypeScript Style Guide](https://github.com/airbnb/javascript)
- **Astro**: [Astro Documentation](https://docs.astro.build/)
- **Tailwind**: [Tailwind Best Practices](https://tailwindcss.com/docs/utility-first)

## Need Help?

- Check existing [Issues](https://github.com/your-repo/issues)
- Review [ARCHITECTURE.md](ARCHITECTURE.md)
- Ask questions in discussions
- Contact maintainers

## License

By contributing, you agree that your contributions will be licensed under the same license as the project (see [LICENSE](LICENSE)).

---

Thank you for contributing to Task Manager! 🎉
