package com.example.admindashboard.repository;

import com.example.admindashboard.model.ReimbursementRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReimbursementRequestRepository extends JpaRepository<ReimbursementRequest, Long> {
}
