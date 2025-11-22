# Resume Checklist - Task Manager Application

**Last Updated**: 2025-11-22 06:30 UTC
**Current State**: Phase 5 Complete (US2), Ready for Phase 6 (US3)
**Progress**: 338/694 tasks complete (49%)

---

## Quick Status Check

### ✅ Application is Running
- [ ] Quarkus dev server running on http://localhost:7171
- [ ] Astro dev server running on http://localhost:3000 (via Quinoa)
- [ ] Swagger UI accessible at http://localhost:7171/swagger-ui
- [ ] Main app accessible at http://localhost:7171/

### ✅ Last Completed Phase
**Phase 5: User Story 2 - Create and Edit Tasks (T265-T338)**
- Backend CRUD operations for tasks
- TaskForm island (create/edit modes)
- NewTaskButton with modal dialog
- TaskList with inline editing and deletion
- Contract tests for all CRUD operations
- All 74 implementation tasks completed

### 🎯 Next Phase to Implement
**Phase 6: User Story 3 - Organize with Categories and Priorities (T350-T432)**
- Contract tests for category CRUD
- Category create/update/delete DTOs
- Category service enhancements
- Category REST endpoints
- CategoryManager island
- Visual indicators for categories and priorities

---

## Step-by-Step Resume Process

### Step 1: Verify Environment (5 minutes)

```bash
# Check if services are running
ps aux | grep "quarkus dev"
ps aux | grep "astro dev"

# If not running, start from repository root:
cd /workspaces
quarkus dev

# This will start both Quarkus (7171) and Astro (3000)
```

**Expected Results**:
- Quarkus banner shows "Listening on: http://localhost:7171"
- Astro shows "ready in XXXms" on port 3000
- No compilation errors

### Step 2: Verify Application State (5 minutes)

**Open these URLs in browser**:
1. http://localhost:7171/ - Should show Task Manager homepage
2. http://localhost:7171/swagger-ui - Should show API documentation with:
   - Categories endpoints (GET)
   - Tasks endpoints (GET, POST, PUT, DELETE)
3. Check console - No JavaScript errors

**Test Basic Functionality**:
1. Click "New Task" button - Modal should open
2. Fill form (title, category, priority) - Dropdowns should populate
3. Submit - Task should appear in list (if categories exist)
4. Click "Edit" on a task - Inline form should appear
5. Click "Delete" - Confirmation dialog should appear

### Step 3: Review Current Code State (10 minutes)

**Check Git Status**:
```bash
cd /workspaces
git status
git diff --stat
```

**Expected Uncommitted Changes (Phase 5)**:
- Modified: `TaskService.java`, `TaskResource.java`, `TaskResourceTest.java`
- Modified: `TaskList.tsx`, `index.astro`, `tasks.md`
- New: `TaskCreateDTO.java`, `TaskUpdateDTO.java`
- New: `TaskForm.tsx`, `NewTaskButton.tsx`
- New: 6 Shadcn UI components (input, textarea, select, label, dialog, alert-dialog)
- Updated: `SESSION_MEMORY.md`, `RESUME_CHECKLIST.md`

**Review Key Files**:
```bash
# Check task list progress
cat /workspaces/specs/001-task-manager-app/tasks.md | grep -A 5 "Phase 6"

# Check backend service
cat /workspaces/quarkus-astro-app/src/main/java/org/acme/taskmanager/service/TaskService.java | head -50

# Check frontend island
ls -la /workspaces/quarkus-astro-app/src/main/webui/src/islands/
```

### Step 4: Run Tests to Verify Integrity (5 minutes)

**Backend Tests**:
```bash
cd /workspaces/quarkus-astro-app
./mvnw test -Dtest=TaskResourceTest

# Expected: 12 tests passing
# - 5 tests for GET /api/tasks (filtering)
# - 7 tests for CRUD operations
```

