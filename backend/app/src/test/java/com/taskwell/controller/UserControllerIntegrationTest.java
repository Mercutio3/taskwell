package com.taskwell.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.test.web.servlet.MockMvc;
import com.taskwell.model.User;

import com.taskwell.repository.UserRepository;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;

import com.jayway.jsonpath.JsonPath;
import com.taskwell.security.CustomUserDetails;

import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class UserControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Test
    void registerUser_ReturnsCreated() throws Exception {
        String userJson = """
                {
                    "username": "testuser",
                    "email": "testuser@example.com",
                    "password": "GoodPassword1!"
                }
                """;

        mockMvc.perform(post("/api/users")
                .contentType("application/json")
                .content(userJson))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.username").value("testuser"))
                .andExpect(jsonPath("$.email").value("testuser@example.com"))
                .andExpect(jsonPath("$.verified").value(false))
                .andExpect(jsonPath("$.locked").value(false))
                .andExpect(jsonPath("$.token").exists());
    }

    @Test
    void registerUser_UsernameTaken_ReturnsBadRequest() throws Exception {
        String firstUserJson = """
                {
                    "username": "duplicateuser",
                    "email": "firstuser@example.com",
                    "password": "GoodPassword1!"
                }
                """;
        String duplicateUserJson = """
                {
                    "username": "duplicateuser",
                    "email": "duplicateuser@example.com",
                    "password": "GoodPassword1!"
                }
                """;

        // First registration should succeed
        mockMvc.perform(post("/api/users")
                .contentType("application/json")
                .content(firstUserJson))
                .andExpect(status().isCreated());

        // Second registration with same username should fail
        mockMvc.perform(post("/api/users")
                .contentType("application/json")
                .content(duplicateUserJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    void registerUser_EmailTaken_ReturnsBadRequest() throws Exception {
        String firstUserJson = """
                {
                    "username": "userone",
                    "email": "userone@example.com",
                    "password": "GoodPassword1!"
                }
                """;
        String duplicateEmailJson = """
                {
                    "username": "usertwo",
                    "email". "userone@example.com",
                    "password": "GoodPassword2!"
                }
                """;

        // First registration should succeed
        mockMvc.perform(post("/api/users")
                .contentType("application/json")
                .content(firstUserJson))
                .andExpect(status().isCreated());

        // Second registration with same email should fail
        mockMvc.perform(post("/api/users")
                .contentType("application/json")
                .content(duplicateEmailJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    void registerUser_BadEmail_ReturnsBadRequest() throws Exception {
        String userJson = """
                {
                    "username": "bademailuser",
                    "email": "invalid-email-format",
                    "password": "GoodPassword1!"
                }
                """;

        mockMvc.perform(post("/api/users")
                .contentType("application/json")
                .content(userJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    void registerUser_WeakPassword_ReturnsBadRequest() throws Exception {
        String userJson = """
                {
                    "username": "weakpassworduser",
                    "email": "weakpassworduser@example.com",
                    "password": "weak"
                }
                """;

        mockMvc.perform(post("/api/users")
                .contentType("application/json")
                .content(userJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getUserById_ReturnsUser() throws Exception {
        String userJson = """
                {
                    "username": "getuserbyid",
                    "email": "getuserbyid@example.com",
                    "password": "GoodPassword1!"
                }
                """;

        // Register user and get ID
        String response = mockMvc.perform(post("/api/users")
                .contentType("application/json")
                .content(userJson))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        Long userId = ((Number) JsonPath.read(response, "$.id")).longValue();

        mockMvc.perform(get("/api/users/{id}", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(userId))
                .andExpect(jsonPath("$.username").value("getuserbyid"))
                .andExpect(jsonPath("$.email").value("getuserbyid@example.com"));
    }

    @Test
    void getUserByUsername_ReturnsUser() throws Exception {
        String userJson = """
                {
                    "username": "getuserbyusername",
                    "email": "getuserbyusername@example.com",
                    "password": "GoodPassword1!"
                }
                """;

        // Register user
        String response = mockMvc.perform(post("/api/users")
                .contentType("application/json")
                .content(userJson))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        String username = JsonPath.read(response, "$.username");

        mockMvc.perform(get("/api/users/username/{username}", username))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value(username))
                .andExpect(jsonPath("$.email").value("getuserbyusername@example.com"));
    }

    @Test
    void getUserByEmail_ReturnsUser() throws Exception {
        String userJson = """
                {
                    "username": "getuserbyemail",
                    "email": "getuserbyemail@example.com",
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

        // Authenticate as verified user
        String email = JsonPath.read(response, "$.email");

        User verifiedUser = userRepository.findByUsername("getuserbyemail").get();
        CustomUserDetails principal = new CustomUserDetails(verifiedUser);

        mockMvc.perform(get("/api/users/email/{email}", email)
                .with(authentication(
                        new UsernamePasswordAuthenticationToken(principal, null,
                                principal.getAuthorities()))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value(email))
                .andExpect(jsonPath("$.username").value("getuserbyemail"));
    }

    @Test
    void updateUser_ChangeUsername_ReturnsUpdatedUser() throws Exception {
        String userJson = """
                {
                    "username": "selfchangeuser",
                    "email": "selfchangeuser@example.com",
                    "password": "GoodPassword1!"
                }
                """;

        String adminUserJson = """
                {
                    "username": "adminuser",
                    "email": "adminuser@example.com",
                    "password": "GoodPassword1!",
                    "role": "ADMIN"
                }
                """;

        // Register user
        String response = mockMvc.perform(post("/api/users")
                .contentType("application/json")
                .content(userJson))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        // Verify user
        String token = JsonPath.read(response, "$.token");
        mockMvc.perform(get("/api/users/verify")
                .param("token", token))
                .andExpect(status().isOk());

        // Authenticate as verified user
        Long userId = ((Number) JsonPath.read(response, "$.id")).longValue();

        User verifiedUser = userRepository.findByUsername("selfchangeuser").get();
        CustomUserDetails principal = new CustomUserDetails(verifiedUser);

        // Register admin
        mockMvc.perform(post("/api/users")
                .contentType("application/json")
                .content(adminUserJson))
                .andExpect(status().isCreated());

        String selfUpdateJson = """
                {
                    "username": "updatedusername",
                    "currentPassword": "GoodPassword1!"
                }
                """;

        // Verified user updates their own username
        mockMvc.perform(put("/api/users/{id}/username", userId)
                .with(authentication(
                        new UsernamePasswordAuthenticationToken(principal, null,
                                principal.getAuthorities())))
                .contentType("application/json")
                .content(selfUpdateJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(userId))
                .andExpect(jsonPath("$.username").value("updatedusername"))
                .andExpect(jsonPath("$.email").value("selfchangeuser@example.com"));
    }

    @Test
    void updateUser_ChangeUsername_UsernameTaken_ReturnsBadRequest() throws Exception {
        String firstUserJson = """
                {
                    "username": "userone",
                    "email": "userone@example.com",
                    "password": "GoodPassword1!"
                }
                """;
        // Register first user
        mockMvc.perform(post("/api/users")
                .contentType("application/json")
                .content(firstUserJson))
                .andExpect(status().isCreated());

        String secondUserJson = """
                {
                    "username": "userone",
                    "email": "usertwo@example.com",
                    "password": "GoodPassword1!"
                }
                """;

        mockMvc.perform(post("/api/users")
                .contentType("application/json")
                .content(secondUserJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateUser_ChangeEmail_ReturnsUpdatedUser() throws Exception {
        String userJson = """
                {
                    "username": "firstuser",
                    "email": "firstemail@example.com",
                    "password": "GoodPassword1!"
                }
                """;
        // Register user
        String response = mockMvc.perform(post("/api/users")
                .contentType("application/json")
                .content(userJson))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        // Verify user
        String token = JsonPath.read(response, "$.token");
        mockMvc.perform(get("/api/users/verify")
                .param("token", token))
                .andExpect(status().isOk());

        // Authenticate as verified user
        Long userId = ((Number) JsonPath.read(response, "$.id")).longValue();

        User verifiedUser = userRepository.findByUsername("firstuser").get();
        CustomUserDetails principal = new CustomUserDetails(verifiedUser);

        String updateJson = """
                {
                    "email": "updatedemail@example.com",
                    "currentPassword": "GoodPassword1!"
                }
                """;

        mockMvc.perform(put("/api/users/{id}/email", userId)
                .with(authentication(
                        new UsernamePasswordAuthenticationToken(principal, null,
                                principal.getAuthorities())))
                .contentType("application/json")
                .content(updateJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(userId))
                .andExpect(jsonPath("$.username").value("firstuser"))
                .andExpect(jsonPath("$.email").value("updatedemail@example.com"));
    }

    @Test
    void updateUser_ChangeEmail_EmailTaken_ReturnsBadRequest() throws Exception {
        String firstUserJson = """
                {
                    "username": "userone",
                    "email": "userone@example.com",
                    "password": "GoodPassword1!"
                }
                """;
        // Register first user
        String response = mockMvc.perform(post("/api/users")
                .contentType("application/json")
                .content(firstUserJson))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        // Verify first user
        String token = JsonPath.read(response, "$.token");
        mockMvc.perform(get("/api/users/verify")
                .param("token", token))
                .andExpect(status().isOk());

        // Authenticate as verified user
        Long userId = ((Number) JsonPath.read(response, "$.id")).longValue();
        User verifiedUser = userRepository.findByUsername("userone").get();
        CustomUserDetails principal = new CustomUserDetails(verifiedUser);

        // Register second user
        String secondUserJson = """
                {
                    "username": "usertwo",
                    "email": "usertwo@example.com",
                    "password": "GoodPassword1!"
                }
                """;
        mockMvc.perform(post("/api/users")
                .contentType("application/json")
                .content(secondUserJson))
                .andExpect(status().isCreated());

        // User 1 attempts to change email to user 2's email

        String updateJson = """
                {
                    "email": "usertwo@example.com"
                }
                """;

        mockMvc.perform(put("/api/users/{id}/email", userId)
                .with(authentication(
                        new UsernamePasswordAuthenticationToken(principal, null,
                                principal.getAuthorities())))
                .contentType("application/json")
                .content(updateJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    void changePassword_ReturnsUpdatedUser() throws Exception {
        String userJson = """
                {
                    "username": "changepassworduser",
                    "email": "changepassworduser@example.com",
                    "password": "GoodPassword1!"
                }
                """;
        // Register user
        String response = mockMvc.perform(post("/api/users")
                .contentType("application/json")
                .content(userJson))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        // Verify user
        String token = JsonPath.read(response, "$.token");
        mockMvc.perform(get("/api/users/verify")
                .param("token", token))
                .andExpect(status().isOk());

        // Authenticate as verified user
        Long userId = ((Number) JsonPath.read(response, "$.id")).longValue();

        User verifiedUser = userRepository.findByUsername("changepassworduser").get();
        CustomUserDetails principal = new CustomUserDetails(verifiedUser);

        String passwordJson = """
                {
                    "password": "NewGoodPassword1!",
                    "currentPassword": "GoodPassword1!"
                }
                """;

        mockMvc.perform(put("/api/users/{id}/password", userId)
                .with(authentication(
                        new UsernamePasswordAuthenticationToken(principal, null,
                                principal.getAuthorities())))
                .contentType("application/json")
                .content(passwordJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(userId));
    }

    @Test
    void changePassword_WeakPassword_ReturnsBadRequest() throws Exception {
        String userJson = """
                {
                    "username": "weakpassworduser",
                    "email": "weakpassworduser@example.com",
                    "password": "GoodPassword1!"
                }
                """;

        // Register user
        String response = mockMvc.perform(post("/api/users")
                .contentType("application/json")
                .content(userJson))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        // Verify user
        String token = JsonPath.read(response, "$.token");
        mockMvc.perform(get("/api/users/verify")
                .param("token", token))
                .andExpect(status().isOk());

        // Authenticate as verified user
        Long userId = ((Number) JsonPath.read(response, "$.id")).longValue();

        User verifiedUser = userRepository.findByUsername("weakpassworduser").get();
        CustomUserDetails principal = new CustomUserDetails(verifiedUser);

        String weakPasswordJson = """
                {
                    "password": "weak"
                }
                """;

        mockMvc.perform(put("/api/users/{id}/password", userId)
                .with(authentication(
                        new UsernamePasswordAuthenticationToken(principal, null,
                                principal.getAuthorities())))
                .contentType("application/json")
                .content(weakPasswordJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    void changeRole_ReturnsUpdatedUser() throws Exception {
        String adminUserJson = """
                {
                    "username": "adminuser",
                    "email": "adminuser@example.com",
                    "password": "GoodPassword1!",
                    "role": "ADMIN"
                }
                """;

        String userJson = """
                {
                    "username": "rolechangeuser",
                    "email": "rolechangeuser@example.com",
                    "password": "GoodPassword1!",
                    "role": "USER"
                }
                """;

        // Register admin
        mockMvc.perform(post("/api/users")
                .contentType("application/json")
                .content(adminUserJson))
                .andExpect(status().isCreated());

        // Register target user
        String response = mockMvc.perform(post("/api/users")
                .contentType("application/json")
                .content(userJson))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        Long userId = ((Number) JsonPath.read(response, "$.id")).longValue();

        String roleJson = """
                {
                    "role": "ADMIN"
                }
                """;

        mockMvc.perform(put("/api/users/{id}/role", userId)
                .with(user("adminuser").roles("ADMIN"))
                .contentType("application/json")
                .content(roleJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(userId))
                .andExpect(jsonPath("$.role").value("ADMIN"));
    }

    @Test
    void changeRole_InvalidRole_ReturnsBadRequest() throws Exception {
        String adminUserJson = """
                {
                    "username": "adminuser",
                    "email": "adminuser@example.com",
                    "password": "GoodPassword1!",
                    "role": "ADMIN"
                }
                """;

        String userJson = """
                {
                    "username": "invalidroleuser",
                    "email": "invalidroleuser@example.com",
                    "password": "GoodPassword1!",
                    "role": "USER"
                }
                """;

        // Register admin
        mockMvc.perform(post("/api/users")
                .contentType("application/json")
                .content(adminUserJson))
                .andExpect(status().isCreated());

        // Register user
        String response = mockMvc.perform(post("/api/users")
                .contentType("application/json")
                .content(userJson))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        Long userId = ((Number) JsonPath.read(response, "$.id")).longValue();

        String roleJson = """
                {
                    "role": "INVALID_ROLE"
                }
                """;

        mockMvc.perform(put("/api/users/{id}/role", userId)
                .with(user("adminuser").roles("ADMIN"))
                .contentType("application/json")
                .content(roleJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    void changeRole_InvalidUserId_ReturnsNotFound() throws Exception {
        Long invalidUserId = 9999L;

        String adminUserJson = """
                {
                    "username": "adminuser",
                    "email": "adminuser@example.com",
                    "password": "GoodPassword1!",
                    "role": "ADMIN"
                }
                """;

        // Register admin
        mockMvc.perform(post("/api/users")
                .contentType("application/json")
                .content(adminUserJson))
                .andExpect(status().isCreated());

        String roleJson = """
                {
                    "role": "ADMIN"
                }
                """;

        mockMvc.perform(put("/api/users/{id}/role", invalidUserId)
                .with(user("adminuser").roles("ADMIN"))
                .contentType("application/json")
                .content(roleJson))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteUser_SelfDelete_ReturnsNoContent() throws Exception {
        String userJson = """
                {
                    "username": "selfdeleteuser",
                    "email": "selfdeleteuser@example.com",
                    "password": "GoodPassword1!",
                    "role": "USER"
                }
                """;

        // Register user
        String response = mockMvc.perform(post("/api/users")
                .contentType("application/json")
                .content(userJson))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        // Verify user
        String token = JsonPath.read(response, "$.token");
        mockMvc.perform(get("/api/users/verify")
                .param("token", token))
                .andExpect(status().isOk());

        // Authenticate as verified user
        Long userId = ((Number) JsonPath.read(response, "$.id")).longValue();
        User verifiedUser = userRepository.findByUsername("selfdeleteuser").get();
        CustomUserDetails principal = new CustomUserDetails(verifiedUser);

        mockMvc.perform(delete("/api/users/{id}", userId)
                .with(authentication(
                        new UsernamePasswordAuthenticationToken(principal, null,
                                principal.getAuthorities()))))
                .andExpect(status().isNoContent());

        // Verify subsequent get returns 404
        mockMvc.perform(get("/api/users/{id}", userId))
                .andExpect(status().isNotFound());
    }

    @Test
    void verifyUser_ReturnsOk() throws Exception {
        String userJson = """
                {
                    "username": "verifyme",
                    "email": "verifyme@example.com",
                    "password": "GoodPassword1!"
                }
                """;

        // Register user
        String response = mockMvc.perform(post("/api/users")
                .contentType("application/json")
                .content(userJson))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        // Verify user
        String token = JsonPath.read(response, "$.token");
        mockMvc.perform(get("/api/users/verify")
                .param("token", token))
                .andExpect(status().isOk());
        Long userId = ((Number) JsonPath.read(response, "$.id")).longValue();

        // Verify subsequent get shows user is verified
        mockMvc.perform(get("/api/users/{id}", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.verified").value(true));
    }

    @Test
    void toggleLock_ReturnsOk() throws Exception {
        String adminUserJson = """
                {
                    "username": "adminuser",
                    "email": "adminuser@example.com",
                    "password": "GoodPassword1!",
                    "role": "ADMIN"
                }
                """;

        String userJson = """
                {
                    "username": "lockme",
                    "email": "lockme@example.com",
                    "password": "GoodPassword1!"
                }
                """;

        // Register admin
        mockMvc.perform(post("/api/users")
                .contentType("application/json")
                .content(adminUserJson))
                .andExpect(status().isCreated());

        // Register user, verify unlocked by default
        String response = mockMvc.perform(post("/api/users")
                .contentType("application/json")
                .content(userJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.locked").value(false))
                .andReturn().getResponse().getContentAsString();

        Long userId = ((Number) JsonPath.read(response, "$.id")).longValue();

        // Lock user and verify
        mockMvc.perform(put("/api/users/{id}/toggle-lock", userId)
                .with(user("adminuser").roles("ADMIN")))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/users/{id}", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.locked").value(true));

        // Unlock again and verify
        mockMvc.perform(put("/api/users/{id}/toggle-lock", userId)
                .with(user("adminuser").roles("ADMIN")))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/users/{id}", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.locked").value(false));
    }
}