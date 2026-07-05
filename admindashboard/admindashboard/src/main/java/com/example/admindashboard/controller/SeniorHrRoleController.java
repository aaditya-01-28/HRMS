package com.example.admindashboard.controller;

import com.example.admindashboard.dto.role.*;
import com.example.admindashboard.model.*;
import com.example.admindashboard.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/senior_hr")
public class SeniorHrRoleController {

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PermissionRepository permissionRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EmployeeRoleAssignmentRepository employeeRoleAssignmentRepository;

    @GetMapping("/roles")
    public String getRolesPage() {
        return "senior_hr-roles";
    }

    @GetMapping("/api/roles")
    @ResponseBody
    public List<RoleDTO> getAllRoles() {
        List<Role> roles = roleRepository.findAll();
        List<RoleDTO> roleDTOs = new ArrayList<>();

        for (Role role : roles) {
            RoleDTO dto = new RoleDTO();
            dto.setId(role.getId());
            dto.setRoleName(role.getRoleName());
            dto.setRoleCode(role.getRoleCode() != null ? role.getRoleCode() : role.getRoleName().toUpperCase().replace(" ", "_"));
            dto.setDescription(role.getDescription() != null ? role.getDescription() : "Standard Role");
            dto.setCategory(role.getCategory() != null ? role.getCategory() : "General");
            dto.setRoleType(role.getRoleType() != null ? role.getRoleType() : "System Role");
            dto.setStatus(role.getStatus() != null ? role.getStatus() : "Active");
            dto.setCreatedOn(role.getCreatedOn() != null ? role.getCreatedOn() : LocalDate.now());
            dto.setCreatedBy(role.getCreatedBy() != null ? role.getCreatedBy() : "System");
            
            // Calculate users count (primary + assigned)
            long usersCount = userRepository.findAll().stream()
                    .filter(u -> (u.getRole() != null && u.getRole().getId().equals(role.getId())) || 
                                 (u.getRoleAssignments() != null && u.getRoleAssignments().stream().anyMatch(a -> "Active".equalsIgnoreCase(a.getStatus()) && a.getRole().getId().equals(role.getId()))))
                    .count();
            dto.setUsersCount((int) usersCount);

            List<PermissionDTO> perms = new ArrayList<>();
            if (role.getPermissions() != null) {
                for (Permission p : role.getPermissions()) {
                    perms.add(new PermissionDTO(p.getId(), p.getPermissionName(), p.getModule(), p.getAccessLevel()));
                }
            }
            dto.setPermissions(perms);
            roleDTOs.add(dto);
        }

        return roleDTOs;
    }

    @GetMapping("/api/permissions/grouped")
    @ResponseBody
    public Map<String, List<PermissionDTO>> getGroupedPermissions() {
        List<Permission> permissions = permissionRepository.findAll();
        Map<String, List<PermissionDTO>> grouped = new HashMap<>();

        for (Permission p : permissions) {
            String module = p.getModule() != null ? p.getModule() : "General";
            grouped.computeIfAbsent(module, k -> new ArrayList<>())
                   .add(new PermissionDTO(p.getId(), p.getPermissionName(), module, p.getAccessLevel()));
        }
        return grouped;
    }

    @PostMapping("/api/roles")
    @ResponseBody
    public Map<String, Object> createRole(@RequestBody Map<String, Object> payload) {
        Map<String, Object> response = new HashMap<>();
        try {
            Role role = new Role();
            role.setRoleName((String) payload.get("roleName"));
            role.setRoleCode((String) payload.get("roleCode"));
            role.setDescription((String) payload.get("description"));
            role.setCategory((String) payload.get("roleCategory"));
            role.setRoleType((String) payload.get("roleType"));
            role.setStatus((String) payload.get("status"));
            role.setCreatedBy("Admin");
            role.setCreatedOn(LocalDate.now());

            List<Integer> permissionIds = (List<Integer>) payload.get("permissionIds");
            if (permissionIds != null && !permissionIds.isEmpty()) {
                Set<Permission> perms = new HashSet<>();
                for (Integer pId : permissionIds) {
                    permissionRepository.findById(pId.longValue()).ifPresent(perms::add);
                }
                role.setPermissions(perms);
            }

            roleRepository.save(role);
            response.put("success", true);
            response.put("message", "Role created successfully");
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Failed to create role: " + e.getMessage());
        }
        return response;
    }

