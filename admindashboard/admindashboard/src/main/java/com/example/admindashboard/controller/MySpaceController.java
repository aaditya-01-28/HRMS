package com.example.admindashboard.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import com.example.admindashboard.model.User;
import com.example.admindashboard.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import com.example.admindashboard.service.LearningDashboardService;
import java.security.Principal;
import java.util.List;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.HashMap;
import java.util.Set;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import com.example.admindashboard.model.Course;
import com.example.admindashboard.repository.CourseRepository;
import com.example.admindashboard.model.Training;
import com.example.admindashboard.repository.TrainingRepository;
import com.example.admindashboard.model.LeaveRequest;
import com.example.admindashboard.model.WeeklyTimesheet;
import com.example.admindashboard.model.ServiceRequest;
import com.example.admindashboard.model.ResignationRequest;
import com.example.admindashboard.repository.LeaveRequestRepository;
import com.example.admindashboard.repository.WeeklyTimesheetRepository;
import com.example.admindashboard.repository.ServiceRequestRepository;
import com.example.admindashboard.repository.ResignationRequestRepository;


@Controller
public class MySpaceController {
	
	@Autowired
	private UserRepository userRepository;

	@Autowired
	private PasswordEncoder passwordEncoder;
	
	@Autowired
	private TrainingRepository trainingRepository;
	
	@Autowired
	private CourseRepository courseRepository;
	
	@Autowired
	private LearningDashboardService learningDashboardService;

        @Autowired
        private LeaveRequestRepository leaveRequestRepository;

        @Autowired
        private WeeklyTimesheetRepository weeklyTimesheetRepository;

        @Autowired
        private ServiceRequestRepository serviceRequestRepository;

        @Autowired
        private ResignationRequestRepository resignationRequestRepository;

    @GetMapping("/space/login")
    public String showLogin() {
        return "myspace-login";
    }

        @GetMapping("/senior_manager/myspace/login")
        public String showSeniorManagerMySpaceLogin(Model model) {
                model.addAttribute("loginAction", "/senior_manager/myspace/authenticate");
                return "myspace-login";
        }

        @PostMapping("/senior_manager/myspace/authenticate")
        public String authenticateSeniorManagerMySpace(
                        @RequestParam String username,
                        @RequestParam String password,
                        Model model) {

                User user = userRepository
                                .findByUsername(username.toUpperCase())
                                .orElse(null);

                if (user == null || !passwordEncoder.matches(password, user.getPassword())) {
                        model.addAttribute("authError", "Invalid username or password");
                        model.addAttribute("loginAction", "/senior_manager/myspace/authenticate");
                        return "myspace-login";
                }

                String role = user.getRole() != null ? user.getRole().getRoleName() : "";
                if (!"SENIOR_MANAGER".equalsIgnoreCase(role)) {
                        model.addAttribute("authError", "Only Senior Manager credentials can access this My Space");
                        model.addAttribute("loginAction", "/senior_manager/myspace/authenticate");
                        return "myspace-login";
                }

                return "redirect:/senior_manager/myspace";
        }

    @PostMapping("/space/authenticate")
    public String authenticate(
            @RequestParam String username,
            @RequestParam String password,
            Model model) {

        User user = userRepository
                .findByUsername(username.toUpperCase())
                .orElse(null);

        if (user == null) {

            model.addAttribute("authError",
                    "Invalid username or password");

            return "myspace-login";
        }

        if (!passwordEncoder.matches(password,
                user.getPassword())) {

            model.addAttribute("authError",
                    "Invalid username or password");

            return "myspace-login";
        }

        String role = user.getRole().getRoleName();

        switch (role) {

            case "SENIOR_LND_HEAD":
                return "redirect:/space/lnd/dashboard";

            case "SENIOR_MANAGER":
                return "redirect:/space/manager/dashboard";

            case "SENIOR_HR":
                return "redirect:/senior_hr/employee";

            case "SENIOR_ACCOUNTS_HEAD":
                return "redirect:/space/accounts/dashboard";

            case "SENIOR_TRANSPORT_HEAD":
                return "redirect:/space/transport/dashboard";

            case "SENIOR_REWARDS_HEAD":
                return "redirect:/space/rewards/dashboard";

            default:

                model.addAttribute("authError",
                        "You are not authorized to access My Space");

                return "myspace-login";
        }
    }

