import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { TaskListComponent } from './task-list.component';
import { TaskService } from '../../services/task.service';
import { Task, TaskStatus, TaskPriority } from '../../models/task.model';
import { of, throwError } from 'rxjs';

describe('TaskListComponent', () => {
  let component: TaskListComponent;
  let fixture: ComponentFixture<TaskListComponent>;
  let taskService: jasmine.SpyObj<TaskService>;

  const mockTasks: Task[] = [
    {
      id: 1,
      title: 'Task 1',
      description: 'Description 1',
      status: TaskStatus.PENDING,
      priority: TaskPriority.HIGH,
      createdAt: new Date(),
      updatedAt: new Date(),
      completedAt: undefined
    },
    {
      id: 2,
      title: 'Task 2',
      description: 'Description 2',
      status: TaskStatus.COMPLETED,
      priority: TaskPriority.LOW,
      createdAt: new Date(),
      updatedAt: new Date(),
      completedAt: new Date()
    }
  ];

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
      imports: [TaskListComponent, HttpClientTestingModule],
      providers: [
        { provide: TaskService, useValue: taskServiceSpy }
      ]
    }).compileComponents();

    taskService = TestBed.inject(TaskService) as jasmine.SpyObj<TaskService>;
    fixture = TestBed.createComponent(TaskListComponent);
    component = fixture.componentInstance;
  });

  describe('Component Initialization', () => {
    it('should create', () => {
      expect(component).toBeTruthy();
    });

    it('should initialize with default values', () => {
      expect(component.tasks).toEqual([]);
      expect(component.filteredTasks).toEqual([]);
      expect(component.loading).toBe(false);
      expect(component.error).toBeNull();
      expect(component.selectedStatus).toBe('');
      expect(component.selectedPriority).toBe('');
      expect(component.searchTerm).toBe('');
      expect(component.showForm).toBe(false);
      expect(component.selectedTask).toBeNull();
    });

    it('should load tasks on init', () => {
      taskService.getAllTasks.and.returnValue(of(mockTasks));

      component.ngOnInit();

      expect(taskService.getAllTasks).toHaveBeenCalled();
      expect(component.tasks).toEqual(mockTasks);
      expect(component.filteredTasks).toEqual(mockTasks);
      expect(component.loading).toBe(false);
    });
  });

  describe('loadTasks', () => {
    it('should load tasks successfully', () => {
      taskService.getAllTasks.and.returnValue(of(mockTasks));

      component.loadTasks();

      expect(component.tasks).toEqual(mockTasks);
      expect(component.filteredTasks).toEqual(mockTasks);
      expect(component.loading).toBe(false);
      expect(component.error).toBeNull();
      expect(taskService.getAllTasks).toHaveBeenCalled();
    });

    it('should handle empty task list', () => {
      taskService.getAllTasks.and.returnValue(of([]));

      component.loadTasks();

      expect(component.tasks).toEqual([]);
      expect(component.filteredTasks).toEqual([]);
      expect(component.loading).toBe(false);
    });

    it('should handle error when loading tasks', () => {
      const errorMessage = 'Failed to load tasks: Internal Server Error';
      taskService.getAllTasks.and.returnValue(throwError(() => ({ 
        error: { message: 'Internal Server Error' } 
      })));

      component.loadTasks();

      expect(component.loading).toBe(false);
      expect(component.error).toContain('Failed to load tasks');
    });

    it('should set loading flag correctly', (done) => {
      taskService.getAllTasks.and.returnValue(of(mockTasks));

      expect(component.loading).toBe(false);
      component.loadTasks();
      expect(component.loading).toBe(false); // Loading already completed
      
      done();
    });
  });

  describe('applyFilters', () => {
    beforeEach(() => {
      component.tasks = mockTasks;
    });

    it('should return all tasks when no filters applied', () => {
      component.selectedStatus = '';
      component.selectedPriority = '';
      component.searchTerm = '';

      component.applyFilters();

      expect(component.filteredTasks).toEqual(mockTasks);
    });

    it('should filter by status', () => {
      component.selectedStatus = 'PENDING';
      component.selectedPriority = '';
      component.searchTerm = '';

      component.applyFilters();

      expect(component.filteredTasks.length).toBe(1);
      expect(component.filteredTasks[0].status).toBe('PENDING');
    });

    it('should filter by priority', () => {
      component.selectedStatus = '';
      component.selectedPriority = 'HIGH';
      component.searchTerm = '';

      component.applyFilters();

      expect(component.filteredTasks.length).toBe(1);
      expect(component.filteredTasks[0].priority).toBe('HIGH');
    });

    it('should filter by search term in title', () => {
      component.selectedStatus = '';
      component.selectedPriority = '';
      component.searchTerm = 'Task 1';

      component.applyFilters();

      expect(component.filteredTasks.length).toBe(1);
      expect(component.filteredTasks[0].title).toContain('Task 1');
    });

    it('should filter by search term in description', () => {
      component.selectedStatus = '';
      component.selectedPriority = '';
      component.searchTerm = 'Description 2';

      component.applyFilters();

      expect(component.filteredTasks.length).toBe(1);
      expect(component.filteredTasks[0].description).toContain('Description 2');
    });

    it('should apply multiple filters together', () => {
      component.selectedStatus = 'PENDING';
      component.selectedPriority = 'HIGH';
      component.searchTerm = '';

      component.applyFilters();

      expect(component.filteredTasks.length).toBe(1);
      expect(component.filteredTasks[0].status).toBe('PENDING');
      expect(component.filteredTasks[0].priority).toBe('HIGH');
    });

    it('should be case-insensitive for search term', () => {
      component.selectedStatus = '';
      component.selectedPriority = '';
      component.searchTerm = 'task 1';

      component.applyFilters();

      expect(component.filteredTasks.length).toBe(1);
      expect(component.filteredTasks[0].title).toContain('Task 1');
    });

    it('should return empty array when filter matches nothing', () => {
      component.selectedStatus = 'IN_PROGRESS';
      component.selectedPriority = '';
      component.searchTerm = '';

      component.applyFilters();

      expect(component.filteredTasks.length).toBe(0);
    });
  });

  describe('onFilterChange', () => {
    it('should apply filters when filter changes', () => {
      component.tasks = mockTasks;
      spyOn(component, 'applyFilters');

      component.onFilterChange();

      expect(component.applyFilters).toHaveBeenCalled();
    });
  });

  describe('selectTask', () => {
    it('should select a task', () => {
      const task = mockTasks[0];

      component.selectTask(task);

      expect(component.selectedTask).toEqual(task);
    });

    it('should update selected task when selecting different task', () => {
      component.selectTask(mockTasks[0]);
      expect(component.selectedTask).toEqual(mockTasks[0]);

      component.selectTask(mockTasks[1]);
      expect(component.selectedTask).toEqual(mockTasks[1]);
    });
  });

  describe('Task Management Actions', () => {
    it('should have TaskStatus enum available in template', () => {
      expect(component.TaskStatus).toBeDefined();
    });

    it('should have TaskPriority enum available in template', () => {
      expect(component.TaskPriority).toBeDefined();
    });

    it('should have status options for filtering', () => {
      expect(component.statusOptions).toBeDefined();
      expect(component.statusOptions.length).toBeGreaterThan(0);
    });

    it('should have priority options for filtering', () => {
      expect(component.priorityOptions).toBeDefined();
      expect(component.priorityOptions.length).toBeGreaterThan(0);
    });
  });

  describe('Error Handling', () => {
    it('should display error message on load failure', () => {
      const mockError = new Error('Network error');
      taskService.getAllTasks.and.returnValue(
        throwError(() => ({ message: 'Network error' }))
      );

      component.loadTasks();

      expect(component.error).toBeTruthy();
      expect(component.loading).toBe(false);
    });

    it('should clear error message when tasks load successfully', () => {
      component.error = 'Previous error';
      taskService.getAllTasks.and.returnValue(of(mockTasks));

      component.loadTasks();

      expect(component.error).toBeNull();
    });
  });
});
