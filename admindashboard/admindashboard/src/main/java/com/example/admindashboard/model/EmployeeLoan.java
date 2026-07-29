package com.example.admindashboard.model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "employee_loans")
public class EmployeeLoan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id")
    private User user;

    private String employeeName;
    private String employeeCode;
    private String loanType; // Personal Loan, Salary Advance, Home Loan, Emergency Loan
    private Double principalAmount;
    private Double monthlyEmi;
    private Integer tenureMonths;
    private Double outstandingAmount;
    private LocalDate requestDate;
    private String status; // Pending, Approved, Rejected, Closed

    public EmployeeLoan() {}

    public EmployeeLoan(User user, String employeeName, String employeeCode, String loanType, 
                        Double principalAmount, Double monthlyEmi, Integer tenureMonths, 
                        Double outstandingAmount, LocalDate requestDate, String status) {
        this.user = user;
        this.employeeName = employeeName;
        this.employeeCode = employeeCode;
        this.loanType = loanType;
        this.principalAmount = principalAmount;
        this.monthlyEmi = monthlyEmi;
        this.tenureMonths = tenureMonths;
        this.outstandingAmount = outstandingAmount;
        this.requestDate = requestDate;
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

    public String getLoanType() { return loanType; }
    public void setLoanType(String loanType) { this.loanType = loanType; }

    public Double getPrincipalAmount() { return principalAmount; }
    public void setPrincipalAmount(Double principalAmount) { this.principalAmount = principalAmount; }

    public Double getMonthlyEmi() { return monthlyEmi; }
    public void setMonthlyEmi(Double monthlyEmi) { this.monthlyEmi = monthlyEmi; }

    public Integer getTenureMonths() { return tenureMonths; }
    public void setTenureMonths(Integer tenureMonths) { this.tenureMonths = tenureMonths; }

    public Double getOutstandingAmount() { return outstandingAmount; }
    public void setOutstandingAmount(Double outstandingAmount) { this.outstandingAmount = outstandingAmount; }

    public LocalDate getRequestDate() { return requestDate; }
    public void setRequestDate(LocalDate requestDate) { this.requestDate = requestDate; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
