package com.example.admindashboard.dto.report;

public class PayrollSummaryRowDTO {
    private String payrollRunId;
    private String payrollMonth;
    private String payDate;
    private int employees;
    private double grossPayroll;
    private double deductions;
    private double netPayroll;

    // Constructors
    public PayrollSummaryRowDTO() {}

    public PayrollSummaryRowDTO(String payrollRunId, String payrollMonth, String payDate, int employees, double grossPayroll, double deductions, double netPayroll) {
        this.payrollRunId = payrollRunId;
        this.payrollMonth = payrollMonth;
        this.payDate = payDate;
        this.employees = employees;
        this.grossPayroll = grossPayroll;
        this.deductions = deductions;
        this.netPayroll = netPayroll;
    }

    // Getters and Setters
    public String getPayrollRunId() { return payrollRunId; }
    public void setPayrollRunId(String payrollRunId) { this.payrollRunId = payrollRunId; }

    public String getPayrollMonth() { return payrollMonth; }
    public void setPayrollMonth(String payrollMonth) { this.payrollMonth = payrollMonth; }

    public String getPayDate() { return payDate; }
    public void setPayDate(String payDate) { this.payDate = payDate; }

    public int getEmployees() { return employees; }
    public void setEmployees(int employees) { this.employees = employees; }

    public double getGrossPayroll() { return grossPayroll; }
    public void setGrossPayroll(double grossPayroll) { this.grossPayroll = grossPayroll; }

    public double getDeductions() { return deductions; }
    public void setDeductions(double deductions) { this.deductions = deductions; }

    public double getNetPayroll() { return netPayroll; }
    public void setNetPayroll(double netPayroll) { this.netPayroll = netPayroll; }
}
