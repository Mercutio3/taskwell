package com.taskwell.utils;

public class ValidationUtils {

    // Validation methods for User entity

    public static boolean isValidEmail(String email) {
        String emailRegex = "^(?=.{1,64}@)[A-Za-z0-9_-]+(\\\\.[A-Za-z0-9_-]+)*@\" \n" + //
                "        + \"[^-][A-Za-z0-9-]+(\\\\.[A-Za-z0-9-]+)*(\\\\.[A-Za-z]{2,})$";
        return email != null && email.matches(emailRegex);
    }

    public static boolean isValidUsername(String username) {
        return username != null && username.length() >= 3 && username.length() <= 50;
    }

    public static boolean isValidPassword(String password) {
        String passwordRegex = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]+$";
        if (password == null || !password.matches(passwordRegex)) {
            return false;
        }
        return password.length() >= 8 && password.length() <= 100;
    }

    // Vaidation methods for Task entity

    public static boolean isValidTaskName(String taskName) {
        return taskName != null && taskName.length() >= 1 && taskName.length() <= 100;
    }
}
