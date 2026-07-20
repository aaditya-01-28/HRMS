package com.example.admindashboard.model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "backup_schedules")
public class BackupSchedule {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String backupName;
    
    private String backupType;
    private String frequency;
    private String source;
    private String destination;
    private String status = "Scheduled";
    private LocalDate lastRunDate;
    private LocalDate nextRunDate;
    private Double sizeGB;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getBackupName() { return backupName; }
    public void setBackupName(String backupName) { this.backupName = backupName; }

    public String getBackupType() { return backupType; }
    public void setBackupType(String backupType) { this.backupType = backupType; }

    public String getFrequency() { return frequency; }
    public void setFrequency(String frequency) { this.frequency = frequency; }

    public String getSource() { return source; }
    public void setSource(String source) { this.source = source; }

    public String getDestination() { return destination; }
    public void setDestination(String destination) { this.destination = destination; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public LocalDate getLastRunDate() { return lastRunDate; }
    public void setLastRunDate(LocalDate lastRunDate) { this.lastRunDate = lastRunDate; }

    public LocalDate getNextRunDate() { return nextRunDate; }
    public void setNextRunDate(LocalDate nextRunDate) { this.nextRunDate = nextRunDate; }

    public Double getSizeGB() { return sizeGB; }
    public void setSizeGB(Double sizeGB) { this.sizeGB = sizeGB; }
}
