package com.example.admindashboard.model;

import jakarta.persistence.*;

@Entity
public class LearningProgram {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String category; // Technical, Behavioral, Leadership, Compliance, Others
    private String programType;
    
    @Column(length = 1000)
    private String description;
    
    private String department;
    private String targetAudience;
    private String businessGoal;
    private String programOwner;
    
    private String duration;
    private String modeOfDelivery; // Online, Offline, Blended
    private String startDate;
    private String endDate;
    private String level;
    private String tags;

    @Column(length = 2000)
    private String objectives; // JSON or newline separated

    private String status; // Active, Draft, Closed

    public LearningProgram() {}

    public LearningProgram(String name, String category, String programType, String description, String department, String targetAudience, String businessGoal, String programOwner, String duration, String modeOfDelivery, String startDate, String endDate, String level, String tags, String objectives, String status) {
        this.name = name;
        this.category = category;
        this.programType = programType;
        this.description = description;
        this.department = department;
        this.targetAudience = targetAudience;
        this.businessGoal = businessGoal;
        this.programOwner = programOwner;
        this.duration = duration;
        this.modeOfDelivery = modeOfDelivery;
        this.startDate = startDate;
        this.endDate = endDate;
        this.level = level;
        this.tags = tags;
        this.objectives = objectives;
        this.status = status;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getProgramType() { return programType; }
    public void setProgramType(String programType) { this.programType = programType; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }

    public String getTargetAudience() { return targetAudience; }
    public void setTargetAudience(String targetAudience) { this.targetAudience = targetAudience; }

    public String getBusinessGoal() { return businessGoal; }
    public void setBusinessGoal(String businessGoal) { this.businessGoal = businessGoal; }

    public String getProgramOwner() { return programOwner; }
    public void setProgramOwner(String programOwner) { this.programOwner = programOwner; }

    public String getDuration() { return duration; }
    public void setDuration(String duration) { this.duration = duration; }

    public String getModeOfDelivery() { return modeOfDelivery; }
    public void setModeOfDelivery(String modeOfDelivery) { this.modeOfDelivery = modeOfDelivery; }

    public String getStartDate() { return startDate; }
    public void setStartDate(String startDate) { this.startDate = startDate; }

    public String getEndDate() { return endDate; }
    public void setEndDate(String endDate) { this.endDate = endDate; }

    public String getLevel() { return level; }
    public void setLevel(String level) { this.level = level; }

    public String getTags() { return tags; }
    public void setTags(String tags) { this.tags = tags; }

    public String getObjectives() { return objectives; }
    public void setObjectives(String objectives) { this.objectives = objectives; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
