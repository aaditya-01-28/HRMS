package com.example.admindashboard.controller;

import com.example.admindashboard.model.Meeting;
import com.example.admindashboard.model.ResignationRequest;
import com.example.admindashboard.repository.MeetingRepository;
import com.example.admindashboard.repository.ResignationRequestRepository;
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
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import com.example.admindashboard.model.User;

@Controller
public class SeniorDashboardController {

    @Autowired
    private ServiceRequestRepository serviceRequestRepository;

    @Autowired
    private MeetingRepository meetingRepository;

    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private ResignationRequestRepository resignationRequestRepository;

    @Autowired
    private com.example.admindashboard.repository.RideBookingRepository rideBookingRepository;

    @Autowired
    private com.example.admindashboard.repository.TransportVendorRepository transportVendorRepository;

    @Autowired
    private com.example.admindashboard.repository.TransportVehicleRepository transportVehicleRepository;

    @Autowired
    private com.example.admindashboard.repository.LeaveRequestRepository leaveRequestRepository;

    @Autowired
    private com.example.admindashboard.repository.ShiftAssignmentRepository shiftAssignmentRepository;

    @Autowired
    private com.example.admindashboard.repository.HolidayRepository holidayRepository;

    @Autowired
    private com.example.admindashboard.repository.CompOffEntryRepository compOffEntryRepository;

    @Autowired
    private com.example.admindashboard.repository.LeaveTypeMasterRepository leaveTypeMasterRepository;

    @Autowired
    private com.example.admindashboard.repository.SalaryStructureRepository salaryStructureRepository;

    @Autowired
    private com.example.admindashboard.repository.SalaryComponentRepository salaryComponentRepository;

    @Autowired
    private com.example.admindashboard.repository.PayslipRepository payslipRepository;

    @Autowired
    private com.example.admindashboard.repository.BonusDeductionRepository bonusDeductionRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // --- LOGIN ROUTE ---
    @GetMapping("/senior_hr/login")
    public String showSeniorHrLogin() {
        return "senior_hr-login";
    }

    @PostMapping("/senior_hr/login")
    public String processSeniorHrLogin(
            @RequestParam String username,
            @RequestParam String password,
            Model model) {

        User user = userRepository
                .findByUsername(username.toUpperCase())
                .orElse(null);

        if (user == null || !passwordEncoder.matches(password, user.getPassword())) {
            model.addAttribute("authError", "Invalid username or password");
            return "senior_hr-login";
        }

        // Redirect to employee dashboard by default after login
        return "redirect:/senior_hr/employee";
    }

