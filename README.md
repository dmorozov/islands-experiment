# Islands architecture experiment

## Frameworks

1. Quarkus Quinoa with Astro in quarkus-astro-app folder. The Astro application is in the quarkus-astro-app/src/main/webui folder.
2. [Astro](https://astro.build/), [Astro Installation](https://docs.astro.build/en/install-and-setup/)
3. [Preact](https://preactjs.com/)
4. [Using Preact with Typescript](https://preactjs.com/guide/v10/typescript/)
5. [Islands Architecture](https://docs.astro.build/en/concepts/islands/)
6. [Astro with Preact](https://docs.astro.build/en/guides/integrations-guide/preact/)
7. [Astro with Typescript](https://docs.astro.build/en/guides/typescript/)
8. [Shadcn UI](https://ui.shadcn.com/)
9. [Shadcn UI with Astro](https://ui.shadcn.com/docs/installation/astro)
10. [Tailwind configuration with Astro](https://docs.astro.build/en/guides/styling/#tailwind)
11. Prettier and ESLint configuration with best practices for the selected tech stack.
12. Use [TanStack Query](https://tanstack.com/query/latest) with Axios for the calls to the server API.
13. Use [Orval](https://orval.dev/overview) to generate client with TanStack Query and Axios from OpenAI schema yaml.
14. To run the application:

```bash
quarkus dev
```

15. When application is started the Astro application is available at: http://localhost:7171/
16. To build the whole application:

```bash
./mvnw clean package
```

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
