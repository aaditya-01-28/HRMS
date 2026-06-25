package com.example.admindashboard.controller;

import com.example.admindashboard.model.Meeting;
import com.example.admindashboard.repository.MeetingRepository;
import com.example.admindashboard.repository.ServiceRequestRepository;
import com.example.admindashboard.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import jakarta.servlet.http.HttpServletRequest;
import java.security.Principal;
import java.util.List;
import java.util.stream.Collectors;

@Controller
public class SeniorDashboardController {

    @Autowired
    private ServiceRequestRepository serviceRequestRepository;

    @Autowired
    private MeetingRepository meetingRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private com.example.admindashboard.repository.RideBookingRepository rideBookingRepository;

    @Autowired
    private com.example.admindashboard.repository.TransportVendorRepository transportVendorRepository;

    @Autowired
    private com.example.admindashboard.repository.TransportVehicleRepository transportVehicleRepository;

    // --- Helper: Get pending meeting invites (same logic as DashboardController) ---
    private List<Meeting> getPendingMeetingInvites(String username) {
        com.example.admindashboard.model.User currentUser = userRepository.findByUsername(username).orElse(null);
        List<Meeting> allPending = meetingRepository.findByMeetingDateGreaterThanEqualOrderByMeetingDateAscStartTimeAsc(java.time.LocalDate.now())
                .stream().filter(m -> "PENDING".equals(m.getStatus()) && m.getOrganizer() != null && !m.getOrganizer().getUsername().equals(username)).toList();
                
        return allPending.stream().filter(meeting -> {
            if (meeting.getSpecificEmployeeIds() != null && meeting.getSpecificEmployeeIds().contains(username)) return true;
            com.example.admindashboard.model.EmployeeProfile myProfile = currentUser != null ? currentUser.getEmployeeProfile() : null;
            com.example.admindashboard.model.EmployeeProfile organizerProfile = meeting.getOrganizer() != null ? meeting.getOrganizer().getEmployeeProfile() : null;
            if ("TEAM".equals(meeting.getParticipantType()) && myProfile != null && myProfile.getBusinessUnit() != null) {
                if (organizerProfile != null && myProfile.getBusinessUnit().equals(organizerProfile.getBusinessUnit())) {
                    return true;
                }
            }
            return false;
        }).toList();
    }

    // --- DASHBOARD ROUTES ---

    @PreAuthorize("hasAuthority('admin_dashboard_view')")
    @GetMapping("/senior_manager/dashboard")
    public String showSeniorManagerDashboard(Model model, Principal principal, HttpServletRequest request) {
    	loadDashboardData(model);
    	return "employee-dashboard";
    }

    @PreAuthorize("hasAuthority('admin_dashboard_view')")
    @GetMapping("/senior_hr/dashboard")
    public String showSeniorHrDashboard(Model model, Principal principal, HttpServletRequest request) {
    	model.addAttribute("showMySpace", true);
    	return "senior_hr-dashboard";
    }

    @PreAuthorize("hasAuthority('admin_dashboard_view')")
    @GetMapping("/senior_lnd/dashboard")
    public String showSeniorLndDashboard(Model model, Principal principal, HttpServletRequest request) {
    	loadDashboardData(model);
    	return "employee-dashboard";
    }

    @PreAuthorize("hasAuthority('admin_dashboard_view')")
    @GetMapping("/senior_accounts/dashboard")
    public String showSeniorAccountsDashboard(Model model, Principal principal, HttpServletRequest request) {
    	loadDashboardData(model);
    	return "employee-dashboard";
    }

    @PreAuthorize("hasAuthority('admin_dashboard_view')")
    @GetMapping("/senior_transport/dashboard")
    public String showSeniorTransportDashboard(Model model, Principal principal, HttpServletRequest request) {
        String currentUserId = principal.getName();
        List<com.example.admindashboard.model.ServiceRequest> recentTickets =
                serviceRequestRepository.findTop3ByEmployeeIdOrderByIdDesc(currentUserId);
        model.addAttribute("recentTickets", recentTickets);
        model.addAttribute("pendingMeetingInvites", getPendingMeetingInvites(currentUserId));
        return "senior_transport-dashboard";
    }

    @PreAuthorize("hasAuthority('admin_dashboard_view')")
    @GetMapping("/senior_rewards/dashboard")
    public String showSeniorRewardsDashboard(Model model, Principal principal, HttpServletRequest request) {
    	loadDashboardData(model);
    	return "employee-dashboard";
    }

    // --- MY SPACE ROUTE (Transport) ---

    @PreAuthorize("hasAuthority('admin_dashboard_view')")
    @GetMapping("/senior_transport/my_space")
    public String showSeniorTransportMySpace(Model model, Principal principal) {
        model.addAttribute("vendors", transportVendorRepository.findAll());
        model.addAttribute("vehicles", transportVehicleRepository.findAll());
        
        List<com.example.admindashboard.model.RideBooking> pendingBookings = rideBookingRepository.findAll().stream()
                .filter(b -> "SCHEDULED".equals(b.getStatus()) || "Pending".equalsIgnoreCase(b.getStatus()))
                .collect(Collectors.toList());
        
        List<com.example.admindashboard.model.RideBooking> allBookings = rideBookingRepository.findAll();
        
        model.addAttribute("pendingBookings", pendingBookings);
        model.addAttribute("allBookings", allBookings);
        
        return "senior_transport-myspace";
    }

    // --- MY SPACE ROUTE (HR Payroll) ---

