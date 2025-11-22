# Code Patterns & Architecture Decisions

This document captures the established patterns and conventions for the Task Manager project. Follow these patterns when implementing new features to maintain consistency.

---

## Backend Patterns (Java/Quarkus)

### 1. Package Structure

```
org.acme.taskmanager/
├── model/          # JPA entities (Task, Category, etc.)
├── dto/            # Data Transfer Objects (request/response)
├── repository/     # Panache repositories
├── service/        # Business logic layer
├── resource/       # JAX-RS REST endpoints
├── exception/      # Custom exceptions + global mapper
└── session/        # Session management utilities
```

**Rule**: Keep separation of concerns. Never call repositories directly from resources - use services.

---

### 2. Entity Pattern (JPA)

```java
@Entity
@Table(
  name = "tasks",
  indexes = {
    @Index(name = "idx_user_id", columnList = "user_id"),
    @Index(name = "idx_category_id", columnList = "category_id")
  }
)
public class Task {

  @Id
  @GeneratedValue
  private UUID id;

  @Column(nullable = false, length = 200)
  private String title;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "category_id")
  private Category category;

  @Enumerated(EnumType.STRING)  // ALWAYS use STRING, never ORDINAL
  @Column(nullable = false)
  private Priority priority;

  @Column(name = "user_id", nullable = false)
  private String userId;  // Session-based, not foreign key

  @CreationTimestamp
  private Instant createdAt;

  @UpdateTimestamp
  private Instant updatedAt;

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof Task)) return false;
    Task task = (Task) o;
    return Objects.equals(id, task.id);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id);
  }
}
```

**Rules**:
- Primary keys: `UUID` (auto-generated)
- User references: `String userId` (from session, not FK)
- Enums: `@Enumerated(EnumType.STRING)` (never ORDINAL)
- Timestamps: `@CreationTimestamp`, `@UpdateTimestamp` (Hibernate annotations)
- Indexes: Add for all foreign keys and frequently queried columns
- Relationships: Use `FetchType.LAZY` by default
- equals/hashCode: Based on `id` only

---

### 3. DTO Pattern (Records)

```java
@RegisterForReflection  // Required for native compilation
@JsonInclude(JsonInclude.Include.NON_NULL)
public record TaskResponseDTO(
  UUID id,
  String title,
  String description,
  CategoryResponseDTO category,
  Priority priority,
  boolean completed,
  Instant completedAt,
  Instant createdAt,
  Instant updatedAt
) {

  /**
   * Converts entity to DTO.
   * Handle null relationships gracefully.
   */
  public static TaskResponseDTO from(Task task) {
    return new TaskResponseDTO(
      task.getId(),
      task.getTitle(),
      task.getDescription(),
      task.getCategory() != null
        ? CategoryResponseDTO.from(task.getCategory())
        : null,
      task.getPriority(),
      task.isCompleted(),
      task.getCompletedAt(),
      task.getCreatedAt(),
      task.getUpdatedAt()
    );
  }
}
```

**Rules**:
- Use Java `record` (immutable, concise)
- Add `@RegisterForReflection` for native compilation
- Add `@JsonInclude(NON_NULL)` to omit null fields from JSON
- Static `from(Entity)` method for entity → DTO conversion
- Never expose entities directly in REST responses
- Handle null relationships gracefully in `from()` method

---

### 4. Repository Pattern (Panache)

```java
@ApplicationScoped
public class TaskRepository implements PanacheRepositoryBase<Task, UUID> {

  /**
   * Find all tasks for a specific user.
   * Always filter by userId to prevent data leaks!
   */
  public List<Task> findByUserId(String userId) {
    return list("userId", userId);
  }

  /**
   * Find tasks with query parameters.
   */
  public List<Task> findByUserIdAndFilters(
      String userId,
      Priority priority,
      Boolean completed
  ) {
    PanacheQuery<Task> query = find("userId = ?1", userId);

    if (priority != null) {
      query = find("userId = ?1 and priority = ?2", userId, priority);
    }

    if (completed != null) {
      query = find("userId = ?1 and completed = ?2", userId, completed);
    }

    return query.list();
  }

  /**
   * Find by ID and verify ownership.
   * Returns null if not found or doesn't belong to user.
   */
  public Task findByIdAndUserId(UUID id, String userId) {
    return find("id = ?1 and userId = ?2", id, userId).firstResult();
  }
}
```

