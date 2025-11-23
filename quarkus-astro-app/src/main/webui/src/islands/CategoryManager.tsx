import { useState, useEffect } from 'preact/hooks';
import { trackIslandHydration } from '@/lib/performance';
import { useQueryClient } from '@tanstack/react-query';
import {
  useGetApiCategories,
  usePostApiCategories,
  usePutApiCategoriesId,
  useDeleteApiCategoriesId,
  getGetApiCategoriesQueryKey,
} from '../lib/api/endpoints/categories/categories';
import type { CategoryResponseDTO, CategoryCreateDTO, CategoryUpdateDTO } from '../lib/api/model';
import { Button } from '../components/ui/button';
import { Input } from '../components/ui/input';
import { Label } from '../components/ui/label';
import {
  Dialog,
  DialogContent,
  DialogDescription,
  DialogHeader,
  DialogTitle,
  DialogTrigger,
} from '../components/ui/dialog';
import {
  AlertDialog,
  AlertDialogAction,
  AlertDialogCancel,
  AlertDialogContent,
  AlertDialogDescription,
  AlertDialogFooter,
  AlertDialogHeader,
  AlertDialogTitle,
} from '../components/ui/alert-dialog';
import { Badge } from '../components/ui/badge';
import { QueryProvider } from '../components/providers/QueryProvider';

/**
 * CategoryManager Island - Category CRUD UI
 * T394-T406: List, create, update, and delete categories with visual indicators
 */