    // --- LMS ROUTE ---
    @GetMapping("/senior_hr/lms")
    public String showSeniorHrLms(Model model) {
        List<com.example.admindashboard.model.LeaveRequest> leaveRequests = leaveRequestRepository.findAll();
        
        java.time.LocalDate today = java.time.LocalDate.now();
        List<com.example.admindashboard.model.LeaveRequest> todayLeaves = leaveRequests.stream()
            .filter(lr -> "APPROVED".equalsIgnoreCase(lr.getStatus()) && lr.getFromDate() != null && !lr.getFromDate().isAfter(today) && (lr.getToDate() == null ? lr.getFromDate().isEqual(today) : !lr.getToDate().isBefore(today)))
            .collect(java.util.stream.Collectors.toList());
            
        long pendingCount = leaveRequests.stream().filter(lr -> "PENDING".equalsIgnoreCase(lr.getStatus())).count();
        long todayOnLeaveCount = todayLeaves.size();
        long totalCount = leaveRequests.size();

        List<com.example.admindashboard.model.LeaveTypeMaster> leaveTypes = leaveTypeMasterRepository.findAll();
        List<com.example.admindashboard.model.Holiday> holidays = holidayRepository.findAll();
        List<com.example.admindashboard.model.CompOffEntry> compOffEntries = compOffEntryRepository.findAll();

        model.addAttribute("leaveRequests", leaveRequests);
        model.addAttribute("todayLeaves", todayLeaves);
        model.addAttribute("pendingCount", pendingCount);
        model.addAttribute("todayOnLeaveCount", todayOnLeaveCount);
        model.addAttribute("totalCount", totalCount);
        
        model.addAttribute("leaveTypes", leaveTypes);
        model.addAttribute("holidays", holidays);
        model.addAttribute("compOffEntries", compOffEntries);
        
        return "senior_hr-lms";
    }

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
        model.addAttribute("isSeniorManager", true);
        model.addAttribute("workflowUrl", "/senior_manager/workflow");
    	return "employee-dashboard";
    }

    @PreAuthorize("hasAuthority('admin_dashboard_view')")
    @GetMapping("/senior_hr/dashboard")
    public String showSeniorHrDashboard(Model model, Principal principal, HttpServletRequest request) {
    	model.addAttribute("showMySpace", true);
        model.addAttribute("isSeniorManager", true);
        model.addAttribute("workflowUrl", "/senior_hr/workflow");
    	return "senior_hr-dashboard";
    }

    @PreAuthorize("hasAuthority('admin_dashboard_view')")
    @GetMapping("/senior_lnd/dashboard")
    public String showSeniorLndDashboard(Model model, Principal principal, HttpServletRequest request) {
    	loadDashboardData(model);
        model.addAttribute("isSeniorManager", true);
        model.addAttribute("workflowUrl", "/senior_lnd/workflow");
    	return "employee-dashboard";
    }

    @PreAuthorize("hasAuthority('admin_dashboard_view')")
    @GetMapping("/senior_accounts/dashboard")
    public String showSeniorAccountsDashboard(Model model, Principal principal, HttpServletRequest request) {
    	loadDashboardData(model);
        model.addAttribute("isSeniorManager", true);
        model.addAttribute("workflowUrl", "/senior_accounts/workflow");
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
        model.addAttribute("isSeniorManager", true);
        model.addAttribute("workflowUrl", "/senior_transport/workflow");
        return "senior_transport-dashboard";
    }

    @PreAuthorize("hasAuthority('admin_dashboard_view')")
    @GetMapping("/senior_rewards/dashboard")
    public String showSeniorRewardsDashboard(Model model, Principal principal, HttpServletRequest request) {
    	loadDashboardData(model);
        model.addAttribute("isSeniorManager", true);
        model.addAttribute("workflowUrl", "/senior_rewards/workflow");
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
        
        // Fetch existing salary structures
        List<com.example.admindashboard.model.SalaryStructure> structures = salaryStructureRepository.findByActiveTrue();
        
        if (structures.isEmpty()) {
            // Seed default structure based on standard formula if DB is empty
            com.example.admindashboard.model.SalaryStructure std = new com.example.admindashboard.model.SalaryStructure();
            std.setStructureName("Standard Template - Amit Sharma");
            std.setEffectiveFrom(java.time.LocalDate.now());
            std.setCtcAmount(960000.0);
            
            // Add components matching the screenshot
            // 1. Basic Salary
            com.example.admindashboard.model.SalaryComponent basic = new com.example.admindashboard.model.SalaryComponent();
            basic.setComponentName("Basic Salary");
            basic.setType("Earning");
            basic.setCategory("Basic");
            basic.setCalculationFormula("40% of CTC");
            basic.setAmount(384000.0);
            basic.setPercentageOfCtc(40.00);
            basic.setTaxable(true);
            std.addComponent(basic);
            
            // 2. HRA
            com.example.admindashboard.model.SalaryComponent hra = new com.example.admindashboard.model.SalaryComponent();
            hra.setComponentName("HRA");
            hra.setType("Earning");
            hra.setCategory("Allowance");
            hra.setCalculationFormula("50% of Basic");
            hra.setAmount(192000.0);
            hra.setPercentageOfCtc(25.00); // from mockup
            hra.setTaxable(true);
            std.addComponent(hra);

            // 3. Conveyance Allow.
            com.example.admindashboard.model.SalaryComponent conv = new com.example.admindashboard.model.SalaryComponent();
            conv.setComponentName("Conveyance Allow.");
            conv.setType("Earning");
            conv.setCategory("Statutory");
            conv.setCalculationFormula("Fixed Amount");
            conv.setAmount(1920.0);
            conv.setPercentageOfCtc(20.00); // from mockup
            conv.setTaxable(true);
            std.addComponent(conv);

            // 4. Professional Tax
            com.example.admindashboard.model.SalaryComponent ptax = new com.example.admindashboard.model.SalaryComponent();
            ptax.setComponentName("Professional Tax");
            ptax.setType("Deduction");
            ptax.setCategory("Statutory");
            ptax.setCalculationFormula("Fixed Amount");
            ptax.setAmount(200.0);
            ptax.setPercentageOfCtc(4.00); // from mockup
            ptax.setTaxable(true);
            std.addComponent(ptax);

            // 5. Provident Fund
            com.example.admindashboard.model.SalaryComponent pf = new com.example.admindashboard.model.SalaryComponent();
            pf.setComponentName("Provident Fund");
            pf.setType("Deduction");
            pf.setCategory("Allowance");
            pf.setCalculationFormula("12% of Basic");
            pf.setAmount(46000.0);
            pf.setPercentageOfCtc(0.02); // from mockup
            pf.setTaxable(true);
            std.addComponent(pf);

            // 6. TDS
            com.example.admindashboard.model.SalaryComponent tds = new com.example.admindashboard.model.SalaryComponent();
            tds.setComponentName("TDS");
            tds.setType("Deduction");
            tds.setCategory("Statutory");
            tds.setCalculationFormula("As per rules");
            tds.setAmount(0.0);
            tds.setPercentageOfCtc(0.00);
            tds.setTaxable(true);
            std.addComponent(tds);

            std.setTotalEarnings(384000.0 + 192000.0 + 19200.0);
            std.setTotalDeductions(200.0 + 46000.0 + 0.0);
            
            salaryStructureRepository.save(std);
            structures = salaryStructureRepository.findByActiveTrue();
        }

        model.addAttribute("salaryStructures", structures);

        // Fetch and seed Payslips
        List<com.example.admindashboard.model.Payslip> payslips = payslipRepository.findAll();
        if (payslips.isEmpty()) {
            com.example.admindashboard.model.Payslip p1 = new com.example.admindashboard.model.Payslip();
            p1.setPayMonth("May");
            p1.setPayYear(2026);
            p1.setCtcAnnual(500000.0);
            p1.setGrossPay(125000.0);
            p1.setTotalDeductions(10000.0);
            p1.setNetPay(100000.0);
            p1.setStatus("Processed");
            p1.setPaymentDate(java.time.LocalDate.of(2026, 6, 20));
            p1.setDepartment("Engineering");
            
            // Dummy user Neha
            com.example.admindashboard.model.User neha = userRepository.findAll().stream().filter(u -> u.getFullName() != null && u.getFullName().contains("Neha")).findFirst().orElse(null);
            p1.setUser(neha);
            payslipRepository.save(p1);

            com.example.admindashboard.model.Payslip p2 = new com.example.admindashboard.model.Payslip();
            p2.setPayMonth("May");
            p2.setPayYear(2026);
            p2.setCtcAnnual(500000.0);
            p2.setGrossPay(125000.0);
            p2.setTotalDeductions(10000.0);
            p2.setNetPay(100000.0);
            p2.setStatus("Not Processed");
            p2.setDepartment("Engineering");
            payslipRepository.save(p2);

            payslips = payslipRepository.findAll();
        }
        model.addAttribute("payslips", payslips);

        // Fetch and seed BonusDeductions
        List<com.example.admindashboard.model.BonusDeduction> bonuses = bonusDeductionRepository.findAll();
        if (bonuses.isEmpty()) {
            com.example.admindashboard.model.BonusDeduction b1 = new com.example.admindashboard.model.BonusDeduction();
            b1.setType("Bonus");
            b1.setCategory("Performance Bonus");
            b1.setDescription("Success of a project");
            b1.setAmount(25000.0);
            b1.setImpact("Increase");
            b1.setStatus("Approved");
            b1.setEffectiveMonth("May 2026");
            bonusDeductionRepository.save(b1);

            com.example.admindashboard.model.BonusDeduction d1 = new com.example.admindashboard.model.BonusDeduction();
            d1.setType("Deduct");
            d1.setCategory("Late Coming");
            d1.setDescription("Late Coming deduct");
            d1.setAmount(25000.0);
            d1.setImpact("Decrease");
            d1.setStatus("Approved");
            d1.setEffectiveMonth("May 2026");
            bonusDeductionRepository.save(d1);

            com.example.admindashboard.model.BonusDeduction d2 = new com.example.admindashboard.model.BonusDeduction();
            d2.setType("Deduct");
            d2.setCategory("Income Tax");
            d2.setDescription("TDS for May 2026");
            d2.setAmount(25000.0);
            d2.setImpact("Decrease");
            d2.setStatus("Pending");
            d2.setEffectiveMonth("May 2026");
            bonusDeductionRepository.save(d2);

            bonuses = bonusDeductionRepository.findAll();
        }
        model.addAttribute("bonusDeductions", bonuses);
        
        // Pass common data for dropdowns, etc.
        model.addAttribute("allUsers", userRepository.findAll());
        return "senior_hr-myspace";
    }

    // --- MY SPACE ROUTE (HR Employee) ---
    @PreAuthorize("hasAuthority('admin_dashboard_view')")
    @GetMapping("/senior_hr/employee")
    public String showSeniorHrEmployee(Model model, Principal principal) {
        List<com.example.admindashboard.model.User> allUsers = userRepository.findAll();
        model.addAttribute("allUsers", allUsers);
        return "senior_hr-employee";
    }
    
    // --- OFFBOARDING ROUTE ---
    @PostMapping("/senior_hr/resignation/offboard")
    public String completeOffboarding(@RequestParam("userId") Long userId, Principal principal) {
        if (principal != null) {
            User l3Hr = userRepository.findByUsername(principal.getName()).orElse(null);
            User exitingUser = userRepository.findById(userId).orElse(null);
            if (exitingUser != null && l3Hr != null) {
                // Update ResignationRequest if exists
                java.util.List<ResignationRequest> reqs = resignationRequestRepository.findByEmployee_Username(exitingUser.getUsername());
                for (ResignationRequest req : reqs) {
                    if ("PENDING_L3".equals(req.getStatus()) || "PENDING_L2".equals(req.getStatus())) {
                        req.setStatus("OFFBOARDED");
                        req.setL3ApprovedBy(l3Hr);
                        resignationRequestRepository.save(req);
                    }
                }
                
                // Update User status to EXITED to prevent login
                exitingUser.setStatus("EXITED");
                userRepository.save(exitingUser);
            }
        }
        return "redirect:/senior_hr/employee?offboarding=success";
    }

    // --- MY SPACE ROUTE (HR Performance) ---
    @PreAuthorize("hasAuthority('admin_dashboard_view')")
    @GetMapping("/senior_hr/performance")
    public String showSeniorHrPerformance(Model model, Principal principal) {
        // Mock data for Appraisal Cycles
        model.addAttribute("appraisals", java.util.List.of(
            new java.util.HashMap<String, Object>() {{ put("name", "Performance Appraisal 2026"); put("type", "Annual"); put("duration", "01 Apr 2026 - 31 Mar 2027"); put("emp", 112); put("prog", 85); put("status", "Active"); }},
            new java.util.HashMap<String, Object>() {{ put("name", "H1 Appraisal 2026"); put("type", "Half-yearly"); put("duration", "01 Apr 2026 - 31 Mar 2027"); put("emp", 112); put("prog", 85); put("status", "Active"); }},
            new java.util.HashMap<String, Object>() {{ put("name", "Q1 Appraisal 2026"); put("type", "Quarterly"); put("duration", "01 Apr 2026 - 31 Mar 2027"); put("emp", 112); put("prog", 100); put("status", "Completed"); }},
            new java.util.HashMap<String, Object>() {{ put("name", "Annual Appraisal 2026"); put("type", "Annual"); put("duration", "01 Apr 2026 - 31 Mar 2027"); put("emp", 112); put("prog", 85); put("status", "Active"); }},
            new java.util.HashMap<String, Object>() {{ put("name", "Q2 Appraisal 2026"); put("type", "Quarterly"); put("duration", "01 Apr 2026 - 31 Mar 2027"); put("emp", 112); put("prog", 85); put("status", "Active"); }}
        ));
        return "senior_hr-performance";
    }

    // --- MY SPACE ROUTE (HR Attendance) ---
    @PreAuthorize("hasAuthority('admin_dashboard_view')")
    @GetMapping("/senior_hr/attendance")
    public String showSeniorHrAttendance(Model model, Principal principal) {
        // Mock data for Daily Attendance
        model.addAttribute("dailyAttendance", java.util.List.of(
            new java.util.HashMap<String, Object>() {{ put("name", "Aman Verma"); put("id", "EMP114"); put("dept", "Engineering"); put("shift", "10:00 AM - 07:00 PM"); put("in", "09:58 AM"); put("out", "07:02 PM"); put("hrs", "9 hrs 04 min"); put("status", "Present"); put("rem", "-"); }},
            new java.util.HashMap<String, Object>() {{ put("name", "Neha Sharma"); put("id", "EMP114"); put("dept", "Engineering"); put("shift", "10:00 AM - 07:00 PM"); put("in", "10:15 AM"); put("out", "06:45 PM"); put("hrs", "8 hrs 30 min"); put("status", "Late"); put("rem", "Late by 15 min"); }},
            new java.util.HashMap<String, Object>() {{ put("name", "Vikram Joshi"); put("id", "EMP114"); put("dept", "Product"); put("shift", "10:00 AM - 07:00 PM"); put("in", "10:02 AM"); put("out", "07:01 PM"); put("hrs", "8 hrs 59 min"); put("status", "Present"); put("rem", "-"); }},
            new java.util.HashMap<String, Object>() {{ put("name", "Isha Patel"); put("id", "EMP114"); put("dept", "Design"); put("shift", "10:00 AM - 07:00 PM"); put("in", "10:05 AM"); put("out", "07:02 PM"); put("hrs", "8 hrs 57 min"); put("status", "Present"); put("rem", "-"); }},
            new java.util.HashMap<String, Object>() {{ put("name", "Arjun Nair"); put("id", "EMP114"); put("dept", "Design"); put("shift", "10:00 AM - 07:00 PM"); put("in", "-"); put("out", "-"); put("hrs", "-"); put("status", "Absent"); put("rem", "-"); }}
        ));
        
        // Mock data for Overrides
        model.addAttribute("overrides", java.util.List.of(
            new java.util.HashMap<String, Object>() {{ put("name", "Aman Verma"); put("id", "EMP114"); put("date", "25/05/2026"); put("type", "Update in/out Time"); put("orig", "10:15 AM - 06:45 PM"); put("upd", "09:15 AM - 07:00 PM"); put("reason", "Traffic Delay"); put("status", "Pending"); }},
            new java.util.HashMap<String, Object>() {{ put("name", "Esha Verma"); put("id", "EMP114"); put("date", "30/05/2026"); put("type", "Mark Full Day"); put("orig", "Absent"); put("upd", "Present"); put("reason", "WFH"); put("status", "Approved"); }},
            new java.util.HashMap<String, Object>() {{ put("name", "Arun Verma"); put("id", "EMP114"); put("date", "29/05/2026"); put("type", "Mark Full Day"); put("orig", "10:15 AM - 06:45 PM"); put("upd", "09:15 AM - 07:00 PM"); put("reason", "Health Issue"); put("status", "Pending"); }}
        ));
        return "senior_hr-attendance";
    }

    // --- WORKFLOW ROUTES HANDLED BY DASHBOARD CONTROLLER ---
    private void loadDashboardData(Model model) {
        model.addAttribute("showMySpace", true);
    }
    
}
