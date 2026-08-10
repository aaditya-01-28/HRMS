package com.example.admindashboard.model;

import jakarta.persistence.*;

@Entity
@Table(name = "candidates")
public class Candidate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String candidateCode;
    private String name;
    private String email;
    private String jobTitle;
    private String department;

    private String joiningDate;
    private String recruiterName;
    private String address;
    private String contactNumber;
    private Double packageAmount;
    private String bgvStatus;
    private String externalVerifier;
    private String activity;
    private String empSource;

    /* GETTERS AND SETTERS */

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCandidateCode() {
        return candidateCode;
    }

    public void setCandidateCode(String candidateCode) {
        this.candidateCode = candidateCode;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getJobTitle() {
        return jobTitle;
    }

    public void setJobTitle(String jobTitle) {
        this.jobTitle = jobTitle;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getJoiningDate() {
        return joiningDate;
    }

    public void setJoiningDate(String joiningDate) {
        this.joiningDate = joiningDate;
    }

    public String getRecruiterName() {
        return recruiterName;
    }

    public void setRecruiterName(String recruiterName) {
        this.recruiterName = recruiterName;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getContactNumber() {
        return contactNumber;
    }

    public void setContactNumber(String contactNumber) {
        this.contactNumber = contactNumber;
    }

    public Double getPackageAmount() {
        return packageAmount;
    }

    public void setPackageAmount(Double packageAmount) {
        this.packageAmount = packageAmount;
    }

    public String getBgvStatus() {
        return bgvStatus;
    }

    public void setBgvStatus(String bgvStatus) {
        this.bgvStatus = bgvStatus;
    }

    public String getExternalVerifier() {
        return externalVerifier;
    }

    public void setExternalVerifier(String externalVerifier) {
        this.externalVerifier = externalVerifier;
    }

    public String getActivity() {
        return activity;
    }

    public void setActivity(String activity) {
        this.activity = activity;
    }

    public String getEmpSource() {
        return empSource;
    }

    public void setEmpSource(String empSource) {
        this.empSource = empSource;
    }

    // Onboarding Fields
    private String onboardingPolicy;
    private String reportingAuthority;
    private String grade;
    private String contactPerson;
    private String aadharNumber;
    private String panNumber;
    private String onboardStatus; // In Progress, Completed
    private Boolean emailVerified;
    private Boolean isRejoin;

    public String getOnboardingPolicy() { return onboardingPolicy; }
    public void setOnboardingPolicy(String onboardingPolicy) { this.onboardingPolicy = onboardingPolicy; }

    public String getReportingAuthority() { return reportingAuthority; }
    public void setReportingAuthority(String reportingAuthority) { this.reportingAuthority = reportingAuthority; }

    public String getGrade() { return grade; }
    public void setGrade(String grade) { this.grade = grade; }

    public String getContactPerson() { return contactPerson; }
    public void setContactPerson(String contactPerson) { this.contactPerson = contactPerson; }

    public String getAadharNumber() { return aadharNumber; }
    public void setAadharNumber(String aadharNumber) { this.aadharNumber = aadharNumber; }

    public String getPanNumber() { return panNumber; }
    public void setPanNumber(String panNumber) { this.panNumber = panNumber; }

    public String getOnboardStatus() { return onboardStatus; }
    public void setOnboardStatus(String onboardStatus) { this.onboardStatus = onboardStatus; }

    public Boolean getEmailVerified() { return emailVerified; }
    public void setEmailVerified(Boolean emailVerified) { this.emailVerified = emailVerified; }

    public Boolean getIsRejoin() { return isRejoin; }
    public void setIsRejoin(Boolean isRejoin) { this.isRejoin = isRejoin; }
}
