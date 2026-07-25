package com.example.admindashboard.controller;

import com.example.admindashboard.model.*;
import com.example.admindashboard.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/senior_it")
@PreAuthorize("hasAuthority('admin_dashboard_view')")
public class SeniorItController {

    @Autowired
    private ItAssetRepository itAssetRepository;

    @Autowired
    private SecurityPolicyRepository securityPolicyRepository;

    @Autowired
    private BackupScheduleRepository backupScheduleRepository;

    @Autowired
    private ItBroadcastRepository itBroadcastRepository;

    @Autowired
    private ServiceRequestRepository serviceRequestRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PermissionRepository permissionRepository;

    @Autowired
    private com.example.admindashboard.repository.DepartmentEntityRepository departmentRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping("/my_space")
    public String showSeniorItMySpace(Model model, Principal principal, @RequestParam(name = "tab", defaultValue = "dashboard") String tab) {
        String currentUserId = principal.getName();
        User currentUser = userRepository.findByUsername(currentUserId).orElse(null);
        model.addAttribute("currentUser", currentUser);
        model.addAttribute("activeTab", tab);

        // 1. Dashboard Tab Data
        List<ServiceRequest> itTickets = serviceRequestRepository.findAll().stream()
                .filter(t -> "IT".equalsIgnoreCase(t.getType()))
                .collect(Collectors.toList());

        long totalTickets = itTickets.size();
        long pendingTickets = itTickets.stream().filter(t -> "Open".equalsIgnoreCase(t.getStatus())).count();
        long inProgressTickets = itTickets.stream().filter(t -> "In Progress".equalsIgnoreCase(t.getStatus())).count();
        long resolvedToday = itTickets.stream().filter(t -> "Closed".equalsIgnoreCase(t.getStatus()) || "Resolved".equalsIgnoreCase(t.getStatus())).count();
        model.addAttribute("totalTicketsCount", totalTickets);
        model.addAttribute("pendingTicketsCount", pendingTickets);
        model.addAttribute("inProgressTicketsCount", inProgressTickets);
        model.addAttribute("resolvedTodayCount", resolvedToday);
        model.addAttribute("avgResolutionTime", "4.2 hrs");

        // Tickets categories counts
        long softwareCount = itTickets.stream().filter(t -> "Software Issue".equalsIgnoreCase(t.getCategory())).count();
        long hardwareCount = itTickets.stream().filter(t -> "Hardware Issue".equalsIgnoreCase(t.getCategory())).count();
        long accessCount = itTickets.stream().filter(t -> "Access/Permission".equalsIgnoreCase(t.getCategory())).count();
        long networkCount = itTickets.stream().filter(t -> "Network/Connectivity".equalsIgnoreCase(t.getCategory())).count();

        model.addAttribute("softwareCount", softwareCount);
        model.addAttribute("hardwareCount", hardwareCount);
        model.addAttribute("accessCount", accessCount);
        model.addAttribute("networkCount", networkCount);

        // Percentage counts
        model.addAttribute("softwarePercent", totalTickets > 0 ? (softwareCount * 100) / totalTickets : 0);
        model.addAttribute("hardwarePercent", totalTickets > 0 ? (hardwareCount * 100) / totalTickets : 0);
        model.addAttribute("accessPercent", totalTickets > 0 ? (accessCount * 100) / totalTickets : 0);
        model.addAttribute("networkPercent", totalTickets > 0 ? (networkCount * 100) / totalTickets : 0);

        // Recent Tickets List
        List<ServiceRequest> recentTickets = itTickets.stream()
                .sorted((t1, t2) -> Long.compare(t2.getId() != null ? t2.getId() : 0, t1.getId() != null ? t1.getId() : 0))
                .limit(10)
                .collect(Collectors.toList());
        model.addAttribute("recentTickets", recentTickets);

        // 2. User & Access Tab Data
        List<User> allUsers = userRepository.findAll();
        List<User> employeeUsers = allUsers.stream()
                .filter(u -> u.getEmployeeProfile() != null)
                .collect(Collectors.toList());
        model.addAttribute("users", employeeUsers);

        List<Role> allRoles = roleRepository.findAll();
        model.addAttribute("roles", allRoles);

        List<Permission> allPermissions = permissionRepository.findAll();
        model.addAttribute("permissions", allPermissions);

        // 3. Devices/Asset Tab Data
        List<ItAsset> allAssets = itAssetRepository.findAll();
        model.addAttribute("assets", allAssets);

        long activeAssets = allAssets.stream().filter(a -> "Active".equalsIgnoreCase(a.getStatus())).count();
        long maintenanceAssets = allAssets.stream().filter(a -> "Maintenance".equalsIgnoreCase(a.getStatus())).count();
        long disposedAssets = allAssets.stream().filter(a -> "Disposed".equalsIgnoreCase(a.getStatus())).count();
        model.addAttribute("activeAssetsCount", activeAssets);
        model.addAttribute("maintenanceAssetsCount", maintenanceAssets);
        model.addAttribute("disposedAssetsCount", disposedAssets);

        // 4. Security & Compliance Tab Data
        List<SecurityPolicy> securityPolicies = securityPolicyRepository.findAll();
        model.addAttribute("securityPolicies", securityPolicies);

        long activePolicies = securityPolicies.stream().filter(p -> "Active".equalsIgnoreCase(p.getStatus())).count();
        long reviewPolicies = securityPolicies.stream().filter(p -> "Under Review".equalsIgnoreCase(p.getStatus())).count();
        long nonCompliantPolicies = securityPolicies.stream().filter(p -> "Non-Compliant".equalsIgnoreCase(p.getStatus())).count();
        model.addAttribute("activePoliciesCount", activePolicies);
        model.addAttribute("reviewPoliciesCount", reviewPolicies);
        model.addAttribute("nonCompliantPoliciesCount", nonCompliantPolicies);

        // 5. Data Access & Backup Tab Data
        List<BackupSchedule> backupSchedules = backupScheduleRepository.findAll();
        model.addAttribute("backupSchedules", backupSchedules);

        long completedBackups = backupSchedules.stream().filter(b -> "Completed".equalsIgnoreCase(b.getStatus())).count();
        long scheduledBackups = backupSchedules.stream().filter(b -> "Scheduled".equalsIgnoreCase(b.getStatus())).count();
        long failedBackups = backupSchedules.stream().filter(b -> "Failed".equalsIgnoreCase(b.getStatus())).count();
        model.addAttribute("completedBackupsCount", completedBackups);
        model.addAttribute("scheduledBackupsCount", scheduledBackups);
        model.addAttribute("failedBackupsCount", failedBackups);

        // 6. Broadcast / Communication Tab Data
        List<ItBroadcast> broadcasts = itBroadcastRepository.findAll();
        model.addAttribute("broadcasts", broadcasts);

        // 7. Departments for autocomplete
        List<String> departments = allUsers.stream()
                .map(u -> u.getEmployeeProfile() != null ? u.getEmployeeProfile().getDepartment() : null)
                .filter(d -> d != null && !d.trim().isEmpty())
                .distinct()
                .sorted()
                .collect(Collectors.toList());
        model.addAttribute("departments", departments);

        return "senior_it-myspace";
    }