function CategoryManagerContent() {
  // T539: Track island hydration for performance monitoring
  useEffect(() => {
    trackIslandHydration('CategoryManager');
  }, []);

  const queryClient = useQueryClient();
  const { data: categories = [], isLoading } = useGetApiCategories();
  const createMutation = usePostApiCategories();
  const updateMutation = usePutApiCategoriesId();
  const deleteMutation = useDeleteApiCategoriesId();

  const [isCreateDialogOpen, setIsCreateDialogOpen] = useState(false);
  const [editingId, setEditingId] = useState<string | null>(null);
  const [deletingCategory, setDeletingCategory] = useState<CategoryResponseDTO | null>(null);

  // Form state for creating
  const [newName, setNewName] = useState('');
  const [newColor, setNewColor] = useState('#3B82F6');

  // Form state for editing
  const [editName, setEditName] = useState('');
  const [editColor, setEditColor] = useState('');

  // Error states
  const [createError, setCreateError] = useState<string | null>(null);
  const [editError, setEditError] = useState<string | null>(null);
  const [deleteError, setDeleteError] = useState<string | null>(null);

  /**
   * T401: Handle category creation
   */
  const handleCreate = async () => {
    try {
      setCreateError(null);
      const dto: CategoryCreateDTO = {
        name: newName.trim(),
        colorCode: newColor,
      };

      await createMutation.mutateAsync({ data: dto });

      // Invalidate categories query cache
      void queryClient.invalidateQueries({ queryKey: getGetApiCategoriesQueryKey() });

      // Reset form
      setNewName('');
      setNewColor('#3B82F6');

      // Add delay before closing to allow DOM updates to complete
      setTimeout(() => {
        setIsCreateDialogOpen(false);
      }, 100);
    } catch (error: unknown) {
      // T405: Display error if duplicate category name
      if (error && typeof error === 'object' && 'status' in error && error.status === 409) {
        setCreateError('Category name already exists');
      } else {
        setCreateError('Failed to create category. Please try again.');
      }
    }
  };

  /**
   * T402: Handle category update (inline editing)
   */
  const startEditing = (category: CategoryResponseDTO) => {
    setEditingId(category.id);
    setEditName(category.name);
    setEditColor(category.colorCode ??  '#3B82F6');
    setEditError(null);
  };

  const cancelEditing = () => {
    setEditingId(null);
    setEditName('');
    setEditColor('');
    setEditError(null);
  };

  const handleUpdate = async (id: string) => {
    try {
      setEditError(null);
      const dto: CategoryUpdateDTO = {
        name: editName.trim(),
        colorCode: editColor,
      };

      await updateMutation.mutateAsync({ id, data: dto });

      // T406: Invalidate categories query cache
      void queryClient.invalidateQueries({ queryKey: getGetApiCategoriesQueryKey() });

      // Reset editing state
      setEditingId(null);
      setEditName('');
      setEditColor('');
    } catch (error: unknown) {
      // T405: Display error if duplicate category name
      if (error && typeof error === 'object' && 'status' in error && error.status === 409) {
        setEditError('Category name already exists');
      } else {
        setEditError('Failed to update category. Please try again.');
      }
    }
  };

  /**
   * T403: Handle category deletion with confirmation
   */
  const handleDelete = async (category: CategoryResponseDTO) => {
    setDeletingCategory(category);
    setDeleteError(null);
  };

  const confirmDelete = async () => {
    if (!deletingCategory) return;

    try {
      await deleteMutation.mutateAsync({ id: deletingCategory.id });

      // T406: Invalidate categories query cache
      void queryClient.invalidateQueries({ queryKey: getGetApiCategoriesQueryKey() });

      // Add delay before closing to allow DOM updates to complete
      setTimeout(() => {
        setDeletingCategory(null);
      }, 100);
    } catch (error: unknown) {
      // T404: Display error if trying to delete default category
      if (error && typeof error === 'object' && 'status' in error && error.status === 400) {
        setDeleteError('Cannot delete default categories');
      } else {
        setDeleteError('Failed to delete category. Please try again.');
      }
    }
  };

  if (isLoading) {
    return (
      <div class="flex items-center justify-center py-8">
        <p class="text-muted-foreground">Loading categories...</p>
      </div>
    );
  }

  return (
    <div class="space-y-6">
      {/* T398: "New Category" button */}
      <div class="flex items-center justify-between">
        <h2 class="text-2xl font-semibold tracking-tight">Categories</h2>
        <Dialog open={isCreateDialogOpen} onOpenChange={setIsCreateDialogOpen}>
          <DialogTrigger asChild>
            <Button>New Category</Button>
          </DialogTrigger>
          <DialogContent
            onCloseAutoFocus={(e: Event) => {
              // Prevent auto-focus behavior that causes errors
              e.preventDefault();
            }}
          >
            <DialogHeader>
              <DialogTitle>Create New Category</DialogTitle>
              <DialogDescription>
                Add a custom category with a unique name and color.
              </DialogDescription>
            </DialogHeader>
            {/* T399: Inline form for category: name input, color picker */}
            <div class="space-y-4">
              <div class="space-y-2">
                <Label htmlFor="new-name">Category Name</Label>
                <Input
                  id="new-name"
                  value={newName}
                  onInput={(e) => setNewName((e.target as HTMLInputElement).value)}
                  placeholder="e.g., Health & Fitness"
                  maxLength={50}
                />
              </div>
              <div class="space-y-2">
                {/* T400: Color picker using Shadcn Input with type="color" */}
                <Label htmlFor="new-color">Color</Label>
                <div class="flex items-center gap-2">
                  <Input
                    id="new-color"
                    type="color"
                    value={newColor}
                    onInput={(e) => setNewColor((e.target as HTMLInputElement).value)}
                    class="h-10 w-20"
                  />
                  <Input
                    value={newColor}
                    onInput={(e) => setNewColor((e.target as HTMLInputElement).value)}
                    placeholder="#3B82F6"
                    maxLength={7}
                    pattern="^#[0-9A-Fa-f]{6}$"
                    class="flex-1"
                  />
                </div>
              </div>
              {createError && (
                <p class="text-sm text-destructive">{createError}</p>
              )}
              <div class="flex justify-end gap-2">
                <Button variant="outline" onClick={() => setIsCreateDialogOpen(false)}>
                  Cancel
                </Button>
                <Button
                  onClick={handleCreate}
                  disabled={!newName.trim() || createMutation.isPending}
                >
                  {createMutation.isPending ? 'Creating...' : 'Create'}
                </Button>
              </div>
            </div>
          </DialogContent>
        </Dialog>
      </div>

      {/* T396: Render list of categories with name, color badge, task count, edit/delete buttons */}
      <div class="space-y-2">
        {categories.map((category) => (
          <div
            key={category.id}
            class="flex items-center justify-between rounded-lg border p-4 hover:bg-accent"
          >
            {editingId === category.id ? (
              // Editing mode
              <div class="flex-1 space-y-3">
                <div class="flex items-center gap-3">
                  <div class="flex-1">
                    <Input
                      value={editName}
                      onInput={(e) => setEditName((e.target as HTMLInputElement).value)}
                      placeholder="Category name"
                      maxLength={50}
                    />
                  </div>
                  <div class="flex items-center gap-2">
                    <Input
                      type="color"
                      value={editColor}
                      onInput={(e) => setEditColor((e.target as HTMLInputElement).value)}
                      class="h-9 w-16"
                    />
                    <Input
                      value={editColor}
                      onInput={(e) => setEditColor((e.target as HTMLInputElement).value)}
                      placeholder="#3B82F6"
                      maxLength={7}
                      class="w-28"
                    />
                  </div>
                </div>
                {editError && (
                  <p class="text-sm text-destructive">{editError}</p>
                )}
                <div class="flex gap-2">
                  <Button
                    size="sm"
                    onClick={() => handleUpdate(category.id)}
                    disabled={!editName.trim() || updateMutation.isPending}
                  >
                    {updateMutation.isPending ? 'Saving...' : 'Save'}
                  </Button>
                  <Button
                    size="sm"
                    variant="outline"
                    onClick={cancelEditing}
                  >
                    Cancel
                  </Button>
                </div>
              </div>
            ) : (
              // Display mode
              <>
                <div class="flex items-center gap-3">
                  {/* Color badge */}
                  <div
                    class="h-6 w-6 rounded-full border-2 border-border"
                    style={{ backgroundColor: category.colorCode ??  '#6B7280' }}
                  />
                  <div>
                    <p class="font-medium">{category.name}</p>
                    <p class="text-sm text-muted-foreground">
                      {category.taskCount ?? 0} {category.taskCount === 1 ? 'task' : 'tasks'}
                    </p>
                  </div>
                  {/* T397: Mark default categories as non-deletable in UI */}
                  {category.isDefault && (
                    <Badge variant="secondary">Default</Badge>
                  )}
                </div>
                <div class="flex gap-2">
                  <Button
                    size="sm"
                    variant="outline"
                    onClick={() => startEditing(category)}
                  >
                    Edit
                  </Button>
                  <Button
                    size="sm"
                    variant="destructive"
                    onClick={() => handleDelete(category)}
                    disabled={category.isDefault}
                  >
                    Delete
                  </Button>
                </div>
              </>
            )}
          </div>
        ))}

        {categories.length === 0 && (
          <div class="py-8 text-center">
            <p class="text-muted-foreground">No categories yet. Create your first category!</p>
          </div>
        )}
      </div>

      {/* T403: Delete confirmation dialog */}
      <AlertDialog open={!!deletingCategory} onOpenChange={(open) => !open && setDeletingCategory(null)}>
        <AlertDialogContent
          onCloseAutoFocus={(e: Event) => {
            // Prevent auto-focus behavior that causes errors
            e.preventDefault();
          }}
        >
          <AlertDialogHeader>
            <AlertDialogTitle>Delete Category</AlertDialogTitle>
            <AlertDialogDescription>
              Are you sure you want to delete "{deletingCategory?.name}"?
              {deletingCategory && deletingCategory.taskCount > 0 && (
                <span class="block mt-2 text-destructive font-medium">
                  Warning: This category contains {deletingCategory.taskCount} task(s).
                </span>
              )}
            </AlertDialogDescription>
          </AlertDialogHeader>
          {deleteError && (
            <p class="text-sm text-destructive">{deleteError}</p>
          )}
          <AlertDialogFooter>
            <AlertDialogCancel onClick={() => setDeletingCategory(null)}>
              Cancel
            </AlertDialogCancel>
            <AlertDialogAction
              onClick={confirmDelete}
              disabled={deleteMutation.isPending}
              class="bg-destructive text-destructive-foreground hover:bg-destructive/90"
            >
              {deleteMutation.isPending ? 'Deleting...' : 'Delete'}
            </AlertDialogAction>
          </AlertDialogFooter>
        </AlertDialogContent>
      </AlertDialog>
    </div>
  );
}

/**
 * CategoryManager Island - Wrapped with QueryProvider
 */
export default function CategoryManager() {
  return (
    <QueryProvider>
      <CategoryManagerContent />
    </QueryProvider>
  );
}
