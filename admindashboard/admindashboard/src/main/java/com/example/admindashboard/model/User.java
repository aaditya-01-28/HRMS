package com.example.admindashboard.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties({
    "hibernateLazyInitializer",
    "handler"
})
@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // LOGIN CREDENTIALS
    @Column(unique = true, nullable = false)
    private String username;
    private String password;

    // BASIC IDENTIFICATION
    private String fullName;
    private String email;

    // RBAC & HIERARCHY (From your Requirement Doc)
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "role_id")
    private Role role;

    // This allows you to link a user to their specific manager for data filtering
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "manager_id")
    @JsonIgnore
    private User manager;

    private Long departmentId; // Optional: For department-level filtering later

    private String status = "ACTIVE"; // ACTIVE, INACTIVE, SUSPENDED
    @Column(columnDefinition = "boolean default false")
    private boolean requiresPasswordChange = false;

    // LINK TO HR DATA
    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
    @JsonIgnore
    private EmployeeProfile employeeProfile;

    // This connects the authentication User table to the Client business details
    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
    @JsonIgnore
    private Client clientProfile;

    // LINK TO MY-THANKS WALLET
    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
    @JsonIgnore
    private ThanksWallet thanksWallet;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @JsonIgnore
    private java.util.List<EmployeeRoleAssignment> roleAssignments = new java.util.ArrayList<>();

    // Helper method to get all aggregated permissions for this user (Option A)
    @Transient
    public java.util.Set<Permission> getAggregatedPermissions() {
        java.util.Set<Permission> allPerms = new java.util.HashSet<>();
        // Add primary role permissions
        if (this.role != null && this.role.getPermissions() != null) {
            allPerms.addAll(this.role.getPermissions());
        }
        // Add all active assignment permissions
        if (this.roleAssignments != null) {
            for (EmployeeRoleAssignment assignment : this.roleAssignments) {
                if ("Active".equalsIgnoreCase(assignment.getStatus()) && 
                    (assignment.getEffectiveFrom() == null || !java.time.LocalDate.now().isBefore(assignment.getEffectiveFrom()))) {
                    if (assignment.getRole() != null && assignment.getRole().getPermissions() != null) {
                        allPerms.addAll(assignment.getRole().getPermissions());
                    }
                }
            }
        }
        return allPerms;
    }

    public java.util.List<EmployeeRoleAssignment> getRoleAssignments() { return roleAssignments; }
    public void setRoleAssignments(java.util.List<EmployeeRoleAssignment> roleAssignments) { this.roleAssignments = roleAssignments; }

    public ThanksWallet getThanksWallet() { return thanksWallet; }
    public void setThanksWallet(ThanksWallet thanksWallet) { this.thanksWallet = thanksWallet; }

    // GETTERS AND SETTERS
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    
    public boolean isRequiresPasswordChange() { return requiresPasswordChange; }
    public void setRequiresPasswordChange(boolean requiresPasswordChange) { this.requiresPasswordChange = requiresPasswordChange; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public Role getRole() { return role; }
    public void setRole(Role role) { this.role = role; }

    public User getManager() { return manager; }
    public void setManager(User manager) { this.manager = manager; }

    public Long getDepartmentId() { return departmentId; }
    public void setDepartmentId(Long departmentId) { this.departmentId = departmentId; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public EmployeeProfile getEmployeeProfile() { return employeeProfile; }
    public void setEmployeeProfile(EmployeeProfile employeeProfile) { this.employeeProfile = employeeProfile; }

    // --- NEW: CLIENT PROFILE GETTERS/SETTERS ---
    public Client getClientProfile() { return clientProfile; }
    public void setClientProfile(Client clientProfile) { this.clientProfile = clientProfile;}
    
 // DESIGNATION FIELD AND METHODS HERE
    private String designation;

    public String getDesignation() { return designation; }
    public void setDesignation(String designation) { this.designation = designation; }
    
 
    private String profileImage;
    public String getProfileImage() { return profileImage; }
    public void setProfileImage(String profileImage) { this.profileImage = profileImage; }

    // DASHBOARD PREFERENCES
    private String pinnedServices;
    private String pinnedApplications;

    public String getPinnedServices() { return pinnedServices; }
    public void setPinnedServices(String pinnedServices) { this.pinnedServices = pinnedServices; }

    public String getPinnedApplications() { return pinnedApplications; }
    public void setPinnedApplications(String pinnedApplications) { this.pinnedApplications = pinnedApplications; }
}