    // CREATE TICKET
    @PostMapping("/tickets/create")
    public String createTicket(@ModelAttribute ServiceRequest req, Principal principal) {
        String currentUserId = principal.getName();
        User currentUser = userRepository.findByUsername(currentUserId).orElse(null);

        // Generate Ticket ID
        req.setTicketId("IT-TKT-" + (100 + new Random().nextInt(900)));
        req.setEmployeeId(currentUserId);
        req.setEmployeeName(currentUser != null ? currentUser.getFullName() : "IT Admin");
        req.setType("IT");
        req.setStatus("Open");
        req.setSubmissionDate(LocalDate.now());
        req.setDepartment("IT");

        serviceRequestRepository.save(req);
        return "redirect:/senior_it/my_space?tab=tickets";
    }

    // UPDATE TICKET
    @PostMapping("/tickets/update")
    public String updateTicket(@RequestParam Long ticketId,
                               @RequestParam String status,
                               @RequestParam String assignedTo,
                               @RequestParam(required = false) String adminComments) {
        ServiceRequest req = serviceRequestRepository.findById(ticketId).orElse(null);
        if (req != null) {
            req.setStatus(status);
            req.setAssignedTo(assignedTo);
            if (adminComments != null) {
                req.setAdminComments(adminComments);
            }
            req.setActionDate(LocalDate.now());
            serviceRequestRepository.save(req);
        }
        return "redirect:/senior_it/my_space?tab=tickets";
    }

    // ADD ASSET
    @PostMapping("/assets/add")
    public String addAsset(@ModelAttribute ItAsset asset) {
        itAssetRepository.save(asset);
        return "redirect:/senior_it/my_space?tab=devices";
    }

