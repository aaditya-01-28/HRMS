package com.example.admindashboard.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

@Entity
@Table(name = "salary_components")
public class SalaryComponent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "salary_structure_id", nullable = false)
    @JsonIgnore
    private SalaryStructure salaryStructure;

    private String componentName; // e.g., Basic Salary, HRA
    
    private String type; // Earning, Deduction
    
    private String category; // Basic, Allowance, Statutory
    
    private String calculationFormula; // 40% of CTC, Fixed Amount, As per rules
    
    private Double amount = 0.0;
    
    private Double percentageOfCtc = 0.0;
    
    private Boolean taxable = true;

    // GETTERS AND SETTERS
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public SalaryStructure getSalaryStructure() { return salaryStructure; }
    public void setSalaryStructure(SalaryStructure salaryStructure) { this.salaryStructure = salaryStructure; }

    public String getComponentName() { return componentName; }
    public void setComponentName(String componentName) { this.componentName = componentName; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getCalculationFormula() { return calculationFormula; }
    public void setCalculationFormula(String calculationFormula) { this.calculationFormula = calculationFormula; }

    public Double getAmount() { return amount; }
    public void setAmount(Double amount) { this.amount = amount; }

    public Double getPercentageOfCtc() { return percentageOfCtc; }
    public void setPercentageOfCtc(Double percentageOfCtc) { this.percentageOfCtc = percentageOfCtc; }

    public Boolean getTaxable() { return taxable; }
    public void setTaxable(Boolean taxable) { this.taxable = taxable; }
}