**Rules**:
- Extend `PanacheRepositoryBase<Entity, UUID>`
- Add `@ApplicationScoped` annotation
- **ALWAYS** filter by `userId` to prevent data leaks
- Use descriptive method names: `findByUserIdAndCompleted()`
- Custom queries: Use Panache query syntax
- Document security-critical methods (userId filtering)

---

### 5. Service Pattern

```java
@ApplicationScoped
public class TaskService {

  @Inject
  TaskRepository taskRepository;

  @Inject
  CategoryRepository categoryRepository;

  /**
   * Get all tasks for current user with optional filters.
   */
  public List<TaskResponseDTO> getTasks(
      String userId,
      Priority priority,
      Boolean completed,
      UUID categoryId
  ) {
    // Apply filters
    List<Task> tasks = taskRepository.findByUserIdAndFilters(
      userId, priority, completed, categoryId
    );

    // Convert to DTOs
    return tasks.stream()
      .map(TaskResponseDTO::from)
      .toList();
  }

  /**
   * Create a new task.
   */
  @Transactional
  public TaskResponseDTO createTask(String userId, TaskCreateDTO dto) {
    // Validate category belongs to user
    if (dto.categoryId() != null) {
      Category category = categoryRepository.findByIdAndUserId(
        dto.categoryId(), userId
      );
      if (category == null) {
        throw new ValidationException(
          "Category not found or doesn't belong to user",
          "categoryId"
        );
      }
    }

    // Create entity
    Task task = new Task();
    task.setTitle(dto.title());
    task.setDescription(dto.description());
    task.setPriority(dto.priority() != null ? dto.priority() : Priority.MEDIUM);
    task.setUserId(userId);
    task.setCompleted(false);

    // Persist
    taskRepository.persist(task);

    return TaskResponseDTO.from(task);
  }

  /**
   * Delete task (verify ownership).
   */
  @Transactional
  public void deleteTask(UUID taskId, String userId) {
    Task task = taskRepository.findByIdAndUserId(taskId, userId);
    if (task == null) {
      throw new ResourceNotFoundException("Task not found with ID: " + taskId);
    }
    taskRepository.delete(task);
  }
}
```

**Rules**:
- Add `@ApplicationScoped` annotation
- Inject repositories with `@Inject`
- Add `@Transactional` to methods that modify data
- **ALWAYS** verify userId/ownership before operations
- Throw `ResourceNotFoundException` for missing resources
- Throw `ValidationException` for business rule violations
- Return DTOs, never entities
- Validate cross-entity references (e.g., category belongs to user)

---

### 6. REST Resource Pattern

```java
@Path("/api/tasks")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class TaskResource {

  @Inject
  TaskService taskService;

  @Context
  RoutingContext context;

  /**
   * Get all tasks with optional filters.
   */
  @GET
  public List<TaskResponseDTO> getTasks(
      @QueryParam("priority") Priority priority,
      @QueryParam("completed") Boolean completed,
      @QueryParam("categoryId") UUID categoryId
  ) {
    String userId = SessionUtils.getCurrentUserId(context);
    return taskService.getTasks(userId, priority, completed, categoryId);
  }

  /**
   * Get single task by ID.
   */
  @GET
  @Path("/{id}")
  public TaskResponseDTO getTask(@PathParam("id") UUID id) {
    String userId = SessionUtils.getCurrentUserId(context);
    Task task = taskService.getTask(id, userId);
    if (task == null) {
      throw new ResourceNotFoundException("Task not found with ID: " + id);
    }
    return TaskResponseDTO.from(task);
  }

  /**
   * Create new task.
   */
  @POST
  public Response createTask(@Valid TaskCreateDTO dto) {
    String userId = SessionUtils.getCurrentUserId(context);
    TaskResponseDTO created = taskService.createTask(userId, dto);
    return Response.status(Response.Status.CREATED).entity(created).build();
  }

  /**
   * Update existing task.
   */
  @PUT
  @Path("/{id}")
  public TaskResponseDTO updateTask(
      @PathParam("id") UUID id,
      @Valid TaskUpdateDTO dto
  ) {
    String userId = SessionUtils.getCurrentUserId(context);
    return taskService.updateTask(id, userId, dto);
  }

  /**
   * Delete task.
   */
  @DELETE
  @Path("/{id}")
  public Response deleteTask(@PathParam("id") UUID id) {
    String userId = SessionUtils.getCurrentUserId(context);
    taskService.deleteTask(id, userId);
    return Response.noContent().build();
  }
}
```

