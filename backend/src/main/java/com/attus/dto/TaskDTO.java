package com.attus.dto;

import com.attus.entity.Task;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

public class TaskDTO {
    private Long id;

    @NotBlank(message = "Title is required")
    private String title;

    private String description;

    @NotNull(message = "Status is required")
    private Task.TaskStatus status;

    @NotNull(message = "Priority is required")
    private Task.TaskPriority priority;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime completedAt;

    // Constructors
    public TaskDTO() {
    }

    public TaskDTO(Long id, String title, String description, Task.TaskStatus status,
                   Task.TaskPriority priority, LocalDateTime createdAt, LocalDateTime updatedAt,
                   LocalDateTime completedAt) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.status = status;
        this.priority = priority;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.completedAt = completedAt;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Task.TaskStatus getStatus() {
        return status;
    }

    public void setStatus(Task.TaskStatus status) {
        this.status = status;
    }

    public Task.TaskPriority getPriority() {
        return priority;
    }

    public void setPriority(Task.TaskPriority priority) {
        this.priority = priority;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public LocalDateTime getCompletedAt() {
        return completedAt;
    }

    public void setCompletedAt(LocalDateTime completedAt) {
        this.completedAt = completedAt;
    }

    // Builder method
    public static TaskDTOBuilder builder() {
        return new TaskDTOBuilder();
    }

    public static class TaskDTOBuilder {
        private Long id;
        private String title;
        private String description;
        private Task.TaskStatus status;
        private Task.TaskPriority priority;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
        private LocalDateTime completedAt;

        public TaskDTOBuilder id(Long id) {
            this.id = id;
            return this;
        }

        public TaskDTOBuilder title(String title) {
            this.title = title;
            return this;
        }

        public TaskDTOBuilder description(String description) {
            this.description = description;
            return this;
        }

        public TaskDTOBuilder status(Task.TaskStatus status) {
            this.status = status;
            return this;
        }

        public TaskDTOBuilder priority(Task.TaskPriority priority) {
            this.priority = priority;
            return this;
        }

        public TaskDTOBuilder createdAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public TaskDTOBuilder updatedAt(LocalDateTime updatedAt) {
            this.updatedAt = updatedAt;
            return this;
        }

        public TaskDTOBuilder completedAt(LocalDateTime completedAt) {
            this.completedAt = completedAt;
            return this;
        }

        public TaskDTO build() {
            return new TaskDTO(id, title, description, status, priority, createdAt, updatedAt, completedAt);
        }
    }

    public static TaskDTO fromEntity(Task task) {
        return TaskDTO.builder()
                .id(task.getId())
                .title(task.getTitle())
                .description(task.getDescription())
                .status(task.getStatus())
                .priority(task.getPriority())
                .createdAt(task.getCreatedAt())
                .updatedAt(task.getUpdatedAt())
                .completedAt(task.getCompletedAt())
                .build();
    }

    public Task toEntity() {
        Task task = new Task();
        task.setId(this.id);
        task.setTitle(this.title);
        task.setDescription(this.description);
        task.setStatus(this.status != null ? this.status : Task.TaskStatus.PENDING);
        task.setPriority(this.priority != null ? this.priority : Task.TaskPriority.MEDIUM);
        task.setCreatedAt(this.createdAt);
        task.setUpdatedAt(this.updatedAt);
        task.setCompletedAt(this.completedAt);
        return task;
    }
}
