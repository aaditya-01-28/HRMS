package com.example.admindashboard.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
@Entity
@Table(name = "employee_profiles")
public class EmployeeProfile {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;

    // SECTION 1: IDENTITY & JOB (Moved from User)
    @Column(name = "employee_code")
    private String employeeCode;
    
    private String designation;
    private String experience;
    @Column(name = "joining_date")
    private LocalDate joiningDate;

    // SECTION 2: PROJECT & ALLOCATION (Moved from User)
    private String businessUnit;
    private String accountName;
    private String projectName;
    private String projectCode;
    private String teamGroup;
    private String customerName;
    private String verticalName;
    private String domainIndustry;

    // SECTION 3: CONTACT DETAILS (Merged from User & Profile)
    private String mobileNumber;
    private String altMobile;
    private String workLocation;
    private String city;
    private String country;
    private String permanentAddress;
    private String workingAddress;
    private String deliveryAddressType;
    private String deliveryAddress;
    private String deliveryPincode;

    // SECTION 4: REPORTING LINES (Moved from User)
    // Note: The actual security hierarchy is User.manager, but these are kept for display/HR records
    private String reportingManager;
    private String projectManager;
    private String buHrContact;

    // SECTION 5: PERSONAL & LEGAL (Original Profile Data)
    private LocalDate dob;
    private String gender;
    private String aadharNo;
	private String panNo;

    private String emergencyContactName;
    private String relationWithEmployee;
    private String emergencyPhone;

    // SECTION 6: EDUCATION (Original Profile Data)
    private String qual1Title;
    private String qual1Inst;
    private String qual1Year;
    private String qual2Title;
    private String qual2Inst;
    private String qual2Year;

    // Bank Details
    private String bankAccountHolder;
    private String bankAccountNumber;
	private String bankIfscCode;
    private String bankName;
    private String bankBranch;
    private String bankAccountType;
    
 // GENERAL
    private String spouseName;
    private String fatherName;
    private String motherName;
    private LocalDate salaryDate;
    private Integer probationPeriod;
    private String maritalStatus;

    @Column(length = 2000)
    private String notes;

    // CLASSIFICATION
    private String branch;
    private String salaryStructure;
    private String leavePolicy;
    private String attendanceStructure;
    private String timesheetPolicy;
    private String department;
    private String taPolicy;
    private String category;

    // STATUTORY
    private String pfNumber;
	private String passportNumber;
    private String esiNumber;
    private String uanNumber;

    // HR CATEGORY
    private String bloodGroup;
    private String casteCategory;
    private String qualification;
    private String closeFriendName;
    private String drivingLicenseNo;
    private String nationality;
    
 // Present Address
    private String presentResidentialName;
    private String presentStreet;
    private String presentArea;
    private String presentCity;
    private String presentState;
    private String presentPincode;

    private String permanentPincode;

    // Permanent Address
    private String permanentResidentialName;
    private String permanentStreet;
    private String permanentArea;
    private String permanentCity;
    private String permanentState;
    

    // Emails
    private String personalEmail;

    private String officialEmail;

    private String alternateEmail;
    

    public String getPresentResidentialName() {
		return presentResidentialName;
	}
	public void setPresentResidentialName(String presentResidentialName) {
		this.presentResidentialName = presentResidentialName;
	}
	public String getPresentStreet() {
		return presentStreet;
	}
	public void setPresentStreet(String presentStreet) {
		this.presentStreet = presentStreet;
	}
	public String getPresentArea() {
		return presentArea;
	}
	public void setPresentArea(String presentArea) {
		this.presentArea = presentArea;
	}
	public String getPresentCity() {
		return presentCity;
	}
	public void setPresentCity(String presentCity) {
		this.presentCity = presentCity;
	}
	public String getPresentState() {
		return presentState;
	}
	public void setPresentState(String presentState) {
		this.presentState = presentState;
	}
	public String getPresentPincode() {
		return presentPincode;
	}
	public void setPresentPincode(String presentPincode) {
		this.presentPincode = presentPincode;
	}
	public String getPermanentResidentialName() {
		return permanentResidentialName;
	}
	public void setPermanentResidentialName(String permanentResidentialName) {
		this.permanentResidentialName = permanentResidentialName;
	}
	public String getPermanentStreet() {
		return permanentStreet;
	}
	public void setPermanentStreet(String permanentStreet) {
		this.permanentStreet = permanentStreet;
	}
	public String getPermanentArea() {
		return permanentArea;
	}
	public void setPermanentArea(String permanentArea) {
		this.permanentArea = permanentArea;
	}
	public String getPermanentCity() {
		return permanentCity;
	}
	public void setPermanentCity(String permanentCity) {
		this.permanentCity = permanentCity;
	}
	public String getPermanentState() {
		return permanentState;
	}
	public void setPermanentState(String permanentState) {
		this.permanentState = permanentState;
	}
	public String getPermanentPincode() {
		return permanentPincode;
	}
	public void setPermanentPincode(String permanentPincode) {
		this.permanentPincode = permanentPincode;
	}
	public String getOfficialEmail() {
		return officialEmail;
	}
	public void setOfficialEmail(String officialEmail) {
		this.officialEmail = officialEmail;
	}
	public String getAlternateEmail() {
		return alternateEmail;
	}
	public void setAlternateEmail(String alternateEmail) {
		this.alternateEmail = alternateEmail;
	}
	// GETTERS AND SETTERS
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public String getDesignation() { return designation; }
    public void setDesignation(String designation) { this.designation = designation; }

