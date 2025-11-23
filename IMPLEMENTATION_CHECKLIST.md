# Task Manager Islands Architecture: Implementation Checklist

Complete step-by-step guide to implement all linting, formatting, and tooling configurations.

---

## Phase 1: Installation & Setup (1-2 hours)

### Step 1: Install Core Dependencies
- [ ] Navigate to webui directory: `cd src/main/webui`
- [ ] Install base packages: `npm install`
- [ ] Verify Node.js version >= 18: `node --version`

### Step 2: Install ESLint Packages
```bash
npm install --save-dev \
  eslint@^9.0.0 \
  @typescript-eslint/eslint-plugin@^7.0.0 \
  @typescript-eslint/parser@^7.0.0 \
  @eslint/js@^9.0.0 \
  globals@^15.0.0 \
  typescript-eslint@^7.0.0 \
  eslint-plugin-astro@^1.2.0 \
  eslint-plugin-preact@^3.2.0 \
  eslint-plugin-jsx-a11y@^6.8.0 \
  eslint-plugin-import@^2.29.0 \
  eslint-config-airbnb-base@^15.0.0 \
  eslint-config-airbnb-typescript@^18.0.0
```

- [ ] Verify installation: `npx eslint --version`
- [ ] Check TypeScript parser: `npx @typescript-eslint/parser --version`

### Step 3: Install Prettier Packages
```bash
npm install --save-dev \
  prettier@^3.0.0 \
  prettier-plugin-astro@^0.13.0 \
  prettier-plugin-tailwindcss@^0.5.0 \
  eslint-config-prettier@^9.0.0
```

- [ ] Verify installation: `npx prettier --version`

### Step 4: Install Husky & lint-staged
```bash
npm install --save-dev \
  husky@^9.0.0 \
  lint-staged@^15.0.0
```

- [ ] Initialize Husky: `npx husky install`
- [ ] Verify `.husky` directory created

### Step 5: Install Tailwind CSS
```bash
npm install \
  tailwindcss@^4.0.0 \
  class-variance-authority@^0.7.0 \
  clsx@^2.0.0 \
  tailwind-merge@^2.2.0

npm install --save-dev \
  @tailwindcss/vite@^4.0.0
```

- [ ] Verify: `npx tailwindcss --version`

### Step 6: Install Orval
```bash
npm install --save-dev orval@^7.0.0
npm install axios@^1.6.0 @tanstack/react-query@^5.0.0 zod@^3.22.0
```

- [ ] Verify: `npx orval --version`

### Step 7: Install Shadcn/ui CLI
```bash
npm install --save-dev @shadcn-cli/init@^0.7.0
```

- [ ] Verify installation completed without errors

---

## Phase 2: Configuration Files (2-3 hours)

### Step 1: Create ESLint Configuration
- [ ] Copy `eslint.config.js` from CONFIGURATION_TEMPLATES.md to `/src/main/webui/`
- [ ] Create `.eslintignore` file
- [ ] Verify config: `npx eslint --print-config src/pages/index.astro | head -20`

### Step 2: Create Prettier Configuration
- [ ] Copy `.prettierrc.json` from templates
- [ ] Copy `.prettierignore` from templates
- [ ] Verify config: `npx prettier --find-config-path .`

### Step 3: Setup ESLint + Prettier Integration
- [ ] Confirm `eslint-config-prettier` is included in eslint.config.js
- [ ] Verify no conflicts: `npx eslint-config-prettier src/pages/index.astro`

### Step 4: Configure TypeScript
- [ ] Copy `tsconfig.json` from templates
- [ ] Add path mappings for `@/*`, `@components/*`, etc.
- [ ] Verify: `npx tsc --noEmit` (should complete without errors)

### Step 5: Setup Husky Pre-commit Hook
- [ ] Create `.husky/pre-commit` file
- [ ] Make executable: `chmod +x .husky/pre-commit`
- [ ] Copy `.lintstagedrc.json` from templates
- [ ] Test hook: `git commit --allow-empty -m "test"` (should run lint-staged)

