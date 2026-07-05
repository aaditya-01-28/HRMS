package com.example.admindashboard.dto.report;

public class LeaveSummaryRowDTO {
    private String department;
    private int totalRequest;
    private int approved;
    private int pending;
    private int rejected;
    private int leaveDaysTaken;
    private double avgLeaveDays;

    // Constructors
    public LeaveSummaryRowDTO() {}

    public LeaveSummaryRowDTO(String department, int totalRequest, int approved, int pending, int rejected, int leaveDaysTaken, double avgLeaveDays) {
        this.department = department;
        this.totalRequest = totalRequest;
        this.approved = approved;
        this.pending = pending;
        this.rejected = rejected;
        this.leaveDaysTaken = leaveDaysTaken;
        this.avgLeaveDays = avgLeaveDays;
    }

    // Getters and Setters
    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }

    public int getTotalRequest() { return totalRequest; }
    public void setTotalRequest(int totalRequest) { this.totalRequest = totalRequest; }

    public int getApproved() { return approved; }
    public void setApproved(int approved) { this.approved = approved; }

    public int getPending() { return pending; }
    public void setPending(int pending) { this.pending = pending; }

    public int getRejected() { return rejected; }
    public void setRejected(int rejected) { this.rejected = rejected; }

    public int getLeaveDaysTaken() { return leaveDaysTaken; }
    public void setLeaveDaysTaken(int leaveDaysTaken) { this.leaveDaysTaken = leaveDaysTaken; }

    public double getAvgLeaveDays() { return avgLeaveDays; }
    public void setAvgLeaveDays(double avgLeaveDays) { this.avgLeaveDays = avgLeaveDays; }
}
