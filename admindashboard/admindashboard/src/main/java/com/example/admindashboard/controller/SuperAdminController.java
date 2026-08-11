package com.example.admindashboard.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import java.util.stream.Collectors;
import java.util.List;
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

    @Autowired
    private com.example.admindashboard.repository.ItAssetRepository itAssetRepository;

    @GetMapping({"/dashboard", "/company", "/statutory", "/recruit", "/onboard", "/employee-details", "/bulk-update", "/asset", "/separation"})
    public String superAdminDashboard(Model model) {
        List<Candidate> candidates = candidateRepository.findAll();
        model.addAttribute("candidates", candidates);

        List<com.example.admindashboard.model.ItAsset> assets = itAssetRepository.findAll();
        model.addAttribute("assets", assets);

        List<Candidate> onboardCandidates = candidates.stream()
                .filter(c -> c.getOnboardStatus() != null)
                .collect(Collectors.toList());

        long total = onboardCandidates.size();
        long inProgress = onboardCandidates.stream().filter(c -> "In Progress".equals(c.getOnboardStatus())).count();
        long completed = onboardCandidates.stream().filter(c -> "Completed".equals(c.getOnboardStatus())).count();

        model.addAttribute("onboardCandidates", onboardCandidates);
        model.addAttribute("totalOnboard", total > 0 ? total : 5);
        model.addAttribute("inProgressOnboard", inProgress > 0 ? inProgress : 5);
        model.addAttribute("completedOnboard", completed > 0 ? completed : 4);

        return "superadmin/dashboard";
    }


    @GetMapping("/onboard")
    public String showOnboardDashboard(Model model) {
        // Fetch onboarding candidates
        List<Candidate> allCandidates = candidateRepository.findAll();
        List<Candidate> onboardCandidates = allCandidates.stream()
                .filter(c -> c.getOnboardStatus() != null)
                .collect(Collectors.toList());

        long total = onboardCandidates.size();
        long inProgress = onboardCandidates.stream().filter(c -> "In Progress".equals(c.getOnboardStatus())).count();
        long completed = onboardCandidates.stream().filter(c -> "Completed".equals(c.getOnboardStatus())).count();

        model.addAttribute("onboardCandidates", onboardCandidates);
        model.addAttribute("totalOnboard", total);
        model.addAttribute("inProgressOnboard", inProgress);
        model.addAttribute("completedOnboard", completed);
        model.addAttribute("activeTab", "onboard-tab");

        return "superadmin/dashboard"; // dashboard handles showing correct tab via JS
    }

}
