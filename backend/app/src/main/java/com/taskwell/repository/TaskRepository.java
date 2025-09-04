package com.taskwell.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.taskwell.model.Task;
import java.util.List;
import com.taskwell.model.User;

public interface TaskRepository extends JpaRepository<Task, Long> {
    // Custom query methods
    Task findByTitle(String title);

    List<Task> findByUserId(Long userId);

    List<Task> findByUser(User user);
}
