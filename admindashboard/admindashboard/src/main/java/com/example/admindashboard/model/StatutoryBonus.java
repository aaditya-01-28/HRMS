package com.example.admindashboard.model;

import jakarta.persistence.*;

@Entity
@Table(name = "statutory_bonuses")
public class StatutoryBonus {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String empId;
    private String employeeName;
    private String department;
    private String designation;
    private String eligibilityStatus;
    private Double bonusableSalary;
    private Double bonusAmount;
    private String disbursementStatus;

    public StatutoryBonus() {}

    public StatutoryBonus(String empId, String employeeName, String department, String designation, String eligibilityStatus, Double bonusableSalary, Double bonusAmount, String disbursementStatus) {
        this.empId = empId;
        this.employeeName = employeeName;
        this.department = department;
        this.designation = designation;
        this.eligibilityStatus = eligibilityStatus;
        this.bonusableSalary = bonusableSalary;
        this.bonusAmount = bonusAmount;
        this.disbursementStatus = disbursementStatus;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getEmpId() { return empId; }
    public void setEmpId(String empId) { this.empId = empId; }

    public String getEmployeeName() { return employeeName; }
    public void setEmployeeName(String employeeName) { this.employeeName = employeeName; }

    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }

    public String getDesignation() { return designation; }
    public void setDesignation(String designation) { this.designation = designation; }

    public String getEligibilityStatus() { return eligibilityStatus; }
    public void setEligibilityStatus(String eligibilityStatus) { this.eligibilityStatus = eligibilityStatus; }

    public Double getBonusableSalary() { return bonusableSalary; }
    public void setBonusableSalary(Double bonusableSalary) { this.bonusableSalary = bonusableSalary; }

    public Double getBonusAmount() { return bonusAmount; }
    public void setBonusAmount(Double bonusAmount) { this.bonusAmount = bonusAmount; }

    public String getDisbursementStatus() { return disbursementStatus; }
    public void setDisbursementStatus(String disbursementStatus) { this.disbursementStatus = disbursementStatus; }
}
