package com.example.admindashboard.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/learning")
public class LearningRestController {

    @GetMapping("/dashboard")
    public Map<String, Object> getDashboardData() {
        Map<String, Object> data = new HashMap<>();
        
        data.put("upcomingEvents", Arrays.asList(
            Map.of("title", "Companies Policy", "date", "Wed, 10 June, 10:00 AM", "type", "LIVE"),
            Map.of("title", "Security Awareness", "date", "Wed, 10 June, 10:00 AM", "type", "UPCOMING"),
            Map.of("title", "Public Speaking", "date", "Wed, 10 June, 10:00 AM", "type", "UPCOMING")
        ));

        data.put("recommended", Arrays.asList(
            "Advance Java for Backend",
            "System Design",
            "REST APIs best practices",
            "Core Java OOPS"
        ));

        data.put("continueLearning", Arrays.asList(
            Map.of("courseName", "Spring Boot", "progress", 85),
            Map.of("courseName", "React Native", "progress", 45)
        ));

        data.put("upcomingTraining", Arrays.asList(
            Map.of("title", "Git Workshop", "date", "5 June, 10:00 AM"),
            Map.of("title", "Security Workshop", "date", "5 June, 10:00 AM")
        ));

        data.put("exploreCourses", getExploreCourses().subList(0, 3));
        
        data.put("leaderboard", getLeaderboardData().subList(0, 3));

        return data;
    }

    @GetMapping("/courses")
    public List<Map<String, Object>> getCourses() {
        return getExploreCourses();
    }

    @GetMapping("/courses/1") // Mock for details
    public Map<String, Object> getCourseDetails() {
        Map<String, Object> data = new HashMap<>();
        data.put("id", 1);
        data.put("title", "Spring Boot Mastery");
        data.put("author", "Abhinav Pandey");
        data.put("rating", "4.5 (10k+)");
        data.put("duration", "10h 50m");
        data.put("level", "Beginner to Advanced");
        data.put("category", "Development");
        data.put("description", "Learn to build scalable enterprise applications using Spring Boot.");
        data.put("modules", Arrays.asList(
            "Introduction",
            "Rest APIs",
            "Security",
            "Microservices",
            "Deployment"
        ));
        return data;
    }

    @GetMapping("/leaderboard")
    public List<Map<String, Object>> getLeaderboard() {
        return getLeaderboardData();
    }

    @GetMapping("/my-learnings")
    public Map<String, Object> getMyLearnings() {
        Map<String, Object> data = new HashMap<>();
        
        data.put("ongoing", Arrays.asList(
            Map.of("title", "Spring Boot Mastery", "level", "Intermediate", "author", "Khan Sir", "progress", 75, "completedModules", 15, "totalModules", 20, "tags", "Trending"),
            Map.of("title", "Docker Zero to Hero", "level", "Basics", "author", "Khan Sir", "progress", 45, "completedModules", 9, "totalModules", 20, "tags", "New Launch")
        ));
        
        data.put("completed", Arrays.asList(
            Map.of("title", "Spring Boot Mastery", "completionDate", "20th May"),
            Map.of("title", "React Native Basics", "completionDate", "20th May"),
            Map.of("title", "Electron Framework", "completionDate", "20th May")
        ));

        return data;
    }

    private List<Map<String, Object>> getExploreCourses() {
        return Arrays.asList(
            Map.of("id", 1, "title", "Spring Boot Mastery", "author", "Abhinav Pandey", "rating", "4.5 (10k+)", "duration", "10h 50m", "level", "Beginner to Advanced", "icon", "spring"),
            Map.of("id", 2, "title", "React Native Mastery", "author", "Chai With Code", "rating", "4.5 (10k+)", "duration", "10h 50m", "level", "Beginner to Advanced", "icon", "react"),
            Map.of("id", 3, "title", "Electron Framework", "author", "Code Babbar", "rating", "4.5 (10k+)", "duration", "10h 50m", "level", "Beginner to Advanced", "icon", "electron"),
            Map.of("id", 4, "title", "CI/CD Pipeline Training", "author", "DevOps Pro", "rating", "4.4 (30+)", "duration", "5h 20m", "level", "Intermediate", "icon", "cicd"),
            Map.of("id", 5, "title", "Kubernetes Basics", "author", "Cloud Guru", "rating", "4.5 (100+)", "duration", "8h 15m", "level", "Beginner to Advanced", "icon", "k8s")
        );
    }

    private List<Map<String, Object>> getLeaderboardData() {
        return Arrays.asList(
            Map.of("rank", 1, "name", "Rohit Sharma", "points", 120, "isCurrentUser", true),
            Map.of("rank", 2, "name", "Neha Sharma", "points", 110, "isCurrentUser", false),
            Map.of("rank", 3, "name", "Amit Sharma", "points", 105, "isCurrentUser", false),
            Map.of("rank", 4, "name", "Virat Kohli", "points", 100, "isCurrentUser", false),
            Map.of("rank", 5, "name", "Sachin Tendulkar", "points", 90, "isCurrentUser", false)
        );
    }
}