        @PreAuthorize("hasRole('SENIOR_MANAGER')")
        @GetMapping({"/senior_manager/myspace", "/space/manager/dashboard"})
        public String showSeniorManagerMySpace(
                        @RequestParam(value = "tab", defaultValue = "directory") String tab,
                        Model model,
                        Principal principal) {

                User loggedInUser = principal != null
                                ? userRepository.findByUsername(principal.getName()).orElse(null)
                                : null;

                                if (loggedInUser == null) {
                                        return "redirect:/login";
                                }

                                List<User> allUsers = userRepository.findAll();
                                List<User> teamMembers = resolveTeamMembers(loggedInUser, allUsers);
                                Set<Long> teamMemberIds = teamMembers.stream().map(User::getId).collect(java.util.stream.Collectors.toSet());

                                LocalDate today = LocalDate.now();
                                long activeEmployees = teamMembers.stream()
                                                .filter(u -> "ACTIVE".equalsIgnoreCase(nullToEmpty(u.getStatus())))
                                                .count();
                                long exitedEmployees = teamMembers.stream()
                                                .filter(u -> {
                                                        String status = nullToEmpty(u.getStatus());
                                                        return "EXITED".equalsIgnoreCase(status) || "INACTIVE".equalsIgnoreCase(status);
                                                })
                                                .count();
                                long onLeaveToday = leaveRequestRepository.findAll().stream()
                                                .filter(r -> r.getUser() != null && r.getUser().getId() != null && teamMemberIds.contains(r.getUser().getId()))
                                                .filter(r -> isApprovedLeaveStatus(r.getStatus()))
                                                .filter(r -> isDateInLeaveRange(today, r.getFromDate(), r.getToDate()))
                                                .count();

                                Set<String> departmentOptions = teamMembers.stream()
                                                .map(this::getDepartment)
                                                .filter(s -> !s.isBlank())
                                                .collect(java.util.stream.Collectors.toCollection(java.util.TreeSet::new));
                                Set<String> employmentTypeOptions = teamMembers.stream()
                                                .map(this::getEmploymentType)
                                                .filter(s -> !s.isBlank())
                                                .collect(java.util.stream.Collectors.toCollection(java.util.TreeSet::new));

                                List<User> level2 = resolveNextLevel(teamMembers, java.util.Set.of(loggedInUser.getFullName(), loggedInUser.getUsername()), new HashSet<>());
                                Set<Long> usedIds = new HashSet<>();
                                usedIds.add(loggedInUser.getId());
                                usedIds.addAll(level2.stream().map(User::getId).collect(java.util.stream.Collectors.toSet()));
                                List<User> level3 = resolveNextLevel(teamMembers, getManagerKeySet(level2), usedIds);
                                usedIds.addAll(level3.stream().map(User::getId).collect(java.util.stream.Collectors.toSet()));
                                List<User> level4 = resolveNextLevel(teamMembers, getManagerKeySet(level3), usedIds);

                                List<Map<String, Object>> pendingActions = buildPendingActions(teamMembers);
                                List<Map<String, Object>> recommendations = buildRecommendations(teamMembers);

                                long needsApproval = pendingActions.stream().filter(a -> "Needs approval".equals(a.get("requestGroup"))).count();
                                long needsReview = pendingActions.stream().filter(a -> "Needs review".equals(a.get("requestGroup"))).count();
                                long infoOnly = pendingActions.stream().filter(a -> "Information only".equals(a.get("requestGroup"))).count();

                                long approvedRecommendations = recommendations.stream().filter(a -> "Approved".equals(a.get("statusGroup"))).count();
                                long pendingRecommendations = recommendations.stream().filter(a -> "Pending".equals(a.get("statusGroup"))).count();
                                long declinedRecommendations = recommendations.stream().filter(a -> "Declined".equals(a.get("statusGroup"))).count();

                                Map<String, Long> pendingSummaryByType = pendingActions.stream()
                                                .collect(java.util.stream.Collectors.groupingBy(a -> String.valueOf(a.get("actionType")), java.util.LinkedHashMap::new, java.util.stream.Collectors.counting()));

                                String safeTab = List.of("directory", "hierarchy", "pending", "recommendations").contains(tab) ? tab : "directory";

                                model.addAttribute("activeTab", safeTab);
                                model.addAttribute("managerUser", loggedInUser);
                                model.addAttribute("teamMembers", teamMembers);
                                model.addAttribute("totalTeamMembers", teamMembers.size());
                                model.addAttribute("activeEmployees", activeEmployees);
                                model.addAttribute("onLeaveToday", onLeaveToday);
                                model.addAttribute("exitedEmployees", exitedEmployees);
                                model.addAttribute("departmentOptions", departmentOptions);
                                model.addAttribute("employmentTypeOptions", employmentTypeOptions);
                                model.addAttribute("hierarchyLevel2", level2);
                                model.addAttribute("hierarchyLevel3", level3);
                                model.addAttribute("hierarchyLevel4", level4);
                                model.addAttribute("pendingActions", pendingActions);
                                model.addAttribute("pendingTotal", pendingActions.size());
                                model.addAttribute("pendingNeedsApproval", needsApproval);
                                model.addAttribute("pendingNeedsReview", needsReview);
                                model.addAttribute("pendingInfoOnly", infoOnly);
                                model.addAttribute("pendingSummaryByType", pendingSummaryByType);
                                model.addAttribute("recommendations", recommendations);
                                model.addAttribute("recommendationsTotal", recommendations.size());
                                model.addAttribute("approvedRecommendations", approvedRecommendations);
                                model.addAttribute("pendingRecommendations", pendingRecommendations);
                                model.addAttribute("declinedRecommendations", declinedRecommendations);

                return "senior_manager-myspace";
        }