### Step 6: Configure Astro
- [ ] Update `astro.config.mjs` with Preact and Tailwind integration
- [ ] Add Vite plugins for Tailwind CSS
- [ ] Verify: `npm run build` (should complete without errors)

### Step 7: Configure Tailwind CSS
- [ ] Copy `tailwind.config.mjs` from templates
- [ ] Create `src/styles/globals.css` with Tailwind imports
- [ ] Update layout to import globals.css
- [ ] Verify: `npm run build` (Tailwind should be compiled)

### Step 8: Setup Shadcn/ui
- [ ] Copy `components.json` from templates
- [ ] Run: `npx shadcn-cli@latest init`
- [ ] Add initial components: `npx shadcn-cli@latest add button`
- [ ] Verify `src/components/ui/button.tsx` created

### Step 9: Configure Orval
- [ ] Create `orval.config.ts` from templates
- [ ] Create `src/api/mutator.ts` with Axios configuration
- [ ] Create `api/openapi.json` (or download from backend)
- [ ] Verify: `npm run api:validate`

### Step 10: Setup Environment Variables
- [ ] Copy `.env.local` from templates
- [ ] Update API_BASE_URL to your environment
- [ ] Verify: `npm run dev` (should start without errors)

### Step 11: VS Code Configuration
- [ ] Copy `.vscode/settings.json` from templates
- [ ] Install recommended extensions:
  - [ ] ESLint (dbaeumer.vscode-eslint)
  - [ ] Prettier (esbenp.prettier-vscode)
  - [ ] Astro (astro-build.astro-vscode)
  - [ ] Tailwind IntelliSense (bradlc.vscode-tailwindcss)

---

## Phase 3: Testing & Verification (1-2 hours)

### Step 1: Test ESLint
```bash
# [ ] Run linting
npm run lint

# [ ] Fix linting errors
npm run lint:fix

# [ ] Check with zero warnings
npm run lint:check
```

Expected: No errors or warnings

### Step 2: Test Prettier
```bash
# [ ] Check formatting
npm run format:check

# [ ] Auto-format files
npm run format
```

Expected: All files properly formatted

### Step 3: Test Pre-commit Hook
```bash
# [ ] Stage a file
git add src/pages/index.astro

# [ ] Try to commit (hook should run)
git commit -m "test: verify pre-commit hook"
```

Expected: lint-staged runs and fixes issues

### Step 4: Test Tailwind CSS
- [ ] Check if styles are applied in dev: `npm run dev`
- [ ] Inspect element to verify Tailwind classes
- [ ] Build and check CSS output: `npm run build`

### Step 5: Test Shadcn/ui Components
- [ ] Create a test component using Button
- [ ] Verify button renders with proper styles
- [ ] Check class names are properly merged

### Step 6: Test Orval API Generation
```bash
# [ ] Ensure openapi.json exists
ls api/openapi.json

# [ ] Generate API client
npm run api:generate

# [ ] Verify files created
ls -la src/api/generated/
```

Expected: Generated files with proper TypeScript types

### Step 7: Full Build Test
```bash
# [ ] Run complete build
npm run build

# [ ] Check for errors
# [ ] Verify dist/ directory created
# [ ] Check bundle size is reasonable
```

Expected: Build completes successfully

### Step 8: Integration Test
- [ ] Create a simple Preact island component
- [ ] Use a generated API hook from Orval
- [ ] Use a Shadcn/ui Button component
- [ ] Use Tailwind classes for styling
- [ ] Test in dev and production build

Expected: All parts work together seamlessly

---

## Phase 4: Documentation & Team Setup (1 hour)

### Step 1: Document Setup
- [ ] Create `SETUP.md` in project root with installation steps
- [ ] Document any project-specific ESLint rules
- [ ] Create troubleshooting guide for common issues

