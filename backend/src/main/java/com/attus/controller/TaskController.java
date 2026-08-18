package com.attus.controller;

import com.attus.dto.ErrorResponse;
import com.attus.dto.TaskDTO;
import com.attus.exception.TaskNotFoundException;
import com.attus.service.TaskService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.WebRequest;

import java.util.List;

@RestController
@RequestMapping("/api/v1/tasks")
@CrossOrigin(origins = "*", maxAge = 3600)
public class TaskController {

    private static final Logger log = LoggerFactory.getLogger(TaskController.class);

    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    /**
     * GET /api/v1/tasks - Get all tasks
     */
    @GetMapping
    public ResponseEntity<List<TaskDTO>> getAllTasks() {
        log.info("GET /api/v1/tasks - Fetching all tasks");
        List<TaskDTO> tasks = taskService.getAllTasks();
        return ResponseEntity.ok(tasks);
    }

    /**
     * GET /api/v1/tasks/{id} - Get task by ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<TaskDTO> getTaskById(@PathVariable Long id) {
        log.info("GET /api/v1/tasks/{} - Fetching task", id);
        TaskDTO task = taskService.getTaskById(id);
        return ResponseEntity.ok(task);
    }

    /**
     * POST /api/v1/tasks - Create a new task
     */
    @PostMapping
    public ResponseEntity<TaskDTO> createTask(@Valid @RequestBody TaskDTO taskDTO) {
        log.info("POST /api/v1/tasks - Creating new task");
        TaskDTO createdTask = taskService.createTask(taskDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdTask);
    }

    /**
     * PUT /api/v1/tasks/{id} - Update an existing task
     */
    @PutMapping("/{id}")
    public ResponseEntity<TaskDTO> updateTask(
            @PathVariable Long id,
            @Valid @RequestBody TaskDTO taskDTO) {
        log.info("PUT /api/v1/tasks/{} - Updating task", id);
        TaskDTO updatedTask = taskService.updateTask(id, taskDTO);
        return ResponseEntity.ok(updatedTask);
    }

    /**
     * DELETE /api/v1/tasks/{id} - Delete a task
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTask(@PathVariable Long id) {
        log.info("DELETE /api/v1/tasks/{} - Deleting task", id);
        taskService.deleteTask(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * GET /api/v1/tasks/filter/status?status={status} - Filter tasks by status
     */
    @GetMapping("/filter/status")
    public ResponseEntity<List<TaskDTO>> getTasksByStatus(
            @RequestParam String status) {
        log.info("GET /api/v1/tasks/filter/status?status={} - Fetching tasks by status", status);
        List<TaskDTO> tasks = taskService.getTasksByStatus(status);
        return ResponseEntity.ok(tasks);
    }

    /**
     * GET /api/v1/tasks/filter/priority?priority={priority} - Filter tasks by priority
     */
    @GetMapping("/filter/priority")
    public ResponseEntity<List<TaskDTO>> getTasksByPriority(
            @RequestParam String priority) {
        log.info("GET /api/v1/tasks/filter/priority?priority={} - Fetching tasks by priority", priority);
        List<TaskDTO> tasks = taskService.getTasksByPriority(priority);
        return ResponseEntity.ok(tasks);
    }

    /**
     * Exception handler for TaskNotFoundException
     */
    @ExceptionHandler(TaskNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleTaskNotFoundException(
            TaskNotFoundException ex,
            WebRequest request) {
        log.error("TaskNotFoundException: {}", ex.getMessage());
        ErrorResponse error = ErrorResponse.of(
                HttpStatus.NOT_FOUND.value(),
                "Task Not Found",
                ex.getMessage(),
                request.getDescription(false)
        );
        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }

    /**
     * Exception handler for IllegalArgumentException
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgumentException(
            IllegalArgumentException ex,
            WebRequest request) {
        log.error("IllegalArgumentException: {}", ex.getMessage());
        ErrorResponse error = ErrorResponse.of(
                HttpStatus.BAD_REQUEST.value(),
                "Invalid Argument",
                ex.getMessage(),
                request.getDescription(false)
        );
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    /**
     * Global exception handler
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGlobalException(
            Exception ex,
            WebRequest request) {
        log.error("Unexpected error occurred: {}", ex.getMessage(), ex);
        ErrorResponse error = ErrorResponse.of(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "Internal Server Error",
                "An unexpected error occurred: " + ex.getMessage(),
                request.getDescription(false)
        );
        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
