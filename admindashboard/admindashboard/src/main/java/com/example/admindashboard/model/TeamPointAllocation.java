package com.example.admindashboard.model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "team_point_allocations")
public class TeamPointAllocation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String teamName;
    private Integer allocatedPoints = 0;
    private Integer distributedPoints = 0;
    private Integer remainingPoints = 0;
    private LocalDate actionDate;
    private String description;

    public TeamPointAllocation() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTeamName() { return teamName; }
    public void setTeamName(String teamName) { this.teamName = teamName; }

    public Integer getAllocatedPoints() { return allocatedPoints; }
    public void setAllocatedPoints(Integer allocatedPoints) { this.allocatedPoints = allocatedPoints; }

    public Integer getDistributedPoints() { return distributedPoints; }
    public void setDistributedPoints(Integer distributedPoints) { this.distributedPoints = distributedPoints; }

    public Integer getRemainingPoints() { return remainingPoints; }
    public void setRemainingPoints(Integer remainingPoints) { this.remainingPoints = remainingPoints; }

    public LocalDate getActionDate() { return actionDate; }
    public void setActionDate(LocalDate actionDate) { this.actionDate = actionDate; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}
