package com.example.admindashboard.controller;
import jakarta.servlet.http.HttpSession;
import com.example.admindashboard.model.*;
import com.example.admindashboard.repository.*;
import com.example.admindashboard.service.AuditLogService;
import com.example.admindashboard.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.access.prepost.PreAuthorize;
import com.example.admindashboard.repository.LeaveRequestRepository;
import com.example.admindashboard.service.GoalBurnChartService;
import java.security.Principal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

import com.example.admindashboard.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import jakarta.validation.Valid;
import org.springframework.validation.BindingResult;
@Controller
public class DashboardController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TimesheetRepository timesheetRepository;
    
    @Autowired
    private ResignationRequestRepository resignationRequestRepository;

    @Autowired
    private WeeklyTimesheetRepository weeklyTimesheetRepository;

    @Autowired
    private JobPostingRepository jobPostingRepository;

    @Autowired
    private ReferralRepository referralRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private com.example.admindashboard.service.CustomUserDetailsService customUserDetailsService;

    @Autowired
    private com.example.admindashboard.repository.MeetingRepository meetingRepository;

    @Autowired
    private ServiceRequestRepository serviceRequestRepository;

    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private EmailService emailService;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private org.springframework.security.crypto.password.PasswordEncoder passwordEncoder;

    @Autowired
    private AuditLogService auditLogService;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private LeaveRequestRepository leaveRequestRepository;
    
    @Autowired
    private GoalRepository goalRepository;
    
    @Autowired
    private GoalBurnChartService goalBurnChartService;

    @Autowired
    private GoalUpdateRepository goalUpdateRepository;

    @Autowired
    private com.example.admindashboard.repository.AttendanceRepository attendanceRepository;

    @Autowired
    private com.example.admindashboard.repository.EmployeeProfileRepository employeeProfileRepository;

    // --- 1. LOGIN PAGE MAPPINGS ---

    @GetMapping("/")
    public String rootRedirect() {
        return "redirect:/login";
    }

    @GetMapping("/login")
    public String showLoginPage() {
        return "index";
    }

    // --- 2. POST-LOGIN TRAFFIC COP ---

    @GetMapping("/default-redirect")
    public String defaultRedirect(HttpServletRequest request, Principal principal) {
        if (principal != null) {
            User user = userRepository.findByUsername(principal.getName()).orElse(null);
            if (user != null && user.isRequiresPasswordChange()) {
                return "redirect:/change-password";
            }
        }
        
        if (request.isUserInRole("SENIOR_MANAGER")) {
            return "redirect:/senior_manager/dashboard";
        } else if (request.isUserInRole("SENIOR_HR")) {
            return "redirect:/senior_hr/dashboard";
        } else if (request.isUserInRole("SENIOR_LND_HEAD")) {
            return "redirect:/senior_lnd/dashboard";
        } else if (request.isUserInRole("SENIOR_ACCOUNTS_HEAD")) {
            return "redirect:/senior_accounts/dashboard";
        } else if (request.isUserInRole("SENIOR_TRANSPORT_HEAD")) {
            return "redirect:/senior_transport/dashboard";
        } else if (request.isUserInRole("SENIOR_REWARDS_HEAD")) {
            return "redirect:/senior_rewards/dashboard";
        } else if (request.isUserInRole("SUPER_ADMIN") || request.isUserInRole("HR_ADMIN") ||
            request.isUserInRole("IT_ADMIN") || request.isUserInRole("HR_MANAGER") ||
            request.isUserInRole("PROJECT_MANAGER") || request.isUserInRole("FINANCE")) {
            return "redirect:/admin/dashboard";
        } else if (request.isUserInRole("IT_SUPPORT")) {
            return "redirect:/itsupport/dashboard";
        } else if (request.isUserInRole("LND")) {
            return "redirect:/learninghead/dashboard";
        } else if (request.isUserInRole("MANAGER") || request.isUserInRole("HR_EXECUTIVE") ||
                   request.isUserInRole("RECRUITER")) {
            return "redirect:/manager/dashboard";
        } else if (request.isUserInRole("TRANSPORT")) {
            return "redirect:/transportation/dashboard";
        } else if (request.isUserInRole("AUDITOR") || request.isUserInRole("FINANCE")) {
            return "redirect:/accounts/dashboard";
        } else if (request.isUserInRole("HR_MANAGER")) {
            return "redirect:/HR/dashboard";
        } else if (request.isUserInRole("CLIENT")) {
            return "redirect:/client/dashboard";
        } else if (request.isUserInRole("REWARDS")) {
            return "redirect:/rewards-manager/dashboard";
        } else {
            return "redirect:/employee/dashboard";
        }
    }

    @GetMapping("/change-password")
    public String showChangePasswordPage(Principal principal) {
        if (principal == null) return "redirect:/login";
        return "change-password";
    }

    @PostMapping("/change-password")
    public String processChangePassword(@RequestParam("newPassword") String newPassword, Principal principal) {
        if (principal != null) {
            User user = userRepository.findByUsername(principal.getName()).orElse(null);
            if (user != null) {
                user.setPassword(passwordEncoder.encode(newPassword));
                user.setRequiresPasswordChange(false);
                userRepository.save(user);
                return "redirect:/default-redirect";
            }
        }
        return "redirect:/login";
    }

    // --- PROTECTED ROUTES ---

    // FIXED LOCK: Any user with the 'admin_dashboard_view' key can enter the portal
    @PreAuthorize("hasAuthority('admin_dashboard_view')")
    @GetMapping("/admin/dashboard")
    public String showAdminDashboard(Model model) {

        // Count all internal staff (Super Admin, HR, Manager, Employee, etc.)
        // EXCLUDES Clients and Soft-Deleted (INACTIVE) accounts
        long totalEmployees = userRepository.findAll().stream()
                .filter(u -> u.getRole() != null &&
                        !"CLIENT".equalsIgnoreCase(u.getRole().getRoleName()) &&
                        !"INACTIVE".equalsIgnoreCase(u.getStatus()))
                .count();

        // FIX: Count directly from the Client repository to perfectly match the Client Directory page.
        // This ignores any old, orphaned "User" test accounts (like CLI001) that don't have a real company profile.
        long totalClients = clientRepository.count();

        model.addAttribute("empCount", totalEmployees);
        model.addAttribute("clientCount", totalClients);

        return "admin-dashboard";
    }

    @GetMapping("/api/verify-user")
    @ResponseBody
    public String verifyUser(@RequestParam String username) {
        return userRepository.findByUsername(username.trim().toUpperCase())
                .map(u -> "User found: " + u.getUsername() + " | Role: " + (u.getRole() != null ? u.getRole().getRoleName() : "None") + " | Password matches welcome123: " + "{noop}welcome123".equals(u.getPassword()))
                .orElse("User NOT found in database for ID: " + username.trim().toUpperCase());
    }

    @GetMapping("/client/dashboard")
    public String showClientDashboard(Model model, Principal principal) {

        // 1. Get the currently logged-in user
        User loggedInUser = userRepository.findByUsername(principal.getName()).orElseThrow();

        // 2. Fetch the projects specifically assigned to this client
        List<Project> clientProjects = projectRepository.findByClientId(loggedInUser.getId());

        // 3. Attach the projects to the model so the Modal can see them
        model.addAttribute("clientProjects", clientProjects);

        // Fetch the Client profile using the logged-in user
        Client client = clientRepository.findByUser_Username(principal.getName()).orElse(null);

        // Extract the Team Lead name securely
        String teamLeadName = "Assigning...";
        if (client != null && client.getTeamLead() != null && !client.getTeamLead().trim().isEmpty()) {
            teamLeadName = client.getTeamLead();
        }
        // Add it to the model so the HTML can display it
        model.addAttribute("teamLeadName", teamLeadName);

        return "client-dashboard";
    }

    private List<Meeting> getPendingMeetingInvites(String username) {
        User currentUser = userRepository.findByUsername(username).orElse(null);
        List<Meeting> allPending = meetingRepository.findByMeetingDateGreaterThanEqualOrderByMeetingDateAscStartTimeAsc(LocalDate.now())
                .stream().filter(m -> "PENDING".equals(m.getStatus()) && !m.getOrganizer().getUsername().equals(username)).toList();
                
        return allPending.stream().filter(meeting -> {
            if (meeting.getSpecificEmployeeIds() != null && meeting.getSpecificEmployeeIds().contains(username)) return true;
            EmployeeProfile myProfile = currentUser != null ? currentUser.getEmployeeProfile() : null;
            EmployeeProfile organizerProfile = meeting.getOrganizer() != null ? meeting.getOrganizer().getEmployeeProfile() : null;
            if ("TEAM".equals(meeting.getParticipantType()) && myProfile != null && myProfile.getBusinessUnit() != null) {
                if (organizerProfile != null && myProfile.getBusinessUnit().equals(organizerProfile.getBusinessUnit())) {
                    return true;
                }
            }
            return false;
        }).toList();
    }

    @GetMapping({"/manager/dashboard", "/itsupport/dashboard", "/learninghead/dashboard", "/accounts/dashboard", "/transportation/dashboard", "/HR/dashboard", "/rewards-manager/dashboard"})
    public String showManagerDashboard(org.springframework.ui.Model model, java.security.Principal principal, jakarta.servlet.http.HttpServletRequest request) {
        String currentUserId = principal.getName();
        User currentUser = userRepository.findByUsername(currentUserId).orElseThrow();
        String myName = currentUser.getFullName();

        List<User> myTeam = userRepository.findAll().stream()
                .filter(u -> u.getEmployeeProfile() != null && myName.equalsIgnoreCase(u.getEmployeeProfile().getReportingManager()))
                .collect(Collectors.toList());

        List<String> teamUsernames = myTeam.stream().map(User::getUsername).collect(Collectors.toList());

        List<Map<String, Object>> unifiedRequests = new ArrayList<>();

        if (!teamUsernames.isEmpty()) {
            List<ServiceRequest> teamTickets = serviceRequestRepository.findAll().stream()
                    .filter(t -> teamUsernames.contains(t.getEmployeeId()) && "Open".equalsIgnoreCase(t.getStatus()))
                    .collect(Collectors.toList());
            for (ServiceRequest t : teamTickets) {
                Map<String, Object> map = new HashMap<>();
                map.put("type", "Service Request");
                map.put("employee", t.getEmployeeName());
                map.put("date", t.getSubmissionDate() != null ? t.getSubmissionDate().toString() : "");
                map.put("status", t.getStatus());
                map.put("description", t.getCategory() != null ? t.getCategory() : t.getType());
                map.put("reviewUrl", "/admin-helpdesk-requests");
                unifiedRequests.add(map);
            }

            List<Timesheet> teamTimesheets = timesheetRepository.findAll().stream()
                    .filter(t -> t.getUser() != null && teamUsernames.contains(t.getUser().getUsername())
                            && ("Pending".equalsIgnoreCase(t.getStatus()) || "Submitted".equalsIgnoreCase(t.getStatus())))
                    .collect(Collectors.toList());
            for (Timesheet t : teamTimesheets) {
                Map<String, Object> map = new HashMap<>();
                map.put("type", "Timesheet");
                map.put("employee", t.getUser().getFullName());
                map.put("date", t.getSubmissionDate() != null ? t.getSubmissionDate().toString() : "");
                map.put("status", t.getStatus());
                map.put("description", "Week: " + t.getWeekStartDate() + " to " + t.getWeekEndDate());
                map.put("reviewUrl", "/admin/timesheet-approval");
                unifiedRequests.add(map);
            }

            List<LeaveRequest> teamLeaves = leaveRequestRepository.findAll().stream()
                    .filter(l -> l.getUser() != null && teamUsernames.contains(l.getUser().getUsername()) && "PENDING".equalsIgnoreCase(l.getStatus()))
                    .collect(Collectors.toList());
            for (LeaveRequest l : teamLeaves) {
                Map<String, Object> map = new HashMap<>();
                map.put("type", "Leave");
                map.put("employee", l.getUser().getFullName());
                map.put("date", l.getCreatedAt() != null ? l.getCreatedAt().toString() : "");
                map.put("status", l.getStatus());
                map.put("description", l.getLeaveType() + " from " + l.getFromDate() + " to " + l.getToDate());
                map.put("reviewUrl", "/admin/leave-approvals");
                unifiedRequests.add(map);
            }
        }

        unifiedRequests.sort((m1, m2) -> ((String)m2.getOrDefault("date", "")).compareTo((String)m1.getOrDefault("date", "")));

        model.addAttribute("recentTickets", serviceRequestRepository.findTop3ByEmployeeIdOrderByIdDesc(currentUserId));
        model.addAttribute("teamRequests", unifiedRequests);
        model.addAttribute("pendingMeetingInvites", getPendingMeetingInvites(currentUserId));

        if (request.getRequestURI().contains("learninghead")) {
            return "learninghead-dashboard";
        } else if (request.getRequestURI().contains("accounts")) {
            return "accounts-dashboard";
        } else if (request.getRequestURI().contains("transportation")) {
            return "transport-dashboard";
        } else if (request.getRequestURI().contains("HR")) {
            return "hr-dashboard";
        } else if (request.getRequestURI().contains("rewards-manager")) {
            return "rewards-dashboard";
        }
        return "manager-dashboard";
    }

    @GetMapping({
        "/manager/workflow", 
        "/accounts/workflow", 
        "/transportation/workflow", 
        "/HR/workflow", 
        "/rewards-manager/workflow",
        "/senior_manager/workflow",
        "/senior_hr/workflow",
        "/senior_lnd/workflow",
        "/senior_accounts/workflow",
        "/senior_transport/workflow",
        "/senior_rewards/workflow"
    })
    public String showManagerWorkflow(org.springframework.ui.Model model, java.security.Principal principal, jakarta.servlet.http.HttpServletRequest request) {
        String currentUserId = principal.getName();
        User currentUser = userRepository.findByUsername(currentUserId).orElseThrow();
        String myName = currentUser.getFullName();

        List<User> myTeam = userRepository.findAll().stream()
                .filter(u -> u.getEmployeeProfile() != null && myName.equalsIgnoreCase(u.getEmployeeProfile().getReportingManager()))
                .collect(Collectors.toList());

        List<String> teamUsernames = myTeam.stream().map(User::getUsername).collect(Collectors.toList());

        // --- PENDING ---
        List<LeaveRequest> pendingLeaves = new ArrayList<>();
        List<WeeklyTimesheet> pendingTimesheets = new ArrayList<>();
        List<ServiceRequest> pendingTickets = new ArrayList<>();
        List<ResignationRequest> pendingResignations = new ArrayList<>();
        List<ServiceRequest> approvedTickets = new ArrayList<>();
        List<ServiceRequest> rejectedTickets = new ArrayList<>();

        // --- APPROVED ---
        List<LeaveRequest> approvedLeaves = new ArrayList<>();
        List<WeeklyTimesheet> approvedTimesheets = new ArrayList<>();

        // --- REJECTED ---
        List<LeaveRequest> rejectedLeaves = new ArrayList<>();
        List<WeeklyTimesheet> rejectedTimesheets = new ArrayList<>();

        boolean isItSupport = currentUser.getRole() != null && "IT_SUPPORT".equalsIgnoreCase(currentUser.getRole().getRoleName());

        if (isItSupport) {
            List<ServiceRequest> allItTickets = serviceRequestRepository.findAll().stream()
                    .filter(t -> (t.getType() != null && "IT".equalsIgnoreCase(t.getType())) ||
                                 (t.getCategory() != null && 
                                  (t.getCategory().toLowerCase().contains("software") || 
                                   t.getCategory().toLowerCase().contains("hardware") || 
                                   t.getCategory().toLowerCase().contains("incident") ||
                                   t.getCategory().toLowerCase().contains("access") ||
                                   t.getCategory().toLowerCase().contains("network") ||
                                   t.getCategory().toLowerCase().contains("permission"))))
                    .collect(Collectors.toList());

            pendingTickets = allItTickets.stream()
                    .filter(t -> "Open".equalsIgnoreCase(t.getStatus()))
                    .collect(Collectors.toList());
                    
            // Also fetch assigned tickets for the user
            List<ServiceRequest> assignedTickets = allItTickets.stream()
                    .filter(t -> "Assigned".equalsIgnoreCase(t.getStatus()) && t.getAssignedTo() != null && t.getAssignedTo().contains(myName))
                    .collect(Collectors.toList());
            model.addAttribute("myAssignedTickets", assignedTickets);
            
            // Fetch other tickets (temporarily empty per user request)
            List<ServiceRequest> otherTickets = new java.util.ArrayList<>();
            model.addAttribute("otherTickets", otherTickets);
            
            // Add all IT tickets to model for the main IT Support tab
            model.addAttribute("allItTickets", allItTickets);
            
            // Dashboard Stats
            int totalTickets = allItTickets.size();
            int pendingCount = pendingTickets.size();
            int inProgressCount = (int) allItTickets.stream().filter(t -> "Assigned".equalsIgnoreCase(t.getStatus())).count();
            int resolvedTodayCount = (int) allItTickets.stream().filter(t -> "Close".equalsIgnoreCase(t.getStatus()) && java.time.LocalDate.now().equals(t.getActionDate())).count();
            String avgResolutionTime = "4.2 hrs"; // Mock for now
            
            model.addAttribute("totalTickets", totalTickets);
            model.addAttribute("pendingCount", pendingCount);
            model.addAttribute("inProgressCount", inProgressCount);
            model.addAttribute("resolvedTodayCount", resolvedTodayCount);
            model.addAttribute("avgResolutionTime", avgResolutionTime);
            
            // Category breakdown counts
            int softwareCount = (int) allItTickets.stream().filter(t -> t.getType().toLowerCase().contains("software")).count();
            int hardwareCount = (int) allItTickets.stream().filter(t -> t.getType().toLowerCase().contains("hardware")).count();
            int accessCount = (int) allItTickets.stream().filter(t -> t.getType().toLowerCase().contains("access") || t.getType().toLowerCase().contains("permission")).count();
            
            List<User> itSupportUsers = userRepository.findAll().stream()
                    .filter(u -> u.getRole() != null && "IT_SUPPORT".equalsIgnoreCase(u.getRole().getRoleName()))
                    .collect(Collectors.toList());
            model.addAttribute("itSupportUsers", itSupportUsers);
            int networkCount = (int) allItTickets.stream().filter(t -> t.getType().toLowerCase().contains("network") || t.getType().toLowerCase().contains("incident")).count();
            
            model.addAttribute("softwareCount", softwareCount);
            model.addAttribute("hardwareCount", hardwareCount);
            model.addAttribute("accessCount", accessCount);
            model.addAttribute("networkCount", networkCount);
            model.addAttribute("allItTickets", allItTickets);
        } else if (!teamUsernames.isEmpty()) {
            // Leaves
            pendingLeaves = leaveRequestRepository.findAll().stream()
                    .filter(l -> l.getUser() != null && teamUsernames.contains(l.getUser().getUsername()) && "Pending".equalsIgnoreCase(l.getStatus()))
                    .collect(Collectors.toList());
            approvedLeaves = leaveRequestRepository.findAll().stream()
                    .filter(l -> l.getUser() != null && teamUsernames.contains(l.getUser().getUsername()) && "Approved".equalsIgnoreCase(l.getStatus()))
                    .collect(Collectors.toList());
            rejectedLeaves = leaveRequestRepository.findAll().stream()
                    .filter(l -> l.getUser() != null && teamUsernames.contains(l.getUser().getUsername()) && "Rejected".equalsIgnoreCase(l.getStatus()))
                    .collect(Collectors.toList());

            // Timesheets
            pendingTimesheets = weeklyTimesheetRepository.findAll().stream()
                    .filter(t -> t.getUser() != null && teamUsernames.contains(t.getUser().getUsername())
                            && ("Pending".equalsIgnoreCase(t.getStatus()) || "Submitted".equalsIgnoreCase(t.getStatus())))
                    .collect(Collectors.toList());
            approvedTimesheets = weeklyTimesheetRepository.findAll().stream()
                    .filter(t -> t.getUser() != null && teamUsernames.contains(t.getUser().getUsername()) && "Approved".equalsIgnoreCase(t.getStatus()))
                    .collect(Collectors.toList());
            rejectedTimesheets = weeklyTimesheetRepository.findAll().stream()
                    .filter(t -> t.getUser() != null && teamUsernames.contains(t.getUser().getUsername()) && "Rejected".equalsIgnoreCase(t.getStatus()))
                    .collect(Collectors.toList());

            // Service Requests
            pendingTickets = serviceRequestRepository.findAll().stream()
                    .filter(t -> teamUsernames.contains(t.getEmployeeId()) && ("Open".equalsIgnoreCase(t.getStatus()) || "In Progress".equalsIgnoreCase(t.getStatus())))
                    .collect(Collectors.toList());
            
            // Resignations
            pendingResignations = resignationRequestRepository.findAll().stream()
                    .filter(r -> r.getEmployee() != null && teamUsernames.contains(r.getEmployee().getUsername()) && "PENDING_MANAGER".equals(r.getStatus()))
                    .collect(Collectors.toList());
            
            approvedTickets = serviceRequestRepository.findAll().stream()
                    .filter(t -> teamUsernames.contains(t.getEmployeeId()) && ("Approved".equalsIgnoreCase(t.getStatus()) || "Resolved".equalsIgnoreCase(t.getStatus())))
                    .collect(Collectors.toList());
            rejectedTickets = serviceRequestRepository.findAll().stream()
                    .filter(t -> teamUsernames.contains(t.getEmployeeId()) && "Rejected".equalsIgnoreCase(t.getStatus()))
                    .collect(Collectors.toList());
        }

        int totalPending = pendingLeaves.size() + pendingTimesheets.size() + pendingTickets.size();
        int totalApproved = approvedLeaves.size() + approvedTimesheets.size() + approvedTickets.size();
        int totalRejected = rejectedLeaves.size() + rejectedTimesheets.size() + rejectedTickets.size();

        model.addAttribute("pendingLeaves", pendingLeaves);
        model.addAttribute("pendingTimesheets", pendingTimesheets);
        model.addAttribute("pendingTickets", pendingTickets);
        model.addAttribute("pendingResignations", pendingResignations);
        model.addAttribute("approvedLeaves", approvedLeaves);
        model.addAttribute("approvedTimesheets", approvedTimesheets);
        model.addAttribute("approvedTickets", approvedTickets);
        model.addAttribute("rejectedLeaves", rejectedLeaves);
        model.addAttribute("rejectedTimesheets", rejectedTimesheets);
        model.addAttribute("rejectedTickets", rejectedTickets);
        
        // Combine all timesheet lists for "Recent Timesheets" table in the frontend
        java.util.List<WeeklyTimesheet> allTimesheets = new ArrayList<>();
        allTimesheets.addAll(pendingTimesheets);
        allTimesheets.addAll(approvedTimesheets);
        allTimesheets.addAll(rejectedTimesheets);
        model.addAttribute("allTimesheets", allTimesheets);
        model.addAttribute("totalPending", totalPending);
        model.addAttribute("totalApproved", totalApproved);
        model.addAttribute("totalRejected", totalRejected);
        
        // Fetch L1/L2 Support users for genuine assignment dropdown
        java.util.List<com.example.admindashboard.model.User> assignableUsers = userRepository.findAll().stream()
                .filter(u -> {
                    if (u.getRole() == null || !"ACTIVE".equals(u.getStatus())) return false;
                    String r = u.getRole().getRoleName();
                    // Using Admin/HR/IT roles as the genuine "Level 1 / Level 2" support assignees
                    return "ROLE_SUPER_ADMIN".equals(r) || "ROLE_ADMIN".equals(r) || "ROLE_HR_ADMIN".equals(r) || "ROLE_IT_ADMIN".equals(r);
                })
                .collect(Collectors.toList());
        model.addAttribute("assignableUsers", assignableUsers);
        
        model.addAttribute("user", currentUser);
        model.addAttribute("isItSupport", isItSupport);

        String uri = request.getRequestURI().toLowerCase();
        if (uri.contains("/senior_")) {
            String[] parts = request.getRequestURI().split("/");
            if (parts.length >= 2) {
                model.addAttribute("backUrl", "/" + parts[1] + "/dashboard");
            }
        }

        if (uri.contains("accounts")) {
            java.util.List<ServiceRequest> allAccountsTickets = serviceRequestRepository.findAll().stream()
                    .filter(t -> "PAYROLL".equalsIgnoreCase(t.getType()) || "FINANCE".equalsIgnoreCase(t.getType()))
                    .collect(Collectors.toList());

            java.util.List<ServiceRequest> pendingAccountsTickets = allAccountsTickets.stream()
                    .filter(t -> "Open".equalsIgnoreCase(t.getStatus()) || "Assigned".equalsIgnoreCase(t.getStatus()) || "Close".equalsIgnoreCase(t.getStatus()))
                    .collect(Collectors.toList());

            model.addAttribute("allItTickets", allAccountsTickets);
            model.addAttribute("pendingTickets", pendingAccountsTickets);
            model.addAttribute("otherTickets", new java.util.ArrayList<>());
            model.addAttribute("isItSupport", true); // To trigger the UI list rendering correctly

            return "accounts-workflow";
        } else if (uri.contains("transportation") || uri.contains("transport")) {
            java.util.List<ServiceRequest> allTransportTickets = serviceRequestRepository.findAll().stream()
                    .filter(t -> "TRANSPORT".equalsIgnoreCase(t.getType()) || "TRANSPORTATION".equalsIgnoreCase(t.getType()) || "FACILITY".equalsIgnoreCase(t.getType()))
                    .collect(Collectors.toList());

            java.util.List<ServiceRequest> pendingTransportTickets = allTransportTickets.stream()
                    .filter(t -> "Open".equalsIgnoreCase(t.getStatus()) || "Assigned".equalsIgnoreCase(t.getStatus()) || "Close".equalsIgnoreCase(t.getStatus()))
                    .collect(Collectors.toList());

            model.addAttribute("allItTickets", allTransportTickets);
            model.addAttribute("pendingTickets", pendingTransportTickets);
            model.addAttribute("otherTickets", new java.util.ArrayList<>());
            model.addAttribute("isItSupport", true); // To trigger the UI list rendering correctly

            return "transport-workflow";
        } else if (uri.contains("hr")) {
            
            // 1. HR Tickets (Pending) -> Used for 'HR Tickets' tab
            java.util.List<ServiceRequest> hrTickets = serviceRequestRepository.findAll().stream()
                    .filter(t -> "HR".equalsIgnoreCase(t.getType()) || "HUMAN_RESOURCES".equalsIgnoreCase(t.getType()))
                    .collect(Collectors.toList());
                    
            java.util.List<ServiceRequest> pendingHrTickets = hrTickets.stream()
                    .filter(t -> "Open".equalsIgnoreCase(t.getStatus()) || "Assigned".equalsIgnoreCase(t.getStatus()))
                    .collect(Collectors.toList());

            // 2. Accounts/Finance Tickets -> Used for 'Accounts/Finance' tab
            java.util.List<ServiceRequest> accountsTickets = serviceRequestRepository.findAll().stream()
                    .filter(t -> "PAYROLL".equalsIgnoreCase(t.getType()) || "FINANCE".equalsIgnoreCase(t.getType()))
                    .collect(Collectors.toList());
                    
            // We'll pass HR tickets as 'pendingTickets' and Accounts tickets as 'accountsTickets'
            // We also need all globally pending leaves and timesheets!
            java.util.List<LeaveRequest> allPendingLeaves = leaveRequestRepository.findAll().stream()
                    .filter(l -> "PENDING".equalsIgnoreCase(l.getStatus()))
                    .collect(Collectors.toList());
                    
            java.util.List<WeeklyTimesheet> allPendingTimesheets = weeklyTimesheetRepository.findAll().stream()
                    .filter(t -> "Submitted".equalsIgnoreCase(t.getStatus()) || "Pending".equalsIgnoreCase(t.getStatus()))
                    .collect(Collectors.toList());

            model.addAttribute("pendingLeaves", allPendingLeaves);
            model.addAttribute("pendingTimesheets", allPendingTimesheets);
            
            // Reusing existing model names to avoid massive UI refactoring, we'll map them in HTML
            model.addAttribute("pendingTickets", pendingHrTickets);
            model.addAttribute("myAssignedTickets", accountsTickets); 
            model.addAttribute("otherTickets", new java.util.ArrayList<>());
            
            model.addAttribute("isItSupport", false); // HR workflow shows all tabs normally
            // For HR managers, resignation approvals are routed to them after reporting manager
            pendingResignations = resignationRequestRepository.findByStatus("PENDING_HR");
            model.addAttribute("pendingResignations", pendingResignations);

            return "hr-workflow";
        } else if (uri.contains("rewards")) {
            java.util.List<ServiceRequest> rewardsTickets = serviceRequestRepository.findAll().stream()
                    .filter(t -> "REWARDS".equalsIgnoreCase(t.getType()))
                    .collect(Collectors.toList());
            
            java.util.List<ServiceRequest> pendingRewardsTickets = rewardsTickets.stream()
                    .filter(t -> "Open".equalsIgnoreCase(t.getStatus()) || "Assigned".equalsIgnoreCase(t.getStatus()))
                    .collect(Collectors.toList());

            // Also pass all pending leaves and timesheets for Rewards manager as in HR
            java.util.List<LeaveRequest> allPendingLeaves = leaveRequestRepository.findAll().stream()
                    .filter(l -> "PENDING".equalsIgnoreCase(l.getStatus()))
                    .collect(Collectors.toList());
                    
            java.util.List<WeeklyTimesheet> allPendingTimesheets = weeklyTimesheetRepository.findAll().stream()
                    .filter(t -> "Submitted".equalsIgnoreCase(t.getStatus()) || "Pending".equalsIgnoreCase(t.getStatus()))
                    .collect(Collectors.toList());

            model.addAttribute("pendingLeaves", allPendingLeaves);
            model.addAttribute("pendingTimesheets", allPendingTimesheets);
            
            model.addAttribute("allItTickets", rewardsTickets);
            model.addAttribute("pendingTickets", pendingRewardsTickets);
            model.addAttribute("otherTickets", new java.util.ArrayList<>());
            model.addAttribute("isItSupport", false);

            return "rewards-workflow";
        }
        return "manager-workflow";
    }

    @GetMapping("/employee/dashboard")
    public String showEmployeeDashboard(Model model, Principal principal) {

        String currentUserId = principal.getName();

        List<ServiceRequest> recentTickets =
                serviceRequestRepository.findTop3ByEmployeeIdOrderByIdDesc(currentUserId);

        model.addAttribute("recentTickets", recentTickets);
        model.addAttribute("pendingMeetingInvites",
                getPendingMeetingInvites(currentUserId));

        User currentUser = userRepository.findByUsername(currentUserId)
                .orElse(null);

        boolean showMySpace = true;

        if (currentUser != null && currentUser.getRole() != null) {

            String roleName = currentUser.getRole().getRoleName();

            showMySpace =
                    "SENIOR_MANAGER".equals(roleName)
                    || "SENIOR_HR".equals(roleName)
                    || "SENIOR_LND_HEAD".equals(roleName)
                    || "SENIOR_ACCOUNTS_HEAD".equals(roleName)
                    || "SENIOR_TRANSPORT_HEAD".equals(roleName)
                    || "SENIOR_REWARDS_HEAD".equals(roleName);
        }
        System.out.println("================================");
        System.out.println("USERNAME = " + currentUserId);

        if(currentUser != null && currentUser.getRole() != null){
            System.out.println("ROLE = " + currentUser.getRole().getRoleName());
        }

        System.out.println("SHOW_MY_SPACE = " + showMySpace);
        System.out.println("================================");

        model.addAttribute("showMySpace", showMySpace);
        return "employee-dashboard";
    }

    @PostMapping("/HR/resignation/{id}/approve")
    public String approveResignationL2(@PathVariable("id") Long id, @RequestParam("noticePeriodDays") Integer noticePeriodDays, Principal principal) {
        if (principal != null) {
            User l2Hr = userRepository.findByUsername(principal.getName()).orElse(null);
            ResignationRequest req = resignationRequestRepository.findById(id).orElse(null);
            if (req != null && l2Hr != null && "PENDING_MANAGER".equals(req.getStatus())) {
                req.setNoticePeriodDays(noticePeriodDays);
                req.setStatus("PENDING_HR");
                req.setL2ApprovedBy(l2Hr);
                resignationRequestRepository.save(req);
                
                User employee = req.getEmployee();
                employee.setStatus("ON NOTICE");
                userRepository.save(employee);
            }
        }
        return "redirect:/HR/workflow";
    }

    @PostMapping("/HR/resignation/{id}/reject")
    public String rejectResignationL2(@PathVariable("id") Long id, Principal principal) {
        if (principal != null) {
            User l2Hr = userRepository.findByUsername(principal.getName()).orElse(null);
            ResignationRequest req = resignationRequestRepository.findById(id).orElse(null);
            if (req != null && l2Hr != null && "PENDING_MANAGER".equals(req.getStatus())) {
                req.setStatus("REJECTED");
                req.setL2ApprovedBy(l2Hr);
                resignationRequestRepository.save(req);
            }
        }
        return "redirect:/HR/workflow";
    }

    @GetMapping("/employee/resignation")
    public String showResignationPage(Model model, Principal principal) {
        if (principal != null) {
            User employee = userRepository.findByUsername(principal.getName()).orElse(null);
            if (employee != null) {
                com.example.admindashboard.model.EmployeeProfile profile = employeeProfileRepository.findByUser_Username(employee.getUsername()).orElse(null);
                model.addAttribute("profile", profile);
                
                // Find existing resignation if any
                java.util.List<ResignationRequest> reqs = resignationRequestRepository.findByEmployee_Username(employee.getUsername());
                ResignationRequest latestReq = null;
                if (!reqs.isEmpty()) {
                    // Assuming ordered by ID desc or just taking the first one
                    latestReq = reqs.get(0);
                }
                model.addAttribute("resignation", latestReq);
            }
        }
        return "employee-resignation";
    }

    @PostMapping("/employee/resignation/submit")
    public String submitResignation(@RequestParam("reason") String reason, 
                                    @RequestParam(value = "comments", required = false) String comments,
                                    @RequestParam("action") String action,
                                    Principal principal, RedirectAttributes redirectAttributes) {
        if (principal != null) {
            User employee = userRepository.findByUsername(principal.getName()).orElse(null);
            if (employee != null) {
                java.util.List<ResignationRequest> existingReqs = resignationRequestRepository.findByEmployee_Username(employee.getUsername());
                ResignationRequest req = existingReqs.isEmpty() ? new ResignationRequest() : existingReqs.get(0);
                
                req.setEmployee(employee);
                req.setReason(reason);
                req.setComments(comments);
                req.setRequestDate(java.time.LocalDate.now());
                
                if ("draft".equals(action)) {
                    req.setStatus("DRAFT");
                    redirectAttributes.addFlashAttribute("successMessage", "Resignation saved as draft.");
                } else {
                    req.setStatus("PENDING_MANAGER");
                    redirectAttributes.addFlashAttribute("successMessage", "Resignation submitted successfully. Forwarded to Manager.");
                }
                resignationRequestRepository.save(req);
            }
        }
        return "redirect:/employee/resignation";
    }

    @GetMapping("/employee/referral")
    public String showReferralPage(org.springframework.ui.Model model, Principal principal) {
        User currentUser = userRepository.findByUsername(principal.getName()).orElse(null);
        if (currentUser == null) return "redirect:/login";
        
        // Seed some mock data if job postings are empty
        if (jobPostingRepository.count() == 0) {
            JobPosting p1 = new JobPosting();
            p1.setJobId("J001");
            p1.setTitle("Associate Engineer - Product and Platform Engineering");
            p1.setDepartment("Product and Platform Engineering");
            p1.setExperienceRequired("0 - 2 years");
            p1.setLocation("Bangalore");
            p1.setPostingDate(java.time.LocalDate.now());
            p1.setDescription("Extensive experience in Java programming, demonstrating advanced proficiency in developing scalable applications...");
            p1.setPrimarySkills("Java Backend, Java, Python");
            p1.setSecondarySkills("Java + spring boot + Microservices, SQL");
            p1.setJobOverview("JD Focus: Strong CS fundamentals, coding, and data structures skills - Freshers from IITs, NITs, BITS, IIITs, and Other Premier Institutes only.");
            p1.setEligibilityCriteria("Candidates must have a CGPA of 7.5 and above. CGPA score is mandatory on the resume. Profiles without CGPA mentioned will not be considered.");
            jobPostingRepository.save(p1);
            
            JobPosting p2 = new JobPosting();
            p2.setJobId("J002");
            p2.setTitle("Senior Frontend Developer");
            p2.setDepartment("UI/UX Engineering");
            p2.setExperienceRequired("4 - 6 years");
            p2.setLocation("Pune / Remote");
            p2.setPostingDate(java.time.LocalDate.now().minusDays(5));
            p2.setDescription("Looking for an experienced React/Angular developer to lead our frontend initiatives.");
            p2.setPrimarySkills("React, Angular, TypeScript");
            p2.setSecondarySkills("Redux, RxJS, HTML/CSS");
            p2.setJobOverview("Lead the development of next-gen web applications.");
            p2.setEligibilityCriteria("B.Tech/MCA with minimum 4 years of frontend experience.");
            jobPostingRepository.save(p2);
        }

        java.util.List<JobPosting> activeJobs = jobPostingRepository.findByIsActiveTrue();
        java.util.List<Referral> myReferrals = referralRepository.findByReferredByIdOrderByIdDesc(currentUser.getId());
        
        model.addAttribute("activeJobs", activeJobs);
        model.addAttribute("myReferrals", myReferrals);
        model.addAttribute("user", currentUser);
        model.addAttribute("backUrl", "/employee/dashboard");
        return "employee-referral";
    }

    @PostMapping("/employee/referral/submit")
    public String submitReferral(
            @RequestParam("jobPostingId") Long jobPostingId,
            @RequestParam("firstName") String firstName,
            @RequestParam("lastName") String lastName,
            @RequestParam("email") String email,
            @RequestParam("countryCode") String countryCode,
            @RequestParam("mobileNumber") String mobileNumber,
            @RequestParam("relationship") String relationship,
            @RequestParam("resumeFile") org.springframework.web.multipart.MultipartFile resumeFile,
            Principal principal,
            RedirectAttributes redirectAttributes) {

        User currentUser = userRepository.findByUsername(principal.getName()).orElse(null);
        JobPosting job = jobPostingRepository.findById(jobPostingId).orElse(null);

        if (currentUser != null && job != null) {
            Referral ref = new Referral();
            ref.setJobPosting(job);
            ref.setReferredBy(currentUser);
            ref.setFirstName(firstName);
            ref.setLastName(lastName);
            ref.setEmail(email);
            ref.setCountryCode(countryCode);
            ref.setMobileNumber(mobileNumber);
            ref.setRelationship(relationship);
            ref.setReferralDate(java.time.LocalDateTime.now());
            
            if (!resumeFile.isEmpty()) {
                try {
                    String uploadsDir = "uploads/resumes/";
                    java.io.File dir = new java.io.File(uploadsDir);
                    if (!dir.exists()) dir.mkdirs();
                    
                    String originalName = resumeFile.getOriginalFilename();
                    String ext = originalName.substring(originalName.lastIndexOf("."));
                    String newFilename = java.util.UUID.randomUUID().toString() + ext;
                    
                    java.nio.file.Path path = java.nio.file.Paths.get(uploadsDir + newFilename);
                    java.nio.file.Files.copy(resumeFile.getInputStream(), path, java.nio.file.StandardCopyOption.REPLACE_EXISTING);
                    
                    ref.setResumeFilename(newFilename);
                } catch (Exception e) {
                    e.printStackTrace();
                    redirectAttributes.addFlashAttribute("errorMessage", "Failed to upload resume.");
                    return "redirect:/employee/referral";
                }
            }
            
            referralRepository.save(ref);
            
            // --- EMAIL TRIGGER START ---
            try {
                java.util.Map<String, Object> emailData = new java.util.HashMap<>();
                emailData.put("candidateName", firstName + " " + lastName);
                emailData.put("employeeName", currentUser.getFullName());
                emailData.put("jobTitle", job.getTitle());
                emailData.put("companyName", "WhiteCircle");
                
                emailService.sendReferralEmailToCandidate(email, firstName + " " + lastName, currentUser.getFullName(), emailData);
            } catch (Exception e) {
                System.err.println("Warning: Could not trigger Candidate Referral email: " + e.getMessage());
            }
            // --- EMAIL TRIGGER END ---
            
            redirectAttributes.addFlashAttribute("successMessage", "You have referred " + firstName + " " + lastName + " successfully!");
        }
        
        return "redirect:/employee/referral";
    }

    // --- EMPLOYEE PROFILE SECTION (Self-Service - No locks needed) ---

    @GetMapping("/employee/profile")
    public String viewProfile(Model model, Principal principal) {

        String username = principal.getName();

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        
        if (user.getEmployeeProfile() == null) {
            user.setEmployeeProfile(new EmployeeProfile());
        }

        model.addAttribute("user", user);
        model.addAttribute("employeeProfile", user.getEmployeeProfile());

        return "employee-profile";
    }

    @GetMapping("/my-profile")
    public String showProfilePage() { return "my-profile"; }

    @GetMapping("/employee/full-profile")
    public String showFullProfile(Model model, Principal principal) {
        String username = principal.getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (user.getEmployeeProfile() == null) {
            user.setEmployeeProfile(new EmployeeProfile());
        }
        model.addAttribute("user", user);
        return "full-profile";
    }

    @PostMapping("/employee/profile/save-detailed")
    public String saveDetailedProfile(
    		 @ModelAttribute EmployeeProfile formProfile,
            @RequestParam(value = "mobileNumber", required = false) String mobileNumber,
            @RequestParam(value = "city", required = false) String city,
            @RequestParam(value = "country", required = false) String country,
            @RequestParam(value = "experience", required = false) String experience,
            @RequestParam(value = "joiningDate", required = false) LocalDate joiningDate,
            @RequestParam(value = "returnUrl", defaultValue = "/employee/profile") String returnUrl,
            Principal principal,
            RedirectAttributes redirectAttributes, Model model) {

        String username = principal.getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        

        EmployeeProfile existingProfile = user.getEmployeeProfile();
        if (existingProfile == null) {
            existingProfile = new EmployeeProfile();
            existingProfile.setUser(user);
        }

        if (mobileNumber != null) existingProfile.setMobileNumber(mobileNumber.trim());
        if (city != null) existingProfile.setCity(city.trim());
        if (country != null) existingProfile.setCountry(country.trim());
        if (experience != null) existingProfile.setExperience(experience.trim());
        if (joiningDate != null) existingProfile.setJoiningDate(joiningDate);

        existingProfile.setDob(formProfile.getDob());
        existingProfile.setGender(formProfile.getGender());
        existingProfile.setPersonalEmail(formProfile.getPersonalEmail());
        existingProfile.setAadharNo(formProfile.getAadharNo());
        existingProfile.setPanNo(formProfile.getPanNo());
        existingProfile.setPermanentAddress(formProfile.getPermanentAddress());
        existingProfile.setWorkingAddress(formProfile.getWorkingAddress());

        existingProfile.setQual1Title(formProfile.getQual1Title());
        existingProfile.setQual1Inst(formProfile.getQual1Inst());
        existingProfile.setQual1Year(formProfile.getQual1Year());
        existingProfile.setQual2Title(formProfile.getQual2Title());
        existingProfile.setQual2Inst(formProfile.getQual2Inst());
        existingProfile.setQual2Year(formProfile.getQual2Year());

        existingProfile.setEmergencyContactName(formProfile.getEmergencyContactName());
        existingProfile.setRelationWithEmployee(formProfile.getRelationWithEmployee());
        existingProfile.setEmergencyPhone(formProfile.getEmergencyPhone());
        existingProfile.setAltMobile(formProfile.getAltMobile());

     // Bank Details
        existingProfile.setBankAccountHolder(formProfile.getBankAccountHolder());
        existingProfile.setBankAccountNumber(formProfile.getBankAccountNumber());
        existingProfile.setBankIfscCode(formProfile.getBankIfscCode());
        existingProfile.setBankName(formProfile.getBankName());
        existingProfile.setBankBranch(formProfile.getBankBranch());
        existingProfile.setBankAccountType(formProfile.getBankAccountType());


        // NEW PROFILE FIELDS

        existingProfile.setSpouseName(formProfile.getSpouseName());
        existingProfile.setFatherName(formProfile.getFatherName());
        existingProfile.setMotherName(formProfile.getMotherName());
        existingProfile.setSalaryDate(formProfile.getSalaryDate());
        existingProfile.setProbationPeriod(formProfile.getProbationPeriod());
        existingProfile.setMaritalStatus(formProfile.getMaritalStatus());
        existingProfile.setNotes(formProfile.getNotes());

        existingProfile.setBranch(formProfile.getBranch());
        existingProfile.setDesignation(formProfile.getDesignation());
        existingProfile.setSalaryStructure(formProfile.getSalaryStructure());
        existingProfile.setLeavePolicy(formProfile.getLeavePolicy());
        existingProfile.setAttendanceStructure(formProfile.getAttendanceStructure());
        existingProfile.setTimesheetPolicy(formProfile.getTimesheetPolicy());
        existingProfile.setDepartment(formProfile.getDepartment());
        existingProfile.setTaPolicy(formProfile.getTaPolicy());
        existingProfile.setCategory(formProfile.getCategory());

        existingProfile.setPfNumber(formProfile.getPfNumber());
        existingProfile.setPassportNumber(formProfile.getPassportNumber());
        existingProfile.setEsiNumber(formProfile.getEsiNumber());
        existingProfile.setUanNumber(formProfile.getUanNumber());

        existingProfile.setBloodGroup(formProfile.getBloodGroup());
        existingProfile.setCasteCategory(formProfile.getCasteCategory());
        existingProfile.setQualification(formProfile.getQualification());
        existingProfile.setCloseFriendName(formProfile.getCloseFriendName());
        existingProfile.setDrivingLicenseNo(formProfile.getDrivingLicenseNo());
        existingProfile.setNationality(formProfile.getNationality());
        
     // Present Address
        existingProfile.setPresentResidentialName(formProfile.getPresentResidentialName());
        existingProfile.setPresentStreet(formProfile.getPresentStreet());
        existingProfile.setPresentArea(formProfile.getPresentArea());
        existingProfile.setPresentCity(formProfile.getPresentCity());
        existingProfile.setPresentState(formProfile.getPresentState());
        existingProfile.setPresentPincode(formProfile.getPresentPincode());

        // Permanent Address
        existingProfile.setPermanentResidentialName(formProfile.getPermanentResidentialName());
        existingProfile.setPermanentStreet(formProfile.getPermanentStreet());
        existingProfile.setPermanentArea(formProfile.getPermanentArea());
        existingProfile.setPermanentCity(formProfile.getPermanentCity());
        existingProfile.setPermanentState(formProfile.getPermanentState());
        existingProfile.setPermanentPincode(formProfile.getPermanentPincode());

        // Emails
        existingProfile.setOfficialEmail(formProfile.getOfficialEmail());
        existingProfile.setAlternateEmail(formProfile.getAlternateEmail());

        user.setEmployeeProfile(existingProfile);
        userRepository.save(user);

        redirectAttributes.addFlashAttribute("successMessage", "Profile updated successfully!");
        return "redirect:" + returnUrl;
    }

    @GetMapping("/employee/profile/edit")
    public String showEditMyProfileForm(Principal principal, Model model) {
        User currentEmployee = userService.findByUsername(principal.getName());
        if (currentEmployee.getEmployeeProfile() == null) {
            EmployeeProfile newProfile = new EmployeeProfile();
            newProfile.setUser(currentEmployee);
            currentEmployee.setEmployeeProfile(newProfile);
        }
        model.addAttribute("employee", currentEmployee);
        return "edit-my-profile";
    }

    @PostMapping("/employee/profile/edit")
    public String updateMyProfile(@ModelAttribute("employee") User updatedEmployee,
                                  Principal principal,
                                  RedirectAttributes redirectAttributes) {

        userService.updateEmployeePersonalDetails(principal.getName(), updatedEmployee);
        redirectAttributes.addFlashAttribute("successMessage", "Your personal details have been updated successfully!");
        return "redirect:/employee/profile";
    }

    // --- VARIOUS EMPLOYEE PAGES ---

    @GetMapping("/conference-room")
    public String showConferencePage(Model model, Principal principal, Authentication authentication) {
        String currentUsername = principal.getName();
        User currentUser = userRepository.findByUsername(currentUsername).orElse(null);

        List<Meeting> allUpcomingMeetings = meetingRepository
                .findByMeetingDateGreaterThanEqualOrderByMeetingDateAscStartTimeAsc(LocalDate.now())
                .stream()
                .filter(m -> !(m.getMeetingDate().isEqual(LocalDate.now()) && m.getEndTime().isBefore(java.time.LocalTime.now())))
                .collect(java.util.stream.Collectors.toList());

        List<Meeting> myBookings = allUpcomingMeetings.stream()
                .filter(meeting -> meeting.getOrganizer().getUsername().equals(currentUsername))
                .toList();

        List<Meeting> upcomingMeetings = allUpcomingMeetings.stream().filter(meeting -> {
            // Only show CONFIRMED meetings in the schedule
            if (!"CONFIRMED".equals(meeting.getStatus())) return false;

            if (meeting.getSpecificEmployeeIds() != null && meeting.getSpecificEmployeeIds().contains(currentUsername)) return true;

            EmployeeProfile myProfile = currentUser != null ? currentUser.getEmployeeProfile() : null;
            EmployeeProfile organizerProfile = meeting.getOrganizer() != null ? meeting.getOrganizer().getEmployeeProfile() : null;

            if ("TEAM".equals(meeting.getParticipantType()) && myProfile != null && myProfile.getBusinessUnit() != null) {
                if (organizerProfile != null && myProfile.getBusinessUnit().equals(organizerProfile.getBusinessUnit())) {
                    return true;
                }
            }
            return false;
        }).toList();

        // FIXED: Dynamic Routing Logic for the "Back" Button
        String backUrl = "/default-redirect"; // Let the centralized redirect handler manage role-based routing

        model.addAttribute("myBookings", myBookings);
        model.addAttribute("upcomingMeetings", upcomingMeetings);
        model.addAttribute("user", currentUser);
        model.addAttribute("allUsers", userRepository.findAll());
        model.addAttribute("backUrl", backUrl); // Send the dynamic URL to the HTML page

        return "conference-room";
    }

    @GetMapping("/apply-leave")
    public String showApplyLeavePage(Model model, Principal principal) {
        String username = principal.getName();
        User currentUser = userRepository.findByUsername(username).orElse(null);

        if (currentUser == null) {
            return "redirect:/login";
        }

        model.addAttribute("user", currentUser);
        return "apply-leave";
    }

    @GetMapping("/leave-management")
    public String showLeaveManagement(Model model, Principal principal) {
        String username = principal.getName();
        User currentUser = userRepository.findByUsername(username).orElse(null);
        if (currentUser == null) {
            return "redirect:/login";
        }
        model.addAttribute("user", currentUser);
        return "leave-management";
    }

    @GetMapping("/attendance")
    public String showAttendanceRegulation(Model model, Principal principal) {
        String username = principal.getName();
        User user = userRepository.findByUsername(username).orElse(null);
        model.addAttribute("user", user);
        model.addAttribute("currentWeek", LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-'W'ww")));
        return "attendance";
    }

    @GetMapping("/employee/my-timesheets")
    public String showMyTimesheets() { return "my-timesheets"; }

    @GetMapping("/email-signature")
    public String showEmailSignaturePage(Model model, Principal principal) {
        String username = principal.getName();
        User currentUser = userRepository.findByUsername(username).orElse(new User());
        model.addAttribute("user", currentUser);
        return "email-signature";
    }

    @GetMapping("/password-reset")
    public String showPasswordResetPage(jakarta.servlet.http.HttpServletRequest request, org.springframework.ui.Model model) {
        String referer = request.getHeader("Referer");
        if (referer != null && !referer.isEmpty()) {
            model.addAttribute("backUrl", referer);
        } else {
            model.addAttribute("backUrl", "/employee/dashboard");
        }
        return "password-reset";
    }

    @GetMapping("/forgot-password")
    public String showForgotPasswordPage() {
        return "forgot-password";
    }

    @PostMapping("/forgot-password")
    public String processForgotPassword(@RequestParam("email") String email, RedirectAttributes redirectAttributes) {
        // Mock sending email
        redirectAttributes.addFlashAttribute("success", "A password reset link has been sent to " + email);
        return "redirect:/forgot-password";
    }

    @GetMapping("/my-whitecircle/login")
    public String showMyWhiteCircle(Model model, Principal principal) { 
        if (principal != null) {
            String username = principal.getName();
            User currentUser = userRepository.findByUsername(username).orElse(new User());
            
            model.addAttribute("user", currentUser);
            model.addAttribute("savedPassword", currentUser.getPassword()); 
        } else {
            model.addAttribute("user", new User());
            model.addAttribute("savedPassword", "");
        }
        // 1. Shows your crisp split login screen first when clicked from the dashboard
        return "my-whitecircle-login"; 
    }
    @GetMapping("/my-whitecircle")
    public String showMyWhiteCircleDashboard() {
        return "my-whitecircle";
    }

    @PostMapping("/my-whitecircle/login")
    public String processMyWhiteCircleLogin(
            @RequestParam("username") String typedUsername,
            @RequestParam("password") String typedPassword,
            Model model,
            Principal principal,
            HttpSession session) {

        if (principal != null) {

            String loginId = principal.getName();

            User currentUser = userRepository.findByUsername(loginId)
                    .orElse(new User());

            String dbPassword = currentUser.getPassword();

            String cleanDbPassword =
                    dbPassword != null
                            ? dbPassword.replace("{noop}", "")
                            : "";

            if (!typedUsername.equalsIgnoreCase(loginId)
                    || !typedPassword.equals(cleanDbPassword)) {

                model.addAttribute("user", currentUser);
                model.addAttribute("savedPassword", cleanDbPassword);
                model.addAttribute("authError", "Invalid credentials");

                return "my-whitecircle-login";
            }

            // SUCCESS LOGIN
            session.setAttribute("loggedInUser", currentUser);
        }

        return "redirect:/my-whitecircle";
    }
    @GetMapping("/coming-soon")
    public String comingSoonPage() {
        return "work-in-progress"; // Work In Progress page for static cards
    }

    @GetMapping("/erp/authenticate")
    public String showErpTimesheetLoginGate(Model model, Principal principal) {
        if (principal != null) {
            String loginId = principal.getName();
            User currentUser = userRepository.findByUsername(loginId).orElse(new User());
            
            model.addAttribute("user", currentUser);
            model.addAttribute("savedPassword", currentUser.getPassword() != null ? currentUser.getPassword() : ""); 
        } else {
            model.addAttribute("user", new User());
            model.addAttribute("savedPassword", "");
        }
        // Always force the split login page view first
        return "erp-login"; 
    }

    @GetMapping("/erp/logout")
    public String erpLogout(jakarta.servlet.http.HttpServletRequest request, jakarta.servlet.http.HttpServletResponse response, org.springframework.security.core.Authentication auth) {
        if (auth != null) {
            new org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler().logout(request, response, auth);
        }
        return "redirect:/erp/authenticate";
    }
    @GetMapping("/erp-timesheet")
    public String erpTimesheetHub(
            Model model,
            Authentication authentication) {

        String backUrl = "/employee/dashboard";

        if (authentication != null &&
                authentication.getAuthorities().stream()
                        .anyMatch(a ->
                                a.getAuthority().equals("admin_dashboard_view"))) {

            backUrl = "/admin/dashboard";
        }

        model.addAttribute("backUrl", backUrl);

        if (authentication != null) {
            userRepository.findByUsername(authentication.getName())
                    .ifPresent(user -> model.addAttribute("user", user));
        }

        model.addAttribute("activeProjects", new ArrayList<>());
        model.addAttribute("submittedTimesheets", new ArrayList<>());

        return "erp-and-timesheet";
    }
    @GetMapping("/erp/back")
    public String erpBack(Authentication authentication) {

        if (authentication != null &&
            authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("admin_dashboard_view"))) {

            return "redirect:/admin/dashboard";
        }

        return "redirect:/employee/dashboard";
    }
    
    @PostMapping("/erp-timesheet")
    public String processErpTimesheetAuthentication(
            @RequestParam("username") String typedUsername,
            @RequestParam("password") String typedPassword,
            Model model, 
            Principal principal,
            jakarta.servlet.http.HttpServletRequest request,
            RedirectAttributes redirectAttributes) {

        Optional<User> optionalUser = userRepository.findByUsername(typedUsername);
        if (optionalUser.isPresent()) {
            User targetUser = optionalUser.get();
            String dbPassword = targetUser.getPassword() != null ? targetUser.getPassword().replace("{noop}", "") : "";
            
            if (dbPassword.equals(typedPassword)) {
                System.out.println("[ERP-PORTAL] Verification successful for user: " + typedUsername);
                
                // Swap the security context globally so the user is actually changed
                org.springframework.security.core.userdetails.UserDetails userDetails = customUserDetailsService.loadUserByUsername(typedUsername);
                org.springframework.security.authentication.UsernamePasswordAuthenticationToken newAuth = 
                    new org.springframework.security.authentication.UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());
                org.springframework.security.core.context.SecurityContextHolder.getContext().setAuthentication(newAuth);
                
                // Save context in session
                request.getSession().setAttribute(
                    org.springframework.security.web.context.HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY, 
                    org.springframework.security.core.context.SecurityContextHolder.getContext()
                );
                
                model.addAttribute("user", targetUser);
                model.addAttribute("activeProjects", new ArrayList<>());
                model.addAttribute("submittedTimesheets", new ArrayList<>());
                
                String backUrl = "/employee/dashboard";

                if (userDetails.getAuthorities().stream()
                        .anyMatch(a -> a.getAuthority().equals("admin_dashboard_view"))) {

                    backUrl = "/admin/dashboard";
                }

                model.addAttribute("backUrl", backUrl);
                
                return "erp-and-timesheet"; 
            }
        }

        System.out.println("[ERP-PORTAL] FAILED verification attempt for user: " + typedUsername);
        
        // Send back an error signal to the frontend login view
        User currentUser = principal != null ? userRepository.findByUsername(principal.getName()).orElse(new User()) : new User();
        model.addAttribute("user", currentUser);
        model.addAttribute("authError", "Invalid credentials. Access Denied.");
        
        return "erp-login"; 
    }

    @GetMapping("/employee/create-timesheet")
    public String showCreateTimesheet() { return "create-timesheet"; }

    @GetMapping("/employee/daily-timecard")
    public String showDailyTimecard() { return "daily-timecard"; }

    @GetMapping("/employee/weekly-timecard")
    public String showWeeklyTimecard() { return "weekly-timecard"; }

    @GetMapping("/employee/monthly-timecard")
    public String showMonthlyTimecard() { return "monthly-timecard"; }

    @GetMapping("/employee/timesheet-report")
    public String showTimesheetReport() { return "timesheet-report"; }

    @GetMapping("/my-timeoff")
    public String showMyTimeoff(Model model, Principal principal) {
        if (principal != null) {
            String loginId = principal.getName();
            User currentUser = userRepository.findByUsername(loginId).orElse(new User());
            model.addAttribute("user", currentUser);
        }
        return "my-timeoff";
    }

 // 1. THIS ALWAYS SHOWS THE LOGIN GATE FIRST WHEN CLICKED FROM THE MAIN DASHBOARD
    @GetMapping("/tickets/authenticate")
    public String showTicketLoginGate(Model model, Principal principal) {
        if (principal != null) {
            String loginId = principal.getName();
            User currentUser = userRepository.findByUsername(loginId).orElse(new User());
            
            model.addAttribute("user", currentUser);
            model.addAttribute("savedPassword", currentUser.getPassword()); 
        } else {
            model.addAttribute("user", new User());
            model.addAttribute("savedPassword", "");
        }
        // Always force the split login page view first
        return "ticket-login"; 
    }

    // 2. THE POST ROUTE TARGETS TICKETS.HTML ONCE THEY CLICK CONTINUE
    @PostMapping("/tickets")
    public String processTicketAuthentication(@RequestParam("username") String typedUsername, @RequestParam("password") String typedPassword, Model model, Principal principal) {
        if (principal != null) {
            String loginId = principal.getName();
            User currentUser = userRepository.findByUsername(loginId).orElse(new User());
            model.addAttribute("user", currentUser);
            String dbPassword = currentUser.getPassword();

            String cleanDbPassword =
                    dbPassword != null ? dbPassword.replace("{noop}", "") : "";

            if (!typedUsername.equalsIgnoreCase(loginId)
                    || !typedPassword.equals(cleanDbPassword)) {

                model.addAttribute("user", currentUser);
                model.addAttribute("savedPassword", cleanDbPassword);
                model.addAttribute("authError", "Invalid credentials");

                return "ticket-login";
            }

            boolean isRoleUser =
                    currentUser.getRole() != null &&
                    currentUser.getRole().getPermissions().stream()
                            .anyMatch(p -> "admin_dashboard_view".equals(p.getPermissionName()));

            if (isRoleUser) {
                return "role-tickets";
            }

            List<ServiceRequest> userRequests =
                    serviceRequestRepository.findByEmployeeIdOrderBySubmissionDateDesc(loginId);
            model.addAttribute("myRequests", userRequests);
        }

        return "tickets";
    }
    // 3. NEW STEP: A SPECIFIC ROUTE FOR THE BACK BUTTON TO BYPASS THE GATE SECURELY
    @GetMapping("/tickets/hub")
    public String backToTicketsHub(Model model, Principal principal) {
        if (principal != null) {
            String loginId = principal.getName();
            User currentUser = userRepository.findByUsername(loginId).orElse(new User());
            model.addAttribute("user", currentUser);
        }
        // Directly renders the tickets.html screen without showing the gate
        return "tickets";
    }
    @GetMapping("/ticket-dashboard")
    public String showTicketDashboard(@RequestParam(name = "dept", required = false, defaultValue = "IT") String dept, 
                                      Model model, Principal principal) {
        if (principal != null) {
            String loginId = principal.getName();
            User currentUser = userRepository.findByUsername(loginId).orElse(new User());
            model.addAttribute("user", currentUser);
            
            // Fetch all requests for the user from your database baseline
            List<ServiceRequest> allUserRequests = serviceRequestRepository.findByEmployeeIdOrderBySubmissionDateDesc(loginId);
            
            // FILTER logic: Only show tickets in the activity table that match the clicked department context
            List<ServiceRequest> filteredRequests = allUserRequests.stream()
                    .filter(req -> {
                        if (req.getType() == null) return false;
                        
                        switch (dept.toUpperCase()) {
                            case "HR":
                                return "HR".equalsIgnoreCase(req.getType());
                            case "FACILITIES":
                                return "FACILITIES".equalsIgnoreCase(req.getType()) || "HARDWARE".equalsIgnoreCase(req.getType());
                            case "PAYROLL":
                                return "PAYROLL".equalsIgnoreCase(req.getType()) || "FINANCE".equalsIgnoreCase(req.getType());
                            case "ALUMNI":
                                return "ALUMNI".equalsIgnoreCase(req.getType());
                            case "ENTERPRISE":
                                return "ENTERPRISE".equalsIgnoreCase(req.getType()) || "ACCESS".equalsIgnoreCase(req.getType());
                            case "LEARNING":
                                return "LEARNING".equalsIgnoreCase(req.getType()) || "TRAINING".equalsIgnoreCase(req.getType());
                            case "IT":
                            default:
                                return "IT".equalsIgnoreCase(req.getType()) || "SOFTWARE".equalsIgnoreCase(req.getType());
                        }
                    })
                    .collect(Collectors.toList());
            
            model.addAttribute("myRequests", filteredRequests);
        } else {
            model.addAttribute("user", new User());
            model.addAttribute("myRequests", new ArrayList<ServiceRequest>());
        }
        
        // CRITICAL: This sends the context string to ticket-dashboard.html
        model.addAttribute("currentDept", dept.toUpperCase());
        return "ticket-dashboard";
    }
    
    @GetMapping("/role-ticket-dashboard")
    public String showRoleTicketDashboard(
            @RequestParam(name = "dept", required = false, defaultValue = "IT") String dept,
            Model model,
            Principal principal) {

        String loginId = principal.getName();

        List<ServiceRequest> allUserRequests =
                serviceRequestRepository
                        .findByEmployeeIdOrderBySubmissionDateDesc(loginId);

        List<ServiceRequest> filteredRequests = allUserRequests.stream()
                .filter(req -> {
                    if (req.getType() == null) return false;

                    switch (dept.toUpperCase()) {
                        case "HR":
                            return "HR".equalsIgnoreCase(req.getType());

                        case "FACILITIES":
                            return "FACILITIES".equalsIgnoreCase(req.getType())
                                    || "HARDWARE".equalsIgnoreCase(req.getType());

                        case "PAYROLL":
                            return "PAYROLL".equalsIgnoreCase(req.getType())
                                    || "FINANCE".equalsIgnoreCase(req.getType());

                        case "ALUMNI":
                            return "ALUMNI".equalsIgnoreCase(req.getType());

                        case "ENTERPRISE":
                            return "ENTERPRISE".equalsIgnoreCase(req.getType())
                                    || "ACCESS".equalsIgnoreCase(req.getType());

                        case "LEARNING":
                            return "LEARNING".equalsIgnoreCase(req.getType())
                                    || "TRAINING".equalsIgnoreCase(req.getType());

                        case "IT":
                        default:
                            return "IT".equalsIgnoreCase(req.getType())
                                    || "SOFTWARE".equalsIgnoreCase(req.getType());
                    }
                })
                .collect(Collectors.toList());

        model.addAttribute("myRequests", filteredRequests);
        model.addAttribute("currentDept", dept.toUpperCase());

        return "role-ticket-dashboard";
    }
    
    @GetMapping("/role-tickets")
    public String roleTicketsHub(Model model, Principal principal) {

        if (principal != null) {
            String loginId = principal.getName();
            User currentUser = userRepository.findByUsername(loginId).orElse(new User());
            model.addAttribute("user", currentUser);
        }

        return "role-tickets";
    }
    @GetMapping("/role-tickets/hub")
    public String roleTicketsHubPage(Model model, Principal principal) {

        if (principal != null) {
            String loginId = principal.getName();
            User currentUser = userRepository.findByUsername(loginId)
                    .orElse(new User());

            model.addAttribute("user", currentUser);
        }

        return "role-tickets";
    }
    
    @GetMapping("/role-ticket-management")
    public String showRoleTicketManagement(Model model,
                                           Principal principal) {

        String loginId = principal.getName();

        User currentUser =
                userRepository.findByUsername(loginId)
                        .orElse(new User());

        String roleName =
                currentUser.getRole() != null
                        ? currentUser.getRole().getRoleName()
                        : "";

        List<ServiceRequest> tickets;

        if ("IT_SUPPORT".equalsIgnoreCase(roleName)) {

            tickets = serviceRequestRepository
                    .findByTypeOrderByIdDesc("IT");

        } else if ("FINANCE".equalsIgnoreCase(roleName)) {

            tickets = serviceRequestRepository
                    .findByTypeOrderByIdDesc("PAYROLL");

        } else if ("LND".equalsIgnoreCase(roleName)) {

            tickets = serviceRequestRepository
                    .findByTypeOrderByIdDesc("LEARNING");

        } else if ("IT_SUPPORT".equalsIgnoreCase(roleName)) {

            tickets = serviceRequestRepository
                    .findByTypeOrderByIdDesc("ENTERPRISE");

        } else if ("HR_MANAGER".equalsIgnoreCase(roleName)) {

            tickets = new ArrayList<>();

            tickets.addAll(
                    serviceRequestRepository
                            .findByTypeOrderByIdDesc("HR")
            );

            tickets.addAll(
                    serviceRequestRepository
                            .findByTypeOrderByIdDesc("FACILITIES")
            );

            tickets.addAll(
                    serviceRequestRepository
                            .findByTypeOrderByIdDesc("ALUMNI")
            );

        }else {

            tickets = new ArrayList<>();
        }

        tickets = tickets.stream()
                .filter(t -> !loginId.equalsIgnoreCase(t.getEmployeeId()))
                .collect(Collectors.toList());

        long openCount = tickets.stream()
                .filter(t -> "Open".equalsIgnoreCase(t.getStatus()))
                .count();

        long progressCount = tickets.stream()
                .filter(t -> "In Progress".equalsIgnoreCase(t.getStatus()))
                .count();

        long closedCount = tickets.stream()
                .filter(t -> "Closed".equalsIgnoreCase(t.getStatus()))
                .count();

        model.addAttribute("tickets", tickets);
        model.addAttribute("openCount", openCount);
        model.addAttribute("progressCount", progressCount);
        model.addAttribute("closedCount", closedCount);
        System.out.println("LOGIN USER = " + loginId);
        System.out.println("ROLE = " + roleName);

        tickets.forEach(t ->
            System.out.println(
                t.getTicketId() + " | " +
                t.getDepartment() + " | " +
                t.getEmployeeId()
            )
        );

        return "role-ticket-management";
    }
    @GetMapping("/service-requests")
    public String showServiceRequests(@RequestParam(name = "dept", required = false, defaultValue = "IT") String dept, 
                                     Model model, Principal principal) {
    	if (principal != null) {
    	    String loginId = principal.getName();
    	    User currentUser = userRepository.findByUsername(loginId).orElse(new User());

    	    String backUrl = "/ticket-dashboard?dept=" + dept;

    	    if (currentUser.getRole() != null &&
    	        currentUser.getRole().getPermissions().stream()
    	            .anyMatch(p -> "admin_dashboard_view".equals(p.getPermissionName()))) {

    	        backUrl = "/role-ticket-dashboard?dept=" + dept;
    	    }

    	    model.addAttribute("backUrl", backUrl);

    	    model.addAttribute("user", currentUser);

    	    boolean isRoleUser =
    	            currentUser.getRole() != null &&
    	            currentUser.getRole().getPermissions().stream()
    	                    .anyMatch(p -> "admin_dashboard_view".equals(p.getPermissionName()));

    	    model.addAttribute("isRoleUser", isRoleUser);

    	    List<ServiceRequest> userRequests =
    	            serviceRequestRepository.findByEmployeeIdOrderBySubmissionDateDesc(loginId);

    	    model.addAttribute("myRequests", userRequests);
    	} else {
    	    model.addAttribute("user", new User());
    	}

        // Initialize our dynamic array data nodes
        List<String> dynamicCategories = new ArrayList<>();
        List<String> dynamicItems = new ArrayList<>();

        // Match your clean string check fallback logic from the working incident controller
        String activeDept = (dept != null && !dept.trim().isEmpty()) ? dept.trim().toUpperCase() : "IT";

        // Distribute data options perfectly customized for NEW service requirements
        switch (activeDept) {
            case "HR":
                dynamicCategories = Arrays.asList("ID Card Management", "Benefits Enrollment", "Transfer Request", "Onboarding Help");
                dynamicItems = Arrays.asList("New Smart ID Badge", "Health Insurance Addition", "Inter-Office Relocation", "Buddy Assignment Request");
                break;
            case "FACILITIES":
                dynamicCategories = Arrays.asList("Space Allocation", "Passes & Access", "Furniture Request", "Event Setup");
                dynamicItems = Arrays.asList("Permanent Cabin Allocation", "Vehicle Parking Sticker", "Ergonomic Standing Desk", "Conference Room AV Setup");
                break;
            case "PAYROLL":
                dynamicCategories = Arrays.asList("Tax Declarations", "Bank Profile Change", "Reimbursement Pre-Approval", "Advance Salary");
                dynamicItems = Arrays.asList("Investment Proof Upload", "Salary Account Migration", "Travel Allowance Approval", "Festival Advance Request");
                break;
            case "ALUMNI":
                dynamicCategories = Arrays.asList("Portal Credentials", "Event Registrations", "Merchandise Orders", "Donation Receipts");
                dynamicItems = Arrays.asList("Reset Portal Access", "Annual Meet Pass", "Alumni Lapel Pin", "Tax Exemption Certificate");
                break;
            case "ENTERPRISE":
                dynamicCategories = Arrays.asList("Account Provisioning", "License Upgrades", "Environment Creation", "Database Schema");
                dynamicItems = Arrays.asList("Production SAP License", "Salesforce Pro Seat Tier", "Staging SandBox Instance", "New Table Allocation");
                break;
            case "LEARNING":
                dynamicCategories = Arrays.asList("External Sponsorship", "Learning Content Access", "Bootcamp Nomination", "Exam Voucher");
                dynamicItems = Arrays.asList("AWS Certification Funding", "Coursera Enterprise License", "Full-Stack BootCamp Entry", "RedHat Exam Voucher Code");
                break;
            case "IT":
            default:
                dynamicCategories = Arrays.asList("Hardware Asset Allocation", "Software Provisioning", "Cloud Sandbox Provisioning", "Network Config");
                dynamicItems = Arrays.asList("MacBook Pro M3 Pro 16GB", "IntelliJ IDEA Ultimate Seat", "AWS Sandbox Budget Increase", "Static IP Reservation");
                break;
        }

        // Send the exact properties your updated service-requests.html template relies on
        model.addAttribute("categoriesList", dynamicCategories);
        model.addAttribute("itemsList", dynamicItems);
        model.addAttribute("currentDept", activeDept);

        return "service-requests";
    }

    @GetMapping("/my-assets")
    public String showMyAssets(@RequestParam(name = "dept", required = false, defaultValue = "IT") String dept, 
                               Model model, Principal principal) {
    	if (principal != null) {
    	    String loginId = principal.getName();
    	    User currentUser = userRepository.findByUsername(loginId).orElse(new User());

    	    String backUrl = "/ticket-dashboard?dept=" + dept;

    	    if (currentUser.getRole() != null &&
    	        currentUser.getRole().getPermissions().stream()
    	            .anyMatch(p -> "admin_dashboard_view".equals(p.getPermissionName()))) {

    	        backUrl = "/role-ticket-dashboard?dept=" + dept;
    	    }

    	    model.addAttribute("backUrl", backUrl);

    	    model.addAttribute("user", currentUser);

    	    boolean isRoleUser =
    	            currentUser.getRole() != null &&
    	            currentUser.getRole().getPermissions().stream()
    	                    .anyMatch(p -> "admin_dashboard_view".equals(p.getPermissionName()));

    	    model.addAttribute("isRoleUser", isRoleUser);

    	    List<ServiceRequest> userRequests =
    	            serviceRequestRepository.findByEmployeeIdOrderBySubmissionDateDesc(loginId);

    	    model.addAttribute("myRequests", userRequests);
    	} else {
    	    model.addAttribute("user", new User());
    	}

        List<String> dynamicCategories = new ArrayList<>();
        List<String> dynamicItems = new ArrayList<>();

        String activeDept = (dept != null && !dept.trim().isEmpty()) ? dept.trim().toUpperCase() : "IT";

        // Asset Form dataset allocation map tailored for item types & spec updates
        switch (activeDept) {
            case "HR":
                dynamicCategories = Arrays.asList("Biometric Tokens", "Office Stationary Kits", "Training Handbooks", "Welcome Kits");
                dynamicItems = Arrays.asList("RFID KeyFob Pro", "Premium Executive Pen & Note Diary", "Employee Handbook Version 2026", "Standard Joining Swag Box");
                break;
            case "FACILITIES":
                dynamicCategories = Arrays.asList("Locker Allocations", "Desk Comfort Hardware", "Safety Equipment", "Storage Solutions");
                dynamicItems = Arrays.asList("Heavy Duty Pedestal Locker Key", "Ergonomic Lumbar Support Pillow", "High-Visibility Vest & Safety Shoes", "3-Tier Desk Document Organizer File");
                break;
            case "PAYROLL":
                dynamicCategories = Arrays.asList("Token Generator", "Physical Ledger Logs", "Secure Document Sleeves", "Encryption Hardware");
                dynamicItems = Arrays.asList("RSA SecurID Hard Token PIN", "Confidential Audit Binder", "Tamper-Proof Financial Envelopes", "Encrypted IronKey USB Module");
                break;
            case "ALUMNI":
                dynamicCategories = Arrays.asList("Souvenirs & Merch", "Archival File Folders", "Event Presentation Displays", "Badge Printers");
                dynamicItems = Arrays.asList("Silver Plated Shield Emblem", "Premium Certificate Leather Folder", "Retractable Banner Stand (Rollup)", "Zebra Desktop ID Card Ribbon Kit");
                break;
            case "ENTERPRISE":
                dynamicCategories = Arrays.asList("Server Node Allocation", "Dedicated Hardware Hubs", "Network Testing Gear", "Storage Extensions");
                dynamicItems = Arrays.asList("Blade Server Bay Tier-3 Unit", "Cisco Hardware Router Module", "Fluke Network LAN Cable Tester", "1TB SSD Expansion Module");
                break;
            case "LEARNING":
                dynamicCategories = Arrays.asList("Lab VR Headsets", "Training Tablet Kits", "Audio Capture Gear", "Smart Board Peripherals");
                dynamicItems = Arrays.asList("Meta Quest 3 Enterprise DevKit", "Samsung Galaxy Tab S9 Training Edition", "Jabra Wireless Podcaster Mic", "Stylus Pen Pro Pack");
                break;
            case "IT":
            default:
                dynamicCategories = Arrays.asList("Input Peripherals", "Display Components", "System Computing Upgrades", "Portable Data Storage");
                dynamicItems = Arrays.asList("Logitech MX Master 3S Mouse", "Dell UltraSharp 27-inch 4K Monitor", "Crucial 16GB DDR5 RAM Stick Module", "SanDisk 1TB Extreme Portable SSD");
                break;
        }

        model.addAttribute("categoriesList", dynamicCategories);
        model.addAttribute("itemsList", dynamicItems);
        model.addAttribute("currentDept", activeDept);

        return "my-assets";
    }

    @GetMapping("/report-incident")
    public String showReportIncident(@RequestParam(name = "dept", required = false, defaultValue = "IT") String dept, 
                                     Model model, Principal principal) {
    	if (principal != null) {
    	    String loginId = principal.getName();
    	    User currentUser = userRepository.findByUsername(loginId).orElse(new User());

    	    String backUrl = "/ticket-dashboard?dept=" + dept;

    	    if (currentUser.getRole() != null &&
    	        currentUser.getRole().getPermissions().stream()
    	            .anyMatch(p -> "admin_dashboard_view".equals(p.getPermissionName()))) {

    	        backUrl = "/role-ticket-dashboard?dept=" + dept;
    	    }

    	    model.addAttribute("backUrl", backUrl);

    	    model.addAttribute("user", currentUser);

    	    boolean isRoleUser =
    	            currentUser.getRole() != null &&
    	            currentUser.getRole().getPermissions().stream()
    	                    .anyMatch(p -> "admin_dashboard_view".equals(p.getPermissionName()));

    	    model.addAttribute("isRoleUser", isRoleUser);

    	    List<ServiceRequest> userRequests =
    	            serviceRequestRepository.findByEmployeeIdOrderBySubmissionDateDesc(loginId);

    	    model.addAttribute("myRequests", userRequests);
    	} else {
    	    model.addAttribute("user", new User());
    	}

        // Initialize lists for the dropdown menus
        List<String> dynamicCategories = new ArrayList<>();
        List<String> dynamicItems = new ArrayList<>();

        // Safe fallback block to check for null parameters safely
        String activeDept = (dept != null && !dept.trim().isEmpty()) ? dept.trim().toUpperCase() : "IT";

        switch (activeDept) {
            case "HR":
                dynamicCategories = Arrays.asList("Employee Policies", "Onboarding", "Grievances", "Documentation");
                dynamicItems = Arrays.asList("Letter of Recommendation", "Leave Balance Correction", "Policy Clarification", "PF Query");
                break;
            case "FACILITIES":
                dynamicCategories = Arrays.asList("Office Maintenance", "Workstation Layout", "Power & Lighting", "Access Control");
                dynamicItems = Arrays.asList("Broken Chair Replacement", "Desk Relocation", "AC Adjustment", "Physical Key Request");
                break;
            case "PAYROLL":
                dynamicCategories = Arrays.asList("Salary Discrepancy", "Reimbursements", "Tax / Form 16", "Bank Details Update");
                dynamicItems = Arrays.asList("Upload Fuel Bill Claims", "CTC Structure Revision", "Form 16 Download", "Update Direct Deposit");
                break;
            case "ALUMNI":
                dynamicCategories = Arrays.asList("Background Verification", "Experience Certificates", "Network Access");
                dynamicItems = Arrays.asList("Former Employee Verification", "Relieving Letter Copy", "Transcript Request");
                break;
            case "ENTERPRISE":
                dynamicCategories = Arrays.asList("ERP Access", "CRM Systems", "Database Permissions", "Cloud Consoles");
                dynamicItems = Arrays.asList("SAP Login Issue", "Salesforce Seat Allocation", "MySQL Environment Setup", "AWS Sandbox Access");
                break;
            case "LEARNING":
                dynamicCategories = Arrays.asList("Upskilling Requests", "Certification Reimbursements", "Training Portals");
                dynamicItems = Arrays.asList("Udemy License Assignment", "Certification Exam Voucher", "Java Bootcamp Enrollment");
                break;
            case "IT":
            default:
                dynamicCategories = Arrays.asList("Software", "Hardware", "Network Infrastructure", "Access/Permissions");
                dynamicItems = Arrays.asList("Operating System Install", "IDE Configuration", "Reset AD Password", "VPN Access Allocation");
                break;
        }

        // Send the generated dropdown datasets to the view
        model.addAttribute("categoriesList", dynamicCategories);
        model.addAttribute("itemsList", dynamicItems);
        model.addAttribute("currentDept", activeDept);

        return "report-incident";
    }

    @GetMapping("/knowledge-base")
    public String showKnowledgeBase(@RequestParam(name = "dept", required = false, defaultValue = "IT") String dept, 
                                   Model model, Principal principal) {
    	if (principal != null) {
    	    String loginId = principal.getName();
    	    User currentUser = userRepository.findByUsername(loginId).orElse(new User());

    	    String backUrl = "/ticket-dashboard?dept=" + dept;

    	    if (currentUser.getRole() != null &&
    	        currentUser.getRole().getPermissions().stream()
    	            .anyMatch(p -> "admin_dashboard_view".equals(p.getPermissionName()))) {

    	        backUrl = "/role-ticket-dashboard?dept=" + dept;
    	    }

    	    model.addAttribute("backUrl", backUrl);

    	    model.addAttribute("user", currentUser);

    	    boolean isRoleUser =
    	            currentUser.getRole() != null &&
    	            currentUser.getRole().getPermissions().stream()
    	                    .anyMatch(p -> "admin_dashboard_view".equals(p.getPermissionName()));

    	    model.addAttribute("isRoleUser", isRoleUser);

    	    List<ServiceRequest> userRequests =
    	            serviceRequestRepository.findByEmployeeIdOrderBySubmissionDateDesc(loginId);

    	    model.addAttribute("myRequests", userRequests);
    	} else {
    	    model.addAttribute("user", new User());
    	}

        List<String> dynamicCategories = new ArrayList<>();
        List<String> dynamicItems = new ArrayList<>();

        String activeDept = (dept != null && !dept.trim().isEmpty()) ? dept.trim().toUpperCase() : "IT";

        // Solutions Form dataset logic tailored for Troubleshooting/Knowledge Queries
        switch (activeDept) {
            case "HR":
                dynamicCategories = Arrays.asList("Policy Clarification", "Leave Disputes", "Appraisal Queries", "Provident Fund");
                dynamicItems = Arrays.asList("Maternity/Paternity Guidelines", "Loss of Pay Reversal", "Rating Grievance Form", "PF Withdrawal Documentation");
                break;
            case "FACILITIES":
                dynamicCategories = Arrays.asList("Office Safety", "Access Control Issues", "Cafeteria Feedback", "Workspace Maintenance");
                dynamicItems = Arrays.asList("Fire Warden Nominations", "ID Badge Demagnetized", "Vendor Hygiene Issue", "AC Vent Adjustment Request");
                break;
            case "PAYROLL":
                dynamicCategories = Arrays.asList("Tax Projection Error", "Payslip Discrepancy", "Bonus Calculations", "Reimbursement Rejections");
                dynamicItems = Arrays.asList("Form 16 Revision Request", "Missing HRA Allowance", "Variable Pay Breakdown", "Fuel Bill Resubmission");
                break;
            case "ALUMNI":
                dynamicCategories = Arrays.asList("Verification Request", "Legacy Records Lookup", "Networking Events", "Chapter Membership");
                dynamicItems = Arrays.asList("Background Verification Form", "Graduation Batch 2022 List", "Global Meet Core Agenda", "Pune Chapter Registration");
                break;
            case "ENTERPRISE":
                dynamicCategories = Arrays.asList("ERP Lag/Timeout", "Integration Pipeline", "Data Recovery", "Access Audit Sync");
                dynamicItems = Arrays.asList("SAP GUI Freeze Fix", "Jenkins WebHook Mismatch", "Lost Lead Record Restoration", "AD Group Reconciliation");
                break;
            case "LEARNING":
                dynamicCategories = Arrays.asList("Course Completion Status", "Exam Scheduling Trouble", "Platform License Expiry", "Skill Badges Missing");
                dynamicItems = Arrays.asList("Udemy Completion Sync Error", "PearsonVue Voucher Failure", "Pluralsight Renewal Delay", "Java Core Badge Upload");
                break;
            case "IT":
            default:
                dynamicCategories = Arrays.asList("Operating System Error", "VPN Connectivity Failure", "Local Environment Crash", "Peripheral Malfunction");
                dynamicItems = Arrays.asList("Windows Blue Screen (BSOD)", "FortiClient Connection Timeout", "MySQL Workbench Port Lockout", "Logitech Wireless Mouse Exchange");
                break;
        }

        model.addAttribute("categoriesList", dynamicCategories);
        model.addAttribute("itemsList", dynamicItems);
        model.addAttribute("currentDept", activeDept);

        return "knowledge-base";
    }

    @GetMapping("/payroll")
    public String showPayrollPage() { return "redirect:/payroll-login"; }

    @GetMapping("/holiday-list")
    public String showHolidayList() { return "holiday-list"; }


    // --- ADMIN & MANAGER PORTAL ROUTES (Secured via RBAC) ---

    // LOCK: Need 'employee_create' permission to access or submit this form
    @PreAuthorize("hasAuthority('employee_create')")
    @GetMapping("/admin/add-employee")
    public String showAddEmployeeForm(Model model) {
        model.addAttribute("user", new User());
        return "add-employee";
    }

    @PreAuthorize("hasAuthority('employee_create')")
    @PostMapping("/admin/add-employee-submit")
    public String addEmployee(
            @ModelAttribute User user,
            @ModelAttribute EmployeeProfile employeeProfile,
            Model model) {
        String rawUsername = user.getUsername() != null ? user.getUsername().trim() : "";

        String employeeId = rawUsername.toUpperCase();

        if (!(employeeId.startsWith("EMP") || employeeId.startsWith("INT"))) {

            model.addAttribute(
                "errorMessage",
                "Invalid ID Format! IDs must start with EMP or INT."
            );

            return "add-employee";
        }

        if (userRepository.existsByUsername(employeeId)) {
            model.addAttribute("errorMessage", "Employee / Intern ID'" + rawUsername + "' already exists. Please use a different ID.");
            return "add-employee";
        }
        String email = user.getEmail();
        if (email != null &&
        	    !email.matches(
        	    "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")) {
	        	 model.addAttribute(
	        		        "errorMessage",
	        		        "Invalid email format");
	
	        		    return "add-employee";
        }
        user.setUsername(rawUsername.toUpperCase());
        user.setPassword(passwordEncoder.encode("welcome123"));

        Role empRole = roleRepository.findByRoleName("EMPLOYEE")
                .orElseThrow(() -> new RuntimeException("EMPLOYEE role not found"));

        user.setRole(empRole);
        /*****************************************
         * EMPLOYEE PROFILE INITIALIZATION
         *****************************************/
        employeeProfile.setUser(user);

        if (employeeId.startsWith("INT")) {
            employeeProfile.setDesignation("Intern");
            employeeProfile.setCategory("Intern");
        }

        user.setEmployeeProfile(employeeProfile);

        userRepository.save(user);
        return "redirect:/admin/reports?type=employee";
    }

    // FIXED LOCK: Approvals are strictly for leadership roles via permissions
    @PreAuthorize("hasAnyAuthority('ROLE_SUPER_ADMIN','ROLE_ADMIN','ROLE_HR_ADMIN','ROLE_HR_EXECUTIVE','ROLE_HR_MANAGER')")
    @GetMapping("/admin/timesheet-approval")
    public String showTimesheetApprovalPage() { return "admin-timesheet-approval"; }

    @PreAuthorize("hasAnyAuthority('attendance_approve', 'attendance_edit')")
    @GetMapping("/admin/attendance-regularization")
    public String showRegularizationPage() { return "admin-attendance-regularization"; }

    @PreAuthorize("hasAnyAuthority('attendance_approve', 'attendance_edit')")
    @PostMapping("/admin/timesheets/approve/{id}")
    public String approveTimesheet(@PathVariable Long id, java.security.Principal principal) {
        Timesheet timesheet = timesheetRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid timesheet Id:" + id));

        timesheet.setStatus("Approved");

        if (principal != null) {
            timesheet.setApprovedBy(principal.getName());
        } else {
            timesheet.setApprovedBy("Admin");
        }

        timesheetRepository.save(timesheet);

        try {
            if (timesheet.getUser() != null && timesheet.getUser().getEmail() != null) {
                Map<String, Object> emailData = new HashMap<>();
                emailData.put("empName", timesheet.getUser().getFullName());

                emailService.sendRequestStatusUpdateToEmployee(
                        timesheet.getUser().getEmail(),
                        timesheet.getUser().getFullName(),
                        "Timesheet",
                        "Approved",
                        emailData
                );
            }
        } catch (Exception e) {
            System.err.println("⚠️ Warning: Could not trigger Timesheet email: " + e.getMessage());
        }

        return "redirect:/admin/timesheet-approval";
    }

    // LOCK: Client/Company configuration is secured by 'settings_manage_company'
    @PreAuthorize("hasAuthority('settings_manage_company')")
    @GetMapping("/admin/add-client")
    public String showAddClientPage() { return "add-new-client"; }

    @PreAuthorize("hasAuthority('settings_manage_company')")
    @PostMapping("/admin/save-client")
    public String saveClient(@ModelAttribute Client client, RedirectAttributes redirectAttributes) {

        // 1. Create the Authentication User Account FIRST
        User clientUser = new User();
        clientUser.setUsername(client.getClientId());
        clientUser.setFullName(client.getContactPerson() + " (" + client.getCompanyName() + ")");

        // Fixed: Matches the success message password exactly
        clientUser.setPassword("{noop}client123");

        Role clientRole = roleRepository.findByRoleName("CLIENT").orElse(null);
        clientUser.setRole(clientRole);

        // 2. Link the entities together (Bidirectional Mapping)
        client.setUser(clientUser);
        clientUser.setClientProfile(client);

        // 3. Save to database. Saving the User will cascade and save the Client properly linked!
        userRepository.save(clientUser);
        clientRepository.save(client); // Ensures the Client table gets the foreign key updated

        redirectAttributes.addFlashAttribute("successMessage",
                "Client " + client.getCompanyName() + " successfully onboarded! Login ID: " + client.getClientId() + " | Temp Password: client123");

        return "redirect:/admin/dashboard";
    }

    @PreAuthorize("hasAuthority('settings_manage_company')")
    @GetMapping("/admin/manage-clients")
    public String showManageClientsPage() { return "admin-manage-clients"; }

    @PreAuthorize("hasAuthority('settings_manage_company')")
    @GetMapping("/api/admin/clients")
    @ResponseBody
    public ResponseEntity<List<Client>> getAllClients() {
        return ResponseEntity.ok(clientRepository.findAll());
    }

    @PreAuthorize("hasAuthority('settings_manage_company')")
    @DeleteMapping("/api/admin/clients/{id}")
    @ResponseBody
    public ResponseEntity<?> deleteClient(@PathVariable Long id) {
        Optional<Client> clientOpt = clientRepository.findById(id);

        if (clientOpt.isPresent()) {
            Client client = clientOpt.get();
            User associatedUser = client.getUser(); // Get the dynamically linked user

            // Delete the client profile first
            clientRepository.delete(client);

            // Delete the login credentials so they can't log in anymore
            if (associatedUser != null) {
                userRepository.delete(associatedUser);
            }

            return ResponseEntity.ok("Client profile and login credentials deleted successfully.");
        }
        return ResponseEntity.notFound().build();
    }

    @PreAuthorize("hasAuthority('settings_manage_company')")
    @PostMapping("/api/admin/clients/update")
    @ResponseBody
    public ResponseEntity<?> updateClient(@ModelAttribute Client updatedClient) {
        Optional<Client> existingOpt = clientRepository.findById(updatedClient.getId());

        if (existingOpt.isPresent()) {
            Client existing = existingOpt.get();
            existing.setCompanyName(updatedClient.getCompanyName());
            existing.setDomain(updatedClient.getDomain());
            existing.setAccountStatus(updatedClient.getAccountStatus());
            existing.setContactPerson(updatedClient.getContactPerson());
            existing.setOfficialEmail(updatedClient.getOfficialEmail());
            existing.setPhoneNumber(updatedClient.getPhoneNumber());
            existing.setAssignedTeam(updatedClient.getAssignedTeam());
            existing.setTeamLead(updatedClient.getTeamLead());
            existing.setAssignedEmployee(updatedClient.getAssignedEmployee());
            existing.setBillingAddress(updatedClient.getBillingAddress());
            existing.setCity(updatedClient.getCity());
            existing.setCountry(updatedClient.getCountry());

            // Sync changes with the User table
            User associatedUser = existing.getUser();
            if (associatedUser != null) {
                associatedUser.setFullName(existing.getContactPerson() + " (" + existing.getCompanyName() + ")");
                userRepository.save(associatedUser);
            }

            clientRepository.save(existing);
            return ResponseEntity.ok("Client updated successfully.");
        }
        return ResponseEntity.notFound().build();
    }

    // LOCK: Employee Directory requires 'employee_view'
    @PreAuthorize("hasAuthority('employee_view')")
    @GetMapping("/admin/staff")
    public String showStaffDirectory(Model model, @RequestParam(required = false) String keyword) {
        List<User> staffList = userRepository.findAll().stream()
                // FIXED: Show ALL internal staff by excluding only Clients and the Super Admin
                .filter(u -> u.getRole() != null &&
                        !"CLIENT".equalsIgnoreCase(u.getRole().getRoleName()) &&
                        !"SUPER_ADMIN".equalsIgnoreCase(u.getRole().getRoleName()))
                .filter(u -> keyword == null || keyword.isEmpty() || (u.getFullName() != null && u.getFullName().toLowerCase().contains(keyword.toLowerCase())))
                .sorted(Comparator.comparing(User::getUsername, Comparator.nullsLast(String::compareToIgnoreCase)))
                .collect(Collectors.toList());

        model.addAttribute("staffList", staffList);
        model.addAttribute("keyword", keyword);
        return "admin-staff";
    }

    // LOCK: Editing an employee record requires 'employee_edit'
    @PreAuthorize("hasAuthority('employee_edit')")
    @GetMapping("/admin/staff/edit/{id}")
    public String showEditEmployeeForm(@PathVariable("id") Long id, Model model) {
        User employee = userService.findById(id);
        model.addAttribute("employee", employee);
        return "admin/edit-employee";
    }

    @PreAuthorize("hasAuthority('employee_edit')")
    @PostMapping("/admin/staff/edit/{id}")
    public String updateEmployee(@PathVariable("id") Long id, @ModelAttribute("employee") User updatedEmployee, RedirectAttributes redirectAttributes) {
        userService.updateEmployeeProfessionalDetails(id, updatedEmployee);
        redirectAttributes.addFlashAttribute("successMessage", "Employee Details Updated Successfully!");
        return "redirect:/admin/staff";
    }

    // FIXED LOCK: Helpdesk requires Asset or IT Permissions
    @PreAuthorize("hasAnyAuthority('asset_assign', 'asset_view', 'settings_manage_roles')")
    @GetMapping("/admin-helpdesk-requests")
    public String viewAdminHelpdeskPortal(Model model) {
        List<ServiceRequest> allRequests = serviceRequestRepository.findAll();

        model.addAttribute("allRequests", allRequests);

        long softwareCount = allRequests.stream().filter(r -> "SOFTWARE".equals(r.getType())).count();
        long hardwareCount = allRequests.stream().filter(r -> "HARDWARE".equals(r.getType())).count();

        model.addAttribute("softwareCount", softwareCount);
        model.addAttribute("hardwareCount", hardwareCount);

        return "admin-helpdesk-requests";
    }

    @Transactional
    @PostMapping("/employee/my-goals/{goalId}/delete")
    public String deleteGoal(@PathVariable Long goalId) {

        Goal goal = goalRepository.findById(goalId)
                .orElseThrow(() -> new RuntimeException("Goal not found"));

        goalUpdateRepository.deleteByGoal(goal);

        goalRepository.delete(goal);

        return "redirect:/employee/my-goals";
    }
    
    @GetMapping("/employee/goals/{goalId}/chart")
    @ResponseBody
    public List<BurnChartPoint> getGoalChart(
            @PathVariable Long goalId) {

        Goal goal = goalRepository.findById(goalId)
                .orElseThrow(() ->
                        new RuntimeException("Goal not found"));

        return goalBurnChartService
                .generateSingleGoalBurnChart(goal);
    }

    // FIXED LOCK: Global search requires basic admin view rights so Finance/Recruiters can use it
    @PreAuthorize("hasAnyAuthority('employee_view', 'admin_dashboard_view')")
    @GetMapping("/api/employees/search")
    @ResponseBody
    public ResponseEntity<List<Map<String, Object>>> globalSearch(@RequestParam("query") String query) {

        if (query == null || query.trim().length() < 2) {
            return ResponseEntity.ok(Collections.emptyList());
        }

        String keyword = query.trim();
        List<Map<String, Object>> results = new ArrayList<>();

        List<User> employees = userRepository.searchByKeyword(keyword);
        for (User u : employees) {
            if (u.getRole() != null && !"CLIENT".equalsIgnoreCase(u.getRole().getRoleName())) {
                Map<String, Object> map = new HashMap<>();
                EmployeeProfile profile = u.getEmployeeProfile();

                map.put("dbId", u.getId());
                map.put("fullName", u.getFullName());
                map.put("identifier", u.getUsername());
                map.put("designation", (profile != null && profile.getDesignation() != null) ? profile.getDesignation() : "Employee");
                map.put("department", (profile != null && profile.getBusinessUnit() != null) ? profile.getBusinessUnit() : "General");
                map.put("email", u.getEmail() != null ? u.getEmail() : "N/A");
                map.put("phone", (profile != null && profile.getMobileNumber() != null) ? profile.getMobileNumber() : "N/A");
                map.put("role", "EMPLOYEE");
                results.add(map);
            }
        }

        List<Client> clients = clientRepository.searchClients(keyword);
        for (Client c : clients) {
            Map<String, Object> map = new HashMap<>();
            map.put("dbId", c.getId());
            map.put("fullName", c.getCompanyName());
            map.put("identifier", c.getClientId());
            map.put("designation", "Client Partner (" + c.getContactPerson() + ")");
            map.put("department", c.getDomain() != null ? c.getDomain() : "External");
            map.put("email", c.getOfficialEmail() != null ? c.getOfficialEmail() : "N/A");
            map.put("phone", c.getPhoneNumber() != null ? c.getPhoneNumber() : "N/A");
            map.put("role", "CLIENT");
            results.add(map);
        }

        if (results.size() > 6) {
            results = results.subList(0, 6);
        }

        return ResponseEntity.ok(results);
    }


    // ROLE MANAGEMENT MODULE (SUPER ADMIN ONLY)
    @PreAuthorize("hasAuthority('settings_manage_roles')")
    @GetMapping("/admin/manage-roles")
    public String showManageRolesPage(Model model, @RequestParam(required = false) String search) {

        // 1. Fetch all roles, but strictly filter out external and legacy roles
        List<Role> allRoles = roleRepository.findAll().stream()
                .filter(role -> !"CLIENT".equalsIgnoreCase(role.getRoleName()) &&
                        !"ADMIN".equalsIgnoreCase(role.getRoleName()))
                .collect(Collectors.toList());

        // 2. Fetch users (with optional search filter)
        List<User> usersList;
        if (search != null && !search.trim().isEmpty()) {
            usersList = userRepository.searchByKeyword(search.trim());
        } else {
            usersList = userRepository.findAll();
        }

        // FIXED: Security Filters
        // 1. Hide ADM001 to prevent the SuperAdmin from locking themselves out.
        // 2. Hide CLIENT accounts so external users cannot be given internal admin roles.
        usersList = usersList.stream()
                .filter(user -> !"ADM001".equalsIgnoreCase(user.getUsername()))
                .filter(user -> user.getRole() == null || !"CLIENT".equalsIgnoreCase(user.getRole().getRoleName()))
                .collect(Collectors.toList());

        // Sort users alphabetically for a cleaner UI
        usersList.sort(Comparator.comparing(User::getUsername, Comparator.nullsLast(String::compareToIgnoreCase)));

        model.addAttribute("usersList", usersList);
        model.addAttribute("allRoles", allRoles);
        model.addAttribute("currentSearch", search);

        return "admin-manage-roles";
    }

    @PostMapping("/admin/update-role")
    public String updateUserRole(@RequestParam("userId") Long userId,
                                 @RequestParam("roleId") Long roleId,
                                 @RequestParam(value = "designation", required = false) String designation,
                                 RedirectAttributes redirectAttributes) {

        User user = userRepository.findById(userId).orElse(null);
        Role newRole = roleRepository.findById(roleId).orElse(null);

        if (user != null && newRole != null) {
            // 1. Update the System Role
            user.setRole(newRole);

            // 2. Update Designation if the admin selected one from the new dropdown
            if (user.getEmployeeProfile() != null && designation != null && !designation.trim().isEmpty()) {
                user.getEmployeeProfile().setDesignation(designation);
            }

            userRepository.save(user);
            redirectAttributes.addFlashAttribute("successMessage", "Security clearance and designation updated for " + user.getFullName());
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "Error updating clearance. User or Role not found.");
        }

        return "redirect:/admin/manage-roles";
    }


    @GetMapping("/admin/profile")
    public String viewAdminProfile(Principal principal, Model model) {
        String username = principal.getName();
        User currentUser = userService.findByUsername(username);

        model.addAttribute("user", currentUser);
        return "admin-profile";
    }

    @PostMapping("/admin/profile/update")
    public String updateAdminProfile(
            // Contact & Emergency
            @RequestParam("fullName") String fullName,
            @RequestParam("mobileNumber") String mobileNumber,
            @RequestParam(value = "altMobile", required = false) String altMobile,
            @RequestParam(value = "personalEmail", required = false) String personalEmail,
            @RequestParam("permanentAddress") String permanentAddress,
            @RequestParam(value = "workingAddress", required = false) String workingAddress,
            @RequestParam(value = "city", required = false) String city,
            @RequestParam(value = "country", required = false) String country,
            @RequestParam("emergencyContactName") String emergencyContactName,
            @RequestParam(value = "relationWithEmployee", required = false) String relationWithEmployee,
            @RequestParam("emergencyPhone") String emergencyPhone,

            // Newly Editable: Identity & Compliance
            @RequestParam(value = "dob", required = false) @org.springframework.format.annotation.DateTimeFormat(pattern = "yyyy-MM-dd") java.time.LocalDate dob,
            @RequestParam(value = "gender", required = false) String gender,
            @RequestParam(value = "panNo", required = false) String panNo,
            @RequestParam(value = "aadharNo", required = false) String aadharNo,

            // Newly Editable: Education
            @RequestParam(value = "qual1Title", required = false) String qual1Title,
            @RequestParam(value = "qual1Inst", required = false) String qual1Inst,
            @RequestParam(value = "qual1Year", required = false) String qual1Year,
            @RequestParam(value = "qual2Title", required = false) String qual2Title,
            @RequestParam(value = "qual2Inst", required = false) String qual2Inst,
            @RequestParam(value = "qual2Year", required = false) String qual2Year,

            Principal principal,
            RedirectAttributes redirectAttributes) {

        String username = principal.getName();
        User existingUser = userService.findByUsername(username);

        existingUser.setFullName(fullName);

        EmployeeProfile profile = existingUser.getEmployeeProfile();
        if (profile == null) {
            profile = new EmployeeProfile();
            profile.setUser(existingUser);
        }

        // Map Contact Data
        profile.setMobileNumber(mobileNumber);
        profile.setAltMobile(altMobile);
        profile.setPersonalEmail(personalEmail);
        profile.setPermanentAddress(permanentAddress);
        profile.setWorkingAddress(workingAddress);
        profile.setCity(city);
        profile.setCountry(country);

        // Map Emergency Data
        profile.setEmergencyContactName(emergencyContactName);
        profile.setRelationWithEmployee(relationWithEmployee);
        profile.setEmergencyPhone(emergencyPhone);

        // Map Identity Data
        profile.setDob(dob);
        profile.setGender(gender);
        profile.setPanNo(panNo);
        profile.setAadharNo(aadharNo);

        // Map Education Data
        profile.setQual1Title(qual1Title);
        profile.setQual1Inst(qual1Inst);
        profile.setQual1Year(qual1Year);
        profile.setQual2Title(qual2Title);
        profile.setQual2Inst(qual2Inst);
        profile.setQual2Year(qual2Year);

        existingUser.setEmployeeProfile(profile);
        userRepository.save(existingUser);

        redirectAttributes.addFlashAttribute("successMessage", "Profile updated successfully.");
        return "redirect:/admin/profile";
    }

}