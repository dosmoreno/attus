package com.attus.service;

import com.attus.dto.TaskDTO;
import com.attus.entity.Task;
import com.attus.exception.TaskNotFoundException;
import com.attus.repository.TaskRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("TaskService Unit Tests")
class TaskServiceTest {

    @Mock
    private TaskRepository taskRepository;

    @InjectMocks
    private TaskService taskService;

    private Task testTask;
    private TaskDTO testTaskDTO;

    @BeforeEach
    void setUp() {
        testTask = new Task();
        testTask.setId(1L);
        testTask.setTitle("Test Task");
        testTask.setDescription("Test Description");
        testTask.setStatus(Task.TaskStatus.PENDING);
        testTask.setPriority(Task.TaskPriority.HIGH);
        testTask.setCreatedAt(LocalDateTime.now());
        testTask.setUpdatedAt(LocalDateTime.now());

        testTaskDTO = new TaskDTO();
        testTaskDTO.setId(1L);
        testTaskDTO.setTitle("Test Task");
        testTaskDTO.setDescription("Test Description");
        testTaskDTO.setStatus(Task.TaskStatus.PENDING);
        testTaskDTO.setPriority(Task.TaskPriority.HIGH);
    }

    @Test
    @DisplayName("Should retrieve all tasks successfully")
    void testGetAllTasks() {
        // Arrange
        Task task1 = new Task();
        task1.setId(1L);
        task1.setTitle("Task 1");
        
        Task task2 = new Task();
        task2.setId(2L);
        task2.setTitle("Task 2");
        
        when(taskRepository.findAllOrderByPriorityAndDate()).thenReturn(Arrays.asList(task1, task2));

        // Act
        List<TaskDTO> result = taskService.getAllTasks();

        // Assert
        assertThat(result).hasSize(2);
        assertThat(result.get(0).getId()).isEqualTo(1L);
        assertThat(result.get(1).getId()).isEqualTo(2L);
        verify(taskRepository, times(1)).findAllOrderByPriorityAndDate();
    }

    @Test
    @DisplayName("Should return empty list when no tasks exist")
    void testGetAllTasksEmpty() {
        // Arrange
        when(taskRepository.findAllOrderByPriorityAndDate()).thenReturn(List.of());

        // Act
        List<TaskDTO> result = taskService.getAllTasks();

        // Assert
        assertThat(result).isEmpty();
        verify(taskRepository, times(1)).findAllOrderByPriorityAndDate();
    }

