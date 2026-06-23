package com.example.admindashboard.model;

import jakarta.persistence.*;

@Entity
@Table(name = "transport_vehicles")
public class TransportVehicle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String vehicleNo;

    // e.g. "Idle", "On-Trip", "Assigned"
    private String status = "Idle";

    private int totalSeats;
    private int availableSeats;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vendor_id", nullable = false)
    private TransportVendor vendor;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "driver_id")
    private TransportDriver driver;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getVehicleNo() {
        return vehicleNo;
    }

    public void setVehicleNo(String vehicleNo) {
        this.vehicleNo = vehicleNo;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getTotalSeats() {
        return totalSeats;
    }

    public void setTotalSeats(int totalSeats) {
        this.totalSeats = totalSeats;
    }

    public int getAvailableSeats() {
        return availableSeats;
    }

    public void setAvailableSeats(int availableSeats) {
        this.availableSeats = availableSeats;
    }

    public TransportVendor getVendor() {
        return vendor;
    }

    public void setVendor(TransportVendor vendor) {
        this.vendor = vendor;
    }

    public TransportDriver getDriver() {
        return driver;
    }

    public void setDriver(TransportDriver driver) {
        this.driver = driver;
    }
}
