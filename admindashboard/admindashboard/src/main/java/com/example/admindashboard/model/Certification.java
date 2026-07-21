package com.example.admindashboard.model;

import jakarta.persistence.*;

@Entity
public class Certification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String code;
    private String title;
    private String issuedBy;
    private String department;
    private String validityPeriod;
    private Integer totalIssued;
    private String status; // Active, Retired

    public Certification() {}

    public Certification(String code, String title, String issuedBy, String department, String validityPeriod, Integer totalIssued, String status) {
        this.code = code;
        this.title = title;
        this.issuedBy = issuedBy;
        this.department = department;
        this.validityPeriod = validityPeriod;
        this.totalIssued = totalIssued;
        this.status = status;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getIssuedBy() { return issuedBy; }
    public void setIssuedBy(String issuedBy) { this.issuedBy = issuedBy; }

    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }

    public String getValidityPeriod() { return validityPeriod; }
    public void setValidityPeriod(String validityPeriod) { this.validityPeriod = validityPeriod; }

    public Integer getTotalIssued() { return totalIssued; }
    public void setTotalIssued(Integer totalIssued) { this.totalIssued = totalIssued; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
