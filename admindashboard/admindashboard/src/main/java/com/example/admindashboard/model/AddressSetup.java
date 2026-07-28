package com.example.admindashboard.model;

import jakarta.persistence.*;

@Entity
public class AddressSetup {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    private String addressType;
    private boolean isMandatory;
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getAddressType() { return addressType; }
    public void setAddressType(String addressType) { this.addressType = addressType; }
    public boolean isMandatory() { return isMandatory; }
    public void setMandatory(boolean mandatory) { isMandatory = mandatory; }
}
