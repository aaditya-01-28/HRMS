package com.example.admindashboard.model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "statutory_compliance")
public class StatutoryCompliance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String complianceType;
    private String applicableTo;
    private String frequency;
    private LocalDate nextDueDate;
    private String status; // Compliant, Due Soon, Overdue
    private LocalDate lastCompletedOn;
    
    @ManyToOne
    @JoinColumn(name = "responsible_person_id")
    private User responsiblePerson;

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getComplianceType() { return complianceType; }
    public void setComplianceType(String complianceType) { this.complianceType = complianceType; }
    public String getApplicableTo() { return applicableTo; }
    public void setApplicableTo(String applicableTo) { this.applicableTo = applicableTo; }
    public String getFrequency() { return frequency; }
    public void setFrequency(String frequency) { this.frequency = frequency; }
    public LocalDate getNextDueDate() { return nextDueDate; }
    public void setNextDueDate(LocalDate nextDueDate) { this.nextDueDate = nextDueDate; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public LocalDate getLastCompletedOn() { return lastCompletedOn; }
    public void setLastCompletedOn(LocalDate lastCompletedOn) { this.lastCompletedOn = lastCompletedOn; }
    public User getResponsiblePerson() { return responsiblePerson; }
    public void setResponsiblePerson(User responsiblePerson) { this.responsiblePerson = responsiblePerson; }
}
