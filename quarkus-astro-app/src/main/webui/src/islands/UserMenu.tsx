import { useSession } from '@/lib/hooks/useSession';
import { usePostApiSessionLogout } from '@/lib/api/endpoints/session/session';
import { Button } from '@/components/ui/button';

/**
 * UserMenu island component.
 * Shows username and logout button when authenticated.
 * Shows login link when not authenticated.
 */
export default function UserMenu() {
  const { user, isAuthenticated, isLoading } = useSession();

  const logoutMutation = usePostApiSessionLogout({
    mutation: {
      onSuccess: () => {
        // Redirect to login page after logout
        window.location.href = '/login';
      },
      onError: (err) => {
        console.error('Logout error:', err);
        // Still redirect even if there's an error
        window.location.href = '/login';
      },
    },
  });

  const handleLogout = () => {
    logoutMutation.mutate();
  };

  if (isLoading) {
    return (
      <div className="flex items-center gap-2">
        <div className="bg-muted h-4 w-20 animate-pulse rounded"></div>
      </div>
    );
  }

  if (!isAuthenticated) {
    return (
      <a
        href="/login"
        className="text-muted-foreground hover:text-foreground text-sm font-medium transition-colors"
      >
        Login
      </a>
    );
  }

  return (
    <div className="flex items-center gap-3">
      <span className="text-muted-foreground text-sm font-medium">{user?.username}</span>
      <Button
        variant="outline"
        size="sm"
        onClick={handleLogout}
        disabled={logoutMutation.isPending}
      >
        {logoutMutation.isPending ? 'Logging out...' : 'Logout'}
      </Button>
    </div>
  );
}
