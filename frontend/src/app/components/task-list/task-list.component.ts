import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { TaskService } from '../../services/task.service';
import { Task, TaskStatus, TaskPriority } from '../../models/task.model';
import { TaskFormComponent } from '../task-form/task-form.component';

@Component({
  selector: 'app-task-list',
  standalone: true,
  imports: [CommonModule, FormsModule, TaskFormComponent],
  templateUrl: './task-list.component.html',
  styleUrls: ['./task-list.component.css']
})
export class TaskListComponent implements OnInit {

  tasks: Task[] = [];
  filteredTasks: Task[] = [];
  loading = false;
  error: string | null = null;

  // Filters
  selectedStatus: string = '';
  selectedPriority: string = '';
  searchTerm: string = '';

  // Enums for template
  TaskStatus = TaskStatus;
  TaskPriority = TaskPriority;

  statusOptions = Object.values(TaskStatus);
  priorityOptions = Object.values(TaskPriority);

  selectedTask: Task | null = null;
  showForm = false;

  constructor(private taskService: TaskService) { }

  ngOnInit(): void {
    this.loadTasks();
  }

  loadTasks(): void {
    this.loading = true;
    this.error = null;

    this.taskService.getAllTasks().subscribe({
      next: (data) => {
        this.tasks = data;
        this.applyFilters();
        this.loading = false;
      },
      error: (err) => {
        this.error = 'Failed to load tasks: ' + (err.error?.message || err.message);
        this.loading = false;
        console.error('Error loading tasks:', err);
      }
    });
  }

  applyFilters(): void {
    this.filteredTasks = this.tasks.filter(task => {
      const matchStatus = !this.selectedStatus || task.status === this.selectedStatus;
      const matchPriority = !this.selectedPriority || task.priority === this.selectedPriority;
      const matchSearch = !this.searchTerm || 
        task.title.toLowerCase().includes(this.searchTerm.toLowerCase()) ||
        (task.description && task.description.toLowerCase().includes(this.searchTerm.toLowerCase()));
      
      return matchStatus && matchPriority && matchSearch;
    });
  }

  onFilterChange(): void {
    this.applyFilters();
  }

  selectTask(task: Task): void {
    this.selectedTask = task;
  }

  editTask(task: Task): void {
    this.selectedTask = task;
    this.showForm = true;
  }

  deleteTask(id: number): void {
    if (confirm('Are you sure you want to delete this task?')) {
      this.taskService.deleteTask(id).subscribe({
        next: () => {
          this.loadTasks();
          this.selectedTask = null;
        },
        error: (err) => {
          this.error = 'Failed to delete task: ' + (err.error?.message || err.message);
          console.error('Error deleting task:', err);
        }
      });
    }
  }

  onTaskSaved(): void {
    this.loadTasks();
    this.showForm = false;
    this.selectedTask = null;
  }

  onFormClosed(): void {
    this.showForm = false;
    this.selectedTask = null;
  }

  getPriorityClass(priority: TaskPriority): string {
    switch (priority) {
      case TaskPriority.CRITICAL:
        return 'priority-critical';
      case TaskPriority.HIGH:
        return 'priority-high';
      case TaskPriority.MEDIUM:
        return 'priority-medium';
      case TaskPriority.LOW:
        return 'priority-low';
      default:
        return '';
    }
  }

  getStatusClass(status: TaskStatus): string {
    switch (status) {
      case TaskStatus.COMPLETED:
        return 'status-completed';
      case TaskStatus.IN_PROGRESS:
        return 'status-in-progress';
      case TaskStatus.CANCELLED:
        return 'status-cancelled';
      case TaskStatus.PENDING:
      default:
        return 'status-pending';
    }
  }
}
