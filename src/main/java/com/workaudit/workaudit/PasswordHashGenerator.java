package com.workaudit.workaudit;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class PasswordHashGenerator {
    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

        String password = "Admin@123";
        String hashedPassword = encoder.encode(password);

        System.out.println("BCrypt Hash:");
        System.out.println(hashedPassword);
    }
}