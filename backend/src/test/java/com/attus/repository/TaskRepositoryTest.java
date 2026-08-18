package com.attus.repository;

import com.attus.entity.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
@DisplayName("TaskRepository Integration Tests")
class TaskRepositoryTest {

    @Autowired
    private TaskRepository taskRepository;

    private Task testTask;

    @BeforeEach
    void setUp() {
        testTask = new Task();
        testTask.setTitle("Test Task");
        testTask.setDescription("Test Description");
        testTask.setStatus(Task.TaskStatus.PENDING);
        testTask.setPriority(Task.TaskPriority.HIGH);
        testTask.setCreatedAt(LocalDateTime.now());
        testTask.setUpdatedAt(LocalDateTime.now());
    }

    @Test
    @DisplayName("Should save a task successfully")
    void testSaveTask() {
        // Act
        Task savedTask = taskRepository.save(testTask);

        // Assert
        assertThat(savedTask).isNotNull();
        assertThat(savedTask.getId()).isNotNull();
        assertThat(savedTask.getTitle()).isEqualTo("Test Task");
        assertThat(savedTask.getStatus()).isEqualTo(Task.TaskStatus.PENDING);
    }

    @Test
    @DisplayName("Should find task by ID")
    void testFindById() {
        // Arrange
        Task savedTask = taskRepository.save(testTask);

        // Act
        Optional<Task> foundTask = taskRepository.findById(savedTask.getId());

        // Assert
        assertThat(foundTask).isPresent();
        assertThat(foundTask.get().getTitle()).isEqualTo("Test Task");
        assertThat(foundTask.get().getId()).isEqualTo(savedTask.getId());
    }

    @Test
    @DisplayName("Should not find non-existent task")
    void testFindByIdNotFound() {
        // Act
        Optional<Task> foundTask = taskRepository.findById(999L);

        // Assert
        assertThat(foundTask).isEmpty();
    }

    @Test
    @DisplayName("Should find all tasks")
    void testFindAll() {
        // Arrange
        Task task1 = new Task();
        task1.setTitle("Task 1");
        task1.setStatus(Task.TaskStatus.PENDING);
        task1.setPriority(Task.TaskPriority.HIGH);
        task1.setCreatedAt(LocalDateTime.now());

        Task task2 = new Task();
        task2.setTitle("Task 2");
        task2.setStatus(Task.TaskStatus.COMPLETED);
        task2.setPriority(Task.TaskPriority.LOW);
        task2.setCreatedAt(LocalDateTime.now());

        taskRepository.save(task1);
        taskRepository.save(task2);

        // Act
        List<Task> allTasks = taskRepository.findAll();

        // Assert
        assertThat(allTasks).hasSize(2);
        assertThat(allTasks).extracting(Task::getTitle).contains("Task 1", "Task 2");
    }

    @Test
    @DisplayName("Should update a task")
    void testUpdateTask() {
        // Arrange
        Task savedTask = taskRepository.save(testTask);
        
        // Act
        savedTask.setTitle("Updated Task");
        savedTask.setStatus(Task.TaskStatus.COMPLETED);
        Task updatedTask = taskRepository.save(savedTask);

        // Assert
        assertThat(updatedTask.getTitle()).isEqualTo("Updated Task");
        assertThat(updatedTask.getStatus()).isEqualTo(Task.TaskStatus.COMPLETED);
    }

    @Test
    @DisplayName("Should delete a task")
    void testDeleteTask() {
        // Arrange
        Task savedTask = taskRepository.save(testTask);
        Long taskId = savedTask.getId();

        // Act
        taskRepository.deleteById(taskId);

        // Assert
        Optional<Task> deletedTask = taskRepository.findById(taskId);
        assertThat(deletedTask).isEmpty();
    }

