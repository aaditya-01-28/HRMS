package com.example.admindashboard.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

@Entity
@Table(name = "feedback_request_fields")
public class FeedbackRequestField {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "feedback_request_id", nullable = false)
    @JsonIgnore
    private PerformanceFeedbackRequest feedbackRequest;

    @Column(nullable = false)
    private String fieldName;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(columnDefinition = "TEXT")
    private String fieldValue; // Filled by the employee

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public PerformanceFeedbackRequest getFeedbackRequest() { return feedbackRequest; }
    public void setFeedbackRequest(PerformanceFeedbackRequest feedbackRequest) { this.feedbackRequest = feedbackRequest; }

    public String getFieldName() { return fieldName; }
    public void setFieldName(String fieldName) { this.fieldName = fieldName; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getFieldValue() { return fieldValue; }
    public void setFieldValue(String fieldValue) { this.fieldValue = fieldValue; }
}
