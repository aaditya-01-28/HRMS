package com.example.admindashboard.model;

import jakarta.persistence.*;

@Entity
@Table(name = "reward_programs")
public class RewardProgram {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String programName;
    
    @Column(columnDefinition = "TEXT")
    private String description;
    
    private String status = "ACTIVE"; // ACTIVE, INACTIVE
    private Integer pointsValue = 0;

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
}