**Rules**:
- Add `@Path("/api/{resource}")` (always prefix with `/api`)
- Add `@Produces(MediaType.APPLICATION_JSON)`
- Add `@Consumes(MediaType.APPLICATION_JSON)`
- Inject `RoutingContext` with `@Context` (for session access)
- **ALWAYS** get userId from session: `SessionUtils.getCurrentUserId(context)`
- Pass userId to service layer
- Use `@Valid` for DTO validation
- Return proper HTTP status codes:
  - 200 OK for GET/PUT
  - 201 Created for POST
  - 204 No Content for DELETE
  - 404 Not Found (via exception)
  - 400 Bad Request (via exception)
- Let `GlobalExceptionMapper` handle exceptions
- Add JavaDoc comments for OpenAPI generation

---

### 7. Exception Handling Pattern

```java
// In service or resource class

// Resource not found
throw new ResourceNotFoundException("Task not found with ID: " + id);
// → HTTP 404 with ErrorDTO

// Business validation error
throw new ValidationException("Cannot delete default category");
// → HTTP 400 with ErrorDTO

// Field-specific validation error
throw new ValidationException("Category name already exists", "name");
// → HTTP 400 with ErrorDTO (includes field)

// Authentication error
throw new UnauthorizedException("No user in session");
// → HTTP 401 with ErrorDTO
```

**GlobalExceptionMapper handles all exceptions automatically**:
- No try-catch needed in resources
- Consistent error format across API
- Generic exceptions logged but sanitized for clients

---

### 8. Session Management Pattern

```java
// In resource class

@Context
RoutingContext context;

// Get current user (throws UnauthorizedException if not authenticated)
String userId = SessionUtils.getCurrentUserId(context);

// Set user after login
SessionUtils.setCurrentUser(context, "user123");

// Check authentication
boolean authenticated = SessionUtils.isAuthenticated(context);

// Logout
SessionUtils.clearCurrentUser(context);
```

**Rules**:
- Use `RoutingContext` (Vert.x), not `HttpSession`
- `getCurrentUserId()` throws `UnauthorizedException` if not authenticated
- Always call at start of protected endpoints
- Session attribute key: `"userId"`

---

## Frontend Patterns (TypeScript/Astro/Preact)

### 9. File Naming Conventions

```
src/
├── pages/
│   ├── index.astro           # Homepage (kebab-case for routes)
│   ├── tasks/
│   │   ├── index.astro       # /tasks
│   │   └── [id].astro        # /tasks/:id (dynamic route)
│   └── categories/
│       └── index.astro
├── islands/
│   ├── TaskList.tsx          # PascalCase for components
│   ├── TaskForm.tsx
│   └── FilterBar.tsx
├── components/
│   ├── ui/                   # Shadcn components
│   │   ├── button.tsx
│   │   ├── card.tsx
│   │   └── badge.tsx
│   └── layout/
│       └── Header.astro
└── lib/
    ├── api/
    │   ├── endpoints/        # Generated by Orval
    │   └── model/            # Generated by Orval
    └── stores/
        └── filterStore.ts    # Nano stores
```

