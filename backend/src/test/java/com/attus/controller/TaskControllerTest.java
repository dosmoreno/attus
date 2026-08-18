package com.attus.controller;

import com.attus.dto.TaskDTO;
import com.attus.entity.Task;
import com.attus.exception.TaskNotFoundException;
import com.attus.service.TaskService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TaskController.class)
@DisplayName("TaskController Integration Tests")
class TaskControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private TaskService taskService;

    private TaskDTO testTaskDTO;

    @BeforeEach
    void setUp() {
        testTaskDTO = new TaskDTO();
        testTaskDTO.setId(1L);
        testTaskDTO.setTitle("Test Task");
        testTaskDTO.setDescription("Test Description");
        testTaskDTO.setStatus(Task.TaskStatus.PENDING);
        testTaskDTO.setPriority(Task.TaskPriority.HIGH);
    }

    @Test
    @DisplayName("GET /api/v1/tasks - Should retrieve all tasks")
    void testGetAllTasks() throws Exception {
        // Arrange
        TaskDTO task1 = new TaskDTO();
        task1.setId(1L);
        task1.setTitle("Task 1");
        task1.setStatus(Task.TaskStatus.PENDING);
        task1.setPriority(Task.TaskPriority.MEDIUM);
        
        TaskDTO task2 = new TaskDTO();
        task2.setId(2L);
        task2.setTitle("Task 2");
        task2.setStatus(Task.TaskStatus.COMPLETED);
        task2.setPriority(Task.TaskPriority.LOW);
        
        when(taskService.getAllTasks()).thenReturn(Arrays.asList(task1, task2));

        // Act & Assert
        mockMvc.perform(get("/api/v1/tasks"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].title", is("Task 1")))
                .andExpect(jsonPath("$[1].id", is(2)))
                .andExpect(jsonPath("$[1].title", is("Task 2")));

        verify(taskService, times(1)).getAllTasks();
    }

    @Test
    @DisplayName("GET /api/v1/tasks - Should return empty list")
    void testGetAllTasksEmpty() throws Exception {
        // Arrange
        when(taskService.getAllTasks()).thenReturn(List.of());

        // Act & Assert
        mockMvc.perform(get("/api/v1/tasks"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));

        verify(taskService, times(1)).getAllTasks();
    }

    @Test
    @DisplayName("GET /api/v1/tasks/{id} - Should retrieve task by ID")
    void testGetTaskById() throws Exception {
        // Arrange
        when(taskService.getTaskById(1L)).thenReturn(testTaskDTO);

        // Act & Assert
        mockMvc.perform(get("/api/v1/tasks/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.title", is("Test Task")))
                .andExpect(jsonPath("$.status", is("PENDING")))
                .andExpect(jsonPath("$.priority", is("HIGH")));

        verify(taskService, times(1)).getTaskById(1L);
    }

    @Test
    @DisplayName("GET /api/v1/tasks/{id} - Should return 404 when task not found")
    void testGetTaskByIdNotFound() throws Exception {
        // Arrange
        when(taskService.getTaskById(999L))
                .thenThrow(new TaskNotFoundException("Task not found with id: 999"));

        // Act & Assert
        mockMvc.perform(get("/api/v1/tasks/999"))
                .andExpect(status().isNotFound());

        verify(taskService, times(1)).getTaskById(999L);
    }

    @Test
    @DisplayName("POST /api/v1/tasks - Should create a new task")
    void testCreateTask() throws Exception {
        // Arrange
        TaskDTO createDTO = new TaskDTO();
        createDTO.setTitle("New Task");
        createDTO.setDescription("New Description");
        createDTO.setStatus(Task.TaskStatus.PENDING);
        createDTO.setPriority(Task.TaskPriority.MEDIUM);

        TaskDTO createdDTO = new TaskDTO();
        createdDTO.setId(5L);
        createdDTO.setTitle("New Task");
        createdDTO.setDescription("New Description");
        createdDTO.setStatus(Task.TaskStatus.PENDING);
        createdDTO.setPriority(Task.TaskPriority.MEDIUM);

        when(taskService.createTask(any())).thenReturn(createdDTO);

        // Act & Assert
        mockMvc.perform(post("/api/v1/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(5)))
                .andExpect(jsonPath("$.title", is("New Task")))
                .andExpect(jsonPath("$.status", is("PENDING")));

        verify(taskService, times(1)).createTask(any());
    }

    @Test
    @DisplayName("PUT /api/v1/tasks/{id} - Should update an existing task")
    void testUpdateTask() throws Exception {
        // Arrange
        TaskDTO updateDTO = new TaskDTO();
        updateDTO.setTitle("Updated Task");
        updateDTO.setStatus(Task.TaskStatus.COMPLETED);

        TaskDTO updatedDTO = new TaskDTO();
        updatedDTO.setId(1L);
        updatedDTO.setTitle("Updated Task");
        updatedDTO.setStatus(Task.TaskStatus.COMPLETED);

        when(taskService.updateTask(eq(1L), any())).thenReturn(updatedDTO);

        // Act & Assert
        mockMvc.perform(put("/api/v1/tasks/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.title", is("Updated Task")))
                .andExpect(jsonPath("$.status", is("COMPLETED")));

        verify(taskService, times(1)).updateTask(eq(1L), any());
    }

    @Test
    @DisplayName("PUT /api/v1/tasks/{id} - Should return 404 when task not found")
    void testUpdateTaskNotFound() throws Exception {
        // Arrange
        TaskDTO updateDTO = new TaskDTO();
        updateDTO.setTitle("Updated Task");

        when(taskService.updateTask(eq(999L), any()))
                .thenThrow(new TaskNotFoundException("Task not found"));

        // Act & Assert
        mockMvc.perform(put("/api/v1/tasks/999")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateDTO)))
                .andExpect(status().isNotFound());

        verify(taskService, times(1)).updateTask(eq(999L), any());
    }

    @Test
    @DisplayName("DELETE /api/v1/tasks/{id} - Should delete a task")
    void testDeleteTask() throws Exception {
        // Arrange
        doNothing().when(taskService).deleteTask(1L);

        // Act & Assert
        mockMvc.perform(delete("/api/v1/tasks/1"))
                .andExpect(status().isNoContent());

        verify(taskService, times(1)).deleteTask(1L);
    }

    @Test
    @DisplayName("DELETE /api/v1/tasks/{id} - Should return 404 when task not found")
    void testDeleteTaskNotFound() throws Exception {
        // Arrange
        doThrow(new TaskNotFoundException("Task not found"))
                .when(taskService).deleteTask(999L);

        // Act & Assert
        mockMvc.perform(delete("/api/v1/tasks/999"))
                .andExpect(status().isNotFound());

        verify(taskService, times(1)).deleteTask(999L);
    }

    @Test
    @DisplayName("GET /api/v1/tasks/filter/status - Should filter tasks by status")
    void testGetTasksByStatus() throws Exception {
        // Arrange
        TaskDTO completedTask = new TaskDTO();
        completedTask.setId(1L);
        completedTask.setStatus(Task.TaskStatus.COMPLETED);

        when(taskService.getTasksByStatus("COMPLETED")).thenReturn(List.of(completedTask));

        // Act & Assert
        mockMvc.perform(get("/api/v1/tasks/filter/status?status=COMPLETED"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].status", is("COMPLETED")));

        verify(taskService, times(1)).getTasksByStatus("COMPLETED");
    }

    @Test
    @DisplayName("GET /api/v1/tasks/filter/priority - Should filter tasks by priority")
    void testGetTasksByPriority() throws Exception {
        // Arrange
        TaskDTO highPriorityTask = new TaskDTO();
        highPriorityTask.setId(1L);
        highPriorityTask.setPriority(Task.TaskPriority.HIGH);

        when(taskService.getTasksByPriority("HIGH")).thenReturn(List.of(highPriorityTask));

        // Act & Assert
        mockMvc.perform(get("/api/v1/tasks/filter/priority?priority=HIGH"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].priority", is("HIGH")));

        verify(taskService, times(1)).getTasksByPriority("HIGH");
    }

    @Test
    @DisplayName("POST /api/v1/tasks - Should return 400 for invalid request")
    void testCreateTaskInvalidRequest() throws Exception {
        // Arrange
        String invalidJson = "{ invalid json }";

        // Act & Assert
        mockMvc.perform(post("/api/v1/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .content(invalidJson))
                .andExpect(status().isBadRequest());
    }
}
