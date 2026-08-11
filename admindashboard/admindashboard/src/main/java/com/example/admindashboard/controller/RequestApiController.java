package com.example.admindashboard.controller;

import com.example.admindashboard.model.ServiceRequest;
import com.example.admindashboard.repository.ServiceRequestRepository;
import com.example.admindashboard.service.EmailService;
import com.example.admindashboard.model.User;
import com.example.admindashboard.repository.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.security.Principal;

@RestController
@RequestMapping("/api/requests")
public class RequestApiController {

    @Autowired
    private ServiceRequestRepository repository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private com.example.admindashboard.repository.EmployeeProfileRepository employeeProfileRepository;

    @Autowired
    private EmailService emailService;

    // SELF-SERVICE: Any authenticated user can submit a ticket. No strict RBAC lock needed.
    @PostMapping("/submit")
    public ResponseEntity<?> submitRequest(@RequestBody ServiceRequest request) {

        if (request.getJustification() == null || request.getJustification().trim().isEmpty()) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Description / Justification is mandatory for submitting a ticket.");
            return ResponseEntity.badRequest().body(error);
        }

        String employeeEmail = "no-reply@whitecircle.com";
        String employeeName = request.getEmployeeName();

        // 1. NEW: Fetch current user to attach contact details to the ticket BEFORE saving
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated() && !"anonymousUser".equals(authentication.getPrincipal())) {
            Optional<User> currentUserOpt = userRepository.findByUsername(authentication.getName());
            if (currentUserOpt.isPresent()) {
                User user = currentUserOpt.get();
                employeeEmail = user.getEmail();

                if (employeeName == null || employeeName.isEmpty()) {
                    employeeName = user.getFullName();
                    request.setEmployeeName(employeeName);
                }

                // Inject contact details into the ticket database row
                request.setEmployeeEmail(employeeEmail);

                // NEW: Fetch EmployeeProfile to get the mobile number
                try {
                    // Assuming your User's username is their Employee ID (e.g. EMP187)
                    com.example.admindashboard.model.EmployeeProfile profile =
                            employeeProfileRepository.findByUser_Username(user.getUsername()).orElse(null);

                    if (profile != null && profile.getMobileNumber() != null) {
                        request.setEmployeePhone(profile.getMobileNumber());
                    } else {
                        request.setEmployeePhone("No Phone Provided");
                    }
                } catch (Exception e) {
                    request.setEmployeePhone("No Phone Provided");
                }
            }
        }
        
        if (authentication != null &&
        	    authentication.isAuthenticated() &&
        	    !"anonymousUser".equals(authentication.getPrincipal())) {

        	    request.setEmployeeId(authentication.getName());
        	}
        // 2. Set the date and save the ticket to the database
        request.setSubmissionDate(LocalDate.now());
        ServiceRequest savedRequest = repository.save(request);

        // 3. ASYNC EMAIL TRIGGER FOR IT TICKETS
        try {
            Map<String, Object> emailData = new HashMap<>();
            emailData.put("empName", employeeName);
            emailData.put("ticketId", savedRequest.getTicketId() != null ? savedRequest.getTicketId() : "TKT-NEW");
            emailData.put("type", savedRequest.getType());
            emailData.put("priority", savedRequest.getPriority());
            emailData.put("category", savedRequest.getCategory());
            emailData.put("detailItem", savedRequest.getDetailItem());
            emailData.put("justification", savedRequest.getJustification());

            // TODO: Replace with your actual admin testing email!
            String adminEmail = "arcthunder07@gmail.com";

            emailService.sendTicketSubmissionToAdmin(
                    adminEmail,
                    employeeName,
                    employeeEmail,
                    savedRequest.getType(),
                    emailData
            );
        } catch (Exception e) {
            System.err.println("Non-fatal error: Failed to trigger IT ticket email - " + e.getMessage());
        }

        // =========================================================================
        // MULTI-DEPARTMENT SYSTEM VARIABLE EMAIL ROUTING TRIGGER For Tickets
        // =========================================================================
        try {
            Map<String, Object> helpdeskModel = new HashMap<>();
            helpdeskModel.put("empName", employeeName);
            helpdeskModel.put("ticketId", savedRequest.getTicketId() != null ? savedRequest.getTicketId() : "TKT-NEW");
            helpdeskModel.put("type", savedRequest.getType());
            helpdeskModel.put("priority", savedRequest.getPriority());
            helpdeskModel.put("category", savedRequest.getCategory());
            helpdeskModel.put("detailItem", savedRequest.getDetailItem());
            helpdeskModel.put("justification", savedRequest.getJustification());

            emailService.sendHelpdeskNotifications(
                    savedRequest.getType(),           // departmentCode context ("IT", "HR", etc.)
                    employeeName,                     // employeeName
                    employeeEmail,                    // employeeEmail
                    savedRequest.getDetailItem(),     // formSubject
                    helpdeskModel                     // shared template data map
            );
        } catch (Exception e) {
            System.err.println("Non-fatal error: Multi-dept email routing issue - " + e.getMessage());
        }

        return ResponseEntity.ok(savedRequest);
    }

    // LOCK: An employee can only fetch their OWN tickets lists, unless they are an Admin.
    @PreAuthorize("#empId == authentication.name or hasAnyAuthority('ROLE_SUPER_ADMIN', 'ROLE_HR_ADMIN', 'ROLE_IT_ADMIN')")
    @GetMapping("/active/{empId}")
    public List<ServiceRequest> getEmployeeRequests(@PathVariable String empId) {
        return repository.findByEmployeeIdOrderBySubmissionDateDesc(empId);
    }

    // LOCK: Only Admins can update the status of a ticket.
    @PreAuthorize("hasAuthority('admin_dashboard_view')")
    @PostMapping("/update-status")
    public ResponseEntity<?> updateStatus(@RequestParam Long id, 
                                          @RequestParam String status,
                                          @RequestParam(required = false) String assignedTo,
                                          @RequestParam(required = false) String adminComments) {
        ServiceRequest request = repository.findById(id).orElseThrow();
        request.setStatus(status);
        if (assignedTo != null && !assignedTo.isEmpty()) {
            request.setAssignedTo(assignedTo);
        }
        if (adminComments != null && !adminComments.isEmpty()) {
            request.setAdminComments(adminComments);
        }
        request.setActionDate(java.time.LocalDate.now());
        repository.save(request);

        // --- EMAIL TRIGGER START ---
        try {
            Optional<User> userOpt = userRepository.findByUsername(request.getEmployeeId());
            String employeeEmail = "no-reply@whitecircle.com";

            if (userOpt.isPresent() && userOpt.get().getEmail() != null) {
                employeeEmail = userOpt.get().getEmail();
            }

            Map<String, Object> emailData = new HashMap<>();
            emailData.put("empName", request.getEmployeeName());

            String specificType = (request.getTicketId() != null ? request.getTicketId() : "Ticket") +
                    (request.getCategory() != null ? " - " + request.getCategory() : "");
            emailData.put("specificType", specificType);

            if (request.getSubmissionDate() != null) {
                emailData.put("submittedOn", request.getSubmissionDate());
            }

            String prettyRequestType = (request.getType() != null ? request.getType().toUpperCase() : "IT") + " Ticket";

            emailService.sendRequestStatusUpdateToEmployee(
                    employeeEmail,
                    request.getEmployeeName(),
                    prettyRequestType,
                    status,
                    emailData
            );
        } catch (Exception e) {
            System.err.println("⚠️ Warning: Could not trigger IT Ticket status email: " + e.getMessage());
        }
        // --- EMAIL TRIGGER END ---

        return ResponseEntity.ok("Status updated to " + status);
    }

    // FIXED LOCK: Horizontal Security (Ownership Check)
    @GetMapping("/{id}")
    public ResponseEntity<ServiceRequest> getRequestById(@PathVariable Long id, Principal principal) {
        Optional<ServiceRequest> requestOpt = repository.findById(id);

        if (requestOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        ServiceRequest request = requestOpt.get();
        String currentUsername = principal.getName();

        // Find the currently logged-in user to check their roles
        User currentUser = userRepository.findByUsername(currentUsername).orElse(null);
        boolean canViewTicket = false;

        if (currentUser != null &&
            currentUser.getRole() != null) {

            canViewTicket =
                currentUser.getRole()
                    .getPermissions()
                    .stream()
                    .anyMatch(p ->
                        "admin_dashboard_view"
                            .equals(p.getPermissionName()));
        }

        // OWNERSHIP CHECK: Are you an admin? OR Are you the creator of this ticket?
        if (canViewTicket || request.getEmployeeId().equals(currentUsername)) {

        	    return ResponseEntity.ok(request);
        	}

        	return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }
}