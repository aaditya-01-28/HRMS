package com.example.admindashboard.model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
public class EstablishmentSettings {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String establishmentCode;
    private LocalDate coverageDate;
    private String signatoryName;
    private String designation;

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getEstablishmentCode() { return establishmentCode; }
    public void setEstablishmentCode(String establishmentCode) { this.establishmentCode = establishmentCode; }

    public LocalDate getCoverageDate() { return coverageDate; }
    public void setCoverageDate(LocalDate coverageDate) { this.coverageDate = coverageDate; }

    public String getSignatoryName() { return signatoryName; }
    public void setSignatoryName(String signatoryName) { this.signatoryName = signatoryName; }

    public String getDesignation() { return designation; }
    public void setDesignation(String designation) { this.designation = designation; }
}
