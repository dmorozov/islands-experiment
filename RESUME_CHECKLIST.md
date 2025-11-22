# Resume Checklist - New Computer Setup

Use this checklist when opening the project on a new computer.

---

## ✅ Prerequisites Check

- [ ] Java 21 JDK installed (`java -version`)
- [ ] Node.js 20.17.0+ installed (`node --version`)
- [ ] npm 10.8.2+ installed (`npm --version`)
- [ ] Maven installed or use `./mvnw`
- [ ] Git installed and configured
- [ ] VS Code installed (recommended)

---

## ✅ Project Setup

### 1. Clone & Verify
```bash
# Clone repository
git clone <repository-url>
cd <project-directory>

# Verify branch
git branch  # Should show: 001-task-manager-app
git status  # Check for uncommitted Phase 3 files
```

### 2. Install Backend Dependencies
```bash
cd quarkus-astro-app
./mvnw clean install

# Should complete without errors
# Downloads all Maven dependencies
```

### 3. Install Frontend Dependencies
```bash
cd src/main/webui
npm install

# Should complete without errors
# Creates node_modules/ directory
```

---

## ✅ Verify Configuration

### 4. Check Husky Installation
```bash
# From repository root (/workspaces)
ls -la .husky/
# Should show: pre-commit file (executable)

# Verify pre-commit hook content
cat .husky/pre-commit
# Should contain: cd quarkus-astro-app/src/main/webui && npx lint-staged
```

### 5. Verify File Structure
```bash
# Backend files (Phase 3)
ls -la quarkus-astro-app/src/main/java/org/acme/taskmanager/
# Should show: dto/ exception/ model/ session/ service/ resource/

# Frontend files (Phase 2)
ls -la quarkus-astro-app/src/main/webui/
# Should show: eslint.config.js, .prettierrc.json, orval.config.ts, etc.
```

---

## ✅ Start Development Environment

### 6. Start Backend (Terminal 1)
```bash
cd quarkus-astro-app
./mvnw quarkus:dev
```

**Expected Output**:
```
__  ____  __  _____   ___  __ ____  ______
 --/ __ \/ / / / _ | / _ \/ //_/ / / / __/
 -/ /_/ / /_/ / __ |/ , _/ ,< / /_/ /\ \
--\___\_\____/_/ |_/_/|_/_/|_|\____/___/
INFO  [io.quarkus] (Quarkus Main Thread) quarkus-astro-app 1.0.0-SNAPSHOT on JVM started in X.XXXs. Listening on: http://localhost:7171
```

**Verify**:
- [ ] Backend starts without errors
- [ ] Listening on port 7171
- [ ] No compilation errors

### 7. Test Backend Endpoints
```bash
# In new terminal
curl http://localhost:7171/q/health
# Should return: {"status": "UP", ...}

# Open browser
open http://localhost:7171/swagger-ui
# Should show Swagger UI (may be empty - that's OK)
```

### 8. Start Frontend (Terminal 2)
```bash
cd quarkus-astro-app/src/main/webui
npm run dev
```

**Expected Output**:
```
astro  v5.x.x ready in XXX ms

┃ Local    http://localhost:3000/
┃ Network  use --host to expose
```

**Verify**:
- [ ] Astro starts without errors
- [ ] Listening on port 3000
- [ ] No TypeScript errors

### 9. Test Frontend
```bash
# Open browser
open http://localhost:3000
# Should show default Astro page or blank page (no content yet - that's OK)
```

---

## ✅ Verify Quality Tools

### 10. Test ESLint
```bash
cd quarkus-astro-app/src/main/webui
npm run lint
# Should complete without errors (or show no violations)
```

### 11. Test Prettier
```bash
npm run format:check
# Should show all files are formatted correctly
```

### 12. Test Pre-commit Hook
```bash
# Create a test file with bad formatting
echo "const x=1" > test-file.ts
git add test-file.ts

# Try to commit
git commit -m "test"
# Should auto-format the file or block commit if ESLint fails

# Clean up
git reset HEAD test-file.ts
rm test-file.ts
```

### 13. Test Vitest
```bash
npm run test
# Should run (may show 0 tests - that's OK, no tests written yet)
```

---

## ✅ Verify VS Code Setup (Optional)

### 14. Install VS Code Extensions
```bash
# Open project in VS Code
code .

# VS Code should prompt to install recommended extensions
# Check .vscode/extensions.json for full list
```

