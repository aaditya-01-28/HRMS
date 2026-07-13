package com.example.admindashboard.repository;

import com.example.admindashboard.model.ProjectUpdateNotification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProjectUpdateNotificationRepository extends JpaRepository<ProjectUpdateNotification, Long> {
}
