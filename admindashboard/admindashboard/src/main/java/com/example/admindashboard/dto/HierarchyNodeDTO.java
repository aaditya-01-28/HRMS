package com.example.admindashboard.dto;

import java.util.ArrayList;
import java.util.List;

public class HierarchyNodeDTO {
    private Long id;
    private String name;
    private String designation;
    private String department;
    private String profileImage;
    private String email;
    private String phone;
    private int directReports;
    private int teamSize;
    private List<HierarchyNodeDTO> children = new ArrayList<>();

    private String l2Manager;
    private String l3Manager;
    private String assignedHr;

    public HierarchyNodeDTO() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDesignation() { return designation; }
    public void setDesignation(String designation) { this.designation = designation; }
    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }
    public String getProfileImage() { return profileImage; }
    public void setProfileImage(String profileImage) { this.profileImage = profileImage; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public int getDirectReports() { return directReports; }
    public void setDirectReports(int directReports) { this.directReports = directReports; }
    public int getTeamSize() { return teamSize; }
    public void setTeamSize(int teamSize) { this.teamSize = teamSize; }
    public List<HierarchyNodeDTO> getChildren() { return children; }
    public void setChildren(List<HierarchyNodeDTO> children) { this.children = children; }

    public String getL2Manager() { return l2Manager; }
    public void setL2Manager(String l2Manager) { this.l2Manager = l2Manager; }
    public String getL3Manager() { return l3Manager; }
    public void setL3Manager(String l3Manager) { this.l3Manager = l3Manager; }
    public String getAssignedHr() { return assignedHr; }
    public void setAssignedHr(String assignedHr) { this.assignedHr = assignedHr; }
}
