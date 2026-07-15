package com.example.admindashboard.model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "reward_programs")
public class RewardProgram {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String programName;
    
    @Column(columnDefinition = "TEXT")
    private String description;
    
    private String status = "ACTIVE"; // ACTIVE, INACTIVE, DRAFT
    private Integer pointsValue = 0;

    // Type classification
    private String programType; // PEER_TO_PEER, SPOT, PERFORMANCE, MILESTONE

    // Eligibility & Scope
    private String eligibility; // e.g. All Employees, High Performers (L1/L2), Tenure based
    private String department;  // e.g. All, Sales, Engineering, HR
    private String location;    // e.g. All, Raipur, Delhi
    
    // Reward Rules & Limits
    private String frequency;   // e.g. One-time, Monthly, Per Year
    private Integer maxLimit;   // Max awards/limit per user
    private Boolean requiresApproval = false;

    // Timeline
    private LocalDate startDate;
    private LocalDate validTill;

    // Type-specific fields
    private String awardCategory;  // e.g. Peer-to-peer thanks, Spot, Performance, Birthday
    private String programOwner;   // e.g. HR Team, CS Team, Sales Manager
    private String criteriaType;   // e.g. Rating, Achievement
    private String criteriaValue;  // e.g. 4.5+ Rating, Sales Target
    private String milestoneType;  // e.g. Work Anniversary, Birthday, Other
    private String milestoneValue; // e.g. 1 Year, 5 Years, 10 Years

    public RewardProgram() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getProgramName() { return programName; }
    public void setProgramName(String programName) { this.programName = programName; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Integer getPointsValue() { return pointsValue; }
    public void setPointsValue(Integer pointsValue) { this.pointsValue = pointsValue; }

    public String getProgramType() { return programType; }
    public void setProgramType(String programType) { this.programType = programType; }

    public String getEligibility() { return eligibility; }
    public void setEligibility(String eligibility) { this.eligibility = eligibility; }

    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public String getFrequency() { return frequency; }
    public void setFrequency(String frequency) { this.frequency = frequency; }

    public Integer getMaxLimit() { return maxLimit; }
    public void setMaxLimit(Integer maxLimit) { this.maxLimit = maxLimit; }

    public Boolean getRequiresApproval() { return requiresApproval; }
    public void setRequiresApproval(Boolean requiresApproval) { this.requiresApproval = requiresApproval; }

    public LocalDate getStartDate() { return startDate; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }

    public LocalDate getValidTill() { return validTill; }
    public void setValidTill(LocalDate validTill) { this.validTill = validTill; }

    public String getAwardCategory() { return awardCategory; }
    public void setAwardCategory(String awardCategory) { this.awardCategory = awardCategory; }

    public String getProgramOwner() { return programOwner; }
    public void setProgramOwner(String programOwner) { this.programOwner = programOwner; }

    public String getCriteriaType() { return criteriaType; }
    public void setCriteriaType(String criteriaType) { this.criteriaType = criteriaType; }

    public String getCriteriaValue() { return criteriaValue; }
    public void setCriteriaValue(String criteriaValue) { this.criteriaValue = criteriaValue; }

    public String getMilestoneType() { return milestoneType; }
    public void setMilestoneType(String milestoneType) { this.milestoneType = milestoneType; }

    public String getMilestoneValue() { return milestoneValue; }
    public void setMilestoneValue(String milestoneValue) { this.milestoneValue = milestoneValue; }
}
