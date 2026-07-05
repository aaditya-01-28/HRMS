package com.example.admindashboard.dto.role;

import java.time.LocalDate;

public class EmployeeRoleAssignmentDTO {
    private Long id;
    private String roleName;
    private String roleCode;
    private String category;
    private LocalDate assignedOn;
    private LocalDate effectiveFrom;
    private String status;

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getRoleName() { return roleName; }
    public void setRoleName(String roleName) { this.roleName = roleName; }
    public String getRoleCode() { return roleCode; }
    public void setRoleCode(String roleCode) { this.roleCode = roleCode; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public LocalDate getAssignedOn() { return assignedOn; }
    public void setAssignedOn(LocalDate assignedOn) { this.assignedOn = assignedOn; }
    public LocalDate getEffectiveFrom() { return effectiveFrom; }
    public void setEffectiveFrom(LocalDate effectiveFrom) { this.effectiveFrom = effectiveFrom; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
