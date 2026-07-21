package com.example.admindashboard.model;

import jakarta.persistence.*;

@Entity
public class Assessment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String code; // ASMT-001, QUIZ-012, etc.
    private String name;
    private String type; // Assessment, Quiz
    private String createdBy;
    private String department;
    private String passingCriteria; // e.g. "70% or above"
    private Integer passingPercentage; // e.g. 70
    private String status; // Active, Draft, Closed, Expired
    private Integer attemptsCount;
    private Double passRate; // e.g. 82.0 for 82%
    
    private String targetAudience;
    private String difficultyLevel;
    private Integer durationMinutes;
    private Integer totalQuestions;
    private Integer marksPerQuestion;
    private Integer totalMarks;
    private Boolean negativeMarking;
    private Boolean shuffleQuestions;
    private String assessmentMode; // Online, Offline, Blended
    private String createdAt;

    public Assessment() {}

    public Assessment(String code, String name, String type, String createdBy, String department, String passingCriteria, Integer passingPercentage, String status, Integer attemptsCount, Double passRate, String targetAudience, String difficultyLevel, Integer durationMinutes, Integer totalQuestions, Integer marksPerQuestion, Integer totalMarks, Boolean negativeMarking, Boolean shuffleQuestions, String assessmentMode, String createdAt) {
        this.code = code;
        this.name = name;
        this.type = type;
        this.createdBy = createdBy;
        this.department = department;
        this.passingCriteria = passingCriteria;
        this.passingPercentage = passingPercentage;
        this.status = status;
        this.attemptsCount = attemptsCount;
        this.passRate = passRate;
        this.targetAudience = targetAudience;
        this.difficultyLevel = difficultyLevel;
        this.durationMinutes = durationMinutes;
        this.totalQuestions = totalQuestions;
        this.marksPerQuestion = marksPerQuestion;
        this.totalMarks = totalMarks;
        this.negativeMarking = negativeMarking;
        this.shuffleQuestions = shuffleQuestions;
        this.assessmentMode = assessmentMode;
        this.createdAt = createdAt;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getCreatedBy() { return createdBy; }
    public void setCreatedBy(String createdBy) { this.createdBy = createdBy; }

    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }

    public String getPassingCriteria() { return passingCriteria; }
    public void setPassingCriteria(String passingCriteria) { this.passingCriteria = passingCriteria; }

    public Integer getPassingPercentage() { return passingPercentage; }
    public void setPassingPercentage(Integer passingPercentage) { this.passingPercentage = passingPercentage; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Integer getAttemptsCount() { return attemptsCount; }
    public void setAttemptsCount(Integer attemptsCount) { this.attemptsCount = attemptsCount; }

    public Double getPassRate() { return passRate; }
    public void setPassRate(Double passRate) { this.passRate = passRate; }

    public String getTargetAudience() { return targetAudience; }
    public void setTargetAudience(String targetAudience) { this.targetAudience = targetAudience; }

    public String getDifficultyLevel() { return difficultyLevel; }
    public void setDifficultyLevel(String difficultyLevel) { this.difficultyLevel = difficultyLevel; }

    public Integer getDurationMinutes() { return durationMinutes; }
    public void setDurationMinutes(Integer durationMinutes) { this.durationMinutes = durationMinutes; }

    public Integer getTotalQuestions() { return totalQuestions; }
    public void setTotalQuestions(Integer totalQuestions) { this.totalQuestions = totalQuestions; }

    public Integer getMarksPerQuestion() { return marksPerQuestion; }
    public void setMarksPerQuestion(Integer marksPerQuestion) { this.marksPerQuestion = marksPerQuestion; }

    public Integer getTotalMarks() { return totalMarks; }
    public void setTotalMarks(Integer totalMarks) { this.totalMarks = totalMarks; }

    public Boolean getNegativeMarking() { return negativeMarking; }
    public void setNegativeMarking(Boolean negativeMarking) { this.negativeMarking = negativeMarking; }

    public Boolean getShuffleQuestions() { return shuffleQuestions; }
    public void setShuffleQuestions(Boolean shuffleQuestions) { this.shuffleQuestions = shuffleQuestions; }

    public String getAssessmentMode() { return assessmentMode; }
    public void setAssessmentMode(String assessmentMode) { this.assessmentMode = assessmentMode; }

    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }
}
