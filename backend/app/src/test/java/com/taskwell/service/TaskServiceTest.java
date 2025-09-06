package com.taskwell.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import com.taskwell.repository.TaskRepository;
import com.taskwell.repository.UserRepository;
import com.taskwell.model.Task;
import com.taskwell.model.TaskPriority;
import com.taskwell.model.TaskStatus;
import com.taskwell.model.User;
import com.taskwell.utils.ValidationUtils;

import java.util.Optional;
import java.util.List;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import org.mockito.InjectMocks;

@ExtendWith(MockitoExtension.class)
class TaskServiceTest {
    @Mock
    private TaskRepository taskRepository;
    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private TaskService taskService;

    @Test
    void createTask_Success() {
        Task task = new Task();
        task.setTitle("Valid Task Name");
        when(taskRepository.save(task)).thenReturn(task);
        Task createdTask = taskService.createTask(task);
        assertEquals("Valid Task Name", createdTask.getTitle());
    }

    @Test
    void createTask_EmptyName_ThrowsException() {
        Task task = new Task();
        task.setTitle(""); // Invalid name

        try (MockedStatic<ValidationUtils> mocked = mockStatic(ValidationUtils.class)) {
            mocked.when(() -> ValidationUtils.isValidTaskName(anyString())).thenReturn(false);

            // Assertions
            Exception exception = assertThrows(IllegalArgumentException.class, () -> {
                taskService.createTask(task);
            });
            assertTrue(exception.getMessage().contains("Invalid task name"));

            // Verifications
            verify(taskRepository, never()).save(task);
        }
    }

    @Test
    void createTask_NullName_ThrowsException() {
        Task task = new Task();
        task.setTitle(null); // Invalid name

        try (MockedStatic<ValidationUtils> mocked = mockStatic(ValidationUtils.class)) {
            mocked.when(() -> ValidationUtils.isValidTaskName(anyString())).thenReturn(false);

            // Assertions
            Exception exception = assertThrows(IllegalArgumentException.class, () -> {
                taskService.createTask(task);
            });
            assertTrue(exception.getMessage().contains("Invalid task name"));

            // Verifications
            verify(taskRepository, never()).save(task);
        }
    }

    @Test
    void createTask_NameTooLong_ThrowsException() {
        Task task = new Task();
        task.setTitle("A".repeat(101)); // Invalid name (too long)

        try (MockedStatic<ValidationUtils> mocked = mockStatic(ValidationUtils.class)) {
            mocked.when(() -> ValidationUtils.isValidTaskName(anyString())).thenReturn(false);

            // Assertions
            Exception exception = assertThrows(IllegalArgumentException.class, () -> {
                taskService.createTask(task);
            });
            assertTrue(exception.getMessage().contains("Invalid task name"));

            // Verifications
            verify(taskRepository, never()).save(task);
        }
    }

    @Test
    void createTask_NullTaskObject_ThrowsException() {
        Task task = null; // Null task

        Exception exception = assertThrows(NullPointerException.class, () -> {
            taskService.createTask(task);
        });
        assertNotNull(exception);

        // Verifications
        verify(taskRepository, never()).save(any());
    }

