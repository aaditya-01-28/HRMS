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
        return "my-thanks/login";
    }

    /* ---------- DASHBOARD ---------- */
    @GetMapping("/dashboard")
    public String dashboard(HttpSession session, Model model, Principal principal) {
        if (principal == null) {
            return "redirect:/my-thanks/login";
        }

        User user = getAuthenticatedUser(session, principal);

        model.addAttribute("user", user);
        model.addAttribute("wallet", thanksService.getOrCreateWallet(user));
        model.addAttribute("transactions", thanksService.getTransactionHistory(user));

        // Let layout know which menu is active
        model.addAttribute("activeMenu", "dashboard");

        return "my-thanks/dashboard";
    }

    /* ---------- STORE ---------- */
    @GetMapping("/store")
    public String store(HttpSession session, Model model, Principal principal) {
        if (principal == null) {
            return "redirect:/my-thanks/login";
        }
        User user = getAuthenticatedUser(session, principal);
        model.addAttribute("user", user);
        model.addAttribute("wallet", thanksService.getOrCreateWallet(user));
        model.addAttribute("activeMenu", "store");
        return "my-thanks/store";
    }

    /* ---------- SEND THANKS ---------- */
    @GetMapping("/send")
    public String sendThanksView(HttpSession session, Model model, Principal principal) {
        if (principal == null) {
            return "redirect:/my-thanks/login";
        }
        User user = getAuthenticatedUser(session, principal);
        model.addAttribute("user", user);
        model.addAttribute("wallet", thanksService.getOrCreateWallet(user));
        model.addAttribute("activeMenu", "send");
        model.addAttribute("users", userRepository.findAll()); // for the dropdown
        return "my-thanks/send";
    }

    @PostMapping("/send")
    public String sendThanks(
            @RequestParam String receiverUsername,
            @RequestParam Integer points,
            @RequestParam(required = false) String message,
            HttpSession session,
            Principal principal,
            RedirectAttributes redirectAttributes
    ) {

        User sender = getAuthenticatedUser(session, principal);
        Optional<User> receiver = userRepository.findByUsername(receiverUsername);

        if (sender != null && receiver.isPresent()) {
            try {
                thanksService.sendThanks(sender, receiver.get(), points, "APPRECIATION", message != null ? message : "Great work!");
                redirectAttributes.addFlashAttribute("success", "Sent successfully");
            } catch (Exception e) {
                redirectAttributes.addFlashAttribute("error", e.getMessage());
            }
        }

        return "redirect:/my-thanks/dashboard";
    }

    /* ---------- HISTORY ---------- */
    @GetMapping("/history")
    public String history(HttpSession session, Model model, Principal principal) {
        if (principal == null) {
            return "redirect:/my-thanks/login";
        }
        User user = getAuthenticatedUser(session, principal);
        model.addAttribute("user", user);
        model.addAttribute("wallet", thanksService.getOrCreateWallet(user));
        model.addAttribute("transactions", thanksService.getTransactionHistory(user));
        model.addAttribute("activeMenu", "history");
        return "my-thanks/history";
    }

    /* ---------- FAQS ---------- */
    @GetMapping("/faqs")
    public String faqs(HttpSession session, Model model, Principal principal) {
        if (principal == null) {
            return "redirect:/my-thanks/login";
        }
        User user = getAuthenticatedUser(session, principal);
        model.addAttribute("user", user);
        model.addAttribute("wallet", thanksService.getOrCreateWallet(user));
        model.addAttribute("activeMenu", "faqs");
        return "my-thanks/faqs";
    }

    /* ---------- PROFILE ---------- */
    @GetMapping("/profile")
    public String viewProfile(HttpSession session, Model model, Principal principal) {
        if (principal == null) {
            return "redirect:/my-thanks/login";
        }
        User user = getAuthenticatedUser(session, principal);
        model.addAttribute("user", user);
        model.addAttribute("wallet", thanksService.getOrCreateWallet(user));
        model.addAttribute("activeMenu", "dashboard"); // keep dashboard menu active
        return "my-thanks/profile";
    }

    @PostMapping("/profile/address")
    public String updateDeliveryAddress(
            @RequestParam String addressType,
            @RequestParam String address,
            @RequestParam String pincode,
            HttpSession session,
            Principal principal,
            RedirectAttributes redirectAttributes
    ) {
        if (principal == null) {
            return "redirect:/my-thanks/login";
        }
        User user = getAuthenticatedUser(session, principal);
        if (user != null && user.getEmployeeProfile() != null) {
            user.getEmployeeProfile().setDeliveryAddressType(addressType);
            user.getEmployeeProfile().setDeliveryAddress(address);
            user.getEmployeeProfile().setDeliveryPincode(pincode);
            userRepository.save(user);
            redirectAttributes.addFlashAttribute("success", "Delivery Address Updated Successfully");
        } else {
            redirectAttributes.addFlashAttribute("error", "Unable to update profile. User or profile not found.");
        }
        return "redirect:/my-thanks/profile";
    }

    /* ---------- LOGOUT ---------- */
    @GetMapping("/logout-thanks")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/my-thanks/login";
    }
}
