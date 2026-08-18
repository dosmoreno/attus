import { Component, Input, Output, EventEmitter, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Task, TaskStatus, TaskPriority, CreateTaskRequest, UpdateTaskRequest } from '../../models/task.model';
import { TaskService } from '../../services/task.service';

@Component({
  selector: 'app-task-form',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './task-form.component.html',
  styleUrls: ['./task-form.component.css']
})
export class TaskFormComponent implements OnInit {

  @Input() task: Task | null = null;
  @Output() taskSaved = new EventEmitter<void>();
  @Output() formClosed = new EventEmitter<void>();

  formData = {
    title: '',
    description: '',
    status: TaskStatus.PENDING,
    priority: TaskPriority.MEDIUM
  };

  statusOptions = Object.values(TaskStatus);
  priorityOptions = Object.values(TaskPriority);

  loading = false;
  error: string | null = null;
  validationErrors: { [key: string]: string } = {};

  TaskStatus = TaskStatus;
  TaskPriority = TaskPriority;

  constructor(private taskService: TaskService) { }

  ngOnInit(): void {
    if (this.task) {
      this.formData = {
        title: this.task.title,
        description: this.task.description || '',
        status: this.task.status,
        priority: this.task.priority
      };
    }
  }

  onSubmit(): void {
    if (!this.validateForm()) {
      return;
    }

    this.loading = true;
    this.error = null;

    if (this.task && this.task.id) {
      // Update existing task
      const updateRequest: UpdateTaskRequest = {
        title: this.formData.title,
        description: this.formData.description || undefined,
        status: this.formData.status,
        priority: this.formData.priority
      };

      this.taskService.updateTask(this.task.id, updateRequest).subscribe({
        next: () => {
          this.loading = false;
          this.taskSaved.emit();
        },
        error: (err) => {
          this.loading = false;
          this.error = 'Failed to update task: ' + (err.error?.message || err.message);
          console.error('Error updating task:', err);
        }
      });
    } else {
      // Create new task
      const createRequest: CreateTaskRequest = {
        title: this.formData.title,
        description: this.formData.description || undefined,
        status: this.formData.status,
        priority: this.formData.priority
      };

      this.taskService.createTask(createRequest).subscribe({
        next: () => {
          this.loading = false;
          this.taskSaved.emit();
        },
        error: (err) => {
          this.loading = false;
          this.error = 'Failed to create task: ' + (err.error?.message || err.message);
          console.error('Error creating task:', err);
        }
      });
    }
  }

  validateForm(): boolean {
    this.validationErrors = {};

    if (!this.formData.title || this.formData.title.trim().length === 0) {
      this.validationErrors['title'] = 'Title is required';
    }

    if (this.formData.title && this.formData.title.length > 255) {
      this.validationErrors['title'] = 'Title must not exceed 255 characters';
    }

    if (this.formData.description && this.formData.description.length > 1000) {
      this.validationErrors['description'] = 'Description must not exceed 1000 characters';
    }

    return Object.keys(this.validationErrors).length === 0;
  }

  onClose(): void {
    this.formClosed.emit();
  }

  getCharacterCount(field: string, limit: number): string {
    const value = this.formData[field as keyof typeof this.formData] as string;
    const count = value ? value.length : 0;
    return `${count}/${limit}`;
  }
}