**Rules**:
- Astro pages: `kebab-case.astro`
- Preact islands: `PascalCase.tsx`
- Shadcn components: `lowercase.tsx` (follow Shadcn convention)
- Stores: `camelCase.ts`
- Generated API files: Don't edit manually

---

### 10. Astro Page Pattern

```astro
---
// src/pages/tasks/index.astro

import Layout from '@/components/layout/Layout.astro';
import TaskList from '@/islands/TaskList';
import { getSession } from '@/lib/session';

// Server-side code (runs at build time or on server)
const session = await getSession(Astro.request);
if (!session?.userId) {
  return Astro.redirect('/login');
}

// Optional: Fetch initial data server-side for better SEO/performance
// const initialTasks = await fetch('http://localhost:7171/api/tasks');
---

<Layout title="My Tasks">
  <!-- Static HTML -->
  <div class="container mx-auto py-8">
    <h1 class="text-3xl font-bold mb-6">My Tasks</h1>

    <!-- Interactive island: Preact component with client-side JavaScript -->
    <TaskList client:load />
    {/*
      client:load = Load JS immediately
      client:idle = Load JS when browser idle
      client:visible = Load JS when visible in viewport
    */}
  </div>
</Layout>
```

**Rules**:
- Use Astro for pages (routes)
- Keep pages mostly static
- Use islands for interactive parts only
- Use `client:*` directives to control when JS loads
- Validate authentication server-side
- Use path aliases: `@/components`, `@/islands`, etc.

---

### 11. Preact Island Pattern

```tsx
// src/islands/TaskList.tsx

import { useState } from 'preact/hooks';
import { useGetTasks, useDeleteTask } from '@/lib/api/endpoints/tasks';
import { Button } from '@/components/ui/button';
import { Card } from '@/components/ui/card';
import type { Priority } from '@/lib/api/model';

interface TaskListProps {
  initialPriority?: Priority;
}

export default function TaskList({ initialPriority }: TaskListProps) {
  const [priority, setPriority] = useState<Priority | undefined>(initialPriority);

  // TanStack Query hook (generated by Orval)
  const { data: tasks, isLoading, error, refetch } = useGetTasks({
    priority,
  });

  const deleteMutation = useDeleteTask();

  const handleDelete = async (id: string) => {
    try {
      await deleteMutation.mutateAsync({ id });
      refetch(); // Refresh list
    } catch (err) {
      console.error('Delete failed:', err);
    }
  };

  if (isLoading) return <div>Loading...</div>;
  if (error) return <div>Error: {error.message}</div>;

  return (
    <div class="space-y-4">
      {/* Filter controls */}
      <div class="flex gap-2">
        <Button onClick={() => setPriority(undefined)}>All</Button>
        <Button onClick={() => setPriority('HIGH')}>High</Button>
        <Button onClick={() => setPriority('MEDIUM')}>Medium</Button>
        <Button onClick={() => setPriority('LOW')}>Low</Button>
      </div>

      {/* Task list */}
      {tasks?.map((task) => (
        <Card key={task.id}>
          <h3 class="text-lg font-semibold">{task.title}</h3>
          <p class="text-sm text-muted-foreground">{task.description}</p>
          <Button
            variant="destructive"
            onClick={() => handleDelete(task.id)}
          >
            Delete
          </Button>
        </Card>
      ))}
    </div>
  );
}
```

**Rules**:
- Use Preact hooks (`preact/hooks`)
- Use generated TanStack Query hooks from Orval
- Use Shadcn/ui components (`@/components/ui/*`)
- Use TypeScript strict mode (no `any`)
- Use Tailwind classes for styling
- Handle loading/error states
- Use `refetch()` or invalidation for cache updates
- Props: Define TypeScript interface

---

### 12. API Client Usage Pattern