        private List<User> resolveTeamMembers(User manager, List<User> allUsers) {
                String managerName = normalizeKey(manager.getFullName());
                String managerUsername = normalizeKey(manager.getUsername());

                return allUsers.stream()
                                .filter(u -> u.getId() != null && !u.getId().equals(manager.getId()))
                                .filter(u -> {
                                        boolean linkedByManagerRef = u.getManager() != null && u.getManager().getId() != null && u.getManager().getId().equals(manager.getId());
                                        String reportingManager = u.getEmployeeProfile() != null ? normalizeKey(u.getEmployeeProfile().getReportingManager()) : "";
                                        String reportsTo = u.getEmployeeProfile() != null ? normalizeKey(u.getEmployeeProfile().getReportsTo()) : "";
                                        boolean linkedByName = (!managerName.isBlank() && (managerName.equals(reportingManager) || managerName.equals(reportsTo)))
                                                        || (!managerUsername.isBlank() && (managerUsername.equals(reportingManager) || managerUsername.equals(reportsTo)));
                                        return linkedByManagerRef || linkedByName;
                                })
                                .sorted(Comparator.comparing(u -> nullToEmpty(u.getFullName()), String.CASE_INSENSITIVE_ORDER))
                                .collect(java.util.stream.Collectors.toList());
        }

        private List<User> resolveNextLevel(List<User> candidates, Set<String> managerKeys, Set<Long> excludeIds) {
                return candidates.stream()
                                .filter(u -> u.getId() != null && !excludeIds.contains(u.getId()))
                                .filter(u -> {
                                        String reportingManager = u.getEmployeeProfile() != null ? normalizeKey(u.getEmployeeProfile().getReportingManager()) : "";
                                        String reportsTo = u.getEmployeeProfile() != null ? normalizeKey(u.getEmployeeProfile().getReportsTo()) : "";
                                        return managerKeys.contains(reportingManager) || managerKeys.contains(reportsTo);
                                })
                                .sorted(Comparator.comparing(u -> nullToEmpty(u.getFullName()), String.CASE_INSENSITIVE_ORDER))
                                .collect(java.util.stream.Collectors.toList());
        }

        private Set<String> getManagerKeySet(List<User> users) {
                Set<String> keys = new LinkedHashSet<>();
                for (User u : users) {
                        keys.add(normalizeKey(u.getFullName()));
                        keys.add(normalizeKey(u.getUsername()));
                }
                keys.remove("");
                return keys;
        }

