package com.taskwell.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;

import java.time.LocalDateTime;

import com.jayway.jsonpath.JsonPath;
import com.taskwell.model.User;
import com.taskwell.repository.UserRepository;
import com.taskwell.security.CustomUserDetails;

import com.taskwell.model.Task;
import com.taskwell.repository.TaskRepository;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class TaskControllerIntegrationTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TaskRepository taskRepository;

    @Test
    void createTask_VerifiedUser_Success() throws Exception {
        String userJson = """
                {
                    "username": "testuser",
                    "email": "testuser@example.com",
                    "password": "GoodPassword1!"
                }
                """;

        // Register user
        String response = mockMvc.perform(post("/api/users")
                .contentType("application/json")
                .content(userJson))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        String token = JsonPath.read(response, "$.token");

        // Verify user
        mockMvc.perform(get("/api/users/verify")
                .param("token", token))
                .andExpect(status().isOk());

        User verifiedUser = userRepository.findByUsername("testuser").get();
        CustomUserDetails principal = new CustomUserDetails(verifiedUser);

        String taskJson = """
                {
                    "title": "New Task",
                    "description": "Task description",
                    "dueDate": "%s",
                    "priority": "MEDIUM",
                    "status": "PENDING"
                }
                """.formatted(LocalDateTime.now().plusDays(3).toString());

        mockMvc.perform(post("/api/tasks")
                .with(authentication(
                        new UsernamePasswordAuthenticationToken(principal, null,
                                principal.getAuthorities())))
                .contentType("application/json")
                .content(taskJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value("New Task"))
                .andExpect(jsonPath("$.description").value("Task description"))
                .andExpect(jsonPath("$.priority").value("MEDIUM"))
                .andExpect(jsonPath("$.status").value("PENDING"));

    }

    @Test
    void createTask_UnverifiedUser_Forbidden() throws Exception {
        String userJson = """
                {
                    "username": "unverifieduser",
                    "email": "unverifieduser@example.com",
                    "password": "GoodPassword1!"
                }
                """;

        // Register user
        mockMvc.perform(post("/api/users")
                .contentType("application/json")
                .content(userJson))
                .andExpect(status().isCreated());

        User unVerifiedUser = userRepository.findByUsername("unverifieduser").get();
        CustomUserDetails principal = new CustomUserDetails(unVerifiedUser);

        String taskJson = """
                {
                    "title": "New Task",
                    "description": "Task description",
                    "dueDate": "%s",
                    "priority": "MEDIUM",
                    "status": "PENDING"
                }
                """.formatted(LocalDateTime.now().plusDays(3).toString());

        mockMvc.perform(post("/api/tasks")
                .with(authentication(
                        new UsernamePasswordAuthenticationToken(principal, null,
                                principal.getAuthorities())))
                .contentType("application/json")
                .content(taskJson))
                .andExpect(status().isForbidden());
    }

    @Test
    void createTask_InvalidName_BadRequest() throws Exception {
        String userJson = """
                {
                    "username": "validuser",
                    "email": "validuser@example.com",
                    "password": "GoodPassword1!"
                }
                """;

        // Register user
        String response = mockMvc.perform(post("/api/users")
                .contentType("application/json")
                .content(userJson))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        String token = JsonPath.read(response, "$.token");

        // Verify user
        mockMvc.perform(get("/api/users/verify")
                .param("token", token))
                .andExpect(status().isOk());

        User verifiedUser = userRepository.findByUsername("validuser").get();
        CustomUserDetails principal = new CustomUserDetails(verifiedUser);

        String taskJson = """
                {
                    "title": "",
                    "description": "Task description",
                    "dueDate": "%s",
                    "priority": "MEDIUM",
                    "status": "PENDING"
                }
                """.formatted(LocalDateTime.now().plusDays(3).toString());

        mockMvc.perform(post("/api/tasks")
                .with(authentication(
                        new UsernamePasswordAuthenticationToken(principal, null,
                                principal.getAuthorities())))
                .contentType("application/json")
                .content(taskJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getTaskById_Success() throws Exception {
        String userJson = """
                {
                    "username": "taskuser",
                    "email": "taskuser@example.com",
                    "password": "GoodPassword1!"
                }
                """;

        // Register user
        String response = mockMvc.perform(post("/api/users")
                .contentType("application/json")
                .content(userJson))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        String token = JsonPath.read(response, "$.token");

        // Verify user
        mockMvc.perform(get("/api/users/verify")
                .param("token", token))
                .andExpect(status().isOk());

        User taskUser = userRepository.findByUsername("taskuser").get();
        CustomUserDetails principal = new CustomUserDetails(taskUser);

        String taskJson = """
                {
                    "title": "New Task",
                    "description": "Task description",
                    "dueDate": "%s",
                    "priority": "MEDIUM",
                    "status": "PENDING"
                }
                """.formatted(LocalDateTime.now().plusDays(3).toString());

        String response2 = mockMvc.perform(post("/api/tasks")
                .with(authentication(
                        new UsernamePasswordAuthenticationToken(principal, null,
                                principal.getAuthorities())))
                .contentType("application/json")
                .content(taskJson))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        Long taskId = ((Number) JsonPath.read(response2, "$.id")).longValue();
        Task createdTask = taskRepository.findById(taskId).get();

        mockMvc.perform(get("/api/tasks/{id}", taskId)
                .with(authentication(
                        new UsernamePasswordAuthenticationToken(principal, null,
                                principal.getAuthorities()))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(createdTask.getId()))
                .andExpect(jsonPath("$.title").value("New Task"))
                .andExpect(jsonPath("$.description").value("Task description"))
                .andExpect(jsonPath("$.dueDate").exists())
                .andExpect(jsonPath("$.priority").value("MEDIUM"))
                .andExpect(jsonPath("$.status").value("PENDING"));
    }

    @Test
    void getTaskById_NotFound() throws Exception {
        String userJson = """
                {
                    "username": "notfounduser",
                    "email": "notfounduser@example.com",
                    "password": "GoodPassword1!"
                }
                """;

        // Register user
        String response = mockMvc.perform(post("/api/users")
                .contentType("application/json")
                .content(userJson))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        String token = JsonPath.read(response, "$.token");

        // Verify user
        mockMvc.perform(get("/api/users/verify")
                .param("token", token))
                .andExpect(status().isOk());

        User taskUser = userRepository.findByUsername("notfounduser").get();
        CustomUserDetails principal = new CustomUserDetails(taskUser);

        mockMvc.perform(get("/api/tasks/{id}", 99999)
                .with(authentication(
                        new UsernamePasswordAuthenticationToken(principal, null,
                                principal.getAuthorities()))))
                .andExpect(status().isNotFound());
    }

    @Test
    void getTaskById_TaskNotOwned_Forbidden() throws Exception {
        String user1Json = """
                {
                    "username": "userone",
                    "email": "userone@example.com",
                    "password": "GoodPassword1!"
                }
                """;

        String user2Json = """
                {
                    "username": "usertwo",
                    "email": "usertwo@example.com",
                    "password": "GoodPassword1!"
                }
                """;

        // Register user 1
        String response1 = mockMvc.perform(post("/api/users")
                .contentType("application/json")
                .content(user1Json))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        String token = JsonPath.read(response1, "$.token");

        // Verify user 1
        mockMvc.perform(get("/api/users/verify")
                .param("token", token))
                .andExpect(status().isOk());

        // Register user 2
        String response2 = mockMvc.perform(post("/api/users")
                .contentType("application/json")
                .content(user2Json))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        String token2 = JsonPath.read(response2, "$.token");

        // Verify user 2
        mockMvc.perform(get("/api/users/verify")
                .param("token", token2))
                .andExpect(status().isOk());

        String taskJson = """
                {
                    "title": "User One Task",
                    "description": "Task description",
                    "dueDate": "%s",
                    "priority": "MEDIUM",
                    "status": "PENDING"
                }
                """.formatted(LocalDateTime.now().plusDays(3).toString());

        // User 1 creates a task

        User taskUser = userRepository.findByUsername("userone").get();
        CustomUserDetails principalA = new CustomUserDetails(taskUser);

        String taskResponse = mockMvc.perform(post("/api/tasks")
                .with(authentication(
                        new UsernamePasswordAuthenticationToken(principalA, null,
                                principalA.getAuthorities())))
                .contentType("application/json")
                .content(taskJson))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        Long taskId = ((Number) JsonPath.read(taskResponse, "$.id")).longValue();

        User taskUser2 = userRepository.findByUsername("usertwo").get();
        CustomUserDetails principalB = new CustomUserDetails(taskUser2);

        // User 2 tries to access User 1's task
        mockMvc.perform(get("/api/tasks/{id}", taskId)
                .with(authentication(
                        new UsernamePasswordAuthenticationToken(
                                principalB, null,
                                principalB.getAuthorities()))))
                .andExpect(status().isForbidden());
    }

    @Test
    void updateTask_Success() throws Exception {
        String userJson = """
                {
                    "username": "updateuser",
                    "email": "updateuser@example.com",
                    "password": "GoodPassword1!"
                }
                """;

        // Register the user
        String response = mockMvc.perform(post("/api/users")
                .contentType("application/json")
                .content(userJson))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        String token = JsonPath.read(response, "$.token");

        // Verify the user
        mockMvc.perform(get("/api/users/verify")
                .param("token", token))
                .andExpect(status().isOk());

        String taskJson = """
                {
                    "title": "Update User Task",
                    "description": "Task description",
                    "dueDate": "%s",
                    "priority": "MEDIUM",
                    "status": "PENDING"
                }
                """.formatted(LocalDateTime.now().plusDays(3).toString());

        // User creates a task
        User taskUser = userRepository.findByUsername("updateuser").get();
        CustomUserDetails principal = new CustomUserDetails(taskUser);

        String taskResponse = mockMvc.perform(post("/api/tasks")
                .with(authentication(
                        new UsernamePasswordAuthenticationToken(principal, null,
                                principal.getAuthorities())))
                .contentType("application/json")
                .content(taskJson))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        Long taskId = ((Number) JsonPath.read(taskResponse, "$.id")).longValue();

        // Update the task
        String updatedTaskJson = """
                {
                    "title": "Updated User Task",
                    "description": "Updated task description",
                    "dueDate": "%s",
                    "priority": "HIGH",
                    "status": "COMPLETE"
                }
                """.formatted(LocalDateTime.now().plusDays(5).toString());

        mockMvc.perform(put("/api/tasks/{id}", taskId)
                .with(authentication(
                        new UsernamePasswordAuthenticationToken(principal, null,
                                principal.getAuthorities())))
                .contentType("application/json")
                .content(updatedTaskJson))
                .andExpect(status().isOk());
    }

    @Test
    void updateTask_InvalidData_BadRequest() throws Exception {
        String userJson = """
                {
                    "username": "updateuser",
                    "email": "updateuser@example.com",
                    "password": "GoodPassword1!"
                }
                """;

        // Register the user
        String response = mockMvc.perform(post("/api/users")
                .contentType("application/json")
                .content(userJson))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        String token = JsonPath.read(response, "$.token");

        // Verify the user
        mockMvc.perform(get("/api/users/verify")
                .param("token", token))
                .andExpect(status().isOk());

        String taskJson = """
                {
                    "title": "Update User Task",
                    "description": "Task description",
                    "dueDate": "%s",
                    "priority": "MEDIUM",
                    "status": "PENDING"
                }
                """.formatted(LocalDateTime.now().plusDays(3).toString());

        // User creates a task
        User taskUser = userRepository.findByUsername("updateuser").get();
        CustomUserDetails principal = new CustomUserDetails(taskUser);

        String taskResponse = mockMvc.perform(post("/api/tasks")
                .with(authentication(
                        new UsernamePasswordAuthenticationToken(principal, null,
                                principal.getAuthorities())))
                .contentType("application/json")
                .content(taskJson))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        Long taskId = ((Number) JsonPath.read(taskResponse, "$.id")).longValue();

        // Update the task, invalid name (empty)
        String updatedTaskJson = """
                {
                    "title": "",
                    "description": "Updated task description",
                    "dueDate": "%s",
                    "priority": "HIGH",
                    "status": "COMPLETE"
                }
                """.formatted(LocalDateTime.now().plusDays(5).toString());

        mockMvc.perform(put("/api/tasks/{id}", taskId)
                .with(authentication(
                        new UsernamePasswordAuthenticationToken(principal, null,
                                principal.getAuthorities())))
                .contentType("application/json")
                .content(updatedTaskJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateTask_AnotherUser_Forbidden() throws Exception {
        String user1Json = """
                {
                    "username": "userone",
                    "email": "userone@example.com",
                    "password": "GoodPassword1!"
                }
                """;

        // Register the user
        String response = mockMvc.perform(post("/api/users")
                .contentType("application/json")
                .content(user1Json))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        String token = JsonPath.read(response, "$.token");

        // Verify the user
        mockMvc.perform(get("/api/users/verify")
                .param("token", token))
                .andExpect(status().isOk());

        String user2Json = """
                {
                    "username": "usertwo",
                    "email": "usertwo@example.com",
                    "password": "GoodPassword1!"
                }
                """;

        // Register the user
        response = mockMvc.perform(post("/api/users")
                .contentType("application/json")
                .content(user2Json))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        token = JsonPath.read(response, "$.token");

        // Verify the user
        mockMvc.perform(get("/api/users/verify")
                .param("token", token))
                .andExpect(status().isOk());

        String taskJson = """
                {
                    "title": "Update User Task",
                    "description": "Task description",
                    "dueDate": "%s",
                    "priority": "MEDIUM",
                    "status": "PENDING"
                }
                """.formatted(LocalDateTime.now().plusDays(3).toString());

        // User 1 creates a task
        User taskUser1 = userRepository.findByUsername("userone").get();
        CustomUserDetails principal1 = new CustomUserDetails(taskUser1);

        String taskResponse = mockMvc.perform(post("/api/tasks")
                .with(authentication(
                        new UsernamePasswordAuthenticationToken(principal1, null,
                                principal1.getAuthorities())))
                .contentType("application/json")
                .content(taskJson))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        Long taskId = ((Number) JsonPath.read(taskResponse, "$.id")).longValue();

        // User 2 tries to update the task
        User taskUser2 = userRepository.findByUsername("usertwo").get();
        CustomUserDetails principal2 = new CustomUserDetails(taskUser2);

        String updatedTaskJson = """
                {
                    "title": "Updated User Task",
                    "description": "Updated task description",
                    "dueDate": "%s",
                    "priority": "HIGH",
                    "status": "COMPLETE"
                }
                """.formatted(LocalDateTime.now().plusDays(5).toString());

        mockMvc.perform(put("/api/tasks/{id}", taskId)
                .with(authentication(
                        new UsernamePasswordAuthenticationToken(principal2, null,
                                principal2.getAuthorities())))
                .contentType("application/json")
                .content(updatedTaskJson))
                .andExpect(status().isForbidden());
    }

    @Test
    void updateTask_TaskNotFound_NotFound() throws Exception {
        String userJson = """
                        {
                            "username": "updateuser",
                            "email": "updateuser@example.com",
                            "password": "GoodPassword1!"
                }
                        """;
        // Register the user
        String response = mockMvc.perform(post("/api/users")
                .contentType("application/json")
                .content(userJson))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        String token = JsonPath.read(response, "$.token");

        // Verify the user
        mockMvc.perform(get("/api/users/verify")
                .param("token", token))
                .andExpect(status().isOk());

        // User updates a nonexistent task
        String updatedTaskJson = """
                {
                    "title": "Updated User Task",
                    "description": "Updated task description",
                    "dueDate": "%s",
                    "priority": "HIGH",
                    "status": "COMPLETE"
                }
                """.formatted(LocalDateTime.now().plusDays(5).toString());
        Long taskId = 99999L;

        User taskUser1 = userRepository.findByUsername("updateuser").get();
        CustomUserDetails principal = new CustomUserDetails(taskUser1);

        mockMvc.perform(put("/api/tasks/{id}", taskId)
                .with(authentication(
                        new UsernamePasswordAuthenticationToken(principal, null,
                                principal.getAuthorities())))
                .contentType("application/json")
                .content(updatedTaskJson))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteTask_Success() throws Exception {
        String userJson = """
                {
                    "username": "deleteuser",
                    "email": "deleteuser@example.com",
                    "password": "GoodPassword1!"
                }
                """;
        // Register the user
        String response = mockMvc.perform(post("/api/users")
                .contentType("application/json")
                .content(userJson))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        String token = JsonPath.read(response, "$.token");

        // Verify the user
        mockMvc.perform(get("/api/users/verify")
                .param("token", token))
                .andExpect(status().isOk());

        // User creates a task
        String taskJson = """
                {
                    "title": "User Task",
                    "description": "Task description",
                    "dueDate": "%s",
                    "priority": "MEDIUM",
                    "status": "IN_PROGRESS"
                }
                """.formatted(LocalDateTime.now().plusDays(5).toString());

        User taskUser1 = userRepository.findByUsername("deleteuser").get();
        CustomUserDetails principal = new CustomUserDetails(taskUser1);

        String taskResponse = mockMvc.perform(post("/api/tasks")
                .with(authentication(
                        new UsernamePasswordAuthenticationToken(principal, null,
                                principal.getAuthorities())))
                .contentType("application/json")
                .content(taskJson))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        Long taskId = ((Number) JsonPath.read(taskResponse, "$.id")).longValue();

        // User deletes the task
        mockMvc.perform(delete("/api/tasks/{id}", taskId)
                .with(authentication(
                        new UsernamePasswordAuthenticationToken(principal, null,
                                principal.getAuthorities()))))
                .andExpect(status().isNoContent());
    }

    @Test
    void deleteTask_AnotherUser_Forbidden() throws Exception {
        String user1Json = """
                {
                    "username": "userone",
                    "email": "userone@example.com",
                    "password": "GoodPassword1!"
                }
                """;
        // Register the user
        String response = mockMvc.perform(post("/api/users")
                .contentType("application/json")
                .content(user1Json))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        String token = JsonPath.read(response, "$.token");

        // Verify the user
        mockMvc.perform(get("/api/users/verify")
                .param("token", token))
                .andExpect(status().isOk());

        // User creates a task
        String taskJson = """
                {
                    "title": "User Task",
                    "description": "Task description",
                    "dueDate": "%s",
                    "priority": "MEDIUM",
                    "status": "IN_PROGRESS"
                }
                """.formatted(LocalDateTime.now().plusDays(5).toString());

        User taskUser1 = userRepository.findByUsername("userone").get();
        CustomUserDetails principal1 = new CustomUserDetails(taskUser1);

        String taskResponse = mockMvc.perform(post("/api/tasks")
                .with(authentication(
                        new UsernamePasswordAuthenticationToken(principal1, null,
                                principal1.getAuthorities())))
                .contentType("application/json")
                .content(taskJson))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        Long taskId = ((Number) JsonPath.read(taskResponse, "$.id")).longValue();

        String user2Json = """
                {
                    "username": "usertwo",
                    "email": "usertwo@example.com",
                    "password": "GoodPassword1!"
                }
                """;
        // Register user 2
        String response2 = mockMvc.perform(post("/api/users")
                .contentType("application/json")
                .content(user2Json))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        String token2 = JsonPath.read(response2, "$.token");

        // Verify the user
        mockMvc.perform(get("/api/users/verify")
                .param("token", token2))
                .andExpect(status().isOk());

        User taskUser2 = userRepository.findByUsername("usertwo").get();
        CustomUserDetails principal2 = new CustomUserDetails(taskUser2);

        // User 2 tries to delete user 1's task
        mockMvc.perform(delete("/api/tasks/{id}", taskId)
                .with(authentication(
                        new UsernamePasswordAuthenticationToken(principal2, null,
                                principal2.getAuthorities()))))
                .andExpect(status().isForbidden());
    }

    @Test
    void deleteTask_TaskNotFound_NotFound() throws Exception {
        String userJson = """
                {
                    "username": "deleteuser",
                    "email": "deleteuser@example.com",
                    "password": "GoodPassword1!"
                }""";
        // Register the user
        String response = mockMvc.perform(post("/api/users")
                .contentType("application/json")
                .content(userJson))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        String token = JsonPath.read(response, "$.token");

        // Verify the user
        mockMvc.perform(get("/api/users/verify")
                .param("token", token))
                .andExpect(status().isOk());

        // User deletes a nonexistent task
        Long taskId = 99999L;
        User taskUser1 = userRepository.findByUsername("deleteuser").get();
        CustomUserDetails principal = new CustomUserDetails(taskUser1);

        mockMvc.perform(delete("/api/tasks/{id}", taskId)
                .with(authentication(
                        new UsernamePasswordAuthenticationToken(principal, null,
                                principal.getAuthorities()))))
                .andExpect(status().isNotFound());
    }
}