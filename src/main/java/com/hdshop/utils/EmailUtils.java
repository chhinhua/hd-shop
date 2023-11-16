package com.hdshop.utils;

public class EmailUtils {
    /**
     * Validates the email address
     * @param email
     * @return
     */
    public static boolean isValidEmail(String email) {
        return email.matches("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$");
    }
}
