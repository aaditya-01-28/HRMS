package com.example.admindashboard.controller;

import org.springframework.stereotype.Controller;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import com.example.admindashboard.model.User;
import com.example.admindashboard.model.HrmsNotification;
import com.example.admindashboard.model.EmployeeProfile;
import com.example.admindashboard.repository.UserRepository;
import com.example.admindashboard.repository.HrmsNotificationRepository;
import com.example.admindashboard.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
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
import com.example.admindashboard.model.Attendance;
import com.example.admindashboard.model.EmployeeLeaveWallet;
import com.example.admindashboard.model.Project;
import com.example.admindashboard.model.ProjectMember;
import com.example.admindashboard.repository.ProjectMemberRepository;
import com.example.admindashboard.model.Ticket;
import com.example.admindashboard.model.JobPosting;
import com.example.admindashboard.repository.LeaveRequestRepository;
import com.example.admindashboard.repository.WeeklyTimesheetRepository;
import com.example.admindashboard.repository.ServiceRequestRepository;
import com.example.admindashboard.repository.ResignationRequestRepository;
import com.example.admindashboard.model.ExpenseClaim;
import com.example.admindashboard.model.ReimbursementRequest;
import com.example.admindashboard.model.BudgetRequest;
import com.example.admindashboard.model.Candidate;
import com.example.admindashboard.model.Interview;
import com.example.admindashboard.model.InterviewFeedback;
import com.example.admindashboard.model.RecruitmentApproval;
import com.example.admindashboard.model.CommunicationBroadcast;
import com.example.admindashboard.model.ProjectUpdateNotification;
import com.example.admindashboard.model.RecentNotificationRecord;
import com.example.admindashboard.repository.ExpenseClaimRepository;
import com.example.admindashboard.repository.ReimbursementRequestRepository;
import com.example.admindashboard.repository.BudgetRequestRepository;
import com.example.admindashboard.repository.CandidateRepository;
import com.example.admindashboard.repository.InterviewRepository;
import com.example.admindashboard.repository.InterviewFeedbackRepository;
import com.example.admindashboard.repository.RecruitmentApprovalRepository;
import com.example.admindashboard.repository.CommunicationBroadcastRepository;
import com.example.admindashboard.repository.ProjectUpdateNotificationRepository;
import com.example.admindashboard.repository.RecentNotificationRecordRepository;
import com.example.admindashboard.model.PerformanceReview;
import com.example.admindashboard.model.AuditLog;
import com.example.admindashboard.model.Goal;
import com.example.admindashboard.repository.GoalRepository;
import com.example.admindashboard.model.EmployeeFeedback;
import com.example.admindashboard.repository.EmployeeFeedbackRepository;

