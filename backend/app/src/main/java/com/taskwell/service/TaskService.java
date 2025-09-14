package com.taskwell.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

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

import org.springframework.security.access.AccessDeniedException;
import com.taskwell.security.CustomUserDetails;
import com.taskwell.utils.SecurityUtils;

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
        if (task == null) {
            throw new NullPointerException("Task must not be null");
        }

        CustomUserDetails currentUser = SecurityUtils.getCurrentUser();
        if (currentUser == null || !currentUser.isVerified()) {
            throw new AccessDeniedException("User must be verified to create tasks");
        }

        if (!ValidationUtils.isValidTaskName(task.getTitle())) {
            throw new IllegalArgumentException("Invalid task name");
        }

        // Set the current user as the owner
        User userEntity = userRepository.findById(currentUser.getId())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        task.setUser(userEntity);

        logger.info("Created task: {}", task.getTitle());
        return taskRepository.save(task);
    }

    // Update an existing task
    public Task updateTask(Long id, Task updatedTask) {
        if (updatedTask == null) {
            throw new IllegalArgumentException("Updated task must not be null");
        }
        if (!ValidationUtils.isValidTaskName(updatedTask.getTitle())) {
            throw new IllegalArgumentException("Invalid task name");
        }
        CustomUserDetails currentUser = SecurityUtils.getCurrentUser();
        Task existingTask = findTaskById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Task not found"));
        if (!existingTask.getUser().getId().equals(currentUser.getId())) {
            throw new AccessDeniedException("You do not own this task");
        }

        existingTask.setTitle(updatedTask.getTitle());
        existingTask.setDescription(updatedTask.getDescription());
        existingTask.setDueDate(updatedTask.getDueDate());
        existingTask.setPriority(updatedTask.getPriority());
        existingTask.setStatus(updatedTask.getStatus());
        // Optionally: do not allow changing user/ID here
        logger.info("Updated task: {}", id);
        return taskRepository.save(existingTask);
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
        task.setStatus(TaskStatus.COMPLETE);
        task.setCompletedAt(LocalDateTime.now());
        return taskRepository.save(task);
    }

    // Mark task as uncompleted
    public Task markTaskAsUncompleted(Long id) {
        Task task = findTaskById(id).orElseThrow(() -> {
            return new IllegalArgumentException("Task not found");
        });
        logger.info("Marking task {} as uncompleted", id);
        task.setStatus(TaskStatus.PENDING);
        task.setCompletedAt(null);
        return taskRepository.save(task);
    }

    // Delete task
    public boolean deleteTask(Long id) {

        CustomUserDetails currentUser = SecurityUtils.getCurrentUser();
        Task existingTask = findTaskById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Task not found"));
        if (!existingTask.getUser().getId().equals(currentUser.getId())) {
            throw new AccessDeniedException("You do not own this task");
        }

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
