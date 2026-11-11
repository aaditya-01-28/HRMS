package com.example.admindashboard.model;

import jakarta.persistence.*;

@Entity
@Table(name = "department_entity")
public class DepartmentEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String departmentName;

    private String headName;
    
    @ManyToOne
    @JoinColumn(name = "head_user_id")
    private User departmentHead;
    
    private Integer employeesCount = 0;
    private Integer totalTeams = 2;
    private String locations;
    private String status = "Active"; // Active, Inactive

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getDepartmentName() { return departmentName; }
    public void setDepartmentName(String departmentName) { this.departmentName = departmentName; }
    public String getHeadName() { 
        if (headName != null && !headName.trim().isEmpty()) return headName;
        if (departmentHead != null && departmentHead.getFullName() != null) return departmentHead.getFullName();
        return "Department Head";
    }
    public void setHeadName(String headName) { this.headName = headName; }
    public User getDepartmentHead() { return departmentHead; }
    public void setDepartmentHead(User departmentHead) { this.departmentHead = departmentHead; }
    public Integer getEmployeesCount() { return employeesCount != null ? employeesCount : 0; }
    public void setEmployeesCount(Integer employeesCount) { this.employeesCount = employeesCount; }
    public Integer getTotalTeams() { return totalTeams != null ? totalTeams : 2; }
    public void setTotalTeams(Integer totalTeams) { this.totalTeams = totalTeams; }
    public String getLocations() { return locations != null ? locations : "Raipur, India"; }
    public void setLocations(String locations) { this.locations = locations; }
    public String getStatus() { return status != null ? status : "Active"; }
    public void setStatus(String status) { this.status = status; }
}