```typescript
// Generated by Orval from OpenAPI schema

import { useGetTasks, useCreateTask, useUpdateTask, useDeleteTask } from '@/lib/api/endpoints/tasks';
import type { TaskResponseDTO, TaskCreateDTO } from '@/lib/api/model';

// GET /api/tasks with filters
const { data, isLoading, error } = useGetTasks({
  priority: 'HIGH',
  completed: false,
});

// POST /api/tasks
const createMutation = useCreateTask();
const handleCreate = async (dto: TaskCreateDTO) => {
  try {
    const created = await createMutation.mutateAsync({ data: dto });
    console.log('Created:', created);
  } catch (err) {
    console.error('Failed:', err);
  }
};

// PUT /api/tasks/:id
const updateMutation = useUpdateTask();
const handleUpdate = async (id: string, dto: TaskUpdateDTO) => {
  await updateMutation.mutateAsync({ id, data: dto });
};

// DELETE /api/tasks/:id
const deleteMutation = useDeleteTask();
const handleDelete = async (id: string) => {
  await deleteMutation.mutateAsync({ id });
};
```

**Rules**:
- Import hooks from `@/lib/api/endpoints/*`
- Import types from `@/lib/api/model`
- Use `useQuery` hooks for GET requests
- Use `useMutation` hooks for POST/PUT/DELETE
- Handle errors with try-catch
- Don't edit generated files manually
- Regenerate after backend changes: `npm run generate:api`

---

### 13. State Management Pattern (Nano Stores)

```typescript
// src/lib/stores/filterStore.ts

import { atom, computed } from 'nanostores';
import type { Priority } from '@/lib/api/model';

// Atom: Writable store
export const selectedPriority = atom<Priority | undefined>(undefined);
export const selectedCategory = atom<string | undefined>(undefined);
export const showCompleted = atom<boolean>(false);

// Computed: Derived value
export const activeFilters = computed(
  [selectedPriority, selectedCategory, showCompleted],
  (priority, category, completed) => ({
    priority,
    category,
    completed,
  })
);

// Actions
export function setPriority(priority: Priority | undefined) {
  selectedPriority.set(priority);
}

export function clearFilters() {
  selectedPriority.set(undefined);
  selectedCategory.set(undefined);
  showCompleted.set(false);
}
```

**Usage in component**:
```tsx
import { useStore } from '@nanostores/preact';
import { selectedPriority, setPriority } from '@/lib/stores/filterStore';

export default function FilterBar() {
  const priority = useStore(selectedPriority);

  return (
    <div>
      <button onClick={() => setPriority('HIGH')}>High Priority</button>
      <p>Current: {priority ?? 'All'}</p>
    </div>
  );
}
```

**Rules**:
- Use Nano Stores for cross-island state (filters, preferences)
- Use `atom()` for writable stores
- Use `computed()` for derived values
- Use `useStore()` hook in components
- Keep stores small and focused
- Export action functions for modifications

---

### 14. Styling Pattern (Tailwind + Shadcn)

```tsx
import { cn } from '@/lib/utils';
import { Button } from '@/components/ui/button';

export default function MyComponent() {
  return (
    <div class="container mx-auto p-4">
      {/* Use Tailwind utility classes */}
      <h1 class="text-3xl font-bold text-foreground">Title</h1>

      {/* Use Shadcn components */}
      <Button variant="default" size="lg">
        Click me
      </Button>

      {/* Merge classes with cn() utility */}
      <div class={cn(
        'rounded-lg border bg-card p-4',
        isActive && 'ring-2 ring-primary'
      )}>
        Card content
      </div>
    </div>
  );
}
```

**Rules**:
- Use Tailwind utility classes
- Use Shadcn/ui components for common UI elements
- Use theme CSS variables: `bg-background`, `text-foreground`, etc.
- Use `cn()` to conditionally merge classes
- Avoid inline styles
- Respect dark mode: use theme variables, not hardcoded colors

---

## Testing Patterns

### 15. Backend Contract Test Pattern

```java
@QuarkusTest
public class TaskResourceTest {

  @Test
  public void testGetTasks() {
    given()
      .when().get("/api/tasks")
      .then()
      .statusCode(200)
      .contentType(MediaType.APPLICATION_JSON)
      .body("$.size()", is(0)); // Empty initially
  }

  @Test
  public void testCreateTask() {
    given()
      .contentType(MediaType.APPLICATION_JSON)
      .body("""
        {
          "title": "Test Task",
          "description": "Test Description",
          "priority": "HIGH"
        }
        """)
      .when().post("/api/tasks")
      .then()
      .statusCode(201)
      .body("title", equalTo("Test Task"))
      .body("priority", equalTo("HIGH"))
      .body("completed", equalTo(false));
  }
}
```

