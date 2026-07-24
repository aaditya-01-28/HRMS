package com.example.admindashboard.model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
public class EsiSettings {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String esiNumber;
    private String signatoryName;
    private Double employeeContributionPercentage;
    private Double employerContributionPercentage;
    private Double esiLimit;
    private LocalDate effectiveDate;

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getEsiNumber() { return esiNumber; }
    public void setEsiNumber(String esiNumber) { this.esiNumber = esiNumber; }

    public String getSignatoryName() { return signatoryName; }
    public void setSignatoryName(String signatoryName) { this.signatoryName = signatoryName; }

    public Double getEmployeeContributionPercentage() { return employeeContributionPercentage; }
    public void setEmployeeContributionPercentage(Double employeeContributionPercentage) { this.employeeContributionPercentage = employeeContributionPercentage; }

    public Double getEmployerContributionPercentage() { return employerContributionPercentage; }
    public void setEmployerContributionPercentage(Double employerContributionPercentage) { this.employerContributionPercentage = employerContributionPercentage; }

    public Double getEsiLimit() { return esiLimit; }
    public void setEsiLimit(Double esiLimit) { this.esiLimit = esiLimit; }

    public LocalDate getEffectiveDate() { return effectiveDate; }
    public void setEffectiveDate(LocalDate effectiveDate) { this.effectiveDate = effectiveDate; }
}