        private List<Map<String, Object>> buildPendingActions(List<User> teamMembers) {
                List<Map<String, Object>> rows = new ArrayList<>();
                Set<Long> teamIds = teamMembers.stream().map(User::getId).collect(java.util.stream.Collectors.toSet());
                DateTimeFormatter dateFmt = DateTimeFormatter.ofPattern("dd/MM/yyyy");

                for (LeaveRequest leave : leaveRequestRepository.findAll()) {
                        if (leave.getUser() == null || leave.getUser().getId() == null || !teamIds.contains(leave.getUser().getId())) continue;
                        if (!isPendingStatus(leave.getStatus())) continue;

                        Map<String, Object> row = new HashMap<>();
                        row.put("employeeName", nullToEmpty(leave.getUser().getFullName()));
                        row.put("employeeCode", nullToEmpty(leave.getUser().getUsername()));
                        row.put("department", getDepartment(leave.getUser()));
                        row.put("actionDetails", nullToEmpty(leave.getLeaveType()) + " leave request");
                        row.put("actionType", "Leave");
                        row.put("requestedBy", nullToEmpty(leave.getUser().getFullName()));
                        LocalDate date = leave.getCreatedAt() != null ? leave.getCreatedAt() : leave.getFromDate();
                        row.put("requestDate", date != null ? dateFmt.format(date) : "-");
                        row.put("priority", "Medium");
                        row.put("status", normalizeUiStatus(leave.getStatus()));
                        row.put("requestGroup", classifyRequestGroup(leave.getStatus()));
                        row.put("actionLabel", "Review");
                        row.put("actionUrl", "/senior_manager/workflow?tab=leave");
                        row.put("rawDate", date);
                        rows.add(row);
                }

                for (WeeklyTimesheet ts : weeklyTimesheetRepository.findAll()) {
                        if (ts.getUser() == null || ts.getUser().getId() == null || !teamIds.contains(ts.getUser().getId())) continue;
                        if (!isPendingStatus(ts.getStatus())) continue;

                        Map<String, Object> row = new HashMap<>();
                        row.put("employeeName", nullToEmpty(ts.getUser().getFullName()));
                        row.put("employeeCode", nullToEmpty(ts.getUser().getUsername()));
                        row.put("department", getDepartment(ts.getUser()));
                        row.put("actionDetails", "Timesheet " + formatRange(ts.getWeekStartDate(), ts.getWeekEndDate()));
                        row.put("actionType", "Timesheet");
                        row.put("requestedBy", nullToEmpty(ts.getUser().getFullName()));
                        LocalDate date = ts.getSubmissionDate() != null ? ts.getSubmissionDate() : ts.getWeekStartDate();
                        row.put("requestDate", date != null ? dateFmt.format(date) : "-");
                        row.put("priority", "High");
                        row.put("status", normalizeUiStatus(ts.getStatus()));
                        row.put("requestGroup", classifyRequestGroup(ts.getStatus()));
                        row.put("actionLabel", "Review");
                        row.put("actionUrl", "/senior_manager/workflow?tab=timesheet");
                        row.put("rawDate", date);
                        rows.add(row);
                }

                Set<String> teamCodes = teamMembers.stream().map(User::getUsername).filter(s -> s != null && !s.isBlank()).collect(java.util.stream.Collectors.toSet());
                for (ServiceRequest req : serviceRequestRepository.findAll()) {
                        if (!teamCodes.contains(req.getEmployeeId())) continue;
                        if (!isPendingStatus(req.getStatus())) continue;

                        Map<String, Object> row = new HashMap<>();
                        row.put("employeeName", nullToEmpty(req.getEmployeeName()));
                        row.put("employeeCode", nullToEmpty(req.getEmployeeId()));
                        row.put("department", nullToEmpty(req.getDepartment()));
                        row.put("actionDetails", nullToEmpty(req.getCategory()).isBlank() ? "Service request" : req.getCategory());
                        row.put("actionType", "Service Request");
                        row.put("requestedBy", nullToEmpty(req.getEmployeeName()));
                        LocalDate date = req.getSubmissionDate();
                        row.put("requestDate", date != null ? dateFmt.format(date) : "-");
                        row.put("priority", nullToEmpty(req.getPriority()).isBlank() ? "Medium" : req.getPriority());
                        row.put("status", normalizeUiStatus(req.getStatus()));
                        row.put("requestGroup", classifyRequestGroup(req.getStatus()));
                        row.put("actionLabel", "Review");
                        row.put("actionUrl", "/senior_manager/workflow?tab=tickets");
                        row.put("rawDate", date);
                        rows.add(row);
                }

                for (ResignationRequest resignation : resignationRequestRepository.findAll()) {
                        if (resignation.getEmployee() == null || resignation.getEmployee().getId() == null || !teamIds.contains(resignation.getEmployee().getId())) continue;
                        if (!isPendingStatus(resignation.getStatus())) continue;

                        Map<String, Object> row = new HashMap<>();
                        row.put("employeeName", nullToEmpty(resignation.getEmployee().getFullName()));
                        row.put("employeeCode", nullToEmpty(resignation.getEmployee().getUsername()));
                        row.put("department", getDepartment(resignation.getEmployee()));
                        row.put("actionDetails", "Resignation approval");
                        row.put("actionType", "Resignation");
                        row.put("requestedBy", nullToEmpty(resignation.getEmployee().getFullName()));
                        LocalDate date = resignation.getRequestDate();
                        row.put("requestDate", date != null ? dateFmt.format(date) : "-");
                        row.put("priority", "High");
                        row.put("status", normalizeUiStatus(resignation.getStatus()));
                        row.put("requestGroup", "Needs approval");
                        row.put("actionLabel", "Review");
                        row.put("actionUrl", "/senior_manager/workflow?tab=resignation");
                        row.put("rawDate", date);
                        rows.add(row);
                }

                rows.sort((a, b) -> {
                        LocalDate db = (LocalDate) b.get("rawDate");
                        LocalDate da = (LocalDate) a.get("rawDate");
                        if (db == null && da == null) return 0;
                        if (db == null) return -1;
                        if (da == null) return 1;
                        return db.compareTo(da);
                });
                return rows;
        }

