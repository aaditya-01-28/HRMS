package com.example.admindashboard.model;

import jakarta.persistence.*;

@Entity
@Table(name = "department_entity")
public class DepartmentEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String departmentName;
    
    @ManyToOne
    @JoinColumn(name = "head_user_id")
    private User departmentHead;
    
    private Integer employeesCount;
    private String locations;
    private String status; // Active, Inactive

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getDepartmentName() { return departmentName; }
    public void setDepartmentName(String departmentName) { this.departmentName = departmentName; }
    public User getDepartmentHead() { return departmentHead; }
    public void setDepartmentHead(User departmentHead) { this.departmentHead = departmentHead; }
    public Integer getEmployeesCount() { return employeesCount; }
    public void setEmployeesCount(Integer employeesCount) { this.employeesCount = employeesCount; }
    public String getLocations() { return locations; }
    public void setLocations(String locations) { this.locations = locations; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
