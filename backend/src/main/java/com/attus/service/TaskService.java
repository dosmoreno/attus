package com.attus.service;

import com.attus.dto.TaskDTO;
import com.attus.entity.Task;
import com.attus.exception.TaskNotFoundException;
import com.attus.repository.TaskRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class TaskService {

    private static final Logger log = LoggerFactory.getLogger(TaskService.class);

    private final TaskRepository taskRepository;

    public TaskService(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    @Transactional(readOnly = true)
    public List<TaskDTO> getAllTasks() {
        log.info("Fetching all tasks");
        return taskRepository.findAllOrderByPriorityAndDate()
                .stream()
                .map(TaskDTO::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public TaskDTO getTaskById(Long id) {
        log.info("Fetching task with id: {}", id);
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Task not found with id: {}", id);
                    return new TaskNotFoundException("Task not found with id: " + id);
                });
        return TaskDTO.fromEntity(task);
    }

    public TaskDTO createTask(TaskDTO taskDTO) {
        log.info("Creating new task with title: {}", taskDTO.getTitle());
        try {
            Task task = taskDTO.toEntity();
            Task savedTask = taskRepository.save(task);
            log.info("Task created successfully with id: {}", savedTask.getId());
            return TaskDTO.fromEntity(savedTask);
        } catch (Exception e) {
            log.error("Error creating task: {}", e.getMessage(), e);
            throw new RuntimeException("Error creating task: " + e.getMessage());
        }
    }

    public TaskDTO updateTask(Long id, TaskDTO taskDTO) {
        log.info("Updating task with id: {}", id);
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Task not found for update with id: {}", id);
                    return new TaskNotFoundException("Task not found with id: " + id);
                });

        try {
            if (taskDTO.getTitle() != null && !taskDTO.getTitle().isBlank()) {
                task.setTitle(taskDTO.getTitle());
            }
            if (taskDTO.getDescription() != null) {
                task.setDescription(taskDTO.getDescription());
            }
            if (taskDTO.getStatus() != null) {
                task.setStatus(taskDTO.getStatus());
                // Set completedAt when task is completed
                if (taskDTO.getStatus() == Task.TaskStatus.COMPLETED && task.getCompletedAt() == null) {
                    task.setCompletedAt(LocalDateTime.now());
                }
            }
            if (taskDTO.getPriority() != null) {
                task.setPriority(taskDTO.getPriority());
            }

            Task updatedTask = taskRepository.save(task);
            log.info("Task updated successfully with id: {}", updatedTask.getId());
            return TaskDTO.fromEntity(updatedTask);
        } catch (Exception e) {
            log.error("Error updating task with id {}: {}", id, e.getMessage(), e);
            throw new RuntimeException("Error updating task: " + e.getMessage());
        }
    }

    public void deleteTask(Long id) {
        log.info("Deleting task with id: {}", id);
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Task not found for deletion with id: {}", id);
                    return new TaskNotFoundException("Task not found with id: " + id);
                });
        try {
            taskRepository.delete(task);
            log.info("Task deleted successfully with id: {}", id);
        } catch (Exception e) {
            log.error("Error deleting task with id {}: {}", id, e.getMessage(), e);
            throw new RuntimeException("Error deleting task: " + e.getMessage());
        }
    }

    @Transactional(readOnly = true)
    public List<TaskDTO> getTasksByStatus(String status) {
        log.info("Fetching tasks with status: {}", status);
        try {
            Task.TaskStatus taskStatus = Task.TaskStatus.valueOf(status.toUpperCase());
            return taskRepository.findByStatus(taskStatus)
                    .stream()
                    .map(TaskDTO::fromEntity)
                    .collect(Collectors.toList());
        } catch (IllegalArgumentException e) {
            log.error("Invalid status: {}", status);
            throw new IllegalArgumentException("Invalid status: " + status);
        }
    }

    @Transactional(readOnly = true)
    public List<TaskDTO> getTasksByPriority(String priority) {
        log.info("Fetching tasks with priority: {}", priority);
        try {
            Task.TaskPriority taskPriority = Task.TaskPriority.valueOf(priority.toUpperCase());
            return taskRepository.findByPriority(taskPriority)
                    .stream()
                    .map(TaskDTO::fromEntity)
                    .collect(Collectors.toList());
        } catch (IllegalArgumentException e) {
            log.error("Invalid priority: {}", priority);
            throw new IllegalArgumentException("Invalid priority: " + priority);
        }
    }
}
