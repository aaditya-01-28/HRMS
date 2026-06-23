package com.example.admindashboard.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "ride_bookings")
public class RideBooking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private LocalDate rideDate;

    @Column(nullable = false)
    private String loginTime;

    @Column(nullable = false)
    private String logoutTime;

    // e.g. "SCHEDULED", "CANCELLED"
    @Column(nullable = false)
    private String status = "SCHEDULED";

    private String cancelReason;

    @Column(length = 1000)
    private String cancelRemarks;

    private LocalDateTime createdAt;

    private String pickupLocation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vehicle_id")
    private TransportVehicle vehicle;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public LocalDate getRideDate() {
        return rideDate;
    }

    public void setRideDate(LocalDate rideDate) {
        this.rideDate = rideDate;
    }

    public String getLoginTime() {
        return loginTime;
    }

    public void setLoginTime(String loginTime) {
        this.loginTime = loginTime;
    }

    public String getLogoutTime() {
        return logoutTime;
    }

    public void setLogoutTime(String logoutTime) {
        this.logoutTime = logoutTime;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCancelReason() {
        return cancelReason;
    }

    public void setCancelReason(String cancelReason) {
        this.cancelReason = cancelReason;
    }

    public String getCancelRemarks() {
        return cancelRemarks;
    }

    public void setCancelRemarks(String cancelRemarks) {
        this.cancelRemarks = cancelRemarks;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public String getPickupLocation() {
        return pickupLocation;
    }

    public void setPickupLocation(String pickupLocation) {
        this.pickupLocation = pickupLocation;
    }

    public TransportVehicle getVehicle() {
        return vehicle;
    }

    public void setVehicle(TransportVehicle vehicle) {
        this.vehicle = vehicle;
    }
}

