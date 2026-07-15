package com.example.admindashboard.repository;

import com.example.admindashboard.model.RewardBudget;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RewardBudgetRepository extends JpaRepository<RewardBudget, Long> {
}
