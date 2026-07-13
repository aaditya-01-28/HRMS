package com.example.admindashboard.model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "leave_escalations")
public class LeaveEscalation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "leave_request_id")
    private LeaveRequest leaveRequest;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "escalated_to_user_id")
    private User escalatedTo;

    private String status; // PENDING, OVERDUE, RESOLVED
    
    private LocalDate escalationDate;
    private LocalDate dueDate;
    private LocalDate resolvedDate;

    public LeaveEscalation() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public LeaveRequest getLeaveRequest() { return leaveRequest; }
    public void setLeaveRequest(LeaveRequest leaveRequest) { this.leaveRequest = leaveRequest; }

    public User getEscalatedTo() { return escalatedTo; }
    public void setEscalatedTo(User escalatedTo) { this.escalatedTo = escalatedTo; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public LocalDate getEscalationDate() { return escalationDate; }
    public void setEscalationDate(LocalDate escalationDate) { this.escalationDate = escalationDate; }

    public LocalDate getDueDate() { return dueDate; }
    public void setDueDate(LocalDate dueDate) { this.dueDate = dueDate; }

    public LocalDate getResolvedDate() { return resolvedDate; }
    public void setResolvedDate(LocalDate resolvedDate) { this.resolvedDate = resolvedDate; }
}
