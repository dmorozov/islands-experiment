import { defineConfig } from 'orval';

/**
 * Orval Configuration for Task Manager API Client Generation
 *
 * This configuration generates a TypeScript client from the OpenAPI schema
 * produced by Quarkus. It uses:
 * - TanStack Query v5 for data fetching and caching
 * - Axios for HTTP requests with custom mutator for auth and error handling
 * - Zod for runtime validation (optional)
 *
 * Workflow:
 * 1. Quarkus exports OpenAPI schema to ./api/openapi.json
 * 2. Run `npm run generate:api`
 * 3. Orval generates TypeScript client to ./src/lib/api/
 * 4. Import generated hooks in components: `import { useGetTasks } from '@/lib/api'`
 */

export default defineConfig({
  taskmanager: {
    input: {
      target: './api/openapi.json',
    },
    output: {
      mode: 'tags-split',
      target: './src/lib/api/endpoints',
      schemas: './src/lib/api/model',
      client: 'react-query',
      httpClient: 'axios',
      baseUrl: 'http://localhost:7171',
      override: {
        mutator: {
          path: './src/api/mutator.ts',
          name: 'customInstance',
        },
        query: {
          useQuery: true,
          useMutation: true,
          signal: true,
        },
      },
      clean: true,
      prettier: true,
    },
    hooks: {
      afterAllFilesWrite: 'prettier --write',
    },
  },
});
