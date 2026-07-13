package com.example.admindashboard.model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "employee_feedbacks")
public class EmployeeFeedback {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "employee_id", nullable = false)
    private User employee;

    @ManyToOne
    @JoinColumn(name = "reviewer_id", nullable = false)
    private User reviewer;

    private String roleLevel; // L1 (Employee) or L2 (Manager)

    // L1 / L2 Ratings
    private Double rating1; // Technical Skills (L1) / Leadership (L2)
    private Double rating2; // Communication (L1) / Project Execution (L2)
    private Double rating3; // Teamwork (L1) / Decision Making (L2)

    @Column(length = 1000)
    private String comments;

    private LocalDate submittedAt;

    /* GETTERS AND SETTERS */

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getEmployee() {
        return employee;
    }

    public void setEmployee(User employee) {
        this.employee = employee;
    }

    public User getReviewer() {
        return reviewer;
    }

    public void setReviewer(User reviewer) {
        this.reviewer = reviewer;
    }

    public String getRoleLevel() {
        return roleLevel;
    }

    public void setRoleLevel(String roleLevel) {
        this.roleLevel = roleLevel;
    }

    public Double getRating1() {
        return rating1;
    }

    public void setRating1(Double rating1) {
        this.rating1 = rating1;
    }

    public Double getRating2() {
        return rating2;
    }

    public void setRating2(Double rating2) {
        this.rating2 = rating2;
    }

    public Double getRating3() {
        return rating3;
    }

    public void setRating3(Double rating3) {
        this.rating3 = rating3;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public LocalDate getSubmittedAt() {
        return submittedAt;
    }

    public void setSubmittedAt(LocalDate submittedAt) {
        this.submittedAt = submittedAt;
    }
}
