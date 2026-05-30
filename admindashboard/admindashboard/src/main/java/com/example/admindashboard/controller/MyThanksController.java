package com.example.admindashboard.controller;

import com.example.admindashboard.model.User;
import com.example.admindashboard.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.security.Principal;

@Controller
@RequestMapping("/my-thanks")
public class MyThanksController {

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/login")
    public String showLogin(Model model, Principal principal) {
        if (principal != null) {
            String loginId = principal.getName();
            User currentUser = userRepository.findByUsername(loginId).orElse(new User());
            model.addAttribute("user", currentUser);
            model.addAttribute("savedPassword", currentUser.getPassword());
        } else {
            model.addAttribute("user", new User());
            model.addAttribute("savedPassword", "");
        }
        return "my-thanks/login";
    }

    @PostMapping("/authenticate")
    public String processAuthentication(Model model, Principal principal) {
        if (principal != null) {
            String loginId = principal.getName();
            User currentUser = userRepository.findByUsername(loginId).orElse(new User());
            model.addAttribute("user", currentUser);
        }
        // Assuming success directly lands to dashboard for now
        return "redirect:/my-thanks/dashboard";
    }

    @GetMapping("/dashboard")
    public String showDashboard(Model model, Principal principal) {
        addUserToModel(model, principal);
        return "my-thanks/dashboard";
    }

    @GetMapping("/store")
    public String showRedemptionStore(Model model, Principal principal) {
        addUserToModel(model, principal);
        return "my-thanks/store";
    }

    @GetMapping("/history")
    public String showTransactionHistory(Model model, Principal principal) {
        addUserToModel(model, principal);
        return "my-thanks/history";
    }

    @GetMapping("/send")
    public String showSendThanks(Model model, Principal principal) {
        addUserToModel(model, principal);
        return "my-thanks/send";
    }

    @GetMapping("/faqs")
    public String showFaqs(Model model, Principal principal) {
        addUserToModel(model, principal);
        return "my-thanks/faqs";
    }

    private void addUserToModel(Model model, Principal principal) {
        if (principal != null) {
            String loginId = principal.getName();
            User currentUser = userRepository.findByUsername(loginId).orElse(new User());
            model.addAttribute("user", currentUser);
        } else {
            model.addAttribute("user", new User());
        }
    }
}
