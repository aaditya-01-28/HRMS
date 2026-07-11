package com.example.admindashboard.model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "facility_contracts")
public class FacilityContract {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String contractId; // e.g. CON-101

    @Column(nullable = false)
    private String contractName;

    private String vendorName;
    private String category; // e.g. Housekeeping, Security, AMC, Pantry, Cafeteria
    private LocalDate startDate;
    private LocalDate endDate;
    private LocalDate nextBillDate;
    private Double cost;
    private String status = "Active"; // Active, Expired, Renewal Due
    
    @Column(columnDefinition = "TEXT")
    private String description;

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getContractId() { return contractId; }
    public void setContractId(String contractId) { this.contractId = contractId; }

    public String getContractName() { return contractName; }
    public void setContractName(String contractName) { this.contractName = contractName; }

    public String getVendorName() { return vendorName; }
    public void setVendorName(String vendorName) { this.vendorName = vendorName; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public LocalDate getStartDate() { return startDate; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }

    public LocalDate getEndDate() { return endDate; }
    public void setEndDate(LocalDate endDate) { this.endDate = endDate; }

    public LocalDate getNextBillDate() { return nextBillDate; }
    public void setNextBillDate(LocalDate nextBillDate) { this.nextBillDate = nextBillDate; }

    public Double getCost() { return cost; }
    public void setCost(Double cost) { this.cost = cost; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}
