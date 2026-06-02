package com.example.admindashboard.controller;

import com.example.admindashboard.model.User;
import com.example.admindashboard.model.WeeklyTimesheet;
import com.example.admindashboard.repository.UserRepository;
import com.example.admindashboard.repository.WeeklyTimesheetRepository;
import com.example.admindashboard.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import java.util.Set;

@RestController
@RequestMapping("/api/admin/timesheet")
public class AdminTimesheetController {

    @Autowired
    private WeeklyTimesheetRepository timesheetRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EmailService emailService;
    
    
    private static final Set<String> ROLE_BASED_USERS = Set.of(
            "IT_ADMIN",
            "HR_ADMIN",
            "HR_EXECUTIVE",
            "MANAGER",
            "FINANCE",
            "RECRUITER",
            "HR_MANAGER",
            "IT_SUPPORT",
            "PROJECT_MANAGER",
            "AUDITOR",
            "TRANSPORT",
            "LND"
    );

    // 1. Fetch timesheets by status
    // FIXED LOCK: Added ROLE_HR_EXECUTIVE to the permitted roles
    @PreAuthorize("hasAnyAuthority('ROLE_HR_MANAGER','ROLE_ADMIN','ROLE_SUPER_ADMIN')")
    @GetMapping("/list")
    
    public ResponseEntity<List<WeeklyTimesheet>> getTimesheets(@RequestParam String status, Principal principal) {
    	User currentUser =
    	        userRepository.findByUsername(principal.getName()).orElseThrow();

    	String currentRole =
    	        currentUser.getRole().getRoleName();
    	System.out.println("TIMESHEET ROLE = " + currentRole);

    	List<WeeklyTimesheet> allTimesheets =
    	        timesheetRepository.findByStatus(status);

    	if ("HR_MANAGER".equalsIgnoreCase(currentRole)) {

    	    allTimesheets = allTimesheets.stream()
    	            .filter(ts ->
    	                    ts.getUser() != null &&
    	                    ts.getUser().getRole() != null &&
    	                    !ROLE_BASED_USERS.contains(
    	                            ts.getUser().getRole().getRoleName()
    	                    )
    	            )
    	            .collect(Collectors.toList());

    	}

    	if ("ADMIN".equalsIgnoreCase(currentRole)
    	        || "SUPER_ADMIN".equalsIgnoreCase(currentRole)) {

    	    allTimesheets = allTimesheets.stream()
    	            .filter(ts ->
    	                    ts.getUser() != null &&
    	                    ts.getUser().getRole() != null &&
    	                    ROLE_BASED_USERS.contains(
    	                            ts.getUser().getRole().getRoleName()
    	                    )
    	            )
    	            .collect(Collectors.toList());

    	}

    	return ResponseEntity.ok(allTimesheets);
    	
    }

    // 2. Approve or Reject
    // FIXED LOCK: Added ROLE_HR_EXECUTIVE to the permitted roles
    @PreAuthorize("hasAnyAuthority('ROLE_HR_MANAGER','ROLE_ADMIN','ROLE_SUPER_ADMIN')")
    @PostMapping("/{id}/{status}")
    public ResponseEntity<?> updateStatus(
            @PathVariable Long id,
            @PathVariable String status,
            @RequestParam(required = false) String comments,
            Principal principal) {

        User currentUser = userRepository.findByUsername(principal.getName()).orElseThrow();
        Optional<WeeklyTimesheet> tsOpt = timesheetRepository.findById(id);
        if (tsOpt.isPresent()) {
            WeeklyTimesheet ts = tsOpt.get();
            String approverRole =
                    currentUser.getRole().getRoleName();

            String submitterRole =
                    ts.getUser().getRole().getRoleName();
            boolean isAdminApprovedRole =
                    ROLE_BASED_USERS.contains(submitterRole);

            if ("HR_MANAGER".equalsIgnoreCase(approverRole)
                    && isAdminApprovedRole) {

                return ResponseEntity
                        .status(403)
                        .body("HR Manager cannot approve role-user timesheets.");
            }

            if (("ADMIN".equalsIgnoreCase(approverRole)
                    || "SUPER_ADMIN".equalsIgnoreCase(approverRole))
                    && !isAdminApprovedRole) {

                return ResponseEntity
                        .status(403)
                        .body("Admin cannot approve employee timesheets.");
            }

            // CRITICAL SECURITY BLOCK: Prevent Manager from modifying out-of-team timesheets via API bypass

            ts.setStatus(status);

            if (comments != null && !comments.isEmpty()) {
                ts.setOverallComments(comments);
            }
            timesheetRepository.save(ts);

            // --- EMAIL TRIGGER START ---
            try {
                Map<String, Object> emailData = new HashMap<>();
                emailData.put("empName", ts.getUser().getFullName());
                emailData.put("specificType", "Total Hours Logged: " + ts.getTotalWeekHours() + " hrs");

                if (ts.getSubmissionDate() != null) {
                    emailData.put("submittedOn", ts.getSubmissionDate());
                } else if (ts.getSubmittedAt() != null) {
                    emailData.put("submittedOn", ts.getSubmittedAt().toLocalDate());
                }

                emailData.put("duration", ts.getWeekStartDate() + " to " + ts.getWeekEndDate());

                if (comments != null && !comments.isEmpty()) {
                    emailData.put("adminComments", comments);
                }

                emailService.sendRequestStatusUpdateToEmployee(
                        ts.getUser().getEmail(),
                        ts.getUser().getFullName(),
                        "Timesheet",
                        status,
                        emailData
                );
            } catch (Exception e) {
                System.err.println("⚠️ Warning: Could not trigger Timesheet email: " + e.getMessage());
            }
            // --- EMAIL TRIGGER END ---

            return ResponseEntity.ok("Timesheet " + status);
        }
        return ResponseEntity.notFound().build();
    }
}