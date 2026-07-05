package com.example.admindashboard.controller;
import org.springframework.web.multipart.MultipartFile;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import com.example.admindashboard.model.Meeting;
import com.example.admindashboard.model.ResignationRequest;
import com.example.admindashboard.model.JobPosting;
import com.example.admindashboard.model.Referral;
import com.example.admindashboard.repository.MeetingRepository;
import com.example.admindashboard.repository.ResignationRequestRepository;
import com.example.admindashboard.repository.ServiceRequestRepository;
import com.example.admindashboard.repository.UserRepository;
import com.example.admindashboard.repository.JobPostingRepository;
import com.example.admindashboard.repository.ReferralRepository;
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
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.http.ResponseEntity;
import java.util.Map;
@Controller
public class SeniorDashboardController {

    @Autowired
    private ServiceRequestRepository serviceRequestRepository;

    @Autowired
    private MeetingRepository meetingRepository;

        @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private com.example.admindashboard.repository.RoleRepository roleRepository;
    
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
    private com.example.admindashboard.repository.AttendanceRepository attendanceRepository;

    @Autowired
    private com.example.admindashboard.repository.AttendanceRegularizationRepository attendanceRegularizationRepository;

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
    private com.example.admindashboard.repository.AppraisalCycleRepository appraisalCycleRepository;

    @Autowired
    private JobPostingRepository jobPostingRepository;

