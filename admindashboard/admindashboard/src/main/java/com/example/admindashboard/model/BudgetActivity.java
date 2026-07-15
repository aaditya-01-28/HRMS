package com.example.admindashboard.model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "budget_activities")
public class BudgetActivity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String activityName;
    private String department;
    private Integer pointsValue = 0;
    private String activityType; // e.g. Allocation, Spending, Redemption
    private LocalDate actionDate;
    private String status = "APPROVED"; // e.g. APPROVED, PENDING, REJECTED

    public BudgetActivity() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getActivityName() { return activityName; }
    public void setActivityName(String activityName) { this.activityName = activityName; }

    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }

    public Integer getPointsValue() { return pointsValue; }
    public void setPointsValue(Integer pointsValue) { this.pointsValue = pointsValue; }

    public String getActivityType() { return activityType; }
    public void setActivityType(String activityType) { this.activityType = activityType; }

    public LocalDate getActionDate() { return actionDate; }
    public void setActionDate(LocalDate actionDate) { this.actionDate = actionDate; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
