/**
 * QueryProvider Component
 *
 * Wraps components with TanStack Query QueryClientProvider to enable
 * React Query hooks throughout the application.
 *
 * This provider must wrap any component that uses React Query hooks
 * like useQuery, useMutation, etc.
 *
 * Usage:
 * ```tsx
 * import { QueryProvider } from '@/components/providers/QueryProvider';
 *
 * <QueryProvider>
 *   <YourComponent />
 * </QueryProvider>
 * ```
 */

import { QueryClient, QueryClientProvider } from '@tanstack/react-query';
import type { ComponentChildren } from 'preact';
import { useMemo } from 'preact/hooks';

interface QueryProviderProps {
  children: ComponentChildren;
}

/**
 * QueryProvider component that creates and provides a QueryClient instance
 * to all child components.
 */
export function QueryProvider({ children }: QueryProviderProps) {
  // Create QueryClient instance with default options
  // useMemo ensures the client is only created once per component instance
  const queryClient = useMemo(
    () =>
      new QueryClient({
        defaultOptions: {
          queries: {
            // Disable automatic refetching on window focus for better UX
            refetchOnWindowFocus: false,
            // Retry failed requests once
            retry: 1,
            // Cache data for 5 minutes
            staleTime: 5 * 60 * 1000,
          },
        },
      }),
    []
  );

  return (
    <QueryClientProvider client={queryClient}>
      {children}
    </QueryClientProvider>
  );
}