    public String getExperience() { return experience; }
    public void setExperience(String experience) { this.experience = experience; }

    public LocalDate getJoiningDate() { return joiningDate; }
    public void setJoiningDate(LocalDate joiningDate) { this.joiningDate = joiningDate; }

    public String getBusinessUnit() { return businessUnit; }
    public void setBusinessUnit(String businessUnit) { this.businessUnit = businessUnit; }

    public String getAccountName() { return accountName; }
    public void setAccountName(String accountName) { this.accountName = accountName; }

    public String getProjectName() { return projectName; }
    public void setProjectName(String projectName) { this.projectName = projectName; }

    public String getProjectCode() { return projectCode; }
    public void setProjectCode(String projectCode) { this.projectCode = projectCode; }

    public String getTeamGroup() { return teamGroup; }
    public void setTeamGroup(String teamGroup) { this.teamGroup = teamGroup; }

    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }

    public String getVerticalName() { return verticalName; }
    public void setVerticalName(String verticalName) { this.verticalName = verticalName; }

    public String getDomainIndustry() { return domainIndustry; }
    public void setDomainIndustry(String domainIndustry) { this.domainIndustry = domainIndustry; }

    public String getMobileNumber() { return mobileNumber; }
    public void setMobileNumber(String mobileNumber) { this.mobileNumber = mobileNumber; }

    public String getAltMobile() { return altMobile; }
    public void setAltMobile(String altMobile) { this.altMobile = altMobile; }

    public String getPersonalEmail() { return personalEmail; }
    public void setPersonalEmail(String personalEmail) { this.personalEmail = personalEmail; }

    public String getWorkLocation() { return workLocation; }
    public void setWorkLocation(String workLocation) { this.workLocation = workLocation; }

    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }

    public String getCountry() { return country; }
    public void setCountry(String country) { this.country = country; }

    public String getPermanentAddress() { return permanentAddress; }
    public void setPermanentAddress(String permanentAddress) { this.permanentAddress = permanentAddress; }

    public String getWorkingAddress() { return workingAddress; }
    public void setWorkingAddress(String workingAddress) { this.workingAddress = workingAddress; }

    public String getReportingManager() { return reportingManager; }
    public void setReportingManager(String reportingManager) { this.reportingManager = reportingManager; }

    public String getProjectManager() { return projectManager; }
    public void setProjectManager(String projectManager) { this.projectManager = projectManager; }

    public String getBuHrContact() { return buHrContact; }
    public void setBuHrContact(String buHrContact) { this.buHrContact = buHrContact; }

    public String getEmployeeCode() { return employeeCode; }
    public void setEmployeeCode(String employeeCode) { this.employeeCode = employeeCode; }

    public LocalDate getDob() { return dob; }
    public void setDob(LocalDate dob) { this.dob = dob; }

    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }

    public String getAadharNo() { return aadharNo; }
    public void setAadharNo(String aadharNo) { this.aadharNo = aadharNo; }

    public String getPanNo() { return panNo; }
    public void setPanNo(String panNo) { this.panNo = panNo; }

    public String getEmergencyContactName() { return emergencyContactName; }
    public void setEmergencyContactName(String emergencyContactName) { this.emergencyContactName = emergencyContactName; }

    public String getRelationWithEmployee() { return relationWithEmployee; }
    public void setRelationWithEmployee(String relationWithEmployee) { this.relationWithEmployee = relationWithEmployee; }

    public String getEmergencyPhone() { return emergencyPhone; }
    public void setEmergencyPhone(String emergencyPhone) { this.emergencyPhone = emergencyPhone; }

    public String getQual1Title() { return qual1Title; }
    public void setQual1Title(String qual1Title) { this.qual1Title = qual1Title; }

    public String getQual1Inst() { return qual1Inst; }
    public void setQual1Inst(String qual1Inst) { this.qual1Inst = qual1Inst; }

    public String getQual1Year() { return qual1Year; }
    public void setQual1Year(String qual1Year) { this.qual1Year = qual1Year; }

    public String getQual2Title() { return qual2Title; }
    public void setQual2Title(String qual2Title) { this.qual2Title = qual2Title; }

    public String getQual2Inst() { return qual2Inst; }
    public void setQual2Inst(String qual2Inst) { this.qual2Inst = qual2Inst; }

    public String getQual2Year() { return qual2Year; }
    public void setQual2Year(String qual2Year) { this.qual2Year = qual2Year; }

    public String getBankAccountHolder() { return bankAccountHolder; }
    public void setBankAccountHolder(String bankAccountHolder) { this.bankAccountHolder = bankAccountHolder; }

    public String getBankAccountNumber() { return bankAccountNumber; }
    public void setBankAccountNumber(String bankAccountNumber) { this.bankAccountNumber = bankAccountNumber; }

    public String getBankIfscCode() { return bankIfscCode; }
    public void setBankIfscCode(String bankIfscCode) { this.bankIfscCode = bankIfscCode; }

    public String getBankName() { return bankName; }
    public void setBankName(String bankName) { this.bankName = bankName; }

    public String getBankBranch() { return bankBranch; }
    public void setBankBranch(String bankBranch) { this.bankBranch = bankBranch; }

    public String getBankAccountType() { return bankAccountType; }
    public void setBankAccountType(String bankAccountType) { this.bankAccountType = bankAccountType; }

    public String getDeliveryAddressType() { return deliveryAddressType; }
    public void setDeliveryAddressType(String deliveryAddressType) { this.deliveryAddressType = deliveryAddressType; }

    public String getDeliveryAddress() { return deliveryAddress; }
    public void setDeliveryAddress(String deliveryAddress) { this.deliveryAddress = deliveryAddress; }

    public String getDeliveryPincode() { return deliveryPincode; }
    public void setDeliveryPincode(String deliveryPincode) { this.deliveryPincode = deliveryPincode; }
    
    public String getSpouseName() {
        return spouseName;
    }

    public void setSpouseName(String spouseName) {
        this.spouseName = spouseName;
    }

    public String getFatherName() {
        return fatherName;
    }

    public void setFatherName(String fatherName) {
        this.fatherName = fatherName;
    }

    public String getMotherName() {
        return motherName;
    }

    public void setMotherName(String motherName) {
        this.motherName = motherName;
    }

    public LocalDate getSalaryDate() {
        return salaryDate;
    }

    public void setSalaryDate(LocalDate salaryDate) {
        this.salaryDate = salaryDate;
    }

    public Integer getProbationPeriod() {
        return probationPeriod;
    }

    public void setProbationPeriod(Integer probationPeriod) {
        this.probationPeriod = probationPeriod;
    }

    public String getMaritalStatus() {
        return maritalStatus;
    }

    public void setMaritalStatus(String maritalStatus) {
        this.maritalStatus = maritalStatus;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getBranch() {
        return branch;
    }

    public void setBranch(String branch) {
        this.branch = branch;
    }

    public String getSalaryStructure() {
        return salaryStructure;
    }

    public void setSalaryStructure(String salaryStructure) {
        this.salaryStructure = salaryStructure;
    }

    public String getLeavePolicy() {
        return leavePolicy;
    }

    public void setLeavePolicy(String leavePolicy) {
        this.leavePolicy = leavePolicy;
    }

    public String getAttendanceStructure() {
        return attendanceStructure;
    }

    public void setAttendanceStructure(String attendanceStructure) {
        this.attendanceStructure = attendanceStructure;
    }

    public String getTimesheetPolicy() {
        return timesheetPolicy;
    }

    public void setTimesheetPolicy(String timesheetPolicy) {
        this.timesheetPolicy = timesheetPolicy;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getTaPolicy() {
        return taPolicy;
    }

    public void setTaPolicy(String taPolicy) {
        this.taPolicy = taPolicy;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getPfNumber() {
        return pfNumber;
    }

    public void setPfNumber(String pfNumber) {
        this.pfNumber = pfNumber;
    }

    public String getPassportNumber() {
        return passportNumber;
    }

    public void setPassportNumber(String passportNumber) {
        this.passportNumber = passportNumber;
    }

    public String getEsiNumber() {
        return esiNumber;
    }

    public void setEsiNumber(String esiNumber) {
        this.esiNumber = esiNumber;
    }

    public String getUanNumber() {
        return uanNumber;
    }

    public void setUanNumber(String uanNumber) {
        this.uanNumber = uanNumber;
    }

    public String getBloodGroup() {
        return bloodGroup;
    }

    public void setBloodGroup(String bloodGroup) {
        this.bloodGroup = bloodGroup;
    }

    public String getCasteCategory() {
        return casteCategory;
    }

    public void setCasteCategory(String casteCategory) {
        this.casteCategory = casteCategory;
    }

    public String getQualification() {
        return qualification;
    }

    public void setQualification(String qualification) {
        this.qualification = qualification;
    }

    public String getCloseFriendName() {
        return closeFriendName;
    }

    public void setCloseFriendName(String closeFriendName) {
        this.closeFriendName = closeFriendName;
    }

    public String getDrivingLicenseNo() {
        return drivingLicenseNo;
    }

    public void setDrivingLicenseNo(String drivingLicenseNo) {
        this.drivingLicenseNo = drivingLicenseNo;
    }

    public String getNationality() {
        return nationality;
    }

    public void setNationality(String nationality) {
        this.nationality = nationality;
    }
}