        private List<Map<String, Object>> buildRecommendations(List<User> teamMembers) {
                List<Map<String, Object>> rows = new ArrayList<>();
                DateTimeFormatter dateFmt = DateTimeFormatter.ofPattern("dd MMM");
                Set<Long> teamIds = teamMembers.stream().map(User::getId).collect(java.util.stream.Collectors.toSet());
                Set<String> teamCodes = teamMembers.stream().map(User::getUsername).filter(s -> s != null && !s.isBlank()).collect(java.util.stream.Collectors.toSet());

                for (LeaveRequest leave : leaveRequestRepository.findAll()) {
                        if (leave.getUser() == null || leave.getUser().getId() == null || !teamIds.contains(leave.getUser().getId())) continue;
                        if (!isCompletedStatus(leave.getStatus())) continue;
                        rows.add(recommendationRow(
                                        leave.getUser(),
                                        "Leave",
                                        nullToEmpty(leave.getLeaveType()),
                                        leave.getToDate() != null ? leave.getToDate() : leave.getFromDate(),
                                        leave.getStatus(),
                                        "/senior_manager/workflow?tab=leave",
                                        dateFmt
                        ));
                }

                for (WeeklyTimesheet ts : weeklyTimesheetRepository.findAll()) {
                        if (ts.getUser() == null || ts.getUser().getId() == null || !teamIds.contains(ts.getUser().getId())) continue;
                        if (!isCompletedStatus(ts.getStatus())) continue;
                        rows.add(recommendationRow(
                                        ts.getUser(),
                                        "Timesheet",
                                        formatRange(ts.getWeekStartDate(), ts.getWeekEndDate()),
                                        ts.getSubmissionDate() != null ? ts.getSubmissionDate() : ts.getWeekEndDate(),
                                        ts.getStatus(),
                                        "/senior_manager/workflow?tab=timesheet",
                                        dateFmt
                        ));
                }

                for (ServiceRequest req : serviceRequestRepository.findAll()) {
                        if (!teamCodes.contains(req.getEmployeeId())) continue;
                        if (!isCompletedStatus(req.getStatus())) continue;

                        User user = teamMembers.stream().filter(u -> req.getEmployeeId().equalsIgnoreCase(nullToEmpty(u.getUsername()))).findFirst().orElse(null);
                        if (user == null) continue;
                        rows.add(recommendationRow(
                                        user,
                                        "Service Request",
                                        nullToEmpty(req.getCategory()).isBlank() ? "General" : req.getCategory(),
                                        req.getActionDate() != null ? req.getActionDate() : req.getSubmissionDate(),
                                        req.getStatus(),
                                        "/senior_manager/workflow?tab=tickets",
                                        dateFmt
                        ));
                }

                for (ResignationRequest resignation : resignationRequestRepository.findAll()) {
                        if (resignation.getEmployee() == null || resignation.getEmployee().getId() == null || !teamIds.contains(resignation.getEmployee().getId())) continue;
                        if (!isCompletedStatus(resignation.getStatus())) continue;
                        rows.add(recommendationRow(
                                        resignation.getEmployee(),
                                        "Resignation",
                                        "Exit workflow",
                                        resignation.getLastWorkingDate() != null ? resignation.getLastWorkingDate() : resignation.getRequestDate(),
                                        resignation.getStatus(),
                                        "/senior_manager/workflow?tab=resignation",
                                        dateFmt
                        ));
                }

                rows.sort((a, b) -> {
                        LocalDate db = (LocalDate) b.get("rawDate");
                        LocalDate da = (LocalDate) a.get("rawDate");
                        if (db == null && da == null) return 0;
                        if (db == null) return -1;
                        if (da == null) return 1;
                        return db.compareTo(da);
                });
                return rows;
        }

