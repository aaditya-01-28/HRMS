package com.example.admindashboard.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import java.security.Principal;
import org.springframework.beans.factory.annotation.Autowired;
import com.example.admindashboard.repository.UserRepository;
import com.example.admindashboard.model.User;

@Controller
@RequestMapping("/learning")
public class LearningViewController {

    @Autowired
    private UserRepository userRepository;

    private void addUserToModel(Principal principal, Model model) {
        if (principal != null) {
            String loginId = principal.getName();
            User currentUser = userRepository.findByUsername(loginId).orElse(null);
            model.addAttribute("user", currentUser);
        }
    }

    @GetMapping("/login")
    public String login() {
        return "learning/login";
    }

    @PostMapping("/login")
    public String handleLogin() {
        // Mock authentication, redirects to dashboard
        return "redirect:/learning/dashboard";
    }

    @GetMapping("/dashboard")
    public String dashboard(Principal principal, Model model) {
        addUserToModel(principal, model);
        model.addAttribute("activeTab", "dashboard");
        return "learning/dashboard";
    }

    @GetMapping("/explore-courses")
    public String exploreCourses(Principal principal, Model model) {
        addUserToModel(principal, model);
        model.addAttribute("activeTab", "explore-courses");
        return "learning/explore-courses";
    }

    @GetMapping("/course-details")
    public String courseDetails(Principal principal, Model model) {
        addUserToModel(principal, model);
        model.addAttribute("activeTab", "explore-courses");
        return "learning/course-details";
    }

    @GetMapping("/leaderboard")
    public String leaderboard(Principal principal, Model model) {
        addUserToModel(principal, model);
        model.addAttribute("activeTab", "leaderboard");
        return "learning/leaderboard";
    }

    @GetMapping("/my-learnings")
    public String myLearnings(Principal principal, Model model) {
        addUserToModel(principal, model);
        model.addAttribute("activeTab", "my-learnings");
        return "learning/my-learnings";
    }

    @GetMapping("/live-training")
    public String liveTraining(Principal principal, Model model) {
        addUserToModel(principal, model);
        model.addAttribute("activeTab", "live-training");
        return "learning/live-training";
    }

    @GetMapping("/helpdesk")
    public String helpdesk(Principal principal, Model model) {
        addUserToModel(principal, model);
        model.addAttribute("activeTab", "helpdesk");
        return "learning/helpdesk";
    }
}
