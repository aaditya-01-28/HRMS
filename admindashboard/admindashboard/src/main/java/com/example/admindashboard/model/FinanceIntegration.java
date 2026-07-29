package com.example.admindashboard.model;

import jakarta.persistence.*;

@Entity
@Table(name = "finance_integrations")
public class FinanceIntegration {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String systemName;
    private String vendorDetails;
    private String systemType;
    private String connectedTo;
    private String integrationMethod;
    private String lastSync;
    private String status;
    private String dataFlow;

    public FinanceIntegration() {}

    public FinanceIntegration(String systemName, String vendorDetails, String systemType, String connectedTo, String integrationMethod, String lastSync, String status, String dataFlow) {
        this.systemName = systemName;
        this.vendorDetails = vendorDetails;
        this.systemType = systemType;
        this.connectedTo = connectedTo;
        this.integrationMethod = integrationMethod;
        this.lastSync = lastSync;
        this.status = status;
        this.dataFlow = dataFlow;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getSystemName() { return systemName; }
    public void setSystemName(String systemName) { this.systemName = systemName; }

    public String getVendorDetails() { return vendorDetails; }
    public void setVendorDetails(String vendorDetails) { this.vendorDetails = vendorDetails; }

    public String getSystemType() { return systemType; }
    public void setSystemType(String systemType) { this.systemType = systemType; }

    public String getConnectedTo() { return connectedTo; }
    public void setConnectedTo(String connectedTo) { this.connectedTo = connectedTo; }

    public String getIntegrationMethod() { return integrationMethod; }
    public void setIntegrationMethod(String integrationMethod) { this.integrationMethod = integrationMethod; }

    public String getLastSync() { return lastSync; }
    public void setLastSync(String lastSync) { this.lastSync = lastSync; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getDataFlow() { return dataFlow; }
    public void setDataFlow(String dataFlow) { this.dataFlow = dataFlow; }
}
