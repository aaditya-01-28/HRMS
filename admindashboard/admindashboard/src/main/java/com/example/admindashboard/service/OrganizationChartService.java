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

        User superAdmin =
                userRepository
                        .findFirstByRole_RoleNameAndStatus(
                                "SUPER_ADMIN",
                                "ACTIVE"
                        )
                        .orElse(null);

        if (superAdmin == null) {
            return null;
        }

        OrganizationNode superparentNode =
                createNode(superAdmin);

        User admin =
                userRepository
                        .findFirstByRole_RoleNameAndStatus(
                                "ADMIN",
                                "ACTIVE"
                        )
                        .orElse(null);

        OrganizationNode parentNode;

        if (admin == null) {

            parentNode = superparentNode;

        } else {

            parentNode = createNode(admin);

            superparentNode
                    .getChildren()
                    .add(parentNode);
        }

        addRoleUsers(
                parentNode,
                "HR_MANAGER"
        );

        addRoleUsers(
                parentNode,
                "HR_ADMIN"
        );

        addRoleUsers(
                parentNode,
                "HR_EXECUTIVE"
        );

        addRoleUsers(
                parentNode,
                "IT_ADMIN"
        );

        addRoleUsers(
                parentNode,
                "IT_SUPPORT"
        );

        addRoleUsers(
                parentNode,
                "FINANCE"
        );

        addRoleUsers(
                parentNode,
                "RECRUITER"
        );

        addRoleUsers(
                parentNode,
                "PROJECT_MANAGER"
        );

        addRoleUsers(
                parentNode,
                "AUDITOR"
        );

        addRoleUsers(
                parentNode,
                "TRANSPORT"
        );

        addRoleUsers(
                parentNode,
                "LND"
        );

        addRoleUsers(
                parentNode,
                "MANAGER"
        );

        OrganizationNode hrManagerNode =
                findRoleNode(
                        parentNode,
                        "HR_MANAGER"
                );

        if (hrManagerNode != null) {

            List<User> employees =
                    userRepository
                            .findByRole_RoleName(
                                    "EMPLOYEE"
                            );

            employees.forEach(employee -> {

                hrManagerNode
                        .getChildren()
                        .add(
                                createNode(employee)
                        );

            });
        }

        return superparentNode;
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