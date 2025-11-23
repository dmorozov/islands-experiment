import { useState, useEffect } from 'preact/hooks';
import { trackIslandHydration } from '@/lib/performance';
import { Button } from '@/components/ui/button';
import TaskForm from './TaskForm';
import {
  Dialog,
  DialogContent,
  DialogDescription,
  DialogHeader,
  DialogTitle,
  DialogTrigger,
} from '@/components/ui/dialog';
import { QueryProvider } from '@/components/providers/QueryProvider';

/**
 * NewTaskButton Island (T335-T338)
 *
 * Renders a button that opens a modal with TaskForm in create mode.
 */
function NewTaskButtonContent() {
  const [isOpen, setIsOpen] = useState(false);

  // Track island hydration for performance monitoring
  useEffect(() => {
    trackIslandHydration('NewTaskButton');
  }, []);

  // T338: After task created, close modal
  const handleSuccess = () => {
    setIsOpen(false);
  };

  return (
    <Dialog open={isOpen} onOpenChange={setIsOpen}>
      {/* T335: "New Task" button in header */}
      <DialogTrigger asChild>
        <Button size="lg">
          <svg
            class="mr-2 h-5 w-5"
            fill="none"
            stroke="currentColor"
            viewBox="0 0 24 24"
            xmlns="http://www.w3.org/2000/svg"
          >
            <path
              stroke-linecap="round"
              stroke-linejoin="round"
              stroke-width="2"
              d="M12 4v16m8-8H4"
            ></path>
          </svg>
          New Task
        </Button>
      </DialogTrigger>

      {/* T337: Show TaskForm in modal with mode="create" */}
      <DialogContent class="max-w-2xl">
        <DialogHeader>
          <DialogTitle>Create New Task</DialogTitle>
          <DialogDescription>
            Add a new task to your task list. Fill in the details below.
          </DialogDescription>
        </DialogHeader>

        <TaskForm mode="create" onSuccess={handleSuccess} onCancel={() => setIsOpen(false)} />
      </DialogContent>
    </Dialog>
  );
}

/**
 * Main NewTaskButton component wrapped with QueryProvider
 */
export default function NewTaskButton() {
  return (
    <QueryProvider>
      <NewTaskButtonContent />
    </QueryProvider>
  );
}
