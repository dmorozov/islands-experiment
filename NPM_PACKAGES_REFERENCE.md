# Task Manager Islands Architecture: npm Packages Reference

Quick reference for all npm packages needed for the complete setup.

---

## Complete package.json Dependencies

```json
{
  "name": "webui",
  "type": "module",
  "version": "0.0.1",
  "scripts": {
    "dev": "astro dev --port 3000",
    "build": "astro build",
    "preview": "astro preview",
    "astro": "astro",
    "lint": "eslint .",
    "lint:fix": "eslint . --fix",
    "lint:check": "eslint . --max-warnings=0",
    "format": "prettier --write \"src/**/*.{astro,js,jsx,ts,tsx,json,css}\"",
    "format:check": "prettier --check \"src/**/*.{astro,js,jsx,ts,tsx,json,css}\"",
    "api:generate": "orval --config orval.config.ts",
    "api:validate": "orval validate --config orval.config.ts",
    "api:watch": "orval --config orval.config.ts --watch",
    "prebuild": "npm run api:generate",
    "prepare": "husky install",
    "add:component": "shadcn-cli add"
  },
  "dependencies": {
    "astro": "^5.16.0",
    "tailwindcss": "^4.0.0",
    "axios": "^1.6.0",
    "@tanstack/react-query": "^5.0.0",
    "class-variance-authority": "^0.7.0",
    "clsx": "^2.0.0",
    "tailwind-merge": "^2.2.0",
    "zod": "^3.22.0"
  },
  "devDependencies": {
    "@astrojs/preact": "^4.0.0",
    "@eslint/js": "^9.0.0",
    "@shadcn-cli/init": "^0.7.0",
    "@tailwindcss/vite": "^4.0.0",
    "@typescript-eslint/eslint-plugin": "^7.0.0",
    "@typescript-eslint/parser": "^7.0.0",
    "eslint": "^9.0.0",
    "eslint-config-airbnb-base": "^15.0.0",
    "eslint-config-airbnb-typescript": "^18.0.0",
    "eslint-config-prettier": "^9.0.0",
    "eslint-plugin-astro": "^1.2.0",
    "eslint-plugin-import": "^2.29.0",
    "eslint-plugin-jsx-a11y": "^6.8.0",
    "eslint-plugin-preact": "^3.2.0",
    "globals": "^15.0.0",
    "husky": "^9.0.0",
    "lint-staged": "^15.0.0",
    "orval": "^7.0.0",
    "prettier": "^3.0.0",
    "prettier-plugin-astro": "^0.13.0",
    "prettier-plugin-tailwindcss": "^0.5.0",
    "typescript": "^5.5.0",
    "typescript-eslint": "^7.0.0"
  }
}
```

---

## Package Installation by Category

### Core Framework (Already in project.json)
```bash
npm install astro@^5.16.0
npm install @astrojs/preact@^4.0.0
npm install typescript@^5.5.0
```

### Linting & Type Checking
```bash
npm install --save-dev \
  eslint@^9.0.0 \
  @typescript-eslint/eslint-plugin@^7.0.0 \
  @typescript-eslint/parser@^7.0.0 \
  @eslint/js@^9.0.0 \
  globals@^15.0.0 \
  typescript-eslint@^7.0.0
```

### Framework-Specific ESLint
```bash
npm install --save-dev \
  eslint-plugin-astro@^1.2.0 \
  eslint-plugin-preact@^3.2.0 \
  eslint-plugin-jsx-a11y@^6.8.0 \
  eslint-plugin-import@^2.29.0 \
  eslint-config-airbnb-base@^15.0.0 \
  eslint-config-airbnb-typescript@^18.0.0
```

### Code Formatting
```bash
npm install --save-dev \
  prettier@^3.0.0 \
  prettier-plugin-astro@^0.13.0 \
  prettier-plugin-tailwindcss@^0.5.0 \
  eslint-config-prettier@^9.0.0
```

### Git Hooks
```bash
npm install --save-dev \
  husky@^9.0.0 \
  lint-staged@^15.0.0
```

### Styling
```bash
npm install \
  tailwindcss@^4.0.0 \
  class-variance-authority@^0.7.0 \
  clsx@^2.0.0 \
  tailwind-merge@^2.2.0

npm install --save-dev \
  @tailwindcss/vite@^4.0.0
```

### API Client & State Management
```bash
npm install \
  axios@^1.6.0 \
  @tanstack/react-query@^5.0.0 \
  zod@^3.22.0

npm install --save-dev \
  orval@^7.0.0
```

### UI Components
```bash
npm install --save-dev \
  @shadcn-cli/init@^0.7.0
```

---

## Verification Commands

```bash
# Verify all packages are installed
npm ls

# Check for outdated packages
npm outdated

# Audit for vulnerabilities
npm audit

# Check ESLint version
npx eslint --version

# Check Prettier version
npx prettier --version

# Check Orval version
npx orval --version

# Check Husky setup
npx husky --version
```

---

## Quick Install Script

Save as `setup-packages.sh` and run with `bash setup-packages.sh`:

```bash
#!/bin/bash

echo "Installing Task Manager Islands Architecture packages..."

# Install dependencies
npm install

# Install dev dependencies - Linting
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

# Install formatting
npm install --save-dev \
  prettier@^3.0.0 \
  prettier-plugin-astro@^0.13.0 \
  prettier-plugin-tailwindcss@^0.5.0 \
  eslint-config-prettier@^9.0.0

# Install git hooks
npm install --save-dev \
  husky@^9.0.0 \
  lint-staged@^15.0.0

# Install Tailwind
npm install \
  tailwindcss@^4.0.0 \
  class-variance-authority@^0.7.0 \
  clsx@^2.0.0 \
  tailwind-merge@^2.2.0

npm install --save-dev \
  @tailwindcss/vite@^4.0.0

# Install API & state management
npm install \
  axios@^1.6.0 \
  @tanstack/react-query@^5.0.0 \
  zod@^3.22.0

npm install --save-dev \
  orval@^7.0.0

# Install UI components
npm install --save-dev \
  @shadcn-cli/init@^0.7.0

# Initialize Husky
npx husky install

echo "Setup complete!"
echo ""
echo "Next steps:"
echo "1. Initialize Prettier: (already configured via .prettierrc.json)"
echo "2. Setup Shadcn/ui: npx shadcn-cli@latest init"
echo "3. Add Tailwind: Update astro.config.mjs with @tailwindcss/vite"
echo "4. Generate API: npm run api:generate"
```

---

## Version Compatibility Matrix

| Package | Min Version | Current | Max Version |
|---------|------------|---------|------------|
| Node.js | 18.0.0 | 20+ | - |
| Astro | 5.0.0 | 5.16.0 | 6.0.0 |
| TypeScript | 5.0.0 | 5.5.0 | - |
| ESLint | 9.0.0 | 9.x | 10.0.0 |
| Prettier | 3.0.0 | 3.x | 4.0.0 |
| Tailwind CSS | 4.0.0 | 4.x | - |
| @tanstack/react-query | 5.0.0 | 5.x | 6.0.0 |
| Orval | 7.0.0 | 7.x | 8.0.0 |

---

## npm Scripts Summary

| Script | Purpose |
|--------|---------|
| `npm run dev` | Start Astro dev server on port 3000 |
| `npm run build` | Build production bundle |
| `npm run preview` | Preview production build locally |
| `npm run lint` | Run ESLint checks |
| `npm run lint:fix` | Auto-fix ESLint issues |
| `npm run lint:check` | ESLint with no warnings tolerance |
| `npm run format` | Format all files with Prettier |
| `npm run format:check` | Check Prettier formatting without changes |
| `npm run api:generate` | Generate API client from OpenAPI schema |
| `npm run api:validate` | Validate OpenAPI schema |
| `npm run api:watch` | Watch for OpenAPI schema changes |
| `npm run prebuild` | Runs before build (generates API) |
| `npm run prepare` | Install Husky hooks (runs on npm install) |
| `npm run add:component` | Add shadcn/ui components |

---

## Important Notes

### Node.js Version
All packages support Node.js 18+. Recommend Node.js 20+ for best compatibility.

### Breaking Changes (v2024)
- ESLint 9.0 moved from `.eslintrc.json` to `eslint.config.js` (flat config)
- Tailwind CSS 4.0 introduced new `@tailwindcss/vite` plugin
- TanStack Query v5 requires different configuration than v4

### Optional but Recommended
- `zod` - TypeScript-first schema validation (for OpenAPI validation)
- `class-variance-authority` - Type-safe CSS class composition for Shadcn UI components
- `tailwind-merge` - Utility for merging Tailwind CSS classes without conflicts

### Security Considerations
Run `npm audit` regularly to check for vulnerabilities:
```bash
# Regular audit
npm audit

# Fix automatically where possible
npm audit fix

# Fix including breaking changes (use with caution)
npm audit fix --force
```

---

## Monorepo Considerations

For Maven + npm integration, the npm packages should be installed in:
```
/workspaces/quarkus-astro-app/src/main/webui/
```

The root `package.json` for Husky should be at:
```
/workspaces/quarkus-astro-app/src/main/webui/package.json
```

Pre-commit hooks in `.husky/` will trigger for the webui directory when configured properly.

---

## Troubleshooting Package Issues

### Issue: npm ERR! peer dep missing
**Solution**: Install missing peer dependencies
```bash
npm install <package-name>
```

### Issue: ESLint parser errors
**Solution**: Ensure all @typescript-eslint packages are at same version
```bash
npm install --save-dev @typescript-eslint/eslint-plugin@^7.0.0 @typescript-eslint/parser@^7.0.0
```

### Issue: Out of memory during build
**Solution**: Increase Node.js memory
```bash
NODE_OPTIONS="--max-old-space-size=4096" npm run build
```

### Issue: Prettier conflicts with Astro formatting
**Solution**: Ensure `prettier-plugin-astro` is installed
```bash
npm install --save-dev prettier-plugin-astro@^0.13.0
```