**Key Extensions**:
- [ ] Astro (astro-build.astro-vscode)
- [ ] Prettier (esbenp.prettier-vscode)
- [ ] ESLint (dbaeumer.vscode-eslint)
- [ ] Tailwind CSS IntelliSense (bradlc.vscode-tailwindcss)
- [ ] Java Extension Pack (redhat.java)
- [ ] Quarkus (redhat.vscode-quarkus)

### 15. Verify VS Code Settings
Open any `.ts` file and verify:
- [ ] Format on save works (Prettier)
- [ ] ESLint errors show in "Problems" panel
- [ ] TypeScript type checking works
- [ ] Tailwind IntelliSense provides autocomplete

---

## ✅ Review Uncommitted Work

### 16. Check Git Status
```bash
git status
```

**Expected Uncommitted Files (Phase 3)**:
- `quarkus-astro-app/src/main/resources/application.properties` (modified)
- `specs/001-task-manager-app/tasks.md` (modified)
- `quarkus-astro-app/src/main/java/org/acme/taskmanager/model/Priority.java` (new)
- `quarkus-astro-app/src/main/java/org/acme/taskmanager/dto/ErrorDTO.java` (new)
- `quarkus-astro-app/src/main/java/org/acme/taskmanager/exception/*.java` (4 new files)
- `quarkus-astro-app/src/main/java/org/acme/taskmanager/session/SessionUtils.java` (new)

### 17. Review Changes
```bash
# Review application.properties changes
git diff quarkus-astro-app/src/main/resources/application.properties

# Review new Java files
cat quarkus-astro-app/src/main/java/org/acme/taskmanager/session/SessionUtils.java
cat quarkus-astro-app/src/main/java/org/acme/taskmanager/exception/GlobalExceptionMapper.java
```

---

## ✅ Read Documentation

### 18. Review Session Memory
```bash
# Read full session context
cat SESSION_MEMORY.md

# Read quick reference
cat QUICK_REFERENCE.md
```

### 19. Review Task List
```bash
# Open task list to see what's next
cat specs/001-task-manager-app/tasks.md | grep -A 20 "Phase 4"
```

**Next Task**: T146 - Create TaskResourceTest for contract testing

---

## ✅ Optional: Commit Phase 3 Work

### 20. Review and Commit (Optional)
```bash
# Review all changes
git status
git diff

# Stage Phase 3 files
git add quarkus-astro-app/src/main/java/org/acme/taskmanager/
git add quarkus-astro-app/src/main/resources/application.properties
git add specs/001-task-manager-app/tasks.md

# Commit
git commit -m "feat: implement backend core infrastructure (Phase 3)

- Configure Quarkus application.properties with H2, CORS, OpenAPI
- Create Priority enum (HIGH, MEDIUM, LOW)
- Implement session management with SessionUtils
- Create ErrorDTO for standardized API responses
- Implement custom exceptions (ResourceNotFound, Validation, Unauthorized)
- Create GlobalExceptionMapper for centralized error handling
- Update tasks.md to mark T126-T145 as completed

Phase 3 complete: Backend infrastructure ready for API endpoint implementations"
```

---

## 🎯 Ready to Resume!

If all checkboxes above are checked, you're ready to continue with **Phase 4: User Story 1**.

**Start with**: Task T146 - Create contract tests for TaskResource

**Reference**:
- Full details: `SESSION_MEMORY.md`
- Quick lookup: `QUICK_REFERENCE.md`
- Task list: `specs/001-task-manager-app/tasks.md`

---

## 🆘 Troubleshooting

**Backend won't start**:
- Check Java version: `java -version` (must be 21+)
- Clean build: `./mvnw clean install`
- Check port 7171 not in use: `lsof -i :7171`

**Frontend won't start**:
- Check Node version: `node --version` (must be 20.17.0+)
- Delete node_modules: `rm -rf node_modules package-lock.json`
- Reinstall: `npm install`
- Check port 3000 not in use: `lsof -i :3000`

**Pre-commit hooks not working**:
- Verify Husky location: `ls -la /workspaces/.husky/`
- Verify executable: `ls -la /workspaces/.husky/pre-commit`
- Make executable: `chmod +x /workspaces/.husky/pre-commit`

**ESLint/Prettier errors**:
- Run format: `npm run format`
- Run lint fix: `npm run lint:fix`
- Check config: `cat eslint.config.js`

---

**End of Checklist**
