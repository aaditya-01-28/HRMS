package com.example.admindashboard.model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "salary_revision_requests")
public class SalaryRevisionRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id")
    private User user;

    private String employeeName;
    private String employeeCode;
    private String department;
    private String currentCtc;
    private String revisedCtc;
    private String percentageIncrease;
    private LocalDate effectiveDate;
    private String reason;
    private String status; // Pending, Approved, Rejected

    public SalaryRevisionRequest() {}

    public SalaryRevisionRequest(User user, String employeeName, String employeeCode, String department, 
                                 String currentCtc, String revisedCtc, String percentageIncrease, 
                                 LocalDate effectiveDate, String reason, String status) {
        this.user = user;
        this.employeeName = employeeName;
        this.employeeCode = employeeCode;
        this.department = department;
        this.currentCtc = currentCtc;
        this.revisedCtc = revisedCtc;
        this.percentageIncrease = percentageIncrease;
        this.effectiveDate = effectiveDate;
        this.reason = reason;
        this.status = status;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public String getEmployeeName() { return employeeName; }
    public void setEmployeeName(String employeeName) { this.employeeName = employeeName; }

    public String getEmployeeCode() { return employeeCode; }
    public void setEmployeeCode(String employeeCode) { this.employeeCode = employeeCode; }

    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }

    public String getCurrentCtc() { return currentCtc; }
    public void setCurrentCtc(String currentCtc) { this.currentCtc = currentCtc; }

    public String getRevisedCtc() { return revisedCtc; }
    public void setRevisedCtc(String revisedCtc) { this.revisedCtc = revisedCtc; }

    public String getPercentageIncrease() { return percentageIncrease; }
    public void setPercentageIncrease(String percentageIncrease) { this.percentageIncrease = percentageIncrease; }

    public LocalDate getEffectiveDate() { return effectiveDate; }
    public void setEffectiveDate(LocalDate effectiveDate) { this.effectiveDate = effectiveDate; }

    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
