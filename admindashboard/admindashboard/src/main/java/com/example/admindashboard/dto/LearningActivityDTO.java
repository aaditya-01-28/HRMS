package com.example.admindashboard.dto;

public class LearningActivityDTO {

    private long newEnrollments;
    private long inProgress;
    private long completed;
    private long overdue;

    public long getNewEnrollments() {
        return newEnrollments;
    }

    public void setNewEnrollments(long newEnrollments) {
        this.newEnrollments = newEnrollments;
    }

    public long getInProgress() {
        return inProgress;
    }

    public void setInProgress(long inProgress) {
        this.inProgress = inProgress;
    }

    public long getCompleted() {
        return completed;
    }

    public void setCompleted(long completed) {
        this.completed = completed;
    }

    public long getOverdue() {
        return overdue;
    }

    public void setOverdue(long overdue) {
        this.overdue = overdue;
    }
}