    @GetMapping("/api/users/{userId}/roles")
    @ResponseBody
    public List<EmployeeRoleAssignmentDTO> getUserRoles(@PathVariable Long userId) {
        List<EmployeeRoleAssignmentDTO> result = new ArrayList<>();
        Optional<User> optUser = userRepository.findById(userId);
        if (optUser.isPresent()) {
            User user = optUser.get();
            // Primary Role
            if (user.getRole() != null) {
                EmployeeRoleAssignmentDTO dto = new EmployeeRoleAssignmentDTO();
                dto.setId(0L); // primary role indicator
                dto.setRoleName(user.getRole().getRoleName());
                dto.setRoleCode(user.getRole().getRoleCode() != null ? user.getRole().getRoleCode() : user.getRole().getRoleName());
                dto.setCategory(user.getRole().getCategory() != null ? user.getRole().getCategory() : "System Role");
                if (user.getEmployeeProfile() != null && user.getEmployeeProfile().getJoiningDate() != null) {
                    dto.setAssignedOn(user.getEmployeeProfile().getJoiningDate());
                    dto.setEffectiveFrom(user.getEmployeeProfile().getJoiningDate());
                } else {
                    dto.setAssignedOn(LocalDate.now());
                    dto.setEffectiveFrom(LocalDate.now());
                }
                dto.setStatus("Active (Primary)");
                result.add(dto);
            }
            // Assigned Roles
            List<EmployeeRoleAssignment> assignments = employeeRoleAssignmentRepository.findByUser(user);
            for (EmployeeRoleAssignment a : assignments) {
                EmployeeRoleAssignmentDTO dto = new EmployeeRoleAssignmentDTO();
                dto.setId(a.getId());
                dto.setRoleName(a.getRole().getRoleName());
                dto.setRoleCode(a.getRole().getRoleCode());
                dto.setCategory(a.getRole().getCategory());
                dto.setAssignedOn(a.getAssignedOn());
                dto.setEffectiveFrom(a.getEffectiveFrom());
                dto.setStatus(a.getStatus());
                result.add(dto);
            }
        }
        return result;
    }

    @PostMapping("/api/users/roles/assign")
    @ResponseBody
    public Map<String, Object> assignRole(@RequestBody Map<String, Object> payload) {
        Map<String, Object> response = new HashMap<>();
        try {
            Long userId = Long.valueOf(payload.get("userId").toString());
            Long roleId = Long.valueOf(payload.get("roleId").toString());
            String effectiveFromStr = (String) payload.get("effectiveFrom");
            
            User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
            Role role = roleRepository.findById(roleId).orElseThrow(() -> new RuntimeException("Role not found"));

            EmployeeRoleAssignment assignment = new EmployeeRoleAssignment();
            assignment.setUser(user);
            assignment.setRole(role);
            assignment.setAssignedOn(LocalDate.now());
            if (effectiveFromStr != null && !effectiveFromStr.isEmpty()) {
                assignment.setEffectiveFrom(LocalDate.parse(effectiveFromStr));
            } else {
                assignment.setEffectiveFrom(LocalDate.now());
            }
            assignment.setStatus("Active");

            employeeRoleAssignmentRepository.save(assignment);
            
            response.put("success", true);
            response.put("message", "Role assigned successfully");
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Failed to assign role: " + e.getMessage());
        }
        return response;
    }

    @GetMapping("/api/users/search")
    @ResponseBody
    public List<Map<String, Object>> searchUsers(@RequestParam String query) {
        List<User> users = userRepository.findAll();
        String lowercaseQuery = query.toLowerCase();
        
        return users.stream()
            .filter(u -> (u.getUsername() != null && u.getUsername().toLowerCase().contains(lowercaseQuery)) ||
                         (u.getEmployeeProfile() != null && (
                            (u.getEmployeeProfile().getFirstName() != null && u.getEmployeeProfile().getFirstName().toLowerCase().contains(lowercaseQuery)) ||
                            (u.getEmployeeProfile().getLastName() != null && u.getEmployeeProfile().getLastName().toLowerCase().contains(lowercaseQuery)) ||
                            (u.getEmployeeProfile().getEmployeeCode() != null && u.getEmployeeProfile().getEmployeeCode().toLowerCase().contains(lowercaseQuery))
                         )))
            .map(u -> {
                Map<String, Object> map = new HashMap<>();
                map.put("id", u.getId());
                if (u.getEmployeeProfile() != null) {
                    map.put("name", u.getEmployeeProfile().getFirstName() + " " + u.getEmployeeProfile().getLastName());
                    map.put("employeeId", u.getEmployeeProfile().getEmployeeCode());
                    map.put("employmentType", u.getEmployeeProfile().getEmploymentType() != null ? u.getEmployeeProfile().getEmploymentType() : "Full-time");
                } else {
                    map.put("name", u.getUsername());
                    map.put("employeeId", "N/A");
                    map.put("employmentType", "Unknown");
                }
                return map;
            })
            .collect(Collectors.toList());
    }
}