import com.example.admindashboard.model.EmployeeGoal;
import com.example.admindashboard.repository.EmployeeGoalRepository;
import com.example.admindashboard.model.LeaveEscalation;
import com.example.admindashboard.repository.LeaveEscalationRepository;

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
        private com.example.admindashboard.repository.DepartmentEntityRepository departmentRepository;

        @Autowired
        private WeeklyTimesheetRepository weeklyTimesheetRepository;

        @Autowired
        private ServiceRequestRepository serviceRequestRepository;

        @Autowired
        private com.example.admindashboard.repository.LeaveTypeMasterRepository leaveTypeMasterRepository;

        @Autowired
        private com.example.admindashboard.repository.EmployeeLeaveWalletRepository employeeLeaveWalletRepository;

        @Autowired
        private com.example.admindashboard.repository.AttendanceRepository attendanceRepository;

        @Autowired
        private com.example.admindashboard.repository.ProjectRepository projectRepository;

        @Autowired
        private com.example.admindashboard.repository.TicketRepository ticketRepository;

        @Autowired
        private ResignationRequestRepository resignationRequestRepository;

        @Autowired
        private com.example.admindashboard.repository.JobPostingRepository jobPostingRepository;

        @Autowired
        private ExpenseClaimRepository expenseClaimRepository;

        @Autowired
        private ReimbursementRequestRepository reimbursementRequestRepository;

        @Autowired
        private BudgetRequestRepository budgetRequestRepository;

        @Autowired
        private CandidateRepository candidateRepository;

        @Autowired
        private com.example.admindashboard.repository.TeamRepository teamRepository;

        @Autowired
        private com.example.admindashboard.repository.TeamMemberRepository teamMemberRepository;

        @Autowired
        private com.example.admindashboard.repository.PerformanceFeedbackRequestRepository performanceFeedbackRequestRepository;

        @Autowired
        private com.example.admindashboard.repository.FeedbackRequestFieldRepository feedbackRequestFieldRepository;


        @Autowired
        private InterviewRepository interviewRepository;

        @Autowired
        private InterviewFeedbackRepository interviewFeedbackRepository;

        @Autowired
        private RecruitmentApprovalRepository recruitmentApprovalRepository;

        @Autowired
        private CommunicationBroadcastRepository communicationBroadcastRepository;

        @Autowired
        private ProjectUpdateNotificationRepository projectUpdateNotificationRepository;

        @Autowired
        private RecentNotificationRecordRepository recentNotificationRecordRepository;

        @Autowired
        private HrmsNotificationRepository hrmsNotificationRepository;

        @Autowired
        private EmailService emailService;

        @Autowired
        private com.example.admindashboard.repository.PerformanceReviewRepository performanceReviewRepository;

        @Autowired
        private com.example.admindashboard.repository.AuditLogRepository auditLogRepository;

        @Autowired
        private GoalRepository goalRepository;

        @Autowired
        private EmployeeFeedbackRepository employeeFeedbackRepository;

        @Autowired
        private ProjectMemberRepository projectMemberRepository;

        @Autowired
        private LeaveEscalationRepository leaveEscalationRepository;

        @Autowired
        private EmployeeGoalRepository employeeGoalRepository;

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
                return "redirect:/senior_rewards/dashboard";

            case "SENIOR_FACILITY_HEAD":
                return "redirect:/senior_facility/dashboard";

            case "SENIOR_IT_HEAD":
                return "redirect:/senior_it/dashboard";

            default:

                model.addAttribute("authError",
                        "You are not authorized to access My Space");

                return "myspace-login";
        }
    }

        @PreAuthorize("hasRole('SENIOR_MANAGER')")
        @GetMapping({"/senior_manager/myspace", "/space/manager/dashboard"})
        @org.springframework.transaction.annotation.Transactional(readOnly = true)
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

                                // Senior Manager sees ALL employees across the org (excluding self and CLIENT accounts)
                                List<User> teamMembers = allUsers.stream()
                                        .filter(u -> u.getId() != null && !u.getId().equals(loggedInUser.getId()))
                                        .filter(u -> {
                                            String role = u.getRole() != null ? u.getRole().getRoleName() : "";
                                            return !"CLIENT".equalsIgnoreCase(role);
                                        })
                                        .sorted(Comparator.comparing(u -> nullToEmpty(u.getFullName()), String.CASE_INSENSITIVE_ORDER))
                                        .collect(java.util.stream.Collectors.toList());
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

                                // ===== ROLE-BASED HIERARCHY (L1-L4) =====
                                java.util.Set<String> L4_ROLES = java.util.Set.of("SUPER_ADMIN", "ADMIN");
                                java.util.Set<String> L3_ROLES = java.util.Set.of("SENIOR_MANAGER", "SENIOR_HR", "SENIOR_LND_HEAD", "SENIOR_ACCOUNTS_HEAD", "SENIOR_TRANSPORT_HEAD", "SENIOR_REWARDS_HEAD", "SENIOR_FACILITY_HEAD", "SENIOR_IT_HEAD");
                                java.util.Set<String> L2_ROLES = java.util.Set.of("HR_ADMIN", "HR_EXECUTIVE", "MANAGER", "HR_MANAGER", "PROJECT_MANAGER", "FINANCE", "RECRUITER", "IT_ADMIN", "IT_SUPPORT", "AUDITOR", "TRANSPORT", "LND", "REWARDS", "FACILITY_L2");
                                // L1 = EMPLOYEE + CLIENT + anyone not in L2/L3/L4

                                List<Map<String, Object>> hLevel1 = new ArrayList<>();
                                List<Map<String, Object>> hLevel2 = new ArrayList<>();
                                List<Map<String, Object>> hLevel3 = new ArrayList<>();
                                List<Map<String, Object>> hLevel4 = new ArrayList<>();

                                // Count reportees for each user
                                Map<Long, Long> reporteeCounts = new HashMap<>();
                                for (User u : allUsers) {
                                    if (u.getManager() != null && u.getManager().getId() != null) {
                                        reporteeCounts.merge(u.getManager().getId(), 1L, Long::sum);
                                    }
                                }

                                Set<String> allDepartments = new java.util.TreeSet<>();

                                for (User u : allUsers) {
                                    if (!"ACTIVE".equalsIgnoreCase(nullToEmpty(u.getStatus()))) continue;
                                    String roleName = u.getRole() != null ? u.getRole().getRoleName() : "";

                                    String designation = u.getDesignation() != null && !u.getDesignation().isBlank()
                                            ? u.getDesignation()
                                            : (u.getEmployeeProfile() != null && u.getEmployeeProfile().getDesignation() != null
                                                ? u.getEmployeeProfile().getDesignation() : roleName);
                                    String dept = getDepartment(u);
                                    if (!dept.isBlank()) allDepartments.add(dept);
                                    long reportees = reporteeCounts.getOrDefault(u.getId(), 0L);

                                    Map<String, Object> card = new HashMap<>();
                                    card.put("id", u.getId());
                                    card.put("username", nullToEmpty(u.getUsername()));
                                    card.put("fullName", nullToEmpty(u.getFullName()));
                                    card.put("designation", designation);
                                    card.put("roleName", roleName);
                                    card.put("department", dept);
                                    card.put("reportees", reportees);
                                    card.put("managerId", u.getManager() != null ? u.getManager().getId() : null);
                                    card.put("email", nullToEmpty(u.getEmail()));
                                    card.put("profileImage", u.getProfileImage());
                                    card.put("isCurrentUser", u.getId() != null && u.getId().equals(loggedInUser.getId()));

                                     // L2 Manager: Direct manager
                                     String l2Mgr = "N/A";
                                     if (u.getManager() != null) {
                                         l2Mgr = u.getManager().getFullName();
                                     }
                                     card.put("l2Manager", l2Mgr);
                                     
                                     // L3 Manager: Senior manager
                                     String l3Mgr = "N/A";
                                     if (u.getManager() != null && u.getManager().getManager() != null) {
                                         l3Mgr = u.getManager().getManager().getFullName();
                                     } else if (u.getManager() != null) {
                                         l3Mgr = u.getManager().getFullName() + " (L2 is top)";
                                     }
                                     card.put("l3Manager", l3Mgr);
                                     
                                     // Assigned HR
                                     String assignedHr = "Neha Verma"; // default/fallback HR
                                     if (u.getEmployeeProfile() != null && u.getEmployeeProfile().getAssignedHrL2() != null && !u.getEmployeeProfile().getAssignedHrL2().isEmpty()) {
                                         assignedHr = u.getEmployeeProfile().getAssignedHrL2();
                                     } else if (u.getEmployeeProfile() != null && u.getEmployeeProfile().getBuHrContact() != null && !u.getEmployeeProfile().getBuHrContact().isEmpty()) {
                                         assignedHr = u.getEmployeeProfile().getBuHrContact();
                                     }
                                     card.put("assignedHr", assignedHr);

                                    if (L4_ROLES.contains(roleName)) hLevel4.add(card);
                                    else if (L3_ROLES.contains(roleName)) hLevel3.add(card);
                                    else if (L2_ROLES.contains(roleName)) hLevel2.add(card);
                                    else hLevel1.add(card);
                                }

                                // Sort each level by name
                                Comparator<Map<String, Object>> byName = Comparator.comparing(m -> String.valueOf(m.get("fullName")), String.CASE_INSENSITIVE_ORDER);
                                hLevel1.sort(byName);
                                hLevel2.sort(byName);
                                hLevel3.sort(byName);
                                hLevel4.sort(byName);

                                int totalHierarchyMembers = hLevel1.size() + hLevel2.size() + hLevel3.size() + hLevel4.size();
                                // Span of control = max reportees among L4 users
                                long spanOfControl = hLevel4.stream().mapToLong(m -> (Long) m.getOrDefault("reportees", 0L)).max().orElse(0);

                                model.addAttribute("hierarchyLevel1", hLevel1);
                                model.addAttribute("hierarchyLevel2", hLevel2);
                                model.addAttribute("hierarchyLevel3", hLevel3);
                                model.addAttribute("hierarchyLevel4", hLevel4);
                                model.addAttribute("totalHierarchyMembers", totalHierarchyMembers);
                                model.addAttribute("hierarchyDepartments", allDepartments.size());
                                model.addAttribute("hierarchySpanOfControl", spanOfControl);

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

                                String safeTab = List.of("directory", "hierarchy", "pending", "recommendations", "my_teams").contains(tab) ? tab : "directory";

                                model.addAttribute("activeTab", safeTab);
                                model.addAttribute("managerUser", loggedInUser);
                                model.addAttribute("allUsers", allUsers);
                                model.addAttribute("teamMembers", teamMembers);
                                model.addAttribute("totalTeamMembers", teamMembers.size());
                                model.addAttribute("activeEmployees", activeEmployees);
                                model.addAttribute("onLeaveToday", onLeaveToday);
                                model.addAttribute("exitedEmployees", exitedEmployees);
                                model.addAttribute("departmentOptions", departmentOptions);
                                model.addAttribute("employmentTypeOptions", employmentTypeOptions);
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
                                
                                List<com.example.admindashboard.model.Team> myCustomTeams = teamRepository.findByManager(loggedInUser);
                                model.addAttribute("myCustomTeams", myCustomTeams);

                return "senior_manager-myspace";
        }

        @PreAuthorize("hasRole('SENIOR_MANAGER')")
        @GetMapping("/senior_manager/myspace/debug_teams")
        @ResponseBody
        public String debugTeams(Principal principal) {
                try {
                        User loggedInUser = userRepository.findByUsername(principal.getName()).orElse(null);
                        List<com.example.admindashboard.model.Team> teams = teamRepository.findByManager(loggedInUser);
                        StringBuilder sb = new StringBuilder();
                        sb.append("LoggedInUser: ").append(loggedInUser.getUsername()).append(" (ID: ").append(loggedInUser.getId()).append(")\n");
                        sb.append("Teams count: ").append(teams.size()).append("\n");
                        for (com.example.admindashboard.model.Team t : teams) {
                                sb.append("Team: ").append(t.getTeamName()).append(" (ID: ").append(t.getId()).append("), CreatedAt: ").append(t.getCreatedAt()).append("\n");
                                sb.append("  Members count: ").append(t.getMembers().size()).append("\n");
                                for (com.example.admindashboard.model.TeamMember tm : t.getMembers()) {
                                        sb.append("    Member ID: ").append(tm.getId()).append(", User: ").append(tm.getUser().getFullName()).append(" (").append(tm.getUser().getUsername()).append(")\n");
                                }
                        }
                        return sb.toString();
                } catch (Exception e) {
                        java.io.StringWriter sw = new java.io.StringWriter();
                        java.io.PrintWriter pw = new java.io.PrintWriter(sw);
                        e.printStackTrace(pw);
                        return "ERROR:\n" + sw.toString();
                }
        }

        @PreAuthorize("hasRole('SENIOR_MANAGER')")
        @PostMapping("/senior_manager/myspace/add_team_members")
        public String addTeamMembers(
                @RequestParam(value = "teamMembers", required = false) List<String> teamMembers,
                Authentication authentication) {
                
                if (teamMembers == null || teamMembers.isEmpty()) {
                        return "redirect:/senior_manager/myspace?tab=hierarchy&error=NoMembersSelected";
                }
                
                String currentUsername = authentication.getName();
                User loggedInUser = userRepository.findByUsername(currentUsername).orElse(null);
                if (loggedInUser == null) {
                        return "redirect:/login";
                }
                
                for (String username : teamMembers) {
                        User user = userRepository.findByUsername(username).orElse(null);
                        if (user != null) {
                                user.setManager(loggedInUser);
                                userRepository.save(user);
                        }
                }
                
                return "redirect:/senior_manager/myspace?tab=hierarchy&success=MembersAdded";
        }

        @PreAuthorize("hasRole('SENIOR_MANAGER')")
        @PostMapping("/senior_manager/myspace/create_custom_team")
        public String createCustomTeam(
                @RequestParam("teamName") String teamName,
                @RequestParam(value = "description", required = false) String description,
                @RequestParam(value = "teamMembers", required = false) List<String> teamMembers,
                Authentication authentication) {
                
                String currentUsername = authentication.getName();
                User loggedInUser = userRepository.findByUsername(currentUsername).orElse(null);
                if (loggedInUser == null) {
                        return "redirect:/login";
                }
                
                com.example.admindashboard.model.Team team = new com.example.admindashboard.model.Team();
                team.setTeamName(teamName);
                team.setDescription(description);
                team.setManager(loggedInUser);
                
                teamRepository.save(team);
                
                if (teamMembers != null && !teamMembers.isEmpty()) {
                        for (String username : teamMembers) {
                                User user = userRepository.findByUsername(username).orElse(null);
                                if (user != null) {
                                        com.example.admindashboard.model.TeamMember tm = new com.example.admindashboard.model.TeamMember();
                                        tm.setTeam(team);
                                        tm.setUser(user);
                                        teamMemberRepository.save(tm);
                                }
                        }
                }
                
                return "redirect:/senior_manager/myspace?tab=my_teams&success=TeamCreated";
        }

        @PreAuthorize("hasRole('SENIOR_MANAGER')")
        @PostMapping("/senior_manager/myspace/edit_custom_team")
        @org.springframework.transaction.annotation.Transactional
        public String editCustomTeam(
                @RequestParam("teamId") Long teamId,
                @RequestParam("teamName") String teamName,
                @RequestParam(value = "description", required = false) String description,
                @RequestParam(value = "teamMembers", required = false) List<String> teamMembers,
                Authentication authentication) {
                
                String currentUsername = authentication.getName();
                User loggedInUser = userRepository.findByUsername(currentUsername).orElse(null);
                if (loggedInUser == null) {
                        return "redirect:/login";
                }
                
                com.example.admindashboard.model.Team team = teamRepository.findById(teamId).orElse(null);
                if (team == null || !team.getManager().getId().equals(loggedInUser.getId())) {
                        return "redirect:/senior_manager/myspace?tab=my_teams&error=UnauthorizedOrNotFound";
                }
                
                team.setTeamName(teamName);
                team.setDescription(description);
                teamRepository.save(team);
                
                teamMemberRepository.deleteByTeam(team);
                teamMemberRepository.flush();
                
                if (teamMembers != null && !teamMembers.isEmpty()) {
                        for (String username : teamMembers) {
                                User user = userRepository.findByUsername(username).orElse(null);
                                if (user != null) {
                                        com.example.admindashboard.model.TeamMember tm = new com.example.admindashboard.model.TeamMember();
                                        tm.setTeam(team);
                                        tm.setUser(user);
                                        teamMemberRepository.save(tm);
                                }
                        }
                }
                
                return "redirect:/senior_manager/myspace?tab=my_teams&success=TeamUpdated";
        }

        @PreAuthorize("hasRole('SENIOR_MANAGER')")
        @GetMapping("/senior_manager/leave_attendance")
        public String showLeaveAttendance(
                @RequestParam(value = "tab", defaultValue = "leave_calendar") String tab,
                @RequestParam(value = "dept", required = false) String dept,
                @RequestParam(value = "leaveType", required = false) String leaveType,
                @RequestParam(value = "loc", required = false) String loc,
                @RequestParam(value = "search", required = false) String search,
                Model model,
                Principal principal) {

                User loggedInUser = principal != null
                        ? userRepository.findByUsername(principal.getName()).orElse(null)
                        : null;

                if (loggedInUser == null) {
                    return "redirect:/login";
                }

                // Seed data if database is fresh
                seedLeaveWalletsAndAttendance();
                seedLeaveEscalations();

                model.addAttribute("loggedInUser", loggedInUser);
                model.addAttribute("activeTab", tab);
                model.addAttribute("selectedDept", dept);
                model.addAttribute("selectedLeaveType", leaveType);
                model.addAttribute("selectedLoc", loc);
                model.addAttribute("searchQuery", search);

                // --- Calculate metrics dynamically ---
                List<User> allUsers = userRepository.findAll();
                List<User> employees = allUsers.stream()
                        .filter(u -> u.getId() != null && !u.getId().equals(loggedInUser.getId()))
                        .filter(u -> u.getRole() == null || !"CLIENT".equalsIgnoreCase(u.getRole().getRoleName()))
                        .collect(java.util.stream.Collectors.toList());
                Set<Long> teamMemberIds = employees.stream().map(User::getId).collect(java.util.stream.Collectors.toSet());

                long totalMembers = employees.size();

                // Today's leaves
                long onLeaveToday = leaveRequestRepository.findAll().stream()
                        .filter(r -> "Approved".equalsIgnoreCase(r.getStatus()))
                        .filter(r -> r.getUser() != null && teamMemberIds.contains(r.getUser().getId()))
                        .filter(r -> isDateInLeaveRange(LocalDate.now(), r.getFromDate(), r.getToDate()))
                        .count();

                // Attendance logic
                long presentToday = attendanceRepository.findAll().stream()
                        .filter(a -> LocalDate.now().equals(a.getDate()) && a.getUser() != null && teamMemberIds.contains(a.getUser().getId()))
                        .filter(a -> "Present".equalsIgnoreCase(a.getStatus()))
                        .count();

                long absentToday = employees.size() - presentToday - onLeaveToday;
                if (absentToday < 0) absentToday = 0;

                // Calculate average attendance from real database records
                List<Attendance> allAttendance = attendanceRepository.findAll().stream()
                        .filter(a -> a.getUser() != null && teamMemberIds.contains(a.getUser().getId()))
                        .collect(java.util.stream.Collectors.toList());
                double avgAttendance = 0.0;
                if (!allAttendance.isEmpty()) {
                    long totalAttendanceDays = allAttendance.stream()
                            .filter(a -> "Present".equalsIgnoreCase(a.getStatus()) || "Absent".equalsIgnoreCase(a.getStatus()))
                            .count();
                    long totalPresentDays = allAttendance.stream()
                            .filter(a -> "Present".equalsIgnoreCase(a.getStatus()))
                            .count();
                    if (totalAttendanceDays > 0) {
                        avgAttendance = (totalPresentDays * 100.0) / totalAttendanceDays;
                    }
                }

                // Calculate leave utilization
                List<EmployeeLeaveWallet> wallets = employeeLeaveWalletRepository.findAll().stream()
                        .filter(w -> w.getUser() != null && teamMemberIds.contains(w.getUser().getId()))
                        .collect(java.util.stream.Collectors.toList());
                double leaveUtil = 0.0;
                long entitledLeaves = 0;
                long usedLeaves = 0;
                long pendingLeaves = 0;
                long remainingLeaves = 0;

                if (!wallets.isEmpty()) {
                    entitledLeaves = Math.round(wallets.stream().mapToDouble(w -> w.getOpeningBalance() != null ? w.getOpeningBalance() : 0.0).sum());
                    usedLeaves = Math.round(wallets.stream().mapToDouble(w -> w.getUsedBalance() != null ? w.getUsedBalance() : 0.0).sum());
                    remainingLeaves = Math.round(wallets.stream().mapToDouble(w -> w.getAvailableBalance() != null ? w.getAvailableBalance() : 0.0).sum());
                    
                    if (entitledLeaves > 0) {
                        leaveUtil = (usedLeaves * 100.0) / entitledLeaves;
                    }
                }

                // Balance overview
                model.addAttribute("totalMembers", totalMembers);
                model.addAttribute("onLeaveToday", onLeaveToday);
                model.addAttribute("avgAttendance", avgAttendance);
                model.addAttribute("leaveUtil", leaveUtil);
                model.addAttribute("leavesTaken", usedLeaves);
                model.addAttribute("presentToday", presentToday);
                model.addAttribute("absentToday", absentToday);

                String avgWorkingHours = "0h 0m";
                if (!allAttendance.isEmpty()) {
                    double totalHrsSum = 0.0;
                    int counts = 0;
                    for (Attendance a : allAttendance) {
                        if (a.getTotalHours() != null && a.getTotalHours().toLowerCase().contains("h")) {
                            try {
                                String clean = a.getTotalHours().replace("h", "").replace("m", "").trim();
                                String[] parts = clean.split(" ");
                                double val = Double.parseDouble(parts[0]);
                                if (parts.length > 1) {
                                    val += Double.parseDouble(parts[1]) / 60.0;
                                }
                                totalHrsSum += val;
                                counts++;
                            } catch (Exception e) {}
                        }
                    }
                    if (counts > 0) {
                        double avg = totalHrsSum / counts;
                        int h = (int) avg;
                        int m = (int) ((avg - h) * 60);
                        avgWorkingHours = h + "h " + m + "m";
                    }
                }
                if ("0h 0m".equals(avgWorkingHours)) {
                    avgWorkingHours = "8h 30m";
                }
                model.addAttribute("avgWorkingHours", avgWorkingHours);

                List<Map<String, Object>> calendarEvents = new ArrayList<>();
                List<LeaveRequest> approvedLeaves = leaveRequestRepository.findAll().stream()
                        .filter(r -> "Approved".equalsIgnoreCase(r.getStatus()) && r.getUser() != null && teamMemberIds.contains(r.getUser().getId()))
                        .collect(java.util.stream.Collectors.toList());
                for (LeaveRequest r : approvedLeaves) {
                    if (r.getUser() == null || r.getFromDate() == null || r.getToDate() == null) continue;
                    Map<String, Object> event = new HashMap<>();
                    event.put("name", r.getUser().getFullName());
                    event.put("fromDate", r.getFromDate().toString());
                    event.put("toDate", r.getToDate().toString());
                    event.put("type", r.getLeaveType());
                    calendarEvents.add(event);
                }
                model.addAttribute("calendarEvents", calendarEvents);

                // Balance cards
                model.addAttribute("entitledLeaves", entitledLeaves);
                model.addAttribute("usedLeaves", usedLeaves);
                model.addAttribute("pendingLeaves", pendingLeaves);
                model.addAttribute("remainingLeaves", remainingLeaves);

                // Department Options
                Set<String> departments = new java.util.TreeSet<>();
                for (User u : allUsers) {
                    String d = getDepartment(u);
                    if (!d.isBlank() && !"Unknown".equalsIgnoreCase(d)) {
                        departments.add(d);
                    }
                }
                model.addAttribute("departments", departments);

                // Team Leave Summary rows from real users
                List<Map<String, Object>> teamLeaveSummary = new ArrayList<>();
                Map<String, List<User>> deptUsersMap = employees.stream()
                        .collect(java.util.stream.Collectors.groupingBy(this::getDepartment));

                for (Map.Entry<String, List<User>> entry : deptUsersMap.entrySet()) {
                    String dName = entry.getKey();
                    if ("Unknown".equalsIgnoreCase(dName) || dName.isBlank()) continue;
                    List<User> dUsers = entry.getValue();
                    long dMembers = dUsers.size();
                    
                    Set<Long> dUserIds = dUsers.stream().map(User::getId).collect(java.util.stream.Collectors.toSet());
                    long dOnLeave = leaveRequestRepository.findAll().stream()
                            .filter(r -> "Approved".equalsIgnoreCase(r.getStatus()))
                            .filter(r -> r.getUser() != null && dUserIds.contains(r.getUser().getId()))
                            .filter(r -> isDateInLeaveRange(LocalDate.now(), r.getFromDate(), r.getToDate()))
                            .count();
                            
                    double dUtil = 0.0;
                    double dOpening = wallets.stream().filter(w -> w.getUser() != null && dUserIds.contains(w.getUser().getId())).mapToDouble(w -> w.getOpeningBalance() != null ? w.getOpeningBalance() : 0.0).sum();
                    double dUsed = wallets.stream().filter(w -> w.getUser() != null && dUserIds.contains(w.getUser().getId())).mapToDouble(w -> w.getUsedBalance() != null ? w.getUsedBalance() : 0.0).sum();
                    if (dOpening > 0) {
                        dUtil = (dUsed * 100.0) / dOpening;
                    }
                    
                    double dAtt = 0.0;
                    long dTotalDays = allAttendance.stream().filter(a -> a.getUser() != null && dUserIds.contains(a.getUser().getId()) && ("Present".equalsIgnoreCase(a.getStatus()) || "Absent".equalsIgnoreCase(a.getStatus()))).count();
                    long dPresentDays = allAttendance.stream().filter(a -> a.getUser() != null && dUserIds.contains(a.getUser().getId()) && "Present".equalsIgnoreCase(a.getStatus())).count();
                    if (dTotalDays > 0) {
                        dAtt = (dPresentDays * 100.0) / dTotalDays;
                    }

                    teamLeaveSummary.add(Map.of(
                        "name", dName,
                        "members", dMembers,
                        "onLeave", dOnLeave,
                        "util", dUtil,
                        "attendance", dAtt
                    ));
                }

                teamLeaveSummary.sort(Comparator.comparing(m -> String.valueOf(m.get("name")), String.CASE_INSENSITIVE_ORDER));
                model.addAttribute("teamLeaveSummary", teamLeaveSummary);

                // Escalated Leave Requests
                List<Map<String, Object>> escalatedRequests = new ArrayList<>();
                List<LeaveRequest> pendingRequests = leaveRequestRepository.findAll().stream()
                        .filter(r -> "Pending".equalsIgnoreCase(r.getStatus()) && r.getUser() != null && teamMemberIds.contains(r.getUser().getId()))
                        .collect(java.util.stream.Collectors.toList());

                DateTimeFormatter dtfFormat = DateTimeFormatter.ofPattern("d MMM yyyy");
                for (LeaveRequest r : pendingRequests) {
                    Map<String, Object> req = new HashMap<>();
                    req.put("id", r.getId());
                    req.put("employee", r.getUser() != null ? r.getUser().getFullName() : "Unknown");
                    req.put("designation", r.getUser() != null && r.getUser().getDesignation() != null ? r.getUser().getDesignation() : "Employee");
                    req.put("type", r.getLeaveType() != null ? r.getLeaveType() : "Casual Leave");
                    
                    String period = "";
                    if (r.getFromDate() != null) {
                        period = r.getFromDate().format(dtfFormat);
                        if (r.getToDate() != null && !r.getToDate().equals(r.getFromDate())) {
                            period += " - " + r.getToDate().format(dtfFormat);
                        }
                    }
                    req.put("period", period);
                    req.put("days", r.getTotalDays() != null ? r.getTotalDays() : 1.0);
                    req.put("escalatedBy", "Admin");
                    req.put("escalatedOn", r.getCreatedAt() != null ? r.getCreatedAt().format(dtfFormat) : LocalDate.now().format(dtfFormat));
                    req.put("reason", r.getReason() != null ? r.getReason() : "General request");
                    req.put("status", "PENDING");
                    req.put("department", r.getUser() != null ? getDepartment(r.getUser()) : "Unknown");
                    escalatedRequests.add(req);
                }
                model.addAttribute("escalatedRequests", escalatedRequests);

                // Escalate stats
                long pendingEscalations = leaveEscalationRepository.findAll().stream().filter(e -> "Pending".equalsIgnoreCase(e.getStatus())).count();
                long escalatedToYou = leaveEscalationRepository.findAll().stream().filter(e -> loggedInUser.getId().equals(e.getEscalatedTo().getId())).count();
                long overdueEscalations = leaveEscalationRepository.findAll().stream().filter(e -> "Pending".equalsIgnoreCase(e.getStatus()) && e.getEscalationDate() != null && e.getEscalationDate().isBefore(java.time.LocalDate.now().minusDays(2))).count();
                long resolvedEscalations = leaveEscalationRepository.findAll().stream().filter(e -> "Resolved".equalsIgnoreCase(e.getStatus())).count();
                
                // Fallbacks if data empty
                if (pendingEscalations == 0) pendingEscalations = 5;
                if (escalatedToYou == 0) escalatedToYou = 3;
                if (overdueEscalations == 0) overdueEscalations = 2;
                if (resolvedEscalations == 0) resolvedEscalations = 12;

                model.addAttribute("pendingEscalations", pendingEscalations);
                model.addAttribute("escalatedToYou", escalatedToYou);
                model.addAttribute("overdueEscalations", overdueEscalations);
                model.addAttribute("resolvedEscalations", resolvedEscalations);

                // Override History from DB completed leaves
                List<Map<String, Object>> overrideHistory = new ArrayList<>();
                List<LeaveRequest> completedLeaves = leaveRequestRepository.findAll().stream()
                        .filter(r -> r.getUser() != null && teamMemberIds.contains(r.getUser().getId()))
                        .filter(r -> "Approved".equalsIgnoreCase(r.getStatus()) || "Rejected".equalsIgnoreCase(r.getStatus()))
                        .sorted((a, b) -> {
                            LocalDate da = a.getCreatedAt() != null ? a.getCreatedAt() : a.getFromDate();
                            LocalDate db = b.getCreatedAt() != null ? b.getCreatedAt() : b.getFromDate();
                            if (da == null && db == null) return 0;
                            if (da == null) return 1;
                            if (db == null) return -1;
                            return db.compareTo(da);
                        })
                        .limit(10)
                        .collect(java.util.stream.Collectors.toList());

                for (LeaveRequest r : completedLeaves) {
                    overrideHistory.add(Map.of(
                        "employee", r.getUser().getFullName(),
                        "type", r.getLeaveType() != null ? r.getLeaveType() : "Casual Leave",
                        "action", r.getStatus().toUpperCase(),
                        "actionBy", "You",
                        "dateTime", r.getCreatedAt() != null ? r.getCreatedAt().format(DateTimeFormatter.ofPattern("d MMM yyyy")) : "-"
                    ));
                }
                model.addAttribute("overrideHistory", overrideHistory);

                // Attendance Reports Table
                List<Map<String, Object>> attendanceRecords = new ArrayList<>();
                Map<User, List<Attendance>> userAttendanceMap = allAttendance.stream()
                        .collect(java.util.stream.Collectors.groupingBy(Attendance::getUser));
                        
                for (Map.Entry<User, List<Attendance>> entry : userAttendanceMap.entrySet()) {
                    User u = entry.getKey();
                    if (u.getRole() != null && "CLIENT".equalsIgnoreCase(u.getRole().getRoleName())) continue;
                    if (u.getId().equals(loggedInUser.getId())) continue;
                    
                    List<Attendance> list = entry.getValue();
                    long days = list.size();
                    long present = list.stream().filter(a -> "Present".equalsIgnoreCase(a.getStatus())).count();
                    long absent = list.stream().filter(a -> "Absent".equalsIgnoreCase(a.getStatus())).count();
                    long leave = list.stream().filter(a -> "Leave".equalsIgnoreCase(a.getStatus())).count();
                    long half = list.stream().filter(a -> "Half-Day".equalsIgnoreCase(a.getStatus()) || "Half Day".equalsIgnoreCase(a.getStatus())).count();
                    
                    double percent = days > 0 ? (present * 100.0) / days : 100.0;
                    
                    Map<String, Object> record = new HashMap<>();
                    record.put("employee", u.getFullName());
                    record.put("designation", u.getDesignation() != null ? u.getDesignation() : "Employee");
                    record.put("department", getDepartment(u));
                    record.put("days", days);
                    record.put("present", present);
                    record.put("absent", absent);
                    record.put("leave", leave);
                    record.put("half", half);
                    record.put("percent", percent);
                    record.put("hours", "8h 45m");
                    record.put("location", u.getEmployeeProfile() != null && u.getEmployeeProfile().getWorkLocation() != null ? u.getEmployeeProfile().getWorkLocation() : "Shahdol");
                    attendanceRecords.add(record);
                }

                attendanceRecords.sort(Comparator.comparing(m -> String.valueOf(m.get("employee")), String.CASE_INSENSITIVE_ORDER));
                model.addAttribute("attendanceRecords", attendanceRecords);

                // Add chart metrics
                long overdueLeaveCount = 0;
                long pendingLeaveCount = 0;
                long approvedLeaveCount = 0;
                for (LeaveRequest r : leaveRequestRepository.findAll()) {
                    if (r.getUser() == null || !teamMemberIds.contains(r.getUser().getId())) continue;
                    if ("Approved".equalsIgnoreCase(r.getStatus())) {
                        approvedLeaveCount++;
                    } else if ("Pending".equalsIgnoreCase(r.getStatus())) {
                        if (r.getCreatedAt() != null && r.getCreatedAt().isBefore(LocalDate.now().minusDays(3))) {
                            overdueLeaveCount++;
                        } else {
                            pendingLeaveCount++;
                        }
                    }
                }
                model.addAttribute("overdueLeaveCount", overdueLeaveCount);
                model.addAttribute("pendingLeaveCount", pendingLeaveCount);
                model.addAttribute("approvedLeaveCount", approvedLeaveCount);

                long casualLeaveCount = 0;
                long sickLeaveCount = 0;
                long earnedLeaveCount = 0;
                long compOffCount = 0;
                for (LeaveRequest r : leaveRequestRepository.findAll()) {
                    if (r.getUser() == null || !teamMemberIds.contains(r.getUser().getId())) continue;
                    if (!"Approved".equalsIgnoreCase(r.getStatus())) continue;
                    String lt = r.getLeaveType() != null ? r.getLeaveType().toLowerCase() : "";
                    if (lt.contains("casual")) casualLeaveCount++;
                    else if (lt.contains("sick")) sickLeaveCount++;
                    else if (lt.contains("earned")) earnedLeaveCount++;
                    else if (lt.contains("comp")) compOffCount++;
                }
                model.addAttribute("casualLeaveCount", casualLeaveCount);
                model.addAttribute("sickLeaveCount", sickLeaveCount);
                model.addAttribute("earnedLeaveCount", earnedLeaveCount);
                model.addAttribute("compOffCount", compOffCount);

                // Last 7 days attendance trends
                List<LocalDate> recentDates = attendanceRepository.findAll().stream()
                        .map(Attendance::getDate)
                        .filter(java.util.Objects::nonNull)
                        .distinct()
                        .sorted(Comparator.reverseOrder())
                        .limit(7)
                        .sorted()
                        .collect(java.util.stream.Collectors.toList());

                List<String> attendanceLabels = new ArrayList<>();
                List<Double> presentTrend = new ArrayList<>();
                List<Double> absentTrend = new ArrayList<>();

                DateTimeFormatter labelFmt = DateTimeFormatter.ofPattern("d MMM");
                for (LocalDate d : recentDates) {
                    List<Attendance> dayAtt = attendanceRepository.findAll().stream()
                            .filter(a -> d.equals(a.getDate()) && a.getUser() != null && teamMemberIds.contains(a.getUser().getId()))
                            .collect(java.util.stream.Collectors.toList());
                    long total = dayAtt.size();
                    long present = dayAtt.stream().filter(a -> "Present".equalsIgnoreCase(a.getStatus())).count();
                    long absent = dayAtt.stream().filter(a -> "Absent".equalsIgnoreCase(a.getStatus())).count();
                    
                    attendanceLabels.add(d.format(labelFmt));
                    presentTrend.add(total > 0 ? (present * 100.0) / total : 0.0);
                    absentTrend.add(total > 0 ? (absent * 100.0) / total : 0.0);
                }

                if (attendanceLabels.isEmpty()) {
                    attendanceLabels = List.of("1 May", "5 May", "10 May", "15 May", "20 May", "25 May", "30 May");
                    presentTrend = List.of(0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0);
                    absentTrend = List.of(0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0);
                }
                
                model.addAttribute("attendanceLabels", attendanceLabels);
                model.addAttribute("presentTrend", presentTrend);
                model.addAttribute("absentTrend", absentTrend);

                // Department Attendance Donut Chart metrics from DB
                List<String> deptAttendanceLabels = new ArrayList<>();
                List<Double> deptAttendanceData = new ArrayList<>();
                Map<String, List<Attendance>> deptAttendanceMap = allAttendance.stream()
                        .collect(java.util.stream.Collectors.groupingBy(a -> getDepartment(a.getUser())));

                for (Map.Entry<String, List<Attendance>> entry : deptAttendanceMap.entrySet()) {
                    String dName = entry.getKey();
                    if ("Unknown".equalsIgnoreCase(dName) || dName.isBlank()) continue;
                    List<Attendance> list = entry.getValue();
                    long total = list.stream().filter(a -> "Present".equalsIgnoreCase(a.getStatus()) || "Absent".equalsIgnoreCase(a.getStatus())).count();
                    long present = list.stream().filter(a -> "Present".equalsIgnoreCase(a.getStatus())).count();
                    if (total > 0) {
                        double avg = (present * 100.0) / total;
                        deptAttendanceLabels.add(dName);
                        deptAttendanceData.add(Double.parseDouble(String.format(java.util.Locale.US, "%.2f", avg)));
                    }
                }
                if (deptAttendanceLabels.isEmpty()) {
                    deptAttendanceLabels = List.of("No Data");
                    deptAttendanceData = List.of(0.0);
                }
                model.addAttribute("deptAttendanceLabels", deptAttendanceLabels);
                model.addAttribute("deptAttendanceData", deptAttendanceData);

                return "senior_manager-leave_attendance";
        }

        @PreAuthorize("hasRole('SENIOR_MANAGER')")
        @PostMapping("/senior_manager/leave_attendance/action")
        public String handleLeaveAction(
                @RequestParam Long id,
                @RequestParam String action,
                org.springframework.web.servlet.mvc.support.RedirectAttributes redirectAttributes) {
                
                String employeeName = "Employee";
                if (id != null && id < 9900L) {
                    LeaveRequest r = leaveRequestRepository.findById(id).orElse(null);
                    if (r != null) {
                        employeeName = r.getUser() != null ? r.getUser().getFullName() : "Employee";
                        r.setStatus("Approve".equalsIgnoreCase(action) ? "PENDING_HR" : "Rejected");
                        r.setAdminComments("Approve".equalsIgnoreCase(action) ? "Approved by Senior Manager" : "Rejected by Senior Manager");
                        r.setActionDate(java.time.LocalDateTime.now());
                        leaveRequestRepository.save(r);
                    }
                } else {
                    if (id == 9991L) employeeName = "Pooja Desai";
                    else if (id == 9992L) employeeName = "Ankit Patel";
                    else if (id == 9993L) employeeName = "Neha Iyer";
                }
                
                redirectAttributes.addFlashAttribute("successMessage", "Leave request for " + employeeName + " has been successfully " + action + "d.");
                return "redirect:/senior_manager/leave_attendance?tab=escalate_override";
        }

        @PreAuthorize("hasRole('SENIOR_MANAGER')")
        @GetMapping("/senior_manager/project_work")
        public String showProjectWork(
                @RequestParam(value = "tab", defaultValue = "project") String tab,
                @RequestParam(value = "dept", required = false) String dept,
                @RequestParam(value = "status", required = false) String status,
                @RequestParam(value = "search", required = false) String search,
                Model model,
                Principal principal) {

                User loggedInUser = principal != null
                        ? userRepository.findByUsername(principal.getName()).orElse(null)
                        : null;

                if (loggedInUser == null) {
                    return "redirect:/login";
                }

                // Seed database if fresh
                seedProjectsAndTicketsAndTimesheets();

                model.addAttribute("loggedInUser", loggedInUser);
                model.addAttribute("activeTab", tab);
                model.addAttribute("selectedDept", dept);
                model.addAttribute("selectedStatus", status);
                model.addAttribute("searchQuery", search);

                // --- Fetch real data ---
                List<User> allUsers = userRepository.findAll();
                List<User> employees = allUsers.stream()
                        .filter(u -> u.getId() != null && !u.getId().equals(loggedInUser.getId()))
                        .filter(u -> u.getRole() == null || !"CLIENT".equalsIgnoreCase(u.getRole().getRoleName()))
                        .collect(java.util.stream.Collectors.toList());

                List<Project> allProjects = projectRepository.findAll();
                List<Ticket> allTickets = ticketRepository.findAll();
                List<WeeklyTimesheet> allTimesheets = weeklyTimesheetRepository.findAll();

                // 1. Calculate active, bench, exited counts
                long totalMembers = employees.size();
                long activeCount = employees.stream().filter(u -> "ACTIVE".equalsIgnoreCase(u.getStatus())).count();

                long benchCount = employees.stream()
                        .filter(u -> "ACTIVE".equalsIgnoreCase(u.getStatus()))
                        .filter(u -> allTickets.stream().noneMatch(t -> t.getAssignedTo() != null && t.getAssignedTo().getId().equals(u.getId())))
                        .count();

                long exitedCount = employees.stream().filter(u -> "EXITED".equalsIgnoreCase(u.getStatus())).count();

                model.addAttribute("totalMembers", totalMembers);
                model.addAttribute("activeCount", activeCount);
                model.addAttribute("benchCount", benchCount);
                model.addAttribute("exitedCount", exitedCount);

                // 2. Project List Tab (View 1)
                List<Map<String, Object>> projectRecords = new ArrayList<>();
                DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd MMM yyyy");
                for (Project p : allProjects) {
                    // Filter by search query if present
                    if (search != null && !search.isBlank() && !p.getProjectName().toLowerCase().contains(search.toLowerCase())) {
                        continue;
                    }
                    List<Ticket> projTickets = allTickets.stream()
                            .filter(t -> t.getProject() != null && t.getProject().getId().equals(p.getId()))
                            .collect(java.util.stream.Collectors.toList());

                    double avgProgress = 0.0;
                    if (!projTickets.isEmpty()) {
                        avgProgress = projTickets.stream().mapToDouble(t -> t.getProgressPercentage() != null ? t.getProgressPercentage() : 0.0).average().orElse(0.0);
                    }

                    // Collect team members assigned to this project
                    List<ProjectMember> pMembers = projectMemberRepository.findByProjectId(p.getId());
                    Set<User> teamUsers = pMembers.stream()
                            .map(ProjectMember::getUser)
                            .filter(java.util.Objects::nonNull)
                            .collect(java.util.stream.Collectors.toSet());

                    Map<String, Object> rec = new HashMap<>();
                    rec.put("projectId", p.getId());
                    rec.put("projectName", p.getProjectName());

                    LocalDate sDate = p.getStartDate();
                    LocalDate eDate = p.getEndDate();
                    if (sDate == null) sDate = p.getCreatedAt() != null ? p.getCreatedAt().toLocalDate() : LocalDate.of(2026, 4, 1);
                    if (eDate == null) eDate = sDate.plusMonths(4);

                    rec.put("startDate", sDate.format(dtf));
                    rec.put("endDate", eDate.format(dtf));
                    rec.put("rawStartDate", sDate.toString());
                    rec.put("rawEndDate", eDate.toString());
                    rec.put("progress", avgProgress);
                    rec.put("budget", "RS. 4.5 L/6.0 L");
                    rec.put("status", p.getStage() != null ? p.getStage() : "Active");
                    rec.put("teams", teamUsers);

                    List<Map<String, Object>> membersDetail = new ArrayList<>();
                    for (ProjectMember pm : pMembers) {
                        if (pm.getUser() == null) continue;
                        Map<String, Object> md = new HashMap<>();
                        md.put("userId", pm.getUser().getId());
                        md.put("name", pm.getUser().getFullName());
                        md.put("role", pm.getRole() != null ? pm.getRole() : "Developer");
                        md.put("billable", pm.getBillable() != null ? pm.getBillable() : true);
                        membersDetail.add(md);
                    }
                    rec.put("membersDetail", membersDetail);

                    String pDept = "IT Department";
                    if (!teamUsers.isEmpty()) {
                        pDept = getDepartment(teamUsers.iterator().next());
                    }
                    rec.put("department", pDept);
                    projectRecords.add(rec);
                }
                model.addAttribute("projectsList", projectRecords);

                List<User> activeEmployees = userRepository.findAll().stream()
                        .filter(u -> u.getStatus() != null && "ACTIVE".equalsIgnoreCase(u.getStatus()))
                        .filter(u -> u.getRole() == null || !"CLIENT".equalsIgnoreCase(u.getRole().getRoleName()))
                        .sorted(Comparator.comparing(User::getFullName, String.CASE_INSENSITIVE_ORDER))
                        .collect(java.util.stream.Collectors.toList());
                model.addAttribute("activeEmployees", activeEmployees);

                // 3. Team Work Tab (View 2)
                List<Map<String, Object>> teamworkRecords = new ArrayList<>();
                Map<User, List<Ticket>> userTicketsMap = allTickets.stream()
                        .filter(t -> t.getAssignedTo() != null)
                        .collect(java.util.stream.Collectors.groupingBy(Ticket::getAssignedTo));

                long teamworkTotalMembers = 0;
                long teamworkTasksAssigned = 0;
                long teamworkPending = 0;
                long teamworkRisk = 0;

                for (Map.Entry<User, List<Ticket>> entry : userTicketsMap.entrySet()) {
                    User u = entry.getKey();
                    if (u.getRole() != null && "CLIENT".equalsIgnoreCase(u.getRole().getRoleName())) continue;
                    if (u.getId().equals(loggedInUser.getId())) continue;

                    // Filter search by employee name
                    if (search != null && !search.isBlank() && !u.getFullName().toLowerCase().contains(search.toLowerCase())) {
                        continue;
                    }

                    List<Ticket> list = entry.getValue();
                    long taskCount = list.size();
                    long inProgress = list.stream().filter(t -> "In Progress".equalsIgnoreCase(t.getStatus()) || "Development".equalsIgnoreCase(t.getStatus())).count();
                    long completed = list.stream().filter(t -> "Completed".equalsIgnoreCase(t.getStatus())).count();
                    long overdue = list.stream()
                            .filter(t -> !"Completed".equalsIgnoreCase(t.getStatus()))
                            .filter(t -> t.getDeadline() != null && t.getDeadline().isBefore(LocalDate.now()))
                            .count();

                    double completionPercent = list.stream().mapToDouble(t -> t.getProgressPercentage() != null ? t.getProgressPercentage() : 0.0).average().orElse(0.0);

                    teamworkTotalMembers++;
                    teamworkTasksAssigned += taskCount;
                    teamworkPending += inProgress;
                    teamworkRisk += overdue;

                    Map<String, Object> rec = new HashMap<>();
                    rec.put("employeeName", u.getFullName());
                    rec.put("employeeCode", u.getUsername());
                    rec.put("taskAssigned", taskCount);
                    rec.put("inProgress", inProgress);
                    rec.put("completed", completed);
                    rec.put("overdue", overdue);
                    rec.put("percent", completionPercent);
                    rec.put("status", overdue > 0 ? "At Risk" : "On Track");
                    rec.put("department", getDepartment(u));
                    teamworkRecords.add(rec);
                }
                model.addAttribute("teamworkRecords", teamworkRecords);
                model.addAttribute("teamworkTotalMembers", teamworkTotalMembers);
                model.addAttribute("teamworkTasksAssigned", teamworkTasksAssigned);
                model.addAttribute("teamworkPending", teamworkPending);
                model.addAttribute("teamworkRisk", teamworkRisk);

                // 4. Timesheets Tab (View 3)
                List<Map<String, Object>> timesheetRecords = new ArrayList<>();
                long tsPending = 0;
                long tsRejected = 0;
                double tsTotalHours = 0;

                for (WeeklyTimesheet ts : allTimesheets) {
                    User u = ts.getUser();
                    if (u == null) continue;

                    // Filter search by employee name
                    if (search != null && !search.isBlank() && !u.getFullName().toLowerCase().contains(search.toLowerCase())) {
                        continue;
                    }

                    if ("Pending".equalsIgnoreCase(ts.getStatus())) tsPending++;
                    if ("Rejected".equalsIgnoreCase(ts.getStatus())) tsRejected++;
                    tsTotalHours += ts.getTotalWeekHours() != null ? ts.getTotalWeekHours() : 0.0;

                    Map<String, Object> rec = new HashMap<>();
                    rec.put("employeeName", u.getFullName());
                    rec.put("employeeCode", u.getUsername());
                    
                    String week = "";
                    if (ts.getWeekStartDate() != null && ts.getWeekEndDate() != null) {
                        week = ts.getWeekStartDate().format(DateTimeFormatter.ofPattern("d MMM")) + " - " + ts.getWeekEndDate().format(DateTimeFormatter.ofPattern("d MMM yyyy"));
                    }
                    rec.put("week", week);
                    rec.put("totalHours", (ts.getTotalWeekHours() != null ? ts.getTotalWeekHours() : 0.0) + " h");
                    rec.put("billable", (ts.getTotalWeekHours() != null ? ts.getTotalWeekHours() * 0.8 : 0.0) + " h");
                    rec.put("projects", 1);
                    rec.put("submittedOn", ts.getSubmissionDate() != null ? ts.getSubmissionDate().format(DateTimeFormatter.ofPattern("dd MMM yyyy")) : "-");
                    rec.put("status", ts.getStatus());
                    rec.put("department", getDepartment(u));
                    rec.put("location", u.getEmployeeProfile() != null && u.getEmployeeProfile().getWorkLocation() != null ? u.getEmployeeProfile().getWorkLocation() : "Shahdol");
                    timesheetRecords.add(rec);
                }

                model.addAttribute("timesheetRecords", timesheetRecords);
                model.addAttribute("tsTotalHours", tsTotalHours);
                model.addAttribute("tsBillableHours", tsTotalHours * 0.8);
                model.addAttribute("tsPending", tsPending);
                model.addAttribute("tsRejected", tsRejected);

                return "senior_manager-project_work";
        }

        @PreAuthorize("hasRole('SENIOR_MANAGER')")
        @GetMapping("/senior_manager/performance")
        public String showPerformance(
                @RequestParam(value = "tab", defaultValue = "overview") String tab,
                @RequestParam(value = "dept", required = false) String dept,
                @RequestParam(value = "cycle", defaultValue = "FY 2025-26") String cycle,
                @RequestParam(value = "search", required = false) String search,
                Model model,
                Principal principal) {

                User loggedInUser = principal != null
                        ? userRepository.findByUsername(principal.getName()).orElse(null)
                        : null;

                if (loggedInUser == null) {
                    return "redirect:/login";
                }

                seedPerformanceReviews();

                model.addAttribute("loggedInUser", loggedInUser);
                model.addAttribute("activeTab", tab);
                model.addAttribute("selectedDept", dept);
                model.addAttribute("selectedCycle", cycle);
                model.addAttribute("searchQuery", search);

                List<PerformanceReview> allReviews = performanceReviewRepository.findAll();
                List<Map<String, Object>> reviews = new ArrayList<>();

                long completedCount = 0;
                long inProgressCount = 0;
                long pendingCount = 0;
                long overdueCount = 0;
                double ratingSum = 0.0;
                long ratedEmployeesCount = 0;

                long outstanding = 0;
                long exceeds = 0;
                long meets = 0;
                long below = 0;
                long unsatisfactory = 0;

                long bucket1 = 0;
                long bucket2 = 0;
                long bucket3 = 0;
                long bucket4 = 0;
                long bucket5 = 0;

                DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd MMM yyyy");

                for (PerformanceReview pr : allReviews) {
                    User u = pr.getEmployee();
                    if (u == null) continue;

                    // Filter search query if present
                    if (search != null && !search.isBlank() && !u.getFullName().toLowerCase().contains(search.toLowerCase())) {
                        continue;
                    }
                    // Filter department if present
                    if (dept != null && !dept.isBlank() && !getDepartment(u).equalsIgnoreCase(dept)) {
                        continue;
                    }

                    Map<String, Object> r = new HashMap<>();
                    r.put("employeeName", u.getFullName());
                    r.put("employeeCode", u.getUsername());
                    r.put("department", getDepartment(u));
                    r.put("designation", pr.getDesignation());
                    r.put("status", pr.getStatus());
                    r.put("reviewType", pr.getReviewType());
                    r.put("reviewPeriod", pr.getReviewPeriod());
                    r.put("dueDate", pr.getDueDate() != null ? pr.getDueDate().format(dtf) : "-");
                    r.put("selfRating", pr.getSelfRating() != null ? pr.getSelfRating() : "-");
                    String userRole = (u.getRole() != null) ? u.getRole().getRoleName() : "EMPLOYEE";
                    r.put("roleLevel", userRole.contains("MANAGER") ? "L2" : "L1");
                    
                    String status = pr.getStatus();
                    if ("Completed".equalsIgnoreCase(status)) {
                        completedCount++;
                        double finalRating = pr.getFinalRating() != null ? pr.getFinalRating() : 0.0;
                        r.put("managerRating", pr.getManagerRating() != null ? pr.getManagerRating() : "-");
                        r.put("finalRating", finalRating > 0 ? finalRating : "-");
                        r.put("reviewDate", pr.getReviewDate() != null ? pr.getReviewDate().format(dtf) : "-");

                        if (finalRating > 0) {
                            ratingSum += finalRating;
                            ratedEmployeesCount++;

                            if (finalRating >= 4.5) { outstanding++; bucket5++; }
                            else if (finalRating >= 3.5) { exceeds++; bucket4++; }
                            else if (finalRating >= 2.5) { meets++; bucket3++; }
                            else if (finalRating >= 1.5) { below++; bucket2++; }
                            else { unsatisfactory++; bucket1++; }
                        }
                    } else {
                        if ("In Progress".equalsIgnoreCase(status)) inProgressCount++;
                        else if ("Overdue".equalsIgnoreCase(status)) overdueCount++;
                        else if ("Pending".equalsIgnoreCase(status)) pendingCount++;

                        r.put("managerRating", "-");
                        r.put("finalRating", "-");
                        r.put("reviewDate", "-");
                    }

                    reviews.add(r);
                }

                long totalMembers = reviews.size();
                double avgRating = ratedEmployeesCount > 0 ? (ratingSum / ratedEmployeesCount) : 0.0;

                model.addAttribute("totalMembers", totalMembers);
                model.addAttribute("completedCount", completedCount);
                model.addAttribute("inProgressCount", inProgressCount);
                model.addAttribute("pendingCount", pendingCount);
                model.addAttribute("overdueCount", overdueCount);
                model.addAttribute("avgRating", avgRating);

                model.addAttribute("outstanding", outstanding);
                model.addAttribute("exceeds", exceeds);
                model.addAttribute("meets", meets);
                model.addAttribute("below", below);
                model.addAttribute("unsatisfactory", unsatisfactory);

                model.addAttribute("bucket1", bucket1);
                model.addAttribute("bucket2", bucket2);
                model.addAttribute("bucket3", bucket3);
                model.addAttribute("bucket4", bucket4);
                model.addAttribute("bucket5", bucket5);

                model.addAttribute("reviewsList", reviews);

                List<User> allUsers = userRepository.findAll();
                Set<String> departments = new java.util.TreeSet<>();
                departments.addAll(List.of("IT Department", "Human Resources", "Finance Team", "Operations"));
                for (User u : allUsers) {
                    String d = getDepartment(u);
                    if (!d.isBlank() && !"Unknown".equalsIgnoreCase(d)) {
                        departments.add(d);
                    }
                }
                model.addAttribute("departments", departments);
                model.addAttribute("allUsers", allUsers);
                
                model.addAttribute("myCustomTeams", teamRepository.findByManager(loggedInUser));
                
                List<com.example.admindashboard.model.PerformanceFeedbackRequest> allFeedbackRequests = 
                    performanceFeedbackRequestRepository.findByManager(loggedInUser);
                
                List<com.example.admindashboard.model.PerformanceFeedbackRequest> pendingReview = allFeedbackRequests.stream()
                    .filter(r -> "COMPLETED".equalsIgnoreCase(r.getStatus()) && r.getManagerRating() == null)
                    .toList();
                    
                List<com.example.admindashboard.model.PerformanceFeedbackRequest> completedReview = allFeedbackRequests.stream()
                    .filter(r -> "FORWARDED_TO_HR".equalsIgnoreCase(r.getStatus()) || ("COMPLETED".equalsIgnoreCase(r.getStatus()) && r.getManagerRating() != null))
                    .toList();
                    
                List<com.example.admindashboard.model.PerformanceFeedbackRequest> pendingEmpResponse = allFeedbackRequests.stream()
                    .filter(r -> "PENDING".equalsIgnoreCase(r.getStatus()) || "DRAFT".equalsIgnoreCase(r.getStatus()))
                    .toList();
                
                model.addAttribute("feedbackRequests", allFeedbackRequests);
                model.addAttribute("pendingReviewRequests", pendingReview);
                model.addAttribute("completedReviewRequests", completedReview);
                model.addAttribute("pendingEmpRequests", pendingEmpResponse);

                return "senior_manager-performance";
        }

        private void seedPerformanceReviews() {
            if (performanceReviewRepository.count() == 0) {
                List<User> employees = userRepository.findAll().stream()
                        .filter(u -> u.getRole() == null || !"CLIENT".equalsIgnoreCase(u.getRole().getRoleName()))
                        .filter(u -> "ACTIVE".equalsIgnoreCase(u.getStatus()))
                        .collect(java.util.stream.Collectors.toList());

                java.util.Random rand = new java.util.Random();
                for (User u : employees) {
                    PerformanceReview pr = new PerformanceReview();
                    pr.setEmployee(u);
                    pr.setDepartment(getDepartment(u));
                    pr.setDesignation(u.getDesignation() != null ? u.getDesignation() : "Employee");
                    
                    int roll = rand.nextInt(100);
                    if (roll < 55) {
                        pr.setStatus("Completed");
                        double rating = Math.round((3.0 + rand.nextDouble() * 2.0) * 10.0) / 10.0;
                        pr.setSelfRating(Math.round((3.0 + rand.nextDouble() * 2.0) * 10.0) / 10.0);
                        pr.setManagerRating(rating);
                        pr.setFinalRating(rating);
                        pr.setReviewDate(LocalDate.now().minusDays(rand.nextInt(30)));
                    } else if (roll < 80) {
                        pr.setStatus("In Progress");
                        pr.setSelfRating(Math.round((3.0 + rand.nextDouble() * 2.0) * 10.0) / 10.0);
                    } else if (roll < 90) {
                        pr.setStatus("Overdue");
                        pr.setSelfRating(Math.round((3.0 + rand.nextDouble() * 2.0) * 10.0) / 10.0);
                        pr.setDueDate(LocalDate.now().minusDays(rand.nextInt(10) + 1));
                    } else {
                        pr.setStatus("Pending");
                    }
                    
                    pr.setReviewType(rand.nextBoolean() ? "Annual Review" : "Probation Review");
                    pr.setReviewPeriod("FY 2025-26");
                    if (pr.getDueDate() == null) {
                        pr.setDueDate(LocalDate.now().plusDays(rand.nextInt(30) + 1));
                    }
                    
                    performanceReviewRepository.save(pr);
                }
            }
        }

        @PreAuthorize("hasRole('SENIOR_MANAGER')")
        @GetMapping("/senior_manager/recruitment")
        public String showRecruitment(
                @RequestParam(value = "tab", defaultValue = "requisitions") String tab,
                @RequestParam(value = "dept", required = false) String dept,
                @RequestParam(value = "search", required = false) String search,
                Model model,
                Principal principal) {

                User loggedInUser = principal != null
                        ? userRepository.findByUsername(principal.getName()).orElse(null)
                        : null;

                if (loggedInUser == null) {
                    return "redirect:/login";
                }

                model.addAttribute("loggedInUser", loggedInUser);
                model.addAttribute("activeTab", tab);
                model.addAttribute("selectedDept", dept);
                model.addAttribute("searchQuery", search);

                if (jobPostingRepository.count() == 0) {
                    seedJobPostings();
                }

                List<User> allUsers = userRepository.findAll();
                Set<String> departments = new java.util.TreeSet<>();
                departments.addAll(List.of("IT - Development", "IT - Design", "IT - Quality", "IT - Operations", "Business"));
                for (User u : allUsers) {
                    String d = getDepartment(u);
                    if (!d.isBlank() && !"Unknown".equalsIgnoreCase(d)) {
                        departments.add(d);
                    }
                }
                model.addAttribute("departments", departments);

                // --- TAB 1: REQUISITIONS ---
                List<Map<String, Object>> requisitions = new ArrayList<>();
                List<JobPosting> postings = jobPostingRepository.findAll();
                long totalReqs = postings.size();
                long openReqs = 0;
                long inProgressReqs = 0;
                long offersExtendedReqs = 0;

                for (JobPosting jp : postings) {
                    Map<String, Object> req = new HashMap<>();
                    req.put("jobId", jp.getJobId() != null ? jp.getJobId() : "R-" + (100 + jp.getId()));
                    req.put("title", jp.getTitle());
                    req.put("department", jp.getDepartment() != null ? jp.getDepartment() : "IT - Development");
                    req.put("requestedOn", jp.getPostingDate() != null ? jp.getPostingDate().format(DateTimeFormatter.ofPattern("dd MMM yyyy")) : "28 May 2026");
                    req.put("positions", jp.getNoOfOpenings() != null ? jp.getNoOfOpenings() : 2);
                    
                    String stage = "Interview";
                    String status = "Open";
                    if (jp.getTitle().contains("DevOps")) {
                        stage = "Offer Extended";
                        status = "Offer Extended";
                        offersExtendedReqs++;
                    } else if (jp.getTitle().contains("Backend")) {
                        stage = "Feedback";
                        inProgressReqs++;
                    } else if (jp.getTitle().contains("UI/UX")) {
                        stage = "Shortlisted";
                        inProgressReqs++;
                    } else if (jp.getTitle().contains("QA")) {
                        stage = "Screening";
                        inProgressReqs++;
                    } else if (jp.getTitle().contains("Business") || !jp.isActive()) {
                        stage = "Closed";
                        status = "Closed";
                    } else {
                        openReqs++;
                    }
                    req.put("stage", stage);
                    req.put("status", status);
                    requisitions.add(req);
                }

                model.addAttribute("requisitionsList", requisitions);
                model.addAttribute("totalReqs", totalReqs);
                model.addAttribute("openReqs", openReqs);
                model.addAttribute("inProgressReqs", inProgressReqs);
                model.addAttribute("offersExtendedReqs", offersExtendedReqs);

                seedRecruitmentData();

                // --- TAB 2: MY INTERVIEWS ---
                List<Map<String, Object>> interviews = new ArrayList<>();
                for (Interview iv : interviewRepository.findAll()) {
                    if (search != null && !search.isBlank() && !iv.getCandidate().getName().toLowerCase().contains(search.toLowerCase())) {
                        continue;
                    }
                    if (dept != null && !dept.isBlank() && !iv.getDepartment().equalsIgnoreCase(dept)) {
                        continue;
                    }
                    Map<String, Object> map = new HashMap<>();
                    map.put("candidate", iv.getCandidate().getName());
                    map.put("candidateCode", iv.getCandidate().getCandidateCode());
                    map.put("title", iv.getTitle());
                    map.put("department", iv.getDepartment());
                    map.put("stage", iv.getStage());
                    map.put("dateTime", iv.getDateTime());
                    map.put("status", iv.getStatus());
                    
                    List<String> initialsList = new ArrayList<>();
                    if (iv.getInterviewerInitials() != null) {
                        for (String s : iv.getInterviewerInitials().split(",")) {
                            initialsList.add(s.trim());
                        }
                    }
                    map.put("interviewerInitials", initialsList);
                    interviews.add(map);
                }
                model.addAttribute("interviewsList", interviews);

                long totalInterviews = interviews.size();
                long upcomingInterviews = interviews.stream().filter(m -> "SCHEDULED".equalsIgnoreCase((String)m.get("status"))).count();
                long completedInterviews = interviews.stream().filter(m -> "COMPLETED".equalsIgnoreCase((String)m.get("status"))).count();
                long cancelledInterviews = interviews.stream().filter(m -> "CANCELLED".equalsIgnoreCase((String)m.get("status"))).count();
                long feedbackPendingInterviews = interviews.stream().filter(m -> "FEEDBACK PENDING".equalsIgnoreCase((String)m.get("status"))).count();

                model.addAttribute("totalInterviews", totalInterviews);
                model.addAttribute("upcomingInterviews", upcomingInterviews);
                model.addAttribute("completedInterviews", completedInterviews);
                model.addAttribute("cancelledInterviews", cancelledInterviews);
                model.addAttribute("feedbackPendingInterviews", feedbackPendingInterviews);

                // --- TAB 3: FEEDBACK ---
                List<Map<String, Object>> feedbackList = new ArrayList<>();
                for (InterviewFeedback fb : interviewFeedbackRepository.findAll()) {
                    if (search != null && !search.isBlank() && !fb.getCandidate().getName().toLowerCase().contains(search.toLowerCase())) {
                        continue;
                    }
                    if (dept != null && !dept.isBlank() && !fb.getDepartment().equalsIgnoreCase(dept)) {
                        continue;
                    }
                    Map<String, Object> map = new HashMap<>();
                    map.put("id", fb.getId());
                    map.put("candidate", fb.getCandidate().getName());
                    map.put("candidateCode", fb.getCandidate().getCandidateCode());
                    map.put("title", fb.getTitle());
                    map.put("department", fb.getDepartment());
                    map.put("stage", fb.getStage());
                    map.put("interviewDate", fb.getInterviewDate());
                    map.put("requestedOn", fb.getRequestedOn());
                    map.put("dueDate", fb.getDueDate());
                    map.put("status", fb.getStatus());
                    feedbackList.add(map);
                }
                model.addAttribute("feedbackList", feedbackList);

                long fbTotalRequests = feedbackList.size();
                long fbCompleted = feedbackList.stream().filter(m -> "Completed".equalsIgnoreCase((String)m.get("status"))).count();
                long fbPending = feedbackList.stream().filter(m -> "Pending".equalsIgnoreCase((String)m.get("status"))).count();
                long fbOverdue = feedbackList.stream().filter(m -> "Overdue".equalsIgnoreCase((String)m.get("status"))).count();
                long fbUpcoming = feedbackList.stream().filter(m -> "Upcoming".equalsIgnoreCase((String)m.get("status"))).count();

                model.addAttribute("fbTotalRequests", fbTotalRequests);
                model.addAttribute("fbCompleted", fbCompleted);
                model.addAttribute("fbPending", fbPending);
                model.addAttribute("fbOverdue", fbOverdue);
                model.addAttribute("fbUpcoming", fbUpcoming);

                // --- TAB 4: APPROVALS ---
                List<Map<String, Object>> approvalsList = new ArrayList<>();
                for (RecruitmentApproval ap : recruitmentApprovalRepository.findAll()) {
                    if (search != null && !search.isBlank() && !ap.getCandidate().getName().toLowerCase().contains(search.toLowerCase())) {
                        continue;
                    }
                    if (dept != null && !dept.isBlank() && !ap.getDepartment().equalsIgnoreCase(dept)) {
                        continue;
                    }
                    Map<String, Object> map = new HashMap<>();
                    map.put("id", ap.getId());
                    map.put("candidate", ap.getCandidate().getName());
                    map.put("email", ap.getEmail());
                    map.put("title", ap.getTitle());
                    map.put("department", ap.getDepartment());
                    map.put("finalInterviewDate", ap.getFinalInterviewDate());
                    map.put("rating", ap.getRating());
                    map.put("recommendation", ap.getRecommendation());
                    map.put("status", ap.getStatus());
                    approvalsList.add(map);
                }
                model.addAttribute("approvalsList", approvalsList);

                long apTotalApprovals = approvalsList.size();
                long apPending = approvalsList.stream().filter(m -> "Pending".equalsIgnoreCase((String)m.get("status"))).count();
                long apApproved = approvalsList.stream().filter(m -> "Approved".equalsIgnoreCase((String)m.get("status"))).count();
                long apRejected = approvalsList.stream().filter(m -> "Rejected".equalsIgnoreCase((String)m.get("status"))).count();
                long apHold = approvalsList.stream().filter(m -> "On Hold".equalsIgnoreCase((String)m.get("status"))).count();

                model.addAttribute("apTotalApprovals", apTotalApprovals);
                model.addAttribute("apPending", apPending);
                model.addAttribute("apApproved", apApproved);
                model.addAttribute("apRejected", apRejected);
                model.addAttribute("apHold", apHold);

                List<Map<String, Object>> activeEmployees = new ArrayList<>();
                for (User u : allUsers) {
                    if (u.getRole() == null || !"CLIENT".equalsIgnoreCase(u.getRole().getRoleName())) {
                        Map<String, Object> emp = new HashMap<>();
                        emp.put("name", u.getFullName());
                        emp.put("designation", u.getDesignation() != null ? u.getDesignation() : "Employee");
                        emp.put("email", u.getEmail());
                        String initials = "AS";
                        if (u.getFullName() != null && u.getFullName().trim().contains(" ")) {
                            int spaceIdx = u.getFullName().trim().indexOf(" ");
                            initials = u.getFullName().substring(0, 1) + u.getFullName().substring(spaceIdx + 1, spaceIdx + 2);
                        } else if (u.getFullName() != null && u.getFullName().length() > 1) {
                            initials = u.getFullName().substring(0, 2);
                        }
                        emp.put("initials", initials.toUpperCase());
                        activeEmployees.add(emp);
                    }
                }
                model.addAttribute("activeEmployees", activeEmployees);

                return "senior_manager-recruitment";
        }

        private void seedJobPostings() {
            try {
                JobPosting jp1 = new JobPosting();
                jp1.setJobId("R-100");
                jp1.setTitle("Frontend Developer");
                jp1.setDepartment("IT - Development");
                jp1.setNoOfOpenings(2);
                jp1.setPostingDate(LocalDate.of(2026, 5, 28));
                jp1.setActive(true);
                jobPostingRepository.save(jp1);

                JobPosting jp2 = new JobPosting();
                jp2.setJobId("R-101");
                jp2.setTitle("Backend Developer");
                jp2.setDepartment("IT - Development");
                jp2.setNoOfOpenings(3);
                jp2.setPostingDate(LocalDate.of(2026, 5, 26));
                jp2.setActive(true);
                jobPostingRepository.save(jp2);

                JobPosting jp3 = new JobPosting();
                jp3.setJobId("R-102");
                jp3.setTitle("UI/UX Designer");
                jp3.setDepartment("IT - Design");
                jp3.setNoOfOpenings(1);
                jp3.setPostingDate(LocalDate.of(2026, 5, 22));
                jp3.setActive(true);
                jobPostingRepository.save(jp3);

                JobPosting jp4 = new JobPosting();
                jp4.setJobId("R-103");
                jp4.setTitle("QA Engineer");
                jp4.setDepartment("IT - Quality");
                jp4.setNoOfOpenings(2);
                jp4.setPostingDate(LocalDate.of(2026, 5, 18));
                jp4.setActive(true);
                jobPostingRepository.save(jp4);

                JobPosting jp5 = new JobPosting();
                jp5.setJobId("R-104");
                jp5.setTitle("DevOps Engineer");
                jp5.setDepartment("IT - Operations");
                jp5.setNoOfOpenings(1);
                jp5.setPostingDate(LocalDate.of(2026, 5, 15));
                jp5.setActive(true);
                jobPostingRepository.save(jp5);

                JobPosting jp6 = new JobPosting();
                jp6.setJobId("R-105");
                jp6.setTitle("Business Analyst");
                jp6.setDepartment("Business");
                jp6.setNoOfOpenings(2);
                jp6.setPostingDate(LocalDate.of(2026, 5, 10));
                jp6.setActive(false);
                jobPostingRepository.save(jp6);
            } catch (Exception e) {
                // ignore
            }
        }

        private void seedExpensesData() {
            if (expenseClaimRepository.count() == 0) {
                User vikram = userRepository.findByUsername("EMP001").orElse(null);
                if (vikram == null) {
                    vikram = userRepository.findAll().stream().filter(u -> !u.getUsername().equals("ADMIN")).findFirst().orElse(null);
                }
                User neha = userRepository.findByUsername("EMP002").orElse(null);
                if (neha == null && vikram != null) neha = vikram;
                User rahul = userRepository.findByUsername("EMP003").orElse(null);
                if (rahul == null && vikram != null) rahul = vikram;
                User ankit = userRepository.findByUsername("EMP004").orElse(null);
                if (ankit == null && vikram != null) ankit = vikram;
                User pooja = userRepository.findByUsername("EMP005").orElse(null);
                if (pooja == null && vikram != null) pooja = vikram;

                if (vikram != null) {
                    ExpenseClaim c1 = new ExpenseClaim();
                    c1.setUser(vikram); c1.setPurpose("Client meeting - Travel"); c1.setAmount(12450.0); c1.setStatus("Pending"); c1.setSubmissionDate(LocalDate.now());
                    expenseClaimRepository.save(c1);
                }
                if (neha != null) {
                    ExpenseClaim c2 = new ExpenseClaim();
                    c2.setUser(neha); c2.setPurpose("Work from home setup"); c2.setAmount(8750.0); c2.setStatus("Approved"); c2.setSubmissionDate(LocalDate.now());
                    expenseClaimRepository.save(c2);
                }
                if (rahul != null) {
                    ExpenseClaim c3 = new ExpenseClaim();
                    c3.setUser(rahul); c3.setPurpose("Team Building Activity"); c3.setAmount(50000.0); c3.setStatus("Pending"); c3.setSubmissionDate(LocalDate.now());
                    expenseClaimRepository.save(c3);
                }
                if (ankit != null) {
                    ExpenseClaim c4 = new ExpenseClaim();
                    c4.setUser(ankit); c4.setPurpose("Software Subscription"); c4.setAmount(15999.0); c4.setStatus("Approved"); c4.setSubmissionDate(LocalDate.now());
                    expenseClaimRepository.save(c4);
                }
            }

            if (reimbursementRequestRepository.count() == 0) {
                User neha = userRepository.findByUsername("EMP002").orElse(null);
                if (neha == null) neha = userRepository.findAll().stream().filter(u -> !u.getUsername().equals("ADMIN")).findFirst().orElse(null);
                User rahul = userRepository.findByUsername("EMP003").orElse(null);
                if (rahul == null && neha != null) rahul = neha;
                User ankit = userRepository.findByUsername("EMP004").orElse(null);
                if (ankit == null && neha != null) ankit = neha;
                User pooja = userRepository.findByUsername("EMP005").orElse(null);
                if (pooja == null && neha != null) pooja = neha;

                if (neha != null) {
                    ReimbursementRequest r1 = new ReimbursementRequest();
                    r1.setUser(neha); r1.setPurpose("Travel - Client Meeting"); r1.setAmount(8750.0); r1.setStatus("Approved"); r1.setSubmissionDate(LocalDate.now());
                    reimbursementRequestRepository.save(r1);
                }
                if (rahul != null) {
                    ReimbursementRequest r2 = new ReimbursementRequest();
                    r2.setUser(rahul); r2.setPurpose("Work from home setup"); r2.setAmount(2350.0); r2.setStatus("Pending"); r2.setSubmissionDate(LocalDate.now());
                    reimbursementRequestRepository.save(r2);
                }
                if (ankit != null) {
                    ReimbursementRequest r3 = new ReimbursementRequest();
                    r3.setUser(ankit); r3.setPurpose("Software Subscription"); r3.setAmount(15999.0); r3.setStatus("Approved"); r3.setSubmissionDate(LocalDate.now());
                    reimbursementRequestRepository.save(r3);
                }
                if (pooja != null) {
                    ReimbursementRequest r4 = new ReimbursementRequest();
                    r4.setUser(pooja); r4.setPurpose("Internet & Mobile"); r4.setAmount(2150.0); r4.setStatus("Rejected"); r4.setSubmissionDate(LocalDate.now());
                    reimbursementRequestRepository.save(r4);
                }
            }

            if (budgetRequestRepository.count() == 0) {
                User vikram = userRepository.findByUsername("EMP001").orElse(null);
                if (vikram == null) vikram = userRepository.findAll().stream().filter(u -> !u.getUsername().equals("ADMIN")).findFirst().orElse(null);
                User neha = userRepository.findByUsername("EMP002").orElse(null);
                if (neha == null && vikram != null) neha = vikram;
                User rahul = userRepository.findByUsername("EMP003").orElse(null);
                if (rahul == null && vikram != null) rahul = vikram;
                User ankit = userRepository.findByUsername("EMP004").orElse(null);
                if (ankit == null && vikram != null) ankit = vikram;
                User pooja = userRepository.findByUsername("EMP005").orElse(null);
                if (pooja == null && vikram != null) pooja = vikram;

                if (vikram != null) {
                    BudgetRequest b1 = new BudgetRequest();
                    b1.setRequester(vikram); b1.setDepartment("Information Technology"); b1.setType("PROJECT BUDGET"); b1.setPurpose("New CRM Software Implementation"); b1.setAmount(250000.0); b1.setStatus("Pending"); b1.setSubmissionDate(LocalDate.now());
                    budgetRequestRepository.save(b1);
                }
                if (neha != null) {
                    BudgetRequest b2 = new BudgetRequest();
                    b2.setRequester(neha); b2.setDepartment("Human Resources"); b2.setType("TEAM EVENT"); b2.setPurpose("Annual Team Offsite 2025"); b2.setAmount(120000.0); b2.setStatus("Approved"); b2.setSubmissionDate(LocalDate.now());
                    budgetRequestRepository.save(b2);
                }
                if (rahul != null) {
                    BudgetRequest b3 = new BudgetRequest();
                    b3.setRequester(rahul); b3.setDepartment("Human Resources"); b3.setType("HIRING"); b3.setPurpose("Q3 Recruitment Drive Costs"); b3.setAmount(300000.0); b3.setStatus("Pending"); b3.setSubmissionDate(LocalDate.now());
                    budgetRequestRepository.save(b3);
                }
                if (ankit != null) {
                    BudgetRequest b4 = new BudgetRequest();
                    b4.setRequester(ankit); b4.setDepartment("Information Technology"); b4.setType("IT INFRA"); b4.setPurpose("Server Upgrade & Maintenance"); b4.setAmount(175000.0); b4.setStatus("Approved"); b4.setSubmissionDate(LocalDate.now());
                    budgetRequestRepository.save(b4);
                }
                if (pooja != null) {
                    BudgetRequest b5 = new BudgetRequest();
                    b5.setRequester(pooja); b5.setDepartment("Finance Team"); b5.setType("OPERATIONAL"); b5.setPurpose("Finance Tools Annual Subscription"); b5.setAmount(85000.0); b5.setStatus("Rejected"); b5.setSubmissionDate(LocalDate.now());
                    budgetRequestRepository.save(b5);
                }
            }
        }

        private void seedRecruitmentData() {
            if (candidateRepository.count() == 0) {
                Candidate c1 = new Candidate();
                c1.setCandidateCode("CAN-2026-045"); c1.setName("Aman Singh"); c1.setEmail("aman@gmail.com"); c1.setJobTitle("Frontend Developer"); c1.setDepartment("IT - Development");
                candidateRepository.save(c1);

                Candidate c2 = new Candidate();
                c2.setCandidateCode("CAN-2026-038"); c2.setName("Priya Rathi"); c2.setEmail("priya@gmail.com"); c2.setJobTitle("Backend Developer"); c2.setDepartment("IT - Development");
                candidateRepository.save(c2);

                Candidate c3 = new Candidate();
                c3.setCandidateCode("CAN-2026-034"); c3.setName("Rohit Kumar"); c3.setEmail("rohit@gmail.com"); c3.setJobTitle("UI/UX Designer"); c3.setDepartment("IT - Design");
                candidateRepository.save(c3);

                Candidate c4 = new Candidate();
                c4.setCandidateCode("CAN-2026-029"); c4.setName("Sneha Nair"); c4.setEmail("sneha@gmail.com"); c4.setJobTitle("QA Engineer"); c4.setDepartment("IT - Quality");
                candidateRepository.save(c4);

                Candidate c5 = new Candidate();
                c5.setCandidateCode("CAN-2026-022"); c5.setName("Vikas Dubey"); c5.setEmail("vikas@gmail.com"); c5.setJobTitle("DevOps Engineer"); c5.setDepartment("IT - Operations");
                candidateRepository.save(c5);
            }

            if (interviewRepository.count() == 0) {
                Candidate aman = candidateRepository.findByName("Aman Singh").orElse(null);
                Candidate priya = candidateRepository.findByName("Priya Rathi").orElse(null);
                Candidate rohit = candidateRepository.findByName("Rohit Kumar").orElse(null);
                Candidate sneha = candidateRepository.findByName("Sneha Nair").orElse(null);
                Candidate vikas = candidateRepository.findByName("Vikas Dubey").orElse(null);

                if (aman != null) {
                    Interview i1 = new Interview();
                    i1.setCandidate(aman); i1.setTitle("Frontend Developer"); i1.setDepartment("IT - Development"); i1.setStage("Technical Round"); i1.setInterviewerInitials("AS,NV,RK"); i1.setDateTime("29 May 2026 10:00 AM - 11:00 AM"); i1.setStatus("Scheduled");
                    interviewRepository.save(i1);
                }
                if (priya != null) {
                    Interview i2 = new Interview();
                    i2.setCandidate(priya); i2.setTitle("Backend Developer"); i2.setDepartment("IT - Development"); i2.setStage("HR Round"); i2.setInterviewerInitials("PR,NV"); i2.setDateTime("28 May 2026 02:00 PM - 03:00 PM"); i2.setStatus("Scheduled");
                    interviewRepository.save(i2);
                }
                if (rohit != null) {
                    Interview i3 = new Interview();
                    i3.setCandidate(rohit); i3.setTitle("UI/UX Designer"); i3.setDepartment("IT - Design"); i3.setStage("Design Round"); i3.setInterviewerInitials("RK,NV,RK"); i3.setDateTime("27 May 2026 11:00 AM - 12:00 PM"); i3.setStatus("Completed");
                    interviewRepository.save(i3);
                }
                if (sneha != null) {
                    Interview i4 = new Interview();
                    i4.setCandidate(sneha); i4.setTitle("QA Engineer"); i4.setDepartment("IT - Quality"); i4.setStage("Technical Round"); i4.setInterviewerInitials("SN,NV,PO"); i4.setDateTime("26 May 2026 03:00 PM - 04:00 PM"); i4.setStatus("Completed");
                    interviewRepository.save(i4);
                }
                if (vikas != null) {
                    Interview i5 = new Interview();
                    i5.setCandidate(vikas); i5.setTitle("DevOps Engineer"); i5.setDepartment("IT - Operations"); i5.setStage("HR Round"); i5.setInterviewerInitials("VD,VI"); i5.setDateTime("25 May 2026 10:30 AM - 11:30 AM"); i5.setStatus("Cancelled");
                    interviewRepository.save(i5);
                }
            }

            if (interviewFeedbackRepository.count() == 0) {
                Candidate aman = candidateRepository.findByName("Aman Singh").orElse(null);
                Candidate priya = candidateRepository.findByName("Priya Rathi").orElse(null);
                Candidate rohit = candidateRepository.findByName("Rohit Kumar").orElse(null);

                if (aman != null) {
                    InterviewFeedback f1 = new InterviewFeedback();
                    f1.setCandidate(aman); f1.setTitle("Frontend Developer"); f1.setDepartment("IT - Development"); f1.setStage("Technical Round"); f1.setInterviewDate("29 May 2026 10:00 AM"); f1.setRequestedOn("27 May 2026"); f1.setDueDate("30 May 2026 (Overdue)"); f1.setStatus("PENDING");
                    interviewFeedbackRepository.save(f1);
                }
                if (priya != null) {
                    InterviewFeedback f2 = new InterviewFeedback();
                    f2.setCandidate(priya); f2.setTitle("Backend Developer"); f2.setDepartment("IT - Development"); f2.setStage("HR Round"); f2.setInterviewDate("28 May 2026 02:00 PM"); f2.setRequestedOn("26 May 2026"); f2.setDueDate("29 May 2026 (Overdue)"); f2.setStatus("PENDING");
                    interviewFeedbackRepository.save(f2);
                }
                if (rohit != null) {
                    InterviewFeedback f3 = new InterviewFeedback();
                    f3.setCandidate(rohit); f3.setTitle("UI/UX Designer"); f3.setDepartment("IT - Design"); f3.setStage("Design Round"); f3.setInterviewDate("27 May 2026 11:00 AM"); f3.setRequestedOn("25 May 2026"); f3.setDueDate("28 May 2026 (Overdue)"); f3.setStatus("PENDING");
                    interviewFeedbackRepository.save(f3);
                }
            }

            if (recruitmentApprovalRepository.count() == 0) {
                Candidate aman = candidateRepository.findByName("Aman Singh").orElse(null);
                Candidate priya = candidateRepository.findByName("Priya Rathi").orElse(null);
                Candidate rohit = candidateRepository.findByName("Rohit Kumar").orElse(null);
                Candidate sneha = candidateRepository.findByName("Sneha Nair").orElse(null);
                Candidate vikas = candidateRepository.findByName("Vikas Dubey").orElse(null);

                if (aman != null) {
                    RecruitmentApproval a1 = new RecruitmentApproval();
                    a1.setCandidate(aman); a1.setEmail("aman@gmail.com"); a1.setTitle("Frontend Developer"); a1.setDepartment("IT - Development"); a1.setFinalInterviewDate("29 May 2026 10:00 AM"); a1.setRating(4.2); a1.setRecommendation("Hire"); a1.setStatus("Pending");
                    recruitmentApprovalRepository.save(a1);
                }
                if (priya != null) {
                    RecruitmentApproval a2 = new RecruitmentApproval();
                    a2.setCandidate(priya); a2.setEmail("priya@gmail.com"); a2.setTitle("Backend Developer"); a2.setDepartment("IT - Development"); a2.setFinalInterviewDate("28 May 2026 02:00 PM"); a2.setRating(4.0); a2.setRecommendation("Hire"); a2.setStatus("Pending");
                    recruitmentApprovalRepository.save(a2);
                }
                if (rohit != null) {
                    RecruitmentApproval a3 = new RecruitmentApproval();
                    a3.setCandidate(rohit); a3.setEmail("rohit@gmail.com"); a3.setTitle("UI/UX Designer"); a3.setDepartment("IT - Design"); a3.setFinalInterviewDate("27 May 2026 11:00 AM"); a3.setRating(3.2); a3.setRecommendation("Consider"); a3.setStatus("Pending");
                    recruitmentApprovalRepository.save(a3);
                }
                if (sneha != null) {
                    RecruitmentApproval a4 = new RecruitmentApproval();
                    a4.setCandidate(sneha); a4.setEmail("sneha@gmail.com"); a4.setTitle("QA Engineer"); a4.setDepartment("IT - Quality"); a4.setFinalInterviewDate("26 May 2026 03:00 PM"); a4.setRating(3.5); a4.setRecommendation("Hold"); a4.setStatus("On Hold");
                    recruitmentApprovalRepository.save(a4);
                }
                if (vikas != null) {
                    RecruitmentApproval a5 = new RecruitmentApproval();
                    a5.setCandidate(vikas); a5.setEmail("vikas@gmail.com"); a5.setTitle("DevOps Engineer"); a5.setDepartment("IT - Operations"); a5.setFinalInterviewDate("25 May 2026 10:30 AM"); a5.setRating(2.8); a5.setRecommendation("Not Suitable"); a5.setStatus("Pending");
                    recruitmentApprovalRepository.save(a5);
                }
            }
        }

        private void seedCommunicationData() {
            if (communicationBroadcastRepository.count() == 0) {
                CommunicationBroadcast b1 = new CommunicationBroadcast();
                b1.setSubject("Project Update - Website Redesign"); b1.setSentTo("IT - Development Team"); b1.setType("Project Update"); b1.setPriority("Normal"); b1.setSentOn("28 May 2026, 10:30 AM"); b1.setRecipients(12); b1.setOpenRate(75); b1.setStatus("Sent");
                communicationBroadcastRepository.save(b1);

                CommunicationBroadcast b2 = new CommunicationBroadcast();
                b2.setSubject("Deadline Reminder: Sprint 3"); b2.setSentTo("Design Team"); b2.setType("Deadline Reminder"); b2.setPriority("High"); b2.setSentOn("27 May 2026, 04:15 PM"); b2.setRecipients(8); b2.setOpenRate(62); b2.setStatus("Sent");
                communicationBroadcastRepository.save(b2);
            }

            if (projectUpdateNotificationRepository.count() == 0) {
                ProjectUpdateNotification p1 = new ProjectUpdateNotification();
                p1.setSubject("Website Redesign - Progress Update"); p1.setProject("Website Redesign"); p1.setSharedWith("IT - Development Team"); p1.setSharedOn("28 May 2026, 10:30 AM"); p1.setRecipients(12); p1.setViews(18); p1.setAcknowledged("10 (83%)"); p1.setStatus("COMPLETED");
                projectUpdateNotificationRepository.save(p1);

                ProjectUpdateNotification p2 = new ProjectUpdateNotification();
                p2.setSubject("API Integration - Issue Update"); p2.setProject("Mobile App Development"); p2.setSharedWith("IT - Development Team"); p2.setSharedOn("27 May 2026, 04:15 PM"); p2.setRecipients(10); p2.setViews(14); p2.setAcknowledged("6 (60%)"); p2.setStatus("OPEN");
                projectUpdateNotificationRepository.save(p2);

                ProjectUpdateNotification p3 = new ProjectUpdateNotification();
                p3.setSubject("Sprint 3 - Task Update"); p3.setProject("CRM Integration"); p3.setSharedWith("Design Team"); p3.setSharedOn("26 May 2026, 11:05 AM"); p3.setRecipients(8); p3.setViews(12); p3.setAcknowledged("7 (88%)"); p3.setStatus("COMPLETED");
                projectUpdateNotificationRepository.save(p3);

                ProjectUpdateNotification p4 = new ProjectUpdateNotification();
                p4.setSubject("Milestone 1 Completion"); p4.setProject("Analytics Dashboard"); p4.setSharedWith("IT - Operations Team"); p4.setSharedOn("25 May 2026, 03:20 PM"); p4.setRecipients(6); p4.setViews(9); p4.setAcknowledged("5 (83%)"); p4.setStatus("COMPLETED");
                projectUpdateNotificationRepository.save(p4);

                ProjectUpdateNotification p5 = new ProjectUpdateNotification();
                p5.setSubject("General Update - Kickoff Notes"); p5.setProject("HRMS Enhancement"); p5.setSharedWith("HR Department"); p5.setSharedOn("24 May 2026, 09:00 AM"); p5.setRecipients(15); p5.setViews(21); p5.setAcknowledged("12 (80%)"); p5.setStatus("COMPLETED");
                projectUpdateNotificationRepository.save(p5);
            }

            if (recentNotificationRecordRepository.count() == 0) {
                RecentNotificationRecord n1 = new RecentNotificationRecord();
                n1.setSubject("Sprint 3 Deadline Reminder"); n1.setNotifyTo("IT - Development Team"); n1.setType("Deadline Reminder"); n1.setPriority("High"); n1.setSentOn("28 May 2026, 10:30 AM"); n1.setRecipients(12); n1.setStatus("Sent");
                recentNotificationRecordRepository.save(n1);

                RecentNotificationRecord n2 = new RecentNotificationRecord();
                n2.setSubject("Policy Update - Work From Home"); n2.setNotifyTo("All Employees"); n2.setType("Policy Change"); n2.setPriority("Normal"); n2.setSentOn("27 May 2026, 04:15 PM"); n2.setRecipients(156); n2.setStatus("Sent");
                recentNotificationRecordRepository.save(n2);

                RecentNotificationRecord n3 = new RecentNotificationRecord();
                n3.setSubject("Client Call Time Change"); n3.setNotifyTo("Design Team"); n3.setType("Schedule Change"); n3.setPriority("Normal"); n3.setSentOn("26 May 2026, 11:05 AM"); n3.setRecipients(8); n3.setStatus("Sent");
                recentNotificationRecordRepository.save(n3);

                RecentNotificationRecord n4 = new RecentNotificationRecord();
                n4.setSubject("Monthly Report Submission Deadline"); n4.setNotifyTo("Finance Team"); n4.setType("Deadline Reminder"); n4.setPriority("High"); n4.setSentOn("25 May 2026, 03:20 PM"); n4.setRecipients(6); n4.setStatus("Pending");
                recentNotificationRecordRepository.save(n4);

                RecentNotificationRecord n5 = new RecentNotificationRecord();
                n5.setSubject("System Maintenance - Saturday"); n5.setNotifyTo("All Employees"); n5.setType("General Update"); n5.setPriority("Low"); n5.setSentOn("24 May 2026, 09:00 AM"); n5.setRecipients(184); n5.setStatus("Scheduled");
                recentNotificationRecordRepository.save(n5);
            }
        }

        @PreAuthorize("hasRole('SENIOR_MANAGER')")
        @GetMapping("/senior_manager/expenses")
        public String showExpenses(
                @RequestParam(value = "tab", defaultValue = "approvals") String tab,
                @RequestParam(value = "dept", required = false) String dept,
                @RequestParam(value = "search", required = false) String search,
                Model model,
                Principal principal) {

                User loggedInUser = principal != null
                        ? userRepository.findByUsername(principal.getName()).orElse(null)
                        : null;

                if (loggedInUser == null) {
                    return "redirect:/login";
                }

                model.addAttribute("loggedInUser", loggedInUser);
                model.addAttribute("activeTab", tab);
                model.addAttribute("selectedDept", dept);
                model.addAttribute("searchQuery", search);

                List<User> allUsers = userRepository.findAll();
                Set<String> departments = new java.util.TreeSet<>();
                departments.addAll(List.of("Information Technology", "Human Resources", "Finance Team", "Operations"));
                for (User u : allUsers) {
                    String d = getDepartment(u);
                    if (!d.isBlank() && !"Unknown".equalsIgnoreCase(d)) {
                        departments.add(d);
                    }
                }
                model.addAttribute("departments", departments);

                seedExpensesData();

                 // --- TAB 1: EXPENSE APPROVALS ---
                 List<Map<String, Object>> approvals = new ArrayList<>();
                 for (ExpenseClaim claim : expenseClaimRepository.findAll()) {
                     User u = claim.getUser();
                     if (search != null && !search.isBlank() && !u.getFullName().toLowerCase().contains(search.toLowerCase())) {
                         continue;
                     }
                     if (dept != null && !dept.isBlank() && !getDepartment(u).equalsIgnoreCase(dept)) {
                         continue;
                     }
                     Map<String, Object> map = new HashMap<>();
                     map.put("id", "EXP-" + claim.getId());
                     map.put("employee", u.getFullName());
                     map.put("employeeCode", u.getUsername());
                     map.put("department", getDepartment(u));
                     map.put("type", "EXPENSE");
                     map.put("purpose", claim.getPurpose());
                     map.put("amount", claim.getAmount());
                     map.put("status", claim.getStatus());
                     map.put("assignedTo", claim.getAssignedTo() != null ? claim.getAssignedTo() : "-");
                     approvals.add(map);
                 }
                 model.addAttribute("approvalsList", approvals);
 
                 // --- TAB 2: REIMBURSEMENTS ---
                 List<Map<String, Object>> reimbursements = new ArrayList<>();
                 for (ReimbursementRequest req : reimbursementRequestRepository.findAll()) {
                     User u = req.getUser();
                     if (search != null && !search.isBlank() && !u.getFullName().toLowerCase().contains(search.toLowerCase())) {
                         continue;
                     }
                     if (dept != null && !dept.isBlank() && !getDepartment(u).equalsIgnoreCase(dept)) {
                         continue;
                     }
                     Map<String, Object> map = new HashMap<>();
                     map.put("id", "REIM-" + req.getId());
                     map.put("employee", u.getFullName());
                     map.put("employeeCode", u.getUsername());
                     map.put("department", getDepartment(u));
                     map.put("type", "REIMBURSEMENT");
                     map.put("purpose", req.getPurpose());
                     map.put("amount", req.getAmount());
                     map.put("status", req.getStatus());
                     map.put("assignedTo", req.getAssignedTo() != null ? req.getAssignedTo() : "-");
                     reimbursements.add(map);
                 }
                 model.addAttribute("reimbursementsList", reimbursements);
 
                 // --- TAB 3: BUDGET REQUESTS ---
                 List<Map<String, Object>> budgetRequests = new ArrayList<>();
                 for (BudgetRequest budget : budgetRequestRepository.findAll()) {
                     User u = budget.getRequester();
                     if (search != null && !search.isBlank() && !u.getFullName().toLowerCase().contains(search.toLowerCase())) {
                         continue;
                     }
                     if (dept != null && !dept.isBlank() && !budget.getDepartment().equalsIgnoreCase(dept)) {
                         continue;
                     }
                     Map<String, Object> map = new HashMap<>();
                     map.put("id", "BUD-" + budget.getId());
                     map.put("requester", u.getFullName());
                     map.put("employee", u.getFullName());
                     map.put("employeeCode", u.getUsername());
                     map.put("department", budget.getDepartment());
                     map.put("type", "BUDGET");
                     map.put("purpose", budget.getPurpose());
                     map.put("amount", budget.getAmount());
                     map.put("status", budget.getStatus());
                     map.put("assignedTo", budget.getAssignedTo() != null ? budget.getAssignedTo() : "-");
                     budgetRequests.add(map);
                 }
                 model.addAttribute("budgetRequestsList", budgetRequests);

                 // --- TAB 4: ASSIGN REQUESTS ---
                 List<Map<String, Object>> assignable = new ArrayList<>();
                 for (ExpenseClaim claim : expenseClaimRepository.findAll()) {
                     User u = claim.getUser();
                     Map<String, Object> map = new HashMap<>();
                     map.put("id", "EXP-" + claim.getId());
                     map.put("employee", u.getFullName());
                     map.put("employeeCode", u.getUsername());
                     map.put("department", getDepartment(u));
                     map.put("type", "EXPENSE");
                     map.put("purpose", claim.getPurpose());
                     map.put("amount", claim.getAmount());
                     map.put("status", claim.getStatus());
                     map.put("assignedTo", claim.getAssignedTo() != null ? claim.getAssignedTo() : "-");
                     assignable.add(map);
                 }
                 for (ReimbursementRequest req : reimbursementRequestRepository.findAll()) {
                     User u = req.getUser();
                     Map<String, Object> map = new HashMap<>();
                     map.put("id", "REIM-" + req.getId());
                     map.put("employee", u.getFullName());
                     map.put("employeeCode", u.getUsername());
                     map.put("department", getDepartment(u));
                     map.put("type", "REIMBURSEMENT");
                     map.put("purpose", req.getPurpose());
                     map.put("amount", req.getAmount());
                     map.put("status", req.getStatus());
                     map.put("assignedTo", req.getAssignedTo() != null ? req.getAssignedTo() : "-");
                     assignable.add(map);
                 }
                 for (BudgetRequest budget : budgetRequestRepository.findAll()) {
                     User u = budget.getRequester();
                     Map<String, Object> map = new HashMap<>();
                     map.put("id", "BUD-" + budget.getId());
                     map.put("employee", u.getFullName());
                     map.put("employeeCode", u.getUsername());
                     map.put("department", budget.getDepartment());
                     map.put("type", "BUDGET");
                     map.put("purpose", budget.getPurpose());
                     map.put("amount", budget.getAmount());
                     map.put("status", budget.getStatus());
                     map.put("assignedTo", budget.getAssignedTo() != null ? budget.getAssignedTo() : "-");
                     assignable.add(map);
                 }
                 model.addAttribute("assignableRequestsList", assignable);

                 List<User> activeEmployees = userRepository.findAll().stream()
                         .filter(u -> !"CLIENT".equalsIgnoreCase(u.getRole() != null ? u.getRole().getRoleName() : ""))
                         .filter(u -> "ACTIVE".equalsIgnoreCase(u.getStatus()))
                         .sorted(Comparator.comparing(User::getFullName))
                         .collect(java.util.stream.Collectors.toList());
                 model.addAttribute("allUsersList", activeEmployees);
 
                 // Reimbursements status counts
                 long reimApprovedCount = 0;
                 long reimPendingCount = 0;
                 long reimRejectedCount = 0;
                 for (ReimbursementRequest req : reimbursementRequestRepository.findAll()) {
                     if ("Approved".equalsIgnoreCase(req.getStatus())) reimApprovedCount++;
                     else if ("Pending".equalsIgnoreCase(req.getStatus())) reimPendingCount++;
                     else if ("Rejected".equalsIgnoreCase(req.getStatus())) reimRejectedCount++;
                 }
                 model.addAttribute("reimApprovedCount", reimApprovedCount);
                 model.addAttribute("reimPendingCount", reimPendingCount);
                 model.addAttribute("reimRejectedCount", reimRejectedCount);
 
                 // Budget requests status counts
                 long budgetApprovedCount = 0;
                 long budgetPendingCount = 0;
                 long budgetRejectedCount = 0;
                 for (BudgetRequest budget : budgetRequestRepository.findAll()) {
                     if ("Approved".equalsIgnoreCase(budget.getStatus())) budgetApprovedCount++;
                     else if ("Pending".equalsIgnoreCase(budget.getStatus())) budgetPendingCount++;
                     else if ("Rejected".equalsIgnoreCase(budget.getStatus())) budgetRejectedCount++;
                 }
                 model.addAttribute("budgetApprovedCount", budgetApprovedCount);
                model.addAttribute("budgetPendingCount", budgetPendingCount);
                model.addAttribute("budgetRejectedCount", budgetRejectedCount);

                long expTotalRequests = expenseClaimRepository.count();
                long expApproved = expenseClaimRepository.findAll().stream().filter(c -> "Approved".equalsIgnoreCase(c.getStatus())).count();
                long expPending = expenseClaimRepository.findAll().stream().filter(c -> "Pending".equalsIgnoreCase(c.getStatus())).count();
                long expRejected = expenseClaimRepository.findAll().stream().filter(c -> "Rejected".equalsIgnoreCase(c.getStatus())).count();
                double expTotalAmountD = expenseClaimRepository.findAll().stream().mapToDouble(c -> c.getAmount() != null ? c.getAmount() : 0.0).sum();
                String expTotalAmount = String.format("%,.0f", expTotalAmountD);

                model.addAttribute("expTotalRequests", expTotalRequests);
                model.addAttribute("expApproved", expApproved);
                model.addAttribute("expPending", expPending);
                model.addAttribute("expRejected", expRejected);
                model.addAttribute("expTotalAmount", expTotalAmount);

                long reimTotalRequests = reimbursementRequestRepository.count();
                double reimTotalAmountD = reimbursementRequestRepository.findAll().stream().mapToDouble(c -> c.getAmount() != null ? c.getAmount() : 0.0).sum();
                String reimTotalAmount = String.format("%,.0f", reimTotalAmountD);

                model.addAttribute("reimTotalRequests", reimTotalRequests);
                model.addAttribute("reimTotalAmount", reimTotalAmount);

                long budgetTotalRequests = budgetRequestRepository.count();
                double budgetTotalAmountD = budgetRequestRepository.findAll().stream().mapToDouble(c -> c.getAmount() != null ? c.getAmount() : 0.0).sum();
                String budgetTotalAmount = String.format("%,.0f", budgetTotalAmountD);

                model.addAttribute("budgetTotalRequests", budgetTotalRequests);
                model.addAttribute("budgetTotalAmount", budgetTotalAmount);

                double overallTotalAmount = expTotalAmountD + reimTotalAmountD + budgetTotalAmountD;
                model.addAttribute("overallTotalAmount", String.format("%,.0f", overallTotalAmount));
                
                double expPercent = overallTotalAmount > 0 ? (expTotalAmountD / overallTotalAmount) * 100 : 0.0;
                double reimPercent = overallTotalAmount > 0 ? (reimTotalAmountD / overallTotalAmount) * 100 : 0.0;
                double budgetPercent = overallTotalAmount > 0 ? (budgetTotalAmountD / overallTotalAmount) * 100 : 0.0;
                
                model.addAttribute("expPercent", String.format("%.2f", expPercent));
                model.addAttribute("reimPercent", String.format("%.2f", reimPercent));
                model.addAttribute("budgetPercent", String.format("%.2f", budgetPercent));


                return "senior_manager-expenses";
        }

        @PreAuthorize("hasRole('SENIOR_MANAGER')")
        @PostMapping("/senior_manager/expenses/action")
        public String handleExpenseAction(
                @RequestParam("requestId") String requestId,
                @RequestParam("action") String action,
                org.springframework.web.servlet.mvc.support.RedirectAttributes redirectAttributes) {
            
            String[] parts = requestId.split("-");
            String type = parts[0];
            Long id = Long.parseLong(parts[1]);
            String newStatus = "Pending";
            if ("Approve".equalsIgnoreCase(action)) newStatus = "Approved";
            else if ("Reject".equalsIgnoreCase(action)) newStatus = "Rejected";
            else if ("Forward".equalsIgnoreCase(action)) newStatus = "Forwarded to Accounts";
            
            if ("EXP".equalsIgnoreCase(type)) {
                java.util.Optional<ExpenseClaim> opt = expenseClaimRepository.findById(id);
                if (opt.isPresent()) {
                    ExpenseClaim c = opt.get();
                    c.setStatus(newStatus);
                    expenseClaimRepository.save(c);
                }
            } else if ("REIM".equalsIgnoreCase(type)) {
                java.util.Optional<ReimbursementRequest> opt = reimbursementRequestRepository.findById(id);
                if (opt.isPresent()) {
                    ReimbursementRequest r = opt.get();
                    r.setStatus(newStatus);
                    reimbursementRequestRepository.save(r);
                }
            } else if ("BUD".equalsIgnoreCase(type)) {
                java.util.Optional<BudgetRequest> opt = budgetRequestRepository.findById(id);
                if (opt.isPresent()) {
                    BudgetRequest b = opt.get();
                    b.setStatus(newStatus);
                    budgetRequestRepository.save(b);
                }
            }
            
            redirectAttributes.addFlashAttribute("successMessage", "Request status updated to '" + newStatus + "' successfully!");
            return "redirect:/senior_manager/expenses";
        }

        @PreAuthorize("hasRole('SENIOR_MANAGER')")
        @PostMapping("/senior_manager/expenses/edit")
        public String handleExpenseEdit(
                @RequestParam("requestId") String requestId,
                @RequestParam("purpose") String purpose,
                @RequestParam("amount") Double amount,
                org.springframework.web.servlet.mvc.support.RedirectAttributes redirectAttributes) {
            
            String[] parts = requestId.split("-");
            String type = parts[0];
            Long id = Long.parseLong(parts[1]);
            
            if ("EXP".equalsIgnoreCase(type)) {
                expenseClaimRepository.findById(id).ifPresent(c -> {
                    c.setPurpose(purpose);
                    c.setAmount(amount);
                    expenseClaimRepository.save(c);
                });
            } else if ("REIM".equalsIgnoreCase(type)) {
                reimbursementRequestRepository.findById(id).ifPresent(r -> {
                    r.setPurpose(purpose);
                    r.setAmount(amount);
                    reimbursementRequestRepository.save(r);
                });
            } else if ("BUD".equalsIgnoreCase(type)) {
                budgetRequestRepository.findById(id).ifPresent(b -> {
                    b.setPurpose(purpose);
                    b.setAmount(amount);
                    budgetRequestRepository.save(b);
                });
            }
            
            redirectAttributes.addFlashAttribute("successMessage", "Request details updated successfully!");
            return "redirect:/senior_manager/expenses";
        }

        @PreAuthorize("hasRole('SENIOR_MANAGER')")
        @PostMapping("/senior_manager/expenses/assign")
        public String handleExpenseAssign(
                @RequestParam("requestId") String requestId,
                @RequestParam("assignee") String assignee,
                org.springframework.web.servlet.mvc.support.RedirectAttributes redirectAttributes) {
            
            String[] parts = requestId.split("-");
            String type = parts[0];
            Long id = Long.parseLong(parts[1]);
            
            if ("EXP".equalsIgnoreCase(type)) {
                expenseClaimRepository.findById(id).ifPresent(c -> {
                    c.setAssignedTo(assignee);
                    expenseClaimRepository.save(c);
                });
            } else if ("REIM".equalsIgnoreCase(type)) {
                reimbursementRequestRepository.findById(id).ifPresent(r -> {
                    r.setAssignedTo(assignee);
                    reimbursementRequestRepository.save(r);
                });
            } else if ("BUD".equalsIgnoreCase(type)) {
                budgetRequestRepository.findById(id).ifPresent(b -> {
                    b.setAssignedTo(assignee);
                    budgetRequestRepository.save(b);
                });
            }
            
            redirectAttributes.addFlashAttribute("successMessage", "Request assigned to '" + assignee + "' successfully!");
            return "redirect:/senior_manager/expenses";
        }

        @PreAuthorize("hasRole('SENIOR_MANAGER')")
        @PostMapping("/senior_manager/performance/feedback")
        public String submitPerformanceFeedback(
                @RequestParam("employeeCode") String employeeCode,
                @RequestParam("roleLevel") String roleLevel,
                @RequestParam("rating1") Double rating1,
                @RequestParam("rating2") Double rating2,
                @RequestParam("rating3") Double rating3,
                @RequestParam("comments") String comments,
                Principal principal,
                org.springframework.web.servlet.mvc.support.RedirectAttributes redirectAttributes) {

            User reviewer = principal != null ? userRepository.findByUsername(principal.getName()).orElse(null) : null;
            User employee = userRepository.findByUsername(employeeCode).orElse(null);

            if (employee != null && reviewer != null) {
                EmployeeFeedback feedback = new EmployeeFeedback();
                feedback.setEmployee(employee);
                feedback.setReviewer(reviewer);
                feedback.setRoleLevel(roleLevel);
                feedback.setRating1(rating1);
                feedback.setRating2(rating2);
                feedback.setRating3(rating3);
                feedback.setComments(comments);
                feedback.setSubmittedAt(LocalDate.now());
                employeeFeedbackRepository.save(feedback);

                // Update performance review status if present
                performanceReviewRepository.findAll().stream()
                        .filter(pr -> pr.getEmployee() != null && pr.getEmployee().getUsername().equals(employeeCode))
                        .findFirst()
                        .ifPresent(pr -> {
                            pr.setStatus("Completed");
                            pr.setFinalRating(Math.round(((rating1 + rating2 + rating3) / 3.0) * 100.0) / 100.0);
                            pr.setReviewDate(LocalDate.now());
                            performanceReviewRepository.save(pr);
                        });
            }

            redirectAttributes.addFlashAttribute("successMessage", "Feedback for " + (employee != null ? employee.getFullName() : employeeCode) + " submitted successfully!");
            return "redirect:/senior_manager/performance";
        }

        @PreAuthorize("hasRole('SENIOR_MANAGER')")
        @PostMapping("/senior_manager/performance/initiate_feedback")
        @org.springframework.transaction.annotation.Transactional
        public String initiatePerformanceFeedback(
                @RequestParam(value = "teamMembers", required = false) List<String> teamMembers,
                @RequestParam(value = "teamId", required = false) Long teamId,
                @RequestParam("roleLevel") String roleLevel,
                @RequestParam(value = "fieldName", required = false) List<String> fieldNames,
                @RequestParam(value = "fieldDesc", required = false) List<String> fieldDescs,
                Principal principal,
                org.springframework.web.servlet.mvc.support.RedirectAttributes redirectAttributes) {

            User manager = principal != null ? userRepository.findByUsername(principal.getName()).orElse(null) : null;
            if (manager == null) {
                return "redirect:/login";
            }

            java.util.Set<User> targets = new java.util.HashSet<>();

            // 1. Resolve team if selected
            if (teamId != null) {
                com.example.admindashboard.model.Team team = teamRepository.findById(teamId).orElse(null);
                if (team != null) {
                    for (com.example.admindashboard.model.TeamMember tm : team.getMembers()) {
                        targets.add(tm.getUser());
                    }
                }
            }

            // 2. Resolve individual employees
            if (teamMembers != null) {
                for (String username : teamMembers) {
                    User u = userRepository.findByUsername(username).orElse(null);
                    if (u != null) {
                        targets.add(u);
                    }
                }
            }

            if (targets.isEmpty()) {
                redirectAttributes.addFlashAttribute("errorMessage", "No employees selected to initiate feedback.");
                return "redirect:/senior_manager/performance?tab=team_reviews";
            }

            int initiatedCount = 0;
            int skippedCount = 0;
            for (User employee : targets) {
                // Enforce one active feedback request at a time per user
                boolean hasActive = performanceFeedbackRequestRepository.findByEmployee(employee).stream()
                    .anyMatch(r -> "PENDING".equalsIgnoreCase(r.getStatus()) || "DRAFT".equalsIgnoreCase(r.getStatus()));
                if (hasActive) {
                    skippedCount++;
                    continue;
                }

                // Save Request
                com.example.admindashboard.model.PerformanceFeedbackRequest req = new com.example.admindashboard.model.PerformanceFeedbackRequest();
                req.setEmployee(employee);
                req.setManager(manager);
                req.setRoleLevel(roleLevel);
                req.setStatus("PENDING");
                req = performanceFeedbackRequestRepository.save(req);

                // Auto-generate serial number
                req.setSerialNumber("FB-" + (1000 + req.getId()));
                performanceFeedbackRequestRepository.save(req);
                initiatedCount++;

                // Save Dynamic Fields
                if (fieldNames != null && fieldDescs != null) {
                    for (int i = 0; i < fieldNames.size(); i++) {
                        if (i < fieldDescs.size() && !fieldNames.get(i).isBlank()) {
                            com.example.admindashboard.model.FeedbackRequestField f = new com.example.admindashboard.model.FeedbackRequestField();
                            f.setFeedbackRequest(req);
                            f.setFieldName(fieldNames.get(i));
                            f.setDescription(fieldDescs.get(i));
                            feedbackRequestFieldRepository.save(f);
                        }
                    }
                }

                // Add Notification
                HrmsNotification notif = new HrmsNotification(
                    employee,
                    "Performance Feedback Required",
                    "Your manager " + manager.getFullName() + " has requested performance feedback. Please fill out the form.",
                    "NOTIFICATION",
                    "High",
                    manager.getFullName(),
                    manager.getDesignation()
                );
                hrmsNotificationRepository.save(notif);
                
                // Create/Update PerformanceReview status to In Progress
                PerformanceReview review = performanceReviewRepository.findAll().stream()
                    .filter(pr -> pr.getEmployee() != null && pr.getEmployee().getId().equals(employee.getId()))
                    .findFirst()
                    .orElse(null);
                
                if (review == null) {
                    review = new PerformanceReview();
                    review.setEmployee(employee);
                    review.setDepartment(getDepartment(employee));
                    review.setDesignation(employee.getDesignation());
                    review.setReviewType("Annual Review");
                    review.setReviewPeriod("FY 2025-26");
                    review.setDueDate(LocalDate.now().plusWeeks(2));
                }
                review.setStatus("In Progress");
                performanceReviewRepository.save(review);
            }

            if (initiatedCount == 0 && skippedCount > 0) {
                redirectAttributes.addFlashAttribute("errorMessage", "All selected employees already have an active/pending feedback request.");
            } else {
                String msg = "Performance feedback request initiated successfully for " + initiatedCount + " employees.";
                if (skippedCount > 0) {
                    msg += " (" + skippedCount + " skipped due to existing active requests)";
                }
                redirectAttributes.addFlashAttribute("successMessage", msg);
            }
            return "redirect:/senior_manager/performance?tab=team_reviews";
        }

        @PreAuthorize("hasRole('SENIOR_MANAGER')")
        @PostMapping("/senior_manager/performance/edit_feedback")
        @org.springframework.transaction.annotation.Transactional
        public String editPerformanceFeedback(
                @RequestParam("requestId") Long requestId,
                @RequestParam("rating") Double rating,
                @RequestParam(value = "comments", required = false) String comments,
                @RequestParam(value = "fieldId", required = false) List<Long> fieldIds,
                @RequestParam(value = "fieldValue", required = false) List<String> fieldValues,
                @RequestParam(value = "fieldManagerRating", required = false) List<Double> fieldManagerRatings,
                Principal principal,
                org.springframework.web.servlet.mvc.support.RedirectAttributes redirectAttributes) {

            User manager = principal != null ? userRepository.findByUsername(principal.getName()).orElse(null) : null;
            if (manager == null) {
                return "redirect:/login";
            }

            com.example.admindashboard.model.PerformanceFeedbackRequest req = performanceFeedbackRequestRepository.findById(requestId).orElse(null);
            if (req == null) {
                redirectAttributes.addFlashAttribute("errorMessage", "Feedback request not found.");
                return "redirect:/senior_manager/performance?tab=feedback_requests";
            }

            req.setManagerRating(rating);
            req.setManagerComments(comments != null ? comments : "");
            req.setStatus("FORWARDED_TO_HR");
            req.setSubmittedAt(LocalDate.now());
            performanceFeedbackRequestRepository.save(req);

            // Update dynamic fields & save manager final ratings per criteria
            if (fieldIds != null) {
                for (int i = 0; i < fieldIds.size(); i++) {
                    com.example.admindashboard.model.FeedbackRequestField f = feedbackRequestFieldRepository.findById(fieldIds.get(i)).orElse(null);
                    if (f != null && f.getFeedbackRequest().getId().equals(requestId)) {
                        if (fieldValues != null && i < fieldValues.size()) {
                            f.setFieldValue(fieldValues.get(i));
                        }
                        if (fieldManagerRatings != null && i < fieldManagerRatings.size()) {
                            f.setManagerRating(fieldManagerRatings.get(i));
                        }
                        feedbackRequestFieldRepository.save(f);
                    }
                }
            }

            // Sync with PerformanceReview
            User employee = req.getEmployee();
            PerformanceReview review = performanceReviewRepository.findAll().stream()
                .filter(pr -> pr.getEmployee() != null && pr.getEmployee().getId().equals(employee.getId()))
                .findFirst()
                .orElse(null);
            
            if (review != null) {
                review.setFinalRating(rating);
                review.setManagerRating(rating);
                performanceReviewRepository.save(review);
            }

            redirectAttributes.addFlashAttribute("successMessage", "Feedback for " + employee.getFullName() + " updated successfully.");
            return "redirect:/senior_manager/performance?tab=feedback_requests";
        }


        @PreAuthorize("hasRole('SENIOR_MANAGER')")
        @GetMapping("/senior_manager/reports")
        public String showReports(
                @RequestParam(value = "tab", defaultValue = "performance") String tab,
                @RequestParam(value = "dept", required = false) String dept,
                @RequestParam(value = "search", required = false) String search,
                Model model,
                Principal principal) {

                User loggedInUser = principal != null
                        ? userRepository.findByUsername(principal.getName()).orElse(null)
                        : null;

                if (loggedInUser == null) {
                    return "redirect:/login";
                }

                model.addAttribute("loggedInUser", loggedInUser);
                model.addAttribute("activeTab", tab);
                model.addAttribute("selectedDept", dept);
                model.addAttribute("searchQuery", search);

                seedEmployeeGoals();

                List<User> allUsers = userRepository.findAll();
                Set<String> departments = new java.util.TreeSet<>();
                departments.addAll(List.of("IT - Development", "IT - Design", "IT - Quality", "IT - Operations", "Business"));
                for (User u : allUsers) {
                    String d = getDepartment(u);
                    if (!d.isBlank() && !"Unknown".equalsIgnoreCase(d)) {
                        departments.add(d);
                    }
                }
                model.addAttribute("departments", departments);

                List<User> employees = allUsers.stream()
                        .filter(u -> u.getId() != null && !u.getId().equals(loggedInUser.getId()))
                        .filter(u -> u.getRole() == null || !"CLIENT".equalsIgnoreCase(u.getRole().getRoleName()))
                        .collect(java.util.stream.Collectors.toList());

                // --- TAB 1: TEAM PERFORMANCE ---
                List<Map<String, Object>> performanceList = new ArrayList<>();
                List<PerformanceReview> dbReviews = performanceReviewRepository.findAll();
                for (PerformanceReview pr : dbReviews) {
                    User u = pr.getEmployee();
                    if (u == null) continue;
                    
                    if (search != null && !search.isBlank() && !u.getFullName().toLowerCase().contains(search.toLowerCase())) {
                        continue;
                    }
                    if (dept != null && !dept.isBlank() && !getDepartment(u).equalsIgnoreCase(dept)) {
                        continue;
                    }

                    List<Goal> goals = goalRepository.findByUser(u);
                    long assigned = goals.size();
                    long achieved = goals.stream().filter(g -> "Completed".equalsIgnoreCase(g.getStatus()) || "Achieved".equalsIgnoreCase(g.getStatus())).count();
                    String achievedPctStr = assigned > 0 ? achieved + " (" + (achieved * 100 / assigned) + "%)" : "0 (0%)";

                    Map<String, Object> map = new HashMap<>();
                    map.put("name", u.getFullName());
                    map.put("lead", u.getManager() != null ? u.getManager().getFullName() : "N/A");
                    map.put("role", pr.getDesignation());
                    map.put("goalsAssigned", assigned);
                    map.put("goalsAchieved", achievedPctStr);
                    map.put("rating", pr.getFinalRating() != null ? pr.getFinalRating() : 0.0);
                    map.put("ratingInt", pr.getFinalRating() != null ? pr.getFinalRating().intValue() : 0);
                    map.put("reviewDate", pr.getReviewDate() != null ? pr.getReviewDate().format(DateTimeFormatter.ofPattern("dd MMM yyyy")) : "-");
                    performanceList.add(map);
                }
                model.addAttribute("performanceList", performanceList);

                // --- TAB 2: ATTENDANCE ---
                List<Map<String, Object>> attendanceList = new ArrayList<>();
                List<Attendance> allAttendance = attendanceRepository.findAll();

                for (User u : employees) {
                    if (search != null && !search.isBlank() && !u.getFullName().toLowerCase().contains(search.toLowerCase())) {
                        continue;
                    }
                    if (dept != null && !dept.isBlank() && !getDepartment(u).equalsIgnoreCase(dept)) {
                        continue;
                    }

                    List<Attendance> userAtt = allAttendance.stream()
                            .filter(a -> a.getUser() != null && a.getUser().getId().equals(u.getId()))
                            .collect(java.util.stream.Collectors.toList());

                    long days = userAtt.size();
                    long present = userAtt.stream().filter(a -> "Present".equalsIgnoreCase(a.getStatus())).count();
                    long absent = userAtt.stream().filter(a -> "Absent".equalsIgnoreCase(a.getStatus())).count();
                    long leave = userAtt.stream().filter(a -> "Leave".equalsIgnoreCase(a.getStatus())).count();
                    long half = userAtt.stream().filter(a -> "Half-Day".equalsIgnoreCase(a.getStatus()) || "Half Day".equalsIgnoreCase(a.getStatus())).count();
                    long late = userAtt.stream()
                            .filter(a -> a.getCheckInTime() != null && a.getCheckInTime().isAfter(java.time.LocalTime.of(9, 30)))
                            .count();

                    String presentPct = days > 0 ? present + " (" + (present * 100 / days) + "%)" : "0 (0%)";
                    String absentPct = days > 0 ? absent + " (" + (absent * 100 / days) + "%)" : "0 (0%)";
                    String latePct = days > 0 ? late + " (" + (late * 100 / days) + "%)" : "0 (0%)";
                    String halfPct = days > 0 ? half + " (" + (half * 100 / days) + "%)" : "0 (0%)";
                    String leavePct = days > 0 ? leave + " (" + (leave * 100 / days) + "%)" : "0 (0%)";

                    double rate = days > 0 ? (present * 100.0) / days : 0.0;
                    String statusStr = rate >= 90 ? "EXCELLENT" : (rate >= 80 ? "GOOD" : "AVERAGE");

                    Map<String, Object> map = new HashMap<>();
                    map.put("name", u.getFullName());
                    map.put("empCode", u.getUsername());
                    map.put("department", getDepartment(u));
                    map.put("workingDays", days);
                    map.put("presentPct", presentPct);
                    map.put("absentPct", absentPct);
                    map.put("latePct", latePct);
                    map.put("halfPct", halfPct);
                    map.put("leavePct", leavePct);
                    map.put("totalPct", String.format("%.1f%%", rate));
                    map.put("status", statusStr);
                    attendanceList.add(map);
                }
                model.addAttribute("attendanceList", attendanceList);

                // --- TAB 3: LEAVE TRENDS ---
                List<Map<String, Object>> leaveTrendsList = new ArrayList<>();
                List<LeaveRequest> allLeaves = leaveRequestRepository.findAll();

                for (User u : employees) {
                    if (search != null && !search.isBlank() && !u.getFullName().toLowerCase().contains(search.toLowerCase())) {
                        continue;
                    }
                    if (dept != null && !dept.isBlank() && !getDepartment(u).equalsIgnoreCase(dept)) {
                        continue;
                    }

                    List<LeaveRequest> userLeaves = allLeaves.stream()
                            .filter(r -> r.getUser() != null && r.getUser().getId().equals(u.getId()))
                            .collect(java.util.stream.Collectors.toList());

                    long totalLeaves = userLeaves.size();
                    long approved = userLeaves.stream().filter(r -> "Approved".equalsIgnoreCase(r.getStatus())).count();
                    long pending = userLeaves.stream().filter(r -> "Pending".equalsIgnoreCase(r.getStatus())).count();
                    long rejected = userLeaves.stream().filter(r -> "Rejected".equalsIgnoreCase(r.getStatus())).count();

                    String rate = totalLeaves > 0 ? String.format("%.1f%%", (approved * 100.0) / totalLeaves) : "0.0%";
                    String primaryType = userLeaves.isEmpty() ? "-" : userLeaves.get(0).getLeaveType();

                    Map<String, Object> map = new HashMap<>();
                    map.put("name", u.getFullName());
                    map.put("empCode", u.getUsername());
                    map.put("department", getDepartment(u));
                    map.put("type", primaryType);
                    map.put("leavesTaken", totalLeaves);
                    map.put("approved", approved);
                    map.put("pending", pending);
                    map.put("rejected", rejected);
                    map.put("rate", rate);
                    leaveTrendsList.add(map);
                }
                model.addAttribute("leaveTrendsList", leaveTrendsList);

                // --- TAB 4: PROJECT METRICS ---
                List<Map<String, Object>> projectMetricsList = new ArrayList<>();
                List<Project> allProjects = projectRepository.findAll();
                List<Ticket> allTickets = ticketRepository.findAll();

                for (Project p : allProjects) {
                    if (search != null && !search.isBlank() && !p.getProjectName().toLowerCase().contains(search.toLowerCase())) {
                        continue;
                    }

                    List<Ticket> projTickets = allTickets.stream()
                            .filter(t -> t.getProject() != null && t.getProject().getId().equals(p.getId()))
                            .collect(java.util.stream.Collectors.toList());

                    long teamCount = projTickets.stream()
                            .map(Ticket::getAssignedTo)
                            .filter(java.util.Objects::nonNull)
                            .distinct()
                            .count();

                    long totalTasks = projTickets.size();
                    long completedTasks = projTickets.stream().filter(t -> "Completed".equalsIgnoreCase(t.getStatus())).count();

                    double avgProgress = 0.0;
                    if (totalTasks > 0) {
                        avgProgress = projTickets.stream().mapToDouble(t -> t.getProgressPercentage() != null ? t.getProgressPercentage() : 0.0).average().orElse(0.0);
                    }

                    String pManager = "N/A";
                    if (!projTickets.isEmpty()) {
                        User firstUser = projTickets.stream().map(Ticket::getAssignedTo).filter(java.util.Objects::nonNull).findFirst().orElse(null);
                        if (firstUser != null && firstUser.getManager() != null) {
                            pManager = firstUser.getManager().getFullName();
                        }
                    }

                    long overdue = projTickets.stream()
                            .filter(t -> !"Completed".equalsIgnoreCase(t.getStatus()))
                            .filter(t -> t.getDeadline() != null && t.getDeadline().isBefore(LocalDate.now()))
                            .count();

                    String statusStr = overdue > 0 ? "At Risk" : "On Track";

                    Map<String, Object> map = new HashMap<>();
                    map.put("name", p.getProjectName());
                    map.put("code", p.getProjectName().replaceAll("[^a-zA-Z0-9]", "").toUpperCase());
                    if (String.valueOf(map.get("code")).length() > 4) {
                        map.put("code", String.valueOf(map.get("code")).substring(0, 4));
                    }
                    
                    String pDept = "IT Department";
                    if (!projTickets.isEmpty()) {
                        User firstUser = projTickets.stream().map(Ticket::getAssignedTo).filter(java.util.Objects::nonNull).findFirst().orElse(null);
                        if (firstUser != null) {
                            pDept = getDepartment(firstUser);
                        }
                    }
                    map.put("department", pDept);
                    map.put("manager", pManager);
                    map.put("start", p.getCreatedAt() != null ? p.getCreatedAt().format(DateTimeFormatter.ofPattern("dd MMM yyyy")) : "-");
                    map.put("end", p.getCreatedAt() != null ? p.getCreatedAt().plusMonths(4).format(DateTimeFormatter.ofPattern("dd MMM yyyy")) : "-");
                    map.put("progress", (int) avgProgress);
                    map.put("status", statusStr);
                    map.put("teamCount", teamCount);
                    map.put("tasks", completedTasks + " / " + totalTasks);
                    projectMetricsList.add(map);
                }
                model.addAttribute("projectMetricsList", projectMetricsList);

                // Leave types counts for reports
                long reportCasualCount = 0;
                long reportSickCount = 0;
                long reportPrivilegeCount = 0;
                long reportWfhCount = 0;
                long reportOtherCount = 0;
                for (LeaveRequest r : allLeaves) {
                    if (r.getUser() == null || !employees.stream().anyMatch(e -> e.getId().equals(r.getUser().getId()))) continue;
                    String lt = r.getLeaveType() != null ? r.getLeaveType().toLowerCase() : "";
                    if (lt.contains("casual")) reportCasualCount++;
                    else if (lt.contains("sick")) reportSickCount++;
                    else if (lt.contains("earned") || lt.contains("privilege")) reportPrivilegeCount++;
                    else if (lt.contains("work") || lt.contains("wfh")) reportWfhCount++;
                    else reportOtherCount++;
                }
                model.addAttribute("reportCasualCount", reportCasualCount);
                model.addAttribute("reportSickCount", reportSickCount);
                model.addAttribute("reportPrivilegeCount", reportPrivilegeCount);
                model.addAttribute("reportWfhCount", reportWfhCount);
                model.addAttribute("reportOtherCount", reportOtherCount);

                // Project statuses for reports
                long reportOnTrackCount = 0;
                long reportAtRiskCount = 0;
                long reportDelayedCount = 0;
                for (Project p : allProjects) {
                    List<Ticket> projTickets = allTickets.stream()
                            .filter(t -> t.getProject() != null && t.getProject().getId().equals(p.getId()))
                            .collect(java.util.stream.Collectors.toList());
                    long overdue = projTickets.stream()
                            .filter(t -> !"Completed".equalsIgnoreCase(t.getStatus()))
                            .filter(t -> t.getDeadline() != null && t.getDeadline().isBefore(LocalDate.now()))
                            .count();
                    if (overdue > 0) {
                        reportAtRiskCount++;
                    } else {
                        reportOnTrackCount++;
                    }
                }
                model.addAttribute("reportOnTrackCount", reportOnTrackCount);
                model.addAttribute("reportAtRiskCount", reportAtRiskCount);
                model.addAttribute("reportDelayedCount", reportDelayedCount);

                // Projects progress distribution
                long progressBucket1 = 0; // 0-25%
                long progressBucket2 = 0; // 26-50%
                long progressBucket3 = 0; // 51-75%
                long progressBucket4 = 0; // 76-100%
                for (Project p : allProjects) {
                    List<Ticket> projTickets = allTickets.stream()
                            .filter(t -> t.getProject() != null && t.getProject().getId().equals(p.getId()))
                            .collect(java.util.stream.Collectors.toList());
                    double avgProgress = 0.0;
                    if (!projTickets.isEmpty()) {
                        avgProgress = projTickets.stream().mapToDouble(t -> t.getProgressPercentage() != null ? t.getProgressPercentage() : 0.0).average().orElse(0.0);
                    }
                    if (avgProgress <= 25) progressBucket1++;
                    else if (avgProgress <= 50) progressBucket2++;
                    else if (avgProgress <= 75) progressBucket3++;
                    else progressBucket4++;
                }
                model.addAttribute("progressBucket1", progressBucket1);
                model.addAttribute("progressBucket2", progressBucket2);
                model.addAttribute("progressBucket3", progressBucket3);
                model.addAttribute("progressBucket4", progressBucket4);

                // --- COMPUTING DYNAMIC REPORTS STATS ---
                long totalTeamMembers = employees.size();
                long highPerformers = dbReviews.stream().filter(r -> r.getFinalRating() != null && r.getFinalRating() >= 4.0).count();
                double avgPerformance = dbReviews.stream().mapToDouble(r -> r.getFinalRating() != null ? r.getFinalRating() : 0.0).average().orElse(0.0);
                
                Double goalAvg = employeeGoalRepository.getAverageCompletionPercentage();
                String goalsAchieved = (goalAvg != null ? String.format("%.0f", goalAvg) : "0") + "%";
                long repExported1 = 12; // Static placeholder until export tracking exists

                model.addAttribute("repTotalTeamMembers", totalTeamMembers);
                model.addAttribute("repHighPerformers", highPerformers);
                model.addAttribute("repAvgPerformance", String.format("%.1f / 5", avgPerformance));
                model.addAttribute("repGoalsAchieved", goalsAchieved);
                model.addAttribute("repExported1", repExported1);

                long presentDays = allAttendance.stream().filter(a -> "Present".equalsIgnoreCase(a.getStatus())).count();
                long absentDays = allAttendance.stream().filter(a -> "Absent".equalsIgnoreCase(a.getStatus())).count();
                long lateDays = allAttendance.stream().filter(a -> a.getCheckInTime() != null && a.getCheckInTime().isAfter(java.time.LocalTime.of(9, 30))).count();
                long totalAttDays = presentDays + absentDays;
                String avgAttendance = totalAttDays > 0 ? String.format("%.1f%%", (presentDays * 100.0) / totalAttDays) : "0.0%";
                long repExported2 = 8;

                model.addAttribute("repAvgAttendance", avgAttendance);
                model.addAttribute("repPresentDays", presentDays);
                model.addAttribute("repAbsentDays", absentDays);
                model.addAttribute("repLateDays", lateDays);
                model.addAttribute("repExported2", repExported2);

                long totalLeavesTaken = allLeaves.size();
                long leaveApproved = allLeaves.stream().filter(l -> "Approved".equalsIgnoreCase(l.getStatus())).count();
                long leaveRejected = allLeaves.stream().filter(l -> "Rejected".equalsIgnoreCase(l.getStatus())).count();
                long leavePending = allLeaves.stream().filter(l -> "Pending".equalsIgnoreCase(l.getStatus())).count();
                long repExported3 = 7;

                model.addAttribute("repTotalLeavesTaken", totalLeavesTaken);
                model.addAttribute("repLeaveApproved", leaveApproved);
                model.addAttribute("repLeaveRejected", leaveRejected);
                model.addAttribute("repLeavePending", leavePending);
                model.addAttribute("repExported3", repExported3);

                long totalProjects = allProjects.size();
                long projCompleted = allProjects.stream().filter(p -> {
                    List<Ticket> t = allTickets.stream().filter(tk -> tk.getProject() != null && tk.getProject().getId().equals(p.getId())).collect(java.util.stream.Collectors.toList());
                    return !t.isEmpty() && t.stream().allMatch(tk -> "Completed".equalsIgnoreCase(tk.getStatus()));
                }).count();

                model.addAttribute("repTotalProjects", totalProjects);
                model.addAttribute("repProjCompleted", projCompleted);

                return "senior_manager-reports";
        }

        @PreAuthorize("hasRole('SENIOR_MANAGER')")
        @GetMapping("/senior_manager/communication")
        public String showCommunication(
                @RequestParam(value = "tab", defaultValue = "send") String tab,
                Model model,
                Principal principal) {

                User loggedInUser = principal != null
                        ? userRepository.findByUsername(principal.getName()).orElse(null)
                        : null;

                if (loggedInUser == null) {
                    return "redirect:/login";
                }

                model.addAttribute("loggedInUser", loggedInUser);
                model.addAttribute("activeTab", tab);

                List<Project> projects = projectRepository.findAll();
                model.addAttribute("projectsList", projects);

                List<User> activeUsers = userRepository.findAll().stream()
                        .filter(u -> !"CLIENT".equalsIgnoreCase(u.getRole() != null ? u.getRole().getRoleName() : ""))
                        .filter(u -> "ACTIVE".equalsIgnoreCase(u.getStatus()))
                        .sorted(java.util.Comparator.comparing(User::getFullName))
                        .collect(java.util.stream.Collectors.toList());
                model.addAttribute("activeUsers", activeUsers);

                seedCommunicationData();

                // --- TAB 1: RECENT COMMUNICATIONS ---
                List<Map<String, Object>> recentCommunications = new ArrayList<>();
                for (CommunicationBroadcast cb : communicationBroadcastRepository.findAll()) {
                    Map<String, Object> map = new HashMap<>();
                    map.put("subject", cb.getSubject());
                    map.put("sentTo", cb.getSentTo());
                    map.put("type", cb.getType());
                    map.put("priority", cb.getPriority());
                    map.put("sentOn", cb.getSentOn());
                    map.put("recipients", cb.getRecipients());
                    map.put("openRate", cb.getOpenRate());
                    map.put("status", cb.getStatus());
                    recentCommunications.add(map);
                }
                model.addAttribute("recentCommunications", recentCommunications);

                long messagesSent = recentCommunications.size();
                long totalRecipients = recentCommunications.stream().mapToLong(m -> (Integer) m.get("recipients")).sum();
                double openRateSum = recentCommunications.stream().mapToLong(m -> (Integer) m.get("openRate")).sum();
                long openRate = messagesSent > 0 ? (long) (openRateSum / messagesSent) : 0;
                long acknowledged = (long) (totalRecipients * (openRate / 100.0) * 0.8); // dummy approximation for acknowledged based on open rate
                long pending = totalRecipients - acknowledged;

                model.addAttribute("messagesSent", messagesSent);
                model.addAttribute("totalRecipients", totalRecipients);
                model.addAttribute("openRate", openRate);
                model.addAttribute("acknowledged", acknowledged);
                model.addAttribute("pending", pending);

                // --- TAB 2: RECENT PROJECT UPDATES ---
                List<Map<String, Object>> recentProjectUpdates = new ArrayList<>();
                for (ProjectUpdateNotification pun : projectUpdateNotificationRepository.findAll()) {
                    Map<String, Object> map = new HashMap<>();
                    map.put("subject", pun.getSubject());
                    map.put("project", pun.getProject());
                    map.put("sharedWith", pun.getSharedWith());
                    map.put("sharedOn", pun.getSharedOn());
                    map.put("recipients", pun.getRecipients());
                    map.put("views", pun.getViews());
                    map.put("acknowledged", pun.getAcknowledged());
                    map.put("status", pun.getStatus());
                    recentProjectUpdates.add(map);
                }
                model.addAttribute("recentProjectUpdates", recentProjectUpdates);

                // --- TAB 3: RECENT NOTIFICATIONS ---
                List<Map<String, Object>> recentNotifications = new ArrayList<>();
                for (RecentNotificationRecord rnr : recentNotificationRecordRepository.findAll()) {
                    Map<String, Object> map = new HashMap<>();
                    map.put("subject", rnr.getSubject());
                    map.put("notifyTo", rnr.getNotifyTo());
                    map.put("type", rnr.getType());
                    map.put("priority", rnr.getPriority());
                    map.put("sentOn", rnr.getSentOn());
                    map.put("recipients", rnr.getRecipients());
                    map.put("status", rnr.getStatus());
                    recentNotifications.add(map);
                }
                model.addAttribute("recentNotifications", recentNotifications);

                long notificationsSent = recentNotifications.size();
                long notifTotalRecipients = recentNotifications.stream().mapToLong(m -> (Integer) m.get("recipients")).sum();
                long upcomingDeadlines = recentNotifications.stream().filter(m -> "Reminder".equals(m.get("type"))).count();
                long changesAnnounced = recentNotifications.stream().filter(m -> "Policy Change".equals(m.get("type"))).count();
                long notifPending = notifTotalRecipients / 3;

                model.addAttribute("notificationsSent", notificationsSent);
                model.addAttribute("notifTotalRecipients", notifTotalRecipients);
                model.addAttribute("upcomingDeadlines", upcomingDeadlines);
                model.addAttribute("changesAnnounced", changesAnnounced);
                model.addAttribute("notifPending", notifPending);

                return "senior_manager-communication";
        }

        private List<User> findUsersInGroup(String group, User loggedInUser) {
            List<User> allUsers = userRepository.findAll().stream()
                    .filter(u -> !"CLIENT".equalsIgnoreCase(u.getRole() != null ? u.getRole().getRoleName() : ""))
                    .filter(u -> "ACTIVE".equalsIgnoreCase(u.getStatus()))
                    .collect(java.util.stream.Collectors.toList());

            if (group == null || group.equalsIgnoreCase("All") || group.equalsIgnoreCase("All Employees")) {
                return allUsers;
            }

            if (group.equalsIgnoreCase("My Team")) {
                if (loggedInUser != null) {
                    List<User> directReports = allUsers.stream()
                            .filter(u -> u.getManager() != null && u.getManager().getId().equals(loggedInUser.getId()))
                            .collect(java.util.stream.Collectors.toList());
                    if (!directReports.isEmpty()) {
                        return directReports;
                    }
                    String dept = getDepartment(loggedInUser);
                    if (!dept.isBlank()) {
                        return allUsers.stream()
                                .filter(u -> getDepartment(u).equalsIgnoreCase(dept) && !u.getId().equals(loggedInUser.getId()))
                                .collect(java.util.stream.Collectors.toList());
                    }
                }
                return new ArrayList<>();
            }
            
            List<User> filtered = new ArrayList<>();
            for (User u : allUsers) {
                EmployeeProfile ep = u.getEmployeeProfile();
                if (ep != null && ep.getDepartment() != null) {
                    String dept = ep.getDepartment().toLowerCase();
                    String target = group.toLowerCase();
                    if (dept.contains(target) || target.contains(dept)) {
                        filtered.add(u);
                    } else if (target.equals("dev") && (dept.contains("dev") || dept.contains("software") || dept.contains("program"))) {
                        filtered.add(u);
                    } else if (target.equals("qa") && (dept.contains("qa") || dept.contains("quality") || dept.contains("test"))) {
                        filtered.add(u);
                    } else if (target.equals("ops") && (dept.contains("ops") || dept.contains("operations") || dept.contains("support"))) {
                        filtered.add(u);
                    }
                }
            }
            return filtered;
        }

        @PreAuthorize("hasRole('SENIOR_MANAGER')")
        @PostMapping("/senior_manager/communication/send")
        public String sendCommunication(
                @RequestParam("recipientGroup") String recipientGroup,
                @RequestParam(value = "recipientUsernames", required = false) List<String> recipientUsernames,
                @RequestParam("subject") String subject,
                @RequestParam("priority") String priority,
                @RequestParam("messageType") String messageType,
                @RequestParam("message") String message,
                Principal principal,
                org.springframework.web.servlet.mvc.support.RedirectAttributes redirectAttributes) {
            
            User sender = principal != null ? userRepository.findByUsername(principal.getName()).orElse(null) : null;
            String senderName = sender != null ? sender.getFullName() : "Senior Manager";
            String senderDesignation = (sender != null && sender.getEmployeeProfile() != null && sender.getEmployeeProfile().getDesignation() != null)
                ? sender.getEmployeeProfile().getDesignation() : "Senior Manager";
            
            List<User> recipients;
            String label = recipientGroup;
            if ("Other".equalsIgnoreCase(recipientGroup) && recipientUsernames != null && !recipientUsernames.isEmpty()) {
                recipients = userRepository.findAll().stream()
                        .filter(u -> recipientUsernames.contains(u.getUsername()))
                        .collect(java.util.stream.Collectors.toList());
                label = "Custom List (" + recipients.size() + " employees)";
            } else {
                recipients = findUsersInGroup(recipientGroup, sender);
            }

            for (User recipient : recipients) {
                HrmsNotification notif = new HrmsNotification(
                    recipient,
                    subject,
                    message,
                    messageType,
                    priority,
                    senderName,
                    senderDesignation
                );
                hrmsNotificationRepository.save(notif);
                
                if (recipient.getEmail() != null && !recipient.getEmail().isEmpty()) {
                    emailService.sendGenericEmail(recipient.getEmail(), subject, message);
                }
            }
            
            try {
                CommunicationBroadcast cb = new CommunicationBroadcast();
                cb.setSubject(subject);
                cb.setSentTo(label);
                cb.setType(messageType);
                cb.setPriority(priority);
                cb.setSentOn(java.time.LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("dd MMM yyyy, hh:mm a")));
                cb.setRecipients(recipients.size());
                cb.setOpenRate(0);
                cb.setStatus("Sent");
                communicationBroadcastRepository.save(cb);

                RecentNotificationRecord rn = new RecentNotificationRecord();
                rn.setSubject(subject);
                rn.setNotifyTo(label);
                rn.setType(messageType);
                rn.setPriority(priority);
                rn.setSentOn(java.time.LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("dd MMM yyyy, hh:mm a")));
                rn.setRecipients(recipients.size());
                rn.setStatus("Sent");
                recentNotificationRecordRepository.save(rn);
            } catch (Exception e) {
                // ignore
            }

            redirectAttributes.addFlashAttribute("successMessage", "Communication message sent via email and dashboard notifications successfully!");
            return "redirect:/senior_manager/communication?tab=send";
        }

        @PreAuthorize("hasRole('SENIOR_MANAGER')")
        @PostMapping("/senior_manager/communication/project-update")
        public String sendProjectUpdate(
                @RequestParam("projectName") String projectName,
                @RequestParam("recipientGroup") String recipientGroup,
                @RequestParam(value = "recipientUsernames", required = false) List<String> recipientUsernames,
                @RequestParam("subject") String subject,
                @RequestParam("message") String message,
                Principal principal,
                org.springframework.web.servlet.mvc.support.RedirectAttributes redirectAttributes) {
            
            User sender = principal != null ? userRepository.findByUsername(principal.getName()).orElse(null) : null;
            String senderName = sender != null ? sender.getFullName() : "Senior Manager";
            String senderDesignation = (sender != null && sender.getEmployeeProfile() != null && sender.getEmployeeProfile().getDesignation() != null)
                ? sender.getEmployeeProfile().getDesignation() : "Senior Manager";
            
            String title = "[" + projectName + "] " + subject;
            List<User> recipients;
            String label = recipientGroup;
            if ("Other".equalsIgnoreCase(recipientGroup) && recipientUsernames != null && !recipientUsernames.isEmpty()) {
                recipients = userRepository.findAll().stream()
                        .filter(u -> recipientUsernames.contains(u.getUsername()))
                        .collect(java.util.stream.Collectors.toList());
                label = "Custom List (" + recipients.size() + " employees)";
            } else {
                recipients = findUsersInGroup(recipientGroup, sender);
            }

            for (User recipient : recipients) {
                HrmsNotification notif = new HrmsNotification(
                    recipient,
                    title,
                    message,
                    "Project Update",
                    "Normal",
                    senderName,
                    senderDesignation
                );
                hrmsNotificationRepository.save(notif);
                
                if (recipient.getEmail() != null && !recipient.getEmail().isEmpty()) {
                    emailService.sendGenericEmail(recipient.getEmail(), title, message);
                }
            }
            
            try {
                ProjectUpdateNotification pu = new ProjectUpdateNotification();
                pu.setSubject(subject);
                pu.setProject(projectName);
                pu.setSharedWith(label);
                pu.setSharedOn(java.time.LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("dd MMM yyyy, hh:mm a")));
                pu.setRecipients(recipients.size());
                pu.setViews(0);
                pu.setAcknowledged("0 (0%)");
                pu.setStatus("OPEN");
                projectUpdateNotificationRepository.save(pu);
            } catch (Exception e) {
                // ignore
            }

            redirectAttributes.addFlashAttribute("successMessage", "Project update shared via email and dashboard notifications successfully!");
            return "redirect:/senior_manager/communication?tab=project";
        }

        @PreAuthorize("hasRole('SENIOR_MANAGER')")
        @PostMapping("/senior_manager/communication/notification")
        public String publishNotification(
                @RequestParam("recipientGroup") String recipientGroup,
                @RequestParam(value = "recipientUsernames", required = false) List<String> recipientUsernames,
                @RequestParam("messageType") String messageType,
                @RequestParam("priority") String priority,
                @RequestParam("subject") String subject,
                @RequestParam("message") String message,
                Principal principal,
                org.springframework.web.servlet.mvc.support.RedirectAttributes redirectAttributes) {
            
            User sender = principal != null ? userRepository.findByUsername(principal.getName()).orElse(null) : null;
            String senderName = sender != null ? sender.getFullName() : "Senior Manager";
            String senderDesignation = (sender != null && sender.getEmployeeProfile() != null && sender.getEmployeeProfile().getDesignation() != null)
                ? sender.getEmployeeProfile().getDesignation() : "Senior Manager";
            
            List<User> recipients;
            String label = recipientGroup;
            if ("Other".equalsIgnoreCase(recipientGroup) && recipientUsernames != null && !recipientUsernames.isEmpty()) {
                recipients = userRepository.findAll().stream()
                        .filter(u -> recipientUsernames.contains(u.getUsername()))
                        .collect(java.util.stream.Collectors.toList());
                label = "Custom List (" + recipients.size() + " employees)";
            } else {
                recipients = findUsersInGroup(recipientGroup, sender);
            }

            for (User recipient : recipients) {
                HrmsNotification notif = new HrmsNotification(
                    recipient,
                    subject,
                    message,
                    messageType,
                    priority,
                    senderName,
                    senderDesignation
                );
                hrmsNotificationRepository.save(notif);
            }
            
            try {
                RecentNotificationRecord rn = new RecentNotificationRecord();
                rn.setSubject(subject);
                rn.setNotifyTo(label);
                rn.setType(messageType);
                rn.setPriority(priority);
                rn.setSentOn(java.time.LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("dd MMM yyyy, hh:mm a")));
                rn.setRecipients(recipients.size());
                rn.setStatus("Sent");
                recentNotificationRecordRepository.save(rn);
            } catch (Exception e) {
                // ignore
            }

            redirectAttributes.addFlashAttribute("successMessage", "Notification published to employee dashboards successfully!");
            return "redirect:/senior_manager/communication?tab=notification";
        }

        @PreAuthorize("hasRole('SENIOR_MANAGER')")
        @GetMapping("/senior_manager/hierarchy")
        public String showHierarchy(Model model, Principal principal) {
                User loggedInUser = principal != null
                        ? userRepository.findByUsername(principal.getName()).orElse(null)
                        : null;

                if (loggedInUser == null) {
                    return "redirect:/login";
                }

                model.addAttribute("loggedInUser", loggedInUser);

                List<User> allUsers = userRepository.findAll();
                long empCount = allUsers.stream()
                        .filter(u -> u.getRole() != null && !"CLIENT".equalsIgnoreCase(u.getRole().getRoleName()))
                        .count();

                long mgrCount = allUsers.stream()
                        .filter(u -> u.getManager() != null)
                        .map(u -> u.getManager().getId())
                        .distinct()
                        .count();

                long deptCount = departmentRepository.count();

                long posCount = allUsers.stream()
                        .filter(u -> u.getDesignation() != null && !u.getDesignation().isBlank())
                        .map(User::getDesignation)
                        .distinct()
                        .count();

                // Statistical summaries
                model.addAttribute("totalEmployees", empCount);
                model.addAttribute("totalDepartments", deptCount);
                model.addAttribute("totalManagers", mgrCount);
                model.addAttribute("totalPositions", posCount);

                // Organization Tree Counts
                long topMgt = 0;
                long hrDept = 0;
                long hrMgr = 0;
                long hrExec = 0;
                long itDept = 0;
                long finDept = 0;
                long opsDept = 0;
                long salesMkt = 0;

                for (User u : allUsers) {
                    String role = u.getRole() != null ? u.getRole().getRoleName() : "";
                    if ("CLIENT".equalsIgnoreCase(role)) {
                        continue;
                    }
                    String dept = getDepartment(u).trim().toLowerCase();
                    String designation = u.getDesignation() != null ? u.getDesignation().trim().toLowerCase() : "";

                    // 1. Top Management
                    if ("ceo".equals(designation) || "cto".equals(designation) || designation.contains("director") 
                        || "top management".equalsIgnoreCase(role) || "SUPER_ADMIN".equalsIgnoreCase(role)
                        || "ADMIN".equalsIgnoreCase(role) || "SENIOR_MANAGER".equalsIgnoreCase(role)) {
                        topMgt++;
                    }
                    // 2. Human Resources
                    else if (dept.contains("human resources") || dept.equals("hr") || dept.contains("recruitment")
                        || role.contains("HR") || "RECRUITER".equalsIgnoreCase(role)) {
                        hrDept++;
                        if (designation.contains("manager") || role.contains("MANAGER")) {
                            hrMgr++;
                        } else {
                            hrExec++;
                        }
                    }
                    // 3. Information Technology
                    else if (dept.contains("information technology") || dept.startsWith("it") 
                        || dept.contains("engineering") || dept.contains("product") || dept.contains("design")
                        || "IT_SUPPORT".equalsIgnoreCase(role) || designation.contains("developer") || designation.contains("engineer")) {
                        itDept++;
                    }
                    // 4. Finance
                    else if (dept.contains("finance") || dept.contains("accounts") 
                        || "FINANCE".equalsIgnoreCase(role) || "ACCOUNTS".equalsIgnoreCase(role)) {
                        finDept++;
                    }
                    // 5. Operations
                    else if (dept.contains("operations") || dept.contains("facilities") || dept.contains("transport")
                        || role.contains("TRANSPORT") || role.contains("FACILITY")) {
                        opsDept++;
                    }
                    // 6. Sales & Marketing / Rewards
                    else if (dept.contains("sales") || dept.contains("marketing") || dept.contains("rewards")
                        || role.contains("REWARDS")) {
                        salesMkt++;
                    }
                    // Fallback default
                    else {
                        opsDept++;
                    }
                }

                long totalTreeCount = topMgt + hrDept + itDept + finDept + opsDept + salesMkt;
                if (totalTreeCount < empCount) {
                    totalTreeCount = empCount;
                }

                model.addAttribute("totalTreeCount", totalTreeCount);
                model.addAttribute("topMgtCount", topMgt);
                model.addAttribute("hrDeptCount", hrDept);
                model.addAttribute("hrMgrCount", hrMgr);
                model.addAttribute("hrExecCount", hrExec);
                model.addAttribute("itDeptCount", itDept);
                model.addAttribute("finDeptCount", finDept);
                model.addAttribute("opsDeptCount", opsDept);
                model.addAttribute("salesMktCount", salesMkt);

                // Direct Reports list for logged-in user dynamically
                List<Map<String, Object>> directReports = new ArrayList<>();
                List<User> reports = userRepository.findAll().stream()
                        .filter(u -> u.getManager() != null && u.getManager().getId().equals(loggedInUser.getId()))
                        .collect(java.util.stream.Collectors.toList());
                for (User r : reports) {
                    directReports.add(Map.of(
                        "name", r.getFullName(),
                        "position", r.getDesignation() != null ? r.getDesignation() : "Employee",
                        "department", getDepartment(r),
                        "status", "ACTIVE".equalsIgnoreCase(r.getStatus()) ? "Full-time" : r.getStatus()
                    ));
                }
                model.addAttribute("directReports", directReports);

                // Recent Changes list from AuditLog dynamically
                List<Map<String, Object>> recentChanges = new ArrayList<>();
                List<AuditLog> logs = auditLogRepository.findAll().stream()
                        .sorted((a, b) -> {
                            if (a.getTimestamp() == null && b.getTimestamp() == null) return 0;
                            if (a.getTimestamp() == null) return 1;
                            if (b.getTimestamp() == null) return -1;
                            return b.getTimestamp().compareTo(a.getTimestamp());
                        })
                        .limit(5)
                        .collect(java.util.stream.Collectors.toList());

                DateTimeFormatter changeDtf = DateTimeFormatter.ofPattern("dd MMM yyyy, hh:mm a");
                for (AuditLog log : logs) {
                    recentChanges.add(Map.of(
                        "change", log.getAction() + " (" + log.getModule() + ")",
                        "employee", log.getNewValue() != null && log.getNewValue().length() > 50 ? log.getNewValue().substring(0, 50) + "..." : (log.getNewValue() != null ? log.getNewValue() : "-"),
                        "changedBy", log.getUsername() != null ? log.getUsername() : "System",
                        "dateTime", log.getTimestamp() != null ? log.getTimestamp().format(changeDtf) : "-"
                    ));
                }
                model.addAttribute("recentChanges", recentChanges);

                return "senior_manager-hierarchy";
        }

        private void seedProjectsAndTicketsAndTimesheets() {
            try {
                List<User> activeUsers = userRepository.findAll().stream()
                        .filter(u -> u.getStatus() != null && "ACTIVE".equalsIgnoreCase(u.getStatus()))
                        .collect(java.util.stream.Collectors.toList());
                
                User client = userRepository.findAll().stream()
                        .filter(u -> u.getRole() != null && "CLIENT".equalsIgnoreCase(u.getRole().getRoleName()))
                        .findFirst().orElse(null);
                
                if (client == null) {
                    client = userRepository.findAll().stream().findFirst().orElse(null);
                }

                if (projectRepository.count() == 0 && client != null) {
                    List<String> names = List.of("CRM Develop.", "Database", "API Gateway Integration", "Mobile App");
                    LocalDate today = LocalDate.now();
                    for (int i = 0; i < names.size(); i++) {
                        String name = names.get(i);
                        Project p = new Project();
                        p.setProjectName(name);
                        p.setClient(client);
                        p.setStage("Development");
                        p.setStartDate(today.minusDays(30));
                        p.setEndDate(today.plusDays(90));
                        projectRepository.save(p);

                        // Seed project members
                        if (!activeUsers.isEmpty()) {
                            for (int j = 0; j < 2; j++) {
                                int uIdx = (i * 2 + j) % activeUsers.size();
                                User memberUser = activeUsers.get(uIdx);
                                ProjectMember pm = new ProjectMember();
                                pm.setProject(p);
                                pm.setUser(memberUser);
                                pm.setRole(j == 0 ? "Developer" : "QA Tester");
                                pm.setBillable(true);
                                projectMemberRepository.save(pm);
                            }
                        }
                    }
                }

                List<Project> projects = projectRepository.findAll();
                if (ticketRepository.count() == 0 && !projects.isEmpty() && !activeUsers.isEmpty()) {
                    java.util.Random rand = new java.util.Random();
                    for (Project p : projects) {
                        for (int i = 1; i <= 3; i++) {
                            Ticket t = new Ticket();
                            t.setProject(p);
                            t.setTitle("Task " + i + " for " + p.getProjectName());
                            t.setDescription("Task description details");
                            t.setStatus(i == 1 ? "Completed" : "In Progress");
                            t.setProgressPercentage(i == 1 ? 100 : (i == 2 ? 65 : 85));
                            t.setPriority(i == 1 ? "Medium" : "High");
                            t.setDeadline(LocalDate.now().plusDays(rand.nextInt(15) - 5));
                            
                            t.setAssignedTo(activeUsers.get(rand.nextInt(activeUsers.size())));
                            ticketRepository.save(t);
                        }
                    }
                }

                if (weeklyTimesheetRepository.count() == 0 && !activeUsers.isEmpty()) {
                    for (int i = 0; i < Math.min(5, activeUsers.size()); i++) {
                        User u = activeUsers.get(i);
                        WeeklyTimesheet ts = new WeeklyTimesheet();
                        ts.setUser(u);
                        ts.setWeekStartDate(LocalDate.now().minusWeeks(1).with(java.time.DayOfWeek.MONDAY));
                        ts.setWeekEndDate(LocalDate.now().minusWeeks(1).with(java.time.DayOfWeek.SUNDAY));
                        ts.setStatus(i % 2 == 0 ? "Approved" : "Pending");
                        ts.setTotalWeekHours(38.5);
                        ts.setSubmissionDate(LocalDate.now().minusDays(2));
                        weeklyTimesheetRepository.save(ts);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        private void seedLeaveWalletsAndAttendance() {
            try {
                List<User> activeUsers = userRepository.findAll().stream()
                        .filter(u -> u.getStatus() != null && "ACTIVE".equalsIgnoreCase(u.getStatus()))
                        .collect(java.util.stream.Collectors.toList());

                if (leaveTypeMasterRepository.count() == 0) {
                    List<String[]> types = List.of(
                        new String[]{"CL", "Casual Leave"},
                        new String[]{"SL", "Sick Leave"},
                        new String[]{"EL", "Earned Leave"},
                        new String[]{"CO", "Comp Off"}
                    );
                    for (String[] t : types) {
                        com.example.admindashboard.model.LeaveTypeMaster ltm = new com.example.admindashboard.model.LeaveTypeMaster();
                        ltm.setLeaveCode(t[0]);
                        ltm.setLeaveName(t[1]);
                        ltm.setActive(true);
                        leaveTypeMasterRepository.save(ltm);
                    }
                }

                if (employeeLeaveWalletRepository.count() == 0) {
                    List<com.example.admindashboard.model.LeaveTypeMaster> types = leaveTypeMasterRepository.findAll();
                    for (User u : activeUsers) {
                        for (com.example.admindashboard.model.LeaveTypeMaster t : types) {
                            com.example.admindashboard.model.EmployeeLeaveWallet wallet = new com.example.admindashboard.model.EmployeeLeaveWallet();
                            wallet.setUser(u);
                            wallet.setLeaveType(t);
                            if ("CL".equals(t.getLeaveCode())) {
                                wallet.setOpeningBalance(18.0);
                                wallet.setAvailableBalance(14.0);
                                wallet.setUsedBalance(4.0);
                            } else if ("SL".equals(t.getLeaveCode())) {
                                wallet.setOpeningBalance(12.0);
                                wallet.setAvailableBalance(9.0);
                                wallet.setUsedBalance(3.0);
                            } else if ("EL".equals(t.getLeaveCode())) {
                                wallet.setOpeningBalance(10.0);
                                wallet.setAvailableBalance(8.0);
                                wallet.setUsedBalance(2.0);
                            } else {
                                wallet.setOpeningBalance(5.0);
                                wallet.setAvailableBalance(4.0);
                                wallet.setUsedBalance(1.0);
                            }
                            employeeLeaveWalletRepository.save(wallet);
                        }
                    }
                }

                if (attendanceRepository.count() == 0) {
                    LocalDate start = LocalDate.now().minusDays(30);
                    java.util.Random rand = new java.util.Random();
                    for (User u : activeUsers) {
                        for (int i = 0; i <= 30; i++) {
                            LocalDate date = start.plusDays(i);
                            if (date.getDayOfWeek() == java.time.DayOfWeek.SATURDAY || date.getDayOfWeek() == java.time.DayOfWeek.SUNDAY) {
                                continue;
                            }
                            Attendance att = new Attendance();
                            att.setUser(u);
                            att.setDate(date);
                            
                            int roll = rand.nextInt(100);
                            if (roll < 90) {
                                att.setStatus("Present");
                                att.setCheckInTime(java.time.LocalTime.of(9, rand.nextInt(30)));
                                att.setCheckOutTime(java.time.LocalTime.of(17, 30 + rand.nextInt(30)));
                                att.setTotalHours("8h " + (15 + rand.nextInt(30)) + "m");
                            } else if (roll < 96) {
                                att.setStatus("Absent");
                            } else {
                                att.setStatus("Leave");
                            }
                            attendanceRepository.save(att);
                        }
                    }
                }

                if (leaveRequestRepository.count() == 0) {
                    List<User> employeesList = activeUsers.stream()
                            .filter(u -> u.getRole() == null || !"CLIENT".equalsIgnoreCase(u.getRole().getRoleName()))
                            .collect(java.util.stream.Collectors.toList());
                    if (employeesList.size() >= 5) {
                        LocalDate today = LocalDate.now();
                        int year = today.getYear();
                        int month = today.getMonthValue();

                        // Leave 1: Vikram M
                        LeaveRequest lr1 = new LeaveRequest();
                        lr1.setUser(employeesList.get(0));
                        lr1.setLeaveType("Casual Leave");
                        lr1.setFromDate(LocalDate.of(year, month, 3));
                        lr1.setToDate(LocalDate.of(year, month, 5));
                        lr1.setTotalDays(3.0);
                        lr1.setStatus("Approved");
                        lr1.setReason("Personal work");
                        lr1.setCreatedAt(today.minusDays(10));
                        leaveRequestRepository.save(lr1);

                        // Leave 2: Pooja Desai
                        LeaveRequest lr2 = new LeaveRequest();
                        lr2.setUser(employeesList.get(1));
                        lr2.setLeaveType("Earned Leave");
                        lr2.setFromDate(LocalDate.of(year, month, 7));
                        lr2.setToDate(LocalDate.of(year, month, 8));
                        lr2.setTotalDays(2.0);
                        lr2.setStatus("Approved");
                        lr2.setReason("Family function");
                        lr2.setCreatedAt(today.minusDays(10));
                        leaveRequestRepository.save(lr2);

                        // Leave 3: Neha Iyer
                        LeaveRequest lr3 = new LeaveRequest();
                        lr3.setUser(employeesList.get(2));
                        lr3.setLeaveType("Sick Leave");
                        lr3.setFromDate(LocalDate.of(year, month, 12));
                        lr3.setToDate(LocalDate.of(year, month, 14));
                        lr3.setTotalDays(3.0);
                        lr3.setStatus("Approved");
                        lr3.setReason("Medical checkup");
                        lr3.setCreatedAt(today.minusDays(10));
                        leaveRequestRepository.save(lr3);

                        // Leave 4: Rahul Kumar
                        LeaveRequest lr4 = new LeaveRequest();
                        lr4.setUser(employeesList.get(3));
                        lr4.setLeaveType("Casual Leave");
                        lr4.setFromDate(LocalDate.of(year, month, 15));
                        lr4.setToDate(LocalDate.of(year, month, 16));
                        lr4.setTotalDays(2.0);
                        lr4.setStatus("Approved");
                        lr4.setReason("Out of station");
                        lr4.setCreatedAt(today.minusDays(10));
                        leaveRequestRepository.save(lr4);

                        // Leave 5: Megha Joshi
                        LeaveRequest lr5 = new LeaveRequest();
                        lr5.setUser(employeesList.get(4));
                        lr5.setLeaveType("Casual Leave");
                        lr5.setFromDate(LocalDate.of(year, month, 26));
                        lr5.setToDate(LocalDate.of(year, month, 27));
                        lr5.setTotalDays(2.0);
                        lr5.setStatus("Approved");
                        lr5.setReason("Rest");
                        lr5.setCreatedAt(today.minusDays(10));
                        leaveRequestRepository.save(lr5);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
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

        return "learning/learning-admin-dashboard";
    }

    @GetMapping("/space/lnd/reports")
    public String lndReports(Model model, Principal principal) {
        User user = userRepository.findByUsername(principal.getName()).orElse(null);
        if (user != null) {
            model.addAttribute("loggedInUserName", user.getFullName());
            model.addAttribute("loggedInEmail", user.getEmail());
        }
        return "learning/learning-reports";
    }

    @GetMapping("/space/lnd/roi")
    public String lndRoi(Model model, Principal principal) {
        User user = userRepository.findByUsername(principal.getName()).orElse(null);
        if (user != null) {
            model.addAttribute("loggedInUserName", user.getFullName());
            model.addAttribute("loggedInEmail", user.getEmail());
        }
        return "learning/learning-roi";
    }

    @GetMapping("/space/lnd/courses")
    public String lndCourses(Model model, Principal principal) {
        User user = userRepository.findByUsername(principal.getName()).orElse(null);
        if (user != null) {
            model.addAttribute("loggedInUserName", user.getFullName());
            model.addAttribute("loggedInEmail", user.getEmail());
        }
        return "learning/learning-courses";
    }

    @GetMapping("/space/lnd/integrations")
    public String lndIntegrations(Model model, Principal principal) {
        User user = userRepository.findByUsername(principal.getName()).orElse(null);
        if (user != null) {
            model.addAttribute("loggedInUserName", user.getFullName());
            model.addAttribute("loggedInEmail", user.getEmail());
        }
        return "learning/learning-integrations";
    }

    @GetMapping("/space/lnd/vendors")
    public String lndVendors(Model model, Principal principal) {
        User user = userRepository.findByUsername(principal.getName()).orElse(null);
        if (user != null) {
            model.addAttribute("loggedInUserName", user.getFullName());
            model.addAttribute("loggedInEmail", user.getEmail());
        }
        return "learning/learning-vendors";
    }

    @GetMapping("/space/lnd/budgets")
    public String lndBudgets(Model model, Principal principal) {
        User user = userRepository.findByUsername(principal.getName()).orElse(null);
        if (user != null) {
            model.addAttribute("loggedInUserName", user.getFullName());
            model.addAttribute("loggedInEmail", user.getEmail());
        }
        return "learning/learning-budgets";
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

        return "learning/learning-approvals";
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

    @org.springframework.transaction.annotation.Transactional
    @PreAuthorize("hasRole('SENIOR_MANAGER')")
    @PostMapping("/senior_manager/project/create")
    public String createProject(
            @RequestParam String projectName,
            @RequestParam String startDate,
            @RequestParam String endDate,
            @RequestParam(required = false) String description,
            @RequestParam(required = false) Long projectManagerId,
            @RequestParam(required = false) String status,
            @RequestParam(value = "userIds", required = false) List<Long> userIds,
            @RequestParam(value = "roles", required = false) List<String> roles,
            @RequestParam(value = "billables", required = false) List<Boolean> billables,
            @RequestParam(value = "taskName", required = false) List<String> taskNames,
            @RequestParam(value = "taskDesc", required = false) List<String> taskDescs,
            @RequestParam(value = "taskAssigneeId", required = false) List<Long> taskAssigneeIds,
            @RequestParam(value = "taskDueDate", required = false) List<String> taskDueDates,
            @RequestParam(value = "taskPriority", required = false) List<String> taskPriorities,
            Principal principal,
            org.springframework.web.servlet.mvc.support.RedirectAttributes redirectAttributes) {

        try {
            Project p = new Project();
            p.setProjectName(projectName);
            p.setStartDate(LocalDate.parse(startDate));
            p.setEndDate(LocalDate.parse(endDate));
            p.setDescription(description != null ? description : "");
            p.setStage(status != null ? status : "Active");

            if (projectManagerId != null) {
                User pm = userRepository.findById(projectManagerId).orElse(null);
                p.setProjectManager(pm);
            }

            // A project must have a client. Let's find any client in the database, or assign a default user as client.
            User client = userRepository.findAll().stream()
                .filter(u -> u.getRole() != null && "CLIENT".equalsIgnoreCase(u.getRole().getRoleName()))
                .findFirst()
                .orElse(null);
            if (client == null) {
                client = userRepository.findAll().stream().findFirst().orElse(null);
            }
            p.setClient(client);

            projectRepository.save(p);

            // Save Project Members
            if (userIds != null) {
                for (int i = 0; i < userIds.size(); i++) {
                    Long uId = userIds.get(i);
                    String role = roles != null && i < roles.size() ? roles.get(i) : "Developer";
                    Boolean billable = billables != null && i < billables.size() ? billables.get(i) : true;

                    User u = userRepository.findById(uId).orElse(null);
                    if (u != null) {
                        ProjectMember pm = new ProjectMember();
                        pm.setProject(p);
                        pm.setUser(u);
                        pm.setRole(role);
                        pm.setBillable(billable);
                        projectMemberRepository.save(pm);
                    }
                }
            }

            // Save Tasks (Tickets)
            if (taskNames != null) {
                for (int i = 0; i < taskNames.size(); i++) {
                    String tName = taskNames.get(i);
                    if (tName == null || tName.trim().isEmpty()) continue;

                    String tDesc = taskDescs != null && i < taskDescs.size() ? taskDescs.get(i) : "";
                    Long assigneeId = taskAssigneeIds != null && i < taskAssigneeIds.size() ? taskAssigneeIds.get(i) : null;
                    String dueDateStr = taskDueDates != null && i < taskDueDates.size() ? taskDueDates.get(i) : "";
                    String tPriority = taskPriorities != null && i < taskPriorities.size() ? taskPriorities.get(i) : "Medium";

                    Ticket t = new Ticket();
                    t.setProject(p);
                    t.setTitle(tName);
                    t.setDescription(tDesc);
                    t.setPriority(tPriority);
                    t.setStatus("Open");
                    if (assigneeId != null) {
                        User assignee = userRepository.findById(assigneeId).orElse(null);
                        t.setAssignedTo(assignee);
                    }
                    if (dueDateStr != null && !dueDateStr.trim().isEmpty()) {
                        t.setDeadline(LocalDate.parse(dueDateStr));
                    }
                    ticketRepository.save(t);
                }
            }

            redirectAttributes.addFlashAttribute("successMessage", "Project created successfully with members and tasks.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error creating project: " + e.getMessage());
        }
        return "redirect:/senior_manager/project_work?tab=project";
    }

    @org.springframework.transaction.annotation.Transactional
    @PreAuthorize("hasRole('SENIOR_MANAGER')")
    @PostMapping("/senior_manager/project/edit")
    public String editProject(
            @RequestParam Long projectId,
            @RequestParam String projectName,
            @RequestParam String startDate,
            @RequestParam String endDate,
            @RequestParam(value = "userIds", required = false) List<Long> userIds,
            @RequestParam(value = "roles", required = false) List<String> roles,
            @RequestParam(value = "billables", required = false) List<Boolean> billables,
            org.springframework.web.servlet.mvc.support.RedirectAttributes redirectAttributes) {

            try {
                Project p = projectRepository.findById(projectId).orElseThrow();
                p.setProjectName(projectName);
                p.setStartDate(LocalDate.parse(startDate));
                p.setEndDate(LocalDate.parse(endDate));
                projectRepository.save(p);

                // Delete existing members
                projectMemberRepository.deleteByProjectId(projectId);

                if (userIds != null) {
                    for (int i = 0; i < userIds.size(); i++) {
                        Long uId = userIds.get(i);
                        String role = roles != null && i < roles.size() ? roles.get(i) : "Developer";
                        Boolean billable = billables != null && i < billables.size() ? billables.get(i) : true;

                        User u = userRepository.findById(uId).orElse(null);
                        if (u != null) {
                            ProjectMember pm = new ProjectMember();
                            pm.setProject(p);
                            pm.setUser(u);
                            pm.setRole(role);
                            pm.setBillable(billable);
                            projectMemberRepository.save(pm);
                        }
                    }
                }
                redirectAttributes.addFlashAttribute("successMessage", "Project and team members updated successfully.");
            } catch (Exception e) {
                redirectAttributes.addFlashAttribute("errorMessage", "Error updating project: " + e.getMessage());
            }
            return "redirect:/senior_manager/project_work?tab=project";
    }

    @PreAuthorize("hasRole('SENIOR_MANAGER')")
    @GetMapping("/senior_manager/attendance/details")
    @ResponseBody
    public List<Map<String, Object>> getAttendanceDetails(
            @RequestParam String employeeName,
            @RequestParam String month) {

            User user = userRepository.findAll().stream()
                    .filter(u -> employeeName.equalsIgnoreCase(u.getFullName()))
                    .findFirst()
                    .orElse(null);

            List<Map<String, Object>> details = new ArrayList<>();
            if (user != null) {
                List<Attendance> list = attendanceRepository.findByUserOrderByIdDesc(user);
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMM yyyy");
                for (Attendance a : list) {
                    if (a.getDate() == null) continue;
                    String dateMonth = a.getDate().getMonth().toString().substring(0, 3) + " " + a.getDate().getYear();
                    if ("All".equalsIgnoreCase(month) || month == null || month.isBlank() || a.getDate().toString().contains(month) || dateMonth.equalsIgnoreCase(month) || month.toLowerCase().contains(a.getDate().getMonth().toString().toLowerCase().substring(0,3))) {
                        Map<String, Object> item = new HashMap<>();
                        item.put("date", a.getDate().format(formatter));
                        item.put("status", a.getStatus() != null ? a.getStatus() : "Present");
                        item.put("checkIn", a.getCheckInTime() != null ? a.getCheckInTime().toString() : "09:00 AM");
                        item.put("checkOut", a.getCheckOutTime() != null ? a.getCheckOutTime().toString() : "06:00 PM");
                        item.put("hours", a.getTotalHours() != null ? a.getTotalHours() : "9h 0m");
                        details.add(item);
                    }
                }
            }
            details.sort((x, y) -> String.valueOf(y.get("date")).compareTo(String.valueOf(x.get("date"))));
            return details;
    }

    private void seedEmployeeGoals() {
        if (employeeGoalRepository.count() == 0) {
            User emp1 = userRepository.findByUsername("EMP001").orElse(null);
            User emp2 = userRepository.findByUsername("EMP002").orElse(null);

            if (emp1 != null) {
                EmployeeGoal g1 = new EmployeeGoal();
                g1.setEmployee(emp1);
                g1.setTitle("Complete Project X Phase 1");
                g1.setCompletionPercentage(85);
                employeeGoalRepository.save(g1);
            }
            if (emp2 != null) {
                EmployeeGoal g2 = new EmployeeGoal();
                g2.setEmployee(emp2);
                g2.setTitle("Achieve Certification Y");
                g2.setCompletionPercentage(100);
                employeeGoalRepository.save(g2);
            }
        }
    }

    private void seedLeaveEscalations() {
        if (leaveEscalationRepository.count() == 0) {
            User admin = userRepository.findByUsername("ADMIN").orElse(null);
            User emp1 = userRepository.findByUsername("EMP001").orElse(null);
            
            if (admin != null && emp1 != null) {
                LeaveRequest leave = leaveRequestRepository.findAll().stream().filter(l -> emp1.getId().equals(l.getUser().getId())).findFirst().orElse(null);
                if (leave != null) {
                    LeaveEscalation e1 = new LeaveEscalation();
                    e1.setLeaveRequest(leave);
                    e1.setEscalatedTo(admin);
                    e1.setEscalationDate(java.time.LocalDate.now().minusDays(1));
                    e1.setStatus("Pending");
                    leaveEscalationRepository.save(e1);
                    
                    LeaveEscalation e2 = new LeaveEscalation();
                    e2.setLeaveRequest(leave);
                    e2.setEscalatedTo(admin);
                    e2.setEscalationDate(java.time.LocalDate.now().minusDays(3));
                    e2.setStatus("Pending");
                    leaveEscalationRepository.save(e2);
                    
                    LeaveEscalation e3 = new LeaveEscalation();
                    e3.setLeaveRequest(leave);
                    e3.setEscalatedTo(admin);
                    e3.setEscalationDate(java.time.LocalDate.now().minusDays(5));
                    e3.setStatus("Resolved");
                    leaveEscalationRepository.save(e3);
                }
            }
        }
    }
}