    @Test
    void findTaskById_Success() {
        Task task = new Task();
        task.setId(1L);
        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));
        Optional<Task> foundTask = taskService.findTaskById(1L);
        assertTrue(foundTask.isPresent());
        assertEquals(1L, foundTask.get().getId());
    }

    @Test
    void findTaskById_NotFound() {
        when(taskRepository.findById(1L)).thenReturn(Optional.empty());
        Optional<Task> foundTask = taskService.findTaskById(1L);
        assertFalse(foundTask.isPresent());
    }

    @Test
    void findAllTasks_Success() {
        taskService.findAllTasks();
        verify(taskRepository).findAll();
    }

    @Test
    void findTasksByUser_Success() {
        Task task = new Task();
        task.setId(1L);
        User user = new User();
        user.setId(1L);
        task.setUser(user);
        when(taskRepository.findByUser(any())).thenReturn(java.util.List.of(task));
        List<Task> tasks = taskService.findTasksByUser(user);
        assertEquals(1, tasks.size());
        assertEquals(task, tasks.get(0));
        verify(taskRepository).findByUser(user);
    }

    @Test
    void findTaskByUser_NoTasks_ReturnsEmptyList() {
        User user = new User();
        user.setId(1L);
        when(taskRepository.findByUser(any())).thenReturn(java.util.List.of());
        List<Task> tasks = taskService.findTasksByUser(user);
        assertTrue(tasks.isEmpty());
        verify(taskRepository).findByUser(user);
    }

    @Test
    void findTasksByStatus_Success() {
        Task task = new Task();
        task.setId(1L);
        task.setStatus(com.taskwell.model.TaskStatus.PENDING);
        when(taskRepository.findByStatus(TaskStatus.PENDING)).thenReturn(List.of(task));
        List<Task> tasks = taskService.findTasksByStatus(TaskStatus.PENDING);
        assertEquals(1, tasks.size());
        assertEquals(task, tasks.get(0));
        verify(taskRepository).findByStatus(TaskStatus.PENDING);
    }

    @Test
    void findTasksByStatus_NoTasks_ReturnsEmptyList() {
        when(taskRepository.findByStatus(TaskStatus.PENDING)).thenReturn(List.of());
        List<Task> tasks = taskService.findTasksByStatus(TaskStatus.PENDING);
        assertTrue(tasks.isEmpty());
        verify(taskRepository).findByStatus(TaskStatus.PENDING);
    }

    @Test
    void findTasksByStatus_NullStatus_ThrowsException() {
        Exception exception = assertThrows(NullPointerException.class, () -> {
            taskService.findTasksByStatus(null);
        });
        assertNotNull(exception);
        verify(taskRepository, never()).findByStatus(any());
    }

    @Test
    void findTasksByCategory_Success() {
        Task task = new Task();
        task.setId(1L);
        task.setCategory("Work");
        when(taskRepository.findByCategory("Work")).thenReturn(List.of(task));
        List<Task> tasks = taskService.findTasksByCategory("Work");
        assertEquals(1, tasks.size());
        assertEquals(task, tasks.get(0));
        verify(taskRepository).findByCategory("Work");
    }

    @Test
    void findTasksByCategory_NoTasks_ReturnsEmptyList() {
        when(taskRepository.findByCategory("Work")).thenReturn(List.of());
        List<Task> tasks = taskService.findTasksByCategory("Work");
        assertTrue(tasks.isEmpty());
        verify(taskRepository).findByCategory("Work");
    }

    @Test
    void findTasksByCategory_NullOrEmptyCategory_ThrowsException() {
        Exception exception1 = assertThrows(IllegalArgumentException.class, () -> {
            taskService.findTasksByCategory(null);
        });
        assertTrue(exception1.getMessage().contains("Category must not be null or empty"));

        Exception exception2 = assertThrows(IllegalArgumentException.class, () -> {
            taskService.findTasksByCategory("   ");
        });
        assertTrue(exception2.getMessage().contains("Category must not be null or empty"));

        verify(taskRepository, never()).findByCategory(any());
    }

    @Test
    void findTasksByPriority_Success() {
        Task task = new Task();
        task.setId(1L);
        task.setPriority(com.taskwell.model.TaskPriority.HIGH);
        when(taskRepository.findByPriority(TaskPriority.HIGH)).thenReturn(List.of(task));
        List<Task> tasks = taskService.findTasksByPriority(TaskPriority.HIGH);
        assertEquals(1, tasks.size());
        assertEquals(task, tasks.get(0));
        verify(taskRepository).findByPriority(TaskPriority.HIGH);
    }

    @Test
    void findTasksByPriority_NoTasks_ReturnsEmptyList() {
        when(taskRepository.findByPriority(TaskPriority.HIGH)).thenReturn(List.of());
        List<Task> tasks = taskService.findTasksByPriority(TaskPriority.HIGH);
        assertTrue(tasks.isEmpty());
        verify(taskRepository).findByPriority(TaskPriority.HIGH);
    }

    @Test
    void findTasksByPriority_NullPriority_ThrowsException() {
        Exception exception = assertThrows(NullPointerException.class, () -> {
            taskService.findTasksByPriority(null);
        });
        assertNotNull(exception);
        verify(taskRepository, never()).findByPriority(any());
    }

    @Test
    void findTasksByDueDate_Success() {
        Task task = new Task();
        task.setId(1L);
        LocalDateTime dueDate = LocalDateTime.now().plusDays(1);
        task.setDueDate(dueDate);
        when(taskRepository.findByDueDate(dueDate)).thenReturn(List.of(task));
        List<Task> tasks = taskService.findTasksByDueDate(dueDate);
        assertEquals(1, tasks.size());
        assertEquals(task, tasks.get(0));
        verify(taskRepository).findByDueDate(dueDate);
    }

    @Test
    void findTasksByDueDate_NoTasks_ReturnsEmptyList() {
        LocalDateTime dueDate = LocalDateTime.now().plusDays(1);
        when(taskRepository.findByDueDate(dueDate)).thenReturn(List.of());
        List<Task> tasks = taskService.findTasksByDueDate(dueDate);
        assertTrue(tasks.isEmpty());
        verify(taskRepository).findByDueDate(dueDate);
    }

    @Test
    void findOverdueTasks_Success() {
        Task task = new Task();
        task.setId(1L);
        task.setDueDate(LocalDateTime.now().minusDays(1));
        task.setCompleted(false);
        when(taskRepository.findOverdueTasks()).thenReturn(List.of(task));
        List<Task> tasks = taskService.findOverdueTasks();
        assertEquals(1, tasks.size());
        assertEquals(task, tasks.get(0));
        verify(taskRepository).findOverdueTasks();
    }

    @Test
    void FindOverdueTasks_NoTasks_ReturnsEmptyList() {
        when(taskRepository.findOverdueTasks()).thenReturn(List.of());
        List<Task> tasks = taskService.findOverdueTasks();
        assertTrue(tasks.isEmpty());
        verify(taskRepository).findOverdueTasks();
    }

    @Test
    void findUpcomingTasks_Success() {
        Task task = new Task();
        task.setId(1L);
        task.setDueDate(LocalDateTime.now().plusDays(1));
        task.setCompleted(false);
        when(taskRepository.findUpcomingTasks()).thenReturn(List.of(task));
        List<Task> tasks = taskService.findUpcomingTasks();
        assertEquals(1, tasks.size());
        assertEquals(task, tasks.get(0));
        verify(taskRepository).findUpcomingTasks();
    }

    @Test
    void findUpcomingTasks_NoTasks_ReturnsEmptyList() {
        when(taskRepository.findUpcomingTasks()).thenReturn(List.of());
        List<Task> tasks = taskService.findUpcomingTasks();
        assertTrue(tasks.isEmpty());
        verify(taskRepository).findUpcomingTasks();
    }

    @Test
    void markTaskAsCompleted_Success() {
        Task task = new Task();
        task.setId(1L);
        task.setCompleted(false);
        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));
        when(taskRepository.save(any(Task.class))).thenReturn(task);

        Task updatedTask = taskService.markTaskAsCompleted(1L);
        assertTrue(updatedTask.isCompleted());
        verify(taskRepository).save(task);
    }

    @Test
    void markTaskAsCompleted_TaskNotFound_ThrowsException() {
        when(taskRepository.findById(1L)).thenReturn(Optional.empty());

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            taskService.markTaskAsCompleted(1L);
        });
        assertTrue(exception.getMessage().contains("Task not found"));
        verify(taskRepository, never()).save(any(Task.class));
    }

    @Test
    void markTaskAsUncompleted_Success() {
        Task task = new Task();
        task.setId(1L);
        task.setCompleted(true);
        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));
        when(taskRepository.save(any(Task.class))).thenReturn(task);

        Task updatedTask = taskService.markTaskAsUncompleted(1L);
        assertFalse(updatedTask.isCompleted());
        verify(taskRepository).save(task);
    }

    @Test
    void markTaskAsUncompleted_TaskNotFound_ThrowsException() {
        when(taskRepository.findById(1L)).thenReturn(Optional.empty());

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            taskService.markTaskAsUncompleted(1L);
        });
        assertTrue(exception.getMessage().contains("Task not found"));
        verify(taskRepository, never()).save(any(Task.class));
    }

    @Test
    void deleteTask_Success() {
        Task task = new Task();
        task.setId(1L);
        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));
        boolean result = taskService.deleteTask(1L);
        assertTrue(result);
        verify(taskRepository).deleteById(1L);
    }

    @Test
    void deleteTask_TaskNotFound_ReturnsFalse() {
        when(taskRepository.findById(1L)).thenReturn(Optional.empty());
        boolean result = taskService.deleteTask(1L);
        assertFalse(result);
        verify(taskRepository, never()).deleteById(1L);
    }

    @Test
    void assignTaskToUser_Success() {
        Task task = new Task();
        task.setId(1L);
        User user = new User();
        user.setId(1L);
        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(taskRepository.save(any(Task.class))).thenReturn(task);
        Task updatedTask = taskService.assignTaskToUser(1L, user);
        assertEquals(user, updatedTask.getUser());
        verify(taskRepository).save(task);
    }

    @Test
    void assignTaskToUser_TaskNotFound_ThrowsException() {
        User user = new User();
        user.setId(1L);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(taskRepository.findById(1L)).thenReturn(Optional.empty());
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            taskService.assignTaskToUser(1L, user);
        });
        assertTrue(exception.getMessage().contains("Task not found"));
        verify(taskRepository, never()).save(any(Task.class));
    }

    @Test
    void assignTaskToUser_UserNotFound_ThrowsException() {
        Task task = new Task();
        task.setId(1L);
        User user = new User();
        user.setId(1L);
        when(userRepository.findById(1L)).thenReturn(Optional.empty());
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            taskService.assignTaskToUser(1L, user);
        });
        assertTrue(exception.getMessage().contains("Invalid user"));
        verify(taskRepository, never()).save(any(Task.class));
    }

    @Test
    void assignTaskToUser_NullUser_ThrowsException() {
        Task task = new Task();
        task.setId(1L);
        User user = null;
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            taskService.assignTaskToUser(1L, user);
        });
        assertTrue(exception.getMessage().contains("Invalid user"));
        verify(taskRepository, never()).save(any(Task.class));
    }
}