package com.example.admindashboard.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "employee_role_assignments")
public class EmployeeRoleAssignment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @JsonIgnore
    private User user;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "role_id", nullable = false)
    private Role role;

    private LocalDate assignedOn;

    private LocalDate effectiveFrom;

    private String status = "Active";

    // Constructors
    public EmployeeRoleAssignment() {}

    public EmployeeRoleAssignment(User user, Role role, LocalDate assignedOn, LocalDate effectiveFrom, String status) {
        this.user = user;
        this.role = role;
        this.assignedOn = assignedOn;
        this.effectiveFrom = effectiveFrom;
        this.status = status;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public Role getRole() { return role; }
    public void setRole(Role role) { this.role = role; }

    public LocalDate getAssignedOn() { return assignedOn; }
    public void setAssignedOn(LocalDate assignedOn) { this.assignedOn = assignedOn; }

    public LocalDate getEffectiveFrom() { return effectiveFrom; }
    public void setEffectiveFrom(LocalDate effectiveFrom) { this.effectiveFrom = effectiveFrom; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
