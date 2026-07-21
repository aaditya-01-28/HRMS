package com.example.admindashboard.repository;

import com.example.admindashboard.model.LearningStrategy;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LearningStrategyRepository extends JpaRepository<LearningStrategy, Long> {
}
