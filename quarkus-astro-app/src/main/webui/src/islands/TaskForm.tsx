import { useState, useEffect } from 'preact/hooks';
import { trackIslandHydration } from '@/lib/performance';
import { useQueryClient } from '@tanstack/react-query';
import {
  usePostApiTasks,
  usePutApiTasksId,
  getGetApiTasksQueryKey,
} from '@/lib/api/endpoints/tasks/tasks';
import { useGetApiCategories } from '@/lib/api/endpoints/categories/categories';
import type { TaskResponseDTO } from '@/lib/api/model';
import { Button } from '@/components/ui/button';
import { Input } from '@/components/ui/input';
import { Textarea } from '@/components/ui/textarea';
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from '@/components/ui/select';
import { Label } from '@/components/ui/label';

/**
 * TaskForm Island - Create and Edit Tasks (T309-T326).
 *
 * This component supports both create and edit modes with optimistic UI updates.
 *
 * @param mode - "create" or "edit" mode
 * @param initialTask - Task data to pre-fill form (for edit mode)
 * @param onSuccess - Callback when task is successfully created/updated
 * @param onCancel - Callback when form is cancelled
 */
interface TaskFormProps {
  mode: 'create' | 'edit';
  initialTask?: TaskResponseDTO;
  onSuccess?: () => void;
  onCancel?: () => void;
}

export default function TaskForm({
  mode,
  initialTask,
  onSuccess,
  onCancel,
}: TaskFormProps) {
  const queryClient = useQueryClient();

  // T313: Form state using Preact useState
  const [title, setTitle] = useState('');
  const [description, setDescription] = useState('');
  const [categoryId, setCategoryId] = useState('');
  const [priority, setPriority] = useState<'HIGH' | 'MEDIUM' | 'LOW'>('MEDIUM');

  // T311: Import useGetCategories hook for category dropdown
  const { data: categories = [], isLoading: categoriesLoading } =
    useGetApiCategories();

  // T310: Import useCreateTask and useUpdateTask hooks
  const createMutation = usePostApiTasks();
  const updateMutation = usePutApiTasksId();

  // T537: Track island hydration for performance monitoring
  useEffect(() => {
    trackIslandHydration('TaskForm');
  }, []);

  // T315: If edit mode, accept initialTask prop and pre-fill form fields
  useEffect(() => {
    if (mode === 'edit' && initialTask) {
      setTitle(initialTask.title || '');
      setDescription(initialTask.description || '');
      setCategoryId(initialTask.category?.id || '');
      setPriority(initialTask.priority || 'MEDIUM');
    }
  }, [mode, initialTask]);

  // T320: Handle form submission
  const handleSubmit = async (e: Event) => {
    e.preventDefault();

    // Validate title required
    if (!title.trim()) {
      alert('Title is required');
      return;
    }

    if (!categoryId) {
      alert('Please select a category');
      return;
    }

    try {
      if (mode === 'create') {
        // T321: Call createTask mutation
        await createMutation.mutateAsync({
          data: {
            title: title.trim(),
            description: description?.trim() || '',
            categoryId,
            priority,
          },
        });

        // T322: Handle mutation success - clear form if create mode
        setTitle('');
        setDescription('');
        setPriority('MEDIUM');
      } else if (mode === 'edit' && initialTask) {
        // Call updateTask mutation
        await updateMutation.mutateAsync({
          id: initialTask.id,
          data: {
            title: title.trim(),
            description: description?.trim(),
            categoryId,
            priority,
          },
        });
      }

      // T322: Invalidate tasks query cache to refetch
      queryClient.invalidateQueries({
        queryKey: getGetApiTasksQueryKey(),
      });

      // Call success callback
      if (onSuccess) {
        onSuccess();
      }
    } catch (error) {
      // T323: Handle mutation error - show user-friendly error message
      console.error('Failed to save task:', error);
      alert('Unable to save task. Please try again.');
    }
  };

  // T325: Add Cancel button to reset form or close edit mode
  const handleCancel = () => {
    if (mode === 'create') {
      setTitle('');
      setDescription('');
      setPriority('MEDIUM');
    }
    if (onCancel) {
      onCancel();
    }
  };

  const isLoading = createMutation.isPending || updateMutation.isPending;

  return (
    <form onSubmit={handleSubmit} class="space-y-4 p-4 border rounded-lg">
      {/* T316: Render title input with required validation */}
      <div class="space-y-2">
        <Label htmlFor="task-title">
          Title <span class="text-red-500">*</span>
        </Label>
        <Input
          id="task-title"
          type="text"
          value={title}
          onInput={(e) => setTitle((e.target as HTMLInputElement).value)}
          placeholder="Enter task title"
          required
          maxLength={200}
          disabled={isLoading}
        />
      </div>

      {/* T317: Render description textarea with max length 2000 */}
      <div class="space-y-2">
        <Label htmlFor="task-description">Description</Label>
        <Textarea
          id="task-description"
          value={description}
          onInput={(e) =>
            setDescription((e.target as HTMLTextAreaElement).value)
          }
          placeholder="Enter task description"
          maxLength={2000}
          rows={4}
          disabled={isLoading}
        />
      </div>

      {/* T318: Render category dropdown populated from useGetCategories() */}
      <div class="space-y-2">
        <Label htmlFor="task-category">
          Category <span class="text-red-500">*</span>
        </Label>
        <Select
          value={categoryId}
          onValueChange={setCategoryId}
          disabled={isLoading || categoriesLoading}
        >
          <SelectTrigger id="task-category">
            <SelectValue placeholder="Select a category" />
          </SelectTrigger>
          <SelectContent>
            {categories.map((category) => (
              <SelectItem key={category.id} value={category.id}>
                <span
                  class="inline-block w-3 h-3 rounded-full mr-2"
                  style={{ backgroundColor: category.colorCode }}
                ></span>
                {category.name}
              </SelectItem>
            ))}
          </SelectContent>
        </Select>
      </div>

      {/* T319: Render priority dropdown with HIGH/MEDIUM/LOW options */}
      <div class="space-y-2">
        <Label htmlFor="task-priority">
          Priority <span class="text-red-500">*</span>
        </Label>
        <Select value={priority} onValueChange={(value) => setPriority(value as 'HIGH' | 'MEDIUM' | 'LOW')} disabled={isLoading}>
          <SelectTrigger id="task-priority">
            <SelectValue placeholder="Select priority" />
          </SelectTrigger>
          <SelectContent>
            <SelectItem value="HIGH">
              <span class="text-red-600 font-semibold">High</span>
            </SelectItem>
            <SelectItem value="MEDIUM">
              <span class="text-yellow-600 font-semibold">Medium</span>
            </SelectItem>
            <SelectItem value="LOW">
              <span class="text-green-600 font-semibold">Low</span>
            </SelectItem>
          </SelectContent>
        </Select>
      </div>

      {/* T324: Add loading state to submit button, T325: Add Cancel button */}
      <div class="flex gap-2 justify-end">
        <Button
          type="button"
          variant="outline"
          onClick={handleCancel}
          disabled={isLoading}
        >
          Cancel
        </Button>
        <Button type="submit" disabled={isLoading}>
          {isLoading
            ? 'Saving...'
            : mode === 'create'
              ? 'Create Task'
              : 'Update Task'}
        </Button>
      </div>
    </form>
  );
}
