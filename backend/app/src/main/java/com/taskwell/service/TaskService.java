package com.taskwell.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.taskwell.repository.TaskRepository;
import com.taskwell.utils.ValidationUtils;

import com.taskwell.model.Task;
import com.taskwell.model.User;
import java.util.List;
import java.util.Optional;
import java.time.LocalDateTime;

@Service
public class TaskService {

    private static final Logger logger = LoggerFactory.getLogger(TaskService.class);
    private final TaskRepository taskRepository;

    public TaskService(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    // Create new task (and assign to user)
    public Task createtask(Task task) {
        logger.info("Creating task: " + task.getTitle());

        if (!ValidationUtils.isValidTaskName(task.getTitle())) {
            throw new IllegalArgumentException("Invalid task name");
        }

        return taskRepository.save(task);
    }

    // Find task by ID
    public Optional<Task> findTaskById(Long id) {
        logger.info("Finding task by ID: " + id);
        return taskRepository.findById(id);
    }

    // List all tasks, or filter by user, status, category, etc...
    public List<Task> findAllTasks() {
        logger.info("Listing all tasks");
        return taskRepository.findAll();
    }

    public List<Task> findTasksByUser(User user) {
        logger.info("Listing tasks for user: " + user);
        return taskRepository.findByUser(user);
    }

    public List<Task> findTasksByStatus(String status) {
        logger.info("Listing tasks with status: " + status);
        return taskRepository.findByStatus(status);
    }

    public List<Task> findTasksByCategory(String category) {
        logger.info("Listing tasks in category: " + category);
        return taskRepository.findByCategory(category);
    }

    public List<Task> findTasksByPriority(int priority) {
        logger.info("Listing tasks with priority: " + priority);
        return taskRepository.findByPriority(priority);
    }

    // Mark task as completed
    public Task markTaskAsCompleted(Long id) {
        logger.info("Marking task " + id + " as completed");
        Task task = findTaskById(id).orElseThrow(() -> {
            logger.error("Task not found: " + id);
            return new IllegalArgumentException("Task not found");
        });
        task.setCompleted(true);
        return taskRepository.save(task);
    }

    // Mark task as uncompleted
    public Task markTaskAsUncompleted(Long id) {
        logger.info("Marking task " + id + " as uncompleted");
        Task task = findTaskById(id).orElseThrow(() -> {
            logger.error("Task not found: " + id);
            return new IllegalArgumentException("Task not found");
        });
        task.setCompleted(false);
        return taskRepository.save(task);
    }

    // Delete task
    public boolean deleteTask(Long id) {
        logger.info("Deleting task: " + id);
        if (findTaskById(id) != null) {
            taskRepository.deleteById(id);
            return true;
        }
        logger.error("Task deletion failed. Task not found: " + id);
        return false;
    }

    // List tasks by due date
    public List<Task> findTasksByDueDate(LocalDateTime dueDate) {
        logger.info("Listing tasks with due date: " + dueDate);
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
        logger.info("Assigning task " + taskId + " to user " + user);
        Task task = findTaskById(taskId).orElseThrow(() -> {
            logger.error("Task not found: " + taskId);
            return new IllegalArgumentException("Task not found");
        });
        task.setUser(user);
        return taskRepository.save(task);
    }
}
