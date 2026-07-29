package com.example.admindashboard.model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "finance_checklists")
public class FinanceChecklist {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id")
    private User user;

    private String employeeName;
    private String employeeCode;
    private String department;
    private String processType; // ONBOARDING, OFFBOARDING
    private Boolean bankDetailsVerified;
    private Boolean panAadhaarLinked;
    private Boolean pfEsiConfigured;
    private Boolean taxDeclarationSubmitted;
    private Boolean finalSettlementCleared;
    private String bankName;
    private String accountNumber;
    private String ifscCode;
    private String taxRegime; // New Regime, Old Regime
    private LocalDate processDate;
    private String status; // Pending, In Progress, Completed

    public FinanceChecklist() {}

    public FinanceChecklist(User user, String employeeName, String employeeCode, String department, 
                            String processType, Boolean bankDetailsVerified, Boolean panAadhaarLinked, 
                            Boolean pfEsiConfigured, Boolean taxDeclarationSubmitted, Boolean finalSettlementCleared, 
                            String bankName, String accountNumber, String ifscCode, String taxRegime, 
                            LocalDate processDate, String status) {
        this.user = user;
        this.employeeName = employeeName;
        this.employeeCode = employeeCode;
        this.department = department;
        this.processType = processType;
        this.bankDetailsVerified = bankDetailsVerified;
        this.panAadhaarLinked = panAadhaarLinked;
        this.pfEsiConfigured = pfEsiConfigured;
        this.taxDeclarationSubmitted = taxDeclarationSubmitted;
        this.finalSettlementCleared = finalSettlementCleared;
        this.bankName = bankName;
        this.accountNumber = accountNumber;
        this.ifscCode = ifscCode;
        this.taxRegime = taxRegime;
        this.processDate = processDate;
        this.status = status;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public String getEmployeeName() { return employeeName; }
    public void setEmployeeName(String employeeName) { this.employeeName = employeeName; }

    public String getEmployeeCode() { return employeeCode; }
    public void setEmployeeCode(String employeeCode) { this.employeeCode = employeeCode; }

    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }

    public String getProcessType() { return processType; }
    public void setProcessType(String processType) { this.processType = processType; }

    public Boolean getBankDetailsVerified() { return bankDetailsVerified; }
    public void setBankDetailsVerified(Boolean bankDetailsVerified) { this.bankDetailsVerified = bankDetailsVerified; }

    public Boolean getPanAadhaarLinked() { return panAadhaarLinked; }
    public void setPanAadhaarLinked(Boolean panAadhaarLinked) { this.panAadhaarLinked = panAadhaarLinked; }

    public Boolean getPfEsiConfigured() { return pfEsiConfigured; }
    public void setPfEsiConfigured(Boolean pfEsiConfigured) { this.pfEsiConfigured = pfEsiConfigured; }

    public Boolean getTaxDeclarationSubmitted() { return taxDeclarationSubmitted; }
    public void setTaxDeclarationSubmitted(Boolean taxDeclarationSubmitted) { this.taxDeclarationSubmitted = taxDeclarationSubmitted; }

    public Boolean getFinalSettlementCleared() { return finalSettlementCleared; }
    public void setFinalSettlementCleared(Boolean finalSettlementCleared) { this.finalSettlementCleared = finalSettlementCleared; }

    public String getBankName() { return bankName; }
    public void setBankName(String bankName) { this.bankName = bankName; }

    public String getAccountNumber() { return accountNumber; }
    public void setAccountNumber(String accountNumber) { this.accountNumber = accountNumber; }

    public String getIfscCode() { return ifscCode; }
    public void setIfscCode(String ifscCode) { this.ifscCode = ifscCode; }

    public String getTaxRegime() { return taxRegime; }
    public void setTaxRegime(String taxRegime) { this.taxRegime = taxRegime; }

    public LocalDate getProcessDate() { return processDate; }
    public void setProcessDate(LocalDate processDate) { this.processDate = processDate; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
