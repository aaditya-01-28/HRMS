package com.example.admindashboard.model;

import jakarta.persistence.*;

@Entity
@Table(name = "reward_budgets")
public class RewardBudget {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String department;
    private Integer allocatedPoints = 0;
    private Integer spentPoints = 0;
    private String fiscalYear;

    public RewardBudget() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }

    public Integer getAllocatedPoints() { return allocatedPoints; }
    public void setAllocatedPoints(Integer allocatedPoints) { this.allocatedPoints = allocatedPoints; }

    public Integer getSpentPoints() { return spentPoints; }
    public void setSpentPoints(Integer spentPoints) { this.spentPoints = spentPoints; }

    public String getFiscalYear() { return fiscalYear; }
    public void setFiscalYear(String fiscalYear) { this.fiscalYear = fiscalYear; }
}
