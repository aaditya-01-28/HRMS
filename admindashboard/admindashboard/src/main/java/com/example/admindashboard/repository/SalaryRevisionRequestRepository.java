package com.example.admindashboard.repository;

import com.example.admindashboard.model.SalaryRevisionRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SalaryRevisionRequestRepository extends JpaRepository<SalaryRevisionRequest, Long> {
    List<SalaryRevisionRequest> findByStatus(String status);
}