    @Test
    @DisplayName("Should retrieve task by ID successfully")
    void testGetTaskById() {
        // Arrange
        when(taskRepository.findById(1L)).thenReturn(Optional.of(testTask));

        // Act
        TaskDTO result = taskService.getTaskById(1L);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getTitle()).isEqualTo("Test Task");
        verify(taskRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Should throw TaskNotFoundException when task not found")
    void testGetTaskByIdNotFound() {
        // Arrange
        when(taskRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> taskService.getTaskById(999L))
                .isInstanceOf(TaskNotFoundException.class)
                .hasMessageContaining("Task not found");
        verify(taskRepository, times(1)).findById(999L);
    }

    @Test
    @DisplayName("Should create task successfully")
    void testCreateTask() {
        // Arrange
        TaskDTO createDTO = new TaskDTO();
        createDTO.setTitle("New Task");
        createDTO.setDescription("New Description");
        createDTO.setStatus(Task.TaskStatus.PENDING);
        createDTO.setPriority(Task.TaskPriority.MEDIUM);

        Task savedTask = new Task();
        savedTask.setId(5L);
        savedTask.setTitle("New Task");
        savedTask.setDescription("New Description");
        savedTask.setStatus(Task.TaskStatus.PENDING);
        savedTask.setPriority(Task.TaskPriority.MEDIUM);
        savedTask.setCreatedAt(LocalDateTime.now());

        when(taskRepository.save(any(Task.class))).thenReturn(savedTask);

        // Act
        TaskDTO result = taskService.createTask(createDTO);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(5L);
        assertThat(result.getTitle()).isEqualTo("New Task");
        assertThat(result.getStatus()).isEqualTo("PENDING");
        verify(taskRepository, times(1)).save(any(Task.class));
    }

    @Test
    @DisplayName("Should update task successfully")
    void testUpdateTask() {
        // Arrange
        Long taskId = 1L;
        TaskDTO updateDTO = new TaskDTO();
        updateDTO.setTitle("Updated Task");
        updateDTO.setStatus(Task.TaskStatus.COMPLETED);

        Task existingTask = new Task();
        existingTask.setId(taskId);
        existingTask.setTitle("Old Task");
        existingTask.setStatus(Task.TaskStatus.PENDING);

        Task updatedTask = new Task();
        updatedTask.setId(taskId);
        updatedTask.setTitle("Updated Task");
        updatedTask.setStatus(Task.TaskStatus.COMPLETED);
        updatedTask.setCompletedAt(LocalDateTime.now());

        when(taskRepository.findById(taskId)).thenReturn(Optional.of(existingTask));
        when(taskRepository.save(any(Task.class))).thenReturn(updatedTask);

        // Act
        TaskDTO result = taskService.updateTask(taskId, updateDTO);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getTitle()).isEqualTo("Updated Task");
        assertThat(result.getStatus()).isEqualTo("COMPLETED");
        verify(taskRepository, times(1)).findById(taskId);
        verify(taskRepository, times(1)).save(any(Task.class));
    }

    @Test
    @DisplayName("Should throw exception when updating non-existent task")
    void testUpdateTaskNotFound() {
        // Arrange
        TaskDTO updateDTO = new TaskDTO();
        updateDTO.setTitle("Updated Task");
        
        when(taskRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> taskService.updateTask(999L, updateDTO))
                .isInstanceOf(TaskNotFoundException.class);
        verify(taskRepository, times(1)).findById(999L);
    }

    @Test
    @DisplayName("Should delete task successfully")
    void testDeleteTask() {
        // Arrange
        when(taskRepository.findById(1L)).thenReturn(Optional.of(testTask));
        doNothing().when(taskRepository).deleteById(1L);

        // Act
        taskService.deleteTask(1L);

        // Assert
        verify(taskRepository, times(1)).findById(1L);
        verify(taskRepository, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("Should throw exception when deleting non-existent task")
    void testDeleteTaskNotFound() {
        // Arrange
        when(taskRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> taskService.deleteTask(999L))
                .isInstanceOf(TaskNotFoundException.class);
        verify(taskRepository, times(1)).findById(999L);
    }

    @Test
    @DisplayName("Should filter tasks by status successfully")
    void testGetTasksByStatus() {
        // Arrange
        Task completedTask = new Task();
        completedTask.setId(1L);
        completedTask.setStatus(Task.TaskStatus.COMPLETED);
        
        when(taskRepository.findByStatus(Task.TaskStatus.COMPLETED))
                .thenReturn(List.of(completedTask));

        // Act
        List<TaskDTO> result = taskService.getTasksByStatus("COMPLETED");

        // Assert
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getStatus()).isEqualTo("COMPLETED");
    }

    @Test
    @DisplayName("Should filter tasks by priority successfully")
    void testGetTasksByPriority() {
        // Arrange
        Task highPriorityTask = new Task();
        highPriorityTask.setId(1L);
        highPriorityTask.setPriority(Task.TaskPriority.HIGH);
        
        when(taskRepository.findByPriority(Task.TaskPriority.HIGH))
                .thenReturn(List.of(highPriorityTask));

        // Act
        List<TaskDTO> result = taskService.getTasksByPriority("HIGH");

        // Assert
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getPriority()).isEqualTo("HIGH");
    }
}
