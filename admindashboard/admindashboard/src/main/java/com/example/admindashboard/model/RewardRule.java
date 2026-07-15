package com.example.admindashboard.model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "reward_rules")
public class RewardRule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String ruleName;
    
    @Column(columnDefinition = "TEXT")
    private String description;
    
    private Integer pointsPerTransaction = 0;
    private String ruleType; // e.g. Peer recognition, Milestone, Monthly credit
    private String targetGroup; // e.g. All Employees, Engineering, Sales
    private LocalDate startDate;
    private LocalDate endDate;
    private String status = "ACTIVE"; // ACTIVE, INACTIVE

    public RewardRule() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getRuleName() { return ruleName; }
    public void setRuleName(String ruleName) { this.ruleName = ruleName; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Integer getPointsPerTransaction() { return pointsPerTransaction; }
    public void setPointsPerTransaction(Integer pointsPerTransaction) { this.pointsPerTransaction = pointsPerTransaction; }

    public String getRuleType() { return ruleType; }
    public void setRuleType(String ruleType) { this.ruleType = ruleType; }

    public String getTargetGroup() { return targetGroup; }
    public void setTargetGroup(String targetGroup) { this.targetGroup = targetGroup; }

    public LocalDate getStartDate() { return startDate; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }

    public LocalDate getEndDate() { return endDate; }
    public void setEndDate(LocalDate endDate) { this.endDate = endDate; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