        private Map<String, Object> recommendationRow(User user, String recommendationType, String recommendedFor, LocalDate actionDate, String rawStatus, String actionUrl, DateTimeFormatter dateFmt) {
                Map<String, Object> row = new HashMap<>();
                String uiStatus = normalizeUiStatus(rawStatus);
                row.put("employeeName", nullToEmpty(user.getFullName()));
                row.put("employeeCode", nullToEmpty(user.getUsername()));
                row.put("currentRole", nullToEmpty(user.getDesignation()).isBlank() ? nullToEmpty(user.getRole() != null ? user.getRole().getRoleName() : "-") : user.getDesignation());
                row.put("recommendationType", recommendationType);
                row.put("recommendedFor", recommendedFor);
                row.put("date", actionDate != null ? dateFmt.format(actionDate) : "-");
                row.put("status", uiStatus);
                row.put("statusGroup", mapRecommendationStatus(uiStatus));
                row.put("department", getDepartment(user));
                row.put("employmentType", getEmploymentType(user));
                row.put("actionUrl", actionUrl);
                row.put("rawDate", actionDate);
                return row;
        }

        private String mapRecommendationStatus(String status) {
                String s = normalizeKey(status);
                if (s.contains("APPROVED") || s.contains("ACTIVE") || s.contains("CLOSED") || s.contains("RESOLVED") || s.contains("OFFBOARDED")) {
                        return "Approved";
                }
                if (s.contains("REJECTED") || s.contains("DECLINED")) {
                        return "Declined";
                }
                return "Pending";
        }

        private boolean isApprovedLeaveStatus(String status) {
                String s = normalizeKey(status);
                return s.equals("APPROVED") || s.equals("ACTIVE");
        }

        private boolean isPendingStatus(String status) {
                String s = normalizeKey(status);
                return s.contains("PENDING") || s.contains("SUBMITTED") || s.contains("OPEN") || s.contains("ASSIGNED") || s.contains("IN_PROGRESS") || s.contains("IN PROGRESS");
        }

        private boolean isCompletedStatus(String status) {
                String s = normalizeKey(status);
                return s.contains("APPROVED") || s.contains("REJECTED") || s.contains("DECLINED") || s.contains("RESOLVED") || s.contains("CLOSED") || s.contains("CLOSE") || s.contains("OFFBOARDED");
        }

        private String classifyRequestGroup(String status) {
                String s = normalizeKey(status);
                if (s.contains("PENDING") || s.contains("SUBMITTED")) return "Needs approval";
                if (s.contains("ASSIGNED") || s.contains("IN_PROGRESS") || s.contains("IN PROGRESS")) return "Needs review";
                return "Information only";
        }

