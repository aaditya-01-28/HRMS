package com.example.admindashboard.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "shift_assignments")
public class ShiftAssignment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    @JsonBackReference
    private User user;

    private String department;
    private String location;
    
    private String shiftName; // e.g. Night Shift
    private String shiftTiming; // e.g. 08:00 PM - 05:00 AM
    private String breakTime; // e.g. 10:30 PM - 11:00 PM
    
    private LocalDate startDate;
    private LocalDate endDate;
    
    private boolean otAllowed;
    private String otCalculationRule;

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
    
    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }
    
    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }
    
    public String getShiftName() { return shiftName; }
    public void setShiftName(String shiftName) { this.shiftName = shiftName; }
    
    public String getShiftTiming() { return shiftTiming; }
    public void setShiftTiming(String shiftTiming) { this.shiftTiming = shiftTiming; }
    
    public String getBreakTime() { return breakTime; }
    public void setBreakTime(String breakTime) { this.breakTime = breakTime; }
    
    public LocalDate getStartDate() { return startDate; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }
    
    public LocalDate getEndDate() { return endDate; }
    public void setEndDate(LocalDate endDate) { this.endDate = endDate; }
    
    public boolean isOtAllowed() { return otAllowed; }
    public void setOtAllowed(boolean otAllowed) { this.otAllowed = otAllowed; }
    
    public String getOtCalculationRule() { return otCalculationRule; }
    public void setOtCalculationRule(String otCalculationRule) { this.otCalculationRule = otCalculationRule; }
}