    @PreAuthorize("hasAuthority('admin_dashboard_view')")
    @GetMapping("/senior_hr/my_space")
    public String showSeniorHrMySpace(Model model, Principal principal) {
        // Pass mock data representing the screenshot tabs until we have JPA entities for Payroll
        
        // Example dynamic summary stats (Mock)
        model.addAttribute("totalEmployees", 100);
        model.addAttribute("totalNetPay", "3,88,000");
        model.addAttribute("deductionsCount", 2);
        model.addAttribute("totalDaysPresent", "30");
        
        // Mock lists for the tables
        model.addAttribute("salaryStructures", java.util.List.of(
            new java.util.HashMap<String, Object>() {{ put("empName", "Aakash Sharma"); put("dept", "Engineering"); put("salary", "12,00,000"); put("deductions", 0); put("bonus", 0); }},
            new java.util.HashMap<String, Object>() {{ put("empName", "Rohan Gupta"); put("dept", "HR"); put("salary", "8,50,000"); put("deductions", 5000); put("bonus", 10000); }}
        ));
        
        return "senior_hr-myspace";
    }

    // --- MY SPACE ROUTE (HR Employee) ---
    @PreAuthorize("hasAuthority('admin_dashboard_view')")
    @GetMapping("/senior_hr/employee")
    public String showSeniorHrEmployee(Model model, Principal principal) {
        // Mock candidates for Onboarding table
        model.addAttribute("candidates", java.util.List.of(
            new java.util.HashMap<String, Object>() {{ put("name", "Neha Sharma"); put("dept", "Engineering"); put("desig", "Sr. Developer"); put("loc", "Delhi, India"); put("join", "20/11/2026"); put("prog", 85); }},
            new java.util.HashMap<String, Object>() {{ put("name", "Amit Sharma"); put("dept", "Engineering"); put("desig", "Sr. Developer"); put("loc", "Delhi, India"); put("join", "20/11/2026"); put("prog", 85); }},
            new java.util.HashMap<String, Object>() {{ put("name", "Pihu Sharma"); put("dept", "Engineering"); put("desig", "Sr. Developer"); put("loc", "Delhi, India"); put("join", "20/11/2026"); put("prog", 85); }},
            new java.util.HashMap<String, Object>() {{ put("name", "Shub Sharma"); put("dept", "Engineering"); put("desig", "Sr. Developer"); put("loc", "Delhi, India"); put("join", "20/11/2026"); put("prog", 85); }}
        ));

        // Mock employees for All Employees table
        model.addAttribute("employees", java.util.List.of(
            new java.util.HashMap<String, Object>() {{ put("name", "Neha Sharma"); put("id", "EMP114"); put("dept", "Engineering"); put("desig", "Full-stack developer"); put("loc", "Delhi, India"); put("type", "Full-time"); put("join", "7 May, 2026"); put("status", "Active"); }},
            new java.util.HashMap<String, Object>() {{ put("name", "Amit Sharma"); put("id", "EMP110"); put("dept", "Product"); put("desig", "Product Manager"); put("loc", "Raipur, India"); put("type", "Full-time"); put("join", "15 May, 2026"); put("status", "Active"); }},
            new java.util.HashMap<String, Object>() {{ put("name", "Pihu Sharma"); put("id", "EMP114"); put("dept", "Engineering"); put("desig", "Software Engineer"); put("loc", "Ooty, India"); put("type", "Full-time"); put("join", "7 May, 2026"); put("status", "On Notice"); }},
            new java.util.HashMap<String, Object>() {{ put("name", "Shub Sharma"); put("id", "EMP114"); put("dept", "Engineering"); put("desig", "Junior developer"); put("loc", "Pune, India"); put("type", "Full-time"); put("join", "7 May, 2026"); put("status", "Active"); }},
            new java.util.HashMap<String, Object>() {{ put("name", "Neha Patel"); put("id", "EMP114"); put("dept", "HR"); put("desig", "Hr Executive"); put("loc", "Delhi, India"); put("type", "Full-time"); put("join", "7 May, 2026"); put("status", "Exited"); }}
        ));

        return "senior_hr-employee";
    }
    // --- WORKFLOW ROUTES ---

    @PreAuthorize("hasAuthority('admin_dashboard_view')")
    @GetMapping("/senior_manager/workflow")
    public String showSeniorManagerWorkflow(Model model, Principal principal) {
        return "senior_manager-workflow";
    }

    @PreAuthorize("hasAuthority('admin_dashboard_view')")
    @GetMapping("/senior_hr/workflow")
    public String showSeniorHrWorkflow(Model model, Principal principal) {
        return "senior_hr-workflow";
    }

    @PreAuthorize("hasAuthority('admin_dashboard_view')")
    @GetMapping("/senior_lnd/workflow")
    public String showSeniorLndWorkflow(Model model, Principal principal) {
        return "senior_lnd-workflow";
    }

    @PreAuthorize("hasAuthority('admin_dashboard_view')")
    @GetMapping("/senior_accounts/workflow")
    public String showSeniorAccountsWorkflow(Model model, Principal principal) {
        return "senior_accounts-workflow";
    }

    @PreAuthorize("hasAuthority('admin_dashboard_view')")
    @GetMapping("/senior_transport/workflow")
    public String showSeniorTransportWorkflow(Model model, Principal principal) {
        return "senior_transport-workflow";
    }

    @PreAuthorize("hasAuthority('admin_dashboard_view')")
    @GetMapping("/senior_rewards/workflow")
    public String showSeniorRewardsWorkflow(Model model, Principal principal) {
        return "senior_rewards-workflow";
    }
    private void loadDashboardData(Model model) {
        model.addAttribute("showMySpace", true);
    }
    
}