**Frontend Linting**:
```bash
cd /workspaces/quarkus-astro-app/src/main/webui
npm run lint
npm run format:check

# Expected: No errors, all files passing
```

### Step 5: Review Documentation (5 minutes)

**Read Session Memory**:
```bash
cat /workspaces/SESSION_MEMORY.md | less
# Focus on:
# - "Phase 5 Completion Details"
# - "Current Application State"
# - "Next Steps - Phase 6 Roadmap"
```

**Read Task List for Next Phase**:
```bash
cat /workspaces/specs/001-task-manager-app/tasks.md | grep -A 100 "Phase 6"
# Understand T350-T432 requirements
```

---

## Critical Information to Remember

### Phase 5 Implementation Highlights

**Backend Changes**:
- `TaskService` now has 4 new methods: `createTask()`, `getTaskById()`, `updateTask()`, `deleteTask()`
- `TaskResource` now has 4 new endpoints: POST, GET/{id}, PUT/{id}, DELETE/{id}
- All methods include category ownership validation
- All endpoints have OpenAPI annotations

**Frontend Changes**:
- `TaskForm.tsx` - Universal form component (create/edit modes)
  - Mode switching via `mode` prop
  - Pre-fills data in edit mode via `initialTask` prop
  - Category and priority dropdowns
  - Form validation (title required, max lengths)
- `NewTaskButton.tsx` - Modal button with TaskForm
  - Opens Shadcn Dialog
  - Auto-closes on success
- `TaskList.tsx` - Enhanced with editing/deletion
  - Inline editing (renders TaskForm when editing)
  - Delete confirmation (Shadcn AlertDialog)
  - Optimistic updates

**API Client**:
- Regenerated with new hooks: `usePostApiTasks`, `useGetApiTasksId`, `usePutApiTasksId`, `useDeleteApiTasksId`
- All hooks auto-generated by Orval from OpenAPI schema
- Location: `src/lib/api/endpoints/tasks/tasks.ts`

### Checkstyle Fix Pattern (Important for Future DTOs)

When creating DTOs, avoid checkstyle violations:
```java
// ❌ BAD - Magic numbers, missing Javadoc
public record TaskCreateDTO(
    @Size(max = 200) String title,  // Magic number!
    @Size(max = 2000) String description  // Magic number!
) {}

// ✅ GOOD - Constants, proper Javadoc
/**
 * DTO for creating tasks.
 * @param title the task title
 * @param description the task description
 */
public record TaskCreateDTO(
    @Size(max = TaskValidationConstants.MAX_TITLE_LENGTH) String title,
    @Size(max = TaskValidationConstants.MAX_DESCRIPTION_LENGTH) String description
) {
    static final class TaskValidationConstants {
        static final int MAX_TITLE_LENGTH = 200;
        static final int MAX_DESCRIPTION_LENGTH = 2000;
        private TaskValidationConstants() {}
    }
}
```

### Key Patterns Established

**Backend Service Pattern**:
```java
@Transactional
public TaskResponseDTO createTask(String userId, TaskCreateDTO dto) {
    // 1. Validate userId
    if (userId == null || userId.isBlank()) {
        throw new IllegalArgumentException("User ID cannot be null or blank");
    }

    // 2. Validate related entities (category ownership)
    Category category = categoryRepository.findByIdOptional(dto.categoryId())
        .orElseThrow(() -> new ValidationException("Category not found"));

    if (!category.getUserId().equals(userId)) {
        throw new ValidationException("Category does not belong to user");
    }

    // 3. Create entity, set userId
    Task task = new Task();
    task.setTitle(dto.title());
    task.setUserId(userId);
    // ... set other fields

    // 4. Persist
    taskRepository.persist(task);

    // 5. Return DTO
    return TaskResponseDTO.from(task);
}
```

