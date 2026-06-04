package com.example.admindashboard.dto;

public class AttendanceDayDTO {

    private String date;
    private String plannedHours;
    private String recordedHours;
    private Integer recordings;
    private String actualDate;
    private String approvalStatus;

    public AttendanceDayDTO(
            String actualDate,
            String date,
            String plannedHours,
            String recordedHours,
            Integer recordings,
            String approvalStatus){

    	this.actualDate = actualDate;
    	this.date = date;
    	this.plannedHours = plannedHours;
    	this.recordedHours = recordedHours;
    	this.recordings = recordings;
    	this.approvalStatus = approvalStatus;
    }

    public String getActualDate() {
        return actualDate;
    }

    public void setActualDate(String actualDate) {
        this.actualDate = actualDate;
    }
    
    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getPlannedHours() {
        return plannedHours;
    }

    public void setPlannedHours(String plannedHours) {
        this.plannedHours = plannedHours;
    }

    public String getRecordedHours() {
        return recordedHours;
    }

    public void setRecordedHours(String recordedHours) {
        this.recordedHours = recordedHours;
    }

    public Integer getRecordings() {
        return recordings;
    }

    public void setRecordings(Integer recordings) {
        this.recordings = recordings;
    }
    public String getApprovalStatus() {
        return approvalStatus;
    }

    public void setApprovalStatus(String approvalStatus) {
        this.approvalStatus = approvalStatus;
    }
}