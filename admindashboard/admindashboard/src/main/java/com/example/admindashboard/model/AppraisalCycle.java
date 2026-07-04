package com.example.admindashboard.model;

import jakarta.persistence.*;

@Entity
@Table(name = "appraisal_cycles")
public class AppraisalCycle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String cycleName;

    @Column(nullable = false)
    private String cycleType;

    private String cycleCode;
    private String durationType;
    private String startDate;
    private String endDate;
    private String frequency;
    private String timeZone;
    private String cycleOwner;

    @Column(columnDefinition = "TEXT")
    private String description;

    private Integer progress = 85; // Default progress e.g. 85% to match screenshot
    private String status = "Active"; // Default status
    private Integer empCount = 112; // Default employee count to match screenshot

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getCycleName() { return cycleName; }
    public void setCycleName(String cycleName) { this.cycleName = cycleName; }

    public String getCycleType() { return cycleType; }
    public void setCycleType(String cycleType) { this.cycleType = cycleType; }

    public String getCycleCode() { return cycleCode; }
    public void setCycleCode(String cycleCode) { this.cycleCode = cycleCode; }

    public String getDurationType() { return durationType; }
    public void setDurationType(String durationType) { this.durationType = durationType; }

    public String getStartDate() { return startDate; }
    public void setStartDate(String startDate) { this.startDate = startDate; }

    public String getEndDate() { return endDate; }
    public void setEndDate(String endDate) { this.endDate = endDate; }

    public String getFrequency() { return frequency; }
    public void setFrequency(String frequency) { this.frequency = frequency; }

    public String getTimeZone() { return timeZone; }
    public void setTimeZone(String timeZone) { this.timeZone = timeZone; }

    public String getCycleOwner() { return cycleOwner; }
    public void setCycleOwner(String cycleOwner) { this.cycleOwner = cycleOwner; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Integer getProgress() { return progress; }
    public void setProgress(Integer progress) { this.progress = progress; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Integer getEmpCount() { return empCount; }
    public void setEmpCount(Integer empCount) { this.empCount = empCount; }
}