### Step 2: Team Communication
- [ ] Share linting rules document
- [ ] Explain when to run `npm run lint:fix`
- [ ] Clarify pre-commit hook behavior
- [ ] Document how to add new Shadcn/ui components

### Step 3: CI/CD Integration
- [ ] Create `.github/workflows/lint.yml` for GitHub Actions
- [ ] Add build and test workflows
- [ ] Configure automated PR checks
- [ ] Set up branch protection rules

### Step 4: Documentation Files to Create
- [ ] `docs/linting.md` - ESLint rules and configuration
- [ ] `docs/formatting.md` - Prettier settings
- [ ] `docs/components.md` - How to use Shadcn/ui
- [ ] `docs/api-generation.md` - Orval workflow
- [ ] `docs/tailwind.md` - Tailwind customization

---

## Phase 5: Ongoing Maintenance

### Weekly Tasks
- [ ] Run `npm outdated` to check for updates
- [ ] Review new ESLint rules or Prettier changes
- [ ] Monitor pre-commit hook performance

### Monthly Tasks
- [ ] Update dependencies: `npm update`
- [ ] Run `npm audit` and fix vulnerabilities
- [ ] Review and update linting rules if needed
- [ ] Check for new Shadcn/ui components to adopt

### Quarterly Tasks
- [ ] Audit TypeScript strict mode compliance
- [ ] Review bundle size and optimization opportunities
- [ ] Evaluate new formatting/linting tools
- [ ] Update documentation as needed

---

## Common Issues & Solutions

### Issue: ESLint says "Cannot find module '@typescript-eslint/parser'"
**Solution:**
```bash
npm install --save-dev @typescript-eslint/parser@^7.0.0
```

### Issue: Prettier conflicts with ESLint
**Solution:**
1. Ensure `eslint-config-prettier` is installed
2. Verify it's last in eslint.config.js
3. Run: `npm run lint:fix && npm run format`

### Issue: Pre-commit hook not running
**Solution:**
```bash
npx husky install
chmod +x .husky/pre-commit
npm install  # Runs prepare script
```

### Issue: Astro files not linting properly
**Solution:**
1. Install `eslint-plugin-astro`: `npm install --save-dev eslint-plugin-astro`
2. Verify eslint.config.js includes astro config
3. Check VS Code ESLint extension has "astro" in validate list

### Issue: Tailwind CSS classes not working
**Solution:**
1. Verify `src/styles/globals.css` has `@import "tailwindcss"`
2. Confirm globals.css is imported in layout
3. Check `astro.config.mjs` includes `tailwindcss()` in Vite plugins
4. Rebuild: `npm run build`

### Issue: Orval code generation failing
**Solution:**
1. Verify OpenAPI schema exists: `ls api/openapi.json`
2. Validate schema: `npm run api:validate`
3. Check schema is valid JSON/YAML
4. Ensure proper OpenAPI v3 format

### Issue: Shadcn/ui components not styling properly
**Solution:**
1. Verify Tailwind CSS is working (see above)
2. Check `components.json` baseColor is correct
3. Ensure `src/lib/utils.ts` has `cn()` function
4. Re-add component: `npx shadcn-cli@latest add button --force`

---

## Verification Checklist

### Before First Commit
- [ ] `npm run lint:check` passes
- [ ] `npm run format:check` passes
- [ ] `npm run api:validate` passes
- [ ] `npm run build` succeeds
- [ ] No TypeScript errors: `npx tsc --noEmit`

### Before First PR
- [ ] All tests pass
- [ ] No console warnings/errors in dev
- [ ] Bundle size is reasonable
- [ ] Performance is acceptable

### Before Production Release
- [ ] All linting and formatting rules met
- [ ] Comprehensive API testing done
- [ ] Accessibility checks passed (a11y)
- [ ] Performance optimized
- [ ] Security audit passed: `npm audit`

---

## File Checklist

Make sure you have created the following files:

