package com.example.admindashboard.model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "upcoming_audits")
public class UpcomingAudit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String auditName;
    private LocalDate auditDate;
    private String status; // Scheduled, Upcoming

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getAuditName() { return auditName; }
    public void setAuditName(String auditName) { this.auditName = auditName; }
    public LocalDate getAuditDate() { return auditDate; }
    public void setAuditDate(LocalDate auditDate) { this.auditDate = auditDate; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
