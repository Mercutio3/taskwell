package com.taskwell.utils;

public class ValidationUtils {

    // Validation methods for User entity

    public static boolean isValidEmail(String email) {
        // Format example@domain.something
        String emailRegex = "^(?=.{1,64}@)[A-Za-z0-9._%+-]+@[A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)*(\\.[A-Za-z]{2,})$";
        return email != null && email.matches(emailRegex);
    }

    public static boolean isValidUsername(String username) {
        // Allow alphanumeric characters, dots, and underscores; no consecutive
        // dots/underscores
        String userRegex = "^(?!.*([_.])\\1)[a-zA-Z0-9._]+$";
        if (username == null || !username.matches(userRegex)) {
            return false;
        }
        return username.length() >= 3 && username.length() <= 50;
    }

    public static boolean isValidPassword(String password) {
        // At least one uppercase, one lowercase, one digit, one special character
        String passwordRegex = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]+$";
        System.out.println("VALIDATING PASSWORD: " + password);
        if (password == null || !password.matches(passwordRegex)) {
            return false;
        }
        return password.length() >= 8 && password.length() <= 100;
    }

    // Validation methods for Task entity

    public static boolean isValidTaskName(String taskName) {
        return taskName != null && taskName.length() >= 1 && taskName.length() <= 100;
    }

    public static boolean isValidDueDate(java.time.LocalDateTime dueDate) {
        if (dueDate == null)
            return false;
        java.time.LocalDateTime now = java.time.LocalDateTime.now();
        return !dueDate.isBefore(now.toLocalDate().atStartOfDay());
    }
}
