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
import io.swagger.v3.oas.annotations.Parameter;

import com.taskwell.security.CustomUserDetails;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.time.LocalDateTime;

@RestController
@Tag(name = "Task API", description = "Operations related to tasks.")
public class TaskController {

    @Autowired
    private TaskService taskService;
    @Autowired
    private UserService userService;

    // Create a new task
    @Operation(summary = "Create a new task", description = "Creates a new task and returns it.")
    @ApiResponse(responseCode = "201", description = "Task created successfully.")
    @ApiResponse(responseCode = "400", description = "Invalid input data.")
    @ApiResponse(responseCode = "403", description = "User must be verified to create tasks.")
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
    @ApiResponse(responseCode = "200", description = "Task found.")
    @ApiResponse(responseCode = "403", description = "Users can only access their own tasks.")
    @ApiResponse(responseCode = "404", description = "Task not found.")
    @GetMapping("/api/tasks/{id}")
    public ResponseEntity<Task> getTaskById(
            @Parameter(description = "ID of the task to retrieve.") @PathVariable Long id) {
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
    @ApiResponse(responseCode = "200", description = "Task updated successfully.")
    @ApiResponse(responseCode = "400", description = "Invalid input data.")
    @ApiResponse(responseCode = "403", description = "Users can only update their own tasks.")
    @ApiResponse(responseCode = "404", description = "Task not found.")
    @PutMapping("/api/tasks/{id}")
    public ResponseEntity<Task> updateTask(@Parameter(description = "ID of the task to update.") @PathVariable Long id,
            @Valid @RequestBody Task task) {
        Task updatedTask = taskService.updateTask(id, task);
        return ResponseEntity.ok(updatedTask);
    }

    @Operation(summary = "Delete a task", description = "Deletes a task by its ID.")
    @ApiResponse(responseCode = "204", description = "Task deleted successfully.")
    @ApiResponse(responseCode = "403", description = "Users can only delete their own tasks.")
    @ApiResponse(responseCode = "404", description = "Task not found.")
    @DeleteMapping("/api/tasks/{id}")
    public ResponseEntity<Void> deleteTask(
            @Parameter(description = "ID of the task to delete.") @PathVariable Long id) {
        taskService.deleteTask(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Get task by user", description = "Returns a list of tasks assigned to a given user.")
    @ApiResponse(responseCode = "200", description = "List of tasks returned successfully")
    @ApiResponse(responseCode = "404", description = "User not found.")
    @GetMapping("/api/tasks/user/{userId}")
    public ResponseEntity<List<Task>> getTasksByUser(
            @Parameter(description = "ID of the user to retrieve tasks for.") @PathVariable Long userId) {
        User user = userService.findByID(userId);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        List<Task> tasks = taskService.findTasksByUser(user);
        return ResponseEntity.ok(tasks);
    }

    @Operation(summary = "Get task by status", description = "Returns a list of tasks with a given status.")
    @ApiResponse(responseCode = "200", description = "List of tasks returned successfully.")
    @ApiResponse(responseCode = "404", description = "No tasks found for the given status.")
    @GetMapping("/api/tasks/status/{status}")
    public ResponseEntity<List<Task>> getTasksByStatus(
            @Parameter(description = "Status of the tasks to retrieve (PENDING, IN_PROGRESS, COMPLETE, CANCELLED, ON_HOLD, OVERDUE, ARCHIVED).") @PathVariable String status) {
        List<Task> tasks = taskService.findTasksByStatus(TaskStatus.valueOf(status.toUpperCase()));
        return ResponseEntity.ok(tasks);
    }

    @Operation(summary = "Get task by priority", description = "Returns a list of tasks with a given priority.")
    @ApiResponse(responseCode = "200", description = "List of tasks returned successfully.")
    @ApiResponse(responseCode = "404", description = "No tasks found for the given priority.")
    @GetMapping("/api/tasks/priority/{priority}")
    public ResponseEntity<List<Task>> getTasksByPriority(
            @Parameter(description = "Priority of the tasks to retrieve (LOW, MEDIUM, or HIGH).") @PathVariable String priority) {
        List<Task> tasks = taskService.findTasksByPriority(TaskPriority.valueOf(priority.toUpperCase()));
        return ResponseEntity.ok(tasks);
    }

    @Operation(summary = "Get task by category", description = "Returns a list of tasks from a given category")
    @ApiResponse(responseCode = "200", description = "List of tasks returned successfully.")
    @ApiResponse(responseCode = "404", description = "No tasks found for the given category.")
    @GetMapping("/api/tasks/category/{category}")
    public ResponseEntity<List<Task>> getTasksByCategory(
            @Parameter(description = "Category of the tasks to retrieve.") @PathVariable String category) {
        List<Task> tasks = taskService.findTasksByCategory(category);
        return ResponseEntity.ok(tasks);
    }

    @Operation(summary = "Get task by due date", description = "Returns a list of tasks with a given due date")
    @ApiResponse(responseCode = "200", description = "List of tasks returned successfully.")
    @ApiResponse(responseCode = "404", description = "No tasks found for the given due date.")
    @GetMapping("/api/tasks/due/{dueDate}")
    public ResponseEntity<List<Task>> getTasksByDueDate(
            @Parameter(description = "Due date of the tasks to retrieve.") @PathVariable String dueDate) {
        LocalDateTime parsedDate;
        try {
            parsedDate = LocalDateTime.parse(dueDate);
            List<Task> tasks = taskService.findTasksByDueDate(parsedDate);
            return ResponseEntity.ok(tasks);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }

    }

    @Operation(summary = "Get overdue tasks", description = "Gets a list of overdue tasks.")
    @ApiResponse(responseCode = "200", description = "List of tasks returned successfully.")
    @GetMapping("/api/tasks/overdue")
    public ResponseEntity<List<Task>> getOverdueTasks() {
        List<Task> tasks = taskService.findOverdueTasks();
        return ResponseEntity.ok(tasks);
    }

    @Operation(summary = "Get upcoming tasks", description = "Gets a list of upcoming tasks.")
    @ApiResponse(responseCode = "200", description = "List of tasks returned successfully.")
    @GetMapping("/api/tasks/upcoming")
    public ResponseEntity<List<Task>> getUpcomingTasks() {
        List<Task> tasks = taskService.findUpcomingTasks();
        return ResponseEntity.ok(tasks);
    }

    @Operation(summary = "Complete task", description = "Marks a task as completed by its ID.")
    @ApiResponse(responseCode = "200", description = "Task marked as completed successfully")
    @PostMapping("/api/tasks/{id}/complete")
    public ResponseEntity<Task> completeTask(
            @Parameter(description = "ID of the task to complete.") @PathVariable Long id) {
        Task completedTask = taskService.markTaskAsCompleted(id);
        return ResponseEntity.ok(completedTask);
    }

    @Operation(summary = "Uncomplete task", description = "Marks a task as uncompleted by its ID.")
    @ApiResponse(responseCode = "200", description = "Task marked as uncompleted successfully")
    @PostMapping("/api/tasks/{id}/uncomplete")
    public ResponseEntity<Task> uncompleteTask(
            @Parameter(description = "ID of the task to uncomplete.") @PathVariable Long id) {
        Task uncompletedTask = taskService.markTaskAsUncompleted(id);
        return ResponseEntity.ok(uncompletedTask);
    }

    @Operation(summary = "Assign task to user", description = "Assigns a task to a user by their IDs.")
    @ApiResponse(responseCode = "200", description = "Task assigned to user successfully")
    @ApiResponse(responseCode = "404", description = "User not found")
    @PostMapping("/api/tasks/{taskId}/assign/{userId}")
    public ResponseEntity<Task> assignTaskToUser(
            @Parameter(description = "ID of the task to assign.") @PathVariable Long taskId,
            @Parameter(description = "ID of the user to assign the task to.") @PathVariable Long userId) {
        User user = userService.findByID(userId);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        Task assignedTask = taskService.assignTaskToUser(taskId, user);
        return ResponseEntity.ok(assignedTask);
    }
}