package com.example.admindashboard.controller;

import com.example.admindashboard.model.User;
import com.example.admindashboard.repository.UserRepository;
import com.example.admindashboard.repository.HrmsNotificationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;
import java.security.Principal;

@ControllerAdvice
public class GlobalControllerAdvice {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private HrmsNotificationRepository hrmsNotificationRepository;

    @ModelAttribute("unreadNotificationCount")
    public int getUnreadNotificationCount(Principal principal) {
        if (principal == null) {
            return 0;
        }
        User user = userRepository.findByUsername(principal.getName()).orElse(null);
        if (user == null) {
            return 0;
        }
        return hrmsNotificationRepository.countByUserAndReadFalse(user);
    }
}