        private boolean isDateInLeaveRange(LocalDate target, LocalDate fromDate, LocalDate toDate) {
                if (target == null || fromDate == null) return false;
                LocalDate endDate = toDate != null ? toDate : fromDate;
                return !target.isBefore(fromDate) && !target.isAfter(endDate);
        }

        private String normalizeUiStatus(String status) {
                String s = normalizeKey(status);
                if (s.isBlank()) return "Pending";
                if (s.contains("APPROVED")) return "Approved";
                if (s.contains("REJECTED")) return "Rejected";
                if (s.contains("SUBMITTED") || s.contains("PENDING")) return "Approval pending";
                if (s.contains("ASSIGNED") || s.contains("IN_PROGRESS") || s.contains("IN PROGRESS")) return "Review pending";
                if (s.contains("RESOLVED") || s.contains("CLOSED") || s.contains("CLOSE")) return "Approved";
                if (s.contains("OFFBOARDED")) return "Approved";
                return status;
        }

        private String getDepartment(User user) {
                if (user == null || user.getEmployeeProfile() == null || user.getEmployeeProfile().getDepartment() == null) {
                        return "Unknown";
                }
                return user.getEmployeeProfile().getDepartment();
        }

        private String getEmploymentType(User user) {
                if (user != null && user.getEmployeeProfile() != null && user.getEmployeeProfile().getEmploymentType() != null && !user.getEmployeeProfile().getEmploymentType().isBlank()) {
                        return user.getEmployeeProfile().getEmploymentType();
                }
                if (user != null && user.getRole() != null && user.getRole().getRoleName() != null) {
                        return user.getRole().getRoleName();
                }
                return "Full-time";
        }

        private String formatRange(LocalDate start, LocalDate end) {
                DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd MMM");
                if (start == null && end == null) return "-";
                if (start != null && end != null) return fmt.format(start) + " - " + fmt.format(end);
                return fmt.format(start != null ? start : end);
        }

        private String nullToEmpty(String value) {
                return value == null ? "" : value;
        }

        private String normalizeKey(String value) {
                return nullToEmpty(value).trim().toUpperCase();
        }

    @GetMapping("/space/lnd/dashboard")
    public String lndDashboard(Model model,
                               Principal principal) {

        model.addAttribute(
                "stats",
                learningDashboardService.getDashboardStats());

        model.addAttribute(
                "activity",
                learningDashboardService.getLearningActivity());
        model.addAttribute(
                "enrollmentData",
                learningDashboardService.getEnrollmentTrend());
        model.addAttribute(
                "pendingApprovals",
                learningDashboardService.getPendingApprovals());
        model.addAttribute(
                "categoryData",
                learningDashboardService
                        .getCategoryDistribution());
        User user = userRepository
                .findByUsername(principal.getName())
                .orElse(null);

        if (user != null) {

            model.addAttribute(
                    "loggedInUserName",
                    user.getFullName());

            model.addAttribute(
                    "loggedInEmail",
                    user.getEmail());
        }

        return "learning-admin-dashboard";
    }
    @GetMapping("/space/lnd/approvals")
    public String learningApprovals(Model model,
                                    Principal principal) {

        User user = userRepository
                .findByUsername(principal.getName())
                .orElse(null);

        if (user != null) {

            model.addAttribute(
                    "loggedInUserName",
                    user.getFullName());

            model.addAttribute(
                    "loggedInEmail",
                    user.getEmail());
        }

        model.addAttribute(
                "approvals",
                learningDashboardService.getApprovalRequests());

        model.addAttribute(
                "approvalCount",
                learningDashboardService
                        .getApprovalRequests()
                        .size());

        return "learning-approvals";
    }
    
