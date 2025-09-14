package com.taskwell.service;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import static org.mockito.Mockito.*;

import com.taskwell.repository.UserRepository;
import com.taskwell.utils.ValidationUtils;

import com.taskwell.model.User;
import com.taskwell.model.UserRole;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.junit.jupiter.api.Assertions.*;
import java.util.Optional;
import java.util.List;

import com.taskwell.security.CustomUserDetails;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    @Test
    void registerUser_Success() {
        User user = new User();
        user.setUsername("testuser");
        user.setEmail("testuser@example.com");
        user.setPassword("GoodPassword1!");

        User savedUser = new User();
        savedUser.setId(1L);
        savedUser.setUsername("testuser");
        savedUser.setEmail("testuser@example.com");
        savedUser.setPassword("GoodPassword1!");

        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        try (MockedStatic<ValidationUtils> mocked = mockStatic(ValidationUtils.class)) {
            mocked.when(() -> ValidationUtils.isValidUsername(anyString())).thenReturn(true);
            mocked.when(() -> ValidationUtils.isValidEmail(anyString())).thenReturn(true);
            mocked.when(() -> ValidationUtils.isValidPassword(anyString())).thenReturn(true);

            // Call the method under test
            User result = userService.registerUser(user);

            // Assertions
            assertNotNull(result.getId());
            assertEquals("testuser", result.getUsername());
            assertEquals("testuser@example.com", result.getEmail());

            // Verifications
            verify(userRepository).save(any(User.class));
            verify(userRepository, never()).delete(any(User.class));
        }
    }

    @Test
    void registerUser_Usernametaken_ThrowsException() {
        User user = new User();
        user.setUsername("existinguser");
        user.setEmail("existinguser@example.com");
        user.setPassword("GoodPassword1!");

        when(userRepository.findByUsername("existinguser")).thenReturn(Optional.of(user));

        try (MockedStatic<ValidationUtils> mocked = mockStatic(ValidationUtils.class)) {
            mocked.when(() -> ValidationUtils.isValidUsername(anyString())).thenReturn(true);
            mocked.when(() -> ValidationUtils.isValidEmail(anyString())).thenReturn(true);
            mocked.when(() -> ValidationUtils.isValidPassword(anyString())).thenReturn(true);

            // Call the method under test and assert that it throws an exception
            assertThrows(IllegalArgumentException.class, () -> userService.registerUser(user));

            // Verifications
            verify(userRepository, never()).save(any(User.class));
        }
    }

    @Test
    void registerUser_EmailTaken_ThrowsException() {
        User user = new User();
        user.setUsername("newuser");
        user.setEmail("newuser@example.com");
        user.setPassword("GoodPassword1!");

        when(userRepository.findByEmail("newuser@example.com")).thenReturn(Optional.of(user));

        try (MockedStatic<ValidationUtils> mocked = mockStatic(ValidationUtils.class)) {
            mocked.when(() -> ValidationUtils.isValidUsername(anyString())).thenReturn(true);
            mocked.when(() -> ValidationUtils.isValidEmail(anyString())).thenReturn(true);
            mocked.when(() -> ValidationUtils.isValidPassword(anyString())).thenReturn(true);

            // Call the method under test and assert that it throws an exception
            assertThrows(IllegalArgumentException.class, () -> userService.registerUser(user));

            // Verifications
            verify(userRepository, never()).save(any(User.class));
        }
    }

    @Test
    void registerUser_InvalidUsername_ThrowsException() {
        User user = new User();
        user.setUsername("invalid username"); // Invalid due to space
        user.setEmail("invalid@example.com");
        user.setPassword("GoodPassword1!");

        try (MockedStatic<ValidationUtils> mocked = mockStatic(ValidationUtils.class)) {
            mocked.when(() -> ValidationUtils.isValidUsername(anyString())).thenReturn(false);
            mocked.when(() -> ValidationUtils.isValidEmail(anyString())).thenReturn(true);
            mocked.when(() -> ValidationUtils.isValidPassword(anyString())).thenReturn(true);

            // Call the method under test and assert that it throws an exception
            assertThrows(IllegalArgumentException.class, () -> userService.registerUser(user));

            // Verifications
            verify(userRepository, never()).save(any(User.class));
        }
    }

    @Test
    void registerUser_InvalidEmail_ThrowsException() {
        User user = new User();
        user.setUsername("validusername");
        user.setEmail("invalid-email"); // Invalid email format
        user.setPassword("GoodPassword1!");

        try (MockedStatic<ValidationUtils> mocked = mockStatic(ValidationUtils.class)) {
            mocked.when(() -> ValidationUtils.isValidUsername(anyString())).thenReturn(true);
            mocked.when(() -> ValidationUtils.isValidEmail(anyString())).thenReturn(false);
            mocked.when(() -> ValidationUtils.isValidPassword(anyString())).thenReturn(true);

            // Call the method under test and assert that it throws an exception
            assertThrows(IllegalArgumentException.class, () -> userService.registerUser(user));

            // Verifications
            verify(userRepository, never()).save(any(User.class));
        }
    }

    @Test
    void registerUser_WeakPassword_ThrowsException() {
        User user = new User();
        user.setUsername("validusername");
        user.setEmail("valid@example.com");
        user.setPassword("weak"); // Invalid password; too short and has no number/special char

        try (MockedStatic<ValidationUtils> mocked = mockStatic(ValidationUtils.class)) {
            mocked.when(() -> ValidationUtils.isValidUsername(anyString())).thenReturn(true);
            mocked.when(() -> ValidationUtils.isValidEmail(anyString())).thenReturn(true);
            mocked.when(() -> ValidationUtils.isValidPassword(anyString())).thenReturn(false);

            // Call the method under test and assert that it throws an exception
            assertThrows(IllegalArgumentException.class, () -> userService.registerUser(user));

            // Verifications
            verify(userRepository, never()).save(any(User.class));
        }
    }

    @Test
    void verifyUser_Success() {
        String token = "test-token";
        User user = new User();
        user.setId(1L);
        user.setUsername("verifyme");
        user.setVerificationToken(token);
        user.setVerified(false);
        when(userRepository.findByVerificationToken(token)).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenReturn(user);

        boolean result = userService.verifyUser(token);

        assertTrue(result);
        assertTrue(user.isVerified());
        assertNull(user.getVerificationToken());
        verify(userRepository).save(user);
    }

    @Test
    void verifyUser_TokenNotFound_ReturnsFalse() {
        String token = "nonexistent-token";
        when(userRepository.findByVerificationToken(token)).thenReturn(Optional.empty());

        boolean result = userService.verifyUser(token);

        assertFalse(result);
    }

    @Test
    void getAllUsers_Success() {
        User user1 = new User();
        user1.setId(1L);
        user1.setUsername("user1");
        user1.setEmail("user1@example.com");

        User user2 = new User();
        user2.setId(2L);
        user2.setUsername("user2");
        user2.setEmail("user2@example.com");

        when(userRepository.findAll()).thenReturn(List.of(user1, user2));

        List<User> users = userService.getAllUsers();

        assertNotNull(users);
        assertEquals(2, users.size());
        assertEquals("user1", users.get(0).getUsername());
        assertEquals("user2", users.get(1).getUsername());
    }

    @Test
    void findByID_Success() {
        User user = new User();
        user.setId(1L);
        user.setUsername("testuser");
        user.setEmail("testuser@example.com");

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        User foundUser = userService.findByID(1L);

        assertNotNull(foundUser);
        assertEquals("testuser", foundUser.getUsername());
        assertEquals("testuser@example.com", foundUser.getEmail());
    }

    @Test
    void findByID_UserNotFound_ThrowsException() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(org.springframework.security.core.userdetails.UsernameNotFoundException.class,
                () -> userService.findByID(1L));
    }

    @Test
    void changeUsername_Success() {
        User user = new User();
        user.setId(1L);
        user.setUsername("oldusername");
        user.setRole(UserRole.USER);
        user.setVerified(true);
        user.setLocked(false);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.findByUsername("newusername")).thenReturn(Optional.empty());
        when(userRepository.save(any(User.class))).thenReturn(user);

        // Set up mock authentication context with CustomUserDetails
        CustomUserDetails principal = new CustomUserDetails(user);
        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                principal, null, principal.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(auth);

        try (MockedStatic<ValidationUtils> mocked = mockStatic(ValidationUtils.class)) {
            mocked.when(() -> ValidationUtils.isValidUsername(anyString())).thenReturn(true);

            User updatedUser = userService.changeUsername(user.getId(), "newusername");

            // Assertions
            assertNotNull(updatedUser);
            assertEquals("newusername", updatedUser.getUsername());

            // Verifications
            verify(userRepository).save(user);
        } finally {
            org.springframework.security.core.context.SecurityContextHolder.clearContext();
        }
    }

    @Test
    void changeUsername_UsernameTaken_ThrowsException() {
        User user = new User();
        user.setId(1L);
        user.setUsername("oldusername");
        user.setRole(UserRole.USER);
        user.setVerified(true);
        user.setLocked(false);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        User takenUser = new User();
        takenUser.setId(2L); // Set non-null ID to avoid NPE
        when(userRepository.findByUsername("newusername")).thenReturn(Optional.of(takenUser));

        // Set up mock authentication context with CustomUserDetails
        CustomUserDetails principal = new CustomUserDetails(user);
        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                principal, null, principal.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(auth);

        try (MockedStatic<ValidationUtils> mocked = mockStatic(ValidationUtils.class)) {
            mocked.when(() -> ValidationUtils.isValidUsername(anyString())).thenReturn(true);

            // Assertions
            Exception ex = assertThrows(IllegalArgumentException.class,
                    () -> userService.changeUsername(user.getId(), "newusername"));
            assertTrue(ex.getMessage() != null && !ex.getMessage().isEmpty());

            // Verifications
            verify(userRepository, never()).save(user);
        } finally {
            org.springframework.security.core.context.SecurityContextHolder.clearContext();
        }
    }

    @Test
    void changeUsername_InvalidUsername_ThrowsException() {
        User user = new User();
        user.setId(1L);
        user.setUsername("oldusername");
        user.setRole(UserRole.USER);
        user.setVerified(true);
        user.setLocked(false);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        // Set up mock authentication context with CustomUserDetails
        CustomUserDetails principal = new CustomUserDetails(user);
        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                principal, null, principal.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(auth);

        try (MockedStatic<ValidationUtils> mocked = mockStatic(ValidationUtils.class)) {
            mocked.when(() -> ValidationUtils.isValidUsername(anyString())).thenReturn(false);

            // Assertions
            Exception ex = assertThrows(IllegalArgumentException.class,
                    () -> userService.changeUsername(user.getId(), "invalid username"));
            assertTrue(ex.getMessage().contains("Invalid username"));

            // Verifications
            verify(userRepository, never()).save(user);
        } finally {
            org.springframework.security.core.context.SecurityContextHolder.clearContext();
        }
    }

    @Test
    void changeUsername_NullUsername_ThrowsException() {
        User user = new User();
        user.setId(1L);
        user.setUsername("oldusername");
        user.setVerified(true);
        user.setLocked(false);
        user.setRole(UserRole.USER);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        // Set up mock authentication context with CustomUserDetails
        CustomUserDetails principal = new CustomUserDetails(user);
        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                principal, null, principal.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(auth);

        try (MockedStatic<ValidationUtils> mocked = mockStatic(ValidationUtils.class)) {
            mocked.when(() -> ValidationUtils.isValidUsername(null)).thenReturn(false);

            // Assertions
            Exception ex = assertThrows(IllegalArgumentException.class,
                    () -> userService.changeUsername(user.getId(), null));
            assertTrue(ex.getMessage().contains("Invalid username"));

            // Verifications
            verify(userRepository, never()).save(user);
        } finally {
            org.springframework.security.core.context.SecurityContextHolder.clearContext();
        }
    }

    @Test
    void changeUsername_EmptyUsername_ThrowsException() {
        User user = new User();
        user.setId(1L);
        user.setUsername("oldusername");
        user.setRole(UserRole.USER);
        user.setVerified(true);
        user.setLocked(false);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        // Set up mock authentication context with CustomUserDetails
        CustomUserDetails principal = new CustomUserDetails(user);
        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                principal, null, principal.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(auth);

        try (MockedStatic<ValidationUtils> mocked = mockStatic(ValidationUtils.class)) {
            mocked.when(() -> ValidationUtils.isValidUsername("")).thenReturn(false);

            // Assertions
            Exception ex = assertThrows(IllegalArgumentException.class,
                    () -> userService.changeUsername(user.getId(), ""));
            assertTrue(ex.getMessage().contains("Invalid username"));

            // Verifications
            verify(userRepository, never()).save(user);
        } finally {
            org.springframework.security.core.context.SecurityContextHolder.clearContext();
        }
    }

    @Test
    void changeEmail_Success() {
        User user = new User();
        user.setId(1L);
        user.setEmail("oldemail@example.com");

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.findByEmail("newemail@example.com")).thenReturn(Optional.empty());
        when(userRepository.save(any(User.class))).thenReturn(user);

        try (MockedStatic<ValidationUtils> mocked = mockStatic(ValidationUtils.class)) {
            mocked.when(() -> ValidationUtils.isValidEmail(anyString())).thenReturn(true);

            User updatedUser = userService.changeEmail(user.getId(), "newemail@example.com");

            // Assertions
            assertNotNull(updatedUser);
            assertEquals("newemail@example.com", updatedUser.getEmail());

            // Verifications
            verify(userRepository).save(user);
        }
    }

    @Test
    void changeEmail_EmailTaken_ThrowsException() {
        User user = new User();
        user.setId(1L);
        user.setEmail("oldemail@example.com");

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.findByEmail("newemail@example.com")).thenReturn(Optional.of(new User()));

        try (MockedStatic<ValidationUtils> mocked = mockStatic(ValidationUtils.class)) {
            mocked.when(() -> ValidationUtils.isValidEmail(anyString())).thenReturn(true);

            // Assertions
            assertThrows(IllegalArgumentException.class,
                    () -> userService.changeEmail(user.getId(), "newemail@example.com"));

            // Verifications
            verify(userRepository, never()).save(user);
        }
    }

    @Test
    void changeEmail_InvalidEmail_ThrowsException() {
        User user = new User();
        user.setId(1L);
        user.setEmail("oldemail@example.com");

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        try (MockedStatic<ValidationUtils> mocked = mockStatic(ValidationUtils.class)) {
            mocked.when(() -> ValidationUtils.isValidEmail(anyString())).thenReturn(false);

            // Assertions
            Exception ex = assertThrows(IllegalArgumentException.class,
                    () -> userService.changeEmail(user.getId(), "invalid-email"));
            assertTrue(ex.getMessage().contains("Invalid email format"));

            // Verifications
            verify(userRepository, never()).save(user);
        }
    }

    @Test
    void changeEmail_NullEmail_ThrowsException() {
        User user = new User();
        user.setId(1L);
        user.setEmail("oldemail@example.com");

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        try (MockedStatic<ValidationUtils> mocked = mockStatic(ValidationUtils.class)) {
            mocked.when(() -> ValidationUtils.isValidEmail(null)).thenReturn(false);

            // Assertions
            Exception ex = assertThrows(IllegalArgumentException.class,
                    () -> userService.changeEmail(user.getId(), null));
            assertTrue(ex.getMessage().contains("Invalid email format"));

            // Verifications
            verify(userRepository, never()).save(user);
        }
    }

    @Test
    void changeEmail_EmptyEmail_ThrowsException() {
        User user = new User();
        user.setId(1L);
        user.setEmail("oldemail@example.com");

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        try (MockedStatic<ValidationUtils> mocked = mockStatic(ValidationUtils.class)) {
            mocked.when(() -> ValidationUtils.isValidEmail("")).thenReturn(false);

            // Assertions
            Exception ex = assertThrows(IllegalArgumentException.class,
                    () -> userService.changeEmail(user.getId(), ""));
            assertTrue(ex.getMessage().contains("Invalid email format"));

            // Verifications
            verify(userRepository, never()).save(user);
        }
    }

    @Test
    void changeRole_Success() {
        User user = new User();
        user.setId(1L);
        user.setRole(UserRole.USER);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenReturn(user);

        User updatedUser = userService.changeRole(user.getId(), UserRole.ADMIN);

        assertNotNull(updatedUser);
        assertEquals(UserRole.ADMIN, updatedUser.getRole());
        verify(userRepository).save(user);
    }

    @Test
    void changeRole_UserNotFound_ThrowsException() {
        Long fakeId = 1L;

        when(userRepository.findById(fakeId)).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class, () -> userService.changeRole(fakeId, UserRole.ADMIN));

        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void deleteUser_Success() {
        User user = new User();
        user.setId(1L);
        user.setUsername("testuser");
        user.setRole(UserRole.USER);
        user.setVerified(true);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        doNothing().when(userRepository).delete(user);

        // Set up mock authentication context as the same user (self-delete)
        CustomUserDetails principal = new CustomUserDetails(user);
        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                principal, null, principal.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(auth);

        try {
            boolean result = userService.deleteUser(1L);
            assertTrue(result);
            verify(userRepository).delete(user);
        } finally {
            SecurityContextHolder.clearContext();
        }
    }

    @Test
    void deleteUser_UserNotFound_ThrowsException() {
        Long fakeId = 1L;

        when(userRepository.findById(fakeId)).thenReturn(Optional.empty());

        // Set up mock authentication context as a verified user
        User user = new User();
        user.setId(fakeId);
        user.setUsername("testuser");
        user.setRole(UserRole.USER);
        user.setVerified(true);
        CustomUserDetails principal = new CustomUserDetails(user);
        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                principal, null, principal.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(auth);

        try {
            assertThrows(UsernameNotFoundException.class, () -> userService.deleteUser(fakeId));
            verify(userRepository, never()).delete(any(User.class));
        } finally {
            SecurityContextHolder.clearContext();
        }
    }

    @Test
    void toggleUserLocked_Success() {
        User user = new User();
        user.setId(1L);
        user.setLocked(false);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenReturn(user);

        userService.toggleUserLocked(1L);

        assertTrue(user.isLocked());
        verify(userRepository).save(user);
    }

    @Test
    void toggleUserLocked_UserNotFound_ThrowsException() {
        Long fakeId = 1L;

        when(userRepository.findById(fakeId)).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class, () -> userService.toggleUserLocked(fakeId));

        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void changePassword_Success() {
        User user = new User();
        user.setId(1L);
        user.setPassword("OldPassword1!");

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenReturn(user);

        try (MockedStatic<ValidationUtils> mocked = mockStatic(ValidationUtils.class)) {
            mocked.when(() -> ValidationUtils.isValidPassword("NewPassword1!")).thenReturn(true);

            User updatedUser = userService.changePassword(user.getId(), "NewPassword1!");

            // Assertions
            assertNotNull(updatedUser);
            BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
            assertTrue(encoder.matches("NewPassword1!", updatedUser.getPassword()));

            // Verifications
            verify(userRepository).save(user);
        }
    }

    @Test
    void changePassword_WeakPassword_ThrowsException() {
        User user = new User();
        user.setId(1L);
        user.setPassword("OldPassword1!");

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        try (MockedStatic<ValidationUtils> mocked = mockStatic(ValidationUtils.class)) {
            mocked.when(() -> ValidationUtils.isValidPassword("badpassword")).thenReturn(false);

            Exception ex = assertThrows(IllegalArgumentException.class,
                    () -> userService.changePassword(user.getId(), "badpassword"));
            assertTrue(ex.getMessage() != null && !ex.getMessage().isEmpty());
            // Verifications
            verify(userRepository, never()).save(user);
        }
    }

    void changePassword_NullPassword_ThrowsException() {
        User user = new User();
        user.setId(1L);
        user.setPassword("OldPassword1!");

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        try (MockedStatic<ValidationUtils> mocked = mockStatic(ValidationUtils.class)) {
            mocked.when(() -> ValidationUtils.isValidPassword(null)).thenReturn(false);

            Exception ex = assertThrows(IllegalArgumentException.class,
                    () -> userService.changePassword(user.getId(), null));
            assertTrue(ex.getMessage().contains("Password does not meet strength requirements"));
            // Verifications
            verify(userRepository, never()).save(user);
        }
    }

    @Test
    void changePassword_EmptyPassword_ThrowsException() {
        User user = new User();
        user.setId(1L);
        user.setPassword("OldPassword1!");

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        try (MockedStatic<ValidationUtils> mocked = mockStatic(ValidationUtils.class)) {
            mocked.when(() -> ValidationUtils.isValidPassword("")).thenReturn(false);

            Exception ex = assertThrows(IllegalArgumentException.class,
                    () -> userService.changePassword(user.getId(), ""));
            assertTrue(ex.getMessage() != null && !ex.getMessage().isEmpty());
            // Verifications
            verify(userRepository, never()).save(user);
        }
    }
}