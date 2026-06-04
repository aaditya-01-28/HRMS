package com.example.admindashboard.model;

import java.io.Serializable;

public class CartItem implements Serializable {
    private String itemName;
    private Integer points;
    private String productType;
    private String imageSrc;
    
    public CartItem() {
    }

    public CartItem(String itemName, Integer points, String productType, String imageSrc) {
        this.itemName = itemName;
        this.points = points;
        this.productType = productType;
        this.imageSrc = imageSrc;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public Integer getPoints() {
        return points;
    }

    public void setPoints(Integer points) {
        this.points = points;
    }

    public String getProductType() {
        return productType;
    }

    public void setProductType(String productType) {
        this.productType = productType;
    }

    public String getImageSrc() {
        return imageSrc;
    }

    public void setImageSrc(String imageSrc) {
        this.imageSrc = imageSrc;
    }
}
