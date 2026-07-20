package com.example.admindashboard.repository;

import com.example.admindashboard.model.LearningBudget;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LearningBudgetRepository extends JpaRepository<LearningBudget, Long> {
}
