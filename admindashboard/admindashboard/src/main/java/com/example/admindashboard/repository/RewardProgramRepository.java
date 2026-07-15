package com.example.admindashboard.repository;

import com.example.admindashboard.model.RewardProgram;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RewardProgramRepository extends JpaRepository<RewardProgram, Long> {
}