**Frontend Island Pattern**:
```tsx
// Island with QueryProvider wrapper
export default function MyIsland() {
  return (
    <QueryProvider>
      <MyIslandContent />
    </QueryProvider>
  );
}

// Actual component logic
function MyIslandContent() {
  const queryClient = useQueryClient();
  const mutation = usePostApiTasks();

  const handleSubmit = async () => {
    await mutation.mutateAsync({ data: {...} });
    queryClient.invalidateQueries({ queryKey: getGetApiTasksQueryKey() });
  };

  return <form onSubmit={handleSubmit}>...</form>;
}
```

---

## Known Issues & Workarounds

### Issue 1: Checkstyle Warnings (Non-blocking)
**Symptom**: Warnings shown during `quarkus dev` startup
**Status**: Configured to warn only, not fail build
**Action**: Fix violations when creating new code

### Issue 2: H2 Database Resets on Restart
**Symptom**: All data lost when Quarkus restarts
**Status**: Expected behavior (in-memory database)
**Workaround**: Use seed data or import.sql for testing

### Issue 3: No Authentication Yet
**Symptom**: Endpoints expect session but no login UI
**Status**: Authentication is Phase 9
**Workaround**: For now, endpoints will throw 401 (expected)

### Issue 4: Background Processes May Be Running
**Symptom**: Port 7171 or 3000 already in use
**Status**: Previous `quarkus dev` may still be running
**Action**: Kill processes:
```bash
pkill -f "quarkus dev"
pkill -f "astro dev"
# Then restart
```

---

## Next Steps - Starting Phase 6

### Task T350: Write Contract Test for POST /api/categories

**File**: `src/test/java/org/acme/taskmanager/contract/CategoryResourceTest.java`

**Requirements**:
- Test POST /api/categories with valid data → expect 201 Created
- Verify response contains: id, name, colorCode, isDefault, createdAt
- Use `@QuarkusTest` annotation
- Follow pattern from `TaskResourceTest.java`

**Example Structure**:
```java
@QuarkusTest
@DisplayName("CategoryResource Contract Tests")
class CategoryResourceTest {

    private static final String API_BASE_PATH = "/api/categories";

    @Test
    @DisplayName("POST /api/categories - should return 201 and created category")
    void testCreateCategory() {
        String requestBody = """
            {
                "name": "Work",
                "colorCode": "#FF5733"
            }
            """;

        given()
            .contentType(ContentType.JSON)
            .body(requestBody)
            .when()
            .post(API_BASE_PATH)
            .then()
            .statusCode(201)
            .contentType(ContentType.JSON)
            .body("id", notNullValue())
            .body("name", equalTo("Work"))
            .body("colorCode", equalTo("#FF5733"))
            .body("isDefault", equalTo(false));
    }
}
```

### Commit Phase 5 Before Starting Phase 6 (Recommended)

**Why**: Clean separation between phases makes rollback easier

**Commands**:
```bash
cd /workspaces
git add .
git status  # Review changes
git commit -m "feat: implement US2 - Create and Edit Tasks

Implemented full CRUD operations for tasks with optimistic UI updates.

Backend:
- Added TaskCreateDTO and TaskUpdateDTO with validation
- Enhanced TaskService with create, getById, update, delete methods
- Added 4 new REST endpoints to TaskResource (POST, GET/{id}, PUT/{id}, DELETE/{id})
- Added 7 contract tests covering all CRUD operations

Frontend:
- Created TaskForm island (supports create/edit modes)
- Created NewTaskButton with modal dialog
- Enhanced TaskList with inline editing and deletion
- Installed Shadcn components: input, textarea, select, label, dialog, alert-dialog
- Generated new API hooks for task CRUD

Success Criteria (SC-001, SC-007, SC-013):
✅ Task creation < 3 seconds
✅ Inline editing with instant feedback
✅ Optimistic UI updates for delete

Tasks: T265-T338 (74 tasks)
Phase: 5/10 complete (US2)"
```

---

## Helpful Commands Reference

