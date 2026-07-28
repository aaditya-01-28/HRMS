package com.example.admindashboard.model;

import jakarta.persistence.*;

@Entity
public class StatutoryConfig {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    private boolean pfEnabled;
    private boolean esiEnabled;
    private boolean ptEnabled;
    private boolean lwfEnabled;
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public boolean isPfEnabled() { return pfEnabled; }
    public void setPfEnabled(boolean pfEnabled) { this.pfEnabled = pfEnabled; }
    public boolean isEsiEnabled() { return esiEnabled; }
    public void setEsiEnabled(boolean esiEnabled) { this.esiEnabled = esiEnabled; }
    public boolean isPtEnabled() { return ptEnabled; }
    public void setPtEnabled(boolean ptEnabled) { this.ptEnabled = ptEnabled; }
    public boolean isLwfEnabled() { return lwfEnabled; }
    public void setLwfEnabled(boolean lwfEnabled) { this.lwfEnabled = lwfEnabled; }
}
