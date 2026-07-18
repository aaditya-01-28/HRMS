package com.example.admindashboard.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "performance_feedback_requests")
public class PerformanceFeedbackRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id", nullable = false)
    @JsonIgnore
    private User employee;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "manager_id", nullable = false)
    @JsonIgnore
    private User manager;

    private String roleLevel; // L1 or L2

    private String status = "PENDING"; // PENDING, COMPLETED

    private LocalDate createdAt;
    private LocalDate submittedAt;

    // Default rating section (filled by employee)
    private Double rating; // 1.0 to 5.0
    
    @Column(columnDefinition = "TEXT")
    private String comments;

    @OneToMany(mappedBy = "feedbackRequest", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<FeedbackRequestField> additionalFields = new ArrayList<>();

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDate.now();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public User getEmployee() { return employee; }
    public void setEmployee(User employee) { this.employee = employee; }

    public User getManager() { return manager; }
    public void setManager(User manager) { this.manager = manager; }

    public String getRoleLevel() { return roleLevel; }
    public void setRoleLevel(String roleLevel) { this.roleLevel = roleLevel; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public LocalDate getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDate createdAt) { this.createdAt = createdAt; }

    public LocalDate getSubmittedAt() { return submittedAt; }
    public void setSubmittedAt(LocalDate submittedAt) { this.submittedAt = submittedAt; }

    public Double getRating() { return rating; }
    public void setRating(Double rating) { this.rating = rating; }

    public String getComments() { return comments; }
    public void setComments(String comments) { this.comments = comments; }

    private Double managerRating;

    @Column(columnDefinition = "TEXT")
    private String managerComments;

    public Double getManagerRating() { return managerRating; }
    public void setManagerRating(Double managerRating) { this.managerRating = managerRating; }

    public String getManagerComments() { return managerComments; }
    public void setManagerComments(String managerComments) { this.managerComments = managerComments; }

    public List<FeedbackRequestField> getAdditionalFields() { return additionalFields; }
    public void setAdditionalFields(List<FeedbackRequestField> additionalFields) { this.additionalFields = additionalFields; }

    private String serialNumber;

    public String getSerialNumber() { return serialNumber; }
    public void setSerialNumber(String serialNumber) { this.serialNumber = serialNumber; }

    @Transient
    public String getFieldsString() {
        if (additionalFields == null || additionalFields.isEmpty()) {
            return "";
        }
        List<String> parts = new ArrayList<>();
        for (FeedbackRequestField f : additionalFields) {
            String fId = String.valueOf(f.getId());
            String fName = f.getFieldName() != null ? f.getFieldName().replace(":", "").replace("|", "") : "";
            String fDesc = f.getDescription() != null ? f.getDescription().replace(":", "").replace("|", "") : "";
            String fVal = f.getFieldValue() != null ? f.getFieldValue().replace(":", "").replace("|", "") : "";
            String empR = f.getEmployeeRating() != null ? String.valueOf(f.getEmployeeRating()) : "";
            String mgrR = f.getManagerRating() != null ? String.valueOf(f.getManagerRating()) : "";
            parts.add(fId + "::" + fName + "::" + fDesc + "::" + fVal + "::" + empR + "::" + mgrR);
        }
        return String.join("||", parts);
    }
}
