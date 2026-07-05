package com.example.admindashboard.dto.report;

import java.util.List;

public class LeaveReportDTO {
    private List<String> trendLabels;
    private List<Integer> totalRequestsTrend;
    private List<Integer> approvedTrend;
    private List<Integer> pendingTrend;
    private List<Integer> rejectedTrend;

    private List<String> distributionLabels;
    private List<Integer> distributionData;

    private List<LeaveSummaryRowDTO> tableData;

    // Getters and Setters
    public List<String> getTrendLabels() { return trendLabels; }
    public void setTrendLabels(List<String> trendLabels) { this.trendLabels = trendLabels; }

    public List<Integer> getTotalRequestsTrend() { return totalRequestsTrend; }
    public void setTotalRequestsTrend(List<Integer> totalRequestsTrend) { this.totalRequestsTrend = totalRequestsTrend; }

    public List<Integer> getApprovedTrend() { return approvedTrend; }
    public void setApprovedTrend(List<Integer> approvedTrend) { this.approvedTrend = approvedTrend; }

    public List<Integer> getPendingTrend() { return pendingTrend; }
    public void setPendingTrend(List<Integer> pendingTrend) { this.pendingTrend = pendingTrend; }

    public List<Integer> getRejectedTrend() { return rejectedTrend; }
    public void setRejectedTrend(List<Integer> rejectedTrend) { this.rejectedTrend = rejectedTrend; }

    public List<String> getDistributionLabels() { return distributionLabels; }
    public void setDistributionLabels(List<String> distributionLabels) { this.distributionLabels = distributionLabels; }

    public List<Integer> getDistributionData() { return distributionData; }
    public void setDistributionData(List<Integer> distributionData) { this.distributionData = distributionData; }

    public List<LeaveSummaryRowDTO> getTableData() { return tableData; }
    public void setTableData(List<LeaveSummaryRowDTO> tableData) { this.tableData = tableData; }
}
