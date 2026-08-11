package com.example.admindashboard.controller;

import com.example.admindashboard.model.MediclaimDependent;
import com.example.admindashboard.model.User;
import com.example.admindashboard.repository.EmployeeProfileRepository;
import com.example.admindashboard.repository.InsurancePolicyRepository;
import com.example.admindashboard.repository.MediclaimDependentRepository;
import com.example.admindashboard.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import com.example.admindashboard.model.Mediclaim;
import com.example.admindashboard.repository.MediclaimRepository;
import org.springframework.web.multipart.MultipartFile;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

import java.security.Principal;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import com.example.admindashboard.model.Hospital;
import com.example.admindashboard.model.InsurancePolicy;
import com.example.admindashboard.model.Mediclaim;
import com.example.admindashboard.model.MediclaimDependent;
import com.example.admindashboard.model.User;
import com.example.admindashboard.repository.EmployeeProfileRepository;
import com.example.admindashboard.repository.HospitalRepository;
import com.example.admindashboard.repository.InsurancePolicyRepository;
import com.example.admindashboard.repository.MediclaimDependentRepository;
import com.example.admindashboard.repository.MediclaimRepository;
import com.example.admindashboard.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.Principal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Controller
@RequestMapping("/employee/mediclaim")
public class MediclaimController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EmployeeProfileRepository profileRepository;

    @Autowired
    private InsurancePolicyRepository policyRepository;

    @Autowired
    private MediclaimDependentRepository dependentRepository;

    @Autowired
    private MediclaimRepository mediclaimRepository;

    @Autowired
    private HospitalRepository hospitalRepository;

    private InsurancePolicy getOrCreatePolicy(User user) {
        return policyRepository.findByUser(user).orElseGet(() -> {
            InsurancePolicy p = new InsurancePolicy();
            p.setUser(user);
            p.setPolicyName("WhiteCircle Health Shield - Gold Plan");
            p.setPolicyNumber("WCG-2026-MED-" + user.getId());
            p.setProviderName("Star Health & Allied Insurance");
            p.setTotalCoverage(500000.0);
            p.setAmountUsed(0.0);
            p.setValidFrom(LocalDate.of(2026, 1, 1));
            p.setValidUntil(LocalDate.of(2027, 12, 31));
            p.setStatus("Active");
            return policyRepository.save(p);
        });
    }

    @GetMapping("/auth")
    public String mediclaimAuth(Model model, Principal principal) {
        if (principal != null) {
            String loginId = principal.getName();
            User currentUser = userRepository.findByUsername(loginId).orElse(new User());
            model.addAttribute("user", currentUser);
        } else {
            model.addAttribute("user", new User());
        }
        return "mediclaim-login";
    }

    @PostMapping("/login")
    public String processMediclaimAuthentication(
            @RequestParam("username") String typedUsername,
            @RequestParam("password") String typedPassword,
            Model model,
            Principal principal) {

        if (principal != null) {
            String loginId = principal.getName();
            User currentUser = userRepository.findByUsername(loginId).orElse(new User());
            String dbPassword = currentUser.getPassword();
            String cleanDbPassword = dbPassword != null ? dbPassword.replace("{noop}", "") : "";

            if (!typedUsername.equalsIgnoreCase(loginId) || !typedPassword.equals(cleanDbPassword)) {
                model.addAttribute("user", currentUser);
                model.addAttribute("authError", "Invalid credentials");
                return "mediclaim-login";
            }
            return "redirect:/employee/mediclaim/portal";
        }
        return "redirect:/login";
    }

    @GetMapping("/portal")
    public String mediclaimPortal(Principal principal, Model model) {
        if (principal == null) {
            return "redirect:/employee/mediclaim/auth";
        }

        Optional<User> userOpt = userRepository.findByUsername(principal.getName());
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            model.addAttribute("user", user);

            profileRepository.findByUser_Username(principal.getName())
                    .ifPresent(profile -> model.addAttribute("profile", profile));

            InsurancePolicy policy = getOrCreatePolicy(user);
            if (policy.getPolicyName() == null || policy.getPolicyName().trim().isEmpty()) {
                policy.setPolicyName("WhiteCircle Health Shield - Gold Plan");
                policyRepository.save(policy);
            }
            model.addAttribute("policy", policy);

            List<MediclaimDependent> dependents = dependentRepository.findByUser(user);
            model.addAttribute("dependents", dependents);

            double totalDepAllocated = dependents.stream()
                    .mapToDouble(d -> (d.getCoveragePercentage() != null ? (policy.getTotalCoverage() * d.getCoveragePercentage()) / 100.0 : 0.0))
                    .sum();
            int totalDepPercent = dependents.stream()
                    .mapToInt(d -> d.getCoveragePercentage() != null ? d.getCoveragePercentage() : 0)
                    .sum();
            double remainingCoverage = Math.max(0.0, policy.getTotalCoverage() - (policy.getAmountUsed() != null ? policy.getAmountUsed() : 0.0));

            model.addAttribute("totalDepAllocated", totalDepAllocated);
            model.addAttribute("totalDepPercent", totalDepPercent);
            model.addAttribute("remainingCoverage", remainingCoverage);

            List<Mediclaim> claims = mediclaimRepository.findByUserOrderBySubmissionDateDesc(user);
            model.addAttribute("claims", claims);

            long pendingCount = claims.stream().filter(c -> "Pending".equalsIgnoreCase(c.getStatus())).count();
            model.addAttribute("pendingCount", pendingCount);
        }

        return "mediclaim-dashboard";
    }

    @GetMapping("/policy")
    public String mediclaimPolicy(Principal principal, Model model) {
        if (principal == null) {
            return "redirect:/employee/mediclaim/auth";
        }

        Optional<User> userOpt = userRepository.findByUsername(principal.getName());
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            model.addAttribute("user", user);

            profileRepository.findByUser_Username(principal.getName())
                    .ifPresent(profile -> model.addAttribute("profile", profile));

            InsurancePolicy policy = getOrCreatePolicy(user);
            if (policy.getPolicyName() == null || policy.getPolicyName().trim().isEmpty()) {
                policy.setPolicyName("WhiteCircle Health Shield - Gold Plan");
                policyRepository.save(policy);
            }
            model.addAttribute("policy", policy);

            List<MediclaimDependent> dependents = dependentRepository.findByUser(user);
            model.addAttribute("dependents", dependents);

            double totalDepAllocated = dependents.stream()
                    .mapToDouble(d -> (d.getCoveragePercentage() != null ? (policy.getTotalCoverage() * d.getCoveragePercentage()) / 100.0 : 0.0))
                    .sum();
            int totalDepPercent = dependents.stream()
                    .mapToInt(d -> d.getCoveragePercentage() != null ? d.getCoveragePercentage() : 0)
                    .sum();
            double remainingCoverage = Math.max(0.0, policy.getTotalCoverage() - (policy.getAmountUsed() != null ? policy.getAmountUsed() : 0.0));

            model.addAttribute("totalDepAllocated", totalDepAllocated);
            model.addAttribute("totalDepPercent", totalDepPercent);
            model.addAttribute("remainingCoverage", remainingCoverage);

            List<Mediclaim> claims = mediclaimRepository.findByUserOrderBySubmissionDateDesc(user);
            model.addAttribute("claims", claims);

            List<Hospital> hospitals = hospitalRepository.findAll();
            model.addAttribute("hospitals", hospitals);
        }

        return "mediclaim-policy";
    }

    @GetMapping("/claim")
    public String showClaimSubmissionForm(Principal principal, Model model) {
        if (principal == null) return "redirect:/employee/mediclaim/auth";

        Optional<User> userOpt = userRepository.findByUsername(principal.getName());
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            model.addAttribute("user", user);

            InsurancePolicy policy = getOrCreatePolicy(user);
            model.addAttribute("policy", policy);

            List<MediclaimDependent> dependents = dependentRepository.findByUser(user);
            model.addAttribute("dependents", dependents);

            List<Hospital> hospitals = hospitalRepository.findAll();
            model.addAttribute("hospitals", hospitals);

            List<Mediclaim> claims = mediclaimRepository.findByUserOrderBySubmissionDateDesc(user);
            model.addAttribute("claims", claims);
        }
        return "mediclaim-claim";
    }

    @GetMapping("/track/list")
    public String listAllClaims(Principal principal, Model model) {
        if (principal == null) return "redirect:/employee/mediclaim/auth";

        Optional<User> userOpt = userRepository.findByUsername(principal.getName());
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            model.addAttribute("user", user);
            List<Mediclaim> allClaims = mediclaimRepository.findByUserOrderBySubmissionDateDesc(user);
            model.addAttribute("allClaims", allClaims);
        }
        return "mediclaim-track";
    }

    @GetMapping("/track/{claimId}")
    public String trackSpecificClaim(@PathVariable Long claimId, Principal principal, Model model) {
        if (principal == null) return "redirect:/employee/mediclaim/auth";

        Optional<User> userOpt = userRepository.findByUsername(principal.getName());
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            model.addAttribute("user", user);
            List<Mediclaim> allClaims = mediclaimRepository.findByUserOrderBySubmissionDateDesc(user);
            model.addAttribute("allClaims", allClaims);

            Optional<Mediclaim> activeClaimOpt = mediclaimRepository.findById(claimId);
            activeClaimOpt.ifPresent(claim -> model.addAttribute("activeClaim", claim));
        }
        return "mediclaim-track";
    }

    @GetMapping("/notifications")
    public String mediclaimNotifications() {
        return "mediclaim-notifications";
    }

    @GetMapping("/profile")
    public String mediclaimProfile(Principal principal, Model model) {
        if (principal == null) {
            return "redirect:/employee/mediclaim/auth";
        }

        String username = principal.getName();
        Optional<User> userOpt = userRepository.findByUsername(username);

        if (userOpt.isPresent()) {
            User user = userOpt.get();
            model.addAttribute("user", user);

            profileRepository.findByUser_Username(username).ifPresent(profile -> {
                model.addAttribute("profile", profile);
            });

            InsurancePolicy policy = getOrCreatePolicy(user);
            model.addAttribute("policy", policy);

            List<MediclaimDependent> dependents = dependentRepository.findByUser(user);
            model.addAttribute("dependents", dependents);
        }

        return "mediclaim-profile";
    }

    @GetMapping("/hospitals")
    public String mediclaimHospitals(Model model) {
        model.addAttribute("hospitals", hospitalRepository.findAll());
        return "mediclaim-hospitals";
    }

    @GetMapping("/dependents")
    public String mediclaimDependents(Principal principal, Model model) {
        if (principal == null) {
            return "redirect:/employee/mediclaim/auth";
        }

        String username = principal.getName();
        Optional<User> userOpt = userRepository.findByUsername(username);

        if (userOpt.isPresent()) {
            User user = userOpt.get();
            model.addAttribute("user", user);

            InsurancePolicy policy = getOrCreatePolicy(user);
            model.addAttribute("policy", policy);

            List<MediclaimDependent> dependents = dependentRepository.findByUser(user);
            model.addAttribute("dependents", dependents);

            int totalDepPercent = dependents.stream()
                    .mapToInt(d -> d.getCoveragePercentage() != null ? d.getCoveragePercentage() : 0)
                    .sum();
            int availablePercent = Math.max(0, 100 - totalDepPercent);
            model.addAttribute("totalDepPercent", totalDepPercent);
            model.addAttribute("availablePercent", availablePercent);

            double totalDepAllocated = dependents.stream()
                    .mapToDouble(d -> (d.getCoveragePercentage() != null ? (policy.getTotalCoverage() * d.getCoveragePercentage()) / 100.0 : 0.0))
                    .sum();
            model.addAttribute("totalDepAllocated", totalDepAllocated);
        }

        return "mediclaim-dependents";
    }

    @PostMapping("/dependents/add")
    public String addDependent(
            @RequestParam String fullName,
            @RequestParam String relationship,
            @RequestParam String dob,
            @RequestParam(required = false) String gender,
            @RequestParam(required = false) Integer coveragePercentage,
            @RequestParam(required = false) Boolean isCovered,
            @RequestParam(value = "document", required = false) MultipartFile document,
            RedirectAttributes redirectAttributes,
            Principal principal) {

        if (principal == null) return "redirect:/employee/mediclaim/auth";

        Optional<User> userOpt = userRepository.findByUsername(principal.getName());
        if (userOpt.isEmpty()) return "redirect:/employee/mediclaim/auth";

        User user = userOpt.get();
        InsurancePolicy policy = getOrCreatePolicy(user);

        // MT019: Mandatory document validation
        if (document == null || document.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Proof of Relationship / Identity document upload is mandatory.");
            return "redirect:/employee/mediclaim/dependents";
        }

        // MT020: Valid coverage percentage (1-100)
        if (coveragePercentage == null || coveragePercentage < 1 || coveragePercentage > 100) {
            redirectAttributes.addFlashAttribute("errorMessage", "Coverage percentage must be between 1% and 100%.");
            return "redirect:/employee/mediclaim/dependents";
        }

        // MT022: Total coverage allocation cannot exceed 100%
        List<MediclaimDependent> existingDeps = dependentRepository.findByUser(user);
        int existingCoverageSum = existingDeps.stream()
                .mapToInt(d -> d.getCoveragePercentage() != null ? d.getCoveragePercentage() : 0)
                .sum();

        if (existingCoverageSum + coveragePercentage > 100) {
            int availablePercent = Math.max(0, 100 - existingCoverageSum);
            redirectAttributes.addFlashAttribute("errorMessage", "Cannot add dependent: Total allocated coverage exceeds 100%. Available coverage balance is " + availablePercent + "%.");
            return "redirect:/employee/mediclaim/dependents";
        }

        try {
            String savedFileName = null;
            if (!document.isEmpty()) {
                String originalFilename = document.getOriginalFilename();
                String cleanName = originalFilename != null ? originalFilename.replaceAll("[^a-zA-Z0-9.-]", "_") : "doc.pdf";
                savedFileName = UUID.randomUUID().toString() + "_" + cleanName;
                Path uploadDir = Paths.get("uploads/mediclaim/");
                if (!Files.exists(uploadDir)) {
                    Files.createDirectories(uploadDir);
                }
                Files.copy(document.getInputStream(), uploadDir.resolve(savedFileName));
            }

            MediclaimDependent dependent = new MediclaimDependent();
            dependent.setUser(user);
            dependent.setFullName(fullName);
            dependent.setRelationship(relationship);
            dependent.setDob(LocalDate.parse(dob));
            dependent.setGender(gender != null && !gender.isEmpty() ? gender : "Other");
            dependent.setCoveragePercentage(coveragePercentage);
            dependent.setCovered(isCovered != null ? isCovered : true);
            dependent.setDocumentFilename(savedFileName);

            // MT021: Calculate allocated sum from total policy coverage
            double allocatedAmount = (policy.getTotalCoverage() * coveragePercentage) / 100.0;
            dependent.setAllocatedAmount(allocatedAmount);

            dependentRepository.save(dependent);
            redirectAttributes.addFlashAttribute("successMessage", "Dependent " + fullName + " added successfully with " + coveragePercentage + "% coverage (₹" + String.format("%,.0f", allocatedAmount) + ").");

        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to save dependent: " + e.getMessage());
        }

        return "redirect:/employee/mediclaim/dependents";
    }

    @GetMapping("/helpdesk")
    public String mediclaimHelpdesk(Model model, Principal principal) {
        if (principal != null) {
            userRepository.findByUsername(principal.getName()).ifPresent(user -> model.addAttribute("user", user));
        }
        return "mediclaim-helpdesk";
    }

    @PostMapping("/claim/submit")
    @ResponseBody
    public ResponseEntity<String> submitClaim(
            @RequestParam("hospitalName") String hospitalName,
            @RequestParam("city") String city,
            @RequestParam("claimType") String claimType,
            @RequestParam("diagnosis") String diagnosis,
            @RequestParam("dateOfAdmission") String dateOfAdmission,
            @RequestParam("dateOfDischarge") String dateOfDischarge,
            @RequestParam("totalBill") Double totalBill,
            @RequestParam("claimAmount") Double claimAmount,
            @RequestParam(value = "remarks", required = false) String remarks,
            @RequestParam(value = "file", required = false) MultipartFile file,
            Principal principal) {

        if (principal == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized");

        Optional<User> userOpt = userRepository.findByUsername(principal.getName());
        if (userOpt.isEmpty()) return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("User not found");

        try {
            Mediclaim claim = new Mediclaim();
            claim.setUser(userOpt.get());
            claim.setHospitalName(hospitalName);
            claim.setCity(city);
            claim.setClaimType(claimType);
            claim.setDiagnosis(diagnosis);

            claim.setDateOfAdmission(LocalDate.parse(dateOfAdmission));
            claim.setDateOfDischarge(LocalDate.parse(dateOfDischarge));

            claim.setTotalBill(totalBill);
            claim.setClaimAmount(claimAmount);
            claim.setRemarks(remarks);

            if (file != null && !file.isEmpty()) {
                String fileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename().replaceAll("[^a-zA-Z0-9.-]", "_");
                Path uploadPath = Paths.get("uploads/mediclaim/");
                if (!Files.exists(uploadPath)) {
                    Files.createDirectories(uploadPath);
                }
                Files.copy(file.getInputStream(), uploadPath.resolve(fileName));
                claim.setDocumentFilename(fileName);
            }

            mediclaimRepository.save(claim);
            return ResponseEntity.ok("Claim submitted successfully!");

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to submit claim");
        }
    }

    @GetMapping("/document/download/{filename:.+}")
    public ResponseEntity<Resource> downloadDocument(@PathVariable String filename) {
        try {
            Path filePath = Paths.get("uploads/mediclaim/").resolve(filename).normalize();
            if (!Files.exists(filePath)) {
                filePath = Paths.get("uploads/").resolve(filename).normalize();
            }
            if (Files.exists(filePath)) {
                Resource resource = new UrlResource(filePath.toUri());
                return ResponseEntity.ok()
                        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                        .body(resource);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ResponseEntity.notFound().build();
    }
}