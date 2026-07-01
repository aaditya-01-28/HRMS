package com.example.admindashboard.controller;

import com.example.admindashboard.model.Role;
import com.example.admindashboard.model.User;
import com.example.admindashboard.repository.RoleRepository;
import com.example.admindashboard.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class SeniorHrRoleController {

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/senior_hr/roles")
    public String showRoles(Model model) {
        List<Role> roles = roleRepository.findAll();
        List<User> users = userRepository.findAll();
        
        model.addAttribute("roles", roles);
        model.addAttribute("users", users);
        model.addAttribute("totalRoles", roles.size());
        
        return "senior_hr-roles";
    }
}