### Development
```bash
# Start everything (from /workspaces/)
quarkus dev

# Stop everything
pkill -f "quarkus dev"
pkill -f "astro dev"

# View logs
# Quarkus logs appear in terminal where you ran `quarkus dev`
# Astro logs appear in same terminal (Quinoa manages it)
```

### Testing
```bash
# Backend tests
cd /workspaces/quarkus-astro-app
./mvnw test                              # All tests
./mvnw test -Dtest=TaskResourceTest      # Specific test
./mvnw verify                            # Tests + quality checks

# Frontend tests
cd /workspaces/quarkus-astro-app/src/main/webui
npm run test                             # Run tests
npm run lint                             # Check linting
npm run format                           # Auto-fix formatting
```

### API Client Generation
```bash
# After adding/modifying backend endpoints:
cd /workspaces/quarkus-astro-app/src/main/webui

# 1. Ensure Quarkus dev is running (generates OpenAPI schema)
# 2. Generate TypeScript client
npm run generate:api

# 3. Verify new hooks were generated
ls -la src/lib/api/endpoints/
```

### Database
```bash
# View H2 console (if enabled)
# Add to application.properties:
# quarkus.datasource.jdbc.url=jdbc:h2:mem:taskmanager;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE
# quarkus.h2.console.enabled=true

# Then access: http://localhost:7171/h2-console
# JDBC URL: jdbc:h2:mem:taskmanager
# Username: sa
# Password: (leave blank)
```

---

## Emergency Recovery

### If Application Won't Start
1. Check ports are free: `lsof -i :7171` and `lsof -i :3000`
2. Kill processes: `pkill -f "quarkus dev"`
3. Clear Maven cache: `rm -rf ~/.m2/repository/org/acme`
4. Reinstall frontend deps: `cd webui && rm -rf node_modules && npm install`
5. Clean build: `./mvnw clean install`
6. Restart: `quarkus dev`

### If Tests Fail
1. Check H2 database is clean (restart Quarkus)
2. Review test output for specific failure
3. Run single failing test: `./mvnw test -Dtest=ClassName#methodName`
4. Check for order-dependent tests (should be isolated)

### If Frontend Build Fails
1. Check for TypeScript errors: `npm run type-check` (if configured)
2. Check for lint errors: `npm run lint`
3. Regenerate API client: `npm run generate:api`
4. Clear build cache: `rm -rf dist/ .astro/`

### If Git State is Unclear
1. Review changes: `git status` and `git diff`
2. See what's staged: `git diff --staged`
3. Discard all changes: `git reset --hard HEAD` (⚠️ DANGEROUS - loses work!)
4. Discard specific file: `git checkout -- path/to/file`

---

## Success Indicators

You're ready to continue if:
- ✅ Application loads at http://localhost:7171
- ✅ "New Task" button opens modal with form
- ✅ Tasks can be edited inline by clicking them
- ✅ Delete confirmation appears when clicking delete
- ✅ Contract tests pass (12/12 in TaskResourceTest)
- ✅ No console errors in browser or terminal
- ✅ OpenAPI schema shows all endpoints (GET, POST, PUT, DELETE for tasks)
- ✅ You understand Phase 6 requirements from tasks.md

---

## Contact & Support

**Documentation Locations**:
- Session state: `/workspaces/SESSION_MEMORY.md`
- Task list: `/workspaces/specs/001-task-manager-app/tasks.md`
- Architecture: `/workspaces/specs/001-task-manager-app/plan.md`
- Spec: `/workspaces/specs/001-task-manager-app/spec.md`

**Key Files to Review**:
1. `SESSION_MEMORY.md` - Complete session context
2. `tasks.md` - Next tasks to implement
3. `plan.md` - Architecture decisions
4. `TaskService.java` - Backend service pattern
5. `TaskForm.tsx` - Frontend island pattern

---

**Last Updated**: 2025-11-22 06:30 UTC
**Next Task**: T350 - Write contract test for POST /api/categories
**Estimated Time to Resume**: 30 minutes (if all checks pass)

**Good luck! 🚀**
