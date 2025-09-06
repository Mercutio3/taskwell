package com.taskwell.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.taskwell.repository.TaskRepository;
import com.taskwell.repository.UserRepository;
import com.taskwell.utils.ValidationUtils;

import com.taskwell.model.Task;
import com.taskwell.model.User;
import com.taskwell.model.TaskStatus;
import com.taskwell.model.TaskPriority;
import java.util.List;
import java.util.Optional;
import java.time.LocalDateTime;

@Service
public class TaskService {

    private static final Logger logger = LoggerFactory.getLogger(TaskService.class);
    private final TaskRepository taskRepository;
    private final UserRepository userRepository;

    public TaskService(TaskRepository taskRepository, UserRepository userRepository) {
        this.taskRepository = taskRepository;
        this.userRepository = userRepository;
    }

    // Create new task (and assign to user)
    public Task createTask(Task task) {
        if (!ValidationUtils.isValidTaskName(task.getTitle())) {
            throw new IllegalArgumentException("Invalid task name");
        }
        logger.info("Created task: {}", task.getTitle());
        return taskRepository.save(task);
    }

    // Find task by ID
    public Optional<Task> findTaskById(Long id) {
        logger.info("Finding task by ID: {}", id);
        return taskRepository.findById(id);
    }

    // List all tasks, or filter by user, status, category, etc...
    public List<Task> findAllTasks() {
        logger.info("Listing all tasks");
        return taskRepository.findAll();
    }

    public List<Task> findTasksByUser(User user) {
        logger.info("Listing tasks for user: {}", user);
        return taskRepository.findByUser(user);
    }

    public List<Task> findTasksByStatus(TaskStatus status) {
        if (status == null) {
            throw new NullPointerException("Task status must not be null");
        }
        logger.info("Listing tasks with status: {}", status);
        return taskRepository.findByStatus(status);
    }

    public List<Task> findTasksByCategory(String category) {
        if (category == null || category.trim().isEmpty()) {
            throw new IllegalArgumentException("Category must not be null or empty");
        }
        logger.info("Listing tasks in category: {}", category);
        return taskRepository.findByCategory(category);
    }

    public List<Task> findTasksByPriority(TaskPriority priority) {
        if (priority == null) {
            throw new NullPointerException("Task priority must not be null");
        }
        logger.info("Listing tasks with priority: {}", priority);
        return taskRepository.findByPriority(priority);
    }

    // Mark task as completed
    public Task markTaskAsCompleted(Long id) {
        Task task = findTaskById(id).orElseThrow(() -> {
            return new IllegalArgumentException("Task not found");
        });
        logger.info("Marking task {} as completed", id);
        task.setCompleted(true);
        return taskRepository.save(task);
    }

    // Mark task as uncompleted
    public Task markTaskAsUncompleted(Long id) {
        Task task = findTaskById(id).orElseThrow(() -> {
            return new IllegalArgumentException("Task not found");
        });
        logger.info("Marking task {} as uncompleted", id);
        task.setCompleted(false);
        return taskRepository.save(task);
    }

    // Delete task
    public boolean deleteTask(Long id) {
        if (findTaskById(id).isPresent()) {
            logger.info("Deleted task: {}", id);
            taskRepository.deleteById(id);
            return true;
        }
        logger.error("Task deletion failed. Task not found: {}", id);
        return false;
    }

    // List tasks by due date
    public List<Task> findTasksByDueDate(LocalDateTime dueDate) {
        logger.info("Listing tasks with due date: {}", dueDate);
        return taskRepository.findByDueDate(dueDate);
    }

    // List overdue or upcoming tasks
    public List<Task> findOverdueTasks() {
        logger.info("Listing overdue tasks");
        return taskRepository.findOverdueTasks();
    }

    public List<Task> findUpcomingTasks() {
        logger.info("Listing upcoming tasks");
        return taskRepository.findUpcomingTasks();
    }

    // Assign / reassign task to a user
    public Task assignTaskToUser(Long taskId, User user) {
        if (user == null || user.getId() == null || !userRepository.findById(user.getId()).isPresent()) {
            throw new IllegalArgumentException("Invalid user");
        }
        Task task = findTaskById(taskId).orElseThrow(() -> {
            return new IllegalArgumentException("Task not found");
        });
        task.setUser(user);
        logger.info("Assigned task {} to user {}", taskId, user);
        return taskRepository.save(task);
    }
}
