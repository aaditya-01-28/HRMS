package com.example.admindashboard.repository;

import com.example.admindashboard.model.UpcomingAudit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UpcomingAuditRepository extends JpaRepository<UpcomingAudit, Long> {
}
