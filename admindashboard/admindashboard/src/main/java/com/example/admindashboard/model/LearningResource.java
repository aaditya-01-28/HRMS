package com.example.admindashboard.model;

import jakarta.persistence.*;

@Entity
public class LearningResource {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String resourceName;
    private String roleType; // e.g., "Instructional Designer", "Trainer"
    private Integer allocationPercentage; // e.g., 60
    private Integer utilizationPercentage; // e.g., 85
    private String availabilityStatus; // e.g., "Available", "Booked"

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getResourceName() { return resourceName; }
    public void setResourceName(String resourceName) { this.resourceName = resourceName; }

    public String getRoleType() { return roleType; }
    public void setRoleType(String roleType) { this.roleType = roleType; }

    public Integer getAllocationPercentage() { return allocationPercentage; }
    public void setAllocationPercentage(Integer allocationPercentage) { this.allocationPercentage = allocationPercentage; }

    public Integer getUtilizationPercentage() { return utilizationPercentage; }
    public void setUtilizationPercentage(Integer utilizationPercentage) { this.utilizationPercentage = utilizationPercentage; }

    public String getAvailabilityStatus() { return availabilityStatus; }
    public void setAvailabilityStatus(String availabilityStatus) { this.availabilityStatus = availabilityStatus; }
}
