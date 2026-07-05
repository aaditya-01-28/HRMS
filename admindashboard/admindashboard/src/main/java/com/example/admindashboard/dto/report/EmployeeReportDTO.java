package com.example.admindashboard.dto.report;

import java.util.List;

public class EmployeeReportDTO {
    private List<String> trendLabels;
    private List<Integer> totalEmployeesTrend;
    private List<Integer> newHiresTrend;
    private List<Integer> exitsTrend;

    private List<String> distributionLabels;
    private List<Integer> distributionData;

    private List<EmployeeSummaryRowDTO> tableData;

    // Getters and Setters
    public List<String> getTrendLabels() { return trendLabels; }
    public void setTrendLabels(List<String> trendLabels) { this.trendLabels = trendLabels; }

    public List<Integer> getTotalEmployeesTrend() { return totalEmployeesTrend; }
    public void setTotalEmployeesTrend(List<Integer> totalEmployeesTrend) { this.totalEmployeesTrend = totalEmployeesTrend; }

    public List<Integer> getNewHiresTrend() { return newHiresTrend; }
    public void setNewHiresTrend(List<Integer> newHiresTrend) { this.newHiresTrend = newHiresTrend; }

    public List<Integer> getExitsTrend() { return exitsTrend; }
    public void setExitsTrend(List<Integer> exitsTrend) { this.exitsTrend = exitsTrend; }

    public List<String> getDistributionLabels() { return distributionLabels; }
    public void setDistributionLabels(List<String> distributionLabels) { this.distributionLabels = distributionLabels; }

    public List<Integer> getDistributionData() { return distributionData; }
    public void setDistributionData(List<Integer> distributionData) { this.distributionData = distributionData; }

    public List<EmployeeSummaryRowDTO> getTableData() { return tableData; }
    public void setTableData(List<EmployeeSummaryRowDTO> tableData) { this.tableData = tableData; }
}
