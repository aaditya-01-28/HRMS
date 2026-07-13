package com.example.admindashboard.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "hrms_notifications")
public class HrmsNotification {

    public HrmsNotification() {
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    private String title;

    @Column(length = 4000)
    private String message;

    private String type; // COMMUNICATION, PROJECT_UPDATE, NOTIFICATION
    private String priority; // Normal, High, Low
    private String senderName;
    private String senderDesignation;

    @Column(name = "is_read")
    private boolean read = false;

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    public HrmsNotification(User user, String title, String message, String type, String priority, String senderName) {
        this.user = user;
        this.title = title;
        this.message = message;
        this.type = type;
        this.priority = priority;
        this.senderName = senderName;
        this.read = false;
        this.createdAt = LocalDateTime.now();
    }

    public HrmsNotification(User user, String title, String message, String type, String priority, String senderName, String senderDesignation) {
        this.user = user;
        this.title = title;
        this.message = message;
        this.type = type;
        this.priority = priority;
        this.senderName = senderName;
        this.senderDesignation = senderDesignation;
        this.read = false;
        this.createdAt = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getPriority() { return priority; }
    public void setPriority(String priority) { this.priority = priority; }

    public String getSenderName() { return senderName; }
    public void setSenderName(String senderName) { this.senderName = senderName; }

    public String getSenderDesignation() { return senderDesignation; }
    public void setSenderDesignation(String senderDesignation) { this.senderDesignation = senderDesignation; }

    public boolean isRead() { return read; }
    public void setRead(boolean read) { this.read = read; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
