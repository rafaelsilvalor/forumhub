package com.rafaellor.forumhub.util;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class PasswordEncoderUtil {

    public static void main(String[] args) {
        // The plaintext password you want to hash
        String plaintextPassword = "testuser";

        // Create a BCryptPasswordEncoder instance
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

        // Hash the plaintext password
        String hashedPassword = encoder.encode(plaintextPassword);

        // Print the hashed password to the console
        System.out.println("Plaintext Password: " + plaintextPassword);
        System.out.println("Hashed Password (for V2__create_users_table.sql): " + hashedPassword);
    }
}
