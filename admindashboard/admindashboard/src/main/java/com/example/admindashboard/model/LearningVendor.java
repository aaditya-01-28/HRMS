package com.example.admindashboard.model;

import jakarta.persistence.*;

@Entity
public class LearningVendor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String vendorName;
    private String category;
    private String contactPerson;
    private String email;
    private String phone;
    
    @Column(length = 1000)
    private String servicesProvided;

    private Double performanceScore;
    private String status;

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getVendorName() { return vendorName; }
    public void setVendorName(String vendorName) { this.vendorName = vendorName; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getContactPerson() { return contactPerson; }
    public void setContactPerson(String contactPerson) { this.contactPerson = contactPerson; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getServicesProvided() { return servicesProvided; }
    public void setServicesProvided(String servicesProvided) { this.servicesProvided = servicesProvided; }

    public Double getPerformanceScore() { return performanceScore; }
    public void setPerformanceScore(Double performanceScore) { this.performanceScore = performanceScore; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
