package com.attus.repository;

import com.attus.entity.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {

    @Query("SELECT t FROM Task t WHERE t.status = :status ORDER BY t.priority DESC, t.createdAt DESC")
    List<Task> findByStatus(@Param("status") Task.TaskStatus status);

    @Query("SELECT t FROM Task t WHERE t.priority = :priority ORDER BY t.createdAt DESC")
    List<Task> findByPriority(@Param("priority") Task.TaskPriority priority);

    @Query("SELECT t FROM Task t ORDER BY t.priority DESC, t.createdAt DESC")
    List<Task> findAllOrderByPriorityAndDate();
}
