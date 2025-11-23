import { useState } from 'preact/hooks';
import { usePostApiSessionLogin } from '@/lib/api/endpoints/session/session';
import { Button } from '@/components/ui/button';
import { Input } from '@/components/ui/input';
import { Label } from '@/components/ui/label';

interface LoginProps {
  onLoginSuccess?: () => void;
}

/**
 * Login island component for demo authentication.
 * Accepts username only (no password).
 */
export default function Login({ onLoginSuccess }: LoginProps) {
  const [username, setUsername] = useState('');
  const [error, setError] = useState<string | null>(null);

  const loginMutation = usePostApiSessionLogin({
    mutation: {
      onSuccess: () => {
        setError(null);
        // Reload the page to refresh the session state
        if (onLoginSuccess) {
          onLoginSuccess();
        } else {
          window.location.href = '/';
        }
      },
      onError: (err) => {
        console.error('Login error:', err);
        setError('Failed to login. Please try again.');
      },
    },
  });

  const handleSubmit = (e: Event) => {
    e.preventDefault();

    if (!username.trim()) {
      setError('Please enter a username');
      return;
    }

    if (username.length > 50) {
      setError('Username must be 50 characters or less');
      return;
    }

    setError(null);
    loginMutation.mutate({ data: { username: username.trim() } });
  };

  return (
    <div className="mx-auto w-full max-w-md">
      <div className="bg-card rounded-lg p-8 shadow-md">
        <h2 className="mb-6 text-center text-2xl font-bold">Task Manager Login</h2>
        <p className="text-muted-foreground mb-6 text-center text-sm">
          Demo mode - enter any username to continue
        </p>

        <form onSubmit={handleSubmit} className="space-y-4">
          <div className="space-y-2">
            <Label htmlFor="username">Username</Label>
            <Input
              id="username"
              type="text"
              placeholder="Enter your username"
              value={username}
              onInput={(e) => setUsername((e.target as HTMLInputElement).value)}
              disabled={loginMutation.isPending}
              maxLength={50}
              autoFocus
            />
          </div>

          {error && (
            <div className="text-destructive bg-destructive/10 rounded p-3 text-sm">{error}</div>
          )}

          <Button type="submit" className="w-full" disabled={loginMutation.isPending}>
            {loginMutation.isPending ? 'Logging in...' : 'Login'}
          </Button>
        </form>

        <div className="text-muted-foreground mt-6 text-center text-sm">
          <p>No password required - this is a demo application</p>
        </div>
      </div>
    </div>
  );
}
