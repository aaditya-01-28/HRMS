package com.example.admindashboard.dto;

import java.util.ArrayList;
import java.util.List;

public class OrganizationNode {

    private Long id;
    private String fullName;
    private String role;
    private String designation;

    private List<OrganizationNode> children =
            new ArrayList<>();

    public OrganizationNode() {
    }

    public OrganizationNode(
            Long id,
            String fullName,
            String role,
            String designation
    ) {
        this.id = id;
        this.fullName = fullName;
        this.role = role;
        this.designation = designation;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getDesignation() {
        return designation;
    }

    public void setDesignation(String designation) {
        this.designation = designation;
    }

    public List<OrganizationNode> getChildren() {
        return children;
    }

    public void setChildren(
            List<OrganizationNode> children
    ) {
        this.children = children;
    }
    
}