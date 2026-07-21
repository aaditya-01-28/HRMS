package com.example.admindashboard.model;

import jakarta.persistence.*;

@Entity
public class LearningStrategy {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    
    @Column(length = 1000)
    private String description;
    
    private String businessGoal;
    private String targetAudience;
    private String status; // Active, Draft, Closed
    private String lastUpdated;

    public LearningStrategy() {}

    public LearningStrategy(String name, String description, String businessGoal, String targetAudience, String status, String lastUpdated) {
        this.name = name;
        this.description = description;
        this.businessGoal = businessGoal;
        this.targetAudience = targetAudience;
        this.status = status;
        this.lastUpdated = lastUpdated;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getBusinessGoal() { return businessGoal; }
    public void setBusinessGoal(String businessGoal) { this.businessGoal = businessGoal; }

    public String getTargetAudience() { return targetAudience; }
    public void setTargetAudience(String targetAudience) { this.targetAudience = targetAudience; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getLastUpdated() { return lastUpdated; }
    public void setLastUpdated(String lastUpdated) { this.lastUpdated = lastUpdated; }
}
