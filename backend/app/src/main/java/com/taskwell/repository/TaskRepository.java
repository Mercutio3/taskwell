package com.taskwell.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.taskwell.model.Task;
import java.util.List;
import com.taskwell.model.User;
import java.time.LocalDateTime;

public interface TaskRepository extends JpaRepository<Task, Long> {
    // Custom query methods
    Task findByTitle(String title);

    List<Task> findByUser(User user);

    List<Task> findByStatus(String status);

    List<Task> findByCategory(String category);

    List<Task> findByPriority(int priority);

    List<Task> findByDueDate(LocalDateTime dateTime);

    @Query("SELECT t FROM Task t WHERE t.dueDate < CURRENT_TIMESTAMP AND t.completed = false")
    List<Task> findOverdueTasks();

    @Query("SELECT t FROM Task t WHERE t.dueDate > CURRENT_TIMESTAMP AND t.completed = false")
    List<Task> findUpcomingTasks();

}
