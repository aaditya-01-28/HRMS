package com.example.admindashboard.repository;

import com.example.admindashboard.model.BudgetRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BudgetRequestRepository extends JpaRepository<BudgetRequest, Long> {
}
