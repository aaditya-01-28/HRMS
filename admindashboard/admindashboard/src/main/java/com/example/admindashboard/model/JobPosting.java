package com.example.admindashboard.model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "job_postings")
public class JobPosting {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String jobId;
    private String title;
    private String department;
    private String experienceRequired;
    private String location;
    
    @Column(columnDefinition = "TEXT")
    private String description;
    
    @Column(columnDefinition = "TEXT")
    private String primarySkills;
    
    @Column(columnDefinition = "TEXT")
    private String secondarySkills;
    
    @Column(columnDefinition = "TEXT")
    private String jobOverview;
    
    @Column(columnDefinition = "TEXT")
    private String eligibilityCriteria;

    private LocalDate postingDate;
    private boolean isActive = true;

    // Added fields for complete recruitment form details
    private String jobType;
    private Integer noOfOpenings;
    
    @Column(columnDefinition = "TEXT")
    private String jobResponsibility;
    
    private String noticePeriod;
    private String ctcRange;
    private LocalDate applicationDeadline;
    private String startTime;
    private String endTime;

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getJobId() { return jobId; }
    public void setJobId(String jobId) { this.jobId = jobId; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }
    public String getExperienceRequired() { return experienceRequired; }
    public void setExperienceRequired(String experienceRequired) { this.experienceRequired = experienceRequired; }
    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getPrimarySkills() { return primarySkills; }
    public void setPrimarySkills(String primarySkills) { this.primarySkills = primarySkills; }
    public String getSecondarySkills() { return secondarySkills; }
    public void setSecondarySkills(String secondarySkills) { this.secondarySkills = secondarySkills; }
    public String getJobOverview() { return jobOverview; }
    public void setJobOverview(String jobOverview) { this.jobOverview = jobOverview; }
    public String getEligibilityCriteria() { return eligibilityCriteria; }
    public void setEligibilityCriteria(String eligibilityCriteria) { this.eligibilityCriteria = eligibilityCriteria; }
    public LocalDate getPostingDate() { return postingDate; }
    public void setPostingDate(LocalDate postingDate) { this.postingDate = postingDate; }
    public boolean isActive() { return isActive; }
    public void setActive(boolean active) { isActive = active; }

    public String getJobType() { return jobType; }
    public void setJobType(String jobType) { this.jobType = jobType; }
    public Integer getNoOfOpenings() { return noOfOpenings; }
    public void setNoOfOpenings(Integer noOfOpenings) { this.noOfOpenings = noOfOpenings; }
    public String getJobResponsibility() { return jobResponsibility; }
    public void setJobResponsibility(String jobResponsibility) { this.jobResponsibility = jobResponsibility; }
    public String getNoticePeriod() { return noticePeriod; }
    public void setNoticePeriod(String noticePeriod) { this.noticePeriod = noticePeriod; }
    public String getCtcRange() { return ctcRange; }
    public void setCtcRange(String ctcRange) { this.ctcRange = ctcRange; }
    public LocalDate getApplicationDeadline() { return applicationDeadline; }
    public void setApplicationDeadline(LocalDate applicationDeadline) { this.applicationDeadline = applicationDeadline; }
    public String getStartTime() { return startTime; }
    public void setStartTime(String startTime) { this.startTime = startTime; }
    public String getEndTime() { return endTime; }
    public void setEndTime(String endTime) { this.endTime = endTime; }
}
