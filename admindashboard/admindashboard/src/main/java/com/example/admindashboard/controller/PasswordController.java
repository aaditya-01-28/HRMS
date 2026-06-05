package com.example.admindashboard.controller;

import com.example.admindashboard.model.User;
import com.example.admindashboard.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.security.Principal;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/password")
public class PasswordController {

    @Autowired
    private UserRepository userRepository;

    @PostMapping("/update")
    public ResponseEntity<?> updatePassword(@RequestBody Map<String, String> payload, Principal principal) {
        // Prevent unauthorized access
        if (principal == null) {
            return ResponseEntity.status(401).body("Not authenticated");
        }

        String currentPassword = payload.get("currentPassword");
        String newPassword = payload.get("newPassword");

        if (newPassword == null || newPassword.trim().isEmpty() || newPassword.contains(" ")) {
            return ResponseEntity.badRequest().body("Password cannot be empty or contain spaces.");
        }
        if (newPassword.length() < 6) {
            return ResponseEntity.badRequest().body("Password must be at least 6 characters long.");
        }

        Optional<User> userOpt = userRepository.findByUsername(principal.getName());

        if (userOpt.isPresent()) {
            User user = userOpt.get();
            String dbPassword = user.getPassword();

            // Safely strip the {noop} prefix from the database password to compare it
            String actualDbPassword = dbPassword != null ? dbPassword.replace("{noop}", "") : "";

            if (!actualDbPassword.equals(currentPassword)) {
                return ResponseEntity.badRequest().body("The current password you entered is incorrect.");
            }

            if (actualDbPassword.equals(newPassword)) {
                return ResponseEntity.badRequest().body("New password cannot be the same as your current password.");
            }

            // Save the new password with the {noop} prefix so Spring Security still accepts it!
            user.setPassword("{noop}" + newPassword);
            userRepository.save(user);

            return ResponseEntity.ok("Password updated successfully.");
        }

        return ResponseEntity.badRequest().body("User not found.");
    }
}