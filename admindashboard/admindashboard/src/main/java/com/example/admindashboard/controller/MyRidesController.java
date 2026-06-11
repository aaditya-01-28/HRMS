package com.example.admindashboard.controller;

import com.example.admindashboard.model.User;
import com.example.admindashboard.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import java.security.Principal;

@Controller
@RequestMapping("/my-rides")
public class MyRidesController {

    @Autowired
    private UserRepository userRepository;

    private void addUserToModel(Model model, Principal principal) {
        if (principal != null) {
            User currentUser = userRepository.findByUsername(principal.getName()).orElse(new User());
            model.addAttribute("user", currentUser);
        } else {
            model.addAttribute("user", new User());
        }
    }

    @GetMapping("/login")
    public String loginPage(Model model, Principal principal) {
        addUserToModel(model, principal);
        return "my-rides/login";
    }

    @PostMapping("/logout")
    public String logout(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }
        return "redirect:/my-rides/login";
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model, Principal principal) {
        addUserToModel(model, principal);
        return "my-rides/dashboard";
    }

    @GetMapping("/book-rides")
    public String bookRides(Model model, Principal principal) {
        addUserToModel(model, principal);
        return "my-rides/book-rides";
    }

    @GetMapping("/book-now")
    public String bookNow(Model model, Principal principal) {
        addUserToModel(model, principal);
        return "my-rides/book-now";
    }

    @GetMapping("/cancel-ride")
    public String cancelRide(Model model, Principal principal) {
        addUserToModel(model, principal);
        return "my-rides/cancel-ride";
    }

    @GetMapping("/helpdesk")
    public String helpdesk(Model model, Principal principal) {
        addUserToModel(model, principal);
        return "my-rides/helpdesk";
    }
}