    @Autowired
    private ReferralRepository referralRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private com.example.admindashboard.service.EmailService emailService;

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
        return "redirect:/senior_hr/employee?tab=onboarding";
    }

    // --- LMS ROUTE ---
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

        // Calculate dynamic stats
        long totalRequestsCount = leaveRequests.size();
        long pendingRequestsCount = leaveRequests.stream().filter(lr -> "PENDING".equalsIgnoreCase(lr.getStatus())).count();
        long approvedRequestsCount = leaveRequests.stream().filter(lr -> "APPROVED".equalsIgnoreCase(lr.getStatus()) || "ACTIVE".equalsIgnoreCase(lr.getStatus())).count();
        long rejectedRequestsCount = leaveRequests.stream().filter(lr -> "REJECTED".equalsIgnoreCase(lr.getStatus())).count();

        // Fetch distinct departments
        List<String> departments = userRepository.findAll().stream()
            .map(u -> u.getEmployeeProfile() != null ? u.getEmployeeProfile().getDepartment() : null)
            .filter(d -> d != null && !d.trim().isEmpty())
            .distinct()
            .sorted()
            .collect(java.util.stream.Collectors.toList());

        model.addAttribute("leaveRequests", leaveRequests);
        model.addAttribute("todayLeaves", todayLeaves);
        model.addAttribute("pendingCount", pendingCount);
        model.addAttribute("todayOnLeaveCount", todayOnLeaveCount);
        model.addAttribute("totalCount", totalCount);
        
        model.addAttribute("totalRequestsCount", totalRequestsCount);
        model.addAttribute("pendingRequestsCount", pendingRequestsCount);
        model.addAttribute("approvedRequestsCount", approvedRequestsCount);
        model.addAttribute("rejectedRequestsCount", rejectedRequestsCount);
        model.addAttribute("departments", departments);

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
    @GetMapping("/senior_hr/payroll")
    public String showSeniorHrMySpace(Model model, Principal principal) {
        // Fetch existing salary structures, seed if empty
        List<com.example.admindashboard.model.SalaryStructure> structures = salaryStructureRepository.findByActiveTrue();
        
        if (structures.isEmpty()) {
            // Seed default structure based on standard formula if DB is empty
            com.example.admindashboard.model.SalaryStructure std = new com.example.admindashboard.model.SalaryStructure();
            std.setStructureName("Standard Template - Amit Sharma");
            std.setEffectiveFrom(java.time.LocalDate.now());
            std.setCtcAmount(960000.0);
            
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
            hra.setPercentageOfCtc(25.00);
            hra.setTaxable(true);
            std.addComponent(hra);

            // 3. Conveyance Allow.
            com.example.admindashboard.model.SalaryComponent conv = new com.example.admindashboard.model.SalaryComponent();
            conv.setComponentName("Conveyance Allow.");
            conv.setType("Earning");
            conv.setCategory("Statutory");
            conv.setCalculationFormula("Fixed Amount");
            conv.setAmount(1920.0);
            conv.setPercentageOfCtc(20.00);
            conv.setTaxable(true);
            std.addComponent(conv);

            // 4. Professional Tax
            com.example.admindashboard.model.SalaryComponent ptax = new com.example.admindashboard.model.SalaryComponent();
            ptax.setComponentName("Professional Tax");
            ptax.setType("Deduction");
            ptax.setCategory("Statutory");
            ptax.setCalculationFormula("Fixed Amount");
            ptax.setAmount(200.0);
            ptax.setPercentageOfCtc(4.00);
            ptax.setTaxable(true);
            std.addComponent(ptax);

            // 5. Provident Fund
            com.example.admindashboard.model.SalaryComponent pf = new com.example.admindashboard.model.SalaryComponent();
            pf.setComponentName("Provident Fund");
            pf.setType("Deduction");
            pf.setCategory("Allowance");
            pf.setCalculationFormula("12% of Basic");
            pf.setAmount(46000.0);
            pf.setPercentageOfCtc(0.02);
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
            com.example.admindashboard.model.User neha = userRepository.findAll().stream().filter(u -> u.getFullName() != null && u.getFullName().contains("Neha")).findFirst().orElse(null);
            p1.setUser(neha);
            payslipRepository.save(p1);

            com.example.admindashboard.model.User anil = userRepository.findAll().stream().filter(u -> u.getFullName() != null && u.getFullName().contains("Anil")).findFirst().orElse(null);
            com.example.admindashboard.model.Payslip p2 = new com.example.admindashboard.model.Payslip();
            p2.setPayMonth("May");
            p2.setPayYear(2026);
            p2.setCtcAnnual(600000.0);
            p2.setGrossPay(150000.0);
            p2.setTotalDeductions(15000.0);
            p2.setNetPay(135000.0);
            p2.setStatus("Not Processed");
            p2.setDepartment("Engineering");
            p2.setUser(anil);
            payslipRepository.save(p2);

            payslips = payslipRepository.findAll();
        }

        // Fetch and seed BonusDeductions
        List<com.example.admindashboard.model.BonusDeduction> bonuses = bonusDeductionRepository.findAll();
        if (bonuses.isEmpty()) {
            com.example.admindashboard.model.User neha = userRepository.findAll().stream().filter(u -> u.getFullName() != null && u.getFullName().contains("Neha")).findFirst().orElse(null);
            com.example.admindashboard.model.User anil = userRepository.findAll().stream().filter(u -> u.getFullName() != null && u.getFullName().contains("Anil")).findFirst().orElse(null);

            com.example.admindashboard.model.BonusDeduction b1 = new com.example.admindashboard.model.BonusDeduction();
            b1.setType("Bonus");
            b1.setCategory("Performance Bonus");
            b1.setDescription("Success of a project");
            b1.setAmount(25000.0);
            b1.setImpact("Increase");
            b1.setStatus("Approved");
            b1.setEffectiveMonth("May 2026");
            b1.setUser(neha);
            bonusDeductionRepository.save(b1);

            com.example.admindashboard.model.BonusDeduction d1 = new com.example.admindashboard.model.BonusDeduction();
            d1.setType("Deduct");
            d1.setCategory("Late Coming");
            d1.setDescription("Late Coming deduct");
            d1.setAmount(25000.0);
            d1.setImpact("Decrease");
            d1.setStatus("Approved");
            d1.setEffectiveMonth("May 2026");
            d1.setUser(neha);
            bonusDeductionRepository.save(d1);

            com.example.admindashboard.model.BonusDeduction d2 = new com.example.admindashboard.model.BonusDeduction();
            d2.setType("Deduct");
            d2.setCategory("Income Tax");
            d2.setDescription("TDS for May 2026");
            d2.setAmount(25000.0);
            d2.setImpact("Decrease");
            d2.setStatus("Pending");
            d2.setEffectiveMonth("May 2026");
            d2.setUser(anil);
            bonusDeductionRepository.save(d2);

            bonuses = bonusDeductionRepository.findAll();
        }

        // --- DASHBOARD REAL DATA AGGREGATION ---
        List<com.example.admindashboard.model.User> allUsers = userRepository.findAll();
        long totalEmployees = allUsers.stream().filter(u -> "ACTIVE".equalsIgnoreCase(u.getStatus()) && !"admin".equals(u.getUsername())).count();
        double totalNetPay = payslips.stream().mapToDouble(p -> p.getNetPay() != null ? p.getNetPay() : 0.0).sum();
        long pendingApprovals = bonuses.stream().filter(b -> "Pending".equalsIgnoreCase(b.getStatus())).count();
        
        model.addAttribute("totalEmployees", totalEmployees > 0 ? totalEmployees : allUsers.size());
        model.addAttribute("totalNetPay", totalNetPay);
        model.addAttribute("pendingApprovals", pendingApprovals);
        model.addAttribute("payrollStatus", "Draft");
        
        // Department Summary
        java.util.Map<String, java.util.Map<String, Object>> deptSummary = new java.util.HashMap<>();
        for (com.example.admindashboard.model.Payslip p : payslips) {
            String dept = p.getDepartment() != null ? p.getDepartment() : "Unassigned";
            deptSummary.putIfAbsent(dept, new java.util.HashMap<>());
            java.util.Map<String, Object> stats = deptSummary.get(dept);
            stats.put("employees", (Long) stats.getOrDefault("employees", 0L) + 1L);
            stats.put("gross", (Double) stats.getOrDefault("gross", 0.0) + (p.getGrossPay() != null ? p.getGrossPay() : 0.0));
            stats.put("deductions", (Double) stats.getOrDefault("deductions", 0.0) + (p.getTotalDeductions() != null ? p.getTotalDeductions() : 0.0));
            stats.put("net", (Double) stats.getOrDefault("net", 0.0) + (p.getNetPay() != null ? p.getNetPay() : 0.0));
            
            boolean allProcessed = "Processed".equalsIgnoreCase(p.getStatus());
            boolean existingProcessed = (Boolean) stats.getOrDefault("allProcessed", true);
            stats.put("allProcessed", allProcessed && existingProcessed);
        }
        
        List<java.util.Map<String, Object>> deptList = new java.util.ArrayList<>();
        double totalGrossAll = 0;
        double totalDedAll = 0;
        double totalNetAll = 0;
        long totalEmpAll = 0;
        
        for (java.util.Map.Entry<String, java.util.Map<String, Object>> entry : deptSummary.entrySet()) {
            java.util.Map<String, Object> row = new java.util.HashMap<>(entry.getValue());
            row.put("department", entry.getKey());
            row.put("status", (Boolean) row.get("allProcessed") ? "Generated" : "Draft");
            deptList.add(row);
            
            totalGrossAll += (Double) row.get("gross");
            totalDedAll += (Double) row.get("deductions");
            totalNetAll += (Double) row.get("net");
            totalEmpAll += (Long) row.get("employees");
        }
        
        // Ensure some mockup data if empty
        if (deptList.isEmpty()) {
            java.util.Map<String, Object> m = new java.util.HashMap<>();
            m.put("department", "Engineering"); m.put("employees", 165L); m.put("gross", 248500.0); m.put("deductions", 50000.0); m.put("net", 129000.0); m.put("status", "Generated");
            deptList.add(m);
            totalGrossAll += 248500.0; totalDedAll += 50000.0; totalNetAll += 129000.0; totalEmpAll += 165L;
        }

        model.addAttribute("deptSummary", deptList);
        model.addAttribute("totalGrossAll", totalGrossAll);
        model.addAttribute("totalDedAll", totalDedAll);
        model.addAttribute("totalNetAll", totalNetAll);
        model.addAttribute("totalEmpAll", totalEmpAll);

        // Recent activities
        List<java.util.Map<String, String>> activities = new java.util.ArrayList<>();
        if (!payslips.isEmpty()) {
            java.util.Map<String, String> a1 = new java.util.HashMap<>();
            a1.put("activity", "Payroll Draft Created");
            a1.put("month", payslips.get(0).getPayMonth() + " " + payslips.get(0).getPayYear());
            a1.put("time", "20 May, 10:15 AM");
            a1.put("status", "Completed");
            activities.add(a1);
        } else {
            java.util.Map<String, String> a1 = new java.util.HashMap<>();
            a1.put("activity", "Payroll Draft Created"); a1.put("month", "May 2026"); a1.put("time", "20 May, 10:15 AM"); a1.put("status", "Completed");
            activities.add(a1);
        }
        model.addAttribute("recentActivities", activities);
        
        // Bonuses Stats
        double totalBonusAmt = bonuses.stream().filter(b -> "Bonus".equalsIgnoreCase(b.getType()) || "Increase".equalsIgnoreCase(b.getImpact())).mapToDouble(b -> b.getAmount() != null ? b.getAmount() : 0.0).sum();
        double totalDeductAmt = bonuses.stream().filter(b -> "Deduct".equalsIgnoreCase(b.getType()) || "Deduction".equalsIgnoreCase(b.getType()) || "Decrease".equalsIgnoreCase(b.getImpact())).mapToDouble(b -> b.getAmount() != null ? b.getAmount() : 0.0).sum();
        double netImpactVal = totalBonusAmt - totalDeductAmt;
        long pendingBonusApprovals = bonuses.stream().filter(b -> "Pending".equalsIgnoreCase(b.getStatus())).count();

        model.addAttribute("totalBonusAmt", totalBonusAmt);
        model.addAttribute("totalDeductAmt", totalDeductAmt);
        model.addAttribute("netImpactVal", Math.abs(netImpactVal));
        model.addAttribute("netImpactLabel", netImpactVal >= 0 ? "Bonus > Deduction" : "Deduction > Bonus");
        model.addAttribute("pendingBonusApprovals", pendingBonusApprovals);

        // Pass common data
        model.addAttribute("salaryStructures", structures);
        model.addAttribute("payslips", payslips);
        model.addAttribute("bonusDeductions", bonuses);
        model.addAttribute("allUsers", allUsers);
        
        return "senior_hr-payroll";
    }

    @GetMapping("/senior_hr/payslip/pdf/{id}")
    public void downloadPayslipPdf(@org.springframework.web.bind.annotation.PathVariable Long id, jakarta.servlet.http.HttpServletResponse response) throws java.io.IOException {
        com.example.admindashboard.model.Payslip slip = payslipRepository.findById(id).orElse(null);
        if (slip == null) {
            response.sendError(404, "Payslip not found");
            return;
        }

        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "attachment; filename=payslip_" + id + ".pdf");

        try {
            com.lowagie.text.Document document = new com.lowagie.text.Document(com.lowagie.text.PageSize.A4, 30, 30, 30, 30);
            com.lowagie.text.pdf.PdfWriter.getInstance(document, response.getOutputStream());
            document.open();

            // Colors
            java.awt.Color primaryColor = new java.awt.Color(0, 45, 114); // Deep blue #002d72
            java.awt.Color darkGray = new java.awt.Color(50, 50, 50);
            java.awt.Color lightGray = new java.awt.Color(240, 240, 240);

            // Fonts
            com.lowagie.text.Font titleFont = new com.lowagie.text.Font(com.lowagie.text.Font.HELVETICA, 18, com.lowagie.text.Font.BOLD, primaryColor);
            com.lowagie.text.Font subTitleFont = new com.lowagie.text.Font(com.lowagie.text.Font.HELVETICA, 9, com.lowagie.text.Font.NORMAL, java.awt.Color.GRAY);
            com.lowagie.text.Font sectionHeaderFont = new com.lowagie.text.Font(com.lowagie.text.Font.HELVETICA, 11, com.lowagie.text.Font.BOLD, darkGray);
            com.lowagie.text.Font boldNameFont = new com.lowagie.text.Font(com.lowagie.text.Font.HELVETICA, 11, com.lowagie.text.Font.BOLD, java.awt.Color.BLACK);
            com.lowagie.text.Font labelFont = new com.lowagie.text.Font(com.lowagie.text.Font.HELVETICA, 8, com.lowagie.text.Font.NORMAL, java.awt.Color.GRAY);
            com.lowagie.text.Font valFont = new com.lowagie.text.Font(com.lowagie.text.Font.HELVETICA, 9, com.lowagie.text.Font.BOLD, java.awt.Color.BLACK);
            com.lowagie.text.Font tableHeaderFont = new com.lowagie.text.Font(com.lowagie.text.Font.HELVETICA, 9, com.lowagie.text.Font.BOLD, darkGray);
            com.lowagie.text.Font tableCellFont = new com.lowagie.text.Font(com.lowagie.text.Font.HELVETICA, 9, com.lowagie.text.Font.NORMAL, java.awt.Color.BLACK);
            com.lowagie.text.Font tableCellBoldFont = new com.lowagie.text.Font(com.lowagie.text.Font.HELVETICA, 9, com.lowagie.text.Font.BOLD, java.awt.Color.BLACK);

            // Header Section
            com.lowagie.text.Paragraph title = new com.lowagie.text.Paragraph("Provisional Payslip " + slip.getPayMonth() + " " + slip.getPayYear(), titleFont);
            title.setSpacingAfter(4);
            document.add(title);
            document.add(new com.lowagie.text.Paragraph("Whitecircle Group", valFont));
            com.lowagie.text.Paragraph subHeader = new com.lowagie.text.Paragraph("Shahdol, Madhya Pradesh 484001", subTitleFont);
            subHeader.setSpacingAfter(8);
            document.add(subHeader);

            // Separator Line
            com.lowagie.text.pdf.draw.LineSeparator ls = new com.lowagie.text.pdf.draw.LineSeparator();
            ls.setLineColor(new java.awt.Color(200, 200, 200));
            document.add(ls);
            document.add(new com.lowagie.text.Paragraph(" "));

            // Employee Name
            com.lowagie.text.Paragraph empNamePara = new com.lowagie.text.Paragraph(slip.getUser() != null ? slip.getUser().getFullName() : "Employee Name", boldNameFont);
            empNamePara.setSpacingAfter(10);
            document.add(empNamePara);

            // Employee Profile Grid (4 Columns)
            com.lowagie.text.pdf.PdfPTable profileTable = new com.lowagie.text.pdf.PdfPTable(4);
            profileTable.setWidthPercentage(100);
            profileTable.setSpacingAfter(15);
            
            addDetailCell(profileTable, "Employee ID", "EMP" + (slip.getUser() != null ? slip.getUser().getId() : "114"), labelFont, valFont);
            addDetailCell(profileTable, "Date Joined", (slip.getUser() != null && slip.getUser().getEmployeeProfile() != null && slip.getUser().getEmployeeProfile().getJoiningDate() != null ? slip.getUser().getEmployeeProfile().getJoiningDate().format(java.time.format.DateTimeFormatter.ofPattern("dd MMM yyyy")) : "N/A"), labelFont, valFont);
            addDetailCell(profileTable, "Department", (slip.getDepartment() != null ? slip.getDepartment() : "Engineering"), labelFont, valFont);
            addDetailCell(profileTable, "Sub-Department", "N/A", labelFont, valFont);

            addDetailCell(profileTable, "Designation", (slip.getUser() != null && slip.getUser().getEmployeeProfile() != null && slip.getUser().getEmployeeProfile().getDesignation() != null ? slip.getUser().getEmployeeProfile().getDesignation() : "N/A"), labelFont, valFont);
            addDetailCell(profileTable, "Payment Mode", "Bank Transfer", labelFont, valFont);
            addDetailCell(profileTable, "PAN", (slip.getUser() != null && slip.getUser().getEmployeeProfile() != null && slip.getUser().getEmployeeProfile().getPanNo() != null ? slip.getUser().getEmployeeProfile().getPanNo() : "N/A"), labelFont, valFont);
            addDetailCell(profileTable, "UAN", "10599890812", labelFont, valFont);

            addDetailCell(profileTable, "PF No.", "MP/SHA/000/12345/2132134", labelFont, valFont);
            addDetailCell(profileTable, "ESI No.", "ESISHA125678", labelFont, valFont);
            addDetailCell(profileTable, "Location", (slip.getUser() != null && slip.getUser().getEmployeeProfile() != null && slip.getUser().getEmployeeProfile().getWorkLocation() != null ? slip.getUser().getEmployeeProfile().getWorkLocation() : "Shahdol"), labelFont, valFont);
            addDetailCell(profileTable, "Monthly Salary", String.format("%,.2f", slip.getGrossPay()), labelFont, valFont);

            document.add(profileTable);
            document.add(ls);
            document.add(new com.lowagie.text.Paragraph(" "));

            // Salary Details Header
            com.lowagie.text.Paragraph salDetailsHeader = new com.lowagie.text.Paragraph("Salary Details", sectionHeaderFont);
            salDetailsHeader.setSpacingAfter(10);
            document.add(salDetailsHeader);

            // Days Info Grid (4 Columns)
            com.lowagie.text.pdf.PdfPTable daysTable = new com.lowagie.text.pdf.PdfPTable(4);
            daysTable.setWidthPercentage(100);
            daysTable.setSpacingAfter(15);
            addDetailCell(daysTable, "Actual Payable Days", "26.0", labelFont, valFont);
            addDetailCell(daysTable, "Total Working Days", "26.0", labelFont, valFont);
            addDetailCell(daysTable, "Loss of Pay Days", "0.0", labelFont, valFont);
            addDetailCell(daysTable, "Days Payable", "26", labelFont, valFont);
            document.add(daysTable);

            document.add(ls);
            document.add(new com.lowagie.text.Paragraph(" "));

            // Side-by-Side parent layout table
            com.lowagie.text.pdf.PdfPTable mainLayoutTable = new com.lowagie.text.pdf.PdfPTable(2);
            mainLayoutTable.setWidthPercentage(100);
            mainLayoutTable.setSpacingAfter(20);

            // Left Cell: Earnings Table
            com.lowagie.text.pdf.PdfPCell leftCell = new com.lowagie.text.pdf.PdfPCell();
            leftCell.setBorder(com.lowagie.text.pdf.PdfPCell.NO_BORDER);
            leftCell.setPaddingRight(15f);

            com.lowagie.text.pdf.PdfPTable earnTable = new com.lowagie.text.pdf.PdfPTable(2);
            earnTable.setWidthPercentage(100);
            
            // Earnings Header
            com.lowagie.text.pdf.PdfPCell eh1 = new com.lowagie.text.pdf.PdfPCell(new com.lowagie.text.Paragraph("Earnings", tableHeaderFont));
            eh1.setBorder(com.lowagie.text.pdf.PdfPCell.BOTTOM);
            eh1.setPaddingBottom(5f);
            com.lowagie.text.pdf.PdfPCell eh2 = new com.lowagie.text.pdf.PdfPCell(new com.lowagie.text.Paragraph("Amount (INR)", tableHeaderFont));
            eh2.setBorder(com.lowagie.text.pdf.PdfPCell.BOTTOM);
            eh2.setPaddingBottom(5f);
            eh2.setHorizontalAlignment(com.lowagie.text.Element.ALIGN_RIGHT);
            earnTable.addCell(eh1);
            earnTable.addCell(eh2);

            // Earnings Rows
            double gross = slip.getGrossPay() != null ? slip.getGrossPay() : 0.0;
            double basic = gross * 0.4;
            double hra = basic * 0.5;
            double special = gross - basic - hra;

            addTableCell(earnTable, "Basic Salary (40% of Gross)", String.format("%,.2f", basic), tableCellFont, false);
            addTableCell(earnTable, "HRA (50% of Basic)", String.format("%,.2f", hra), tableCellFont, false);
            addTableCell(earnTable, "Special Allowance", String.format("%,.2f", special), tableCellFont, false);
            addTableCell(earnTable, "Total Earnings (A)", String.format("%,.2f", gross), tableCellBoldFont, true);

            leftCell.addElement(earnTable);
            mainLayoutTable.addCell(leftCell);

            // Right Cell: Deductions Table
            com.lowagie.text.pdf.PdfPCell rightCell = new com.lowagie.text.pdf.PdfPCell();
            rightCell.setBorder(com.lowagie.text.pdf.PdfPCell.NO_BORDER);
            rightCell.setPaddingLeft(15f);

            com.lowagie.text.pdf.PdfPTable dedTable = new com.lowagie.text.pdf.PdfPTable(2);
            dedTable.setWidthPercentage(100);

            // Deductions Header
            com.lowagie.text.pdf.PdfPCell dh1 = new com.lowagie.text.pdf.PdfPCell(new com.lowagie.text.Paragraph("Deductions & Taxes", tableHeaderFont));
            dh1.setBorder(com.lowagie.text.pdf.PdfPCell.BOTTOM);
            dh1.setPaddingBottom(5f);
            com.lowagie.text.pdf.PdfPCell dh2 = new com.lowagie.text.pdf.PdfPCell(new com.lowagie.text.Paragraph("Amount (INR)", tableHeaderFont));
            dh2.setBorder(com.lowagie.text.pdf.PdfPCell.BOTTOM);
            dh2.setPaddingBottom(5f);
            dh2.setHorizontalAlignment(com.lowagie.text.Element.ALIGN_RIGHT);
            dedTable.addCell(dh1);
            dedTable.addCell(dh2);

            // Deductions Rows
            double totalDed = slip.getTotalDeductions() != null ? slip.getTotalDeductions() : 0.0;
            double pt = totalDed > 200 ? 200 : totalDed;
            double it = totalDed - pt;

            addTableCell(dedTable, "Professional Tax", String.format("%,.2f", pt), tableCellFont, false);
            addTableCell(dedTable, "Total Income Tax (TDS)", String.format("%,.2f", it), tableCellFont, false);
            addTableCell(dedTable, "Total Deductions (B)", String.format("%,.2f", totalDed), tableCellBoldFont, true);

            rightCell.addElement(dedTable);
            mainLayoutTable.addCell(rightCell);

            document.add(mainLayoutTable);
            document.add(ls);
            document.add(new com.lowagie.text.Paragraph(" "));

            // Net salary payable footer
            com.lowagie.text.pdf.PdfPTable footerTable = new com.lowagie.text.pdf.PdfPTable(2);
            footerTable.setWidthPercentage(100);
            footerTable.setWidths(new int[]{200, 400});
            footerTable.setSpacingAfter(15);

            addFooterCell(footerTable, "Net Salary Payable (A - B)", String.format("%,.2f", (slip.getNetPay() != null ? slip.getNetPay() : 0.0)), tableCellBoldFont);
            addFooterCell(footerTable, "Net Salary in words", numberToWords(Math.round(slip.getNetPay() != null ? slip.getNetPay() : 0.0)) + " only", tableCellBoldFont);
            
            document.add(footerTable);
            document.add(ls);
            document.add(new com.lowagie.text.Paragraph(" "));

            com.lowagie.text.Paragraph note = new com.lowagie.text.Paragraph("Note: All amounts displayed in this payslip are in INR", tableCellBoldFont);
            document.add(note);

            document.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void addDetailCell(com.lowagie.text.pdf.PdfPTable table, String label, String value, com.lowagie.text.Font lFont, com.lowagie.text.Font vFont) {
        com.lowagie.text.pdf.PdfPCell cell = new com.lowagie.text.pdf.PdfPCell();
        cell.setBorder(com.lowagie.text.pdf.PdfPCell.NO_BORDER);
        cell.setPaddingBottom(8f);
        cell.addElement(new com.lowagie.text.Paragraph(label, lFont));
        cell.addElement(new com.lowagie.text.Paragraph(value, vFont));
        table.addCell(cell);
    }

    private void addTableCell(com.lowagie.text.pdf.PdfPTable table, String label, String value, com.lowagie.text.Font font, boolean isTotalRow) {
        com.lowagie.text.pdf.PdfPCell cell1 = new com.lowagie.text.pdf.PdfPCell(new com.lowagie.text.Paragraph(label, font));
        com.lowagie.text.pdf.PdfPCell cell2 = new com.lowagie.text.pdf.PdfPCell(new com.lowagie.text.Paragraph(value, font));
        cell2.setHorizontalAlignment(com.lowagie.text.Element.ALIGN_RIGHT);

        if (isTotalRow) {
            cell1.setBorder(com.lowagie.text.pdf.PdfPCell.TOP);
            cell2.setBorder(com.lowagie.text.pdf.PdfPCell.TOP);
            cell1.setPaddingTop(5f);
            cell2.setPaddingTop(5f);
        } else {
            cell1.setBorder(com.lowagie.text.pdf.PdfPCell.NO_BORDER);
            cell2.setBorder(com.lowagie.text.pdf.PdfPCell.NO_BORDER);
        }
        cell1.setPaddingBottom(5f);
        cell2.setPaddingBottom(5f);
        table.addCell(cell1);
        table.addCell(cell2);
    }

    private void addFooterCell(com.lowagie.text.pdf.PdfPTable table, String label, String value, com.lowagie.text.Font font) {
        com.lowagie.text.pdf.PdfPCell cell1 = new com.lowagie.text.pdf.PdfPCell(new com.lowagie.text.Paragraph(label, font));
        com.lowagie.text.pdf.PdfPCell cell2 = new com.lowagie.text.pdf.PdfPCell(new com.lowagie.text.Paragraph(value, font));
        cell1.setBorder(com.lowagie.text.pdf.PdfPCell.NO_BORDER);
        cell2.setBorder(com.lowagie.text.pdf.PdfPCell.NO_BORDER);
        cell1.setPaddingBottom(4f);
        cell2.setPaddingBottom(4f);
        table.addCell(cell1);
        table.addCell(cell2);
    }

    private static String numberToWords(long number) {
        if (number == 0) return "Zero";
        String[] units = {"", "One", "Two", "Three", "Four", "Five", "Six", "Seven", "Eight", "Nine", "Ten", "Eleven", "Twelve", "Thirteen", "Fourteen", "Fifteen", "Sixteen", "Seventeen", "Eighteen", "Nineteen"};
        String[] tens = {"", "", "Twenty", "Thirty", "Forty", "Fifty", "Sixty", "Seventy", "Eighty", "Ninety"};
        
        String words = "";
        
        if ((number / 10000000) > 0) {
            words += numberToWords(number / 10000000) + " Crore ";
            number %= 10000000;
        }
        if ((number / 100000) > 0) {
            words += numberToWords(number / 100000) + " Lakh ";
            number %= 100000;
        }
        if ((number / 1000) > 0) {
            words += numberToWords(number / 1000) + " Thousand ";
            number %= 1000;
        }
        if ((number / 100) > 0) {
            words += numberToWords(number / 100) + " Hundred ";
            number %= 100;
        }
        if (number > 0) {
            if (number < 20) {
                words += units[(int) number];
            } else {
                words += tens[(int) (number / 10)] + " " + units[(int) (number % 10)];
            }
        }
        return words.trim();
    }

    @PostMapping("/senior_hr/payroll/add_bonus")
    public String addBonusDeduction(
            @org.springframework.web.bind.annotation.RequestParam Long userId,
            @org.springframework.web.bind.annotation.RequestParam String type,
            @org.springframework.web.bind.annotation.RequestParam String category,
            @org.springframework.web.bind.annotation.RequestParam Double amount,
            @org.springframework.web.bind.annotation.RequestParam String impact,
            @org.springframework.web.bind.annotation.RequestParam String effectiveMonth,
            @org.springframework.web.bind.annotation.RequestParam(required = false) String description) {
        
        com.example.admindashboard.model.User user = userRepository.findById(userId).orElse(null);
        if (user != null) {
            com.example.admindashboard.model.BonusDeduction bd = new com.example.admindashboard.model.BonusDeduction();
            bd.setUser(user);
            bd.setType(type);
            bd.setCategory(category);
            bd.setAmount(amount);
            bd.setImpact(impact);
            bd.setEffectiveMonth(effectiveMonth);
            bd.setDescription(description != null ? description : "");
            bd.setStatus("Approved");
            bonusDeductionRepository.save(bd);
        }
        
        return "redirect:/senior_hr/payroll";
    }

    // --- MY SPACE ROUTE (HR Employee) ---
    @PreAuthorize("hasAuthority('admin_dashboard_view')")
    @GetMapping("/senior_hr/employee")
    public String showSeniorHrEmployee(Model model, Principal principal) {
        List<com.example.admindashboard.model.User> allUsers = userRepository.findAll();
        
        long totalEmp = allUsers.size();
        long activeEmp = allUsers.stream().filter(u -> "ACTIVE".equalsIgnoreCase(u.getStatus())).count();
        long noticeEmp = allUsers.stream().filter(u -> "ON NOTICE".equalsIgnoreCase(u.getStatus())).count();
        long offboardedEmp = allUsers.stream().filter(u -> "EXITED".equalsIgnoreCase(u.getStatus())).count();
        // Since candidates are hardcoded in the frontend or managed separately, we'll keep onboarding to a dummy count for now, or you can calculate it based on a candidate table if it exists.
        List<com.example.admindashboard.model.User> onboardingList = allUsers.stream()
            .filter(u -> "ONBOARDING".equalsIgnoreCase(u.getStatus()) || 
                         ("ACTIVE".equalsIgnoreCase(u.getStatus()) && u.getEmployeeProfile() != null && u.getEmployeeProfile().isOnboardingCompleted()))
            .collect(Collectors.toList());
        long onboardingEmp = onboardingList.size();
        model.addAttribute("candidates", onboardingList); 
        
        java.util.Map<String, Long> deptCounts = allUsers.stream()
            .filter(u -> u.getEmployeeProfile() != null && u.getEmployeeProfile().getDepartment() != null)
            .collect(Collectors.groupingBy(u -> u.getEmployeeProfile().getDepartment(), Collectors.counting()));
            
        // Calculate department stats
        long totalWithDept = allUsers.stream()
            .filter(u -> u.getEmployeeProfile() != null && u.getEmployeeProfile().getDepartment() != null)
            .count();
        double totalD = totalWithDept > 0 ? (double)totalWithDept : 1.0;

        long eng = deptCounts.getOrDefault("Engineering", 0L);
        long prod = deptCounts.getOrDefault("Product", 0L);
        long des = deptCounts.getOrDefault("Design", 0L);
        long mkt = deptCounts.getOrDefault("Marketing", 0L);
        long sales = deptCounts.getOrDefault("Sales", 0L);
        long hr = deptCounts.getOrDefault("HR", 0L);
        long fin = deptCounts.getOrDefault("Finance", 0L);
        long others = totalWithDept - (eng + prod + des + mkt + sales + hr + fin);
        if (others < 0) others = 0;

        long engPct = Math.round((eng / totalD) * 100);
        long prodPct = Math.round((prod / totalD) * 100);
        long desPct = Math.round((des / totalD) * 100);
        long mktPct = Math.round((mkt / totalD) * 100);
        long salesPct = Math.round((sales / totalD) * 100);
        long hrPct = Math.round((hr / totalD) * 100);
        long finPct = Math.round((fin / totalD) * 100);
        long othersPct = 100 - (engPct + prodPct + desPct + mktPct + salesPct + hrPct + finPct);
        if (othersPct < 0) othersPct = 0;

        model.addAttribute("totalEmployees", totalEmp);
        model.addAttribute("activeEmployees", activeEmp);
        model.addAttribute("noticePeriodEmployees", noticeEmp);
        model.addAttribute("offboardedEmployees", offboardedEmp);
        model.addAttribute("onboardingEmployees", onboardingEmp);
        model.addAttribute("deptCounts", deptCounts);

        model.addAttribute("engPct", engPct);
        model.addAttribute("prodPct", prodPct);
        model.addAttribute("desPct", desPct);
        model.addAttribute("mktPct", mktPct);
        model.addAttribute("salesPct", salesPct);
        model.addAttribute("hrPct", hrPct);
        model.addAttribute("finPct", finPct);
        model.addAttribute("othersPct", othersPct);
        
        model.addAttribute("allUsers", allUsers);
        return "senior_hr-employee";
    }
    @PostMapping("/senior_hr/onboard")
    public String submitOnboarding(@org.springframework.web.bind.annotation.ModelAttribute com.example.admindashboard.model.EmployeeProfile employeeProfile,
                                   @RequestParam(required=false) Long id,
                                   @RequestParam(required=false) String prefix,
                                   @RequestParam(required=false) String firstName,
                                   @RequestParam(required=false) String middleName,
                                   @RequestParam(required=false) String lastName,
                                   @RequestParam(required=false) String personalEmail,
                                   @RequestParam(required=false) String employeeCode,
                                   @RequestParam(required=false) @org.springframework.format.annotation.DateTimeFormat(iso = org.springframework.format.annotation.DateTimeFormat.ISO.DATE) java.time.LocalDate probationCompletionDate,
                                   @RequestParam(required=false) String physicallyChallenged,
                                   @RequestParam(required=false) MultipartFile photoDoc,
                                   @RequestParam(required=false) MultipartFile resumeDoc,
                                   @RequestParam(required=false) MultipartFile aadhaarDoc,
                                   @RequestParam(required=false) MultipartFile panDoc,
                                   @RequestParam(required=false) MultipartFile educationalDoc,
                                   @RequestParam(required=false) MultipartFile experienceDoc,
                                   @RequestParam(required=false) MultipartFile offerDoc,
                                   org.springframework.web.servlet.mvc.support.RedirectAttributes redirectAttrs) {
        
        User user;
        boolean isNew = false;
        String randomPassword = null;
        
        if (id != null) {
            user = userRepository.findById(id).orElse(new User());
        } else {
            user = new User();
            isNew = true;
            user.setUsername(employeeCode != null && !employeeCode.isEmpty() ? employeeCode.toUpperCase() : "TEMP" + (int)(Math.random() * 1000));
        }

        // Set role to EMPLOYEE if not set
        if (user.getRole() == null) {
            roleRepository.findByRoleName("EMPLOYEE").ifPresent(user::setRole);
        }

        // Generate temporary password for the email and set status to ACTIVE (Complete Onboarding)
        randomPassword = java.util.UUID.randomUUID().toString().substring(0, 8);
        user.setPassword(passwordEncoder.encode(randomPassword));
        user.setStatus("ACTIVE");

        // Build full name
        String fullName = (firstName != null ? firstName : "") + " " + (lastName != null ? lastName : "");
        user.setFullName(fullName.trim());
        if(personalEmail != null) user.setEmail(personalEmail);
        
        com.example.admindashboard.model.EmployeeProfile existingProfile = user.getEmployeeProfile();
        if (existingProfile == null) {
            existingProfile = employeeProfile != null ? employeeProfile : new com.example.admindashboard.model.EmployeeProfile();
            existingProfile.setUser(user);
            user.setEmployeeProfile(existingProfile);
        } else if (employeeProfile != null) {
            org.springframework.beans.BeanUtils.copyProperties(employeeProfile, existingProfile, "id", "user", "photoDocPath", "resumeDocPath", "aadhaarDocPath", "panDocPath", "educationalDocPath", "experienceDocPath", "offerDocPath");
            if (prefix != null) existingProfile.setPrefix(prefix);
            if (firstName != null) existingProfile.setFirstName(firstName);
            if (middleName != null) existingProfile.setMiddleName(middleName);
            if (lastName != null) existingProfile.setLastName(lastName);
            if (employeeCode != null) existingProfile.setEmployeeCode(employeeCode);
            if (probationCompletionDate != null) existingProfile.setProbationCompletionDate(probationCompletionDate);
            if (physicallyChallenged != null) existingProfile.setPhysicallyChallenged(physicallyChallenged);
        }
        
        // Handle file uploads
        String uploadDir = "uploads/documents/";
        try {
            Path uploadPath = Paths.get(uploadDir);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }
            
            if (photoDoc != null && !photoDoc.isEmpty()) {
                String fileName = user.getUsername() + "_photo_" + photoDoc.getOriginalFilename();
                Files.copy(photoDoc.getInputStream(), uploadPath.resolve(fileName), StandardCopyOption.REPLACE_EXISTING);
                existingProfile.setPhotoDocPath(uploadDir + fileName);
            }
            if (resumeDoc != null && !resumeDoc.isEmpty()) {
                String fileName = user.getUsername() + "_resume_" + resumeDoc.getOriginalFilename();
                Files.copy(resumeDoc.getInputStream(), uploadPath.resolve(fileName), StandardCopyOption.REPLACE_EXISTING);
                existingProfile.setResumeDocPath(uploadDir + fileName);
            }
            if (aadhaarDoc != null && !aadhaarDoc.isEmpty()) {
                String fileName = user.getUsername() + "_aadhaar_" + aadhaarDoc.getOriginalFilename();
                Files.copy(aadhaarDoc.getInputStream(), uploadPath.resolve(fileName), StandardCopyOption.REPLACE_EXISTING);
                existingProfile.setAadhaarDocPath(uploadDir + fileName);
            }
            if (panDoc != null && !panDoc.isEmpty()) {
                String fileName = user.getUsername() + "_pan_" + panDoc.getOriginalFilename();
                Files.copy(panDoc.getInputStream(), uploadPath.resolve(fileName), StandardCopyOption.REPLACE_EXISTING);
                existingProfile.setPanDocPath(uploadDir + fileName);
            }
            if (educationalDoc != null && !educationalDoc.isEmpty()) {
                String fileName = user.getUsername() + "_edu_" + educationalDoc.getOriginalFilename();
                Files.copy(educationalDoc.getInputStream(), uploadPath.resolve(fileName), StandardCopyOption.REPLACE_EXISTING);
                existingProfile.setEducationalDocPath(uploadDir + fileName);
            }
            if (experienceDoc != null && !experienceDoc.isEmpty()) {
                String fileName = user.getUsername() + "_exp_" + experienceDoc.getOriginalFilename();
                Files.copy(experienceDoc.getInputStream(), uploadPath.resolve(fileName), StandardCopyOption.REPLACE_EXISTING);
                existingProfile.setExperienceDocPath(uploadDir + fileName);
            }
            if (offerDoc != null && !offerDoc.isEmpty()) {
                String fileName = user.getUsername() + "_offer_" + offerDoc.getOriginalFilename();
                Files.copy(offerDoc.getInputStream(), uploadPath.resolve(fileName), StandardCopyOption.REPLACE_EXISTING);
                existingProfile.setOfferDocPath(uploadDir + fileName);
            }
        } catch (java.io.IOException e) {
            e.printStackTrace();
        }

        existingProfile.setOnboardingCompleted(true);
        userRepository.save(user);
        
        // Send email
        if (personalEmail != null && !personalEmail.isEmpty()) {
            emailService.sendOnboardingEmail(personalEmail, user.getFullName(), user.getUsername(), randomPassword, existingProfile.getOfficialEmail());
        }
        
        redirectAttrs.addFlashAttribute("successMessage", "Onboarding Completed Successfully! Login credentials sent via email.");
        return "redirect:/senior_hr/employee?tab=onboarding";
    }

    

    // --- DELETE EMPLOYEE ROUTE ---
    @PostMapping("/senior_hr/employee/delete/{id}")
    public String deleteEmployee(@org.springframework.web.bind.annotation.PathVariable Long id, org.springframework.web.servlet.mvc.support.RedirectAttributes redirectAttrs) {
        if(userRepository.existsById(id)) {
            userRepository.deleteById(id);
            redirectAttrs.addFlashAttribute("successMessage", "Candidate deleted successfully!");
        }
        return "redirect:/senior_hr/employee?tab=onboarding";
    }

    // --- GET EMPLOYEE API (For Edit) ---
    @GetMapping("/senior_hr/api/employee/{id}")
    @org.springframework.web.bind.annotation.ResponseBody
    public java.util.Map<String, Object> getEmployeeApi(@org.springframework.web.bind.annotation.PathVariable Long id) {
        User user = userRepository.findById(id).orElse(null);
        if (user == null) return java.util.Collections.emptyMap();
        
        java.util.Map<String, Object> map = new java.util.HashMap<>();
        map.put("id", user.getId());
        map.put("username", user.getUsername());
        map.put("fullName", user.getFullName());
        map.put("email", user.getEmail());
        
        com.example.admindashboard.model.EmployeeProfile ep = user.getEmployeeProfile();
        if (ep != null) {
            map.put("prefix", ep.getPrefix());
            map.put("firstName", ep.getFirstName());
            map.put("middleName", ep.getMiddleName());
            map.put("lastName", ep.getLastName());
            map.put("employeeCode", ep.getEmployeeCode());
            map.put("mobileNumber", ep.getMobileNumber());
            map.put("emergencyPhone", ep.getEmergencyPhone());
            if(ep.getDob() != null) map.put("dob", ep.getDob().toString());
            map.put("gender", ep.getGender());
            map.put("maritalStatus", ep.getMaritalStatus());
            map.put("nationality", ep.getNationality());
            map.put("fatherName", ep.getFatherName());
            map.put("motherName", ep.getMotherName());
            map.put("spouseName", ep.getSpouseName());
            if(ep.getJoiningDate() != null) map.put("joiningDate", ep.getJoiningDate().toString());
            map.put("probationPeriod", ep.getProbationPeriod());
            if(ep.getProbationCompletionDate() != null) map.put("probationCompletionDate", ep.getProbationCompletionDate().toString());
            map.put("bloodGroup", ep.getBloodGroup());
            map.put("physicallyChallenged", ep.getPhysicallyChallenged());
            map.put("department", ep.getDepartment());
            map.put("designation", ep.getDesignation());
            map.put("officialEmail", ep.getOfficialEmail());
            map.put("workLocation", ep.getWorkLocation());
            map.put("reportsTo", ep.getReportsTo());
            map.put("workLocationType", ep.getWorkLocationType());
            map.put("payType", ep.getPayType());
            map.put("payFrequency", ep.getPayFrequency());
            map.put("roleName", ep.getRoleName());
            map.put("ptState", ep.getPtState());
            map.put("staffingType", ep.getStaffingType());
            map.put("taxRegime", ep.getTaxRegime());
            map.put("travelRequired", ep.getTravelRequired());
            map.put("employmentType", ep.getEmploymentType());
            map.put("ptApplicable", ep.getPtApplicable());
            map.put("probationApplicable", ep.getProbationApplicable());
            map.put("gradeLevel", ep.getGradeLevel());
            map.put("departmentHead", ep.getDepartmentHead());
            map.put("jobTitle", ep.getJobTitle());
            map.put("ptRegistrationNo", ep.getPtRegistrationNo());
            map.put("otEligible", ep.getOtEligible());
            map.put("probationPeriodStr", ep.getProbationPeriodStr());
            map.put("businessUnit", ep.getBusinessUnit());
            map.put("photoDocPath", ep.getPhotoDocPath());
            map.put("resumeDocPath", ep.getResumeDocPath());
            map.put("aadhaarDocPath", ep.getAadhaarDocPath());
            map.put("panDocPath", ep.getPanDocPath());
            map.put("educationalDocPath", ep.getEducationalDocPath());
            map.put("experienceDocPath", ep.getExperienceDocPath());
            map.put("offerDocPath", ep.getOfferDocPath());
            map.put("panNo", ep.getPanNo());
            map.put("aadharNo", ep.getAadharNo());
            map.put("pfNumber", ep.getPfNumber());
            map.put("esiNumber", ep.getEsiNumber());
            map.put("uanNumber", ep.getUanNumber());
            map.put("category", ep.getCategory());
            map.put("workShift", ep.getWorkShift());
            map.put("dateOfConfirmation", ep.getDateOfConfirmation());
            map.put("probationReviewDate", ep.getProbationReviewDate());
        }
        return map;
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
        return "redirect:/senior_hr/employee?tab=offboarding&offboarding=success";
    }

    // --- MY SPACE ROUTE (HR Performance) ---
    @PreAuthorize("hasAuthority('admin_dashboard_view')")
    @GetMapping("/senior_hr/performance")
    public String showSeniorHrPerformance(Model model, Principal principal) {
        // Fetch real Appraisal Cycles, seed if empty
        java.util.List<com.example.admindashboard.model.AppraisalCycle> cycles = appraisalCycleRepository.findAll();
        if (cycles.isEmpty()) {
            com.example.admindashboard.model.AppraisalCycle c1 = new com.example.admindashboard.model.AppraisalCycle();
            c1.setCycleName("Performance Appraisal 2026");
            c1.setCycleType("Annual");
            c1.setCycleCode("PERF2026");
            c1.setDurationType("Custom");
            c1.setStartDate("2026-04-01");
            c1.setEndDate("2027-03-31");
            c1.setFrequency("Annual");
            c1.setTimeZone("(GMT +5:30) Kolkata");
            c1.setCycleOwner("Neha Sharma");
            c1.setDescription("Annual performance cycle for FY 2026-2027.");
            c1.setProgress(85);
            c1.setStatus("Active");
            c1.setEmpCount(112);
            appraisalCycleRepository.save(c1);

            com.example.admindashboard.model.AppraisalCycle c2 = new com.example.admindashboard.model.AppraisalCycle();
            c2.setCycleName("H1 Appraisal 2026");
            c2.setCycleType("Half-yearly");
            c2.setCycleCode("H1PERF");
            c2.setDurationType("Custom");
            c2.setStartDate("2026-04-01");
            c2.setEndDate("2027-03-31");
            c2.setFrequency("Half-yearly");
            c2.setTimeZone("(GMT +5:30) Kolkata");
            c2.setCycleOwner("Neha Sharma");
            c2.setDescription("H1 review.");
            c2.setProgress(85);
            c2.setStatus("Active");
            c2.setEmpCount(112);
            appraisalCycleRepository.save(c2);

            com.example.admindashboard.model.AppraisalCycle c3 = new com.example.admindashboard.model.AppraisalCycle();
            c3.setCycleName("Q1 Appraisal 2026");
            c3.setCycleType("Quarterly");
            c3.setCycleCode("Q1PERF");
            c3.setDurationType("Custom");
            c3.setStartDate("2026-04-01");
            c3.setEndDate("2027-03-31");
            c3.setFrequency("Quarterly");
            c3.setTimeZone("(GMT +5:30) Kolkata");
            c3.setCycleOwner("Neha Sharma");
            c3.setDescription("Q1 review.");
            c3.setProgress(100);
            c3.setStatus("Completed");
            c3.setEmpCount(112);
            appraisalCycleRepository.save(c3);

            cycles = appraisalCycleRepository.findAll();
        }
        
        java.util.List<java.util.Map<String, Object>> list = new java.util.ArrayList<>();
        for (com.example.admindashboard.model.AppraisalCycle c : cycles) {
            java.util.Map<String, Object> map = new java.util.HashMap<>();
            map.put("name", c.getCycleName());
            map.put("type", c.getCycleType());
            
            // Format start/end date for duration display
            String duration = "-";
            if (c.getStartDate() != null && c.getEndDate() != null) {
                try {
                    java.time.LocalDate sd = java.time.LocalDate.parse(c.getStartDate());
                    java.time.LocalDate ed = java.time.LocalDate.parse(c.getEndDate());
                    java.time.format.DateTimeFormatter dtf = java.time.format.DateTimeFormatter.ofPattern("dd MMM yyyy");
                    duration = sd.format(dtf) + " - " + ed.format(dtf);
                } catch (Exception e) {
                    duration = c.getStartDate() + " - " + c.getEndDate();
                }
            }
            map.put("duration", duration);
            map.put("emp", c.getEmpCount());
            map.put("prog", c.getProgress());
            map.put("status", c.getStatus());
            list.add(map);
        }
        model.addAttribute("appraisals", list);
        model.addAttribute("allUsers", userRepository.findAll());
        return "senior_hr-performance";
    }

    @PostMapping("/senior_hr/performance/create-cycle")
    public String createAppraisalCycle(
            @RequestParam("cycleName") String cycleName,
            @RequestParam("cycleType") String cycleType,
            @RequestParam(value = "cycleCode", required = false) String cycleCode,
            @RequestParam("durationType") String durationType,
            @RequestParam("startDate") String startDate,
            @RequestParam("endDate") String endDate,
            @RequestParam("frequency") String frequency,
            @RequestParam("timeZone") String timeZone,
            @RequestParam("cycleOwner") String cycleOwner,
            @RequestParam("description") String description) {

        com.example.admindashboard.model.AppraisalCycle cycle = new com.example.admindashboard.model.AppraisalCycle();
        cycle.setCycleName(cycleName);
        cycle.setCycleType(cycleType);
        cycle.setCycleCode(cycleCode);
        cycle.setDurationType(durationType);
        cycle.setStartDate(startDate);
        cycle.setEndDate(endDate);
        cycle.setFrequency(frequency);
        cycle.setTimeZone(timeZone);
        cycle.setCycleOwner(cycleOwner);
        cycle.setDescription(description);
        cycle.setProgress(0); // starts at 0%
        cycle.setStatus("Active");
        cycle.setEmpCount(112); // match default count

        appraisalCycleRepository.save(cycle);

        return "redirect:/senior_hr/performance?tab=cycles";
    }

    // --- MY SPACE ROUTE (HR Attendance) ---
    @PreAuthorize("hasAuthority('admin_dashboard_view')")
    @GetMapping("/senior_hr/attendance")
    public String showSeniorHrAttendance(Model model, Principal principal) {
        
        // 1. Fetch & Seed Daily Attendance
        java.util.List<com.example.admindashboard.model.Attendance> allAttendance = attendanceRepository.findAll();
        if (allAttendance.isEmpty()) {
            java.util.List<com.example.admindashboard.model.User> users = userRepository.findAll();
            java.time.LocalDate baseDate = java.time.LocalDate.of(2026, 5, 19);
            for (com.example.admindashboard.model.User u : users) {
                for (int i = 0; i < 5; i++) {
                    java.time.LocalDate date = baseDate.minusDays(i);
                    com.example.admindashboard.model.Attendance att = new com.example.admindashboard.model.Attendance();
                    att.setUser(u);
                    att.setDate(date);
                    
                    int hash = (u.getId().hashCode() + i) % 100;
                    String status;
                    java.time.LocalTime checkIn = null;
                    java.time.LocalTime checkOut = null;
                    String totalHours = "-";
                    
                    if (hash < 70) {
                        status = "Present";
                        checkIn = java.time.LocalTime.of(9, 0).plusMinutes(hash % 15);
                        checkOut = java.time.LocalTime.of(18, 0).plusMinutes(hash % 30);
                        totalHours = "9.0";
                    } else if (hash < 80) {
                        status = "Late";
                        checkIn = java.time.LocalTime.of(10, 15).plusMinutes(hash % 10);
                        checkOut = java.time.LocalTime.of(18, 0).plusMinutes(hash % 30);
                        totalHours = "7.8";
                    } else if (hash < 90) {
                        status = "On Leave";
                        totalHours = "-";
                    } else if (hash < 95) {
                        status = "Half Day";
                        checkIn = java.time.LocalTime.of(9, 0).plusMinutes(hash % 15);
                        checkOut = java.time.LocalTime.of(13, 0);
                        totalHours = "4.0";
                    } else {
                        status = "Absent";
                        totalHours = "-";
                    }
                    
                    att.setStatus(status);
                    att.setCheckInTime(checkIn);
                    att.setCheckOutTime(checkOut);
                    att.setTotalHours(totalHours);
                    att.setReason(status.equals("Late") ? "Traffic delay" : (status.equals("On Leave") ? "Sick leave" : ""));
                    
                    attendanceRepository.save(att);
                }
            }
            allAttendance = attendanceRepository.findAll();
        }
        
        java.time.format.DateTimeFormatter timeFormatter = java.time.format.DateTimeFormatter.ofPattern("hh:mm a");
        java.util.List<java.util.Map<String, Object>> rawList = new java.util.ArrayList<>();
        java.util.List<java.util.Map<String, Object>> dailyList = new java.util.ArrayList<>();
        for (com.example.admindashboard.model.Attendance a : allAttendance) {
            if (a.getUser() != null) {
                java.util.Map<String, Object> map = new java.util.HashMap<>();
                map.put("name", a.getUser().getFullName());
                map.put("id", a.getUser().getEmployeeProfile() != null ? a.getUser().getEmployeeProfile().getEmployeeCode() : "EMP" + a.getUser().getId());
                map.put("dept", a.getUser().getEmployeeProfile() != null ? a.getUser().getEmployeeProfile().getDepartment() : "Engineering");
                map.put("shift", a.getUser().getEmployeeProfile() != null ? a.getUser().getEmployeeProfile().getWorkShift() : "General Shift");
                
                String checkInStr = a.getCheckInTime() != null ? a.getCheckInTime().format(timeFormatter) : "-";
                String checkOutStr = a.getCheckOutTime() != null ? a.getCheckOutTime().format(timeFormatter) : "-";
                
                map.put("in", checkInStr);
                map.put("out", checkOutStr);
                map.put("hrs", a.getTotalHours() != null ? a.getTotalHours() : "-");
                map.put("status", a.getStatus() != null ? a.getStatus() : "Absent");
                map.put("rem", a.getReason() != null ? a.getReason() : "-");
                map.put("date", a.getDate() != null ? a.getDate().toString() : "");
                map.put("loc", a.getUser().getEmployeeProfile() != null ? a.getUser().getEmployeeProfile().getWorkLocation() : "Raipur, India");
                
                dailyList.add(map);
                
                java.util.Map<String, Object> rawMap = new java.util.HashMap<>();
                rawMap.put("userId", a.getUser().getId());
                rawMap.put("date", a.getDate() != null ? a.getDate().toString() : "");
                rawMap.put("checkIn", checkInStr);
                rawMap.put("checkOut", checkOutStr);
                rawMap.put("hrs", a.getTotalHours() != null ? a.getTotalHours() : "-");
                rawMap.put("status", a.getStatus() != null ? a.getStatus() : "Absent");
                rawList.add(rawMap);
            }
        }
        model.addAttribute("dailyAttendance", dailyList);
        model.addAttribute("dailyAttendanceRaw", rawList);
        
        // 2. Fetch Regularizations (Overrides) (Seed if empty)
        java.util.List<com.example.admindashboard.model.AttendanceRegularization> allReg = attendanceRegularizationRepository.findAll();
        if (allReg.isEmpty()) {
            java.util.List<com.example.admindashboard.model.User> users = userRepository.findAll();
            String[] types = {"Late Mark", "Forgot ID", "Work From Home"};
            String[] reasons = {"Train delay", "Forgot card at home", "Power cut at residential area"};
            String[] statuses = {"Approved", "Pending", "Declined"};
            int count = 0;
            for (com.example.admindashboard.model.User u : users) {
                if (count >= 15) break;
                com.example.admindashboard.model.AttendanceRegularization reg = new com.example.admindashboard.model.AttendanceRegularization();
                reg.setUser(u);
                reg.setDate(java.time.LocalDate.of(2026, 5, 19).minusDays(count % 5));
                reg.setType(types[count % 3]);
                reg.setDuration(count % 3 == 0 ? "1 hr" : "Full Day");
                reg.setReason(reasons[count % 3]);
                reg.setStatus(statuses[count % 3]);
                reg.setHrComments(statuses[count % 3].equals("Approved") ? "Approved based on manager review." : "");
                attendanceRegularizationRepository.save(reg);
                count++;
            }
            allReg = attendanceRegularizationRepository.findAll();
        }
        
        java.util.List<java.util.Map<String, Object>> regList = new java.util.ArrayList<>();
        for (com.example.admindashboard.model.AttendanceRegularization r : allReg) {
            if (r.getUser() != null) {
                java.util.Map<String, Object> map = new java.util.HashMap<>();
                map.put("name", r.getUser().getFullName());
                map.put("id", r.getUser().getEmployeeProfile() != null ? r.getUser().getEmployeeProfile().getEmployeeCode() : "EMP" + r.getUser().getId());
                map.put("dept", r.getUser().getEmployeeProfile() != null ? r.getUser().getEmployeeProfile().getDepartment() : "Engineering");
                
                map.put("date", r.getDate() != null ? r.getDate().toString() : "");
                map.put("type", r.getType() != null ? r.getType() : "-");
                map.put("orig", r.getType().equals("Late Mark") ? "10:15 AM" : "-"); 
                map.put("upd", r.getDuration() != null ? r.getDuration() : "-"); 
                map.put("reason", r.getReason() != null ? r.getReason() : "-");
                map.put("status", r.getStatus() != null ? r.getStatus() : "Pending");
                regList.add(map);
            }
        }
        model.addAttribute("overrides", regList);

        // 3. Fetch Shift Assignments (Seed if empty)
        java.util.List<com.example.admindashboard.model.ShiftAssignment> allShifts = shiftAssignmentRepository.findAll();
        if (allShifts.isEmpty()) {
            java.util.List<com.example.admindashboard.model.User> users = userRepository.findAll();
            String[] shiftNames = {"General Shift", "Night Shift", "Flexible Shift", "Half-day Shift"};
            String[] shiftTimings = {"10:00 AM - 07:00 PM", "10:00 PM - 07:00 AM", "02:00 PM - 10:00 PM", "10:00 AM - 02:00 PM"};
            String[] breaks = {"01:00 PM - 02:00 PM", "02:00 AM - 03:00 AM", "05:00 PM - 06:00 PM", "12:00 PM - 12:30 PM"};
            int count = 0;
            for (com.example.admindashboard.model.User u : users) {
                if (count >= 10) break;
                com.example.admindashboard.model.ShiftAssignment sa = new com.example.admindashboard.model.ShiftAssignment();
                sa.setUser(u);
                sa.setDepartment(u.getEmployeeProfile() != null && u.getEmployeeProfile().getDepartment() != null ? u.getEmployeeProfile().getDepartment() : "Engineering");
                sa.setLocation(u.getEmployeeProfile() != null && u.getEmployeeProfile().getWorkLocation() != null ? u.getEmployeeProfile().getWorkLocation() : "Raipur, India");
                sa.setShiftName(shiftNames[count % 4]);
                sa.setShiftTiming(shiftTimings[count % 4]);
                sa.setBreakTime(breaks[count % 4]);
                sa.setStartDate(java.time.LocalDate.now().minusDays(5));
                sa.setEndDate(java.time.LocalDate.now().plusDays(30));
                sa.setOtAllowed(count % 2 == 0);
                sa.setOtCalculationRule("After Shift Hours");
                shiftAssignmentRepository.save(sa);
                count++;
            }
            allShifts = shiftAssignmentRepository.findAll();
        }
        model.addAttribute("shiftAssignments", allShifts);

        // 4. Calculate Shift Metrics
        long totalActive = userRepository.count(); 
        long assigned = allShifts.stream().filter(s -> s.getUser() != null).map(s -> s.getUser().getId()).distinct().count();
        long unassigned = totalActive - assigned;
        long otAssigned = allShifts.stream().filter(com.example.admindashboard.model.ShiftAssignment::isOtAllowed).count();
        
        model.addAttribute("totalEmployees", totalActive);
        model.addAttribute("assignedEmployees", assigned);
        model.addAttribute("unassignedEmployees", unassigned > 0 ? unassigned : 0);
        model.addAttribute("otAssigned", otAssigned);
        
        // 5. Extract Distinct Shifts for the Shift List Sidebar
        java.util.List<java.util.Map<String, Object>> distinctShifts = new java.util.ArrayList<>();
        java.util.Set<String> shiftNamesSet = new java.util.HashSet<>();
        for (com.example.admindashboard.model.ShiftAssignment sa : allShifts) {
            if (sa.getShiftName() != null && !shiftNamesSet.contains(sa.getShiftName())) {
                shiftNamesSet.add(sa.getShiftName());
                java.util.Map<String, Object> shiftMap = new java.util.HashMap<>();
                shiftMap.put("shiftName", sa.getShiftName());
                shiftMap.put("shiftTiming", sa.getShiftTiming());
                shiftMap.put("otAllowed", sa.isOtAllowed());
                
                long empCount = allShifts.stream().filter(s -> sa.getShiftName().equals(s.getShiftName())).count();
                shiftMap.put("empCount", empCount);
                distinctShifts.add(shiftMap);
            }
        }
        model.addAttribute("distinctShifts", distinctShifts);

        // 6. Pass allUsers and distinct departments for the UI
        java.util.List<com.example.admindashboard.model.User> allUsers = userRepository.findAll();
        model.addAttribute("allUsers", allUsers);

        java.util.List<String> departments = allUsers.stream()
            .map(u -> u.getEmployeeProfile() != null ? u.getEmployeeProfile().getDepartment() : null)
            .filter(java.util.Objects::nonNull)
            .distinct()
            .collect(java.util.stream.Collectors.toList());
        if (departments.isEmpty()) {
            departments = java.util.Arrays.asList("Engineering", "Product", "Marketing", "Finance", "Operations", "HR");
        }
        model.addAttribute("departments", departments);

        java.util.List<String> locations = allUsers.stream()
            .map(u -> u.getEmployeeProfile() != null ? u.getEmployeeProfile().getWorkLocation() : null)
            .filter(java.util.Objects::nonNull)
            .distinct()
            .collect(java.util.stream.Collectors.toList());
        if (locations.isEmpty()) {
            locations = java.util.Arrays.asList("Raipur, India", "Shahdol", "Bangalore, India");
        }
        model.addAttribute("locations", locations);

        return "senior_hr-attendance";
    }

    @PostMapping("/senior_hr/attendance/assign-shift")
    public String assignShift(
            @RequestParam("userId") Long userId,
            @RequestParam("department") String department,
            @RequestParam("location") String location,
            @RequestParam("shiftName") String shiftName,
            @RequestParam("shiftTiming") String shiftTiming,
            @RequestParam("breakTime") String breakTime,
            @RequestParam("startDate") @org.springframework.format.annotation.DateTimeFormat(iso = org.springframework.format.annotation.DateTimeFormat.ISO.DATE) java.time.LocalDate startDate,
            @RequestParam("endDate") @org.springframework.format.annotation.DateTimeFormat(iso = org.springframework.format.annotation.DateTimeFormat.ISO.DATE) java.time.LocalDate endDate,
            @RequestParam("otAllowed") boolean otAllowed,
            @RequestParam("otCalculationRule") String otCalculationRule) {
        
        com.example.admindashboard.model.User user = userRepository.findById(userId).orElse(null);
        if (user != null) {
            // Find existing shift assignment for this user, or create new
            com.example.admindashboard.model.ShiftAssignment sa = shiftAssignmentRepository.findAll().stream()
                .filter(s -> s.getUser() != null && s.getUser().getId().equals(userId))
                .findFirst()
                .orElse(new com.example.admindashboard.model.ShiftAssignment());
            
            sa.setUser(user);
            sa.setDepartment(department);
            sa.setLocation(location);
            sa.setShiftName(shiftName);
            sa.setShiftTiming(shiftTiming);
            sa.setBreakTime(breakTime);
            sa.setStartDate(startDate);
            sa.setEndDate(endDate);
            sa.setOtAllowed(otAllowed);
            sa.setOtCalculationRule(otCalculationRule);
            
            shiftAssignmentRepository.save(sa);
        }
        
        return "redirect:/senior_hr/attendance?tab=shift-mgmt";
    }

    @PostMapping("/senior_hr/attendance/override")
    public String overrideAttendance(
            @RequestParam("userId") Long userId,
            @RequestParam("date") @org.springframework.format.annotation.DateTimeFormat(iso = org.springframework.format.annotation.DateTimeFormat.ISO.DATE) java.time.LocalDate date,
            @RequestParam("overrideType") String overrideType,
            @RequestParam("checkIn") String checkIn,
            @RequestParam("checkOut") String checkOut,
            @RequestParam("duration") String duration,
            @RequestParam("reason") String reason) {
        
        com.example.admindashboard.model.User user = userRepository.findById(userId).orElse(null);
        if (user != null) {
            com.example.admindashboard.model.AttendanceRegularization reg = new com.example.admindashboard.model.AttendanceRegularization();
            reg.setUser(user);
            reg.setDate(date);
            reg.setType(overrideType);
            reg.setDuration(duration);
            reg.setReason(reason);
            reg.setStatus("Approved");
            reg.setHrComments("Override performed directly by Senior HR");
            attendanceRegularizationRepository.save(reg);
            
            com.example.admindashboard.model.Attendance att = attendanceRepository.findAll().stream()
                .filter(a -> a.getUser() != null && a.getUser().getId().equals(userId) && date.equals(a.getDate()))
                .findFirst()
                .orElse(new com.example.admindashboard.model.Attendance());
            
            att.setUser(user);
            att.setDate(date);
            
            java.time.LocalTime checkInTime = null;
            java.time.LocalTime checkOutTime = null;
            if (checkIn != null && !checkIn.trim().isEmpty() && !"-".equals(checkIn)) {
                try {
                    checkInTime = java.time.LocalTime.parse(checkIn);
                } catch (Exception e) {}
            }
            if (checkOut != null && !checkOut.trim().isEmpty() && !"-".equals(checkOut)) {
                try {
                    checkOutTime = java.time.LocalTime.parse(checkOut);
                } catch (Exception e) {}
            }
            
            att.setCheckInTime(checkInTime);
            att.setCheckOutTime(checkOutTime);
            att.setTotalHours(duration);
            
            String status = "Present";
            if (overrideType.contains("Half")) {
                status = "Half Day";
            } else if (overrideType.contains("Absent")) {
                status = "Absent";
            } else if (overrideType.contains("Leave")) {
                status = "On Leave";
            } else if (checkInTime != null && checkInTime.isAfter(java.time.LocalTime.of(10, 0))) {
                status = "Late";
            }
            
            att.setStatus(status);
            att.setReason(reason);
            attendanceRepository.save(att);
        }
        
        return "redirect:/senior_hr/attendance?tab=override";
    }

    // --- RECRUITMENT ROUTE ---
    @PreAuthorize("hasAuthority('admin_dashboard_view')")
    @GetMapping("/senior_hr/recruitment")
    public String showSeniorHrRecruitment(Model model, Principal principal) {
        // Seed job postings if empty
        if (jobPostingRepository.count() == 0) {
            JobPosting j1 = new JobPosting();
            j1.setJobId("J-101");
            j1.setTitle("Sr. Frontend Dev.");
            j1.setDepartment("Development Team");
            j1.setLocation("Shahdol, India");
            j1.setJobType("Full-time");
            j1.setExperienceRequired("0-2 Years");
            j1.setNoOfOpenings(2);
            j1.setDescription("We are looking for a skilled Frontend Developer...");
            j1.setJobResponsibility("Add key responsibilities");
            j1.setEligibilityCriteria("Any UG/PG Graduate");
            j1.setPrimarySkills("React, Angular, Vue.js");
            j1.setPostingDate(java.time.LocalDate.now().minusDays(10));
            j1.setActive(true);
            jobPostingRepository.save(j1);

            JobPosting j2 = new JobPosting();
            j2.setJobId("J-102");
            j2.setTitle("Backend Dev.");
            j2.setDepartment("Development Team");
            j2.setLocation("Raipur, India");
            j2.setJobType("Full-time");
            j2.setExperienceRequired("2-4 Years");
            j2.setNoOfOpenings(1);
            j2.setDescription("Backend Dev opening description...");
            j2.setJobResponsibility("Backend Responsibilities");
            j2.setEligibilityCriteria("Any UG/PG Graduate");
            j2.setPrimarySkills("Java, Python");
            j2.setPostingDate(java.time.LocalDate.now().minusDays(15));
            j2.setActive(false); // Closed
            jobPostingRepository.save(j2);

            JobPosting j3 = new JobPosting();
            j3.setJobId("J-103");
            j3.setTitle("UI Designer");
            j3.setDepartment("Design Team");
            j3.setLocation("Raipur, India");
            j3.setJobType("Intern");
            j3.setExperienceRequired("0-1 Years");
            j3.setNoOfOpenings(1);
            j3.setDescription("Intern UI Designer...");
            j3.setJobResponsibility("Mockups, UX flows");
            j3.setEligibilityCriteria("Any Graduate");
            j3.setPrimarySkills("Figma, Adobe XD");
            j3.setPostingDate(java.time.LocalDate.now().minusDays(20));
            j3.setActive(false); // Closed
            jobPostingRepository.save(j3);
        }

        // Seed candidates if empty
        if (referralRepository.count() == 0) {
            JobPosting j1 = jobPostingRepository.findByIsActiveTrue().stream().findFirst().orElse(null);
            com.example.admindashboard.model.User nehaUser = userRepository.findAll().stream().filter(u -> u.getFullName() != null && u.getFullName().contains("Neha")).findFirst().orElse(null);

            if (j1 != null) {
                Referral r1 = new Referral();
                r1.setFirstName("Neha");
                r1.setLastName("Sharma");
                r1.setEmail("neha.sharma@gmail.com");
                r1.setCountryCode("+91");
                r1.setMobileNumber("9874561230");
                r1.setRelationship("Friend");
                r1.setResumeFilename("neha_resume.pdf");
                r1.setJobPosting(j1);
                r1.setReferredBy(nehaUser);
                r1.setReferralDate(java.time.LocalDateTime.now().minusDays(2));
                r1.setStatus("INTERVIEWING");
                r1.setExperienceYears("2 Years");
                r1.setCurrentCompany("Tech Solutions");
                r1.setCurrentRole("Frontend Developer");
                r1.setNextStep("HR Interview 25 June, 2026");
                r1.setHometown("Bangalore, India");
                r1.setStage("Screening");
                referralRepository.save(r1);

                Referral r2 = new Referral();
                r2.setFirstName("Amit");
                r2.setLastName("Sharma");
                r2.setEmail("amit.sharma401@gmail.com");
                r2.setCountryCode("+91");
                r2.setMobileNumber("9874561231");
                r2.setRelationship("Friend");
                r2.setResumeFilename("amit_resume.pdf");
                r2.setJobPosting(j1);
                r2.setReferredBy(nehaUser);
                r2.setReferralDate(java.time.LocalDateTime.now().minusDays(2));
                r2.setStatus("SUBMITTED");
                r2.setExperienceYears("2 Years");
                r2.setCurrentCompany("TechHolder");
                r2.setCurrentRole("Frontend Engineer");
                r2.setNextStep("HR Interview 25 June, 2026");
                r2.setHometown("Bangalore, India");
                r2.setStage("Screening");
                referralRepository.save(r2);

                Referral r3 = new Referral();
                r3.setFirstName("Pihu");
                r3.setLastName("Sharma");
                r3.setEmail("pihu.sharma@gmail.com");
                r3.setCountryCode("+91");
                r3.setMobileNumber("9874561232");
                r3.setRelationship("Cousin");
                r3.setResumeFilename("pihu_resume.pdf");
                r3.setJobPosting(j1);
                r3.setReferredBy(nehaUser);
                r3.setReferralDate(java.time.LocalDateTime.now().minusDays(2));
                r3.setStatus("SUBMITTED");
                r3.setExperienceYears("2 Years");
                r3.setCurrentCompany("Code Vita");
                r3.setCurrentRole("Frontend Developer");
                r3.setNextStep("HR Interview 25 June, 2026");
                r3.setHometown("Bangalore, India");
                r3.setStage("Screening");
                referralRepository.save(r3);

                Referral r4 = new Referral();
                r4.setFirstName("Sneha");
                r4.setLastName("Patel");
                r4.setEmail("sneha@gmail.com");
                r4.setCountryCode("+91");
                r4.setMobileNumber("9874561233");
                r4.setRelationship("Cousin");
                r4.setResumeFilename("sneha_resume.pdf");
                r4.setJobPosting(j1);
                r4.setReferredBy(nehaUser);
                r4.setReferralDate(java.time.LocalDateTime.now().minusDays(5));
                r4.setStatus("IN_REVIEW");
                r4.setExperienceYears("1.5 Years");
                r4.setCurrentCompany("XYZ Technoworks");
                r4.setCurrentRole("Jr. Developer");
                r4.setNextStep("HR Interview 25 June, 2026");
                r4.setHometown("Bangalore, India");
                r4.setStage("Tech Interview");
                referralRepository.save(r4);

                Referral r5 = new Referral();
                r5.setFirstName("Piyush");
                r5.setLastName("Patel");
                r5.setEmail("piyush@gmail.com");
                r5.setCountryCode("+91");
                r5.setMobileNumber("9874561234");
                r5.setRelationship("Cousin");
                r5.setResumeFilename("piyush_resume.pdf");
                r5.setJobPosting(j1);
                r5.setReferredBy(nehaUser);
                r5.setReferralDate(java.time.LocalDateTime.now().minusDays(5));
                r5.setStatus("IN_REVIEW");
                r5.setExperienceYears("1.5 Years");
                r5.setCurrentCompany("XYZ Technoworks");
                r5.setCurrentRole("Jr. Developer");
                r5.setNextStep("HR Interview 25 June, 2026");
                r5.setHometown("Bangalore, India");
                r5.setStage("HR Interview");
                referralRepository.save(r5);
            }
        }

        List<JobPosting> jobs = jobPostingRepository.findAll();
        List<Referral> candidates = referralRepository.findAll();

        model.addAttribute("jobs", jobs);
        model.addAttribute("candidates", candidates);
        model.addAttribute("allUsers", userRepository.findAll());

        // Count stats
        long totalOpenings = jobs.size();
        long openVacancy = jobs.stream().filter(JobPosting::isActive).count();
        long closedOpenings = jobs.stream().filter(j -> !j.isActive()).count();
        long draftOpenings = 3; // matching default in mock

        model.addAttribute("totalOpenings", totalOpenings);
        model.addAttribute("openVacancy", openVacancy);
        model.addAttribute("closedOpenings", closedOpenings);
        model.addAttribute("draftOpenings", draftOpenings);

        return "senior_hr-recruitment";
    }

    @PostMapping("/senior_hr/recruitment/create-job")
    public String createJobOpening(
            @RequestParam("jobTitle") String jobTitle,
            @RequestParam("department") String department,
            @RequestParam("location") String location,
            @RequestParam("jobType") String jobType,
            @RequestParam("jobId") String jobId,
            @RequestParam("experienceRequired") String experienceRequired,
            @RequestParam("noOfOpenings") Integer noOfOpenings,
            @RequestParam("description") String description,
            @RequestParam("jobResponsibility") String jobResponsibility,
            @RequestParam("qualifications") String qualifications,
            @RequestParam("preferredSkills") String preferredSkills,
            @RequestParam("noticePeriod") String noticePeriod,
            @RequestParam("ctcRange") String ctcRange,
            @RequestParam("applicationDeadline") String applicationDeadline,
            @RequestParam("startTime") String startTime,
            @RequestParam("endTime") String endTime) {

        JobPosting job = new JobPosting();
        job.setTitle(jobTitle);
        job.setDepartment(department);
        job.setLocation(location);
        job.setJobType(jobType);
        job.setJobId(jobId);
        job.setExperienceRequired(experienceRequired);
        job.setNoOfOpenings(noOfOpenings);
        job.setDescription(description);
        job.setJobResponsibility(jobResponsibility);
        job.setEligibilityCriteria(qualifications);
        job.setPrimarySkills(preferredSkills);
        job.setNoticePeriod(noticePeriod);
        job.setCtcRange(ctcRange);
        if (applicationDeadline != null && !applicationDeadline.isEmpty()) {
            job.setApplicationDeadline(java.time.LocalDate.parse(applicationDeadline));
        }
        job.setStartTime(startTime);
        job.setEndTime(endTime);
        job.setPostingDate(java.time.LocalDate.now());
        job.setActive(true);

        jobPostingRepository.save(job);

        return "redirect:/senior_hr/recruitment?tab=openings";
    }

    @PostMapping("/senior_hr/recruitment/candidate/update-stage")
    public String updateCandidateStage(
            @RequestParam("candidateId") Long candidateId,
            @RequestParam("stage") String stage) {

        Referral ref = referralRepository.findById(candidateId).orElse(null);
        if (ref != null) {
            ref.setStage(stage);
            // Sync status badge options: Screening -> SUBMITTED/IN_REVIEW, Tech/HR -> INTERVIEWING, Offer -> HIRED
            if ("Screening".equals(stage)) {
                ref.setStatus("IN_REVIEW");
            } else if ("Tech Interview".equals(stage) || "HR Interview".equals(stage)) {
                ref.setStatus("INTERVIEWING");
            } else if ("Offer".equals(stage)) {
                ref.setStatus("HIRED");
            }
            referralRepository.save(ref);
        }
        return "redirect:/senior_hr/recruitment?tab=candidates";
    }

    @PostMapping("/senior_hr/recruitment/candidate/reject")
    public String rejectCandidate(@RequestParam("candidateId") Long candidateId) {
        Referral ref = referralRepository.findById(candidateId).orElse(null);
        if (ref != null) {
            ref.setStatus("REJECTED");
            referralRepository.save(ref);
        }
        return "redirect:/senior_hr/recruitment?tab=candidates";
    }

    @PostMapping("/senior_hr/recruitment/job/close")
    public String closeJobOpening(@RequestParam("jobId") Long jobId) {
        JobPosting job = jobPostingRepository.findById(jobId).orElse(null);
        if (job != null) {
            job.setActive(false);
            jobPostingRepository.save(job);
        }
        return "redirect:/senior_hr/recruitment?tab=openings";
    }

    // --- WORKFLOW ROUTES HANDLED BY DASHBOARD CONTROLLER ---
    private void loadDashboardData(Model model) {
        model.addAttribute("showMySpace", true);
    }
    
    @GetMapping("/senior_hr/api/referrals/{id}")
    @ResponseBody
    public ResponseEntity<?> getReferral(@PathVariable Long id) {
        Referral ref = referralRepository.findById(id).orElse(null);
        if (ref == null) {
            return ResponseEntity.notFound().build();
        }
        // Map to a clean DTO to avoid JSON recursion and Lazy proxy errors
        Map<String, Object> data = new java.util.HashMap<>();
        data.put("id", ref.getId());
        data.put("firstName", ref.getFirstName());
        data.put("lastName", ref.getLastName());
        data.put("email", ref.getEmail());
        data.put("status", ref.getStatus());
        data.put("relationship", ref.getRelationship());
        
        if (ref.getJobPosting() != null) {
            data.put("jobPosting", Map.of("title", ref.getJobPosting().getTitle()));
        } else {
            data.put("jobPosting", null);
        }
        
        if (ref.getReferredBy() != null) {
            data.put("referredBy", Map.of("fullName", ref.getReferredBy().getFullName()));
        } else {
            data.put("referredBy", null);
        }
        
        return ResponseEntity.ok(data);
    }

    @DeleteMapping("/senior_hr/api/referrals/{id}")
    @ResponseBody
    public ResponseEntity<?> deleteReferral(@PathVariable Long id) {
        Referral ref = referralRepository.findById(id).orElse(null);
        if (ref == null) {
            return ResponseEntity.notFound().build();
        }
        referralRepository.delete(ref);
        return ResponseEntity.ok(Map.of("success", true));
    }
}