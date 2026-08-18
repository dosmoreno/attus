import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { TaskService } from './task.service';
import { Task, TaskStatus, TaskPriority, CreateTaskRequest, UpdateTaskRequest } from '../models/task.model';

describe('TaskService', () => {
  let service: TaskService;
  let httpMock: HttpTestingController;
  const apiUrl = '/api/v1/tasks';

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [TaskService]
    });
    service = TestBed.inject(TaskService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  describe('getAllTasks', () => {
    it('should fetch all tasks successfully', () => {
      // Arrange
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

      // Act
      service.getAllTasks().subscribe(tasks => {
        // Assert
        expect(tasks).toEqual(mockTasks);
        expect(tasks.length).toBe(2);
      });

      const req = httpMock.expectOne(apiUrl);
      expect(req.request.method).toBe('GET');
      req.flush(mockTasks);
    });

    it('should return empty array when no tasks exist', () => {
      // Act
      service.getAllTasks().subscribe(tasks => {
        // Assert
        expect(tasks).toEqual([]);
        expect(tasks.length).toBe(0);
      });

      const req = httpMock.expectOne(apiUrl);
      expect(req.request.method).toBe('GET');
      req.flush([]);
    });

    it('should handle HTTP error', () => {
      // Act & Assert
      service.getAllTasks().subscribe(
        () => fail('should have failed'),
        (error) => {
          expect(error.status).toBe(500);
        }
      );

      const req = httpMock.expectOne(apiUrl);
      req.flush('Server error', { status: 500, statusText: 'Server Error' });
    });
  });

  describe('getTaskById', () => {
    it('should fetch task by ID successfully', () => {
      // Arrange
      const taskId = 1;
      const mockTask: Task = {
        id: 1,
        title: 'Task 1',
        description: 'Description 1',
        status: TaskStatus.PENDING,
        priority: TaskPriority.HIGH,
        createdAt: new Date(),
        updatedAt: new Date(),
        completedAt: undefined
      };

      // Act
      service.getTaskById(taskId).subscribe(task => {
        // Assert
        expect(task).toEqual(mockTask);
        expect(task.id).toBe(1);
        expect(task.title).toBe('Task 1');
      });

      const req = httpMock.expectOne(`${apiUrl}/${taskId}`);
      expect(req.request.method).toBe('GET');
      req.flush(mockTask);
    });

    it('should handle 404 error when task not found', () => {
      // Act & Assert
      service.getTaskById(999).subscribe(
        () => fail('should have failed'),
        (error) => {
          expect(error.status).toBe(404);
        }
      );

      const req = httpMock.expectOne(`${apiUrl}/999`);
      req.flush('Task not found', { status: 404, statusText: 'Not Found' });
    });
  });

  describe('createTask', () => {
    it('should create a new task successfully', () => {
      // Arrange
      const newTask: CreateTaskRequest = {
        title: 'New Task',
        description: 'New Description',
        status: TaskStatus.PENDING,
        priority: TaskPriority.MEDIUM
      };

      const createdTask: Task = {
        id: 5,
        title: 'New Task',
        description: 'New Description',
        status: TaskStatus.PENDING,
        priority: TaskPriority.MEDIUM,
        createdAt: new Date(),
        updatedAt: new Date(),
        completedAt: undefined
      };

      // Act
      service.createTask(newTask).subscribe(task => {
        // Assert
        expect(task).toEqual(createdTask);
        expect(task.id).toBe(5);
      });

      const req = httpMock.expectOne(apiUrl);
      expect(req.request.method).toBe('POST');
      expect(req.request.body).toEqual(newTask);
      req.flush(createdTask);
    });

    it('should handle validation error when creating task', () => {
      // Arrange
      const invalidTask: any = {
        title: '', // Invalid: empty title
        priority: 'MEDIUM'
      };

      // Act & Assert
      service.createTask(invalidTask).subscribe(
        () => fail('should have failed'),
        (error) => {
          expect(error.status).toBe(400);
        }
      );

      const req = httpMock.expectOne(apiUrl);
      req.flush('Validation error', { status: 400, statusText: 'Bad Request' });
    });
  });

  describe('updateTask', () => {
    it('should update task successfully', () => {
      // Arrange
      const taskId = 1;
      const updateData: UpdateTaskRequest = {
        title: 'Updated Task',
        status: TaskStatus.COMPLETED
      };

      const updatedTask: Task = {
        id: 1,
        title: 'Updated Task',
        description: 'Original Description',
        status: TaskStatus.COMPLETED,
        priority: TaskPriority.HIGH,
        createdAt: new Date(),
        updatedAt: new Date(),
        completedAt: new Date()
      };

      // Act
      service.updateTask(taskId, updateData).subscribe(task => {
        // Assert
        expect(task).toEqual(updatedTask);
        expect(task.title).toBe('Updated Task');
        expect(task.status).toBe('COMPLETED');
      });

      const req = httpMock.expectOne(`${apiUrl}/${taskId}`);
      expect(req.request.method).toBe('PUT');
      expect(req.request.body).toEqual(updateData);
      req.flush(updatedTask);
    });

    it('should handle 404 error when updating non-existent task', () => {
      // Arrange
      const updateData: UpdateTaskRequest = { title: 'Updated' };

      // Act & Assert
      service.updateTask(999, updateData).subscribe(
        () => fail('should have failed'),
        (error) => {
          expect(error.status).toBe(404);
        }
      );

      const req = httpMock.expectOne(`${apiUrl}/999`);
      req.flush('Task not found', { status: 404, statusText: 'Not Found' });
    });
  });

  describe('deleteTask', () => {
    it('should delete task successfully', () => {
      // Arrange
      const taskId = 1;

      // Act
      service.deleteTask(taskId).subscribe(
        () => {
          // Assert: Success path
          expect(true).toBe(true);
        }
      );

      const req = httpMock.expectOne(`${apiUrl}/${taskId}`);
      expect(req.request.method).toBe('DELETE');
      req.flush(null);
    });

    it('should handle 404 error when deleting non-existent task', () => {
      // Act & Assert
      service.deleteTask(999).subscribe(
        () => fail('should have failed'),
        (error) => {
          expect(error.status).toBe(404);
        }
      );

      const req = httpMock.expectOne(`${apiUrl}/999`);
      req.flush('Task not found', { status: 404, statusText: 'Not Found' });
    });
  });

  describe('getTasksByStatus', () => {
    it('should filter tasks by status successfully', () => {
      // Arrange
      const status = 'COMPLETED';
      const mockTasks: Task[] = [
        {
          id: 1,
          title: 'Completed Task',
          description: 'Description',
          status: TaskStatus.COMPLETED,
          priority: TaskPriority.HIGH,
          createdAt: new Date(),
          updatedAt: new Date(),
          completedAt: new Date()
        }
      ];

      // Act
      service.getTasksByStatus(status).subscribe(tasks => {
        // Assert
        expect(tasks).toEqual(mockTasks);
        expect(tasks[0].status).toBe('COMPLETED');
      });

      const req = httpMock.expectOne(req => req.url === `${apiUrl}/filter/status` && req.params.get('status') === status);
      expect(req.request.method).toBe('GET');
      req.flush(mockTasks);
    });

    it('should return empty array when no tasks match status', () => {
      // Act
      service.getTasksByStatus('IN_PROGRESS').subscribe(tasks => {
        // Assert
        expect(tasks).toEqual([]);
      });

      const req = httpMock.expectOne(req => req.url === `${apiUrl}/filter/status`);
      req.flush([]);
    });
  });

  describe('getTasksByPriority', () => {
    it('should filter tasks by priority successfully', () => {
      // Arrange
      const priority = 'HIGH';
      const mockTasks: Task[] = [
        {
          id: 1,
          title: 'High Priority Task',
          description: 'Description',
          status: TaskStatus.PENDING,
          priority: TaskPriority.HIGH,
          createdAt: new Date(),
          updatedAt: new Date(),
          completedAt: undefined
        }
      ];

      // Act
      service.getTasksByPriority(priority).subscribe(tasks => {
        // Assert
        expect(tasks).toEqual(mockTasks);
        expect(tasks[0].priority).toBe('HIGH');
      });

      const req = httpMock.expectOne(req => req.url === `${apiUrl}/filter/priority` && req.params.get('priority') === priority);
      expect(req.request.method).toBe('GET');
      req.flush(mockTasks);
    });

    it('should return empty array when no tasks match priority', () => {
      // Act
      service.getTasksByPriority('LOW').subscribe(tasks => {
        // Assert
        expect(tasks).toEqual([]);
      });

      const req = httpMock.expectOne(req => req.url === `${apiUrl}/filter/priority`);
      req.flush([]);
    });
  });
});
