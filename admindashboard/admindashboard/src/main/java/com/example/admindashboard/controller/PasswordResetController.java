package com.example.admindashboard.controller;

import com.example.admindashboard.model.PasswordResetToken;
import com.example.admindashboard.model.User;
import com.example.admindashboard.repository.PasswordResetTokenRepository;
import com.example.admindashboard.repository.UserRepository;
import com.example.admindashboard.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Controller
public class PasswordResetController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordResetTokenRepository tokenRepository;

    @Autowired
    private EmailService emailService;

    // We process the forgot password form submitted from DashboardController's view
    @PostMapping("/api/forgot-password")
    public String processForgotPassword(@RequestParam("email") String email, RedirectAttributes redirectAttributes, jakarta.servlet.http.HttpServletRequest request) {
        
        Optional<User> optionalUser = userRepository.findAll().stream()
            .filter(u -> email.equalsIgnoreCase(u.getEmail()))
            .findFirst();

        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            
            // Delete any existing token for this user
            tokenRepository.findByUser(user).ifPresent(t -> tokenRepository.delete(t));

            // Generate new token
            String token = UUID.randomUUID().toString();
            PasswordResetToken resetToken = new PasswordResetToken(token, user, LocalDateTime.now().plusHours(1));
            tokenRepository.save(resetToken);

            // Create reset URL
            String appUrl = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort();
            String resetUrl = appUrl + "/reset-password?token=" + token;

            // Send Email
            emailService.sendPasswordResetEmail(user.getEmail(), resetUrl);
        }

        // Always show the same success message to prevent email enumeration attacks
        redirectAttributes.addFlashAttribute("success", "If your email is registered, a password reset link has been sent to " + email);
        return "redirect:/forgot-password";
    }

    @GetMapping("/reset-password")
    public String showResetPasswordPage(@RequestParam("token") String token, Model model, RedirectAttributes redirectAttributes) {
        Optional<PasswordResetToken> tokenOptional = tokenRepository.findByToken(token);

        if (tokenOptional.isEmpty() || tokenOptional.get().isExpired()) {
            redirectAttributes.addFlashAttribute("error", "The password reset link is invalid or has expired.");
            return "redirect:/forgot-password";
        }

        model.addAttribute("token", token);
        return "forgot-password-reset";
    }

    @PostMapping("/api/reset-password")
    public String processResetPassword(@RequestParam("token") String token, 
                                     @RequestParam("password") String password,
                                     RedirectAttributes redirectAttributes) {
        Optional<PasswordResetToken> tokenOptional = tokenRepository.findByToken(token);

        if (tokenOptional.isEmpty() || tokenOptional.get().isExpired()) {
            redirectAttributes.addFlashAttribute("error", "The password reset link is invalid or has expired.");
            return "redirect:/forgot-password";
        }

        User user = tokenOptional.get().getUser();
        // The project uses {noop} prefix for plaintext, or we could use BCrypt. Sticking with {noop} for now as per system behavior
        user.setPassword("{noop}" + password);
        userRepository.save(user);

        // Delete token after successful use
        tokenRepository.delete(tokenOptional.get());

        redirectAttributes.addFlashAttribute("success", "Your password has been successfully reset. You can now log in.");
        return "redirect:/login";
    }
}
