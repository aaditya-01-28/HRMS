package com.example.admindashboard.controller;
import org.springframework.web.bind.annotation.ResponseBody;

import com.example.admindashboard.service.OrganizationChartService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import com.example.admindashboard.dto.OrganizationNode;
import com.example.admindashboard.dto.EmployeeRelationshipResponse;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class OrganizationChartController {

    private final OrganizationChartService organizationChartService;

    public OrganizationChartController(
            OrganizationChartService organizationChartService
    ) {
        this.organizationChartService =
                organizationChartService;
    }

    @GetMapping("/organization-structure")
    public String organizationStructure(
            Model model
    ) {

        model.addAttribute(
                "organizationTree",
                organizationChartService
                        .buildOrganizationTree()
        );

        return "organization-structure";
    }

    @GetMapping("/api/organization-test")
    @ResponseBody
    public Object organizationTest() {

        return organizationChartService
                .buildOrganizationTree();
    }
    @GetMapping("/api/organization-structure")
    @ResponseBody
    public OrganizationNode getOrganizationStructure() {

        return organizationChartService
                .buildOrganizationTree();
    }
    
    @GetMapping("/api/employee-relationship/{id}")
    @ResponseBody
    public EmployeeRelationshipResponse getEmployeeRelationship(
            @PathVariable Long id
    ) {

        return organizationChartService
                .getEmployeeRelationship(id);
    }
    
    @GetMapping("/employee-relationship/{id}")
    public String employeeRelationship() {

        return "employee-relationship";
    }
}