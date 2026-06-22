package com.example.admindashboard.dto;

public class LearningDashboardStats {

    private long totalCourses;
    private long activeLearners;
    private long pendingApprovals;
    private long completedCourses;
    private long certificatesIssued;

    public long getTotalCourses() {
        return totalCourses;
    }

    public void setTotalCourses(long totalCourses) {
        this.totalCourses = totalCourses;
    }

    public long getActiveLearners() {
        return activeLearners;
    }

    public void setActiveLearners(long activeLearners) {
        this.activeLearners = activeLearners;
    }

    public long getPendingApprovals() {
        return pendingApprovals;
    }

    public void setPendingApprovals(long pendingApprovals) {
        this.pendingApprovals = pendingApprovals;
    }

    public long getCompletedCourses() {
        return completedCourses;
    }

    public void setCompletedCourses(long completedCourses) {
        this.completedCourses = completedCourses;
    }

    public long getCertificatesIssued() {
        return certificatesIssued;
    }

    public void setCertificatesIssued(long certificatesIssued) {
        this.certificatesIssued = certificatesIssued;
    }
}