package com.example.admindashboard.service;

import com.example.admindashboard.dto.OrganizationNode;
import com.example.admindashboard.model.User;
import com.example.admindashboard.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import com.example.admindashboard.dto.EmployeeRelationshipResponse;
@Service
public class OrganizationChartService {

    private final UserRepository userRepository;

    public OrganizationChartService(
            UserRepository userRepository
    ) {
        this.userRepository = userRepository;
    }

    public OrganizationNode buildOrganizationTree() {
        User rootUser = userRepository.findFirstByRole_RoleNameAndStatus("SUPER_ADMIN", "ACTIVE")
                .orElseGet(() -> userRepository.findFirstByRole_RoleNameAndStatus("ADMIN", "ACTIVE").orElse(null));

        if (rootUser == null) {
            List<User> all = userRepository.findAll();
            if (all.isEmpty()) return null;
            rootUser = all.get(0);
        }

        OrganizationNode rootNode = createNode(rootUser);
        List<User> allUsers = userRepository.findAll();
        
        java.util.Map<Long, OrganizationNode> nodeMap = new java.util.HashMap<>();
        nodeMap.put(rootUser.getId(), rootNode);
        
        java.util.Map<String, OrganizationNode> nameToNodeMap = new java.util.HashMap<>();
        if (rootUser.getFullName() != null) nameToNodeMap.put(rootUser.getFullName().toLowerCase(), rootNode);
        if (rootUser.getUsername() != null) nameToNodeMap.put(rootUser.getUsername().toLowerCase(), rootNode);

        for (User user : allUsers) {
            if (user.getId().equals(rootUser.getId())) continue;
            OrganizationNode node = createNode(user);
            nodeMap.put(user.getId(), node);
            if (user.getFullName() != null) nameToNodeMap.put(user.getFullName().toLowerCase(), node);
            if (user.getUsername() != null) nameToNodeMap.put(user.getUsername().toLowerCase(), node);
        }
        
        for (User user : allUsers) {
            if (user.getId().equals(rootUser.getId())) continue;
            OrganizationNode node = nodeMap.get(user.getId());
            User manager = user.getManager();
            OrganizationNode parentNode = null;
            
            if (manager != null && nodeMap.containsKey(manager.getId())) {
                parentNode = nodeMap.get(manager.getId());
            } else if (user.getEmployeeProfile() != null && user.getEmployeeProfile().getReportingManager() != null) {
                String repMgr = user.getEmployeeProfile().getReportingManager().trim().toLowerCase();
                if (nameToNodeMap.containsKey(repMgr)) {
                    parentNode = nameToNodeMap.get(repMgr);
                }
            }
            
            if (parentNode != null && parentNode != node) {
                parentNode.getChildren().add(node);
            } else {
                rootNode.getChildren().add(node);
            }
        }
        
        return rootNode;
    }

    private void addRoleUsers(
            OrganizationNode parent,
            String roleName
    ) {
        List<User> users =
                userRepository
                        .findByRole_RoleName(
                                roleName
                        );

        users.forEach(user -> {
            parent.getChildren()
                    .add(
                            createNode(user)
                    );
        });
    }

    private OrganizationNode createNode(
            User user
    ) {
        String designation = "";

        if (user.getEmployeeProfile() != null && user.getEmployeeProfile().getDesignation() != null && !user.getEmployeeProfile().getDesignation().trim().isEmpty()) {
            designation = user.getEmployeeProfile().getDesignation().trim();
        } else if (user.getRole() != null && user.getRole().getRoleName() != null) {
            String r = user.getRole().getRoleName().replace("ROLE_", "");
            designation = java.util.Arrays.stream(r.split("_"))
                    .map(word -> word.isEmpty() ? "" : Character.toUpperCase(word.charAt(0)) + word.substring(1).toLowerCase())
                    .collect(java.util.stream.Collectors.joining(" "));
        }

        String roleName = user.getRole() != null ? user.getRole().getRoleName() : "EMPLOYEE";

        return new OrganizationNode(
                user.getId(),
                user.getFullName(),
                roleName,
                designation
        );
    }

    private OrganizationNode findRoleNode(
            OrganizationNode node,
            String roleName
    ) {
        for (OrganizationNode child :
                node.getChildren()) {

            if (roleName.equals(
                    child.getRole()
            )) {
                return child;
            }
        }
        return null;
    }
    public EmployeeRelationshipResponse getEmployeeRelationship(Long id) {

        User employee =
                userRepository.findById(id)
                        .orElseThrow();

        User manager = employee.getManager();

        EmployeeRelationshipResponse response =
                new EmployeeRelationshipResponse();

        response.setEmployeeId(employee.getId());
        response.setEmployeeName(employee.getFullName());
        response.setEmployeeRole(employee.getRole() != null ? employee.getRole().getRoleName() : "EMPLOYEE");

        if (employee.getEmployeeProfile() != null && employee.getEmployeeProfile().getDesignation() != null && !employee.getEmployeeProfile().getDesignation().trim().isEmpty()) {
            response.setEmployeeDesignation(
                    employee.getEmployeeProfile().getDesignation().trim()
            );
        } else {
            String r = response.getEmployeeRole().replace("ROLE_", "");
            response.setEmployeeDesignation(
                    java.util.Arrays.stream(r.split("_"))
                            .map(word -> word.isEmpty() ? "" : Character.toUpperCase(word.charAt(0)) + word.substring(1).toLowerCase())
                            .collect(java.util.stream.Collectors.joining(" "))
            );
        }

        response.setEmployeeEmail(employee.getEmail() != null && !employee.getEmail().trim().isEmpty() ? employee.getEmail() : "-");
        
        if (employee.getEmployeeProfile() != null) {
            String dept = employee.getEmployeeProfile().getDepartment();
            if (dept == null || dept.trim().isEmpty()) {
                dept = employee.getEmployeeProfile().getBusinessUnit();
            }
            response.setEmployeeDepartment(dept != null && !dept.trim().isEmpty() ? dept : "-");
            response.setEmployeeJoiningDate(employee.getEmployeeProfile().getJoiningDate() != null ? employee.getEmployeeProfile().getJoiningDate().toString() : "-");
        } else {
            response.setEmployeeDepartment("-");
            response.setEmployeeJoiningDate("-");
        }

        if (manager != null) {

            response.setManagerId(manager.getId());
            response.setManagerName(manager.getFullName());
            response.setManagerRole(manager.getRole() != null ? manager.getRole().getRoleName() : "MANAGER");

            if (manager.getEmployeeProfile() != null && manager.getEmployeeProfile().getDesignation() != null && !manager.getEmployeeProfile().getDesignation().trim().isEmpty()) {
                response.setManagerDesignation(
                        manager.getEmployeeProfile().getDesignation().trim()
                );
            } else {
                String r = response.getManagerRole().replace("ROLE_", "");
                response.setManagerDesignation(
                        java.util.Arrays.stream(r.split("_"))
                                .map(word -> word.isEmpty() ? "" : Character.toUpperCase(word.charAt(0)) + word.substring(1).toLowerCase())
                                .collect(java.util.stream.Collectors.joining(" "))
                );
            }
        }

        return response;
    }
}