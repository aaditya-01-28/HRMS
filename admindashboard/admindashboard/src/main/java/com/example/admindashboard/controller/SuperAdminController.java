package com.example.admindashboard.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import com.example.admindashboard.repository.CandidateRepository;
import com.example.admindashboard.model.Candidate;
import java.util.List;

@Controller
@RequestMapping("/superadmin")
@PreAuthorize("hasAuthority('ROLE_SUPER_ADMIN') or hasAuthority('ROLE_SUPER ADMIN') or hasAuthority('SUPER_ADMIN')")
public class SuperAdminController {

    @Autowired
    private CandidateRepository candidateRepository;

    @GetMapping({"/dashboard", "/company", "/statutory", "/recruit"})
    public String superAdminDashboard(Model model) {
        List<Candidate> candidates = candidateRepository.findAll();
        model.addAttribute("candidates", candidates);
        return "superadmin/dashboard";
    }

}
