package com.example.admindashboard.controller;

import com.example.admindashboard.model.User;
import com.example.admindashboard.repository.UserRepository;
import com.example.admindashboard.service.ThanksService;

import jakarta.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.util.Optional;

@Controller
@RequestMapping("/my-thanks")
public class MyThanksController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ThanksService thanksService;

    /* ---------- SESSION CHECK ---------- */
    private boolean isThanksAuthenticated(HttpSession session) {
        return Boolean.TRUE.equals(session.getAttribute("thanksAuthenticated"));
    }

    private User getAuthenticatedUser(HttpSession session, Principal principal) {
        String username = (String) session.getAttribute("thanksUser");

        if (username == null && principal != null) {
            username = principal.getName();
        }

        if (username != null) {
            return userRepository.findByUsername(username).orElse(null);
        }

        return null;
    }

    /* ---------- LOGIN ---------- */
    @GetMapping("/login")
    public String showLogin() {
        return "mythanks/thanks-login";
    }

    @PostMapping("/login")
    public String login(
            @RequestParam String loginId,
            @RequestParam String password,
            HttpSession session,
            Model model
    ) {
        Optional<User> userOpt = userRepository.findByUsername(loginId);

        if (userOpt.isPresent()) {
            User user = userOpt.get();

            String dbPassword = user.getPassword().replace("{noop}", "");

            if (dbPassword.equals(password)) {
                session.setAttribute("thanksAuthenticated", true);
                session.setAttribute("thanksUser", loginId);
                return "redirect:/my-thanks/dashboard";
            }
        }

        model.addAttribute("error", "Invalid credentials");
        return "mythanks/thanks-login";
    }

    /* ---------- DASHBOARD ---------- */
    @GetMapping("/dashboard")
    public String dashboard(HttpSession session, Model model, Principal principal) {

        if (!isThanksAuthenticated(session)) {
            return "redirect:/my-thanks/login";
        }

        User user = getAuthenticatedUser(session, principal);

        model.addAttribute("user", user);
        model.addAttribute("wallet", thanksService.getOrCreateWallet(user));
        model.addAttribute("transactions", thanksService.getTransactionHistory(user));

        return "mythanks/thanks-dashboard";
    }

    /* ---------- SEND THANKS ---------- */
    @PostMapping("/send")
    public String sendThanks(
            @RequestParam String receiverUsername,
            @RequestParam Integer points,
            HttpSession session,
            Principal principal,
            RedirectAttributes redirectAttributes
    ) {

        User sender = getAuthenticatedUser(session, principal);
        Optional<User> receiver = userRepository.findByUsername(receiverUsername);

        if (sender != null && receiver.isPresent()) {
            try {
                thanksService.sendThanks(sender, receiver.get(), points, "APPRECIATION", "Great work!");
                redirectAttributes.addFlashAttribute("success", "Sent successfully");
            } catch (Exception e) {
                redirectAttributes.addFlashAttribute("error", e.getMessage());
            }
        }

        return "redirect:/my-thanks/dashboard";
    }

    /* ---------- LOGOUT ---------- */
    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/my-thanks/login";
    }
}
