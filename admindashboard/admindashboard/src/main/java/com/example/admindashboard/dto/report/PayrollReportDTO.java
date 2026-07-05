package com.example.admindashboard.dto.report;

import java.util.List;

public class PayrollReportDTO {
    private List<String> trendLabels;
    private List<Double> netPayrollTrend;

    private List<String> distributionLabels;
    private List<Double> distributionData;

    private List<PayrollSummaryRowDTO> tableData;

    // Getters and Setters
    public List<String> getTrendLabels() { return trendLabels; }
    public void setTrendLabels(List<String> trendLabels) { this.trendLabels = trendLabels; }

    public List<Double> getNetPayrollTrend() { return netPayrollTrend; }
    public void setNetPayrollTrend(List<Double> netPayrollTrend) { this.netPayrollTrend = netPayrollTrend; }

    public List<String> getDistributionLabels() { return distributionLabels; }
    public void setDistributionLabels(List<String> distributionLabels) { this.distributionLabels = distributionLabels; }

    public List<Double> getDistributionData() { return distributionData; }
    public void setDistributionData(List<Double> distributionData) { this.distributionData = distributionData; }

    public List<PayrollSummaryRowDTO> getTableData() { return tableData; }
    public void setTableData(List<PayrollSummaryRowDTO> tableData) { this.tableData = tableData; }
}
