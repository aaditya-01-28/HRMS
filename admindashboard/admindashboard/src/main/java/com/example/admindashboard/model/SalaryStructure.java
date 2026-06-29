package com.example.admindashboard.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "salary_structures")
public class SalaryStructure {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user; // Can be null if it's a generic template

    private String structureName;

    private LocalDate effectiveFrom;

    private Double ctcAmount = 0.0;
    
    private Double totalEarnings = 0.0;
    
    private Double totalDeductions = 0.0;

    private Boolean active = true;

    @OneToMany(mappedBy = "salaryStructure", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SalaryComponent> components = new ArrayList<>();

    // GETTERS AND SETTERS
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public String getStructureName() { return structureName; }
    public void setStructureName(String structureName) { this.structureName = structureName; }

    public LocalDate getEffectiveFrom() { return effectiveFrom; }
    public void setEffectiveFrom(LocalDate effectiveFrom) { this.effectiveFrom = effectiveFrom; }

    public Double getCtcAmount() { return ctcAmount; }
    public void setCtcAmount(Double ctcAmount) { this.ctcAmount = ctcAmount; }

    public Double getTotalEarnings() { return totalEarnings; }
    public void setTotalEarnings(Double totalEarnings) { this.totalEarnings = totalEarnings; }

    public Double getTotalDeductions() { return totalDeductions; }
    public void setTotalDeductions(Double totalDeductions) { this.totalDeductions = totalDeductions; }

    public Boolean getActive() { return active; }
    public void setActive(Boolean active) { this.active = active; }

    public List<SalaryComponent> getComponents() { return components; }
    public void setComponents(List<SalaryComponent> components) { this.components = components; }
    
    public void addComponent(SalaryComponent component) {
        components.add(component);
        component.setSalaryStructure(this);
    }
}
