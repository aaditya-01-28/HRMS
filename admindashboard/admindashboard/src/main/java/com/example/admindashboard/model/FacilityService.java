package com.example.admindashboard.model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "facility_services")
public class FacilityService {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String serviceName;

    private String vendorName;
    private String category; // e.g. Housekeeping, Maintenance, Security, Pantry, Cafeteria
    private Double cost;
    
    @Column(columnDefinition = "TEXT")
    private String description;
    
    private LocalDate startDate;
    private LocalDate endDate;
    private LocalDate nextBillDate;
    private String status = "Active"; // Active, Expired, Suspended

    // Stepper / assignment details
    private String frequency = "Monthly"; // Monthly, Quarterly, Weekly
    private String assignedTo;
    private String priority = "Medium";

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getServiceName() { return serviceName; }
    public void setServiceName(String serviceName) { this.serviceName = serviceName; }

    public String getVendorName() { return vendorName; }
    public void setVendorName(String vendorName) { this.vendorName = vendorName; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public Double getCost() { return cost; }
    public void setCost(Double cost) { this.cost = cost; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public LocalDate getStartDate() { return startDate; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }

    public LocalDate getEndDate() { return endDate; }
    public void setEndDate(LocalDate endDate) { this.endDate = endDate; }

    public LocalDate getNextBillDate() { return nextBillDate; }
    public void setNextBillDate(LocalDate nextBillDate) { this.nextBillDate = nextBillDate; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getFrequency() { return frequency; }
    public void setFrequency(String frequency) { this.frequency = frequency; }

    public String getAssignedTo() { return assignedTo; }
    public void setAssignedTo(String assignedTo) { this.assignedTo = assignedTo; }

    public String getPriority() { return priority; }
    public void setPriority(String priority) { this.priority = priority; }
}
