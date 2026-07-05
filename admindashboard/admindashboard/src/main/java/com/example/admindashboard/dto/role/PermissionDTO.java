package com.example.admindashboard.dto.role;

public class PermissionDTO {
    private Long id;
    private String permissionName;
    private String module;
    private String accessLevel;

    // Constructors
    public PermissionDTO() {}

    public PermissionDTO(Long id, String permissionName, String module, String accessLevel) {
        this.id = id;
        this.permissionName = permissionName;
        this.module = module;
        this.accessLevel = accessLevel;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getPermissionName() { return permissionName; }
    public void setPermissionName(String permissionName) { this.permissionName = permissionName; }
    public String getModule() { return module; }
    public void setModule(String module) { this.module = module; }
    public String getAccessLevel() { return accessLevel; }
    public void setAccessLevel(String accessLevel) { this.accessLevel = accessLevel; }
}