### Root Level (webui directory)
- [ ] `eslint.config.js`
- [ ] `.eslintignore`
- [ ] `.prettierrc.json`
- [ ] `.prettierignore`
- [ ] `.lintstagedrc.json`
- [ ] `tsconfig.json`
- [ ] `astro.config.mjs`
- [ ] `tailwind.config.mjs`
- [ ] `components.json`
- [ ] `orval.config.ts`
- [ ] `.env.local`
- [ ] `.gitignore`

### .husky Directory
- [ ] `.husky/pre-commit`
- [ ] `.husky/_/husky.sh` (auto-generated)

### .vscode Directory
- [ ] `.vscode/settings.json`
- [ ] `.vscode/extensions.json` (optional)

### src Directory
- [ ] `src/styles/globals.css`
- [ ] `src/lib/utils.ts`
- [ ] `src/api/mutator.ts`
- [ ] `src/components/ui/` (populated by shadcn-cli)

### api Directory
- [ ] `api/openapi.json`

---

## Performance Metrics to Track

After setup, monitor these metrics:

- **Pre-commit hook time**: Should be < 5 seconds
- **Build time**: Should be < 30 seconds (dev), < 2 minutes (prod)
- **Bundle size**: Astro component output should be < 100KB gzipped
- **Linting time**: Should be < 10 seconds for entire project
- **Formatting time**: Should be < 10 seconds for entire project

---

## Success Criteria

✓ **Phase 1 Complete When:**
- All npm packages installed without errors
- Node.js version is >= 18
- No dependency conflicts

✓ **Phase 2 Complete When:**
- All configuration files created and in place
- No TypeScript errors in config files
- All tools recognize their config files

✓ **Phase 3 Complete When:**
- All tests pass
- `npm run build` succeeds
- Pre-commit hook runs and fixes issues
- No console errors in dev mode

✓ **Phase 4 Complete When:**
- Documentation is written and clear
- Team understands setup and workflow
- CI/CD workflows are configured
- GitHub branch protection is set

✓ **Phase 5 Ongoing:**
- Dependencies are regularly updated
- Security audits are run monthly
- Team follows linting rules
- New components are properly documented

---

## Quick Reference Commands

```bash
# Development
npm run dev                 # Start dev server
npm run build              # Build for production

# Linting and Formatting
npm run lint               # Check ESLint
npm run lint:fix           # Fix ESLint issues
npm run format             # Format all files with Prettier
npm run format:check       # Check formatting without changes

# API
npm run api:generate       # Generate API client from OpenAPI
npm run api:validate       # Validate OpenAPI schema
npm run api:watch          # Watch for OpenAPI changes

# UI Components
npm run add:component      # Add shadcn/ui component

# Git Hooks
npx husky install          # Install Husky hooks
npx husky uninstall        # Remove Husky hooks

# Package Management
npm outdated               # Check for updates
npm audit                  # Security audit
npm update                 # Update dependencies
npm install                # Install all dependencies
```

---

## Timeline Estimate

| Phase | Duration | Effort |
|-------|----------|--------|
| Phase 1: Installation | 1-2 hours | Low |
| Phase 2: Configuration | 2-3 hours | Medium |
| Phase 3: Testing | 1-2 hours | Medium |
| Phase 4: Documentation | 1 hour | Low |
| Phase 5: Maintenance | Ongoing | Low |
| **Total** | **6-9 hours** | **Medium** |

---

## Support & Resources

- **ESLint Documentation**: https://eslint.org/docs/latest/
- **Prettier Documentation**: https://prettier.io/docs/en/
- **Astro Documentation**: https://docs.astro.build/
- **Tailwind CSS**: https://tailwindcss.com/
- **Shadcn/ui**: https://ui.shadcn.com/
- **Orval**: https://orval.dev/
- **TanStack Query**: https://tanstack.com/query/latest
- **Husky**: https://typicode.github.io/husky/
- **lint-staged**: https://github.com/lint-staged/lint-staged

