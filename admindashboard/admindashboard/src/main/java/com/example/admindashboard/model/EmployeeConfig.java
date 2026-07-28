package com.example.admindashboard.model;

import jakarta.persistence.*;

@Entity
public class EmployeeConfig {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    private String employeePrefix;
    private Integer defaultProbationPeriod;
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getEmployeePrefix() { return employeePrefix; }
    public void setEmployeePrefix(String employeePrefix) { this.employeePrefix = employeePrefix; }
    public Integer getDefaultProbationPeriod() { return defaultProbationPeriod; }
    public void setDefaultProbationPeriod(Integer defaultProbationPeriod) { this.defaultProbationPeriod = defaultProbationPeriod; }
}
