import { useGetApiSessionUser } from '@/lib/api/endpoints/session/session';

/**
 * Hook to manage session state.
 * Returns current user information and authentication status.
 */
export function useSession() {
  const {
    data: user,
    isLoading,
    error,
    refetch,
  } = useGetApiSessionUser({
    query: {
      retry: false, // Don't retry if unauthorized
      refetchOnWindowFocus: false,
    },
  });

  const isAuthenticated = !!user && !error;

  return {
    user,
    isAuthenticated,
    isLoading,
    error,
    refetch,
  };
}
