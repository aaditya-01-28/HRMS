package com.example.admindashboard.model;

import jakarta.persistence.*;

@Entity
@Table(name = "reward_merchandise")
public class RewardMerchandise {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String itemName;
    private String category; // e.g. Audio, Apparel, Gift Cards, Certificates
    private String brand;
    
    @Column(columnDefinition = "TEXT")
    private String description;

    private Integer redemptionPoints = 0;
    private Integer totalStocks = 0;
    private Integer perUserLimit = 1;
    private String imagePath;
    private String status = "PUBLISHED"; // PUBLISHED, DRAFT

    public RewardMerchandise() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getItemName() { return itemName; }
    public void setItemName(String itemName) { this.itemName = itemName; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getBrand() { return brand; }
    public void setBrand(String brand) { this.brand = brand; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Integer getRedemptionPoints() { return redemptionPoints; }
    public void setRedemptionPoints(Integer redemptionPoints) { this.redemptionPoints = redemptionPoints; }

    public Integer getTotalStocks() { return totalStocks; }
    public void setTotalStocks(Integer totalStocks) { this.totalStocks = totalStocks; }

    public Integer getPerUserLimit() { return perUserLimit; }
    public void setPerUserLimit(Integer perUserLimit) { this.perUserLimit = perUserLimit; }

    public String getImagePath() { return imagePath; }
    public void setImagePath(String imagePath) { this.imagePath = imagePath; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