    // UPDATE ASSET STATUS
    @PostMapping("/assets/update")
    public String updateAsset(@RequestParam Long assetId,
                              @RequestParam String status,
                              @RequestParam String assignedTo,
                              @RequestParam String location) {
        ItAsset asset = itAssetRepository.findById(assetId).orElse(null);
        if (asset != null) {
            asset.setStatus(status);
            asset.setAssignedTo(assignedTo);
            asset.setLocation(location);
            itAssetRepository.save(asset);
        }
        return "redirect:/senior_it/my_space?tab=devices";
    }

    // CREATE BACKUP
    @PostMapping("/backup/create")
    public String createBackup(@ModelAttribute BackupSchedule backup) {
        backup.setStatus("Scheduled");
        backup.setLastRunDate(LocalDate.now());
        backup.setNextRunDate(LocalDate.now().plusDays(1));
        backupScheduleRepository.save(backup);
        return "redirect:/senior_it/my_space?tab=backup";
    }

    // CREATE BROADCAST
    @PostMapping("/broadcast/create")
    public String createBroadcast(@ModelAttribute ItBroadcast broadcast, Principal principal) {
        broadcast.setStatus("Active");
        broadcast.setCreatedDate(LocalDate.now());
        broadcast.setCreatedBy(principal.getName());
        itBroadcastRepository.save(broadcast);
        return "redirect:/senior_it/my_space?tab=communication";
    }

    // DELETE BROADCAST
    @PostMapping("/broadcast/delete/{id}")
    public String deleteBroadcast(@PathVariable Long id) {
        itBroadcastRepository.deleteById(id);
        return "redirect:/senior_it/my_space?tab=communication";
    }

    // ADD ROLE POSITION (System Role Matrix)
    @PostMapping("/roles/add")
    public String addRolePosition(@RequestParam String roleName,
                                  @RequestParam String roleCode,
                                  @RequestParam String category,
                                  @RequestParam String description,
                                  @RequestParam String status,
                                  @RequestParam(required = false) List<Long> permissionIds) {
        Role role = new Role();
        role.setRoleName("ROLE_" + roleName.toUpperCase().replace(" ", "_"));
        role.setRoleCode(roleCode.toUpperCase());
        role.setCategory(category);
        role.setDescription(description);
        role.setStatus(status);

        if (permissionIds != null) {
            Set<Permission> perms = new HashSet<>(permissionRepository.findAllById(permissionIds));
            role.setPermissions(perms);
        }

        roleRepository.save(role);
        return "redirect:/senior_it/my_space?tab=hierarchy";
    }

    // ASSIGN SYSTEM ROLE MATRIX
    @PostMapping("/roles/assign")
    public String assignRole(@RequestParam String username, 
                             @RequestParam String roleName,
                             @RequestParam(required = false) String departmentId,
                             @RequestParam(defaultValue = "hierarchy") String tab) {
        User user = userRepository.findByUsername(username.toUpperCase()).orElse(null);
        Role role = roleRepository.findByRoleName(roleName).orElse(null);
        if (user != null) {
            if (role != null) {
                user.setRole(role);
            }
            if (departmentId != null && !departmentId.trim().isEmpty()) {
                try {
                    user.setDepartmentId(Long.valueOf(departmentId));
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
            }
            userRepository.save(user);
        }
        return "redirect:/senior_it/my_space?tab=" + tab;
    }

    // ADD USER & ACCESS
    @PostMapping("/users/add")
    public String addUser(@RequestParam String username,
                          @RequestParam String fullName,
                          @RequestParam String email,
                          @RequestParam String designation,
                          @RequestParam String department,
                          @RequestParam String roleName,
                          @RequestParam String workLocation) {
        if (userRepository.findByUsername(username.toUpperCase()).isEmpty()) {
            User user = new User();
            user.setUsername(username.toUpperCase());
            user.setPassword(passwordEncoder.encode("Welcome@123"));
            user.setFullName(fullName);
            user.setEmail(email);

            Role role = roleRepository.findByRoleName(roleName).orElse(null);
            if (role != null) {
                user.setRole(role);
            }

            EmployeeProfile profile = new EmployeeProfile();
            profile.setDesignation(designation);
            profile.setDepartment(department);
            profile.setJoiningDate(LocalDate.now());
            profile.setWorkLocation(workLocation);
            profile.setEmployeeCode(username.toUpperCase());

            profile.setUser(user);
            user.setEmployeeProfile(profile);

            userRepository.save(user);
        }
        return "redirect:/senior_it/my_space?tab=user-access";
    }
}
