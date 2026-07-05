package com.example.admindashboard.dto.report;

public class EmployeeSummaryRowDTO {
    private String department;
    private int totalEmployee;
    private int male;
    private int female;
    private int newHire;
    private int exits;
    private double avgTenure;

    // Constructors
    public EmployeeSummaryRowDTO() {}

    public EmployeeSummaryRowDTO(String department, int totalEmployee, int male, int female, int newHire, int exits, double avgTenure) {
        this.department = department;
        this.totalEmployee = totalEmployee;
        this.male = male;
        this.female = female;
        this.newHire = newHire;
        this.exits = exits;
        this.avgTenure = avgTenure;
    }

    // Getters and Setters
    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }

    public int getTotalEmployee() { return totalEmployee; }
    public void setTotalEmployee(int totalEmployee) { this.totalEmployee = totalEmployee; }

    public int getMale() { return male; }
    public void setMale(int male) { this.male = male; }

    public int getFemale() { return female; }
    public void setFemale(int female) { this.female = female; }

    public int getNewHire() { return newHire; }
    public void setNewHire(int newHire) { this.newHire = newHire; }

    public int getExits() { return exits; }
    public void setExits(int exits) { this.exits = exits; }

    public double getAvgTenure() { return avgTenure; }
    public void setAvgTenure(double avgTenure) { this.avgTenure = avgTenure; }
}
