# Islands architecture experiment

## Project folders

Quarkus Quinoa with Astro front-end is located in quarkus-astro-app folder.
The Astro application is in the quarkus-astro-app/src/main/webui folder.

## Frameworks

1. [Astro](https://astro.build/), [Astro Installation](https://docs.astro.build/en/install-and-setup/)
2. [Preact](https://preactjs.com/)
3. [Using Preact with Typescript](https://preactjs.com/guide/v10/typescript/)
4. [Islands Architecture](https://docs.astro.build/en/concepts/islands/)
5. [Astro with Preact](https://docs.astro.build/en/guides/integrations-guide/preact/)
6. [Astro with Typescript](https://docs.astro.build/en/guides/typescript/)
7. [Shadcn UI](https://ui.shadcn.com/)
8. [Shadcn UI with Astro](https://ui.shadcn.com/docs/installation/astro)
9. [Tailwind configuration with Astro](https://docs.astro.build/en/guides/styling/#tailwind)
10. Prettier and ESLint configuration with best practices for the selected tech stack.
11. Use [TanStack Query](https://tanstack.com/query/latest) with Axios for the calls to the server API.
12. Use [Orval](https://orval.dev/overview) to generate client with TanStack Query and Axios from OpenAI schema yaml.
13. To run the application:

```bash
quarkus dev
```

15. When application is started the Astro application is available at: http://localhost:7171/
16. To build the whole application:

```bash
./mvnw clean package
```

## Performance & Islands Architecture

This project demonstrates the performance benefits of Islands Architecture through a real-world task management application.

### Performance Metrics

Visit `/performance` to see live performance metrics including:

- **Web Vitals**: FCP, LCP, and TTI measurements
- **Island Hydration**: Individual component hydration times
- **Bundle Sizes**: JavaScript payload analysis
- **Architecture Comparison**: Islands vs Traditional SPA

### Bundle Size Analysis

Production build measurements (gzipped):

- Total JavaScript (excluding charts): ~85 KB
- Recharts library: ~102 KB
- Individual islands: 2-20 KB each

### Key Performance Features

1. **Selective Hydration**: Only interactive components load JavaScript
2. **Progressive Enhancement**: Static HTML loads first, islands hydrate independently
3. **Code Splitting**: Each island is bundled separately
4. **Minimal Runtime**: Preact reduces runtime overhead by ~70% vs React (4KB vs 45KB)
5. **Static Generation**: Pages pre-rendered as HTML at build time

### Performance Targets

- First Contentful Paint (FCP): < 1.5s
- Largest Contentful Paint (LCP): < 2.5s
- Time to Interactive (TTI): < 3.8s
- Island Hydration: < 200ms per island
- Bundle Size: < 100KB (gzipped, excluding heavy libraries)

### Optimization Techniques Used

- **Lazy Loading**: Islands can load on-demand with `client:visible` or `client:idle`
- **Hydration Tracking**: Performance API measures when islands become interactive
- **Preact Runtime**: Lightweight alternative to React
- **Tree Shaking**: Unused code eliminated during build
- **Asset Optimization**: Images and static assets optimized

### Measuring Performance

1. **Development Metrics**: Visit `/performance` while running `quarkus dev`
2. **Production Build**: Run `npm run build` in `src/main/webui/` to see bundle sizes
3. **Lighthouse Audit**: Run Lighthouse on the deployed application
4. **Export Metrics**: Use the "Export JSON" button on the performance page

### Hydration Visualizer

The performance page includes an optional Hydration Visualizer that shows:

- Visual indicators when islands hydrate
- Timeline showing hydration order
- Duration measurements for each island
- Real-time status updates

Enable it on the `/performance` page to see islands light up as they become interactive.

## Using AI

### SpecKit

```bash
uv tool install specify-cli --from git+https://github.com/github/spec-kit.git
```

Initialize for the project:

```bash
specify init .
specify check
```

### Gemini CLI

Run instantly with npx:

```bash
npx https://github.com/google-gemini/gemini-cli
```

Install globally with npm:

```bash
npm install -g @google/gemini-cli
```

Install globally with Homebrew:

```bash
brew install gemini-cli
```

VSCode extension: Google.gemini-cli-vscode-ide-companion

### Claude Code (cli)

Global using NPM:

```bash
npm install -g @anthropic-ai/claude-code
```

or using curl:

```bash
curl -fsSL https://claude.ai/install.sh | bash
```

or MacOS:

```bash
brew install --cask claude-code
```

VS Code extension: Anthropic.claude-code

Hint: claude --dangerously-skip-permissions
that will skip too many asking for permissions.

### Code refactoring with SpecKit

Start using slash commands with your AI agent:

1. /speckit.constitution - Establish project principles
2. /speckit.specify - Create baseline specification
3. /speckit.plan - Create implementation plan
4. /speckit.tasks - Generate actionable tasks
5. /speckit.implement - Execute implementation

Optional commands that you can use for your specs (improve quality & confidence)

- /speckit.clarify (optional) - Ask structured questions to de-risk ambiguous areas before planning (run before /speckit.plan if used)
- /speckit.analyze (optional) - Cross-artifact consistency & alignment report (after /speckit.tasks, before /speckit.implement)
- /speckit.checklist (optional) - Generate quality checklists to validate requirements completeness, clarity, and consistency (after /speckit.plan)

### Additional Resources:

- [SpecKit Documentation](https://github.com/github/spec-kit)
- [Gemini CLI Documentation](https://ai.google.dev/gemini-api/docs)
- [Claude Code Documentation](https://claude.ai/help/claude_code)
