package com.example.admindashboard.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "resignation_requests")
public class ResignationRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User employee;

    @Column(nullable = false)
    private LocalDate requestDate;

    @Column(length = 1000)
    private String reason;

    @Column(length = 2000)
    private String comments;

    @Column(length = 2000)
    private String hrComments;

    // Status: DRAFT, PENDING_L2, PENDING_L3, APPROVED, REJECTED, OFFBOARDED
    @Column(nullable = false)
    private String status = "PENDING_L2";

    private Integer noticePeriodDays;
    
    private LocalDate lastWorkingDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "l2_approved_by")
    private User l2ApprovedBy;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "l3_approved_by")
    private User l3ApprovedBy;

    private LocalDateTime createdAt = LocalDateTime.now();

    public ResignationRequest() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public User getEmployee() { return employee; }
    public void setEmployee(User employee) { this.employee = employee; }

    public LocalDate getRequestDate() { return requestDate; }
    public void setRequestDate(LocalDate requestDate) { this.requestDate = requestDate; }

    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }

    public String getComments() { return comments; }
    public void setComments(String comments) { this.comments = comments; }

    public String getHrComments() { return hrComments; }
    public void setHrComments(String hrComments) { this.hrComments = hrComments; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Integer getNoticePeriodDays() { return noticePeriodDays; }
    public void setNoticePeriodDays(Integer noticePeriodDays) { this.noticePeriodDays = noticePeriodDays; }

    public LocalDate getLastWorkingDate() { return lastWorkingDate; }
    public void setLastWorkingDate(LocalDate lastWorkingDate) { this.lastWorkingDate = lastWorkingDate; }

    public User getL2ApprovedBy() { return l2ApprovedBy; }
    public void setL2ApprovedBy(User l2ApprovedBy) { this.l2ApprovedBy = l2ApprovedBy; }

    public User getL3ApprovedBy() { return l3ApprovedBy; }
    public void setL3ApprovedBy(User l3ApprovedBy) { this.l3ApprovedBy = l3ApprovedBy; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
