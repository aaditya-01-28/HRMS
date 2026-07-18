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
        
        for (User user : allUsers) {
            if (user.getId().equals(rootUser.getId())) continue;
            nodeMap.put(user.getId(), createNode(user));
        }
        
        for (User user : allUsers) {
            if (user.getId().equals(rootUser.getId())) continue;
            OrganizationNode node = nodeMap.get(user.getId());
            User manager = user.getManager();
            if (manager != null && nodeMap.containsKey(manager.getId())) {
                nodeMap.get(manager.getId()).getChildren().add(node);
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

        if (user.getEmployeeProfile() != null) {
            designation =
                    user.getEmployeeProfile()
                            .getDesignation();
        }

        return new OrganizationNode(
                user.getId(),
                user.getFullName(),
                user.getRole().getRoleName(),
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
        System.out.println(
        	    "Employee = " + employee.getFullName()
        	);

        	if(employee.getManager() != null){
        	    System.out.println(
        	        "Manager = " +
        	        employee.getManager().getFullName()
        	    );
        	}

        EmployeeRelationshipResponse response =
                new EmployeeRelationshipResponse();

        response.setEmployeeId(employee.getId());
        response.setEmployeeName(employee.getFullName());
        response.setEmployeeRole(employee.getRole().getRoleName());

        if (employee.getEmployeeProfile() != null) {
            response.setEmployeeDesignation(
                    employee.getEmployeeProfile().getDesignation()
            );
        }

        if (manager != null) {

            response.setManagerId(manager.getId());
            response.setManagerName(manager.getFullName());
            response.setManagerRole(manager.getRole().getRoleName());

            if (manager.getEmployeeProfile() != null) {
                response.setManagerDesignation(
                        manager.getEmployeeProfile().getDesignation()
                );
            }
        }

        return response;
    }
}