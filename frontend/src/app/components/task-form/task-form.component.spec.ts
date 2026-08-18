import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { TaskFormComponent } from './task-form.component';
import { TaskService } from '../../services/task.service';
import { Task, TaskStatus, TaskPriority } from '../../models/task.model';
import { of, throwError } from 'rxjs';

describe('TaskFormComponent', () => {
  let component: TaskFormComponent;
  let fixture: ComponentFixture<TaskFormComponent>;
  let taskService: jasmine.SpyObj<TaskService>;

  const mockTask: Task = {
    id: 1,
    title: 'Test Task',
    description: 'Test Description',
    status: 'PENDING',
    priority: 'HIGH',
    createdAt: new Date(),
    updatedAt: new Date(),
    completedAt: null
  };

  beforeEach(async () => {
    const taskServiceSpy = jasmine.createSpyObj('TaskService', [
      'getAllTasks',
      'getTaskById',
      'createTask',
      'updateTask',
      'deleteTask',
      'getTasksByStatus',
      'getTasksByPriority'
    ]);

    await TestBed.configureTestingModule({
      imports: [TaskFormComponent, HttpClientTestingModule],
      providers: [
        { provide: TaskService, useValue: taskServiceSpy }
      ]
    }).compileComponents();

    taskService = TestBed.inject(TaskService) as jasmine.SpyObj<TaskService>;
    fixture = TestBed.createComponent(TaskFormComponent);
    component = fixture.componentInstance;
  });

  describe('Component Initialization', () => {
    it('should create', () => {
      expect(component).toBeTruthy();
    });

    it('should initialize with default values for new task', () => {
      component.task = null;
      fixture.detectChanges();

      expect(component.formData.title).toBe('');
      expect(component.formData.description).toBe('');
      expect(component.formData.status).toBe(TaskStatus.PENDING);
      expect(component.formData.priority).toBe(TaskPriority.MEDIUM);
      expect(component.loading).toBe(false);
      expect(component.error).toBeNull();
    });

    it('should populate form with existing task data', () => {
      component.task = mockTask;
      component.ngOnInit();
      fixture.detectChanges();

      expect(component.formData.title).toBe('Test Task');
      expect(component.formData.description).toBe('Test Description');
      expect(component.formData.status).toBe('PENDING');
      expect(component.formData.priority).toBe('HIGH');
    });

    it('should have status options available', () => {
      expect(component.statusOptions).toBeDefined();
      expect(component.statusOptions.length).toBeGreaterThan(0);
    });

    it('should have priority options available', () => {
      expect(component.priorityOptions).toBeDefined();
      expect(component.priorityOptions.length).toBeGreaterThan(0);
    });
  });

  describe('Form Validation', () => {
    it('should require title field', () => {
      component.formData.title = '';

      const isValid = component.validateForm();

      expect(isValid).toBe(false);
      expect(component.validationErrors['title']).toBe('Title is required');
    });

    it('should accept valid title with content', () => {
      component.formData.title = 'Valid Task Title';
      component.formData.description = '';

      const isValid = component.validateForm();

      expect(isValid).toBe(true);
      expect(component.validationErrors['title']).toBeUndefined();
    });

    it('should reject title with only whitespace', () => {
      component.formData.title = '   ';

      const isValid = component.validateForm();

      expect(isValid).toBe(false);
      expect(component.validationErrors['title']).toBe('Title is required');
    });

    it('should enforce title max length of 255 characters', () => {
      component.formData.title = 'a'.repeat(256);

      const isValid = component.validateForm();

      expect(isValid).toBe(false);
      expect(component.validationErrors['title']).toBe('Title must not exceed 255 characters');
    });

    it('should accept title with exactly 255 characters', () => {
      component.formData.title = 'a'.repeat(255);

      const isValid = component.validateForm();

      expect(isValid).toBe(true);
      expect(component.validationErrors['title']).toBeUndefined();
    });

    it('should enforce description max length of 1000 characters', () => {
      component.formData.title = 'Valid Title';
      component.formData.description = 'a'.repeat(1001);

      const isValid = component.validateForm();

      expect(isValid).toBe(false);
      expect(component.validationErrors['description']).toBe('Description must not exceed 1000 characters');
    });

    it('should accept description with exactly 1000 characters', () => {
      component.formData.title = 'Valid Title';
      component.formData.description = 'a'.repeat(1000);

      const isValid = component.validateForm();

      expect(isValid).toBe(true);
      expect(component.validationErrors['description']).toBeUndefined();
    });

    it('should allow empty description', () => {
      component.formData.title = 'Valid Title';
      component.formData.description = '';

      const isValid = component.validateForm();

      expect(isValid).toBe(true);
      expect(component.validationErrors['description']).toBeUndefined();
    });

    it('should clear validation errors when form becomes valid', () => {
      component.formData.title = '';
      component.validateForm();
      expect(Object.keys(component.validationErrors).length).toBeGreaterThan(0);

      component.formData.title = 'Valid Title';
      component.validateForm();
      expect(component.validationErrors['title']).toBeUndefined();
    });
  });

  describe('Create Task', () => {
    beforeEach(() => {
      component.task = null;
      component.formData = {
        title: 'New Task',
        description: 'New Description',
        status: TaskStatus.PENDING,
        priority: TaskPriority.MEDIUM
      };
    });

    it('should create new task successfully', (done) => {
      taskService.createTask.and.returnValue(of({} as Task));

      component.onSubmit();

      fixture.whenStable().then(() => {
        expect(taskService.createTask).toHaveBeenCalled();
        expect(component.loading).toBe(false);
        expect(component.error).toBeNull();
        done();
      });
    });

    it('should emit taskSaved event after successful creation', (done) => {
      taskService.createTask.and.returnValue(of({} as Task));
      spyOn(component.taskSaved, 'emit');

      component.onSubmit();

      fixture.whenStable().then(() => {
        expect(component.taskSaved.emit).toHaveBeenCalled();
        done();
      });
    });

    it('should not create task if validation fails', () => {
      component.formData.title = ''; // Invalid: empty title
      taskService.createTask.and.returnValue(of({} as Task));

      component.onSubmit();

      expect(taskService.createTask).not.toHaveBeenCalled();
    });

    it('should handle error when creating task', (done) => {
      const mockError = { error: { message: 'Server error' } };
      taskService.createTask.and.returnValue(throwError(() => mockError));

      component.onSubmit();

      fixture.whenStable().then(() => {
        expect(component.loading).toBe(false);
        expect(component.error).toContain('Failed to create task');
        done();
      });
    });
  });

  describe('Update Task', () => {
    beforeEach(() => {
      component.task = mockTask;
      component.ngOnInit();
      component.formData.title = 'Updated Task';
      component.formData.status = TaskStatus.COMPLETED;
    });

    it('should update existing task successfully', (done) => {
      taskService.updateTask.and.returnValue(of({} as Task));

      component.onSubmit();

      fixture.whenStable().then(() => {
        expect(taskService.updateTask).toHaveBeenCalledWith(
          mockTask.id,
          jasmine.objectContaining({
            title: 'Updated Task',
            status: TaskStatus.COMPLETED
          })
        );
        expect(component.loading).toBe(false);
        expect(component.error).toBeNull();
        done();
      });
    });

    it('should emit taskSaved event after successful update', (done) => {
      taskService.updateTask.and.returnValue(of({} as Task));
      spyOn(component.taskSaved, 'emit');

      component.onSubmit();

      fixture.whenStable().then(() => {
        expect(component.taskSaved.emit).toHaveBeenCalled();
        done();
      });
    });

    it('should handle error when updating task', (done) => {
      const mockError = { error: { message: 'Update failed' } };
      taskService.updateTask.and.returnValue(throwError(() => mockError));

      component.onSubmit();

      fixture.whenStable().then(() => {
        expect(component.loading).toBe(false);
        expect(component.error).toContain('Failed to update task');
        done();
      });
    });

    it('should not update task if validation fails', () => {
      component.formData.title = ''; // Invalid
      taskService.updateTask.and.returnValue(of({} as Task));

      component.onSubmit();

      expect(taskService.updateTask).not.toHaveBeenCalled();
    });
  });

  describe('Form Actions', () => {
    it('should emit formClosed event when closing form', () => {
      spyOn(component.formClosed, 'emit');

      component.onClose();

      expect(component.formClosed.emit).toHaveBeenCalled();
    });

    it('should set loading flag during submission', (done) => {
      component.formData = {
        title: 'Valid Title',
        description: '',
        status: TaskStatus.PENDING,
        priority: TaskPriority.MEDIUM
      };
      taskService.createTask.and.returnValue(of({} as Task));

      component.loading = false;
      component.onSubmit();
      expect(component.loading).toBe(true);

      fixture.whenStable().then(() => {
        expect(component.loading).toBe(false);
        done();
      });
    });

    it('should clear error message on new submission attempt', () => {
      component.error = 'Previous error';
      component.formData = {
        title: 'Valid Title',
        description: '',
        status: TaskStatus.PENDING,
        priority: TaskPriority.MEDIUM
      };
      taskService.createTask.and.returnValue(of({} as Task));

      component.onSubmit();

      expect(component.error).toBeNull();
    });
  });

  describe('Character Count', () => {
    it('should calculate character count for title', () => {
      component.formData.title = 'Test';

      const count = component.getCharacterCount('title', 255);

      expect(count).toBe('4/255');
    });

    it('should calculate character count for description', () => {
      component.formData.description = 'Test Description';

      const count = component.getCharacterCount('description', 1000);

      expect(count).toBe('16/1000');
    });

    it('should return 0 when field is empty', () => {
      component.formData.title = '';

      const count = component.getCharacterCount('title', 255);

      expect(count).toBe('0/255');
    });

    it('should handle full character limit', () => {
      component.formData.description = 'a'.repeat(1000);

      const count = component.getCharacterCount('description', 1000);

      expect(count).toBe('1000/1000');
    });
  });

  describe('Enum Availability', () => {
    it('should have TaskStatus enum available in template context', () => {
      expect(component.TaskStatus).toBeDefined();
      expect(component.TaskStatus).toBe(TaskStatus);
    });

    it('should have TaskPriority enum available in template context', () => {
      expect(component.TaskPriority).toBeDefined();
      expect(component.TaskPriority).toBe(TaskPriority);
    });
  });
});
