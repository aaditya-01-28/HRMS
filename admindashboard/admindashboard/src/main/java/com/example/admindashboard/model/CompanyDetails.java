package com.example.admindashboard.model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
public class CompanyDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String companyName;
    private LocalDate incorporationDate;
    private String panNo;
    private String tanNo;
    private String gstNo;
    private String uanNo;
    private String website;
    private String telephone;
    
    private String addressLine1;
    private String addressLine2;
    private String city;
    private String state;
    private String pincode;

    private String authorizedSignatory;
    private String directorName;

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getCompanyName() { return companyName; }
    public void setCompanyName(String companyName) { this.companyName = companyName; }

    public LocalDate getIncorporationDate() { return incorporationDate; }
    public void setIncorporationDate(LocalDate incorporationDate) { this.incorporationDate = incorporationDate; }

    public String getPanNo() { return panNo; }
    public void setPanNo(String panNo) { this.panNo = panNo; }

    public String getTanNo() { return tanNo; }
    public void setTanNo(String tanNo) { this.tanNo = tanNo; }

    public String getGstNo() { return gstNo; }
    public void setGstNo(String gstNo) { this.gstNo = gstNo; }

    public String getUanNo() { return uanNo; }
    public void setUanNo(String uanNo) { this.uanNo = uanNo; }

    public String getWebsite() { return website; }
    public void setWebsite(String website) { this.website = website; }

    public String getTelephone() { return telephone; }
    public void setTelephone(String telephone) { this.telephone = telephone; }

    public String getAddressLine1() { return addressLine1; }
    public void setAddressLine1(String addressLine1) { this.addressLine1 = addressLine1; }

    public String getAddressLine2() { return addressLine2; }
    public void setAddressLine2(String addressLine2) { this.addressLine2 = addressLine2; }

    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }

    public String getState() { return state; }
    public void setState(String state) { this.state = state; }

    public String getPincode() { return pincode; }
    public void setPincode(String pincode) { this.pincode = pincode; }

    public String getAuthorizedSignatory() { return authorizedSignatory; }
    public void setAuthorizedSignatory(String authorizedSignatory) { this.authorizedSignatory = authorizedSignatory; }

    public String getDirectorName() { return directorName; }
    public void setDirectorName(String directorName) { this.directorName = directorName; }
}