    @Test
    @DisplayName("Should find tasks by status")
    void testFindByStatus() {
        // Arrange
        Task pendingTask1 = new Task();
        pendingTask1.setTitle("Pending Task 1");
        pendingTask1.setStatus(Task.TaskStatus.PENDING);
        pendingTask1.setPriority(Task.TaskPriority.MEDIUM);
        pendingTask1.setCreatedAt(LocalDateTime.now());

        Task completedTask = new Task();
        completedTask.setTitle("Completed Task");
        completedTask.setStatus(Task.TaskStatus.COMPLETED);
        completedTask.setPriority(Task.TaskPriority.LOW);
        completedTask.setCreatedAt(LocalDateTime.now());

        taskRepository.save(pendingTask1);
        taskRepository.save(completedTask);

        // Act
        List<Task> pendingTasks = taskRepository.findByStatus(Task.TaskStatus.PENDING);

        // Assert
        assertThat(pendingTasks).hasSize(1);
        assertThat(pendingTasks.get(0).getTitle()).isEqualTo("Pending Task 1");
        assertThat(pendingTasks.get(0).getStatus()).isEqualTo(Task.TaskStatus.PENDING);
    }

    @Test
    @DisplayName("Should find tasks by priority")
    void testFindByPriority() {
        // Arrange
        Task highPriorityTask1 = new Task();
        highPriorityTask1.setTitle("High Priority 1");
        highPriorityTask1.setStatus(Task.TaskStatus.PENDING);
        highPriorityTask1.setPriority(Task.TaskPriority.HIGH);
        highPriorityTask1.setCreatedAt(LocalDateTime.now());

        Task highPriorityTask2 = new Task();
        highPriorityTask2.setTitle("High Priority 2");
        highPriorityTask2.setStatus(Task.TaskStatus.COMPLETED);
        highPriorityTask2.setPriority(Task.TaskPriority.HIGH);
        highPriorityTask2.setCreatedAt(LocalDateTime.now());

        Task lowPriorityTask = new Task();
        lowPriorityTask.setTitle("Low Priority");
        lowPriorityTask.setStatus(Task.TaskStatus.PENDING);
        lowPriorityTask.setPriority(Task.TaskPriority.LOW);
        lowPriorityTask.setCreatedAt(LocalDateTime.now());

        taskRepository.save(highPriorityTask1);
        taskRepository.save(highPriorityTask2);
        taskRepository.save(lowPriorityTask);

        // Act
        List<Task> highPriorityTasks = taskRepository.findByPriority(Task.TaskPriority.HIGH);

        // Assert
        assertThat(highPriorityTasks).hasSize(2);
        assertThat(highPriorityTasks)
                .extracting(Task::getTitle)
                .contains("High Priority 1", "High Priority 2");
    }

    @Test
    @DisplayName("Should find all tasks ordered by priority and date")
    void testFindAllOrderByPriorityAndDate() {
        // Arrange
        Task lowPriorityTask = new Task();
        lowPriorityTask.setTitle("Low Priority");
        lowPriorityTask.setStatus(Task.TaskStatus.PENDING);
        lowPriorityTask.setPriority(Task.TaskPriority.LOW);
        lowPriorityTask.setCreatedAt(LocalDateTime.now());

        Task highPriorityTask = new Task();
        highPriorityTask.setTitle("High Priority");
        highPriorityTask.setStatus(Task.TaskStatus.PENDING);
        highPriorityTask.setPriority(Task.TaskPriority.HIGH);
        highPriorityTask.setCreatedAt(LocalDateTime.now().minusDays(1));

        taskRepository.save(lowPriorityTask);
        taskRepository.save(highPriorityTask);

        // Act
        List<Task> orderedTasks = taskRepository.findAllOrderByPriorityAndDate();

        // Assert
        assertThat(orderedTasks).hasSize(2);
        // High priority should come first
        assertThat(orderedTasks.get(0).getPriority()).isEqualTo(Task.TaskPriority.HIGH);
        assertThat(orderedTasks.get(1).getPriority()).isEqualTo(Task.TaskPriority.LOW);
    }
}
