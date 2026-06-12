package com.example.admindashboard.model;

public class BurnChartPoint {

    private String label;

    private Integer expected;

    private Integer actual;

    public BurnChartPoint(String label,
                          Integer expected,
                          Integer actual) {

        this.label = label;
        this.expected = expected;
        this.actual = actual;
    }

    public String getLabel() {
        return label;
    }

    public Integer getExpected() {
        return expected;
    }

    public Integer getActual() {
        return actual;
    }
}