**Rules**:
- Add `@QuarkusTest` annotation
- Use RestAssured DSL (`given().when().then()`)
- Test HTTP status codes
- Test response body structure
- Test validation errors (400 responses)
- Test authentication (401 responses)

---

### 16. Frontend Component Test Pattern

```typescript
// src/islands/TaskList.test.tsx

import { render, screen, waitFor } from '@testing-library/preact';
import { describe, it, expect, vi } from 'vitest';
import TaskList from './TaskList';

// Mock API hooks
vi.mock('@/lib/api/endpoints/tasks', () => ({
  useGetTasks: () => ({
    data: [
      { id: '1', title: 'Test Task', priority: 'HIGH' }
    ],
    isLoading: false,
    error: null,
  }),
}));

describe('TaskList', () => {
  it('renders tasks', async () => {
    render(<TaskList />);

    await waitFor(() => {
      expect(screen.getByText('Test Task')).toBeInTheDocument();
    });
  });
});
```

**Rules**:
- Use Vitest + Testing Library
- Mock API hooks with `vi.mock()`
- Test user interactions
- Test loading/error states
- Use `waitFor()` for async operations

---

## Documentation Patterns

### 17. JavaDoc Pattern

```java
/**
 * Creates a new task for the authenticated user.
 *
 * <p>This endpoint validates that the provided category (if any) belongs to the current user
 * before creating the task. If no priority is specified, defaults to MEDIUM.
 *
 * <p><b>Security:</b> Requires authenticated session (401 if not authenticated)
 *
 * <p><b>Validation:</b>
 * <ul>
 *   <li>Title: Required, 1-200 characters
 *   <li>Description: Optional, max 1000 characters
 *   <li>Category: Must belong to current user (400 if invalid)
 * </ul>
 *
 * @param dto the task creation request
 * @return the created task with generated ID and timestamps
 * @throws UnauthorizedException if no user in session
 * @throws ValidationException if category doesn't belong to user
 */
@POST
public Response createTask(@Valid TaskCreateDTO dto) {
  // ...
}
```

**Rules**:
- Document all public methods
- Explain "why", not just "what"
- Document validation rules
- Document security requirements
- Document exceptions thrown
- Use `<p>`, `<ul>`, `<b>` for formatting (generates nice OpenAPI docs)

---

### 18. TypeScript JSDoc Pattern

```typescript
/**
 * Task list component with filtering and actions.
 *
 * Fetches tasks from the API using TanStack Query and displays them
 * with filter controls. Supports delete operations with optimistic updates.
 *
 * @param props - Component props
 * @param props.initialPriority - Initial priority filter (optional)
 *
 * @example
 * ```tsx
 * <TaskList initialPriority="HIGH" />
 * ```
 */
export default function TaskList({ initialPriority }: TaskListProps) {
  // ...
}
```

**Rules**:
- Document complex components
- Document props with `@param`
- Provide usage examples with `@example`
- Keep docs concise for simple components

---

## Key Architectural Decisions

1. **Session-based auth** (not JWT): Simpler, server-side state
2. **UUID primary keys** (not auto-increment): Better for distributed systems
3. **String userId** (not FK): Session-based, no User entity needed yet
4. **Records for DTOs**: Immutable, concise, Java 14+
5. **Panache repositories**: Active Record pattern, less boilerplate
6. **Islands Architecture**: Minimal JS, static by default
7. **Orval code generation**: Type-safe API client, always in sync
8. **Nano Stores**: Lightweight state (<1KB), perfect for islands
9. **Shadcn/ui**: Copy-paste components (not npm dependency)
10. **Tailwind v4**: Latest version with better DX

---

**End of Code Patterns**

Always refer to this document when implementing new features to maintain consistency!
