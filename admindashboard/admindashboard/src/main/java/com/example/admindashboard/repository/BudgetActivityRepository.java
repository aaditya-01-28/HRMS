package com.example.admindashboard.repository;

import com.example.admindashboard.model.BudgetActivity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BudgetActivityRepository extends JpaRepository<BudgetActivity, Long> {
}
