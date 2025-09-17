package com.taskwell.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import com.taskwell.repository.TaskRepository;
import com.taskwell.repository.UserRepository;
import com.taskwell.model.Task;
import com.taskwell.model.TaskCategory;
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
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

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
        task.setDueDate(LocalDateTime.now().plusDays(1)); // (Valid due date)

        try (MockedStatic<com.taskwell.utils.SecurityUtils> mockedSecurity = mockStatic(
                com.taskwell.utils.SecurityUtils.class)) {
            com.taskwell.model.User user = new com.taskwell.model.User();
            user.setId(1L);
            user.setVerified(true);
            com.taskwell.security.CustomUserDetails principal = new com.taskwell.security.CustomUserDetails(user);
            mockedSecurity.when(com.taskwell.utils.SecurityUtils::getCurrentUser).thenReturn(principal);

            when(userRepository.findById(1L)).thenReturn(Optional.of(user));
            task.setUser(user);

            when(taskRepository.save(task)).thenReturn(task);
            Task createdTask = taskService.createTask(task);
            assertEquals("Valid Task Name", createdTask.getTitle());
        }
    }

    @Test
    void createTask_EmptyName_ThrowsException() {
        Task task = new Task();
        task.setTitle(""); // Invalid name

        try (MockedStatic<com.taskwell.utils.SecurityUtils> mockedSecurity = mockStatic(
                com.taskwell.utils.SecurityUtils.class)) {
            com.taskwell.model.User user = new com.taskwell.model.User();
            user.setId(1L);
            user.setVerified(true);
            com.taskwell.security.CustomUserDetails principal = new com.taskwell.security.CustomUserDetails(user);
            mockedSecurity.when(com.taskwell.utils.SecurityUtils::getCurrentUser).thenReturn(principal);

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
    }

    @Test
    void createTask_NullName_ThrowsException() {
        Task task = new Task();
        task.setTitle(null); // Invalid name

        try (MockedStatic<com.taskwell.utils.SecurityUtils> mockedSecurity = mockStatic(
                com.taskwell.utils.SecurityUtils.class)) {
            com.taskwell.model.User user = new com.taskwell.model.User();
            user.setId(1L);
            user.setVerified(true);
            com.taskwell.security.CustomUserDetails principal = new com.taskwell.security.CustomUserDetails(user);
            mockedSecurity.when(com.taskwell.utils.SecurityUtils::getCurrentUser).thenReturn(principal);

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
    }

    @Test
    void createTask_NameTooLong_ThrowsException() {
        Task task = new Task();
        task.setTitle("A".repeat(101)); // Invalid name (too long)

        try (MockedStatic<com.taskwell.utils.SecurityUtils> mockedSecurity = mockStatic(
                com.taskwell.utils.SecurityUtils.class)) {
            com.taskwell.model.User user = new com.taskwell.model.User();
            user.setId(1L);
            user.setVerified(true);
            com.taskwell.security.CustomUserDetails principal = new com.taskwell.security.CustomUserDetails(user);
            mockedSecurity.when(com.taskwell.utils.SecurityUtils::getCurrentUser).thenReturn(principal);

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
    }

    @Test
    void createTask_NullTaskObject_ThrowsException() {
        Task task = null; // Null task

        // Optionally mock security context, but not strictly needed for null input
        Exception exception = assertThrows(NullPointerException.class, () -> {
            taskService.createTask(task);
        });
        assertNotNull(exception);

        // Verifications
        verify(taskRepository, never()).save(any());
    }

    void createTask_DueDateInPast_ThrowsException() {
        Task task = new Task();
        task.setTitle("Valid Task Name");
        task.setDueDate(LocalDateTime.now().minusDays(1)); // Invalid due date (in the past)

        try (MockedStatic<com.taskwell.utils.SecurityUtils> mockedSecurity = mockStatic(
                com.taskwell.utils.SecurityUtils.class)) {
            com.taskwell.model.User user = new com.taskwell.model.User();
            user.setId(1L);
            user.setVerified(true);
            com.taskwell.security.CustomUserDetails principal = new com.taskwell.security.CustomUserDetails(user);
            mockedSecurity.when(com.taskwell.utils.SecurityUtils::getCurrentUser).thenReturn(principal);

            try (MockedStatic<ValidationUtils> mocked = mockStatic(ValidationUtils.class)) {
                mocked.when(() -> ValidationUtils.isValidTaskName(anyString())).thenReturn(true);
                mocked.when(() -> ValidationUtils.isValidDueDate(any())).thenReturn(false);

                // Assertions
                Exception exception = assertThrows(IllegalArgumentException.class, () -> {
                    taskService.createTask(task);
                });
                assertTrue(exception.getMessage().contains("Due date cannot be in the past"));

                // Verifications
                verify(taskRepository, never()).save(task);
            }
        }
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
        task.setCategory(TaskCategory.WORK);
        when(taskRepository.findByCategory(TaskCategory.WORK)).thenReturn(List.of(task));
        List<Task> tasks = taskService.findTasksByCategory(TaskCategory.WORK);
        assertEquals(1, tasks.size());
        assertEquals(task, tasks.get(0));
        verify(taskRepository).findByCategory(TaskCategory.WORK);
    }

    @Test
    void findTasksByCategory_NoTasks_ReturnsEmptyList() {
        when(taskRepository.findByCategory(TaskCategory.WORK)).thenReturn(List.of());
        List<Task> tasks = taskService.findTasksByCategory(TaskCategory.WORK);
        assertTrue(tasks.isEmpty());
        verify(taskRepository).findByCategory(TaskCategory.WORK);
    }

    @Test
    void findTasksByCategory_NullCategory_ThrowsException() {
        Exception exception = assertThrows(NullPointerException.class, () -> {
            taskService.findTasksByCategory(null);
        });
        assertTrue(exception.getMessage().contains("Category must not be null"));

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
        task.setStatus(TaskStatus.PENDING);
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
        task.setStatus(TaskStatus.PENDING);
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
        try (MockedStatic<com.taskwell.utils.SecurityUtils> mockedSecurity = mockStatic(
                com.taskwell.utils.SecurityUtils.class)) {
            User user = new User();
            user.setId(1L);
            user.setVerified(true);
            com.taskwell.security.CustomUserDetails principal = new com.taskwell.security.CustomUserDetails(user);
            mockedSecurity.when(com.taskwell.utils.SecurityUtils::getCurrentUser).thenReturn(principal);

            Task task = new Task();
            task.setId(1L);
            task.setStatus(TaskStatus.PENDING);
            task.setUser(user);
            when(taskRepository.findById(1L)).thenReturn(Optional.of(task));
            when(taskRepository.save(any(Task.class))).thenReturn(task);

            Task updatedTask = taskService.markTaskAsCompleted(1L);
            assertEquals(TaskStatus.COMPLETE, updatedTask.getStatus());
            verify(taskRepository).save(task);
        }
    }

    @Test
    void markTaskAsCompleted_TaskNotFound_ThrowsException() {
        when(taskRepository.findById(1L)).thenReturn(Optional.empty());

        Exception exception = assertThrows(org.springframework.web.server.ResponseStatusException.class, () -> {
            taskService.markTaskAsCompleted(1L);
        });
        assertEquals(HttpStatus.NOT_FOUND,
                ((org.springframework.web.server.ResponseStatusException) exception).getStatusCode());
        verify(taskRepository, never()).save(any(Task.class));
    }

    @Test
    void markTaskAsUncompleted_Success() {
        try (MockedStatic<com.taskwell.utils.SecurityUtils> mockedSecurity = mockStatic(
                com.taskwell.utils.SecurityUtils.class)) {
            User user = new User();
            user.setId(1L);
            user.setVerified(true);
            com.taskwell.security.CustomUserDetails principal = new com.taskwell.security.CustomUserDetails(user);
            mockedSecurity.when(com.taskwell.utils.SecurityUtils::getCurrentUser).thenReturn(principal);

            Task task = new Task();
            task.setId(1L);
            task.setStatus(TaskStatus.COMPLETE);
            task.setUser(user);
            when(taskRepository.findById(1L)).thenReturn(Optional.of(task));
            when(taskRepository.save(any(Task.class))).thenReturn(task);

            Task updatedTask = taskService.markTaskAsUncompleted(1L);
            assertEquals(TaskStatus.PENDING, updatedTask.getStatus());
            verify(taskRepository).save(task);
        }
    }

    @Test
    void markTaskAsUncompleted_TaskNotFound_ThrowsException() {
        when(taskRepository.findById(1L)).thenReturn(Optional.empty());

        Exception exception = assertThrows(org.springframework.web.server.ResponseStatusException.class, () -> {
            taskService.markTaskAsUncompleted(1L);
        });
        assertEquals(HttpStatus.NOT_FOUND,
                ((org.springframework.web.server.ResponseStatusException) exception).getStatusCode());
        verify(taskRepository, never()).save(any(Task.class));
    }

    @Test
    void deleteTask_Success() {
        Task task = new Task();
        task.setId(1L);
        User user = new User();
        user.setId(1L);
        task.setUser(user);
        try (MockedStatic<com.taskwell.utils.SecurityUtils> mockedSecurity = mockStatic(
                com.taskwell.utils.SecurityUtils.class)) {
            com.taskwell.security.CustomUserDetails principal = new com.taskwell.security.CustomUserDetails(user);
            mockedSecurity.when(com.taskwell.utils.SecurityUtils::getCurrentUser).thenReturn(principal);

            when(taskRepository.findById(1L)).thenReturn(Optional.of(task));
            boolean result = taskService.deleteTask(1L);
            assertTrue(result);
            verify(taskRepository).deleteById(1L);
        }
    }

    @Test
    void deleteTask_TaskNotFound_ReturnsFalse() {
        // Optionally mock security context
        try (MockedStatic<com.taskwell.utils.SecurityUtils> mockedSecurity = mockStatic(
                com.taskwell.utils.SecurityUtils.class)) {
            com.taskwell.model.User user = new com.taskwell.model.User();
            user.setId(1L);
            com.taskwell.security.CustomUserDetails principal = new com.taskwell.security.CustomUserDetails(user);
            mockedSecurity.when(com.taskwell.utils.SecurityUtils::getCurrentUser).thenReturn(principal);

            when(taskRepository.findById(1L)).thenReturn(Optional.empty());
            Exception exception = assertThrows(ResponseStatusException.class, () -> {
                taskService.deleteTask(1L);
            });
            assertEquals(HttpStatus.NOT_FOUND, ((ResponseStatusException) exception).getStatusCode());
            verify(taskRepository, never()).deleteById(anyLong());
        }
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
        assertTrue(exception.getMessage().contains("User not found"));
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