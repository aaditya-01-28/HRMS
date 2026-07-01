package com.example.admindashboard.repository;

import com.example.admindashboard.model.StatutoryCompliance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StatutoryComplianceRepository extends JpaRepository<StatutoryCompliance, Long> {
}
