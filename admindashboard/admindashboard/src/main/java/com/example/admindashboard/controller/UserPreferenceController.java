package com.example.admindashboard.controller;

import com.example.admindashboard.model.User;
import com.example.admindashboard.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/user/preferences")
public class UserPreferenceController {

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/dashboard")
    public ResponseEntity<?> getDashboardPreferences(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(401).body(Map.of("error", "Unauthorized"));
        }

        String username = authentication.getName();
        User user = userRepository.findByUsername(username.toUpperCase()).orElse(null);

        if (user == null) {
            return ResponseEntity.status(404).body(Map.of("error", "User not found"));
        }

        return ResponseEntity.ok(Map.of(
            "pinnedServices", user.getPinnedServices() != null ? user.getPinnedServices() : "",
            "pinnedApplications", user.getPinnedApplications() != null ? user.getPinnedApplications() : ""
        ));
    }

    @PostMapping("/dashboard")
    public ResponseEntity<?> saveDashboardPreferences(
            @RequestBody Map<String, String> payload,
            Authentication authentication) {
        
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(401).body("Unauthorized");
        }

        String username = authentication.getName();
        User user = userRepository.findByUsername(username.toUpperCase()).orElse(null);

        if (user == null) {
            return ResponseEntity.status(404).body("User not found");
        }

        if (payload.containsKey("pinnedServices")) {
            user.setPinnedServices(payload.get("pinnedServices"));
        }
        if (payload.containsKey("pinnedApplications")) {
            user.setPinnedApplications(payload.get("pinnedApplications"));
        }

        userRepository.save(user);
        return ResponseEntity.ok(Map.of("success", true, "message", "Preferences saved successfully"));
    }
}