    @GetMapping("/space/lnd/course-addition")
    public String courseAddition(
            @RequestParam(required = false) String courseType,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String search,
            Model model,
            Principal principal) {

        User user = userRepository
                .findByUsername(principal.getName())
                .orElse(null);

        if (user != null) {

            model.addAttribute(
                    "loggedInUserName",
                    user.getFullName());

            model.addAttribute(
                    "loggedInEmail",
                    user.getEmail());
        }

        List<Course> courses;

        if (courseType != null && !courseType.isBlank()
                && status != null && !status.isBlank()) {

            courses = courseRepository
                    .findByCourseTypeAndStatus(
                            courseType,
                            status);

        } else if (courseType != null
                && !courseType.isBlank()) {

            courses = courseRepository
                    .findByCourseType(courseType);

        } else if (status != null
                && !status.isBlank()) {

            courses = courseRepository
                    .findByStatus(status);

        } else if (search != null
                && !search.isBlank()) {

            courses = courseRepository
                    .findByCourseNameContainingIgnoreCase(search);

        } else {

            courses = courseRepository.findAll();
        }
        model.addAttribute(
                "providers",
                courseRepository.findAll()
                        .stream()
                        .map(Course::getCourseType)
                        .distinct()
                        .toList());

        model.addAttribute("courses", courses);

        return "learning-course-addition";
    }
    @GetMapping("/space/lnd/course-addition/new")
    public String addCoursePage(Model model,
                                Principal principal) {

        User user = userRepository
                .findByUsername(principal.getName())
                .orElse(null);

        if(user != null){
            model.addAttribute("loggedInUserName",
                    user.getFullName());

            model.addAttribute("loggedInEmail",
                    user.getEmail());
        }

        return "learning-add-course";
    }
    @PostMapping("/space/lnd/course/save")
    public String saveCourse(
            @RequestParam String courseName,
            @RequestParam String description,
            @RequestParam String category,
            @RequestParam String level,
            @RequestParam Integer durationHours,
            @RequestParam String courseType,
            Principal principal) {

        User user = userRepository
                .findByUsername(principal.getName())
                .orElse(null);

        Course course = new Course();

        course.setCourseName(courseName);
        course.setDescription(description);
        course.setCategory(category);
        course.setLevel(level);
        course.setDurationHours(durationHours);
        course.setCourseType(courseType);

        course.setStatus("ACTIVE");

        course.setCreatedBy(user);

        courseRepository.save(course);

        return "redirect:/space/lnd/course-addition";
    }
    @GetMapping("/space/lnd/upcoming-training")
    public String upcomingTraining(

            @RequestParam(required = false)
            String trainingType,

            @RequestParam(required = false)
            String status,

            @RequestParam(required = false)
            String search,

            Model model,
            Principal principal) {

        User user = userRepository
                .findByUsername(principal.getName())
                .orElse(null);

        if (user != null) {

            model.addAttribute(
                    "loggedInUserName",
                    user.getFullName());

            model.addAttribute(
                    "loggedInEmail",
                    user.getEmail());
        }

        List<Training> trainings;

        if (trainingType != null
                && !trainingType.isBlank()
                && status != null
                && !status.isBlank()) {

            trainings =
                    trainingRepository
                            .findByTrainingTypeAndStatus(
                                    trainingType,
                                    status);

        } else if (trainingType != null
                && !trainingType.isBlank()) {

            trainings =
                    trainingRepository
                            .findByTrainingType(
                                    trainingType);

        } else if (status != null
                && !status.isBlank()) {

            trainings =
                    trainingRepository
                            .findByStatus(status);

        } else if (search != null
                && !search.isBlank()) {

            trainings =
                    trainingRepository
                            .findByTitleContainingIgnoreCase(
                                    search);

        } else {

            trainings =
                    trainingRepository.findAll();
        }

        model.addAttribute(
                "trainings",
                trainings);

        model.addAttribute(
                "trainingTypes",
                trainingRepository.findAll()
                        .stream()
                        .map(Training::getTrainingType)
                        .distinct()
                        .toList());

        return "learning-upcoming-training";
    }
    @PostMapping("/space/lnd/training/save")
    public String saveTraining(

            @RequestParam String title,

            @RequestParam String trainerName,

            @RequestParam String trainingType,

            @RequestParam LocalDate trainingDate,

            @RequestParam Integer duration,

            @RequestParam String description) {

        Training training = new Training();

        training.setTitle(title);

        training.setTrainerName(trainerName);

        training.setTrainingType(trainingType);

        training.setTrainingDate(trainingDate);

        training.setDuration(duration);

        training.setDescription(description);

        training.setStatus("SCHEDULED");

        trainingRepository.save(training);

        return "redirect:/space/lnd/upcoming-training";
    }
    
}