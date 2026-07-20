package com.example.admindashboard.model;

import jakarta.persistence.*;

@Entity
public class LearningBudget {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String departmentOrProgram;
    private Double allocatedAmount;
    private Double spentAmount;
    private Double remainingAmount;
    private String budgetPeriod; // e.g., "Q1 2026", "FY2026"
    private String status;

    @PrePersist
    @PreUpdate
    public void calculateRemaining() {
        if (allocatedAmount != null) {
            if (spentAmount == null) spentAmount = 0.0;
            remainingAmount = allocatedAmount - spentAmount;
        }
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getDepartmentOrProgram() { return departmentOrProgram; }
    public void setDepartmentOrProgram(String departmentOrProgram) { this.departmentOrProgram = departmentOrProgram; }

    public Double getAllocatedAmount() { return allocatedAmount; }
    public void setAllocatedAmount(Double allocatedAmount) { this.allocatedAmount = allocatedAmount; }

    public Double getSpentAmount() { return spentAmount; }
    public void setSpentAmount(Double spentAmount) { this.spentAmount = spentAmount; }

    public Double getRemainingAmount() { return remainingAmount; }
    public void setRemainingAmount(Double remainingAmount) { this.remainingAmount = remainingAmount; }

    public String getBudgetPeriod() { return budgetPeriod; }
    public void setBudgetPeriod(String budgetPeriod) { this.budgetPeriod = budgetPeriod; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
