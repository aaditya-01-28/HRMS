package com.example.admindashboard.model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
public class EpfSettings {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String pfNumber;
    private String signatoryName;
    private Double employeeContributionPercentage;
    private Double employerContributionPercentage;
    private Double basicPayLimit;
    private LocalDate effectiveDate;

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getPfNumber() { return pfNumber; }
    public void setPfNumber(String pfNumber) { this.pfNumber = pfNumber; }

    public String getSignatoryName() { return signatoryName; }
    public void setSignatoryName(String signatoryName) { this.signatoryName = signatoryName; }

    public Double getEmployeeContributionPercentage() { return employeeContributionPercentage; }
    public void setEmployeeContributionPercentage(Double employeeContributionPercentage) { this.employeeContributionPercentage = employeeContributionPercentage; }

    public Double getEmployerContributionPercentage() { return employerContributionPercentage; }
    public void setEmployerContributionPercentage(Double employerContributionPercentage) { this.employerContributionPercentage = employerContributionPercentage; }

    public Double getBasicPayLimit() { return basicPayLimit; }
    public void setBasicPayLimit(Double basicPayLimit) { this.basicPayLimit = basicPayLimit; }

    public LocalDate getEffectiveDate() { return effectiveDate; }
    public void setEffectiveDate(LocalDate effectiveDate) { this.effectiveDate = effectiveDate; }
}
