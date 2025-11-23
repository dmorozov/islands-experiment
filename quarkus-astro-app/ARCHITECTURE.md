# Architecture Documentation

## Overview

This application demonstrates **Islands Architecture** using Quarkus (Java backend) and Astro (frontend framework). It serves as a production-ready template showcasing modern web development patterns with optimal performance.

## Table of Contents

- [Islands Architecture](#islands-architecture)
- [Technology Stack](#technology-stack)
- [State Management Patterns](#state-management-patterns)
- [Project Structure](#project-structure)
- [API Architecture](#api-architecture)
- [Performance Optimizations](#performance-optimizations)
- [Security Considerations](#security-considerations)

## Islands Architecture

### What is Islands Architecture?

Islands Architecture is a component-based web architecture that:
- Renders most content as **static HTML**
- Hydrates only **interactive components** (islands) with JavaScript
- Results in **smaller bundle sizes** and **faster page loads**
- Improves **Time to Interactive (TTI)** and **First Contentful Paint (FCP)**

### Benefits

1. **Performance**: Only hydrate what needs to be interactive
2. **SEO**: Static HTML is fully crawlable
3. **User Experience**: Faster initial page loads
4. **Developer Experience**: Component isolation and reusability

### Implementation in This Project

**Static Content** (Astro Components):
- `Layout.astro` - Page layout and structure
- `Navigation.astro` - Navigation menu
- Page components (`index.astro`, `categories.astro`, etc.)

**Interactive Islands** (Preact Components):
- `TaskList.tsx` - Task display with real-time updates
- `TaskForm.tsx` - Task creation and editing
- `TaskFilter.tsx` - Filtering controls
- `CategoryManager.tsx` - Category CRUD operations
- `CompletionToggle.tsx` - Task completion checkbox
- `ThemeToggle.tsx` - Dark/light mode toggle
- `UserMenu.tsx` - Session management
- `Login.tsx` - Authentication form

**Hydration Strategy**:
```astro
<!-- Load island when page loads -->
<TaskList client:load />

<!-- Load island only in browser (skip SSR) -->
<ThemeToggle client:only="preact" />

<!-- Load island when visible in viewport -->
<CompletionChart client:visible />

<!-- Load island when browser is idle -->
<PerformanceMetrics client:idle />
```

## Technology Stack

### Backend

- **Quarkus 3.28.1** - Java framework
  - Reactive REST endpoints (JAX-RS)
  - Hibernate ORM with Panache
  - H2 in-memory database
  - SmallRye OpenAPI
  - WebSockets Next (for real-time features)
  - Quinoa (frontend integration)

### Frontend

- **Astro** - Islands Architecture framework
- **Preact** - Lightweight React alternative for islands
- **TypeScript** - Type safety
- **Tailwind CSS v4** - Utility-first styling
- **Shadcn/UI** - Component library
- **TanStack Query v5** - Data fetching and caching
- **Nano Stores** - Lightweight state management
- **Orval** - OpenAPI client generator

### Build & Quality Tools

- **Maven** - Java build tool
- **npm** - Frontend package manager
- **ESLint** - JavaScript/TypeScript linting
- **Prettier** - Code formatting
- **Checkstyle & PMD** - Java code quality
- **Husky** - Git hooks
- **Vitest** - Frontend testing

## State Management Patterns

This application demonstrates **4 distinct state management patterns** as per the architecture requirements:

### 1. Client-Side State (Component-Local)

**Use Case**: State that doesn't need to persist or share across components

**Implementation**: Preact's `useState` hook

**Example**: Form input state in `TaskForm.tsx`
```typescript
const [title, setTitle] = useState('');
const [description, setDescription] = useState('');
```

**When to Use**:
- Temporary UI state (modals, dropdowns)
- Form inputs before submission
- Component-specific toggles

### 2. Cross-Island State (Nano Stores)

**Use Case**: State shared between multiple islands on the same page

**Implementation**: Nano Stores with `@nanostores/preact`

**Example**: Task filters in `src/lib/state.ts`
```typescript
import { atom } from 'nanostores';

export const taskFilter = atom({
  category: undefined,
  priority: undefined,
  status: undefined,
});
```

**Usage in Islands**:
```typescript
import { useStore } from '@nanostores/preact';
import { taskFilter } from '@/lib/state';

function TaskFilter() {
  const filter = useStore(taskFilter);
  const setFilter = (newFilter) => taskFilter.set(newFilter);
  // ...
}
```

**When to Use**:
- Filters that affect multiple components
- UI preferences (theme, layout)
- Shared selection state

### 3. Client-Side Persistent State (localStorage)

**Use Case**: State that persists across browser sessions

**Implementation**: Nano Stores with `@nanostores/persistent`

**Example**: Theme preference in `src/lib/state.ts`
```typescript
import { persistentAtom } from '@nanostores/persistent';

export const userTheme = persistentAtom('theme', 'light');
```

**Example**: User preferences in `src/lib/storage.ts`
```typescript
export const preferences = {
  get(userId: string): UserPreferences {
    const key = `preferences_${userId}`;
    const stored = localStorage.getItem(key);
    return stored ? JSON.parse(stored) : defaultPreferences;
  },

  set(userId: string, prefs: UserPreferences) {
    const key = `preferences_${userId}`;
    localStorage.setItem(key, JSON.stringify(prefs));
  },
};
```

**When to Use**:
- Theme preferences
- User-specific settings
- Recently viewed items
- Draft form data

### 4. Server-Side State (HTTP Session + Database)

**Use Case**: State that must persist server-side and be secure

**Implementation**: Vert.x session + Hibernate ORM

**Example**: User authentication in `SessionResource.java`
```java
@POST
@Path("/login")
public Response login(@Valid final UserDTO userDTO) {
    // Store in server-side session
    SessionUtils.setCurrentUser(routingContext, userDTO.username());

    // Persist to database
    categoryService.ensureDefaultCategories(userDTO.username());

    return Response.ok(userDTO).build();
}
```

**Example**: Task data in `TaskService.java`
```java
@Transactional
public TaskResponseDTO createTask(String userId, TaskCreateDTO dto) {
    // Validate and persist to database
    Category category = categoryRepository.findById(dto.categoryId())
        .orElseThrow(() -> new ResourceNotFoundException("Category not found"));

    Task task = new Task();
    task.setUserId(userId);
    task.setTitle(dto.title());
    // ...
    taskRepository.persist(task);

    return TaskResponseDTO.from(task);
}
```

**When to Use**:
- User authentication
- Task data (CRUD operations)
- Category management
- Completion statistics

### State Management Decision Tree

```
Is state sensitive or requires authorization?
├─ YES → Server-Side State (HTTP Session + Database)
└─ NO → Does state need to persist across sessions?
    ├─ YES → Client-Side Persistent (localStorage)
    └─ NO → Does state need to be shared across islands?
        ├─ YES → Cross-Island State (Nano Stores)
        └─ NO → Client-Side State (useState)
```

## Project Structure

```
quarkus-astro-app/
├── src/main/
│   ├── java/org/acme/taskmanager/
│   │   ├── model/           # JPA entities
│   │   │   ├── Category.java
│   │   │   ├── Task.java
│   │   │   └── Priority.java
│   │   ├── dto/             # Data Transfer Objects
│   │   │   ├── TaskResponseDTO.java
│   │   │   ├── TaskCreateDTO.java
│   │   │   ├── CategoryResponseDTO.java
│   │   │   └── UserDTO.java
│   │   ├── resource/        # REST endpoints
│   │   │   ├── TaskResource.java
│   │   │   ├── CategoryResource.java
│   │   │   ├── StatsResource.java
│   │   │   └── SessionResource.java
│   │   ├── service/         # Business logic
│   │   │   ├── TaskService.java
│   │   │   ├── CategoryService.java
│   │   │   └── StatsService.java
│   │   ├── repository/      # Data access
│   │   │   ├── TaskRepository.java
│   │   │   └── CategoryRepository.java
│   │   ├── exception/       # Custom exceptions
│   │   │   ├── ResourceNotFoundException.java
│   │   │   ├── ValidationException.java
│   │   │   └── GlobalExceptionMapper.java
│   │   └── session/         # Session utilities
│   │       └── SessionUtils.java
│   ├── resources/
│   │   └── application.properties
│   └── webui/               # Astro frontend
│       ├── src/
│       │   ├── components/  # Astro components (static)
│       │   │   ├── Layout.astro
│       │   │   └── Navigation.astro
│       │   ├── islands/     # Preact islands (interactive)
│       │   │   ├── TaskList.tsx
│       │   │   ├── TaskForm.tsx
│       │   │   ├── TaskFilter.tsx
│       │   │   ├── Login.tsx
│       │   │   └── UserMenu.tsx
│       │   ├── pages/       # File-based routing
│       │   │   ├── index.astro
│       │   │   ├── categories.astro
│       │   │   ├── dashboard.astro
│       │   │   ├── performance.astro
│       │   │   ├── login.astro
│       │   │   └── 404.astro
│       │   ├── lib/         # Utilities and state
│       │   │   ├── api/     # Generated API client (Orval)
│       │   │   ├── hooks/   # Custom hooks
│       │   │   ├── state.ts # Nano stores
│       │   │   └── storage.ts # localStorage utilities
│       │   ├── styles/
│       │   │   └── globals.css
│       │   └── types/
│       ├── api/             # OpenAPI schema (generated)
│       │   └── openapi.json
│       ├── astro.config.mjs
│       ├── tailwind.config.mjs
│       ├── tsconfig.json
│       ├── orval.config.ts
│       └── package.json
└── pom.xml
```

## API Architecture

### OpenAPI-First Development

1. **Backend generates OpenAPI schema** (`/api/openapi.json`)
2. **Orval generates TypeScript client** with TanStack Query hooks
3. **Frontend uses type-safe hooks** for all API calls

### API Endpoints

**Tasks**:
- `GET /api/tasks` - List tasks with filters and pagination
- `GET /api/tasks/{id}` - Get single task
- `POST /api/tasks` - Create task
- `PUT /api/tasks/{id}` - Update task
- `DELETE /api/tasks/{id}` - Delete task
- `PATCH /api/tasks/{id}/complete` - Toggle completion

**Categories**:
- `GET /api/categories` - List categories
- `GET /api/categories/{id}` - Get category
- `POST /api/categories` - Create category
- `PUT /api/categories/{id}` - Update category
- `DELETE /api/categories/{id}` - Delete category

**Statistics**:
- `GET /api/stats/summary` - Completion stats
- `GET /api/stats/history` - Completion history

**Session**:
- `POST /api/session/login` - Login (username only)
- `GET /api/session/user` - Get current user
- `POST /api/session/logout` - Logout

### Data Flow

```
1. User Action (Island)
   ↓
2. TanStack Query Hook (Generated by Orval)
   ↓
3. Axios Request (Custom mutator with auth)
   ↓
4. REST Endpoint (Quarkus)
   ↓
5. Service Layer (Business Logic)
   ↓
6. Repository (Database Access)
   ↓
7. Response DTO
   ↓
8. Query Cache Update (Optimistic UI)
   ↓
9. Island Re-render
```

## Performance Optimizations

### Frontend Optimizations

1. **Islands Architecture**: Only hydrate interactive components
2. **Code Splitting**: Separate bundles per island
3. **Lazy Loading**: Islands load on-demand (`client:visible`, `client:idle`)
4. **Static Generation**: Most HTML pre-rendered at build time
5. **Optimistic Updates**: UI updates before server confirms
6. **Query Caching**: TanStack Query prevents redundant requests

### Backend Optimizations

1. **HTTP/2**: Multiplexing for faster resource loading
2. **Gzip Compression**: Reduced payload sizes
3. **Static Resource Caching**: 24-hour cache for assets
4. **Database Indexing**: Indexes on frequently queried fields
5. **Connection Pooling**: Efficient database connections

### Performance Targets

- **Bundle Size**: <100KB gzipped (per page)
- **First Contentful Paint**: <1.5s
- **Time to Interactive**: <3s
- **Island Hydration**: <200ms per island
- **API Response Time**: <500ms (p95)

## Security Considerations

### Authentication

- **Demo Mode**: Username-only authentication (no password)
- **Session Management**: Server-side sessions with Vert.x
- **Session Timeout**: 30 minutes (configurable)
- **CSRF Protection**: Enabled via Quarkus security

### Data Isolation

- **User Isolation**: All queries filter by `userId`
- **Resource Ownership**: Validation before CRUD operations
- **Exception Handling**: Safe error messages (no data leakage)

### API Security

- **Input Validation**: Jakarta Bean Validation on DTOs
- **SQL Injection**: Prevented by Panache/Hibernate
- **XSS Prevention**: Framework-level escaping in Astro/Preact
- **CORS**: Restricted to development origins

### Production Recommendations

1. **Replace demo auth** with proper authentication (OAuth2, JWT)
2. **Enable HTTPS** in production
3. **Set secure cookie flags**: HttpOnly, Secure, SameSite
4. **Implement rate limiting** on API endpoints
5. **Use production database** (PostgreSQL, MySQL)
6. **Enable security headers**: CSP, X-Frame-Options, etc.

## Trade-offs and Decisions

### Why Islands Architecture?

**Pros**:
- Optimal performance for content-heavy sites
- Better SEO with static HTML
- Smaller JavaScript bundles
- Component isolation

**Cons**:
- Requires careful planning of interactivity boundaries
- Not ideal for highly dynamic single-page apps
- Learning curve for developers used to SPAs

### Why Preact over React?

**Pros**:
- 3KB vs 45KB (React)
- Same API as React
- Faster hydration

**Cons**:
- Smaller ecosystem
- Some React libraries incompatible

### Why Nano Stores over Zustand/Redux?

**Pros**:
- 334 bytes vs 1.2KB (Zustand) vs 4.7KB (Redux)
- Framework-agnostic
- Simple API

**Cons**:
- Less documentation
- Fewer DevTools

### Why Quarkus over Spring Boot?

**Pros**:
- Faster startup time
- Lower memory footprint
- Native compilation support (GraalVM)
- Reactive by default

**Cons**:
- Smaller ecosystem
- Fewer third-party integrations

## Conclusion

This architecture balances **performance**, **developer experience**, and **production-readiness**. The Islands Architecture pattern is ideal for content-heavy applications with selective interactivity, while the state management patterns provide flexibility for different data persistence needs.

For questions or contributions, see [CONTRIBUTING.md](CONTRIBUTING.md).
