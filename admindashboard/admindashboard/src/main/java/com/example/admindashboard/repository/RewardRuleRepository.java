package com.example.admindashboard.repository;

import com.example.admindashboard.model.RewardRule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RewardRuleRepository extends JpaRepository<RewardRule, Long> {
}
