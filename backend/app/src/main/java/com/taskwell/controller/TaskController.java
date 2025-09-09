package com.taskwell.controller;

import java.util.List;

import com.taskwell.model.Task;
import com.taskwell.model.User;
import com.taskwell.service.TaskService;
import com.taskwell.service.UserService;
import com.taskwell.model.TaskStatus;
import com.taskwell.model.TaskPriority;
import com.taskwell.utils.SecurityUtils;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import jakarta.validation.Valid;
import io.swagger.v3.oas.annotations.Operation;
import com.taskwell.security.CustomUserDetails;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.time.LocalDateTime;

@RestController
@Tag(name = "Task API", description = "Operations related to tasks")
public class TaskController {

    @Autowired
    private TaskService taskService;
    @Autowired
    private UserService userService;

    // Create a new task
    @Operation(summary = "Create a new task", description = "Creates a new task and returns it.")
    @ApiResponse(responseCode = "201", description = "Task created successfully")
    @PostMapping("/api/tasks")
    public ResponseEntity<Task> createTask(@Valid @RequestBody Task task) {
        Task createdTask = taskService.createTask(task);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdTask);
    }

    // Get all tasks
    @Operation(summary = "Get all tasks", description = "Returns a list of all tasks.")
    @ApiResponse(responseCode = "200", description = "List of tasks returned successfully")
    @GetMapping("/api/tasks")
    public ResponseEntity<List<Task>> getAllTasks() {
        List<Task> tasks = taskService.findAllTasks();
        return ResponseEntity.ok(tasks);
    }

    // Get task by ID
    @Operation(summary = "Get task by ID", description = "Returns a single task by its ID.")
    @ApiResponse(responseCode = "200", description = "Task found")
    @ApiResponse(responseCode = "404", description = "Task not found")
    @GetMapping("/api/tasks/{id}")
    public ResponseEntity<Task> getTaskById(@PathVariable Long id) {
        CustomUserDetails currentUser = SecurityUtils.getCurrentUser();
        return taskService.findTaskById(id)
                .map(task -> {
                    if (!task.getUser().getId().equals(currentUser.getId())) {
                        throw new AccessDeniedException("You do not own this task");
                    }
                    return ResponseEntity.ok(task);
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Operation(summary = "Update a task", description = "Updates an existing task by its ID.")
    @ApiResponse(responseCode = "200", description = "Task updated successfully")
    @ApiResponse(responseCode = "404", description = "Task not found")
    @PutMapping("/api/tasks/{id}")
    public ResponseEntity<Task> updateTask(@PathVariable Long id, @Valid @RequestBody Task task) {
        Task updatedTask = taskService.updateTask(id, task);
        return ResponseEntity.ok(updatedTask);
    }

    @Operation(summary = "Delete a task", description = "Deletes a task by its ID.")
    @ApiResponse(responseCode = "204", description = "Task deleted successfully")
    @ApiResponse(responseCode = "404", description = "Task not found")
    @DeleteMapping("/api/tasks/{id}")
    public ResponseEntity<Void> deleteTask(@PathVariable Long id) {
        taskService.deleteTask(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/api/tasks/user/{userId}")
    public ResponseEntity<List<Task>> getTasksByUser(@PathVariable Long userId) {
        User user = userService.findByID(userId);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        List<Task> tasks = taskService.findTasksByUser(user);
        return ResponseEntity.ok(tasks);
    }

    @GetMapping("/api/tasks/status/{status}")
    public ResponseEntity<List<Task>> getTasksByStatus(@PathVariable String status) {
        List<Task> tasks = taskService.findTasksByStatus(TaskStatus.valueOf(status.toUpperCase()));
        return ResponseEntity.ok(tasks);
    }

    @GetMapping("/api/tasks/priority/{priority}")
    public ResponseEntity<List<Task>> getTasksByPriority(@PathVariable String priority) {
        List<Task> tasks = taskService.findTasksByPriority(TaskPriority.valueOf(priority.toUpperCase()));
        return ResponseEntity.ok(tasks);
    }

    @GetMapping("/api/tasks/category/{category}")
    public ResponseEntity<List<Task>> getTasksByCategory(@PathVariable String category) {
        List<Task> tasks = taskService.findTasksByCategory(category);
        return ResponseEntity.ok(tasks);
    }

    @GetMapping("/api/tasks/due/{dueDate}")
    public ResponseEntity<List<Task>> getTasksByDueDate(@PathVariable String dueDate) {
        LocalDateTime parsedDate;
        try {
            parsedDate = LocalDateTime.parse(dueDate);
            List<Task> tasks = taskService.findTasksByDueDate(parsedDate);
            return ResponseEntity.ok(tasks);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }

    }

    @GetMapping("/api/tasks/overdue")
    public ResponseEntity<List<Task>> getOverdueTasks() {
        List<Task> tasks = taskService.findOverdueTasks();
        return ResponseEntity.ok(tasks);
    }

    @GetMapping("/api/tasks/upcoming")
    public ResponseEntity<List<Task>> getUpcomingTasks() {
        List<Task> tasks = taskService.findUpcomingTasks();
        return ResponseEntity.ok(tasks);
    }

    @PostMapping("/api/tasks/{id}/complete")
    public ResponseEntity<Task> completeTask(@PathVariable Long id) {
        Task completedTask = taskService.markTaskAsCompleted(id);
        return ResponseEntity.ok(completedTask);
    }

    @PostMapping("/api/tasks/{id}/uncomplete")
    public ResponseEntity<Task> uncompleteTask(@PathVariable Long id) {
        Task uncompletedTask = taskService.markTaskAsUncompleted(id);
        return ResponseEntity.ok(uncompletedTask);
    }

    @PostMapping("/api/tasks/{taskId}/assign/{userId}")
    public ResponseEntity<Task> assignTaskToUser(@PathVariable Long taskId, @PathVariable Long userId) {
        User user = userService.findByID(userId);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        Task assignedTask = taskService.assignTaskToUser(taskId, user);
        return ResponseEntity.ok(assignedTask);
    }
}